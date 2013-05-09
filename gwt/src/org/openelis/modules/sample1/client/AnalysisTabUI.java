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
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.widget.Confirm;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisUserManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestSectionManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
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
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

    public enum Action {
        ANALYSIS_ADDED, PANEL_ADDED, ORDER_LIST_ADDED, CHANGED_DONT_CHECK_PREPS, ITEM_CHANGED,
        SAMPLE_TYPE_CHANGED, UNIT_CHANGED
    };

    @UiField
    protected AutoComplete testName, samplePrep, panel;

    @UiField
    protected Dropdown<Integer> sectionId, unitOfMeasureId, statusId;

    protected Dropdown<Integer> userActionId;

    @UiField
    protected CheckBox          isReportable, isPreliminary;

    @UiField
    protected TextBox<String>   methodName;

    @UiField
    protected TextBox<Integer>  revision;

    @UiField
    protected Calendar          startedDate, completedDate, releasedDate, printedDate;
    @UiField
    protected Table             worksheetTable, analysisUserTable;

    @UiField
    protected Button            selectWkshtButton, addActionButton, removeActionButton;

    protected Screen            parentScreen;

    protected boolean           canEdit, canQuery, isVisible;

    protected SampleManager1    manager;

    protected AnalysisViewDO    analysis;

    protected String            displayedUid;

    protected Confirm           changeTestConfirm;

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
                testName.setValue(getTestId(), getTestName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                AutoCompleteValue selectedRow;

                selectedRow = testName.getValue();
                //
                // we allow test&method to change if analysis has not been
                // committed. However,
                // if analysis has been committed, then we will only allow
                // method to be changed.
                // For those cases that the user blanks the entire field
                // (selecting nothing;
                // selectedRow == null), we will put the test back in the
                // dropdown.
                //
                if (getId() == null || getId() < 0) {
                    testChanged();
                    return;
                } else if (selectedRow == null) {
                    testName.setValue(getTestId(), getTestName());
                    methodName.setValue(getMethodName());
                    return;
                }

                if ( !DataBaseUtil.isSame( ((TestMethodVO)selectedRow.getData()).getTestId(),
                                          getTestId())) {
                    if (changeTestConfirm == null) {
                        changeTestConfirm = new Confirm(Confirm.Type.WARN,
                                                        Messages.get().loseResultsCaption(),
                                                        Messages.get().loseResultsWarning(),
                                                        "No",
                                                        "Yes");
                        changeTestConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:
                                        testName.setValue(getTestId(), getTestName());
                                        methodName.setValue(getMethodName());
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

            public void onStateChange(StateChangeEvent event) {
                testName.setEnabled(canEdit);
                testName.setQueryMode(canQuery);
            }
        });

        /*
         * testName.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
         * public void onBeforeGetMatches(BeforeGetMatchesEvent event) { int i;
         * Integer sampleType, testPanelId, sepIndex; String value, flag;
         * ArrayList<Item<Integer>> model; FormErrorException feE; Item<Integer>
         * row; DictionaryDO stDO; PanelDO pDO; PanelManager pMan; TestManager
         * tMan; TestTypeOfSampleDO ttosDO; TestTypeOfSampleManager ttosMan;
         * TestViewDO tVDO;
         * 
         * // / if analysis has been added, then don't allow test to be //
         * changed if (getId() == null || getId() < 0) return;
         * 
         * value = event.getMatch(); if (value.matches("[tp][0-9]*\\-[0-9]*")) {
         * flag = value.substring(0, 1); sepIndex = value.indexOf("-");
         * testPanelId = Integer.valueOf(value.substring(1, sepIndex));
         * sampleType = Integer.valueOf(value.substring(sepIndex + 1)); try { if
         * (sampleItem.getTypeOfSampleId() == null) { stDO =
         * DictionaryCache.getById(sampleType);
         * sampleItem.setTypeOfSampleId(stDO.getId());
         * sampleItem.setTypeOfSample(stDO.getEntry()); //
         * ActionEvent.fire(anTab, // Action.SAMPLE_TYPE_CHANGED, null); }
         * 
         * row = new Item<Integer>(3); if ("t".equals(flag)) { tMan =
         * TestService.get().fetchById(testPanelId); tVDO = tMan.getTest();
         * ttosMan = tMan.getSampleTypes(); for (i = 0; i < ttosMan.count(); i++
         * ) { ttosDO = ttosMan.getTypeAt(i); if (ttosDO.getTypeOfSampleId()
         * .equals(sampleItem.getTypeOfSampleId())) { row.setKey(tVDO.getId());
         * row.setCell(0, tVDO.getName()); row.setCell(1, tVDO.getMethodName());
         * row.setCell(2, tVDO.getDescription()); row.setData(tVDO); break; } }
         * if (i == ttosMan.count()) { feE = new
         * FormErrorException(Messages.get()
         * .testMethodSampleTypeMismatch(tVDO.getName() + ", " +
         * tVDO.getMethodName(), sampleItem.getTypeOfSample()));
         * Window.alert(feE.getMessage()); } } else if ("p".equals(flag)) { pMan
         * = PanelService.get().fetchById(testPanelId); pDO = pMan.getPanel();
         * row.setKey(pDO.getId()); row.setCell(0, pDO.getName());
         * row.setCell(2, pDO.getDescription()); row.setData(pDO); } model = new
         * ArrayList<Item<Integer>>(); model.add(row); testName.setModel(model);
         * // TODO change this code // testName.setValue(row.getKey()); //
         * testName.complete(); } catch (Exception e) {
         * Window.alert(e.getMessage()); } event.cancel(); } } });
         */

        /*
         * testName.addGetMatchesHandler(new GetMatchesHandler() { public void
         * onGetMatches(GetMatchesEvent event) { Integer sampleType, key;
         * ArrayList<QueryData> fields; ArrayList<Item<Integer>> model;
         * ArrayList<TestMethodVO> autoList; Query query; QueryData field;
         * Item<Integer> row;
         * 
         * sampleType = sampleItem.getTypeOfSampleId(); if (sampleType == null)
         * { window.setError(Messages.get().sampleItemTypeRequired()); return; }
         * 
         * fields = new ArrayList<QueryData>(); query = new Query();
         * 
         * field = new QueryData(); // // the methd if (getId() == null ||
         * getId() < 0)
         * field.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch()) +
         * "%"); else
         * field.setQuery(QueryFieldUtil.parseAutocomplete(getTestName()));
         * 
         * fields.add(field);
         * 
         * field = new QueryData();
         * field.setQuery(String.valueOf(sampleItem.getTypeOfSampleId()));
         * fields.add(field);
         * 
         * query.setFields(fields); query.setRowsPerPage(100);
         * 
         * try { autoList =
         * PanelService.get().fetchByNameSampleTypeWithTests(query); model = new
         * ArrayList<Item<Integer>>();
         * 
         * for (TestMethodVO autoDO : autoList) { /* Since the keys of the rows
         * need to be unique and it can happen that a panel has the same id as a
         * test, a negative number is used as the key for a row showing a panel
         * and a positive one for a row showing a test. An index in a loop can't
         * be used here because it can clash with an id and two different rows
         * may be treated as the same.
         * 
         * key = autoDO.getMethodId() == null ? -autoDO.getTestId() :
         * autoDO.getTestId(); row = new Item<Integer>(key,
         * autoDO.getTestName(), autoDO.getMethodName(),
         * autoDO.getTestDescription()); row.setData(autoDO); model.add(row); }
         * testName.showAutoMatches(model); } catch (Exception e) {
         * Window.alert(e.getMessage()); } } });
         */

        addScreenHandler(methodName,
                         SampleMeta.getAnalysisMethodName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 methodName.setValue(getMethodName());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setMethodId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 methodName.setEnabled(isState(QUERY));
                                 methodName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(statusId, SampleMeta.getAnalysisStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
                // if (isState(QUERY))
                // ActionEvent.fire(anTab, Action.CHANGED_DONT_CHECK_PREPS,
                // null);
            }

            public void onStateChange(StateChangeEvent event) {
                int i;
                ArrayList<Item<Integer>> model;
                Item<Integer> r;

                statusId.setEnabled(canEdit);
                statusId.setQueryMode(canQuery);

                model = statusId.getModel();
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
        });

        // TODO change this code
        /*
         * statusId.addBeforeSelectionHandler(new
         * BeforeSelectionHandler<TableRow>() { public void
         * onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
         * Item<Integer> r;
         * 
         * r = event.getItem().row; if (!r.enabled) event.cancel(); } });
         */

        addScreenHandler(revision, SampleMeta.getAnalysisRevision(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(getRevision());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                revision.setEnabled(canQuery);
                revision.setQueryMode(canQuery);
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
                                 isPreliminary.setEnabled(canEdit);
                                 isPreliminary.setQueryMode(isState(QUERY));
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
                                 isReportable.setEnabled(canEdit);
                                 isReportable.setQueryMode(canQuery);
                             }
                         });

        addScreenHandler(sectionId,
                         SampleMeta.getAnalysisSectionId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 // TODO change this code
                                 /*
                                  * ArrayList<Item<Integer>> sections;
                                  * 
                                  * 
                                  * if (isState(QUERY, DEFAULT, DISPLAY)) { if (
                                  * !fullSectionShown)
                                  * sectionId.setModel(fullSectionModel);
                                  * fullSectionShown = true; } else {
                                  * fullSectionShown = false; sections = null;
                                  * 
                                  * if (getTestId() != null) { sections =
                                  * sectionModel.get(getTestId());
                                  * 
                                  * if (sections == null) { try { sections =
                                  * getSectionsModel
                                  * (manager.getTestAt(bundle.getAnalysisIndex
                                  * ()) .getTestSections()); } catch (Exception
                                  * e) { sections = null; }
                                  * sectionModel.put(getTestId(), sections); } }
                                  * sectionId.setModel(sections); }
                                  * 
                                  * sectionId.setValue(getSectionId());
                                  */
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSectionId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sectionId.setEnabled(canEdit);
                                 sectionId.setQueryMode(canQuery);
                             }
                         });

        addScreenHandler(unitOfMeasureId,
                         SampleMeta.getAnalysisUnitOfMeasureId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 // TODO change this code
                                 /*
                                  * ArrayList<Item<Integer>> units;
                                  * 
                                  * if (isState(QUERY, DEFAULT, DISPLAY)) { if (
                                  * !fullUnitShown)
                                  * unitOfMeasureId.setModel(fullUnitModel);
                                  * fullUnitShown = true; } else { fullUnitShown
                                  * = false; units = null;
                                  * 
                                  * if (getTestId() != null) { units =
                                  * unitModel.get(getTestId() + "|" +
                                  * sampleItem.getTypeOfSampleId());
                                  * 
                                  * if (units == null) { try { units =
                                  * getUnitsModel
                                  * (manager.getTestAt(bundle.getAnalysisIndex
                                  * ()) .getSampleTypes(),
                                  * sampleItem.getTypeOfSampleId()); } catch
                                  * (Exception e) { units = null; }
                                  * unitModel.put(getTestId() + "|" +
                                  * sampleItem.getTypeOfSampleId(), units); } }
                                  * unitOfMeasureId.setModel(units); }
                                  * 
                                  * unitOfMeasureId.setValue(getUnitOfMeasureId()
                                  * );
                                  */
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setUnitOfMeasureId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 unitOfMeasureId.setEnabled(canEdit);
                                 unitOfMeasureId.setQueryMode(isState(QUERY));
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
                                 startedDate.setEnabled(canEdit);
                                 startedDate.setQueryMode(canQuery);
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
                                 completedDate.setEnabled(canEdit);
                                 completedDate.setQueryMode(canQuery);
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
                                 releasedDate.setEnabled(canEdit);
                                 releasedDate.setQueryMode(canQuery);
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
                                 printedDate.setEnabled(canQuery);
                                 printedDate.setQueryMode(canQuery);
                             }
                         });

        addScreenHandler(samplePrep,
                         SampleMeta.getAnalysisSamplePrep(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 String testMethodString = null;

                                 if (getPreAnalysisTest() != null)
                                     testMethodString = getPreAnalysisTest() + " : " +
                                                        getPreAnalysisMethod();

                                 samplePrep.setValue(getPreAnalysisId(), testMethodString);
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 // TODO change this code
                                 /*
                                  * try { if (event.getValue() == null) {
                                  * manager
                                  * .unlinkPrepTest(bundle.getAnalysisIndex());
                                  * 
                                  * } else { anBundle =
                                  * (SampleDataBundle)samplePrep
                                  * .getValue().getData(); itemMan =
                                  * bundle.getSampleManager().getSampleItems();
                                  * 
                                  * itemMan.linkPrepTest(bundle.getSampleItemIndex
                                  * (), bundle.getAnalysisIndex(),
                                  * anBundle.getSampleItemIndex(),
                                  * anBundle.getAnalysisIndex()); }
                                  * 
                                  * ActionEvent.fire(anTab,
                                  * Action.CHANGED_DONT_CHECK_PREPS, null);
                                  * 
                                  * DataChangeEvent.fire(anTab, statusId);
                                  * 
                                  * } catch (Exception e) {
                                  * Window.alert("samplePrep valueChange: " +
                                  * e.getMessage()); }
                                  */
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 samplePrep.setEnabled(canEdit);
                                 samplePrep.setQueryMode(canQuery);
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

                /*
                 * try { currentId = getId(); match = event.getMatch(); model =
                 * new ArrayList<Item<Integer>>(); itemMan =
                 * bundle.getSampleManager().getSampleItems();
                 * 
                 * numOfRows = 0; for (i = 0; i < itemMan.count(); i++ ) { if
                 * (numOfRows > 9) break;
                 * 
                 * itemDO = itemMan.getSampleItemAt(i); anMan =
                 * itemMan.getAnalysisAt(i);
                 * 
                 * for (j = 0; j < anMan.count(); j++ ) { if (numOfRows > 9)
                 * break;
                 * 
                 * anDO = anMan.getAnalysisAt(j);
                 * 
                 * if ( !currentId.equals(anDO.getId()) && anDO.getTestName() !=
                 * null && anDO.getTestName().startsWith(match) &&
                 * !Constants.dictionary
                 * ().ANALYSIS_CANCELLED.equals(anDO.getStatusId())) { row = new
                 * Item<Integer>( anDO.getId(), anDO.getTestName() + " : " +
                 * anDO.getMethodName() + " | " +
                 * formatTreeString(itemDO.getTypeOfSample()));
                 * row.setData(anMan.getBundleAt(j)); model.add(row); } } }
                 * 
                 * samplePrep.showAutoMatches(model);/ } catch (Exception e) {
                 * Window.alert("prep getMatches: " + e.getMessage()); }
                 */
            }
        });

        addScreenHandler(panel,
                         SampleMeta.getAnalysisPanelId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 panel.setValue(getPanelId(),
                                                getPanelName());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 //TODO make it like neonatal
                                 setPanelId(event.getValue());
                                 setPanelName(panel.getValue()
                                                            .getDisplay());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 panel.setEnabled(canEdit);
                                 panel.setQueryMode(canQuery);
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
                        row = new Item<Integer>(data.getId(),
                                                data.getName());
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

            public void onValueChange(ValueChangeEvent<Item<Integer>> event) {
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetTable.setEnabled(true);
            }
        });

        // TODO change this code
        /*
         * worksheetTable.addSelectionHandler(new SelectionHandler<TableRow>() {
         * public void onSelection(SelectionEvent<TableRow> event) {
         * selectWkshtButton.setEnabled(true); } });
         * 
         * worksheetTable.addBeforeCellEditedHandler(new
         * BeforeCellEditedHandler() { public void
         * onBeforeCellEdited(BeforeCellEditedEvent event) { // this table
         * cannot be edited event.cancel(); } });
         */

        addScreenHandler(analysisUserTable,
                         "analysisUserTable",
                         new ScreenHandler<Item<Integer>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisUserTable.setModel(getAnalysisUserTableModel());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisUserTable.setEnabled(true);
                             }
                         });

        // TODO change this code
        /*
         * final AutoComplete userName =
         * ((AutoComplete)analysisUserTable.getColumns() .get(0).colWidget);
         * analysisUserTable.addBeforeCellEditedHandler(new
         * BeforeCellEditedHandler() { public void
         * onBeforeCellEdited(BeforeCellEditedEvent event) { if (!canEdit() ||
         * !EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
         * event.cancel(); } } });
         * 
         * analysisUserTable.addCellEditedHandler(new CellEditedHandler() {
         * public void onCellUpdated(CellEditedEvent event) { int r, c; Object
         * val; AnalysisUserViewDO data; Item<Integer> row;
         * 
         * r = event.getRow(); c = event.getCol(); val =
         * analysisUserTable.getObject(r, c);
         * 
         * try { data = manager.getAnalysisUserAt(bundle.getAnalysisIndex())
         * .getAnalysisUserAt(r); } catch (Exception e) {
         * Window.alert(e.getMessage()); return; }
         * 
         * switch (c) { case 0: row = userName.getSelection();
         * 
         * if (row != null) { data.setSystemUserId((Integer)row.key);
         * data.setSystemUser((String)row.cells.get(0).value); } else {
         * data.setSystemUserId(null); data.setSystemUser(null); } break; case
         * 1: if
         * (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId
         * ())) { analysisUserTable.setCell(r, c, data.getActionId());
         * window.setError(Messages.get().analysisUserActionException()); } else
         * { data.setActionId((Integer)val); } break; } } });
         * 
         * analysisUserTable.addRowAddedHandler(new RowAddedHandler() { public
         * void onRowAdded(RowAddedEvent event) { try {
         * manager.getAnalysisUserAt(bundle.getAnalysisIndex())
         * .addAnalysisUser(new AnalysisUserViewDO()); } catch (Exception e) {
         * Window.alert(e.getMessage()); } } });
         * 
         * analysisUserTable.addRowDeletedHandler(new RowDeletedHandler() {
         * public void onRowDeleted(RowDeletedEvent event) { try {
         * manager.getAnalysisUserAt(bundle.getAnalysisIndex())
         * .removeAnalysisUserAt(event.getIndex()); } catch (Exception e) {
         * Window.alert(e.getMessage()); }
         * 
         * } });
         * 
         * userName.addGetMatchesHandler(new GetMatchesHandler() { public void
         * onGetMatches(GetMatchesEvent event) { ArrayList<SystemUserVO> users;
         * ArrayList<Item<Integer>> model;
         * 
         * try { users =
         * UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(
         * event.getMatch())); model = new ArrayList<Item<Integer>>(); for
         * (SystemUserVO user : users) model.add(new Item<Integer>(user.getId(),
         * user.getLoginName())); userName.showAutoMatches(model); } catch
         * (Exception e) { e.printStackTrace(); Window.alert(e.toString()); } }
         * });
         * 
         * userActionId =
         * ((Dropdown<Integer>)analysisUserTable.getColumns().get(
         * 1).getColumnWidget()); userActionId.addBeforeSelectionHandler(new
         * BeforeSelectionHandler<TableRow>() { public void
         * onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
         * Item<Integer> r;
         * 
         * r = event.getItem().row; if (!r.enabled) event.cancel(); } });
         */

        addScreenHandler(selectWkshtButton, "selectWkshtButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                // TODO change this code
                /*
                 * WorksheetCompletionScreen worksheetScreen; Item<Integer> row;
                 * try { ScreenWindow modal = new
                 * ScreenWindow(ScreenWindow.Mode.LOOK_UP);
                 * modal.setName(Messages.get().worksheetCompletion());
                 * 
                 * row = worksheetTable.getSelection(); worksheetScreen = new
                 * WorksheetCompletionScreen((Integer)row.key,modal);
                 * 
                 * modal.setContent(worksheetScreen);
                 * 
                 * } catch (Exception e) { Window.alert("openCompletionScreen: "
                 * + e.getMessage()); }
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                selectWkshtButton.setEnabled(false);
            }
        });

        addScreenHandler(addActionButton, "addActionButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                analysisUserTable.addRow();
                n = analysisUserTable.getRowCount() - 1;
                analysisUserTable.selectRowAt(n);
                // TODO change this code
                // analysisUserTable.scrollToSelection();
                analysisUserTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent event) {
                addActionButton.setEnabled(canEdit);
            }
        });

        addScreenHandler(removeActionButton, "removeActionButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                Integer action;

                /*
                 * r = analysisUserTable.getSelectedRow();
                 * 
                 * if (r > -1 && analysisUserTable.getRowCount() > 0) { try {
                 * action =
                 * manager.getAnalysisUserAt(bundle.getAnalysisIndex()).
                 * getAnalysisUserAt(r).getActionId(); } catch (Exception e) {
                 * Window.alert(e.getMessage()); return; }
                 * 
                 * if
                 * (!Constants.dictionary().AN_USER_AC_RELEASED.equals(action))
                 * analysisUserTable.removeRowAt(r); else
                 * window.setError(Messages
                 * .get().analysisUserActionException()); }
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                removeActionButton.setEnabled(canEdit);
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

        statusId.setModel(model);

        // section full dropdown model
        /*
         * model = new ArrayList<Item<Integer>>(); model.add(new
         * Item<Integer>(null, "")); for (SectionDO s : SectionCache.getList())
         * model.add(new Item<Integer>(s.getId(), s.getName()));
         * 
         * fullSectionModel = model; sectionId.setModel(model); sectionModel =
         * new HashMap<Integer, ArrayList<Item<Integer>>>(); fullSectionShown =
         * true;
         * 
         * // unit full dropdown model model = new ArrayList<Item<Integer>>();
         * model.add(new Item<Integer>(null, "")); for (DictionaryDO d :
         * CategoryCache.getBySystemName("unit_of_measure")) model.add(new
         * Item<Integer>(d.getId(), d.getEntry()));
         * 
         * fullUnitModel = model; unitOfMeasureId.setModel(model); unitModel =
         * new HashMap<String, ArrayList<Item<Integer>>>(); fullUnitShown =
         * true;
         */

        // analysis user action
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("user_action")) {
            r = new Item<Integer>(d.getId(), d.getEntry());
            if (Constants.dictionary().AN_USER_AC_RELEASED.equals(d.getId()))
                r.setEnabled(false);
            model.add(r);
        }

        // TODO change this code
        /*
         * userActionId.setModel(model);
         * 
         * // worksheet status model = new ArrayList<Item<Integer>>();
         * model.add(new Item<Integer>(null, "")); for (DictionaryDO d :
         * CategoryCache.getBySystemName("worksheet_status")) model.add(new
         * Item<Integer>(d.getId(), d.getEntry()));
         * ((Dropdown<Integer>)worksheetTable
         * .getColumns().get(2).getColumnWidget()).setModel(model);
         */

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
                        entry = DictionaryCache.getById(sampleType.getUnitOfMeasureId());
                        model.add(new Item<Integer>(entry.getId(), entry.getEntry()));
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                return new ArrayList<Item<Integer>>();
            }
        }

        return model;
    }

    private void testChanged() {
        // TODO change this code
        /*
         * Item<Integer> selectedRow; TestMethodVO tmVO;
         * 
         * /* we need to see if what they selected is a test or a panel
         * 
         * try { selectedRow = testName.getSelection(); if (selectedRow != null)
         * { tmVO = (TestMethodVO)selectedRow.data; if (tmVO.getMethodId() ==
         * null) ActionEvent.fire(this, Action.PANEL_ADDED, tmVO.getTestId());
         * else ActionEvent.fire(this, Action.ANALYSIS_ADDED, tmVO.getTestId());
         * } else { ActionEvent.fire(this, Action.ANALYSIS_ADDED, null); } }
         * catch (Exception e) { Window.alert(e.getMessage()); }
         */
    }

    private ArrayList<Item<Integer>> getWorksheetTableModel() {
        ArrayList<Item<Integer>> model;
        ArrayList<WorksheetViewDO> worksheets;
        WorksheetViewDO wksht;
        AnalysisViewDO anDO;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        if (manager == null)
            return model;

        // TODO change this code
        /*
         * anDO = manager.getAnalysisAt(bundle.getAnalysisIndex()); if
         * (anDO.getId() > 0) { try { worksheets =
         * WorksheetService.get().fetchByAnalysisId(anDO.getId());
         * 
         * for (int i = 0; i < worksheets.size(); i++ ) { wksht =
         * worksheets.get(i);
         * 
         * row = new Item<Integer>(4); row.setKey(wksht.getId()); row.setCell(0,
         * wksht.getId()); row.setCell(1, wksht.getCreatedDate());
         * row.setCell(2, wksht.getStatusId()); row.setCell(3,
         * wksht.getSystemUser()); model.add(row);
         * 
         * } } catch (Exception e) { Window.alert("getWorksheetTableModel: " +
         * e.getMessage()); } }
         */
        return model;
    }

    private ArrayList<Item<Integer>> getAnalysisUserTableModel() {
        ArrayList<Item<Integer>> model;
        AnalysisUserManager userMan;
        AnalysisUserViewDO userDO;

        model = new ArrayList<Item<Integer>>();
        if (manager == null)
            return model;

        // TODO change this code
        /*
         * try { userMan = manager.getAnalysisUserAt(bundle.getAnalysisIndex());
         * for (int iter = 0; iter < userMan.count(); iter++ ) { userDO =
         * userMan.getAnalysisUserAt(iter);
         * 
         * Item<Integer> row = new Item<Integer>(2); row.setKey(userDO.getId());
         * 
         * row.setCell(0, new Item<Integer>(userDO.getSystemUserId(),
         * userDO.getSystemUser())); row.setCell(1, userDO.getActionId());
         * 
         * model.add(row); } } catch (Exception e) {
         * Window.alert("getAnalysisUserTableModel: " + e.getMessage()); return
         * null; }
         */
        return model;
    }

    public void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = canQuery = false;
        if (isState(QUERY)) {
            canEdit = canQuery = true;
        } else if (manager != null) {
            if (isState(ADD, UPDATE)) {
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
    }

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager)) {
            this.manager = manager;
            evaluateEdit();
        }
    }

    /*
     * public void setData(SampleDataBundle data) { try { bundle = data; if
     * (data != null && SampleDataBundle.Type.ANALYSIS.equals(data.getType())) {
     * 
     * analysisIndex = data.getAnalysisIndex(); manager =
     * data.getSampleManager() .getSampleItems()
     * .getAnalysisAt(data.getSampleItemIndex()); analysis =
     * manager.getAnalysisAt(analysisIndex); sampleItem =
     * data.getSampleManager() .getSampleItems()
     * .getSampleItemAt(data.getSampleItemIndex());
     * 
     * if (isState(ADD) || isState(UPDATE)) StateChangeEvent.fire(this, UPDATE);
     * } else { analysis = new AnalysisViewDO(); sampleItem = new
     * SampleItemViewDO(); manager = null;
     * 
     * if (!isState(QUERY)) StateChangeEvent.fire(this, DEFAULT); } loaded =
     * false; } catch (Exception e) { Window.alert("analysisTab setData: " +
     * e.getMessage()); } }
     */

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private void displayAnalysis(String uid) {
        /*
         * don't redraw unless the data has changed
         */
        if (uid != null)
            analysis = (AnalysisViewDO)manager.getObject(uid);
        else
            analysis = null;

        if ( !isVisible)
            return;

        if (DataBaseUtil.isDifferent(displayedUid, uid)) {
            displayedUid = uid;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private Integer getId() {
        if (analysis != null)
            return analysis.getId();

        return null;
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

    private void setMethodId(Integer methodId) {
        analysis.setMethodId(methodId);
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
    
    private void setSectionId(Integer sectionId) {
        analysis.setSectionId(sectionId);
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
        // ActionEvent.fire(anTab, Action.UNIT_CHANGED, bundle);
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private void setStatusId(Integer statusId) {
        analysis.setStatusId(statusId);
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