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
package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.QcManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class WorksheetQcAnalysisSelectionScreen extends Screen implements HasActionHandlers<WorksheetQcAnalysisSelectionScreen.Action> {

    private AppButton          okButton, cancelButton;
    private ScreenService      qcService;
    private TableWidget        worksheetQcAnalysisTable;
    
    protected Integer          worksheetId;
    protected WorksheetManager manager;
    
    public enum Action {
        OK, CANCEL
    };
    
    public WorksheetQcAnalysisSelectionScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetQcAnalysisSelectionDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheet.server.WorksheetService");
        qcService = new ScreenService("OpenELISServlet?service=org.openelis.modules.qc.server.QcService");

        // Setup link between Screen and widget Handlers
        initialize();
        
        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        worksheetQcAnalysisTable = (TableWidget)def.getWidget("worksheetQcAnalysisTable");
        addScreenHandler(worksheetQcAnalysisTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetQcAnalysisTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetQcAnalysisTable.enable(true);
            }
        });
        
        worksheetQcAnalysisTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
           public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
               //do nothing
           }; 
        });
        
        worksheetQcAnalysisTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (okButton.isEnabled())
                    ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (cancelButton.isEnabled())
                    cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });
    }
    
    private void ok() {
        ArrayList<TableDataRow> selections = worksheetQcAnalysisTable.getSelections();
        
        if (selections.size() > 0)
            ActionEvent.fire(this, Action.OK, selections);
        
        window.close();
    }
    
    private void cancel(){
        window.close();
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int                      i, j;
        ArrayList<TableDataRow>  model;
        QcManager                qcManager;
        TableDataRow             row;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null) 
            return model;

        try {
            wiManager = manager.getItems();
            for (i = 0; i < wiManager.count(); i++) {
                wiDO = wiManager.getWorksheetItemAt(i);
                
                try {
                    waManager = wiManager.getWorksheetAnalysisAt(i);
                    for (j = 0; j < waManager.count(); j++) {
                        waDO = waManager.getWorksheetAnalysisAt(j);

                        //
                        // we only want QC records
                        //
                        if (waDO.getQcId() == null)
                            continue;
                        
                        qcManager = qcService.call("fetchById", waDO.getQcId());
                        row = new TableDataRow(waDO.getId(),
                                               wiDO.getPosition(), 
                                               waDO.getAccessionNumber(), 
                                               qcManager.getQc().getName());
                        row.data = waDO;
                        
                        model.add(row);
                    }
                } catch (Exception anyE2) {
                    anyE2.printStackTrace();
                    Window.alert("error: " + anyE2.getMessage());
                    return model;
                }
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
            Window.alert("error: " + anyE.getMessage());
            return model;
        }
            
        return model;
    }
    
    public void draw() {
        try{
            manager = service.call("fetchWithItems", worksheetId);
            
            DataChangeEvent.fire(this);
        }catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
    }
}