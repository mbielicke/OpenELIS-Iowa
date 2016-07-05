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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAnalyteVO;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.StatusBarPopupScreenUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.fileupload.FileInput;
import org.openelis.ui.widget.fileupload.FormData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreenUI extends Screen {

    @UiTemplate("DataView.ui.xml")
    interface DataViewScreenUiBinder extends UiBinder<Widget, DataViewScreenUI> {
    };

    private static DataViewScreenUiBinder  uiBinder = GWT.create(DataViewScreenUiBinder.class);

    protected DataView1VO                  data;

    @UiField
    protected FileInput                    fileInput;

    @UiField
    protected Button                       openQueryButton, saveQueryButton, executeQueryButton;

    @UiField
    protected TabLayoutPanel               tabPanel;

    @UiField(provided = true)
    protected QueryTabUI                   queryTab;

    @UiField(provided = true)
    protected CommonTabUI                  commonTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI           environmentalTab;

    @UiField(provided = true)
    protected SDWISTabUI                   sdwisTab;

    @UiField(provided = true)
    protected ClinicalTabUI                clinicalTab;

    @UiField(provided = true)
    protected NeonatalTabUI                neonatalTab;

    @UiField(provided = true)
    protected PTTabUI                      ptTab;
    
    @UiField(provided = true)
    protected AnimalTabUI                 animalTab;

    @UiField(provided = true)
    protected ColumnOrderTabUI             columnOrderTab;

    protected DataViewScreenUI             screen;

    protected FilterScreenUI               filterScreen;

    private DataViewReportScreen1          reportScreen;

    private PopupPanel                     statusPanel;

    private StatusBarPopupScreenUI         statusScreen;

    private Timer                          timer;

    protected AsyncCallbackUI<DataView1VO> fetchTestAnalyteAndAuxFieldCall;

    public DataViewScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        queryTab = new QueryTabUI(this);
        commonTab = new CommonTabUI(this);
        environmentalTab = new EnvironmentalTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        clinicalTab = new ClinicalTabUI(this);
        neonatalTab = new NeonatalTabUI(this);
        ptTab = new PTTabUI(this);
        animalTab = new AnimalTabUI(this);
        columnOrderTab = new ColumnOrderTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        data = new DataView1VO();
        data.setExcludeResultOverride("N");
        data.setExcludeResults("N");
        data.setIncludeNotReportableResults("N");
        data.setExcludeAuxData("N");
        data.setIncludeNotReportableAuxData("N");
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Data View Screen Opened");
    }

    private void initialize() {
        screen = this;

        addScreenHandler(fileInput, "fileInput", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                fileInput.setEnabled(true);
            }
        });

        fileInput.setSendUrl("openelis/upload");

        fileInput.addFormDataCallback(new FormData.Callback() {
            @Override
            public void success() {
                openQuery();
            }

            @Override
            public void failure() {
                Window.alert(Messages.get().dataView_fileUploadException());
            }
        });

        addScreenHandler(saveQueryButton, "saveQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                saveQueryButton.setEnabled(true);
            }
        });

        addScreenHandler(executeQueryButton, "executeQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                executeQueryButton.setEnabled(true);
            }
        });

        /*
         * this is done to make the tabs detachable
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        /*
         * add the handlers for the tabs, so that they can be treated like other
         * widgets
         */
        addScreenHandler(queryTab, "queryTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                queryTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                queryTab.setState(event.getState());
            }

            public Object getQuery() {
                return queryTab.getQueryFields();
            }
        });

        addScreenHandler(commonTab, "commonTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                commonTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                commonTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(environmentalTab, "environmentalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                environmentalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                sdwisTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(clinicalTab, "clinicalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                clinicalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                clinicalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(neonatalTab, "neonatalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                neonatalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                neonatalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(ptTab, "ptTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                ptTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                ptTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(animalTab, "animalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                animalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                animalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(columnOrderTab, "columnOrderTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                columnOrderTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                columnOrderTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
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

    /**
     * This is overridden because the tabs need to be enabled or disabled based
     * on the data on the screen e.g. on opening a query; otherwise,
     * StateChangeEvent won't be fired because the screen's state never changes
     */
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * Sets the VO in the tabs
     */
    private void setData() {
        queryTab.setData(data);
        commonTab.setData(data);
        environmentalTab.setData(data);
        sdwisTab.setData(data);
        clinicalTab.setData(data);
        neonatalTab.setData(data);
        ptTab.setData(data);
        animalTab.setData(data);
        columnOrderTab.setData(data);
    }

    /**
     * Validates the screen and shows the errors if there are any; otherwise,
     * calls the service method to save the data on the screen in an xml file;
     * everything except analytes and values is saved
     */
    @UiHandler("saveQueryButton")
    protected void saveQuery(ClickEvent event) {
        Validation validation;

        finishEditing();

        clearStatus();
        validation = validate();
        if (validation.getStatus() == Validation.Status.ERRORS) {
            showErrors(validation);
            return;
        }

        data.setQueryFields(getQueryFields());
        data.setTestAnalytes(null);
        data.setAuxFields(null);

        getReportScreen("saveQuery", window, "DataView.xml");

        setBusy(Messages.get().gen_saving());
        runReport();
    }

    /**
     * Validates the screen and shows the errors if there are any; if there are
     * no errors, calls the service method to generate the report if the user
     * has excluded both results and aux data; otherwise, calls the service
     * method to fetch analytes and values and shows them on the filter screen
     */
    @UiHandler("executeQueryButton")
    protected void executeQuery(ClickEvent event) {
        boolean excludeResults, excludeAuxData;
        Validation validation;
        ArrayList<String> columns;

        finishEditing();

        /*
         * if both results and aux data are excluded then at least one column
         * must be selected
         */
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());
        columns = data.getColumns();
        if (excludeResults && excludeAuxData && (columns == null || columns.size() == 0)) {
            setError(Messages.get().dataView_selAtleastOneField());
            return;
        }

        clearStatus();
        validation = validate();
        if (validation.getStatus() == Validation.Status.ERRORS) {
            showErrors(validation);
            return;
        }

        data.setQueryFields(getQueryFields());
        data.setTestAnalytes(null);
        data.setAuxFields(null);
        setBusy(Messages.get().gen_querying());

        /*
         * if the user has excluded both results and aux data, don't show the
         * filter screen, just generate the report; otherwise fetch the analytes
         * and values and show them on the filter screen
         */
        if (excludeResults && excludeAuxData) {
            executeQuery(window);
        } else {
            if (fetchTestAnalyteAndAuxFieldCall == null) {
                fetchTestAnalyteAndAuxFieldCall = new AsyncCallbackUI<DataView1VO>() {
                    @Override
                    public void success(DataView1VO result) {
                        data.setTestAnalytes(result.getTestAnalytes());
                        data.setAuxFields(result.getAuxFields());
                        showFilter(data);
                        clearStatus();
                    }

                    @Override
                    public void notFound() {
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            DataViewReportService1.get()
                                  .fetchTestAnalyteAndAuxField(data,
                                                               fetchTestAnalyteAndAuxFieldCall);
        }
    }

    /**
     * Calls the service method to return a VO filled from a file containing
     * query fields, filters and columns used for a previous query; initializes
     * the filters if they're not set
     */
    private void openQuery() {
        data = null;

        try {
            data = (DataView1VO)DataViewReportService1.get().loadQuery();
            if (data == null) {
                Window.alert(Messages.get().report_fileNameNotValidException());
            } else {
                if (DataBaseUtil.isEmpty(data.getExcludeResultOverride()))
                    data.setExcludeResultOverride("N");
                if (DataBaseUtil.isEmpty(data.getExcludeResults()))
                    data.setExcludeResults("N");
                if (DataBaseUtil.isEmpty(data.getIncludeNotReportableResults()))
                    data.setIncludeNotReportableResults("N");
                if (DataBaseUtil.isEmpty(data.getExcludeAuxData()))
                    data.setExcludeAuxData("N");
                if (DataBaseUtil.isEmpty(data.getIncludeNotReportableAuxData()))
                    data.setIncludeNotReportableAuxData("N");
            }
        } catch (Exception e) {
            Window.alert("There was an error with loading the query: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        setData();
        setState(DEFAULT);
        fireDataChange();
    }

    /**
     * This method is called after validating the screen and finding errors;
     * this can happen if the widgets are in error and/or if there were
     * exceptions added in a tab like query tab; the exceptions are added
     * because the errors are due to a combination of widgets and not one
     * widget; if there are exceptions present, they are shown in the bottom
     * panel instead of the generic message "Please correct..."; otherwise the
     * user wouldn't get a chance to see them; the generic message is shown if
     * there are no exceptions; errors in the widgets can be seen in either case
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
     * Shows the screen that allows the user to select one or more analyte(s)
     * and value(s) linked to results and aux data; these were returned by the
     * query executed by the user
     */
    private void showFilter(DataView1VO data) {
        ModalWindow modal;

        if (filterScreen == null) {
            filterScreen = new FilterScreenUI() {
                @Override
                public void runReport() {
                    int numTA, numAux;
                    ArrayList<DataViewAnalyteVO> testAnas, auxFields;

                    numTA = 0;
                    numAux = 0;
                    testAnas = data.getTestAnalytes();
                    if (testAnas != null) {
                        for (DataViewAnalyteVO ta : testAnas) {
                            if ("Y".equals(ta.getIsIncluded()))
                                numTA++ ;
                        }
                    }

                    /*
                     * don't allow the report to be run if there are no test
                     * analytes or aux fields selected
                     */
                    if (numTA == 0) {
                        auxFields = data.getAuxFields();
                        if (auxFields != null) {
                            for (DataViewAnalyteVO af : auxFields) {
                                if ("Y".equals(af.getIsIncluded()))
                                    numAux++ ;
                            }
                        }
                        if (numAux == 0) {
                            window.setError(Messages.get().filter_selectOneAnaOrAux());
                            return;
                        }
                    }

                    screen.executeQuery(window);
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "687px");
        modal.setName(Messages.get().filter_testAnalyteAuxDataFilter());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(filterScreen);

        filterScreen.setWindow(modal);
        filterScreen.setData(data);
    }

    /**
     * Calls the service method to generate the data view report; shows a
     * progress bar for the status of the report; the status gets refreshed
     * every second, using a timer
     */
    private void executeQuery(WindowInt window) {
        getReportScreen("runReport", window, null);

        if (statusScreen == null) {
            statusScreen = new StatusBarPopupScreenUI() {
                @Override
                public boolean isStopVisible() {
                    return true;
                }

                @Override
                public void stop() {
                    /*
                     * set the attribute in the session that would tell the bean
                     * that the report should be stopped
                     */
                    DataViewReportService1.get().stopReport();
                }
            };

            /*
             * initialize and show the popup screen
             */
            statusPanel = new PopupPanel();
            statusPanel.setSize("450px", "125px");
            statusPanel.setWidget(statusScreen);
            statusPanel.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
            statusPanel.setModal(true);
        }
        statusPanel.show();
        statusScreen.setStatus(null);

        window.setBusy(Messages.get().report_generatingReport());

        runReport();

        /*
         * refresh the status of generating the report every second, until the
         * process successfully completes or is aborted because of an error or
         * by the user
         */
        if (timer == null) {
            timer = new Timer() {
                public void run() {
                    ReportStatus status;

                    try {
                        status = DataViewReportService1.get().getStatus();
                        /*
                         * the status only needs to be refreshed while the
                         * status panel is showing because once the job is
                         * finished, the panel is closed
                         */
                        if (statusPanel.isShowing()) {
                            statusScreen.setStatus(status);
                            this.schedule(50);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            };
        }
        timer.schedule(50);
    }

    /**
     * Calls a service method for generating a file and shows the file in the
     * front-end; the service method and type of file generated may vary
     */
    private void runReport() {
        reportScreen.runReport(data, new AsyncCallbackUI<ReportStatus>() {
            @Override
            public void success(ReportStatus result) {
                String url;

                hideStatus();
                if (result.getStatus() == ReportStatus.Status.SAVED) {
                    url = "/openelis/openelis/report?file=" + result.getMessage();
                    if (reportScreen.getAttachmentName() != null)
                        url += "&attachment=" + reportScreen.getAttachmentName();

                    Window.open(URL.encode(url), reportScreen.getRunReportInterface(), null);
                    reportScreen.getWindow().setDone("Generated file " + result.getMessage());
                } else {
                    reportScreen.getWindow().setDone(result.getMessage());
                }
            }

            @Override
            public void notFound() {
                hideStatus();
                reportScreen.getWindow().setDone(Messages.get().gen_noRecordsFound());
            }

            @Override
            public void failure(Throwable caught) {
                hideStatus();
                reportScreen.getWindow().setError("Failed");
                Window.alert(caught.getMessage());
                logger.log(Level.SEVERE, caught.getMessage(), caught);
            }
        });
    }

    /**
     * Creates DataViewReportScreen1 if it's not already present; sets the
     * passed arguments in it
     */
    private void getReportScreen(String reportMethod, WindowInt window, String attachment) {
        if (reportScreen == null) {
            try {
                reportScreen = new DataViewReportScreen1(reportMethod, window, attachment);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        } else {
            reportScreen.setWindow(window);
            reportScreen.setRunReportInterface(reportMethod);
            reportScreen.setAttachmentName(attachment);
        }
    }

    /**
     * Hides the panel that shows the progress bar
     */
    private void hideStatus() {
        if (statusPanel != null && statusPanel.isShowing()) {
            statusPanel.hide();
            statusScreen.setStatus(null);
        }
    }
}