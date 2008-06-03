package org.openelis.modules.provider.client;


import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenQueryTableWidget;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProviderScreen extends OpenELISScreenForm implements ClickListener, TabListener{

    
    private boolean loadNotes = true; // tells whether notes tab is to be filled with data
    private boolean loadAddresses = true; // tells whether table tab is to be filled with data
    private boolean clearNotes = false; // tells whether notes panel is to be cleared 
    private boolean clearAddresses = false; // tells whether table tab is to be cleared
    
    private ScreenVertical svp = null;
    private AppButton        removeContactButton, standardNoteButton;
    private ScreenTextBox provId = null; 
    private TextBox lastName = null;
    private TextBox subjectBox = null;
    private ScreenTextArea noteArea = null;
    private EditTable provAddController = null;
    private TabPanel noteTab = null;    
    private ScreenAutoDropdown displayType = null;
    
    private StringField note = null; 
    private StringField subject = null;
    
    private static boolean loaded = false;

    private static DataModel typeDropDown = null;
    private static DataModel stateDropDown = null;  
    private static DataModel countryDropDown = null; 
    
    
    public ProviderScreen(){
        super("org.openelis.modules.provider.server.ProviderService",!loaded);
    }
    
    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if(action.startsWith("query:")){
               getProviders(action.substring(6, action.length()));      
           }
        }else{
            super.onChange(sender);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeContactButton)
            onRemoveRowButtonClick();
        else if (sender == standardNoteButton)
            onStandardNoteButtonClick();    
    }

    public void afterDraw(boolean success) {
        loaded = true;
        setBpanel((ButtonPanel) getWidget("buttons"));        
                   
//      load other widgets
        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        removeContactButton = (AppButton) getWidget("removeAddressButton");
        
        standardNoteButton = (AppButton) getWidget("standardNoteButton");
        
        provId = (ScreenTextBox)widgets.get("provider.id");
        lastName = (TextBox)getWidget("provider.lastName");
        subjectBox = (TextBox)getWidget("note.subject");
        noteArea = (ScreenTextArea)widgets.get("note.text");
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        noteTab = (TabPanel)getWidget("provTabPanel");  
        
        displayType = (ScreenAutoDropdown)widgets.get("provider.type");
        
        provAddController = (EditTable)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.setAutoAdd(false);
        
        ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddController.manager;
        proAddManager.setProviderForm(this);
        
        //load dropdowns
       if(typeDropDown == null){
         typeDropDown = (DataModel) initData.get("providers");
         stateDropDown = (DataModel) initData.get("states");
         countryDropDown = (DataModel) initData.get("countries");
        } 
                                
       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDropDown);                                
    
       ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
       ScreenQueryTableWidget queryContactTable = (ScreenQueryTableWidget)displayAddressTable.getQueryWidget();
       
       TableAutoDropdown displayContactState = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                    controller.editors[5];
       displayContactState.setModel(stateDropDown);
       
       TableAutoDropdown queryContactState = (TableAutoDropdown)((QueryTable)queryContactTable.getWidget()).editors[5];
        queryContactState.setModel(stateDropDown);
       
       
       TableAutoDropdown displayContactCountry = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                   controller.editors[6];
       displayContactCountry.setModel(countryDropDown);
       
       TableAutoDropdown queryContactCountry = (TableAutoDropdown)((QueryTable)queryContactTable.getWidget()).editors[6];
       queryContactCountry.setModel(countryDropDown);              
        
        super.afterDraw(success);
    }
        
    public void afterFetch(boolean success){       
    //      every time a fetch is done, data in both tabs should be loaded afresh
           if(success){ 
            loadAddresses = true;
            loadNotes = true;                        
                
            loadTabs();        
           } 
           super.afterFetch(success); 
           
         }

    public void query(){
    	clearNotes();   	       
       super.query();
    
        //    set focus to the last name field
        provId.setFocus(true);
        noteArea.enable(false);
         removeContactButton.changeState(AppButton.ButtonState.DISABLED);
         
         standardNoteButton.changeState(AppButton.ButtonState.DISABLED);
    }

    public void add(){                                       
    
        removeContactButton.changeState(AppButton.ButtonState.UNPRESSED);
        
        standardNoteButton.changeState(AppButton.ButtonState.UNPRESSED);
        
        svp.clear();
        
        provAddController.setAutoAdd(true);
         
        //ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddController.manager;
        //proAddManager.disableRows = false;
        super.add();     
        
        noteArea.enable(true);
        provId.enable(false);
        
        //set focus to the last name field       
        lastName.setFocus(true);
        
    }

    public void update() {                        
        note = (StringField)rpc.getField("note.text");             
        note.setValue("");
                 
        subject = (StringField)rpc.getField("note.subject");
        subject.setValue("");
        
        provAddController.setAutoAdd(true);
        
        //ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddController.manager;
        //proAddManager.disableRows = false;
        
        noteArea.enable(true);
        super.update();      
    }
    
    public void afterUpdate(boolean success) {
        
        super.afterUpdate(success);
                
        removeContactButton.changeState(AppButton.ButtonState.UNPRESSED);
        
        standardNoteButton.changeState(AppButton.ButtonState.UNPRESSED);
        
        provId.enable(false);
        
       if(success){ 
        // this code is for the event of the update mode being enabled 
        loadAddresses = true;
        loadNotes = true;                
        
        loadTabs();
       } 
        //      set focus to the last name field
        lastName.setFocus(true);
    }

    public void abort(){      
        
      provAddController.setAutoAdd(false);      
      
      if(state == FormInt.State.ADD || state == FormInt.State.QUERY){
          loadAddresses = false;
          clearAddresses = true;
          
          loadNotes = false;
          clearNotes = true;
      }else{
          loadAddresses = true;
          loadNotes = true;
      }
      
      //the super needs to ge before the load tabs method or the table wont load.
      super.abort();
      
      loadTabs();                                
    }
    
    public void commitQuery(FormRPC rpcQuery){
        provAddController.unselect(-1);
        
        super.commitQuery(rpcQuery);
    }

    public void commitAdd(){
                       
        provAddController.unselect(-1);
               
        super.commitAdd();                              
        
        removeContactButton.changeState(AppButton.ButtonState.DISABLED);
        
        standardNoteButton.changeState(AppButton.ButtonState.DISABLED);
        
        provAddController.setAutoAdd(false);      
    }
    
    public void afterCommitAdd(boolean success){      
        // we need to load the notes tab if it has been already loaded
       if(success){  
        loadNotes = true;
        clearNotes = true;    
        
        loadAddresses = true;
        clearAddresses = false;
        
        Integer provId = (Integer)rpc.getFieldValue("provider.id");
        NumberObject provIdObj = new NumberObject(provId);
        
//      done because key is set to null in AppScreenForm for the add operation 
        if(key ==null){  
            key = new DataSet();
            key.setKey(provIdObj);
            
        }else{
            key.setKey(provIdObj);
            
        }
        
        loadTabs();
                
        clearNotesFields();
      }
        
        super.afterCommitAdd(success);         
    }

    public void afterCommitUpdate(boolean success){
        provAddController.setAutoAdd(false); 
        //we need to do this reset to get rid of the last row
        provAddController.reset();
        
        super.afterCommitUpdate(success); 
        
        //we need to load the notes tab if it has been already loaded
        if(success){  
         loadNotes = true;
         clearNotes = true;                
         
         loadTabs();
                 
         clearNotesFields();
       }
        
                    
    }
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index){
       // this code is for the generic situation of a tab being clicked  
                
        if(index ==0 && loadAddresses){
          if(clearAddresses){
            clearAddresses();
           } 
          fillAddressModel();
          loadAddresses = false;
        }
        else if(index ==1 && loadNotes){
           if(clearNotes){ 
            clearNotes();
           } 
           fillNotesModel();
           loadNotes = false;      
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    	// TODO Auto-generated method stub
    	
    }
    
    private void getProviders(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            
            letterRPC.setFieldValue("provider.lastName", query);            
            commitQuery(letterRPC); 
       }
    }
    
   private void fillNotesModel(){  
       
       Integer providerId = null;
       boolean getModel = false;  
     
     // access the database only if id is not null 
      if(key!=null){ 
       if(key.getKey()!=null){        
         getModel = true;
         providerId = (Integer)key.getKey().getValue();          
        }else{
            clearNotes = false;
        }
      }else{
          clearNotes = false;
      } 
        
       if(getModel){ 
         window.setStatus("","spinnerIcon");
         
         NumberObject provId = new NumberObject(providerId);
         
        // prepare the argument list for the getObject function
         DataObject[] args = new DataObject[] {provId}; 
         
        screenService.getObject("getNotesModel", args, new AsyncCallback(){
           public void onSuccess(Object result){  
               // get the datamodel, load it in the notes panel and set the value in the rpc
          	   String xmlString = (String) ((StringObject)result).getValue();               
               svp.load(xmlString);
               
               if(((VerticalPanel)svp.getPanel()).getWidgetCount() > 0){
                   clearNotes = true;
                }else {
                    clearNotes = false;
                }
               
               window.setStatus("","");
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
     }       
   }
   
   private void fillAddressModel(){
    try{          
     Integer providerId = null;
     boolean getModel = false;             
          
     // access the database only if id is not null 
    if(key!=null){
      if(key.getKey()!=null){        
        getModel = true;
        providerId = (Integer)key.getKey().getValue();         
      }else{
        clearAddresses = false;  
      }   
     }else {
        clearAddresses = false;
     } 
     
      if(getModel){
          window.setStatus("","spinnerIcon");
          
          NumberObject provId = new NumberObject(providerId);
          TableField tf = new TableField();
          tf.setValue(provAddController.model);
          
         //prepare the argument list for the getObject function
          DataObject[] args = new DataObject[] {provId, tf};
          
       screenService.getObject("getAddressModel", args , new AsyncCallback(){
           public void onSuccess(Object result){
               // get the table model and load it in the table 
               rpc.setFieldValue("providerAddressTable",(TableModel)((TableField)result).getValue());
               
               provAddController.setModel((TableModel)((TableField)result).getValue());                             
                
               if(provAddController.model.numRows()>0){      
                   clearAddresses = true;
               }else{
                   clearAddresses = false;
               }        
               
               window.setStatus("","");
           }
          
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       });
      } 
    }catch(Exception ex){
        Window.alert(ex.getMessage());
        ex.printStackTrace();
    } 
   }
   
   private void clearNotesFields(){
    //the note subject and body fields need to be refeshed after every successful commit 
                  
      subjectBox.setText("");
      
      ((TextArea)noteArea.getWidget()).setText("");           
      rpc.setFieldValue("note.subject", null);
      rpc.setFieldValue("note.text", null);  
   }
   
   private void loadTabs(){        
       int selectedTab = noteTab.getTabBar().getSelectedTab();     
                         
       if(selectedTab == 0 && loadAddresses){ 
          if(clearAddresses){ 
           clearAddresses(); 
          } 
         //load the table  
          fillAddressModel();
         // don't load it again unless the mode changes or a new fetch is done  
          loadAddresses = false;
       }
      
       else if(selectedTab == 1 && loadNotes){
          if(clearNotes){ 
           clearNotes(); 
          } 
         //load the notes model          
          fillNotesModel();
       // don't load it again unless the mode changes or a new fetch is done  
          loadNotes = false;

       }
   }     
   
   private void clearAddresses(){       
                 
       provAddController.model.reset(); 
       provAddController.setModel(provAddController.model);
       rpc.setFieldValue("providerAddressTable",provAddController.model);
   }
   
   private void clearNotes(){        
     
     svp.clear();          
   }
   
   private void onStandardNoteButtonClick(){
	   PopupPanel standardNotePopupPanel = new PopupPanel(false,true);
	   ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
	   pickerWindow.setContent(new StandardNotePickerScreen((TextArea)getWidget("note.text")));
			
	   standardNotePopupPanel.add(pickerWindow);
	   int left = this.getAbsoluteLeft();
	   int top = this.getAbsoluteTop();
	   standardNotePopupPanel.setPopupPosition(left,top);
	   standardNotePopupPanel.show();
    }
    
    private void onRemoveRowButtonClick(){
    	int selectedRow = provAddController.selected;
         if (selectedRow > -1
                 && provAddController.model.numRows() > 0) {
             TableRow row = provAddController.model.getRow(selectedRow);
             
             provAddController.model.hideRow(row);
             // delete the last row of the table because it is autoadd
             //provAddController.model.deleteRow(provAddController.model.numRows() - 1);
             // reset the model
             provAddController.reset();
             // need to set the deleted flag to "Y" also
             StringField deleteFlag = new StringField();
             deleteFlag.setValue("Y");

             row.addHidden("deleteFlag", deleteFlag);
         }  
    }
}