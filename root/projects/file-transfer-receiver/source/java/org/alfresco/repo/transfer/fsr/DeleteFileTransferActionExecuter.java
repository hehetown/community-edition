package org.alfresco.repo.transfer.fsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.transfer.TransferModel;
import org.alfresco.repo.transfer.TransferTargetImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.transfer.TransferCallback;
import org.alfresco.service.cmr.transfer.TransferDefinition;
import org.alfresco.service.cmr.transfer.TransferService2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Logger action executer. This action will send over a content node to a File Transfer Receiver.
 *
 * @author Philippe Dubois
 */
public class DeleteFileTransferActionExecuter extends ActionExecuterAbstractBase
{
    /** The logger */
    private static Log logger = LogFactory.getLog("org.alfresco.repo.transfer.fsr.FileTransferActionExecuter");

    /** The name of the action */
    public static final String NAME = "delete-ftr-action";
    /** Name of the target */
    public static final String TARGET_NAME = "ftr";

    private TransferService2 transferService;

    private NodeService nodeService;

    private FileFolderService fileFolderService;

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        Set<NodeRef> nodeToTransfer = new HashSet<NodeRef>();
        nodeToTransfer.add(actionedUponNodeRef);
        TransferDefinition transferDef = new TransferDefinition();
        transferDef.setNodes(nodeToTransfer);
        //setting sync to true to generate the delete
        //transferDef.setSync(true);
        if (!transferService.targetExists(TARGET_NAME))
        {
            TransferTargetImpl newTarget = new TransferTargetImpl();
            newTarget.setEndpointProtocol("http");
            newTarget.setEndpointHost("localhost");
            newTarget.setEndpointPort(9090);
            newTarget.setEndpointPath("/alfresco-ftr/service/api/transfer");
            newTarget.setName(TARGET_NAME);
            newTarget.setTitle("FTR test title target");
            newTarget.setUsername("phil");
            newTarget.setPassword("phil".toCharArray());
            transferService.saveTransferTarget(newTarget);
            // get the created target and define the root
            // the root is implicitly defined has the root of the current node.
            // get underlying node ref corresponding to the target definition
            NodeRef transferTargetNodeRef = transferService.getTransferTarget(TARGET_NAME).getNodeRef();
            // Get the primary parent of the node the action is execute up on
            NodeRef rooTarget = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
            //Set the type to "fileTransferTarget" to transferTargetNodeRef and associate it to the root
            nodeService.setType(transferTargetNodeRef, TransferModel.TYPE_FILE_TRANSFER_TARGET);
            // create the association
            nodeService.createAssociation(transferTargetNodeRef, rooTarget, TransferModel.ASSOC_ROOT_FILE_TRANSFER);
        }

        fileFolderService.delete(actionedUponNodeRef);
        transferService.transfer(TARGET_NAME, transferDef, (Collection<TransferCallback>) null);
        //just simulate, therefore restore the node.
        NodeRef archivedNode = new NodeRef(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE,actionedUponNodeRef.getId());
        nodeService.restoreNode(archivedNode, null,null, null);
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // Specify the parameters
    }

    public void setTransferService(TransferService2 transferService)
    {
        this.transferService = transferService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

}
