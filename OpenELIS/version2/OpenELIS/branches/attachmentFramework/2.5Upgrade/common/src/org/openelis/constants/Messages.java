package org.openelis.constants;


import org.openelis.manager.MessagesProxy;

public class Messages {
    
    public static OpenELISConstants get() {
        return MessagesProxy.get();
    }
        
    private Messages() {
        
    }

}
