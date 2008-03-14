package org.openelis.modules.provider.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

public class ProviderScreen extends OpenELISScreenForm {

    private Widget selected;
    private boolean loadNotes = true; // tells whether notes tab is to be filled with data
    private boolean loadTable = true; // tells whether table tab is to be filled with data
    //private TableController provAddController = null;  
   
    public ProviderScreen(){
        super("org.openelis.modules.provider.server.ProviderService",true);
        name="Provider";
    }
    
    public void afterDraw(boolean success) {
        bpanel = (ButtonPanel) getWidget("buttons");        
        message.setText("done");
        
        TableWidget provideNamesTable = (TableWidget) getWidget("providersTable");
        modelWidget.addChangeListener(provideNamesTable.controller);
        
        ((ProviderNamesTable) provideNamesTable.controller.manager).setProviderForm(this);               
        
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.addClickListener(this);
        removeContactButton.changeState(AppButton.DISABLED);              
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(false);
        
        ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddTable.manager;
        proAddManager.disableRows = true;
           
        loadDropdowns();
        super.afterDraw(success);
                       
        bpanel.setButtonState("prev", AppButton.DISABLED);
        bpanel.setButtonState("next", AppButton.DISABLED);
    }
      
   
        
    public void up(int state) {                        
        
        StringField note = (StringField)rpc.getField("usersNote");  
        note.setValue("");
        
        StringField subject = (StringField)rpc.getField("usersSubject");  
        subject.setValue("");
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(true);
        
        ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddTable.manager;
        proAddManager.disableRows = false;
        super.up(state);      
    }
    
    public void abort(int state){      
        
      TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
      provAddTable.setAutoAdd(false);      
      
      ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddTable.manager;
      proAddManager.disableRows = true;
      
       super.abort(state); 
       
       loadTable = true;
       loadNotes = true;          
       clearTabs();
       loadTabs();
       
             
      // need to get the provider name table model
       TableWidget catNameTM = (TableWidget) getWidget("providersTable");
       int rowSelected = -1;
       if(catNameTM.controller!=null){
           rowSelected = catNameTM.controller.selected;
       } 

       // set the update button if needed
       if (rowSelected == -1){
           bpanel.setButtonState("update", AppButton.DISABLED);
           bpanel.setButtonState("prev", AppButton.DISABLED);
           bpanel.setButtonState("next", AppButton.DISABLED);
       }
    }
    
    public void add(int state){                       
       if(key!=null) 
        key.setObject(0, null);
                
        
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.UNPRESSED);
        
        ScreenVertical vp = (ScreenVertical) widgets.get("notesPanel");        
        vp.clear();
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(true);
         
        ProviderAddressesTable proAddManager = (ProviderAddressesTable)provAddTable.manager;
        proAddManager.disableRows = false;
        super.add(state);     
        
        ScreenTextBox provId = (ScreenTextBox)widgets.get("providerId");
        provId.enable(false);
        
//      set focus to the last name field
        TextBox lastName = (TextBox)getWidget("lastName");
        lastName.setFocus(true);
        
        TableWidget catNameTM = (TableWidget) getWidget("providersTable");
        catNameTM.controller.unselect(-1);
    }
    
    public void afterUpdate(boolean success) {
        
        super.afterUpdate(success);
                
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.UNPRESSED);
        
        ScreenTextBox provId = (ScreenTextBox)widgets.get("providerId");
        provId.enable(false);
        
     // this code is for the event of the update mode being enabled 
        loadTable = true;
        loadNotes = true;                
        
        clearTabs();
        
        loadTabs();
        
//      set focus to the last name field
        TextBox lastName = (TextBox)getWidget("lastName");
        lastName.setFocus(true);
    }
    
    public void query(int state){
    	ScreenVertical vp = (ScreenVertical) widgets.get("notesPanel");        
    	vp.clear();
    	//shownotes = true;
      
      super.query(state);
      
//    set focus to the last name field
        TextBox provId = (TextBox)getWidget("providerId");
        provId.setFocus(true);
        
         AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
         removeContactButton.changeState(AppButton.DISABLED);
         
         AppButton standardNote = (AppButton) getWidget("standardNoteButton");
         standardNote.changeState(AppButton.DISABLED);
    }
       
    public void fetch(){                 
        super.fetch();                
        // add whole lazy load code here
    }
    
    public void afterFetch(boolean success){
      
//      every time a fetch is done, data in both tabs should be loaded afresh
        loadTable = true;
        loadNotes = true;                
        
        clearTabs();
        
        loadTabs();
        
       super.afterFetch(success); 
    }
    
    
    public void onClick(Widget sender) {
        String action = ((AppButton)sender).action;
        if(action.startsWith("query:")){
            getProviders(action.substring(6, action.length()), sender);
            
        } else if (action.equals("removeRow")) {                     
            TableWidget provAddTable = (TableWidget) getWidget("providerAddressTable");
            int selectedRow = provAddTable.controller.selected;
            if (selectedRow > -1
                    && provAddTable.controller.model.numRows() > 1) {
                TableRow row = provAddTable.controller.model
                        .getRow(selectedRow);
                
                row.setShow(false);
                // delete the last row of the table because it is autoadd
                provAddTable.controller.model
                        .deleteRow(provAddTable.controller.model.numRows() - 1);
                // reset the model
                provAddTable.controller.reset();
                // need to set the deleted flag to "Y" also
                StringField deleteFlag = new StringField();
                deleteFlag.setValue("Y");

                row.addHidden("deleteFlag", deleteFlag);
            }  
        }else if(action.equals("standardNote")){
            new StandardNotePickerScreen((TextArea)getWidget("usersNote"));
        }        
    }
        
    
    private void getProviders(String letter, Widget sender) {
        // we only want to allow them to select a letter if they are in display
        // mode..
        if (bpanel.getState() == FormInt.DISPLAY) {

            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            letterRPC.setFieldValue("lastName", letter.toUpperCase() + "*");
             
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

    public void onTabSelected(SourcesTabEvents sources, int index) {       
       //this code is for the generic situation of a tab being clicked  
        
       // when the add  mode is enabled,loading of data from the database is not applicable    
       if(!(bpanel.state == FormInt.ADD)){ 
        if(index == 0 && loadTable){   
            fillAddressModel();
            loadTable = false;
        }
        
        else if(index == 1 && loadNotes){
            fillNotesModel();
            loadNotes = false;            
        }
       } 
    }    
               
    
    public void commitAdd(){
                       
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);
        
       
        super.commitAdd();      
                        
        
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
        
        provAddController.setAutoAdd(false);      
    }
    
    public void commitUpdate(){ 
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);               
        
        super.commitUpdate();        
        
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
                
        provAddController.setAutoAdd(false);      
    }
    
    public void afterCommitUpdate(boolean success){
      // when a provider's data is committed, after being updated, to the database, this code will make sure that whichever tab is open, 
      //or will be opened subsequently, will have the latest data in it     
        
        loadTable = true;
        loadNotes = true;
        
        clearTabs();
        
        loadTabs();
        
      // the note subject and body fields need to be refeshed after every successful commit
        clearNotesFields();
        super.afterCommitUpdate(success);     
        
    }
    
    public void afterCommitAdd(boolean success){
      // when a new provider's data is added to the database, this code will make sure that whichever tab is open, 
      //or will be opened subsequently, will have the latest data in it
        
        loadTable = true;
        loadNotes = true;
        
        //Window.alert("afterCommitAdd");
        
        Integer providerId = (Integer)rpc.getFieldValue("providerId");
        NumberObject provId = new NumberObject();
        provId.setType("integer");
        provId.setValue(providerId);
        //Window.alert(new Boolean(key == null).toString());
        
        //done because key is set to null in AppScreenForm for the add operation 
        if(key ==null){  
         key = new DataSet();
         key.addObject(provId);
        }
        else{
            key.setObject(0,provId);
        }
        clearTabs();
         
        //
        loadTabs();
        
        
        clearNotesFields();
       
        
        super.afterCommitAdd(success);         
    }
    
    
    public void commitQuery(FormRPC rpcQuery){
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);
        
        super.commitQuery(rpcQuery);
    }

    /*private void loadNotes(DataModel notesModel){       
                     
        VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");
                  
                  //we need to remove anything in the notes tab if it exists
                  vp.clear();
                  int i=0;
                  if(notesModel != null){ 
                      
                    while(i<notesModel.size()){
                      HorizontalPanel subjectPanel = new HorizontalPanel();
                      HorizontalPanel spacerPanel = new HorizontalPanel();
                      HorizontalPanel bodyPanel = new HorizontalPanel();
                      
                      Label subjectLabel = new Label();
                      Label bodyLabel = new Label();
                      
                      vp.add(subjectPanel);
                      vp.add(bodyPanel);
                      subjectPanel.add(subjectLabel);
                      bodyPanel.add(spacerPanel);
                      bodyPanel.add(bodyLabel);           
                      
                      spacerPanel.setWidth("25px");
                      subjectPanel.setWidth("100%");
                      bodyPanel.setWidth("100%");
                      
                      subjectLabel.addStyleName("NotesText");
                      bodyLabel.addStyleName("NotesText");
                      
                      subjectLabel.setWordWrap(true);
                      bodyLabel.setWordWrap(true);
                      
                      subjectLabel.setText((String)notesModel.get(i).getObject(0).getValue());
                      bodyLabel.setText((String)notesModel.get(i).getObject(1).getValue());
                      
                      i++;
                  }
                 } 
              }*/
    
    
                 
    private void loadDropdowns(){
        
        DataModel typeDropDown = (DataModel) initData[0];
        DataModel stateDropDown = (DataModel) initData[1];
        DataModel countryDropDown = (DataModel) initData[2];
                
                ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("providerType");
                ScreenAutoDropdown queryType = displayType.getQueryWidget();
                
                ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDropDown);
                ((AutoCompleteDropdown)queryType.getWidget()).setModel(typeDropDown);
                                       
                    
                   
                   ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
                   ScreenTableWidget queryAddressTable = (ScreenTableWidget)displayAddressTable.getQueryWidget();
                   
                   TableAutoDropdown displayContactState = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                                controller.editors[5];
                   displayContactState.setModel(stateDropDown);
                   
                   TableAutoDropdown queryContactState = (TableAutoDropdown)((TableWidget)queryAddressTable.getWidget()).
                        controller.editors[5];
                   queryContactState.setModel(stateDropDown);
                                                                                              
                   
                   TableAutoDropdown displayContactCountry = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                                controller.editors[6];
                   displayContactCountry.setModel(countryDropDown);
                   
                   TableAutoDropdown queryContactCountry = (TableAutoDropdown)((TableWidget)queryAddressTable.getWidget()).
                        controller.editors[6];
                   queryContactCountry.setModel(countryDropDown);
               
    } 
    
   public boolean validate(){
       TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
       provAddController.unselect(-1);
       boolean noLocErrors =  true;
       
       for(int iter = 0; iter < provAddController.model.numRows(); iter++){
           StringField snfield = (StringField)provAddController.model.getFieldAt(iter, 0);  
           
           if(snfield.getValue()!=null){
               if((snfield.getValue().toString().trim().equals(""))){
                   snfield.addError("Field is required");
               }  
             }else{
                 snfield.addError("Field is required");
             }
           if(!(snfield.getErrors().length==0)){
               noLocErrors = false;                   
           }
       }  
       
       if(!noLocErrors){
           return false;
       }
       
       return true; 
   }
   
   private void fillNotesModel(){  
       
       Integer providerId = null;
       boolean getModel = false;  
     
     // access the database only if id is not null 
      if(key!=null){ 
       if(key.getObject(0)!=null){        
         getModel = true;
         providerId = (Integer)key.getObject(0).getValue(); 
        }
      } 
        
       if(getModel){ 
        
         NumberObject provId = new NumberObject();
         provId.setType("integer");
         provId.setValue(providerId);
         
        // prepare the argument list for the getObject function
         DataObject[] args = new DataObject[] {provId}; 
         
        screenService.getObject("getNotesModel", args, new AsyncCallback(){
           public void onSuccess(Object result){  
        	   ScreenVertical vp = (ScreenVertical) widgets.get("notesPanel");
               // get the datamodel, load it in the notes panel and set the value in the rpc
          	   String xmlString = (String) ((StringObject)result).getValue();
               vp.load(xmlString);
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
     }       
   }
   
   private void fillAddressModel(){
              
     Integer providerId = null;
     boolean getModel = false;  
     
     // access the database only if id is not null 
     if(key!=null){
      if(key.getObject(0)!=null){        
       getModel = true;
       providerId = (Integer)key.getObject(0).getValue(); 
      }   
     } 
     
      if(getModel){
         // reset the model so that old data goes away 
          TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);         
          //provAddController.model.reset();        
          NumberObject provId = new NumberObject();
          provId.setType("integer");
          provId.setValue(providerId);
          TableField tf = new TableField();
          tf.setValue(provAddController.model);
          
//        prepare the argument list for the getObject function
          DataObject[] args = new DataObject[] {provId, tf};
          
       screenService.getObject("getAddressModel", args , new AsyncCallback(){
           public void onSuccess(Object result){
               // get the table model and load it in the table 
               rpc.setFieldValue("providerAddressTable",(TableModel)((TableField)result).getValue());
               TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
               provAddController.setModel((TableModel)((TableField)result).getValue());                             
              
               //Window.alert("fillAddressModel");
           }
          
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       });
      } 
   }
   
   private void clearNotesFields(){
    //     the note subject and body fields need to be refeshed after every successful commit 
       TextBox subjectBox = (TextBox)getWidget("usersSubject");           
       subjectBox.setText("");
      TextArea noteArea = (TextArea)getWidget("usersNote");
      noteArea.setText("");           
      rpc.setFieldValue("usersSubject", null);
      rpc.setFieldValue("usersNote", null);  
   }
   
   private void loadTabs(){
       TabPanel noteTab = (TabPanel)getWidget("provTabPanel");        
       int selectedTab = noteTab.getTabBar().getSelectedTab();     
                            
       if(selectedTab == 0 && loadTable){     
         //load the table  
          fillAddressModel();
         // don't load it again unless the mode changes or a new fetch is done  
          loadTable = false;
       }
      
       else if(selectedTab == 1 && loadNotes){
         //load the notes model          
          fillNotesModel();
       // don't load it again unless the mode changes or a new fetch is done  
          loadNotes = false;

       }
   }
   
   private void clearTabs(){
       TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);         
       provAddController.model.reset(); 
       provAddController.setModel(provAddController.model);
       rpc.setFieldValue("providerAddressTable",provAddController.model);
       
       ScreenVertical vp = (ScreenVertical) widgets.get("notesPanel");
       vp.clear();
   }
   
}