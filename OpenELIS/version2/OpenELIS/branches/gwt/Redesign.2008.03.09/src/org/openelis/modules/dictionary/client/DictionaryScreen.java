package org.openelis.modules.dictionary.client;




import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
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
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class DictionaryScreen extends OpenELISScreenForm implements MouseListener{

    private TableWidget dictEntryTable  = null;
    //private HashMap hmap = null;
    
    public DictionaryScreen(){
        super("org.openelis.modules.dictionary.server.DictionaryService",true); 
        name = "Dictionary";
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
        
        
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");        
        removeEntryButton.addClickListener(this);
        removeEntryButton.changeState(AppButton.DISABLED);                                     
        
        super.afterDraw(success);        
        
        bpanel.setButtonState("prev", AppButton.DISABLED);
        bpanel.setButtonState("next", AppButton.DISABLED);
        
        loadDropdowns();
        //ConstantMap cmap = (ConstantMap)initData[3];
        //hmap = (HashMap)cmap.getValue(); 
        
      
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
    
    public void afterCommitAdd(boolean success){
        Integer categoryId = (Integer)rpc.getFieldValue("categoryId");
        NumberObject categoryIdObj = new NumberObject();
        categoryIdObj.setType("integer");
        categoryIdObj.setValue(categoryId);           
        
        //done because key is set to null in AppScreenForm for the add operation 
        if(key ==null){  
         key = new DataSet();
         key.addObject(categoryIdObj);
        }
        else{
            key.setObject(0,categoryIdObj);
        }
         super.afterCommitAdd(success);
    }
    
    public void commitUpdate(){        
        
            dictEntryTable.controller.unselect(-1);   
                                   
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
        
    	protected void setStyleNameOnButton(Widget sender) {
    		((AppButton)sender).changeState(AppButton.PRESSED);
    		if (selected != null)
    			((AppButton)selected).changeState(AppButton.UNPRESSED);
    		selected = sender;
    	}
        
       public void add(int state){                                  
           dictEntryTable.controller.setAutoAdd(true);           
          
           DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
           dictEntManager.resetLists();
           
           super.add(state);                                              
            
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
            
            final int trow = row;
            StringObject sysNameObj = new StringObject();
            sysNameObj.setValue(systemName);
            
            DataObject[] args = {sysNameObj};
            screenService.getObject("getEntryIdForSystemName", args, new AsyncCallback() {
                boolean hasError = false;
                public void onSuccess(Object result) {                      
                    NumberField idField  = (NumberField)result;
                    Integer retId = (Integer)idField.getValue();
                    if(retId != null){
                      if(entryId !=null){                           
                       if(!entryId.equals(retId)) {                            
                           hasError = true;                            
                       }
                      }else{
                          hasError = true;                    
                      }  
                    }                                                
                    
                    if(hasError){                                                
                        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
                        String dictSystemNameError = "An entry with this System Name already exists in the database.Please choose some other name";
                        //String dictSystemNameError = (String)(hmap.get("dictSystemNameError"));
                        dictEntManager.showError(trow, 1, dictEntryTable.controller, dictSystemNameError);
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
            StringObject entryObj = new StringObject();
            entryObj.setValue(entry);
            
            DataObject[] args = {entryObj};
            
            screenService.getObject("getEntryIdForEntry", args, new AsyncCallback() {
                boolean hasError = false;
                public void onSuccess(Object result) {
                    NumberField idField  = (NumberField)result;
                    Integer retId = (Integer)idField.getValue();
                    if(retId != null){
                        if(entryId !=null){                              
                         if(!entryId.equals(retId)) {                       
                             hasError = true;                            
                         }
                        }else{
                            hasError = true;                    
                        }  
                      }                                                                                                   
                                  
                    if(hasError){                                               
                        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
                        String dictEntryError = "An entry with this Entry text already exists in the database.Please choose some other text";
                        //String dictEntryError = (String)(hmap.get("dictEntryError"));
                        dictEntManager.showError(trow, 3, dictEntryTable.controller, dictEntryError); 
                    }
                   }                 
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                 });          
           
        }
            
                                      
        
        
        private void loadDropdowns(){           
            
            DataModel sectionDropDown = (DataModel)initData[0];
            DataModel activeDropDown = (DataModel)initData[1];
            
                       ScreenAutoDropdown displaySection = (ScreenAutoDropdown)widgets.get("section");
                       ScreenAutoDropdown querySection = displaySection.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displaySection.getWidget()).setModel(sectionDropDown);
                       ((AutoCompleteDropdown)querySection.getWidget()).setModel(sectionDropDown);
                                              
                   
                    ScreenTableWidget displayEntryTable = (ScreenTableWidget)widgets.get("dictEntTable");
                    ScreenTableWidget queryEntryTable = (ScreenTableWidget)displayEntryTable.getQueryWidget();
                                      
                    
                    TableAutoDropdown queryContactActive = (TableAutoDropdown)((TableWidget)queryEntryTable.getWidget()).
                         controller.editors[0];
                    queryContactActive.setModel(activeDropDown);
                
            
        }
                 
                             
        public boolean validate(){              
              
              boolean entryError = false;
              boolean sysNameError = false;
           for(int iter = 0; iter < dictEntryTable.controller.model.numRows(); iter++){
               StringField snfield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 1);
               StringField efield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 3);
                if(efield.getValue()!=null){
                  if((efield.getValue().toString().trim().equals(""))){
                      efield.addError("Field is required");
                  }  
                }else{
                    efield.addError("Field is required");
                }
               
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