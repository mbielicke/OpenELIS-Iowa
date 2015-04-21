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

import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.TestAnalyteDataViewVO;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

    protected DataView1VO               data;

    @UiField
    protected Table                     testAnalyteTable, resultTable, auxFieldTable, auxDataTable;

    @UiField
    protected Button                    selectAllAnalyteButton, unselectAllAnalyteButton,
                    selectAllResultButton, unselectAllResultButton, selectAllAuxDataButton,
                    unselectAllAuxDataButton, runReportButton, selectAllValueButton,
                    unselectAllValueButton, cancelButton;

    public FilterScreenUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        setState(DEFAULT);
        fireDataChange();
        
        logger.fine("Filter Screen Opened");
    }

    private void initialize() {
        addScreenHandler(testAnalyteTable, "testAnalyteTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<Row> model;
                
                model = getTestAnalyteTableModel();
                testAnalyteTable.setModel(model);
                if (model != null && model.size() > 0)
                    testAnalyteTable.applySort(1, Table.SORT_ASCENDING, null);
            }

            public void onStateChange(StateChangeEvent event) {
                testAnalyteTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAnalyteButton : cancelButton;
            }
        });
        
        testAnalyteTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                resultTable.setModel(getResultTableModel(event.getSelectedItem()));
            }
        });

        addScreenHandler(selectAllAnalyteButton,
                         "selectAllAnalyteButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 selectAllAnalyteButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? runReportButton : testAnalyteTable;
                             }
                         });

        addScreenHandler(unselectAllAnalyteButton,
                         "unselectAllAnalyteButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unselectAllAnalyteButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? resultTable : selectAllAnalyteButton;
                             }
                         });

        addScreenHandler(resultTable, "resultTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                resultTable.setModel(getResultTableModel(-1));
            }

            public void onStateChange(StateChangeEvent event) {
                resultTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAnalyteButton : cancelButton;
            }
        });

        addScreenHandler(selectAllResultButton,
                         "selectAllResultButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 selectAllResultButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? unselectAllResultButton : testAnalyteTable;
                             }
                         });

        addScreenHandler(unselectAllResultButton,
                         "unselectAllResultButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unselectAllResultButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? auxFieldTable : selectAllResultButton;
                             }
                         });

        addScreenHandler(auxFieldTable, "auxFieldTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                ArrayList<Row> model;
                
                model = getAuxFieldTableModel();
                auxFieldTable.setModel(model);
                if (model != null && model.size() > 0)
                    auxFieldTable.applySort(1, Table.SORT_ASCENDING, null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxFieldTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllAuxDataButton : unselectAllResultButton;
            }
        });
        
        auxFieldTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                auxDataTable.setModel(getAuxDataTableModel(event.getSelectedItem()));
            }
        });

        addScreenHandler(selectAllAuxDataButton,
                         "selectAllAuxDataButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 selectAllAuxDataButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? unselectAllAuxDataButton : auxFieldTable;
                             }
                         });

        addScreenHandler(unselectAllAuxDataButton,
                         "unselectAllAuxDataButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unselectAllAuxDataButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? auxDataTable : selectAllAuxDataButton;
                             }
                         });

        addScreenHandler(auxDataTable, "auxDataTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTable.setModel(getAuxDataTableModel(-1));
            }

            public void onStateChange(StateChangeEvent event) {
                auxDataTable.setEnabled(isState(DEFAULT));
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
                return forward ? unselectAllResultButton : auxDataTable;
            }
        });

        addScreenHandler(unselectAllValueButton,
                         "unselectAllValueButton",
                         new ScreenHandler<Object>() {
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

    public void setData(DataView1VO data) {
        this.data = data;
        fireDataChange();
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

    private ArrayList<Row> getTestAnalyteTableModel() {
        ArrayList<Row> model;
        ArrayList<TestAnalyteDataViewVO> analytes;        
        Row row;              
        
        model = new ArrayList<Row>();
        if (data == null)
            return model;
        
        analytes =  data.getTestAnalytes();
        for (TestAnalyteDataViewVO ana : analytes) {
            row = new Row(2);
            row.setCell(0,"N");
            row.setCell(1,ana.getAnalyteName());
            row.setData(ana);
            model.add(row);
        }
        
        return model;
    }
    
    private ArrayList<Row> getResultTableModel(int index) {
        ArrayList<Row> model;
        ArrayList<ResultDataViewVO> results;
        TestAnalyteDataViewVO data;
        Row row;

        model = new ArrayList<Row>();
        if (index == -1)
            return null;

        model = new ArrayList<Row>();
        row = testAnalyteTable.getRowAt(index);
        data = (TestAnalyteDataViewVO) row.getData();
        results = data.getResults();
        for (ResultDataViewVO res : results) {
            row = new Row(2);
            row.setCell(0,res.getIsIncluded());
            row.setCell(1,res.getValue());
            row.setData(res);
            model.add(row);
        }
        return model;
    }
    
    private ArrayList<Row> getAuxFieldTableModel() {
        ArrayList<Row> model;
        ArrayList<AuxFieldDataViewVO> auxFields;        
        Row row;

        model = new ArrayList<Row>();
        if (data == null)
            return model;
        
        auxFields =  data.getAuxFields();
        model = new ArrayList<Row>();
        for (AuxFieldDataViewVO aux : auxFields) {
            row = new Row(2);
            row.setCell(0,"N");
            row.setCell(1,aux.getAnalyteName());
            row.setData(aux);
            model.add(row);
        }
        
        return model;
    }
    
    private ArrayList<Row> getAuxDataTableModel(int index) {
        ArrayList<Row> model;
        ArrayList<AuxDataDataViewVO> auxiliary;
        AuxFieldDataViewVO data;
        Row row;

        if (index == -1)
            return null;

        model = new ArrayList<Row>();
        row = auxFieldTable.getRowAt(index);
        data = (AuxFieldDataViewVO) row.getData();
        auxiliary = data.getValues();
        for (AuxDataDataViewVO aux : auxiliary) {
            row = new Row(2);
            row.setCell(0,aux.getIsIncluded());
            row.setCell(1,aux.getValue());
            row.setData(aux);
            model.add(row);
        }
        return model;
    }
}