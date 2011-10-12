/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.dataView.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.TestAnalyteDataViewVO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;


public class FilterScreen extends Screen {
    private DataViewVO                data;
    
    private FilterScreen              screen;   

    private TableWidget               analyteTable, resultTable, auxDataTable, 
                                      valueTable;

    private AppButton                 selectAllAnalyteButton, unselectAllAnalyteButton,
                                      selectAllResultButton, unselectAllResultButton,
                                      selectAllAuxButton, unselectAllAuxButton,
                                      selectAllValueButton, unselectAllValueButton,
                                      runReportButton, cancelButton;
    private DataViewReportScreen      reportRunUtil;
    
    public FilterScreen() throws Exception {
        super((ScreenDefInt)GWT.create(FilterDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.dataView.server.DataViewService");        
        
        initialize();
        
        setState(State.DEFAULT);      
    }

    private void initialize() {
        screen = this;
         
        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {                
                analyteTable.load(getAnalyteTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analyteTable.addSelectionHandler(new SelectionHandler<TableRow>() {            
            public void onSelection(SelectionEvent<TableRow> event) {   
                DataChangeEvent.fire(screen, resultTable);
            }
        });
        
        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                TableDataRow arow;
                TestAnalyteDataViewVO data;
                String val;
                
                r = event.getRow();
                c = event.getCol();
                arow = analyteTable.getRow(r);
                val = (String)analyteTable.getObject(r, c);
                data = (TestAnalyteDataViewVO)arow.data;
                switch (c) {
                    case 0: 
                        /*
                         * when the checkbox for an analyte is checked or unchecked
                         * the checkboxes for all the results asscoiated with it
                         * need to be checked or unchecked too. Also the analyte
                         * and the result need to be flagged as "included" or not
                         * appropriately   
                         */
                        updateResultsForAnalyte(r, val); 
                        data.setIsIncluded(val);                                      
                        break;
                }
            }
        });

        selectAllAnalyteButton = (AppButton)def.getWidget("selectAllAnalyteButton");
        addScreenHandler(selectAllAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                TestAnalyteDataViewVO data;
                Object val;
                
                model = analyteTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = (String)analyteTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {
                        row = model.get(i);
                        data = (TestAnalyteDataViewVO)row.data;
                        data.setIsIncluded("Y");
                        analyteTable.setCell(i, 0, "Y");                        
                        updateResultsForAnalyte(i, "Y");
                    }
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAnalyteButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllAnalyteButton = (AppButton)def.getWidget("unselectAllAnalyteButton");
        addScreenHandler(unselectAllAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                TestAnalyteDataViewVO data;
                Object val;
                
                model = analyteTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = analyteTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        row = model.get(i);
                        data = (TestAnalyteDataViewVO)row.data;
                        data.setIsIncluded("N");
                        analyteTable.setCell(i, 0, "N");
                        updateResultsForAnalyte(i, "N");                        
                    }
                }                                   
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllAnalyteButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        
        resultTable = (TableWidget)def.getWidget("resultTable");
        addScreenHandler(resultTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                resultTable.load(getResultTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                resultTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        resultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                TestAnalyteDataViewVO ana;
                
                ana = data.getTestAnalytes().get(analyteTable.getSelectedRow());                 
                if ("N".equals(ana.getIsIncluded())) {
                    event.cancel();                
                    if (event.getCol() == 0)
                        Window.alert(consts.get("selAnaToSelVal"));
                }
            }
        });
        
        resultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;  
                String val;        
                TableDataRow row;
                ResultDataViewVO data;

                r = event.getRow();
                c = event.getCol();                
                row = resultTable.getRow(r);
                val = (String)resultTable.getObject(r,c);
                data = (ResultDataViewVO)row.data;
                /*
                 * If we execute the code for unchecking the checkbox of an analyte
                 * when one of its results' checkbox is unchecked, then we will
                 * have to make sure that the analyte's checkbox is only unchecked
                 * if all its results have their checkboxes unchecked. We have the 
                 * following check because we don't want to execute all of that logic.     
                 */
                switch(c) {
                    case 0:
                        data.setIsIncluded(val);
                        break;
                }
            }
        });

        selectAllResultButton = (AppButton)def.getWidget("selectAllResultButton");
        addScreenHandler(selectAllResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;                   
                TableDataRow row;
                ResultDataViewVO data;
                
                model = resultTable.getData();
                //TODO comment
                if (model == null)
                    return;
                for (int i = 0; i < model.size(); i++) {
                    val = resultTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {                        
                        resultTable.setCell(i, 0, "Y");
                        row = resultTable.getRow(i);
                        data = (ResultDataViewVO)row.data;
                        data.setIsIncluded("Y");
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllResultButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllResultButton = (AppButton)def.getWidget("unselectAllResultButton");
        addScreenHandler(unselectAllResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;                   
                TableDataRow row;
                ResultDataViewVO data;                             
                
                model = resultTable.getData();
                if (model == null)
                    return;
                for (int i = 0; i < model.size(); i++) {
                    val = resultTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        resultTable.setCell(i, 0, "N");
                        row = resultTable.getRow(i);
                        data = (ResultDataViewVO)row.data;
                        data.setIsIncluded("N");
                    }
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllResultButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        auxDataTable = (TableWidget)def.getWidget("auxDataTable");
        addScreenHandler(auxDataTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTable.load(getAuxDataTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        auxDataTable.addSelectionHandler(new SelectionHandler<TableRow>() {            
            public void onSelection(SelectionEvent<TableRow> event) {                
                DataChangeEvent.fire(screen, valueTable);
            }
        });
        
        auxDataTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                TableDataRow row;
                AuxFieldDataViewVO data;
                String val;
                
                r = event.getRow();
                c = event.getCol();
                row = auxDataTable.getRow(r);
                val = (String)auxDataTable.getObject(r, c);
                data = (AuxFieldDataViewVO)row.data;
                
                switch (c) {
                    case 0:
                        updateValuesForAnalyte(r, val); 
                        data.setIsIncluded(val);                                              
                        break;
                }
            }
        });

        selectAllAuxButton = (AppButton)def.getWidget("selectAllAuxButton");
        addScreenHandler(selectAllAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AuxFieldDataViewVO data;
                Object val;
                
                model = auxDataTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = auxDataTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {
                        row = model.get(i);
                        data = (AuxFieldDataViewVO)row.data;
                        data.setIsIncluded("Y");
                        auxDataTable.setCell(i, 0, "Y");
                        updateValuesForAnalyte(i, "Y");
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAuxButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllAuxButton = (AppButton)def.getWidget("unselectAllAuxButton");
        addScreenHandler(unselectAllAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AuxFieldDataViewVO data;
                Object val;
                
                model = auxDataTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = auxDataTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        row = model.get(i);
                        data = (AuxFieldDataViewVO)row.data;
                        data.setIsIncluded("N");
                        auxDataTable.setCell(i, 0, "N");
                        updateValuesForAnalyte(i, "N");                        
                    }
                } 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllAuxButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        valueTable = (TableWidget)def.getWidget("valueTable");
        addScreenHandler(valueTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                valueTable.load(getValueTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                valueTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        valueTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                AuxFieldDataViewVO af;
                
                af = data.getAuxFields().get(auxDataTable.getSelectedRow());                 
                if ("N".equals(af.getIsIncluded())) {
                    event.cancel();
                    if (event.getCol() == 0)
                        Window.alert(consts.get("selAnaToSelVal"));
                }
            }
        });

        valueTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c; 
                String val;        
                TableDataRow row;
                AuxDataDataViewVO data;

                r = event.getRow();
                c = event.getCol();                
                row = valueTable.getRow(r);
                val = (String)valueTable.getObject(r,c);
                data = (AuxDataDataViewVO)row.data;
                switch(c) {
                    case 0:
                        data.setIsIncluded(val);
                        break;
                }
            }
        });

        selectAllValueButton = (AppButton)def.getWidget("selectAllValueButton");
        addScreenHandler(selectAllValueButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;                   
                TableDataRow row;
                AuxDataDataViewVO data;
                
                model = valueTable.getData();
                if (model == null)
                    return;
               //TODO comment
                for (int i = 0; i < model.size(); i++) {
                    val = valueTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {
                        row = valueTable.getRow(i);
                        data = (AuxDataDataViewVO)row.data;
                        data.setIsIncluded("Y");
                        valueTable.setCell(i, 0, "Y");
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllValueButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllValueButton = (AppButton)def.getWidget("unselectAllValueButton");
        addScreenHandler(unselectAllValueButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;                   
                TableDataRow row;
                AuxDataDataViewVO data;
                
                model = valueTable.getData();
                if (model == null)
                    return;
                for (int i = 0; i < model.size(); i++) {
                    val = valueTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        row = valueTable.getRow(i);
                        data = (AuxDataDataViewVO)row.data;
                        data.setIsIncluded("N");
                        valueTable.setCell(i, 0, "N");
                    }
                } 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllValueButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        runReportButton = (AppButton)def.getWidget("runReportButton");
        addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                runReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                runReportButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        cancelButton = (AppButton)def.getWidget("cancelButton");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });        
    }
    
    public void setData(DataViewVO data) {
        this.data = data;
    }
    
    public void reset() {
        DataChangeEvent.fire(this);
    }
    
    protected void runReport() {
        int numTA, numAux;
        ArrayList<TestAnalyteDataViewVO> taList;
        ArrayList<AuxFieldDataViewVO> afList;        

        numTA = 0;
        numAux = 0;
        taList = data.getTestAnalytes();
        if (taList != null) {
            for (TestAnalyteDataViewVO ta : taList) {
                if ("Y".equals(ta.getIsIncluded()))
                    numTA++;
            }
        }
        
        if (numTA == 0) {            
            afList = data.getAuxFields();
            if (afList != null) {
                for (AuxFieldDataViewVO af : afList) {
                    if ("Y".equals(af.getIsIncluded()))
                        numAux++ ;
                }
            }
            if (numAux == 0) {
                window.setError(consts.get("selectOneAnaOrAux"));
                return;
            }
        }               
        
        try {
            if (reportRunUtil == null) 
                reportRunUtil = new DataViewReportScreen("runReport", window);  
            else
            	/*
            	 * Since a FilterScreen screen can be reused by DataView Screen in showFilter() 
            	 * and inserted into a new window, we need to reset the window in the runUtil.  
            	 */
            	reportRunUtil.setWindow(window);
            
            reportRunUtil.runReport(data);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected void cancel() {
        window.close();        
    }
    
    private ArrayList<TableDataRow> getAnalyteTableModel() {
        ArrayList<TableDataRow> model;
        ArrayList<TestAnalyteDataViewVO> analytes;        
        TableDataRow row;              
        
        if (data == null || data.getTestAnalytes() == null)
            return null;
        analytes =  data.getTestAnalytes();
        model = new ArrayList<TableDataRow>();
        for (TestAnalyteDataViewVO ana : analytes) {
            row = new TableDataRow(2);
            row.cells.get(0).setValue("N");
            row.cells.get(1).setValue(ana.getAnalyteName());
            row.data = ana;
            model.add(row);
        }
        
        return model;
    }
    
    private ArrayList<TableDataRow> getResultTableModel() {
        int index;
        ArrayList<TableDataRow> model;
        ArrayList<ResultDataViewVO> results;
        TestAnalyteDataViewVO data;
        TableDataRow row;

        index = analyteTable.getSelectedRow();
        if (index == -1)
            return null;

        model = new ArrayList<TableDataRow>();
        row = analyteTable.getRow(index);
        data = (TestAnalyteDataViewVO) row.data;
        results = data.getResults();
        for (ResultDataViewVO res : results) {
            row = new TableDataRow(2);
            row.cells.get(0).setValue(res.getIsIncluded());
            row.cells.get(1).setValue(res.getValue());
            row.data = res;
            model.add(row);
        }
        return model;
    }
    
    private ArrayList<TableDataRow> getAuxDataTableModel() {
        ArrayList<TableDataRow> model;
        ArrayList<AuxFieldDataViewVO> auxFields;        
        TableDataRow row;              
        
        if (data == null || data.getAuxFields() == null)
            return null;
        
        auxFields =  data.getAuxFields();
        model = new ArrayList<TableDataRow>();
        for (AuxFieldDataViewVO aux : auxFields) {
            row = new TableDataRow(2);
            row.cells.get(0).setValue("N");
            row.cells.get(1).setValue(aux.getAnalyteName());
            row.data = aux;
            model.add(row);
        }
        
        return model;
    }
    
    
    private ArrayList<TableDataRow> getValueTableModel() {
        int index;
        ArrayList<TableDataRow> model;
        ArrayList<AuxDataDataViewVO> values;
        AuxFieldDataViewVO data;
        TableDataRow row;

        index = auxDataTable.getSelectedRow();
        if (index == -1)
            return null;

        model = new ArrayList<TableDataRow>();
        row = auxDataTable.getRow(index);
        data = (AuxFieldDataViewVO) row.data;
        values = data.getValues();
        for (AuxDataDataViewVO val : values) {
            row = new TableDataRow(2);
            row.cells.get(0).setValue(val.getIsIncluded());
            row.cells.get(1).setValue(val.getValue());
            row.data = val;
            model.add(row);
        }
        return model;
    }
    
    private void updateResultsForAnalyte(int index, String val) {
        ResultDataViewVO res;       
        ArrayList<ResultDataViewVO> list;
                          
        list = data.getTestAnalytes().get(index).getResults();
        for (int i = 0; i < list.size(); i++) {
            res = list.get(i);
            /*
             * since this method is called in two scenarios - one, when a user 
             * physically selects a row in the table showing analytes and two,
             * when the button to select or unselect all rows is clicked - we 
             * have to check to see if the table showing results is loaded and 
             * that it's loaded with the results for the analyte at this index 
             * in DataDumpVO's list before checking any checkboxes in this table  
             */
            if (index == analyteTable.getSelectedRow())
                resultTable.setCell(i, 0, val);                
            res.setIsIncluded(val);
        } 
            
    }
    
    private void updateValuesForAnalyte(int index, String val) {
        AuxDataDataViewVO ad;         
        ArrayList<AuxDataDataViewVO> list;
                
        list = data.getAuxFields().get(index).getValues();
        for (int i = 0; i < list.size(); i++) {
            ad = list.get(i);
            /*
             * since this method is called in two scenarios - one, when a user 
             * physically selects a row in the table showing aux fields and two,
             * when the button to select or unselect all rows in that table
             * is clicked - we have to check to see if the table showing values
             * is loaded and that it's loaded with the values for the aux field
             * at this index in DataDumpVO's list before checking any checkboxes
             * in this table    
             */
            if (index == auxDataTable.getSelectedRow())
                valueTable.setCell(i, 0, val);                
            ad.setIsIncluded(val);
        }                
    }
}