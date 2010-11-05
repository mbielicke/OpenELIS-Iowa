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
package org.openelis.modules.inventoryTransfer.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.InventoryItemCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.PermissionException;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Item;
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
import org.openelis.manager.InventoryTransferManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class InventoryTransferScreen extends Screen {

    private ModulePermission                      userPermission;
    private InventoryTransferScreen               screen;
    private InventoryTransferManager              manager;
    private boolean                               openedFromMenu, reloadTable;
    private TextBox                               inventoryItemDescription, inventoryItemStoreId,
                                                  inventoryItemDispensedUnitsId,
                                                  inventoryLocationLotNumber, inventoryLocationExpirationDate,
                                                  toDescription, toStoreId, toDispensedUnits,
                                                  toLotNumber, toExpDate;
    private AutoComplete                          fromItemName, toItemName, toStorageLocationName;
    private Button                                addButton, commitButton, abortButton,
                                                  addReceiptButton, removeReceiptButton;
    private Table                                 receiptTable;
    private ScreenService                         inventoryItemService, inventoryLocationService, 
                                                  storageService;    
    
    public InventoryTransferScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryTransferDef.class));

        InventoryTransferScreenImpl(true);
    }
    
    public InventoryTransferScreen(Window window) throws Exception {
        super((ScreenDefInt)GWT.create(InventoryTransferDef.class));
        this.window = window;

        InventoryTransferScreenImpl(false);
    }
    
    private void InventoryTransferScreenImpl(boolean fromMenu) throws Exception {
        service = new ScreenService("controller?service=org.openelis.modules.inventoryTransfer.server.InventoryTransferService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");        
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("inventorytransfer");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Inventory Transfer Screen");
        
        openedFromMenu = fromMenu;
        //
        // this is done here in order to make sure that if the screen is brought
        // up from some other screen then its widgets are initialized before the
        // constructor ends execution
        //
        if (window != null) {
            postConstructor();
        } else {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    postConstructor();
                }
            });
        }
    } 
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        initialize(); 
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        //
        // button panel buttons
        //
        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add(null);
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

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
            }
        });         

        receiptTable = (Table)def.getWidget("receiptTable");
        addScreenHandler(receiptTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                // this is done in order to prevent the table from getting refreshed
                // when DataChangeEvent is fired to refresh the widgets that are
                // loaded from the "from/to" inventory item/location when one of
                // these is set in one of the rows in the table
                //
                if(reloadTable)      
                    receiptTable.setModel(getTransferModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receiptTable.setEnabled(true);
                receiptTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        fromItemName = (AutoComplete)receiptTable.getColumnWidget("fromItemName");
        toItemName = (AutoComplete)receiptTable.getColumnWidget("toItemName");
        toStorageLocationName = (AutoComplete)receiptTable.getColumnWidget("toLoc"); 
        
        fromItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String location;
                InventoryLocationViewDO data;
                Item<Integer> row;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<Item<Integer>> model;                
                DictionaryDO store;

                try {
                    list = inventoryLocationService.callList("fetchByInventoryItemName", event.getMatch());
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                data.getStorageLocationUnitDescription(),
                                                                                data.getStorageLocationLocation());
                        row.setCell(0,data.getInventoryItemName());
                        store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                        row.setCell(1,store.getEntry());
                        row.setCell(2,location);
                        row.setCell(3,data.getQuantityOnhand());

                        row.setData(data);                           

                        model.add(row);
                    }
                    fromItemName.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });
        
        toItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int r;
                Integer id;
                InventoryItemDO fromData, data;
                Item<Integer> row;
                ArrayList<InventoryItemDO> list;
                ArrayList<Item<Integer>> model;
                DictionaryDO store, units;
                Query query;
                QueryData field;
                QueryFieldUtil parser;

                window.clearStatus();

                r = receiptTable.getSelectedRow();
                fromData = manager.getFromInventoryItemAt(r); 
                
                id = fromData.getId();                
                query = new Query();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                field = new QueryData();
                field.key = InventoryItemMeta.getName();
                field.type = QueryData.Type.STRING;
                field.query = parser.getParameter().get(0);
                query.setFields(field);
                
                field = new QueryData();
                field.key = InventoryItemMeta.getParentInventoryItemId();
                field.type = QueryData.Type.INTEGER;                
                field.query = id.toString();           
                query.setFields(field);
                try {
                    list = inventoryItemService.callList("fetchActiveByNameStoreAndParentInventoryItem", query);
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        store = DictionaryCache.getEntryFromId(data.getStoreId());
                        units = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                        row = new Item<Integer>(data.getId(), data.getName(), 
                                               store.getEntry(), units.getEntry());
                        row.setData(data);
                        model.add(row);
                    }
                    toItemName.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });
        
        toStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i, r;
                Integer itemId;
                String param, location;
                InventoryItemDO data;
                ArrayList<InventoryLocationViewDO> invLocList; 
                InventoryLocationViewDO invLoc;
                ArrayList<StorageLocationViewDO> storLocList; 
                StorageLocationViewDO storLoc;
                Item<Integer> row;                
                ArrayList<Item<Integer>> model;
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                
                r = receiptTable.getSelectedRow();                          
                                              
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
                model = new ArrayList<Item<Integer>>();
                try {
                    data = manager.getToInventoryItemAt(r);
                    if(data == null) {
                        com.google.gwt.user.client.Window.alert(consts.get("selToItem"));
                        window.clearStatus();
                        return;
                    }
                    
                    itemId = data.getId();
                    if ("Y".equals(manager.getAddtoExistingAt(r))) {   
                        fields = new ArrayList<QueryData>();
                        query = new Query();                                                                                                     
                        
                        field = new QueryData();
                        field.query = param;
                        fields.add(field);

                        field = new QueryData();
                        field.query = Integer.toString(itemId);
                        fields.add(field);

                        query.setFields(fields);
                        invLocList = inventoryLocationService.callList("fetchByLocationNameInventoryItemId", query);
                        for (i = 0; i < invLocList.size(); i++ ) {
                            row = new Item<Integer>(4);
                            invLoc = invLocList.get(i);

                            row.setKey(invLoc.getId());
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            row.setCell(0,location);
                            row.setCell(2,invLoc.getQuantityOnhand());
                            row.setCell(3,invLoc.getExpirationDate());

                            row.setData(invLoc);                           

                            model.add(row);
                        }
                    } else {
                        storLocList = storageService.callList("fetchAvailableByName", param);
                        for (i = 0; i < storLocList.size(); i++ ) {
                            row = new Item<Integer>(4);
                            storLoc = storLocList.get(i);
                            row.setKey(-1);
                            location = StorageLocationManager.getLocationForDisplay(storLoc.getName(),
                                                                                    storLoc.getStorageUnitDescription(),
                                                                                    storLoc.getLocation());
                            row.setCell(0,location);
                            invLoc = new InventoryLocationViewDO();
                            invLoc.setStorageLocationId(storLoc.getId());
                            invLoc.setStorageLocationName(storLoc.getName());
                            invLoc.setStorageLocationUnitDescription(storLoc.getStorageUnitDescription());
                            invLoc.setStorageLocationLocation(storLoc.getLocation());
                            row.setData(invLoc);

                            model.add(row);
                        }
                    }
                    toStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }                                   
        });
        
        receiptTable.addSelectionHandler(new SelectionHandler(){
            public void onSelection(SelectionEvent event) {
                reloadTable = false;
                DataChangeEvent.fire(screen);
            }
            
        });
        
        receiptTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                InventoryItemDO fromData,toData;
                
                r = event.getRow();
                c = event.getCol();
                
                fromData = manager.getFromInventoryItemAt(r);
                toData = manager.getToInventoryItemAt(r);
                if (state != State.ADD)  
                    event.cancel();                
                
                switch(c) {
                    case 4:
                        if (fromData == null) {
                            com.google.gwt.user.client.Window.alert(consts.get("selFromItem"));
                            event.cancel();                                                                       
                        }
                        break;
                    case 5:
                        if (toData == null) {
                            com.google.gwt.user.client.Window.alert(consts.get("selToItem"));
                            event.cancel();
                            return;                                            
                        }
                        if ("N".equals(toData.getIsBulk()))
                            event.cancel();
                        break;
                    case 6:
                        if (toData == null) {
                            com.google.gwt.user.client.Window.alert(consts.get("selToItem")); 
                            event.cancel();                                            
                        }                        
                        break;                   
                }
            }            
        });       

        receiptTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String location;
                AutoCompleteValue av;
                InventoryLocationViewDO data, tempData;
                Object val;

                r = event.getRow();
                c = event.getCol();
                val = receiptTable.getValueAt(r,c);
                
                switch(c){
                    case 0:
                        av = (AutoCompleteValue)val;
                        if (av != null) {      
                            data = (InventoryLocationViewDO)av.getData();
                            try {
                                manager.setFromInventoryItemAt(InventoryItemCache.getActiveInventoryItemFromId(data.getInventoryItemId()), r);
                            } catch (Exception e) {
                                com.google.gwt.user.client.Window.alert(e.getMessage());
                                e.printStackTrace();
                            }
                            manager.setFromInventoryLocationAt(data, r);                                                  
                            location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                    data.getStorageLocationUnitDescription(),
                                                                                    data.getStorageLocationLocation());
                            receiptTable.setValueAt(r, 1, location);
                            receiptTable.setValueAt(r, 2, data.getQuantityOnhand());
                            //
                            // we clear field required exceptions from the 2nd
                            // column because it can't be edited 
                            //
                            receiptTable.clearExceptions(r, 1);
                            
                            tempData = manager.getToInventoryLocationAt(r);     
                            if (tempData != null) {
                                tempData.setLotNumber(data.getLotNumber());
                                tempData.setExpirationDate(data.getExpirationDate());
                            }
                        } else { 
                            manager.setFromInventoryItemAt(null, r);
                            receiptTable.setValueAt(r, 1, null);
                            receiptTable.setValueAt(r, 2, null);
                        }
                        reloadTable = false;
                        //
                        // this is done in order to refresh the data in all the 
                        // widgets that are loaded from "from" inventory item/location
                        //
                        DataChangeEvent.fire(screen);
                        break;
                    case 3:
                        manager.setQuantityAt((Integer)val, r);
                        break;                        
                    case 4:
                        av = (AutoCompleteValue)val;
                        if (av != null) 
                            manager.setToInventoryItemAt((InventoryItemDO)av.getData(), r);
                        else 
                            manager.setToInventoryItemAt(null, r);
                        receiptTable.setValueAt(r, 5, "N");
                        reloadTable = false;
                        //
                        // this is done in order to refresh the data in all the 
                        // widgets that are loaded from "to" inventory item
                        //
                        DataChangeEvent.fire(screen);
                        break;
                    case 5:
                        manager.setAddtoExistingAt((String)val,r);
                        break;
                    case 6:
                        av = (AutoCompleteValue)val;
                        if (av != null) {
                            data = (InventoryLocationViewDO)av.getData();
                            tempData =  manager.getFromInventoryLocationAt(r);
                            if (tempData != null) {
                                data.setLotNumber(tempData.getLotNumber());
                                data.setExpirationDate(tempData.getExpirationDate());
                            }
                            
                            manager.setToInventoryLocationAt(data, r);                            
                        } else { 
                            manager.setToInventoryLocationAt(null, r);
                        }
                        reloadTable = false;
                        //
                        // this is done in order to refresh the data in all the 
                        // widgets that are loaded from "to" inventory location
                        //
                        DataChangeEvent.fire(screen);
                        break;                   
                }
            }
        });

        receiptTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.addTransfer();
            }
        });
        
        receiptTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeTransferAt(event.getIndex());
            }
        });
        
        addReceiptButton = (Button)def.getWidget("addReceiptButton");
        addScreenHandler(addReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;  
                
                receiptTable.addRow();                
                n = receiptTable.getRowCount() - 1;
                receiptTable.selectRowAt(n);
                receiptTable.scrollToVisible(n);
                receiptTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReceiptButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        removeReceiptButton = (Button)def.getWidget("removeReceiptButton");
        addScreenHandler(removeReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = receiptTable.getSelectedRow();
                if (r > -1 && receiptTable.getRowCount() > 0) 
                    receiptTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReceiptButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        inventoryItemDescription = (TextBox)def.getWidget("inventoryItemDescription");
        addScreenHandler(inventoryItemDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;                
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {
                    data = manager.getFromInventoryItemAt(r);
                    if (data != null)
                        inventoryItemDescription.setValue(data.getDescription());
                    else 
                        inventoryItemDescription.setValue(null);
                } else {
                    inventoryItemDescription.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemDescription.setEnabled(false);
            }
        });

        inventoryItemStoreId = (TextBox)def.getWidget("inventoryItemStoreId");
        addScreenHandler(inventoryItemStoreId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;
                DictionaryDO dict;
                
                r = receiptTable.getSelectedRow();
                
                try {
                    if (r > -1) {
                        data = manager.getFromInventoryItemAt(r);
                        if (data != null) {
                            dict = DictionaryCache.getEntryFromId(data.getStoreId());
                            inventoryItemStoreId.setValue(dict.getEntry());
                        } else {
                            inventoryItemStoreId.setValue(null);
                        }
                    } else {
                        inventoryItemStoreId.setValue(null);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemStoreId.setEnabled(false);
            }
        });

        inventoryItemDispensedUnitsId = (TextBox)def.getWidget("inventoryItemDispensedUnitsId");
        addScreenHandler(inventoryItemDispensedUnitsId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;
                DictionaryDO dict;
                
                r = receiptTable.getSelectedRow();
                
                try {
                    if (r > -1) {
                        data = manager.getFromInventoryItemAt(r);
                        if (data != null) {
                            dict = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                            inventoryItemDispensedUnitsId.setValue(dict.getEntry());
                        } else {
                            inventoryItemDispensedUnitsId.setValue(null);
                        }
                    } else {
                        inventoryItemDispensedUnitsId.setValue(null);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemDispensedUnitsId.setEnabled(false);
            }
        });

        inventoryLocationLotNumber = (TextBox)def.getWidget("inventoryLocationLotNumber");
        addScreenHandler(inventoryLocationLotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;                
                InventoryLocationViewDO data;
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {
                    data = manager.getFromInventoryLocationAt(r);
                    if (data != null)
                        inventoryLocationLotNumber.setValue(data.getLotNumber());
                    else 
                        inventoryLocationLotNumber.setValue(null);
                } else {
                    inventoryLocationLotNumber.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryLocationLotNumber.setEnabled(false);
            }
        });

        inventoryLocationExpirationDate = (TextBox)def.getWidget("inventoryLocationExpirationDate");
        addScreenHandler(inventoryLocationExpirationDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryLocationViewDO data;
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {
                    data = manager.getFromInventoryLocationAt(r);
                    if (data != null)
                        inventoryLocationExpirationDate.setValue(data.getExpirationDate());
                    else 
                        inventoryLocationExpirationDate.setValue(null);
                } else {
                    inventoryLocationExpirationDate.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryLocationExpirationDate.setEnabled(false);
            }
        });

        toDescription = (TextBox)def.getWidget("toDescription");
        addScreenHandler(toDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {
                    data = manager.getToInventoryItemAt(r);
                    if (data != null)
                        toDescription.setValue(data.getDescription());
                    else 
                        toDescription.setValue(null);
                } else {
                    toDescription.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toDescription.setEnabled(false);
            }
        });

        toStoreId = (TextBox)def.getWidget("toStoreId");
        addScreenHandler(toStoreId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;
                DictionaryDO dict;
                
                r = receiptTable.getSelectedRow();
                
                try {
                    if (r > -1) {
                        data = manager.getToInventoryItemAt(r);
                        if (data != null) {
                            dict = DictionaryCache.getEntryFromId(data.getStoreId());
                            toStoreId.setValue(dict.getEntry());
                        } else {
                            toStoreId.setValue(null);
                        }
                    } else {
                        toStoreId.setValue(null);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }            

            public void onStateChange(StateChangeEvent<State> event) {
                toStoreId.setEnabled(false);
            }
        });

        toDispensedUnits = (TextBox)def.getWidget("toDispensedUnits");
        addScreenHandler(toDispensedUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryItemDO data;
                DictionaryDO dict;
                
                r = receiptTable.getSelectedRow();
                
                try {
                    if (r > -1) {
                        data = manager.getToInventoryItemAt(r);
                        if (data != null) {
                            dict = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                            toDispensedUnits.setValue(dict.getEntry());
                        } else {
                            toDispensedUnits.setValue(null);
                        }
                    } else {
                        toDispensedUnits.setValue(null);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toDispensedUnits.setEnabled(false);
            }
        });

        toLotNumber = (TextBox)def.getWidget("toLotNumber");
        addScreenHandler(toLotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryLocationViewDO data;
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {
                    data = manager.getToInventoryLocationAt(r);
                    if (data != null)
                        toLotNumber.setValue(data.getLotNumber());
                    else 
                        toLotNumber.setValue(null);
                } else {
                    toLotNumber.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toLotNumber.setEnabled(false);
            }
        });

        toExpDate = (TextBox)def.getWidget("toExpDate");
        addScreenHandler(toExpDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                int r;
                InventoryLocationViewDO data;
                
                r = receiptTable.getSelectedRow();
                
                if (r > -1) {                   
                    data = manager.getToInventoryLocationAt(r);
                    if (data != null)
                        toExpDate.setValue(data.getExpirationDate());
                    else 
                        toExpDate.setValue(null);
                } else {
                    toExpDate.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toExpDate.setEnabled(false);
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
        
        screen = this;        
    }
    
    public void loadTransferData(InventoryTransferManager manager) {
        add(manager);
    } 
    
    protected void add(InventoryTransferManager manager) { 
        if (manager == null) {
            this.manager = InventoryTransferManager.getInstance();            
        } else {
            this.manager = manager;
        }
        
        reloadTable = true;
        setState(State.ADD);
        DataChangeEvent.fire(this);
        
        window.setDone(consts.get("enterInformationPressCommit"));        
    }
    
    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
                
                if (!openedFromMenu)                     
                    window.close();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
                e.printStackTrace();
                window.clearStatus();
            }
        }
        
    }
    
    protected void abort() {        
        setFocus(null);        
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        
        if (state == State.QUERY) {            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {            
            reloadTable = true;
            manager = InventoryTransferManager.getInstance();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
            
            if (!openedFromMenu)                     
                window.close();
        }        
    }    
    
    private ArrayList<Row> getTransferModel() {
        String location;
        ArrayList<Row> model;
        InventoryItemDO fromItem, toItem;
        InventoryLocationViewDO fromLoc, toLoc;
        Row row;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            for (int i = 0; i < manager.count(); i++) {
                row = new Row(7);                   
                fromItem = manager.getFromInventoryItemAt(i);                             
                if (fromItem != null)                    
                    row.setCell(0,new AutoCompleteValue(fromItem.getId(), fromItem.getName()));
                    
                fromLoc = manager.getFromInventoryLocationAt(i);
                if (fromLoc != null) {
                    location = StorageLocationManager.getLocationForDisplay(fromLoc.getStorageLocationName(),
                                                                            fromLoc.getStorageLocationUnitDescription(),
                                                                            fromLoc.getStorageLocationLocation());
                    row.setCell(1,location);
                    row.setCell(2,fromLoc.getQuantityOnhand());
                }
                
                row.setCell(3,manager.getQuantityAt(i));
                
                toItem = manager.getToInventoryItemAt(i);                
                if (toItem != null)                    
                    row.setCell(4,new AutoCompleteValue(toItem.getId(), toItem.getName()));
                
                row.setCell(5,manager.getAddtoExistingAt(i));
                
                toLoc = manager.getToInventoryLocationAt(i);
                if (toLoc != null) {
                    location = StorageLocationManager.getLocationForDisplay(toLoc.getStorageLocationName(),
                                                                            toLoc.getStorageLocationUnitDescription(),
                                                                            toLoc.getStorageLocationLocation());
                    row.setCell(6,new AutoCompleteValue(toLoc.getId(), location));
                }
                
                
                
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
}  

