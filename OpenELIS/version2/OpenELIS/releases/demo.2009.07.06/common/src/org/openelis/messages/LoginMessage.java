package org.openelis.messages;

import org.openelis.persistence.Message;

public class LoginMessage implements Message {

    public String handler = "org.openelis.server.handlers.LoginMessageHandler";
    public String user;
    public String timestamp;
    
    public String getHandler() {
        // TODO Auto-generated method stub
        return handler;
    }

}
