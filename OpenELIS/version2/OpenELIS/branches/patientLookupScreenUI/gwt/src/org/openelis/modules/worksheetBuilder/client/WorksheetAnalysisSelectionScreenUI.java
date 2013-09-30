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
package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.constants.Messages;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public class WorksheetAnalysisSelectionScreenUI extends Screen
                                                implements HasActionHandlers<WorksheetAnalysisSelectionScreenUI.Action> {

    @UiTemplate("WorksheetAnalysisSelection.ui.xml")
    interface WorksheetAnalysisSelectionUiBinder extends UiBinder<Widget, WorksheetAnalysisSelectionScreenUI> {
    };

    private static WorksheetAnalysisSelectionUiBinder uiBinder = GWT.create(WorksheetAnalysisSelectionUiBinder.class);

    @UiField
    protected Button                                  select, cancel;
    @UiField
    protected Table                                   worksheetAnalysisTable;

    protected Integer                                 worksheetId;
    protected WorksheetManager1                       manager;

    public enum Action {
        SELECT, CANCEL
    };

    public WorksheetAnalysisSelectionScreenUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void initialize() {
        //
        // worksheet analysis search results table
        //
        addScreenHandler(worksheetAnalysisTable, "worksheetAnalysisTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                worksheetAnalysisTable.setEnabled(true);
                worksheetAnalysisTable.setAllowMultipleSelection(true);
            }
        });
        
        worksheetAnalysisTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (worksheetAnalysisTable.getSelectedRow() != -1)
                    select.setEnabled(true);
            }
        });
        
        worksheetAnalysisTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (worksheetAnalysisTable.getSelectedRow() == -1)
                    select.setEnabled(false);
            }
        });
        
        worksheetAnalysisTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                select.setEnabled(true);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(true);
            }
        });

        setState(State.DEFAULT);
    }
    
    @UiHandler("select")
    protected void select(ClickEvent event) {
        int i;
        ArrayList<WorksheetAnalysisViewDO> data;
        Integer[] selections;
        
        
        data = new ArrayList<WorksheetAnalysisViewDO>();
        selections = worksheetAnalysisTable.getSelectedRows();
        for (i = 0; i < selections.length; i++)
            data.add((WorksheetAnalysisViewDO)worksheetAnalysisTable.getRowAt(selections[i]).getData());

        ActionEvent.fire(this, Action.SELECT, data);
        window.close();
    }
    
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
    }
    
    private ArrayList<Row> getTableModel() {
        int i, j;
        ArrayList<Row> model;
        Row row;
        WorksheetAnalysisViewDO waDO;
        WorksheetItemDO wiDO;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.item.count(); i++) {
                wiDO = (WorksheetItemDO)manager.item.get(i);
                
                row = new Row(5);
                row.setCell(0, wiDO.getPosition());
                for (j = 0; j < manager.analysis.count(wiDO); j++) {
                    waDO = manager.analysis.get(wiDO, j);
                    
                    if (j > 0) {
                        row = new Row(5);
                        row.setCell(0, wiDO.getPosition());
                    }
                    row.setCell(1, waDO.getAccessionNumber());
                    if (waDO.getAnalysisId() != null) {
                        row.setCell(2, waDO.getDescription());
                        row.setCell(3, waDO.getTestName());
                        row.setCell(4, waDO.getMethodName());
                    } else if (waDO.getQcLotId() != null) {
                       row.setCell(2, waDO.getDescription());
                    }
                    row.setData(waDO);
                    model.add(row);
                }
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
            Window.alert("error: " + anyE.getMessage());
        }
            
        return model;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void setWorksheetId(Integer worksheetId) {
        try {
            manager = WorksheetService1.get().fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
            worksheetAnalysisTable.setModel(getTableModel());
        } catch (NotFoundException nfE) {
            Window.alert(Messages.get().worksheetAnalysesNotFound(DataBaseUtil.toString(worksheetId)));
        } catch (Exception anyE) {
            anyE.printStackTrace();
            Window.alert("setWorksheetId(): "+anyE.getMessage());
        }
    }
    
    public void setWindow(WindowInt window) {
        super.setWindow(window);
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {                
                worksheetAnalysisTable.setModel(null);
            }
        });
    }
}