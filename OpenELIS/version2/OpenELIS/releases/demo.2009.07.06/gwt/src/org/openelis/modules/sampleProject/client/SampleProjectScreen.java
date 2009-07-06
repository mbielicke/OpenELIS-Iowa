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
package org.openelis.modules.sampleProject.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.modules.environmentalSampleLogin.client.SampleProjectForm;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.shipping.client.ShippingScreen.Action;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleProjectScreen extends OpenELISScreenForm<SampleProjectForm,Query<TableDataRow<Integer>>> implements TableManager, TableWidgetListener {

    public enum Action {Commited, Aborted}
    private CommandListener commandTarget;
    private ScreenTableWidget            sampleProjectTable;
    
    private KeyListManager keyList = new KeyListManager();

    public SampleProjectScreen() {
        this(new SampleProjectForm());
        
    }
    
    public SampleProjectScreen(SampleProjectForm form) {                
        super("org.openelis.modules.sampleProject.server.SampleProjectService");
        query = new Query<TableDataRow<Integer>>();

        getScreen(form);
    }
    
    public SampleProjectScreen(SampleProjectForm form, CommandListener target) {                
        super("org.openelis.modules.sampleProject.server.SampleProjectService");
        query = new Query<TableDataRow<Integer>>();
        
        commandTarget = target;

        getScreen(form);
    }

    public void setForm(SampleProjectForm form){
        this.form = form;
        load(form);
    }
    
    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        sampleProjectTable = (ScreenTableWidget)widgets.get("sampleProjectTable");
        ((TableWidget)sampleProjectTable.getWidget()).addTableWidgetListener(this);
        
        super.afterDraw(success);
        
        //if the default set is null we can assume the rpc hasnt been loaded
        //if(form.sampleProjectTable.getValue().getDefaultSet() != null)
        //    load(form);
        //else
        //    form.sampleProjectTable.setValue(((TableWidget)sampleProjectTable.getWidget()).model.getData());
        
        //enable auto add and put the cursor in the first cell
        sampleProjectTable.enable(true);
        ((TableWidget)sampleProjectTable.getWidget()).model.enableAutoAdd(true);
        ((TableWidget)sampleProjectTable.getWidget()).select(0, 0);
    }
    
    /*
    protected AsyncCallback afterCommit = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            trackingNumbersTable.model.enableAutoAdd(false);
            
            if(target != null)
                target.performCommand(Action.Commited, this);
            r
            if(closeOnCommitAbort)
                window.close();
        }
    };*/
    
    public void commit() {
        if(commandTarget != null)
            commandTarget.performCommand(Action.Commited, form.sampleProjectTable.getValue());
        
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
        if(col == 0){
            TableDataRow<Integer> tableRow = ((TableWidget)sampleProjectTable.getWidget()).model.getRow(row);
            DropDownField<Integer> projField = (DropDownField)tableRow.cells[0];
            ArrayList selections = projField.getValue();

            if(selections.size() > 0){
                TableWidget projTable = (TableWidget)sampleProjectTable.getWidget();
                TableDataRow<Integer> selectedRow = (TableDataRow<Integer>)selections.get(0);
                                
                projTable.model.setCell(row, 1, selectedRow.cells[1].getValue());
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
}
