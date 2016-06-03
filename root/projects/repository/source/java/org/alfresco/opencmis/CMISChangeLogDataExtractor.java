package org.alfresco.opencmis;

import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.repo.audit.extractor.AbstractDataExtractor;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

/**
 * An extractor that allows to filter data using the following rule: Audit
 * records should only be created for items in the CMIS domain model.
 * 
 * @author Stas Sokolovsky
 */
public class CMISChangeLogDataExtractor extends AbstractDataExtractor
{
    public static final String KEY_NODE_REF = "nodeRef";
    public static final String KEY_OBJECT_ID = "objectId";

    private NodeService nodeService;
    private CMISDictionaryService cmisDictionaryService;
    private CMISConnector cmisConnector;

    /**
     * Extracts relevant node refs and Ids from auditing data
     * 
     * @see org.alfresco.repo.audit.extractor.DataExtractor#extractData(java.io.Serializable)
     */
    public Serializable extractData(Serializable value) throws Throwable
    {
        NodeRef nodeRef = getNodeRef(value);

        HashMap<String, Serializable> result = new HashMap<String, Serializable>(5);
        result.put(KEY_NODE_REF, nodeRef);
        result.put(KEY_OBJECT_ID, cmisConnector.createObjectId(nodeRef, true));

        return result;
    }

    /**
     * @return Returns <tt>true</tt> if items in the CMIS domain model
     * @see org.alfresco.repo.audit.extractor.DataExtractor#isSupported(java.io.Serializable)
     */
    public boolean isSupported(Serializable data)
    {
        if (data != null)
        {
            NodeRef nodeRef = getNodeRef(data);
            if (nodeRef != null)
            {
                QName typeQName = nodeService.getType(nodeRef);
                TypeDefinitionWrapper type = cmisDictionaryService.findNodeType(typeQName);

                return (type != null)
                        && (type.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT || type.getBaseTypeId() == BaseTypeId.CMIS_FOLDER);
            }
        }
        return false;
    }

    /**
     * Gets the NodeRef from auditing data
     * 
     * @param data
     *            audit data
     * @return Node Reference
     */
    private NodeRef getNodeRef(Serializable data)
    {
        NodeRef nodeRef = null;
        if (data instanceof ChildAssociationRef)
        {
            nodeRef = ((ChildAssociationRef) data).getChildRef();
        }
        else if (data instanceof FileInfo)
        {
            nodeRef = ((FileInfo) data).getNodeRef();
        }
        else if (data instanceof NodeRef)
        {
            nodeRef = (NodeRef) data;
        }
        return nodeRef;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setOpenCMISDictionaryService(CMISDictionaryService cmisDictionaryService)
    {
        this.cmisDictionaryService = cmisDictionaryService;
    }

    public void setCmisConnector(CMISConnector cmisConnector) {
        this.cmisConnector = cmisConnector;
    }
}
