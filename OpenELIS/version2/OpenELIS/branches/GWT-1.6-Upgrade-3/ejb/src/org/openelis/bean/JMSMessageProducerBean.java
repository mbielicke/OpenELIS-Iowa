package org.openelis.bean;

import org.openelis.local.JMSMessageProducerLocal;

import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

@Stateless
public class JMSMessageProducerBean implements JMSMessageProducerLocal{
    
    @Resource SessionContext context;

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeMessage(org.openelis.persistence.Message msg) {
        Connection connect = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            ConnectionFactory cf = (ConnectionFactory)context.lookup("ConnectionFactory");
            Topic topic = (Topic)context.lookup("topic/openelisTopic");
            connect = cf.createConnection();
            session = connect.createSession(false,Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(topic);
            producer.send(session.createObjectMessage(msg)); 
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                producer.close();
                session.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
