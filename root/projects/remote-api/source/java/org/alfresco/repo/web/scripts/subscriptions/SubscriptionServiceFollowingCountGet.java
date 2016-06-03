package org.alfresco.repo.web.scripts.subscriptions;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class SubscriptionServiceFollowingCountGet extends AbstractSubscriptionServiceWebScript
{
    @SuppressWarnings("unchecked")
    public JSONObject executeImpl(String userId, WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        int count = subscriptionService.getFollowingCount(userId);

        JSONObject obj = new JSONObject();
        obj.put("count", count);

        return obj;
    }
}
