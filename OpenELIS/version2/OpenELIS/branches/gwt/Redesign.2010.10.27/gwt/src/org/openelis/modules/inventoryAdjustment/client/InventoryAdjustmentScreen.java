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
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryAdjustmentScreen extends Screen {
    
    private InventoryAdjustmentManager manager;
    private ModulePermission           userPermission;    
    
    
    private ButtonGroup                atoz;
    private ScreenNavigator            nav;
    
    private Dropdown<Integer>          inventoryItemStoreId;
    private Calendar                   adjustmentDate;
    private TextBox                    id, description, systemUserId;
    private AutoComplete               inventoryLocationId, inventoryLocationInventoryItemName;
    
    private Button                     queryButton, previousButton, nextButton, addButton,
                                       updateButton, commitButton, abortButton, addRowButton,
                                       removeRowButton;
    protected MenuItem                 inventoryAdjustmentHistory, inventoryAdjustmentLocationHistory;
    private Table                      adjustmentTable;
    private ScreenService              inventoryLocationService;
    
    public InventoryAdjustmentScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryAdjustmentDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");        

        userPermission = OpenELIS.getSystemUserPermission().getModule("inventoryadjustment");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Inventory Adjustment Screen");

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
            com.google.gwt.user.client.Window.alert("Inventory Adjustment Screen: missing dictionary entry; " + e.getMessage());
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
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        previousButton = (Button)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (Button)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasAddPermission());
                if (event.getState() == State.ADD) {
                    addButton.setPressed(true);
                    addButton.lock();
                }
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }           

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        inventoryAdjustmentHistory = (MenuItem)def.getWidget("inventoryAdjustmentHistory");
        addScreenHandler(inventoryAdjustmentHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                inventoryAdjustmentHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        inventoryAdjustmentHistory.addCommand(new Command() {
			public void execute() {
				inventoryAdjustmentHistory();
			}
		});
        
        inventoryAdjustmentLocationHistory = (MenuItem)def.getWidget("inventoryAdjustmentLocationHistory");
        addScreenHandler(inventoryAdjustmentLocationHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                inventoryAdjustmentLocationHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        inventoryAdjustmentLocationHistory.addCommand(new Command() {
			public void execute() {
				inventoryAdjustmentLocationHistory();
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
                id.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
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
                description.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        adjustmentDate = (Calendar)def.getWidget(InventoryAdjustmentMeta.getAdjustmentDate());
        addScreenHandler(adjustmentDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                adjustmentDate.setValue(manager.getInventoryAdjustment().getAdjustmentDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInventoryAdjustment().setAdjustmentDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                adjustmentDate.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                systemUserId.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                systemUserId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        inventoryItemStoreId = (Dropdown)def.getWidget(InventoryAdjustmentMeta.getInventoryLocationInventoryItemStoreId());
        addScreenHandler(inventoryItemStoreId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                inventoryItemStoreId.setValue(manager.getInventoryAdjustment().getIInventoryXAdjustInventoryLocationInventoryItemStoreId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryAdjustment().setInventoryXAdjustInventoryLocationInventoryItemStoreId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemStoreId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                inventoryItemStoreId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        adjustmentTable = (Table)def.getWidget("adjustmentTable");
        addScreenHandler(adjustmentTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    adjustmentTable.setModel(getAdjustmentTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                adjustmentTable.setEnabled(true);
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
                AutoCompleteValue av;                             
                Object val;
                
                r = event.getRow();
                c = event.getCol();
                val = adjustmentTable.getValueAt(r,c);
                
                try {
                    data = manager.getAdjustments().getAdjustmentAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                switch(c) {
                    case 0:
                        av = (AutoCompleteValue)val;
                        if (av != null && av.getData() != null) {
                            invLoc = (InventoryLocationViewDO)av.getData();
                            data.setInventoryLocationId(invLoc.getId());
                            data.setInventoryLocationInventoryItemId(invLoc.getInventoryItemId());
                            data.setInventoryLocationInventoryItemName(invLoc.getInventoryItemName());
                            data.setInventoryLocationLotNumber(invLoc.getLotNumber());
                            data.setInventoryLocationQuantityOnhand(invLoc.getQuantityOnhand());
                            data.setInventoryLocationStorageLocationLocation(invLoc.getStorageLocationLocation());
                            data.setInventoryLocationStorageLocationName(invLoc.getStorageLocationName());
                            data.setInventoryLocationStorageLocationUnitDescription(invLoc.getStorageLocationUnitDescription());
                            
                            adjustmentTable.setValueAt(r, 1, new AutoCompleteValue(invLoc.getInventoryItemId(),invLoc.getInventoryItemName()));
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            adjustmentTable.setValueAt(r, 2, location);
                            adjustmentTable.setValueAt(r, 3, invLoc.getQuantityOnhand());
                            adjustmentTable.setValueAt(r, 4, null);
                            adjustmentTable.setValueAt(r, 5, null);
                            adjustmentTable.clearExceptions(r, 1);
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
                            
                            adjustmentTable.setValueAt(r, 1, null);                            
                            adjustmentTable.setValueAt(r, 2, null);
                            adjustmentTable.setValueAt(r, 3, null);
                            adjustmentTable.setValueAt(r, 4, null);
                            adjustmentTable.setValueAt(r, 5, null);
                        }                      
                        break;
                    case 1:
                        av = (AutoCompleteValue)val;
                        if (av != null) {
                            invLoc = (InventoryLocationViewDO)av.getData();
                            data.setInventoryLocationId(invLoc.getId());
                            data.setInventoryLocationInventoryItemId(invLoc.getInventoryItemId());
                            data.setInventoryLocationInventoryItemName(invLoc.getInventoryItemName());
                            data.setInventoryLocationLotNumber(invLoc.getLotNumber());
                            data.setInventoryLocationQuantityOnhand(invLoc.getQuantityOnhand());
                            data.setInventoryLocationStorageLocationLocation(invLoc.getStorageLocationLocation());
                            data.setInventoryLocationStorageLocationName(invLoc.getStorageLocationName());
                            data.setInventoryLocationStorageLocationUnitDescription(invLoc.getStorageLocationUnitDescription());
                            
                            adjustmentTable.setValueAt(r, 0, new AutoCompleteValue(invLoc.getId(), invLoc.getId().toString()));
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            adjustmentTable.setValueAt(r, 2, location);
                            adjustmentTable.setValueAt(r, 3, invLoc.getQuantityOnhand());
                            adjustmentTable.setValueAt(r, 4, null);
                            adjustmentTable.setValueAt(r, 5, null);
                            adjustmentTable.clearExceptions(r, 0);
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
                            
                            adjustmentTable.setValueAt(r, 0, null);                            
                            adjustmentTable.setValueAt(r, 2, null);
                            adjustmentTable.setValueAt(r, 3, null);
                            adjustmentTable.setValueAt(r, 4, null);
                            adjustmentTable.setValueAt(r, 5, null);
                        }                      
                        break;
                    case 4:
                        physCount = (Integer)val;
                        qtyOnHand = (Integer)adjustmentTable.getValueAt(r, 3);
                        data.setPhysicalCount(physCount);
                        if (physCount != null && qtyOnHand != null) {
                            adjQty = physCount - qtyOnHand;
                            data.setQuantity(adjQty);
                            adjustmentTable.setValueAt(r, 5, adjQty);
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        adjustmentTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAdjustments().removeAdjustmentAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });
        
        inventoryLocationId = (AutoComplete)adjustmentTable.getColumnWidget(InventoryAdjustmentMeta.getInventoryLocationId());
        
        inventoryLocationId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer id;
                InventoryLocationViewDO data;
                DictionaryDO store;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;
                String location;
                
                id = null;
                model = new ArrayList<Item<Integer>>();
                try {
                    id = new Integer(event.getMatch());
                } catch (NumberFormatException e) {
                    model.add(new Item<Integer>(null, ""));
                    inventoryLocationId.showAutoMatches(model);
                    e.printStackTrace();
                    return;
                }
                
                try {
                    data = inventoryLocationService.call("fetchById", id);
                    
                    row = new Item<Integer>(7);
                    row.setKey(data.getId());  
                    row.setCell(0,data.getId().toString());
                    row.setCell(1,data.getInventoryItemName());
                    store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                    row.setCell(2,store.getEntry());
                    location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                            data.getStorageLocationUnitDescription(),
                                                                            data.getStorageLocationLocation());
                    row.setCell(3,location);
                    row.setCell(4,data.getLotNumber());
                    row.setCell(5,data.getExpirationDate());
                    row.setCell(6,data.getQuantityOnhand());

                    row.setData(data);

                    model.add(row);

                    
                } catch (NotFoundException e) {    
                    model.add(new Item<Integer>(null, ""));
                } catch (Exception e) {                     
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    
                }
                inventoryLocationId.showAutoMatches(model);
            }
        });
        
        inventoryLocationInventoryItemName = (AutoComplete)adjustmentTable.getColumnWidget(InventoryAdjustmentMeta.getInventoryLocationInventoryItemName());
        
        inventoryLocationInventoryItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer storeId;
                String location;
                InventoryLocationViewDO data;
                Item<Integer> row;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<Item<Integer>> model;
                DictionaryDO store;
                Query query;
                QueryData field;
                QueryFieldUtil parser;

                storeId = inventoryItemStoreId.getValue();
                
                if (storeId == null) {
                    com.google.gwt.user.client.Window.alert(consts.get("plsSelStore"));
                    return;
                }
                
                query = new Query();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(!event.getMatch().equals("") ? event.getMatch() : "*");
                }catch(Exception e) {
                	
                }

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
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(6);
                        data = list.get(i);

                        row.setKey(data.getId());                        
                        row.setCell(0,data.getInventoryItemName());
                        store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                        row.setCell(1,store.getEntry());
                        location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                data.getStorageLocationUnitDescription(),
                                                                                data.getStorageLocationLocation());
                        row.setCell(2,location);
                        row.setCell(3,data.getLotNumber());
                        row.setCell(4,data.getExpirationDate());
                        row.setCell(5,data.getQuantityOnhand());

                        row.setData(data);

                        model.add(row);
                    }
                    inventoryLocationInventoryItemName.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });    
        
        addRowButton = (Button)def.getWidget("addRowButton");
        addScreenHandler(addRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;                
                    
                adjustmentTable.addRow();
                n = adjustmentTable.getRowCount() - 1;
                adjustmentTable.selectRowAt(n);
                adjustmentTable.scrollToVisible(n);
                adjustmentTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addRowButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeRowButton = (Button)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = adjustmentTable.getSelectedRow();
                if (r > -1 && adjustmentTable.getRowCount() > 0)
                    adjustmentTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
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
                            com.google.gwt.user.client.Window.alert("Error: Inventory Adjustment call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((InventoryAdjustmentDO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<InventoryAdjustmentDO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (InventoryAdjustmentDO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getId(), entry.getAdjustmentDate()));
                }
                return model;
            }
        };
        
        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                atoz.setEnabled(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = InventoryAdjustmentMeta.getId();
                field.query = ((Button)event.getSource()).getAction();
                field.type = QueryData.Type.INTEGER;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        // country dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_store");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
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
            now = org.openelis.gwt.screen.Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("Inventory Adjustment Datetime: " +e.getMessage());
            return;
        }
        manager = InventoryAdjustmentManager.getInstance();
        data = manager.getInventoryAdjustment();
        data.setAdjustmentDate(now);
        data.setSystemUserId(OpenELIS.getSystemUserPermission().getSystemUserId());
        data.setSystemUserName(OpenELIS.getSystemUserPermission().getLoginName());
                
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
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
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
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
                com.google.gwt.user.client.Window.alert(e.getMessage());
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }

        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private ArrayList<Row> getAdjustmentTableModel() {
        ArrayList<Row> model;
        InventoryXAdjustManager man;
        InventoryXAdjustViewDO data;
        Row row;
        String location;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getAdjustments();
            for(int i = 0; i < man.count(); i++) {
                data = man.getAdjustmentAt(i);
                row = new Row(6);
                //row.key = data.getId();
                row.setCell(0,new AutoCompleteValue(data.getInventoryLocationId(), data.getInventoryLocationId().toString()));
                row.setCell(1,new AutoCompleteValue(data.getInventoryLocationInventoryItemId(),
                                                           data.getInventoryLocationInventoryItemName()));
                location = StorageLocationManager.getLocationForDisplay(data.getInventoryLocationStorageLocationName(),
                                                                        data.getInventoryLocationStorageLocationUnitDescription(),
                                                                        data.getInventoryLocationStorageLocationLocation());
                row.setCell(2,location);
                row.setCell(3,data.getInventoryLocationQuantityOnhand());
                row.setCell(4,data.getPhysicalCount());
                row.setCell(5,data.getQuantity());
                
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
}