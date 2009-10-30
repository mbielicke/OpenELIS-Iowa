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
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableSorter;
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
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationScreen extends Screen implements HasActionHandlers<WorksheetCreationScreen.Action> {

    private ScreenService                   testService;
    private SecurityModule                  security;
    private WorksheetCreationScreen         source;
    private WorksheetManager                manager;
    private WorksheetMetaMap                meta;

    private AppButton                       saveButton, exitButton,
                                            insertQCButton, removeRowButton;

    protected ArrayList<Integer>            testIds;
    protected ArrayList<TableDataRow>       analysisItems;
    protected TableWidget                   worksheetItemTable;
    protected TextBox<Integer>              worksheetId;
    protected WorksheetCreationLookupScreen wcLookupScreen;
    
    public enum Action {
        ITEMS_CHANGED
    };
    
    public WorksheetCreationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));

        service     = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        testService = new ScreenService("OpenELISServlet?service=org.openelis.modules.test.server.TestService");

        security = OpenELIS.security.getModule("worksheet");
        if (security == null)
            throw new SecurityException("screenPermException", "Worksheet Creation Screen");

        meta   = new WorksheetMetaMap();

        // Setup link between Screen and widget Handlers
        initialize();

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
        manager       = WorksheetManager.getInstance();
        source        = this;
        testIds       = new ArrayList<Integer>();

        setState(State.DEFAULT);
        openLookupWindow();

        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        worksheetId = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
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
                exit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                exitButton.enable(true);
            }
        });

        worksheetItemTable = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(worksheetItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetItemTable.enable(true);
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
            }
        });

        worksheetItemTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
                TableSorter sorter;
                
                sorter = new TableSorter();
                sorter.sort(analysisItems, event.getIndex(), event.getDirection());
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
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(false);
            }
        });   
    }

    protected void openLookupWindow() {
        ScreenWindow lookupWindow;
        
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
                                    newRow = new TableDataRow(7);
                                    data = (WorksheetCreationVO)row.data;
                                    
                                    if (!testIds.contains(data.getTestId()))
                                        testIds.add(data.getTestId());
                                    
                                    newRow.key = row.key;                                   // analysis id
                                    newRow.cells.get(0).value = analysisItems.size() + 1;   // position
                                    newRow.cells.get(2).value = row.cells.get(0).value;     // accession #
                                    newRow.cells.get(3).value = row.cells.get(1).value;     // description
                                    newRow.cells.get(4).value = row.cells.get(5).value;     // status
                                    newRow.cells.get(5).value = row.cells.get(6).value;     // collection date
                                    newRow.cells.get(6).value = row.cells.get(7).value;     // received data and time
                                    analysisItems.add(newRow);
                                }
                                
                                saveButton.enable(true);
                                insertQCButton.enable(true);
                                
                                if (testIds.size() > 1) {
                                    Window.alert(consts.get("multipleTestsOnWorksheet"));
                                    // TODO -- Clear QC Template
                                } else {
                                    loadQCTemplate(testIds.get(0));
                                }
                                mergeAnalysesAndQCs();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        lookupWindow = new ScreenWindow("Worksheet Creation Lookup Screen",
                                        "worksheetCreationLookupScreen","",false,true);
        lookupWindow.setName(consts.get("worksheetCreationLookup"));
        lookupWindow.setContent(wcLookupScreen);
    }
    
    protected void save() {
        int          i;
        TableDataRow row;
        WorksheetItemManager wiManager;
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert("You may not save an empty worksheet");
            return;
        }
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
        }
        
    }

    protected void exit() {
    }

    private void loadQCTemplate(Integer testId) {
/*
        int                  i;
        TestWorksheetViewDO  twDO;
        TestWorksheetItemDO  twiDO;
        TestWorksheetManager twM;
        
        // TODO -- Retrieve worksheet template information for specified test
        try {
            twM = TestWorksheetManager.findByTestId(testIds.get(0));
            twDO = twM.getWorksheet();
            for (i = 0; i < twM.itemCount(); i++) {
                twiDO = twM.getItemAt(i);
            }
        } catch (Exception anyE) {
            
        }
        
        
        buildQCWorksheet();
*/
    }
    
    private void buildQCWorksheet() {
        // TODO -- Implement loading of QCs from the specified test definition
    }
    
    private void sort() {
        // TODO -- Implement sorting of the added analyses
        mergeAnalysesAndQCs();
    }
    
    private void mergeAnalysesAndQCs() {
        // TODO -- Implement merging of sorted analyses with the QC template
        worksheetItemTable.load(analysisItems);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int i;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        WorksheetItemDO data;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                data = (WorksheetItemDO)manager.getItems().getItemAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = data.getPosition();
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return model;
    }

    public HandlerRegistration addActionHandler(ActionHandler<WorksheetCreationScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }        
}
