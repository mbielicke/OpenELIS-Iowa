package org.openelis.modules.utilities.client.dictionary;



import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class Dictionary extends OpenELISScreenForm implements MouseListener{

    private TableWidget dictEntryTable  = null;
    
    public Dictionary(){
        super("org.openelis.modules.utilities.server.DictionaryServlet");        
    }
    
    private Widget selected;
    
    public void afterDraw(boolean success) {
      //try{ 
        bpanel = (ButtonPanel) getWidget("buttons");        
        message.setText("done");  
        
        TableWidget categoryTable = (TableWidget)getWidget("categoryTable");
        modelWidget.addChangeListener(categoryTable.controller);
        
        
        ((CategorySystemNamesTable) categoryTable.controller.manager).setDictionaryForm(this);
        
        dictEntryTable = (TableWidget)getWidget("dictEntTable");
        ((DictionaryEntriesTable) dictEntryTable.controller.manager).setDictionaryForm(this);
                
        dictEntryTable.controller.setAutoAdd(false);
        
        //Button removeEntryButton = (Button) getWidget("removeEntryButton");
        //removeEntryButton.setEnabled(false);
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");        
        removeEntryButton.addClickListener(this);
        removeEntryButton.changeState(AppButton.DISABLED);                                     
        
        super.afterDraw(success);
        
        bpanel.setButtonState("prev", AppButton.DISABLED);
        bpanel.setButtonState("next", AppButton.DISABLED);
        
        loadDropdowns();
      
    }
    
    public void up(int state){
                
        dictEntryTable.controller.setAutoAdd(true);
        
        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
        dictEntManager.resetLists();
        //dictEntManager.thiscontroller = dictEntryTable.controller;
        dictEntManager.createLists(dictEntryTable.controller);
        super.up(state);
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
        removeEntryButton.changeState(AppButton.UNPRESSED);                          
        
		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
    }
    
    public void commitAdd(){

          dictEntryTable.controller.unselect(-1);  
                 
          super.commitAdd();
         AppButton removeEntryButton = (AppButton) widgets.get("removeEntryButton");
         removeEntryButton.changeState(AppButton.DISABLED); 
         
         
         dictEntryTable.controller.setAutoAdd(false);             
    }
    
    public void commitUpdate(){        
        
            dictEntryTable.controller.unselect(-1);   
            
           /* for(int iter = 0; iter < dictEntryTable.controller.model.numRows(); iter++){
               StringField field = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 3);   
                Window.alert((String)field.getValue());
             TableRow row = dictEntryTable.controller.model.getRow(iter);
              if(row.getHidden("deleteFlag")!=null){
                Window.alert((String)row.getHidden("deleteFlag").getValue())  ;
              }
            }*/
            
            super.commitUpdate();
            AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
            removeEntryButton.changeState(AppButton.DISABLED);  
                        
            dictEntryTable.controller.setAutoAdd(false);
          
             
    }
    
    public void abort(int state){
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
        removeEntryButton.changeState(AppButton.DISABLED);     
       try{        
                
        dictEntryTable.controller.setAutoAdd(false);
        
        super.abort(state);
        
       //  need to get the category name table model
        TableWidget catNameTM = (TableWidget) getWidget("categoryTable");
        int rowSelected = catNameTM.controller.selected;               

        // set the update button if needed
        if (rowSelected == -1){
            bpanel.setButtonState("update", AppButton.DISABLED);
            bpanel.setButtonState("prev", AppButton.DISABLED);
            bpanel.setButtonState("next", AppButton.DISABLED);
        }
       }catch(Exception ex){
           Window.alert(ex.getMessage());
       }
    }
    
    public void onClick(Widget sender){
    	String action = ((AppButton)sender).action;
		if(action.startsWith("query:")){
			getCategories(action.substring(6, action.length()), sender);
			
		}else if (action.equals("removeEntry")) {   
            
            TableWidget dictEntTable = (TableWidget) getWidget("dictEntTable");
            int selectedRow = dictEntTable.controller.selected;
            if (selectedRow > -1
                    && dictEntTable.controller.model.numRows() > 1) {
                TableRow row = dictEntTable.controller.model
                        .getRow(selectedRow);
                
                row.setShow(false);
                // delete the last row of the table because it is autoadd
                
                dictEntTable.controller.model
                        .deleteRow(dictEntTable.controller.model.numRows() - 1);
                
                // reset the model
                dictEntTable.controller.reset();
                // need to set the deleted flag to "Y" also
                StringField deleteFlag = new StringField();
                deleteFlag.setValue("Y");

                row.addHidden("deleteFlag", deleteFlag);
            }  
        }        
    }
    
        private void getCategories(String letter, Widget sender) {
            // we only want to allow them to select a letter if they are in display
            // mode..
            if (bpanel.getState() == FormInt.DISPLAY) {

                FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
                letterRPC.setFieldValue("name", letter.toUpperCase() + "*"+" | " + letter.toLowerCase() + "*");  
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
        
       public void add(int state){                                  
           dictEntryTable.controller.setAutoAdd(true);           
          
           DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
           dictEntManager.resetLists();
           
           super.add(state);                                  
            //Button removeEntryButton = (Button) getWidget("removeEntryButton");
           // removeEntryButton.setEnabled(true);
            
            AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
            removeEntryButton.changeState(AppButton.UNPRESSED);  
            
           // set focus to the name field
    		TextBox name = (TextBox)getWidget("name");
    		name.setFocus(true);
            
            TableWidget catNameTM = (TableWidget) getWidget("categoryTable");
             catNameTM.controller.unselect(-1);               
           
        }
        
        public void query(int state){             
            super.query(state);
            
    		//set focus to the name field
    		TextBox name = (TextBox)getWidget("name");
    		name.setFocus(true);
            
            AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
            removeEntryButton.changeState(AppButton.DISABLED);  
        }
        
        
        
        public void checkSystemName(Integer id,String systemName, int row){
            final Integer entryId = id; 
            //sysNameExists = false;
            final int trow = row;
            screenService.getObject(systemName, null, new AsyncCallback() {
                boolean hasError = false;
                public void onSuccess(Object result) {                                         
                    if(result != null){
                      if(entryId !=null){                           
                       if(!entryId.equals(result)) {                            
                           hasError = true;                            
                       }
                      }else{
                          hasError = true;                    
                      }  
                    }       
                      
                    if(hasError){                        
                        StringField snfield = (StringField)dictEntryTable.controller.model.getFieldAt(trow, 1);
                       // snfield.addError(constants.getString("dictSystemNameError"));
                        ((TableCellInputWidget)dictEntryTable.controller.view.table.getWidget(trow,1)).drawErrors();                        
                    }                                           
     
                 }                
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                 });            
            
        }
        
        public void checkEntry(Integer id,String entry, int row){
            final Integer entryId = id; 
            final int trow = row;
            // entryExists = false;
            screenService.getObject(entry, null, new AsyncCallback() {
                boolean hasError = false;
                public void onSuccess(Object result) {
                   
                    if(result != null){
                        if(entryId !=null){                              
                         if(!entryId.equals(result)) {                       
                             hasError = true;                            
                         }
                        }else{
                            hasError = true;                    
                        }  
                      }                                                                                                   
                                  
                    if(hasError){
                        
                        StringField efield = (StringField)dictEntryTable.controller.model.getFieldAt(trow, 3); 
                        //efield.addError(constants.getString("dictEntryError"));
                        ((TableCellInputWidget)dictEntryTable.controller.view.table.getWidget(trow,3)).drawErrors();
                       
                    }
                   }                 
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                 });          
           
        }
            
                                      
        
        
        private void loadDropdowns(){
           StringObject catObj = new StringObject();
            catObj.setValue("section");
            DataObject[] args = new DataObject[] {catObj}; 
            
            screenService.getObject("getModelField", args, new AsyncCallback(){
                   public void onSuccess(Object result){
                       ModelField field = (ModelField)result;
                       DataModel stateDataModel = (DataModel)field.getValue();
                       ScreenAutoDropdown displaySection = (ScreenAutoDropdown)widgets.get("section");
                       ScreenAutoDropdown querySection = displaySection.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displaySection.getWidget()).setModel(stateDataModel);
                       ((AutoCompleteDropdown)querySection.getWidget()).setModel(stateDataModel);
                                              
                   }
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
                });
            
            catObj = new StringObject();
            catObj.setValue("isActive");
            args = new DataObject[] {catObj}; 
            
            screenService.getObject("getModelField", args, new AsyncCallback(){
                public void onSuccess(Object result){
                    ModelField field = (ModelField)result;
                    DataModel activeDataModel = (DataModel)field.getValue();                   
                    
                    ScreenTableWidget displayEntryTable = (ScreenTableWidget)widgets.get("dictEntTable");
                    ScreenTableWidget queryEntryTable = (ScreenTableWidget)displayEntryTable.getQueryWidget();
                                      
                    
                    TableAutoDropdown queryContactActive = (TableAutoDropdown)((TableWidget)queryEntryTable.getWidget()).
                         controller.editors[0];
                    queryContactActive.setModel(activeDataModel);
                }
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                }
             });
            
        }
                 
                             
        public boolean validate(){              
              
              boolean entryError = false;
              boolean sysNameError = false;
           for(int iter = 0; iter < dictEntryTable.controller.model.numRows(); iter++){
               StringField snfield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 1);
               StringField efield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 3);
               
               if(!(efield.getErrors().length==0)){
                  // entryExists = true;       
                   entryError = true; 
               }
               if(!(snfield.getErrors().length==0)){
                  // sysNameExists = true;       
                   sysNameError = true;
               }
           }  
           
            if(entryError || sysNameError){ 
                return false;
            } 
            else {
                return true;
            }
         }
}
