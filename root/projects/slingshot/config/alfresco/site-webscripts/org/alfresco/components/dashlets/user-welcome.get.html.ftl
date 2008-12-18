<script type="text/javascript">//<![CDATA[
new Alfresco.UserWelcome("${args.htmlid}");
//]]></script>
<div class="dashlet user-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body scrollableList">
      <div class="detail-list-item-alt">
         <h4>${msg("header.userDashboard")}</h4>
         <div>${msg("text.userDashboard")}</div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.customiseDashboard")}</h4>
         <div>${msg("text.customiseDashboard")}</div>
         <div><a href="${url.context}/page/customise-user-dashboard">${msg("link.customiseDashboard")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.userProfile")}</h4>
         <div>${msg("text.userProfile")}</div>
         <div><a href="${url.context}/page/user/${user.name?url}/profile">${msg("link.userProfile")}</a></div>
      </div>
<#if sites?? && sites?size &gt; 0>
      <div class="detail-list-item">
         <h4>${msg("header.mySites")}</h4>
         <div>${msg("text.mySites")}</div>
   <#list sites as site>
         <div class="site"><a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title?html}</a></div>
   </#list>
      </div>
</#if>
      <div class="detail-list-item">
         <h4>${msg("header.createSite")}</h4>
         <div>${msg("text.createSite")}</div>
         <div><a id="${args.htmlid}-createSite-button" href="#">${msg("link.createSite")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.onlineHelp")}</h4>
         <div>${msg("text.onlineHelp")}</div>
         <div><a href="http://www.alfresco.com/help/3/enterprise/DMShareHelp" target="_new">${msg("link.onlineHelp")}</a></div>
      </div>
      <div class="detail-list-item last-item">
         <h4>${msg("header.featureTour")}</h4>
         <div>${msg("text.featureTour")}</div>
         <div><a href="http://www.alfresco.com/help/3/enterprise/ShareTutorial" target="_new">${msg("link.featureTour")}</a></div>
      </div>
      <div class="clear"></div>
   </div>
</div>