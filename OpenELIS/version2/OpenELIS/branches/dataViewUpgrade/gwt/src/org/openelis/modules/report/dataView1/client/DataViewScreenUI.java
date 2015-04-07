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

import org.openelis.constants.Messages;
import org.openelis.domain.DataViewVO1;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreenUI extends Screen {

    @UiTemplate("DataView.ui.xml")
    interface DataViewScreenUiBinder extends UiBinder<Widget, DataViewScreenUI> {
    };

    private static DataViewScreenUiBinder uiBinder = GWT.create(DataViewScreenUiBinder.class);

    protected DataViewVO1                 data;

    @UiField
    protected Button                      openQueryButton, saveQueryButton, executeQueryButton;
    @UiField
    protected TabLayoutPanel              tabPanel;

    @UiField(provided = true)
    protected QueryTabUI                  queryTab;

    @UiField(provided = true)
    protected CommonTabUI                 commonTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI          environmentalTab;

    @UiField(provided = true)
    protected PrivateWellTabUI            privateWellTab;

    @UiField(provided = true)
    protected SDWISTabUI                  sdwisTab;

    @UiField(provided = true)
    protected ClinicalTabUI               clinicalTab;

    @UiField(provided = true)
    protected NeonatalTabUI               neonatalTab;

    @UiField(provided = true)
    protected PTTabUI                     ptTab;

    protected FilterScreenUI              filterScreen;

    public DataViewScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        queryTab = new QueryTabUI(this);
        commonTab = new CommonTabUI(this);
        environmentalTab = new EnvironmentalTabUI(this);
        privateWellTab = new PrivateWellTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        clinicalTab = new ClinicalTabUI(this);
        neonatalTab = new NeonatalTabUI(this);
        ptTab = new PTTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        data = new DataViewVO1();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Data View Screen Opened");
    }

    private void initialize() {
        addScreenHandler(openQueryButton, "openQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                openQueryButton.setEnabled(true);
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
            public void onDataChange(DataChangeEvent event) {
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
            public void onDataChange(DataChangeEvent event) {
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
            public void onDataChange(DataChangeEvent event) {
                environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                environmentalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(privateWellTab, "privateWellTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                privateWellTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                privateWellTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
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
            public void onDataChange(DataChangeEvent event) {
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
            public void onDataChange(DataChangeEvent event) {
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
            public void onDataChange(DataChangeEvent event) {
                ptTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                ptTab.setState(event.getState());
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

    private void setData() {
        queryTab.setData(data);
        commonTab.setData(data);
        environmentalTab.setData(data);
        privateWellTab.setData(data);
        sdwisTab.setData(data);
        clinicalTab.setData(data);
        neonatalTab.setData(data);
        ptTab.setData(data);
    }

    @UiHandler("executeQueryButton")
    protected void executeQuery(ClickEvent event) {
        boolean excludeResults, excludeAuxData;
        Validation validation;
        ModalWindow modal;
        ArrayList<String> columns;
        ArrayList<Exception> errors;
        ArrayList<QueryData> queryFields;

        finishEditing();

        columns = new ArrayList<String>();
        /*
         * find out which columns are selected in each tab
         */
        commonTab.addColumns(columns);
        environmentalTab.addColumns(columns);
        privateWellTab.addColumns(columns);
        sdwisTab.addColumns(columns);
        clinicalTab.addColumns(columns);
        neonatalTab.addColumns(columns);
        ptTab.addColumns(columns);

        /*
         * if the both results and aux data are excluded then at least one
         * column must be selected
         */
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        if (excludeResults && excludeAuxData && columns.size() == 0) {
            window.setError(Messages.get().dataView_selAtleastOneField());
            return;
        }

        window.clearStatus();

        validation = validate();
        if (validation.getStatus() == Validation.Status.ERRORS) {
            errors = validation.getExceptions();
            if (errors != null && errors.size() > 0) {
                setError(Messages.get().gen_errorOneOfMultiple(errors.size(),
                                                               errors.get(0).getMessage()));
                window.setMessagePopup(errors, "ErrorPanel");
            } else {
                window.setError(Messages.get().gen_correctErrors());
            }
            return;
        }

        data.setQueryFields(getQueryFields());
        data.setColumns(columns);

        if (filterScreen == null) {
            filterScreen = new FilterScreenUI() {
                @Override
                public void runReport() {
                    // TODO Auto-generated method stub
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "687px");
        modal.setName(Messages.get().filterScreen_testAnalyteAuxDataFilter());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(filterScreen);

        filterScreen.setWindow(modal);
        // filterScreen.setData(manager, state);
    }
}