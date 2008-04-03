package org.openelis.modules.qaevent.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

 public class QAEventScreen extends OpenELISScreenForm {

     private Widget selected = null;
     
     private TableWidget qaEventsTable = null;
     private TextBox tname = null;
     private ScreenAutoDropdown displayType = null;
     private ScreenAutoDropdown displayTest = null;
     
     private static boolean loaded = false;
     
     private static DataModel qaEventTypeDropDown = null;
     private static DataModel testDropDown = null;
     
       
     public QAEventScreen(){
         super("org.openelis.modules.qaevent.server.QAEventService",!loaded);  
         name="QA Event";
     }
     
         public void afterDraw(boolean success) {
             loaded = true;
               bpanel = (ButtonPanel) getWidget("buttons");        
               message.setText("done");                 
               
               //qaEventsTable = (TableWidget)getWidget("qaEventsTable");
               //modelWidget.addChangeListener(qaEventsTable.controller); 
               AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
               modelWidget.addChangeListener(atozTable);
               addChangeListener(atozTable);
               
               //((QAEventsNamesTable)qaEventsTable.controller.manager).setQaEventForm(this);
               
               ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
               atozButtons.addChangeListener(this);
               
               tname = (TextBox)getWidget("qaevent.name");
               displayType = (ScreenAutoDropdown)widgets.get("qaevent.type");
               displayTest = (ScreenAutoDropdown)widgets.get("qaevent.test");

               super.afterDraw(success);        
               
               loadDropdowns();                           
        }
         
         /*public void onClick(Widget sender){
             String action = ((AppButton)sender).action;
            if(action.startsWith("query:")){
                getQAEvents(action.substring(6, action.length()), sender);
                
            } 
         }*/
         
         public void onChange(Widget sender) {
             
             if(sender == getWidget("atozButtons")){           
                String action = ((ButtonPanel)sender).buttonClicked.action;           
                if(action.startsWith("query:")){
                    getQAEvents(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
                }
             }else{
                 super.onChange(sender);
             }
         }
         
        public void query() {
            super.query();
            
         //set focus to the name field
             
            tname.setFocus(true);
        }                 
                      
         
         public void add(){                                  
             super.add();             
            //TextBox name = (TextBox)getWidget("name");
            tname.setFocus(true);
            
            //TableWidget catNameTM = (TableWidget) getWidget("qaEventsTable");
            qaEventsTable.controller.unselect(-1);
         }
         
        public void afterUpdate(boolean success) {
            super.afterUpdate(success);
            
            //set focus to the name field
            //TextBox name = (TextBox)getWidget("name");
            tname.setFocus(true);
        }
         
         private void getQAEvents(String letter, Widget sender) {
             // we only want to allow them to select a letter if they are in display
             // mode..
             if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

                 FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
                 letterRPC.setFieldValue("qaevent.name", letter.toLowerCase() + "*");
                  
                 commitQuery(letterRPC);
                 
                 //setStyleNameOnButton(sender);
                 
             }
         }
         
     	protected void setStyleNameOnButton(Widget sender) {
    		((AppButton)sender).changeState(AppButton.PRESSED);
    		if (selected != null)
    			((AppButton)selected).changeState(AppButton.UNPRESSED);
    		selected = sender;
    	}                
        
     private void loadDropdowns(){
            
            //load type and test dropdowns
               if(qaEventTypeDropDown == null){
                   qaEventTypeDropDown = (DataModel)initData[0];               
                   testDropDown = (DataModel)initData[1];
               }       

                    //ScreenAutoDropdown queryType = displayType.getQueryWidget();                       
                    ((AutoCompleteDropdown)displayType.getWidget()).setModel(qaEventTypeDropDown);
                    //((AutoCompleteDropdown)queryType.getWidget()).setModel(qaEventTypeDropDown);
                                                                                                          
                    //ScreenAutoDropdown queryTest = displayTest.getQueryWidget();                    
                    ((AutoCompleteDropdown)displayTest.getWidget()).setModel(testDropDown);
                    //((AutoCompleteDropdown)queryTest.getWidget()).setModel(testDropDown);
                                           
                }
                
         
       public void afterCommitAdd(boolean success){
           Integer qaeId = (Integer)rpc.getFieldValue("qaevent.id");
           NumberObject qaeIdObj = new NumberObject();
           qaeIdObj.setType("integer");
           qaeIdObj.setValue(qaeId);           
           
           //done because key is set to null in AppScreenForm for the add operation 
           if(key ==null){  
            key = new DataSet();
            key.addObject(qaeIdObj);
           }
           else{
               key.setObject(0,qaeIdObj);
           }
           
           super.afterCommitAdd(success);
       }
                          
              
 }