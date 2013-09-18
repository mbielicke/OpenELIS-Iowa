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

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
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
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
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
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AnalysisTab extends Screen implements HasActionHandlers<AnalysisTab.Action> {
    public enum Action {
        ANALYSIS_ADDED, PANEL_ADDED, ORDER_LIST_ADDED, CHANGED_DONT_CHECK_PREPS,
        ITEM_CHANGED, SAMPLE_TYPE_CHANGED
    };

    private boolean                                     loaded;

    protected AutoComplete                              test, method, samplePrep;
    protected Dropdown<Integer>                         sectionId, unitOfMeasureId, statusId, userActionId;
    protected CheckBox                                  isReportable;
    protected TextBox                                   revision;
    protected Calendar                                  startedDate, completedDate, releasedDate,
                                                        printedDate;
    protected Table                                     worksheetTable, analysisUserTable;
    protected Button                                    selectWkshtButton, addActionButton,
                                                        removeActionButton;

    protected ArrayList<Item<Integer>>                  fullSectionModel, fullUnitModel;
    protected HashMap<Integer, ArrayList<Item<Integer>>>sectionModel;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModel;

    protected boolean                                   fullSectionShown, fullUnitShown;
    protected int                                       analysisIndex = -1;
    protected AnalysisManager                           manager;
    protected SampleDataBundle                          bundle;
    protected AnalysisViewDO                            analysis;
    protected SampleItemViewDO                          sampleItem;

    protected Integer                                   analysisLoggedInId, analysisInitiatedId, 
                                                        analysisOnHoldId, analysisRequeueId,
                                                        analysisCompletedId, analysisCancelledId,
                                                        analysisReleasedId, analysisInPrepId, changedTestId,
                                                        actionReleasedId;

    private Confirm                                     changeTestConfirm;
    protected ScreenService                             panelService, testService,
                                                        userService, worksheetService;

    public AnalysisTab(ScreenDefInt def, Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        panelService = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        userService = new ScreenService("controller?service=org.openelis.server.SystemUserService");
        worksheetService = new ScreenService("controller?service=org.openelis.modules.worksheet.server.WorksheetService");

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
                test.setValue(analysis.getTestId(), analysis.getTestName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                changedTestId = event.getValue();

                if (manager.hasAnalysisResultsAt(analysisIndex)) {
                    if (changeTestConfirm == null) {
                        changeTestConfirm = new Confirm(Confirm.Type.WARN,
                                                        consts.get("loseResultsCaption"),
                                                        consts.get("loseResultsWarning"), "No",
                                                        "Yes");
                        changeTestConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:
                                        test.setValue(analysis.getTestId(),
                                                          analysis.getTestName());
                                        break;
                                    case 1:
                                        testChanged(changedTestId);
                                        break;
                                }
                            }
                        });
                    }

                    changeTestConfirm.show();
                } else {
                    testChanged(changedTestId);
                }

            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.setEnabled(canEdit() &&
                            EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                test.setQueryMode(event.getState() == State.QUERY);
            }
        });

        test.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                int                     i;
                Integer                 sampleType, testPanelId, sepIndex;
                String                  value, flag;
                ArrayList<Item<Integer>> model;
                FormErrorException      feE;
                Item<Integer>           row;
                DictionaryDO            stDO;
                PanelDO                 pDO;
                PanelManager            pMan;
                TestManager             tMan;
                TestTypeOfSampleDO      ttosDO;
                TestTypeOfSampleManager ttosMan;
                TestViewDO              tVDO;

                value = event.getMatch();
                if (value.matches("[tp][0-9]*\\-[0-9]*")) {
                    flag = value.substring(0, 1);
                    sepIndex = value.indexOf("-");
                    testPanelId = Integer.valueOf(value.substring(1, sepIndex));
                    sampleType = Integer.valueOf(value.substring(sepIndex + 1));
                    try {
                        if (sampleItem.getTypeOfSampleId() == null) {
                            stDO = DictionaryCache.getEntryFromId(sampleType);
                            sampleItem.setTypeOfSampleId(stDO.getId());
                            sampleItem.setTypeOfSample(stDO.getEntry());
                            ActionEvent.fire(anTab, Action.SAMPLE_TYPE_CHANGED, null);
                        }

                        row = new Item<Integer>(3);
                        if ("t".equals(flag)) {
                            tMan = testService.call("fetchById", testPanelId);
                            tVDO = tMan.getTest();
                            ttosMan = tMan.getSampleTypes();
                            for (i = 0; i < ttosMan.count(); i++) {
                                ttosDO = ttosMan.getTypeAt(i);
                                if (ttosDO.getTypeOfSampleId().equals(sampleItem.getTypeOfSampleId())) {
                                    row.setKey(tVDO.getId());
                                    row.setCell(0,tVDO.getName());
                                    row.setCell(1,tVDO.getMethodName());
                                    row.setCell(2,tVDO.getDescription());
                                    row.setData(tVDO.getMethodId());
                                    break;
                                }
                            }
                            if (i == ttosMan.count()) {
                                feE = new FormErrorException("testMethodSampleTypeMismatch",
                                                             tVDO.getName()+", "+tVDO.getMethodName(),
                                                             sampleItem.getTypeOfSample());
                                com.google.gwt.user.client.Window.alert(feE.getMessage());
                            }
                        } else if ("p".equals(flag)) {
                            pMan = panelService.call("fetchById", testPanelId);
                            pDO = pMan.getPanel();
                            row.setKey(pDO.getId());
                            row.setCell(0,pDO.getName());
                            row.setCell(2,pDO.getDescription());
                        }
                        model = new ArrayList<Item<Integer>>();
                        model.add(row);
                        test.setModel(model);
                        test.setSelectedIndex(0);
                        test.validateValue();
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                    }
                    event.cancel();
                }
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int                     i;
                Integer                 sampleType;
                ArrayList<QueryData>    fields;
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> autoList;
                Query                   query;
                QueryData               field;
                QueryFieldUtil          parser;
                Item<Integer>           row;
                TestMethodVO            autoDO;

                sampleType = sampleItem.getTypeOfSampleId();
                if (sampleType == null) {
                    window.setError(consts.get("sampleItemTypeRequired"));
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                field = new QueryData();
                field.setQuery(parser.getParameter().get(0));
                fields.add(field);

                field = new QueryData();
                field.setQuery(String.valueOf(sampleItem.getTypeOfSampleId()));
                fields.add(field);

                query.setFields(fields);

                try {
                    autoList = service.callList("getTestMethodMatches", query);
                    model = new ArrayList<Item<Integer>>();

                    for (i = 0; i < autoList.size(); i++ ) {
                        autoDO = autoList.get(i);

                        row = new Item<Integer>(autoDO.getTestId(),
                                                              autoDO.getTestName(),
                                                              autoDO.getMethodName(),
                                                              autoDO.getTestDescription());
                        row.setData(autoDO.getMethodId());

                        model.add(row);
                    }

                    test.showAutoMatches(model);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        method = (AutoComplete)def.getWidget(SampleMeta.getAnalysisMethodName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setValue(analysis.getMethodId(), analysis.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.setEnabled(canEdit() && EnumSet.of(State.QUERY).contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown)def.getWidget(SampleMeta.getAnalysisStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(analysis.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setStatusId(event.getValue());
                if (!State.QUERY.equals(state))
                    ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                int                      i;
                ArrayList<Item<Integer>> model;
                Item<Integer>            r;

                statusId.setEnabled(canEdit() &&
                                EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                .contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);

                model = statusId.getModel();
                for (i = 0; i < model.size(); i++) {
                    r = model.get(i);
                    if (!analysisInitiatedId.equals(r.getKey()) && !analysisOnHoldId.equals(r.getKey()) && 
                        !analysisRequeueId.equals(r.getKey()) && !analysisCompletedId.equals(r.getKey()) && 
                        !analysisLoggedInId.equals(r.getKey()) && !State.QUERY.equals(event.getState())) 
                        r.setEnabled(false);
                    else
                        r.setEnabled(true);
                }
            }
        });
        
        statusId.getPopupContext().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {           
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {                
                Item<Integer> r;
                
                r = statusId.getModel().get(event.getItem());
                if (!r.isEnabled())
                    event.cancel();
            }
        });

        revision = (TextBox)def.getWidget(SampleMeta.getAnalysisRevision());
        addScreenHandler(revision, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(Util.toString(analysis.getRevision()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                revision.setQueryMode(event.getState() == State.QUERY);
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
                isReportable.setEnabled(canEdit() &&
                                    EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sectionId = (Dropdown<Integer>)def.getWidget(SampleMeta.getAnalysisSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<Item<Integer>> sections;

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
                sectionId.setValue(analysis.getSectionId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.setEnabled(canEdit() &&
                                 EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                sectionId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        unitOfMeasureId = (Dropdown<Integer>)def.getWidget(SampleMeta.getAnalysisUnitOfMeasureId());
        addScreenHandler(unitOfMeasureId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<Item<Integer>> units;

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
                unitOfMeasureId.setValue(analysis.getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unitOfMeasureId.setEnabled(canEdit() &&
                                       EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
                unitOfMeasureId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        startedDate = (Calendar)def.getWidget(SampleMeta.getAnalysisStartedDate());
        addScreenHandler(startedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startedDate.setValue(analysis.getStartedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startedDate.setEnabled(canEdit() &&
                                   EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                startedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        completedDate = (Calendar)def.getWidget(SampleMeta.getAnalysisCompletedDate());
        addScreenHandler(completedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                completedDate.setValue(analysis.getCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedDate.setEnabled(canEdit() &&
                                     EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                     .contains(event.getState()));
                completedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        releasedDate = (Calendar)def.getWidget(SampleMeta.getAnalysisReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(analysis.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                releasedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        printedDate = (Calendar)def.getWidget(SampleMeta.getAnalysisPrintedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                printedDate.setValue(analysis.getPrintedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setPrintedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printedDate.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                printedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        samplePrep = (AutoComplete)def.getWidget(SampleMeta.getAnalysisSamplePrep());
        addScreenHandler(samplePrep, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                String testMethodString = null;

                if (analysis.getPreAnalysisTest() != null)
                    testMethodString = analysis.getPreAnalysisTest() + " : " +
                                       analysis.getPreAnalysisMethod();

                samplePrep.setValue(analysis.getPreAnalysisId(), testMethodString);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                SampleDataBundle anBundle;
                SampleItemManager itemMan;

                try {
                    if (event.getValue() == null) {
                        manager.unlinkPrepTest(bundle.getAnalysisIndex());

                    } else {
                        anBundle = (SampleDataBundle)samplePrep.getSelectedItem().getData();
                        itemMan = bundle.getSampleManager().getSampleItems();

                        itemMan.linkPrepTest(bundle.getSampleItemIndex(),
                                             bundle.getAnalysisIndex(),
                                             anBundle.getSampleItemIndex(),
                                             anBundle.getAnalysisIndex());
                    }

                    ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
                    DataChangeEvent.fire(anTab, statusId);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert("samplePrep valueChange: " + e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplePrep.setEnabled(canEdit() &&
                                  EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                samplePrep.setQueryMode(event.getState() == State.QUERY);
            }
        });

        worksheetTable = (Table)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<Row>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetTable.setModel(getWorksheetTableModel());
            }

            public void onValueChange(ValueChangeEvent<Row> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.setEnabled(true);
            }
        });

        worksheetTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                selectWkshtButton.setEnabled(true);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        analysisUserTable = (Table)def.getWidget("analysisUserTable");
        addScreenHandler(analysisUserTable, new ScreenEventHandler<Row>() {
            public void onDataChange(DataChangeEvent event) {
                analysisUserTable.setModel(getAnalysisUserTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisUserTable.setEnabled(canEdit() &&
                                         EnumSet.of(State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                analysisUserTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete userName = (AutoComplete)analysisUserTable.getColumnWidget(0);
                                      
        analysisUserTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisUserViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = analysisUserTable.getValueAt(r, c);

                try {
                    data = manager.getAnalysisUserAt(bundle.getAnalysisIndex())
                                  .getAnalysisUserAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        Item<Integer> selectedRow = userName.getSelectedItem();

                        if (selectedRow != null) {
                            data.setSystemUserId((Integer)selectedRow.getKey());
                            data.setSystemUser((String)selectedRow.getCell(0));
                        } else {
                            data.setSystemUserId(null);
                            data.setSystemUser(null);
                        }
                        break;
                    case 1:
                        data.setActionId((Integer)val);
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        analysisUserTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAnalysisUserAt(bundle.getAnalysisIndex())
                           .removeAnalysisUserAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }

            }
        });

        userName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                try {
                    users = userService.callList("fetchByLoginName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    userName.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.toString());
                }
            }
        });
        
        userActionId = ((Dropdown<Integer>)analysisUserTable.getColumnWidget(1));
        
        userActionId.getPopupContext().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {           
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {                
                Item<Integer> r;
                
                r = userActionId.getModel().get(event.getItem());                
                if (!r.isEnabled())
                    event.cancel();
            }
        });

        selectWkshtButton = (Button)def.getWidget("selectWkshtButton");
        addScreenHandler(selectWkshtButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                WorksheetCompletionScreen worksheetScreen;
                Row row;
                try {
                    row = worksheetTable.getRowAt(worksheetTable.getSelectedRow());
                    worksheetScreen = new WorksheetCompletionScreen((Integer)row.getData());

                    ModalWindow modal = new ModalWindow();
                    modal.setName(consts.get("worksheetCompletion"));
                    modal.setContent(worksheetScreen);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert("openCompletionScreen: " + e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectWkshtButton.setEnabled(false);
            }
        });

        addActionButton = (Button)def.getWidget("addActionButton");
        addScreenHandler(addActionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                analysisUserTable.addRow();
                n = analysisUserTable.getRowCount() - 1;
                analysisUserTable.selectRowAt(n);
                analysisUserTable.scrollToVisible(n);
                analysisUserTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addActionButton.setEnabled(canEdit() && 
                                       EnumSet.of(State.ADD, State.UPDATE)
                                       .contains(event.getState()));
            }
        });

        removeActionButton = (Button)def.getWidget("removeActionButton");
        addScreenHandler(removeActionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                Integer action;

                r = analysisUserTable.getSelectedRow();
                
                if (r > -1 && analysisUserTable.getRowCount() > 0) {
                    try {
                        action = manager.getAnalysisUserAt(bundle.getAnalysisIndex()).getAnalysisUserAt(r).getActionId();
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                        return;
                    }
                    
                    if (!actionReleasedId.equals(action))
                        analysisUserTable.removeRowAt(r);  
                    else 
                        window.setError(consts.get("analysisUserActionException"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeActionButton.setEnabled(canEdit() &&
                                          EnumSet.of(State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        samplePrep.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                Item<Integer> row;
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
                    model = new ArrayList<Item<Integer>>();
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
                                anDO.getTestName().startsWith(match)) {
                                row = new Item<Integer>(
                                                       anDO.getId(),
                                                       anDO.getTestName() +
                                                                       " : " +
                                                                       anDO.getMethodName() +
                                                                       " | " +
                                                                       formatTreeString(itemDO.getTypeOfSample()));
                                row.setData(anMan.getBundleAt(j));
                                model.add(row);
                            }
                        }
                    }

                    samplePrep.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert("prep getMatches: " + e.getMessage());
                }
            }
        });
    }

    private ArrayList<Item<Integer>> getSectionsModel(TestSectionManager sectionManager) {
        ArrayList<Item<Integer>> model;
        TestSectionViewDO section;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));

        if (sectionManager != null) {
            for (int i = 0; i < sectionManager.count(); i++ ) {
                section = sectionManager.getSectionAt(i);
                model.add(new Item<Integer>(section.getSectionId(), section.getSection()));
            }
        }

        return model;
    }

    private ArrayList<Item<Integer>> getUnitsModel(TestTypeOfSampleManager man, Integer sampleTypeId) {
        ArrayList<Item<Integer>> model;
        DictionaryDO entry;
        TestTypeOfSampleDO sampleType;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        if (man != null) {
            try {
                for (int i = 0; i < man.count(); i++ ) {
                    sampleType = man.getTypeAt(i);
                    if (sampleType.getUnitOfMeasureId() != null &&
                        sampleTypeId.equals(sampleType.getTypeOfSampleId())) {
                        entry = DictionaryCache.getEntryFromId(sampleType.getUnitOfMeasureId());
                        model.add(new Item<Integer>(entry.getId(), entry.getEntry()));
                    }
                }
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                return new ArrayList<Item<Integer>>();
            }
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        Item<Integer> r;
        
        try {
            analysisInitiatedId = DictionaryCache.getIdFromSystemName("analysis_initiated"); 
            analysisOnHoldId = DictionaryCache.getIdFromSystemName("analysis_on_hold");
            analysisRequeueId = DictionaryCache.getIdFromSystemName("analysis_requeue");
            analysisCompletedId = DictionaryCache.getIdFromSystemName("analysis_completed");
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            analysisInPrepId = DictionaryCache.getIdFromSystemName("analysis_inprep");
            actionReleasedId = DictionaryCache.getIdFromSystemName("an_user_ac_released");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        statusId.setModel(model);

        // section full dropdown model
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (SectionDO s : SectionCache.getSectionList())
            model.add(new Item<Integer>(s.getId(), s.getName()));

        fullSectionModel = model;
        sectionId.setModel(model);
        sectionModel = new HashMap<Integer, ArrayList<Item<Integer>>>();
        fullSectionShown = true;

        // unit full dropdown model
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("unit_of_measure"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));
        
        fullUnitModel = model;
        unitOfMeasureId.setModel(model);
        unitModel = new HashMap<String, ArrayList<Item<Integer>>>();
        fullUnitShown = true;

        // analysis user action
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("user_action")) {
            r = new Item<Integer>(d.getId(), d.getEntry());
            if(actionReleasedId.equals(d.getId())) 
                r.setEnabled(false);
            model.add(r);
        }
        
        userActionId.setModel(model);

        // worksheet status
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("worksheet_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)worksheetTable.getColumnWidget(2)).setModel(model);

    }

    private void testChanged(Integer id) {
        Item<Integer> selectedRow;

        try {
            selectedRow = test.getSelectedItem();

            //
            // If method is empty, the selected row is a panel
            //
            if (selectedRow != null && selectedRow.getKey() != null) {
                if (selectedRow.getCell(1) == null)
                    ActionEvent.fire(this, Action.PANEL_ADDED, id);
                else {
                    ActionEvent.fire(this, Action.ANALYSIS_ADDED, id);
                }
            } else
                ActionEvent.fire(this, Action.ANALYSIS_ADDED, null);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }

    private ArrayList<Row> getWorksheetTableModel() {
        ArrayList<Row> model;
        ArrayList<WorksheetViewDO> worksheets;
        WorksheetViewDO wksht;
        AnalysisViewDO anDO;
        Row row;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        anDO = manager.getAnalysisAt(bundle.getAnalysisIndex());

        if (anDO.getId() > 0) {
            try {
                worksheets = worksheetService.callList("fetchByAnalysisId", anDO.getId());

                for (int i = 0; i < worksheets.size(); i++ ) {
                    wksht = worksheets.get(i);

                    row = new Row(4);
                    row.setData(wksht.getId());
                    row.setCell(0,wksht.getId());
                    row.setCell(1,wksht.getCreatedDate());
                    row.setCell(2,wksht.getStatusId());
                    row.setCell(3,wksht.getSystemUser());
                    model.add(row);

                }
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("getWorksheetTableModel: " + e.getMessage());
            }
        }

        return model;
    }

    private ArrayList<Row> getAnalysisUserTableModel() {
        ArrayList<Row> model;
        AnalysisUserManager userMan;
        AnalysisUserViewDO userDO;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            userMan = manager.getAnalysisUserAt(bundle.getAnalysisIndex());

            for (int iter = 0; iter < userMan.count(); iter++ ) {
                userDO = userMan.getAnalysisUserAt(iter);

                Row row = new Row(2);
                row.setData(userDO.getId());

                row.setCell(0,new AutoCompleteValue(userDO.getSystemUserId(),
                                                          userDO.getSystemUser()));
                row.setCell(1,userDO.getActionId());
                
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("getAnalysisUserTableModel: " + e.getMessage());
            return null;
        }

        return model;
    }

    private boolean canEdit() {
        SectionPermission perm;
        SectionViewDO     sectionVDO;
        
        if (analysis != null) {
            if (analysis.getSectionId() == null)
                return true;
            
            try {
                sectionVDO = SectionCache.getSectionFromId(analysis.getSectionId());
                perm = OpenELIS.getSystemUserPermission().getSection(sectionVDO.getName());
                return !analysisCancelledId.equals(analysis.getStatusId()) &&
                       !analysisReleasedId.equals(analysis.getStatusId()) &&
                       perm != null &&
                       (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                com.google.gwt.user.client.Window.alert("canEdit:" + anyE.getMessage());
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
            bundle = data;
            loaded = false;
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("analysisTab setData: " + e.getMessage());
        }
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}