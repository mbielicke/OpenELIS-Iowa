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
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
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
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
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
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCreationScreen extends Screen {

    private boolean                            isTemplateLoaded, isSaved, wasExitCalled;
    private int                                tempId, qcStartIndex;
    private Integer                            formatId, formatTotal,
                                               statusWorking, typeDup, typeFixed,
                                               typeFixedAlways, typeLastBoth,
                                               typeLastRun, typeLastSubset, typeRand;
    private String                             typeLastBothString, typeLastRunString,
                                               typeLastSubsetString, typeRandString;
    private ScreenService                      analysisService, instrumentService,
                                               qcService, worksheetService;
    private ModulePermission                   userPermission;
    private WorksheetManager                   manager;

    private AppButton                          lookupWorksheetButton, saveButton,
                                               exitButton, insertAnalysisWorksheetButton,
                                               insertQCLookupButton, removeRowButton;

    protected ArrayList<Integer>               testIds;
    protected ArrayList<TableDataRow>          analysisItems, qcLastRunList,
                                               qcLastBothList, qcLinkModel,
                                               testWorksheetItems;
    protected AutoComplete<Integer>            instrumentId;
    protected Confirm                          worksheetRemoveDuplicateQCConfirm,
                                               worksheetRemoveQCConfirm, worksheetRemoveLastOfQCConfirm,
                                               worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer,Exception>       qcErrors;
    protected QcLookupScreen                   qcLookupScreen;
    protected TableDataRow                     qcItems[];
    protected TableWidget                      worksheetItemTable;
    protected TextBox<Integer>                 worksheetId, relatedWorksheetId;
    protected TestWorksheetDO                  testWorksheetDO;
    protected TestWorksheetManager             twManager;
    protected WorksheetAnalysisSelectionScreen waSelectionScreen;
    protected WorksheetCreationLookupScreen    wcLookupScreen;
    protected WorksheetLookupScreen            wLookupScreen, wAnaLookupScreen;
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service           = new ScreenService("controller?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        analysisService   = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        instrumentService = new ScreenService("controller?service=org.openelis.modules.instrument.server.InstrumentService");
        qcService         = new ScreenService("controller?service=org.openelis.modules.qc.server.QcService");
        worksheetService  = new ScreenService("controller?service=org.openelis.modules.worksheet.server.WorksheetService");

        userPermission = UserCache.getPermission().getModule("worksheet");
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
            CategoryCache.getBySystemNames("analysis_status",
                                           "instrument_type",
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
        loadDefaultQCTemplate();
        
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        worksheetId = (TextBox<Integer>)def.getWidget(WorksheetCreationMeta.getWorksheetId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(false);
            }
        });

        relatedWorksheetId = (TextBox<Integer>)def.getWidget(WorksheetCreationMeta.getWorksheetRelatedWorksheetId());
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

        instrumentId = (AutoComplete)def.getWidget("instrumentId");
        addScreenHandler(instrumentId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                instrumentId.enable(true);
            }
        });

        instrumentId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow>     model;
                ArrayList<InstrumentViewDO> matches;
                TableDataRow                row;
                InstrumentViewDO            iVDO;                

                try {
                    model = new ArrayList<TableDataRow>();
                    matches = instrumentService.callList("fetchActiveByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (int i = 0; i < matches.size(); i++) {
                        iVDO = (InstrumentViewDO)matches.get(i);
                        
                        row = new TableDataRow(5);
                        row.key = iVDO.getId();
                        row.cells.get(0).value = iVDO.getName();
                        row.cells.get(1).value = iVDO.getDescription();
                        row.cells.get(2).value = DictionaryCache.getById(iVDO.getTypeId()).getEntry();
                        row.cells.get(3).value = iVDO.getLocation();
                        row.data = iVDO;
                        
                        model.add(row);
                    } 
                    
                    instrumentId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
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
        
        insertAnalysisWorksheetButton = (AppButton)def.getWidget("insertAnalysisWorksheetButton");
        addScreenHandler(insertAnalysisWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetAnalysisLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertAnalysisWorksheetButton.enable(true);
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
                int                 i, rowIndex;
                Integer             tempKey;
                TableDataRow        dataRow, tempRow;
                
                worksheetItemTable.finishEditing();
                rowIndex = worksheetItemTable.getSelectedRow();
                if (rowIndex > -1 && worksheetItemTable.numRows() > 0) {
                    dataRow = worksheetItemTable.getRow(rowIndex);
                    
                    for (i = 0; i < worksheetItemTable.numRows(); i++) {
                        tempRow = worksheetItemTable.getRow(i);
                        if (tempRow != dataRow && tempRow.cells.get(3).getValue() != null) {
                            if (tempRow.cells.get(3).getValue() instanceof ArrayList) {
                                if (((ArrayList<Object>)tempRow.cells.get(3).getValue()).size() > 0)
                                    tempKey = (Integer)((ArrayList<Object>)tempRow.cells.get(3).getValue()).get(0);
                                else
                                    tempKey = null;
                            } else {
                                tempKey = (Integer)tempRow.cells.get(3).getValue();
                            }
                            
                            if (((Integer)dataRow.key).equals(tempKey)) {
                                Window.alert(consts.get("oneOrMoreQcLinkOnRemove"));
                                return;
                            }
                        }
                    }
                    
                    if (dataRow.data instanceof ArrayList) {
                        if (((ArrayList<Object>)dataRow.data).size() != 3) {
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
                                                
                                                if (typeLastSubset.equals(twiDO.getTypeId()) ||
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
                        } else {
                            analysisItems.remove(dataRow);
                            mergeAnalysesAndQCs();
                        }
                    } else if (dataRow.data instanceof WorksheetCreationVO) {
                        if (((String)dataRow.cells.get(1).value).startsWith("D")) {
                            if (worksheetRemoveDuplicateQCConfirm == null) {
                                worksheetRemoveDuplicateQCConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                                                consts.get("worksheetRemoveDuplicateQCConfirm"),
                                                                                "Don't Remove", "Remove");
                                worksheetRemoveDuplicateQCConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                                    public void onSelection(SelectionEvent<Integer> event) {
                                        TableDataRow qDataRow;

                                        switch(event.getSelectedItem().intValue()) {
                                            case 1:
                                                qDataRow = qcItems[worksheetItemTable.getSelectedRow()];
                                                testWorksheetItems.remove(qDataRow);
                                                buildQCWorksheet();
                                                mergeAnalysesAndQCs();
                                                break;
                                        }
                                    }
                                });
                            }
                            worksheetRemoveDuplicateQCConfirm.show();
                        } else {
                            analysisItems.remove(dataRow);
                            mergeAnalysesAndQCs();
                        }
                    }
                    
                    if (worksheetItemTable.numRows() <= 0) {
                        formatId = null;
                        testIds.clear();
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
            formatTotal = DictionaryCache.getIdBySystemName("wf_total");
            statusWorking = DictionaryCache.getIdBySystemName("worksheet_working");
            typeDup = DictionaryCache.getIdBySystemName("pos_duplicate");
            typeFixed = DictionaryCache.getIdBySystemName("pos_fixed");
            typeFixedAlways = DictionaryCache.getIdBySystemName("pos_fixed_always");
            typeLastBoth = DictionaryCache.getIdBySystemName("pos_last_of_subset_&_run");
            typeLastRun = DictionaryCache.getIdBySystemName("pos_last_of_run");
            typeLastSubset = DictionaryCache.getIdBySystemName("pos_last_of_subset");
            typeRand = DictionaryCache.getIdBySystemName("pos_random");
            typeLastSubsetString = DictionaryCache.getById(typeLastSubset).getEntry();
            typeLastRunString = DictionaryCache.getById(typeLastRun).getEntry();
            typeLastBothString = DictionaryCache.getById(typeLastBoth).getEntry();
            typeRandString = DictionaryCache.getById(typeRand).getEntry();
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
        dictList  = CategoryCache.getBySystemName("analysis_status");
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
                                        if (data.getWorksheetFormatId() != null)
                                            formatId = data.getWorksheetFormatId();
                                        else
                                            formatId = formatTotal;
                                    } else if (formatTotal.equals(formatId) && data.getWorksheetFormatId() == null) {
                                        // if a worksheet format is not specified
                                        // in the test definition we default it 
                                        // to DefaultTotal, therefore tests with
                                        // a format of DefaultTotal and tests with
                                        // a null format can be on the same worksheet
                                    } else if (!formatId.equals(data.getWorksheetFormatId())) {
                                        message.append(consts.get("accessionNum")).append(data.getAccessionNumber())
                                               .append("\t").append(data.getTestName().trim()).append(", ")
                                               .append(data.getMethodName().trim());
                                        try {
                                            sectionVDO = SectionCache.getById(data.getSectionId());
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

                                    newRow.key = getNextTempId();                                       // fake worksheet analysis id
                                    newRow.cells.get(1).value = String.valueOf(row.cells.get(0).value); // accession #
                                    newRow.cells.get(2).value = row.cells.get(1).value;                 // description
                                    newRow.cells.get(4).value = row.cells.get(2).value;                 // test name
                                    newRow.cells.get(5).value = row.cells.get(3).value;                 // method name
                                    newRow.cells.get(6).value = row.cells.get(5).value;                 // status
                                    newRow.cells.get(7).value = row.cells.get(6).value;                 // collection date
                                    newRow.cells.get(8).value = row.cells.get(7).value;                 // received date and time
                                    newRow.cells.get(9).value = row.cells.get(8).value;                 // due days
                                    newRow.cells.get(10).value = row.cells.get(9).value;                // expire date and time
                                    newRow.data = data;
                                    analysisItems.add(newRow);
                                }
                                
                                if (message.length() > 0)
                                    Window.alert(consts.get("worksheetItemsFormatConflict")+":\n\n"+message.toString());

                                if (testIds.size() > 1)
                                    Window.alert(consts.get("multipleTestsOnWorksheet"));
                                else if (!isTemplateLoaded)
                                    loadQCTemplate();

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
        int                      i, j, k;
        ArrayList<IdNameVO>      columnNameVOs;
        HashMap<String,Integer>  toColumnNames;
        HashMap<Integer,String>  fromColumnNames;
        HashMap<Integer,HashMap<Integer,String>> formatColumnNames;
        IdNameVO                 columnNameVO;
        Integer                  fromFormatId, toIndex;
        String                   fromName;
        TableDataRow             row;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        WorksheetQcResultManager wqrManager, newWqrManager;
        WorksheetQcResultViewDO  wqrVDO, newWqrVDO;
        WorksheetResultManager   wrManager, newWrManager;
        WorksheetResultViewDO    wrVDO, newWrVDO;
        WorksheetViewDO          wVDO;
        
        setFocus(null);
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert(consts.get("worksheetNotSaveEmpty"));
            return;
        }
        
        window.setBusy(consts.get("saving"));
        
        //
        // If the format has not been set (QC only worksheet), set it to the default
        //
        if (formatId == null)
            formatId = formatTotal;
        
        formatColumnNames = new HashMap<Integer,HashMap<Integer,String>>();

        try {
            columnNameVOs = service.callList("getColumnNames", formatId);
            toColumnNames = new HashMap<String,Integer>();
            for (i = 0; i < columnNameVOs.size(); i++) {
                columnNameVO = columnNameVOs.get(i);
                toColumnNames.put(columnNameVO.getName(), columnNameVO.getId());
            }
        } catch (Exception anyE) {
//            Window.alert(consts.get("worksheetToColumnMappingLoadError"));
            anyE.printStackTrace();
            toColumnNames = null;
        }

        wVDO = manager.getWorksheet();
        wVDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wVDO.setSystemUserId(UserCache.getPermission().getSystemUserId());
        wVDO.setStatusId(statusWorking);
        wVDO.setFormatId(formatId);
        wVDO.setSubsetCapacity(testWorksheetDO.getSubsetCapacity());
        if (relatedWorksheetId.getFieldValue() != null)
            wVDO.setRelatedWorksheetId(relatedWorksheetId.getFieldValue());
        if (instrumentId.getSelection() != null && instrumentId.getSelection().key != null)
            wVDO.setInstrumentId((Integer)instrumentId.getSelection().key);
        
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
            waDO.setId((Integer)row.key);
            waDO.setAccessionNumber((String)row.cells.get(1).value);
            
            //
            // Set either the qc id or the analysis id depending on what type
            // of row we have
            //
            if (row.data instanceof ArrayList) {
                if (((ArrayList<Object>)row.data).size() == 3)
                    waDO.setAnalysisId(((AnalysisViewDO)((ArrayList<Object>)row.data).get(0)).getId());
                else
                    waDO.setQcLotId(((QcLotViewDO)((ArrayList<Object>)row.data).get(1)).getId());
            } else {
                waDO.setAnalysisId(((WorksheetCreationVO)row.data).getAnalysisId());
            }
            
            //
            // Pull out the analysis id for the qc link column
            //
            if (row.cells.get(3).getValue() != null) {
                if (row.cells.get(3).getValue() instanceof ArrayList) {
                    if (((ArrayList<Object>)row.cells.get(3).getValue()).size() > 0)
                        waDO.setWorksheetAnalysisId((Integer)((ArrayList<Object>)row.cells.get(3).getValue()).get(0));
                } else {
                    waDO.setWorksheetAnalysisId((Integer)row.cells.get(3).getValue());
                }
            }
            waDO.setIsFromOther("N");
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
                //
                // If this analysis is from another worksheet, copy the result
                // records from the manager in the ArrayList
                //
                if (row.data instanceof ArrayList && ((ArrayList<Object>)row.data).size() >= 3) {
                    waDO.setIsFromOther("Y");
                    fromFormatId = (Integer) ((ArrayList<Object>)row.data).get(((ArrayList<Object>)row.data).size() - 1);
                    if (!formatId.equals(fromFormatId)) {
                        fromColumnNames = formatColumnNames.get(fromFormatId);
                        if (fromColumnNames == null) {
                            try {
                                columnNameVOs = service.callList("getColumnNames", fromFormatId);
                                fromColumnNames = new HashMap<Integer,String>();
                                for (j = 0; j < columnNameVOs.size(); j++) {
                                    columnNameVO = columnNameVOs.get(j);
                                    fromColumnNames.put(columnNameVO.getId(), columnNameVO.getName());
                                }
                                formatColumnNames.put(fromFormatId, fromColumnNames);
                            } catch (Exception anyE1) {
//                                Window.alert(consts.get("worksheetFromColumnMappingLoadError"));
                                anyE1.printStackTrace();
                                fromColumnNames = null;
                            }
                        }
                    } else {
                        fromColumnNames = null;
                    }
                    
                    if (((ArrayList<Object>)row.data).size() == 3) {
                        wrManager = (WorksheetResultManager) ((ArrayList<Object>)row.data).get(1);
                        newWrManager = waManager.getWorksheetResultAt(waManager.count() - 1);
                        for (j = 0; j < wrManager.count(); j++) {
                            wrVDO = wrManager.getWorksheetResultAt(j);
                            newWrVDO = new WorksheetResultViewDO();
                            newWrVDO.setTestAnalyteId(wrVDO.getTestAnalyteId());
                            newWrVDO.setTestResultId(wrVDO.getTestResultId());
                            newWrVDO.setResultRow(wrVDO.getResultRow());
                            newWrVDO.setAnalyteId(wrVDO.getAnalyteId());
                            newWrVDO.setTypeId(wrVDO.getTypeId());
                            if (fromColumnNames != null && toColumnNames != null) {
                                for (k = 0; k < 30; k++) {
                                    fromName = fromColumnNames.get(k+9);
                                    if (fromName != null) {
                                        toIndex = toColumnNames.get(fromName);
                                        if (toIndex != null)
                                            newWrVDO.setValueAt(toIndex.intValue() - 9, wrVDO.getValueAt(k));
                                    }
                                }
                            } else {
                                for (k = 0; k < 30; k++)
                                    newWrVDO.setValueAt(k, wrVDO.getValueAt(k));
                            }
                            newWrVDO.setAnalyteName(wrVDO.getAnalyteName());
                            newWrVDO.setAnalyteExternalId(wrVDO.getAnalyteExternalId());
                            newWrVDO.setResultGroup(wrVDO.getResultGroup());
                            newWrManager.addWorksheetResult(newWrVDO);
                        }
                    } else {
                        wqrManager = (WorksheetQcResultManager) ((ArrayList<Object>)row.data).get(2);
                        newWqrManager = waManager.getWorksheetQcResultAt(waManager.count() - 1);
                        for (j = 0; j < wqrManager.count(); j++) {
                            wqrVDO = wqrManager.getWorksheetQcResultAt(j);
                            newWqrVDO = new WorksheetQcResultViewDO();
                            newWqrVDO.setSortOrder(wqrVDO.getSortOrder());
                            newWqrVDO.setQcAnalyteId(wqrVDO.getQcAnalyteId());
                            newWqrVDO.setTypeId(wqrVDO.getTypeId());
                            if (fromColumnNames != null && toColumnNames != null) {
                                for (k = 0; k < 30; k++) {
                                    fromName = fromColumnNames.get(k+9);
                                    if (fromName != null) {
                                        toIndex = toColumnNames.get(fromName);
                                        if (toIndex != null)
                                            newWqrVDO.setValueAt(toIndex.intValue() - 9, wqrVDO.getValueAt(k));
                                    }
                                }
                            } else {
                                for (k = 0; k < 30; k++)
                                    newWqrVDO.setValueAt(k, wqrVDO.getValueAt(k));
                            }
                            newWqrVDO.setAnalyteId(wqrVDO.getAnalyteId());
                            newWqrVDO.setAnalyteName(wqrVDO.getAnalyteName());
                            newWqrManager.addWorksheetQcResult(newWqrVDO);
                        }
                    }
                }
            } catch (Exception anyE) {
                Window.alert("save(): " + anyE.getMessage());
                window.clearStatus();
            }
        }
        
        final WorksheetCreationScreen wcs = this;
        worksheetService.call("add", manager, new AsyncCallback<WorksheetManager>() {
            public void onSuccess(WorksheetManager newMan) {
                manager = newMan;

                setState(State.DISPLAY);
                DataChangeEvent.fire(wcs);
                window.setDone(consts.get("savingComplete"));
                
                isSaved = true;
                saveButton.enable(false);
                insertAnalysisWorksheetButton.enable(false);
                insertQCLookupButton.enable(false);
                instrumentId.enable(false);
                lookupWorksheetButton.enable(false);
                removeRowButton.enable(false);
                worksheetItemTable.enable(false);
            }
            
            public void onFailure(Throwable error) {
                if (error instanceof ValidationErrorsList) {
                    showErrors((ValidationErrorsList)error);
                    manager = WorksheetManager.getInstance();
                } else {
                    Window.alert("save(): " + error.getMessage());
                    window.clearStatus();
                    manager = WorksheetManager.getInstance();
                }
            }
        });
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
        int                    i, j;
        ArrayList<Object>      dataList;
        ArrayList<QcLotViewDO> list;
        QcLotViewDO            qcDO = null;
        TableDataRow           qcRow;
        TestWorksheetItemDO    twiDO;
        
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
                    loadDefaultQCTemplate();
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
                        list = qcService.callList("fetchActiveByExactName", twiDO.getQcName());
                        if (list.size() == 0) {
                            if (typeRand.equals(twiDO.getTypeId())) {
                                qcErrors.put(-1, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeRandString));
                            } else if (typeLastRun.equals(twiDO.getTypeId())) {
                                qcErrors.put(-2, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastRunString));
                            } else if (typeLastSubset.equals(twiDO.getTypeId())) {
                                qcErrors.put(-3, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastSubsetString));
                            } else if (typeLastBoth.equals(twiDO.getTypeId())) {
                                qcErrors.put(-4, new FormErrorException("noMatchingActiveQc", twiDO.getQcName(), typeLastBothString));
                            } else {
                                for (j = twiDO.getPosition(); j < testWorksheetDO.getTotalCapacity(); j += testWorksheetDO.getSubsetCapacity())
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
                        qcDO = new QcLotViewDO();
                        qcDO.setQcName("Duplicate");
                    }

                    qcRow = new TableDataRow(11);
                    qcRow.cells.get(2).value = qcDO.getQcName();             // description
                    
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
        int                      i, j, posNum, randSize, numSubsets, startIndex;
        String                   accessionNumber;
        ArrayList<TableDataRow>  qcRandList, qcLastWellList, lastOf;
        TableDataRow             qcRow, qcRow1;
        TestWorksheetItemDO      twiDO;

        //
        // initialize/clear the qcItems
        //
        qcItems = new TableDataRow[testWorksheetDO.getTotalCapacity()];

        qcRandList     = new ArrayList<TableDataRow>();
        qcLastWellList = new ArrayList<TableDataRow>();
        qcLastRunList.clear();
        qcLastBothList.clear();
        numSubsets = testWorksheetDO.getTotalCapacity() / testWorksheetDO.getSubsetCapacity();
        
        //
        // Insert Fixed/Duplicate QCItems into worksheet per subset and store 
        // Random and LastOf QC Items for later.
        //
        for (i = 0; i < testWorksheetItems.size(); i++) {
            qcRow = testWorksheetItems.get(i);
            twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)testWorksheetItems.get(i).data).get(0);
            
            qcRow.key = getNextTempId();                           // fake worksheet analysis id
            
            if (typeDup.equals(twiDO.getTypeId()) || typeFixed.equals(twiDO.getTypeId()) ||
                typeFixedAlways.equals(twiDO.getTypeId())) {
                for (j = 0; j < numSubsets; j++) {
                    posNum = j * testWorksheetDO.getSubsetCapacity() + twiDO.getPosition();
                    
                    //
                    // Do NOT overwrite accession numbers on QCs pulled from other
                    // worksheets
                    //
                    accessionNumber = (String) qcRow.cells.get(1).value;
                    if (accessionNumber == null || accessionNumber.startsWith("X."))
                        qcRow.cells.get(1).value = "X."+posNum;     // qc accession #
                    qcItems[posNum-1] = (TableDataRow) qcRow.clone();
                    qcItems[posNum-1].key = qcRow.key;
                    qcItems[posNum-1].data = qcRow.data;
                }
            } else if (typeRand.equals(twiDO.getTypeId())) {
                qcRandList.add(qcRow);
            } else if (typeLastSubset.equals(twiDO.getTypeId())) {
                qcLastWellList.add(qcRow);
            } else if (typeLastRun.equals(twiDO.getTypeId())) {
                qcLastRunList.add(qcRow);
            } else if (typeLastBoth.equals(twiDO.getTypeId())) {
                qcLastBothList.add(qcRow);
            }
        }

        //
        // Insert Last of Well/Both QCItems into the worksheet per subset
        //
        if (! qcLastWellList.isEmpty())
            lastOf = qcLastWellList;
        else
            lastOf = qcLastBothList;

        startIndex = testWorksheetDO.getSubsetCapacity() - lastOf.size();
        for (i = 0; i < numSubsets; i++) {
            posNum = i * testWorksheetDO.getSubsetCapacity() + startIndex + 1;
            for (j = 0; j < lastOf.size(); j++) {
                qcRow = (TableDataRow) lastOf.get(j).clone();
                qcRow.cells.get(1).value = "X."+posNum;     // qc accession #
                qcRow.key = lastOf.get(j).key;
                qcRow.data = lastOf.get(j).data;
                qcItems[posNum-1] = qcRow;
                posNum++;
            }
        }

        //
        // Insert random QCItems into the worksheet per subset
        //
        randSize = qcRandList.size();
        for (i = 0; i < numSubsets; i++) {
            j = 0;
            while (j < randSize) {
                qcRow = (TableDataRow) qcRandList.get(j).clone();
                qcRow.key = qcRandList.get(j).key;
                qcRow.data = qcRandList.get(j).data;
                posNum = (int) (Math.random() * (testWorksheetDO.getSubsetCapacity() - 1)) + i * testWorksheetDO.getSubsetCapacity() + 1;
                if (qcItems[posNum-1] == null) {
                    if (posNum < testWorksheetDO.getTotalCapacity()) {
                        qcRow1 = qcItems[posNum];
                        if (qcRow1 != null && typeDup.equals(((TestWorksheetItemDO)((ArrayList<Object>)qcRow1.data).get(0)).getTypeId()))
                            continue;
                    }
                    qcRow.cells.get(1).value = "X."+posNum;     // qc accession #
                    qcItems[posNum-1] = qcRow;
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
            row.cells.get(0).value = i + 1;
            items.add(row);
        }
        
        //
        // Append Last of Run QCItems interweaving any fixedAlways QC items
        //
        for (k = 0; k < lastOf.size() && i < testWorksheetDO.getTotalCapacity();) {
            row = qcItems[i];
            if (row != null &&
                typeFixedAlways.equals(((TestWorksheetItemDO)((ArrayList<Object>)row.data).get(0)).getTypeId())) {
                row.cells.get(0).value = i + 1;
                items.add(row);
            } else {
                row = lastOf.get(k);
                row.cells.get(0).value = i + 1;
                row.cells.get(1).value = "X."+(i + 1);     // qc accession #
                items.add(row);
                k++;
            }
            i++;
        }
        
        //
        // Add in any remaining fixed always QCs
        //
        k = i;
        while (k < testWorksheetDO.getTotalCapacity()) {
            row = qcItems[k];
            if (row != null) {
                twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)row.data).get(0);
                if (typeFixedAlways.equals(twiDO.getTypeId())) {
                    row.cells.get(0).value = k + 1;
                    items.add(row);
                }
            }
            k++;
        }

        //
        // If last subset contains only QC items and this is not a QC only worksheet,
        // remove it
        //
        if (analysisItems.size() > 0) {
            for (i--; i > -1 && items.get(i).data instanceof ArrayList && ((ArrayList<Object>)items.get(i).data).size() == 2; i--) {
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
            qcLinkModel.add(new TableDataRow(items.get(i).key,(String)items.get(i).cells.get(1).value));
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

    private void openWorksheetAnalysisLookup() {
        ScreenWindow modal;
        
        try {
            if (wAnaLookupScreen == null) {
                wAnaLookupScreen = new WorksheetLookupScreen();
                wAnaLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
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
                                        waSelectionScreen = new WorksheetAnalysisSelectionScreen();
                                        waSelectionScreen.addActionHandler(new ActionHandler<WorksheetAnalysisSelectionScreen.Action>() {
                                            public void onAction(ActionEvent<WorksheetAnalysisSelectionScreen.Action> event) {
                                                boolean                  buildQC;
                                                int                      i, r;
                                                ArrayList<TableDataRow>  list;
                                                ArrayList<Object>        data, dataList;
                                                AnalysisViewDO           aVDO;
                                                Integer                  fromFormatId;
                                                QcLotViewDO              qcDO;
                                                TableDataRow             newRow;
                                                TestWorksheetItemDO      twiDO;
                                                WorksheetAnalysisDO      waDO;
                                                WorksheetResultManager   wrManager;      
                                                WorksheetQcResultManager wqrManager;      
    
                                                if (event.getAction() == WorksheetAnalysisSelectionScreen.Action.OK) {
                                                    data = (ArrayList<Object>)event.getData();
                                                    
                                                    list = (ArrayList<TableDataRow>)data.get(0);
                                                    fromFormatId = (Integer)data.get(1);
                                                    if (list != null) {
                                                        r = worksheetItemTable.getSelectedRow();
                                                        if (r == -1)
                                                            r = worksheetItemTable.numRows();
                                                        else
                                                            r++;
                                                        
                                                        buildQC = false;
                                                        for (i = 0; i < list.size(); i++) {
                                                            waDO = (WorksheetAnalysisDO)list.get(i).data;
                                                            if (waDO.getAnalysisId() != null) {
                                                                try {
                                                                    aVDO = analysisService.call("fetchById", waDO.getAnalysisId());
                                                                    wrManager = worksheetService.call("fetchWorksheeetResultByWorksheetAnalysisId", waDO.getId());
                                                                    
                                                                    newRow = new TableDataRow(11);
                                                                    newRow.key = getNextTempId();
                                                                    newRow.cells.get(1).value = waDO.getAccessionNumber();
                                                                    newRow.cells.get(4).value = aVDO.getTestName();
                                                                    newRow.cells.get(5).value = aVDO.getMethodName();
                                                                    newRow.cells.get(6).value = aVDO.getStatusId();
                                                                    
                                                                    dataList = new ArrayList<Object>();
                                                                    dataList.add(aVDO);
                                                                    dataList.add(wrManager);
                                                                    dataList.add(fromFormatId);
                                                                    newRow.data = dataList;
                                                                    
                                                                    analysisItems.add(newRow);
                                                                } catch (Exception anyE) {
                                                                    anyE.printStackTrace();
                                                                    Window.alert("error: " + anyE.getMessage());
                                                                }
                                                            } else if (waDO.getQcLotId() != null) {
                                                                try {
                                                                    qcDO = qcService.call("fetchLotById", waDO.getQcLotId());
                                                                    wqrManager = worksheetService.call("fetchWorksheeetQcResultByWorksheetAnalysisId", waDO.getId());
                                                                    
                                                                    twiDO = new TestWorksheetItemDO();
                                                                    twiDO.setPosition(r+1);
                                                                    twiDO.setTypeId(typeFixed);
                                                                    twiDO.setQcName(qcDO.getQcName());
                                                                    
                                                                    newRow = new TableDataRow(11);
                                                                    newRow.cells.get(1).value = waDO.getAccessionNumber();
                                                                    newRow.cells.get(2).value = qcDO.getQcName();
                                                                    
                                                                    dataList = new ArrayList<Object>();
                                                                    dataList.add(twiDO);
                                                                    dataList.add(qcDO);
                                                                    dataList.add(wqrManager);
                                                                    dataList.add(fromFormatId);
                                                                    newRow.data = dataList;
                                                                    
                                                                    testWorksheetItems.add(newRow);
                                                                    buildQC = true;
                                                                    r++;
                                                                } catch (Exception anyE) {
                                                                    anyE.printStackTrace();
                                                                    Window.alert("error: " + anyE.getMessage());
                                                                }
                                                            }
                                                        }
                                                        
                                                        if (buildQC)
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
            modal.setContent(wAnaLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void openQCLookup(String name, ArrayList<QcLotViewDO> list) {
        ScreenWindow modal;
        
        try {
            if (qcLookupScreen == null) {
                qcLookupScreen = new QcLookupScreen();
                qcLookupScreen.postConstructor();
                qcLookupScreen.addActionHandler(new ActionHandler<QcLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<QcLookupScreen.Action> event) {
                        int                    i, r;
                        ArrayList<Object>      dataList;
                        ArrayList<QcLotViewDO> list;
                        TableDataRow           qcRow;
                        QcLotViewDO            qcDO;
                        TestWorksheetItemDO    twiDO;

                        if (event.getAction() == QcLookupScreen.Action.OK) {
                            list = (ArrayList<QcLotViewDO>)event.getData();
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
                                    qcRow.cells.get(2).value = qcDO.getQcName();             // description
                                    
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
                                        twiDO.setQcName(qcDO.getQcName());
                                        
                                        qcRow = new TableDataRow(11);
                                        qcRow.cells.get(2).value = qcDO.getQcName();          // description
                                        
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

    private int getNextTempId() {
        return --tempId;
    }
    
    private void loadDefaultQCTemplate() {
        twManager = null;
        if (testWorksheetDO == null)
            testWorksheetDO = new TestWorksheetDO();
        testWorksheetDO.setFormatId(formatTotal);
        testWorksheetDO.setSubsetCapacity(500);
        testWorksheetDO.setTotalCapacity(500);
        testWorksheetItems.clear();
        buildQCWorksheet();
        isTemplateLoaded = false;
    }
}