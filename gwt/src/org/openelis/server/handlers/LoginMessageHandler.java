package org.openelis.server.handlers;

import org.openelis.messages.LoginMessage;
import org.openelis.persistence.MessageHandler;

public class LoginMessageHandler implements MessageHandler<LoginMessage> {

    public void handle(LoginMessage msg) {
        System.out.println(msg.user + " logged in at " + msg.timestamp);
    }


}
