package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class WorksheetTab extends Screen {

    private boolean          loaded;
    private Integer          formatBatch, formatTotal;
    private AppButton        editMultipleButton;
    private TableWidget      table;
    private WorksheetManager manager;

    public WorksheetTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if (table.getSelectedRow() != -1)
                    editMultipleButton.enable(true);
            }
        });
        
        table.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                if (table.getSelectedRow() == -1)
                    editMultipleButton.enable(false);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
/*
                int r, c;
                Object val;
                OrganizationContactDO data;

                if (state == State.QUERY)
                    return;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r,c);

                try {
                    data = manager.getContacts().getContactAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setContactTypeId((Integer)val);
                        break;
                    case 1:
                        data.setName((String)val);
                        break;
                    case 2:
                        data.getAddressDO().setMultipleUnit((String)val);
                        break;
                    case 3:
                        data.getAddressDO().setStreetAddress((String)val);
                        break;
                    case 4:
                        data.getAddressDO().setCity((String)val);
                        break;
                    case 5:
                        data.getAddressDO().setState((String)val);
                        break;
                    case 6:
                        data.getAddressDO().setZipCode((String)val);
                        break;
                    case 7:
                        data.getAddressDO().setCountry((String)val);
                        break;
                    case 8:
                        data.getAddressDO().setWorkPhone((String)val);
                        break;
                    case 9:
                        data.getAddressDO().setHomePhone((String)val);
                        break;
                    case 10:
                        data.getAddressDO().setCellPhone((String)val);
                        break;
                    case 11:
                        data.getAddressDO().setFaxPhone((String)val);
                        break;
                    case 12:
                        data.getAddressDO().setEmail((String)val);
                        break;
                }
*/
            }
        });

        editMultipleButton = (AppButton)def.getWidget("editMultipleButton");
        addScreenHandler(editMultipleButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editMultiple();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editMultipleButton.enable(false);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        try {
            formatBatch = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatTotal = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int                      i, j, k;
        ArrayList<TableDataRow>  model;
        TableDataRow             row;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisViewDO           aVDO;
        QcManager                qcManager;
        SampleDataBundle         bundle;
        SampleManager            sManager;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetQcResultViewDO  wqrVDO;
        WorksheetResultManager   wrManager;
        WorksheetResultViewDO    wrVDO;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = getPositionNumber(wiDO.getPosition());
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);

                    if (j != 0)
                        row.cells.get(0).value = null;
                    
                    row.cells.get(1).value = waDO.getAccessionNumber();

                    if (waDO.getAnalysisId() != null) {
                        bundle = waManager.getBundleAt(j);
                        sManager = bundle.getSampleManager();
                        aManager = sManager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                        arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());
                        aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());

                        row.cells.get(2).value = "";
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = aVDO.getTestName();
                        row.cells.get(5).value = aVDO.getMethodName();
                        row.cells.get(6).value = aVDO.getStatusId();

                        wrManager = waManager.getWorksheetResultAt(j);
                        if (wrManager.count() == 0)
                            initializeWorksheetResults(aVDO, arManager, wrManager);
                                                       
                        for (k = 0; k < wrManager.count(); k++) {
                            if (k != 0) {
                                row.cells.get(0).value = null;
                                row.cells.get(1).value = null;
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = 0;
                            }
                            wrVDO = wrManager.getWorksheetResultAt(k);
                            row.cells.get(7).value = wrVDO.getAnalyteName();
                            row.cells.get(8).value = "";
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = "";
                            row.cells.get(11).value = "";
                            row.cells.get(12).value = "";
                            row.data = bundle;
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            row.data = bundle;
                            model.add((TableDataRow)row.clone());
                        }
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.cells.get(2).value = qcManager.getQc().getName();
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = "";
                        row.cells.get(5).value = "";
                        row.cells.get(6).value = 0;

                        wqrManager = waManager.getWorksheetQcResultAt(j);
                        if (wqrManager.count() == 0)
                            initializeWorksheetQcResults(qcManager, wqrManager);
                                                       
                        for (k = 0; k < wqrManager.count(); k++) {
                            if (k != 0) {
                                row.cells.get(0).value = null;
                                row.cells.get(1).value = null;
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = 0;
                            }
                            wqrVDO = wqrManager.getWorksheetQcResultAt(k);
                            row.cells.get(7).value = wqrVDO.getAnalyteName();
                            row.cells.get(8).value = "";
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = "";
                            row.cells.get(11).value = wqrVDO.getValue();
                            row.cells.get(12).value = "";
                            row.data = qcManager;
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            row.data = qcManager;
                            model.add((TableDataRow)row.clone());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    public void setManager(WorksheetManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if (!loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
    
    protected void editMultiple() {
        // TODO - Add edit multiple code
        Window.alert("Edit Multiple Popup");
    }

    private Object getPositionNumber(int position) {
        int    major, minor;
        Object positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(manager.getWorksheet().getFormatId())) {
            major = getPositionMajorNumber(position);
            minor = getPositionMinorNumber(position);
            positionNumber = major+"-"+minor;
        } else if (formatTotal.equals(manager.getWorksheet().getFormatId())) {
            positionNumber = position;
        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
    private int getPositionMajorNumber(int position) {
        return (int) (position / (double)manager.getWorksheet().getBatchCapacity() + .99);
    }

    /**
      * Parses the position number and returns the minor number
      * for batch numbering.
      */
    private int getPositionMinorNumber(int position) {
        return position - (getPositionMajorNumber(position) - 1) * manager.getWorksheet().getBatchCapacity();
    }

    /**
     * Loads the WorksheetResultManager from the provided AnalysisManager.  This method is
     * called when we have never loaded this worksheet since it was created. 
     */
    private void initializeWorksheetResults(AnalysisViewDO aVDO, AnalysisResultManager arManager,
                                            WorksheetResultManager wrManager) {
        int                                         i, j;
        ArrayList<ResultViewDO>                     resultRow;
        ArrayList<ArrayList<ResultViewDO>>          results;
        HashMap<Integer,TestWorksheetAnalyteViewDO> twAnalytes;
        ResultViewDO                                result;
        TestWorksheetAnalyteViewDO                  twaVDO;
        TestWorksheetManager                        twManager;
        WorksheetResultViewDO                       wrVDO;

        twAnalytes = new HashMap<Integer,TestWorksheetAnalyteViewDO>();
        try {
            twManager = TestWorksheetManager.fetchByTestId(aVDO.getTestId());
            for (i = 0; i < twManager.analyteCount(); i++) {
                twaVDO = twManager.getAnalyteAt(i);
                twAnalytes.put(twaVDO.getTestAnalyteId(), twaVDO);
            }
        } catch (Exception anyE) {
            // TODO - Code real exception handling
            anyE.printStackTrace();
            Window.alert(anyE.getMessage());
            return;
        }

        results = arManager.getResults();
        for (i = 0; i < results.size(); i++) {
            resultRow = results.get(i);
            result = resultRow.get(0);
//            if (twAnalytes.containsKey(result.getTestAnalyteId())) {
                for (j = 0; j < resultRow.size(); j++) {
                    result = resultRow.get(j);
                    wrVDO = new WorksheetResultViewDO();
                    wrVDO.setTestAnalyteId(result.getTestAnalyteId());
                    wrVDO.setSortOrder(i+1);
                    wrVDO.setAnalyteId(result.getAnalyteId());
                    wrVDO.setTypeId(result.getTypeId());
                    wrVDO.setAnalyteName(result.getAnalyte());
                    wrManager.addWorksheetResult(wrVDO);
                }
//            }           
        }
    }

    /**
     * Loads the WorksheetQcResultManager from the provided QcManager.  This method is
     * called when we have never loaded this worksheet since it was created. 
     */
    private void initializeWorksheetQcResults(QcManager qcManager, WorksheetQcResultManager wqrManager) {
        int                     i;
        QcAnalyteViewDO         qcaVDO;
        QcAnalyteManager        qcaManager;
        WorksheetQcResultViewDO wqrVDO;

        try {
            qcaManager = qcManager.getAnalytes();
            for (i = 0; i < qcaManager.count(); i++) {
                qcaVDO = qcaManager.getAnalyteAt(i);
                wqrVDO = new WorksheetQcResultViewDO();
                wqrVDO.setSortOrder(i+1);
                wqrVDO.setQcAnalyteId(qcaVDO.getId());
                wqrVDO.setAnalyteName(qcaVDO.getAnalyteName());
                wqrVDO.setTypeId(qcaVDO.getTypeId());
                wqrVDO.setValue(qcaVDO.getValue());
                wqrManager.addWorksheetQcResult(wqrVDO);
            }
        } catch (Exception anyE) {
            // TODO - Code real exception handling
            anyE.printStackTrace();
            Window.alert(anyE.getMessage());
        }
    }
}
