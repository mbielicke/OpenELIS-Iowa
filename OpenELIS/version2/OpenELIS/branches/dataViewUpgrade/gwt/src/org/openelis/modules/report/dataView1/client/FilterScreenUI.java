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

import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import static org.openelis.ui.screen.State.*;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to choose the test analytes and/or aux data
 * and their results or values, to be shown in the data view report
 */
public abstract class FilterScreenUI extends Screen {

    @UiTemplate("Filter.ui.xml")
    interface FilterScreenUIBinder extends UiBinder<Widget, FilterScreenUI> {
    };

    private static FilterScreenUIBinder uiBinder = GWT.create(FilterScreenUIBinder.class);

    @UiField
    protected Table                     testAnalyteTable, resultTable, auxDataTable, valueTable;

    @UiField
    protected Button                    selectAllAnalyteButton, unselectAllAnalyteButton,
                    selectAllResultButton, unselectAllResultButton, selectAllAuxDataButton,
                    unselectAllAuxDataButton, runReportButton, selectAllValueButton,
                    unselectAllValueButton, cancelButton;

    public FilterScreenUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        setState(DEFAULT);
    }

    private void initialize() {
        addScreenHandler(testAnalyteTable, "testAnalyteTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                
            }
            
            public void onStateChange(StateChangeEvent event) {
                testAnalyteTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAnalyteButton : cancelButton;
            }
        });

        addScreenHandler(selectAllAnalyteButton, "selectAllAnalyteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectAllAnalyteButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? runReportButton : testAnalyteTable;
            }
        });

        addScreenHandler(unselectAllAnalyteButton, "unselectAllAnalyteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unselectAllAnalyteButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? resultTable : selectAllAnalyteButton;
            }
        });
        
        addScreenHandler(resultTable, "resultTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                
            }
            
            public void onStateChange(StateChangeEvent event) {
                resultTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAnalyteButton : cancelButton;
            }
        });

        addScreenHandler(selectAllResultButton, "selectAllResultButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectAllResultButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? unselectAllResultButton : testAnalyteTable;
            }
        });

        addScreenHandler(unselectAllResultButton, "unselectAllResultButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unselectAllResultButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? resultTable : selectAllResultButton;
            }
        });
        
        addScreenHandler(auxDataTable, "auxDataTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                
            }
            
            public void onStateChange(StateChangeEvent event) {
                auxDataTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAuxDataButton : cancelButton;
            }
        });

        addScreenHandler(selectAllAuxDataButton, "selectAllAuxDataButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectAllAuxDataButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? unselectAllAuxDataButton : auxDataTable;
            }
        });

        addScreenHandler(unselectAllAuxDataButton, "unselectAllAuxDataButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unselectAllAuxDataButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? valueTable : selectAllAuxDataButton;
            }
        });
        
        addScreenHandler(valueTable, "valueTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                
            }
            
            public void onStateChange(StateChangeEvent event) {
                valueTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllValueButton : cancelButton;
            }
        });

        addScreenHandler(selectAllValueButton, "selectAllValueButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                selectAllValueButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? unselectAllResultButton : valueTable;
            }
        });

        addScreenHandler(unselectAllValueButton, "unselectAllValueButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unselectAllValueButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? resultTable : selectAllValueButton;
            }
        });
        

        addScreenHandler(runReportButton, "runReportButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                runReportButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? cancelButton : unselectAllValueButton;
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? testAnalyteTable : runReportButton;
            }
        });
    }

    /**
     * overridden to respond to the user clicking "Run Report"
     */
    public abstract void runReport();

    /**
     * overridden to respond to the user clicking "Cancel"
     */
    public abstract void cancel();
    
    @UiHandler("runReportButton")
    protected void runReport(ClickEvent event) {
        runReport();
    }
    
    
    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }
}
