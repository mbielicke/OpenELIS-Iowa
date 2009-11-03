package org.openelis.messages;

import org.openelis.persistence.Message;

public class ContactTypeCacheMessage implements Message {

    public String handler = "org.openelis.server.handlers.ContactTypeCacheHandler";
    public enum Action {ADDED,UPDATED,DELETED}
    public Action action;
    
    public String getHandler() {
        // TODO Auto-generated method stub
        return handler;
    }

}
