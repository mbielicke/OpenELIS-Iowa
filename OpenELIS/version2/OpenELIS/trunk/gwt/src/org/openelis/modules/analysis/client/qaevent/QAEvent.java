package org.openelis.modules.analysis.client.qaevent;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

 public class QAEvent extends OpenELISScreenForm {

     private Widget selected;

     public QAEvent(){
         super("org.openelis.modules.analysis.server.QAEventServlet");                   
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
        	
//        	set focus to the name field
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
        
                 
        
     private void loadDropdowns(){
            
            //load state dropdowns
            StringObject catObj = new StringObject();
            catObj.setValue("qaEventType");
            DataObject[] args = new DataObject[] {catObj};            
            
            screenService.getObject("getModelField", args, new AsyncCallback(){
                   public void onSuccess(Object result){
                       ModelField field = (ModelField)result;
                       DataModel typeDataModel = (DataModel)field.getValue();
                       ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("qaEventType");
                       ScreenAutoDropdown queryType = displayType.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDataModel);
                       ((AutoCompleteDropdown)queryType.getWidget()).setModel(typeDataModel);
                                              
                   }
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
                });
            
            catObj = new StringObject();
            catObj.setValue("test");
            args = new DataObject[] {catObj};
            
            screenService.getObject("getModelField", args, new AsyncCallback(){
                public void onSuccess(Object result){
                    ModelField field = (ModelField)result;
                    DataModel testDataModel = (DataModel)field.getValue();
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
