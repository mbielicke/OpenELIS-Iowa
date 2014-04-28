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
package org.openelis.modules.worksheetCompletion.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

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
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public abstract class WorksheetEditMultiplePopupUI extends Screen {

    @UiTemplate("WorksheetEditMultiplePopup.ui.xml")
    interface WorksheetAnalysisSelectionUiBinder extends UiBinder<Widget, WorksheetEditMultiplePopupUI> {
    };

    private static WorksheetAnalysisSelectionUiBinder uiBinder = GWT.create(WorksheetAnalysisSelectionUiBinder.class);

    @UiField
    protected Button                                  ok, cancel;
    @UiField
    protected Table                                   analyteResultTable;
    @UiField
    protected Dropdown<Integer>                       analysisStatusId, unitOfMeasureId;

    protected ArrayList<WorksheetAnalysisViewDO>      analyses;
    protected WorksheetManager1                       manager;

    public WorksheetEditMultiplePopupUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void initialize() {
        //
        // worksheet analysis search results table
        //
        addScreenHandler(analyteResultTable, "analyteResultTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                analyteResultTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                analyteResultTable.setEnabled(true);
                analyteResultTable.setAllowMultipleSelection(false);
            }
        });
        
        analyteResultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
//                ArrayList<Item<Integer>> model;
//                ArrayList<FormattedValue> values;
//                DataObject data;
//                HashMap<Integer, Integer> rgRow;
//                Integer rg, testId, unitId;
//                ResultFormatter rf;
//                SectionPermission perm;
//                String accessionNumber, resultKey;
//                TestManager tMan;
//                WorksheetAnalysisViewDO waVDO;
//                
//                if (!isState(UPDATE) || !canEdit || event.getCol() == 1 || event.getCol() == 2 ||
//                    event.getCol() == 3 || event.getCol() == 4 || event.getCol() == 5 ||
//                    event.getCol() == 6 || event.getCol() == 9 || event.getCol() > maxColumn) {
//                    event.cancel();
//                } else if (!getUpdateTransferMode() && (event.getCol() == 0 || event.getCol() == 7 ||
//                                                        event.getCol() == 8 || event.getCol() == 10)) {
//                    event.cancel();
//                } else if (getUpdateTransferMode() && event.getCol() != 0 && event.getCol() != 7 &&
//                                                      event.getCol() != 8 && event.getCol() != 10) {
//                    event.cancel();
//                } else {
//                    data = manager.getObject((String)worksheetItemTable.getRowAt(event.getRow()).getData());
//                    accessionNumber = worksheetItemTable.getValueAt(event.getRow(), 2);
//                    if (data instanceof WorksheetItemDO) {
//                        event.cancel();
//                    } else if ((accessionNumber == null || accessionNumber.length() <= 0) && event.getCol() < 10) {
//                        event.cancel();
//                    } else if (data instanceof WorksheetQcResultViewDO && event.getCol() < 11) {
//                        event.cancel();
//                    } else {
//                        if (data instanceof WorksheetResultViewDO)
//                            waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetResultViewDO)data).getWorksheetAnalysisId()));
//                        else
//                            waVDO = (WorksheetAnalysisViewDO)data;
//                        perm = UserCache.getPermission().getSection(waVDO.getSectionName());
//                        if (waVDO.getQcLotId() != null || waVDO.getFromOtherId() != null ||
//                            Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
//                            Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
//                            event.cancel();
//                        } else if (perm == null || !perm.hasCompletePermission()) {
//                            Window.alert(Messages.get().worksheet_completePermissionRequiredForOperation(waVDO.getSectionName(),
//                                                                                                         Messages.get().edit()));
//                            event.cancel();
//                        } else if (event.getCol() >= 11) {
//                            rgRow = resultGroupMap.get(((WorksheetResultViewDO)data).getTestAnalyteId());
//                            rg = rgRow.get(event.getCol() - 1);
//                            testId = waVDO.getTestId();
//                            unitId = waVDO.getUnitOfMeasureId();
//
//                            resultKey = testId + ":" + rg + ":" + (unitId == null ? 0 : unitId);
//                            model = dictionaryResultMap.get(resultKey);
//                            if (model == null) {
//                                try {
//                                    tMan = testManagers.get(testId);
//                                    rf = tMan.getFormatter();
//                                    /*
//                                     * if all the ranges for this unit in this result
//                                     * group are dictionary values, then create a
//                                     * dropdown model from them
//                                     */
//                                    if (rf.hasAllDictionary(rg, unitId)) {
//                                        values = rf.getDictionaryValues(rg, unitId);
//                                        if (values != null) {
//                                            model = new ArrayList<Item<Integer>>();
//                                            for (FormattedValue v : values)
//                                                model.add(new Item<Integer>(v.getId(), v.getDisplay()));
//                                        }
//                                    }
//                                    dictionaryResultMap.put(resultKey, model);
//                                } catch (Exception e) {
//                                    Window.alert(e.getMessage());
//                                    logger.log(Level.SEVERE, e.getMessage(), e);
//                                    event.cancel();
//                                }
//                            }
//                            ((ResultCell)worksheetItemTable.getColumnAt(event.getCol()).getCellEditor()).setModel(model);
//                        }
//                    }
//                }
            }
        });
        
        analyteResultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
//                int r, c;
//                DataObject data;
//                Integer rowIndex;
//                Object val;
//                Row row;
//                Value value;
//                WorksheetAnalysisViewDO waVDO;
//                WorksheetResultViewDO wrVDO;
//                WorksheetQcResultViewDO wqrVDO;
//
//                r = event.getRow();
//                c = event.getCol();
//                
//                row = worksheetItemTable.getRowAt(r);
//                val = worksheetItemTable.getValueAt(r,c);
//
//                data = manager.getObject((String)row.getData());
//                if (data instanceof WorksheetAnalysisViewDO) {
//                    waVDO = (WorksheetAnalysisViewDO)data;
//                    wrVDO = null;
//                    wqrVDO = null;
//                } else if (data instanceof WorksheetResultViewDO) {
//                    wqrVDO = null;
//                    wrVDO = (WorksheetResultViewDO)data;
//                    waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wrVDO.getWorksheetAnalysisId()));
//                } else if (data instanceof WorksheetQcResultViewDO) {
//                    wrVDO = null;
//                    wqrVDO = (WorksheetQcResultViewDO)data;
//                    waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wqrVDO.getWorksheetAnalysisId()));
//                } else {
//                    return;
//                }
//                
//                switch (c) {
//                    case 0:
//                        if ("Y".equals(val)) {
//                            rowIndex = transferRowMap.get(waVDO.getAnalysisId());
//                            if (rowIndex == null) {
//                                transferRowMap.put(waVDO.getAnalysisId(), r);
//                            } else {
//                                worksheetItemTable.setValueAt(rowIndex, 0, "N");
//                                transferRowMap.put(waVDO.getAnalysisId(), r);
//                            }
//                        } else if ("N".equals(val)) {
//                            transferRowMap.remove(waVDO.getAnalysisId());
//                        }
//                        break;
//                    case 7:
//                        if (val != null)
//                            waVDO.setUnitOfMeasureId(((AutoCompleteValue)val).getId());
//                        else
//                            waVDO.setUnitOfMeasureId(null);
//                        break;
//                    case 8:
//                        waVDO.setStatusId((Integer)val);
//                        break;
//                    case 10:
//                        wrVDO.setIsReportable((String)val);
//                        break;
//                    case 11:
//                    case 12:
//                    case 13:
//                    case 14:
//                    case 15:
//                    case 16:
//                    case 17:
//                    case 18:
//                    case 19:
//                    case 20:
//                    case 21:
//                    case 22:
//                    case 23:
//                    case 24:
//                    case 25:
//                    case 26:
//                    case 27:
//                    case 28:
//                    case 29:
//                    case 30:
//                    case 31:
//                    case 32:
//                    case 33:
//                    case 34:
//                    case 35:
//                    case 36:
//                    case 37:
//                    case 38:
//                    case 39:
//                    case 40:
//                        value = (Value)val;
//                        if (wrVDO != null)
//                            wrVDO.setValueAt(c - 11, value.getDisplay());
//                        else if (wqrVDO != null)
//                            wqrVDO.setValueAt(c - 11, value.getDisplay());
//                        break;
//                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                ok.setEnabled(true);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(true);
            }
        });

        setState(State.DEFAULT);
    }
    
    public void setData(WorksheetManager1 manager, ArrayList<WorksheetAnalysisViewDO> analyses) {
        this.manager = manager;
        this.analyses = analyses;
        setState(UPDATE);
        fireDataChange();
        /*
         * this is done to get rid of any old error messages
         */
        window.clearStatus();
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("ok")
    protected void ok(ClickEvent event) {
        window.close();
        ok();
    }
    
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }
    
    private ArrayList<Row> getTableModel() {
        int i, j;
        ArrayList<Row> model;
        Row row;
        WorksheetItemDO wiDO;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (WorksheetAnalysisViewDO waVDO : analyses) {
//                wiDO = (WorksheetItemDO)manager.item.get(i);
//                
//                row = new Row(5);
//                row.setCell(0, wiDO.getPosition());
//                for (j = 0; j < manager.analysis.count(wiDO); j++) {
//                    waDO = manager.analysis.get(wiDO, j);
//                    
//                    if (j > 0) {
//                        row = new Row(5);
//                        row.setCell(0, wiDO.getPosition());
//                    }
//                    row.setCell(1, waDO.getAccessionNumber());
//                    if (waDO.getAnalysisId() != null) {
//                        row.setCell(2, waDO.getDescription());
//                        row.setCell(3, waDO.getTestName());
//                        row.setCell(4, waDO.getMethodName());
//                    } else if (waDO.getQcLotId() != null) {
//                       row.setCell(2, waDO.getDescription());
//                    }
//                    row.setData(waDO);
//                    model.add(row);
//                }
            }
        } catch (Exception anyE) {
            Window.alert("error: " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }
            
        return model;
    }
}