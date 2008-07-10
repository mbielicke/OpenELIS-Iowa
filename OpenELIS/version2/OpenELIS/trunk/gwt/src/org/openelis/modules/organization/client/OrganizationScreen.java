/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
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
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationScreen extends OpenELISScreenForm implements
                                                          ClickListener,
                                                          TabListener {

    private static boolean loaded = false;
    private static DataModel stateDropdown, countryDropdown,
                    contactTypeDropdown;

    private AppButton        removeContactButton, standardNoteButton;
    private EditTable        contactsController;
    private ScreenTextBox    orgId;
    private ScreenTextArea   noteText;
    private TextBox          orgName;
    private ButtonPanel      atozButtons;

    private boolean          loadNotes = true, loadContacts = true,
                    clearNotes = false, clearContacts = false;

    private ScreenVertical   svp       = null;
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();

    public OrganizationScreen() {
        super("org.openelis.modules.organization.server.OrganizationService",
              !loaded);
    }

    public void onChange(Widget sender) {
        if (sender == atozButtons) {
            String action = atozButtons.buttonClicked.action;
            if (action.startsWith("query:")) {
                getOrganizations(action.substring(6, action.length()));
            }
        } else {
            super.onChange(sender);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeContactButton)
            onRemoveContactRowButtonClick();
        else if (sender == standardNoteButton)
            onStandardNoteButtonClick();
    }

    public void afterDraw(boolean success) {
        loaded = true;

        TableWidget d;
        QueryTable q;
        ScreenTableWidget sw;
        AutoCompleteDropdown drop;
        AToZTable atozTable;

        //
        // we are interested in in getting button actions in two places,
        // modelwidget and us.
        //
        atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        removeContactButton = (AppButton)getWidget("removeContactButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");
        
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);

        //
        // disable auto add and make sure there are no rows in the table
        //
        contactsController = ((TableWidget)getWidget("contactsTable")).controller;
        contactsController.setAutoAdd(false);
        ((OrganizationContactsTable)contactsController.manager).setOrganizationForm(this);

        orgId = (ScreenTextBox)widgets.get(OrgMeta.getId());
        orgName = (TextBox)getWidget(OrgMeta.getName());
        noteText = (ScreenTextArea)widgets.get(OrgMeta.getNote().getText());
        svp = (ScreenVertical) widgets.get("notesPanel");

        if (stateDropdown == null) {
            stateDropdown = (DataModel)initData.get("states");
            countryDropdown = (DataModel)initData.get("countries");
            contactTypeDropdown = (DataModel)initData.get("contacts");
        }

        drop = (AutoCompleteDropdown)getWidget(OrgMeta.getAddress().getState());
        drop.setModel(stateDropdown);

        sw = (ScreenTableWidget)widgets.get("contactsTable");
        d = (TableWidget)sw.getWidget();
        q = (QueryTable)sw.getQueryWidget().getWidget();
        //
        // state dropdown
        //
        ((TableAutoDropdown)d.controller.editors[5]).setModel(stateDropdown);
        ((TableAutoDropdown)q.editors[5]).setModel(stateDropdown);

        //
        // country dropdowns
        //
        drop = (AutoCompleteDropdown)getWidget(OrgMeta.getAddress().getCountry());
        drop.setModel(countryDropdown);

        ((TableAutoDropdown)d.controller.editors[7]).setModel(countryDropdown);
        ((TableAutoDropdown)q.editors[7]).setModel(countryDropdown);

        //
        // contact type dropdowns
        //
        ((TableAutoDropdown)d.controller.editors[0]).setModel(contactTypeDropdown);
        ((TableAutoDropdown)q.editors[0]).setModel(contactTypeDropdown);
        
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(success);
    }

    /*
     * Overriden to reset the data in Contact and Note tabs
     */
    public void afterFetch(boolean success) {
        if (success) {
            loadContacts = true;
            loadNotes = true;
            loadTabs();
        }
        super.afterFetch(success);
    }

    public void query() {
        super.query();
        orgId.setFocus(true);
        //
        // disable notes and contact remove button
        //
        noteText.enable(false);
        removeContactButton.changeState(AppButton.ButtonState.DISABLED);
        clearNotes();
    }

    public void add() {
        ScreenVertical vp;
        //
        // make sure the contact table gets set before the main add
        //
        contactsController.setAutoAdd(true);
        super.add();

        //
        // disable / remove anything that is not editable
        //
        orgId.enable(false);
        vp = (ScreenVertical)widgets.get("notesPanel");
        vp.clear();

        orgName.setFocus(true);
    }

    public void update() {
        contactsController.setAutoAdd(true);
        super.update();
    }

    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        if (success) {
            loadContacts = true;
            loadNotes = true;
            loadTabs();
        }
        orgId.enable(false);
        orgName.setFocus(true);
    }

    public void abort() {
        contactsController.setAutoAdd(false);
        
        if(state == FormInt.State.ADD || state == FormInt.State.QUERY){
            loadContacts = false;
            clearContacts = true;
            
            loadNotes = false;
            clearNotes = true;
        }else{
            loadContacts = true;
            loadNotes = true;
        }
        
        //the super needs to ge before the load tabs method or the table wont load.
        super.abort();
        
        loadTabs();        
    }

    public void afterCommitAdd(boolean success) {
        contactsController.setAutoAdd(false);
        super.afterCommitAdd(success);

        // we need to load the notes tab if it has been already loaded
        if(success){ 
            loadNotes = true;
            clearNotes = true;
            
            loadContacts = true;
            clearContacts = false;
            
            Integer orgId = (Integer)rpc.getFieldValue(OrgMeta.getId());
            NumberObject orgIdObj = new NumberObject(orgId);
            
//          done because key is set to null in AppScreenForm for the add operation 
            if(key ==null){  
                key = new DataSet();
                key.setKey(orgIdObj);
                
            }else{
                key.setKey(orgIdObj);
                
            }
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearNotesFields();
        }
    }

    public void afterCommitUpdate(boolean success) {
        contactsController.setAutoAdd(false);
        //we need to do this reset to get rid of the last row
        contactsController.reset();
        
        super.afterCommitUpdate(success);

        //we need to load the notes tab if it has been already loaded
        if(success){ 
            loadNotes = true;
            clearNotes = true;
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearNotesFields();
        }
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    }

    /*
     * Overriden to allow lasy loading Contact and Note tabs
     */
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if (index == 0 && loadContacts) {
            if (clearContacts)
                clearContacts();
            fillContactsModel();
            loadContacts = false;
        } else if (index == 1 && loadNotes) {
            if (clearNotes)
                clearNotes();
            fillNotesModel();
            loadNotes = false;
        }
        return true;
    }

    private void getOrganizations(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            
            FormRPC rpc;

            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(OrgMeta.getName(), query);
            commitQuery(rpc);
        }
    }

    private void loadTabs() {
        TabPanel noteTab = (TabPanel)getWidget("orgTabPanel");
        int selectedTab = noteTab.getTabBar().getSelectedTab();

        if (selectedTab == 0 && loadContacts) {
            // if there was data previously then clear the contacts table
            // otherwise don't
            if (clearContacts) {
                clearContacts();
            }
            // load the table
            fillContactsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadContacts = false;
        }

        else if (selectedTab == 1 && loadNotes) {
            // if there was data previously then clear the notes panel otherwise
            // don't
            if (clearNotes) {
                clearNotes();
            }
            // load the notes model
            fillNotesModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadNotes = false;

        }
    }

    /*
     * Get all notes for organization (key)
     */
    private void fillNotesModel(){            
        Integer orgId = null;
        boolean getModel = false;
         
         // access the database only if id is not null  
         if(key!=null){
           if(key.getKey()!=null){        
             getModel = true;
             orgId = (Integer)key.getKey().getValue(); 
           }else{
               clearNotes = false;
           }
         }else{
             clearNotes = false;
         } 
            
           if(getModel){ 
               window.setStatus("","spinnerIcon");
               NumberObject orgIdObj = new NumberObject(orgId);
                 
               // prepare the argument list for the getObject function
               DataObject[] args = new DataObject[] {orgIdObj}; 
                 
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

    private void clearNotes() {
        svp = (ScreenVertical)widgets.get("notesPanel");
        svp.clear();
    }
    
    private void clearNotesFields(){
        //     the note subject and body fields need to be refeshed after every successful commit 
           TextBox subjectBox = (TextBox)getWidget(OrgMeta.getNote().getSubject());           
           subjectBox.setText("");
          TextArea noteArea = (TextArea)getWidget(OrgMeta.getNote().getText());
          noteArea.setText("");           
          rpc.setFieldValue(OrgMeta.getNote().getSubject(), null);
          rpc.setFieldValue(OrgMeta.getNote().getText(), null);  
       }

    private void fillContactsModel() {
        Integer orgId = null;
        NumberObject orgIdObj;
        TableField f;

        if (key == null || key.getKey() == null) {
            clearContacts = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orgId = (Integer)key.getKey().getValue();
        orgIdObj = new NumberObject(orgId);

        f = new TableField();
        f.setValue(contactsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orgIdObj, f};

        screenService.getObject("getContactsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                rpc.setFieldValue("contactsTable",
                                  (TableModel)((TableField)result).getValue());
                EditTable orgContactController = (EditTable)(((TableWidget)getWidget("contactsTable")).controller);
                orgContactController.loadModel((TableModel)((TableField)result).getValue());

                clearContacts = orgContactController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }

    private void clearContacts() {
        EditTable orgContactController = (EditTable)(((TableWidget)getWidget("contactsTable")).controller);
        orgContactController.model.reset();
        orgContactController.setModel(orgContactController.model);
        rpc.setFieldValue("contactsTable", orgContactController.model);
    }

    private void onStandardNoteButtonClick() {
        PopupPanel standardNotePopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel,
                                                     "Choose Standard Note",
                                                     "standardNotePicker",
                                                     "Loading...");
        pickerWindow.setContent(new StandardNotePickerScreen((TextArea)getWidget(OrgMeta.getNote().getText())));

        standardNotePopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        standardNotePopupPanel.setPopupPosition(left, top);
        standardNotePopupPanel.show();
    }

    private void onRemoveContactRowButtonClick() {
        int selectedRow = contactsController.selected;
        if (selectedRow > -1 && contactsController.model.numRows() > 1) {
            TableRow row = contactsController.model.getRow(selectedRow);
            contactsController.model.hideRow(row);
            
            // reset the model
            contactsController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
}
