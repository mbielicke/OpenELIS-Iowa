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
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.TestSectionManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalysisTab extends Screen implements HasActionHandlers<AnalysisTab.Action> {
    public enum Action {
        ANALYSIS_ADDED, PANEL_ADDED, ORDER_LIST_ADDED, CHANGED_DONT_CHECK_PREPS, ITEM_CHANGED
    };

    private boolean loaded;

    protected AutoComplete<Integer> test, method, samplePrep;
    protected Dropdown<Integer>     sectionId, unitOfMeasureId, statusId;
    protected CheckBox              isReportable;
    protected TextBox               revision;
    protected CalendarLookUp        startedDate, completedDate, releasedDate, printedDate;

    protected ArrayList<TableDataRow> fullSectionModel, fullUnitModel;
    protected HashMap<Integer, ArrayList<TableDataRow>> sectionModel, unitModel;

    protected boolean                                   fullSectionShown, fullUnitShown;
    protected int                                       analysisIndex = -1;
    protected AnalysisManager                           manager;
    protected SampleDataBundle                          bundle;
    protected AnalysisViewDO                            analysis;
    protected SampleItemViewDO                          sampleItem;

    protected Integer                                   analysisLoggedInId, analysisCancelledId,
                    analysisReleasedId, analysisInPrepId, changedTestId;
    
    private Confirm                                     changeTestConfirm;
    protected ScreenService                             panelService;

    public AnalysisTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService(
                                    "OpenELISServlet?service=org.openelis.modules.analysis.server.AnalysisService");
        panelService = new ScreenService(
                                         "controller?service=org.openelis.modules.panel.server.PanelService");

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
                                        test.setSelection(analysis.getTestId(),
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
                test.enable(canEdit() &&
                            EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                   .contains(event.getState()));
                test.setQueryMode(event.getState() == State.QUERY);
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Query query;
                QueryData field;
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                ArrayList<TestMethodVO> autoList;
                TestMethodVO autoDO;
                ArrayList<TableDataRow> model;
                Integer sampleType;

                sampleType = sampleItem.getTypeOfSampleId();

                if (sampleType == null) {
                    window.setError(consts.get("sampleItemTypeRequired"));
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                field = new QueryData();
                field.query = parser.getParameter().get(0);
                fields.add(field);

                field = new QueryData();
                field.query = String.valueOf(sampleItem.getTypeOfSampleId());
                fields.add(field);

                query.setFields(fields);

                try {
                    autoList = service.callList("getTestMethodMatches", query);
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < autoList.size(); i++ ) {
                        autoDO = autoList.get(i);

                        TableDataRow row = new TableDataRow(autoDO.getTestId(),
                                                            autoDO.getTestName(),
                                                            autoDO.getMethodName(),
                                                            autoDO.getTestDescription());
                        row.data = autoDO.getMethodId();

                        model.add(row);
                    }

                    test.showAutoMatches(model);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        method = (AutoComplete)def.getWidget(SampleMeta.getAnalysisMethodName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setSelection(analysis.getMethodId(), analysis.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(canEdit() && EnumSet.of(State.QUERY).contains(event.getState()));
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
                ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(canEdit() && EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
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
                revision.enable(EnumSet.of(State.QUERY).contains(event.getState()));
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
                isReportable.enable(canEdit() &&
                                    EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
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
                                sections = getSectionsModel(manager.getTestAt(
                                                                              bundle.getAnalysisIndex())
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
                sectionId.enable(canEdit() &&
                                 EnumSet.of(State.ADD, State.UPDATE, State.QUERY).contains(event.getState()));
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
                        units = unitModel.get(analysis.getTestId());

                        if (units == null) {
                            try {
                                units = getUnitsModel(manager.getTestAt(bundle.getAnalysisIndex())
                                                             .getSampleTypes(),
                                                      sampleItem.getTypeOfSampleId());
                            } catch (Exception e) {
                                units = null;
                            }

                            unitModel.put(analysis.getTestId(), units);
                        }
                    }
                    unitOfMeasureId.setModel(units);
                }

                unitOfMeasureId.setSelection(analysis.getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unitOfMeasureId.enable(canEdit() &&
                                       EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
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
                startedDate.enable(EnumSet.of(State.QUERY).contains(event.getState()));
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
                completedDate.enable(EnumSet.of(State.QUERY).contains(event.getState()));
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
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
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
                if (event.getValue() == null) {
                    manager.unlinkPrepTest(bundle.getAnalysisIndex());

                } else {
                    anBundle = (SampleDataBundle)samplePrep.getSelection().data;
                    manager.linkPrepTest(bundle.getAnalysisIndex(), anBundle.getAnalysisIndex());
                }

                ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS, null);
                DataChangeEvent.fire(anTab, statusId);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplePrep.enable(canEdit() &&
                                  EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                samplePrep.setQueryMode(event.getState() == State.QUERY);
            }
        });

        samplePrep.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AnalysisViewDO anDO;
                SampleDataBundle anBundle;
                Integer currentId;
                String match;

                currentId = analysis.getId();
                match = event.getMatch();
                model = new ArrayList<TableDataRow>();

                for (int i = 0; i < manager.count(); i++ ) {
                    anDO = manager.getAnalysisAt(i);

                    if ( !currentId.equals(anDO.getId()) && anDO.getTestName().startsWith(match)) {
                        row = new TableDataRow(anDO.getId(), anDO.getTestName() + " : " +
                                                             anDO.getMethodName());
                        row.data = manager.getBundleAt(i);
                        model.add(row);
                    }
                }

                samplePrep.showAutoMatches(model);
            }
        });
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
                    if (sampleTypeId.equals(sampleType.getTypeOfSampleId())) {
                        entry = DictionaryCache.getEntryFromId(sampleType.getUnitOfMeasureId());

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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            analysisInPrepId = DictionaryCache.getIdFromSystemName("analysis_inprep");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        statusId.setModel(model);

        // section full dropdown model
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (SectionDO s : SectionCache.getSectionList())
            model.add(new TableDataRow(s.getId(), s.getName()));

        fullSectionModel = model;
        sectionId.setModel(model);
        sectionModel = new HashMap<Integer, ArrayList<TableDataRow>>();
        fullSectionShown = true;

        // unit full dropdown model
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("unit_of_measure"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        fullUnitModel = model;
        unitOfMeasureId.setModel(model);
        unitModel = new HashMap<Integer, ArrayList<TableDataRow>>();
        fullUnitShown = true;
    }

    private void testChanged(Integer id) {
        TableDataRow selectedRow;

        try {
            selectedRow = test.getSelection();

            //
            // If method is empty, the selected row is a panel
            //
            if (selectedRow != null && selectedRow.key != null) {
                if (selectedRow.cells.get(1).value == null)
                    ActionEvent.fire(this, Action.PANEL_ADDED, id);
                else {
                    ActionEvent.fire(this, Action.ANALYSIS_ADDED, id);
                }
            } else
                ActionEvent.fire(this, Action.ANALYSIS_ADDED, null);

        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private boolean canEdit() {
        return (analysis != null && !analysisCancelledId.equals(analysis.getStatusId()) && !analysisReleasedId.equals(analysis.getStatusId()));
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
            Window.alert("analysisTab setData: " + e.getMessage());
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
