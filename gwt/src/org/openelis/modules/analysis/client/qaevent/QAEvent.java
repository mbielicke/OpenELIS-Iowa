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
import com.google.gwt.user.client.ui.TextBox;
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
             if (sender == getWidget("a")) {
                 getQAEvents("a", sender);
             } else if (sender == getWidget("b")) {
                 getQAEvents("b", sender);
             } else if (sender == getWidget("c")) {
                 getQAEvents("c", sender);
             } else if (sender == getWidget("d")) {
                 getQAEvents("d", sender);
             } else if (sender == getWidget("e")) {
                 getQAEvents("e", sender);
             } else if (sender == getWidget("f")) {
                 getQAEvents("f", sender);
             } else if (sender == getWidget("g")) {
                 getQAEvents("g", sender);
             } else if (sender == getWidget("h")) {
                 getQAEvents("h", sender);
             } else if (sender == getWidget("i")) {
                 getQAEvents("i", sender);
             } else if (sender == getWidget("j")) {
                 getQAEvents("j", sender);
             } else if (sender == getWidget("k")) {
                 getQAEvents("k", sender);
             } else if (sender == getWidget("l")) {
                 getQAEvents("l", sender);
             } else if (sender == getWidget("m")) {
                 getQAEvents("m", sender);
             } else if (sender == getWidget("n")) {
                 getQAEvents("n", sender);
             } else if (sender == getWidget("o")) {
                 getQAEvents("o", sender);
             } else if (sender == getWidget("p")) {
                 getQAEvents("p", sender);
             } else if (sender == getWidget("q")) {
                 getQAEvents("q", sender);
             } else if (sender == getWidget("r")) {
                 getQAEvents("r", sender);
             } else if (sender == getWidget("s")) {
                 getQAEvents("s", sender);
             } else if (sender == getWidget("t")) {
                 getQAEvents("t", sender);
             } else if (sender == getWidget("u")) {
                 getQAEvents("u", sender);
             } else if (sender == getWidget("v")) {
                 getQAEvents("v", sender);
             } else if (sender == getWidget("w")) {
                 getQAEvents("w", sender);
             } else if (sender == getWidget("x")) {
                 getQAEvents("x", sender);
             } else if (sender == getWidget("y")) {
                 getQAEvents("y", sender);
             } else if (sender == getWidget("z")) {
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
         
        public void query(int state) {
        	super.query(state);
        	
//        	set focus to the name field
    		TextBox name = (TextBox)getWidget("name");
    		name.setFocus(true);
        }
         
         public void commitAdd(){                        
                // Window.alert("super");                                 
                 super.commitAdd();             
                        
         }
         
         public void add(int state){                                  
             super.add(state);
             //reportingText.setText("<Click here to enter reporting text>");
            // reportingText.selectAll(); 
             
//         	set focus to the name field
     		TextBox name = (TextBox)getWidget("name");
     		name.setFocus(true);
         }
         
        public void afterUpdate(boolean success) {
        	super.afterUpdate(success);
        	
//        	set focus to the name field
    		TextBox name = (TextBox)getWidget("name");
    		name.setFocus(true);
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
