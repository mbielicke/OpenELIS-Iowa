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

import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAnalyteVO;
import org.openelis.domain.DataViewValueVO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

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
    protected Button                    selectAllTestAnalyteButton, unselectAllTestAnalyteButton,
                    selectAllResultButton, unselectAllResultButton, selectAllAuxFieldButton,
                    unselectAllAuxFieldButton, selectAllValueButton, unselectAllValueButton,
                    runReportButton, cancelButton;

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
                return forward ? selectAllTestAnalyteButton : cancelButton;
            }
        });

        testAnalyteTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                resultTable.setModel(getResultTableModel(event.getSelectedItem()));
            }
        });

        testAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                DataViewAnalyteVO data;
                String val;

                r = event.getRow();
                c = event.getCol();
                val = testAnalyteTable.getValueAt(r, c);
                data = (DataViewAnalyteVO)testAnalyteTable.getRowAt(r).getData();
                switch (c) {
                    case 0:
                        /*
                         * when the checkbox for an analyte is checked or
                         * unchecked the checkboxes for all the results
                         * asscoiated with it need to be checked or unchecked
                         * too; also, the analyte and the result need to be
                         * flagged as "included" or not, appropriately
                         */
                        updateResultsForAnalyte(data, val);
                        data.setIsIncluded(val);
                        break;
                }
            }
        });

        addScreenHandler(selectAllTestAnalyteButton,
                         "selectAllAnalyteButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 selectAllTestAnalyteButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? runReportButton : testAnalyteTable;
                             }
                         });

        addScreenHandler(unselectAllTestAnalyteButton,
                         "unselectAllAnalyteButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unselectAllTestAnalyteButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? resultTable : selectAllTestAnalyteButton;
                             }
                         });

        addScreenHandler(resultTable, "resultTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                resultTable.setModel(getResultTableModel( -1));
            }

            public void onStateChange(StateChangeEvent event) {
                resultTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllTestAnalyteButton : cancelButton;
            }
        });

        resultTable.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                DataViewValueVO data;

                r = event.getRow();
                c = event.getCol();
                val = resultTable.getValueAt(r, c);
                data = (DataViewValueVO)resultTable.getRowAt(r).getData();
                switch (c) {
                    case 0:
                        data.setIsIncluded(val);
                        break;
                }
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
                return forward ? selectAllAuxFieldButton : unselectAllResultButton;
            }
        });

        auxFieldTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                auxDataTable.setModel(getAuxDataTableModel(event.getSelectedItem()));
            }
        });

        auxFieldTable.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                DataViewAnalyteVO data;
                String val;

                r = event.getRow();
                c = event.getCol();
                val = auxFieldTable.getValueAt(r, c);
                data = (DataViewAnalyteVO)auxFieldTable.getRowAt(r).getData();
                switch (c) {
                    case 0:
                        /*
                         * when the checkbox for an analyte is checked or
                         * unchecked the checkboxes for all the aux data
                         * asscoiated with it need to be checked or unchecked
                         * too; also, the analyte and the aux data need to be
                         * flagged as "included" or not, appropriately
                         */
                        updateAuxDataForAnalyte(data, val);
                        data.setIsIncluded(val);
                        break;
                }
            }
        });

        addScreenHandler(selectAllAuxFieldButton,
                         "selectAllAuxDataButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 selectAllAuxFieldButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? unselectAllAuxFieldButton : auxFieldTable;
                             }
                         });

        addScreenHandler(unselectAllAuxFieldButton,
                         "unselectAllAuxDataButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unselectAllAuxFieldButton.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? auxDataTable : selectAllAuxFieldButton;
                             }
                         });

        addScreenHandler(auxDataTable, "auxDataTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTable.setModel(getAuxDataTableModel( -1));
            }

            public void onStateChange(StateChangeEvent event) {
                auxDataTable.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? selectAllValueButton : cancelButton;
            }
        });

        auxDataTable.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                DataViewValueVO data;

                r = event.getRow();
                c = event.getCol();
                val = auxDataTable.getValueAt(r, c);
                data = (DataViewValueVO)auxDataTable.getRowAt(r).getData();
                switch (c) {
                    case 0:
                        data.setIsIncluded(val);
                        break;
                }
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

    @UiHandler("selectAllTestAnalyteButton")
    protected void selectAllTestAnalyte(ClickEvent event) {
        updateAllTestAnalyte("Y");
    }

    @UiHandler("unselectAllTestAnalyteButton")
    protected void unselectAllTestAnalyte(ClickEvent event) {
        updateAllTestAnalyte("N");
    }

    @UiHandler("selectAllResultButton")
    protected void selectAllResult(ClickEvent event) {
        updateAllResult("Y");
    }

    @UiHandler("unselectAllResultButton")
    protected void unselectAllResult(ClickEvent event) {
        updateAllResult("N");
    }

    @UiHandler("selectAllAuxFieldButton")
    protected void selectAllAuxField(ClickEvent event) {
        updateAllAuxField("Y");
    }

    @UiHandler("unselectAllAuxFieldButton")
    protected void unselectAllAuxField(ClickEvent event) {
        updateAllAuxField("N");
    }

    @UiHandler("selectAllValueButton")
    protected void selectAllValue(ClickEvent event) {
        updateAllValue("Y");
    }

    @UiHandler("unselectAllValueButton")
    protected void unselectAllValue(ClickEvent event) {
        updateAllValue("N");
    }

    private ArrayList<Row> getTestAnalyteTableModel() {
        ArrayList<Row> model;
        ArrayList<DataViewAnalyteVO> analytes;
        Row row;

        model = new ArrayList<Row>();
        if (data == null)
            return model;

        analytes = data.getTestAnalytes();
        if (analytes != null) {
            for (DataViewAnalyteVO ana : analytes) {
                row = new Row(2);
                row.setCell(0, "N");
                row.setCell(1, ana.getAnalyteName());
                row.setData(ana);
                model.add(row);
            }
        }

        return model;
    }

    private ArrayList<Row> getResultTableModel(int index) {
        ArrayList<Row> model;
        ArrayList<DataViewValueVO> results;
        DataViewAnalyteVO data;
        Row row;

        model = new ArrayList<Row>();
        if (index == -1)
            return model;

        row = testAnalyteTable.getRowAt(index);
        data = (DataViewAnalyteVO)row.getData();
        results = data.getValues();
        for (DataViewValueVO res : results) {
            row = new Row(2);
            row.setCell(0, res.getIsIncluded());
            row.setCell(1, res.getValue());
            row.setData(res);
            model.add(row);
        }
        return model;
    }

    private ArrayList<Row> getAuxFieldTableModel() {
        ArrayList<Row> model;
        ArrayList<DataViewAnalyteVO> auxFields;
        Row row;

        model = new ArrayList<Row>();
        if (data == null)
            return model;

        auxFields = data.getAuxFields();
        if (auxFields != null) {
            for (DataViewAnalyteVO aux : auxFields) {
                row = new Row(2);
                row.setCell(0, "N");
                row.setCell(1, aux.getAnalyteName());
                row.setData(aux);
                model.add(row);
            }
        }

        return model;
    }

    private ArrayList<Row> getAuxDataTableModel(int index) {
        ArrayList<Row> model;
        ArrayList<DataViewValueVO> auxiliary;
        DataViewAnalyteVO data;
        Row row;

        model = new ArrayList<Row>();
        if (index == -1)
            return model;

        row = auxFieldTable.getRowAt(index);
        data = (DataViewAnalyteVO)row.getData();
        auxiliary = data.getValues();
        for (DataViewValueVO aux : auxiliary) {
            row = new Row(2);
            row.setCell(0, aux.getIsIncluded());
            row.setCell(1, aux.getValue());
            row.setData(aux);
            model.add(row);
        }
        return model;
    }

    /**
     * Sets the passed value as the "include" flag for all test analytes and
     * their results
     */
    private void updateAllTestAnalyte(String newVal) {
        DataViewAnalyteVO data;
        Object val;
        Row row;
        ArrayList<Row> model;

        model = testAnalyteTable.getModel();
        for (int i = 0; i < model.size(); i++ ) {
            val = (String)testAnalyteTable.getValueAt(i, 0);
            if ( !DataBaseUtil.isSame(newVal, val)) {
                row = model.get(i);
                data = (DataViewAnalyteVO)row.getData();
                data.setIsIncluded(newVal);
                testAnalyteTable.setValueAt(i, 0, newVal);
                updateResultsForAnalyte(data, newVal);
            }
        }
    }

    /**
     * Sets the passed value as the "include" flag for all results of the passed
     * test analyte
     */
    private void updateResultsForAnalyte(DataViewAnalyteVO data, String val) {
        int r;
        DataViewAnalyteVO sel;
        DataViewValueVO res;
        ArrayList<DataViewValueVO> list;

        list = data.getValues();
        r = testAnalyteTable.getSelectedRow();
        sel = r > -1 ? (DataViewAnalyteVO)testAnalyteTable.getRowAt(r).getData() : null;
        for (int i = 0; i < list.size(); i++ ) {
            res = list.get(i);
            /*
             * this method can be called when all test analytes are
             * selected/unselected; at that time, no results may be shown if no
             * analyte is selected or the results being shown may be of a
             * different analyte than the passed one; this check makes sure that
             * an exception is not thrown on trying to check/uncheck a analyte's
             * checkbox if the result table doesn't have as many rows as the
             * number of results for the passed analyte
             */
            if (data == sel)
                resultTable.setValueAt(i, 0, val);
            res.setIsIncluded(val);
        }
    }
    
    /**
     * Sets the passed value as the "include" flag for all result values
     */
    private void updateAllResult(String newVal) {
        DataViewValueVO data;
        Object val;
        Row row;
        ArrayList<Row> model;

        model = resultTable.getModel();
        for (int i = 0; i < model.size(); i++ ) {
            val = (String)resultTable.getValueAt(i, 0);
            if ( !DataBaseUtil.isSame(newVal, val)) {
                row = model.get(i);
                data = (DataViewValueVO)row.getData();
                data.setIsIncluded(newVal);
                resultTable.setValueAt(i, 0, newVal);
            }
        }
    }

    /**
     * Sets the passed value as the "include" flag for all aux fields and their
     * aux data
     */
    private void updateAllAuxField(String newVal) {
        Row row;
        Object val;
        DataViewAnalyteVO data;
        ArrayList<Row> model;

        model = auxFieldTable.getModel();
        for (int i = 0; i < model.size(); i++ ) {
            val = (String)auxFieldTable.getValueAt(i, 0);
            if ( !DataBaseUtil.isSame(newVal, val)) {
                row = model.get(i);
                data = (DataViewAnalyteVO)row.getData();
                data.setIsIncluded(newVal);
                auxFieldTable.setValueAt(i, 0, newVal);
                updateAuxDataForAnalyte(data, newVal);
            }
        }
    }

    /**
     * Sets the passed value as the "include" flag for all aux data of the
     * passed aux field
     */
    private void updateAuxDataForAnalyte(DataViewAnalyteVO data, String val) {
        int r;
        DataViewAnalyteVO sel;
        DataViewValueVO aux;
        ArrayList<DataViewValueVO> list;

        list = data.getValues();
        r = auxFieldTable.getSelectedRow();
        sel = r > -1 ? (DataViewAnalyteVO)auxFieldTable.getRowAt(r).getData() : null;
        for (int i = 0; i < list.size(); i++ ) {
            aux = list.get(i);
            /*
             * this method can be called when all aux fields are
             * selected/unselected; at that time, no aux data may be shown if no
             * aux field is selected or the aux data being shown may be of a
             * different aux field than the passed one; this check makes sure
             * that an exception is not thrown on trying to check/uncheck a aux
             * field's checkbox if the aux data table doesn't have as many rows
             * as the number of aux data for the passed aux field
             */
            if (data == sel)
                auxDataTable.setValueAt(i, 0, val);
            aux.setIsIncluded(val);
        }
    }
    
    /**
     * Sets the passed value as the "include" flag for all aux data values
     */
    private void updateAllValue(String newVal) {
        DataViewValueVO data;
        Object val;
        Row row;
        ArrayList<Row> model;

        model = auxDataTable.getModel();
        for (int i = 0; i < model.size(); i++ ) {
            val = (String)auxDataTable.getValueAt(i, 0);
            if ( !DataBaseUtil.isSame(newVal, val)) {
                row = model.get(i);
                data = (DataViewValueVO)row.getData();
                data.setIsIncluded(newVal);
                auxDataTable.setValueAt(i, 0, newVal);
            }
        }
    }
}