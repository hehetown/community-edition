package org.alfresco.solr.query;

import org.alfresco.opencmis.search.CMISQueryOptions.CMISQueryMode;
import org.alfresco.repo.search.impl.querymodel.Order;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.PropertyAccessor;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Score;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.util.Pair;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class CmisQParserPlugin extends QParserPlugin
{
    protected final static Logger log = LoggerFactory.getLogger(CmisQParserPlugin.class);

    /*
     * (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String,
     * org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams,
     * org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new CmisQParser(qstr, localParams, params, req);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    public void init(NamedList arg0)
    {
    }

    public static class CmisQParser extends AbstractQParser
    {
        public CmisQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
        {
            super(qstr, localParams, params, req);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws ParseException
        {   
            Pair<SearchParameters, Boolean> searchParametersAndFilter = getSearchParameters();
            SearchParameters searchParameters = searchParametersAndFilter.getFirst();
            // these could either be checked & set here, or in the SolrQueryParser constructor

            String id = req.getSchema().getResourceLoader().getInstanceDir();
            IndexReader indexReader = req.getSearcher().getIndexReader();

            String cmisVersionString = this.params.get("cmisVersion");
            CmisVersion cmisVersion = (cmisVersionString == null ? CmisVersion.CMIS_1_0 : CmisVersion.valueOf(cmisVersionString));
            
            String altDic = this.params.get(SearchParameters.ALTERNATIVE_DICTIONARY);
            org.alfresco.repo.search.impl.querymodel.Query queryModelQuery
              = AlfrescoSolrDataModel.getInstance(id).parseCMISQueryToAlfrescoAbstractQuery(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS, searchParameters, indexReader, altDic, cmisVersion);
            
            // build the sort param and update the params on the request if required .....
            
            if ((queryModelQuery.getOrderings() != null) && (queryModelQuery.getOrderings().size() > 0))
            {
                StringBuilder sortParameter = new StringBuilder();

                for (Ordering ordering : queryModelQuery.getOrderings())
                {
                    if (ordering.getColumn().getFunction().getName().equals(PropertyAccessor.NAME))
                    {
                        PropertyArgument property = (PropertyArgument) ordering.getColumn().getFunctionArguments().get(PropertyAccessor.ARG_PROPERTY);

                        if (property == null)
                        {
                            throw new IllegalStateException();
                        }

                        String propertyName = property.getPropertyName();

                        String luceneField =  AlfrescoSolrDataModel.getInstance(id).getCMISFunctionEvaluationContext(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS,cmisVersion,altDic).getLuceneFieldName(propertyName);

                        if(sortParameter.length() > 0)
                        {
                            sortParameter.append(", ");
                        }
                        sortParameter.append(luceneField).append(" ");
                        if(ordering.getOrder() == Order.DESCENDING)
                        {
                            sortParameter.append("desc");
                        }
                        else
                        {
                            sortParameter.append("asc");
                        }
                        
                    }
                    else if (ordering.getColumn().getFunction().getName().equals(Score.NAME))
                    {
                        if(sortParameter.length() > 0)
                        {
                            sortParameter.append(", ");
                        }
                        sortParameter.append("SCORE ");
                        if(ordering.getOrder() == Order.DESCENDING)
                        {
                            sortParameter.append("desc");
                        }
                        else
                        {
                            sortParameter.append("asc");
                        }
                    }
                    else
                    {
                        throw new IllegalStateException();
                    }

                }
                
                // update request params
                
                ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
                newParams.set("sort", sortParameter.toString());
                req.setParams(newParams);
                this.params = newParams;
            }

            Query query = AlfrescoSolrDataModel.getInstance(id).getCMISQuery(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS, searchParametersAndFilter, indexReader, queryModelQuery, cmisVersion, altDic);
            if(log.isDebugEnabled())
            {
                log.debug("AFTS QP query as lucene:\t    "+query);
            }
            return query;
        }
    }

}
