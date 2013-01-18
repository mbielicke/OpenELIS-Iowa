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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.InventoryItemCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
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
import org.openelis.modules.report.client.BuildKitsReportScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class BuildKitsScreen extends Screen {
    
    private ModulePermission                      userPermission;
    private BuildKitsScreen                       screen;
    private InventoryTransferScreen               inventoryTransferScreen;
    private BuildKitManager                       manager;
    private AppButton                             addButton, commitButton, abortButton;
    private CalendarLookUp                        locationExpirationDate;
    private TextBox                               numRequested, qcReference, locationLotNumber;
    private AutoComplete<Integer>                 name, locationStorageLocationName,
                                                  componentLocationStorageLocationName;
    private AppButton                             transferButton;
    private CheckBox                              addToExisting;
    private TableWidget                           componentTable;
    private Dropdown<Integer>                     dispensedUnitsId;
    private BuildKitsReportScreen                 buildKitsReportScreen; 
    private ScreenService                         inventoryItemService, inventoryLocationService,
                                                  storageService;
    
    public BuildKitsScreen() throws Exception {
        super((ScreenDefInt)GWT.create(BuildKitsDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.buildKits.server.BuildKitsService");
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");       
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        
        userPermission = UserCache.getPermission().getModule("buildkits");
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
            CategoryCache.getBySystemNames("inventory_unit");
        } catch (Exception e) {
            Window.alert("Build Kits Screen: missing dictionary entry; " + e.getMessage());
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
        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
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
                commitButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });       
        
        name = (AutoComplete)def.getWidget(InventoryItemMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if (manager == null || manager.getInventoryItem() == null)
                        name.setSelection(null, "");
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                } 
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                String num;
                Integer val;  
                
                manager.setInventoryItemId(event.getValue());                                                                                                    
                DataChangeEvent.fire(screen);
                try {
                    num = numRequested.getValue();
                    if (!DataBaseUtil.isEmpty(num)) {
                        val = Integer.valueOf(num);  
                        setTotalInComponents(val);
                    }
                } catch ( NumberFormatException e) {
                    e.printStackTrace();
                }                                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.ADD).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        name.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                InventoryItemDO data;
                TableDataRow row;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;
                DictionaryDO store, units;

                try {
                    list = inventoryItemService.callList("fetchActiveByName", event.getMatch());
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        store = DictionaryCache.getById(data.getStoreId());
                        units = DictionaryCache.getById(data.getDispensedUnitsId());
                        row = new TableDataRow(data.getId(), data.getName(), data.getDescription(),
                                               store.getEntry(), units.getEntry());
                        row.data = data;
                        model.add(row);
                    }
                    name.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addToExisting = (CheckBox)def.getWidget("addToExisting");
        addScreenHandler(addToExisting, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                TableDataRow row;
                
                item = null;
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        addToExisting.setValue("N");
                        addToExisting.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                } 
                
                row = name.getSelection();
                item = (InventoryItemDO)row.data;
                                                            
                if (state == State.ADD && item != null) 
                    addToExisting.enable("Y".equals(item.getIsBulk())); 
                else
                    addToExisting.enable(false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                manager.getInventoryReceipt().setAddToExistingLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addToExisting.enable(false);
            }
        });

        numRequested = (TextBox)def.getWidget("numRequested");
        addScreenHandler(numRequested, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {       
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        numRequested.setValue(null);
                        numRequested.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }               
                                                            
                numRequested.enable(state == State.ADD && name.getValue() != null);                  
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {                                
                setTotalInComponents(event.getValue());                               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                numRequested.enable(false);
            }
        });
        
        qcReference = (TextBox)def.getWidget("qcReference");
        addScreenHandler(qcReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                                                
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        qcReference.setValue(null);
                        qcReference.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }            
                
                qcReference.enable(state == State.ADD && name.getValue() != null);  
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryReceipt().setQcReference(event.getValue());     
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcReference.enable(false);
            }
        });

        locationStorageLocationName = (AutoComplete)def.getWidget(InventoryItemMeta.getLocationStorageLocationName());
        addScreenHandler(locationStorageLocationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {         
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationStorageLocationName.setSelection(null, "");
                        locationStorageLocationName.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }         
                                                            
                locationStorageLocationName.enable(state == State.ADD && name.getValue() != null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                InventoryReceiptViewDO data;
                InventoryLocationViewDO location;
                TableDataRow row;

                data = manager.getInventoryReceipt();
                row = locationStorageLocationName.getSelection();

                if (row != null && row.data != null) {
                    location = (InventoryLocationViewDO)row.data;
                    data.getInventoryLocations().set(0, location);
                } else {
                    location = data.getInventoryLocations().get(0);
                    location.setId(null);
                    location.setStorageLocationId(null);
                    location.setStorageLocationName(null);
                    location.setStorageLocationLocation(null);
                    location.setStorageLocationUnitDescription(null);
                }
                locationLotNumber.setValue(location.getLotNumber());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationStorageLocationName.enable(false);
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
                TableDataRow row;                
                ArrayList<TableDataRow> model;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                
                if(manager == null)
                    return;
                                              
                param = QueryFieldUtil.parseAutocomplete(event.getMatch());                                
                model = new ArrayList<TableDataRow>();
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
                        window.setBusy();
                        invLocList = inventoryLocationService.callList("fetchByLocationNameInventoryItemId", query);
                        for (i = 0; i < invLocList.size(); i++ ) {
                            row = new TableDataRow(4);
                            invLoc = invLocList.get(i);

                            row.key = invLoc.getId();
                            location = StorageLocationManager.getLocationForDisplay(invLoc.getStorageLocationName(),
                                                                                    invLoc.getStorageLocationUnitDescription(),
                                                                                    invLoc.getStorageLocationLocation());
                            row.cells.get(0).setValue(location);
                            row.cells.get(1).setValue(invLoc.getLotNumber());
                            row.cells.get(2).setValue(invLoc.getQuantityOnhand());
                            row.cells.get(3).setValue(invLoc.getExpirationDate());

                            row.data = invLoc;                            

                            model.add(row);
                        }
                    } else {
                        window.setBusy();
                        storLocList = storageService.callList("fetchAvailableByName", param);
                        for (i = 0; i < storLocList.size(); i++ ) {
                            row = new TableDataRow(4);
                            storLoc = storLocList.get(i);
                            row.key = storLoc.getId();
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
                    locationStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }            
        });
        
        locationLotNumber = (TextBox)def.getWidget(InventoryItemMeta.getLocationLotNumber());
        addScreenHandler(locationLotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                TableDataRow row;
                
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationLotNumber.setValue(null);
                        locationLotNumber.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                
                row = name.getSelection();
                item = (InventoryItemDO)row.data;
                                                            
                if (state == State.ADD && item != null) 
                    locationLotNumber.enable("N".equals(item.getIsBulk()));
                else
                    locationLotNumber.enable(false);
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                InventoryReceiptViewDO data;   
                InventoryLocationViewDO location;
                
                data = manager.getInventoryReceipt();
                location = data.getInventoryLocations().get(0);
                location.setLotNumber(event.getValue());     
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationLotNumber.enable(false);
            }
        });

        locationExpirationDate = (CalendarLookUp)def.getWidget(InventoryItemMeta.getLocationExpirationDate());
        addScreenHandler(locationExpirationDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemDO item;
                TableDataRow row;
                
                item = null;
                try {
                    if (manager == null || manager.getInventoryItem() == null) {
                        locationExpirationDate.setValue(null);
                        locationExpirationDate.enable(false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                
                row = name.getSelection();
                item = (InventoryItemDO)row.data;
                                                            
                if (state == State.ADD && item != null) 
                    locationExpirationDate.enable("N".equals(item.getIsBulk()));
                else
                    locationExpirationDate.enable(false);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                InventoryReceiptViewDO data;        
                InventoryLocationViewDO location;
                
                data = manager.getInventoryReceipt();
                location = data.getInventoryLocations().get(0);
                location.setExpirationDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationExpirationDate.enable(false);
            }
        });

        componentTable = (TableWidget)def.getWidget("componentTable");
        addScreenHandler(componentTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                componentTable.load(getTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                componentTable.enable(true);
            }
        });
        
        dispensedUnitsId = (Dropdown<Integer>)componentTable.getColumnWidget(InventoryItemMeta.getComponentDispensedUnitsId());
        componentLocationStorageLocationName  = (AutoComplete<Integer>)componentTable.getColumnWidget(InventoryItemMeta.getLocationStorageLocationName());

        componentLocationStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                Integer itemId, storeId;
                InventoryItemViewDO data;
                InventoryComponentViewDO component; 
                ArrayList<InventoryLocationViewDO> invLocList; 
                InventoryLocationViewDO invLoc;
                TableDataRow tableRow, autoRow;                
                ArrayList<TableDataRow> model;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                String param;    
                              
                tableRow = componentTable.getSelection();                     
                component = (InventoryComponentViewDO)tableRow.data;
                param = QueryFieldUtil.parseAutocomplete(event.getMatch());                                
                window.setBusy();
                model = new ArrayList<TableDataRow>();
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
                        autoRow = new TableDataRow(4);
                        invLoc = invLocList.get(i);

                        autoRow.key = invLoc.getId();
                        autoRow.cells.get(0).setValue(invLoc.getStorageLocationName() + ", " +
                                                  invLoc.getStorageLocationUnitDescription() + " " +
                                                  invLoc.getStorageLocationLocation());
                        autoRow.cells.get(1).setValue(invLoc.getLotNumber());
                        autoRow.cells.get(2).setValue(invLoc.getQuantityOnhand());
                        autoRow.cells.get(3).setValue(invLoc.getExpirationDate());

                        autoRow.data = invLoc;

                        model.add(autoRow);
                    }
                    
                    componentLocationStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
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
                TableDataRow autoRow, tableRow;
                InventoryComponentViewDO data;
                InventoryItemManager itemMan;
                InventoryLocationViewDO location;

                r = event.getRow();
                c = event.getCol();
                val = componentTable.getObject(r,c);
                tableRow = componentTable.getRow(r);
                data = null;
                try {
                    itemMan = manager.getInventoryItem();
                    if (itemMan == null)
                        return;
                    data = itemMan.getComponents().getComponentAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                switch(c) {
                    case 1:
                        autoRow = (TableDataRow)val;
                        if (autoRow == null) {
                            componentTable.setCell(r, 5, null);  
                            return;
                        }
                        
                        location = (InventoryLocationViewDO)autoRow.data;
                        data.setInventoryLocationId(location.getId());
                        data.setInventoryLocationLotNumber(location.getLotNumber());
                        data.setInventoryLocationQuantityOnhand(location.getQuantityOnhand());
                        data.setInventoryLocationStorageLocationLocation(location.getStorageLocationLocation());
                        data.setInventoryLocationStorageLocationName(location.getStorageLocationName());
                        data.setInventoryLocationStorageLocationUnitDescription(location.getStorageLocationUnitDescription());
                        componentTable.setCell(r, 5, location.getQuantityOnhand());
                        componentTable.setCell(r, 2, location.getLotNumber());
                        prevTotal = (Integer)componentTable.getObject(r, 4);
                                                              
                        if (prevTotal == null || prevTotal > location.getQuantityOnhand()){
                            factor = location.getQuantityOnhand() / data.getQuantity();
                            for (r = 0; r < componentTable.numRows(); r++) {
                                tableRow = componentTable.getRow(r);                                           
                                data = (InventoryComponentViewDO)tableRow.data;
                                data.setTotal(factor * data.getQuantity());
                                componentTable.setCell(r, 4, factor * data.getQuantity());                                
                            }                                 
                            numRequested.clearExceptions();
                            numRequested.setValue(factor);
                            manager.getInventoryReceipt().setQuantityReceived(factor);
                        }                    
                        break;
                }
            }
        });

        transferButton = (AppButton)def.getWidget("transferButton");
        addScreenHandler(transferButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                componentTable.finishEditing();
                showTransfer();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                transferButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
        
        screen = this;
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;       
        
        // units dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("inventory_unit");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        dispensedUnitsId.setModel(model);
    }
    
    protected void add() {
        InventoryReceiptViewDO data;
        
        manager = BuildKitManager.getInstance();        
        data = manager.getInventoryReceipt();
        data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
        data.getInventoryLocations().add(new InventoryLocationViewDO());
        
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
            
            showReportScreen();
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("commitAdd(): " + e.getMessage());
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
    
    protected void showReportScreen() {
        ScreenWindow modal;

        try {
            if (buildKitsReportScreen == null) {
                buildKitsReportScreen = new BuildKitsReportScreen();
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        try {
                            loadReportScreen();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                buildKitsReportScreen.reset();
                loadReportScreen();
            }

            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(consts.get("print"));
            modal.setContent(buildKitsReportScreen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReportScreen() throws Exception {
        OrderViewDO order;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;
        InventoryItemManager man;

        man = manager.getInventoryItem();
        order = manager.getOrder();
        receipt = manager.getInventoryReceipt();
        location = receipt.getInventoryLocations().get(0);

        buildKitsReportScreen.setFieldValue("LOT_NUMBER", location.getLotNumber());
        buildKitsReportScreen.setFieldValue("ORDER_ID", order.getId());
        buildKitsReportScreen.setFieldValue("CREATED_DATE", order.getOrderedDate());
        buildKitsReportScreen.setFieldValue("EXPIRED_DATE", location.getExpirationDate());
        buildKitsReportScreen.setFieldValue("ENDING_NUMBER", receipt.getQuantityReceived());
        buildKitsReportScreen.setFieldValue("KIT_DESCRIPTION", man.getInventoryItem().getName());
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        String location;
        InventoryComponentViewDO data;
        ArrayList<TableDataRow> model;
        InventoryComponentManager components;
        TableDataRow tableRow;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            if (manager.getInventoryItem() != null) {
                components = manager.getInventoryItem().getComponents();
                for (int i = 0; i < components.count(); i++ ) {
                    data = components.getComponentAt(i);
                    tableRow = new TableDataRow(6);
                    tableRow.cells.get(0).setValue(data.getComponentName());
                    tableRow.cells.get(3).setValue(data.getComponentDispensedUnitsId());  
                    tableRow.cells.get(4).setValue(data.getTotal());
                    if (data.getInventoryLocationId() != null) {
                        location = StorageLocationManager.getLocationForDisplay(data.getInventoryLocationStorageLocationName(),
                                                                                data.getInventoryLocationStorageLocationUnitDescription(),
                                                                                data.getInventoryLocationStorageLocationLocation());
                        tableRow.cells.get(1).setValue(new TableDataRow(data.getInventoryLocationId(), location));
                        tableRow.cells.get(2).setValue(data.getInventoryLocationLotNumber());
                        tableRow.cells.get(5).setValue(data.getInventoryLocationQuantityOnhand());
                    }
                    tableRow.data = data;
                    model.add(tableRow);
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private void showTransfer() {       
        ScreenWindow modal; 
        InventoryTransferManager manager;        

        manager = getInventoryTransferManager();
        if (manager == null)
            return;
        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("inventoryTransfer"));
        try {
            if (inventoryTransferScreen == null)
                inventoryTransferScreen = new InventoryTransferScreen(modal);   
            modal.setContent(inventoryTransferScreen);
            
            inventoryTransferScreen.loadTransferData(manager);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }        
    }  
    
    private InventoryTransferManager getInventoryTransferManager() {
        int selRows[];
        InventoryTransferManager itman;
        InventoryComponentManager icman;
        InventoryComponentViewDO comp;
        InventoryLocationViewDO loc;
        TableDataRow row;
        InventoryItemDO toItem, fromItem;
                
        selRows = componentTable.getSelectedRows();
        toItem = null;
        
        if (selRows.length == 0) {
            Window.alert(consts.get("selRowsToTransfer"));
            return null;
        }
           
        loc = null;
        itman = InventoryTransferManager.getInstance();
        try {
            icman = manager.getInventoryItem().getComponents();
            Arrays.sort(selRows);
            for (int i = 0; i < selRows.length; i++) {
                fromItem = null;
                
                comp = icman.getComponentAt(selRows[i]);
                itman.addTransfer();           
                
                toItem = InventoryItemCache.getById(comp.getComponentId());
                if (toItem.getParentInventoryItemId() == null) {
                    window.setError(consts.get("transferNotAllowed")+ " "+ toItem.getName());
                    return null;
                }
                    fromItem = InventoryItemCache.getById(toItem.getParentInventoryItemId());                
                
                itman.setToInventoryItemAt(toItem, i);                
                itman.setFromInventoryItemAt(fromItem, i);
                row = (TableDataRow)componentTable.getObject(selRows[i], 1);
                if (row != null) 
                    loc = (InventoryLocationViewDO)row.data;                
                itman.setToInventoryLocationAt(loc, i);                
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return itman;
    }
    
    private void setTotalInComponents(Integer val) {
        int i;
        Integer total, onHand;
        boolean changeTotal;
        TableDataRow row;
        InventoryComponentViewDO data;  
        
        manager.getInventoryReceipt().setQuantityReceived(val);
        if (val == null)
            return;
        
        if (val < 1) {
            numRequested.addException(new LocalizedException("numRequestedMoreThanZeroException"));
            return;
        } 
        
        if (componentTable.numRows() == 0)
            return;
                                        
        total = (Integer)componentTable.getObject(0, 4);
        
        if(total == null) {
            for (i = 0; i < componentTable.numRows(); i++) {
                row = componentTable.getRow(i);
                data = (InventoryComponentViewDO)row.data;
                data.setTotal(val * data.getQuantity());
                componentTable.setCell(i, 4, val * data.getQuantity());
            }
        } else {
            changeTotal = true;
            for (i = 0; i < componentTable.numRows(); i++) {
                row = componentTable.getRow(i);
                data = (InventoryComponentViewDO)row.data;
                onHand = (Integer)componentTable.getObject(i, 5);                                                
                if (onHand != null && onHand < (val * data.getQuantity())) {
                    changeTotal = false;
                    break;
                }                            
            }
            
            if (changeTotal) {
                for (i = 0; i < componentTable.numRows(); i++) {
                    row = componentTable.getRow(i);
                    data = (InventoryComponentViewDO)row.data;
                    data.setTotal(val * data.getQuantity());
                    componentTable.setCell(i, 4, val * data.getQuantity());                           
                }
                numRequested.clearExceptions();
            } else {
                numRequested.addException(new LocalizedException("qtyOnHandNotSufficientException"));
            }
        }
    }
}