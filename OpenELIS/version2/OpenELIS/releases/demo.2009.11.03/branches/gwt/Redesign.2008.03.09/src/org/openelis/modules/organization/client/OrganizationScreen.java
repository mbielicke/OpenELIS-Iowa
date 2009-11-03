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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;

public class OrganizationScreen extends OpenELISScreenForm {

	private Widget selected;
  
    private static DataModel stateDropdown;
    
    private static DataModel countryDropdown;
    
    private static DataModel contactTypeDropdown; 
    
    private static boolean loaded = false;

    private boolean loadNotes = true; // tells whether notes tab is to be filled with data
    private boolean loadTable = true; // tells whether table tab is to be filled with data


	public OrganizationScreen() {
        super("org.openelis.modules.organization.server.OrganizationService",!loaded);
        name = "Organization";
	}

	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.startsWith("query:")){
			getOrganizations(action.substring(6, action.length()), sender);
			
		} else if (action.equals("removeRow")) {
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

		}else if(action.equals("standardNote")){
			new StandardNotePickerScreen((TextArea)getWidget("usersNote"));
		}
	}

    public void onTabSelected(SourcesTabEvents sources, int index) {       
           //this code is for the generic situation of a tab being clicked  
            
           // when the add  mode is enabled,loading of data from the database is not applicable    
           if(!(bpanel.state == FormInt.ADD)){ 
            if(index == 0 && loadTable){   
                fillContactsModel();
                loadTable = false;
            }
            
            else if(index == 1 && loadNotes){
                fillNotesModel();
                loadNotes = false;            
            }
           } 
        }    
    
	public void afterDraw(boolean success) {
        loaded = true;
		bpanel = (ButtonPanel) getWidget("buttons");

		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		//removeContactButton.addClickListener(this);
		removeContactButton.changeState(AppButton.DISABLED);
		
		//AppButton standardNoteButton = (AppButton) getWidget("standardNoteButton");
		//standardNoteButton.addClickListener(this);
		//standardNoteButton.changeState(AppButton.DISABLED);

		TableWidget orgNameTable = (TableWidget) getWidget("organizationsTable");
		modelWidget.addChangeListener(orgNameTable.controller);

		// if(constants != null)
		// message.setText(openElisConstants.getString("loadCompleteMessage"));
		// else
		message.setText("done");

		// get contacts table and set the managers form
		TableWidget contactsTable = (TableWidget) getWidget("contactsTable");
		((OrganizationContactsTable) contactsTable.controller.manager)
				.setOrganizationForm(this);

		// get contacts table and set the managers form

		((OrganizationNameTable) orgNameTable.controller.manager)
				.setOrganizationForm(this);

        loadDropdowns();
		super.afterDraw(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		
				
	}

    public void fetch(){
        super.fetch();               
        
    }
    
	public void afterFetch(boolean success) {
//    every time a fetch is done, data in both tabs should be loaded afresh
        loadTable = true;
        loadNotes = true;                
        clearTabs();
        loadTabs();
		super.afterFetch(success);       
	}
	private void getOrganizations(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (bpanel.getState() == FormInt.DISPLAY) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue("orgName", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}

	// button panel action methods
	public void add(int state) {
       if(key!=null) 
        key.setObject(0, null);
       
		super.add(state);
		
		//FIXME need to load the model for the dropdown
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
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
		TextBox orgName = (TextBox)getWidget("orgName");
		orgName.setFocus(true);

		// unselect the row from the table
		((TableWidget) getWidget("organizationsTable")).controller.unselect(-1);

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);

		TableWidget contactsTable = (TableWidget) getWidget("contactsTable");
	}

	public void abort(int state) {
        TableWidget orgContacts = (TableWidget) getWidget("contactsTable");
        orgContacts.controller.setAutoAdd(false);
          	 

		super.abort(state);
                        
        loadTable = true;
        loadNotes = true;                
        clearTabs(); 
        loadTabs();
   
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) orgContacts.controller.manager;
		orgContactsTable.disableRows = true;
        orgContacts.controller.unselect(-1);
       		
		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.DISABLED);	

		// need to get the org name table model
		TableWidget orgNameTM = (TableWidget) getWidget("organizationsTable");
		int rowSelected = orgNameTM.controller.selected;

		// set the update button if needed
		if (rowSelected == -1){
			bpanel.setButtonState("update", AppButton.DISABLED);
			bpanel.setButtonState("prev", AppButton.DISABLED);
			bpanel.setButtonState("next", AppButton.DISABLED);
		}
	}

	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
        
        // this code is for the event of the update mode being enabled 
        loadTable = true;
        loadNotes = true;                
        clearTabs();
        loadTabs();

		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);
		
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		OrganizationContactsTable orgContactsTableManager = (OrganizationContactsTable) contactTable.controller.manager;
		orgContactsTableManager.disableRows = false;
		contactTable.controller.setAutoAdd(true);
		contactTable.controller.addRow();
		
		//set focus to the org name field
		TextBox orgName = (TextBox)getWidget("orgName");
		orgName.setFocus(true);

		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
	}

	public void afterCommitAdd(boolean success) {
//    when a new organization's data is added to the database, this code will make sure that whichever tab is open, 
          //or will be opened subsequently, will have the latest data in it
        
        loadTable = true;
        loadNotes = true;
        
        Integer orgId = (Integer)rpc.getFieldValue("orgId");
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
        
        clearTabs();             
        loadTabs(); 
        
        clearNotesFields();
        
		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
				
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		contactTable.controller.setAutoAdd(false);
        
        
		
		super.afterCommitAdd(success);
	}

	public void query(int state) {
		super.query(state);
		
		//set focus to the org id field
		TextBox orgName = (TextBox)getWidget("orgId");
		orgName.setFocus(true);
	}
	
	public void afterCommitUpdate(boolean success) {
        // when an organization's data is committed, after being updated, to the database, this code will make sure that whichever tab is open, 
        //or will be opened subsequently, will have the latest data in it 
        
        loadTable = true;
        loadNotes = true;
        clearTabs();              
        loadTabs();               
        
//      the note subject and body fields need to be refeshed after every successful commit   
        clearNotesFields();
        
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable) ((TableWidget) getWidget("contactsTable")).controller.manager;
		orgContactsTable.disableRows = true;
		TableWidget contactTable = (TableWidget) getWidget("contactsTable");
		contactTable.controller.setAutoAdd(false);
		
		AppButton removeContactButton = (AppButton) getWidget("removeContactButton");
		removeContactButton.changeState(AppButton.UNPRESSED);
		
        

		super.afterCommitUpdate(success);
	}

	public void commit(int state) {
		if (state == FormInt.QUERY) {
			((TableWidget) ((ScreenTableWidget) ((ScreenTableWidget) widgets
					.get("contactsTable")).getQueryWidget()).getWidget()).controller
					.unselect(-1);
		} else {
			((TableWidget) getWidget("contactsTable")).controller.unselect(-1);
		}
		super.commit(state);
	}
	
	protected void doReset() {
		//we need to reset the dropdowns
		//clear the state dropdown
		((AutoCompleteDropdown)getWidget("state")).reset();
		//clear the country dropdown
		((AutoCompleteDropdown)getWidget("country")).reset();
		
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
		ScreenAutoDropdown displayState = (ScreenAutoDropdown)widgets.get("state");
	    ScreenAutoDropdown queryState = displayState.getQueryWidget();
	               
	    ((AutoCompleteDropdown)displayState.getWidget()).setModel(stateDropdown);
	    ((AutoCompleteDropdown)queryState.getWidget()).setModel(stateDropdown);
	               
	    ScreenTableWidget displayContactTable = (ScreenTableWidget)widgets.get("contactsTable");
	    ScreenTableWidget queryContactTable = (ScreenTableWidget)displayContactTable.getQueryWidget();
	               
	    TableAutoDropdown displayContactState = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
	              																				controller.editors[5];
	    displayContactState.setModel(stateDropdown);
	               
	    TableAutoDropdown queryContactState = (TableAutoDropdown)((TableWidget)queryContactTable.getWidget()).
						controller.editors[5];
	    queryContactState.setModel(stateDropdown);
	     
	     
		//load the country dropdowns
	    ScreenAutoDropdown displayCountry = (ScreenAutoDropdown)widgets.get("country");
	    ScreenAutoDropdown queryCountry = displayCountry.getQueryWidget();
	               
	    ((AutoCompleteDropdown)displayCountry.getWidget()).setModel(countryDropdown);
	    ((AutoCompleteDropdown)queryCountry.getWidget()).setModel(countryDropdown);
	               
	    TableAutoDropdown displayContactCountry = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
	               																				controller.editors[12];
	    displayContactCountry.setModel(countryDropdown);
	               
	    TableAutoDropdown queryContactCountry = (TableAutoDropdown)((TableWidget)queryContactTable.getWidget()).
						controller.editors[12];
	    queryContactCountry.setModel(countryDropdown);
	    
	    
		//load the contact type dropdowns	        	   
  	   TableAutoDropdown displayContactType = (TableAutoDropdown)((TableWidget)displayContactTable.getWidget()).
																controller.editors[0];
   	   displayContactType.setModel(contactTypeDropdown);

   	   TableAutoDropdown queryContactType = (TableAutoDropdown)((TableWidget)queryContactTable.getWidget()).
	        	   											controller.editors[0];
  	   queryContactType.setModel(contactTypeDropdown);
	}
    
    
    private void fillNotesModel(){            
        Integer orgId = null;
        boolean getModel = false;
         
         // access the database only if id is not null  
         if(key!=null){
           if(key.getObject(0)!=null){        
             getModel = true;
             orgId = (Integer)key.getObject(0).getValue(); 
            }        
          } 
            
           if(getModel){               
             NumberObject orgIdObj = new NumberObject();
             orgIdObj.setType("integer");
             orgIdObj.setValue(orgId);
             
            // prepare the argument list for the getObject function
             DataObject[] args = new DataObject[] {orgIdObj}; 
             
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
       
       private void fillContactsModel(){
        
         Integer orgId = null;
         boolean getModel = false;  
         
         // access the database only if id is not null 
         if(key!=null){
          if(key.getObject(0)!=null){        
           getModel = true;
           orgId = (Integer)key.getObject(0).getValue(); 
          }      
         } 
         
          if(getModel){              
             // reset the model so that old data goes away 
              TableController orgContactController = (TableController)(((TableWidget)getWidget("contactsTable")).controller);
              //orgContactController.model.reset();
            
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
                   TableController orgContactController = (TableController)(((TableWidget)getWidget("contactsTable")).controller);
                   orgContactController.setModel((TableModel)((TableField)result).getValue());
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
              TabPanel noteTab = (TabPanel)getWidget("orgTabPanel");        
              int selectedTab = noteTab.getTabBar().getSelectedTab();     
                                   
              if(selectedTab == 0 && loadTable){     
                //load the table  
                  fillContactsModel();
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
              TableController provAddController = (TableController)(((TableWidget)getWidget("contactsTable")).controller);         
              provAddController.model.reset(); 
              provAddController.setModel(provAddController.model);
              rpc.setFieldValue("contactsTable",provAddController.model);
              
              ScreenVertical vp = (ScreenVertical)widgets.get("notesPanel");
              vp.clear();
              
          } 
}
