package org.openelis.client.dataEntry.screen.dictionary;


import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.widget.ButtonPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class Dictionary extends AppScreenForm{
    private static DictionaryServletIntAsync screenService = (DictionaryServletIntAsync) GWT
    .create(DictionaryServletInt.class);
    
    private static ServiceDefTarget target = (ServiceDefTarget) screenService;
    
    public Dictionary(){
        super();        
       try{         
        String base = GWT.getModuleBaseURL();
        base += "DictionaryServlet";        
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;        
        getXML(); 
       }catch(Exception ex){
           Window.alert(ex.getMessage());
       } 
    }
    
    public void afterDraw(boolean success) {
      //try{ 
        bpanel = (ButtonPanel) getWidget("buttons");        
        message.setText("done");        
        super.afterDraw(success);
        
      // }catch(Exception ex){
       //    ex.printStackTrace();
       //    Window.alert(ex.getMessage());
      // } 
    }
    
    
}
