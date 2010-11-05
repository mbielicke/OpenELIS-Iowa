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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetTab extends Screen {

    private boolean              loaded;
    private Integer              formatBatch, formatTotal;
    private Button               editWorksheetButton, loadFromEditButton, loadFilePopupButton;
    private ArrayList<SectionDO> sections;
    private Table                table;
    private WorksheetManager     manager;

    protected WorksheetFileUploadScreen wFileUploadScreen;

    public WorksheetTab(ScreenDefInt def, Window window) {
        setDefinition(def);

        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");

        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (Table)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.setEnabled(true);
            }
        });
/*
        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if (table.getSelectedRow() != -1)
                    editWorksheetButton.setEnabled(true);
            }
        });
        
        table.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                if (table.getSelectedRow() == -1)
                    editWorksheetButton.setEnabled(false);
            }
        });
*/        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // table cannot be edited directly
                event.cancel();
            }
        });

        editWorksheetButton = (Button)def.getWidget("editWorksheetButton");
        addScreenHandler(editWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editWorksheet();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editWorksheetButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFromEditButton = (Button)def.getWidget("loadFromEditButton");
        addScreenHandler(loadFromEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadFromEdit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFromEditButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFilePopupButton = (Button)def.getWidget("loadFilePopupButton");
        addScreenHandler(loadFilePopupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetFileUpload();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFilePopupButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        try {
            formatBatch = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatTotal = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)table.getColumnWidget(6)).setModel(model);
    }
    
    private ArrayList<Row> getTableModel() {
        int                      i, j, k;
        ArrayList<Row>           model;
        Row                      row;
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
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new Row(13);
                row.setCell(0,getPositionNumber(wiDO.getPosition()));
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);

                    if (j != 0)
                        row.setCell(0,null);
                    
                    row.setCell(1,waDO.getAccessionNumber());

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
                            row.setCell(2,sDomain.getDomainDescription());
                        else
                            row.setCell(2,"");
                        
                        row.setCell(3,"");
                        row.setCell(4,aVDO.getTestName());
                        row.setCell(5,aVDO.getMethodName());
                        row.setCell(6,aVDO.getStatusId());

                        wrManager = waManager.getWorksheetResultAt(j);
                        for (k = 0; k < wrManager.count(); k++) {
                            if (k != 0) {
                                row.setCell(0,null);
                                row.setCell(1,null);
                                row.setCell(2,"");
                                row.setCell(3,"");
                                row.setCell(4,"");
                                row.setCell(5,"");
                                row.setCell(6,0);
                            }
                            wrVDO = wrManager.getWorksheetResultAt(k);
                            rVDO = arManager.getResultAt(wrVDO.getResultRow(), 0);
                            row.setCell(7,wrVDO.getAnalyteName());
                            row.setCell(8,wrVDO.getValueAt(0));
                            row.setCell(9,"");
                            row.setCell(10,rVDO.getValue());
                            row.setCell(11,"");
                            row.setCell(12,"");
                            row.setData(bundle);
                            model.add((Row)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.setCell(7,"NO ANALYTES FOUND");
                            row.setData(bundle);
                            model.add((Row)row.clone());
                        }
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.setCell(2,qcManager.getQc().getName());
                        row.setCell(3,"");
                        row.setCell(4,"");
                        row.setCell(5,"");
                        row.setCell(6,0);

                        wqrManager = waManager.getWorksheetQcResultAt(j);
                        for (k = 0; k < wqrManager.count(); k++) {
                            if (k != 0) {
                                row.setCell(0,null);
                                row.setCell(1, null);
                                row.setCell(2,"");
                                row.setCell(3,"");
                                row.setCell(4,"");
                                row.setCell(5,"");
                                row.setCell(6,0);
                            }
                            wqrVDO = wqrManager.getWorksheetQcResultAt(k);
                            qcaVDO = qcManager.getAnalytes().getAnalyteAt(k);
                            row.setCell(7,wqrVDO.getAnalyteName());
                            row.setCell(8,wqrVDO.getValue());
                            row.setCell(9,"");
                            row.setCell(10,"");
                            row.setCell(11,qcaVDO.getValue());
                            row.setCell(12,"");
                            row.setData(qcManager);
                            model.add((Row)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.setCell(7,"NO ANALYTES FOUND");
                            row.setData(qcManager);
                            model.add((Row)row.clone());
                        }
                    }
                }
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
        ArrayList<? extends Row> model;
        SampleDataBundle        bundle;
        SampleManager           manager;
        
        model = table.getModel();
        for (i = 0; i < model.size(); i++) {
            if (model.get(i).getData() instanceof SampleDataBundle) {
                bundle = (SampleDataBundle) model.get(i).getData();
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
            com.google.gwt.user.client.Window.alert(anyE.getMessage());
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
            com.google.gwt.user.client.Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    protected void openWorksheetFileUpload() {
        ModalWindow modal;
        
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
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetFileUpload"));
            modal.setContent(wFileUploadScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
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
