package org.openelis.local;

import org.openelis.persistence.Message;

import javax.ejb.Local;

@Local
public interface JMSMessageProducerLocal {
    
    public void writeMessage(Message msg);
    
}
