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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.QcManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetQcAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCreationScreen extends Screen {

    private boolean                               hasErrors, isTemplateLoaded,
                                                  isSaved, wasExitCalled;
    private int                                   tempId, qcStartIndex;
    private Integer                               formatId, formatBatch, formatTotal,
                                                  qcDup, statusWorking, typeFixed,
                                                  typeRand, typeLastWell, typeLastRun,
                                                  typeLastBoth;
    private ScreenService                         qcService;
    private ModulePermission                      userPermission;
    private WorksheetManager                      manager;

    private Button                                lookupWorksheetButton, saveButton,
                                                  exitButton, insertQCWorksheetButton,
                                                  insertQCLookupButton, removeRowButton;

    protected ArrayList<Integer>                  testIds;
    protected ArrayList<Item<Integer>>            analysisItems, qcLastRunList,
                                                  qcLastBothList, qcLinkModel,
                                                  testWorksheetItems;
    protected Confirm                             worksheetRemoveQCConfirm, worksheetRemoveLastOfQCConfirm,
                                                  worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer,Exception>          qcErrors;
    protected QcLookupScreen                      qcLookupScreen;
    protected Item<Integer>                       qcItems[];
    protected Table                               worksheetItemTable;
    protected TextBox<Integer>                    worksheetId, relatedWorksheetId;
    protected TestWorksheetDO                     testWorksheetDO;
    protected TestWorksheetManager                twManager;
    protected WorksheetQcAnalysisSelectionScreen  waSelectionScreen;
    protected WorksheetCreationLookupScreen       wcLookupScreen;
    protected WorksheetLookupScreen               wLookupScreen, wQCLookupScreen;
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service   = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        qcService = new ScreenService("OpenELISServlet?service=org.openelis.modules.qc.server.QcService");

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
        analysisItems      = new ArrayList<Item<Integer>>();
        hasErrors          = false;
        isSaved            = true;
        isTemplateLoaded   = false;
        manager            = WorksheetManager.getInstance();
        qcErrors           = new HashMap<Integer,Exception>();
        qcLastRunList      = new ArrayList<Item<Integer>>();
        qcLastBothList     = new ArrayList<Item<Integer>>();
        qcStartIndex       = 0;
        tempId             = -1;
        testIds            = new ArrayList<Integer>();
        testWorksheetItems = new ArrayList<Item<Integer>>();
        wasExitCalled      = false;

        try {
            DictionaryCache.preloadByCategorySystemNames("analysis_status",
                                                         "type_of_sample", 
                                                         "test_worksheet_format",
                                                         "test_worksheet_item_type",
                                                         "worksheet_status");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
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
                worksheetId.setEnabled(false);
            }
        });

        relatedWorksheetId = (TextBox)def.getWidget(WorksheetCreationMeta.getWorksheetRelatedWorksheetId());
        addScreenHandler(relatedWorksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setValue(manager.getWorksheet().getRelatedWorksheetId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                relatedWorksheetId.setEnabled(false);
            }
        });

        lookupWorksheetButton = (Button)def.getWidget("lookupWorksheetButton");
        addScreenHandler(lookupWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lookupWorksheetButton.setEnabled(true);
            }
        });

        saveButton = (Button)def.getWidget("saveButton");
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
                saveButton.setEnabled(false);
            }
        });

        exitButton = (Button)def.getWidget("exitButton");
        addScreenHandler(exitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                exit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                exitButton.setEnabled(true);
            }
        });

        worksheetItemTable = (Table)def.getWidget("worksheetItemTable");
        addScreenHandler(worksheetItemTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetItemTable.setEnabled(false);
            }
        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetItemTable.getSelectedRow() != -1)
                    removeRowButton.setEnabled(true);
            }
        });
        
        worksheetItemTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent event) {
                if (worksheetItemTable.getSelectedRow() == -1)
                    removeRowButton.setEnabled(false);
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

        /*
        worksheetItemTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
				ColumnComparator comparator;
                
                comparator = new ColumnComparator(event.getIndex(), event.getDirection());
                Collections.sort(analysisItems, comparator);

                mergeAnalysesAndQCs();
            }
        });
        */
        
        insertQCWorksheetButton = (Button)def.getWidget("insertQCWorksheetButton");
        addScreenHandler(insertQCWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetQCLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCWorksheetButton.setEnabled(false);
            }
        });

        insertQCLookupButton = (Button)def.getWidget("insertQCLookupButton");
        addScreenHandler(insertQCLookupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openQCLookup(null, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCLookupButton.setEnabled(false);
            }
        });

        removeRowButton = (Button)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int                 rowIndex;
                Row                 dataRow;
                
                rowIndex = worksheetItemTable.getSelectedRow();
                if (rowIndex > -1 && worksheetItemTable.getRowCount() > 0) {
                    dataRow = worksheetItemTable.getRowAt(rowIndex);
                    if (dataRow.getData() instanceof ArrayList) {
                        if (worksheetRemoveQCConfirm == null) {
                            worksheetRemoveQCConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                                   consts.get("worksheetRemoveQCConfirm"),
                                                                   "Don't Remove", "Remove");
                            worksheetRemoveQCConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                                public void onSelection(SelectionEvent<Integer> event) {
                                    Row                 qcRow;
                                    TestWorksheetItemDO twiDO;

                                    switch(event.getSelectedItem().intValue()) {
                                        case 1:
                                            qcRow = worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow());
                                            twiDO = (TestWorksheetItemDO) ((ArrayList)qcRow.getData()).get(0);
                                            
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
                                                                    testWorksheetItems.remove(worksheetItemTable.getSelectedRow());
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
                    } else if (dataRow.getData() instanceof WorksheetCreationVO) {
                        analysisItems.remove(dataRow);
                        mergeAnalysesAndQCs();
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.setEnabled(false);
            }
        });   
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns(){
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        try {
            formatBatch = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatTotal = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
            qcDup = DictionaryCache.getIdFromSystemName("qc_duplicate");
            statusWorking = DictionaryCache.getIdFromSystemName("worksheet_working");
            typeFixed = DictionaryCache.getIdFromSystemName("pos_fixed");
            typeRand = DictionaryCache.getIdFromSystemName("pos_random");
            typeLastWell = DictionaryCache.getIdFromSystemName("pos_last_of_well");
            typeLastRun = DictionaryCache.getIdFromSystemName("pos_last_of_run");
            typeLastBoth = DictionaryCache.getIdFromSystemName("pos_last_of_well_&_run");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            wcLookupScreen.getWindow().close();
            window.close();
        }

        //
        // load empty QC Link dropdown model
        //
        qcLinkModel = new ArrayList<Item<Integer>>();
        qcLinkModel.add(new Item<Integer>(null, ""));
        ((Dropdown<Integer>)worksheetItemTable.getColumnWidget(3)).setModel(qcLinkModel);

        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)worksheetItemTable.getColumnWidget(6)).setModel(model);
}
    
    protected void openLookupWindow() {
        if (wcLookupScreen == null) {
            try {
                wcLookupScreen = new WorksheetCreationLookupScreen();
                wcLookupScreen.addActionHandler(new ActionHandler<WorksheetCreationLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetCreationLookupScreen.Action> event) {
                        Integer                 testId;
                        ArrayList<Item<Integer>>list;
                        StringBuffer            message;
                        Item<Integer>            row, newRow;
                        SectionViewDO           sectionVDO;
                        WorksheetCreationVO     data;

                        if (event.getAction() == WorksheetCreationLookupScreen.Action.ADD) {
                            list = (ArrayList<Item<Integer>>)event.getData();
                            if (list != null && list.size() > 0) {
                                message = new StringBuffer();
                                for (int i = 0; i < list.size(); i++) {
                                    row  = list.get(i);
                                    newRow = new Item<Integer>(11);
                                    data = (WorksheetCreationVO)row.getData();
                                    
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

                                    newRow.setKey(getNextTempId());                           // fake worksheet analysis id
                                    newRow.setCell(1,row.getCell(0));     // accession #
                                    newRow.setCell(2,row.getCell(1));     // description
                                    newRow.setCell(4,row.getCell(2));     // test name
                                    newRow.setCell(5,row.getCell(3));     // method name
                                    newRow.setCell(6,row.getCell(5));     // status
                                    newRow.setCell(7,row.getCell(6));     // collection date
                                    newRow.setCell(8,row.getCell(7));     // received date and time
                                    newRow.setCell(9,row.getCell(8));     // due days
                                    newRow.setCell(10,row.getCell(9));    // expire date and time
                                    newRow.setData(data);
                                    analysisItems.add(newRow);
                                }
                                
                                if (message.length() > 0)
                                    com.google.gwt.user.client.Window.alert(consts.get("worksheetItemsFormatConflict")+":\n\n"+message.toString());

                                if (testIds.size() > 1) {
                                    com.google.gwt.user.client.Window.alert(consts.get("multipleTestsOnWorksheet"));
                                    clearQCTemplate();
                                } else if (!isTemplateLoaded) {
                                    loadQCTemplate();
                                }
                                mergeAnalysesAndQCs();

                                isSaved = false;
                                saveButton.setEnabled(true);
                                worksheetItemTable.setEnabled(true);
                                insertQCWorksheetButton.setEnabled(true);
                                insertQCLookupButton.setEnabled(true);
                            }
                        }
                    }
                });
                
                OpenELIS.getBrowser().addScreen(wcLookupScreen);
            } catch (Exception e) {
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
                return;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void save() {
        int                      i;
        Object                   position;
        Item<Integer>            row;
        WorksheetDO              wDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        
        setFocus(null);
        
        if (worksheetItemTable.getRowCount() == 0) {
            com.google.gwt.user.client.Window.alert(consts.get("worksheetNotSaveEmpty"));
            return;
        }
        
        if (hasErrors) {
            com.google.gwt.user.client.Window.alert("Please fix errors on worksheet before saving");
            return;
        }
        
        window.setBusy(consts.get("saving"));

        wDO = manager.getWorksheet();
        wDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wDO.setSystemUserId(OpenELIS.getSystemUserPermission().getSystemUserId());
        wDO.setStatusId(statusWorking);
        wDO.setFormatId(testWorksheetDO.getFormatId());
        if (formatBatch.equals(wDO.getFormatId()))
            wDO.setBatchCapacity(testWorksheetDO.getBatchCapacity());
        if (relatedWorksheetId.getValue() != null)
            wDO.setRelatedWorksheetId(relatedWorksheetId.getValue());
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.getRowCount(); i++) {
            row = (Item<Integer>)worksheetItemTable.getRowAt(i);
            
            wiDO = new WorksheetItemDO();
            position = row.getCell(0);
            if (formatBatch.equals(wDO.getFormatId()))
                wiDO.setPosition(parseBatchPosition(position, testWorksheetDO.getBatchCapacity()));
            else
                wiDO.setPosition(Integer.valueOf((String)position));
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setId((Integer)row.getKey());
            waDO.setAccessionNumber(row.getCell(1).toString());
            if (row.getData() instanceof ArrayList) {
                waDO.setQcId(((QcDO)((ArrayList<Object>)row.getData()).get(1)).getId());
            } else {
                waDO.setAnalysisId(((WorksheetCreationVO)row.getData()).getAnalysisId());
            }
            waDO.setWorksheetAnalysisId((Integer)row.getCell(3));
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
            } catch (Exception anyE) {
                com.google.gwt.user.client.Window.alert("save(): " + anyE.getMessage());
                window.clearStatus();
            }
        }
        
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("savingComplete"));
            
            isSaved = true;
            saveButton.setEnabled(false);
            insertQCWorksheetButton.setEnabled(false);
            insertQCLookupButton.setEnabled(false);
            lookupWorksheetButton.setEnabled(false);
            removeRowButton.setEnabled(false);
            worksheetItemTable.setEnabled(false);
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("save(): " + e.getMessage());
            window.clearStatus();
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
        int                  i, j;
//        String               message;
        ArrayList<Object>    dataList;
        ArrayList<QcDO>      list;
        QcDO                 qcDO = null;
        Item<Integer>        qcRow;
        TestWorksheetItemDO  twiDO;
        
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
                    list = qcService.callList("fetchActiveByName", twiDO.getQcName());
                    if (list.size() == 0) {
                        for (j = twiDO.getPosition(); j < testWorksheetDO.getTotalCapacity(); j += testWorksheetDO.getBatchCapacity())
                            qcErrors.put(j, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), String.valueOf(j)));
                        qcDO = new QcDO();
                        qcDO.setName(twiDO.getQcName());
                    } else if (list.size() > 1) {
                        com.google.gwt.user.client.Window.alert(new FormErrorException("multiMatchingActiveQc", twiDO.getQcName(), String.valueOf(i+1)).getMessage());
                        openQCLookup(twiDO.getQcName(), list);
                        qcStartIndex = i + 1;
                        break;
                    } else {
                        qcDO = list.get(0);
                    }

                    qcRow = new Item<Integer>(11);
                    qcRow.setCell(2,qcDO.getName());             // description
                    
                    dataList = new ArrayList<Object>();
                    dataList.add(twiDO);
                    dataList.add(qcDO);
                    qcRow.setData(dataList);
                    
                    testWorksheetItems.add(qcRow);
                } catch (Exception anyE) {
                    com.google.gwt.user.client.Window.alert("loadQCTemplate(): " + anyE.getMessage());
                    window.clearStatus();
                }
            }
        } catch (Exception anyE) {
            com.google.gwt.user.client.Window.alert("loadQCTemplate(): " + anyE.getMessage());
            window.clearStatus();
        }
        
        buildQCWorksheet();
        isTemplateLoaded = true;
    }
    
    @SuppressWarnings("unchecked")
    private void buildQCWorksheet() {
        int                      i, j, posNum, randSize, numBatches, startIndex;
        String                   accessionNumber;
        ArrayList<Item<Integer>>  qcRandList, qcLastWellList, lastOf;
        Item<Integer>             qcRow, qcRow1;
        TestWorksheetItemDO      twiDO;

        //
        // initialize/clear the qcItems and qcErrors
        //
        if (qcItems == null) {
            qcItems = new Item[testWorksheetDO.getTotalCapacity()];
        } else {
            for (i = 0; i < qcItems.length; i++)
                qcItems[i] = null;
        }

        qcRandList     = new ArrayList<Item<Integer>>();
        qcLastWellList = new ArrayList<Item<Integer>>();
        qcLastRunList.clear();
        qcLastBothList.clear();
        numBatches = testWorksheetDO.getTotalCapacity() / testWorksheetDO.getBatchCapacity();
        
        //
        // Insert Fixed/Duplicate QCItems into worksheet per batch and store 
        // Random and LastOf QC Items for later.
        //
        for (i = 0; i < testWorksheetItems.size(); i++) {
            qcRow = testWorksheetItems.get(i);
            twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)testWorksheetItems.get(i).getData()).get(0);
            
            qcRow.setKey(getNextTempId());                           // fake worksheet analysis id
            
            if (typeFixed.equals(twiDO.getTypeId()))
                for (j = 0; j < numBatches; j++) {
                    posNum = j * testWorksheetDO.getBatchCapacity() + twiDO.getPosition() - 1;
                    
                    //
                    // Do NOT overwrite accession numbers on QCs pulled from other
                    // worksheets
                    //
                    accessionNumber = (String) qcRow.getCell(1);
                    if (accessionNumber == null || accessionNumber.startsWith("X."))
                        qcRow.setCell(1,"X."+getPositionNumber(posNum));     // qc accession #
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
                qcRow = (Item<Integer>) lastOf.get(j).clone();
                qcRow.setCell(1,"X."+getPositionNumber(posNum));     // qc accession #
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
                qcRow = (Item<Integer>) qcRandList.get(j).clone();
                posNum = (int) (Math.random() * (testWorksheetDO.getBatchCapacity() - 1)) + i * testWorksheetDO.getBatchCapacity();
                if (qcItems[posNum] == null) {
                    if (posNum + 1 < testWorksheetDO.getTotalCapacity()) {
                        qcRow1 = qcItems[posNum+1];
                        if (qcRow1 != null && qcDup.equals(((QcDO)qcRow1.getData()).getTypeId()))
                            continue;
                    }
                    qcRow.setCell(1,"X."+getPositionNumber(posNum));     // qc accession #
                    qcItems[posNum] = qcRow;
                    j++;
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void mergeAnalysesAndQCs() {
        int                     i, j, k;
        ArrayList<Item<Integer>> items, lastOf;
        Exception               tempE;
        String                  message;
        Item<Integer>           row;
        
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
        items = new ArrayList<Item<Integer>>();
        for (i = 0; i < testWorksheetDO.getTotalCapacity() - lastOf.size(); i++) {
            row = qcItems[i];
            if (row == null) {
                if (j >= analysisItems.size())
                    break;

                row = analysisItems.get(j);
                row.setCell(0,getPositionNumber(i));
                items.add(analysisItems.get(j));
                j++;
            } else {
                row.setCell(0,getPositionNumber(i));
                items.add(row);
            }
        }
        
        //
        // Append Last of Run QCItems
        //
        for (k = 0; k < lastOf.size() && i < testWorksheetDO.getTotalCapacity(); k++) {
            row = lastOf.get(k);
            row.setCell(0,getPositionNumber(i));
            row.setCell(1,"X."+getPositionNumber(i));     // qc accession #
            items.add(row);
            i++;
        }

        //
        // If last batch contains only QC items, remove it
        //
        for (i--; i > -1 && items.get(i).getData() instanceof ArrayList; i--) {
            if (i % testWorksheetDO.getTotalCapacity() == 0) {
                while (i < items.size())
                    items.remove(i);
            }
        }

        if (j < analysisItems.size())
            com.google.gwt.user.client.Window.alert(consts.get("worksheetIsFull"));

        //
        // load QC Link dropdown model
        //
        qcLinkModel.clear();
        qcLinkModel.add(new Item<Integer>(null, ""));
        for (i = 0; i < items.size(); i++)
            qcLinkModel.add(new Item<Integer>(items.get(i).getKey(),items.get(i).getCell(1).toString()));
        ((Dropdown<Integer>)worksheetItemTable.getColumnWidget(3)).setModel(qcLinkModel);
        
        worksheetItemTable.setModel(items);
        
        clearErrors();
        hasErrors = false;
        if (!qcErrors.isEmpty()) {
            message = "";
            for (i = 0; i < items.size(); i++) {
                tempE = (Exception)qcErrors.get(i+1);
                if (tempE == null)
                    continue;
                if (message.length() > 0)
                    message += "\n\n";
                message += tempE.getMessage();
                hasErrors = true;
            }
            
            if (hasErrors) {
                window.setError(message);
                com.google.gwt.user.client.Window.alert(message);
            }
        }
    }
    
    private void openWorksheetLookup() {
        ModalWindow modal;
        
        try {
            if (wLookupScreen == null) {
                wLookupScreen = new WorksheetLookupScreen();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<Item<Integer>> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<Item<Integer>>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).getData();
                                relatedWorksheetId.setValue(wVDO.getId());
                            }
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void openWorksheetQCLookup() {
        ModalWindow modal;
        
        try {
            if (wQCLookupScreen == null) {
                wQCLookupScreen = new WorksheetLookupScreen();
                wQCLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<Item<Integer>> list;
                        ModalWindow            modal2;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<Item<Integer>>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).getData();
                                try {
                                    if (waSelectionScreen == null) {
                                        waSelectionScreen = new WorksheetQcAnalysisSelectionScreen();
                                        waSelectionScreen.addActionHandler(new ActionHandler<WorksheetQcAnalysisSelectionScreen.Action>() {
                                            public void onAction(ActionEvent<WorksheetQcAnalysisSelectionScreen.Action> event) {
                                                int                     i, r;
                                                ArrayList<Item<Integer>> list;
                                                ArrayList<Object>       dataList;
                                                QcManager               qcManager;
                                                Item<Integer>           qcRow;
                                                TestWorksheetItemDO     twiDO;
                                                WorksheetAnalysisDO     waDO;
    
                                                if (event.getAction() == WorksheetQcAnalysisSelectionScreen.Action.OK) {
                                                    list = (ArrayList<Item<Integer>>)event.getData();
                                                    if (list != null) {
                                                        r = worksheetItemTable.getSelectedRow();
                                                        if (r == -1)
                                                            r = worksheetItemTable.getRowCount();
                                                        else
                                                            r++;
                                                        
                                                        for (i = 0; i < list.size(); i++) {
                                                            waDO = (WorksheetAnalysisDO)list.get(i).getData();
                                                            try {
                                                                qcManager = qcService.call("fetchById", waDO.getQcId());
                                                                twiDO = new TestWorksheetItemDO();
                                                                twiDO.setPosition(r+1);
                                                                twiDO.setTypeId(typeFixed);
                                                                twiDO.setQcName(qcManager.getQc().getName());
                                                                
                                                                qcRow = new Item<Integer>(11);
                                                                qcRow.setCell(1, waDO.getAccessionNumber());
                                                                qcRow.setCell(2, qcManager.getQc().getName());
                                                                
                                                                dataList = new ArrayList<Object>();
                                                                dataList.add(twiDO);
                                                                dataList.add((QcDO)qcManager.getQc());
                                                                qcRow.setData(dataList);
                                                                
                                                                testWorksheetItems.add(qcRow);
                                                                r++;
                                                            } catch (Exception anyE) {
                                                                anyE.printStackTrace();
                                                                com.google.gwt.user.client.Window.alert("error: " + anyE.getMessage());
                                                            }
                                                        }
                                                        
                                                        buildQCWorksheet();
                                                        mergeAnalysesAndQCs();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    
                                    modal2 = new ModalWindow();
                                    waSelectionScreen.setWorksheetId(wVDO.getId());
                                    waSelectionScreen.draw();
                                    modal2.setContent(waSelectionScreen);
                                    modal2.setName(consts.get("worksheetNumber")+wVDO.getId().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
                                    return;
                                }
                            }
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wQCLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void openQCLookup(String name, ArrayList<QcDO> list) {
        ModalWindow modal;
        
        try {
            if (qcLookupScreen == null) {
                qcLookupScreen = new QcLookupScreen();
                qcLookupScreen.addActionHandler(new ActionHandler<QcLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<QcLookupScreen.Action> event) {
                        int                 i, r;
                        ArrayList<Object>   dataList;
                        ArrayList<QcDO>     list;
                        Item<Integer>       qcRow;
                        QcDO                qcDO;
                        TestWorksheetItemDO twiDO;

                        if (event.getAction() == QcLookupScreen.Action.OK) {
                            list = (ArrayList<QcDO>)event.getData();
                            if (list != null) {
                                if (qcStartIndex != 0) {
                                    r = twManager.getItemAt(qcStartIndex - 1).getPosition();
                                } else {
                                    r = worksheetItemTable.getSelectedRow();
                                    if (r == -1)
                                        r = worksheetItemTable.getRowCount();
                                    else
                                        r++;
                                }
                                
                                for (i = 0; i < list.size(); i++) {
                                    if (qcItems[r+i] != null) {
                                        com.google.gwt.user.client.Window.alert("Fixed QC already designated for position "+(r+i+1)+
                                                     "; Please select a different position or "+
                                                     "a shorter list of QCs to add");
                                        return;
                                    }
                                }
                                
                                for (i = 0; i < list.size(); i++) {
                                    qcDO  = list.get(i);

                                    twiDO = new TestWorksheetItemDO();
                                    if (qcStartIndex != 0)
                                        twiDO.setPosition(r);
                                    else
                                        twiDO.setPosition(r+1);
                                    twiDO.setTypeId(typeFixed);
                                    twiDO.setQcName(qcDO.getName());
                                    
                                    qcRow = new Item<Integer>(11);
                                    qcRow.setCell(2, qcDO.getName());             // description
                                    
                                    dataList = new ArrayList<Object>();
                                    dataList.add(twiDO);
                                    dataList.add(qcDO);
                                    qcRow.setData(dataList);
                                    
                                    testWorksheetItems.add(qcRow);
                                    r++;
                                }
                                
                                qcLookupScreen.getWindow().close();
                                
                                if (qcStartIndex != 0)
                                    loadQCTemplate();
                                else
                                    buildQCWorksheet();
                                
                                mergeAnalysesAndQCs();
                            }
                        }
                    }
                });
            }
            
            if (qcStartIndex != 0)
                qcLookupScreen.enableMultiSelect(false);
            else
                qcLookupScreen.enableMultiSelect(true);
            
            modal = new ModalWindow();
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
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private String getPositionNumber(int position) {
        int    major, minor;
        String positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(testWorksheetDO.getFormatId())) {
            major = getPositionMajorNumber(position+1);
            minor = getPositionMinorNumber(position+1);
            positionNumber = major+"-"+minor;
        } else {
            positionNumber = String.valueOf(position + 1);
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
   
   private Integer parseBatchPosition(Object position, Integer batchCapacity) {
       int major, minor, splitIndex;
       
       splitIndex = ((String)position).indexOf("-");
       
       major = Integer.parseInt(((String)position).substring(0, splitIndex));
       minor = Integer.parseInt(((String)position).substring(splitIndex + 1));
       
       return new Integer(major * batchCapacity.intValue() + minor);
   }
   
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