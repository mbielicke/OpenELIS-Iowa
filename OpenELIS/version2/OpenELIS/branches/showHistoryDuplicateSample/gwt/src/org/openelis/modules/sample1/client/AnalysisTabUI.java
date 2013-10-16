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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

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
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
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
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalysisTabUI extends Screen {

    @UiTemplate("AnalysisTab.ui.xml")
    interface AnalysisTabUIBinder extends UiBinder<Widget, AnalysisTabUI> {
    };

    private static AnalysisTabUIBinder uiBinder = GWT.create(AnalysisTabUIBinder.class);

    @UiField
    protected TextBox<String>          test;

    @UiField
    protected AutoComplete             method, user;

    @UiField
    protected Dropdown<Integer>        section, unitOfMeasure, analysisStatus, panel, samplePrep,
                    worksheetStatus, userAction;

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

    protected AnalysisTabUI            screen;

    protected EventBus                 parentBus;

    protected SampleManager1           manager;

    protected AnalysisViewDO           analysis;

    protected SampleItemViewDO         sampleItem;

    protected String                   displayedUid;

    protected boolean                  canEdit, isVisible, redraw;

    protected ArrayList<Item<Integer>> allUnitsModel, allSectionsModel;

    public AnalysisTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;

        screen = this;

        addScreenHandler(test, SampleMeta.getAnalysisTestName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setValue(getTestName());
            }

            public void onStateChange(StateChangeEvent event) {
                test.setEnabled(isState(QUERY));
                test.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? method : userTable;
            }
        });

        addScreenHandler(method,
                         SampleMeta.getAnalysisMethodName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 method.setValue(getMethodId(), getMethodName());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 setMethod(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 method.setEnabled(isState(QUERY) ||
                                                   (isState(ADD, UPDATE) && canEdit));
                                 method.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisStatus : test;
                             }
                         });

        method.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> tests;
                Item<Integer> row;

                if (sampleItem.getTypeOfSampleId() == null) {
                    window.setError(Messages.get().sample_sampleItemTypeRequired());
                    return;
                }

                try {
                    tests = TestService.get().fetchByNameSampleType(analysis.getTestName(),
                                                                    sampleItem.getTypeOfSampleId());
                    model = new ArrayList<Item<Integer>>();

                    for (TestMethodVO t : tests) {
                        row = new Item<Integer>(t.getMethodId(),
                                                t.getMethodName(),
                                                t.getMethodDescription());
                        row.setData(t);
                        model.add(row);
                    }
                    method.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        addScreenHandler(analysisStatus,
                         SampleMeta.getAnalysisStatusId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisStatus.setValue(getStatusId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setStatusId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 int i;
                                 ArrayList<Item<Integer>> model;
                                 Item<Integer> r;

                                 analysisStatus.setEnabled(isState(QUERY) ||
                                                           (isState(ADD, UPDATE) && canEdit));
                                 analysisStatus.setQueryMode(isState(QUERY));

                                 model = analysisStatus.getModel();
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
                                 return forward ? section : method;
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

        addScreenHandler(section, SampleMeta.getAnalysisSectionId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * For all states other than Display and Query, the model
                 * depends on the analysis showing in the tab, which is known
                 * for certain only before this event is fired. So for those
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
                 * The model shown in this dropdown in Display and Query state
                 * is the list of all sections in the system. Also, In Query
                 * state, the model needs to be set in this dropdown before it
                 * can be switched to query mode. So for those states, the model
                 * is set here.
                 */
                if (isState(DISPLAY, QUERY))
                    section.setModel(allSectionsModel);
                section.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit));
                section.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isPreliminary : analysisStatus;
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
                                  * The model shown in this dropdown in Query
                                  * and Display states is the list of all units
                                  * in the system. Also, In Query state, the
                                  * model needs to be set in this dropdown
                                  * before it can be switched to query mode. So
                                  * for those states, the model is set here.
                                  */
                                 if (isState(DISPLAY, QUERY))
                                     unitOfMeasure.setModel(allUnitsModel);
                                 unitOfMeasure.setEnabled(isState(QUERY) ||
                                                          (isState(ADD, UPDATE) && canEdit));
                                 unitOfMeasure.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? panel : isReportable;
                             }
                         });

        addScreenHandler(panel, SampleMeta.getAnalysisPanelId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                panel.setValue(getPanelId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPanel(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                panel.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit));
                panel.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? samplePrep : unitOfMeasure;
            }
        });

        addScreenHandler(samplePrep,
                         SampleMeta.getAnalysisSamplePrep(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 samplePrep.setModel(getPrepModel());
                                 samplePrep.setValue(getPreAnalysisId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPreAnalysisId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 samplePrep.setEnabled(isState(ADD, UPDATE) && canEdit);
                                 samplePrep.setQueryMode(false);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? startedDate : panel;
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
                                 return forward ? completedDate : samplePrep;
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
                                 releasedDate.setEnabled(isState(QUERY));
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

        addScreenHandler(selectWkshtButton, "selectWkshtButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectWkshtButton.setEnabled(false);
            }
        });

        addScreenHandler(userTable, "userTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                userTable.setModel(getUserTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                userTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? test : worksheetTable;
            }
        });

        userTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !canEdit || !isState(ADD, UPDATE)) {
                    event.cancel();
                }
            }
        });

        userTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisUserViewDO data;
                SystemUserVO u;
                AutoCompleteValue sel;

                r = event.getRow();
                c = event.getCol();
                val = userTable.getValueAt(r, c);
                data = manager.analysisUser.get(analysis, r);

                switch (c) {
                    case 0:
                        sel = user.getValue();

                        if (sel != null) {
                            u = (SystemUserVO)sel.getData();
                            data.setSystemUserId(u.getId());
                            data.setSystemUser(u.getLoginName());
                        } else {
                            data.setSystemUserId(null);
                            data.setSystemUser(null);
                        }
                        break;
                    case 1:
                        if (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId())) {
                            userTable.setValueAt(r, c, data.getActionId());
                            parentScreen.getWindow()
                                        .setError(Messages.get().analysis_userActionException());
                        } else {
                            data.setActionId((Integer)val);
                        }
                        break;
                }
            }
        });

        userTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.analysisUser.add(analysis);
            }
        });

        userTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.analysisUser.remove(analysis, event.getIndex());
            }
        });

        user.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> item;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;

                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch() +
                                                                                    "%"));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users) {
                        item = new Item<Integer>(user.getId(), user.getLoginName());
                        item.setData(user);
                        model.add(item);
                    }
                    user.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.toString());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        addScreenHandler(addActionButton, "addActionButton", new ScreenHandler<Object>() {
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

        /*
         * handlers for the events fired by the screen containing this tab
         */
        parentBus.addHandlerToSource(StateChangeEvent.getType(),
                                     parentScreen,
                                     new StateChangeEvent.Handler() {
                                         public void onStateChange(StateChangeEvent event) {
                                             evaluateEdit();
                                             setState(event.getState());
                                         }
                                     });

        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
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

                if (DataBaseUtil.isDifferent(displayedUid, uid)) {
                    displayedUid = uid;
                    redraw = true;
                } else if (isState(QUERY)) {
                    /*
                     * No analysis is currently selected in the tree because it
                     * is empty in query state, so the current uid is null. If
                     * there was no analysis selected in the tree, before going
                     * in query state, the previous (displayed) uid was null
                     * too. This makes sure that the tab is redrawn for query
                     * state even if both uids are null.
                     */
                    redraw = true;
                }

                displayAnalysis(uid);
            }
        });

        parentBus.addHandler(SampleItemChangeEvent.getType(), new SampleItemChangeEvent.Handler() {
            public void onSampleItemChange(SampleItemChangeEvent event) {
                if (SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED.equals(event.getAction())) {
                    unitOfMeasure.setModel(getUnitsModel());
                    unitOfMeasure.setValue(getUnitOfMeasureId());
                }
            }
        });

        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            public void onAnalysisChange(AnalysisChangeEvent event) {
                /*
                 * this handler needs to respond to this event only if it is
                 * fired from the main screen and not this tab
                 */
                if (screen != event.getSource()) {
                    redraw = true;
                    displayAnalysis(event.getUid());
                }
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        analysisStatus.setModel(model);

        allSectionsModel = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList())
            allSectionsModel.add(new Item<Integer>(s.getId(), s.getName()));

        allUnitsModel = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            if ("Y".equals(d.getIsActive()))
                allUnitsModel.add(new Item<Integer>(d.getId(), d.getEntry()));
        }

        try {
            model = new ArrayList<Item<Integer>>();
            for (PanelDO p : PanelService.get().fetchAll())
                model.add(new Item<Integer>(p.getId(), p.getName()));
            panel.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            parentScreen.getWindow().close();
        }

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("worksheet_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        worksheetStatus.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("user_action")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            if (Constants.dictionary().AN_USER_AC_RELEASED.equals(d.getId()))
                row.setEnabled(false);
            model.add(row);
        }

        userAction.setModel(model);
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

    @UiHandler("addActionButton")
    protected void addAction(ClickEvent event) {
        int n;

        userTable.addRow();
        n = userTable.getRowCount() - 1;
        userTable.selectRowAt(n);
        userTable.scrollToVisible(userTable.getSelectedRow());
        userTable.startEditing(n, 0);
    }

    @UiHandler("removeActionButton")
    protected void removeAction(ClickEvent event) {
        int r;
        Integer action;

        r = userTable.getSelectedRow();

        if (r > -1 && userTable.getRowCount() > 0) {
            action = manager.analysisUser.get(analysis, r).getActionId();

            if ( !Constants.dictionary().AN_USER_AC_RELEASED.equals(action))
                userTable.removeRowAt(r);
            else
                parentScreen.getWindow().setError(Messages.get().analysis_userActionException());
        }
    }

    private void displayAnalysis(String uid) {
        if (uid != null) {
            analysis = (AnalysisViewDO)manager.getObject(uid);
            sampleItem = (SampleItemViewDO)manager.getObject(manager.getSampleItemUid(analysis.getSampleItemId()));
        } else {
            analysis = null;
            sampleItem = null;
        }

        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
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
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private String getTestName() {
        if (analysis != null)
            return analysis.getTestName();

        return null;
    }

    private void setMethod(AutoCompleteValue value) {
        TestMethodVO data;

        if (value != null) {
            data = (TestMethodVO)value.getData();
            parentBus.fireEventFromSource(new AnalysisChangeEvent(displayedUid,
                                                                  data.getMethodId(),
                                                                  AnalysisChangeEvent.Action.METHOD_CHANGED),
                                          this);
        } else {
            /*
             * if the user blanks the field, selecting nothing, the previous
             * methiod is put back in the autocomplete
             */
            method.setValue(getMethodId(), getMethodName());
        }
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

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private void setStatusId(Integer statusId) {
        parentBus.fireEventFromSource(new AnalysisChangeEvent(displayedUid,
                                                              statusId,
                                                              AnalysisChangeEvent.Action.STATUS_CHANGED),
                                      this);
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    private void setSection(Integer sectionId, String sectionName) {
        analysis.setSectionId(sectionId);
        analysis.setSectionName(sectionName);

        /*
         * notify the tabs showing the analysis' child data to reevaluate the
         * permissions for this section
         */
        parentBus.fireEvent(new AnalysisChangeEvent(displayedUid,
                                                    sectionId,
                                                    AnalysisChangeEvent.Action.SECTION_CHANGED));
    }

    private String getIsPreliminary() {
        if (analysis != null)
            return analysis.getIsPreliminary();

        return null;
    }

    private void setIsPreliminary(String isPreliminary) {
        analysis.setIsPreliminary(isPreliminary);
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
        parentBus.fireEventFromSource(new AnalysisChangeEvent(displayedUid,
                                                              unitOfMeasureId,
                                                              AnalysisChangeEvent.Action.UNIT_CHANGED),
                                      this);
    }

    private Integer getPanelId() {
        if (analysis != null)
            return analysis.getPanelId();

        return null;
    }

    private void setPanel(Integer panelId) {
        analysis.setPanelId(panelId);
    }

    private Integer getPreAnalysisId() {
        if (analysis != null)
            return analysis.getPreAnalysisId();

        return null;
    }

    private void setPreAnalysisId(Integer preAnalysisId) {
        parentBus.fireEventFromSource(new AnalysisChangeEvent(displayedUid,
                                                              preAnalysisId,
                                                              AnalysisChangeEvent.Action.PREP_CHANGED),
                                      this);
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

    private Integer getRevision() {
        if (analysis != null)
            return analysis.getRevision();

        return null;
    }

    private void setRevision(Integer revision) {
        analysis.setRevision(revision);
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

        if (analysis != null && isState(ADD, UPDATE)) {
            try {
                /*
                 * create the model from the sections associated with the
                 * analysis' test
                 */
                tsm = getTestManager(analysis.getTestId()).getTestSections();
                if (tsm != null) {
                    for (int i = 0; i < tsm.count(); i++ ) {
                        ts = tsm.getSectionAt(i);
                        model.add(new Item<Integer>(ts.getSectionId(), ts.getSection()));
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
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
        TestTypeOfSampleManager ttsm;

        model = new ArrayList<Item<Integer>>();

        if (analysis != null && isState(ADD, UPDATE) && sampleItem != null &&
            sampleItem.getTypeOfSampleId() != null) {
            try {
                /*
                 * create the model from the units associated with the sample
                 * item's sample type
                 */
                ttsm = getTestManager(analysis.getTestId()).getSampleTypes();
                for (int i = 0; i < ttsm.count(); i++ ) {
                    type = ttsm.getTypeAt(i);
                    if (type.getUnitOfMeasureId() != null &&
                        DataBaseUtil.isSame(sampleItem.getTypeOfSampleId(),
                                            type.getTypeOfSampleId())) {
                        d = DictionaryCache.getById(type.getUnitOfMeasureId());
                        model.add(new Item<Integer>(d.getId(), d.getEntry()));
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return model;
    }

    /**
     * Returns the model created from all analyses in the sample, except the one
     * shown currently in the tab and any cancelled ones.
     */
    private ArrayList<Item<Integer>> getPrepModel() {
        int i;
        int j;
        StringBuffer buf;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        model = new ArrayList<Item<Integer>>();
        if (manager != null && analysis != null) {
            buf = new StringBuffer();
            for (i = 0; i < manager.item.count(); i++ ) {
                item = manager.item.get(i);
                for (j = 0; j < manager.analysis.count(item); j++ ) {
                    ana = manager.analysis.get(item, j);
                    if ( !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) &&
                        !ana.getId().equals(analysis.getId())) {
                        row = new Item<Integer>(3);
                        row.setKey(ana.getId());

                        buf.setLength(0);
                        buf.append(ana.getTestName());
                        buf.append(", ");
                        buf.append(ana.getMethodName());
                        row.setCell(0, buf.toString());
                        row.setCell(1, item.getTypeOfSample());
                        row.setCell(2, item.getItemSequence());

                        row.setData(ana);

                        model.add(row);
                    }
                }
            }
        }
        return model;
    }

    /**
     * returns the TestManager for the specified id, from the cache maintained
     * by the parent screen
     */
    private TestManager getTestManager(Integer testId) throws Exception {
        if ( ! (parentScreen instanceof CacheProvider))
            throw new Exception("Parent screen must implement " + CacheProvider.class.toString());

        return ((CacheProvider)parentScreen).get(testId, TestManager.class);
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
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return model;
    }

    private ArrayList<Row> getUserTableModel() {
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
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return model;
    }
}