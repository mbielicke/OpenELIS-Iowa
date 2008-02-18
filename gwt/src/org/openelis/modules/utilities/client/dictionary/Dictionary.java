package org.openelis.modules.utilities.client.dictionary;



import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableController;
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
    private boolean sysUnique = true;
    private boolean entryUnique = true;  
    
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
        
        TableController provAddTable = (TableController)(dictEntryTable.controller);
        provAddTable.setAutoAdd(false);
        
        //Button removeEntryButton = (Button) getWidget("removeEntryButton");
        //removeEntryButton.setEnabled(false);
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");        
        removeEntryButton.addClickListener(this);
        removeEntryButton.changeState(AppButton.DISABLED);                      
        
        super.afterDraw(success);
        loadDropdowns();
      // }catch(Exception ex){
       //    ex.printStackTrace();
       //    Window.alert(ex.getMessage());
      // } 
    }
    
    public void up(int state){
        
        TableController provAddTable = (TableController)(dictEntryTable.controller);
        provAddTable.setAutoAdd(true);
        
        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryTable.controller.manager);
        dictEntManager.resetLists();
        dictEntManager.thiscontroller = dictEntryTable.controller;
        dictEntManager.createLists();
        super.up(state);
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
        removeEntryButton.changeState(AppButton.UNPRESSED);          
        
        //Button removeEntryButton = (Button) getWidget("removeEntryButton");
        //removeEntryButton.setEnabled(true);
        
		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
    }
    
    public void commitAdd(){
       
        
      //if((!entryExists) && (!sysNameExists) && sysEntryUnique){    
          dictEntryTable.controller.unselect(-1);  
          //entryExists = false;
          //sysNameExists = false;
          //sysEntryUnique = true;          
          super.commitAdd();
         AppButton removeEntryButton = (AppButton) widgets.get("removeEntryButton");
         removeEntryButton.changeState(AppButton.DISABLED);  
         TableController provAddTable = (TableController)(dictEntryTable.controller);
         provAddTable.setAutoAdd(false);
      //}     
      //else {
       //   Window.alert("Please correct the errors first");
      //}        
    }
    
    public void commitUpdate(){        
       // if((!entryExists) && (!sysNameExists) && sysEntryUnique){ 
            dictEntryTable.controller.unselect(-1);     
           // entryExists = false;
           // sysNameExists = false;
           // sysEntryUnique = true;            
            super.commitUpdate();
            AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
            removeEntryButton.changeState(AppButton.DISABLED);  
            TableController provAddTable = (TableController)(dictEntryTable.controller);
            provAddTable.setAutoAdd(false);
          //}     
          //else {
          //    Window.alert("Please correct the errors first");
         // }
             
    }
    
    public void abort(int state){
        AppButton removeEntryButton = (AppButton) getWidget("removeEntryButton");
        removeEntryButton.changeState(AppButton.DISABLED);     
       try{ 
       // Button removeEntryButton = (Button) getWidget("removeEntryButton");
        //removeEntryButton.setEnabled(false);
        
        TableController provAddTable = (TableController)(dictEntryTable.controller);
        provAddTable.setAutoAdd(false);
        //entryExists = false;
        //sysNameExists = false;
        sysUnique = true;
        entryUnique = true;
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
        }else if (sender == widgets.get("removeEntryButton")) {   
            
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
           TableController dictEntController = (TableController)(dictEntryTable.controller);
           dictEntController.setAutoAdd(true);           
          
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
                        //Window.alert("result "+ result+ " entryId "+ entryId);
                        //((DictionaryEntriesTable) dictEntryTable.controller.manager).sysNameExists = true;
                        StringField snfield = (StringField)dictEntryTable.controller.model.getFieldAt(trow, 1);
                        snfield.addError(constants.getString("dictSystemNameError"));
                        ((TableCellInputWidget)dictEntryTable.controller.view.table.getWidget(trow,1)).drawErrors();
                        //sysNameExists = true;
                    }else{
                        //sysNameExists = false; 
                        //((DictionaryEntriesTable) dictEntryTable.controller.manager).sysNameExists = false; 
                    }
                      
                      //showMessage("sysNameExists"); 

                    //  if(sysEntryUnique) {                          
                          //showMessage("noErrors");
                     // } 
     
                 }
                
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                 });            
            //return sysNameExists;
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
                        //((DictionaryEntriesTable) dictEntryTable.controller.manager).entryExists = true;
                        StringField efield = (StringField)dictEntryTable.controller.model.getFieldAt(trow, 3); 
                        efield.addError(constants.getString("dictEntryError"));
                        ((TableCellInputWidget)dictEntryTable.controller.view.table.getWidget(trow,3)).drawErrors();
                       // entryExists = true;
                    }else{
                        //((DictionaryEntriesTable) dictEntryTable.controller.manager).entryExists = false; 
                       // entryExists = false;
                    }
                   }                 
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                 });          
           // return entryExists;
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
        }
        
         public void setSysNameUnique(boolean unique){
             if(unique){
                 sysUnique = true;
             }else{
                 sysUnique = false;
             } 
         }
         
         public void setEntryUnique(boolean unique){
             if(unique){
                 entryUnique = true;
             }else{
                 entryUnique = false;
             } 
         }
                             
        public boolean validate(){
              boolean entryExists = false;
              boolean sysNameExists = false;
           for(int iter = 0; iter < dictEntryTable.controller.model.numRows(); iter++){
               StringField snfield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 1);
               StringField efield = (StringField)dictEntryTable.controller.model.getFieldAt(iter, 3);
               
               if(!(efield.getErrors().length==0)){
                   entryExists = true;                   
               }
               if(!(snfield.getErrors().length==0)){
                   sysNameExists = true;                   
               }
           }  
           
            if(sysNameExists || entryExists || (!sysUnique) || (!entryUnique)){
                return false;
            } 
            else {
                return true;
            }
         }
}
