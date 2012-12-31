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
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ExchangeExternalTermDO;
import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
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
import org.openelis.modules.analyte.client.AnalyteService;
import org.openelis.modules.dictionary.client.DictionaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.method.client.MethodService;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.test.client.TestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExchangeVocabularyMapScreen extends Screen {
    private ExchangeLocalTermManager    manager;
    private ModulePermission            userPermission;

    private ExchangeVocabularyMapScreen screen;
    private ScreenNavigator             nav;

    private AppButton                   queryButton, previousButton, nextButton,
                    addButton, updateButton, commitButton, abortButton,
                    addExternalTermButton, removeExternalTermButton;
    private MenuItem                    localTermHistory, externalTermHistory;
    private AutoComplete<Integer>       referenceName;
    private Dropdown<Integer>           referenceTableId;
    private TableWidget                 termMappingTable;

    public ExchangeVocabularyMapScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ExchangeVocabularyMapDef.class));

        userPermission = UserCache.getPermission().getModule("exchangevocabularymap");
        if (userPermission == null)
            throw new PermissionException("screenPermException",
                                          "Exchange Vocabulary Map Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        manager = ExchangeLocalTermManager.getInstance();

        try {
            CategoryCache.getBySystemNames("exchange_local_type", "exchange_profile");
        } catch (Exception e) {
            Window.alert("ExchangeVocabularyMapScreen: missing dictionary entry; " +
                         e.getMessage());
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

        localTermHistory = (MenuItem)def.getWidget("localTermHistory");
        addScreenHandler(localTermHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                localTermHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                localTermHistory.enable(EnumSet.of(State.DISPLAY)
                                               .contains(event.getState()));
            }
        });

        externalTermHistory = (MenuItem)def.getWidget("externalTermHistory");
        addScreenHandler(externalTermHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                externalTermHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                externalTermHistory.enable(EnumSet.of(State.DISPLAY)
                                                  .contains(event.getState()));
            }
        });

        referenceTableId = (Dropdown)def.getWidget(ExchangeLocalTermMeta.getReferenceTableId());
        addScreenHandler(referenceTableId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                referenceTableId.setSelection(manager.getExchangeLocalTerm()
                                                     .getReferenceTableId());
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
                if (Constants.table().ANALYTE.equals(refTableId) || Constants.table().DICTIONARY.equals(refTableId))
                    referenceName.setCase(Case.MIXED);
                else if (Constants.table().METHOD.equals(refTableId) || Constants.table().TEST.equals(refTableId))
                    referenceName.setCase(Case.LOWER);
                else if (Constants.table().ORGANIZATION.equals(refTableId))
                    referenceName.setCase(Case.UPPER);

                if (state == State.QUERY)
                    return;

                data = manager.getExchangeLocalTerm();
                data.setReferenceTableId(refTableId);
                data.setReferenceId(null);
                data.setReferenceName("");
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

                data = manager.getExchangeLocalTerm();
                referenceName.setSelection(data.getReferenceId(), data.getReferenceName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                ExchangeLocalTermViewDO data;

                data = manager.getExchangeLocalTerm();
                data.setReferenceId(event.getValue());
                data.setReferenceName(referenceName.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                referenceName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        referenceName.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                if (referenceTableId.getValue() == null) {
                    Window.alert(consts.get("pleaseSelectType"));
                    event.cancel();
                }
            }
        });

        referenceName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String search;

                search = QueryFieldUtil.parseAutocomplete(event.getMatch());

                window.setBusy(consts.get("fetching"));

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

                window.clearStatus();
            }
        });

        termMappingTable = (TableWidget)def.getWidget("termMappingTable");
        addScreenHandler(termMappingTable,
                         new ScreenEventHandler<ArrayList<TableDataRow>>() {
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
                window.setBusy(consts.get("querying"));

                query.setRowsPerPage(20);
                ExchangeVocabularyMapService.get().query(query, new AsyncCallback<ArrayList<ExchangeLocalTermViewDO>>() {
                    public void onSuccess(ArrayList<ExchangeLocalTermViewDO> result) {
                        setQueryResult(result);
                    }

                                     public void onFailure(Throwable error) {
                                         setQueryResult(null);
                                         if (error instanceof NotFoundException) {
                                             window.setDone(consts.get("noRecordsFound"));
                                             setState(State.DEFAULT);
                                         } else if (error instanceof LastPageException) {
                                             window.setError(consts.get("noMoreRecordInDir"));
                                         } else {
                                             Window.alert("Error: Exchange Vocabulary Map call query failed; " +
                                                          error.getMessage());
                                             window.setError(consts.get("queryFailed"));
                                         }
                                     }
                                 });
            }

            public boolean fetch(ExchangeLocalTermViewDO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<ExchangeLocalTermViewDO> result;
                ArrayList<TableDataRow> model;
                String name;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (ExchangeLocalTermViewDO entry : result) {
                        if (entry.getReferenceDescription() == null)
                            name = entry.getReferenceName();
                        else
                            name = DataBaseUtil.concatWithSeparator(entry.getReferenceName(),
                                                                    ", ",
                                                                    entry.getReferenceDescription());
                        model.add(new TableDataRow(entry.getId(), name));
                    }
                }
                return model;
            }
        };

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
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
            if ("local_type_analyte".equals(d.getSystemName())) {
                row = new TableDataRow(Constants.table().ANALYTE, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("local_type_dictionary".equals(d.getSystemName())) {
                row = new TableDataRow(Constants.table().DICTIONARY, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("local_type_method".equals(d.getSystemName())) {
                row = new TableDataRow(Constants.table().METHOD, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("local_type_organization".equals(d.getSystemName())) {
                row = new TableDataRow(Constants.table().ORGANIZATION, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("local_type_test".equals(d.getSystemName())) {
                row = new TableDataRow(Constants.table().TEST, d.getEntry());
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
            referenceTableId.addException(new LocalizedException(consts.get("fieldRequiredException")));
        } else if (sels.size() > 1) {
            //
            // we don't allow more than one type to be selected
            //
            referenceTableId.addException(new LocalizedException(consts.get("onlyOneTypeSelectionForQueryException")));
        }
        return super.validate();
    }

    protected void query() {
        manager = ExchangeLocalTermManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(referenceTableId);
        window.setDone(consts.get("enterFieldsToQuery"));
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
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

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
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
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
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected void localTermHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getExchangeLocalTerm().getId(),
                            manager.getExchangeLocalTerm().getReferenceName());
        HistoryScreen.showHistory(consts.get("localTermHistory"),
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

        HistoryScreen.showHistory(consts.get("externalTermHistory"),
                                  Constants.table().EXCHANGE_EXTERNAL_TERM,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ExchangeLocalTermManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = ExchangeLocalTermManager.fetchWithExternalTerms(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
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

                row = new TableDataRow(5);
                row.cells.get(0).setValue(data.getIsActive());
                row.cells.get(1).setValue(data.getProfileId());
                row.cells.get(2).setValue(data.getExternalTerm());
                row.cells.get(3).setValue(data.getExternalDescription());
                row.cells.get(4).setValue(data.getExternalCodingSystem());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private ArrayList<TableDataRow> getAnalyteModel(String search) {
        ArrayList<AnalyteDO> list;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        try {
            list = AnalyteService.get().fetchByName(search);
            model = new ArrayList<TableDataRow>();
            for (AnalyteDO data : list)
                model.add(new TableDataRow(data.getId(), data.getName()));
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
        field.key = CategoryMeta.getDictionaryEntry();
        field.query = search + "*";
        field.type = QueryData.Type.STRING;
        query.setFields(field);

        field = new QueryData();
        field.key = CategoryMeta.getDictionaryIsActive();
        field.query = "Y";
        field.type = QueryData.Type.STRING;
        query.setFields(field);

        model = new ArrayList<TableDataRow>();
        try {
            list = DictionaryService.get().fetchByEntry(query);
            for (IdNameVO data : list) {
                row = new TableDataRow(1);
                row.key = data.getId();
                row.cells.get(0)
                         .setValue(DataBaseUtil.concatWithSeparator(data.getName(),
                                                                    ", ",
                                                                    data.getDescription()));
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
        ArrayList<MethodDO> list;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        try {
            list = MethodService.get().fetchByName(search);

            for (MethodDO data : list)
                model.add(new TableDataRow(data.getId(), data.getName()));
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
        ArrayList<String> list;

        model = new ArrayList<TableDataRow>();
        list = null;
        try {
            orgs = OrganizationService.get().fetchByIdOrName(search);
            for (OrganizationDO org : orgs) {
                addr = org.getAddress();
                if (list == null)
                    list = new ArrayList<String>();
                list.clear();
                list.add(org.getName());
                list.add(addr.getStreetAddress());
                list.add(addr.getCity());
                list.add(addr.getState());

                row = new TableDataRow(1);
                row.key = org.getId();
                row.cells.get(0).setValue(DataBaseUtil.concatWithSeparator(list, ", "));

                model.add(row);
            }
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private ArrayList<TableDataRow> getTestModel(String search) {
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<TestMethodVO> list;

        model = new ArrayList<TableDataRow>();
        try {
            list  = TestService.get().fetchByName(search);
            for (TestMethodVO data: list) {
                row = new TableDataRow(1);
                row.key = data.getTestId();
                row.cells.get(0)
                         .setValue(DataBaseUtil.concatWithSeparator(data.getTestName(),
                                                                    ", ",
                                                                    data.getMethodName()));
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }
}