/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisUserManager;
import org.openelis.manager.PanelManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalysisTab extends Screen implements HasActionHandlers<AnalysisTab.Action> {
    public enum Action {
        ANALYSIS_ADDED, PANEL_ADDED, ORDER_LIST_ADDED, CHANGED_DONT_CHECK_PREPS,
        ITEM_CHANGED, SAMPLE_TYPE_CHANGED, UNIT_CHANGED
    };

    private boolean                                     loaded;

    protected AutoComplete<Integer>                     test, samplePrep, panel;
    protected Dropdown<Integer>                         sectionId, unitOfMeasureId, statusId, userActionId, typeId;
    protected CheckBox                                  isReportable;
    protected TextBox                                   method;
    protected TextBox<Integer>                          revision;
    protected CalendarLookUp                            startedDate, completedDate, releasedDate,
                                                        printedDate;
    protected TableWidget                               worksheetTable, analysisUserTable;
    protected AppButton                                 selectWkshtButton, addActionButton,
                                                        removeActionButton;

    protected ArrayList<TableDataRow>                   fullSectionModel, fullUnitModel;
    protected HashMap<Integer, ArrayList<TableDataRow>> sectionModel;
    protected HashMap<String, ArrayList<TableDataRow>>  unitModel;

    protected boolean                                   fullSectionShown, fullUnitShown;
    protected int                                       analysisIndex = -1;
    protected AnalysisManager                           manager;
    protected SampleDataBundle                          bundle;
    protected AnalysisViewDO                            analysis;
    protected SampleItemViewDO                          sampleItem;

    private Confirm                                     changeTestConfirm;

    public AnalysisTab(ScreenDefInt def, WindowInt window) {

        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        final AnalysisTab anTab = this;

        test = (AutoComplete)def.getWidget(SampleMeta.getAnalysisTestName());
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setSelection(analysis.getTestId(), analysis.getTestName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TableDataRow selectedRow;

                selectedRow = test.getSelection();
                //
                // we allow test&method to change if analysis has not been committed. However, 
                // if analysis has been committed, then we will only allow method to be changed.
                // For those cases that the user blanks the entire field (selecting nothing;
                // selectedRow == null), we will put the test back in the dropdown.
                //
                if (analysis.getId() == null || analysis.getId() < 0) {
                    testChanged();
                    return;
                } else if (selectedRow == null) {
                    test.setSelection(analysis.getTestId(), analysis.getTestName());
                    method.setValue(analysis.getMethodName());                                       
                    return;
                }

                if (! DataBaseUtil.isSame(((TestMethodVO)selectedRow.data).getTestId(), analysis.getTestId())) {
                    if (changeTestConfirm == null) {
                        changeTestConfirm = new Confirm(Confirm.Type.WARN,
                                                        Messages.get().loseResultsCaption(),
                                                        Messages.get().loseResultsWarning(), "No",
                                                        "Yes");
                        changeTestConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:                                                                                                                                                                
                                        test.setSelection(analysis.getTestId(),
                                                          analysis.getTestName());
                                        method.setValue(analysis.getMethodName());                                       
                                        break;
                                    case 1:
                                        testChanged();
                                        break;
                                }
                            }
                        });
                    }
                    changeTestConfirm.show();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(event.getState() == State.QUERY ||
                            (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState())));
                test.setQueryMode(event.getState() == State.QUERY);
            }
        });

        test.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                int                     i;
                Integer                 sampleType, testPanelId, sepIndex;
                String                  value, flag;
                ArrayList<TableDataRow> model;
                FormErrorException      feE;
                TableDataRow            row;
                DictionaryDO            stDO;
                PanelDO                 pDO;
                PanelManager            pMan;
                TestManager             tMan;
                TestTypeOfSampleDO      ttosDO;
                TestTypeOfSampleManager ttosMan;
                TestViewDO              tVDO;

                /// if analysis has been added, then don't allow test to be changed
                if (analysis.getId() == null || analysis.getId() < 0)
                    return;
                
                value = event.getMatch();
                if (value.matches("[tp][0-9]*\\-[0-9]*")) { 
                    flag = value.substring(0, 1);
                    sepIndex = value.indexOf("-");
                    testPanelId = Integer.valueOf(value.substring(1, sepIndex));
                    sampleType = Integer.valueOf(value.substring(sepIndex + 1));
                    try {
                        if (sampleItem.getTypeOfSampleId() == null) {
                            stDO = DictionaryCache.getById(sampleType);
                            sampleItem.setTypeOfSampleId(stDO.getId());
                            sampleItem.setTypeOfSample(stDO.getEntry());
                            ActionEvent.fire(anTab, Action.SAMPLE_TYPE_CHANGED, null);
                        }

                        row = new TableDataRow(3);
                        if ("t".equals(flag)) {
                            tMan = TestService.get().fetchById(testPanelId);
                            tVDO = tMan.getTest();
                            ttosMan = tMan.getSampleTypes();
                            for (i = 0; i < ttosMan.count(); i++) {
                                ttosDO = ttosMan.getTypeAt(i);
                                if (ttosDO.getTypeOfSampleId().equals(sampleItem.getTypeOfSampleId())) {
                                    row.key = tVDO.getId();
                                    row.cells.get(0).value = tVDO.getName();
                                    row.cells.get(1).value = tVDO.getMethodName();
                                    row.cells.get(2).value = tVDO.getDescription();
                                    row.data = tVDO;
                                    break;
                                }
                            }
                            if (i == ttosMan.count()) {
                                feE = new FormErrorException(Messages.get().testMethodSampleTypeMismatch(
                                                             tVDO.getName()+", "+tVDO.getMethodName(),
                                                             sampleItem.getTypeOfSample()));
                                Window.alert(feE.getMessage());
                            }
                        } else if ("p".equals(flag)) {
                            pMan = PanelService.get().fetchById(testPanelId);
                            pDO = pMan.getPanel();
                            row.key = pDO.getId();
                            row.cells.get(0).value = pDO.getName();
                            row.cells.get(2).value = pDO.getDescription();
                            row.data = pDO;
                        }
                        model = new ArrayList<TableDataRow>();
                        model.add(row);
                        test.setModel(model);
                        test.setSelection(row.key);
                        test.complete();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    event.cancel();
                }
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {                
                Integer                 sampleType, key;
                ArrayList<QueryData>    fields;
                ArrayList<TableDataRow> model;
                ArrayList<TestMethodVO> autoList;
                Query                   query;
                QueryData               field;
                TableDataRow            row;
                
                sampleType = sampleItem.getTypeOfSampleId();
                if (sampleType == null) {
                    window.setError(Messages.get().sampleItemTypeRequired());
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();

                field = new QueryData();
                //
                // the methd
                if (analysis.getId() == null || analysis.getId() < 0)
                    field.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch()) + "%");
                else
                    field.setQuery(QueryFieldUtil.parseAutocomplete(analysis.getTestName()));
                
                fields.add(field);

                field = new QueryData();
                field.setQuery(String.valueOf(sampleItem.getTypeOfSampleId()));
                fields.add(field);

                query.setFields(fields);
                query.setRowsPerPage(100);

                try {
                    autoList = PanelService.get().fetchByNameSampleTypeWithTests(query);
                    model = new ArrayList<TableDataRow>();

                    for (TestMethodVO autoDO : autoList) {                     
                        /*
                         * Since the keys of the rows need to be unique and it can
                         * happen that a panel has the same id as a test, a negative
                         * number is used as the key for a row showing a panel 
                         * and a positive one for a row showing a test. An index
                         * in a loop can't be used here because it can clash with
                         * an id and two different rows may be treated as the same.                         
                         */
                        key = autoDO.getMethodId() == null ? -autoDO.getTestId() : autoDO.getTestId();                        
                        row = new TableDataRow(key, autoDO.getTestName(), autoDO.getMethodName(),
                                               autoDO.getTestDescription());
                        row.data = autoDO;
                        model.add(row);
                    }
                    test.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        method = (TextBox)def.getWidget(SampleMeta.getAnalysisMethodName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setValue(analysis.getMethodName());                                       
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(event.getState() == State.QUERY);
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });


        statusId = (Dropdown)def.getWidget(SampleMeta.getAnalysisStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(analysis.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setStatusId(event.getValue());
                if (!State.QUERY.equals(state))
                    ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                int                     i;
                ArrayList<TableDataRow> model;
                TableDataRow            r;

                statusId.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                statusId.setQueryMode(event.getState() == State.QUERY);

                model = statusId.getData();
                for (i = 0; i < model.size(); i++) {
                    r = model.get(i);
                    if (!Constants.dictionary().ANALYSIS_INITIATED.equals(r.key) &&
                        !Constants.dictionary().ANALYSIS_ON_HOLD.equals(r.key) && 
                        !Constants.dictionary().ANALYSIS_REQUEUE.equals(r.key) &&
                        !Constants.dictionary().ANALYSIS_LOGGED_IN.equals(r.key) &&
                        !State.QUERY.equals(event.getState())) 
                        r.enabled = false;
                    else
                        r.enabled = true;
                }
            }
        });
        
        statusId.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {           
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {                
                TableDataRow r;
                
                r = event.getItem().row;                
                if (!r.enabled)
                    event.cancel();
            }
        });

        revision = (TextBox<Integer>)def.getWidget(SampleMeta.getAnalysisRevision());
        addScreenHandler(revision, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setFieldValue(analysis.getRevision());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                revision.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown<Integer>)def.getWidget(SampleMeta.getAnalysisTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(analysis.getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isReportable = (CheckBox)def.getWidget(SampleMeta.getAnalysisIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(analysis.getIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                analysis.setIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReportable.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sectionId = (Dropdown<Integer>)def.getWidget(SampleMeta.getAnalysisSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<TableDataRow> sections;

                if (EnumSet.of(State.QUERY, State.DEFAULT, State.DISPLAY).contains(state)) {
                    if ( !fullSectionShown)
                        sectionId.setModel(fullSectionModel);
                    fullSectionShown = true;
                } else {
                    fullSectionShown = false;
                    sections = null;

                    if (analysis.getTestId() != null) {
                        sections = sectionModel.get(analysis.getTestId());

                        if (sections == null) {
                            try {
                                sections = getSectionsModel(manager.getTestAt(bundle.getAnalysisIndex())
                                                                   .getTestSections());
                            } catch (Exception e) {
                                sections = null;
                            }
                            sectionModel.put(analysis.getTestId(), sections);
                        }
                    }
                    sectionId.setModel(sections);
                }
                sectionId.setSelection(analysis.getSectionId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                sectionId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        unitOfMeasureId = (Dropdown<Integer>)def.getWidget(SampleMeta.getAnalysisUnitOfMeasureId());
        addScreenHandler(unitOfMeasureId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<TableDataRow> units;

                if (EnumSet.of(State.QUERY, State.DEFAULT, State.DISPLAY).contains(state)) {
                    if ( !fullUnitShown)
                        unitOfMeasureId.setModel(fullUnitModel);
                    fullUnitShown = true;
                } else {
                    fullUnitShown = false;
                    units = null;

                    if (analysis.getTestId() != null) {
                        units = unitModel.get(analysis.getTestId() + "|" +
                                              sampleItem.getTypeOfSampleId());

                        if (units == null) {
                            try {
                                units = getUnitsModel(manager.getTestAt(bundle.getAnalysisIndex())
                                                             .getSampleTypes(),
                                                      sampleItem.getTypeOfSampleId());
                            } catch (Exception e) {
                                units = null;
                            }
                            unitModel.put(analysis.getTestId() + "|" +
                                          sampleItem.getTypeOfSampleId(), units);
                        }
                    }
                    unitOfMeasureId.setModel(units);
                }
                unitOfMeasureId.setSelection(analysis.getUnitOfMeasureId());                
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setUnitOfMeasureId(event.getValue());
                ActionEvent.fire(anTab, Action.UNIT_CHANGED, bundle);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unitOfMeasureId.enable(event.getState() == State.QUERY ||
                                       (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                            .contains(event.getState())));
                unitOfMeasureId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        startedDate = (CalendarLookUp)def.getWidget(SampleMeta.getAnalysisStartedDate());
        addScreenHandler(startedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startedDate.setValue(analysis.getStartedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startedDate.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                startedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        completedDate = (CalendarLookUp)def.getWidget(SampleMeta.getAnalysisCompletedDate());
        addScreenHandler(completedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                completedDate.setValue(analysis.getCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedDate.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
                completedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        releasedDate = (CalendarLookUp)def.getWidget(SampleMeta.getAnalysisReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(analysis.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                releasedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        printedDate = (CalendarLookUp)def.getWidget(SampleMeta.getAnalysisPrintedDate());
        addScreenHandler(printedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                printedDate.setValue(analysis.getPrintedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setPrintedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printedDate.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                printedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        panel = (AutoComplete<Integer>)def.getWidget(SampleMeta.getAnalysisPanelId());
        addScreenHandler(panel, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                panel.setSelection(analysis.getPanelId(), analysis.getPanelName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setPanelId(event.getValue());
                analysis.setPanelName(panel.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panel.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState()));
                panel.setQueryMode(event.getState() == State.QUERY);
            }
        });

        panel.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                PanelDO data;
                ArrayList<PanelDO> list;
                ArrayList<TableDataRow> model;
                
                try {
                    list = PanelService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()) + "%");
                    model = new ArrayList<TableDataRow>();
                    
                    for (i = 0; i < list.size(); i++) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    }
                    panel.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        samplePrep = (AutoComplete<Integer>)def.getWidget(SampleMeta.getAnalysisSamplePrep());
        addScreenHandler(samplePrep, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                String testMethodString = null;

                if (analysis.getPreAnalysisTest() != null)
                    testMethodString = analysis.getPreAnalysisTest() + " : " +
                                       analysis.getPreAnalysisMethod();

                samplePrep.setSelection(analysis.getPreAnalysisId(), testMethodString);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                SampleDataBundle anBundle;
                SampleItemManager itemMan;

                try {
                    if (event.getValue() == null) {
                        manager.unlinkPrepTest(bundle.getAnalysisIndex());

                    } else {
                        anBundle = (SampleDataBundle)samplePrep.getSelection().data;
                        itemMan = bundle.getSampleManager().getSampleItems();

                        itemMan.linkPrepTest(bundle.getSampleItemIndex(),
                                             bundle.getAnalysisIndex(),
                                             anBundle.getSampleItemIndex(),
                                             anBundle.getAnalysisIndex());
                    }

                    ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
                    DataChangeEvent.fire(anTab, statusId);

                } catch (Exception e) {
                    Window.alert("samplePrep valueChange: " + e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplePrep.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState()));
                samplePrep.setQueryMode(event.getState() == State.QUERY);
            }
        });

        samplePrep.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AnalysisViewDO anDO;
                SampleItemViewDO itemDO;
                SampleItemManager itemMan;
                AnalysisManager anMan;
                Integer currentId;
                String match;
                int numOfRows, i, j;

                try {
                    currentId = analysis.getId();
                    match = event.getMatch();
                    model = new ArrayList<TableDataRow>();
                    itemMan = bundle.getSampleManager().getSampleItems();

                    numOfRows = 0;
                    for (i = 0; i < itemMan.count(); i++ ) {
                        if (numOfRows > 9)
                            break;

                        itemDO = itemMan.getSampleItemAt(i);
                        anMan = itemMan.getAnalysisAt(i);

                        for (j = 0; j < anMan.count(); j++ ) {
                            if (numOfRows > 9)
                                break;

                            anDO = anMan.getAnalysisAt(j);

                            if ( !currentId.equals(anDO.getId()) && anDO.getTestName() != null &&
                                anDO.getTestName().startsWith(match) && 
                                !Constants.dictionary().ANALYSIS_CANCELLED.equals(anDO.getStatusId())) {
                                row = new TableDataRow(
                                                       anDO.getId(),
                                                       anDO.getTestName() +
                                                                       " : " +
                                                                       anDO.getMethodName() +
                                                                       " | " +
                                                                       formatTreeString(itemDO.getTypeOfSample()));
                                row.data = anMan.getBundleAt(j);
                                model.add(row);
                            }
                        }
                    }

                    samplePrep.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert("prep getMatches: " + e.getMessage());
                }
            }
        });

        worksheetTable = (TableWidget)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<TableDataRow>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetTable.load(getWorksheetTableModel());
            }

            public void onValueChange(ValueChangeEvent<TableDataRow> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.enable(true);
            }
        });

        worksheetTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                selectWkshtButton.enable(true);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        analysisUserTable = (TableWidget)def.getWidget("analysisUserTable");
        addScreenHandler(analysisUserTable, new ScreenEventHandler<TableDataRow>() {
            public void onDataChange(DataChangeEvent event) {
                analysisUserTable.load(getAnalysisUserTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisUserTable.enable(true);
            }
        });

        final AutoComplete<Integer> userName = ((AutoComplete<Integer>)analysisUserTable.getColumns()
                                                                                        .get(0).colWidget);
        analysisUserTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (!canEdit() || !EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                }
            }
        });

        analysisUserTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisUserViewDO data;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                val = analysisUserTable.getObject(r, c);

                try {
                    data = manager.getAnalysisUserAt(bundle.getAnalysisIndex())
                                  .getAnalysisUserAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        row = userName.getSelection();

                        if (row != null) {
                            data.setSystemUserId((Integer)row.key);
                            data.setSystemUser((String)row.cells.get(0).value);
                        } else {
                            data.setSystemUserId(null);
                            data.setSystemUser(null);
                        }
                        break;
                    case 1:
                        if (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId())) {
                            analysisUserTable.setCell(r, c, data.getActionId());
                            window.setError(Messages.get().analysisUserActionException());
                        } else {
                            data.setActionId((Integer)val);
                        }
                        break;
                }
            }
        });

        analysisUserTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getAnalysisUserAt(bundle.getAnalysisIndex())
                           .addAnalysisUser(new AnalysisUserViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        analysisUserTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAnalysisUserAt(bundle.getAnalysisIndex())
                           .removeAnalysisUserAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });

        userName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<TableDataRow> model;

                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (SystemUserVO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    userName.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });
        
        userActionId = ((Dropdown<Integer>)analysisUserTable.getColumns().get(1).getColumnWidget());
        userActionId.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {           
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {                
                TableDataRow r;
                
                r = event.getItem().row;                
                if (!r.enabled)
                    event.cancel();
            }
        });

        selectWkshtButton = (AppButton)def.getWidget("selectWkshtButton");
        addScreenHandler(selectWkshtButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;
                TableDataRow row;
                WorksheetCompletionScreen worksheetScreen;
                
                try {
                    modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
                    modal.setName(Messages.get().worksheetCompletion());
                    
                    row = worksheetTable.getSelection();
                    worksheetScreen = new WorksheetCompletionScreen((Integer)row.key,modal);
                    
                    modal.setContent(worksheetScreen);
                } catch (Exception e) {
                    Window.alert("openCompletionScreen: " + e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectWkshtButton.enable(false);
            }
        });

        addActionButton = (AppButton)def.getWidget("addActionButton");
        addScreenHandler(addActionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                analysisUserTable.addRow();
                n = analysisUserTable.numRows() - 1;
                analysisUserTable.selectRow(n);
                analysisUserTable.scrollToSelection();
                analysisUserTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addActionButton.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                           .contains(event.getState()));
            }
        });

        removeActionButton = (AppButton)def.getWidget("removeActionButton");
        addScreenHandler(removeActionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                Integer action;

                r = analysisUserTable.getSelectedRow();
                
                if (r > -1 && analysisUserTable.numRows() > 0) {
                    try {
                        action = manager.getAnalysisUserAt(bundle.getAnalysisIndex()).getAnalysisUserAt(r).getActionId();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        return;
                    }
                    
                    if (!Constants.dictionary().AN_USER_AC_RELEASED.equals(action))
                        analysisUserTable.deleteRow(r);  
                    else 
                        window.setError(Messages.get().analysisUserActionException());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeActionButton.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                              .contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        TableDataRow r;
    
        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
    
        statusId.setModel(model);
    
        // analysis type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
    
        typeId.setModel(model);

        // section full dropdown model
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (SectionDO s : SectionCache.getList())
            model.add(new TableDataRow(s.getId(), s.getName()));
    
        fullSectionModel = model;
        sectionId.setModel(model);
        sectionModel = new HashMap<Integer, ArrayList<TableDataRow>>();
        fullSectionShown = true;
    
        // unit full dropdown model
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        
        fullUnitModel = model;
        unitOfMeasureId.setModel(model);
        unitModel = new HashMap<String, ArrayList<TableDataRow>>();
        fullUnitShown = true;
    
        // analysis user action
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("user_action")) {
            r = new TableDataRow(d.getId(), d.getEntry());
            if(Constants.dictionary().AN_USER_AC_RELEASED.equals(d.getId())) 
                r.enabled = false;
            model.add(r);
        }
        
        userActionId.setModel(model);
    
        // worksheet status
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("worksheet_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)worksheetTable.getColumns().get(2).getColumnWidget()).setModel(model);
    
    }

    private ArrayList<TableDataRow> getSectionsModel(TestSectionManager sectionManager) {
        ArrayList<TableDataRow> model;
        TestSectionViewDO section;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));

        if (sectionManager != null) {
            for (int i = 0; i < sectionManager.count(); i++ ) {
                section = sectionManager.getSectionAt(i);
                model.add(new TableDataRow(section.getSectionId(), section.getSection()));
            }
        }

        return model;
    }

    private ArrayList<TableDataRow> getUnitsModel(TestTypeOfSampleManager man, Integer sampleTypeId) {
        ArrayList<TableDataRow> model;
        DictionaryDO entry;
        TestTypeOfSampleDO sampleType;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        if (man != null) {
            try {
                for (int i = 0; i < man.count(); i++ ) {
                    sampleType = man.getTypeAt(i);
                    if (sampleType.getUnitOfMeasureId() != null &&
                        sampleTypeId.equals(sampleType.getTypeOfSampleId())) {
                        entry = DictionaryCache.getById(sampleType.getUnitOfMeasureId());
                        model.add(new TableDataRow(entry.getId(), entry.getEntry()));
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                return new ArrayList<TableDataRow>();
            }
        }

        return model;
    }

    private void testChanged() {
        TableDataRow selectedRow;
        TestMethodVO tmVO;

        /*
         * we need to see if what they selected is a test or a panel
         */
        try {
            selectedRow = test.getSelection();
            if (selectedRow != null) {
                tmVO = (TestMethodVO)selectedRow.data;
                if (tmVO.getMethodId() == null)
                    ActionEvent.fire(this, Action.PANEL_ADDED, tmVO.getTestId());
                else
                    ActionEvent.fire(this, Action.ANALYSIS_ADDED, tmVO.getTestId());
            } else {
                ActionEvent.fire(this, Action.ANALYSIS_ADDED, null);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private ArrayList<TableDataRow> getWorksheetTableModel() {
        ArrayList<TableDataRow> model;
        ArrayList<WorksheetViewDO> worksheets;
        WorksheetViewDO wksht;
        AnalysisViewDO anDO;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        anDO = manager.getAnalysisAt(bundle.getAnalysisIndex());
        if (anDO.getId() > 0) {
            try {
                worksheets = WorksheetService.get().fetchByAnalysisId(anDO.getId());

                for (int i = 0; i < worksheets.size(); i++ ) {
                    wksht = worksheets.get(i);

                    row = new TableDataRow(4);
                    row.key = wksht.getId();
                    row.cells.get(0).value = wksht.getId();
                    row.cells.get(1).value = wksht.getCreatedDate();
                    row.cells.get(2).value = wksht.getStatusId();
                    row.cells.get(3).value = wksht.getSystemUser();
                    model.add(row);

                }
            } catch (Exception e) {
                Window.alert("getWorksheetTableModel: " + e.getMessage());
            }
        }
        return model;
    }

    private ArrayList<TableDataRow> getAnalysisUserTableModel() {
        ArrayList<TableDataRow> model;
        AnalysisUserManager userMan;
        AnalysisUserViewDO userDO;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            userMan = manager.getAnalysisUserAt(bundle.getAnalysisIndex());
            for (int iter = 0; iter < userMan.count(); iter++ ) {
                userDO = userMan.getAnalysisUserAt(iter);

                TableDataRow row = new TableDataRow(2);
                row.key = userDO.getId();

                row.cells.get(0).value = new TableDataRow(userDO.getSystemUserId(),
                                                          userDO.getSystemUser());
                row.cells.get(1).value = userDO.getActionId();
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert("getAnalysisUserTableModel: " + e.getMessage());
            return null;
        }
        return model;
    }

    private boolean canEdit() {
        SectionPermission perm;
        SectionViewDO     sectionVDO;
        
        if (analysis != null && analysis.getId() != null) {
            if (analysis.getSectionId() == null)
                return true;
            
            try {
                sectionVDO = SectionCache.getById(analysis.getSectionId());
                perm = UserCache.getPermission().getSection(sectionVDO.getName());
                return !Constants.dictionary().ANALYSIS_CANCELLED.equals(analysis.getStatusId()) &&
                       !Constants.dictionary().ANALYSIS_RELEASED.equals(analysis.getStatusId()) &&
                       perm != null &&
                       (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }
        return false;       
    }

    private String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }

    public void setData(SampleDataBundle data) {
        try {
            bundle = data;
            if (data != null && SampleDataBundle.Type.ANALYSIS.equals(data.getType())) {

                analysisIndex = data.getAnalysisIndex();
                manager = data.getSampleManager()
                              .getSampleItems()
                              .getAnalysisAt(data.getSampleItemIndex());
                analysis = manager.getAnalysisAt(analysisIndex);
                sampleItem = data.getSampleManager()
                                 .getSampleItems()
                                 .getSampleItemAt(data.getSampleItemIndex());

                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            } else {
                analysis = new AnalysisViewDO();
                sampleItem = new SampleItemViewDO();
                manager = null;

                if (state != State.QUERY)
                    StateChangeEvent.fire(this, State.DEFAULT);
            }
            loaded = false;
        } catch (Exception e) {
            Window.alert("analysisTab setData: " + e.getMessage());
        }
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    public boolean validate() {
        if ( !loaded)
            return true;
        return super.validate();
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}