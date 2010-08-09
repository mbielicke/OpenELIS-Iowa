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
package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.domain.InventoryAdjustmentViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryXAdjustViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.DictionaryManager;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.InventoryAdjustmentMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryAdjustmentScreen extends Screen {
    
    private InventoryAdjustmentManager manager;
    private SecurityModule             security;    
    
    
    private ButtonGroup                atoz;
    private ScreenNavigator            nav;
    
    private Dropdown<Integer>          inventoryItemStoreId;
    private CalendarLookUp             adjustmentDate;
    private TextBox                    id, description, systemUserId;
    private AutoComplete<Integer>      inventoryLocationId, inventoryLocationInventoryItemName;
    
    private AppButton                  queryButton, previousButton, nextButton, addButton,
                                       updateButton, commitButton, abortButton, addRowButton,
                                       removeRowButton;
    protected MenuItem                 inventoryAdjustmentHistory, inventoryAdjustmentLocationHistory;
    private TableWidget                adjustmentTable;
    private ScreenService              inventoryLocationService;
    
    public InventoryAdjustmentScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryAdjustmentDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");        

        security = OpenELIS.security.getModule("inventoryadjustment");
        if (security == null)
            throw new SecurityException("screenPermException", "Inventory Adjustment Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    private void postConstructor() {
        manager = InventoryAdjustmentManager.getInstance();
        try {
            DictionaryCache.preloadByCategorySystemNames("inventory_store");
        } catch (Exception e) {
            Window.alert("Inventory Adjustment Screen: missing dictionary entry; " + e.getMessage());
            window.close();
        }
        
        initialize(); 
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this); 
    }   

    private void initialize() {
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && security.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }           

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        inventoryAdjustmentHistory = (MenuItem)def.getWidget("inventoryAdjustmentHistory");
        addScreenHandler(inventoryAdjustmentHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                inventoryAdjustmentHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryAdjustmentHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        inventoryAdjustmentLocationHistory = (MenuItem)def.getWidget("inventoryAdjustmentLocationHistory");
        addScreenHandler(inventoryAdjustmentLocationHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                inventoryAdjustmentLocationHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryAdjustmentLocationHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        id = (TextBox)def.getWidget(InventoryAdjustmentMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getInventoryAdjustment().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                // this field is not edited except for in query mode 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(InventoryAdjustmentMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getInventoryAdjustment().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryAdjustment().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        adjustmentDate = (CalendarLookUp)def.getWidget(InventoryAdjustmentMeta.getAdjustmentDate());
        addScreenHandler(adjustmentDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                adjustmentDate.setValue(manager.getInventoryAdjustment().getAdjustmentDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInventoryAdjustment().setAdjustmentDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                adjustmentDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                adjustmentDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        systemUserId = (TextBox)def.getWidget(InventoryAdjustmentMeta.getSystemUserId());
        addScreenHandler(systemUserId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                systemUserId.setValue(manager.getInventoryAdjustment().getSystemUserName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryAdjustment().setSystemUserName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                systemUserId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                systemUserId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        inventoryItemStoreId = (Dropdown)def.getWidget(InventoryAdjustmentMeta.getInventoryLocationInventoryItemStoreId());
        addScreenHandler(inventoryItemStoreId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                inventoryItemStoreId.setSelection(manager.getInventoryAdjustment().getIInventoryXAdjustInventoryLocationInventoryItemStoreId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryAdjustment().setInventoryXAdjustInventoryLocationInventoryItemStoreId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemStoreId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                inventoryItemStoreId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        adjustmentTable = (TableWidget)def.getWidget("adjustmentTable");
        addScreenHandler(adjustmentTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    adjustmentTable.load(getAdjustmentTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                adjustmentTable.enable(true);
                adjustmentTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        adjustmentTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) { 
                int c;
                
                c = event.getCol();
                
                if (state != State.UPDATE && state != State.ADD) {
                    event.cancel();
                } else if (c == 3 || c == 5) {
                    event.cancel();                
                }                     
            }           
        });

        adjustmentTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String location;
                Integer qtyOnHand, physCount, adjQty;
                InventoryXAdjustViewDO data;
                InventoryLocationViewDO invLoc;
                TableDataRow row;                             
                Object val;
                
                r = event.getRow();
                c = event.getCol();
                val = adjustmentTable.getObject(r,c);
                
                try {
                    data = manager.getAdjustments().getAdjustmentAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                switch(c) {
                    case 0:
                        row = (TableDataRow)val;
                        if (row != null && row.data != null) {
                            invLoc = (InventoryLocationViewDO)row.data;
                            data.setInventoryLocationId(invLoc.getId());
                            data.setInventoryLocationInventoryItemId(invLoc.getInventoryItemId());
                            data.setInventoryLocationInventoryItemName(invLoc.getInventoryItemName());
                            data.setInventoryLocationLotNumber(invLoc.getLotNumber());
                            data.setInventoryLocationQuantityOnhand(invLoc.getQuantityOnhand());
                            data.setInventoryLocationStorageLocationLocation(invLoc.getStorageLocationLocation());
                            data.setInventoryLocationStorageLocationName(invLoc.getStorageLocationName());
                            data.setInventoryLocationStorageLocationUnitDescription(invLoc.getStorageLocationUnitDescription());
                            
                            adjustmentTable.setCell(r, 1, new TableDataRow(invLoc.getInventoryItemId(),invLoc.getInventoryItemName()));
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            adjustmentTable.setCell(r, 2, location);
                            adjustmentTable.setCell(r, 3, invLoc.getQuantityOnhand());
                            adjustmentTable.setCell(r, 4, null);
                            adjustmentTable.setCell(r, 5, null);
                            adjustmentTable.clearCellExceptions(r, 1);
                        } else  {
                            data.setInventoryLocationId(null);
                            data.setInventoryLocationInventoryItemId(null);
                            data.setInventoryLocationInventoryItemName(null);
                            data.setInventoryLocationLotNumber(null);
                            data.setInventoryLocationQuantityOnhand(null);
                            data.setInventoryLocationQuantityOnhand(null);
                            data.setInventoryLocationStorageLocationLocation(null);
                            data.setInventoryLocationStorageLocationName(null);
                            data.setInventoryLocationStorageLocationUnitDescription(null);
                            
                            adjustmentTable.setCell(r, 1, null);                            
                            adjustmentTable.setCell(r, 2, null);
                            adjustmentTable.setCell(r, 3, null);
                            adjustmentTable.setCell(r, 4, null);
                            adjustmentTable.setCell(r, 5, null);
                        }                      
                        break;
                    case 1:
                        row = (TableDataRow)val;
                        if (row != null) {
                            invLoc = (InventoryLocationViewDO)row.data;
                            data.setInventoryLocationId(invLoc.getId());
                            data.setInventoryLocationInventoryItemId(invLoc.getInventoryItemId());
                            data.setInventoryLocationInventoryItemName(invLoc.getInventoryItemName());
                            data.setInventoryLocationLotNumber(invLoc.getLotNumber());
                            data.setInventoryLocationQuantityOnhand(invLoc.getQuantityOnhand());
                            data.setInventoryLocationStorageLocationLocation(invLoc.getStorageLocationLocation());
                            data.setInventoryLocationStorageLocationName(invLoc.getStorageLocationName());
                            data.setInventoryLocationStorageLocationUnitDescription(invLoc.getStorageLocationUnitDescription());
                            
                            adjustmentTable.setCell(r, 0, new TableDataRow(invLoc.getId(), invLoc.getId().toString()));
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            adjustmentTable.setCell(r, 2, location);
                            adjustmentTable.setCell(r, 3, invLoc.getQuantityOnhand());
                            adjustmentTable.setCell(r, 4, null);
                            adjustmentTable.setCell(r, 5, null);
                            adjustmentTable.clearCellExceptions(r, 0);
                        } else  {
                            data.setInventoryLocationId(null);
                            data.setInventoryLocationInventoryItemId(null);
                            data.setInventoryLocationInventoryItemName(null);
                            data.setInventoryLocationLotNumber(null);
                            data.setInventoryLocationQuantityOnhand(null);
                            data.setInventoryLocationQuantityOnhand(null);
                            data.setInventoryLocationStorageLocationLocation(null);
                            data.setInventoryLocationStorageLocationName(null);
                            data.setInventoryLocationStorageLocationUnitDescription(null);
                            
                            adjustmentTable.setCell(r, 0, null);                            
                            adjustmentTable.setCell(r, 2, null);
                            adjustmentTable.setCell(r, 3, null);
                            adjustmentTable.setCell(r, 4, null);
                            adjustmentTable.setCell(r, 5, null);
                        }                      
                        break;
                    case 4:
                        physCount = (Integer)val;
                        qtyOnHand = (Integer)adjustmentTable.getObject(r, 3);
                        data.setPhysicalCount(physCount);
                        if (physCount != null && qtyOnHand != null) {
                            adjQty = physCount - qtyOnHand;
                            data.setQuantity(adjQty);
                            adjustmentTable.setCell(r, 5, adjQty);
                        }
                        break;
                }
            }
        });

        adjustmentTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getAdjustments().addAdjustment();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        adjustmentTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAdjustments().removeAdjustmentAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        inventoryLocationId = (AutoComplete<Integer>)adjustmentTable.getColumnWidget(InventoryAdjustmentMeta.getInventoryLocationId());
        
        inventoryLocationId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer id;
                InventoryLocationViewDO data;
                DictionaryDO store;
                TableDataRow row;
                ArrayList<TableDataRow> model;
                String location;
                
                id = null;
                model = new ArrayList<TableDataRow>();
                try {
                    id = new Integer(event.getMatch());
                } catch (NumberFormatException e) {
                    model.add(new TableDataRow(null, ""));
                    inventoryLocationId.showAutoMatches(model);
                    e.printStackTrace();
                    return;
                }
                
                try {
                    data = inventoryLocationService.call("fetchById", id);
                    
                    row = new TableDataRow(7);
                    row.key = data.getId();  
                    row.cells.get(0).setValue(data.getId().toString());
                    row.cells.get(1).setValue(data.getInventoryItemName());
                    store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                    row.cells.get(2).setValue(store.getEntry());
                    location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                            data.getStorageLocationUnitDescription(),
                                                                            data.getStorageLocationLocation());
                    row.cells.get(3).setValue(location);
                    row.cells.get(4).setValue(data.getLotNumber());
                    row.cells.get(5).setValue(data.getExpirationDate());
                    row.cells.get(6).setValue(data.getQuantityOnhand());

                    row.data = data;

                    model.add(row);

                    
                } catch (NotFoundException e) {    
                    model.add(new TableDataRow(null, ""));
                } catch (Exception e) {                     
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                    
                }
                inventoryLocationId.showAutoMatches(model);
            }
        });
        
        inventoryLocationInventoryItemName = (AutoComplete<Integer>)adjustmentTable.getColumnWidget(InventoryAdjustmentMeta.getInventoryLocationInventoryItemName());
        
        inventoryLocationInventoryItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer storeId;
                String location;
                InventoryLocationViewDO data;
                TableDataRow row;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<TableDataRow> model;
                DictionaryDO store;
                Query query;
                QueryData field;
                QueryFieldUtil parser;

                storeId = inventoryItemStoreId.getValue();
                
                if (storeId == null) {
                    Window.alert(consts.get("plsSelStore"));
                    return;
                }
                
                query = new Query();
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                field = new QueryData();
                field.key = InventoryItemMeta.getName();
                field.type = QueryData.Type.STRING;
                field.query = parser.getParameter().get(0);
                query.setFields(field);
                
                field = new QueryData();
                field.key = InventoryItemMeta.getStoreId();
                field.type = QueryData.Type.INTEGER;                     
                field.query = storeId.toString();           
                query.setFields(field);
                
                try {
                    list = inventoryLocationService.callList("fetchByInventoryItemNameStoreId",
                                                             query);
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(6);
                        data = list.get(i);

                        row.key = data.getId();                        
                        row.cells.get(0).setValue(data.getInventoryItemName());
                        store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                        row.cells.get(1).setValue(store.getEntry());
                        location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                data.getStorageLocationUnitDescription(),
                                                                                data.getStorageLocationLocation());
                        row.cells.get(2).setValue(location);
                        row.cells.get(3).setValue(data.getLotNumber());
                        row.cells.get(4).setValue(data.getExpirationDate());
                        row.cells.get(5).setValue(data.getQuantityOnhand());

                        row.data = data;

                        model.add(row);
                    }
                    inventoryLocationInventoryItemName.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });    
        
        addRowButton = (AppButton)def.getWidget("addRowButton");
        addScreenHandler(addRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;                
                    
                adjustmentTable.addRow();
                n = adjustmentTable.numRows() - 1;
                adjustmentTable.selectRow(n);
                adjustmentTable.scrollToSelection();
                adjustmentTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addRowButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = adjustmentTable.getSelectedRow();
                if (r > -1 && adjustmentTable.numRows() > 0)
                    adjustmentTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
        
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<InventoryAdjustmentDO>>() {
                    public void onSuccess(ArrayList<InventoryAdjustmentDO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: Inventory Adjustment call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((InventoryAdjustmentDO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<InventoryAdjustmentDO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (InventoryAdjustmentDO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getId().toString(), entry.getAdjustmentDate()));
                }
                return model;
            }
        };
        
        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = InventoryAdjustmentMeta.getId();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.INTEGER;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        // country dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_store");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        inventoryItemStoreId.setModel(model);
        
    }
    
    private void query() {
        manager = InventoryAdjustmentManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);        
        setFocus(id);
        window.setDone(consts.get("enterFieldsToQuery"));
        
    }
    
    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        Datetime now;
        InventoryAdjustmentViewDO data;
        try {
            now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            Window.alert("Inventory Adjustment Datetime: " +e.getMessage());
            return;
        }
        manager = InventoryAdjustmentManager.getInstance();
        data = manager.getInventoryAdjustment();
        data.setAdjustmentDate(now);
        data.setSystemUserId(OpenELIS.security.getSystemUserId());
        data.setSystemUserName(OpenELIS.security.getSystemUserName());
                
        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(description);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(description);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
        
    }
    
    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }        
    }
    
    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }        
    }
    
    protected void inventoryAdjustmentHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getInventoryAdjustment().getId(), manager.getInventoryAdjustment().getDescription());
        HistoryScreen.showHistory(consts.get("inventoryAdjustmentHistory"), ReferenceTable.INVENTORY_ADJUSTMENT, hist);        
    }
    
    protected void inventoryAdjustmentLocationHistory() {
        int count;
        IdNameVO list[];
        InventoryXAdjustManager man;
        InventoryXAdjustViewDO data;
        String label;

        try {
            man = manager.getAdjustments();
            count = man.count();
            list = new IdNameVO[count];
            for (int i = 0; i < count; i++ ) {
                data = man.getAdjustmentAt(i);
                label = data.getInventoryLocationInventoryItemName()+ ": "+
                                StorageLocationManager.getLocationForDisplay(data.getInventoryLocationStorageLocationName(),
                                                                             data.getInventoryLocationStorageLocationUnitDescription(),
                                                                             data.getInventoryLocationStorageLocationLocation());
                list[i] = new IdNameVO(data.getId(), label);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("inventoryAdjustmentLocationHistory"), ReferenceTable.INVENTORY_X_ADJUST, list);
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = InventoryAdjustmentManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = InventoryAdjustmentManager.fetchWithAdjustments(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }

        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private ArrayList<TableDataRow> getAdjustmentTableModel() {
        ArrayList<TableDataRow> model;
        InventoryXAdjustManager man;
        InventoryXAdjustViewDO data;
        TableDataRow row;
        String location;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getAdjustments();
            for(int i = 0; i < man.count(); i++) {
                data = man.getAdjustmentAt(i);
                row = new TableDataRow(6);
                row.key = data.getId();
                row.cells.get(0).setValue(new TableDataRow(data.getInventoryLocationId(), data.getInventoryLocationId().toString()));
                row.cells.get(1).setValue(new TableDataRow(data.getInventoryLocationInventoryItemId(),
                                                           data.getInventoryLocationInventoryItemName()));
                location = StorageLocationManager.getLocationForDisplay(data.getInventoryLocationStorageLocationName(),
                                                                        data.getInventoryLocationStorageLocationUnitDescription(),
                                                                        data.getInventoryLocationStorageLocationLocation());
                row.cells.get(2).setValue(location);
                row.cells.get(3).setValue(data.getInventoryLocationQuantityOnhand());
                row.cells.get(4).setValue(data.getPhysicalCount());
                row.cells.get(5).setValue(data.getQuantity());
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
}
/*    

    private TableWidget        adjustmentsTable;
    private AppButton        removeRowButton;
    
    private ScreenTextBox   idText, userText, descText;
    private Dropdown store;
    private ScreenCalendar adjustmentDateText;
    private KeyListManager   keyList = new KeyListManager();
    private Integer lastLocValue = null;
    
    private InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static String storeIdKey;
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new InventoryAdjustmentForm());
    }
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void afterDraw(boolean sucess) {
        removeRowButton = (AppButton)getWidget("removeRowButton");
        adjustmentsTable = (TableWidget)getWidget("adjustmentsTable");
        adjustmentsTable.addTableWidgetListener(this);
        adjustmentsTable.model.enableAutoAdd(false);
        
        storeIdKey = InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId();
        
        idText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getId());
        adjustmentDateText = (ScreenCalendar) widgets.get(InventoryAdjustmentMeta.getAdjustmentDate());
        userText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getSystemUserId());
        store = (Dropdown) getWidget(storeIdKey);
        descText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getDescription());
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        updateChain.add(afterUpdate);
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        super.afterDraw(sucess);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("inventory_item_stores");
        model = getDictionaryIdEntryList(cache);
        store.setModel(model);
    }
    
    public void add() {
        super.add();
        adjustmentsTable.model.enableAutoAdd(true);
        
        idText.enable(false);
        adjustmentDateText.enable(false);
        userText.enable(false);
        
        //InventoryAdjustmentForm iarpc = new InventoryAdjustmentForm();
        //iarpc.screenKey = form.screenKey;
        //iarpc.form = form.form;
        
        screenService.call("getAddAutoFillValues", form, new AsyncCallback<InventoryAdjustmentForm>(){
            public void onSuccess(InventoryAdjustmentForm result){    

                //load the values
                adjustmentDateText.load(result.adjustmentDate);
                userText.load(result.systemUser);
                
                //set the values in the rpc
                form.systemUser.setValue(result.systemUser.getValue());
                form.adjustmentDate.setValue(result.adjustmentDate.getValue());
                form.systemUserId = result.systemUserId;
                descText.setFocus(true);
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
    }
    
    protected SyncCallback afterUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            idText.enable(false);
            adjustmentDateText.enable(false);
            userText.enable(false);
            ((ScreenDropDownWidget)widgets.get(storeIdKey)).enable(false);
            descText.setFocus(true);
            removeRowButton.changeState(AppButton.ButtonState.DISABLED);
            
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void abort() {
        adjustmentsTable.model.enableAutoAdd(false);
        
        if(state == State.ADD){
            //we need to unlock all the locs
          //unlock the record
            InventoryAdjustmentForm iarpc = new InventoryAdjustmentForm();
            iarpc.lockedIds = getLockedSetsFromTable();

            screenService.call("unlockLocations", iarpc, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    superAbort();
                    
               }

               public void onFailure(Throwable caught) {
                   Window.alert(caught.getMessage());
               }
                });
        }else
            super.abort();
    }
    
    private void superAbort(){
        super.abort();
    }
    
    public void query() {
        super.query();
        userText.enable(false);
        idText.setFocus(true);
    }
    
    protected SyncCallback afterCommitUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            adjustmentsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected SyncCallback afterCommitAdd = new SyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          adjustmentsTable.model.enableAutoAdd(false);
      }   
  };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return !tableRowEmpty(addRow);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(state == State.UPDATE && (col == 0 || col == 1))
            return false;
        
       return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)           
            return true;
        return false;
    }
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if(state == State.QUERY || row >= adjustmentsTable.model.numRows())
            return;
        final TableDataRow<Integer> tableRow = adjustmentsTable.model.getRow(row);
        final DropDownField<Integer> invItemField = (DropDownField<Integer>)tableRow.cells[1];
        switch (col){
            case 0:
                if(store.getSelections().size() == 0){
                    window.setError(consts.get("inventoryAdjLocAutoException"));
                    return;
                }
                
                Integer currentLocValue = (Integer)adjustmentsTable.model.getCell(row, col);
                if(store.getSelections().size() > 0)
                    form.storeIdKey = (Integer)store.getSelections().get(0).key;
                form.locId = currentLocValue;
                
                //we need to make sure the location id isnt already in the table
                if(locationIdAlreadyExists(form.locId, row)){
                    ((IntegerField)tableRow.cells[0]).clearErrors();
                    adjustmentsTable.model.setCellError(row, 0, consts.get("fieldUniqueException"));
                    return;
                }
                
                if((lastLocValue == null && currentLocValue != null) || 
                                (lastLocValue != null && !lastLocValue.equals(currentLocValue))){
                    //we need to send lock/fetch call back
                    form.oldLocId = lastLocValue;
                    
                    window.setBusy();
                    
                    screenService.call("getInventoryItemInformation", form, new AsyncCallback<InventoryAdjustmentForm>(){
                        public void onSuccess(InventoryAdjustmentForm result){  
                            if(result.invItemModel != null && result.invItemModel.size() > 0){
                                invItemField.setModel(result.invItemModel);
                                adjustmentsTable.model.setCell(row, 1, result.invItemModel.get(0));
                                adjustmentsTable.model.clearCellError(row, 1);
                                adjustmentsTable.model.setCell(row, 2, result.storageLocation);
                                adjustmentsTable.model.setCell(row, 3, result.qtyOnHand);
                            }
                            window.clearStatus();
                        }
                        
                        public void onFailure(Throwable caught){
                            //clear out the columns
                            adjustmentsTable.model.setCell(row, 0, null);
                            adjustmentsTable.model.setCell(row, 1, null);
                            adjustmentsTable.model.setCell(row, 2, null);
                            adjustmentsTable.model.setCell(row, 3, null);
                            adjustmentsTable.model.setCell(row, 4, null);
                            adjustmentsTable.model.setCell(row, 5, null);
                            //adjustmentsTable.select(row, 0);
                            
                            Window.alert(caught.getMessage());
                            window.clearStatus();
                        }
                    });
                }
                break;
            case 1:
                    ArrayList selections = invItemField.getValue();
          
                    if(selections.size() > 0){
                        TableDataRow selectedRow = (TableDataRow)selections.get(0);
          
                        if(selectedRow.size() > 1){
                            Integer curLocValue = ((IntegerObject)selectedRow.getData()).getValue();
                            if((lastLocValue == null && curLocValue != null) || 
                                (lastLocValue != null && !lastLocValue.equals(curLocValue))){
                                //we need to make sure this inventory item isnt already in the table
                                if(locationIdAlreadyExists(((IntegerObject)selectedRow.getData()).getValue(), row)){
                                    ((DropDownField)tableRow.cells[1]).clearErrors();
                                    adjustmentsTable.model.setCellError(row, 1, consts.get("fieldUniqueException"));
                                 
                                }else{
                                    adjustmentsTable.model.setCell(row, 0, ((IntegerObject)selectedRow.getData()).getValue());
                                    adjustmentsTable.model.clearCellError(row, 0);
                                    adjustmentsTable.model.setCell(row, 2, ((StringObject)selectedRow.cells[2]).getValue());
                                    adjustmentsTable.model.setCell(row, 3, ((IntegerObject)selectedRow.cells[5]).getValue());
                                    
                                    window.setBusy();
                                    
                                    form.locId = curLocValue;
                                    form.oldLocId = lastLocValue;
                                    
                                    screenService.call("fetchLocationAndLock", form, new AsyncCallback<InventoryAdjustmentForm>(){
                                        public void onSuccess(InventoryAdjustmentForm result){    
                                            adjustmentsTable.model.setCell(row, 3, result.qtyOnHand);

                                            window.clearStatus();
                                        }
                                        
                                        public void onFailure(Throwable caught){
                                            //clear out the columns
                                            adjustmentsTable.model.setCell(row, 0, null);
                                            adjustmentsTable.model.setCell(row, 1, null);
                                            adjustmentsTable.model.setCell(row, 2, null);
                                            adjustmentsTable.model.setCell(row, 3, null);
                                            adjustmentsTable.model.setCell(row, 4, null);
                                            adjustmentsTable.model.setCell(row, 5, null);
                                            adjustmentsTable.select(row, 1);
                                            
                                            Window.alert(caught.getMessage());
                                            window.clearStatus();
                                            form.locId = null;
                                            form.oldLocId = null;
                                        }
                                    });
                                
                                }
                            }
                        }    
                    }

                break;
            case 4:
                Integer qtyOnHand = null;
                Integer physicalCount = null;
                Integer adjQty = null;
                
                qtyOnHand = (Integer)tableRow.cells[3].getValue();
                physicalCount = (Integer)tableRow.cells[4].getValue();
                
                if(qtyOnHand != null && physicalCount != null){
                    adjQty = physicalCount - qtyOnHand;
                    
                    adjustmentsTable.model.setCell(row, 5, adjQty);
                }
                break;
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(state == State.QUERY)
            return;
        
        if(col == 0 && row < adjustmentsTable.model.numRows()){
            lastLocValue = (Integer)adjustmentsTable.model.getCell(row, col);

        }else if(col == 1 && row < adjustmentsTable.model.numRows()){
            ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)adjustmentsTable.model.getObject(row, col)).getValue();
            
            if(selections != null && selections.size() == 1)
                lastLocValue = (Integer)selections.get(0).getData().getValue();
        }
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    private boolean tableRowEmpty(TableDataRow<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.cells.length; i++){
            if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
    
    private void onRemoveRowButtonClick() {
        int selectedRow = adjustmentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && adjustmentsTable.model.numRows() > 0) {
            adjustmentsTable.model.deleteRow(selectedRow);

        }
    }
    
    private boolean locationIdAlreadyExists(Integer locationId, int currentRow){
        boolean exists = false;
        
        if(locationId != null){        
            TableModel model = (TableModel)adjustmentsTable.model;
            
            for(int i=0; i<model.numRows(); i++){
                if(i != currentRow && locationId.equals(model.getCell(i, 0))){
                    exists = true;
                    break;
                }            
            }
        }
        
        return exists;
    }

    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
        // prepare the arguments
        InventoryAdjustmentItemAutoRPC iaarpc = new InventoryAdjustmentItemAutoRPC();
        iaarpc.key = form.entityKey;
        iaarpc.cat = widget.cat;
        iaarpc.text = text;
        if(store.getSelections().size() > 0)
            iaarpc.storeId = (Integer)store.getSelections().get(0).key;
        
        screenService.call("getMatchesObj", iaarpc, new AsyncCallback<InventoryAdjustmentItemAutoRPC>() {
            public void onSuccess(InventoryAdjustmentItemAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setError(caught.getMessage());
                }else
                    Window.alert(caught.getMessage());
            }
        });        
    }
    
    private TableDataModel<TableDataRow<Integer>> getLockedSetsFromTable(){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();

        for(int i=0; i<adjustmentsTable.model.numRows(); i++)
            returnModel.add(new TableDataRow<Integer>((Integer)adjustmentsTable.model.getCell(i, 0)));
            
        return returnModel;
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
*/
