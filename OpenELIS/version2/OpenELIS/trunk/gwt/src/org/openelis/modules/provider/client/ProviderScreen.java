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
package org.openelis.modules.provider.client;


import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTabPanel;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

public class ProviderScreen extends OpenELISScreenForm<ProviderForm,Query<TableDataRow<Integer>>> implements ClickListener, 
                                                                  TabListener,
                                                                  TableManager{
         
    private ScreenVertical svp = null;
    private AppButton removeContactButton, standardNoteButton;
    private ScreenTextBox provId = null; 
    private TextBox lastName = null;
    private ScreenTextArea noteArea = null;
    private TableWidget provAddController = null;  
    private Dropdown displayType = null;
    private ScreenTabPanel tabPanel;
    private KeyListManager keyList = new KeyListManager();       
    
    private ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    public ProviderScreen(){
        super("org.openelis.modules.provider.server.ProviderService");      
        query = new Query<TableDataRow<Integer>>();
        getScreen(new ProviderForm());        
    }
    
    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf("query:") != -1)
                getProviders(query.substring(6));
            else                         
                super.performCommand(action, obj);            
         }  else {                         
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeContactButton)
            onRemoveRowButtonClick();
        else if (sender == standardNoteButton)
            onStandardNoteButtonClick();    
    }

    public void afterDraw(boolean success) {
        ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");

        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);

        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        // load other widgets
        removeContactButton = (AppButton)getWidget("removeAddressButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");

        provId = (ScreenTextBox)widgets.get(ProvMeta.getId());
        lastName = (TextBox)getWidget(ProvMeta.getLastName());
        // subjectBox = (TextBox)getWidget(ProvMeta.getNote().getSubject());
        noteArea = (ScreenTextArea)widgets.get(ProvMeta.getNote().getText());
        svp = (ScreenVertical) widgets.get("notesPanel");
        tabPanel = (ScreenTabPanel)widgets.get("provTabPanel");
        
        displayType = (Dropdown)getWidget(ProvMeta.getTypeId());

        provAddController = ((TableWidget)getWidget("providerAddressTable"));

        // load dropdowns
        ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
        provAddController = (TableWidget)displayAddressTable.getWidget();

        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);

        super.afterDraw(success);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("state");
        model = getDictionaryEntryKeyList(cache);
        ((TableDropdown)provAddController.columns.get(5).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("country");
        model = getDictionaryEntryKeyList(cache);
        ((TableDropdown)provAddController.columns.get(6).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("provider_type");
        model = getDictionaryIdEntryList(cache);
        displayType.setModel(model);
    }
    
    public void query(){
        //clearNotes();            
       super.query();
    
        //set focus to the last name field
        provId.setFocus(true);
        noteArea.enable(false);
        provAddController.model.enableAutoAdd(false);
    }

    public void add(){                                                  
        svp.clear();
        
        //provAddController.setAutoAdd(true);         
        super.add();     
        
        noteArea.enable(true);
        provId.enable(false);
        
        //set focus to the last name field       
        lastName.setFocus(true);
        provAddController.model.enableAutoAdd(true);
        
    }   
    
    public void abort() {
        provAddController.model.enableAutoAdd(false); 
        super.abort();
    }
    
    protected SyncCallback<ProviderForm> afterUpdate = new SyncCallback<ProviderForm>() {
        public void onFailure(Throwable caught) {
        }
        public void onSuccess(ProviderForm result) {            
            provId.enable(false);                                      
            noteArea.enable(true);
            
            //set focus to the last name field
            lastName.setFocus(true);
            provAddController.model.enableAutoAdd(true);
        }
           
    };    
    
    protected SyncCallback<ProviderForm> commitUpdateCallback = new SyncCallback<ProviderForm>() {
        public void onSuccess(ProviderForm result) {
            if (form.status != Form.Status.invalid)                                
                provAddController.model.enableAutoAdd(false);
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<ProviderForm> commitAddCallback = new SyncCallback<ProviderForm>() {
        public void onSuccess(ProviderForm result) {
            if (form.status != Form.Status.invalid)                                
                provAddController.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };   
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != State.QUERY){
            if (index == 0 && !form.addresses.load) 
                fillAddressModel();
            else if (index == 1 && !form.notes.load) 
                fillNotesModel();
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        form.provTabPanel = tabPanel.getSelectedTabKey();
    }
    
    public boolean canAdd(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canAutoAdd(TableWidget widget,TableDataRow row) {        
        return ((DataObject)row.getCells().get(0)).getValue() != null && !((DataObject)row.getCells().get(0)).getValue().equals("");
    }



    public boolean canDelete(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canEdit(TableWidget widget,TableDataRow set, int row, int col) {
        return true;
    }



    public boolean canSelect(TableWidget widget,TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY){      
            return true;
        } 
        return false;
    }
    
    private void getProviders(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(ProvMeta.getLastName());
            qField.setValue(query);
            commitQuery(qField); 
       }
    }
    
   private void fillNotesModel() {  
       if(form.entityKey == null)
           return;
       
       window.setBusy();
       
       form.notes.entityKey = form.entityKey;
              
       screenService.call("loadNotes", form.notes, new AsyncCallback<NotesForm>(){
           public void onSuccess(NotesForm result){    
               form.notes = result;
               load(form.notes);
               window.clearStatus();
           }
                  
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });       
   }
   
   private void fillAddressModel() {
       
       if(form.entityKey == null)
           return;
       
       window.setBusy();
       
       form.addresses.entityKey = form.entityKey;
       
       screenService.call("loadAddresses", form.addresses, new AsyncCallback<AddressesForm>() {
           public void onSuccess(AddressesForm result) { 
               form.addresses = result;
               load(form.addresses);
               window.clearStatus();
           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });
      } 
     
      
   
   private void onStandardNoteButtonClick(){
       ScreenWindow modal = new ScreenWindow(null,"Standard Note Screen",
                                             "standardNoteScreen","",true,false);
       modal.setName(consts.get("standardNote"));
       modal.setContent(new StandardNotePickerScreen((TextBox)getWidget(ProvMeta.getNote()
                                                                                 .getSubject()),
                                                      (TextArea)getWidget(ProvMeta.getNote()
                                                                                  .getText())));
    }
    
    private void onRemoveRowButtonClick(){
        int index = provAddController.modelIndexList[provAddController.activeRow];
        if (index > -1) 
            provAddController.model.deleteRow(index);                  
    }

    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m;
        TableDataRow<Integer> row;
        DictionaryDO dictDO;
        
        if(list == null)
            return null;
                       
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
    
    private TableDataModel<TableDataRow> getDictionaryEntryKeyList(ArrayList list){
        TableDataModel<TableDataRow> m;
        TableDataRow<String> row;
        DictionaryDO dictDO;
        
        if(list == null)
            return null;
                
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<String>(1);
            dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getEntry();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}
