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
package org.openelis.modules.instrument.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.screen.deprecated.ScreenTableWidget;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.Dropdown;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.gwt.widget.table.deprecated.TableDropdown;
import org.openelis.gwt.widget.table.deprecated.TableManager;
import org.openelis.gwt.widget.table.deprecated.TableWidget;
import org.openelis.metamap.InstrumentMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InstrumentScreen extends OpenELISScreenForm<InstrumentForm, Query<TableDataRow<Integer>>> implements
                                                                                                      TableManager,
                                                                                                      ClickListener{
    
    private KeyListManager<Integer> keyList = new KeyListManager<Integer>();
    
    private InstrumentMetaMap instMeta = new InstrumentMetaMap();
    
    private Dropdown instType;   
    
    private TableWidget logTableWidget;
    
    private TextBox instName;
    
    private AppButton removeEntryButton;

    public InstrumentScreen() {
        super("org.openelis.modules.instrument.server.InstrumentService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new InstrumentForm());
    }
    
    public void afterDraw(boolean success) {
        ButtonPanel bpanel, atozButtons;
        CommandChain chain;       
        ResultsTable atozTable;
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        ScreenTableWidget sl,sa;
        
        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        bpanel = (ButtonPanel)getWidget("buttons");        
        //
        // we are interested in getting button actions in two places,
        // modelwidget and this screen.
        //
        chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);    
        
        instType = (Dropdown)getWidget(instMeta.getTypeId()); 
        
        sl = (ScreenTableWidget)widgets.get("logTable");
        logTableWidget = (TableWidget)sl.getWidget();   
        sl.submit(form.logTable);        
        
        instName = (TextBox)getWidget(instMeta.getName());        
        
        removeEntryButton = (AppButton)getWidget("removeEntryButton"); 
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
                
        super.afterDraw(success);
        
        cache = DictionaryCache.getListByCategorySystemName("instrument_type");
        model = getDictionaryIdEntryList(cache);
        instType.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("instrument_log_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)logTableWidget.columns.get(0).getColumnWidget()).setModel(model);
    }
    
    public void performCommand(Enum action, Object obj) {        
        if(obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf("*") != -1)
                getInstruments(query);
            else                         
                super.performCommand(action, obj);            
         }  else {                         
            super.performCommand(action, obj);
        }        
    }
    
    public void query() {
        super.query();
        enableTableAutoAdd(false);
    }
    
    public void add() {
        super.add();
        instName.setFocus(true);
        enableTableAutoAdd(true); 
        setActiveRow();         
    }
    
    public void abort() {
        enableTableAutoAdd(false);
        super.abort();
    }
    
    protected SyncCallback<InstrumentForm> afterUpdate = new SyncCallback<InstrumentForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(InstrumentForm result) {
            instName.setFocus(true);
            enableTableAutoAdd(true);
            setActiveRow();

        }
    };
    
    protected SyncCallback<InstrumentForm> commitUpdateCallback = new SyncCallback<InstrumentForm>() {
        public void onSuccess(InstrumentForm result) {
            if (form.status != Form.Status.invalid)                                           
                enableTableAutoAdd(false);            
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<InstrumentForm> commitAddCallback = new SyncCallback<InstrumentForm>() {
        public void onSuccess(InstrumentForm result) {
            if (form.status != Form.Status.invalid)                                           
                enableTableAutoAdd(false);            
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };    
    
    private void getInstruments(String query) {
        QueryStringField qField;
        if (state == State.DISPLAY || state == State.DEFAULT) {
            qField = new QueryStringField(instMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
        
    }
    
    public <T> boolean canAdd(TableWidget widget, TableDataRow<T> set, int row) {
        // TODO Auto-generated method stub
        return false;
    }


    public <T> boolean canAutoAdd(TableWidget widget, TableDataRow<T> addRow) {
        DropDownField<Integer> ddf;
        DateField dtf;

        ddf = (DropDownField<Integer>)addRow.cells[0];
        dtf = (DateField)addRow.cells[2];
        if(ddf.getSelectedKey() != null && dtf.getValue() != null)
            return true;
        
        return false;
    }


    public <T> boolean canDelete(TableWidget widget,
                                 TableDataRow<T> set,
                                 int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public <T> boolean canEdit(TableWidget widget,
                               TableDataRow<T> set,
                               int row,
                               int col) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public <T> boolean canSelect(TableWidget widget,
                                 TableDataRow<T> set,
                                 int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public void onClick(Widget sender) {
        if(sender == removeEntryButton) {
            removeLogRow();
        } 

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
    
    private void removeLogRow() {
        int index = logTableWidget.modelIndexList[logTableWidget.activeRow];        
        if (index > -1)
            logTableWidget.model.deleteRow(index);
    }
          
    
    private void enableTableAutoAdd(boolean enable) {
        logTableWidget.model.enableAutoAdd(enable); 
    }
    
    private void setActiveRow() {
        logTableWidget.activeRow = -1;
    }

}
