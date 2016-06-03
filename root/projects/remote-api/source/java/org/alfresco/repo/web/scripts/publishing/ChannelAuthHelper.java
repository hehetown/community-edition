package org.alfresco.repo.web.scripts.publishing;

import java.util.Map;
import java.util.TreeMap;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.publishing.channels.ChannelType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.UrlUtil;

/**
 * @author Brian
 * @since 4.0
 */
public class ChannelAuthHelper
{
    private String basePath = "/proxy/alfresco/api/publishing/channels/";
    private SysAdminParams sysAdminParams;

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    public String getBaseChannelApiUrl(NodeRef channelId)
    {
        StringBuilder urlBuilder = new StringBuilder(UrlUtil.getShareUrl(sysAdminParams));
        urlBuilder.append(basePath);
        urlBuilder.append(channelId.getStoreRef().getProtocol());
        urlBuilder.append('/');
        urlBuilder.append(channelId.getStoreRef().getIdentifier());
        urlBuilder.append('/');
        urlBuilder.append(channelId.getId());
        urlBuilder.append('/');

        return urlBuilder.toString();
    }

    public String getDefaultAuthoriseUrl(NodeRef channelId)
    {
        return getBaseChannelApiUrl(channelId) + "authform";
    }

    public String getAuthoriseCallbackUrl(NodeRef channelId)
    {
        return getBaseChannelApiUrl(channelId) + "authcallback";
    }
    
    public Map<String, Object> buildAuthorisationModel(Channel channel)
    {
        String alfrescoCallbackUrl = getAuthoriseCallbackUrl(channel.getNodeRef());
        ChannelType.AuthUrlPair authUrlPair = channel.getChannelType().getAuthorisationUrls(channel, alfrescoCallbackUrl); 
        String authoriseUrl = authUrlPair.authorisationRequestUrl;
        if (authoriseUrl == null)
        {
            // If a channel type returns null as the authorise URL then we
            // assume credentials are to be supplied to us directly. We'll point the 
            // user at our own credential-gathering form.
            authoriseUrl = getDefaultAuthoriseUrl(channel.getNodeRef());
        }

        Map<String, Object> model = new TreeMap<String, Object>();
        model.put("authoriseUrl", authoriseUrl);
        model.put("channelId", channel.getId());
        model.put("authCallbackUrl", alfrescoCallbackUrl);
        model.put("authRedirectUrl", authUrlPair.authorisationRedirectUrl);
        return model;
    }
}
