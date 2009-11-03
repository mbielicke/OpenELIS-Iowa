package org.openelis.messages;

import org.openelis.persistence.Message;

public class QaEventTypeCacheMessage implements Message {

    private static final long serialVersionUID = 7767666969593649432L;

    private String handler = "org.openelis.server.handlers.ProviderTypeCacheHandler";
    public enum Action {ADDED,UPDATED,DELETED}
    public Action action;
    
    public String getHandler() {
        return handler;
    }

}
