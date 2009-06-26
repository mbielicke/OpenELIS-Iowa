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
package org.openelis.modules.sampleOrganization.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.modules.environmentalSampleLogin.client.SampleOrganizationForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleProjectForm;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoForm;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.order.client.OrderOrgKey;
import org.openelis.modules.organization.client.OrganizationForm;
import org.openelis.modules.sampleProject.client.SampleProjectScreen.Action;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextArea;

public class SampleOrganizationScreen extends OpenELISScreenForm<SampleOrganizationForm,Query<TableDataRow<Integer>>> implements TableManager, TableWidgetListener {

    public enum Action {Commited, Aborted}
    private CommandListener commandTarget;
    private ScreenTableWidget            sampleOrgTable;
    
    private KeyListManager keyList = new KeyListManager();

    AsyncCallback<SampleOrganizationForm> checkModels = new AsyncCallback<SampleOrganizationForm>() {
        public void onSuccess(SampleOrganizationForm rpc) {
            if(rpc.types != null) {
                setOrgTypesModel(rpc.types);
                rpc.types = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public SampleOrganizationScreen() {                
        this(new SampleOrganizationForm());
        
    }
    
    public SampleOrganizationScreen(SampleOrganizationForm form) {                
        super("org.openelis.modules.sampleOrganization.server.SampleOrganizationService");
        query = new Query<TableDataRow<Integer>>();

        getScreen(form);
    }
    
    public SampleOrganizationScreen(SampleOrganizationForm form, CommandListener target) {                
        super("org.openelis.modules.sampleOrganization.server.SampleOrganizationService");
        query = new Query<TableDataRow<Integer>>();
        
        commandTarget = target;

        getScreen(form);
    }
    
    public void setForm(SampleOrganizationForm form){
        this.form = form;
        load(form);
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        sampleOrgTable = (ScreenTableWidget)widgets.get("sampleOrganizationTable");
        ((TableWidget)sampleOrgTable.getWidget()).addTableWidgetListener(this);
        
        setOrgTypesModel(form.types);
        form.types = null;
        
        super.afterDraw(success);
        
        //if the default set is null we can assume the rpc hasnt been loaded        
        if(form.sampleOrganizationTable.getValue().getDefaultSet() != null)
            load(form);
        else
            form.sampleOrganizationTable.setValue(((TableWidget)sampleOrgTable.getWidget()).model.getData());
        
        //enable auto add and put the cursor in the first cell
        sampleOrgTable.enable(true);
        ((TableWidget)sampleOrgTable.getWidget()).model.enableAutoAdd(true);
        ((TableWidget)sampleOrgTable.getWidget()).select(0, 0);
    }
    
    public void commit() {
        if(commandTarget != null)
            commandTarget.performCommand(Action.Commited, form.sampleOrganizationTable.getValue());
        
        window.close();
    }

    public void abort() {
        if(commandTarget != null)
            commandTarget.performCommand(Action.Aborted, null);
        
        window.close();
    }

    //
    //start table manager methods
    //
    public  boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public  boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return !tableRowEmpty(addRow);
    }

    public  boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public  boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(col == 0 || col == 2)
            return true;
        
        return false;
    }

    public  boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        return true;
    }
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }
    
    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }
    
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(col == 2){
            TableDataRow<Integer> tableRow = ((TableWidget)sampleOrgTable.getWidget()).model.getRow(row);
            DropDownField<Integer> orgField = (DropDownField)tableRow.cells[2];
            ArrayList selections = orgField.getValue();

            if(selections.size() > 0){
                TableWidget sampleTable = (TableWidget)sampleOrgTable.getWidget();
                TableDataRow<Integer> selectedRow = (TableDataRow<Integer>)selections.get(0);
                                
                sampleTable.model.setCell(row, 1, selectedRow.key);
                sampleTable.model.setCell(row, 3, selectedRow.cells[2].getValue());
                sampleTable.model.setCell(row, 4, selectedRow.cells[3].getValue());
            }
        }
    }
    //
    //end table listener methods
    //
    
    private boolean tableRowEmpty(TableDataRow<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.cells.length; i++){
            if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                empty = false;
                break;
            }
        }

        return empty;
    }
    
    public void setOrgTypesModel(TableDataModel<TableDataRow<Integer>> typesModel) {
        ((TableDropdown)((TableWidget)sampleOrgTable.getWidget()).columns.get(0).getColumnWidget()).setModel(typesModel);
    }
}