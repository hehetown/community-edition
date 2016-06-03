package org.alfresco.repo.web.scripts.archive;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.node.archive.RestoreNodeReport;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the archivednode.put webscript.
 * 
 * @author Neil Mc Erlean
 * @since 3.5
 */
public class ArchivedNodePut extends AbstractArchivedNodeWebScript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        // Current user
        String userID = AuthenticationUtil.getFullyAuthenticatedUser();
        if (userID == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_UNAUTHORIZED, "Web Script ["
                        + req.getServiceMatch().getWebScript().getDescription()
                        + "] requires user authentication.");
        }

        NodeRef nodeRefToBeRestored = parseRequestForNodeRef(req);
        if (nodeRefToBeRestored == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "nodeRef not recognised. Could not restore.");
        }
        
        // check if the current user has the permission to restore the node
        validatePermission(nodeRefToBeRestored, userID);
        
        RestoreNodeReport report = nodeArchiveService.restoreArchivedNode(nodeRefToBeRestored);

        // Handling of some error scenarios
        if (report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_INVALID_ARCHIVE_NODE))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find archive node: " + nodeRefToBeRestored);
        }
        else if (report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_PERMISSION))
        {
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Unable to restore archive node: " + nodeRefToBeRestored);
        }
        else if (report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_DUPLICATE_CHILD_NODE_NAME))
        {
            throw new WebScriptException(HttpServletResponse.SC_CONFLICT, "Unable to restore archive node: " + nodeRefToBeRestored +". Duplicate child node name");
        }
        else if (report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_INVALID_PARENT) ||
                 report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_INTEGRITY) ||
                 report.getStatus().equals(RestoreNodeReport.RestoreStatus.FAILURE_OTHER))
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to restore archive node: " + nodeRefToBeRestored);
        }
        
        model.put("restoreNodeReport", report);
        return model;
    }
}
