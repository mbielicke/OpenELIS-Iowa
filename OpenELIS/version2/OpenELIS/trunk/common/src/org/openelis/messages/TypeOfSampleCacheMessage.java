package org.openelis.messages;

import org.openelis.persistence.Message;

public class TypeOfSampleCacheMessage implements Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String handler = "org.openelis.server.handlers.TypeOfSampleCacheHandler";
    public enum Action {ADDED,UPDATED,DELETED}
    public Action action;
    
    public String getHandler() {       
        return handler;
    }

}
