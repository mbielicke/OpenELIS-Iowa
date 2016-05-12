package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.report.client.ToDoReportScreen;
import org.openelis.modules.sampleTracking1.client.SampleTrackingEntry;
import org.openelis.modules.sampleTracking1.client.SampleTrackingScreenUI;
import org.openelis.modules.worksheetCompletion1.client.WorksheetCompletionEntry;
import org.openelis.modules.worksheetCompletion1.client.WorksheetCompletionScreenUI;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckMenuItem;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ToDoScreenUI extends Screen {

    @UiTemplate("ToDo.ui.xml")
    interface ToDoUiBinder extends UiBinder<Widget, ToDoScreenUI> {
    };

    private static final ToDoUiBinder uiBinder = GWT.create(ToDoUiBinder.class);

    @UiField
    protected Button                  refreshButton, detailsButton, exportButton;

    @UiField
    protected CheckMenuItem           showTrf, mySection;

    @UiField
    protected TabLayoutPanel          tabPanel;

    @UiField(provided = true)
    protected LoggedInTabUI           loggedInTab;

    @UiField(provided = true)
    protected InitiatedTabUI          initiatedTab;

    @UiField(provided = true)
    protected WorksheetTabUI          worksheetTab;

    @UiField(provided = true)
    protected CompletedTabUI          completedTab;

    @UiField(provided = true)
    protected ReleasedTabUI           releasedTab;

    @UiField(provided = true)
    protected ToBeVerifiedTabUI       toBeVerifiedTab;

    @UiField(provided = true)
    protected OtherTabUI              otherTab;

    protected ToDoScreenUI            screen;

    protected Integer                 lastSampleId;

    public ToDoScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        try {
            CategoryCache.getBySystemNames(new String[] {"sample_domain", "analysis_status"});
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        loggedInTab = new LoggedInTabUI(this);
        initiatedTab = new InitiatedTabUI(this);
        worksheetTab = new WorksheetTabUI(this);
        completedTab = new CompletedTabUI(this);
        releasedTab = new ReleasedTabUI(this);
        toBeVerifiedTab = new ToBeVerifiedTabUI(this);
        otherTab = new OtherTabUI(this);

        initWidget((Widget)uiBinder.createAndBindUi(this));

        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        screen = this;

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(true);
                detailsButton.setEnabled(true);
                exportButton.setEnabled(true);

                loggedInTab.setState(state);
                initiatedTab.setState(state);
                worksheetTab.setState(state);
                completedTab.setState(state);
                releasedTab.setState(state);
                toBeVerifiedTab.setState(state);
                otherTab.setState(state);
            }
        });

        /*
         * option menu
         */
        showTrf.setEnabled(true);
        showTrf.addCommand(new Command() {
            public void execute() {
                showTrf();
            }
        });

        mySection.setCheck(true);
        mySection.addCommand(new Command() {
            public void execute() {
                loggedInTab.setMySectionOnly(mySection.isChecked());
                initiatedTab.setMySectionOnly(mySection.isChecked());
                worksheetTab.setMySectionOnly(mySection.isChecked());
                completedTab.setMySectionOnly(mySection.isChecked());
                releasedTab.setMySectionOnly(mySection.isChecked());
                toBeVerifiedTab.setMySectionOnly(mySection.isChecked());
                otherTab.setMySectionOnly(mySection.isChecked());
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        bus.addHandler(SelectionEvent.getType(), new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                showTrf();
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                /*
                 * make sure that all detached tabs are closed when the main
                 * screen is closed
                 */
                tabPanel.close();
            }
        });
    }

    @UiHandler({"refreshButton"})
    protected void refresh(ClickEvent event) {
        loggedInTab.refresh();
        initiatedTab.refresh();
        worksheetTab.refresh();
        completedTab.refresh();
        releasedTab.refresh();
        toBeVerifiedTab.refresh();
        otherTab.refresh();
    }

    @UiHandler({"detailsButton"})
    protected void details(ClickEvent event) {
        Integer id;

        id = null;
        try {
            switch (tabPanel.getSelectedIndex()) {
                case 0:
                    id = loggedInTab.getSelectedId();
                    break;
                case 1:
                    id = initiatedTab.getSelectedId();
                    break;
                case 2:
                    id = worksheetTab.getSelectedId();
                    break;
                case 3:
                    id = completedTab.getSelectedId();
                    break;
                case 4:
                    id = releasedTab.getSelectedId();
                    break;
                case 5:
                    id = toBeVerifiedTab.getSelectedId();
                    break;
                case 6:
                    id = otherTab.getSelectedId();
                    break;
            }

            if (id == null) {
                setError(Messages.get().todo_selectRowMessage());
            } else {
                clearStatus();
                if (tabPanel.getSelectedIndex() == 2)
                    showCompletionScreen(id);
                else
                    showTrackingScreen(id);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @UiHandler({"exportButton"})
    protected void export(ClickEvent event) {
        ToDoReportScreen export;

        try {
            export = new ToDoReportScreen(window);
            export.setMySectionOnly(mySection.isChecked());

            setBusy(Messages.get().todo_exportingToExcel());
            export.runReport(null);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        clearStatus();
    }

    private void showTrackingScreen(final Integer id) {
        final ArrayList<Integer> ids;
        final SampleTrackingScreenUI trackingScreen;
        ScheduledCommand cmd;
        SampleTrackingEntry entry;

        setBusy();

        try {
            entry = new SampleTrackingEntry();
            trackingScreen = entry.addScreen();

            ids = new ArrayList<Integer>();
            ids.add(id);
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

        clearStatus();
    }

    private void showCompletionScreen(final Integer id) {
        final WorksheetCompletionScreenUI worksheetScreen;
        ScheduledCommand cmd;
        WorksheetCompletionEntry entry;

        setBusy();

        try {
            entry = new WorksheetCompletionEntry();
            worksheetScreen = entry.addScreen();

            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    worksheetScreen.query(id);
                }
            };
            Scheduler.get().scheduleDeferred(cmd);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        clearStatus();
    }

    private void showTrf() {
        Integer id;

        if ( !showTrf.isChecked()) {
            /*
             * this allows showing the TRF for a sample just by unchecking and
             * checking the checkbox and not having to click the row for a
             * different sample
             */
            lastSampleId = null;
            return;
        }

        id = null;
        switch (tabPanel.getSelectedIndex()) {
            case 0:
                id = loggedInTab.getSelectedId();
                break;
            case 1:
                id = initiatedTab.getSelectedId();
                break;
            case 2:
                /*
                 * ignore because this is worksheet tab 
                 */
                return;
            case 3:
                id = completedTab.getSelectedId();
                break;
            case 4:
                id = releasedTab.getSelectedId();
                break;
            case 5:
                id = toBeVerifiedTab.getSelectedId();
                break;
            case 6:
                id = otherTab.getSelectedId();
                break;
        }

        if (id == null) {
            setError(Messages.get().todo_selectRowMessage());
        } else if ( !id.equals(lastSampleId)) {
            clearStatus();
            lastSampleId = id;
            try {
                AttachmentUtil.displayTRF(id, null, window);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
            }
        }

    }
}