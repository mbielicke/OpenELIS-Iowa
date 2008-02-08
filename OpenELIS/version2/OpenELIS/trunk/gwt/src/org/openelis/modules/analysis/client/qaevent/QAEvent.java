package org.openelis.modules.analysis.client.qaevent;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

 public class QAEvent extends AppScreenForm {
     private static QAEventServletIntAsync screenService = (QAEventServletIntAsync) GWT
     .create(QAEventServletInt.class);
     
     private static ServiceDefTarget target = (ServiceDefTarget) screenService; 
     
     private TextArea reportingText = null;
     
     
     
     private Widget selected;
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
               reportingText = (TextArea)getWidget("reportingText");
               //reportingText.setText("");               
               reportingText.addClickListener(this);
               
               TableWidget qaEventsTable = (TableWidget)getWidget("qaEventsTable");
               modelWidget.addChangeListener(qaEventsTable.controller); 
               
               //String arr[] = {"Name,Test,Method"}; 
               
               //qaEventsTable.setHeaders(arr);
               ((QAEventsNamesTable)qaEventsTable.controller.manager).setQaEventForm(this);
               
               loadDropdowns();
               super.afterDraw(success);        
               
               bpanel.setButtonState("prev", AppButton.DISABLED);
               bpanel.setButtonState("next", AppButton.DISABLED);
            
        }
         
         public void onClick(Widget sender){
             if (sender == widgets.get("a")) {
                 getQAEvents("a", sender);
             } else if (sender == widgets.get("b")) {
                 getQAEvents("b", sender);
             } else if (sender == widgets.get("c")) {
                 getQAEvents("c", sender);
             } else if (sender == widgets.get("d")) {
                 getQAEvents("d", sender);
             } else if (sender == widgets.get("e")) {
                 getQAEvents("e", sender);
             } else if (sender == widgets.get("f")) {
                 getQAEvents("f", sender);
             } else if (sender == widgets.get("g")) {
                 getQAEvents("g", sender);
             } else if (sender == widgets.get("h")) {
                 getQAEvents("h", sender);
             } else if (sender == widgets.get("i")) {
                 getQAEvents("i", sender);
             } else if (sender == widgets.get("j")) {
                 getQAEvents("j", sender);
             } else if (sender == widgets.get("k")) {
                 getQAEvents("k", sender);
             } else if (sender == widgets.get("l")) {
                 getQAEvents("l", sender);
             } else if (sender == widgets.get("m")) {
                 getQAEvents("m", sender);
             } else if (sender == widgets.get("n")) {
                 getQAEvents("n", sender);
             } else if (sender == widgets.get("o")) {
                 getQAEvents("o", sender);
             } else if (sender == widgets.get("p")) {
                 getQAEvents("p", sender);
             } else if (sender == widgets.get("q")) {
                 getQAEvents("q", sender);
             } else if (sender == widgets.get("r")) {
                 getQAEvents("r", sender);
             } else if (sender == widgets.get("s")) {
                 getQAEvents("s", sender);
             } else if (sender == widgets.get("t")) {
                 getQAEvents("t", sender);
             } else if (sender == widgets.get("u")) {
                 getQAEvents("u", sender);
             } else if (sender == widgets.get("v")) {
                 getQAEvents("v", sender);
             } else if (sender == widgets.get("w")) {
                 getQAEvents("w", sender);
             } else if (sender == widgets.get("x")) {
                 getQAEvents("x", sender);
             } else if (sender == widgets.get("y")) {
                 getQAEvents("y", sender);
             } else if (sender == widgets.get("z")) {
                 getQAEvents("z", sender);
             }
              if(sender == getWidget("reportingText")){
               /* if(bpanel.state == FormInt.ADD){
                 if(reportingText.getText().equals("<Click here to enter reporting text>")){    
                   textEdited = true;
                   reportingText = (TextArea)sender;
                   reportingText.setText("");
                 } 
                } */  
              }
         }
         
         public void commitAdd(){                        
                // Window.alert("super");                                 
                 super.commitAdd();             
                        
         }
         
         public void add(int state){                                  
             super.add(state);
             //reportingText.setText("<Click here to enter reporting text>");
            // reportingText.selectAll();       
         }
         
         private void getQAEvents(String letter, Widget sender) {
             // we only want to allow them to select a letter if they are in display
             // mode..
             if (bpanel.getState() == FormInt.DISPLAY) {

                 FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
                 letterRPC.setFieldValue("name", letter + "*");
                  
                 commitQuery(letterRPC);
                 
                 setStyleNameOnButton(sender);
                 
             }
         }
         
         protected Widget setStyleNameOnButton(Widget sender) {
             sender.addStyleName("current");
             if (selected != null)
                 selected.removeStyleName("current");
             selected = sender;
             return sender;
         }
        
         
        public boolean validate(){
           boolean textEdited = true;  
           if(reportingText.getText()!= null){
              if(reportingText.getText().trim().equals("")){
                  textEdited = false;   
              } 
           }else{
               textEdited = false; 
           }
           
            if(!textEdited){
                StringField repText = (StringField)rpc.getField("reportingText");  
                repText.addError("Reporting text must be specified for a QA Event");                      
              return false;
            }
            return true;
        }
        
     private void loadDropdowns(){
            
            //load state dropdowns
            screenService.getInitialModel("qaEventType", new AsyncCallback(){
                   public void onSuccess(Object result){
                       DataModel typeDataModel = (DataModel)result;
                       ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("qaEventType");
                       ScreenAutoDropdown queryType = displayType.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDataModel);
                       ((AutoCompleteDropdown)queryType.getWidget()).setModel(typeDataModel);
                                              
                   }
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
                });
            
            screenService.getInitialModel("test", new AsyncCallback(){
                public void onSuccess(Object result){
                    DataModel testDataModel = (DataModel)result;
                    ScreenAutoDropdown displayTest = (ScreenAutoDropdown)widgets.get("test");
                    ScreenAutoDropdown queryTest = displayTest.getQueryWidget();
                    
                    ((AutoCompleteDropdown)displayTest.getWidget()).setModel(testDataModel);
                    ((AutoCompleteDropdown)queryTest.getWidget()).setModel(testDataModel);
                                           
                }
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                }
             });
        } 
                  
 }
