package org.alfresco.module.vti.web.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.repo.webdav.ActivityPostProducer;
import org.alfresco.repo.webdav.WebDAVActivityPoster;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVMethod;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.FileFilterMode.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiWebDavAction is processor of WebDAV protocol. It provides 
* the back-end controller for dispatching among set of WebDAVMethods. 
* It selects and invokes a realization of {@link WebDAVMethod}
* to perform the requested method of WebDAV protocol.</p>
*
* @author Stas Sokolovsky
*
*/
public abstract class VtiWebDavAction implements VtiAction, VtiWebDavActionExecutor
{
    protected VtiPathHelper pathHelper;

    protected WebDAVHelper webDavHelper;

    
    private static Log logger = LogFactory.getLog(VtiWebDavAction.class);

    private VtiWebDavActionExecutor davActionExecutor = this;
    
    private WebDAVActivityPoster activityPoster;
    
    /**
     * <p>Process WebDAV protocol request, dispatch among set of 
     * WebDAVMethods, selects and invokes a realization of {@link WebDAVMethod}
     * to perform the requested method of WebDAV protocol.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        WebDAVMethod method = getWebDAVMethod();
        try
        {
            FileFilterMode.setClient(Client.webdav);
            davActionExecutor.execute(method, request, response);
        }
        catch (WebDAVServerException e)
        {
            logger.debug(e);

            if (response.isCommitted())
            {
                logger.warn("Could not return the status code to the client as the response has already been committed!", e);
            }
            else
            {
                try
                {
                    response.sendError(e.getHttpStatusCode());
                }
                catch (IOException e1)
                {
                    throw new RuntimeException(e1);
                }
            }
        }
    }

    /**
     * <p>Return executing WebDAV method.</p>
     */
    public abstract WebDAVMethod getWebDAVMethod();

    
    /**
     * @see #execute(WebDAVMethod, HttpServletRequest, HttpServletResponse)
     * @param davActionExecutor the WebDAV method executor.
     */
    public void setDavActionExecutor(VtiWebDavActionExecutor davActionExecutor)
    {
        this.davActionExecutor = davActionExecutor;
    }

    /**
     * @param activityPoster the activityPoster to set
     */
    public void setActivityPoster(WebDAVActivityPoster activityPoster)
    {
        this.activityPoster = activityPoster;
    }

    /**
     * Plugable executor implementation allows overriding of this behaviour without disturbing
     * the class hierarchy.
     * 
     * @param method WebDAVMethod
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws WebDAVServerException
     */
    @Override
    public void execute(WebDAVMethod method, HttpServletRequest request,
                HttpServletResponse response) throws WebDAVServerException
    {
        method.setDetails(request, response, webDavHelper, pathHelper.getRootNodeRef());
        
        // A very few WebDAV methods produce activity posts.
        if (method instanceof ActivityPostProducer)
        {
            ActivityPostProducer activityPostProducer = (ActivityPostProducer) method;
            activityPostProducer.setActivityPoster(activityPoster);
        }
        
        method.execute();
    }

    /**
     * <p>VtiPathHelper setter.</p>
     *
     * @param pathHelper {@link VtiPathHelper}.    
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * Provide a WebDAVHelper collaborator.
     * 
     * @param webDavHelper the webDavHelper to set
     */
    public void setWebDavHelper(WebDAVHelper webDavHelper)
    {
        this.webDavHelper = webDavHelper;
    }
}
