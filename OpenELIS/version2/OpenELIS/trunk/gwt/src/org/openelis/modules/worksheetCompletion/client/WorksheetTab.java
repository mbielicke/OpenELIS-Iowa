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
package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
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
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetTab extends Screen {

    private boolean              loaded;
    private Integer              formatBatch, formatTotal;
    private AppButton            editWorksheetButton, loadFromEditButton, loadFilePopupButton;
    private ArrayList<SectionDO> sections;
    private TableWidget          table;
    private WorksheetManager     manager;

    protected WorksheetFileUploadScreen wFileUploadScreen;

    public WorksheetTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);

        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");

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
/*
        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if (table.getSelectedRow() != -1)
                    editWorksheetButton.enable(true);
            }
        });
        
        table.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                if (table.getSelectedRow() == -1)
                    editWorksheetButton.enable(false);
            }
        });
*/        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // table cannot be edited directly
                event.cancel();
            }
        });

        editWorksheetButton = (AppButton)def.getWidget("editWorksheetButton");
        addScreenHandler(editWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editWorksheet();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editWorksheetButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFromEditButton = (AppButton)def.getWidget("loadFromEditButton");
        addScreenHandler(loadFromEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadFromEdit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFromEditButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFilePopupButton = (AppButton)def.getWidget("loadFilePopupButton");
        addScreenHandler(loadFilePopupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetFileUpload();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFilePopupButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
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
        ResultViewDO             rVDO;
        QcAnalyteViewDO          qcaVDO;
        QcManager                qcManager;
        SampleDataBundle         bundle;
        SampleDomainInt          sDomain;
        SampleManager            sManager;
        SectionDO                sectionDO;
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
                        sDomain = sManager.getDomainManager();
                        aManager = sManager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                        aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                        arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());

                        if (sections == null)
                            sections = new ArrayList<SectionDO>();
                        sectionDO = SectionCache.getSectionFromId(aVDO.getSectionId());
                        if (!sections.contains(sectionDO))
                            sections.add(sectionDO);
                        
                        if (sDomain != null)
                            row.cells.get(2).value = sDomain.getDomainDescription();
                        else
                            row.cells.get(2).value = "";
                        
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = aVDO.getTestName();
                        row.cells.get(5).value = aVDO.getMethodName();
                        row.cells.get(6).value = aVDO.getStatusId();

                        wrManager = waManager.getWorksheetResultAt(j);
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
                            rVDO = arManager.getResultForWorksheet(waDO.getAnalysisId(), wrVDO.getAnalyteId());
                            row.cells.get(7).value = wrVDO.getAnalyteName();
                            row.cells.get(8).value = wrVDO.getValue();
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = rVDO.getValue();
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
                            qcaVDO = qcManager.getAnalytes().getAnalyteAt(k);
                            row.cells.get(7).value = wqrVDO.getAnalyteName();
                            row.cells.get(8).value = wqrVDO.getValue();
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = "";
                            row.cells.get(11).value = qcaVDO.getValue();
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
    
    /*
     * Call the update method on SampleManagers attached to analysis data rows
     */
    public void save() throws Exception {
        int                     i;
        ArrayList<TableDataRow> model;
        SampleDataBundle        bundle;
        SampleManager           manager;
        
        model = table.getData();
        for (i = 0; i < model.size(); i++) {
            if (model.get(i).data instanceof SampleDataBundle) {
                bundle = (SampleDataBundle) model.get(i).data;
                manager = bundle.getSampleManager();
                try {
                    manager.update();
                } catch (ValidationErrorsList e) {
                    throw e;
                } catch (Exception anyE) {
                    throw new Exception("WorksheetTable Row "+(i+1)+": "+anyE.getMessage());
                }
            }
        }
    }
    
    protected void editWorksheet() {
        window.setBusy("Saving worksheet for editing");
        try {
            service.call("saveForEdit", manager);
            window.setDone("Worksheet saved for editing");
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    protected void loadFromEdit() {
        window.setBusy("Loading worksheet from edited file");
        try {
            manager = service.call("loadFromEdit", manager);
            // TODO - Add code to load the worksheet from the edited excel file
            DataChangeEvent.fire(this);
            window.setDone("Worksheet loaded");
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    protected void openWorksheetFileUpload() {
        ScreenWindow modal;
        
        try {
            if (wFileUploadScreen == null) {
                final WorksheetTab wt = this;
                wFileUploadScreen = new WorksheetFileUploadScreen();
                wFileUploadScreen.addActionHandler(new ActionHandler<WorksheetFileUploadScreen.Action>() {
                    public void onAction(ActionEvent<WorksheetFileUploadScreen.Action> event) {
                        if (event.getAction() == WorksheetFileUploadScreen.Action.OK) {
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetFileUpload"));
            modal.setContent(wFileUploadScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    public ArrayList<SectionDO> getSections() {
        return sections;
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
}
