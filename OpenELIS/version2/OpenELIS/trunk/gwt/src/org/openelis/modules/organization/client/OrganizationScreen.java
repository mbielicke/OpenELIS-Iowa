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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenTabPanel;
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
import java.util.HashMap;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

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
    private ScreenTabPanel         tabPanel;
    private HashMap<String,FieldType> fields;
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
   
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
        tabPanel = (ScreenTabPanel)widgets.get("orgTabPanel");
  
        updateChain.add(afterUpdate);
        
        super.afterDraw(success);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("state");
        model = getDictionaryEntryKeyList(cache);
        states.setModel(model);
        ((TableDropdown)contactsTable.columns.get(5).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("country");
        model = getDictionaryEntryKeyList(cache);
        countries.setModel(model);
        ((TableDropdown)contactsTable.columns.get(7).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("contact_type");
        model = getDictionaryEntryKeyList(cache);
        ((TableDropdown)contactsTable.columns.get(0).getColumnWidget()).setModel(model);
    }
    
    public void loadScreen(OrganizationForm rpc) {
        form.load(rpc);
        load();
        if (form.status == Form.Status.invalid) {
            drawErrors();
        }
        if(form.getErrors() != null && form.getErrors().size() > 0){
            if(form.getErrors().size() > 1){
                window.setMessagePopup((String[])form.getErrors().toArray(new String[form.getErrors().size()]), "ErrorPanel");
                window.setError("(Error 1 of "+form.getErrors().size()+") "+(String)form.getErrors().get(0));
            }else
                window.setError((String)form.getErrors().get(0));
        }            
    }
    /*
    public void drawScreen(String xmlDef) {
        xml = XMLParser.parse(xmlDef);
        draw();
        this.fields = new HashMap<String,FieldType>();
            NodeList rpcList = xml.getDocumentElement().getElementsByTagName("rpc");
            for(int i = 0; i < rpcList.getLength(); i++){
                if(rpcList.item(i).getNodeType() == Node.ELEMENT_NODE && rpcList.item(i).getNodeName().equals("rpc")){
                    String key = rpcList.item(i).getAttributes().getNamedItem("key").getNodeValue();
                    NodeList fields = rpcList.item(i).getChildNodes();
                    for(int j = 0; j < fields.getLength(); j++){
                        if(fields.item(j).getNodeType() == Node.ELEMENT_NODE && !fields.item(j).getNodeName().equals("rpc")){
                            String fkey = fields.item(j).getAttributes().getNamedItem("key").getNodeValue();
                            FieldType field = (FieldType)ScreenBase.createField(fields.item(j));
                            if(widgets.get(fkey) != null)
                                widgets.get(fkey).load((AbstractField)field);
                            this.fields.put(fkey,field);
                        }
                    }
                }
            }
           
   }
   */
 
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
    
    protected SyncCallback<OrganizationForm> afterUpdate = new SyncCallback<OrganizationForm>() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(OrganizationForm result) {
            contactsTable.model.enableAutoAdd(true);
            orgId.enable(false);
            orgName.setFocus(true);
        }
    };


    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        //form.orgTabPanel = tabPanel.getSelectedTabKey();
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
                form.notes.load(form.notes.data);
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
        ScreenWindow modal = new ScreenWindow(null,"Standard Note Screen","standardNoteScreen","",true,false);
        modal.setName(consts.get("standardNote"));
        modal.setContent(new StandardNotePickerScreen((TextBox)getWidget(OrgMeta.getNote().getSubject()), (TextArea)getWidget(OrgMeta.getNote().getText())));
    }

    private void onRemoveContactRowButtonClick() {
        int selectedRow = contactsTable.model.getSelectedIndex();
        if (selectedRow > -1 && contactsTable.model.numRows() > 0) {
            contactsTable.model.deleteRow(selectedRow);

        }
    }

    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<Integer> row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
    
    private TableDataModel<TableDataRow> getDictionaryEntryKeyList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<String> row = new TableDataRow<String>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getEntry();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}