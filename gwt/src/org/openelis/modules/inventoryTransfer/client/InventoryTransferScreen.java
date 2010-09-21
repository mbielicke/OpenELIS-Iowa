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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
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
import com.google.gwt.user.client.Window;

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
    private AutoComplete<Integer>                 fromItemName, toItemName, toStorageLocationName;
    private AppButton                             addButton, commitButton, abortButton,
                                                  addReceiptButton, removeReceiptButton;
    private TableWidget                           receiptTable;
    private ScreenService                         inventoryItemService, inventoryLocationService, 
                                                  storageService;    
    
    public InventoryTransferScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryTransferDef.class));

        InventoryTransferScreenImpl(true);
    }
    
    public InventoryTransferScreen(ScreenWindow window) throws Exception {
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
        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
            }
        });         

        receiptTable = (TableWidget)def.getWidget("receiptTable");
        addScreenHandler(receiptTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                // this is done in order to prevent the table from getting refreshed
                // when DataChangeEvent is fired to refresh the widgets that are
                // loaded from the "from/to" inventory item/location when one of
                // these is set in one of the rows in the table
                //
                if(reloadTable)      
                    receiptTable.load(getTransferModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receiptTable.enable(true);
                receiptTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        fromItemName = (AutoComplete<Integer>)receiptTable.getColumnWidget("fromItemName");
        toItemName = (AutoComplete<Integer>)receiptTable.getColumnWidget("toItemName");
        toStorageLocationName = (AutoComplete<Integer>)receiptTable.getColumnWidget("toLoc"); 
        
        fromItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String location;
                InventoryLocationViewDO data;
                TableDataRow row;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<TableDataRow> model;                
                DictionaryDO store;

                try {
                    list = inventoryLocationService.callList("fetchByInventoryItemName", event.getMatch());
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                data.getStorageLocationUnitDescription(),
                                                                                data.getStorageLocationLocation());
                        row.cells.get(0).setValue(data.getInventoryItemName());
                        store = DictionaryCache.getEntryFromId(data.getInventoryItemStoreId());
                        row.cells.get(1).setValue(store.getEntry());
                        row.cells.get(2).setValue(location);
                        row.cells.get(3).setValue(data.getQuantityOnhand());

                        row.data = data;                           

                        model.add(row);
                    }
                    fromItemName.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        toItemName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int r;
                Integer id;
                InventoryItemDO fromData, data;
                TableDataRow row;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;
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
                parser.parse(event.getMatch());

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
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        store = DictionaryCache.getEntryFromId(data.getStoreId());
                        units = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                        row = new TableDataRow(data.getId(), data.getName(), 
                                               store.getEntry(), units.getEntry());
                        row.data = data;
                        model.add(row);
                    }
                    toItemName.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                TableDataRow row;                
                ArrayList<TableDataRow> model;
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                
                r = receiptTable.getSelectedRow();                          
                                              
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());  
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
                model = new ArrayList<TableDataRow>();
                try {
                    data = manager.getToInventoryItemAt(r);
                    if(data == null) {
                        Window.alert(consts.get("selToItem"));
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
                            row = new TableDataRow(4);
                            invLoc = invLocList.get(i);

                            row.key = invLoc.getId();
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            row.cells.get(0).setValue(location);
                            row.cells.get(2).setValue(invLoc.getQuantityOnhand());
                            row.cells.get(3).setValue(invLoc.getExpirationDate());

                            row.data = invLoc;                           

                            model.add(row);
                        }
                    } else {
                        storLocList = storageService.callList("fetchAvailableByName", param);
                        for (i = 0; i < storLocList.size(); i++ ) {
                            row = new TableDataRow(4);
                            storLoc = storLocList.get(i);
                            row.key = -1;
                            location = StorageLocationManager.getLocationForDisplay(storLoc.getName(),
                                                                                    storLoc.getStorageUnitDescription(),
                                                                                    storLoc.getLocation());
                            row.cells.get(0).setValue(location);
                            invLoc = new InventoryLocationViewDO();
                            invLoc.setStorageLocationId(storLoc.getId());
                            invLoc.setStorageLocationName(storLoc.getName());
                            invLoc.setStorageLocationUnitDescription(storLoc.getStorageUnitDescription());
                            invLoc.setStorageLocationLocation(storLoc.getLocation());
                            row.data = invLoc;

                            model.add(row);
                        }
                    }
                    toStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
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
                            Window.alert(consts.get("selFromItem"));
                            event.cancel();                                                                       
                        }
                        break;
                    case 5:
                        if (toData == null) {
                            Window.alert(consts.get("selToItem"));
                            event.cancel();
                            return;                                            
                        }
                        if ("N".equals(toData.getIsBulk()))
                            event.cancel();
                        break;
                    case 6:
                        if (toData == null) {
                            Window.alert(consts.get("selToItem")); 
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
                TableDataRow row;
                InventoryLocationViewDO data, tempData;
                Object val;

                r = event.getRow();
                c = event.getCol();
                val = receiptTable.getObject(r,c);
                
                switch(c){
                    case 0:
                        row = (TableDataRow)val;
                        if (row != null) {      
                            data = (InventoryLocationViewDO)row.data;
                            try {
                                manager.setFromInventoryItemAt(InventoryItemCache.getActiveInventoryItemFromId(data.getInventoryItemId()), r);
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                e.printStackTrace();
                            }
                            manager.setFromInventoryLocationAt(data, r);                                                  
                            location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                    data.getStorageLocationUnitDescription(),
                                                                                    data.getStorageLocationLocation());
                            receiptTable.setCell(r, 1, location);
                            receiptTable.setCell(r, 2, data.getQuantityOnhand());
                            //
                            // we clear field required exceptions from the 2nd
                            // column because it can't be edited 
                            //
                            receiptTable.clearCellExceptions(r, 1);
                            
                            tempData = manager.getToInventoryLocationAt(r);     
                            if (tempData != null) {
                                tempData.setLotNumber(data.getLotNumber());
                                tempData.setExpirationDate(data.getExpirationDate());
                            }
                        } else { 
                            manager.setFromInventoryItemAt(null, r);
                            receiptTable.setCell(r, 1, null);
                            receiptTable.setCell(r, 2, null);
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
                        row = (TableDataRow)val;
                        if (row != null) 
                            manager.setToInventoryItemAt((InventoryItemDO)row.data, r);
                        else 
                            manager.setToInventoryItemAt(null, r);
                        receiptTable.setCell(r, 5, "N");
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
                        row = (TableDataRow)val;
                        if (row != null) {
                            data = (InventoryLocationViewDO)row.data;
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
        
        addReceiptButton = (AppButton)def.getWidget("addReceiptButton");
        addScreenHandler(addReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;  
                
                receiptTable.addRow();                
                n = receiptTable.numRows() - 1;
                receiptTable.selectRow(n);
                receiptTable.scrollToSelection();
                receiptTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReceiptButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        removeReceiptButton = (AppButton)def.getWidget("removeReceiptButton");
        addScreenHandler(removeReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = receiptTable.getSelectedRow();
                if (r > -1 && receiptTable.numRows() > 0) 
                    receiptTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReceiptButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
                inventoryItemDescription.enable(false);
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
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemStoreId.enable(false);
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
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemDispensedUnitsId.enable(false);
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
                inventoryLocationLotNumber.enable(false);
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
                inventoryLocationExpirationDate.enable(false);
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
                toDescription.enable(false);
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
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }            

            public void onStateChange(StateChangeEvent<State> event) {
                toStoreId.enable(false);
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
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toDispensedUnits.enable(false);
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
                toLotNumber.enable(false);
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
                toExpDate.enable(false);
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
                Window.alert("commitAdd(): " + e.getMessage());
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
    
    private ArrayList<TableDataRow> getTransferModel() {
        String location;
        ArrayList<TableDataRow> model;
        InventoryItemDO fromItem, toItem;
        InventoryLocationViewDO fromLoc, toLoc;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            for (int i = 0; i < manager.count(); i++) {
                row = new TableDataRow(7);                   
                fromItem = manager.getFromInventoryItemAt(i);                             
                if (fromItem != null)                    
                    row.cells.get(0).setValue(new TableDataRow(fromItem.getId(), fromItem.getName()));
                    
                fromLoc = manager.getFromInventoryLocationAt(i);
                if (fromLoc != null) {
                    location = StorageLocationManager.getLocationForDisplay(fromLoc.getStorageLocationName(),
                                                                            fromLoc.getStorageLocationUnitDescription(),
                                                                            fromLoc.getStorageLocationLocation());
                    row.cells.get(1).setValue(location);
                    row.cells.get(2).setValue(fromLoc.getQuantityOnhand());
                }
                
                row.cells.get(3).setValue(manager.getQuantityAt(i));
                
                toItem = manager.getToInventoryItemAt(i);                
                if (toItem != null)                    
                    row.cells.get(4).setValue(new TableDataRow(toItem.getId(), toItem.getName()));
                
                row.cells.get(5).setValue(manager.getAddtoExistingAt(i));
                
                toLoc = manager.getToInventoryLocationAt(i);
                if (toLoc != null) {
                    location = StorageLocationManager.getLocationForDisplay(toLoc.getStorageLocationName(),
                                                                            toLoc.getStorageLocationUnitDescription(),
                                                                            toLoc.getStorageLocationLocation());
                    row.cells.get(6).setValue(new TableDataRow(toLoc.getId(), location));
                }
                
                
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
}  

