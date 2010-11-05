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
package org.openelis.modules.buildKits.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.InventoryItemCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.manager.BuildKitManager;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryTransferManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.modules.inventoryTransfer.client.InventoryTransferScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class BuildKitsScreen extends Screen {
    
    private ModulePermission                      userPermission;
    private BuildKitsScreen                       screen;
    private InventoryTransferScreen               inventoryTransferScreen;
    private BuildKitManager                       manager;
    private Button                                addButton, commitButton, abortButton;
    private Calendar                              locationExpirationDate;
    private TextBox                               numRequested, qcReference, locationLotNumber;
    private AutoComplete                          name, locationStorageLocationName,
                                                  componentLocationStorageLocationName;
    private Button                                transferButton;
    private CheckBox                              addToExisting;
    private Table                                 componentTable;
    private Dropdown<Integer>                     dispensedUnitsId;
    private ScreenService                         inventoryItemService, inventoryLocationService,
                                                  storageService;
    
    public BuildKitsScreen() throws Exception {
        super((ScreenDefInt)GWT.create(BuildKitsDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.buildKits.server.BuildKitsService");
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");       
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        
        userPermission = OpenELIS.getSystemUserPermission().getModule("buildkits");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Build Kits Screen");

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
        
        try {
            DictionaryCache.preloadByCategorySystemNames("inventory_unit");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("Build Kits Screen: missing dictionary entry; " + e.getMessage());
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

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        name = (AutoComplete)def.getWidget(InventoryItemMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if (manager == null || manager.getInventoryItem() == null)
                        name.setValue(null, "");
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                } 
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer val;  
                
                val = null;            
                manager.setInventoryItemId(event.getValue());                                                                                                    
                DataChangeEvent.fire(screen);
                try {
                    val = Integer.valueOf((String)numRequested.getValue());                    
                } catch ( NumberFormatException e) {
                    e.printStackTrace();
                }
                
                setTotalInComponents(val);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        name.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                InventoryItemDO data;
                Item<Integer> item;
                ArrayList<InventoryItemDO> list;
                ArrayList<Item<Integer>> model;
                DictionaryDO store, units;

                try {
                    list = inventoryItemService.callList("fetchActiveByName", event.getMatch());
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        store = DictionaryCache.getEntryFromId(data.getStoreId());
                        units = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                        item = new Item<Integer>(data.getId(), data.getName(), data.getDescription(),
                                               store.getEntry(), units.getEntry());
                        item.setData(data);
                        model.add(item);
                    }
                    name.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addToExisting = (CheckBox)def.getWidget("addToExisting");
        addScreenHandler(addToExisting, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                Item<Integer> row;
                
                item = null;
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        addToExisting.setValue("N");
                        addToExisting.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                } 
                
                row = name.getSelectedItem();
                item = (InventoryItemDO)row.getData();
                                                            
                if (state == State.ADD && item != null) 
                    addToExisting.setEnabled("Y".equals(item.getIsBulk())); 
                else
                    addToExisting.setEnabled(false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                manager.getInventoryReceipt().setAddToExistingLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addToExisting.setEnabled(false);
            }
        });

        numRequested = (TextBox)def.getWidget("numRequested");
        addScreenHandler(numRequested, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {       
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        numRequested.setValue(null);
                        numRequested.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }               
                                                            
                numRequested.setEnabled(state == State.ADD && name.getValue() != null);                  
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {                                
                setTotalInComponents(event.getValue());                               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                numRequested.setEnabled(false);
            }
        });
        
        qcReference = (TextBox)def.getWidget("qcReference");
        addScreenHandler(qcReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                                                
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        qcReference.setValue(null);
                        qcReference.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }            
                
                qcReference.setEnabled(state == State.ADD && name.getValue() != null);  
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryReceipt().setQcReference(event.getValue());     
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcReference.setEnabled(false);
            }
        });

        locationStorageLocationName = (AutoComplete)def.getWidget(InventoryItemMeta.getLocationStorageLocationName());
        addScreenHandler(locationStorageLocationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {         
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationStorageLocationName.setValue(null, "");
                        locationStorageLocationName.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }         
                                                            
                locationStorageLocationName.setEnabled(state == State.ADD && name.getValue() != null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                InventoryReceiptViewDO data; 
                InventoryLocationViewDO location;
                Item<Integer> row;               
                
                data = manager.getInventoryReceipt();
                row = locationStorageLocationName.getSelectedItem();
                
                if (data.getInventoryLocations() == null) {
                    data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
                    data.getInventoryLocations().add(new InventoryLocationViewDO());
                }
                
                location = data.getInventoryLocations().get(0);
                if (row != null) {
                    location = (InventoryLocationViewDO)row.getData();
                    data.getInventoryLocations().set(0, location);                    
                } else {
                    location.setId(null);
                    location.setStorageLocationId(null);
                    location.setStorageLocationName(null);
                    location.setStorageLocationLocation(null);
                    location.setStorageLocationUnitDescription(null);
                }
                locationLotNumber.setValue(location.getLotNumber());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationStorageLocationName.setEnabled(false);
                locationStorageLocationName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;                
                Integer itemId;
                String param,location;
                InventoryItemViewDO data;
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
                
                if(manager == null)
                    return;
                                              
                parser = new QueryFieldUtil();
                
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e){
                	
                }
                
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
                model = new ArrayList<Item<Integer>>();
                try {
                    if(manager.getInventoryItem() == null)
                        return;
                    data = manager.getInventoryItem().getInventoryItem();
                    itemId = data.getId();
                    if ("Y".equals(addToExisting.getValue()) && itemId != null) {   
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
                            row.setCell(1,invLoc.getLotNumber());
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
                    locationStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }            
        });
        
        locationLotNumber = (TextBox)def.getWidget(InventoryItemMeta.getLocationLotNumber());
        addScreenHandler(locationLotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                Item<Integer> row;
                
                item = null;
                
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationLotNumber.setValue(null);
                        locationLotNumber.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                
                row = name.getSelectedItem();
                item = (InventoryItemDO)row.getData();
                                                            
                if (state == State.ADD && item != null) 
                    locationLotNumber.setEnabled(!"Y".equals(item.getIsBulk()));
                else
                    locationLotNumber.setEnabled(false);
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                InventoryReceiptViewDO data;   
                InventoryLocationViewDO location;
                
                data = manager.getInventoryReceipt();
                location = data.getInventoryLocations().get(0);
                location.setLotNumber(event.getValue());     
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationLotNumber.setEnabled(false);
            }
        });

        locationExpirationDate = (Calendar)def.getWidget(InventoryItemMeta.getLocationExpirationDate());
        addScreenHandler(locationExpirationDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                Item<Integer> row;
                
                item = null;
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationExpirationDate.setValue(null);
                        locationExpirationDate.setEnabled(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                
                row = name.getSelectedItem();
                item = (InventoryItemDO)row.getData();
                                                            
                if (state == State.ADD && item != null) 
                    locationExpirationDate.setEnabled(!"Y".equals(item.getIsBulk()));
                else
                    locationExpirationDate.setEnabled(false);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                InventoryReceiptViewDO data;        
                InventoryLocationViewDO location;
                
                data = manager.getInventoryReceipt();
                location = data.getInventoryLocations().get(0);
                location.setExpirationDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationExpirationDate.setEnabled(false);
            }
        });

        componentTable = (Table)def.getWidget("componentTable");
        addScreenHandler(componentTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                componentTable.setModel(getTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                componentTable.setEnabled(true);
            }
        });
        
        dispensedUnitsId = (Dropdown<Integer>)componentTable.getColumnWidget(InventoryItemMeta.getComponentDispensedUnitsId());
        componentLocationStorageLocationName  = (AutoComplete)componentTable.getColumnWidget(InventoryItemMeta.getLocationStorageLocationName());

        componentLocationStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                Integer itemId, storeId;
                InventoryItemViewDO data;
                InventoryComponentViewDO component; 
                ArrayList<InventoryLocationViewDO> invLocList; 
                InventoryLocationViewDO invLoc;
                Row tableRow;
                Item<Integer> autoRow;                
                ArrayList<Item<Integer>> model;
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                String param;    
                              
                tableRow = componentTable.getRowAt(componentTable.getSelectedRow());                     
                component = (InventoryComponentViewDO)tableRow.getData();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
                model = new ArrayList<Item<Integer>>();
                try {
                    data = manager.getInventoryItem().getInventoryItem();
                    itemId = component.getComponentId();
                    storeId = data.getStoreId();
                    fields = new ArrayList<QueryData>();
                    query = new Query();                                                                                                     
                    
                    field = new QueryData();
                    field.query = param;
                    fields.add(field);

                    field = new QueryData();
                    field.query = Integer.toString(itemId);
                    fields.add(field);
                    
                    field = new QueryData();
                    field.query = Integer.toString(storeId);
                    fields.add(field);

                    query.setFields(fields);
                    invLocList = inventoryLocationService.callList("fetchByLocationNameInventoryItemIdStoreId", query);
                    for (i = 0; i < invLocList.size(); i++ ) {
                        autoRow = new Item<Integer>(4);
                        invLoc = invLocList.get(i);

                        autoRow.setKey(invLoc.getId());
                        autoRow.setCell(0,invLoc.getStorageLocationName() + ", " +
                                                  invLoc.getStorageLocationUnitDescription() + " " +
                                                  invLoc.getStorageLocationLocation());
                        autoRow.setCell(1,invLoc.getLotNumber());
                        autoRow.setCell(2,invLoc.getQuantityOnhand());
                        autoRow.setCell(3,invLoc.getExpirationDate());

                        autoRow.setData(invLoc);

                        model.add(autoRow);
                    }
                    
                    componentLocationStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }                        
        });
        
        componentTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int c;
                
                c = event.getCol();
                if (state != State.ADD || c != 1) 
                    event.cancel();                                    
            }            
        });
        
        componentTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer prevTotal, factor; 
                Object val;    
                Row tableRow;
                AutoCompleteValue autoRow;
                InventoryComponentViewDO data;
                InventoryItemManager itemMan;
                InventoryLocationViewDO location;

                r = event.getRow();
                c = event.getCol();
                val = componentTable.getValueAt(r,c);
                tableRow = componentTable.getRowAt(r);
                data = null;
                try {
                    itemMan = manager.getInventoryItem();
                    if (itemMan == null)
                        return;
                    data = itemMan.getComponents().getComponentAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                switch(c) {
                    case 1:
                        autoRow = (AutoCompleteValue)val;
                        if (autoRow == null) {
                            componentTable.setValueAt(r, 5, null);  
                            return;
                        }
                        
                        location = (InventoryLocationViewDO)autoRow.getData();
                        data.setInventoryLocationId(location.getId());
                        data.setInventoryLocationLotNumber(location.getLotNumber());
                        data.setInventoryLocationQuantityOnhand(location.getQuantityOnhand());
                        data.setInventoryLocationStorageLocationLocation(location.getStorageLocationLocation());
                        data.setInventoryLocationStorageLocationName(location.getStorageLocationName());
                        data.setInventoryLocationStorageLocationUnitDescription(location.getStorageLocationUnitDescription());
                        componentTable.setValueAt(r, 5, location.getQuantityOnhand());
                        componentTable.setValueAt(r, 2, location.getLotNumber());
                        prevTotal = (Integer)componentTable.getValueAt(r, 4);
                                                              
                        if (prevTotal == null || prevTotal > location.getQuantityOnhand()){
                            factor = location.getQuantityOnhand() / data.getQuantity();
                            for (r = 0; r < componentTable.getRowCount(); r++) {
                                tableRow = componentTable.getRowAt(r);                                           
                                data = (InventoryComponentViewDO)tableRow.getData();
                                data.setTotal(factor * data.getQuantity());
                                componentTable.setValueAt(r, 4, factor * data.getQuantity());                                
                            }                                 
                            numRequested.clearExceptions();
                            numRequested.setValue(factor);
                            manager.getInventoryReceipt().setQuantityReceived(factor);
                        }                    
                        break;
                }
            }
        });

        transferButton = (Button)def.getWidget("transferButton");
        addScreenHandler(transferButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                componentTable.finishEditing();
                showTransfer();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                transferButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                if (EnumSet.of(State.ADD).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
        
        screen = this;
    }
    
    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> list;
        Item<Integer> row;       
        
        // units dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_unit");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        dispensedUnitsId.setModel(model);
    }
    
    protected void add() {
        manager = BuildKitManager.getInstance();
        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    public void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        window.setBusy(consts.get("adding"));
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addingComplete"));
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }        
    }

    protected void abort() {
        setFocus(null);
        manager = BuildKitManager.getInstance();        
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("addAborted"));
    }
    
    private ArrayList<Row> getTableModel() {
        String location;
        InventoryComponentViewDO data;
        ArrayList<Row> model;
        InventoryComponentManager components;
        Row tableRow;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            if (manager.getInventoryItem() != null) {
                components = manager.getInventoryItem().getComponents();
                for (int i = 0; i < components.count(); i++ ) {
                    data = components.getComponentAt(i);
                    tableRow = new Row(6);
                    tableRow.setCell(0,data.getComponentName() + " , " +
                                              data.getComponentDescription());
                    tableRow.setCell(3,data.getComponentDispensedUnitsId());  
                    tableRow.setCell(4,data.getTotal());
                    if (data.getInventoryLocationId() != null) {
                        location = StorageLocationManager.getLocationForDisplay(data.getInventoryLocationStorageLocationName(),
                                                                                data.getInventoryLocationStorageLocationUnitDescription(),
                                                                                data.getInventoryLocationStorageLocationLocation());
                        tableRow.setCell(1,new AutoCompleteValue(data.getInventoryLocationId(), location));
                        tableRow.setCell(2,data.getInventoryLocationLotNumber());
                        tableRow.setCell(5,data.getInventoryLocationQuantityOnhand());
                    }
                    tableRow.setData(data);
                    model.add(tableRow);
                }
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private void showTransfer() {       
        ModalWindow modal; 
        InventoryTransferManager manager;        

        manager = getInventoryTransferManager();
        if (manager == null)
            return;
        modal = new ModalWindow();
        modal.setName(consts.get("inventoryTransfer"));
        try {
            if (inventoryTransferScreen == null)
                inventoryTransferScreen = new InventoryTransferScreen(modal);   
            modal.setContent(inventoryTransferScreen);
            
            inventoryTransferScreen.loadTransferData(manager);
        } catch (Throwable e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }        
    }  
    
    private InventoryTransferManager getInventoryTransferManager() {
        Integer selRows[];
        InventoryTransferManager itman;
        InventoryComponentManager icman;
        InventoryComponentViewDO comp;
        InventoryLocationViewDO loc;
        AutoCompleteValue av;
        InventoryItemDO toItem, fromItem;
                
        selRows = componentTable.getSelectedRows();
        toItem = null;
        fromItem = null;
        
        if (selRows.length == 0) {
            com.google.gwt.user.client.Window.alert(consts.get("selRowsToTransfer"));
            return null;
        }
           
        loc = null;
        itman = InventoryTransferManager.getInstance();
        try {
            icman = manager.getInventoryItem().getComponents();
            Arrays.sort(selRows);
            for (int i = 0; i < selRows.length; i++) {
                comp = icman.getComponentAt(selRows[i]);
                itman.addTransfer();           
                
                toItem = InventoryItemCache.getActiveInventoryItemFromId(comp.getComponentId());
                fromItem = InventoryItemCache.getActiveInventoryItemFromId(toItem.getParentInventoryItemId());                
                
                itman.setToInventoryItemAt(toItem, i);                
                itman.setFromInventoryItemAt(fromItem, i);
                av = (AutoCompleteValue)componentTable.getValueAt(selRows[i], 1);
                if (av != null) 
                    loc = (InventoryLocationViewDO)av.getData();                
                itman.setToInventoryLocationAt(loc, i);                
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return itman;
    }
    
    private void setTotalInComponents(Integer val) {
        int i;
        Integer total, onHand;
        boolean changeTotal;
        Row row;
        InventoryComponentViewDO data;  
        
        manager.getInventoryReceipt().setQuantityReceived(val);
        if (val == null)
            return;
        
        if (val < 1) {
            numRequested.addException(new LocalizedException("numRequestedMoreThanZeroException"));
            return;
        } 
        
        if (componentTable.getRowCount() == 0)
            return;
                                        
        total = (Integer)componentTable.getValueAt(0, 4);
        
        if(total == null) {
            for (i = 0; i < componentTable.getRowCount(); i++) {
                row = componentTable.getRowAt(i);
                data = (InventoryComponentViewDO)row.getData();
                data.setTotal(val * data.getQuantity());
                componentTable.setValueAt(i, 4, val * data.getQuantity());
            }
        } else {
            changeTotal = true;
            for (i = 0; i < componentTable.getRowCount(); i++) {
                row = componentTable.getRowAt(i);
                data = (InventoryComponentViewDO)row.getData();
                onHand = (Integer)componentTable.getValueAt(i, 5);                                                
                if (onHand != null && onHand < (val * data.getQuantity())) {
                    changeTotal = false;
                    break;
                }                            
            }
            
            if (changeTotal) {
                for (i = 0; i < componentTable.getRowCount(); i++) {
                    row = componentTable.getRowAt(i);
                    data = (InventoryComponentViewDO)row.getData();
                    data.setTotal(val * data.getQuantity());
                    componentTable.setValueAt(i, 4, val * data.getQuantity());                           
                }
                numRequested.clearExceptions();
            } else {
                numRequested.addException(new LocalizedException("qtyOnHandNotSufficientException"));
            }
        }
    }
}