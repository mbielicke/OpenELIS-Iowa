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
package org.openelis.modules.test1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.TestManager1;
import org.openelis.meta.TestMeta;
import org.openelis.modules.label.client.LabelService;
import org.openelis.modules.scriptlet.client.ScriptletService;
import org.openelis.modules.testTrailer.client.TestTrailerService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
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
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class DetailsTabUI extends Screen {

    @UiTemplate("DetailsTab.ui.xml")
    interface DetailsTabUiBinder extends UiBinder<Widget, DetailsTabUI> {
    };

    private static DetailsTabUiBinder uiBinder = GWT.create(DetailsTabUiBinder.class);

    @UiField
    protected Table                   sectionTable;

    @UiField
    protected TextBox<String>         description, reportingDescription;

    @UiField
    protected TextBox<Integer>        reportingSequence, timeTaMax, timeTaAverage, timeTaWarning,
                    timeTransit, timeHolding, labelQty;

    @UiField
    protected Dropdown<Integer>       sortingMethod, reportingMethod, testFormat, revisionMethod,
                    sectionName, sectionOption;

    @UiField
    protected AutoComplete            testTrailer, scriptlet, label;

    @UiField
    protected CheckBox                isActive, isReportable;

    @UiField
    protected Calendar                activeBegin, activeEnd;

    @UiField
    protected Button                  removeSectionButton, addSectionButton;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    protected boolean                 isVisible, redraw;

    protected TestManager1            manager;

    public DetailsTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        Item<Integer> item;
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        List<SectionViewDO> sectList;

        addScreenHandler(description, TestMeta.getDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(reportingDescription,
                         TestMeta.getReportingDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 reportingDescription.setValue(getReportingDescription());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setReportingDescription(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportingDescription.setEnabled(isState(QUERY, ADD, UPDATE));
                                 reportingDescription.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(timeTaMax, TestMeta.getTimeTaMax(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaMax.setValue(getTimeTaMax());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTimeTaMax(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                timeTaMax.setEnabled(isState(QUERY, ADD, UPDATE));
                timeTaMax.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(timeTaAverage, TestMeta.getTimeTaAverage(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaAverage.setValue(getTimeTaAverage());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTimeTaAverage(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                timeTaAverage.setEnabled(isState(QUERY, ADD, UPDATE));
                timeTaAverage.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(timeTaWarning, TestMeta.getTimeTaWarning(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaWarning.setValue(getTimeTaWarning());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTimeTaWarning(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                timeTaWarning.setEnabled(isState(QUERY, ADD, UPDATE));
                timeTaWarning.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(timeTransit, TestMeta.getTimeTransit(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTransit.setValue(getTimeTransit());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTimeTransit(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                timeTransit.setEnabled(isState(QUERY, ADD, UPDATE));
                timeTransit.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(timeHolding, TestMeta.getTimeHolding(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeHolding.setValue(getTimeHolding());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTimeHolding(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                timeHolding.setEnabled(isState(QUERY, ADD, UPDATE));
                timeHolding.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(isActive, TestMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isActive.setEnabled(isState(QUERY, ADD, UPDATE));
                isActive.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(activeBegin, TestMeta.getActiveBegin(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                activeBegin.setEnabled(isState(QUERY, ADD, UPDATE));
                activeBegin.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(activeEnd, TestMeta.getActiveEnd(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                activeEnd.setEnabled(isState(QUERY, ADD, UPDATE));
                activeEnd.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(label, TestMeta.getLabelName(), new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                label.setValue(getLabel());
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                setLabel(event);
            }

            public void onStateChange(StateChangeEvent event) {
                label.setEnabled(isState(QUERY, ADD, UPDATE));
                label.setQueryMode(isState(QUERY));
            }
        });

        label.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getLabelMatches(event.getMatch());
            }

        });

        addScreenHandler(labelQty, TestMeta.getTimeHolding(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                labelQty.setValue(getLabelQty());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setLabelQty(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                labelQty.setEnabled(isState(QUERY, ADD, UPDATE));
                labelQty.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(sectionTable, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    sectionTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                sectionTable.setEnabled(isState(QUERY, DISPLAY, ADD, UPDATE));
                sectionTable.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 9; i++ ) {
                    qd = (QueryData) ((Queryable)sectionTable.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(TestMeta.getSectionId());
                                break;
                            case 1:
                                qd.setKey(TestMeta.getSectionFlagId());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        sectionTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE, QUERY))
                    event.cancel();
            }
        });

        sectionTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, i;
                Integer val;
                String systemName;
                TestSectionViewDO data;

                r = event.getRow();
                c = event.getCol();

                val = (Integer)sectionTable.getValueAt(r, c);

                try {
                    data = manager.section.get(r);

                    switch (c) {
                        case 0:
                            data.setSectionId(val);
                            break;
                        case 1:
                            data.setFlagId(val);
                            if (val == null)
                                break;
                            systemName = DictionaryCache.getSystemNameById(val);
                            if (systemName != null) {
                                if ("test_section_default".equals(systemName)) {
                                    for (i = 0; i < manager.section.count(); i++ ) {
                                        if (i == r)
                                            continue;
                                        data = manager.section.get(i);
                                        data.setFlagId(null);
                                        sectionTable.setValueAt(i, c, null);
                                    }
                                } else {
                                    for (i = 0; i < manager.section.count(); i++ ) {
                                        data = manager.section.get(i);
                                        data.setFlagId(val);
                                        sectionTable.setValueAt(i, c, val);
                                    }
                                }
                            }
                            break;
                    }

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
            }
        });

        sectionTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.section.add(new TestSectionViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });

        sectionTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.section.remove(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeSectionButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addSectionButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addScreenHandler(isReportable, TestMeta.getIsReportable(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(getIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isReportable.setEnabled(isState(QUERY, ADD, UPDATE));
                isReportable.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(revisionMethod,
                         TestMeta.getRevisionMethodId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 revisionMethod.setValue(getRevisionMethod());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setRevisionMethod(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 revisionMethod.setEnabled(isState(QUERY, ADD, UPDATE));
                                 revisionMethod.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(sortingMethod,
                         TestMeta.getSortingMethodId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sortingMethod.setValue(getSortingMethod());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSortingMethod(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sortingMethod.setEnabled(isState(QUERY, ADD, UPDATE));
                                 sortingMethod.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(reportingMethod,
                         TestMeta.getReportingMethodId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 reportingMethod.setValue(getReportingMethod());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setReportingMethod(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportingMethod.setEnabled(isState(QUERY, ADD, UPDATE));
                                 reportingMethod.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(reportingSequence,
                         TestMeta.getReportingSequence(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 reportingSequence.setValue(getReportingSequence());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setReportingSequence(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportingSequence.setEnabled(isState(QUERY, ADD, UPDATE));
                                 reportingSequence.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(testTrailer,
                         TestMeta.getTestTrailerName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 testTrailer.setValue(getTestTrailer());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 setTestTrailer(event);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 testTrailer.setEnabled(isState(QUERY, ADD, UPDATE));
                                 testTrailer.setQueryMode(isState(QUERY));
                             }
                         });

        testTrailer.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getTrailerMatches(event.getMatch());
            }
        });

        addScreenHandler(testFormat, TestMeta.getTestFormatId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testFormat.setValue(getTestFormat());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTestFormat(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                testFormat.setEnabled(isState(QUERY, ADD, UPDATE));
                testFormat.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(scriptlet,
                         TestMeta.getScriptletName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 scriptlet.setValue(getScriptlet());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 setScriptlet(event);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 scriptlet.setEnabled(isState(QUERY, ADD, UPDATE));
                                 scriptlet.setQueryMode(isState(QUERY));
                             }
                         });

        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getScriptletMatches(event.getMatch());
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("test_format");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }
        testFormat.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("test_reporting_method");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }
        reportingMethod.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("test_sorting_method");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }
        sortingMethod.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("test_revision_method");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }
        revisionMethod.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("test_section_flags");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }
        sectionOption.setModel(model);

        model = new ArrayList<Item<Integer>>();
        sectList = SectionCache.getList();
        model.add(new Item<Integer>(null, ""));
        for (SectionViewDO resultDO : sectList) {
            model.add(new Item<Integer>(resultDO.getId(), resultDO.getName()));
        }
        sectionName.setModel(model);
    }

    @UiHandler("removeSectionButton")
    protected void removeSection(ClickEvent event) {
        int r;

        r = sectionTable.getSelectedRow();
        if (r > -1 && sectionTable.getRowCount() > 0)
            sectionTable.removeRowAt(r);
    }

    @UiHandler("addSectionButton")
    protected void addSection(ClickEvent event) {
        int n;

        sectionTable.addRow();
        n = sectionTable.getRowCount() - 1;
        sectionTable.selectRowAt(n);
        sectionTable.scrollToVisible(sectionTable.getSelectedRow());
        sectionTable.startEditing(n, 0);
    }

    public void setData(TestManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        TestViewDO test;

        /*
         * find out if there's any difference between the test being displayed
         * and the test in the manager
         */
        test = manager.getTest();
        if (DataBaseUtil.isDifferent(test.getDescription(), description.getValue()) ||
            DataBaseUtil.isDifferent(test.getReportingDescription(),
                                     reportingDescription.getValue()) ||
            DataBaseUtil.isDifferent(test.getTimeTaMax(), timeTaMax.getValue()) ||
            DataBaseUtil.isDifferent(test.getTimeTransit(), timeTransit.getValue()) ||
            DataBaseUtil.isDifferent(test.getTimeTaAverage(), timeTaAverage.getValue()) ||
            DataBaseUtil.isDifferent(test.getTimeHolding(), timeHolding.getValue()) ||
            DataBaseUtil.isDifferent(test.getTimeTaWarning(), timeTaWarning.getValue()) ||
            DataBaseUtil.isDifferent(test.getIsActive(), isActive.getValue()) ||
            DataBaseUtil.isDifferent(test.getActiveBegin(), activeBegin.getValue()) ||
            DataBaseUtil.isDifferent(test.getActiveEnd(), activeEnd.getValue()) ||
            DataBaseUtil.isDifferent(test.getLabelId(), label.getValue().getId()) ||
            DataBaseUtil.isDifferent(test.getLabelQty(), labelQty.getValue()) ||
            DataBaseUtil.isDifferent(test.getIsReportable(), isReportable.getValue()) ||
            DataBaseUtil.isDifferent(test.getRevisionMethodId(), revisionMethod.getValue()) ||
            DataBaseUtil.isDifferent(test.getTestTrailerId(), testTrailer.getValue().getId()) ||
            DataBaseUtil.isDifferent(test.getSortingMethodId(), sortingMethod.getValue()) ||
            DataBaseUtil.isDifferent(test.getTestFormatId(), testFormat.getValue()) ||
            DataBaseUtil.isDifferent(test.getReportingMethodId(), reportingMethod.getValue()) ||
            DataBaseUtil.isDifferent(test.getScriptletId(), scriptlet.getValue().getId()) ||
            DataBaseUtil.isDifferent(test.getReportingSequence(), reportingSequence.getValue())) {
            redraw = true;
        }
        for (int i = 0; i < sectionTable.getRowCount(); i++ ) {
            if (redraw == true)
                break;
            for (int j = 0; j < manager.section.count(); j++ ) {
                if (DataBaseUtil.isDifferent(sectionTable.getRowAt(i).getCell(0),
                                             manager.section.get(j).getSectionId()) ||
                    DataBaseUtil.isDifferent(sectionTable.getRowAt(i).getCell(1),
                                             manager.section.get(j).getFlagId())) {
                    redraw = true;
                    break;
                }
            }
        }
        displayDetails();
    }

    private void displayDetails() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    /*
     * getters and setters
     */
    private String getDescription() {
        if (manager == null)
            return null;
        return manager.getTest().getDescription();
    }

    private void setDescription(String description) {
        manager.getTest().setDescription(description);
    }

    private String getReportingDescription() {
        if (manager == null)
            return null;
        return manager.getTest().getReportingDescription();
    }

    private void setReportingDescription(String reportingDescription) {
        manager.getTest().setReportingDescription(reportingDescription);
    }

    private Integer getTimeTaMax() {
        if (manager == null)
            return null;
        return manager.getTest().getTimeTaMax();
    }

    private void setTimeTaMax(Integer timeTaMax) {
        manager.getTest().setTimeTaMax(timeTaMax);
    }

    private Integer getTimeTaAverage() {
        if (manager == null)
            return null;
        return manager.getTest().getTimeTaAverage();
    }

    private void setTimeTaAverage(Integer timeTaAverage) {
        manager.getTest().setTimeTaAverage(timeTaAverage);
    }

    private Integer getTimeTaWarning() {
        if (manager == null)
            return null;
        return manager.getTest().getTimeTaWarning();
    }

    private void setTimeTaWarning(Integer timeTaWarning) {
        manager.getTest().setTimeTaWarning(timeTaWarning);
    }

    private Integer getTimeTransit() {
        if (manager == null)
            return null;
        return manager.getTest().getTimeTransit();
    }

    private void setTimeTransit(Integer timeTransit) {
        manager.getTest().setTimeTransit(timeTransit);
    }

    private Integer getTimeHolding() {
        if (manager == null)
            return null;
        return manager.getTest().getTimeHolding();
    }

    private void setTimeHolding(Integer timeHolding) {
        manager.getTest().setTimeHolding(timeHolding);
    }

    private String getIsActive() {
        if (manager == null)
            return null;
        return manager.getTest().getIsActive();
    }

    private void setIsActive(String isActive) {
        manager.getTest().setIsActive(isActive);
    }

    private Datetime getActiveBegin() {
        if (manager == null)
            return null;
        return manager.getTest().getActiveBegin();
    }

    private void setActiveBegin(Datetime activeBegin) {
        manager.getTest().setActiveBegin(activeBegin);
    }

    private Datetime getActiveEnd() {
        if (manager == null)
            return null;
        return manager.getTest().getActiveEnd();
    }

    private void setActiveEnd(Datetime activeEnd) {
        manager.getTest().setActiveEnd(activeEnd);
    }

    private AutoCompleteValue getLabel() {
        if (manager == null)
            return null;
        return new AutoCompleteValue(manager.getTest().getLabelId(), manager.getTest()
                                                                            .getLabelName());
    }

    private void setLabel(ValueChangeEvent<AutoCompleteValue> event) {
        manager.getTest().setLabelId(event.getValue().getId());
        manager.getTest().setLabelName(label.getDisplay());
    }

    private Integer getLabelQty() {
        if (manager == null)
            return null;
        return manager.getTest().getLabelQty();
    }

    private void setLabelQty(Integer labelQty) {
        manager.getTest().setLabelQty(labelQty);
    }

    private String getIsReportable() {
        if (manager == null)
            return null;
        return manager.getTest().getIsReportable();
    }

    private void setIsReportable(String isReportable) {
        manager.getTest().setIsReportable(isReportable);
    }

    private Integer getRevisionMethod() {
        if (manager == null)
            return null;
        return manager.getTest().getRevisionMethodId();
    }

    private void setRevisionMethod(Integer revisionMethod) {
        manager.getTest().setRevisionMethodId(revisionMethod);
    }

    private Integer getSortingMethod() {
        if (manager == null)
            return null;
        return manager.getTest().getSortingMethodId();
    }

    private void setSortingMethod(Integer sortingMethod) {
        manager.getTest().setSortingMethodId(sortingMethod);
    }

    private Integer getReportingMethod() {
        if (manager == null)
            return null;
        return manager.getTest().getReportingMethodId();
    }

    private void setReportingMethod(Integer reportingMethod) {
        manager.getTest().setReportingMethodId(reportingMethod);
    }

    private Integer getReportingSequence() {
        if (manager == null)
            return null;
        return manager.getTest().getReportingSequence();
    }

    private void setReportingSequence(Integer reportingSequence) {
        manager.getTest().setReportingSequence(reportingSequence);
    }

    private AutoCompleteValue getTestTrailer() {
        if (manager == null)
            return null;
        return new AutoCompleteValue(manager.getTest().getTestTrailerId(), manager.getTest()
                                                                                  .getTrailerName());
    }

    private void setTestTrailer(ValueChangeEvent<AutoCompleteValue> event) {
        manager.getTest().setTestTrailerId(event.getValue().getId());
        manager.getTest().setTrailerName(testTrailer.getDisplay());
    }

    private Integer getTestFormat() {
        if (manager == null)
            return null;
        return manager.getTest().getTestFormatId();
    }

    private void setTestFormat(Integer testFormat) {
        manager.getTest().setTestFormatId(testFormat);
    }

    private AutoCompleteValue getScriptlet() {
        if (manager == null)
            return null;
        return new AutoCompleteValue(manager.getTest().getScriptletId(), manager.getTest()
                                                                                .getScriptletName());
    }

    private void setScriptlet(ValueChangeEvent<AutoCompleteValue> event) {
        manager.getTest().setScriptletId(event.getValue().getId());
        manager.getTest().setScriptletName(testTrailer.getDisplay());
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        TestSectionViewDO data;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.section.count(); i++ ) {
            data = manager.section.get(i);
            row = new Row(2);
            row.setCell(0, data.getSectionId());
            row.setCell(1, data.getFlagId());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private void getTrailerMatches(String match) {
        ArrayList<Item<Integer>> model;
        ArrayList<IdNameVO> list;

        try {
            list = TestTrailerService.get().fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (IdNameVO data : list)
                model.add(new Item<Integer>(data.getId(), data.getName()));

            testTrailer.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private void getScriptletMatches(String match) {
        ArrayList<Item<Integer>> model;
        ArrayList<IdNameVO> list;

        try {
            list = ScriptletService.get().fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (IdNameVO data : list) {
                model.add(new Item<Integer>(data.getId(), data.getName()));
            }
            scriptlet.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private void getLabelMatches(String match) {
        ArrayList<LabelDO> list;
        ArrayList<Item<Integer>> model;

        setBusy();
        try {
            list = LabelService.get().fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (LabelDO data : list)
                model.add(new Item<Integer>(data.getId(), data.getName()));
            testTrailer.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }
}
