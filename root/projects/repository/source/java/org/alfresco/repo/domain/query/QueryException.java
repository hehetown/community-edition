package org.alfresco.repo.domain.query;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Exception generated by failures to execute canned queries.
 * 
 * @author Derek Hulley
 * @since 3.5
 */
public class QueryException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -7827116537885580234L;

    /**
     * @param msg           the message
     */
    public QueryException(String msg)
    {
        super(msg);
    }

    /**
     * @param msg           the message
     * @param cause         the exception cause
     */
    public QueryException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     */
    public QueryException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     * @param cause         the exception cause
     */
    public QueryException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }
}
