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

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationScreen extends OpenELISScreenForm implements
                                                          ClickListener,
                                                          TabListener {

    private static boolean loaded = false;
    private static DataModel stateDropdown, countryDropdown,
                    contactTypeDropdown;

    private AppButton        removeContactButton, standardNoteButton;
    private TableWidget       contactsTable;
    private ScreenTextBox    orgId;
    private ScreenTextArea   noteText;
    private TextBox          orgName;
    private ButtonPanel      atozButtons;
    private KeyListManager   keyList = new KeyListManager();
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();

    public OrganizationScreen() {
        super("org.openelis.modules.organization.server.OrganizationService",
              !loaded);
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
        loaded = true;

        TableWidget d;
        QueryTable q;
        ScreenTableWidget sw;
        Dropdown drop;
        AToZTable atozTable;

        //
        // we are interested in in getting button actions in two places,
        // modelwidget and us.
        //
        atozTable = (AToZTable)getWidget("azTable");
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
        contactsTable = (TableWidget)getWidget("contactsTable");
        contactsTable.model.enableAutoAdd(false);
        
        orgId = (ScreenTextBox)widgets.get(OrgMeta.getId());
        orgName = (TextBox)getWidget(OrgMeta.getName());
        noteText = (ScreenTextArea)widgets.get(OrgMeta.getNote().getText());
   
        if (stateDropdown == null) {
            stateDropdown = (DataModel)initData.get("states");
            countryDropdown = (DataModel)initData.get("countries");
            contactTypeDropdown = (DataModel)initData.get("contacts");
        }

        drop = (Dropdown)getWidget(OrgMeta.getAddress().getState());
        drop.setModel(stateDropdown);

        sw = (ScreenTableWidget)widgets.get("contactsTable");
        d = (TableWidget)sw.getWidget();
        q = (QueryTable)sw.getQueryWidget().getWidget();
        //
        // state dropdown
        //
        ((TableDropdown)d.columns.get(5).getColumnWidget()).setModel(stateDropdown);
        ((TableDropdown)q.columns.get(5).getColumnWidget()).setModel(stateDropdown);

        //
        // country dropdowns
        //
        drop = (Dropdown)getWidget(OrgMeta.getAddress().getCountry());
        drop.setModel(countryDropdown);

        ((TableDropdown)d.columns.get(7).getColumnWidget()).setModel(countryDropdown);
        ((TableDropdown)q.columns.get(7).getColumnWidget()).setModel(countryDropdown);

        //
        // contact type dropdowns
        //
        ((TableDropdown)d.columns.get(0).getColumnWidget()).setModel(contactTypeDropdown);
        ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(contactTypeDropdown);
        

        
        //override the callbacks
        updateChain.add(afterUpdate);
        
        super.afterDraw(success);
       ((FormRPC)rpc.getField("contacts")).setFieldValue("contactsTable", contactsTable.model.getData());
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
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
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
        if(state != FormInt.State.QUERY){
            if (index == 0 && !((FormRPC)rpc.getField("contacts")).load) {
                fillContactsModel();
            } else if (index == 2 && !((FormRPC)rpc.getField("notes")).load) {
                fillNotesModel();
            }
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

    /*
     * Get all notes for organization (key)
     */
    private void fillNotesModel(){  
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");
        
        screenService.getObject("loadNotes", new Data[] {key,rpc.getField("notes")}, new AsyncCallback<FormRPC>(){
            public void onSuccess(FormRPC result){    
                // get the datamodel, load it in the notes panel and set the value in the rpc
                load(result);
                rpc.setField("notes",(FormRPC)result);
                window.setStatus("","");
            }
                   
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });        
    }

    private void fillContactsModel() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");
        
        screenService.getObject("loadContacts", new Data[] {key,rpc.getField("contacts")}, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
               
                load(result);
                rpc.setField("contacts", (FormRPC)result);
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
