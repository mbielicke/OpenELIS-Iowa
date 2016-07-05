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

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.auxiliary1.client.AuxiliaryService1Impl;
import org.openelis.modules.eventLog.client.EventLogService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.patient.client.PatientService;
import org.openelis.modules.sample1.client.PatientPermission;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.systemvariable1.client.SystemVariableService1Impl;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SecondDataEntryScreenUI extends Screen implements CacheProvider {

    @UiTemplate("SecondDataEntry.ui.xml")
    interface SecondDataEntryUiBinder extends UiBinder<Widget, SecondDataEntryScreenUI> {
    };

    public static final SecondDataEntryUiBinder             uiBinder      = GWT.create(SecondDataEntryUiBinder.class);

    @UiField
    protected Table                                         table;

    @UiField
    protected Button                                        queryButton, nextButton,
                    previousButton, updateButton, commitButton, abortButton, nextPageButton;

    @UiField
    protected TextBox<Integer>                              accessionNumber;

    @UiField
    protected AutoComplete                                  historySystemUserLoginNames;

    @UiField
    protected Calendar                                      receivedDate;

    @UiField
    protected Dropdown<String>                              domain;

    @UiField
    protected TextArea                                      orderMessage;

    @UiField
    protected TabLayoutPanel                                tabPanel;

    @UiField(provided = true)
    protected EnvironmentalTabUI                            environmentalTab;

    @UiField(provided = true)
    protected SDWISTabUI                                    sdwisTab;

    @UiField(provided = true)
    protected ClinicalTabUI                                 clinicalTab;

    @UiField(provided = true)
    protected NeonatalTabUI                                 neonatalTab;

    @UiField(provided = true)
    protected PTTabUI                                       ptTab;

    @UiField(provided = true)
    protected AnimalTabUI                                   animalTab;

    @UiField(provided = true)
    protected NoDomainTabUI                                 noDomainTab;

    @UiField(provided = true)
    protected SampleNotesTabUI                              sampleNotesTab;

    protected ScreenNavigator<SecondDataEntryVO>            nav;

    protected SampleManager1                                manager;

    protected SecondDataEntryScreenUI                       screen;

    protected ModulePermission                              samplePermission;

    protected PatientPermission                             patientPermission;

    protected HashMap<String, Object>                       cache;

    protected SecondDataEntryServiceImpl                    service       = SecondDataEntryServiceImpl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<SecondDataEntryVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>               fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallbackUI<SampleManager1>               fetchByIdCall;

    protected Query                                         query;

    protected static int                                    ROWS_PER_PAGE = 500;

    protected boolean                                       allowScanTrf, scanTrfFetched;

    protected static final SampleManager1.Load              fetchElements[] = {
                    SampleManager1.Load.QA, SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE},
                    updateElements[] = {SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE,
                    SampleManager1.Load.EORDER, SampleManager1.Load.PROVIDER};

    protected enum Tab {
        ENVIRONMENTAL, SDWIS, CLINICAL, NEONATAL, PT, ANIMAL, NO_DOMAIN, SAMPLE_NOTES
    };

    public SecondDataEntryScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        if (UserCache.getPermission().getModule("verification") == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Second Data Entry"));

        samplePermission = UserCache.getPermission().getModule("sample");
        if (samplePermission == null)
            samplePermission = new ModulePermission();

        patientPermission = new PatientPermission();

        environmentalTab = new EnvironmentalTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        clinicalTab = new ClinicalTabUI(this);
        neonatalTab = new NeonatalTabUI(this);
        ptTab = new PTTabUI(this);
        animalTab = new AnimalTabUI(this);
        noDomainTab = new NoDomainTabUI(this);
        sampleNotesTab = new SampleNotesTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        showTabs();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Second Data Entry Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        String dom;
        Item<String> srow;
        ArrayList<Item<String>> smodel;

        screen = this;

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextPageButton.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * button panel buttons
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                       samplePermission.hasSelectPermission());
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
                                        samplePermission.hasUpdatePermission());
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
        addScreenHandler(accessionNumber,
                         SampleMeta.ACCESSION_NUMBER,
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 accessionNumber.setValue(getAccessionNumber());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(QUERY));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? historySystemUserLoginNames : domain;
                             }
                         });

        addScreenHandler(historySystemUserLoginNames,
                         SampleMeta.HISTORY_SYSTEM_USER_ID,
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 /*
                                  * -1 is set as the key instead of a user id;
                                  * that's because the display doesn't show just
                                  * one user, but all users who added/updated
                                  * the selected sample; the key can't be null
                                  * either because then the display will always
                                  * be blank
                                  */
                                 historySystemUserLoginNames.setValue( -1,
                                                                      getHistorySystemUserNames());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 historySystemUserLoginNames.setEnabled(isState(QUERY));
                             }

                             public Object getQuery() {
                                 QueryData qd;

                                 qd = (QueryData)historySystemUserLoginNames.getQuery();
                                 /*
                                  * if the query is -1 it means that the user
                                  * didn't query by user name; -1 is the default
                                  * key set in the autocomplete
                                  */
                                 if (qd != null && "-1".equals(qd.getQuery()))
                                     return null;
                                 return qd;
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDate : accessionNumber;
                             }
                         });

        historySystemUserLoginNames.addGetMatchesHandler(new GetMatchesHandler() {
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
                    historySystemUserLoginNames.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.toString());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }
            }
        });

        addScreenHandler(receivedDate, SampleMeta.RECEIVED_DATE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                receivedDate.setValue(getReceivedDate());
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(isState(QUERY));
                receivedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? domain : historySystemUserLoginNames;
            }
        });

        addScreenHandler(domain, SampleMeta.DOMAIN, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                domain.setValue(getDomain());
            }

            public void onStateChange(StateChangeEvent event) {
                domain.setEnabled(isState(QUERY));
                domain.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumber : receivedDate;
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

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(environmentalTab, "environmentalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().ENVIRONMENTAL.equals(getDomain()))
                    environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().ENVIRONMENTAL.equals(getDomain()))
                    environmentalTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().ENVIRONMENTAL.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().SDWIS.equals(getDomain()))
                    sdwisTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().SDWIS.equals(getDomain()))
                    sdwisTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().SDWIS.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(clinicalTab, "clinicalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().CLINICAL.equals(getDomain()))
                    clinicalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().CLINICAL.equals(getDomain()))
                    clinicalTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().CLINICAL.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(neonatalTab, "neonatalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().NEONATAL.equals(getDomain()))
                    neonatalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().NEONATAL.equals(getDomain()))
                    neonatalTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().NEONATAL.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(ptTab, "ptTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().PT.equals(getDomain()))
                    ptTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().PT.equals(getDomain()))
                    ptTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().PT.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(animalTab, "animalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                if (Constants.domain().ANIMAL.equals(getDomain()))
                    animalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (Constants.domain().ANIMAL.equals(getDomain()))
                    animalTab.setState(event.getState());
            }

            public void isValid(Validation validation) {
                if (Constants.domain().ANIMAL.equals(getDomain()))
                    super.isValid(validation);
            }
        });

        addScreenHandler(noDomainTab, "noDomainTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                noDomainTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                noDomainTab.setState(event.getState());
            }
        });

        addScreenHandler(sampleNotesTab, "sampleNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                sampleNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleNotesTab.setState(event.getState());
            }
        });

        /*
         * call for fetching a sample by its id
         */
        if (fetchByIdCall == null) {
            fetchByIdCall = new AsyncCallbackUI<SampleManager1>() {
                ScheduledCommand cmd;

                /*
                 * this command makes sure that widgets get initialized in the
                 * tabs before any other action takes place e.g. the state being
                 * set
                 */
                private void createCmd() {
                    if (cmd == null) {
                        cmd = new ScheduledCommand() {
                            @Override
                            public void execute() {
                                showTabs();
                                setData();
                                setState(DISPLAY);
                                fireDataChange();
                            }
                        };
                    }
                }

                public void success(SampleManager1 result) {
                    SystemVariableDO data;

                    manager = result;

                    createCmd();
                    Scheduler.get().scheduleDeferred(cmd);
                    clearStatus();

                    /*
                     * find out if verification by scanned TRF is allowed; don't
                     * look up the TRF if it's not allowed; don't try to fetch
                     * the system variable if it was tried to be fetched before
                     * but could not be fetched
                     */
                    if ( !scanTrfFetched) {
                        try {
                            data = SystemVariableService1Impl.INSTANCE
                                                        .fetchByExactName("ver_with_scanned_trf");
                            allowScanTrf = new Boolean(data.getValue());
                        } catch (NotFoundException e) {
                            // ignore
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage()
                                                                           : "null", e);
                        }
                    }

                    if (allowScanTrf)
                        displayTRF(manager.getSample().getId(),
                                   Messages.get().secondDataEntry_secondDataEntry());
                }

                public void notFound() {
                    fetchById(null);
                    setDone(Messages.get().gen_noRecordsFound());
                    clearStatus();
                }

                public void failure(Throwable e) {
                    fetchById(null);
                    Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        /*
         * call for locking and fetching a sample
         */
        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    int i;
                    SampleItemViewDO item;

                    manager = result;
                    /*
                     * the sample must still be not-verified
                     */
                    if ( !Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(manager.getSample()
                                                                                   .getStatusId())) {
                        Window.alert(Messages.get().secondDataEntry_sampleAlreadyVerified());
                        /*
                         * this resets the "busy" counter to 0, which is set to
                         * 1 when setBusy() is called by update(); otherwise,
                         * when abort() calls setBusy(), the counter will be
                         * incremented and won't be 0 even when setDone() is
                         * called; if the counter is not 0, the screen will stay
                         * locked
                         */
                        clearStatus();

                        state = UPDATE;
                        abort();
                        return;
                    }

                    /*
                     * samples with no analyses can't be verified
                     */
                    for (i = 0; i < manager.item.count(); i++ ) {
                        item = manager.item.get(i);
                        if (manager.analysis.count(item) > 0)
                            break;
                    }
                    if (i >= manager.item.count()) {
                        Window.alert(Messages.get().secondDataEntry_mustHaveAnalysesToVerify());
                        /*
                         * this is done for the same reason as above
                         */
                        clearStatus();

                        state = UPDATE;
                        abort();
                        return;
                    }

                    showTabs();
                    buildCache();
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                    clearStatus();
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        /*
         * call for updating a sample
         */
        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    Integer id;

                    manager = result;
                    /*
                     * create an event log to record that the sample was
                     * verified; if a field was verified using an operation like
                     * copying from the widget to the sample, the log's text
                     * would have the meta key of the field and the code for the
                     * operation
                     */
                    try {
                        id = DictionaryCache.getIdBySystemName("log_type_sample_verification");
                        EventLogService.get().add(id,
                                                  Messages.get()
                                                          .secondDataEntry_sampleVerification(),
                                                  Constants.table().SAMPLE,
                                                  manager.getSample().getId(),
                                                  Constants.dictionary().LOG_LEVEL_INFO,
                                                  getLogText());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                        return;
                    }
                    showTabs();
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    setDone(Messages.get().gen_updatingComplete());
                    /*
                     * the cache is cleared only if the update succeeds because
                     * otherwise, it can't be used by any tabs if the user wants
                     * to change any data
                     */
                    cache = null;
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        /*
         * call for unlocking a sample
         */
        if (unlockCall == null) {
            unlockCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    manager = result;
                    showTabs();
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    setDone(Messages.get().gen_updateAborted());
                    cache = null;
                }

                public void failure(Throwable e) {
                    setData();
                    setState(DEFAULT);
                    fireDataChange();
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                    cache = null;
                }
            };
        }

        /*
         * left hand navigation panel
         */
        nav = new ScreenNavigator<SecondDataEntryVO>(table, nextPageButton) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());
                /*
                 * this callback is not placed with the other callbacks because
                 * it uses some protected methods of ScreenNavigator which can't
                 * be accessed from outside
                 */
                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<SecondDataEntryVO>>() {
                        public void success(ArrayList<SecondDataEntryVO> result) {
                            int i;
                            ArrayList<SecondDataEntryVO> addedList;

                            clearStatus();
                            /*
                             * don't show samples containing patient info if the
                             * user doesn't have permission to view patients
                             */
                            i = 0;
                            while (i < result.size()) {
                                if ( !patientPermission.canViewSample(result.get(i)))
                                    result.remove(i);
                                else
                                    i++ ;
                            }

                            if (nav.getQuery().getPage() == 0) {
                                if (result.size() == 0) {
                                    notFound();
                                    return;
                                }
                                setQueryResult(result);
                            } else {
                                if (result.size() == 0)
                                    return;
                                addedList = getQueryResult();
                                addedList.addAll(result);
                                setQueryResult(addedList);
                                select(table.getModel().size() - result.size());
                            }
                        }

                        public void notFound() {
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
                ArrayList<SecondDataEntryVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (SecondDataEntryVO entry : result) {
                        row = new Item<Integer>(entry.getSampleId(),
                                                entry.getSampleAccessionNumber(),
                                                getHistorySystemUserNames(entry),
                                                entry.getSampleDomain());
                        row.setData(entry);
                        model.add(row);
                    }
                }
                return model;
            }

            @Override
            public boolean fetch(SecondDataEntryVO entry) {
                fetchById( (entry == null) ? null : entry.getSampleId());
                return true;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nav.enable(isState(DEFAULT, DISPLAY) && samplePermission.hasSelectPermission());
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        smodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_domain")) {
            dom = null;
            if (Constants.dictionary().ENVIRONMENTAL.equals(d.getId()))
                dom = Constants.domain().ENVIRONMENTAL;
            else if (Constants.dictionary().SDWIS.equals(d.getId()))
                dom = Constants.domain().SDWIS;
            else if (Constants.dictionary().CLINICAL.equals(d.getId()))
                dom = Constants.domain().CLINICAL;
            else if (Constants.dictionary().NEWBORN.equals(d.getId()))
                dom = Constants.domain().NEONATAL;
            else if (Constants.dictionary().PT.equals(d.getId()))
                dom = Constants.domain().PT;
            else if (Constants.dictionary().ANIMAL.equals(d.getId()))
                dom = Constants.domain().ANIMAL;

            if (dom != null) {
                srow = new Item<String>(dom, d.getEntry());
                smodel.add(srow);
            }
        }

        domain.setModel(smodel);
        ((Dropdown<String>)table.getColumnWidget(2)).setModel(smodel);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * Returns from the cache, the object that has the specified key and is of
     * the specified class
     */
    @Override
    public <T> T get(Object key, Class<?> c) {
        String cacheKey;
        Object obj;

        if (cache == null)
            return null;

        cacheKey = null;
        if (c == AuxFieldGroupManager1.class)
            cacheKey = Constants.uid().getAuxFieldGroup((Integer)key);

        obj = cache.get(cacheKey);
        if (obj != null)
            return (T)obj;

        /*
         * if the requested object is not in the cache then obtain it and put it
         * in the cache
         */
        try {
            if (c == AuxFieldGroupManager1.class)
                obj = AuxiliaryService1Impl.INSTANCE.fetchById((Integer)key);

            cache.put(cacheKey, obj);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
        }
        return (T)obj;
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
        showTabs();
        setData();
        setState(QUERY);
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
        setBusy(Messages.get().gen_lockForUpdate());

        SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                            updateElements,
                                            fetchForUpdateCall);
    }

    @UiHandler("commitButton")
    protected void commit(ClickEvent event) {
        commit();
    }

    protected void commit() {
        Validation validation;

        finishEditing();

        validation = validate();
        if (validation.getStatus() == Validation.Status.ERRORS) {
            showErrors(validation);
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
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
        Integer accession;
        String prefix, expires;
        PatientDO data;
        ValidationErrorsList e1;

        setBusy(Messages.get().gen_updating());
        /*
         * lock and update patient(s)
         */
        if (manager.getSampleClinical() != null) {
            data = manager.getSampleClinical().getPatient();

            /*
             * try to lock the patient if its fields were changed; update it if
             * locking succeeded
             */
            if (data.isChanged() || data.getAddress().isChanged()) {
                try {
                    PatientService.get().fetchForUpdate(data.getId());
                } catch (EntityLockedException e) {
                    expires = new Date(e.getExpires()).toString();
                    Window.alert(Messages.get()
                                         .secondDataEntry_patientLockException(e.getUserName(),
                                                                               expires));
                    clearStatus();
                    return;
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                    return;
                }

                try {
                    PatientService.get().validate(data);
                    data = PatientService.get().update(data);
                    manager.getSampleClinical().setPatient(data);
                } catch (ValidationErrorsList e) {
                    /*
                     * for display
                     */
                    accession = manager.getSample().getAccessionNumber();
                    if (accession == null)
                        accession = 0;

                    /*
                     * new FormErrorExceptions are created to prepend accession
                     * number to the messages of FormErrorExceptions returned by
                     * patient validation; other exceptions are shown as is
                     */
                    e1 = new ValidationErrorsList();
                    prefix = Messages.get().sample_accessionPrefix(accession);
                    for (Exception ex : e.getErrorList()) {
                        if (ex instanceof FormErrorException)
                            e1.add(new FormErrorException(DataBaseUtil.concatWithSeparator(prefix,
                                                                                           " ",
                                                                                           ex.getMessage())));
                        else
                            e1.add(ex);
                    }

                    showErrors(e1);
                    return;
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                    return;
                }
            }
        }
        manager.getSample().setStatusId(Constants.dictionary().SAMPLE_LOGGED_IN);
        SampleService1.get().update(manager, true, updateCall);
    }

    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        abort();
    }

    protected void abort() {
        SampleClinicalViewDO data;

        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                showTabs();
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                clearStatus();
            }
        } else if (isState(UPDATE)) {
            /*
             * unlock any locked or changed patient
             */
            data = manager.getSampleClinical();
            if (data != null && data.getPatient().isChanged()) {
                try {
                    PatientService.get().abortUpdate(data.getPatientId());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                    return;
                }
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
        clinicalTab.setData(manager);
        neonatalTab.setData(manager);
        ptTab.setData(manager);
        animalTab.setData(manager);
        sampleNotesTab.setData(manager);
    }

    /**
     * Returns the domain of the selected sample
     */
    private String getDomain() {
        return manager != null ? manager.getSample().getDomain() : null;
    }

    /**
     * Creates the cache of objects like AuxFieldGroupManager that are used
     * frequently by the different parts of the screen
     */
    private void buildCache() {
        int i;
        Integer prevId;
        ArrayList<Integer> ids;
        AuxDataViewDO aux;
        ArrayList<AuxFieldGroupManager1> afgms;

        cache = new HashMap<String, Object>();

        try {
            /*
             * the list of aux field groups to be fetched
             */
            ids = new ArrayList<Integer>();
            prevId = null;
            for (i = 0; i < manager.auxData.count(); i++ ) {
                aux = manager.auxData.get(i);
                if ( !aux.getAuxFieldGroupId().equals(prevId)) {
                    ids.add(aux.getAuxFieldGroupId());
                    prevId = aux.getAuxFieldGroupId();
                }
            }

            if (ids.size() > 0) {
                afgms = AuxiliaryService1Impl.INSTANCE.fetchByIds(ids);
                for (AuxFieldGroupManager1 afgm : afgms)
                    cache.put(Constants.uid().getAuxFieldGroup(afgm.getGroup().getId()), afgm);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
        }
    }

    /**
     * Makes the tabs applicable to the selected sample's domain visible and the
     * others not visible; also sets the notifications on the tab
     */
    private void showTabs() {
        int count1, count2;
        String domain, label;
        ArrayList<Tab> tabs;
        EnumSet<Tab> el;

        tabs = new ArrayList<Tab>();
        label = null;
        if (manager != null) {
            /*
             * find out which domain's tab is to be shown
             */
            domain = manager.getSample().getDomain();
            if (Constants.domain().ENVIRONMENTAL.equals(domain))
                tabs.add(Tab.ENVIRONMENTAL);
            else if (Constants.domain().SDWIS.equals(domain))
                tabs.add(Tab.SDWIS);
            else if (Constants.domain().CLINICAL.equals(domain))
                tabs.add(Tab.CLINICAL);
            else if (Constants.domain().NEONATAL.equals(domain))
                tabs.add(Tab.NEONATAL);
            else if (Constants.domain().PT.equals(domain))
                tabs.add(Tab.PT);
            else if (Constants.domain().ANIMAL.equals(domain))
                tabs.add(Tab.ANIMAL);
            else
                tabs.add(Tab.NO_DOMAIN);
            /*
             * create the notification for sample notes tab
             */
            count1 = manager.sampleExternalNote.get() == null ? 0 : 1;
            count2 = manager.sampleInternalNote.count();
            if (count1 > 0 || count2 > 0)
                label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
        } else {
            tabs.add(Tab.NO_DOMAIN);
        }

        /*
         * sample notes tab is always shown
         */
        tabs.add(Tab.SAMPLE_NOTES);

        el = EnumSet.copyOf(tabs);

        for (Tab tab : Tab.values())
            tabPanel.setTabVisible(tab.ordinal(), el.contains(tab));

        tabPanel.setTabNotification(Tab.SAMPLE_NOTES.ordinal(), label);

        /*
         * select the first visible tab if no tab is already selected
         */
        if (tabs.get(0) != Tab.NO_DOMAIN && tabPanel.getSelectedIndex() < 0)
            tabPanel.selectTab(tabs.get(0).ordinal());
    }

    /**
     * Fetches the manager for the sample with this id, sets the manager to null
     * if fetch fails or if the id is null
     */
    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            showTabs();
            setData();
            setState(DEFAULT);
            fireDataChange();
        } else {
            setBusy(Messages.get().gen_fetching());
            SampleService1.get().fetchById(id, fetchElements, fetchByIdCall);
        }
    }

    /**
     * Opens the file linked to the attachment showing on the passed row; if
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    private void displayTRF(Integer sampleId, String name) {
        try {
            /*
             * passing the same name to displayTRF makes sure that the files
             * open in the same window
             */
            AttachmentUtil.displayTRF(sampleId, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
        }
    }

    /**
     * This method is called after validating the screen and finding errors;
     * this can happen if the widgets are in error and/or if there were
     * exceptions added in a tab; if there are exceptions present, they are
     * shown in the bottom panel instead of the generic message
     * "Please correct..."; otherwise the user wouldn't get a chance to see
     * them; the generic message is shown if there are no exceptions; errors in
     * the widgets can be seen in either case
     */
    private void showErrors(Validation validation) {
        ArrayList<Exception> errors;

        errors = validation.getExceptions();
        if (errors != null && errors.size() > 0) {
            setError(Messages.get().gen_errorOneOfMultiple(errors.size(),
                                                           errors.get(0).getMessage()));
            window.setMessagePopup(errors, "ErrorPanel");
        } else {
            setError(Messages.get().gen_correctErrors());
        }
    }

    /**
     * Returns the text for the event log for this sample; the text shows which
     * operation, e.g. copy from or to the sample, was performed on which field
     */
    private String getLogText() {
        String domain;

        domain = manager.getSample().getDomain();
        if (Constants.domain().ENVIRONMENTAL.equals(domain))
            return environmentalTab.getLogText();
        else if (Constants.domain().SDWIS.equals(domain))
            return sdwisTab.getLogText();
        else if (Constants.domain().CLINICAL.equals(domain))
            return clinicalTab.getLogText();
        else if (Constants.domain().NEONATAL.equals(domain))
            return neonatalTab.getLogText();
        else if (Constants.domain().PT.equals(domain))
            return ptTab.getLogText();
        else if (Constants.domain().ANIMAL.equals(domain))
            return animalTab.getLogText();
        return null;
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
     * Returns the concatenation of the login names of the users who
     * added/updated the selected sample
     */
    private String getHistorySystemUserNames() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (manager == null || row == null)
            return null;

        return getHistorySystemUserNames((SecondDataEntryVO)row.getData());
    }

    /**
     * Returns the concatenation of the login names in the passed VO
     */
    private String getHistorySystemUserNames(SecondDataEntryVO entry) {
        return DataBaseUtil.concatWithSeparator(entry.getHistorySystemUserLoginNames(), ", ");
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
        String domain;

        if (manager == null || manager.getSample().getOrderId() == null)
            return null;

        domain = manager.getSample().getDomain();
        if (Constants.domain().ENVIRONMENTAL.equals(domain) ||
            Constants.domain().SDWIS.equals(domain) || Constants.domain().PT.equals(domain) ||
            Constants.domain().ANIMAL.equals(domain))
            return Messages.get().secondDataEntry_loadedWithSendoutOrder();
        else if (Constants.domain().CLINICAL.equals(domain) ||
                 Constants.domain().NEONATAL.equals(domain))
            return Messages.get().secondDataEntry_loadedWithEOrder();

        return null;
    }
}