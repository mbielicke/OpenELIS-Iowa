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
package org.openelis.modules.sample1.client;

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.cache.CacheProvider;
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
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalysisTabUI extends Screen {

    @UiTemplate("AnalysisTab.ui.xml")
    interface AnalysisTabUIBinder extends UiBinder<Widget, AnalysisTabUI> {
    };

    private static AnalysisTabUIBinder uiBinder = GWT.create(AnalysisTabUIBinder.class);

    @UiField
    protected TextBox<String>          testName;

    @UiField
    protected AutoComplete             methodName, samplePrep, panel;

    @UiField
    protected Dropdown<Integer>        section, unitOfMeasure, status;

    @UiField
    protected CheckBox                 isReportable, isPreliminary;

    @UiField
    protected TextBox<Integer>         revision;

    @UiField
    protected Calendar                 startedDate, completedDate, releasedDate, printedDate;

    @UiField
    protected Table                    worksheetTable, userTable;

    @UiField
    protected Button                   selectWkshtButton, addActionButton, removeActionButton;

    protected Screen                   parentScreen;

    protected boolean                  canEdit, isVisible;

    protected SampleManager1           manager;

    protected AnalysisViewDO           analysis;

    protected SampleItemViewDO         sampleItem;

    protected String                   displayedUid;

    protected ArrayList<Item<Integer>> allUnitsModel, allSectionsModel;

    public AnalysisTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer> r;

        addScreenHandler(testName, SampleMeta.getAnalysisTestName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testName.setValue(getTestName());
            }

            public void onStateChange(StateChangeEvent event) {
                testName.setEnabled(isState(QUERY));
                testName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? methodName : userTable;
            }
        });

        addScreenHandler(methodName,
                         SampleMeta.getAnalysisMethodName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 methodName.setValue(getMethodId(), getMethodName());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {

                             }

                             public void onStateChange(StateChangeEvent event) {
                                 methodName.setEnabled(isState(QUERY) ||
                                                       (isState(ADD, UPDATE) && canEdit));
                                 methodName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? status : testName;
                             }
                         });

        methodName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<QueryData> fields;
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> tests;
                Query query;
                QueryData field;
                Item<Integer> row;

                if (sampleItem.getTypeOfSampleId() == null) {
                    window.setError(Messages.get().sampleItemTypeRequired());
                    return;
                }

                field = new QueryData();
                try {
                    field.setQuery(QueryFieldUtil.parseAutocomplete(analysis.getTestName()));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();
                fields.add(field);

                field = new QueryData();
                field.setQuery(String.valueOf(sampleItem.getTypeOfSampleId()));
                fields.add(field);

                query.setFields(fields);
                query.setRowsPerPage(100);

                try {
                    tests = PanelService.get().fetchByNameSampleTypeWithTests(query);
                    model = new ArrayList<Item<Integer>>();

                    for (TestMethodVO t : tests) {
                        row = new Item<Integer>(t.getMethodId(),
                                                t.getMethodName(),
                                                t.getMethodDescription());
                        row.setData(t);
                        model.add(row);
                    }
                    methodName.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addScreenHandler(status, SampleMeta.getAnalysisStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                int i;
                ArrayList<Item<Integer>> model;
                Item<Integer> r;

                status.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit));
                status.setQueryMode(isState(QUERY));

                model = status.getModel();
                // TODO if necessary, change this code
                for (i = 0; i < model.size(); i++ ) {
                    r = model.get(i);
                    if ( !Constants.dictionary().ANALYSIS_INITIATED.equals(r.getKey()) &&
                        !Constants.dictionary().ANALYSIS_ON_HOLD.equals(r.getKey()) &&
                        !Constants.dictionary().ANALYSIS_REQUEUE.equals(r.getKey()) &&
                        !Constants.dictionary().ANALYSIS_LOGGED_IN.equals(r.getKey()) &&
                        !isState(QUERY))
                        r.setEnabled(false);
                    else
                        r.setEnabled(true);
                }
            }

            public Widget onTab(boolean forward) {
                return forward ? section : methodName;
            }
        });

        addScreenHandler(isPreliminary,
                         SampleMeta.getAnalysisIsPreliminary(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isPreliminary.setValue(getIsPreliminary());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsPreliminary(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isPreliminary.setEnabled(isState(QUERY) ||
                                                          (isState(ADD, UPDATE) && canEdit));
                                 isPreliminary.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isReportable : section;
                             }
                         });

        addScreenHandler(isReportable,
                         SampleMeta.getAnalysisIsReportable(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isReportable.setValue(getIsReportable());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsReportable(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isReportable.setEnabled(isState(QUERY) ||
                                                         (isState(ADD, UPDATE) && canEdit));
                                 isReportable.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? unitOfMeasure : isPreliminary;
                             }
                         });

        addScreenHandler(section,
                         SampleMeta.getAnalysisSectionId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*
                                  * For all states other than Display and Query,
                                  * the model depends on the analysis showing in
                                  * the tab, which is known for certain only
                                  * before this event is fired. So for those
                                  * states, the model is generated and set here.
                                  */
                                 if ( !isState(DISPLAY, QUERY))
                                     section.setModel(getSectionsModel());
                                 section.setValue(getSectionId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSection(event.getValue(), section.getDisplay());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 /*
                                  * The model shown in this dropdown in Query as
                                  * well as Display state is the list of all
                                  * sections in the system. Also, In Query
                                  * state, the model needs to be set in this
                                  * dropdown before it can be switched to query
                                  * mode. So for those states, the model is set
                                  * here.
                                  */
                                 if (isState(DISPLAY, QUERY))
                                     section.setModel(allSectionsModel);
                                 section.setEnabled(isState(QUERY) ||
                                                      (isState(ADD, UPDATE) && canEdit));
                                 section.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isPreliminary : status;
                             }
                         });

        addScreenHandler(unitOfMeasure,
                         SampleMeta.getAnalysisUnitOfMeasureId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*
                                  * For all states other than Display and Query,
                                  * the model depends on the analysis showing in
                                  * the tab, which is known for certain only
                                  * before this event is fired. So for those
                                  * states, the model is generated and set here.
                                  */
                                 if ( !isState(DISPLAY, QUERY))
                                     unitOfMeasure.setModel(getUnitsModel());
                                 unitOfMeasure.setValue(getUnitOfMeasureId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setUnitOfMeasureId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 /*
                                  * The model shown in this dropdown in Query as
                                  * well as Display state is the list of all
                                  * units in the system. Also, In Query
                                  * state, the model needs to be set in this
                                  * dropdown before it can be switched to query
                                  * mode. So for those states, the model is set
                                  * here.
                                  */
                                 if (isState(DISPLAY, QUERY))
                                     unitOfMeasure.setModel(allUnitsModel);
                                 unitOfMeasure.setEnabled(isState(QUERY) ||
                                                            (isState(ADD, UPDATE) && canEdit));
                                 unitOfMeasure.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? startedDate : isReportable;
                             }
                         });

        addScreenHandler(startedDate,
                         SampleMeta.getAnalysisStartedDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 startedDate.setValue(getStartedDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setStartedDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 startedDate.setEnabled(isState(QUERY) ||
                                                        (isState(ADD, UPDATE) && canEdit));
                                 startedDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? completedDate : unitOfMeasure;
                             }
                         });

        addScreenHandler(completedDate,
                         SampleMeta.getAnalysisCompletedDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 completedDate.setValue(getCompletedDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setCompletedDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 completedDate.setEnabled(isState(QUERY) ||
                                                          (isState(ADD, UPDATE) && canEdit));
                                 completedDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? releasedDate : startedDate;
                             }
                         });

        addScreenHandler(releasedDate,
                         SampleMeta.getAnalysisReleasedDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 releasedDate.setValue(getReleasedDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setReleasedDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 releasedDate.setEnabled(isState(QUERY) ||
                                                         (isState(ADD, UPDATE) && canEdit));
                                 releasedDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? printedDate : completedDate;
                             }
                         });

        addScreenHandler(printedDate,
                         SampleMeta.getAnalysisPrintedDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 printedDate.setValue(getPrintedDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPrintedDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 printedDate.setEnabled(isState(QUERY));
                                 printedDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? revision : releasedDate;
                             }
                         });

        addScreenHandler(samplePrep,
                         SampleMeta.getAnalysisSamplePrep(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 String tmLabel;

                                 tmLabel = null;

                                 if (getPreAnalysisTest() != null)
                                     tmLabel = getPreAnalysisTest() + " : " +
                                               getPreAnalysisMethod();

                                 samplePrep.setValue(getPreAnalysisId(), tmLabel);
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 samplePrep.setEnabled(isState(ADD, UPDATE) && canEdit);
                                 samplePrep.setQueryMode(false);
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

            }
        });

        addScreenHandler(revision, SampleMeta.getAnalysisRevision(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(getRevision());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                revision.setEnabled(isState(QUERY));
                revision.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? worksheetTable : printedDate;
            }
        });

        addScreenHandler(panel, SampleMeta.getAnalysisPanelId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                panel.setValue(getPanelId(), getPanelName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                // TODO make it like neonatal
                setPanelId(event.getValue());
                setPanelName(panel.getValue().getDisplay());
            }

            public void onStateChange(StateChangeEvent event) {
                panel.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit));
                panel.setQueryMode(isState(QUERY));
            }
        });

        panel.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                ArrayList<Item<Integer>> model;
                ArrayList<PanelDO> list;
                Item<Integer> row;
                PanelDO data;

                try {
                    list = PanelService.get()
                                       .fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();

                    for (i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        row = new Item<Integer>(data.getId(), data.getName());
                        model.add(row);
                    }
                    panel.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert("panel getMatches: " + e.getMessage());
                }
            }
        });

        addScreenHandler(worksheetTable, "worksheetTable", new ScreenHandler<Item<Integer>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetTable.setModel(getWorksheetTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? userTable : revision;
            }
        });

        addScreenHandler(userTable, "userTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                userTable.setModel(getAnalysisUserTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                userTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? testName : worksheetTable;
            }
        });

        addScreenHandler(selectWkshtButton, "selectWkshtButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectWkshtButton.setEnabled(false);
            }
        });

        addScreenHandler(addActionButton, "addActionButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                userTable.addRow();
                n = userTable.getRowCount() - 1;
                userTable.selectRowAt(n);
                // TODO change this code
                // analysisUserTable.scrollToSelection();
                userTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent event) {
                addActionButton.setEnabled( (isState(ADD, UPDATE) && canEdit));
            }
        });

        addScreenHandler(removeActionButton, "removeActionButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeActionButton.setEnabled( (isState(ADD, UPDATE) && canEdit));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = manager.getUid(analysis);
                else
                    uid = null;
                displayAnalysis(uid);
            }
        });

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        status.setModel(model);

        // analysis user action
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("user_action")) {
            r = new Item<Integer>(d.getId(), d.getEntry());
            if (Constants.dictionary().AN_USER_AC_RELEASED.equals(d.getId()))
                r.setEnabled(false);
            model.add(r);
        }

        allUnitsModel = new ArrayList<Item<Integer>>();
        allUnitsModel.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            if ("Y".equals(d.getIsActive()))
                allUnitsModel.add(new Item<Integer>(d.getId(), d.getEntry()));
        }

        allSectionsModel = new ArrayList<Item<Integer>>();
        allSectionsModel.add(new Item<Integer>(null, ""));
        for (SectionDO s : SectionCache.getList())
            allSectionsModel.add(new Item<Integer>(s.getId(), s.getName()));

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }

                displayAnalysis(uid);
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                switch (event.getAction()) {
                    case SAMPLE_TYPE_CHANGED:
                        unitOfMeasure.setModel(getUnitsModel());
                        unitOfMeasure.setValue(getUnitOfMeasureId());
                        break;
                    case TEST_CHANGED:
                        break;
                }
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public boolean validate() {
        /*
         * validate only if there's data loaded in the tab
         */
        if (displayedUid == null)
            return true;
        return super.validate();
    }

    /**
     * Returns the model for sections dropdown. In add, update, sections
     * specific to the analysis' test are returned.
     */
    private ArrayList<Item<Integer>> getSectionsModel() {
        ArrayList<Item<Integer>> model;
        TestSectionManager tsm;
        TestSectionViewDO ts;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));

        if (analysis != null && isState(ADD, UPDATE)) {
            /*
             * create the model from the sections associated with the analysis'
             * test
             */
            tsm = getTestManager(analysis.getTestId()).getTestSections();
            if (tsm != null) {
                for (int i = 0; i < tsm.count(); i++ ) {
                    ts = tsm.getSectionAt(i);
                    model.add(new Item<Integer>(ts.getSectionId(), ts.getSection()));
                }
            }
        }

        return model;
    }

    /**
     * Returns the model for units dropdown. In add, update, units specific to
     * the sample item's sample type are returned.
     */
    private ArrayList<Item<Integer>> getUnitsModel() {
        ArrayList<Item<Integer>> model;
        DictionaryDO d;
        TestTypeOfSampleDO type;
        TestTypeOfSampleManager tts;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        if (analysis != null && isState(ADD, UPDATE)) {
            try {
                /*
                 * create the model from the units associated with the sample
                 * item's sample type
                 */
                tts = getTestManager(analysis.getTestId()).getSampleTypes();
                for (int i = 0; i < tts.count(); i++ ) {
                    type = tts.getTypeAt(i);
                    if (type.getUnitOfMeasureId() != null &&
                        DataBaseUtil.isSame(sampleItem.getTypeOfSampleId(),
                                            type.getTypeOfSampleId())) {
                        d = DictionaryCache.getById(type.getUnitOfMeasureId());
                        model.add(new Item<Integer>(d.getId(), d.getEntry()));
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
        return model;
    }

    private ArrayList<Row> getWorksheetTableModel() {
        ArrayList<Row> model;
        ArrayList<WorksheetViewDO> ws;
        Row row;

        model = new ArrayList<Row>();
        if (analysis == null)
            return model;

        if (analysis.getId() > 0) {
            try {
                ws = WorksheetService.get().fetchByAnalysisId(analysis.getId());

                for (WorksheetViewDO w : ws) {
                    row = new Row(4);
                    row.setCell(0, w.getId());
                    row.setCell(1, w.getCreatedDate());
                    row.setCell(2, w.getStatusId());
                    row.setCell(3, w.getSystemUser());
                    model.add(row);
                }
            } catch (Exception e) {
                Window.alert("getWorksheetTableModel: " + e.getMessage());
            }
        }
        return model;
    }

    private ArrayList<Row> getAnalysisUserTableModel() {
        ArrayList<Row> model;
        AnalysisUserViewDO user;
        Row row;

        model = new ArrayList<Row>();
        if (analysis == null)
            return model;

        try {
            for (int i = 0; i < manager.analysisUser.count(analysis); i++ ) {
                user = manager.analysisUser.get(analysis, i);

                row = new Row(2);
                row.setCell(0, new AutoCompleteValue(user.getSystemUserId(), user.getSystemUser()));
                row.setCell(1, user.getActionId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert("getAnalysisUserTableModel: " + e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void displayAnalysis(String uid) {
        /*
         * don't redraw unless the data has changed
         */
        if (uid != null) {
            analysis = (AnalysisViewDO)manager.getObject(uid);
            sampleItem = (SampleItemViewDO)manager.getObject(manager.getSampleItemUid(analysis.getSampleItemId()));
        } else {
            analysis = null;
            sampleItem = null;
        }

        if ( !isVisible)
            return;

        if (DataBaseUtil.isDifferent(displayedUid, uid)) {
            displayedUid = uid;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    /**
     * returns the TestManager, for the specified id, from the cache maintained
     * by the parent screen
     */
    private TestManager getTestManager(Integer testId) {
        if ( ! (parentScreen instanceof CacheProvider)) {
            Window.alert("Parent screen must implement " + CacheProvider.class.toString());
            return null;
        }
        return ((CacheProvider)parentScreen).get(testId, TestManager.class);
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null && analysis != null) {
            sectId = getSectionId();
            statId = getStatusId();

            if (sectId == null) {
                canEdit = true;
                return;
            }

            try {
                sect = SectionCache.getById(sectId);
                perm = UserCache.getPermission().getSection(sect.getName());
                canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                          !Constants.dictionary().ANALYSIS_RELEASED.equals(statId) &&
                          perm != null &&
                          (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }
    }

    private Integer getRevision() {
        if (analysis != null)
            return analysis.getRevision();

        return null;
    }

    private void setRevision(Integer revision) {
        analysis.setRevision(revision);
    }

    private String getIsPreliminary() {
        if (analysis != null)
            return analysis.getIsPreliminary();

        return null;
    }

    private void setIsPreliminary(String isPreliminary) {
        analysis.setIsPreliminary(isPreliminary);
    }

    private Integer getTestId() {
        if (analysis != null)
            return analysis.getTestId();

        return null;
    }

    private String getTestName() {
        if (analysis != null)
            return analysis.getTestName();

        return null;
    }

    private Integer getMethodId() {
        if (analysis != null)
            return analysis.getMethodId();

        return null;
    }

    private String getMethodName() {
        if (analysis != null)
            return analysis.getMethodName();

        return null;
    }

    private Integer getPreAnalysisId() {
        if (analysis != null)
            return analysis.getPreAnalysisId();

        return null;
    }

    private String getPreAnalysisTest() {
        if (analysis != null)
            return analysis.getPreAnalysisTest();

        return null;
    }

    private String getPreAnalysisMethod() {
        if (analysis != null)
            return analysis.getPreAnalysisMethod();

        return null;
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    private void setSection(Integer sectionId, String sectionName) {
        analysis.setSectionId(sectionId);
        analysis.setSectionName(sectionName);
    }

    private Integer getPanelId() {
        if (analysis != null)
            return analysis.getPanelId();

        return null;
    }

    private void setPanelId(Integer panelId) {
        analysis.setPanelId(panelId);
    }

    private String getPanelName() {
        if (analysis != null)
            return analysis.getPanelName();

        return null;
    }

    private void setPanelName(String panelName) {
        analysis.setPanelName(panelName);
    }

    private String getIsReportable() {
        if (analysis != null)
            return analysis.getIsReportable();

        return null;
    }

    private void setIsReportable(String isReportable) {
        analysis.setIsReportable(isReportable);
    }

    private Integer getUnitOfMeasureId() {
        if (analysis != null)
            return analysis.getUnitOfMeasureId();

        return null;
    }

    private void setUnitOfMeasureId(Integer unitOfMeasureId) {
        analysis.setUnitOfMeasureId(unitOfMeasureId);
        bus.fireEvent(new AnalysisChangeEvent(displayedUid, AnalysisChangeEvent.Action.UNIT_CHANGED));
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private void setStatusId(Integer statusId) {
        analysis.setStatusId(statusId);
        bus.fireEvent(new AnalysisChangeEvent(displayedUid,
                                              AnalysisChangeEvent.Action.STATUS_CHANGED));
    }

    private Datetime getStartedDate() {
        if (analysis != null)
            return analysis.getStartedDate();

        return null;
    }

    private void setStartedDate(Datetime startedDate) {
        analysis.setStartedDate(startedDate);
    }

    private Datetime getCompletedDate() {
        if (analysis != null)
            return analysis.getCompletedDate();

        return null;
    }

    private void setCompletedDate(Datetime completedDate) {
        analysis.setCompletedDate(completedDate);
    }

    private Datetime getReleasedDate() {
        if (analysis != null)
            return analysis.getReleasedDate();

        return null;
    }

    private void setReleasedDate(Datetime releasedDate) {
        analysis.setReleasedDate(releasedDate);
    }

    private Datetime getPrintedDate() {
        if (analysis != null)
            return analysis.getPrintedDate();

        return null;
    }

    private void setPrintedDate(Datetime printedDate) {
        analysis.setPrintedDate(printedDate);
    }
}