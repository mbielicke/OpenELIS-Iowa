package org.openelis.modules.qaevent.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

 public class QAEventScreen extends OpenELISScreenForm {

     private Widget selected;

     public QAEventScreen(){
         super("org.openelis.modules.qaevent.server.QAEventService",true);  
         name="QA Event";
     }
     
         public void afterDraw(boolean success) {                  
               bpanel = (ButtonPanel) getWidget("buttons");        
               message.setText("done");                 
               
               TableWidget qaEventsTable = (TableWidget)getWidget("qaEventsTable");
               modelWidget.addChangeListener(qaEventsTable.controller); 
                              
               ((QAEventsNamesTable)qaEventsTable.controller.manager).setQaEventForm(this);
               
               loadDropdowns();
               super.afterDraw(success);        
               
               bpanel.setButtonState("prev", AppButton.DISABLED);
               bpanel.setButtonState("next", AppButton.DISABLED);               
            
        }
         
         public void onClick(Widget sender){
             String action = ((AppButton)sender).action;
            if(action.startsWith("query:")){
                getQAEvents(action.substring(6, action.length()), sender);
                
            } 
         }
         
        public void query(int state) {
            super.query(state);
            
//          set focus to the name field
            TextBox name = (TextBox)getWidget("name");
            name.setFocus(true);
        }                 
         
         public void abort(int state){
             super.abort(state);
             // need to get the provider name table model
             TableWidget catNameTM = (TableWidget) getWidget("qaEventsTable");
             int rowSelected = catNameTM.controller.selected;               

             // set the update button if needed
             if (rowSelected == -1){
                 bpanel.setButtonState("update", AppButton.DISABLED);
                 bpanel.setButtonState("prev", AppButton.DISABLED);
                 bpanel.setButtonState("next", AppButton.DISABLED);
             }
                          
         }
         
         public void add(int state){                                  
             super.add(state);             
            TextBox name = (TextBox)getWidget("name");
            name.setFocus(true);
            
            TableWidget catNameTM = (TableWidget) getWidget("qaEventsTable");
            catNameTM.controller.unselect(-1);
         }
         
        public void afterUpdate(boolean success) {
            super.afterUpdate(success);
            
//          set focus to the name field
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
         
     	protected void setStyleNameOnButton(Widget sender) {
    		((AppButton)sender).changeState(AppButton.PRESSED);
    		if (selected != null)
    			((AppButton)selected).changeState(AppButton.UNPRESSED);
    		selected = sender;
    	}                
        
     private void loadDropdowns(){
            
            //load type and test dropdowns
                        
            DataModel qaEventTypeDropDown = (DataModel)initData[0];
            DataModel testDropDown = (DataModel)initData[1];

                       ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("qaEventType");
                       ScreenAutoDropdown queryType = displayType.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displayType.getWidget()).setModel(qaEventTypeDropDown);
                       ((AutoCompleteDropdown)queryType.getWidget()).setModel(qaEventTypeDropDown);
                                              
                                         
                    ScreenAutoDropdown displayTest = (ScreenAutoDropdown)widgets.get("test");
                    ScreenAutoDropdown queryTest = displayTest.getQueryWidget();
                    
                    ((AutoCompleteDropdown)displayTest.getWidget()).setModel(testDropDown);
                    ((AutoCompleteDropdown)queryTest.getWidget()).setModel(testDropDown);
                                           
                }
                
         
       public void afterCommitAdd(boolean success){
           Integer qaeId = (Integer)rpc.getFieldValue("qaeId");
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