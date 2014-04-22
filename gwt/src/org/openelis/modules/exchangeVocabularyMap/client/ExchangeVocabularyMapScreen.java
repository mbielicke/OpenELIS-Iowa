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
package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ExchangeExternalTermDO;
import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.TestAnalyteViewVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox.Case;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.ExchangeLocalTermMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.TestAnalyteViewMeta;
import org.openelis.modules.analyte.client.AnalyteService;
import org.openelis.modules.dictionary.client.DictionaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.method.client.MethodService;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExchangeVocabularyMapScreen extends Screen {
    private ExchangeLocalTermManager    manager;
    private ModulePermission            userPermission;

    private ExchangeVocabularyMapScreen screen;
    private ScreenNavigator             nav;

    private AppButton                   queryButton, previousButton, nextButton, addButton,
                    updateButton, commitButton, abortButton, addExternalTermButton,
                    removeExternalTermButton;
    private MenuItem                    localTermHistory, externalTermHistory;
    private AutoComplete<Integer>       referenceName;
    private Dropdown<Integer>           referenceTableId;
    private TableWidget                 termMappingTable;
    private static final String         DATE_RANGE_PATTERN = "\\[\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\.{2}\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\]";

    public ExchangeVocabularyMapScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(ExchangeVocabularyMapDef.class));

        setWindow(window);

        userPermission = UserCache.getPermission().getModule("exchangevocabularymap");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Exchange Vocabulary Map Screen"));

        manager = ExchangeLocalTermManager.getInstance();

        try {
            CategoryCache.getBySystemNames("exchange_local_type", "exchange_profile");
        } catch (Exception e) {
            Window.alert("ExchangeVocabularyMapScreen: missing dictionary entry; " + e.getMessage());
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
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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

        localTermHistory = (MenuItem)def.getWidget("localTermHistory");
        addScreenHandler(localTermHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                localTermHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                localTermHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        externalTermHistory = (MenuItem)def.getWidget("externalTermHistory");
        addScreenHandler(externalTermHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                externalTermHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                externalTermHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        referenceTableId = (Dropdown)def.getWidget(ExchangeLocalTermMeta.getReferenceTableId());
        addScreenHandler(referenceTableId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                referenceTableId.setSelection(manager.getExchangeLocalTerm().getReferenceTableId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer refTableId;
                ExchangeLocalTermViewDO data;

                refTableId = event.getValue();
                if (refTableId == null)
                    return;

                /*
                 * since the autocomplete can show data from different tables,
                 * the text's case needs to vary based on the data being queried
                 * for
                 */
                if (Constants.table().ANALYTE.equals(refTableId) ||
                    Constants.table().DICTIONARY.equals(refTableId) ||
                    Constants.table().TEST_ANALYTE.equals(refTableId))
                    referenceName.setCase(Case.MIXED);
                else if (Constants.table().METHOD.equals(refTableId) ||
                         Constants.table().TEST.equals(refTableId))
                    referenceName.setCase(Case.LOWER);
                else if (Constants.table().ORGANIZATION.equals(refTableId))
                    referenceName.setCase(Case.UPPER);

                if (state == State.QUERY)
                    return;

                data = manager.getExchangeLocalTerm();
                data.setReferenceTableId(refTableId);
                data.setReferenceId(null);
                data.setReferenceName(null);
                data.setReferenceDescription1(null);
                data.setReferenceDescription2(null);
                data.setReferenceDescription3(null);

                /*
                 * if the reference table is test_analyte then the popup screen
                 * is used for finding the desired record (test analyte); for
                 * all other reference tables, the autocomplete is used
                 */
                referenceName.enable(true);
                DataChangeEvent.fire(screen, referenceName);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceTableId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                referenceTableId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        referenceName = (AutoComplete<Integer>)def.getWidget(ExchangeLocalTermMeta.getReferenceName());
        addScreenHandler(referenceName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ExchangeLocalTermViewDO data;
                ArrayList<String> disp;

                data = manager.getExchangeLocalTerm();
                disp = new ArrayList<String>();
                disp.add(data.getReferenceName());
                disp.add(data.getReferenceDescription1());
                disp.add(data.getReferenceDescription2());
                disp.add(data.getReferenceDescription3());
                referenceName.setSelection(data.getReferenceId(),
                                           DataBaseUtil.concatWithSeparator(disp, ", "));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                String name, desc1, desc2, desc3;
                ExchangeLocalTermViewDO data;
                AnalyteDO ana;
                IdNameVO dict;
                MethodDO m;
                OrganizationDO org;
                AddressDO addr;
                TestMethodVO t;
                TestAnalyteViewVO tav;
                TableDataRow row;

                row = referenceName.getSelection();
                data = manager.getExchangeLocalTerm();
                name = null;
                desc1 = null;
                desc2 = null;
                desc3 = null;
                /*
                 * set the name and description for the local term from the
                 * record selected in the autocomplete
                 */
                if (row != null) {
                    if (Constants.table().ANALYTE.equals(data.getReferenceTableId())) {
                        ana = (AnalyteDO)row.data;
                        name = ana.getName();
                    } else if (Constants.table().DICTIONARY.equals(data.getReferenceTableId())) {
                        dict = (IdNameVO)row.data;
                        name = dict.getName();
                        desc1 = dict.getDescription();
                    } else if (Constants.table().METHOD.equals(data.getReferenceTableId())) {
                        m = (MethodDO)row.data;
                        name = m.getName();
                    } else if (Constants.table().ORGANIZATION.equals(data.getReferenceTableId())) {
                        org = (OrganizationDO)row.data;
                        name = org.getName();
                        addr = org.getAddress();
                        desc1 = addr.getStreetAddress();
                        desc2 = addr.getCity();
                        desc3 = addr.getState();
                    } else if (Constants.table().TEST.equals(data.getReferenceTableId())) {
                        t = (TestMethodVO)row.data;
                        name = t.getTestName();
                        desc1 = t.getMethodName();
                    } else if (Constants.table().TEST_ANALYTE.equals(data.getReferenceTableId())) {
                        tav = (TestAnalyteViewVO)row.data;
                        name = tav.getTestName();
                        desc1 = tav.getMethodName();
                        desc2 = tav.getRowAnalyteName();
                        /*
                         * for row analytes, the column analyte name is the same
                         * as the row analyte name, so it doesn't need to be
                         * shown twice
                         */
                        if ( !tav.getRowAnalyteName().equals(tav.getColAnalyteName()))
                            desc3 = tav.getColAnalyteName();
                    }
                }

                data.setReferenceId(event.getValue());
                data.setReferenceName(name);
                data.setReferenceDescription1(desc1);
                data.setReferenceDescription2(desc2);
                data.setReferenceDescription3(desc3);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceName.enable(EnumSet.of(State.QUERY, State.QUERY, State.UPDATE)
                                            .contains(event.getState()));
                referenceName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        referenceName.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                if (referenceTableId.getValue() == null) {
                    Window.alert(Messages.get().pleaseSelectType());
                    event.cancel();
                }
            }
        });

        referenceName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String search;

                search = QueryFieldUtil.parseAutocomplete(event.getMatch());

                window.setBusy(Messages.get().fetching());

                if (Constants.table().ANALYTE.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getAnalyteModel(search));
                else if (Constants.table().DICTIONARY.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getDictionaryModel(search));
                else if (Constants.table().METHOD.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getMethodModel(search));
                else if (Constants.table().ORGANIZATION.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getOrganizationModel(search));
                else if (Constants.table().TEST.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getTestModel(search));
                else if (Constants.table().TEST_ANALYTE.equals(referenceTableId.getValue()))
                    referenceName.showAutoMatches(getTestAnalyteModel(search));

                window.clearStatus();
            }
        });

        termMappingTable = (TableWidget)def.getWidget("termMappingTable");
        addScreenHandler(termMappingTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    termMappingTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                termMappingTable.enable(true);
                termMappingTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        termMappingTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE)
                    event.cancel();
            }
        });

        termMappingTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ExchangeExternalTermDO data;

                r = event.getRow();
                c = event.getCol();
                val = termMappingTable.getObject(r, c);

                try {
                    data = manager.getExternalTerms().getExternalTermAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setIsActive((String)val);
                        break;
                    case 1:
                        data.setProfileId((Integer)val);
                        break;
                    case 2:
                        data.setExternalTerm((String)val);
                        break;
                    case 3:
                        data.setExternalDescription((String)val);
                        break;
                    case 4:
                        data.setExternalCodingSystem((String)val);
                        break;
                    case 5:
                        data.setVersion((String)val);
                        break;
                }
            }
        });

        termMappingTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                ExchangeExternalTermDO data;
                try {
                    data = new ExchangeExternalTermDO();
                    data.setIsActive("Y");
                    manager.getExternalTerms().addExternalTerm(data);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        termMappingTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getExternalTerms().removeExternalTermAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        removeExternalTermButton = (AppButton)def.getWidget("removeExternalTermButton");
        addScreenHandler(removeExternalTermButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = termMappingTable.getSelectedRow();
                if (r > -1 && termMappingTable.numRows() > 0)
                    termMappingTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeExternalTermButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState()));
            }
        });

        addExternalTermButton = (AppButton)def.getWidget("addExternalTermButton");
        addScreenHandler(addExternalTermButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                termMappingTable.addRow();
                n = termMappingTable.numRows() - 1;
                termMappingTable.selectRow(n);
                termMappingTable.scrollToSelection();
                termMappingTable.setCell(n, 0, "Y");
                termMappingTable.startEditing(n, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addExternalTermButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<ExchangeLocalTermViewDO>(def) {
            public void executeQuery(final Query query) {
                String refQuery;
                Integer refTableId;
                QueryData refField;
                ArrayList<QueryData> fields;

                refTableId = null;
                refField = null;
                for (QueryData field : query.getFields()) {
                    if (ExchangeLocalTermMeta.getReferenceTableId().equals(field.getKey()))
                        refTableId = Integer.parseInt(field.getQuery());
                    else if (ExchangeLocalTermMeta.getReferenceName().equals(field.getKey()))
                        /*
                         * this field contains the search string
                         */
                        refField = field;
                }

                if (Constants.table().TEST_ANALYTE.equals(refTableId)) {
                    if (refField != null) {
                        refQuery = refField.getQuery();
                        /*
                         * remove this field because one or more fields
                         * corresponding to the name of the column queried for
                         * will be added below
                         */
                        query.getFields().remove(refField);
                    } else {
                        refQuery = "";
                    }

                    /*
                     * create separate query fields for each column queried for
                     * in the search string, e.g. test name, row analyte name
                     */
                    fields = getTestAnalyteQueryFields(refQuery);
                    for (QueryData f : fields)
                        query.setFields(f);
                } else {
                    if (refField == null) {
                        refField = new QueryData();
                        refField.setQuery("*");
                        refField.setType(QueryData.Type.STRING);
                        query.setFields(refField);
                    }
                    /*
                     * depending upon the reference table selected replace the
                     * generic key in the query field with the name of the
                     * column in the reference table being queried for, e.g.
                     * test name
                     */
                    if (Constants.table().ANALYTE.equals(refTableId))
                        refField.setKey(ExchangeLocalTermMeta.getAnalyteName());
                    else if (Constants.table().DICTIONARY.equals(refTableId))
                        refField.setKey(ExchangeLocalTermMeta.getDictionaryEntry());
                    else if (Constants.table().METHOD.equals(refTableId))
                        refField.setKey(ExchangeLocalTermMeta.getMethodName());
                    else if (Constants.table().ORGANIZATION.equals(refTableId))
                        refField.setKey(ExchangeLocalTermMeta.getOrganizationName());
                    else if (Constants.table().TEST.equals(refTableId))
                        refField.setKey(ExchangeLocalTermMeta.getTestName());
                }

                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(20);
                ExchangeVocabularyMapService.get()
                                            .query(query,
                                                   new AsyncCallback<ArrayList<ExchangeLocalTermViewDO>>() {
                                                       public void onSuccess(ArrayList<ExchangeLocalTermViewDO> result) {
                                                           setQueryResult(result);
                                                       }

                                                       public void onFailure(Throwable error) {
                                                           setQueryResult(null);
                                                           if (error instanceof NotFoundException) {
                                                               window.setDone(Messages.get()
                                                                                      .noRecordsFound());
                                                               setState(State.DEFAULT);
                                                           } else if (error instanceof LastPageException) {
                                                               window.setError(Messages.get()
                                                                                       .noMoreRecordInDir());
                                                           } else {
                                                               Window.alert("Error: Exchange Vocabulary Map call query failed; " +
                                                                            error.getMessage());
                                                               window.setError(Messages.get()
                                                                                       .queryFailed());
                                                           }
                                                       }
                                                   });
            }

            public boolean fetch(ExchangeLocalTermViewDO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                boolean isTestAnalyteType;
                ArrayList<ExchangeLocalTermViewDO> result;
                ArrayList<TableDataRow> model;
                ArrayList<String> disp;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    isTestAnalyteType = Constants.table().TEST_ANALYTE.equals(referenceTableId.getValue());
                    disp = new ArrayList<String>();
                    model = new ArrayList<TableDataRow>();
                    for (ExchangeLocalTermViewDO entry : result) {
                        disp.clear();
                        disp.add(entry.getReferenceName());
                        disp.add(entry.getReferenceDescription1());
                        disp.add(entry.getReferenceDescription2());
                        /*
                         * if the query returned mappings for test analytes then
                         * for row analytes, the column analyte name is the same
                         * as the row analyte name, so it doesn't need to be
                         * shown twice
                         */
                        if ( !isTestAnalyteType ||
                            !entry.getReferenceDescription2()
                                  .equals(entry.getReferenceDescription3()))
                            disp.add(entry.getReferenceDescription3());
                        model.add(new TableDataRow(entry.getId(),
                                                   DataBaseUtil.concatWithSeparator(disp, ", ")));
                    }
                }
                return model;
            }
        };
        
        addStateChangeHandler(new StateChangeHandler<Screen.State>() {
            public void onStateChange(StateChangeEvent<State> event) {
                nav.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                           .contains(event.getState()) &&
                    userPermission.hasSelectPermission());
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("exchange_local_type");
        for (DictionaryDO d : list) {
            if (Constants.dictionary().LOCAL_TYPE_ANALYTE.equals(d.getId())) {
                row = new TableDataRow(Constants.table().ANALYTE, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if (Constants.dictionary().LOCAL_TYPE_DICTIONARY.equals(d.getId())) {
                row = new TableDataRow(Constants.table().DICTIONARY, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if (Constants.dictionary().LOCAL_TYPE_METHOD.equals(d.getId())) {
                row = new TableDataRow(Constants.table().METHOD, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if (Constants.dictionary().LOCAL_TYPE_ORGANIZATION.equals(d.getId())) {
                row = new TableDataRow(Constants.table().ORGANIZATION, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if (Constants.dictionary().LOCAL_TYPE_TEST.equals(d.getId())) {
                row = new TableDataRow(Constants.table().TEST, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if (Constants.dictionary().LOCAL_TYPE_TEST_ANALYTE.equals(d.getId())) {
                row = new TableDataRow(Constants.table().TEST_ANALYTE, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            }
        }

        referenceTableId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("exchange_profile");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        ((Dropdown)termMappingTable.getColumnWidget(ExchangeLocalTermMeta.getExternalTermExchangeProfileId())).setModel(model);
    }

    public boolean validate() {
        ArrayList<TableDataRow> sels;

        if (state != State.QUERY)
            return super.validate();

        sels = referenceTableId.getSelections();

        if (sels.size() == 0 || sels.get(0).key == null) {
            //
            // type e.g. Test, Analyte etc. must be specified in query mode
            //
            referenceTableId.addException(new Exception(Messages.get().fieldRequiredException()));
        } else if (sels.size() > 1) {
            //
            // we don't allow more than one type to be selected
            //
            referenceTableId.addException(new Exception(Messages.get()
                                                                .onlyOneTypeSelectionForQueryException()));
        }
        return super.validate();
    }

    protected void query() {
        manager = ExchangeLocalTermManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(referenceTableId);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = ExchangeLocalTermManager.getInstance();

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(referenceTableId);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(referenceTableId);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();

    }

    protected void commit() {
        Query query;

        setFocus(null);
        /*
         * this is done to make sure that the errors added from the back-end are
         * cleaned up before the screen does its validation so that if there are
         * no errors added on the screen, the data can be committed
         */
        termMappingTable.clearCellExceptions();

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(Messages.get().adding());
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().addingComplete());
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().updatingComplete());
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
            fetchById(null);
            window.setDone(Messages.get().addAborted());
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
        } else {
            window.clearStatus();
        }
    }

    protected void localTermHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getExchangeLocalTerm().getId(), manager.getExchangeLocalTerm()
                                                                           .getReferenceName());
        HistoryScreen.showHistory(Messages.get().localTermHistory(),
                                  Constants.table().EXCHANGE_LOCAL_TERM,
                                  hist);

    }

    protected void externalTermHistory() {
        int i, count;
        IdNameVO refVoList[];
        ExchangeExternalTermManager man;
        ExchangeExternalTermDO data;

        try {
            man = manager.getExternalTerms();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getExternalTermAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getExternalTerm());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().externalTermHistory(),
                                  Constants.table().EXCHANGE_EXTERNAL_TERM,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ExchangeLocalTermManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = ExchangeLocalTermManager.fetchWithExternalTerms(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }
        }

        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<TableDataRow> getTableModel() {
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ExchangeExternalTermDO data;
        ExchangeExternalTermManager man;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            man = manager.getExternalTerms();
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getExternalTermAt(i);

                row = new TableDataRow(6);
                row.cells.get(0).setValue(data.getIsActive());
                row.cells.get(1).setValue(data.getProfileId());
                row.cells.get(2).setValue(data.getExternalTerm());
                row.cells.get(3).setValue(data.getExternalDescription());
                row.cells.get(4).setValue(data.getExternalCodingSystem());
                row.cells.get(5).setValue(data.getVersion());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private ArrayList<TableDataRow> getAnalyteModel(String search) {
        TableDataRow row;
        ArrayList<AnalyteDO> list;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        try {
            list = AnalyteService.get().fetchByName(search);
            model = new ArrayList<TableDataRow>();
            for (AnalyteDO data : list) {
                row = new TableDataRow(data.getId(), data.getName());
                row.data = data;
                model.add(row);
            }
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getDictionaryModel(String search) {
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
        Query query;
        QueryData field;

        query = new Query();

        field = new QueryData();
        field.setKey(CategoryMeta.getDictionaryEntry());
        field.setQuery(search + "*");
        field.setType(QueryData.Type.STRING);
        query.setFields(field);

        field = new QueryData();
        field.setKey(CategoryMeta.getDictionaryIsActive());
        field.setQuery("Y");
        field.setType(QueryData.Type.STRING);
        query.setFields(field);

        model = new ArrayList<TableDataRow>();
        try {
            list = DictionaryService.get().fetchByEntry(query);
            for (IdNameVO data : list) {
                row = new TableDataRow(1);
                row.key = data.getId();
                row.cells.get(0).setValue(DataBaseUtil.concatWithSeparator(data.getName(),
                                                                           ", ",
                                                                           data.getDescription()));
                row.data = data;
                model.add(row);
            }
        } catch (NotFoundException ignE) {
            // ignore
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getMethodModel(String search) {
        TableDataRow row;
        ArrayList<MethodDO> list;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        try {
            list = MethodService.get().fetchByName(search);
            for (MethodDO data : list) {
                row = new TableDataRow(data.getId(), data.getName());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getOrganizationModel(String search) {
        TableDataRow row;
        ArrayList<OrganizationDO> orgs;
        ArrayList<TableDataRow> model;
        AddressDO addr;
        ArrayList<String> disp;

        model = new ArrayList<TableDataRow>();
        disp = new ArrayList<String>();
        try {
            orgs = OrganizationService.get().fetchByIdOrName(search);
            for (OrganizationDO data : orgs) {
                addr = data.getAddress();
                disp.clear();
                disp.add(data.getName());
                disp.add(addr.getStreetAddress());
                disp.add(addr.getCity());
                disp.add(addr.getState());

                row = new TableDataRow(1);
                row.key = data.getId();
                row.cells.get(0).setValue(DataBaseUtil.concatWithSeparator(disp, ", "));

                row.data = data;
                model.add(row);
            }
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getTestModel(String search) {
        StringBuilder sb;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<TestMethodVO> tests;

        model = new ArrayList<TableDataRow>();
        try {
            /*
             * the search string may contain the begin and end dates of an
             * inactive test, so they need to be removed before executing the
             * query so that they don't unintentionally get included in the
             * query
             */
            search = search.replaceAll(DATE_RANGE_PATTERN, "");
            tests = TestService.get().fetchByName(search);
            sb = new StringBuilder();
            for (TestMethodVO data : tests) {
                row = new TableDataRow(1);
                row.key = data.getTestId();
                sb.setLength(0);
                sb.append(data.getTestName());
                sb.append(", ");
                sb.append(data.getMethodName());
                /*
                 * for inactive tests, show the active begin and end dates
                 */
                if ("N".equals(data.getIsActive())) {
                    sb.append(" [");
                    sb.append(data.getActiveBegin());
                    sb.append("..");
                    sb.append(data.getActiveEnd());
                    sb.append("]");
                }
                row.cells.get(0).setValue(sb.toString());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getTestAnalyteModel(String search) {
        StringBuilder sb;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<TestAnalyteViewVO> tavs;
        ArrayList<QueryData> fields;

        fields = getTestAnalyteQueryFields(search);
        model = new ArrayList<TableDataRow>();
        if (fields == null || fields.size() == 0)
            return model;

        try {
            tavs = ExchangeVocabularyMapService.get().fetchTestAnalytes(fields);
            sb = new StringBuilder();
            for (TestAnalyteViewVO data : tavs) {
                row = new TableDataRow(1);
                row.key = data.getId();
                sb.setLength(0);
                sb.append(data.getTestName());
                sb.append(", ");
                sb.append(data.getMethodName());
                sb.append(", ");
                sb.append(data.getRowAnalyteName());
                /*
                 * if the test analyte is a row analyte then don't show anything
                 * as the column analyte
                 */
                if ( !data.getRowAnalyteName().equals(data.getColAnalyteName())) {
                    sb.append(", ");
                    sb.append(data.getColAnalyteName());
                }

                /*
                 * for inactive tests, show the active begin and end dates
                 */
                if ("N".equals(data.getTestIsActive())) {
                    sb.append(" [");
                    sb.append(data.getTestActiveBegin());
                    sb.append("..");
                    sb.append(data.getTestActiveEnd());
                    sb.append("]");
                }
                row.cells.get(0).setValue(sb.toString());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<QueryData> getTestAnalyteQueryFields(String search) {
        boolean isLastLevel;
        String name, key, names[];
        ArrayList<QueryData> fields;
        QueryData field;

        /*
         * the search string may contain the begin and end dates of an inactive
         * test, so they need to be removed before executing the query so that
         * they don't unintentionally get included in the query
         */
        search = search.replaceAll(DATE_RANGE_PATTERN, "");
        names = search.split(",");

        /*
         * create a query field for each level, e.g. test, row analyte etc.,
         * that the user is querying for
         */
        fields = new ArrayList<QueryData>();
        isLastLevel = true;
        for (int i = (names.length - 1); i >= 0; i-- ) {
            name = DataBaseUtil.trim(names[i]);
            if (name == null)
                name = "";

            if (isLastLevel) {
                /*
                 * to prevent queries from becoming too general and taking a
                 * long time to execute, the wild card is added for only the
                 * last level being queried e.g. if both test and method are
                 * present then the wildcard will be added for only the method
                 */
                name = name + "*";
                isLastLevel = false;
            }

            key = null;
            switch (i) {
                case 3:
                    key = TestAnalyteViewMeta.getColAnalyteName();
                    break;
                case 2:
                    key = TestAnalyteViewMeta.getRowAnalyteName();
                    break;
                case 1:
                    key = TestAnalyteViewMeta.getMethodName();
                    break;
                case 0:
                    key = TestAnalyteViewMeta.getTestName();
                    break;
            }

            field = new QueryData();
            field.setKey(key);
            field.setQuery(name);
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }

        return fields;
    }
}