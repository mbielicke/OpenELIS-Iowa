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
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.IdNameVO;
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
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.CalendarService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.MenuPanel;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
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
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet.client.WorksheetAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetService;
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
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

public class WorksheetBuilderScreen extends Screen {

    /**
     * Flags that specifies what type of data is in each row
     */
    public enum ROW_TYPE {
        ANALYSIS, QC, OTHER_ANALYSIS, OTHER_QC
    };

    private boolean                                    addRowDirection;
    private int                                        tempId, qcStartIndex;
    private String                                     typeLastBothString, typeLastRunString,
                                                       typeLastSubsetString, typeRandString;
    private ModulePermission                           userPermission;
    private WorksheetManager                           manager;

    private AppButton                                  queryButton, previousButton,
                                                       nextButton, addButton, updateButton,
                                                       commitButton, abortButton,
                                                       lookupWorksheetButton, removeRowButton;
    private Dropdown<Integer>                          formatId, statusId;
    private MenuPanel                                  addRowMenu, loadTemplateMenu, 
                                                       undoQcsMenu;
    private MenuItem                                   insertAnalysisAbove, insertAnalysisBelow,
                                                       insertFromWorksheetAbove,
                                                       insertFromWorksheetBelow,
                                                       insertFromQcTableAbove, insertFromQcTableBelow,
                                                       undoAllMenu, undoManualMenu,
                                                       undoTemplateMenu, worksheetHistory;
    private WorksheetBuilderScreen                     screen;
    private ButtonGroup                                atoz;
    private ScreenNavigator                            nav;

    protected ArrayList<Integer>                       formatIds;
    protected ArrayList<TableDataRow>                  qcLastRunList, qcLastBothList,
                                                       qcLinkModel, testWorksheetItems;
    protected AutoComplete<Integer>                    instrumentName, systemUserId,
                                                       unitOfMeasureId;
    protected CalendarLookUp                           createdDate;
    protected Confirm                                  worksheetRemoveDuplicateQCConfirm,
                                                       worksheetRemoveQCConfirm,
                                                       worksheetRemoveLastOfQCConfirm,
                                                       worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer, Exception>              qcErrors;
    protected HashMap<MenuItem, Integer>               templateMap;
    protected HashMap<String, ArrayList<TableDataRow>> unitModels;
    protected HashMap<Integer, SampleManager1>         sampleManagers;
    protected QcLookupScreen                           qcLookupScreen;
    protected TableDataRow                             qcItems[];
    protected TableWidget                              worksheetItemTable, analyteTable;
    protected TextBox                                  description, relatedWorksheetId,
                                                       worksheetId;
    protected TestWorksheetDO                          testWorksheetDO;
    protected TestWorksheetManager                     twManager;
    protected WorksheetAnalysisSelectionScreen         waSelectionScreen;
    protected WorksheetBuilderLookupScreen             wbLookupScreen;
    protected WorksheetLookupScreen                    wLookupScreen, wAnaLookupScreen;
    
    public WorksheetBuilderScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetBuilderDef.class));

        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Builder Screen"));

        addRowDirection = true;
        manager = WorksheetManager.getInstance();
        formatIds = new ArrayList<Integer>();
        qcErrors = new HashMap<Integer, Exception>();
        qcLastRunList = new ArrayList<TableDataRow>();
        qcLastBothList = new ArrayList<TableDataRow>();
        qcStartIndex = 0;
        sampleManagers = new HashMap<Integer, SampleManager1>();
        tempId = -1;
        templateMap = new HashMap<MenuItem, Integer>();

        try {
            CategoryCache.getBySystemNames("analysis_status",
                                           "instrument_type",
                                           "type_of_sample", 
                                           "test_worksheet_format",
                                           "test_worksheet_item_type",
                                           "unit_of_measure",
                                           "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        screen = this;
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY)
                                             .contains(event.getState()));
            }
        });

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        worksheetHistory = (MenuItem)def.getWidget("worksheetHistory");
        addScreenHandler(worksheetHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                worksheetHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        worksheetId = (TextBox)def.getWidget(WorksheetBuilderMeta.getWorksheetId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setFieldValue(manager.getWorksheet().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                worksheetId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(WorksheetBuilderMeta.getWorksheetStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getWorksheet().getStatusId());
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        systemUserId = (AutoComplete)def.getWidget(WorksheetBuilderMeta.getWorksheetSystemUserId());
        addScreenHandler(systemUserId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                systemUserId.setSelection(manager.getWorksheet().getSystemUserId(),
                                          manager.getWorksheet().getSystemUser());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setSystemUserId(event.getValue());
                manager.getWorksheet().setSystemUser(systemUserId.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                systemUserId.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<TableDataRow> model;
                
                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (SystemUserVO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        formatId = (Dropdown<Integer>)def.getWidget(WorksheetBuilderMeta.getWorksheetFormatId());
        addScreenHandler(formatId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                formatId.setSelection(manager.getWorksheet().getFormatId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setFormatId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                formatId.enable(EnumSet.of(State.ADD, State.QUERY).contains(event.getState()));
                formatId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        relatedWorksheetId = (TextBox)def.getWidget(WorksheetBuilderMeta.getWorksheetRelatedWorksheetId());
        addScreenHandler(relatedWorksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setFieldValue(manager.getWorksheet().getRelatedWorksheetId());
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
                lookupWorksheetButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
            }
        });

        instrumentName = (AutoComplete)def.getWidget(WorksheetBuilderMeta.getInstrumentName());
        addScreenHandler(instrumentName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                instrumentName.setSelection(manager.getWorksheet().getInstrumentId(),
                                          manager.getWorksheet().getInstrumentName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setInstrumentId(event.getValue());
                manager.getWorksheet().setInstrumentName(instrumentName.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                instrumentName.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                instrumentName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        instrumentName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow>     model;
                ArrayList<InstrumentViewDO> matches;
                TableDataRow                row;

                try {
                    model = new ArrayList<TableDataRow>();
                    matches = InstrumentService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (InstrumentViewDO iVDO : matches) {
                        row = new TableDataRow(5);
                        row.key = iVDO.getId();
                        row.cells.get(0).value = iVDO.getName();
                        row.cells.get(1).value = iVDO.getDescription();
                        row.cells.get(2).value = DictionaryCache.getById(iVDO.getTypeId()).getEntry();
                        row.cells.get(3).value = iVDO.getLocation();
                        row.data = iVDO;
                        
                        model.add(row);
                    }
                    
                    instrumentName.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        createdDate = (CalendarLookUp)def.getWidget(WorksheetBuilderMeta.getWorksheetCreatedDate());
        addScreenHandler(createdDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                createdDate.setValue(manager.getWorksheet().getCreatedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getWorksheet().setCreatedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                createdDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                createdDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(WorksheetBuilderMeta.getWorksheetDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getWorksheet().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getWorksheet().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);

            }
        });

        worksheetItemTable = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(worksheetItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetItemTable.enable(true);
            }
        });

        unitOfMeasureId = (AutoComplete<Integer>)worksheetItemTable.getColumnWidget(WorksheetBuilderMeta.getAnalysisUnitOfMeasureId());
        unitOfMeasureId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<QueryData> fields;
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;
                Query query;
                QueryData testIdField, typeOfSampleIdField, unitOfMeasureField;
                WorksheetBuilderVO data;

                query = new Query();
                fields = new ArrayList<QueryData>();
                model = new ArrayList<TableDataRow>();

                data = (WorksheetBuilderVO) worksheetItemTable.getSelection().data;
                
                testIdField = new QueryData();
                testIdField.setType(QueryData.Type.INTEGER);
                testIdField.setQuery(String.valueOf(data.getTestId()));
                fields.add(testIdField);

                typeOfSampleIdField = new QueryData();
                typeOfSampleIdField.setType(QueryData.Type.INTEGER);
                typeOfSampleIdField.setQuery(String.valueOf(data.getTypeOfSampleId()));
                fields.add(typeOfSampleIdField);

                unitOfMeasureField = new QueryData();
                unitOfMeasureField.setType(QueryData.Type.INTEGER);
                unitOfMeasureField.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch())+"%");
                fields.add(unitOfMeasureField);

                query.setFields(fields);
                try {
                    list = TestService.get().fetchUnitsForWorksheetAutocomplete(query);
                    for (IdNameVO unitVO : list)
                        model.add(new TableDataRow(unitVO.getId(),unitVO.getName()));
                    unitOfMeasureId.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetItemTable.getSelectedRow() != -1 &&
                    (state == State.ADD || state == State.UPDATE))
                    removeRowButton.enable(true);
                showAnalytes();
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
                //  only the QC Link and unit fields can be edited
                //
                if ((state != State.ADD && state != State.UPDATE) ||
                    (event.getCol() != 3 && event.getCol() != 6))
                    event.cancel();
            }
        });
        
        worksheetItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                
                row = worksheetItemTable.getRow(r);
                val = worksheetItemTable.getObject(r,c);

                switch (c) {
                    case 3:
                        ((RowItem)row.data).worksheetAnalysis.setWorksheetAnalysisId((Integer)val);
                        break;
                    case 6:
                        ((RowItem)row.data).analysis.setUnitOfMeasureId((Integer)val);
                        break;
                }
            }
        });

        worksheetItemTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
                int              i;
				ColumnComparator comparator;
                
                comparator = new ColumnComparator(event.getIndex(), event.getDirection());
                Collections.sort(worksheetItemTable.getData(), comparator);
                for (i = 0; i < worksheetItemTable.numRows(); i++)
                    worksheetItemTable.setCell(i, 0, i+1);
                worksheetItemTable.refresh();
            }
        });
        
        addRowMenu = (MenuPanel)def.getWidget("addRowMenu");
        addScreenHandler(addRowMenu, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                enable = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                addRowMenu.menuItems.get(0).enable(enable);
                ((AppButton)addRowMenu.menuItems.get(0).getWidget()).enable(enable);
            }
        });

        insertAnalysisAbove = (MenuItem)def.getWidget("insertAnalysisAbove");
        addScreenHandler(insertAnalysisAbove, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = false;
                openLookupWindow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertAnalysisAbove.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });
                
        insertAnalysisBelow = (MenuItem)def.getWidget("insertAnalysisBelow");
        addScreenHandler(insertAnalysisBelow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = true;
                openLookupWindow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertAnalysisBelow.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });
                
        insertFromWorksheetAbove = (MenuItem)def.getWidget("insertFromWorksheetAbove");
        addScreenHandler(insertFromWorksheetAbove, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = false;
                openWorksheetAnalysisLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertFromWorksheetAbove.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState()));
            }
        });
                
        insertFromWorksheetBelow = (MenuItem)def.getWidget("insertFromWorksheetBelow");
        addScreenHandler(insertFromWorksheetBelow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = true;
                openWorksheetAnalysisLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertFromWorksheetBelow.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState()));
            }
        });
                
        insertFromQcTableAbove = (MenuItem)def.getWidget("insertFromQcTableAbove");
        addScreenHandler(insertFromQcTableAbove, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = false;
//                openQCLookup(null, null, false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertFromQcTableAbove.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });
                
        insertFromQcTableBelow = (MenuItem)def.getWidget("insertFromQcTableBelow");
        addScreenHandler(insertFromQcTableBelow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addRowDirection = true;
//                openQCLookup(null, null, true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertFromQcTableBelow.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
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
                                Window.alert(Messages.get().oneOrMoreQcLinkOnRemove());
                                return;
                            }
                        }
                    }
                    
                    worksheetItemTable.deleteRow(rowIndex);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(false);
            }
        });   


        loadTemplateMenu = (MenuPanel)def.getWidget("loadTemplateMenu");
        addScreenHandler(loadTemplateMenu, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                loadTemplateMenu.menuItems.get(0).enable(false);
                ((AppButton)loadTemplateMenu.menuItems.get(0).getWidget()).enable(false);
            }
        });

        undoQcsMenu = (MenuPanel)def.getWidget("undoQcsMenu");
        addScreenHandler(undoQcsMenu, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                enable = EnumSet.of(State.ADD).contains(event.getState());
                undoQcsMenu.menuItems.get(0).enable(enable);
                ((AppButton)undoQcsMenu.menuItems.get(0).getWidget()).enable(enable);
            }
        });

        undoAllMenu = (MenuItem)def.getWidget("undoAll");
        addScreenHandler(undoAllMenu, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
//                undoAllQcs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                undoAllMenu.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        undoManualMenu = (MenuItem)def.getWidget("undoManual");
        addScreenHandler(undoManualMenu, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
//                undoManualQcs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                undoManualMenu.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        undoTemplateMenu = (MenuItem)def.getWidget("undoTemplate");
        addScreenHandler(undoTemplateMenu, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
//                undoTemplateQcs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                undoTemplateMenu.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                analyteTable.enable(true);
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(13);
                WorksheetBuilderService.get().query(query, new AsyncCallback<ArrayList<IdVO>>() {
                    public void onSuccess(ArrayList<IdVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(Messages.get().noRecordsFound());
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(Messages.get().noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Worksheet call query failed; " +
                                         error.getMessage());
                            window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdVO entry) {
                return fetchById((entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdVO> list;
                ArrayList<TableDataRow> model;

                list = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (list != null) {
                    for (IdVO entry : list)
                        model.add(new TableDataRow(entry.getId(), entry.getId()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY)
                                .contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.setKey(WorksheetBuilderMeta.getWorksheetId());
                field.setQuery(((AppButton)event.getSource()).action);
                field.setType(QueryData.Type.INTEGER);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        //
        // right hand analyte table panel
        //
        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                //  only the reportable field can be edited
                //
                if (event.getCol() != 0)
                    event.cancel();
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns(){
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

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
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load worksheet format dropdown model
        //
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);

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
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(7).getColumnWidget()).setModel(model);
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = WorksheetManager.getInstance();
        sampleManagers.clear();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(worksheetId);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        Datetime now;
        WorksheetViewDO wVDO;
        
        now = CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE);
        
        manager = WorksheetManager.getInstance();
        sampleManagers.clear();
        wVDO = manager.getWorksheet();
        wVDO.setCreatedDate(now);
        wVDO.setSystemUserId(UserCache.getPermission().getSystemUserId());
        wVDO.setSystemUser(UserCache.getPermission().getLoginName());
        wVDO.setStatusId(Constants.dictionary().WORKSHEET_WORKING);
        
        setState(State.ADD);
        DataChangeEvent.fire(this);
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(statusId);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

        worksheetItemTable.finishEditing();
        
        if (!validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            commitAdd();
//            window.setBusy(consts.get("adding"));
//            try {
//                manager = manager.add();
//
//                setState(State.DISPLAY);
//                DataChangeEvent.fire(this);
//                window.setDone(consts.get("addingComplete"));
//            } catch (ValidationErrorsList e) {
//                showErrors(e);
//            } catch (Exception e) {
//                Window.alert("commitAdd(): " + e.getMessage());
//                window.clearStatus();
//            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().updatingComplete());
//                wbLookupScreen.getWindow().close();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(Messages.get().queryAborted());
        } else if (state == State.ADD) {
            abortAdd();
//            fetchById(null);
//            window.setDone(Messages.get().addAborted());
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(Messages.get().updateAborted());
//            wbLookupScreen.getWindow().close();
        } else {
            window.clearStatus();
        }
    }

    @SuppressWarnings("unchecked")
    protected void commitAdd() {
        int                      i, j, k;
        ArrayList<IdNameVO>      columnNameVOs;
        ArrayList<String>        testMethodNames;
        HashMap<String,Integer>  toColumnNames;
        HashMap<Integer,String>  fromColumnNames;
        HashMap<Integer,HashMap<Integer,String>> formatColumnNames;
        Integer                  fromFormatId, toIndex;
        String                   fromName, description;
        TableDataRow             row;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        WorksheetQcResultManager wqrManager, newWqrManager;
        WorksheetQcResultViewDO  wqrVDO, newWqrVDO;
        WorksheetResultManager   wrManager, newWrManager;
        WorksheetResultViewDO    wrVDO, newWrVDO;
        
        setFocus(null);
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert(Messages.get().worksheetNotSaveEmpty());
            return;
        }
        
        window.setBusy(Messages.get().saving());
        
        //
        // If the format has not been set (QC only worksheet), set it to the default
        //
//        if (formatId == null)
//            formatId = formatTotal;
        
        formatColumnNames = new HashMap<Integer,HashMap<Integer,String>>();

        try {
            columnNameVOs = WorksheetCreationService.get().getColumnNames(formatId.getValue());
            toColumnNames = new HashMap<String,Integer>();
            for (IdNameVO columnNameVO : columnNameVOs)
                toColumnNames.put(columnNameVO.getName(), columnNameVO.getId());
        } catch (Exception anyE) {
//            Window.alert(Messages.get().worksheetToColumnMappingLoadError"));
            anyE.printStackTrace();
            toColumnNames = null;
        }

        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        testMethodNames = new ArrayList<String>();
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
            wiDO = new WorksheetItemDO();
            wiDO.setPosition((Integer)row.cells.get(0).value);
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setId((Integer)row.key);
            waDO.setAccessionNumber((String)row.cells.get(1).value);

            testMethodNames.add((String)row.cells.get(4).value + ", " + (String)row.cells.get(5).value);
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
                waDO.setAnalysisId(((WorksheetBuilderVO)row.data).getAnalysisId());
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
                                columnNameVOs = WorksheetCreationService.get().getColumnNames(fromFormatId);
                                fromColumnNames = new HashMap<Integer,String>();
                                for (IdNameVO columnNameVO : columnNameVOs)
                                    fromColumnNames.put(columnNameVO.getId(), columnNameVO.getName());
                                formatColumnNames.put(fromFormatId, fromColumnNames);
                            } catch (Exception anyE1) {
//                                Window.alert(Messages.get().worksheetFromColumnMappingLoadError"));
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
        
        if (manager.getWorksheet().getDescription() == null) {
            description = "";
            for (String testMethodName : testMethodNames) {
                if (description.length() > 0)
                    description += "; ";
                description += testMethodName;
            }
            manager.getWorksheet().setDescription(description);
        }
        
        final WorksheetBuilderScreen wcs = this;
        WorksheetService.get().add(manager, new AsyncCallback<WorksheetManager>() {
            public void onSuccess(WorksheetManager newMan) {
                manager = newMan;

                setState(State.DISPLAY);
                DataChangeEvent.fire(wcs);
                window.setDone(Messages.get().savingComplete());
                
//                wbLookupScreen.getWindow().close();
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
    
    protected void abortAdd() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        //
        // reset worksheet format dropdown model
        //
        formatIds.clear();
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);
        
        //
        // remove all options from the load template menu
        //
        templateMap.clear();
        loadTemplateMenu.menuItems.get(0).menuItemsPanel.clear();
        loadTemplateMenu.menuItems.get(0).menuItemsPanel.menuItems.clear();
        
        worksheetItemTable.clear();
        
        manager = WorksheetManager.getInstance();
        sampleManagers.clear();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.clearStatus();
        
//        wbLookupScreen.getWindow().close();
    }

    protected void worksheetHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getWorksheet().getId(), manager.getWorksheet()
                                                                   .getId()
                                                                   .toString());
        HistoryScreen.showHistory(Messages.get().worksheetHistory(),
                                  Constants.table().WORKSHEET,
                                  hist);
    }

    protected boolean fetchById(Integer id) {
        int i, j;
        ArrayList<Integer> analysisIds;
        ArrayList<SampleManager1> sMans;
        Integer accessionNumber;
        WorksheetAnalysisDO waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemManager wiManager;
        
        if (id == null) {
            manager = WorksheetManager.getInstance();
            sampleManagers.clear();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = WorksheetManager.fetchWithAllData(id);
                analysisIds = new ArrayList<Integer>();
                sampleManagers.clear();
                wiManager = manager.getItems();
                for (i = 0; i < wiManager.count(); i++) {
                    waManager = wiManager.getWorksheetAnalysisAt(i);
                    for (j = 0; j < waManager.count(); j++) {
                        waDO = waManager.getWorksheetAnalysisAt(j);
                        if (waDO.getAnalysisId() != null) {
                            if (!analysisIds.contains(waDO.getAnalysisId()))
                                analysisIds.add(waDO.getAnalysisId());
                        }
                    }
                }

                sMans = SampleService1.get().fetchByAnalyses(analysisIds, SampleManager1.Load.ORGANIZATION,
                                                             SampleManager1.Load.SINGLERESULT);
                for (SampleManager1 sManager : sMans)
                    sampleManagers.put(sManager.getSample().getAccessionNumber(), sManager);
                
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                sampleManagers.clear();
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                sampleManagers.clear();
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }

        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        QueryData field;
        
        fields = super.getQueryFields();
        
        if (systemUserId.getSelection() != null) {
            field = new QueryData();
            field.setKey(WorksheetBuilderMeta.getWorksheetSystemUserId());
            field.setQuery(((Integer)systemUserId.getSelection().key).toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }
        
        return fields;
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i, j, pos;
        AnalysisViewDO aVDO = null;
        ArrayList<TableDataRow> model;
        DictionaryDO dictDO;
        Integer accessionNumber;
        QcLotViewDO qcLotVDO;
        RowItem data;
        SampleDO sDO;
        SampleManager1 sManager;
        SampleOrganizationViewDO soVDO;
        SamplePrivateWellViewDO spwVDO;
        String description, location;
        TableDataRow row;
        TestManager tManager = null;
        WorksheetAnalysisDO waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO wiDO;
        
        qcLinkModel.clear();
        qcLinkModel.add(new TableDataRow(null, ""));

        model = new ArrayList<TableDataRow>();
        if (manager != null) {
            try {
                pos = 1;
                for (i = 0; i < manager.getItems().count(); i++) {
                    wiDO = (WorksheetItemDO)manager.getItems().getWorksheetItemAt(i);
                    waManager = manager.getItems().getWorksheetAnalysisAt(i);
    
                    row = new TableDataRow(12);
                    row.cells.get(0).setValue(pos++);
                    for (j = 0; j < waManager.count(); j++) {
                        waDO = waManager.getWorksheetAnalysisAt(j);
                        qcLinkModel.add(new TableDataRow(waDO.getId(), waDO.getAccessionNumber()));
    
                        if (j > 0) {
                            row = new TableDataRow(12);
                            row.cells.get(0).setValue(pos++);
                        }
                        row.key = i + "_" + j;
                        row.cells.get(1).setValue(waDO.getAccessionNumber());
                        
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
                            row.cells.get(2).value = description;
                            row.cells.get(3).value = waDO.getWorksheetAnalysisId();
                            row.cells.get(4).value = aVDO.getTestName();
                            row.cells.get(5).value = aVDO.getMethodName();
                            
                            dictDO = DictionaryCache.getById(aVDO.getUnitOfMeasureId());
                            row.cells.get(6).value = new TableDataRow(dictDO.getId(),
                                                                      dictDO.getEntry());
                            
                            row.cells.get(7).value = aVDO.getStatusId();
                            row.cells.get(8).setValue(sDO.getCollectionDate());
                            row.cells.get(9).setValue(sDO.getReceivedDate());
                            if (Constants.domain().ENVIRONMENTAL.equals(sManager.getSample()
                                                                                .getDomain()))
                                row.cells.get(10).setValue(DataBaseUtil.getDueDays(sDO.getReceivedDate(),
                                                                                   sManager.getSampleEnvironmental()
                                                                                           .getPriority()));
                            row.cells.get(11).setValue(DataBaseUtil.getExpireDate(sDO.getCollectionDate(),
                                                                                  sDO.getCollectionTime(),
                                                                                  tManager.getTest()
                                                                                          .getTimeHolding()));
                            
                            data = new RowItem();
                            data.worksheetItem = wiDO;
                            data.worksheetAnalysis = waDO;
                            data.analysis = aVDO;
                            row.data = data;
                            model.add(row);
                        } else if (waDO.getQcLotId() != null) {
                            qcLotVDO = QcService.get().fetchLotById(waDO.getQcLotId());
    
                            row.cells.get(2).value = qcLotVDO.getQcName();
                            row.cells.get(3).value = waDO.getWorksheetAnalysisId();
    
                            data = new RowItem();
                            data.worksheetItem = wiDO;
                            data.worksheetAnalysis = waDO;
                            data.qcLotVDO = qcLotVDO;
                            row.data = data; 
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
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(3).getColumnWidget()).setModel(qcLinkModel);

        return model;
    }

    protected void openLookupWindow() {
        org.openelis.ui.widget.Window win;
        
        win = new org.openelis.ui.widget.Window(false);
        win.setName(Messages.get().worksheetBuilderLookup());
        if (wbLookupScreen == null) {
            try {
                wbLookupScreen = new WorksheetBuilderLookupScreen();
                wbLookupScreen.addActionHandler(new ActionHandler<WorksheetBuilderLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetBuilderLookupScreen.Action> event) {
                        int index;
                        ArrayList<TableDataRow> list;
                        DictionaryDO unitDO;
                        TableDataRow newRow;
                        WorksheetBuilderVO data;

                        if (event.getAction() == WorksheetBuilderLookupScreen.Action.ADD) {
                            if (worksheetItemTable.getSelectedRow() != -1) {
                                index = worksheetItemTable.getSelectedRow();
                                if (addRowDirection)
                                    index++;
                            } else {
                                index = worksheetItemTable.numRows();
                            }
                            
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null && list.size() > 0) {
                                for (TableDataRow row : list) {
                                    newRow = new TableDataRow(13);
                                    data = (WorksheetBuilderVO)row.data;

                                    //
                                    // set the format if it has not already been set
                                    //
                                    if (formatId.getValue() == null)
                                        formatId.setValue(data.getWorksheetFormatId());
                                    
                                    //
                                    // add template to list
                                    //
                                    if (!templateMap.containsValue(data.getTestId())) {
                                        final MenuItem item = new MenuItem("AddRowButtonImage", data.getTestName()+", "+data.getMethodName(), null);
                                        item.setVisible(true);
                                        item.enable(true);
                                        addScreenHandler(item, new ScreenEventHandler<Object>() {
                                            public void onClick(ClickEvent event) {
                                                loadTemplate(templateMap.get(item));
                                            }
                                        });
                                        loadTemplateMenu.menuItems.get(0).menuItemsPanel.add(item);
                                        templateMap.put(item, data.getTestId());
                                        loadTemplateMenu.menuItems.get(0).enable(true);
                                        ((AppButton)loadTemplateMenu.menuItems.get(0).getWidget()).enable(true);
                                    }

                                    newRow.key = getNextTempId();                                       // fake worksheet analysis id
                                    newRow.cells.get(1).value = String.valueOf(row.cells.get(0).value); // accession #
                                    newRow.cells.get(2).value = row.cells.get(1).value;                 // description
                                    newRow.cells.get(4).value = row.cells.get(2).value;                 // test name
                                    newRow.cells.get(5).value = row.cells.get(3).value;                 // method name
                                    
                                    if (row.cells.get(5).value != null) {
                                        try {                                                               // unit
                                            unitDO = DictionaryCache.getById((Integer)row.cells.get(5).value);
                                            newRow.cells.get(6).value = new TableDataRow(unitDO.getId(), unitDO.getEntry());
                                        } catch (Exception anyE) {
                                            anyE.printStackTrace();
                                            Window.alert("error: " + anyE.getMessage());
                                        }
                                    }
                                    
                                    newRow.cells.get(7).value = row.cells.get(6).value;                 // status
                                    newRow.cells.get(8).value = row.cells.get(7).value;                 // collection date
                                    newRow.cells.get(9).value = row.cells.get(8).value;                 // received date and time
                                    newRow.cells.get(10).value = row.cells.get(9).value;                // due days
                                    newRow.cells.get(11).value = row.cells.get(10).value;               // expire date and time
                                    newRow.cells.get(12).value = ROW_TYPE.ANALYSIS;
                                    newRow.data = data;
                                    worksheetItemTable.addRow(index++, newRow);
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
                                manager.getWorksheet().setRelatedWorksheetId(wVDO.getId());
                                DataChangeEvent.fire(screen, relatedWorksheetId);
                            }
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(Messages.get().worksheetLookup());
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
                                    
                                    modal2 = new ScreenWindow(ScreenWindow.Mode.DIALOG);
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
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(Messages.get().worksheetLookup());
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
    }

    private void showAnalytes() {
        ArrayList<TableDataRow> selections;
        
        if (analyteTable.isVisible()) {
            selections = worksheetItemTable.getSelections();
            if (selections.size() == 1) {
                if (selections.get(0).data instanceof WorksheetAnalysisDO) {
                    ResultService.get().fetchByAnalysisIdForDisplay(((WorksheetAnalysisDO)selections.get(0).data).getAnalysisId(),
                                                                    new AsyncCallback<AnalysisResultManager>() {
                        public void onSuccess(AnalysisResultManager arMan) {
                            loadAnalyteTable(arMan);
                        }
            
                        public void onFailure(Throwable error) {
                            analyteTable.load(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().noAnalytesFoundForRow());
                            } else {
                                Window.alert("Error: WorksheetCreation call showAnalytes failed; "+error.getMessage());
                            }
                        }
                    });
                } else if (selections.get(0).data instanceof WorksheetBuilderVO) {
                    ResultService.get().fetchByAnalysisIdForDisplay(((WorksheetBuilderVO)selections.get(0).data).getAnalysisId(),
                                                                    new AsyncCallback<AnalysisResultManager>() {
                        public void onSuccess(AnalysisResultManager arMan) {
                            loadAnalyteTable(arMan);
                        }
            
                        public void onFailure(Throwable error) {
                            analyteTable.load(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().noAnalytesFoundForRow());
                            } else {
                                Window.alert("Error: WorksheetCreation call showAnalytes failed; "+error.getMessage());
                            }
                        }
                    });
                } else {
                    analyteTable.load(null);
                }
            } else {
                analyteTable.load(null);
            }            
        } else {
            analyteTable.load(null);
        }
    }

    public void loadAnalyteTable(AnalysisResultManager arMan) {
        int i;
        ArrayList<ResultViewDO> resultRow;
        ArrayList<TableDataRow> model;
        ResultViewDO result;
        TableDataRow row;

        model = null;
        if (arMan != null) {
            model = new ArrayList<TableDataRow>();
            for (i = 0; i < arMan.getResults().size(); i++) {
                resultRow = arMan.getRowAt(i);
                result = (ResultViewDO)resultRow.get(0);

                row = new TableDataRow(2);
                row.key = result.getId();
                row.cells.get(0).value = result.getIsReportable();
                row.cells.get(1).value = result.getAnalyte();
                row.data = result;
                model.add(row);
            }
        }
        analyteTable.load(model);
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
        testWorksheetItems.clear();
        buildQCWorksheet();
    }
    
    private class RowItem {
        AnalysisViewDO analysis;
        QcLotViewDO qcLotVDO;
        WorksheetAnalysisDO worksheetAnalysis;
        WorksheetItemDO worksheetItem;
    }
}