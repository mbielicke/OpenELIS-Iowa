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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
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
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.WorksheetManager;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationScreen extends Screen {

    private SecurityModule   security;
    private WorksheetManager manager;
    private WorksheetMetaMap worksheetMetaMap;

    protected TextBox                       worksheetId;
    protected TableWidget                   worksheetItemTable;
    protected WorksheetCreationLookupScreen wcLookupScreen;
    
    public WorksheetCreationScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super((ScreenDefInt)GWT.create(WorksheetCreationDef.class));
        
        security = OpenELIS.security.getModule("worksheet");

        manager = WorksheetManager.getInstance();
        
        worksheetMetaMap = new WorksheetMetaMap();

        // Setup service used by screen
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
//        setState(State.ADD);
        
        openLookupWindow();
    }
    
    private void initialize() {
        final TextBox worksheetNumber = (TextBox)def.getWidget(worksheetMetaMap.getId());
        addScreenHandler(worksheetNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetNumber.setValue(manager.getWorksheet().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetNumber.enable(false);
            }
        });

        final AppButton saveButton = (AppButton)def.getWidget("saveButton");
        addScreenHandler(saveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                save();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                saveButton.enable(true);
            }
        });

        final AppButton exitButton = (AppButton)def.getWidget("exitButton");
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

        final AppButton insertQCButton = (AppButton)def.getWidget("insertQCButton");
        addScreenHandler(insertQCButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                insertQCButton.enable(true);
            }
        });

        final AppButton removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                worksheetItemTable.selectAll();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(true);
            }
        });   
    }

    protected void openLookupWindow() {
        ScreenWindow lookupWindow;
        
        if (wcLookupScreen == null) {
            try {
                wcLookupScreen = new WorksheetCreationLookupScreen();
/*
                wcLookupScreen.addActionHandler(new ActionHandler<WorksheetCreationLookupScreen.Action>() {
                    public void onAction(ActionEvent<WorksheetCreationLookupScreen.Action> event) {
                        ArrayList<TableDataRow> model;
                        TableDataRow            row;

                        if (event.getAction() == WorksheetCreationLookupScreen.Action.ADD) {
                            model = (ArrayList<TableDataRow>)event.getData();
                            if(model != null) {
                                for(int i = 0; i < model.size(); i++) {
                                    row = model.get(i);                                                   
                                    testResultManager.addResultAt(selTab+1,resultTable.numRows(),getNextTempId());
                                    resDO = testResultManager.getResultAt(selTab+1,resultTable.numRows());
                                    dictId = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
                                    resDO.setValue((String)row.cells.get(0).getValue());
                                    resDO.setTypeId(dictId);                                           
                                }
                                DataChangeEvent.fire(source, resultTable);
                            }
                        }
                    }
                });
*/
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
    }

    protected void exit() {
    }
}
