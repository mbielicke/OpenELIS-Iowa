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
package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationScreen extends Screen implements HasActionHandlers<WorksheetCreationScreen.Action> {

    private boolean                          isSaved;
    private Integer                          formatBatch, formatTotal, statusWorking;
    private SecurityModule                   security;
    private WorksheetManager                 manager;
    private WorksheetMetaMap                 meta;

    private AppButton                        saveButton, exitButton,
                                             insertQCButton, removeRowButton;

    protected ArrayList<Integer>             testIds;
    protected ArrayList<TableDataRow>        analysisItems;
    protected ArrayList<TestWorksheetItemDO> testWorksheetItems;
    protected TableWidget                    worksheetItemTable;
    protected TextBox<Integer>               worksheetId;
    protected TestWorksheetDO                testWorksheetDO;
    protected WorksheetCreationLookupScreen  wcLookupScreen;
    
    public enum Action {
        ITEMS_CHANGED
    };
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");

        security = OpenELIS.security.getModule("worksheet");
        if (security == null)
            throw new SecurityException("screenPermException", "Worksheet Creation Screen");

        meta   = new WorksheetMetaMap();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        analysisItems = new ArrayList<TableDataRow>();
        isSaved       = true;
        manager       = WorksheetManager.getInstance();
        testIds       = new ArrayList<Integer>();

        initialize();

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent event) {
                if (exit())
                    wcLookupScreen.getWindow().close();
                else
                    event.cancel();
            }
        });
        
        setState(State.DEFAULT);
        initializeDropdowns();
        openLookupWindow();

        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        worksheetId = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(false);
            }
        });

        saveButton = (AppButton)def.getWidget("saveButton");
        addScreenHandler(saveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                save();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                saveButton.enable(false);
            }
        });

        exitButton = (AppButton)def.getWidget("exitButton");
        addScreenHandler(exitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                window.close();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                exitButton.enable(true);
            }
        });

        worksheetItemTable = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(worksheetItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetItemTable.enable(false);
            }
        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetItemTable.getSelectedRow() != -1)
                    removeRowButton.enable(true);
                else
                    removeRowButton.enable(false);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });

        worksheetItemTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
            }
        });

        worksheetItemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                // TODO -- Update to include QC Removal
                analysisItems.remove(event.getIndex());
                mergeAnalysesAndQCs();
            }
        });

        worksheetItemTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
				ColumnComparator comparator;
//                TableSorter sorter;
                
                comparator = new ColumnComparator(event.getIndex(), event.getDirection());
                Collections.sort(analysisItems, comparator);
                
//                sorter = new TableSorter();
//                sorter.sort(analysisItems, event.getIndex(), event.getDirection());
                
                mergeAnalysesAndQCs();
            }
        });
        
        insertQCButton = (AppButton)def.getWidget("insertQCButton");
        addScreenHandler(insertQCButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCButton.enable(false);
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = worksheetItemTable.getSelectedRow();
                if (r > -1 && worksheetItemTable.numRows() > 0)
                    worksheetItemTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(false);
            }
        });   
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns(){
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)worksheetItemTable.getColumns().get(6).getColumnWidget()).setModel(model);

        try {
//            formatBatch   = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatBatch   = DictionaryCache.getIdFromSystemName("batch");
//            formatTotal   = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
            formatTotal   = DictionaryCache.getIdFromSystemName("total");
            statusWorking = DictionaryCache.getIdFromSystemName("worksheet_working");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    protected void openLookupWindow() {
        if (wcLookupScreen == null) {
            try {
                wcLookupScreen = new WorksheetCreationLookupScreen();
                wcLookupScreen.addActionHandler(new ActionHandler<WorksheetCreationLookupScreen.Action>() {
                    public void onAction(ActionEvent<WorksheetCreationLookupScreen.Action> event) {
                        ArrayList<TableDataRow> model;
                        TableDataRow            row, newRow;
                        WorksheetCreationVO     data;

                        if (event.getAction() == WorksheetCreationLookupScreen.Action.ADD) {
                            model = (ArrayList<TableDataRow>)event.getData();
                            if (model != null) {
                                for (int i = 0; i < model.size(); i++) {
                                    row  = model.get(i);
                                    newRow = new TableDataRow(11);
                                    data = (WorksheetCreationVO)row.data;
                                    
                                    if (!testIds.contains(data.getTestId()))
                                        testIds.add(data.getTestId());
                                    
                                    newRow.key = row.key;                                   // analysis id
                                    newRow.cells.get(2).value = row.cells.get(0).value;     // accession #
                                    newRow.cells.get(3).value = row.cells.get(1).value;     // description
                                    newRow.cells.get(4).value = row.cells.get(2).value;     // test name
                                    newRow.cells.get(5).value = row.cells.get(3).value;     // method name
                                    newRow.cells.get(6).value = row.cells.get(5).value;     // status
                                    newRow.cells.get(7).value = row.cells.get(6).value;     // collection date
                                    newRow.cells.get(8).value = row.cells.get(7).value;     // received date and time
                                    newRow.cells.get(9).value = row.cells.get(8).value;     // due days
                                    newRow.cells.get(10).value = row.cells.get(9).value;    // expire date and time
                                    analysisItems.add(newRow);
                                }
                                
                                if (testIds.size() > 1) {
                                    Window.alert(consts.get("multipleTestsOnWorksheet"));
                                    testWorksheetDO = new TestWorksheetDO();
                                    testWorksheetDO.setFormatId(formatTotal);
                                    testWorksheetDO.setBatchCapacity(500);
                                    testWorksheetDO.setTotalCapacity(500);
                                    testWorksheetItems.clear();
                                } else {
                                    loadQCTemplate(testIds.get(0));
                                }
                                mergeAnalysesAndQCs();

                                isSaved = false;
                                saveButton.enable(true);
//                                insertQCButton.enable(true);
                                worksheetItemTable.enable(true);
                            }
                        }
                    }
                });
                
                OpenELIS.browser.addScreen(wcLookupScreen);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }
    }
    
    protected void save() {
        boolean      choice;
        int          i;
        TableDataRow row;
        WorksheetDO              wDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert("You may not save an empty worksheet");
            return;
        }
        
        choice = Window.confirm("Worksheets cannot be changed once they have been saved.\n"+
                                "Are you sure you would like to save this worksheet?");
        if (!choice)
            return;

        window.setBusy(consts.get("saving"));

        wDO = manager.getWorksheet();
        wDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wDO.setSystemUserId(OpenELIS.security.getSystemUserId());
        wDO.setStatusId(statusWorking);
        wDO.setFormatId(testWorksheetDO.getFormatId());
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
            wiDO = new WorksheetItemDO();
            wiDO.setPosition((Integer)row.cells.get(0).value);
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setReferenceId((Integer)row.key);
            waDO.setReferenceTableId(ReferenceTable.ANALYSIS);
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addAnalysis(waDO);
            } catch (Exception anyE) {
                // TODO -- Need to code real exception handling
                anyE.printStackTrace();
            }
        }
        
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("savingComplete"));
            
            isSaved = true;
            saveButton.enable(false);
            insertQCButton.enable(false);
            removeRowButton.enable(false);
            worksheetItemTable.enable(false);
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }
    }

    protected boolean exit() {
        boolean choice;
        
        choice = true;
        if (!isSaved)
            choice = Window.confirm("This worksheet has not been saved.\n"+
                                    "Are you sure you would like to exit without saving?");
        return choice;
    }

    private void loadQCTemplate(Integer testId) {
        int                  i;
        TestWorksheetItemDO  twiDO;
        TestWorksheetManager twM;
        
        try {
            twM = TestWorksheetManager.fetchByTestId(testId);
            testWorksheetDO = twM.getWorksheet();
            for (i = 0; i < twM.itemCount(); i++) {
                twiDO = twM.getItemAt(i);
            }
        } catch (Exception anyE) {
            // TODO -- Need to code real exception handling
            anyE.printStackTrace();
        }
        
        buildQCWorksheet();
    }
    
    private void buildQCWorksheet() {
        // TODO -- Implement loading of QCs from the specified test definition
    }
    
    private void sort() {
        // TODO -- Implement sorting of the added analyses
        mergeAnalysesAndQCs();
    }
    
    private void mergeAnalysesAndQCs() {
        int          i, major, minor;
        TableDataRow row;
        
        // TODO -- Implement merging of sorted analyses with the QC template
        for (i = 0; i < analysisItems.size(); i++) {
            row = analysisItems.get(i);
            if (formatBatch.equals(testWorksheetDO.getFormatId())) {
                major = getWellMajorNumber(i+1);
                minor = getWellMinorNumber(i+1);
                row.cells.get(0).value = major+"-"+minor;
            } else if (formatTotal.equals(testWorksheetDO.getFormatId())) {
                row.cells.get(0).value = i + 1;
            }
        }
        worksheetItemTable.load(analysisItems);
    }

    public HandlerRegistration addActionHandler(ActionHandler<WorksheetCreationScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }        

    /**
     * Parses the well number and returns the major number
     * for batch numbering.
     */
   private int getWellMajorNumber(int wellNumber) {
       return (int) (wellNumber / (double)testWorksheetDO.getBatchCapacity() + .99);
   }

   /**
     * Parses the well number and returns the minor number
     * for batch numbering.
     */
   private int getWellMinorNumber(int wellNumber) {
       return wellNumber - (getWellMajorNumber(wellNumber) - 1) * testWorksheetDO.getBatchCapacity();
   }
}
