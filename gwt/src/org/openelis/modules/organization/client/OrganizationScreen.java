package org.openelis.modules.organization.client;

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
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationScreen extends OpenELISScreenForm {

	private Widget selected;
  
    private static DataModel stateDropdown;
    
    private static DataModel countryDropdown;
    
    private static DataModel contactTypeDropdown; 
    
    private static boolean loaded = false;

    private boolean loadNotes = true; // tells whether notes tab is to be filled with data
    private boolean loadContacts = true; // tells whether contacts tab is to be filled with data
    private ScreenVertical svp = null;
    private boolean clearNotes = false; // tells whether notes panel is to be cleared 
    private boolean clearContacts = false; // tells whether contacts tab is to be cleared
    

	public OrganizationScreen() {
        super("org.openelis.modules.organization.server.OrganizationService",!loaded);
        name = "Organization";
	}

    public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
               getOrganizations(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
           }
        }else{
            super.onChange(sender);
        }
    }
    
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
        if (action.equals("removeRow")) {
			onRemoveRowButtonClick();

		}else if(action.equals("standardNote")){
			onStandardNoteButtonClick();
			
		}
	}

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index){
           // this code is for the generic situation of a tab being clicked  
                        
            if(index ==0 && loadContacts){
              if(clearContacts){
                clearContacts();
               } 
              fillContactsModel();
              loadContacts = false;
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
    
	public void afterDraw(boolean success) {
        loaded = true;
		bpanel = (ButtonPanel) getWidget("buttons");
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");

		removeContactButton.changeState(AppButton.DISABLED);


		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
		message.setText("done");

		// get contacts table and set the managers form
		TableWidget contactsTable = (TableWidget) getWidget("contactsTable");
		((OrganizationContactsTable) contactsTable.controller.manager)
				.setOrganizationForm(this);

        loadDropdowns();
		super.afterDraw(success);			
	}
    
	public void afterFetch(boolean success) {
		    
// every time a fetch is done, data in both tabs should be loaded afresh
      if(success){  
        loadContacts = true;
        loadNotes = true;                
        
        loadTabs();
      } 
        super.afterFetch(success);   
	}
	private void getOrganizations(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue("organization.name", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			//setStyleNameOnButton(sender);
		}
	}

	// button panel action methods
	public void add() {
       if(key!=null) 
        key.setObject(0, null);
       
		super.add();
		
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("organization.id");
		orgId.enable(false);

		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		OrganizationContactsTable orgContactsTableManager = (OrganizationContactsTable) contactTable.controller.manager;
		orgContactsTableManager.disableRows = false;
		contactTable.controller.setAutoAdd(true);
		contactTable.controller.addRow();
		
		ScreenVertical vp = (ScreenVertical) widgets.get("notesPanel");
		//we need to remove anything in the notes tab if it exists
		vp.clear();
		
		//set focus to the org name field
		TextBox orgName = (TextBox)getWidget("organization.name");
		orgName.setFocus(true);

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
	}
    
    public void afterAbort(boolean success){
        loadContacts = true;
        loadNotes = true;                
        
        TableWidget orgContacts = (TableWidget) getWidget("contactsTable");
        orgContacts.controller.setAutoAdd(false);
        
        loadTabs();
        
        OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) orgContacts.controller.manager;
        orgContactsTable.disableRows = true;
        orgContacts.controller.unselect(-1);
            
        AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
        removeContactButton.changeState(AppButton.DISABLED);    
    }

	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
       
      if(success){ 
        // this code is for the event of the update mode being enabled 
        loadContacts = true;
        loadNotes = true;                
        
        loadTabs();
         
       }
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("organization.id");
		orgId.enable(false);
		
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		OrganizationContactsTable orgContactsTableManager = (OrganizationContactsTable) contactTable.controller.manager;
		orgContactsTableManager.disableRows = false;
		contactTable.controller.setAutoAdd(true);
		contactTable.controller.addRow();
		
		//set focus to the org name field
		TextBox orgName = (TextBox)getWidget("organization.name");
		orgName.setFocus(true);

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
	}

	public void afterCommitAdd(boolean success) {
//    when a new organization's data is added to the database, this code will make sure that whichever tab is open, 
          //or will be opened subsequently, will have the latest data in it
       if(success){ 
        loadContacts = true;
        loadNotes = true;
        
        Integer orgId = (Integer)rpc.getFieldValue("organization.id");
        NumberObject orgIdObj = new NumberObject();
        orgIdObj.setType("integer");
        orgIdObj.setValue(orgId);
        
        // done because key is set to null in AppScreenForm for the add operation 
        if(key ==null){  
         key = new DataSet();
         key.addObject(orgIdObj);
        }
        else{
            key.setObject(0,orgIdObj);
        }
        
                
        loadTabs(); 
        
        clearNotesFields();
       } 
		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
				
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		contactTable.controller.setAutoAdd(false);

		super.afterCommitAdd(success);
	}

	public void query() {
		super.query();
		
		//set focus to the org id field
		TextBox orgId = (TextBox)getWidget("organization.id");
		orgId.setFocus(true);
	}
	
	public void afterCommitUpdate(boolean success) {
        // when an organization's data is committed, after being updated, to the database, this code will make sure that whichever tab is open, 
        //or will be opened subsequently, will have the latest data in it 
      if(success){ 
        loadContacts = true;
        loadNotes = true;
                      
        loadTabs();               
        
       //the note subject and body fields need to be refeshed after every successful commit   
        clearNotesFields();
       } 
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		contactTable.controller.setAutoAdd(false);
		
		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);

		super.afterCommitUpdate(success);
	}
	
	protected void doReset() {
		//we need to reset the dropdowns
		//clear the state dropdown
		((AutoCompleteDropdown)getWidget("organization.address.state")).reset();
		//clear the country dropdown
		((AutoCompleteDropdown)getWidget("organization.address.country")).reset();
		
		 ScreenTableWidget displayContactTable = (ScreenTableWidget)widgets.get("contactsTable");
		TableAutoDropdown displayContactType = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
		controller.editors[0];
		displayContactType.editor.reset();
		TableAutoDropdown displayState = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
		controller.editors[5];
		displayState.editor.reset();
		TableAutoDropdown displayCountry = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
		controller.editors[12];
		displayCountry.editor.reset();
		super.doReset();
	}

	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
	
	private void loadDropdowns(){
		if(stateDropdown == null){
		    stateDropdown = (DataModel)initData[0];
	    
		    countryDropdown = (DataModel)initData[1];
	    
		    contactTypeDropdown = (DataModel)initData[2];
        }
	
//	  load the state dropdowns
		ScreenAutoDropdown displayState = (ScreenAutoDropdown)widgets.get("organization.address.state");
	               
	    ((AutoCompleteDropdown)displayState.getWidget()).setModel(stateDropdown);
	               
	    ScreenTableWidget displayContactTable = (ScreenTableWidget)widgets.get("contactsTable");
	    ScreenQueryTableWidget queryContactTable = (ScreenQueryTableWidget)displayContactTable.getQueryWidget();
	               
	    TableAutoDropdown displayContactState = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
	              																				controller.editors[5];
	    displayContactState.setModel(stateDropdown);
	               
	    TableAutoDropdown queryContactState = (TableAutoDropdown)((QueryTable)queryContactTable.getWidget()).editors[5];
	    queryContactState.setModel(stateDropdown);
	     
	     
		//load the country dropdowns
	    ScreenAutoDropdown displayCountry = (ScreenAutoDropdown)widgets.get("organization.address.country");
	               
	    ((AutoCompleteDropdown)displayCountry.getWidget()).setModel(countryDropdown);
	               
	    TableAutoDropdown displayContactCountry = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
	               																				controller.editors[12];
	    displayContactCountry.setModel(countryDropdown);
	               
	    TableAutoDropdown queryContactCountry = (TableAutoDropdown)((QueryTable)queryContactTable.getWidget()).editors[12];
	    queryContactCountry.setModel(countryDropdown);
	    
	    
		//load the contact type dropdowns	        	   
  	   TableAutoDropdown displayContactType = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
																controller.editors[0];
   	   displayContactType.setModel(contactTypeDropdown);

   	   TableAutoDropdown queryContactType = (TableAutoDropdown)((QueryTable)queryContactTable.getWidget()).editors[0];
  	   queryContactType.setModel(contactTypeDropdown);
	}
    
    
    private void fillNotesModel(){            
        Integer orgId = null;
        boolean getModel = false;
         
         // access the database only if id is not null  
         if(key!=null){
           if(key.getObject(0)!=null){        
             getModel = true;
             orgId = (Integer)key.getKey().getValue(); 
           }else{
               clearNotes = false;
           }
         }else{
             clearNotes = false;
         } 
            
           if(getModel){               
             NumberObject orgIdObj = new NumberObject();
             orgIdObj.setType("integer");
             orgIdObj.setValue(orgId);
             
            // prepare the argument list for the getObject function
             DataObject[] args = new DataObject[] {orgIdObj}; 
             
            screenService.getObject("getNotesModel", args, new AsyncCallback(){
               public void onSuccess(Object result){    
            	   svp = (ScreenVertical) widgets.get("notesPanel");
                 // get the datamodel, load it in the notes panel and set the value in the rpc
            	   String xmlString = (String) ((StringObject)result).getValue();
                   svp.load(xmlString);   
                   
                   if(((VerticalPanel)svp.getPanel()).getWidgetCount() > 0){
                       clearNotes = true;
                    }else {
                        clearNotes = false;
                    }
               }
               
               public void onFailure(Throwable caught){
                   Window.alert(caught.getMessage());
               }
           }); 
         }       
       }
       
       private void fillContactsModel(){
        
         Integer orgId = null;
         boolean getModel = false;  
         
         // access the database only if id is not null 
         if(key!=null){
          if(key.getObject(0)!=null){        
           getModel = true;
           orgId = (Integer)key.getKey().getValue(); 
          }else{
              clearContacts = false;  
            }   
           }else {
               clearContacts = false;
           }  
         
          if(getModel){              
             // reset the model so that old data goes away 
              TableController orgContactController = (TableController)(((TableWidget)getWidget("contactsTable")).controller);
            
              NumberObject orgIdObj = new NumberObject();
              orgIdObj.setType("integer");
              orgIdObj.setValue(orgId);
              TableField tf = new TableField();
              tf.setValue(orgContactController.model);
              
//          prepare the argument list for the getObject function
              DataObject[] args = new DataObject[] {orgIdObj, tf};
              
           screenService.getObject("getContactsModel", args , new AsyncCallback(){
               public void onSuccess(Object result){
                   // get the table model and load it in the table 
                   rpc.setFieldValue("contactsTable",(TableModel)((TableField)result).getValue());
                   EditTable orgContactController = (EditTable)(((TableWidget)getWidget("contactsTable")).controller);
                   orgContactController.loadModel((TableModel)((TableField)result).getValue());
                   
                   if(orgContactController.model.numRows()>0){      
                       clearContacts = true;
                   }else{
                       clearContacts = false;
                   } 
               }
              
               public void onFailure(Throwable caught){
                   Window.alert(caught.getMessage());
               }
           });
          } 
       }
       
       private void clearNotesFields(){
           //     the note subject and body fields need to be refeshed after every successful commit 
              TextBox subjectBox = (TextBox)getWidget("note.subject");           
              subjectBox.setText("");
             TextArea noteArea = (TextArea)getWidget("note.text");
             noteArea.setText("");           
             rpc.setFieldValue("note.subject", null);
             rpc.setFieldValue("note.text", null);  
          }
          
          private void loadTabs(){
              TabPanel noteTab = (TabPanel)getWidget("orgTabPanel");        
              int selectedTab = noteTab.getTabBar().getSelectedTab();     
                                   
              if(selectedTab == 0 && loadContacts){
               // if there was data previously then clear the contacts table otherwise don't   
                if(clearContacts){ 
                    clearContacts(); 
                   }   
                //load the table  
                  fillContactsModel();
                // don't load it again unless the mode changes or a new fetch is done  
                 loadContacts = false;
              }
             
             else if(selectedTab == 1 && loadNotes){
              //if there was data previously then clear the notes panel otherwise don't    
               if(clearNotes){
                   clearNotes(); 
               }   
                //load the notes model          
                 fillNotesModel();
              // don't load it again unless the mode changes or a new fetch is done  
                 loadNotes = false;

              }
          }  
          
          private void clearContacts(){       
              EditTable orgContactController = (EditTable)(((TableWidget)getWidget("contactsTable")).controller);             
              orgContactController.model.reset(); 
              orgContactController.setModel(orgContactController.model);
              rpc.setFieldValue("contactsTable",orgContactController.model);
          }
          
          private void clearNotes(){              
            svp = (ScreenVertical) widgets.get("notesPanel");     
            svp.clear();          
          }
          
          private void onStandardNoteButtonClick(){
        	 PopupPanel standardNotePopupPanel = new PopupPanel(false,true);
  			ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
  			pickerWindow.setContent(new StandardNotePickerScreen((TextArea)getWidget("note.text")));
  			
  			//StandardNotePickerScreen pickerScreen = new StandardNotePickerScreen((TextArea)getWidget("organization.orgNote.text"));
  			standardNotePopupPanel.add(pickerWindow);
  			int left = this.getAbsoluteLeft();
  			int top = this.getAbsoluteTop();
  			standardNotePopupPanel.setPopupPosition(left,top);
  			standardNotePopupPanel.show();
          }
          
          private void onRemoveRowButtonClick(){
        	  TableWidget orgContactsTable = (TableWidget) getWidget("contactsTable");
  				int selectedRow = orgContactsTable.controller.selected;
  				if (selectedRow > -1
  					&& orgContactsTable.controller.model.numRows() > 1) {
	  				TableRow row = orgContactsTable.controller.model
	  						.getRow(selectedRow);
	  				row.setShow(false);
	  				// delete the last row of the table because it is autoadd
	  				orgContactsTable.controller.model
	  						.deleteRow(orgContactsTable.controller.model.numRows() - 1);
	  				// reset the model
	  				orgContactsTable.controller.reset();
	  				// need to set the deleted flag to "Y" also
	  				StringField deleteFlag = new StringField();
	  				deleteFlag.setValue("Y");
	
	  				row.addHidden("deleteFlag", deleteFlag);
  				}
          }
}
