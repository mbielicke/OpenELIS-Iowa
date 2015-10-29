package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.StatusBarPopupScreenUI;
import org.openelis.modules.sampleTracking1.client.SampleTrackingScreenUI;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.worksheetCompletion1.client.WorksheetCompletionScreenUI;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToDoScreenUI extends Screen {

    @UiTemplate("ToDo.ui.xml")
    interface ToDoUiBinder extends UiBinder<Widget, ToDoScreenUI> {
    };

    private static final ToDoUiBinder       uiBinder = GWT.create(ToDoUiBinder.class);

    @UiField
    protected Dropdown<String>              displaySections;

    @UiField
    protected Button                        refreshButton;

    @UiField
    protected Button                        trackingButton;

    @UiField
    protected Button                        exportToXl;

    @UiField
    protected TabLayoutPanel                tabPanel;

    @UiField(provided = true)
    protected LoggedInTabUI                 loggedInTab;

    @UiField(provided = true)
    protected InitiatedTabUI                initiatedTab;

    @UiField(provided = true)
    protected WorksheetTabUI                worksheetTab;

    @UiField(provided = true)
    protected CompletedTabUI                completedTab;

    @UiField(provided = true)
    protected ReleasedTabUI                 releasedTab;

    @UiField(provided = true)
    protected ToBeVerifiedTabUI             toBeVerifiedTab;

    @UiField(provided = true)
    protected OtherTabUI                    otherTab;

    @UiField(provided = true)
    protected InstrumentTabUI               instrumentTab;

    protected ToDoScreenUI                  screen;

    protected AsyncCallbackUI<ReportStatus> exportToExcelCall;

    private ToDoService1Impl                service  = ToDoService1Impl.INSTANCE;

    private PopupPanel                      statusPanel;

    private StatusBarPopupScreenUI          statusScreen;

    private Timer                           exportTimer;

    protected String                        displayExcelDirectory;

    public ToDoScreenUI(WindowInt window) throws Exception {
        SystemVariableDO sysVarDO;

        setWindow(window);
        try {
            CategoryCache.getBySystemNames(new String[] {"sample_domain", "analysis_status",
                            "todo_section"});
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }
        try {
            sysVarDO = SystemVariableService.get().fetchByExactName("worksheet_display_directory");
            displayExcelDirectory = sysVarDO.getValue();
        } catch (Exception anyE) {
            throw new Exception(Messages.get().worksheet_displayDirectoryLookupException());
        }

        loggedInTab = new LoggedInTabUI(this);
        initiatedTab = new InitiatedTabUI(this);
        worksheetTab = new WorksheetTabUI(this);
        completedTab = new CompletedTabUI(this);
        releasedTab = new ReleasedTabUI(this);
        toBeVerifiedTab = new ToBeVerifiedTabUI(this);
        otherTab = new OtherTabUI(this);
        instrumentTab = new InstrumentTabUI(this);

        initWidget((Widget)uiBinder.createAndBindUi(this));

        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        Item<String> row;
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;

        screen = this;

        addScreenHandler(displaySections, "displaySections", new ScreenHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                drawTabs(false);
                fireDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                displaySections.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? refreshButton : exportToXl;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(true);
                ;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                trackingButton.setEnabled(true);
                ;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                exportToXl.setEnabled(true);
                ;
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(loggedInTab, "loggedInTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    loggedInTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                loggedInTab.setState(event.getState());
            }

            public Object getQuery() {
                return loggedInTab.getQueryFields();
            }
        });

        addScreenHandler(initiatedTab, "initiatedTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    initiatedTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                initiatedTab.setState(event.getState());
            }

            public Object getQuery() {
                return initiatedTab.getQueryFields();
            }
        });

        addScreenHandler(worksheetTab, "worksheetTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    worksheetTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetTab.setState(event.getState());
            }

            public Object getQuery() {
                return worksheetTab.getQueryFields();
            }
        });

        addScreenHandler(completedTab, "completedTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    completedTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                completedTab.setState(event.getState());
            }

            public Object getQuery() {
                return completedTab.getQueryFields();
            }
        });

        addScreenHandler(releasedTab, "releasedTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    releasedTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                releasedTab.setState(event.getState());
            }

            public Object getQuery() {
                return releasedTab.getQueryFields();
            }
        });

        addScreenHandler(toBeVerifiedTab, "toBeVerifiedTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    toBeVerifiedTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                toBeVerifiedTab.setState(event.getState());
            }

            public Object getQuery() {
                return toBeVerifiedTab.getQueryFields();
            }
        });

        addScreenHandler(otherTab, "otherTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    otherTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                otherTab.setState(event.getState());
            }

            public Object getQuery() {
                return otherTab.getQueryFields();
            }
        });

        addScreenHandler(instrumentTab, "instrumentTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (displaySections.getSelectedItem() != null &&
                    displaySections.getSelectedItem().getKey() != null)
                    instrumentTab.onDataChange(displaySections.getSelectedItem().getKey());
            }

            public void onStateChange(StateChangeEvent event) {
                instrumentTab.setState(event.getState());
            }

            public Object getQuery() {
                return instrumentTab.getQueryFields();
            }
        });
        instrumentTab.setEnabled(false);
        instrumentTab.setVisible(false);

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                /*
                 * make sure that all detached tabs are closed when the main
                 * screen is closed
                 */
                tabPanel.close();
            }
        });

        setState(State.DEFAULT);
        fireDataChange();

        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("todo_section");
        for (DictionaryDO d : list) {
            row = new Item<String>(d.getCode(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        displaySections.setModel(model);
        displaySections.setValue("Y");
    }

    @UiHandler({"refreshButton"})
    protected void refresh(ClickEvent event) {
        fireDataChange();
    }

    @UiHandler({"trackingButton"})
    protected void tracking(ClickEvent event) {
        Integer id;

        try {
            switch (tabPanel.getSelectedIndex()) {
                case 0:
                    id = loggedInTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 1:
                    id = initiatedTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 2:
                    id = worksheetTab.getSelectedId();
                    if (id != null) {
                        showCompletionScreen(id);
                    }
                    break;
                case 3:
                    id = completedTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 4:
                    id = releasedTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 5:
                    id = toBeVerifiedTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 6:
                    id = otherTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
                case 7:
                    id = instrumentTab.getSelectedId();
                    if (id != null) {
                        showTrackingScreen(id);
                    }
                    break;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @UiHandler({"exportToXl"})
    protected void exportToXl(ClickEvent event) {
        createStatusBarPopup();
        statusPanel.show();
        statusScreen.setStatus(null);

        setBusy("Exporting to Excel");
        if (exportToExcelCall == null) {
            exportToExcelCall = new AsyncCallbackUI<ReportStatus>() {
                public void success(ReportStatus result) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);

                    if (result.getStatus() == ReportStatus.Status.SAVED) {
                        String url = "/openelis/openelis/report?file=" + result.getMessage();
                        Window.open(URL.encode(url), "ToDo", "resizable=yes");
                    }
                    window.clearStatus();
                }

                public void failure(Throwable e) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    Window.alert("export to Excel: " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }
        service.exportToExcel("m".equals(displaySections.getSelectedItem().getKey()),
                              exportToExcelCall);
        /*
         * refresh the status of exporting the worksheet to Excel every second,
         * until the process successfully completes or is aborted because of an
         * error
         */
        if (exportTimer == null) {
            exportTimer = new Timer() {
                public void run() {
                    ReportStatus status;
                    try {
                        status = service.getExportToExcelStatus();
                        /*
                         * the status only needs to be refreshed while the
                         * status panel is showing because once the job is
                         * finished, the panel is closed
                         */
                        if (statusPanel.isShowing()) {
                            statusScreen.setStatus(status);
                            schedule(1000);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            };
        }
        exportTimer.schedule(1000);
    }

    protected void onAttach() {
        super.onAttach();
        drawTabs(true);
    }

    private void drawTabs(boolean reattachChart) {
        String val;

        if ( (window == null) || (displaySections == null)) {
            return;
        }
        val = (String)displaySections.getSelectedItem().getKey();
        if (reattachChart) {
            loggedInTab.reattachChart();
            initiatedTab.reattachChart();
            completedTab.reattachChart();
            toBeVerifiedTab.reattachChart();
        }
        loggedInTab.draw(val);
        initiatedTab.draw(val);
        worksheetTab.draw(val);
        completedTab.draw(val);
        releasedTab.draw(val);
        toBeVerifiedTab.draw(val);
        otherTab.draw(val);
        instrumentTab.draw(val);
    }

    private void showTrackingScreen(final Integer id) throws Exception {
        final ArrayList<Integer> ids;
        org.openelis.ui.widget.Window window;
        final SampleTrackingScreenUI trackingScreen;
        ScheduledCommand cmd;

        try {
            ids = new ArrayList<Integer>();
            ids.add(id);
            window = new org.openelis.ui.widget.Window();
            window.setName(Messages.get().sampleTracking_tracking());
            window.setSize("1074px", "435px");
            trackingScreen = new SampleTrackingScreenUI(window);
            window.setContent(trackingScreen);
            OpenELIS.getBrowser().addWindow(window, "tracking");
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    trackingScreen.query(ids);
                }
            };
            Scheduler.get().scheduleDeferred(cmd);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void showCompletionScreen(final Integer id) throws Exception {
        ScheduledCommand cmd;
        org.openelis.ui.widget.Window window;
        final WorksheetCompletionScreenUI wcScreen;

        window = new org.openelis.ui.widget.Window();
        window.setName(Messages.get().worksheetCompletion());
        window.setSize("1061px", "511px");
        wcScreen = new WorksheetCompletionScreenUI(window);
        window.setContent(wcScreen);
        wcScreen.initialize();
        OpenELIS.getBrowser().addWindow(window, "worksheetCompletionUI");

        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                wcScreen.query(id);
            }
        };
        Scheduler.get().scheduleDeferred(cmd);
    }

    private void createStatusBarPopup() {
        if (statusScreen == null) {
            statusScreen = new StatusBarPopupScreenUI() {
                public boolean isStopVisible() {
                    return false;
                }

                public void stop() {
                }
            };

            /*
             * initialize and show the popup screen
             */
            statusPanel = new PopupPanel();
            statusPanel.setSize("450px", "125px");
            statusPanel.setWidget(statusScreen);
            statusPanel.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop());
            statusPanel.setModal(true);
        }
    }

}