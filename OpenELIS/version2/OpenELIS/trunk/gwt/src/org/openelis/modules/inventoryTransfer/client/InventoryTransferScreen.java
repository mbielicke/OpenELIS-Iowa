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
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
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

    private SecurityModule                        security;
    private InventoryTransferScreen               screen;
    private InventoryTransferManager              manager;
    private boolean                               openedFromMenu, reloadTable;
    private TextBox                               inventoryItemDescription, inventoryItemStoreId, inventoryItemDispensedUnitsId,
                                                  inventoryLocationLotNumber, inventoryLocationExpirationDate, toDescription,
                                                  toStoreId, toDispensedUnits, toLotNumber, toExpDate;
    private AutoComplete<Integer>                 fromItemName, toItemName, toStorageLocationName;
    private AppButton                             queryButton, addButton, commitButton,
                                                  abortButton, addReceiptButton, removeReceiptButton;
    private TableWidget                           receiptTable;
    private ScreenService                         inventoryItemService, inventoryLocationService, storageService;    

    private HashMap<Integer, InventoryItemViewDO> inventoryItemMap;
    
    public InventoryTransferScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryTransferDef.class));
        init();
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
        openedFromMenu = true;
    }
    
    public InventoryTransferScreen(ScreenWindow window) throws Exception {
        super((ScreenDefInt)GWT.create(InventoryTransferDef.class));
        init();
        this.window = window;
        postConstructor();
        openedFromMenu = false;
    }
    
    private void init() throws Exception {
        service = new ScreenService("controller?service=org.openelis.modules.inventoryTransfer.server.InventoryTransferService");
        
        security = OpenELIS.security.getModule("inventorytransfer");
        if (security == null)
            throw new SecurityException("screenPermException", "Inventory Transfer Screen");
        
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");        
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
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

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasAddPermission());
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
                if(reloadTable) 
                    //receiptTable.load(receiptModel);       
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
                InventoryItemDO fromData,toData, data;
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
                toData = manager.getToInventoryItemAt(r);
                if (fromData == null) {
                    if (toData == null) {
                        Window.alert(consts.get("selFromItem"));                    
                        return;
                    } else {
                        id = toData.getId();
                    }
                } else {
                    id = fromData.getId();
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
                    if(manager.getToInventoryItemAt(r) == null)
                        return;
                    data = manager.getToInventoryItemAt(r);
                    itemId = data.getId();
                    if ("Y".equals(manager.getAddtoExistingAt(r)) && itemId != null) {   
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
                InventoryItemDO data;
                
                r = event.getRow();
                c = event.getCol();
                
                if (state != State.ADD) { 
                    event.cancel();
                } else if (c == 4){
                    data = manager.getToInventoryItemAt(r);
                    if (data == null || "N".equals(data.getIsBulk()))
                        event.cancel();
                } 
            }            
        });

        receiptTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String location;
                TableDataRow row;
                InventoryLocationViewDO data;
                Object val;

                r = event.getRow();
                c = event.getCol();
                val = receiptTable.getObject(r,c);
                
                switch(c){
                    case 0:
                        row = (TableDataRow)val;
                        if (row != null) {      
                            data = (InventoryLocationViewDO)row.data;
                            manager.setFromInventoryItemAt(getInventoryItem(data.getInventoryItemId()), r);
                            manager.setFromInventoryLocationAt(data, r);
                            location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                                    data.getStorageLocationUnitDescription(),
                                                                                    data.getStorageLocationLocation());
                            receiptTable.setCell(r, 1, location);
                            receiptTable.setCell(r, 2, data.getQuantityOnhand());
                        } else { 
                            manager.setFromInventoryItemAt(null, r);
                            receiptTable.setCell(r, 1, null);
                            receiptTable.setCell(r, 2, null);
                        }
                        reloadTable = false;
                        DataChangeEvent.fire(screen);
                        break;
                    case 3:
                        row = (TableDataRow)val;
                        if (row != null) 
                            manager.setToInventoryItemAt((InventoryItemDO)row.data, r);
                        else 
                            manager.setToInventoryItemAt(null, r);
                        receiptTable.setCell(r, 4, "N");
                        reloadTable = false;
                        DataChangeEvent.fire(screen);
                        break;
                    case 4:
                        manager.setAddtoExistingAt((String)val,r);
                        break;
                    case 5:
                        row = (TableDataRow)val;
                        if (row != null) 
                            manager.setToInventoryLocationAt((InventoryLocationViewDO)row.data, r);
                        else 
                            manager.setToInventoryLocationAt(null, r);
                        reloadTable = false;
                        DataChangeEvent.fire(screen);
                        break;
                    case 6:
                        manager.setQuantityAt((Integer)val, r);
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
        
        screen = this;        
        inventoryItemMap = new HashMap<Integer, InventoryItemViewDO>();
    }
    
    public void loadTransferData(InventoryTransferManager manager) {
        add(manager);
    } 

    protected void query() {        
        
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

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            executeQuery(query); 
        } else if (state == State.ADD) {
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
        manager = InventoryTransferManager.getInstance(); 
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        
        if (state == State.QUERY) {            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {            
            reloadTable = true;
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
            
            if (!openedFromMenu)                     
                window.close();
        }        
    }
    
    private InventoryItemViewDO getInventoryItem(Integer id) {
        InventoryItemViewDO data;         
                    
        data = inventoryItemMap.get(id);
        if (data == null && id != null) {
            try {
                data  = inventoryItemService.call("fetchInventoryItemById", id);                
                inventoryItemMap.put(id, data);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert(e.getMessage());
            }
        }
        
        return data;        
    }  
    
    private void executeQuery(Query query) {                
        //window.setBusy(consts.get("querying"));
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
                
                toItem = manager.getToInventoryItemAt(i);
                if (toItem != null)                    
                    row.cells.get(3).setValue(new TableDataRow(toItem.getId(), toItem.getName()));
                
                row.cells.get(4).setValue(manager.getAddtoExistingAt(i));
                
                toLoc = manager.getToInventoryLocationAt(i);
                if (toLoc != null) {
                    location = StorageLocationManager.getLocationForDisplay(toLoc.getStorageLocationName(),
                                                                            toLoc.getStorageLocationUnitDescription(),
                                                                            toLoc.getStorageLocationLocation());
                    row.cells.get(5).setValue(new TableDataRow(toLoc.getId(), location));
                }
                
                row.cells.get(6).setValue(manager.getQuantityAt(i));
                
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
    
    private ResultsTable receiptsTable;
    private QueryTable receiptsQueryTable;
    
    private AppButton        removeReceiptButton;
    private TextBox orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText,
                    itemDescText, itemStoreText, itemDisUnitsText, itemLotNum, transItemExpDate,
                    toItemDescText, toItemStoreText, toItemDisUnitsText, toItemLotNum,
                    toItemExpDate;
    private ScreenAutoCompleteWidget itemLocation;
    private ScreenCalendar recItemExpDate;
    private ScreenCheck addToExisiting;
    private ButtonPanel      atozButtons;
    private KeyListManager   keyList = new KeyListManager();
    private String screenType;
    private Integer currentEditingRow = -1;
    private Integer lastLocValue = null;
    private InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    public InventoryReceiptScreen() {     
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");
        
        screenType = "receipt";
      
        InventoryReceiptForm receiptRPC = new InventoryReceiptForm();
        receiptRPC.screenType = screenType;
        query = new InventoryReceiptQuery();
        query.type = screenType;
        
        getScreen(receiptRPC);
    }
    
    public InventoryReceiptScreen(Object[] args) {                
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");
        
        screenType = (String)((StringObject)args[0]).getValue();
        
        InventoryReceiptForm receiptRPC = new InventoryReceiptForm();
        receiptRPC.screenType = screenType;
        query = new InventoryReceiptQuery();
        query.type = screenType;
        
        getScreen(receiptRPC);
    }

    public void performCommand(Enum action, Object obj) {
        if(action == KeyListManager.Action.FETCH){
            if(state == State.DISPLAY || state == State.DEFAULT){
                if(keyList.getList().size() > 0)
                    setState(State.DISPLAY);
                else
                    setState(State.DEFAULT);
            }
        }else if(action == KeyListManager.Action.SELECTION){
            //do nothing for now
        }else if(action == KeyListManager.Action.GETPAGE){
            //do nothing for now
        }else{
            super.performCommand(action, obj);
        }
    } 

    
    public void onClick(Widget sender) {
        if (sender == removeReceiptButton)
        onRemoveReceiptRowButtonClick();
        else if(sender == addToExisiting && addToExisiting.isEnabled() && receiptsTable.model.getSelectedIndex() > -1 && receiptsTable.model.numRows() > 0){
            TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
            InvReceiptItemInfoForm hiddenRPC = tableRow.key;
        
        if(((CheckBox)addToExisiting.getWidget()).getState() == CheckBox.CHECKED)
            hiddenRPC.addToExisting.setValue(CheckBox.UNCHECKED);

        else
            hiddenRPC.addToExisting.setValue(CheckBox.CHECKED);
        
        ((AutoComplete)itemLocation.getWidget()).setSelections(null);
        hiddenRPC.storageLocationId.clear();
        }
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        
        if(receiptsTable.model.numRows() > 0){
            if(sender == itemLocation.getWidget()){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                hiddenRPC.storageLocationId.setValue(((AutoComplete)itemLocation.getWidget()).getSelections());
                
            }else if(sender == itemLotNum){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                hiddenRPC.lotNumber.setValue(((TextBox)itemLotNum).getText());
                
            }else if(sender == recItemExpDate.getWidget()){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                hiddenRPC.expirationDate.setValue((String)((CalendarLookUp)recItemExpDate.getWidget()).getValue().toString());
                
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        orgAptSuiteText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());        
        orgAddressText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        itemDescText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getDescription());
        itemStoreText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getStoreId());
        itemDisUnitsText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getDispensedUnitsId());
        itemLocation = (ScreenAutoCompleteWidget)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
        itemLotNum = (TextBox)getWidget(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber());
        toItemDescText = (TextBox)getWidget("toDescription");
        toItemStoreText = (TextBox)getWidget("toStoreId");
        toItemDisUnitsText = (TextBox)getWidget("toDispensedUnits");
        toItemLotNum = (TextBox)getWidget("toLotNumber");
        toItemExpDate = (TextBox)getWidget("toExpDate");
        
        if("receipt".equals(screenType))
            recItemExpDate = (ScreenCalendar)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
        else
            transItemExpDate = (TextBox)getWidget(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
            
        addToExisiting = (ScreenCheck)widgets.get("addToExisting");
        removeReceiptButton = (AppButton)getWidget("removeReceiptButton");
        
        //
        // disable auto add and make sure there are no rows in the table
        //
        ScreenResultsTable sw = (ScreenResultsTable)widgets.get("receiptsTable");
        receiptsTable = (ResultsTable)sw.getWidget();
        receiptsTable.model.enableAutoAdd(false);
        receiptsTable.table.addTableWidgetListener(this);
        receiptsTable.model.addTableModelListener(this);
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(receiptsTable);
        
        updateChain.add(afterUpdate);
        commitUpdateChain.add(afterCommitUpdate);
        commitAddChain.add(afterCommitAdd);
        
        super.afterDraw(sucess);
    }
    
    protected SyncCallback afterUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            if("transfer".equals(screenType)){
                removeReceiptButton.changeState(ButtonState.DISABLED);
            }
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void add() {
        super.add();
        
        receiptsTable.model.enableAutoAdd(true);
        
        
        if("receipt".equals(screenType)){
            addToExisiting.enable(false);
            recItemExpDate.enable(false);
            itemLotNum.setReadOnly(true);
            itemLocation.enable(false);
        }
        
        receiptsTable.table.select(0, 0);
        
        //TODO not sure how to replace this  receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 0);
       
    }
    
    public void query() {
        super.query();
        
        if("receipt".equals(screenType)){
        addToExisiting.enable(false);
        recItemExpDate.enable(false);
        itemLotNum.setReadOnly(true);
        itemLocation.enable(false);
        }
        removeReceiptButton.changeState(AppButton.ButtonState.DISABLED);
        //receiptsQueryTable.select(0,0);
        
    }
    
    public void update() {
        window.setBusy(consts.get("lockForUpdate"));
        
        form.tableRows = keyList.getList(); 
        
        screenService.call("commitQueryAndLock", form, new AsyncCallback<InventoryReceiptForm>(){
            public void onSuccess(InventoryReceiptForm result){           
                resetForm();
                load();
                //keyList.setModel(result.tableRows);
                //keyList.select(0);
                
                enable(true);
                window.setDone(consts.get("updateFields"));
                setState(State.UPDATE);
                receiptsTable.model.load(result.tableRows);
                receiptsTable.table.select(0, 0);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    public void abort() {
        receiptsTable.table.finishEditing();
        receiptsTable.model.enableAutoAdd(false);
        
        if (state == State.UPDATE) {
            window.setBusy();
            clearErrors();
            resetForm();
            load();
            enable(false);

            form.tableRows = keyList.getList(); 
            
            screenService.call("commitQueryAndUnlock", form, new AsyncCallback<InventoryReceiptForm>(){
                public void onSuccess(InventoryReceiptForm result){                    
                    receiptsTable.model.load(result.tableRows);

                    window.clearStatus();
                    setState(State.DISPLAY);
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.clearStatus();
               }
            });
            
        }else if(state == State.ADD){
            if(screenType.equals("receipt")){
                //unlock the order records
                InvReceiptItemInfoForm iriif = new InvReceiptItemInfoForm();
                iriif.lockedIds = getLockedSetsFromTable();

                screenService.call("unlockOrders", iriif, new AsyncCallback() {
                    public void onSuccess(Object result) {
                        superAbort();
                        
                   }

                   public void onFailure(Throwable caught) {
                       Window.alert(caught.getMessage());
                   }
                    });
            }else{
                //unlock the loc records
                InvReceiptItemInfoForm iriif = new InvReceiptItemInfoForm();
                iriif.lockedIds = getLockedSetsFromTable();

                screenService.call("unlockLocations", iriif, new AsyncCallback() {
                    public void onSuccess(Object result) {
                        superAbort();
                        
                   }

                   public void onFailure(Throwable caught) {
                       Window.alert(caught.getMessage());
                   }
                    });
            }
        }else{
            super.abort();
        }
    }
    
    private void superAbort(){
        super.abort();
    }
    
    protected SyncCallback afterCommitUpdate = new SyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            receiptsTable.model.enableAutoAdd(false);
            
        }
    };
    
    protected SyncCallback afterCommitAdd = new SyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            receiptsTable.model.enableAutoAdd(false);
            
        }
    };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        if(state != State.UPDATE && state != State.QUERY){
            if("receipt".equals(screenType))
                return !receiptTableRowEmpty(addRow, true);
            else
                return !transferTableRowEmpty(addRow);
                
        }
        
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(state == State.QUERY){
            if(col == 0 || col == 3 || col == 5)
                return true;
            else
                return false;
            
        }else if(state != State.UPDATE && state != State.ADD)
            return false;
        
        int numRows = receiptsTable.model.numRows();
        if("receipt".equals(screenType)){
            if(col == 0 && row > -1){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                
                //order id is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableOrderId)
                    return false;
           
            }else if(col == 2 && row > -1){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                
                //upc is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableUpc)
                    return false;
                
            }else if(col == 3 && row > -1){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                
                //inv item is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableInvItem)
                    return false;
                
            }else if(col == 4 && row > -1){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoForm hiddenRPC = tableRow.key;
                
                //upc is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableOrg)
                    return false;
            }
            return true;
        }else{
            if(state == State.UPDATE || state == State.ADD)
                return true;
        }
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        currentEditingRow = row;
        return true;
    }
    
    public boolean canDrag(TableWidget widget, TableDataRow item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget, Widget dragWidget, TableDataRow dropTarget, int targetRow) {
        return false;
    }

    public void drop(TableWidget widget, Widget dragWidget, TableDataRow dropTarget, int targetRow) {}

    public void drop(TableWidget widget, Widget dragWidget) {}
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    @SuppressWarnings(value={"unchecked"})
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if(state == State.QUERY)
            return;
        
        if("receipt".equals(screenType)){
            //we need to try and lookup the order using the order number that they have entered
            if(col == 0 && row < receiptsTable.model.numRows() && receiptTableRowEmpty(receiptsTable.model.getRow(row), false)){
                window.setBusy();

                form.orderId = (Integer)receiptsTable.model.getCell(row, 0);
                
                screenService.call("getReceiptsAndLockOrder", form, new AsyncCallback<InventoryReceiptForm>(){
                    public void onSuccess(InventoryReceiptForm result){    
                        if(result.tableRows.size() > 0){
                            createReceiptRows(row, result.tableRows);
                            receiptsTable.table.activeCell = -1;
                            receiptsTable.table.activeRow = -1;
                            receiptsTable.table.select(row, 1);
                            
                        }
                        
                        window.clearStatus();
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.clearStatus();
                    }
                });
            }else if(col == 2 && row > -1 && row < receiptsTable.model.numRows()){
                final TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                final String upcValue = (String)receiptsTable.model.getCell(row, 2);
                
                if(upcValue != null && !"".equals(upcValue)){
                window.setStatus("","spinnerIcon");

                //prepare the argument list
                //InventoryReceiptRPC irrpc = new InventoryReceiptRPC();
                //irrpc.key = form.key;
                //irrpc.form = form.form;
               form.upcValue = upcValue;
                
                screenService.call("getInvItemFromUPC", form, new AsyncCallback<InventoryReceiptForm>(){
                    public void onSuccess(InventoryReceiptForm result){   
                        if(result.invItemsByUPC.size() > 1){
                            //we need to have the user select which item they want
                        }else if(result.invItemsByUPC.size() == 1){
                            //we need to set the values in the datamap
                            
                            InvReceiptItemInfoForm rowSubForm = (InvReceiptItemInfoForm)tableRow.key;
                            if(rowSubForm == null){
                                rowSubForm = new InvReceiptItemInfoForm();
                                
                                rowSubForm = new InvReceiptItemInfoForm(form.itemInformation.node);
                                tableRow.key = rowSubForm;
                            }
                            
                            TableDataRow<Integer> set = result.invItemsByUPC.get(0);
                       
                            //name
                            TableDataModel<TableDataRow<Integer>> invItemModel = new TableDataModel<TableDataRow<Integer>>();
                            invItemModel.add(new TableDataRow<Integer>(set.key, set.cells[0]));
               
                            rowSubForm.disableInvItem = true;
                            rowSubForm.disableOrderId = true;
                            rowSubForm.itemIsSerialMaintained = (String)set.cells[6].getValue();
                            rowSubForm.itemIsLotMaintained = (String)set.cells[5].getValue();;
                            rowSubForm.itemIsBulk = (String)set.cells[4].getValue();;
                            rowSubForm.description.setValue(set.cells[2].getValue());
                            rowSubForm.dispensedUnits.setValue(set.cells[3].getValue());
                            rowSubForm.storeId.setValue(set.cells[1].getValue());
                            
                            ((DropDownField<Integer>)tableRow.cells[3]).setModel(invItemModel);
                            ((DropDownField<Integer>)tableRow.cells[3]).setValue(invItemModel.get(0));
                            
                            receiptsTable.model.refresh();
                            
                            //set the text boxes
                            itemDescText.setText(rowSubForm.description.getValue());
                            itemStoreText.setText(rowSubForm.storeId.getValue());
                            itemDisUnitsText.setText(rowSubForm.dispensedUnits.getValue());
                        }
                            
                        window.clearStatus();
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.clearStatus();
                    }
                });
                }
                
            }else if(col == 3 && row > -1 && row < receiptsTable.model.numRows()){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)tableRow.cells[3]).getValue();
               
                TableDataRow<Integer> set = null;
                if(selections.size() > 0)
                    set = (TableDataRow<Integer>)selections.get(0);
                
               if(set != null && set.cells.length > 1){
                    //set the text boxes
                   ReceiptInvItemKey dropdownData = (ReceiptInvItemKey)set.getData();
                   
                   itemDescText.setText(dropdownData.desc);
                   itemStoreText.setText(((StringField)set.cells[1]).getValue());
                   itemDisUnitsText.setText(dropdownData.dispensedUnits);
                    
                   InvReceiptItemInfoForm rowSubForm = (InvReceiptItemInfoForm)tableRow.key;
                    if(rowSubForm == null){
                        rowSubForm = new InvReceiptItemInfoForm();
                        
                        rowSubForm = new InvReceiptItemInfoForm(form.itemInformation.node);
                        tableRow.key = rowSubForm;
                    }
                    
                    rowSubForm.description.setValue(dropdownData.desc);
                    rowSubForm.storeId.setValue(((StringField)set.cells[1]).getValue());
                    rowSubForm.dispensedUnits.setValue(dropdownData.dispensedUnits);
                    rowSubForm.itemIsBulk = dropdownData.isBulk;
                    rowSubForm.itemIsLotMaintained = dropdownData.isLotMaintained;
                    rowSubForm.itemIsSerialMaintained = dropdownData.isSerialMaintained;
                    
               }
            }else if(col == 4 && row > -1 && row < receiptsTable.model.numRows()){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)tableRow.cells[4]).getValue();
                TableDataRow<Integer> set = null;
                if(selections.size() > 0)
                    set = selections.get(0);
                
                if(set != null && set.cells.length > 1){
                    InvReceiptOrgKey orgKey = (InvReceiptOrgKey)set.getData();
                    //set the text boxes
                    orgAddressText.setText(((StringObject)set.cells[1]).getValue());
                    orgCityText.setText(((StringObject)set.cells[2]).getValue());
                    orgStateText.setText(((StringObject)set.cells[3]).getValue());               
                    orgAptSuiteText.setText(orgKey.aptSuite);
                    orgZipCodeText.setText(orgKey.zipCode);
                    
                    InvReceiptItemInfoForm rowSubForm = (InvReceiptItemInfoForm)tableRow.key;
                    if(rowSubForm == null){
                        rowSubForm = new InvReceiptItemInfoForm();
                        
                        rowSubForm= new InvReceiptItemInfoForm(form.itemInformation.node);
                        tableRow.key = rowSubForm;
                    }
                    
                    rowSubForm.multUnit.setValue(orgKey.aptSuite);
                    rowSubForm.streetAddress.setValue(((StringObject)set.cells[1]).getValue());
                    rowSubForm.city.setValue(((StringObject)set.cells[2]).getValue());
                    rowSubForm.state.setValue(((StringObject)set.cells[3]).getValue());
                    rowSubForm.zipCode.setValue(orgKey.zipCode);
                    
                }
            }
        }else if("transfer".equals(screenType)){
            if(col == 0 && row < receiptsTable.model.numRows()){
                final TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);

                ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)tableRow.cells[0]).getValue();
                Integer currentLocValue = null;
                if(selections.size() == 1){
                    ReceiptInvItemKey rowData = (ReceiptInvItemKey)selections.get(0).getData();
                    currentLocValue = rowData.locId;
                }
                
                if((lastLocValue == null && currentLocValue != null) || 
                                (lastLocValue != null && !lastLocValue.equals(currentLocValue))){
                    //we need to send lock/fetch call back
                    TransferLocationRPC tlrpc = new TransferLocationRPC();
                    tlrpc.oldLocId = lastLocValue;
                    tlrpc.currentLocId = currentLocValue;
                    screenService.call("fetchLocationAndLock", tlrpc, new SyncCallback() {
                        public void onSuccess(Object obj) {
                            TransferLocationRPC result = (TransferLocationRPC)obj;

                            receiptsTable.model.setCell(row, 2, result.currentQtyOnHand);
                            
                            //call refactored method
                            loadFromInvItemData(row);
                        }

                        public void onFailure(Throwable caught) {
                            Window.alert(caught.getMessage());
                            //clear row
                            ((DropDownField<Integer>)tableRow.cells[0]).setModel(null);
                            ((DropDownField<Integer>)tableRow.cells[0]).setValue(null);
                            tableRow.cells[1].setValue(null);
                            tableRow.cells[2].setValue(null);
                            ((DropDownField<Integer>)tableRow.cells[3]).setModel(null);
                            ((DropDownField<Integer>)tableRow.cells[3]).setValue(null);
                            tableRow.cells[4].setValue(null);
                            ((DropDownField<Integer>)tableRow.cells[5]).setModel(null);
                            ((DropDownField<Integer>)tableRow.cells[5]).setValue(null);
                            tableRow.cells[6].setValue(null);
                            receiptsTable.model.refresh();
                            
                            //clear subrpc
                            InvReceiptItemInfoForm rowSubForm = (InvReceiptItemInfoForm)tableRow.key;
                            if(rowSubForm != null){
                                rowSubForm.description.setValue(null);
                                rowSubForm.expirationDate.setValue("");
                                rowSubForm.itemIsBulk = null;
                                rowSubForm.itemIsLotMaintained = null;
                                rowSubForm.itemIsSerialMaintained = null;
                                rowSubForm.lotNumber.setValue(null);
                                rowSubForm.storeId.setValue(null);
                                rowSubForm.toDescription.setValue(null);
                                rowSubForm.toDispensedUnits.setValue(null);
                                rowSubForm.toExpDate.setValue(null);
                                rowSubForm.toLotNumber.setValue(null);
                                rowSubForm.toStoreId.setValue(null);
                                                             
                                //reload cleared subrpc
                                load(rowSubForm);
                            }
                            
                        }
                    });
                }
               
            }else if(col == 3 && row < receiptsTable.model.numRows()){
                TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)tableRow.cells[3]).getValue();
               
                TableDataRow<Integer> set = null;
                if(selections.size() > 0)
                    set = (TableDataRow)selections.get(0);
                
               if(set != null && set.size() > 1){
                   //set the text boxes
                   ReceiptInvItemKey dropdownData = (ReceiptInvItemKey)set.getData();
                   //toItemDescText.setText(dropdownData.desc);
                   //toItemStoreText.setText((String)set.cells[1].getValue());
                   //toItemDisUnitsText.setText(dropdownData.dispensedUnits);
                   
                   InvReceiptItemInfoForm rowSubForm = tableRow.key;
                   if(rowSubForm == null){
                       rowSubForm = new InvReceiptItemInfoForm();
                       rowSubForm= new InvReceiptItemInfoForm(form.itemInformation.node);
                       tableRow.key = rowSubForm;
                   }
                   rowSubForm.toDescription.setValue(dropdownData.desc);
                   rowSubForm.toStoreId.setValue(set.cells[1].getValue());
                   rowSubForm.toDispensedUnits.setValue(dropdownData.dispensedUnits);
                    
                   load(rowSubForm);
                   //receiptsTable.model.refresh();
               }

            }else if(col == 4 && row < receiptsTable.model.numRows()){
                String checkedValue = (String)receiptsTable.model.getCell(row, 4);

                //if the checkbox changes value we need to clear out the to location column
                if(CheckBox.CHECKED.equals(checkedValue))
                    receiptsTable.model.setCell(row, 5 , null);
                
            }/*else if(col == 6 && row < receiptsTable.model.numRows()){ //qty column
                if(receiptsTable.model.getCell(row, 2) != null && ((Integer)receiptsTable.model.getCell(row, 2)).compareTo((Integer)receiptsTable.model.getCell(row, 6)) < 0){
                    receiptsTable.model.clearCellError(row, 6);
                    receiptsTable.model.setCellError(row, 6, consts.get("notEnoughQuantityOnHand"));
                }
                
            }
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(state == State.QUERY)
            return;
        
        if(!"receipt".equals(screenType)){
            //we need to try and lookup the order using the order number that they have entered
            if(col == 0 && row < receiptsTable.model.numRows()){
                ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)receiptsTable.model.getObject(row, col)).getValue();
                
                if(selections != null && selections.size() == 1){
                    ReceiptInvItemKey rowData = (ReceiptInvItemKey)selections.get(0).getData();
                    lastLocValue = rowData.locId;
                }
            }
        }
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    
    private boolean receiptTableRowEmpty(TableDataRow<InvReceiptItemInfoForm> row, boolean checkFirstColumn){
        boolean empty = true;
        
        if(checkFirstColumn){
            for(int i=0; i<row.cells.length; i++){
                if(i != 1 && row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                    empty = false;
                    break;
                }
            }
        }else{
            if(row.cells[0].getValue() == null || "".equals(row.cells[0].getValue()))
                return false;
            
            //we dont need to check the first column
            for(int i=2; i<row.cells.length; i++){
                if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }
    
    
    private boolean transferTableRowEmpty(TableDataRow<InvReceiptItemInfoForm> row){
        boolean empty = true;
        
        for(int i=0; i<row.cells.length; i++){
            if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                empty = false;
                break;
            }
        }

        return empty;
    }
    
    private void onRemoveReceiptRowButtonClick() {
        int selectedRow = receiptsTable.model.getSelectedIndex();
        if (selectedRow > -1 && receiptsTable.model.numRows() > 0) {
            receiptsTable.model.deleteRow(selectedRow);

        }
    }
    
    private void loadFromInvItemData(int row){
        TableDataRow<InvReceiptItemInfoForm> tableRow = receiptsTable.model.getRow(row);
        ArrayList selections = (ArrayList)((DropDownField)tableRow.cells[0]).getValue();
        
        TableDataRow<Integer> set = null;
        if(selections.size() > 0)
            set = (TableDataRow)selections.get(0);
        
       if(set != null && set.size() > 1){
            //set the text boxes
           ReceiptInvItemKey dropdownData = (ReceiptInvItemKey)set.getData();
           String descObj = dropdownData.desc;
           String isBulkObj = dropdownData.isBulk;
           String isLotMaintainedObj = dropdownData.isLotMaintained;            
           String isSerialMaintainedObj = dropdownData.isSerialMaintained;
           String dispensedUnits = dropdownData.dispensedUnits;
           String lotNum = dropdownData.lotNum;
           Datetime expDate = dropdownData.expDate;
           
           tableRow.cells[1].setValue(set.cells[2].getValue());
           tableRow.cells[2].setValue(set.cells[3].getValue());

           InvReceiptItemInfoForm rowSubForm = tableRow.key;
           if(rowSubForm == null){
               rowSubForm = new InvReceiptItemInfoForm();
               rowSubForm= new InvReceiptItemInfoForm(form.itemInformation.node);
               tableRow.key = rowSubForm;
           }
            
           rowSubForm.description.setValue(descObj);
           rowSubForm.storeId.setValue((String)set.cells[1].getValue());
           rowSubForm.itemIsBulk = isBulkObj;
           rowSubForm.itemIsLotMaintained = isLotMaintainedObj;
           rowSubForm.itemIsSerialMaintained = isSerialMaintainedObj;
           rowSubForm.dispensedUnits.setValue(dispensedUnits);
           
           if(expDate != null){
               rowSubForm.expirationDate.setValue(expDate.toString());
               rowSubForm.toExpDate.setValue(expDate.toString());
           }
           rowSubForm.lotNumber.setValue(lotNum);
           rowSubForm.toLotNumber.setValue(lotNum);
            
           //we need to clear out to item, check box, and location
           ((DropDownField<Integer>)tableRow.cells[3]).clear();
           ((DropDownField<Integer>)tableRow.cells[3]).setModel(null);
           tableRow.cells[4].setValue(null);
           ((DropDownField<Integer>)tableRow.cells[5]).clear();
           ((DropDownField<Integer>)tableRow.cells[5]).setModel(null);
           
           rowSubForm.toDescription.setValue(null);
           rowSubForm.toStoreId.setValue(null);
           
           receiptsTable.model.refresh();
           load(rowSubForm);
       }
       
       if(receiptsTable.model.getCell(row, 6) != null && ((Integer)receiptsTable.model.getCell(row, 6)).compareTo((Integer)receiptsTable.model.getCell(row, 2)) > 0){
           receiptsTable.model.clearCellError(row, 6);
           receiptsTable.model.setCellError(row, 6, consts.get("notEnoughQuantityOnHand"));
       }
    }
/*    
    private void loadDataMapIntoTransferTableRow(DataSet<Data> row){
        DataMap map = (DataMap)row.getData();
        
        row.get(0).setValue(getValueFromHashWithNulls(map, "orderNumber"));
        row.get(1).setValue(getValueFromHashWithNulls(map, "receivedDate"));
        row.get(2).setValue(getValueFromHashWithNulls(map, "upc"));
        
        if(map.get("org") != null){
            ((DropDownField)row.get(4)).setModel(((DropDownField)map.get("org")).getModel());
            ((DropDownField)row.get(4)).setValue(((DropDownField)map.get("org")).getSelections());
        }
        
        row.get(5).setValue(getValueFromHashWithNulls(map, "qtyReceived"));
        
        if(map.get("fromInventoryItem") != null){
            ((DropDownField)row.get(0)).setModel(((DropDownField)map.get("fromInventoryItem")).getModel());
            ((DropDownField)row.get(0)).setValue(((DropDownField)map.get("fromInventoryItem")).getValue());
        }
        
        String fromLocationName = InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId();
        if(map.get(fromLocationName) != null){
            row.get(1).setValue(((DropDownField)map.get(fromLocationName)).getTextValue());
        }
        
        if(map.get("inventoryItem") != null){
            ((DropDownField)row.get(2)).setModel(((DropDownField)map.get("inventoryItem")).getModel());
            ((DropDownField)row.get(2)).setValue(((DropDownField)map.get("inventoryItem")).getSelections());
        }
        
        row.get(2).setValue(getValueFromHashWithNulls(map, "fromQtyOnHand"));
        
        if(map.get("inventoryItem") != null){
            ((DropDownField)row.get(3)).setModel(((DropDownField)map.get("inventoryItem")).getModel());
            ((DropDownField)row.get(3)).setValue(((DropDownField)map.get("inventoryItem")).getValue());
        }
        row.get(4).setValue(((CheckField)map.get("addToExisting")).getValue());
        
        if(map.get("toInventoryLocation") != null){
            ((DropDownField)row.get(5)).setModel(((DropDownField)map.get("toInventoryLocation")).getModel());
            ((DropDownField)row.get(5)).setValue(((DropDownField)map.get("toInventoryLocation")).getValue());
        }
        
        row.get(6).setValue(getValueFromHashWithNulls(map, "qtyRequested"));
        

    }

    
    private void createReceiptRows(int row, TableDataModel<TableDataRow<InvReceiptItemInfoForm>> model){
        for(int i=0; i<model.size(); i++)
            receiptsTable.model.addRow(model.get(i));
        
        //delete the row for now
        receiptsTable.model.deleteRow(row);
    }
    
    //
    //auto complete call
    //
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
        
        ReceiptInvLocationAutoRPC rilrpc = new ReceiptInvLocationAutoRPC();
        rilrpc.cat = widget.cat;
        rilrpc.match = text;
        
        if("toInventoryItemTrans".equals(widget.cat)){
            if(receiptsTable.model.numRows() > 0){
                //get dropdown key
                ArrayList<TableDataRow<Integer>> selected = (ArrayList<TableDataRow<Integer>>)receiptsTable.model.getCell(currentEditingRow, 0);
                ReceiptInvItemKey setData = null;
                if(selected.size() > 0)
                    setData = (ReceiptInvItemKey)selected.get(0).getData();
                
                if(setData != null && setData.parentInvItemId != null)
                    rilrpc.parentInvItemId = setData.parentInvItemId;
                else
                    rilrpc.invItemId = (Integer)((DropDownField)receiptsTable.model.getObject(currentEditingRow, 0)).getSelectedKey();
            }

        }else{
            //add to existing
            if(addToExisiting != null)
                rilrpc.addToExisting = ((CheckBox)addToExisiting.getWidget()).getState();
            else{
                if(receiptsTable.model.numRows() > 0)
                    rilrpc.addToExisting = (String)receiptsTable.model.getCell(currentEditingRow, 4);
            }
            
            //inv item id
            if(receiptsTable.model.numRows() > 0) //<--this is for receipt
                rilrpc.invItemId = (Integer)((DropDownField)receiptsTable.model.getObject(currentEditingRow, 3)).getSelectedKey();
        }
        
        // prepare the argument list for the getObject function
        screenService.call("getMatchesCall", rilrpc, new AsyncCallback<ReceiptInvLocationAutoRPC>() {
            public void onSuccess(ReceiptInvLocationAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }

    //
    //start table model listener methods
    //
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTableModelEvents sender) {}

    public void rowAdded(SourcesTableModelEvents sender, int rows) {}

    public void rowDeleted(SourcesTableModelEvents sender, int row) {}

    public void rowSelected(SourcesTableModelEvents sender, int row) {
        if(state == State.QUERY)
            return;
        
        TableDataRow<InvReceiptItemInfoForm> tableRow=null;
        if(addToExisiting != null && !addToExisiting.isEnabled() && receiptsTable.model.numRows() > 0 && (state == State.ADD || state == State.UPDATE)){
            itemLotNum.setReadOnly(false);
            itemLocation.enable(true);
            recItemExpDate.enable(true);
            addToExisiting.enable(true);
        }
        
        if(row >=0 && receiptsTable.model.numRows() > row)
            tableRow = receiptsTable.model.getRow(row);
        
        if(tableRow != null && tableRow.key != null)
            load(tableRow.key);
        else{
            resetForm(form.itemInformation);
            load(form.itemInformation);
        }
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {}

    public void rowUpdated(SourcesTableModelEvents sender, int row) {}

    public void unload(SourcesTableModelEvents sender) {}
    //
    //end table model listener methods
    //
    
    private TableDataModel<TableDataRow<Integer>> getLockedSetsFromTable(){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        for(int i=0; i<receiptsTable.model.numRows(); i++){
            if(screenType.equals("receipt")){
                if(receiptsTable.model.getCell(i, 0) != null)
                    returnModel.add(new TableDataRow<Integer>((Integer)receiptsTable.model.getCell(i, 0)));
                
            }else{
                ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)receiptsTable.model.getObject(i, 0)).getValue();
                
                if(selections.size() == 1){
                    ReceiptInvItemKey setData = (ReceiptInvItemKey)selections.get(0).getData();
                    returnModel.add(new TableDataRow<Integer>(setData.locId));
                }
            }
        }
            
        return returnModel;
    }   
*/

