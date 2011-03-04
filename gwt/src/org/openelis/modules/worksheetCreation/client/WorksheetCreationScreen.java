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
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Confirm;
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
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.QcManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetQcAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCreationScreen extends Screen {

    private boolean                               isTemplateLoaded, isSaved, wasExitCalled;
    private int                                   tempId, qcStartIndex;
    private Integer                               formatId, formatTotal,
                                                  statusWorking, typeFixed, typeDup,
                                                  typeRand, typeLastWell, typeLastRun,
                                                  typeLastBoth;
    private String                                typeRandString, typeLastWellString,
                                                  typeLastRunString, typeLastBothString;
    private ScreenService                         qcService, worksheetService;
    private ModulePermission                      userPermission;
    private WorksheetManager                      manager;

    private AppButton                             lookupWorksheetButton, saveButton,
                                                  exitButton, insertQCWorksheetButton,
                                                  insertQCLookupButton, removeRowButton;

    protected ArrayList<Integer>                  testIds;
    protected ArrayList<TableDataRow>             analysisItems, qcLastRunList,
                                                  qcLastBothList, qcLinkModel,
                                                  testWorksheetItems;
    protected Confirm                             worksheetRemoveQCConfirm, worksheetRemoveLastOfQCConfirm,
                                                  worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer,Exception>          qcErrors;
    protected QcLookupScreen                      qcLookupScreen;
    protected TableDataRow                        qcItems[];
    protected TableWidget                         worksheetItemTable;
    protected TextBox<Integer>                    worksheetId, relatedWorksheetId;
    protected TestWorksheetDO                     testWorksheetDO;
    protected TestWorksheetManager                twManager;
    protected WorksheetQcAnalysisSelectionScreen  waSelectionScreen;
    protected WorksheetCreationLookupScreen       wcLookupScreen;
    protected WorksheetLookupScreen               wLookupScreen, wQCLookupScreen;
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service          = new ScreenService("controller?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        qcService        = new ScreenService("controller?service=org.openelis.modules.qc.server.QcService");
        worksheetService = new ScreenService("controller?service=org.openelis.modules.worksheet.server.WorksheetService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Worksheet Creation Screen");

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
        analysisItems      = new ArrayList<TableDataRow>();
        isSaved            = true;
        isTemplateLoaded   = false;
        manager            = WorksheetManager.getInstance();
        qcErrors           = new HashMap<Integer,Exception>();
        qcLastRunList      = new ArrayList<TableDataRow>();
        qcLastBothList     = new ArrayList<TableDataRow>();
        qcStartIndex       = 0;
        tempId             = -1;
        testIds            = new ArrayList<Integer>();
        testWorksheetItems = new ArrayList<TableDataRow>();
        wasExitCalled      = false;

        try {
            DictionaryCache.preloadByCategorySystemNames("analysis_status",
                                                         "type_of_sample", 
                                                         "test_worksheet_format",
                                                         "test_worksheet_item_type",
                                                         "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (wasExitCalled) {
                    wcLookupScreen.getWindow().close();
                } else {
                    event.cancel();
                    exit();
                }
            }
        });
        
        setState(State.DEFAULT);
        openLookupWindow();
        initializeDropdowns();
        clearQCTemplate();
        
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        worksheetId = (TextBox)def.getWidget(WorksheetCreationMeta.getWorksheetId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(false);
            }
        });

        relatedWorksheetId = (TextBox)def.getWidget(WorksheetCreationMeta.getWorksheetRelatedWorksheetId());
        addScreenHandler(relatedWorksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setValue(manager.getWorksheet().getRelatedWorksheetId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                relatedWorksheetId.enable(false);
            }
        });

        lookupWorksheetButton = (AppButton)def.getWidget("lookupWorksheetButton");
        addScreenHandler(lookupWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lookupWorksheetButton.enable(true);
            }
        });

        saveButton = (AppButton)def.getWidget("saveButton");
        addScreenHandler(saveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (worksheetSaveConfirm == null) {
                    worksheetSaveConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                       consts.get("worksheetCreationSaveConfirm"),
                                                       "Don't Save", "Save");
                    worksheetSaveConfirm.addSelectionHandler(new SelectionHandler<Integer>(){
                        public void onSelection(SelectionEvent<Integer> event) {
                            switch(event.getSelectedItem().intValue()) {
                                case 1:
                                    save();
                                    break;
                            }
                        }
                    });
                }
                
                worksheetSaveConfirm.show();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                saveButton.enable(false);
            }
        });

        exitButton = (AppButton)def.getWidget("exitButton");
        addScreenHandler(exitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                exit();
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
            }
        });
        
        worksheetItemTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent event) {
                if (worksheetItemTable.getSelectedRow() == -1)
                    removeRowButton.enable(false);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                //  only the QC Link field can be edited
                //
                if (event.getCol() != 3)
                    event.cancel();
            }
        });

        worksheetItemTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
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
        
        insertQCWorksheetButton = (AppButton)def.getWidget("insertQCWorksheetButton");
        addScreenHandler(insertQCWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetQCLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCWorksheetButton.enable(true);
            }
        });

        insertQCLookupButton = (AppButton)def.getWidget("insertQCLookupButton");
        addScreenHandler(insertQCLookupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openQCLookup(null, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCLookupButton.enable(true);
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int                 rowIndex;
                TableDataRow        dataRow;
                
                rowIndex = worksheetItemTable.getSelectedRow();
                if (rowIndex > -1 && worksheetItemTable.numRows() > 0) {
                    dataRow = worksheetItemTable.getRow(rowIndex);
                    if (dataRow.data instanceof ArrayList) {
                        if (worksheetRemoveQCConfirm == null) {
                            worksheetRemoveQCConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                                   consts.get("worksheetRemoveQCConfirm"),
                                                                   "Don't Remove", "Remove");
                            worksheetRemoveQCConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                                public void onSelection(SelectionEvent<Integer> event) {
                                    TableDataRow        qcRow;
                                    TestWorksheetItemDO twiDO;

                                    switch(event.getSelectedItem().intValue()) {
                                        case 1:
                                            qcRow = worksheetItemTable.getSelection();
                                            twiDO = (TestWorksheetItemDO) ((ArrayList)qcRow.data).get(0);
                                            
                                            if (typeLastWell.equals(twiDO.getTypeId()) ||
                                                typeLastRun.equals(twiDO.getTypeId()) ||
                                                typeLastBoth.equals(twiDO.getTypeId())) {
                                                if (worksheetRemoveLastOfQCConfirm == null) {
                                                    worksheetRemoveLastOfQCConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                                                                 consts.get("worksheetRemoveLastOfQCConfirm"),
                                                                                                 "Don't Remove", "Remove");
                                                    worksheetRemoveLastOfQCConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                                                        public void onSelection(SelectionEvent<Integer> event) {
                                                            switch(event.getSelectedItem().intValue()) {
                                                                case 1:
                                                                    testWorksheetItems.remove(worksheetItemTable.getSelection());
                                                                    buildQCWorksheet();
                                                                    mergeAnalysesAndQCs();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                                }
                                                worksheetRemoveLastOfQCConfirm.show();
                                            } else {
                                                testWorksheetItems.remove(qcRow);
                                                buildQCWorksheet();
                                                mergeAnalysesAndQCs();
                                                break;
                                            }
                                    }
                                }
                            });
                        }
                        worksheetRemoveQCConfirm.show();
                    } else if (dataRow.data instanceof WorksheetCreationVO) {
                        analysisItems.remove(dataRow);
                        mergeAnalysesAndQCs();
                    }
                }
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

        try {
            formatTotal = DictionaryCache.getIdFromSystemName("wformat_total");
            statusWorking = DictionaryCache.getIdFromSystemName("worksheet_working");
            typeFixed = DictionaryCache.getIdFromSystemName("pos_fixed");
            typeDup = DictionaryCache.getIdFromSystemName("pos_duplicate");
            typeRand = DictionaryCache.getIdFromSystemName("pos_random");
            typeLastWell = DictionaryCache.getIdFromSystemName("pos_last_of_well");
            typeLastRun = DictionaryCache.getIdFromSystemName("pos_last_of_run");
            typeLastBoth = DictionaryCache.getIdFromSystemName("pos_last_of_well_&_run");
            typeRandString = DictionaryCache.getEntryFromId(typeRand).getEntry();
            typeLastWellString = DictionaryCache.getEntryFromId(typeLastWell).getEntry();
            typeLastRunString = DictionaryCache.getEntryFromId(typeLastRun).getEntry();
            typeLastBothString = DictionaryCache.getEntryFromId(typeLastBoth).getEntry();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            wcLookupScreen.getWindow().close();
            window.close();
        }

        //
        // load empty QC Link dropdown model
        //
        qcLinkModel = new ArrayList<TableDataRow>();
        qcLinkModel.add(new TableDataRow(null, ""));
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(3).getColumnWidget()).setModel(qcLinkModel);

        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(6).getColumnWidget()).setModel(model);
}
    
    protected void openLookupWindow() {
        if (wcLookupScreen == null) {
            try {
                wcLookupScreen = new WorksheetCreationLookupScreen();
                wcLookupScreen.addActionHandler(new ActionHandler<WorksheetCreationLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetCreationLookupScreen.Action> event) {
                        Integer                 testId;
                        ArrayList<TableDataRow> list;
                        StringBuffer            message;
                        TableDataRow            row, newRow;
                        SectionViewDO           sectionVDO;
                        WorksheetCreationVO     data;

                        if (event.getAction() == WorksheetCreationLookupScreen.Action.ADD) {
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null && list.size() > 0) {
                                message = new StringBuffer();
                                for (int i = 0; i < list.size(); i++) {
                                    row  = list.get(i);
                                    newRow = new TableDataRow(11);
                                    data = (WorksheetCreationVO)row.data;
                                    
                                    if (formatId == null) {
                                        formatId = data.getWorksheetFormatId();
                                    } else if (!formatId.equals(data.getWorksheetFormatId())) {
                                        message.append(consts.get("accessionNum")).append(data.getAccessionNumber())
                                               .append("\t").append(data.getTestName().trim()).append(", ")
                                               .append(data.getMethodName().trim());
                                        try {
                                            sectionVDO = SectionCache.getSectionFromId(data.getSectionId());
                                            message.append("\t\t").append(sectionVDO.getName().trim());
                                        } catch (Exception anyE) {
                                            anyE.printStackTrace();
                                            message.append("\t\t").append("ERROR");
                                        }
                                        message.append("\n");
                                        continue;
                                    }
                                    
                                    testId = data.getTestId();
                                    if (!testIds.contains(testId))
                                        testIds.add(testId);

                                    newRow.key = getNextTempId();                           // fake worksheet analysis id
                                    newRow.cells.get(1).value = row.cells.get(0).value;     // accession #
                                    newRow.cells.get(2).value = row.cells.get(1).value;     // description
                                    newRow.cells.get(4).value = row.cells.get(2).value;     // test name
                                    newRow.cells.get(5).value = row.cells.get(3).value;     // method name
                                    newRow.cells.get(6).value = row.cells.get(5).value;     // status
                                    newRow.cells.get(7).value = row.cells.get(6).value;     // collection date
                                    newRow.cells.get(8).value = row.cells.get(7).value;     // received date and time
                                    newRow.cells.get(9).value = row.cells.get(8).value;     // due days
                                    newRow.cells.get(10).value = row.cells.get(9).value;    // expire date and time
                                    newRow.data = data;
                                    analysisItems.add(newRow);
                                }
                                
                                if (message.length() > 0)
                                    Window.alert(consts.get("worksheetItemsFormatConflict")+":\n\n"+message.toString());

                                if (testIds.size() > 1) {
                                    Window.alert(consts.get("multipleTestsOnWorksheet"));
                                    clearQCTemplate();
                                } else if (!isTemplateLoaded) {
                                    loadQCTemplate();
                                }
                                mergeAnalysesAndQCs();

                                isSaved = false;
                                saveButton.enable(true);
                                worksheetItemTable.enable(true);
                            }
                        }
                    }
                });
                
                OpenELIS.getBrowser().addScreen(wcLookupScreen);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void save() {
        int                      i, j;
        Object                   position;
        TableDataRow             row;
        WorksheetDO              wDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        WorksheetQcResultManager wqrManager, newWqrManager;
        WorksheetQcResultViewDO  wqrVDO, newWqrVDO;
        
        setFocus(null);
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert(consts.get("worksheetNotSaveEmpty"));
            return;
        }
        
        window.setBusy(consts.get("saving"));

        wDO = manager.getWorksheet();
        wDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wDO.setSystemUserId(OpenELIS.getSystemUserPermission().getSystemUserId());
        wDO.setStatusId(statusWorking);
        wDO.setFormatId(testWorksheetDO.getFormatId());
//        if (formatBatch.equals(wDO.getFormatId()))
            wDO.setBatchCapacity(testWorksheetDO.getBatchCapacity());
        if (relatedWorksheetId.getFieldValue() != null)
            wDO.setRelatedWorksheetId(relatedWorksheetId.getFieldValue());
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
            wiDO = new WorksheetItemDO();
            position = row.cells.get(0).value;
//            if (formatBatch.equals(wDO.getFormatId()))
//                wiDO.setPosition(parseBatchPosition(position, testWorksheetDO.getBatchCapacity()));
//            else
                wiDO.setPosition(Integer.valueOf((String)position));
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setId((Integer)row.key);
            waDO.setAccessionNumber(row.cells.get(1).value.toString());
            if (row.data instanceof ArrayList) {
                waDO.setQcId(((QcDO)((ArrayList<Object>)row.data).get(1)).getId());
            } else {
                waDO.setAnalysisId(((WorksheetCreationVO)row.data).getAnalysisId());
            }
            if (row.cells.get(3).getValue() != null) {
                if (row.cells.get(3).getValue() instanceof ArrayList)
                    waDO.setWorksheetAnalysisId((Integer)((ArrayList<Object>)row.cells.get(3).getValue()).get(0));
            }
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
                //
                // If this analysis is a QC from another worksheet, copy the result
                // records from the manager in the 3rd slot of the ArrayList
                //
                if (row.data instanceof ArrayList && ((ArrayList<Object>)row.data).size() == 3) {
                    wqrManager = (WorksheetQcResultManager) ((ArrayList<Object>)row.data).get(2);
                    newWqrManager = waManager.getWorksheetQcResultAt(waManager.count() - 1);
                    for (j = 0; j < wqrManager.count(); j++) {
                        wqrVDO = wqrManager.getWorksheetQcResultAt(j);
                        newWqrVDO = new WorksheetQcResultViewDO();
                        newWqrVDO.setSortOrder(wqrVDO.getSortOrder());
                        newWqrVDO.setQcAnalyteId(wqrVDO.getQcAnalyteId());
                        newWqrVDO.setAnalyteName(wqrVDO.getAnalyteName());
                        newWqrVDO.setTypeId(wqrVDO.getTypeId());
                        newWqrVDO.setValueAt(0, wqrVDO.getValueAt(0));
                        newWqrVDO.setValueAt(1, wqrVDO.getValueAt(1));
                        newWqrVDO.setValueAt(2, wqrVDO.getValueAt(2));
                        newWqrVDO.setValueAt(3, wqrVDO.getValueAt(3));
                        newWqrVDO.setValueAt(4, wqrVDO.getValueAt(4));
                        newWqrVDO.setValueAt(5, wqrVDO.getValueAt(5));
                        newWqrVDO.setValueAt(6, wqrVDO.getValueAt(6));
                        newWqrVDO.setValueAt(7, wqrVDO.getValueAt(7));
                        newWqrVDO.setValueAt(8, wqrVDO.getValueAt(8));
                        newWqrVDO.setValueAt(9, wqrVDO.getValueAt(9));
                        newWqrVDO.setValueAt(10, wqrVDO.getValueAt(10));
                        newWqrVDO.setValueAt(11, wqrVDO.getValueAt(11));
                        newWqrVDO.setValueAt(12, wqrVDO.getValueAt(12));
                        newWqrVDO.setValueAt(13, wqrVDO.getValueAt(13));
                        newWqrVDO.setValueAt(14, wqrVDO.getValueAt(14));
                        newWqrVDO.setValueAt(15, wqrVDO.getValueAt(15));
                        newWqrVDO.setValueAt(16, wqrVDO.getValueAt(16));
                        newWqrVDO.setValueAt(17, wqrVDO.getValueAt(17));
                        newWqrVDO.setValueAt(18, wqrVDO.getValueAt(18));
                        newWqrVDO.setValueAt(19, wqrVDO.getValueAt(19));
                        newWqrVDO.setValueAt(20, wqrVDO.getValueAt(20));
                        newWqrVDO.setValueAt(21, wqrVDO.getValueAt(21));
                        newWqrVDO.setValueAt(22, wqrVDO.getValueAt(22));
                        newWqrVDO.setValueAt(23, wqrVDO.getValueAt(23));
                        newWqrVDO.setValueAt(24, wqrVDO.getValueAt(24));
                        newWqrVDO.setValueAt(25, wqrVDO.getValueAt(25));
                        newWqrVDO.setValueAt(26, wqrVDO.getValueAt(26));
                        newWqrVDO.setValueAt(27, wqrVDO.getValueAt(27));
                        newWqrVDO.setValueAt(28, wqrVDO.getValueAt(28));
                        newWqrVDO.setValueAt(29, wqrVDO.getValueAt(29));
                        newWqrManager.addWorksheetQcResult(newWqrVDO);
                    }
                }
            } catch (Exception anyE) {
                Window.alert("save(): " + anyE.getMessage());
                window.clearStatus();
            }
        }
        
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("savingComplete"));
            
            isSaved = true;
            saveButton.enable(false);
            insertQCWorksheetButton.enable(false);
            insertQCLookupButton.enable(false);
            lookupWorksheetButton.enable(false);
            removeRowButton.enable(false);
            worksheetItemTable.enable(false);
        } catch (ValidationErrorsList e) {
            showErrors(e);
            manager = WorksheetManager.getInstance();
        } catch (Exception e) {
            Window.alert("save(): " + e.getMessage());
            window.clearStatus();
            manager = WorksheetManager.getInstance();
        }
    }

    protected void exit() {
        if (!isSaved) {
            if (worksheetExitConfirm == null) {
                worksheetExitConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                   consts.get("worksheetExitConfirm"),
                                                   "Don't Exit", "Exit");
                worksheetExitConfirm.addSelectionHandler(new SelectionHandler<Integer>(){
                    public void onSelection(SelectionEvent<Integer> event) {
                        switch(event.getSelectedItem().intValue()) {
                            case 1:
                                wasExitCalled = true;
                                window.close();
                                break;
                        }
                    }
                });
            }
            
            worksheetExitConfirm.show();
        } else {
            wasExitCalled = true;
            window.close();
        }
    }

    private void loadQCTemplate() {
        int                 i, j;
        ArrayList<Object>   dataList;
        ArrayList<QcDO>     list;
        QcDO                qcDO = null;
        TableDataRow        qcRow;
        TestWorksheetItemDO twiDO;
        
        try {
            if (twManager == null) {
                twManager = TestWorksheetManager.fetchByTestId(testIds.get(0));
                //
                // If there is no worksheet definition, an empty manager is returned
                //
                testWorksheetDO = twManager.getWorksheet();
                if (testWorksheetDO.getId() == null) {
                    //
                    // If there is no worksheet definition for the test, load
                    // default definition and clear items list
                    //
                    clearQCTemplate();
                    return;
                }                
            }

            //
            // Only clear the error list if this is our first time through
            //
            if (qcStartIndex == 0)
                qcErrors = new HashMap<Integer, Exception>(testWorksheetDO.getTotalCapacity());
            
            i = qcStartIndex;
            qcStartIndex = 0;
            for (; i < twManager.itemCount(); i++) {
                twiDO = twManager.getItemAt(i);
                try {
                    if (!typeDup.equals(twiDO.getTypeId())) {
                        list = qcService.callList("fetchActiveByName", twiDO.getQcName());
                        if (list.size() == 0) {
                            if (typeRand.equals(twiDO.getTypeId())) {
                                qcErrors.put(-1, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeRandString));
                            } else if (typeLastRun.equals(twiDO.getTypeId())) {
                                qcErrors.put(-2, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastRunString));
                            } else if (typeLastWell.equals(twiDO.getTypeId())) {
                                qcErrors.put(-3, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastWellString));
                            } else if (typeLastBoth.equals(twiDO.getTypeId())) {
                                qcErrors.put(-4, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastBothString));
                            } else {
                                for (j = twiDO.getPosition(); j < testWorksheetDO.getTotalCapacity(); j += testWorksheetDO.getBatchCapacity())
                                    qcErrors.put(j, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), String.valueOf(j)));
                            }
                            continue;
                        } else if (list.size() > 1) {
                            Window.alert(new FormErrorException("multiMatchingActiveQc", twiDO.getQcName(), String.valueOf(i+1)).getMessage());
                            openQCLookup(twiDO.getQcName(), list);
                            qcStartIndex = i + 1;
                            break;
                        } else {
                            qcDO = list.get(0);
                        }
                    } else {
                        qcDO = new QcDO();
                        qcDO.setName("Duplicate");
                    }

                    qcRow = new TableDataRow(11);
                    qcRow.cells.get(2).value = qcDO.getName();             // description
                    
                    dataList = new ArrayList<Object>();
                    dataList.add(twiDO);
                    dataList.add(qcDO);
                    qcRow.data = dataList;
                    
                    testWorksheetItems.add(qcRow);
                } catch (Exception anyE) {
                    Window.alert("loadQCTemplate(): " + anyE.getMessage());
                    window.clearStatus();
                }
            }
        } catch (Exception anyE) {
            Window.alert("loadQCTemplate(): " + anyE.getMessage());
            window.clearStatus();
        }
        
        buildQCWorksheet();
        isTemplateLoaded = true;
    }
    
    @SuppressWarnings("unchecked")
    private void buildQCWorksheet() {
        int                      i, j, posNum, randSize, numBatches, startIndex;
        String                   accessionNumber;
        ArrayList<TableDataRow>  qcRandList, qcLastWellList, lastOf;
        TableDataRow             qcRow, qcRow1;
        TestWorksheetItemDO      twiDO;

        //
        // initialize/clear the qcItems and qcErrors
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
            qcRow = testWorksheetItems.get(i);
            twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)testWorksheetItems.get(i).data).get(0);
            
            qcRow.key = getNextTempId();                           // fake worksheet analysis id
            
            if (typeFixed.equals(twiDO.getTypeId()) || typeDup.equals(twiDO.getTypeId())) {
                for (j = 0; j < numBatches; j++) {
                    posNum = j * testWorksheetDO.getBatchCapacity() + twiDO.getPosition() - 1;
                    
                    //
                    // Do NOT overwrite accession numbers on QCs pulled from other
                    // worksheets
                    //
                    accessionNumber = (String) qcRow.cells.get(1).value;
                    if (accessionNumber == null || accessionNumber.startsWith("X."))
                        qcRow.cells.get(1).value = "X."+getPositionNumber(posNum);     // qc accession #
                    qcItems[posNum] = qcRow;
                }
            } else if (typeRand.equals(twiDO.getTypeId())) {
                qcRandList.add(qcRow);
            } else if (typeLastWell.equals(twiDO.getTypeId())) {
                qcLastWellList.add(qcRow);
            } else if (typeLastRun.equals(twiDO.getTypeId())) {
                qcLastRunList.add(qcRow);
            } else if (typeLastBoth.equals(twiDO.getTypeId())) {
                qcLastBothList.add(qcRow);
            }
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
                qcRow = (TableDataRow) lastOf.get(j).clone();
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
                qcRow = (TableDataRow) qcRandList.get(j).clone();
                posNum = (int) (Math.random() * (testWorksheetDO.getBatchCapacity() - 1)) + i * testWorksheetDO.getBatchCapacity();
                if (qcItems[posNum] == null) {
                    if (posNum + 1 < testWorksheetDO.getTotalCapacity()) {
                        qcRow1 = qcItems[posNum+1];
                        if (qcRow1 != null && typeDup.equals(((TestWorksheetItemDO)((ArrayList<Object>)qcRow1.data).get(0)).getTypeId()))
                            continue;
                    }
                    qcRow.cells.get(1).value = "X."+getPositionNumber(posNum);     // qc accession #
                    qcItems[posNum] = qcRow;
                    j++;
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void mergeAnalysesAndQCs() {
        int                     i, j, k;
        ArrayList<TableDataRow> items, lastOf;
        Exception               tempE;
        String                  message;
        TableDataRow            row, dupedRow;
        TestWorksheetItemDO     twiDO;
        
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
                j++;
            } else {
                twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)row.data).get(0);
                if (typeDup.equals(twiDO.getTypeId())) {
                    //
                    // copy previous analysis row and set it as the qc link for
                    // this row
                    //
                    dupedRow = analysisItems.get(j-1); 
                    row = new TableDataRow(11);
                    row.key = getNextTempId();                                  // fake worksheet analysis id
                    row.cells.get(1).value = "D"+dupedRow.cells.get(1).value;   // accession #
                    row.cells.get(2).value = "Duplicate of "+
                                             dupedRow.cells.get(1).value;       // description
                    row.cells.get(3).value = dupedRow.key;                      // qc link
                    row.cells.get(4).value = dupedRow.cells.get(4).value;       // test name
                    row.cells.get(5).value = dupedRow.cells.get(5).value;       // method name
                    row.cells.get(6).value = dupedRow.cells.get(6).value;       // status
                    row.cells.get(7).value = dupedRow.cells.get(7).value;       // collection date
                    row.cells.get(8).value = dupedRow.cells.get(8).value;       // received date and time
                    row.cells.get(9).value = dupedRow.cells.get(9).value;       // due days
                    row.cells.get(10).value = dupedRow.cells.get(10).value;     // expire date and time
                    row.data = dupedRow.data;
                }
            }
            row.cells.get(0).value = getPositionNumber(i);
            items.add(row);
        }
        
        //
        // Append Last of Run QCItems
        //
        for (k = 0; k < lastOf.size() && i < testWorksheetDO.getTotalCapacity(); k++) {
            row = lastOf.get(k);
            row.cells.get(0).value = getPositionNumber(i);
            row.cells.get(1).value = "X."+getPositionNumber(i);     // qc accession #
            items.add(row);
            i++;
        }

        //
        // If last batch contains only QC items and this is not a QC only worksheet,
        // remove it
        //
        if (analysisItems.size() > 0) {
            for (i--; i > -1 && items.get(i).data instanceof ArrayList; i--) {
                if (i % testWorksheetDO.getTotalCapacity() == 0) {
                    while (i < items.size())
                        items.remove(i);
                }
            }
        }

        if (j < analysisItems.size())
            Window.alert(consts.get("worksheetIsFull"));

        //
        // load QC Link dropdown model
        //
        qcLinkModel.clear();
        qcLinkModel.add(new TableDataRow(null, ""));
        for (i = 0; i < items.size(); i++)
            qcLinkModel.add(new TableDataRow(items.get(i).key,items.get(i).cells.get(1).value.toString()));
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(3).getColumnWidget()).setModel(qcLinkModel);
        
        worksheetItemTable.load(items);
        
        clearErrors();
        if (!qcErrors.isEmpty()) {
            message = "";
            for (i = -4; i < items.size(); i++) {
                tempE = (Exception)qcErrors.get(i+1);
                if (tempE == null)
                    continue;
                if (message.length() > 0)
                    message += "\n\n";
                message += tempE.getMessage();
            }

            if (message.length() > 0) {
//                window.setError(message);
                Window.alert(message);
            }
        }
    }
    
    private void openWorksheetLookup() {
        ScreenWindow modal;
        
        try {
            if (wLookupScreen == null) {
                wLookupScreen = new WorksheetLookupScreen();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<TableDataRow> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).data;
                                relatedWorksheetId.setValue(wVDO.getId());
                            }
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void openWorksheetQCLookup() {
        ScreenWindow modal;
        
        try {
            if (wQCLookupScreen == null) {
                wQCLookupScreen = new WorksheetLookupScreen();
                wQCLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<TableDataRow> list;
                        ScreenWindow            modal2;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).data;
                                try {
                                    if (waSelectionScreen == null) {
                                        waSelectionScreen = new WorksheetQcAnalysisSelectionScreen();
                                        waSelectionScreen.addActionHandler(new ActionHandler<WorksheetQcAnalysisSelectionScreen.Action>() {
                                            public void onAction(ActionEvent<WorksheetQcAnalysisSelectionScreen.Action> event) {
                                                int                      i, r;
                                                ArrayList<TableDataRow>  list;
                                                ArrayList<Object>        dataList;
                                                QcManager                qcManager;
                                                TableDataRow             qcRow;
                                                TestWorksheetItemDO      twiDO;
                                                WorksheetAnalysisDO      waDO;
                                                WorksheetQcResultManager wqrManager;      
    
                                                if (event.getAction() == WorksheetQcAnalysisSelectionScreen.Action.OK) {
                                                    list = (ArrayList<TableDataRow>)event.getData();
                                                    if (list != null) {
                                                        r = worksheetItemTable.getSelectedRow();
                                                        if (r == -1)
                                                            r = worksheetItemTable.numRows();
                                                        else
                                                            r++;
                                                        
                                                        for (i = 0; i < list.size(); i++) {
                                                            waDO = (WorksheetAnalysisDO)list.get(i).data;
                                                            try {
                                                                qcManager = qcService.call("fetchById", waDO.getQcId());
                                                                wqrManager = worksheetService.call("fetchWorksheeetQcResultByWorksheetAnalysisId", waDO.getId());
                                                                
                                                                twiDO = new TestWorksheetItemDO();
                                                                twiDO.setPosition(r+1);
                                                                twiDO.setTypeId(typeFixed);
                                                                twiDO.setQcName(qcManager.getQc().getName());
                                                                
                                                                qcRow = new TableDataRow(11);
                                                                qcRow.cells.get(1).value = waDO.getAccessionNumber();
                                                                qcRow.cells.get(2).value = qcManager.getQc().getName();
                                                                
                                                                dataList = new ArrayList<Object>();
                                                                dataList.add(twiDO);
                                                                dataList.add((QcDO)qcManager.getQc());
                                                                dataList.add(wqrManager);
                                                                qcRow.data = dataList;
                                                                
                                                                testWorksheetItems.add(qcRow);
                                                                r++;
                                                            } catch (Exception anyE) {
                                                                anyE.printStackTrace();
                                                                Window.alert("error: " + anyE.getMessage());
                                                            }
                                                        }
                                                        
                                                        buildQCWorksheet();
                                                        mergeAnalysesAndQCs();

                                                        isSaved = false;
                                                        saveButton.enable(true);
                                                        worksheetItemTable.enable(true);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    
                                    modal2 = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                                    waSelectionScreen.setWorksheetId(wVDO.getId());
                                    waSelectionScreen.draw();
                                    modal2.setContent(waSelectionScreen);
                                    modal2.setName(consts.get("worksheetNumber")+wVDO.getId().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Window.alert("error: " + e.getMessage());
                                    return;
                                }
                            }
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wQCLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void openQCLookup(String name, ArrayList<QcDO> list) {
        ScreenWindow modal;
        
        try {
            if (qcLookupScreen == null) {
                qcLookupScreen = new QcLookupScreen();
                qcLookupScreen.addActionHandler(new ActionHandler<QcLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<QcLookupScreen.Action> event) {
                        int                 i, r;
                        ArrayList<Object>   dataList;
                        ArrayList<QcDO>     list;
                        TableDataRow        qcRow;
                        QcDO                qcDO;
                        TestWorksheetItemDO twiDO;

                        if (event.getAction() == QcLookupScreen.Action.OK) {
                            list = (ArrayList<QcDO>)event.getData();
                            if (list != null) {
                                if (qcStartIndex != 0) {
                                    //
                                    // If qcStartIndex != 0, then we are coming
                                    // here because the popup was triggered by
                                    // multiple matching qcs in a predefined location
                                    // and we will only be getting one qc selected
                                    // from the popup
                                    //
                                    twiDO = twManager.getItemAt(qcStartIndex - 1);

                                    qcDO = list.get(0);

                                    qcRow = new TableDataRow(11);
                                    qcRow.cells.get(2).value = qcDO.getName();             // description
                                    
                                    dataList = new ArrayList<Object>();
                                    dataList.add(twiDO);
                                    dataList.add(qcDO);
                                    qcRow.data = dataList;
                                    
                                    testWorksheetItems.add(qcRow);
                                    
                                    loadQCTemplate();
                                } else {
                                    //
                                    // If qcStartIndex == 0, then we are coming
                                    // here because the popup was triggered by
                                    // the user clicking the lookup button
                                    //
                                    r = worksheetItemTable.getSelectedRow();
                                    if (r == -1)
                                        r = worksheetItemTable.numRows();
                                    else
                                        r++;

                                    for (i = 0; i < list.size(); i++) {
                                        if (qcItems != null && qcItems[r+i] != null) {
                                            Window.alert("Fixed QC already designated for position "+(r+i+1)+
                                                         "; Please select a different position or "+
                                                         "a shorter list of QCs to add");
                                            return;
                                        }
                                    }
                                    
                                    for (i = 0; i < list.size(); i++) {
                                        qcDO  = list.get(i);

                                        twiDO = new TestWorksheetItemDO();
                                        twiDO.setPosition(r+1);
                                        twiDO.setTypeId(typeFixed);
                                        twiDO.setQcName(qcDO.getName());
                                        
                                        qcRow = new TableDataRow(11);
                                        qcRow.cells.get(2).value = qcDO.getName();             // description
                                        
                                        dataList = new ArrayList<Object>();
                                        dataList.add(twiDO);
                                        dataList.add(qcDO);
                                        qcRow.data = dataList;
                                        
                                        testWorksheetItems.add(qcRow);
                                        r++;
                                    }

                                    buildQCWorksheet();
                                }
                                
                                mergeAnalysesAndQCs();

                                isSaved = false;
                                saveButton.enable(true);
                                worksheetItemTable.enable(true);
                            }
                        }
                    }
                });
            }
            
            if (qcStartIndex != 0)
                qcLookupScreen.enableMultiSelect(false);
            else
                qcLookupScreen.enableMultiSelect(true);
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("qcLookup"));
            modal.setContent(qcLookupScreen);

            if (list != null) {
                qcLookupScreen.clearFields();
                qcLookupScreen.setQueryResult(name, list);
            } else if (name != null) {
                qcLookupScreen.clearFields();
                qcLookupScreen.executeQuery(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private String getPositionNumber(int position) {
//        int    major, minor;a
        String positionNumber;
        
        positionNumber = "";
//        if (formatBatch.equals(testWorksheetDO.getFormatId())) {
//            major = getPositionMajorNumber(position+1);
//            minor = getPositionMinorNumber(position+1);
//            positionNumber = major+"-"+minor;
//        } else {
            positionNumber = String.valueOf(position + 1);
//        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
//   private int getPositionMajorNumber(int position) {
//       return (int) (position / (double)testWorksheetDO.getBatchCapacity() + .99);
//   }

   /**
     * Parses the position number and returns the minor number
     * for batch numbering.
     */
//   private int getPositionMinorNumber(int position) {
//       return position - (getPositionMajorNumber(position) - 1) * testWorksheetDO.getBatchCapacity();
//   }
   
//   private Integer parseBatchPosition(Object position, Integer batchCapacity) {
//       int major, minor, splitIndex;
//       
//       splitIndex = ((String)position).indexOf("-");
//       
//       major = Integer.parseInt(((String)position).substring(0, splitIndex));
//       minor = Integer.parseInt(((String)position).substring(splitIndex + 1));
//       
//       return new Integer(major * batchCapacity.intValue() + minor);
//   }
   
   private int getNextTempId() {
       return --tempId;
   }
   
   private void clearQCTemplate() {
       twManager = null;
       if (testWorksheetDO == null)
           testWorksheetDO = new TestWorksheetDO();
       testWorksheetDO.setFormatId(formatTotal);
       testWorksheetDO.setBatchCapacity(500);
       testWorksheetDO.setTotalCapacity(500);
       testWorksheetItems.clear();
       buildQCWorksheet();
       isTemplateLoaded = false;
   }
}