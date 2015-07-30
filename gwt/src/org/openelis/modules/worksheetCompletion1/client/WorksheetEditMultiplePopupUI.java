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
package org.openelis.modules.worksheetCompletion1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.modules.worksheetCompletion1.client.WorksheetResultCell;
import org.openelis.modules.worksheetCompletion1.client.WorksheetResultCell.Value;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;

public abstract class WorksheetEditMultiplePopupUI extends Screen {

    @UiTemplate("WorksheetEditMultiplePopup.ui.xml")
    interface WorksheetEditMultiplePopupUiBinder extends UiBinder<Widget, WorksheetEditMultiplePopupUI> {
    };

    private static WorksheetEditMultiplePopupUiBinder              uiBinder = GWT.create(WorksheetEditMultiplePopupUiBinder.class);

    @UiField
    protected Button                                               ok, cancel;
    @UiField
    protected CheckBox                                             ifEmpty;
    @UiField
    protected Table                                                analyteResultTable;

    protected ArrayList<WorksheetAnalysisViewDO>                   analyses;
    protected HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> qcResultsByQcAnalyteId;
    protected HashMap<Integer, ArrayList<WorksheetResultViewDO>>   resultsByTestAnalyteId;
    protected HashMap<Integer, HashMap<Integer, Integer>>          resultGroupMap;
    protected HashMap<String, ArrayList<Item<String>>>             dictionaryResultMap;
    protected Integer                                              testId, unitId;
    protected ResultFormatter                                      resultFormatter;
    protected WorksheetManager1                                    manager;

    public WorksheetEditMultiplePopupUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void initialize() {
        ifEmpty.setEnabled(true);
        ok.setEnabled(true);
        cancel.setEnabled(true);
        analyteResultTable.setEnabled(true);
        analyteResultTable.setAllowMultipleSelection(false);

        addScreenHandler(ifEmpty, "ifEmpty", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ifEmpty.setValue("Y");
            }
        });
        
        addScreenHandler(analyteResultTable, "analyteResultTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                analyteResultTable.setModel(getTableModel());
            }
        });
        
        analyteResultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                ArrayList<Item<String>> model;
                ArrayList<FormattedValue> values;
                HashMap<Integer, Integer> rgRow;
                Integer rg;
                Row row;
                String data, resultKey;
                
                if (event.getCol() == 0) {
                    event.cancel();
                } else {
                    row = analyteResultTable.getRowAt(event.getRow());
                    data = row.getData();
                    if (data.startsWith("R")) {
                        rgRow = resultGroupMap.get(Integer.valueOf(data.substring(1)));
                        rg = rgRow.get(event.getCol() - 1);
    
                        resultKey = testId + ":" + rg + ":" + (unitId == null ? 0 : unitId);
                        model = dictionaryResultMap.get(resultKey);
                        if (model == null) {
                            /*
                             * if all the ranges for this unit in this result
                             * group are dictionary values, then create a
                             * dropdown model from them
                             */
                            if (resultFormatter.hasAllDictionary(rg, unitId)) {
                                values = resultFormatter.getDictionaryValues(rg, unitId);
                                if (values != null) {
                                    model = new ArrayList<Item<String>>();
                                    for (FormattedValue v : values)
                                        model.add(new Item<String>(v.getDisplay(), v.getDisplay()));
                                }
                            }
                            dictionaryResultMap.put(resultKey, model);
                        }
                        ((WorksheetResultCell)analyteResultTable.getColumnAt(event.getCol()).getCellEditor()).setModel(model);
                    } else {
                        ((WorksheetResultCell)analyteResultTable.getColumnAt(event.getCol()).getCellEditor()).setModel(null);
                    }
                }
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
    
    public void setData(WorksheetManager1 manager, Integer testId, Integer unitId,
                        ResultFormatter resultFormatter, ArrayList<WorksheetAnalysisViewDO> analyses,
                        HashMap<Integer, HashMap<Integer, Integer>> resultGroupMap,
                        HashMap<String, ArrayList<Item<String>>> dictionaryResultMap) {
        this.manager = manager;
        this.testId = testId;
        this.unitId = unitId;
        this.resultFormatter = resultFormatter;
        this.analyses = analyses;
        this.resultGroupMap = resultGroupMap;
        this.dictionaryResultMap = dictionaryResultMap;
        fireDataChange();
        window.clearStatus();
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    @SuppressWarnings("unused")
    @UiHandler("ok")
    protected void ok(ClickEvent event) {
        int i;
        ArrayList<WorksheetQcResultViewDO> wqrVDOs;
        ArrayList<WorksheetResultViewDO> wrVDOs;
        String data;
        Value val;
        
        window.close();
        
        for (Row row : analyteResultTable.getModel()) {
            data = row.getData();
            if (((String)row.getData()).startsWith("R")) {
                wrVDOs = resultsByTestAnalyteId.get(Integer.valueOf(data.substring(1)));
                for (i = 1; i < row.size(); i++) {
                    val = (Value) row.getCell(i);
                    if (val.getDisplay() != null && val.getDisplay().length() > 0) {
                        for (WorksheetResultViewDO wrVDO : wrVDOs) {
                            if (wrVDO.getValueAt(i - 1) == null || "N".equals(ifEmpty.getValue()))
                                wrVDO.setValueAt(i - 1, val.getDisplay());
                        }
                    }
                }
            } else if (((String)row.getData()).startsWith("Q")) {
                wqrVDOs = qcResultsByQcAnalyteId.get(Integer.valueOf(data.substring(1)));
                for (i = 1; i < row.size(); i++) {
                    val = (Value) row.getCell(i);
                    if (val.getDisplay() != null && val.getDisplay().length() > 0) {
                        for (WorksheetQcResultViewDO wqrVDO : wqrVDOs) {
                            if (wqrVDO.getValueAt(i - 1) == null || "N".equals(ifEmpty.getValue()))
                                wqrVDO.setValueAt(i - 1, val.getDisplay());
                        }
                    }
                }
            }
        }
        
        ok();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
    }
    
    private ArrayList<Row> getTableModel() {
        int i, j, rowSize;
        ArrayList<IdNameVO> headers;
        ArrayList<Row> model;
        ArrayList<WorksheetQcResultViewDO> wqrVDOs;
        ArrayList<WorksheetResultViewDO> wrVDOs;
        Column col;
        HashSet<Integer> testAnalytes, qcAnalytes;
        Row row;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        headers = new ArrayList<IdNameVO>();
        try {
            headers = WorksheetService1.get().getHeaderLabelsForScreen(manager);
        } catch (Exception anyE) {
            Window.alert("Error loading headers for format; " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }

        rowSize = Math.min(headers.size(), 30) + 1;
        for (i = 1; i < rowSize; i++) {
            if (i == analyteResultTable.getColumnCount())
                analyteResultTable.addColumn();
            col = analyteResultTable.getColumnAt(i);
            col.setLabel(headers.get(i - 1).getName());
            col.setCellRenderer(new WorksheetResultCell());
            col.setWidth(150);
        }
        while (rowSize < analyteResultTable.getColumnCount())
            analyteResultTable.removeColumnAt(rowSize);
        

        testAnalytes = new HashSet<Integer>();
        qcAnalytes = new HashSet<Integer>();
        qcResultsByQcAnalyteId = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
        resultsByTestAnalyteId = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        try {
            for (WorksheetAnalysisViewDO waVDO : analyses) {
                if (waVDO.getAnalysisId() != null) {
                    for (i = 0; i < manager.result.count(waVDO); i++) {
                        wrVDO = manager.result.get(waVDO, i);
                        if (!testAnalytes.contains(wrVDO.getTestAnalyteId())) {
                            testAnalytes.add(wrVDO.getTestAnalyteId());
                            row = new Row(rowSize);
                            row.setCell(0, wrVDO.getAnalyteName());
                            for (j = 1; j < rowSize; j++)
                                row.setCell(j, new WorksheetResultCell.Value(null, null));
                            row.setData("R"+wrVDO.getTestAnalyteId());
                            model.add(row);
                        }
                        wrVDOs = resultsByTestAnalyteId.get(wrVDO.getTestAnalyteId());
                        if (wrVDOs == null) {
                            wrVDOs = new ArrayList<WorksheetResultViewDO>();
                            resultsByTestAnalyteId.put(wrVDO.getTestAnalyteId(), wrVDOs);
                        }
                        wrVDOs.add(wrVDO);
                    }
                } else if (waVDO.getQcId() != null) {
                    for (i = 0; i < manager.qcResult.count(waVDO); i++) {
                        wqrVDO = manager.qcResult.get(waVDO, i);
                        if (!qcAnalytes.contains(wqrVDO.getQcAnalyteId())) {
                            qcAnalytes.add(wqrVDO.getQcAnalyteId());
                            row = new Row(rowSize);
                            row.setCell(0, "(QC) " + wqrVDO.getAnalyteName());
                            for (j = 1; j < rowSize; j++)
                                row.setCell(j, new WorksheetResultCell.Value(null, null));
                            row.setData("Q"+wqrVDO.getQcAnalyteId());
                            model.add(row);
                        }
                        wqrVDOs = qcResultsByQcAnalyteId.get(wqrVDO.getQcAnalyteId());
                        if (wqrVDOs == null) {
                            wqrVDOs = new ArrayList<WorksheetQcResultViewDO>();
                            qcResultsByQcAnalyteId.put(wqrVDO.getQcAnalyteId(), wqrVDOs);
                        }
                        wqrVDOs.add(wqrVDO);
                    }
                }
            }
        } catch (Exception anyE) {
            Window.alert("error: " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }
            
        return model;
    }
}