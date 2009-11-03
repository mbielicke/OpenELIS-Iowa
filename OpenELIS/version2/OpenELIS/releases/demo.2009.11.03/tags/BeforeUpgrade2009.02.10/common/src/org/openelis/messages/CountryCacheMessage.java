package org.openelis.messages;

import org.openelis.persistence.Message;

public class CountryCacheMessage implements Message {

    public String handler = "org.openelis.server.handlers.CountryCacheHandler";
    public enum Action {ADDED,UPDATED,DELETED}
    public Action action;
    
    public String getHandler() {
        // TODO Auto-generated method stub
        return handler;
    }

}
