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
package org.openelis.modules.secondDataEntry.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.Screen.Validation.Status;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

public class SecondDataEntryScreenUI extends Screen {

    @UiTemplate("SecondDataEntry.ui.xml")
    interface SecondDataEntryUiBinder extends UiBinder<Widget, SecondDataEntryScreenUI> {
    };

    public static final SecondDataEntryUiBinder             uiBinder         = GWT.create(SecondDataEntryUiBinder.class);

    protected ModulePermission                              userPermission;

    protected ScreenNavigator<SecondDataEntryVO>            nav;

    @UiField
    protected Table                                         table;

    @UiField
    protected Button                                        queryButton, nextButton,
                    previousButton, updateButton, commitButton, abortButton, nextPageButton;

    @UiField
    protected TextBox<Integer>                              accessionNumber;

    @UiField
    protected TextBox<String>                               historySystemUser;
    
    @UiField
    protected Calendar                                     receivedDate;

    @UiField
    protected TextArea                                      orderMessage;

    @UiField
    protected DeckPanel                                     deckPanel;
    
    @UiField(provided = true)
    protected SampleNotesTabUI                          sampleNotesTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI                            environmentalTab;

    @UiField(provided = true)
    protected SDWISTabUI                                    sdwisTab;

    protected SampleManager1                                manager;

    protected HashMap<Integer, SampleManager1>              managers;

    protected HashMap<Integer, ArrayList<Integer>>          userIds;

    protected SecondDataEntryScreenUI                       screen;

    protected SecondDataEntryServiceImpl                    service          = SecondDataEntryServiceImpl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<SecondDataEntryVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>               fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallbackUI<ArrayList<SampleManager1>>    fetchByIdsCall;

    protected String                                        domain;

    protected boolean                                       envFieldsAdded, sdwisFieldsAdded;

    protected static int                                    ROWS_PER_PAGE    = 23;

    protected static final SampleManager1.Load              fetchElements[]  = {},
                    updateElements[] = {SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                    SampleManager1.Load.RESULT, SampleManager1.Load.EORDER,
                    SampleManager1.Load.PROVIDER};

    public SecondDataEntryScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Second Data Entry"));

        environmentalTab = new EnvironmentalTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        sampleNotesTab = new SampleNotesTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Second Data Entry Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                       userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    queryButton.lock();
                    queryButton.setPressed(true);
                }
            }
        });

        addShortcut(queryButton, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previousButton.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previousButton, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextButton.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(nextButton, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(UPDATE, DISPLAY) &&
                                        userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    updateButton.lock();
                    updateButton.setPressed(true);
                }
            }
        });

        addShortcut(updateButton, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commitButton.setEnabled(isState(QUERY, UPDATE));
            }
        });

        addShortcut(commitButton, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(QUERY, UPDATE));
            }
        });

        addShortcut(abortButton, 'o', CTRL);

        /*
         * screen fields and widgets
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Row row;

                if ( !isState(UPDATE))
                    return;

                /*
                 * in Update state, only the node that belongs to the locked
                 * sample can be selected
                 */
                row = table.getRowAt(event.getItem());
                if ( !manager.getSample().getId().equals(row.getData()))
                    event.cancel();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextPageButton.setEnabled(isState(DISPLAY));
            }
        });

        addScreenHandler(accessionNumber,
                         SampleMeta.getAccessionNumber(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 accessionNumber.setValue(getAccessionNumber());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(QUERY));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? historySystemUser : receivedDate;
                             }
                         });

        addScreenHandler(historySystemUser,
                         SampleMeta.getHistorySystemUserId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 historySystemUser.setValue(getUsers());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 historySystemUser.setEnabled(isState(QUERY));
                                 historySystemUser.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDate : accessionNumber;
                             }
                         });

        addScreenHandler(receivedDate,
                         SampleMeta.getReceivedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 receivedDate.setValue(getReceivedDate());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedDate.setEnabled(isState(QUERY));
                                 receivedDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? accessionNumber : historySystemUser;
                             }
                         });

        addScreenHandler(orderMessage, "orderMessage", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                orderMessage.setValue(getOrderMessage());
            }

            public void onStateChange(StateChangeEvent event) {
                orderMessage.setEnabled(false);
            }
        });

        addScreenHandler(environmentalTab, "environmentalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                environmentalTab.setState(event.getState());
            }
        });

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                sdwisTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<SecondDataEntryVO>(table, nextPageButton) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<SecondDataEntryVO>>() {
                        public void success(ArrayList<SecondDataEntryVO> result) {
                            ArrayList<SecondDataEntryVO> addedList;
                            HashSet<Integer> ids;

                            userIds = new HashMap<Integer, ArrayList<Integer>>();

                            clearStatus();
                            if (nav.getQuery().getPage() == 0) {
                                setQueryResult(result);
                                select(0);
                            } else {
                                addedList = getQueryResult();
                                addedList.addAll(result);
                                setQueryResult(addedList);
                                select(table.getModel().size() - result.size());
                                table.scrollToVisible(table.getModel().size() - 1);
                            }

                            ids = new HashSet<Integer>();
                            for (SecondDataEntryVO data : result)
                                ids.add(data.getSampleId());
                            fetchSamples(DataBaseUtil.toArrayList(ids));
                        }

                        public void notFound() {
                            manager = null;
                            setData();
                            setState(DEFAULT);
                            fireDataChange();
                            setQueryResult(null);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Second Data Entry call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(ROWS_PER_PAGE);
                service.query(query, queryCall);
            }

            public ArrayList<Item<Integer>> getModel() {
                Integer prevId, currId;
                Item<Integer> node;
                ArrayList<Integer> ids;
                ArrayList<SecondDataEntryVO> result;
                ArrayList<Item<Integer>> model;

                prevId = null;
                node = null;
                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (SecondDataEntryVO entry : result) {
                        currId = entry.getSampleId();
                        if ( !currId.equals(prevId)) {
                            node = new Item<Integer>(2);
                            node.setCell(0, entry.getSampleAccessionNumber());
                            node.setCell(1, entry.getHistorysystemUserLoginName());
                            node.setData(entry.getSampleId());
                            model.add(node);
                        } else {
                            node.setCell(1,
                                         DataBaseUtil.concatWithSeparator(node.getCell(1),
                                                                          ", ",
                                                                          entry.getHistorysystemUserLoginName()));
                        }

                        /*
                         * this keeps track of which users added/updated which
                         * sample
                         */
                        ids = userIds.get(currId);
                        if (ids == null) {
                            ids = new ArrayList<Integer>();
                            userIds.put(currId, ids);
                        }
                        ids.add(entry.getHistorySystemUserId());
                        prevId = currId;
                    }
                }
                return model;
            }

            @Override
            public boolean fetch(SecondDataEntryVO entry) {
                rowSelected(entry.getSampleId());
                return true;
            }
        };

        //
        // screen fields
        //
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public Validation validate() {
        String loginName;
        ArrayList<SystemUserVO> userList;
        Validation validation;

        historySystemUser.clearExceptions();
        validation = super.validate();

        if (isState(QUERY)) {
            loginName = historySystemUser.getText();
            if ( !DataBaseUtil.isEmpty(loginName)) {
                try {
                    userList = UserCacheService.get().getEmployees(loginName);
                    if (userList.size() == 0) {
                        validation.setStatus(Status.ERRORS);
                        historySystemUser.addException(new Exception(Messages.get()
                                                                             .secondDataEntry_enterValidUsername()));
                    }
                } catch (Exception anyE) {
                    Window.alert(anyE.getMessage());
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
            }
        }
        
        return validation;
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        ArrayList<SystemUserVO> userList;
        String loginName;
        StringBuffer userIds;

        fields = super.getQueryFields();
        for (QueryData field : fields) {
            if (SampleMeta.getHistorySystemUserId().equals(field.getKey())) {
                /*
                 * Since we cannot join with the security database to link
                 * system user's login name to the query, we need to lookup the
                 * matching id(s) from the UserCache to input into the query
                 */
                loginName = field.getQuery();

                field.setType(QueryData.Type.INTEGER);
                userIds = new StringBuffer();
                try {
                    userList = UserCacheService.get().getEmployees(loginName);
                    for (SystemUserVO userVO : userList) {
                        if (userIds.length() > 0)
                            userIds.append(" | ");
                        userIds.append(userVO.getId());
                    }
                    field.setQuery(userIds.toString());
                } catch (Exception anyE) {
                    Window.alert(anyE.getMessage());
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
                break;
            }
        }

        return fields;
    }

    /*
     * basic button methods
     */

    /**
     * Puts the screen in query state, sets the manager to null
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        manager = null;
        managers = null;
        userIds = null;
        domain = null;

        setData();
        setState(QUERY);
        table.setModel(null);
        deckPanel.showWidget(deckPanel.getWidgetCount() - 1);
        fireDataChange();
        accessionNumber.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previousButton")
    protected void previous(ClickEvent event) {
        if (DataBaseUtil.isSame(table.getSelectedRow(), 0)) {
            setError(Messages.get().gen_noMoreRecordInDir());
            return;
        }
        nav.previous();
    }

    @UiHandler("nextButton")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    ScheduledCommand cmd;

                    manager = result;
                    managers.put(manager.getSample().getId(), manager);

                    /*
                     * make sure that the sample is still not verified; if it is
                     * verified, unlock it because the user can't be allowed to
                     * change anything in it
                     */
                    if ( !Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(manager.getSample()
                                                                                   .getStatusId())) {
                        Window.alert(Messages.get().secondDataEntry_sampleAlreadyVerified());
                        state = UPDATE;
                        abort();
                    }

                    /*
                     * this is done so that if the domain of the locked sample
                     * was changed from when it was initially fetched, the
                     * fields for the current domain are shown
                     */
                    addWidgets(manager.getSample().getDomain());

                    /*
                     * this command makes sure that widgets get added to the tab
                     * before any other action takes place on the tab e.g. the
                     * state being set
                     */
                    cmd = new ScheduledCommand() {
                        @Override
                        public void execute() {
                            setData();
                            setState(UPDATE);
                            fireDataChange();
                        }
                    };
                    Scheduler.get().scheduleDeferred(cmd);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    Window.alert(e.getMessage());
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                            updateElements,
                                            fetchForUpdateCall);
    }

    @UiHandler("commitButton")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();

        validation = validate();
        if (Validation.Status.ERRORS.equals(validation.getStatus())) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate();
                break;
            case UPDATE:
                commitUpdate();
                break;
        }
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitUpdate() {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    manager = result;
                    managers.put(manager.getSample().getId(), manager);
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert("commitUpdate(): " + e.getMessage());
                    clearStatus();
                }
            };
        }

        SampleService1.get().update(manager, true, updateCall);
    }

    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        abort();
    }

    protected void abort() {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        manager = result;
                        managers.put(manager.getSample().getId(), manager);
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        setData();
                        setState(DEFAULT);
                        fireDataChange();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                        clearStatus();
                    }
                };
            }

            SampleService1.get().unlock(manager.getSample().getId(), fetchElements, unlockCall);
        }
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        environmentalTab.setData(manager);
        sdwisTab.setData(manager);
    }

    private void fetchSamples(ArrayList<Integer> ids) {
        if (fetchByIdsCall == null) {
            fetchByIdsCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    Item<Integer> row;

                    if (managers == null)
                        managers = new HashMap<Integer, SampleManager1>();

                    /*
                     * this map is used to link a tree node with the manager
                     * containing the sample and analysis that the node is
                     * showing
                     */
                    for (SampleManager1 sm : result)
                        managers.put(sm.getSample().getId(), sm);

                    setState(DISPLAY);
                    row = table.getRowAt(table.getSelectedRow());
                    rowSelected((Integer)row.getData());
                    clearStatus();
                }

                public void notFound() {
                    manager = null;
                    setData();
                    setState(DEFAULT);
                    fireDataChange();
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Second Data Entry call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        SampleService1.get().fetchByIds(ids, fetchElements, fetchByIdsCall);
    }

    private void rowSelected(Integer sampleId) {
        ScheduledCommand cmd;

        if (managers == null)
            return;

        /*
         * if a top level node is selected and if its sample has any
         * attachments, open the first attachment whose description begins with
         * "TRF"; if no such attachment is found, tell the user that an
         * attachment should be selected to open it; if an attachment's node is
         * selected, open it
         */
        manager = managers.get(sampleId);
        
        if (manager == null)
            return;

        displayTRF(sampleId,
                   manager.getSample().getAccessionNumber(),
                   Messages.get().secondDataEntry_secondDataEntry());

        if ( !isState(UPDATE)) {
            addWidgets(manager.getSample().getDomain());
            /*
             * this command makes sure that widgets get added to the tab before
             * any other action takes place on the tab e.g. the state being set
             */
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    setData();
                    setState(state);
                    fireDataChange();
                }
            };
            Scheduler.get().scheduleDeferred(cmd);
        }
    }

    /**
     * Opens the file linked to the attachment showing on the passed row; if
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    private void displayTRF(Integer sampleId, Integer accession, String name) {
        try {
            /*
             * passing the same name to displayTRF makes sure that the files
             * open in the same window
             */
            AttachmentUtil.displayTRF(sampleId, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Returns the accession number or null if the manager is null
     */
    private Integer getAccessionNumber() {
        if (manager != null)
            return manager.getSample().getAccessionNumber();
        return null;
    }

    /**
     * Returns the login names of the users who added/updated the selected
     * sample
     */
    private String getUsers() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null)
            return null;

        return row.getCell(1);
    }
    
    /**
     * Returns the received date or null if the manager is null
     */
    private Datetime getReceivedDate() {
        if (manager != null)
            return manager.getSample().getReceivedDate();
        return null;
    }

    /**
     * Returns the message that tells the user whether the sample was loaded
     * from a send-out order or e-order
     */
    private String getOrderMessage() {
        if (manager == null || manager.getSample().getOrderId() == null)
            return null;
        else if (Constants.domain().ENVIRONMENTAL.equals(domain) ||
                 Constants.domain().SDWIS.equals(domain) || Constants.domain().PT.equals(domain))
            return Messages.get().secondDataEntry_loadedWithSendoutOrder();
        else if (Constants.domain().CLINICAL.equals(domain) ||
                 Constants.domain().NEONATAL.equals(domain))
            return Messages.get().secondDataEntry_loadedWithEOrder();

        return null;
    }

    /**
     * Adds widgets used for verification for the passed domain to the screen
     */
    private void addWidgets(String dom) {
        ReportStatus status;

        /*
         * this is done to avoid clearing the widget table unless the current
         * sample's domain is different from the previous one's
         */
        if (DataBaseUtil.isSame(domain, dom))
            return;
        else
            domain = dom;

        try {
            /*
             * if this is the first time that a sample with this domain has been
             * selected in the tree, get the xml string for the passed domain
             * from the back-end and add the widgets to the appropriate tab;
             * otherwise just show the tab
             */
            if (Constants.domain().ENVIRONMENTAL.equals(dom)) {
                if ( !envFieldsAdded) {
                    status = service.getFields("env_verification_fields");
                    environmentalTab.addWidgets(status.getMessage());
                    envFieldsAdded = true;
                }
                deckPanel.showWidget(0);
            } else if (Constants.domain().SDWIS.equals(dom)) {
                if ( !sdwisFieldsAdded) {
                    status = service.getFields("sdwis_verification_fields");
                    sdwisTab.addWidgets(status.getMessage());
                    sdwisFieldsAdded = true;
                }
                deckPanel.showWidget(1);
            } else {
                deckPanel.showWidget(deckPanel.getWidgetCount() - 1);
                return;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }
}