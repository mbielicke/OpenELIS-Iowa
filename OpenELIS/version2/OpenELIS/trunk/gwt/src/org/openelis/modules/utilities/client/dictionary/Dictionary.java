package org.openelis.modules.utilities.client.dictionary;



import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class Dictionary extends AppScreenForm implements MouseListener{
    private static DictionaryServletIntAsync screenService = (DictionaryServletIntAsync) GWT
    .create(DictionaryServletInt.class);
    
    private static ServiceDefTarget target = (ServiceDefTarget) screenService;
    private TableWidget dictEntryTable  = null;
    public PopupWindow window; 
    
    public Dictionary(){
        super();        
       
        String base = GWT.getModuleBaseURL();
        base += "DictionaryServlet";        
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;        
        getXML(); 
      
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
        if (sender == getWidget("a")) {
            getCategories("a", sender);
        } else if (sender == getWidget("b")) {
            getCategories("b", sender);
        } else if (sender == getWidget("c")) {
            getCategories("c", sender);
        } else if (sender == getWidget("d")) {
            getCategories("d", sender);
        } else if (sender == getWidget("e")) {
            getCategories("e", sender);
        } else if (sender == getWidget("f")) {
            getCategories("f", sender);
        } else if (sender == getWidget("g")) {
            getCategories("g", sender);
        } else if (sender == getWidget("h")) {
            getCategories("h", sender);
        } else if (sender == getWidget("i")) {
            getCategories("i", sender);
        } else if (sender == getWidget("j")) {
            getCategories("j", sender);
        } else if (sender == getWidget("k")) {
            getCategories("k", sender);
        } else if (sender == getWidget("l")) {
            getCategories("l", sender);
        } else if (sender == getWidget("m")) {
            getCategories("m", sender);
        } else if (sender == getWidget("n")) {
            getCategories("n", sender);
        } else if (sender == getWidget("o")) {
            getCategories("o", sender);
        } else if (sender == getWidget("p")) {
            getCategories("p", sender);
        } else if (sender == getWidget("q")) {
            getCategories("q", sender);
        } else if (sender == getWidget("r")) {
            getCategories("r", sender);
        } else if (sender == getWidget("s")) {
            getCategories("s", sender);
        } else if (sender == getWidget("t")) {
            getCategories("t", sender);
        } else if (sender == getWidget("u")) {
            getCategories("u", sender);
        } else if (sender == getWidget("v")) {
            getCategories("v", sender);
        } else if (sender == getWidget("w")) {
            getCategories("w", sender);
        } else if (sender == getWidget("x")) {
            getCategories("x", sender);
        } else if (sender == getWidget("y")) {
            getCategories("y", sender);
        } else if (sender == getWidget("z")) {
            getCategories("z", sender);
        }else if (sender == getWidget("removeEntryButton")) {   
            
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
            screenService.getEntryIdForSystemName(systemName, new AsyncCallback() {
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
            screenService.getEntryIdForEntry(entry, new AsyncCallback() {
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

            screenService.getInitialModel("section", new AsyncCallback(){
                   public void onSuccess(Object result){
                       DataModel stateDataModel = (DataModel)result;
                       ScreenAutoDropdown displaySection = (ScreenAutoDropdown)widgets.get("section");
                       ScreenAutoDropdown querySection = displaySection.getQueryWidget();
                       
                       ((AutoCompleteDropdown)displaySection.getWidget()).setModel(stateDataModel);
                       ((AutoCompleteDropdown)querySection.getWidget()).setModel(stateDataModel);
                                              
                   }
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
                });
            
            screenService.getInitialModel("isActive", new AsyncCallback(){
                public void onSuccess(Object result){
                    DataModel activeDataModel = (DataModel)result;                   
                    
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
              //boolean entryExists = false;
              //boolean sysNameExists = false;
              
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
           
            //if(sysNameExists || entryExists || (!sysUnique) || (!entryUnique)){
             if(entryError || sysNameError){ 
                return false;
            } 
            else {
                return true;
            }
         }
}
