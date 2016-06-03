package org.alfresco.repo.search.impl.noindex;

import java.util.Collection;

import org.alfresco.error.StackTraceUtil;
import org.alfresco.repo.search.Indexer;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A no action indexer - the indexing is done automatically along with
 * persistence
 * 
 * TODO: Rename to Adaptor?
 * 
 * @author andyh
 * 
 */
public class NoIndexIndexer implements Indexer
{
    
    private static Log s_logger = LogFactory.getLog(NoIndexIndexer.class);
    
    
    public void setReadThrough(boolean isReadThrough)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("setReadThrough = "+isReadThrough);
        }
        trace();
        return;
    }

    private void trace()
    {
        if(s_logger.isTraceEnabled())
        {
            Exception e = new Exception();
            e.fillInStackTrace();

            StringBuilder sb = new StringBuilder(1024);
            StackTraceUtil.buildStackTrace(
                    "Index trace ...",
                    e.getStackTrace(),
                    sb,
                    -1);
            s_logger.trace(sb);
        }
    }
    
    public void createNode(ChildAssociationRef relationshipRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("createNode = "+relationshipRef);
        }
        trace();
        return;
    }

    public void updateNode(NodeRef nodeRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("updateNode = "+nodeRef);
        }
        trace();
        return;
    }

    public void deleteNode(ChildAssociationRef relationshipRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("deleteNode = "+relationshipRef);
        }
        trace();
        return;
    }

    public void createChildRelationship(ChildAssociationRef relationshipRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("createChildRelationship = "+relationshipRef);
        }
        trace();
        return;
    }

    public void updateChildRelationship(ChildAssociationRef relationshipBeforeRef, ChildAssociationRef relationshipAfterRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("updateChildRelationship = "+relationshipBeforeRef+ " -> "+relationshipAfterRef);
        }
        trace();
        return;
    }

    public void deleteChildRelationship(ChildAssociationRef relationshipRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("deleteChildRelationship = "+relationshipRef);
        }
        trace();
        return;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.Indexer#detectNodeChanges(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.search.SearchService, java.util.Collection, java.util.Collection, java.util.Collection, java.util.Collection)
     */
    @Override
    public void detectNodeChanges(NodeRef nodeRef, SearchService searcher,
            Collection<ChildAssociationRef> addedParents, Collection<ChildAssociationRef> deletedParents,
            Collection<ChildAssociationRef> createdNodes, Collection<NodeRef> updatedNodes)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("detectNodeChanges = "+nodeRef);
        }
        trace();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.Indexer#deleteIndex(org.alfresco.service.cmr.repository.StoreRef)
     */
    public void deleteIndex(StoreRef storeRef)
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("deleteIndex = "+storeRef);
        }
        trace();
        return;
    }

    public void flushPending()
    {
        if(s_logger.isDebugEnabled())
        {
            s_logger.debug("flushPending");
        }
        trace();
        return;
    }
}
