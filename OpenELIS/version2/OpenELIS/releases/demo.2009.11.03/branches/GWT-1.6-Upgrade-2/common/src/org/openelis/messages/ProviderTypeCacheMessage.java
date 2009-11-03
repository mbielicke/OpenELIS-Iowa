package org.openelis.messages;

import org.openelis.persistence.Message;

public class ProviderTypeCacheMessage implements Message {

    private static final long serialVersionUID = 6017911048564923335L;

    private String handler = "org.openelis.server.handlers.ProviderTypeCacheHandler";
    public enum Action {ADDED,UPDATED,DELETED}
    public Action action;
    
    public String getHandler() {       
        return handler;
    }

}
