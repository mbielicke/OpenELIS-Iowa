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
package org.openelis.modules.project.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.ProjectMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProjectScreen extends OpenELISScreenForm<ProjectForm, Query<TableDataRow<Integer>>> 
                           implements TableManager, ClickListener    {

    private KeyListManager<Integer> keyList = new KeyListManager<Integer>();
    
    private TableWidget  projParamTableWidget;   
    
    private ProjectMetaMap  projMeta = new ProjectMetaMap();
    
    private AppButton removeParamButton;  
    
    private ScreenTextBox projId;
    
    private TextBox projName;  
    
    private AutoComplete owner;
        
    public ProjectScreen() {
        super("org.openelis.modules.project.server.ProjectService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new ProjectForm());
    }    
    
    public void afterDraw(boolean success) {
        ButtonPanel bpanel, atozButtons;
        CommandChain chain;       
        ResultsTable atozTable;
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        
        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

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
        
        projParamTableWidget = (TableWidget)getWidget("parameterTable");       
        removeParamButton = (AppButton)getWidget("removeParameterButton");       
        
        projId = (ScreenTextBox)widgets.get(projMeta.getId());
        projName = (TextBox)getWidget(projMeta.getName());
        
        owner = (AutoComplete)getWidget(projMeta.getOwnerId());
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
                       
        super.afterDraw(success);
               
        cache = DictionaryCache.getListByCategorySystemName("project_parameter_operations");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)projParamTableWidget.columns.get(1).getColumnWidget()).setModel(model);
    }
    

    public void performCommand(Enum action, Object obj) {        
        if(obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf("query:") != -1)
                getProjects(query.substring(6));
            else                         
                super.performCommand(action, obj);            
         }  else {                         
            super.performCommand(action, obj);
        }        
    }
    
    public void query() {
        super.query();
        projId.setFocus(true);
        removeParamButton.changeState(ButtonState.DISABLED);
        projParamTableWidget.model.enableAutoAdd(false);  
        owner.enabled(false);
    }
    
    public void add() {
        super.add();
        projId.enable(false);
        projName.setFocus(true);
        projParamTableWidget.model.enableAutoAdd(true); 
        projParamTableWidget.activeRow = -1;
        owner.enabled(true);
    }
    
    public void abort() {
        projParamTableWidget.model.enableAutoAdd(false); 
        super.abort();
    }
    
    protected SyncCallback<ProjectForm> afterUpdate = new SyncCallback<ProjectForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(ProjectForm result) {
            projId.enable(false);
            projName.setFocus(true);
            projParamTableWidget.model.enableAutoAdd(true);  
            projParamTableWidget.activeRow = -1;
            owner.enabled(true);
        }
    };   
    
    protected SyncCallback<ProjectForm> commitUpdateCallback = new SyncCallback<ProjectForm>() {
        public void onSuccess(ProjectForm result) {
            if (form.status != Form.Status.invalid)                                
                projParamTableWidget.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<ProjectForm> commitAddCallback = new SyncCallback<ProjectForm>() {
        public void onSuccess(ProjectForm result) {
            if (form.status != Form.Status.invalid)                                
                projParamTableWidget.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    private void getProjects(String query) {
        QueryStringField qField;
        if (state == State.DISPLAY || state == State.DEFAULT) {
            qField = new QueryStringField(projMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
        
    }

    public <T> boolean canAdd(TableWidget widget, TableDataRow<T> set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {        
        return !parameterTableRowEmpty(addRow);
    }

    public <T> boolean canDelete(TableWidget widget,TableDataRow<T> set,int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public <T> boolean canEdit(TableWidget widget,TableDataRow<T> set,int row,int col) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public <T> boolean canSelect(TableWidget widget,TableDataRow<T> set,int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public void onClick(Widget sender) {
        int index = projParamTableWidget.modelIndexList[projParamTableWidget.activeRow];  
        if(index > -1)
            projParamTableWidget.model.deleteRow(index);        
    }        
    
    /**
      * This method finds out whether no value has been set to any fields in the 
      * table for project parameters and returns true if this is the case and false
      * otherwise. 
      */
    private boolean parameterTableRowEmpty(TableDataRow<Integer> addRow) {
        StringField prmField,valField;
        DropDownField<Integer> oprField;
        String value;
        int empty;
        
        prmField = (StringField)addRow.cells[0];
        oprField = (DropDownField<Integer>)addRow.cells[1];
        valField = (StringField)addRow.cells[2];
        empty = 0;
        
        value = prmField.getValue();
        if(value == null || (value != null && "".equals(value.trim())))
            empty++;
        
        if(oprField.getSelectedKey()==null)
            empty++;
        
        value = valField.getValue();
        if(value == null || (value != null && "".equals(value.trim())))
            empty++;
                    
        
        if(empty == 3)
            return true;
        
        return false;
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
}
