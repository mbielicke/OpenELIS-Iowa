package org.openelis.client.analysis.screen.qaevent;

import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.widget.ButtonPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

 public class QAEvent extends AppScreenForm {
     private static QAEventServletIntAsync screenService = (QAEventServletIntAsync) GWT
     .create(QAEventServletInt.class);
     
     private static ServiceDefTarget target = (ServiceDefTarget) screenService; 
     
     public QAEvent(){
         super();        
         
         String base = GWT.getModuleBaseURL();
         base += "QAEventServlet";        
         target.setServiceEntryPoint(base);
         service = screenService;
         formService = screenService;        
         getXML(); 
     }
     
         public void afterDraw(boolean success) {             
               bpanel = (ButtonPanel) getWidget("buttons");        
               message.setText("done");  
               super.afterDraw(success);
        }
 }
