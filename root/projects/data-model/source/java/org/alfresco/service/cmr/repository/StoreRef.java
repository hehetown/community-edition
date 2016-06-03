package org.alfresco.service.cmr.repository;

import java.io.Serializable;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Reference to a node store
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public final class StoreRef implements EntityRef, Serializable
{
    private static final long serialVersionUID = 3905808565129394486L;

    public static final String PROTOCOL_WORKSPACE = "workspace";
    public static final String PROTOCOL_ARCHIVE = "archive";
    public static final String PROTOCOL_AVM = "avm";
    public static final String PROTOCOL_TEST = "test";
    public static final String PROTOCOL_DELETED = "deleted";
    
    public static final StoreRef STORE_REF_WORKSPACE_SPACESSTORE = new StoreRef(PROTOCOL_WORKSPACE, "SpacesStore");
    public static final StoreRef STORE_REF_ARCHIVE_SPACESSTORE = new StoreRef(PROTOCOL_ARCHIVE, "SpacesStore");
    
    public static final String URI_FILLER = "://";

    private final String protocol;
    private final String identifier;

    /**
     * @param protocol
     *            well-known protocol for the store, e.g. <b>workspace</b> or
     *            <b>versionstore</b>
     * @param identifier
     *            the identifier, which may be specific to the protocol
     */
    public StoreRef(String protocol, String identifier)
    {
        if (protocol == null)
        {
            throw new IllegalArgumentException("Store protocol may not be null");
        }
        if (identifier == null)
        {
            throw new IllegalArgumentException("Store identifier may not be null");
        }

        this.protocol = protocol;
        this.identifier = identifier;
    }

    public StoreRef(String string)
    {
        int dividerPatternPosition = string.indexOf(URI_FILLER);
        if(dividerPatternPosition == -1)
        {
            throw new AlfrescoRuntimeException("Invalid store ref: Does not contain " + URI_FILLER + "   " + string);
        }
        this.protocol = string.substring(0, dividerPatternPosition);
        this.identifier = string.substring(dividerPatternPosition+3);
    }

    public String toString()
    {
        return protocol + URI_FILLER + identifier;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof StoreRef)
        {
            StoreRef that = (StoreRef) obj;
            return (this.protocol.equals(that.protocol)
                    && this.identifier.equals(that.identifier));
        } else
        {
            return false;
        }
    }
    
    /**
     * Creates a hashcode from both the {@link #getProtocol()} and {@link #getIdentifier()}
     */
    public int hashCode()
    {
        return (protocol.hashCode() + identifier.hashCode());
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getIdentifier()
    {
        return identifier;
    }
}
