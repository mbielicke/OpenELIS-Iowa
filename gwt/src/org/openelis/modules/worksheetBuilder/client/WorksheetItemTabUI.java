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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.Preferences;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.NotesTabUI;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet.client.WorksheetAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.modules.worksheetBuilder.client.WorksheetBuilderLookupScreenUI.Action;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public class WorksheetItemTabUI extends Screen {

    @UiTemplate("WorksheetItemTab.ui.xml")
    interface WorksheetItemTabUiBinder extends UiBinder<Widget, WorksheetItemTabUI> {
    };
    
    private static WorksheetItemTabUiBinder             uiBinder = GWT.create(WorksheetItemTabUiBinder.class);

    private boolean                                     addRowDirection, canEdit,
                                                        tableLoaded;
    private int                                         tempId, qcStartIndex;
    private String                                      typeLastBothString, typeLastRunString,
                                                        typeLastSubsetString, typeRandString;
    private ModulePermission                            userPermission;
    private WorksheetItemTabUI                          screen;
    private WorksheetManager1                           manager, displayedManager;

    @UiField
    protected Button                                    removeRowButton;
    @UiField
    protected Dropdown<Integer>                         analysisStatusId, qcLink;
    @UiField
    protected Menu                                      addRowMenu, loadTemplateMenu,
                                                        undoQcsMenu;
    @UiField
    protected MenuItem                                  insertAnalysisAbove, insertAnalysisBelow,
                                                        insertFromWorksheetAbove, 
                                                        insertFromWorksheetBelow, 
                                                        insertFromQcTableAbove,
                                                        insertFromQcTableBelow, 
                                                        undoAll, undoManual, undoTemplate;
    protected Screen                                    parentScreen;
    @UiField
    protected Table                                     worksheetItemTable;
    
    
    
    protected ArrayList<Integer>                        formatIds;
    protected ArrayList<Item<Integer>>                  qcLinkModel;
//    protected ArrayList<TableDataRow>                   qcLastRunList, qcLastBothList,
//                                                        testWorksheetItems;
    protected Confirm                                   worksheetRemoveDuplicateQCConfirm,
                                                        worksheetRemoveQCConfirm, 
                                                        worksheetRemoveLastOfQCConfirm, 
                                                        worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer, Exception>               qcErrors;
    protected HashMap<MenuItem, Integer>                templateMap;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModels;
    protected HashMap<Integer, SampleManager1>          sampleManagers;
    protected QcLookupScreen                            qcLookupScreen;
//    protected TableDataRow                              qcItems[];
    protected TestWorksheetDO                           testWorksheetDO;
    protected TestWorksheetManager                      twManager;
    protected WorksheetAnalysisSelectionScreen          waSelectionScreen;
    protected WorksheetBuilderLookupScreenUI            wbLookupScreen;
    protected WorksheetLookupScreenUI                   wLookupScreen, wAnaLookupScreen;
    
    /**
     * Flags that specifies what type of data is in each row
     */
    public enum ROW_TYPE {
        ANALYSIS, QC, OTHER_ANALYSIS, OTHER_QC
    };
    
    public WorksheetItemTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        addRowDirection = true;
        manager = null;
        displayedManager = null;
        formatIds = new ArrayList<Integer>();
        qcErrors = new HashMap<Integer, Exception>();
//        qcLastRunList = new ArrayList<TableDataRow>();
//        qcLastBothList = new ArrayList<TableDataRow>();
        qcStartIndex = 0;
        sampleManagers = new HashMap<Integer, SampleManager1>();
        tempId = -1;
        templateMap = new HashMap<MenuItem, Integer>();

        try {
            CategoryCache.getBySystemNames("analysis_status", "instrument_type",
                                           "type_of_sample", "test_worksheet_format",
                                           "test_worksheet_item_type", "unit_of_measure",
                                           "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
//    @SuppressWarnings("unchecked")
    private void initialize() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        screen = this;

        addScreenHandler(worksheetItemTable, "worksheetItemTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetItemTable.setEnabled(true);
            }
        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetItemTable.getSelectedRow() != -1 &&
                    isState(ADD, UPDATE) && canEdit)
                    removeRowButton.setEnabled(true);
//                showAnalytes();
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                //  only the QC Link and unit fields can be edited
                //
//                if (!isState(ADD, UPDATE) || !canEdit || (event.getCol() != 3 && event.getCol() != 6))
                    event.cancel();
            }
        });
        
        worksheetItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
//                int r, c;
//                Object val;
//                TableDataRow row;
//
//                r = event.getRow();
//                c = event.getCol();
//                
//                row = worksheetItemTable.getRow(r);
//                val = worksheetItemTable.getObject(r,c);
//
//                switch (c) {
//                    case 3:
//                        ((RowItem)row.data).worksheetAnalysis.setWorksheetAnalysisId((Integer)val);
//                        break;
//                    case 6:
//                        ((RowItem)row.data).analysis.setUnitOfMeasureId((Integer)val);
//                        break;
//                }
            }
        });

//        worksheetItemTable.addSortHandler(new SortHandler() {
//            public void onSort(SortEvent event) {
//                int              i;
//				ColumnComparator comparator;
//                
//                comparator = new ColumnComparator(event.getIndex(), event.getDirection());
//                Collections.sort(worksheetItemTable.getData(), comparator);
//                for (i = 0; i < worksheetItemTable.numRows(); i++)
//                    worksheetItemTable.setCell(i, 0, i+1);
//                worksheetItemTable.refresh();
//            }
//        });
        
//        unitOfMeasureId.addGetMatchesHandler(new GetMatchesHandler() {
//            public void onGetMatches(GetMatchesEvent event) {
//                ArrayList<QueryData> fields;
//                ArrayList<Item<Integer>> model;
//                ArrayList<IdNameVO> list;
//                Query query;
//                QueryData testIdField, typeOfSampleIdField, unitOfMeasureField;
//                WorksheetBuilderVO data;
//
//                try {
//                    query = new Query();
//                    fields = new ArrayList<QueryData>();
//                    model = new ArrayList<Item<Integer>>();
//    
//                    data = (WorksheetBuilderVO) worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow())
//                                                                  .getData();
//                    
//                    testIdField = new QueryData();
//                    testIdField.setType(QueryData.Type.INTEGER);
//                    testIdField.setQuery(String.valueOf(data.getTestId()));
//                    fields.add(testIdField);
//    
//                    typeOfSampleIdField = new QueryData();
//                    typeOfSampleIdField.setType(QueryData.Type.INTEGER);
//                    typeOfSampleIdField.setQuery(String.valueOf(data.getTypeOfSampleId()));
//                    fields.add(typeOfSampleIdField);
//    
//                    unitOfMeasureField = new QueryData();
//                    unitOfMeasureField.setType(QueryData.Type.INTEGER);
//                    unitOfMeasureField.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch())+"%");
//                    fields.add(unitOfMeasureField);
//    
//                    query.setFields(fields);
//    
//                    list = TestService.get().fetchUnitsForWorksheetAutocomplete(query);
//                    for (IdNameVO unitVO : list)
//                        model.add(new Item<Integer>(unitVO.getId(), unitVO.getName()));
//                    unitOfMeasureId.showAutoMatches(model);
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                }
//            }
//        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addRowMenu.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        insertAnalysisAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertAnalysisAbove();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertAnalysisAbove.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });
                
        insertAnalysisBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertAnalysisBelow();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertAnalysisBelow.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });
                
        insertFromWorksheetAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromWorksheetAbove();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertFromWorksheetAbove.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });
                
        insertFromWorksheetBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromWorksheetBelow();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertFromWorksheetBelow.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });
                
        insertFromQcTableAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromQcTableAbove();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertFromQcTableAbove.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });
                
        insertFromQcTableBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromQcTableBelow();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                insertFromQcTableBelow.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                loadTemplateMenu.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                undoQcsMenu.setEnabled(false);
            }
        });

        undoAll.addCommand(new Command() {
            @Override
            public void execute() {
                undoAllQcs();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                undoAll.setEnabled(false);
            }
        });

        undoManual.addCommand(new Command() {
            @Override
            public void execute() {
                undoManualQcs();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                undoManual.setEnabled(false);
            }
        });

        undoTemplate.addCommand(new Command() {
            @Override
            public void execute() {
                undoTemplateQcs();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                undoTemplate.setEnabled(false);
            }
        });

        try {            
            typeLastSubsetString = DictionaryCache.getById(Constants.dictionary().POS_LAST_OF_SUBSET).getEntry();
            typeLastRunString = DictionaryCache.getById(Constants.dictionary().POS_LAST_OF_RUN).getEntry();
            typeLastBothString = DictionaryCache.getById(Constants.dictionary().POS_LAST_OF_SUBSET_AND_RUN).getEntry();
            typeRandString = DictionaryCache.getById(Constants.dictionary().POS_RANDOM).getEntry();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        //
        // load empty QC Link dropdown model
        //
        qcLinkModel = new ArrayList<Item<Integer>>();
        qcLinkModel.add(new Item<Integer>(null, ""));
        qcLink.setModel(qcLinkModel);

        //
        // load analysis status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        analysisStatusId.setModel(model);
    }
    
    public void setData(WorksheetManager1 manager) {
        if (!DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
            evaluateEdit();
        }
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null && isState(ADD, UPDATE))
            canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                             .getStatusId());
    }

    private ArrayList<Item<Object>> getTableModel() {
        int i, j, pos;
        AnalysisViewDO aVDO = null;
        ArrayList<Item<Object>> model;
        DictionaryDO dictDO;
        Integer accessionNumber;
        QcLotViewDO qcLotVDO;
        RowItem data;
        SampleDO sDO;
        SampleManager1 sManager;
        SampleOrganizationViewDO soVDO;
        SamplePrivateWellViewDO spwVDO;
        String description, location;
        Item<Object> row;
        TestManager tManager = null;
        WorksheetAnalysisDO waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO wiDO;
        
        qcLinkModel.clear();
        qcLinkModel.add(new Item<Integer>(null, ""));

        model = new ArrayList<Item<Object>>();
        if (manager != null) {
            try {
                pos = 1;
                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = (WorksheetItemDO)manager.item.get(i);
    
                    row = new Item<Object>(12);
                    row.setCell(0, pos++);
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        qcLinkModel.add(new Item<Integer>(waDO.getId(), waDO.getAccessionNumber()));
    
                        if (j > 0) {
                            row = new Item<Object>(12);
                            row.setCell(0, pos++);
                        }
                        row.setKey(i + "_" + j);
                        row.setCell(1, waDO.getAccessionNumber());
                        
                        if (waDO.getAnalysisId() != null) {
                            if (waDO.getAccessionNumber().startsWith("D"))
                                accessionNumber = Integer.valueOf(waDO.getAccessionNumber().substring(1));
                            else
                                accessionNumber = Integer.valueOf(waDO.getAccessionNumber());
                            sManager = sampleManagers.get(accessionNumber);
                            sDO = sManager.getSample();

                            aVDO = (AnalysisViewDO) sManager.getObject("A:"+waDO.getAnalysisId());
                            if (aVDO == null)
                                throw new Exception("Cannot find analysis linked to worksheet analysis");
                            tManager = TestService.get().fetchById(aVDO.getTestId());
    
                            description = "";
                            if (Constants.domain().ENVIRONMENTAL.equals(sManager.getSample()
                                                                                .getDomain())) {
                                location = sManager.getSampleEnvironmental().getLocation();
                                if (location != null && location.length() > 0)
                                    description = "[loc]" + location;
                                soVDO = sManager.organization.getByType(Constants.dictionary().ORG_REPORT_TO).get(0);
                                if (soVDO != null && soVDO.getOrganizationName() != null &&
                                    soVDO.getOrganizationName().length() > 0) {
                                    if (description.length() > 0)
                                        description += " ";
                                    description += "[rpt]" + soVDO.getOrganizationName();
                                }
                            } else if (Constants.domain().SDWIS.equals(sManager.getSample()
                                                                                      .getDomain())) {
                                location = sManager.getSampleSDWIS().getLocation();
                                if (location != null && location.length() > 0)
                                    description = "[loc]" + location;
                                soVDO = sManager.organization.getByType(Constants.dictionary().ORG_REPORT_TO).get(0);
                                if (soVDO != null && soVDO.getOrganizationName() != null &&
                                    soVDO.getOrganizationName().length() > 0) {
                                    if (description.length() > 0)
                                        description += " ";
                                    description += "[rpt]" + soVDO.getOrganizationName();
                                }
                            } else if (Constants.domain().PRIVATEWELL.equals(sManager.getSample()
                                                                                     .getDomain())) {
                                spwVDO = sManager.getSamplePrivateWell();
                                if (spwVDO.getLocation() != null &&
                                    spwVDO.getLocation().length() > 0)
                                    description = "[loc]" + spwVDO.getLocation();
                                if (spwVDO.getOrganizationId() != null &&
                                    spwVDO.getOrganization().getName() != null &&
                                    spwVDO.getOrganization().getName().length() > 0) {
                                    if (description.length() > 0)
                                        description += " ";
                                    description += "[rpt]" +
                                                   spwVDO.getOrganization().getName();
                                } else if (spwVDO.getReportToName() != null &&
                                           spwVDO.getReportToName().length() > 0) {
                                    if (description.length() > 0)
                                        description += " ";
                                    description += "[rpt]" + spwVDO.getReportToName();
                                }
                            }
                            row.setCell(2, description);
                            row.setCell(3, waDO.getWorksheetAnalysisId());
                            row.setCell(4, aVDO.getTestName());
                            row.setCell(5, aVDO.getMethodName());
                            
//                            dictDO = DictionaryCache.getById(aVDO.getUnitOfMeasureId());
//                            row.setCell(6, new Item<Integer>(dictDO.getId(), dictDO.getEntry()));
                            
                            row.setCell(7, aVDO.getStatusId());
                            row.setCell(8, sDO.getCollectionDate());
                            row.setCell(9, sDO.getReceivedDate());
                            if (Constants.domain().ENVIRONMENTAL.equals(sManager.getSample()
                                                                                .getDomain()))
                                row.setCell(10, DataBaseUtil.getDueDays(sDO.getReceivedDate(),
                                                                        sManager.getSampleEnvironmental()
                                                                                .getPriority()));
                            row.setCell(11, DataBaseUtil.getExpireDate(sDO.getCollectionDate(),
                                                                       sDO.getCollectionTime(),
                                                                       tManager.getTest()
                                                                               .getTimeHolding()));
                            
                            data = new RowItem();
                            data.worksheetItem = wiDO;
                            data.worksheetAnalysis = waDO;
                            data.analysis = aVDO;
                            row.setData(data);
                            model.add(row);
                        } else if (waDO.getQcLotId() != null) {
                            qcLotVDO = QcService.get().fetchLotById(waDO.getQcLotId());
    
                            row.setCell(2, qcLotVDO.getQcName());
                            row.setCell(3, waDO.getWorksheetAnalysisId());
    
                            data = new RowItem();
                            data.worksheetItem = wiDO;
                            data.worksheetAnalysis = waDO;
                            data.qcLotVDO = qcLotVDO;
                            row.setData(data); 
                            model.add(row);
                        }
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }        

        /*
         * Reload the model for the QC Link column
         */
        qcLink.setModel(qcLinkModel);

        tableLoaded = true;
        return model;
    }

    private void insertAnalysisAbove() {
        addRowDirection = false;
        openLookupWindow();
    }
    
    private void insertAnalysisBelow() {
        addRowDirection = true;
        openLookupWindow();
    }
    
    private void openLookupWindow() {
        org.openelis.ui.widget.Window win;
        
        win = new org.openelis.ui.widget.Window(false);
        win.setName(Messages.get().worksheetBuilderLookup());
        if (wbLookupScreen == null) {
            try {
                wbLookupScreen = new WorksheetBuilderLookupScreenUI();
                wbLookupScreen.addActionHandler(new org.openelis.ui.event.ActionHandler<WorksheetBuilderLookupScreenUI.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(org.openelis.ui.event.ActionEvent<Action> event) {
                        int index;
                        ArrayList<Item<Integer>> list;
                        DictionaryDO unitDO;
                        Item<Integer> newRow;
                        WorksheetBuilderVO data;

                        if (event.getAction() == WorksheetBuilderLookupScreenUI.Action.ADD) {
                            if (worksheetItemTable.getSelectedRow() != -1) {
                                index = worksheetItemTable.getSelectedRow();
                                if (addRowDirection)
                                    index++;
                            } else {
                                index = worksheetItemTable.getRowCount();
                            }
                            
                            list = (ArrayList<Item<Integer>>)event.getData();
                            if (list != null && list.size() > 0) {
                                for (Item<Integer> row : list) {
                                    newRow = new Item<Integer>(13);
                                    data = (WorksheetBuilderVO)row.getData();

                                    //
                                    // set the format if it has not already been set
                                    //
//                                    if (formatId.getValue() == null)
//                                        formatId.setValue(data.getWorksheetFormatId());
                                    
                                    //
                                    // add template to list
                                    //
                                    if (!templateMap.containsValue(data.getTestId())) {
                                        final MenuItem item = new MenuItem("AddRowButtonImage", data.getTestName()+", "+data.getMethodName(), null);
                                        item.setVisible(true);
                                        item.setEnabled(true);
                                        item.addHandler(new ClickHandler() {
                                            public void onClick(ClickEvent event) {
                                                loadTemplate(templateMap.get(item));
                                            }
                                        }, ClickEvent.getType());
                                        loadTemplateMenu.add(item);
                                        templateMap.put(item, data.getTestId());
                                    }

                                    newRow.setKey(getNextTempId());                                       // fake worksheet analysis id
                                    newRow.setCell(1, row.getCells().get(0)); // accession #
                                    newRow.setCell(2, row.getCells().get(1));                 // description
                                    newRow.setCell(4, row.getCells().get(2));                 // test name
                                    newRow.setCell(5, row.getCells().get(3));                 // method name
                                    
                                    if (row.getCells().get(5) != null) {
                                        try {                                                               // unit
                                            unitDO = DictionaryCache.getById((Integer)row.getCells().get(5));
                                            newRow.setCell(6, new Item<Integer>(unitDO.getId(), unitDO.getEntry()));
                                        } catch (Exception anyE) {
                                            anyE.printStackTrace();
                                            Window.alert("error: " + anyE.getMessage());
                                        }
                                    }
                                    
                                    newRow.setCell(7, row.getCells().get(6));                 // status
                                    newRow.setCell(8, row.getCells().get(7));                 // collection date
                                    newRow.setCell(9, row.getCells().get(8));                 // received date and time
                                    newRow.setCell(10, row.getCells().get(9));                // due days
                                    newRow.setCell(11, row.getCells().get(10));               // expire date and time
                                    newRow.setCell(12, ROW_TYPE.ANALYSIS);
                                    newRow.setData(data);
                                    worksheetItemTable.addRowAt(index++, newRow);
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        wbLookupScreen.setWindow(win);
        win.setContent(wbLookupScreen);
        OpenELIS.getBrowser().addWindow(win, "wbLookupScreen");
    }
    
    private void loadTemplate(Integer testId) {
/*
        int                    i, j;
        ArrayList<Object>      dataList;
        ArrayList<QcLotViewDO> list;
        Preferences            prefs;
        QcLotViewDO            qcLotVDO = null;
        TableDataRow           qcRow;
        TestWorksheetItemDO    twiDO;
        
        try {
            twManager = TestWorksheetManager.fetchByTestId(testId);
            //
            // If there is no worksheet definition, an empty manager is returned
            //
            testWorksheetDO = twManager.getWorksheet();
            if (testWorksheetDO.getId() == null) {
                //
                // If there is no worksheet definition for the test, load
                // default template
                //
                manager.getWorksheet().setFormatId(Constants.dictionary().WF_TOTAL);
                manager.getWorksheet().setSubsetCapacity(500);
                return;
            } else {
                manager.getWorksheet().setFormatId(testWorksheetDO.getFormatId());
            }
            DataChangeEvent.fire(this, formatId);

            //
            // Only clear the error list if this is our first time through
            //
            if (qcStartIndex == 0)
                qcErrors = new HashMap<Integer, Exception>(testWorksheetDO.getTotalCapacity());
            
            i = qcStartIndex;
            qcStartIndex = 0;
            prefs = Preferences.userRoot();
            for (; i < twManager.itemCount(); i++) {
                twiDO = twManager.getItemAt(i);
                try {
                    if (!Constants.dictionary().POS_DUPLICATE.equals(twiDO.getTypeId())) {
                        list = QcService.get().fetchActiveByExactName(twiDO.getQcName());
                        if (list.size() == 0) {
                            if (Constants.dictionary().POS_RANDOM.equals(twiDO.getTypeId())) {
                                qcErrors.put(-1, new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), typeRandString)));
                            } else if (Constants.dictionary().POS_LAST_OF_RUN.equals(twiDO.getTypeId())) {
                                qcErrors.put(-2, new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), typeLastRunString)));
                            } else if (Constants.dictionary().POS_LAST_OF_SUBSET.equals(twiDO.getTypeId())) {
                                qcErrors.put(-3, new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), typeLastSubsetString)));
                            } else if (Constants.dictionary().POS_LAST_OF_SUBSET_AND_RUN.equals(twiDO.getTypeId())) {
                                qcErrors.put(-4, new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), typeLastBothString)));
                            } else {
                                for (j = twiDO.getPosition(); j < testWorksheetDO.getTotalCapacity(); j += testWorksheetDO.getSubsetCapacity())
                                    qcErrors.put(j, new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), String.valueOf(j))));
                            }
                            continue;
                        } else if (list.size() > 1) {
                            //
                            // look for a single qc in the list that matches the user's
                            // location. if none or multiple match or the user has 
                            // not specified a preference, show the lookup screen
                            //
                            qcLotVDO = null;
                            for (QcLotViewDO tempQcLot : list) {
                                if (tempQcLot.getLocationId() != null && tempQcLot.getLocationId().equals(prefs.getInt("location", -1))) {
                                    if (qcLotVDO == null) {
                                        qcLotVDO = tempQcLot;
                                    } else {
                                        qcLotVDO = null;
                                        break;
                                    }
                                }
                            }
                            
                            if (qcLotVDO == null) {
                                Window.alert(new FormErrorException(Messages.get().multiMatchingActiveQc(twiDO.getQcName(), String.valueOf(i+1))).getMessage());
                                openQCLookup(twiDO.getQcName(), list);
                                qcStartIndex = i + 1;
                                break;
                            }
                        } else {
                            qcLotVDO = list.get(0);
                        }
                    } else {
                        qcLotVDO = new QcLotViewDO();
                        qcLotVDO.setQcName("Duplicate");
                    }

                    qcRow = new TableDataRow(11);
                    qcRow.cells.get(2).value = qcLotVDO.getQcName();             // description
                    
                    dataList = new ArrayList<Object>();
                    dataList.add(twiDO);
                    dataList.add(qcLotVDO);
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
*/
    }
    
    @SuppressWarnings("unchecked")
    private void buildQCWorksheet() {
/*
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
            twiDO = (TestWorksheetItemDO) ((ArrayList<Object>)qcRow.data).get(0);
            
            qcRow.key = getNextTempId();                           // fake worksheet analysis id
            
            if (Constants.dictionary().POS_DUPLICATE.equals(twiDO.getTypeId()) || 
                Constants.dictionary().POS_FIXED.equals(twiDO.getTypeId()) ||
                Constants.dictionary().POS_FIXED_ALWAYS.equals(twiDO.getTypeId())) {
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
            } else if (Constants.dictionary().POS_RANDOM.equals(twiDO.getTypeId())) {
                qcRandList.add(qcRow);
            } else if (Constants.dictionary().POS_LAST_OF_SUBSET.equals(twiDO.getTypeId())) {
                qcLastWellList.add(qcRow);
            } else if (Constants.dictionary().POS_LAST_OF_RUN.equals(twiDO.getTypeId())) {
                qcLastRunList.add(qcRow);
            } else if (Constants.dictionary().POS_LAST_OF_SUBSET_AND_RUN.equals(twiDO.getTypeId())) {
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
                        if (qcRow1 != null && Constants.dictionary().POS_DUPLICATE.equals(((TestWorksheetItemDO)((ArrayList<Object>)qcRow1.data).get(0)).getTypeId()))
                            continue;
                    }
                    qcRow.cells.get(1).value = "X."+posNum;     // qc accession #
                    qcItems[posNum-1] = qcRow;
                    j++;
                }
            }
        }
*/
    }
    
    @SuppressWarnings("unchecked")
    private void mergeAnalysesAndQCs() {
/*        
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
            Window.alert(Messages.get().worksheetIsFull"));

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
*/
    }
    
    private void insertFromWorksheetAbove() {
        addRowDirection = false;
        openWorksheetAnalysisLookup();
    }
    
    private void insertFromWorksheetBelow() {
        addRowDirection = true;
        openWorksheetAnalysisLookup();
    }
    
    private void openWorksheetAnalysisLookup() {
        WindowInt modal;
        
        try {
            if (wAnaLookupScreen == null) {
                wAnaLookupScreen = new WorksheetLookupScreenUI();
                wAnaLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreenUI.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreenUI.Action> event) {
                        Item<Integer> row;
                        WindowInt modal2;
                        WorksheetViewDO wVDO;

                        if (event.getAction() == WorksheetLookupScreenUI.Action.SELECT) {
/*
                            row = (Item<Integer>)event.getData();
                            if (row != null) {
                                wVDO = (WorksheetViewDO)row.getData();
                                try {
                                    if (waSelectionScreen == null) {
                                        waSelectionScreen = new WorksheetAnalysisSelectionScreen();
                                        waSelectionScreen.addActionHandler(new ActionHandler<WorksheetAnalysisSelectionScreen.Action>() {
                                            public void onAction(ActionEvent<WorksheetAnalysisSelectionScreen.Action> event) {
                                                int                      i, r;
                                                ArrayList<TableDataRow>  list;
                                                ArrayList<Object>        data, dataList;
                                                AnalysisViewDO           aVDO;
                                                Integer                  fromFormatId;
                                                QcLotViewDO              qcLotVDO;
                                                TableDataRow             newRow;
                                                WorksheetAnalysisDO      waDO;
                                                WorksheetResultManager   wrManager;      
                                                WorksheetQcResultManager wqrManager;      
    
                                                if (event.getAction() == WorksheetAnalysisSelectionScreen.Action.OK) {
                                                    data = (ArrayList<Object>)event.getData();
                                                    
                                                    list = (ArrayList<TableDataRow>)data.get(0);
                                                    fromFormatId = (Integer)data.get(1);
                                                    if (list != null && list.size() > 0) {
                                                        r = worksheetItemTable.getSelectedRow();
                                                        if (r == -1)
                                                            r = worksheetItemTable.numRows();
                                                        else if (addRowDirection)
                                                            r++;
                                                        
                                                        for (TableDataRow row : list) {
                                                            waDO = (WorksheetAnalysisDO)row.data;
                                                            newRow = new TableDataRow(11);
                                                            try {
                                                                if (waDO.getAnalysisId() != null) {
                                                                    aVDO = AnalysisService.get().fetchById(waDO.getAnalysisId());
                                                                    wrManager = WorksheetService.get().fetchWorksheeetResultByWorksheetAnalysisId(waDO.getId());
                                                                    
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
                                                                } else if (waDO.getQcLotId() != null) {
                                                                    qcLotVDO = QcService.get().fetchLotById(waDO.getQcLotId());
                                                                    wqrManager = WorksheetService.get().fetchWorksheeetQcResultByWorksheetAnalysisId(waDO.getId());
                                                                    
                                                                    newRow.cells.get(1).value = waDO.getAccessionNumber();
                                                                    newRow.cells.get(2).value = qcLotVDO.getQcName();
                                                                    
                                                                    dataList = new ArrayList<Object>();
                                                                    dataList.add(qcLotVDO);
                                                                    dataList.add(wqrManager);
                                                                    dataList.add(fromFormatId);
                                                                    newRow.data = dataList;
                                                                }
                                                                worksheetItemTable.addRow(r, newRow);
                                                                r++;
                                                            } catch (Exception anyE) {
                                                                anyE.printStackTrace();
                                                                Window.alert("error: " + anyE.getMessage());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    
                                    modal2 = new ModalWindow();
                                    waSelectionScreen.setWorksheetId(wVDO.getId());
                                    waSelectionScreen.draw();
                                    modal2.setContent(waSelectionScreen);
                                    modal2.setName(Messages.get().worksheetNumber()+wVDO.getId().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Window.alert("error: " + e.getMessage());
                                    return;
                                }
                            }
*/
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(Messages.get().worksheetLookup());
            modal.setContent(wAnaLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void insertFromQcTableAbove() {
        addRowDirection = false;
//        openQCLookup();
    }
    
    private void insertFromQcTableBelow() {
        addRowDirection = true;
//        openQCLookup();
    }
    
    private void openQCLookup(String name, ArrayList<QcLotViewDO> list) {
/*
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
                        QcLotViewDO            qcLotVDO;
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

                                    qcLotVDO = list.get(0);

                                    qcRow = new TableDataRow(11);
                                    qcRow.cells.get(2).value = qcLotVDO.getQcName();             // description
                                    
                                    dataList = new ArrayList<Object>();
                                    dataList.add(twiDO);
                                    dataList.add(qcLotVDO);
                                    qcRow.data = dataList;
                                    
                                    testWorksheetItems.add(qcRow);
                                    
//                                    loadQCTemplate();
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
                                        qcLotVDO  = list.get(i);

                                        twiDO = new TestWorksheetItemDO();
                                        twiDO.setPosition(r+1);
                                        twiDO.setTypeId(Constants.dictionary().POS_FIXED);
                                        twiDO.setQcName(qcLotVDO.getQcName());
                                        
                                        qcRow = new TableDataRow(11);
                                        qcRow.cells.get(2).value = qcLotVDO.getQcName();             // description
                                        
                                        dataList = new ArrayList<Object>();
                                        dataList.add(twiDO);
                                        dataList.add(qcLotVDO);
                                        qcRow.data = dataList;
                                        
                                        testWorksheetItems.add(qcRow);
                                        r++;
                                    }

                                    buildQCWorksheet();
                                }
                                
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
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(Messages.get().QCLookup());
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
*/
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
/*
        int i, rowIndex;
        Integer tempKey;
        Item<String> dataRow, tempRow;
        
        worksheetItemTable.finishEditing();
        rowIndex = worksheetItemTable.getSelectedRow();
        if (rowIndex > -1 && worksheetItemTable.getRowCount() > 0) {
            dataRow = worksheetItemTable.getRowAt(rowIndex);
            
            for (i = 0; i < worksheetItemTable.getRowCount(); i++) {
                tempRow = worksheetItemTable.getRowAt(i);
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
                        Window.alert(Messages.get().oneOrMoreQcLinkOnRemove());
                        return;
                    }
                }
            }
            
            worksheetItemTable.deleteRow(rowIndex);
        }
*/
    }
    
    private void undoAllQcs() {
        
    }

    private void undoManualQcs() {
        
    }

    private void undoTemplateQcs() {
        
    }

    private int getNextTempId() {
        return --tempId;
    }
    
    private void loadDefaultQCTemplate() {
        twManager = null;
        if (testWorksheetDO == null)
            testWorksheetDO = new TestWorksheetDO();
        testWorksheetDO.setFormatId(Constants.dictionary().WF_TOTAL);
        testWorksheetDO.setSubsetCapacity(500);
        testWorksheetDO.setTotalCapacity(500);
//        testWorksheetItems.clear();
        buildQCWorksheet();
    }
    
    private class RowItem {
        AnalysisViewDO analysis;
        QcLotViewDO qcLotVDO;
        WorksheetAnalysisDO worksheetAnalysis;
        WorksheetItemDO worksheetItem;
    }
}