/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.organization.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import java.util.ArrayList;

public class OrganizationScreen extends OpenELISScreenForm<OrganizationForm,Query<TableDataRow<Integer>>> implements
                                                          ClickListener,
                                                          TabListener {

    private AppButton              removeContactButton, standardNoteButton;
    private TableWidget            contactsTable;
    private ScreenTextBox          orgId;
    private ScreenTextArea         noteText;
    private TextBox                orgName;
    private ButtonPanel            atozButtons;
    private KeyListManager         keyList = new KeyListManager();
    private Dropdown               states;
    private Dropdown               countries;
    private QueryTable             queryContactsTable;
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    /*
     * This callback is used to check the returned RPC for updated DataModels for the dropdown
     * widgets on the screen.  It is inserted at the front of the call chain.
     * 
     * if model is returned set it to the widgets and make sure to null the rpc field 
     * so it is not sent back with future RPC calls
     */
    AsyncCallback<OrganizationForm> checkModels = new AsyncCallback<OrganizationForm>() {
        public void onSuccess(OrganizationForm rpc) {
            if(rpc.contactTypes != null) {
                setContactTypesModel(rpc.contactTypes);
                rpc.contactTypes = null;
            }
            if(rpc.countries != null) {
                setCountriesModel(rpc.countries);
                rpc.countries = null;
            }
            if(rpc.states != null) {
                setStatesModel(rpc.states);
                rpc.states = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };

    public OrganizationScreen() {
        super("org.openelis.modules.organization.server.OrganizationService");
        /*
         * Setting widgets in wrappedWidgets has three potential effects.  
         * 1. Can be used to link widgets from the XML definition to member fields in the screen class
         * 2. Can be used to set widgets to parameterized versions that accept generics
         * 3. Can be used to set Widgets to classes that extend the widget being defined in the xml.
         *    (i.e. ContactsTable extends TableWidget<DataSet> could be a table specifically for contacts
         *     with additional helper methods specific to contacts)
         */
        wrappedWidgets.put(OrgMeta.getAddress().getState(), states = new Dropdown());
        wrappedWidgets.put(OrgMeta.getAddress().getCountry(), countries = new Dropdown());
        wrappedWidgets.put("contactsTable", contactsTable = new TableWidget());
        
        /*
         * To assign the Form used in the RPC to a specific Form instance which is the point of the upgrade,
         * we need to insert an instance of the form to use into the forms hash under the "display" key.
         * 
         * This is required mostly for backwards compatibility until all screens can be upgraded. Then we may 
         * have the option of setting this some other way.
         */
        query = new Query<TableDataRow<Integer>>();
        getScreen(new OrganizationForm());
    }

    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getOrganizations(baction.substring(6, baction.length()));
            }else
                super.performCommand(action, obj);
        } else {
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeContactButton)
            onRemoveContactRowButtonClick();
        else if (sender == standardNoteButton)
            onStandardNoteButtonClick();
    }

    public void afterDraw(boolean success) {

        ResultsTable atozTable;
        //
        // we are interested in in getting button actions in two places,
        // modelwidget and us.
        //
        atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(atozTable);
        formChain.addCommand(atozButtons);

        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        removeContactButton = (AppButton)getWidget("removeContactButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");
        
        //
        // disable auto add and make sure there are no rows in the table
        //
        contactsTable.model.enableAutoAdd(false);
        
        orgName = (TextBox)getWidget(OrgMeta.getName());
        noteText = (ScreenTextArea)widgets.get(OrgMeta.getNote().getText());
        orgId = (ScreenTextBox)widgets.get(OrgMeta.getId());
   
        //queryContactsTable = (QueryTable)((ScreenTableWidget)widgets.get("contactsTable")).getQueryWidget().getWidget();
   
        /*
         * Setting of the models has been split to three methods so that they can be individually updated when needed.
         * 
         * Models are now pulled directly from RPC rather than initData.
         */
        setContactTypesModel(form.contactTypes);
        setCountriesModel(form.countries);
        setStatesModel(form.states);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        form.contactTypes = null;
        form.countries = null;
        form.states = null;

        //override the callbacks
        updateChain.add(afterUpdate);
        
        /*
         * Set the CheckModels to the first call back in all chains
         * 
         * It is debatable if the checkmodels needs to be in all call chains
         * at a minimum though it should be in fetchChain and updateChain 
         */
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        
        super.afterDraw(success);
        form.contacts.contacts.setValue(contactsTable.model.getData());
    }
    
    public void setContactTypesModel(TableDataModel<TableDataRow<Integer>> typesModel) {
        ((TableDropdown)contactsTable.columns.get(0).getColumnWidget()).setModel(typesModel);
        //((TableDropdown)queryContactsTable.columns.get(0).getColumnWidget()).setModel(typesModel);
    }
    
    public void setCountriesModel(TableDataModel<TableDataRow<String>> countriesModel) {
        countries.setModel(countriesModel);
        ((TableDropdown)contactsTable.columns.get(7).getColumnWidget()).setModel(countriesModel);
        //((TableDropdown)queryContactsTable.columns.get(7).getColumnWidget()).setModel(countriesModel);
    }
    
    public void setStatesModel(TableDataModel<TableDataRow<String>> statesModel) {
        states.setModel(statesModel);
        ((TableDropdown)contactsTable.columns.get(5).getColumnWidget()).setModel(statesModel);
        //((TableDropdown)queryContactsTable.columns.get(5).getColumnWidget()).setModel(statesModel);
    }
    
    public void query() {
        super.query();
        orgId.setFocus(true);
        //
        // disable notes and contact remove button
        //
        noteText.enable(false);
        removeContactButton.changeState(AppButton.ButtonState.DISABLED);
    }

    public void add() {
        super.add();

        contactsTable.model.enableAutoAdd(true);
        
        //
        // disable / remove anything that is not editable
        //
        orgId.enable(false);
        ((ScreenVertical)widgets.get("notesPanel")).clear();

        orgName.setFocus(true);
    }
    
    protected AsyncCallback<OrganizationForm> afterUpdate = new AsyncCallback<OrganizationForm>() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(OrganizationForm result) {
            contactsTable.model.enableAutoAdd(true);
            orgId.enable(false);
            orgName.setFocus(true);
        }
    };


    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    }

    /*
     * Overriden to allow lazy loading Contact and Note tabs
     */
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != State.QUERY){
            if (index == 0 && !((OrganizationForm)form).contacts.load) {
                fillContactsModel();
            } else if (index == 2 && !((OrganizationForm)form).notes.load) {
                fillNotesModel();
            }
        }
        return true;
    }

    private void getOrganizations(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(OrgMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }

    /*
     * Get all notes for organization (key)
     */
    private void fillNotesModel(){  
        if(form.entityKey == null)
            return;
        
        window.setStatus("","spinnerIcon");
        //NotesRPC nrpc = new NotesRPC();
        //nrpc.key = key;
        form.notes.entityKey = form.entityKey;
        screenService.call("loadNotes", form.notes, new AsyncCallback<NotesForm>(){
            public void onSuccess(NotesForm result){    
                form.notes = result;
                load(form.notes);
                window.setStatus("","");
            }
                   
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });        
    }

    private void fillContactsModel() {
        if(form.entityKey == null)
            return;
        
        window.setStatus("","spinnerIcon");
        //ContactsRPC crpc = new ContactsRPC();
        //crpc.key = key;
        form.contacts.entityKey = form.entityKey;
        screenService.call("loadContacts", form.contacts, new AsyncCallback<ContactsForm>() {
            public void onSuccess(ContactsForm result) {
                form.contacts = result;
                load(form.contacts);
                window.setStatus("","");

            }
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
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
        int selectedRow = contactsTable.model.getSelectedIndex();
        if (selectedRow > -1 && contactsTable.model.numRows() > 0) {
            contactsTable.model.deleteRow(selectedRow);

        }
    }

}