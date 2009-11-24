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
package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultDO;
import org.openelis.domain.WorksheetResultDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationScreen extends Screen implements HasActionHandlers<WorksheetCreationScreen.Action> {

    private boolean                          isSaved;
    private Integer                          formatBatch, formatTotal, statusWorking,
                                             typeFixed, typeDup, typeRand, typeLastWell,
                                             typeLastRun, typeLastBoth;
    private ScreenService                    qcService;
    private SecurityModule                   security;
    private WorksheetManager                 manager;
    private WorksheetMetaMap                 meta;

    private AppButton                        saveButton, exitButton,
                                             insertQCButton, removeRowButton;

    protected ArrayList<Integer>             testIds;
    protected ArrayList<TableDataRow>        analysisItems, qcLastRunList, qcLastBothList;
    protected ArrayList<TestWorksheetItemDO> testWorksheetItems;
    protected TableDataRow                   qcItems[];
    protected TableWidget                    worksheetItemTable;
    protected TextBox<Integer>               worksheetId;
    protected TestWorksheetDO                testWorksheetDO;
    protected WorksheetCreationLookupScreen  wcLookupScreen;
    
    public enum Action {
        ITEMS_CHANGED
    };
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service   = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        qcService = new ScreenService("OpenELISServlet?service=org.openelis.modules.qc.server.QcService");

        security = OpenELIS.security.getModule("worksheet");
        if (security == null)
            throw new SecurityException("screenPermException", "Worksheet Creation Screen");

        meta   = new WorksheetMetaMap();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        analysisItems  = new ArrayList<TableDataRow>();
        isSaved        = true;
        manager        = WorksheetManager.getInstance();
        qcLastRunList  = new ArrayList<TableDataRow>();
        qcLastBothList = new ArrayList<TableDataRow>();
        testIds        = new ArrayList<Integer>();

        initialize();

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent event) {
                if (exit())
                    wcLookupScreen.getWindow().close();
                else
                    event.cancel();
            }
        });
        
        setState(State.DEFAULT);
        initializeDropdowns();
        openLookupWindow();

        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        worksheetId = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(false);
            }
        });

        saveButton = (AppButton)def.getWidget("saveButton");
        addScreenHandler(saveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                save();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                saveButton.enable(false);
            }
        });

        exitButton = (AppButton)def.getWidget("exitButton");
        addScreenHandler(exitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                window.close();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                exitButton.enable(true);
            }
        });

        worksheetItemTable = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(worksheetItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetItemTable.enable(false);
            }
        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetItemTable.getSelectedRow() != -1)
                    removeRowButton.enable(true);
                else
                    removeRowButton.enable(false);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });

        worksheetItemTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
            }
        });

        worksheetItemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                // TODO -- Update to include QC Removal
//                analysisItems.remove(event.getIndex());
                mergeAnalysesAndQCs();
            }
        });

        worksheetItemTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
				ColumnComparator comparator;
                
                comparator = new ColumnComparator(event.getIndex(), event.getDirection());
                Collections.sort(analysisItems, comparator);

                mergeAnalysesAndQCs();
            }
        });
        
        insertQCButton = (AppButton)def.getWidget("insertQCButton");
        addScreenHandler(insertQCButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCButton.enable(false);
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = worksheetItemTable.getSelectedRow();
                if (r > -1 && worksheetItemTable.numRows() > 0)
                    worksheetItemTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(false);
            }
        });   
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns(){
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(6).getColumnWidget()).setModel(model);

        try {
//            formatBatch   = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatBatch   = DictionaryCache.getIdFromSystemName("batch");
//            formatTotal   = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
            formatTotal   = DictionaryCache.getIdFromSystemName("total");
            statusWorking = DictionaryCache.getIdFromSystemName("worksheet_working");
            typeFixed = DictionaryCache.getIdFromSystemName("pos_fixed");
            typeDup = DictionaryCache.getIdFromSystemName("pos_duplicate");
            typeRand = DictionaryCache.getIdFromSystemName("pos_random");
            typeLastWell = DictionaryCache.getIdFromSystemName("pos_last_of_well");
            typeLastRun = DictionaryCache.getIdFromSystemName("pos_last_of_run");
            typeLastBoth = DictionaryCache.getIdFromSystemName("pos_last_of_well_&_run");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    protected void openLookupWindow() {
        if (wcLookupScreen == null) {
            try {
                wcLookupScreen = new WorksheetCreationLookupScreen();
                wcLookupScreen.addActionHandler(new ActionHandler<WorksheetCreationLookupScreen.Action>() {
                    public void onAction(ActionEvent<WorksheetCreationLookupScreen.Action> event) {
                        ArrayList<TableDataRow> model;
                        TableDataRow            row, newRow;
                        WorksheetCreationVO     data;

                        if (event.getAction() == WorksheetCreationLookupScreen.Action.ADD) {
                            model = (ArrayList<TableDataRow>)event.getData();
                            if (model != null) {
                                for (int i = 0; i < model.size(); i++) {
                                    row  = model.get(i);
                                    newRow = new TableDataRow(11);
                                    data = (WorksheetCreationVO)row.data;
                                    
                                    if (!testIds.contains(data.getTestId()))
                                        testIds.add(data.getTestId());
                                    
                                    newRow.key = row.key;                                   // analysis id
                                    newRow.cells.get(1).value = row.cells.get(0).value;     // accession #
                                    newRow.cells.get(2).value = row.cells.get(1).value;     // description
                                    newRow.cells.get(4).value = row.cells.get(2).value;     // test name
                                    newRow.cells.get(5).value = row.cells.get(3).value;     // method name
                                    newRow.cells.get(6).value = row.cells.get(5).value;     // status
                                    newRow.cells.get(7).value = row.cells.get(6).value;     // collection date
                                    newRow.cells.get(8).value = row.cells.get(7).value;     // received date and time
                                    newRow.cells.get(9).value = row.cells.get(8).value;     // due days
                                    newRow.cells.get(10).value = row.cells.get(9).value;    // expire date and time
                                    analysisItems.add(newRow);
                                }
                                
                                if (testIds.size() > 1) {
                                    Window.alert(consts.get("multipleTestsOnWorksheet"));
                                    testWorksheetDO = new TestWorksheetDO();
                                    testWorksheetDO.setFormatId(formatTotal);
                                    testWorksheetDO.setBatchCapacity(500);
                                    testWorksheetDO.setTotalCapacity(500);
                                    testWorksheetItems.clear();
                                } else {
                                    loadQCTemplate(testIds.get(0));
                                }
                                mergeAnalysesAndQCs();

                                isSaved = false;
                                saveButton.enable(true);
//                                insertQCButton.enable(true);
                                worksheetItemTable.enable(true);
                            }
                        }
                    }
                });
                
                OpenELIS.browser.addScreen(wcLookupScreen);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }
    }
    
    protected void save() {
        boolean                  choice;
        int                      i, j;
        TableDataRow             row;
        QcAnalyteDO              qcaDO;
        QcAnalyteManager         qcaManager;
        WorksheetDO              wDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        WorksheetQcResultDO      wqrDO;
        WorksheetQcResultManager wqrManager = null;
        WorksheetResultDO        wrDO;
        WorksheetResultManager   wrManager = null;
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert("You may not save an empty worksheet");
            return;
        }
        
        choice = Window.confirm("Worksheets cannot be changed once they have been saved.\n"+
                                "Are you sure you would like to save this worksheet?");
        if (!choice)
            return;

        window.setBusy(consts.get("saving"));

        wDO = manager.getWorksheet();
        wDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wDO.setSystemUserId(OpenELIS.security.getSystemUserId());
        wDO.setStatusId(statusWorking);
        wDO.setFormatId(testWorksheetDO.getFormatId());
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
            wiDO = new WorksheetItemDO();
            wiDO.setPosition((Integer)row.cells.get(0).value);
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            if (row.data instanceof TestWorksheetItemDO)
                waDO.setReferenceTableId(ReferenceTable.QC);
            else
                waDO.setReferenceTableId(ReferenceTable.ANALYSIS);
            waDO.setReferenceId((Integer)row.key);
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
            } catch (Exception anyE) {
                // TODO -- Need to code real exception handling
                anyE.printStackTrace();
            }

            if (row.data instanceof TestWorksheetItemDO) {
                try {
                    qcaManager = qcService.call("fetchAnalytesByQcId", (Integer)row.key);
                    wqrManager = waManager.getWorksheetQcResultAt(0);
                    for (j = 0; j < qcaManager.count(); j++) {
                        qcaDO = qcaManager.getAnalyteAt(j);

                        wqrDO = new WorksheetQcResultDO();
                        wqrDO.setSortOrder(j+1);
                        wqrDO.setQcAnalyteId(qcaDO.getId());
                        wqrDO.setTypeId(qcaDO.getTypeId());
                        
                        wqrManager.addWorksheetQcResult(wqrDO);
                    }
                } catch (Exception anyE) {
                    // TODO Auto-generated catch block
                    anyE.printStackTrace();
                }
            } else {
                // TODO -- Need to add worksheet_result records
/*
                try {
                    qcaManager = qcService.call("fetchAnalytesByQcId", (Integer)row.key);
                    wrManager = waManager.getWorksheetResultAt(0);
                    for (j = 0; j < qcaManager.count(); j++) {
                        aDO = aManager.getAnalyteAt(j);

                        wrDO = new WorksheetResultDO();
                        wrDO.setSortOrder(aDO.getSortOrder());
                        wrDO.setAnalyteId(aDO.getId());
                        wrDO.setTypeId(aDO.getTypeId());
                        
                        wrManager.addResult(wrDO);
                    }
                } catch (Exception anyE) {
                    // TODO Auto-generated catch block
                    anyE.printStackTrace();
                }
*/
            }
        }
        
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("savingComplete"));
            
            isSaved = true;
            saveButton.enable(false);
            insertQCButton.enable(false);
            removeRowButton.enable(false);
            worksheetItemTable.enable(false);
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }
    }

    protected boolean exit() {
        boolean choice;
        
        choice = true;
        if (!isSaved)
            choice = Window.confirm("This worksheet has not been saved.\n"+
                                    "Are you sure you would like to exit without saving?");
        return choice;
    }

    private void loadQCTemplate(Integer testId) {
        int                  i;
        TestWorksheetManager twM;
        
        try {
            twM = TestWorksheetManager.fetchByTestId(testId);
            testWorksheetDO = twM.getWorksheet();
            if (testWorksheetItems == null)
                testWorksheetItems = new ArrayList<TestWorksheetItemDO>();
            for (i = 0; i < twM.itemCount(); i++)
                testWorksheetItems.add(twM.getItemAt(i));
        } catch (Exception anyE) {
            // TODO -- Need to code real exception handling
            anyE.printStackTrace();
        }
        
        buildQCWorksheet();
    }
    
    private void buildQCWorksheet() {
        int                     i, j, posNum, randSize, numBatches, startIndex;
        Integer                 qcId;
        String                  qcName;
        ArrayList<QcDO>         list;
        ArrayList<TableDataRow> qcRandList, qcLastWellList, lastOf;
        TableDataRow            qcRow, qcRow1;
        TestWorksheetItemDO     twiDO;

        //
        // initialize/clear the qcItems
        //
        if (qcItems == null) {
            qcItems = new TableDataRow[testWorksheetDO.getTotalCapacity()];
        } else {
            for (i = 0; i < qcItems.length; i++)
                qcItems[i] = null;
        }
        
        qcRandList     = new ArrayList<TableDataRow>();
        qcLastWellList = new ArrayList<TableDataRow>();
        qcLastRunList.clear();
        qcLastBothList.clear();
        numBatches = testWorksheetDO.getTotalCapacity() / testWorksheetDO.getBatchCapacity();
        
        //
        // Insert Fixed/Duplicate QCItems into worksheet per batch and store 
        // Random and LastOf QC Items for later.
        //
        for (i = 0; i < testWorksheetItems.size(); i++) {
            qcId   = null;
            qcName = null;
            twiDO  = (TestWorksheetItemDO) testWorksheetItems.get(i);
            
            try {
                list = qcService.callList("fetchActiveByName", twiDO.getQcName());
                if (list.size() == 0) {
                    Window.alert("No active QC matching '"+twiDO.getQcName()+"' at row "+(i+1));
                    qcName = "MISSING: "+twiDO.getQcName();
                } else if (list.size() > 1) {
                    // TODO -- Need to code a chooser for multiple active QCs matching name
                    Window.alert("More than one active QC was found matching '"+twiDO.getQcName()+"' at row "+(i+1));
                } else {
                    qcId = list.get(0).getId();
                    qcName = list.get(0).getName();
                }
            } catch (Exception anyE) {
                // TODO Auto-generated catch block
                anyE.printStackTrace();
            }
            
            qcRow = new TableDataRow(11);
            qcRow.key = qcId;                                      // qc id
            qcRow.cells.get(2).value = qcName;                     // description
            qcRow.data = twiDO;
            
            
            if (typeFixed.equals(twiDO.getTypeId()) || typeDup.equals(twiDO.getTypeId()))
                for (j = 0; j < numBatches; j++) {
                    posNum = j * testWorksheetDO.getBatchCapacity() + twiDO.getPosition() - 1;
                    qcRow.cells.get(1).value = "X."+getPositionNumber(posNum);     // qc accession #
                    qcItems[posNum] = qcRow;
                }
            else if (typeRand.equals(twiDO.getTypeId()))
                qcRandList.add(qcRow);
            else if (typeLastWell.equals(twiDO.getTypeId()))
                qcLastWellList.add(qcRow);
            else if (typeLastRun.equals(twiDO.getTypeId()))
                qcLastRunList.add(qcRow);
            else if (typeLastBoth.equals(twiDO.getTypeId()))
                qcLastBothList.add(qcRow);
        }

        //
        // Insert Last of Well/Both QCItems into the worksheet per batch
        //
        if (! qcLastWellList.isEmpty())
            lastOf = qcLastWellList;
        else
            lastOf = qcLastBothList;

        startIndex = testWorksheetDO.getBatchCapacity() - lastOf.size();
        for (i = 0; i < numBatches; i++) {
            posNum = i * testWorksheetDO.getBatchCapacity() + startIndex;
            for (j = 0; j < lastOf.size(); j++) {
                qcRow = lastOf.get(j);
                qcRow.cells.get(1).value = "X."+getPositionNumber(posNum);     // qc accession #
                qcItems[posNum++] = qcRow;
            }
        }

        //
        // Insert random QCItems into the worksheet per batch
        //
        randSize = qcRandList.size();
        for (i = 0; i < numBatches; i++) {
            j = 0;
            while (j < randSize) {
                qcRow = qcRandList.get(j);
                posNum = (int) (Math.random() * (testWorksheetDO.getBatchCapacity() - 1)) + i * testWorksheetDO.getBatchCapacity();
                if (qcItems[posNum] == null) {
                    if (posNum + 1 < testWorksheetDO.getTotalCapacity()) {
                        qcRow1 = qcItems[posNum+1];
                        if (qcRow1 != null && typeDup.equals(((TestWorksheetItemDO)qcRow1.data).getTypeId()))
                            continue;
                    }
                    qcRow.cells.get(1).value = "X."+getPositionNumber(posNum);     // qc accession #
                    qcItems[posNum] = qcRow;
                    j++;
                }
            }
        }
    }
    
    private void mergeAnalysesAndQCs() {
        int                     i, j, k;
        ArrayList<TableDataRow> items, lastOf;
        TableDataRow            row;
        
        //
        // insert end of run QCs into lastOf list
        //
        if (!qcLastRunList.isEmpty())
            lastOf = qcLastRunList;
        else
            lastOf = qcLastBothList;
            
        //
        // Insert analyses, duplicates and fixed QCs into worksheet
        //
        j = 0;
        items = new ArrayList<TableDataRow>();
        for (i = 0; i < testWorksheetDO.getTotalCapacity() - lastOf.size(); i++) {
            row = qcItems[i];
            if (row == null) {
                if (j >= analysisItems.size())
                    break;

                row = analysisItems.get(j);
                row.cells.get(0).value = getPositionNumber(i);
                items.add(analysisItems.get(j));
                j++;
            } else if (typeDup.equals(((TestWorksheetItemDO)row.data).getTypeId()) && j > 0) {
                // TODO -- Add a duplicate of the previous analysis
            } else {
                row.cells.get(0).value = getPositionNumber(i);
                items.add(row);
            }
        }
        
        //
        // Append Last of Run QCItems
        //
        if (i < testWorksheetDO.getTotalCapacity()) {
            for (k = 0; k < lastOf.size() && i < testWorksheetDO.getTotalCapacity(); k++) {
                row = lastOf.get(k);
                row.cells.get(0).value = getPositionNumber(i);
                row.cells.get(1).value = "X."+getPositionNumber(i);     // qc accession #
                items.add(row);
                i++;
            }
        }

        //
        // Correct i for the case where we incremented it before breaking out
        // due to running out of analyses and didn't later add lastOf QCs
        //
        if (j >= analysisItems.size() && lastOf.size() <= 0)
            i--;

        //
        // If last batch contains only QC items, remove it
        //
        for (i--; i > -1 && items.get(i).data instanceof TestWorksheetItemDO; i--) {
            if (i % testWorksheetDO.getTotalCapacity() == 0) {
                while (i < items.size())
                    items.remove(i);
            }
        }

        if (j < analysisItems.size())
            Window.alert("Worksheet is full, cannot add more Analyses");

        worksheetItemTable.load(items);
    }

    public HandlerRegistration addActionHandler(ActionHandler<WorksheetCreationScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }        

    private Object getPositionNumber(int position) {
        int    major, minor;
        Object positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(testWorksheetDO.getFormatId())) {
            major = getPositionMajorNumber(position+1);
            minor = getPositionMinorNumber(position+1);
            positionNumber = major+"-"+minor;
        } else if (formatTotal.equals(testWorksheetDO.getFormatId())) {
            positionNumber = position + 1;
        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
   private int getPositionMajorNumber(int position) {
       return (int) (position / (double)testWorksheetDO.getBatchCapacity() + .99);
   }

   /**
     * Parses the position number and returns the minor number
     * for batch numbering.
     */
   private int getPositionMinorNumber(int position) {
       return position - (getPositionMajorNumber(position) - 1) * testWorksheetDO.getBatchCapacity();
   }
}
