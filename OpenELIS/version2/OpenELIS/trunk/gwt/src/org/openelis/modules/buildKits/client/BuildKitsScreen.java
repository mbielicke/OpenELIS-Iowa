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
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
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
import org.openelis.manager.BuildKitManager;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.modules.inventoryTransfer.client.InventoryTransferScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class BuildKitsScreen extends Screen {
    
    private SecurityModule          security;
    private BuildKitsScreen         screen;
    private InventoryTransferScreen inventoryTransferScreen;
    private BuildKitManager         manager;          
    private AppButton               addButton, commitButton, abortButton;
    private CalendarLookUp          locationExpirationDate;
    private TextBox                 numRequested, qcReference, locationLotNumber;
    private AutoComplete<Integer>   name, locationStorageLocationName,
                                    componentLocationStorageLocationName;
    private AppButton               transferButton;
    private CheckBox                addToExisting;
    private TableWidget             componentTable;
    private Dropdown<Integer>       dispensedUnitsId;
    private ScreenService           inventoryItemService, orderFillService, storageService;     
    
    public BuildKitsScreen() throws Exception {
        super((ScreenDefInt)GWT.create(BuildKitsDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.buildKits.server.BuildKitsService");
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");
        orderFillService = new ScreenService("controller?service=org.openelis.modules.orderFill.server.OrderFillService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        
        security = OpenELIS.security.getModule("buildkits");
        if (security == null)
            throw new SecurityException("screenPermException", "Build Kits Screen");

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
                manager.setInventoryItemId(event.getValue());                                                                                                    
                DataChangeEvent.fire(screen);
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
                        store = DictionaryCache.getEntryFromId(data.getStoreId());
                        units = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
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
                int i;
                Integer val, total, onHand;
                boolean changeTotal;
                TableDataRow row;
                InventoryComponentViewDO data;                
                
                val = event.getValue();
                                
                if (val == null || componentTable.numRows() == 0)
                    return;
                
                if (val < 1) {
                    numRequested.addException(new LocalizedException("numRequestedMoreThanZeroException"));
                    return;
                }
                
                manager.getInventoryReceipt().setQuantityReceived(val);                
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
                
                if (data.getInventoryLocations() == null) {
                    data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
                    data.getInventoryLocations().add(new InventoryLocationViewDO());
                }
                
                location = data.getInventoryLocations().get(0);
                if (row != null) {
                    location = (InventoryLocationViewDO)row.data;
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
                locationStorageLocationName.enable(false);
                locationStorageLocationName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                Integer itemId;
                InventoryItemViewDO data;
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
                String param;
                
                if(manager == null)
                    return;
                                              
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());  
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
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
                        invLocList = orderFillService.callList("fetchByLocationNameInventoryItemId", query);
                        for (i = 0; i < invLocList.size(); i++ ) {
                            row = new TableDataRow(4);
                            invLoc = invLocList.get(i);

                            row.key = invLoc.getId();
                            row.cells.get(0).setValue(invLoc.getStorageLocationName() + ", " +
                                                      invLoc.getStorageLocationUnitDescription() + " " +
                                                      invLoc.getStorageLocationLocation());
                            row.cells.get(1).setValue(invLoc.getLotNumber());
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
                            row.cells.get(0).setValue(storLoc.getName() + ", " +
                                                      storLoc.getStorageUnitDescription() + " " +
                                                      storLoc.getLocation());
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
                
                item = null;
                
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
                    locationLotNumber.enable(!"Y".equals(item.getIsBulk()));
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
                    locationExpirationDate.enable(!"Y".equals(item.getIsBulk()));
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
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                String param;    
                              
                tableRow = componentTable.getSelection();                     
                component = (InventoryComponentViewDO)tableRow.data;
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());  
                param = parser.getParameter().get(0); 
                                
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
                    invLocList = orderFillService.callList("fetchByLocationNameInventoryItemIdStoreId", query);
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
                InventoryLocationViewDO location;

                r = event.getRow();
                c = event.getCol();
                val = componentTable.getObject(r,c);
                tableRow = componentTable.getRow(r);
                try {
                    data = manager.getInventoryItem().getComponents().getComponentAt(r);
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
                            numRequested.setValue(factor);
                        }                    
                        break;
                }
            }
        });

        transferButton = (AppButton)def.getWidget("transferButton");
        addScreenHandler(transferButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showTransfer();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                transferButton.enable(false);
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
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;       
        
        // units dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_unit");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
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
            Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }        
    }

    protected void abort() {
        manager = BuildKitManager.getInstance();
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("addAborted"));
    }
    
    private ArrayList<TableDataRow> getTableModel() {
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
                    tableRow.cells.get(0).setValue(data.getComponentName() + " , " +
                                              data.getComponentDescription());
                    tableRow.cells.get(3).setValue(data.getComponentDispensedUnitsId());   
                    if (data.getInventoryLocationId() != null) {
                        tableRow.cells.get(1).setValue(new TableDataRow(data.getInventoryLocationId(),data.getInventoryLocationStorageLocationName() + ", " +
                                                       data.getInventoryLocationStorageLocationUnitDescription() + " " +
                                                       data.getInventoryLocationStorageLocationLocation()));
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
        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("inventoryTransfer"));
        try {
            if (inventoryTransferScreen == null)
                inventoryTransferScreen = new InventoryTransferScreen(modal);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }
        modal.setContent(inventoryTransferScreen);
    }    
}
/*

    private KeyListManager keyList = new KeyListManager();
    
    private AutoComplete kitDropdown, kitLocationDropdown;
    private TableWidget subItemsTable;
    private TextBox numRequestedText;
    private int currentTableRow = -1;
    private ScreenCheck addToExisiting;
    private Integer currentKitDropdownValue;
    private Integer lastLocValue;
    private AppButton transferButton;
    
    private InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    public BuildKitsScreen() {                
        super("org.openelis.modules.buildKits.server.BuildKitsService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new BuildKitsForm());
    }
    
    public void onClick(Widget sender) {
        if (sender == transferButton)
            onTransferRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        if(sender == kitDropdown && kitDropdown.getSelections().size() > 0 && !kitDropdown.getSelections().get(0).key.equals(currentKitDropdownValue)){
            currentKitDropdownValue = (Integer)kitDropdown.getSelections().get(0).key;

            // prepare the arguments
            //BuildKitsForm bkrpc = new BuildKitsForm();
            //bkrpc.screenKey = form.screenKey;
            form.kitId = currentKitDropdownValue;
            //bkrpc = form;
            
            screenService.call("getComponentsFromId", form, new AsyncCallback<BuildKitsForm>() {
                public void onSuccess(BuildKitsForm result) {
                   subItemsTable.model.clear();

                   for(int i=0; i<result.subItemsModel.size(); i++){
                       TableDataRow<Integer> set = result.subItemsModel.get(i);
                       TableDataRow<Integer> tableRow = subItemsTable.model.createRow();
                       //id
                       //name
                       //qty
                       tableRow.key = set.key;
                       tableRow.cells[0].setValue(set.cells[0].getValue());
                       tableRow.cells[3].setValue(set.cells[1].getValue());
                       
                       if(numRequestedText.getText() != null && !"".equals(numRequestedText.getText())){
                           Integer unit = new Integer((int)((Double)((DoubleField)tableRow.cells[3]).getValue()).doubleValue());
                           tableRow.cells[4].setValue(unit * Integer.valueOf(numRequestedText.getText()));
                       }
                       
                       subItemsTable.model.addRow(tableRow);
                   }
                }
                
                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
        }else if(sender == numRequestedText){
            //if the kit isnt selected do nothing
            if(!"".equals(numRequestedText.getText()) && kitDropdown.getSelections().size() > 0){
                Integer requested = Integer.valueOf(numRequestedText.getText());
                
                TableModel model = (TableModel)subItemsTable.model;

                for(int i=0; i<model.numRows(); i++){

                    if(model.getCell(i, 1) == null){
                        //we just take unit times num requested 
                        Integer unit = new Integer((int)((Double)((DoubleField)model.getObject(i, 3)).getValue()).doubleValue());
                        IntegerField total = (IntegerField)model.getObject(i, 4);
                        
                        total.setValue(unit * requested);
                        
                        subItemsTable.model.setCell(i, 4, total.getValue());
                        
                    }else{
                        //we need to look at the quantity on hand to make sure we can build the requested number of kits
                        Integer unit = new Integer((int)((Double)model.getCell(i, 3)).doubleValue());
                        Integer qtyOnHand = (Integer)model.getCell(i, 5);

                        Integer totalProposed = unit * requested;
                        IntegerField total = (IntegerField)model.getObject(i, 4);

                        if(totalProposed.compareTo(qtyOnHand) > 0){
                            total.setValue(new Integer(totalProposed));
                            subItemsTable.model.clearCellError(i, 4);
                            subItemsTable.model.setCellError(i, 4, consts.get("totalIsGreaterThanOnHandException"));
                        }else{
                            subItemsTable.model.clearCellError(i, 4);
                            total.setValue(totalProposed);
                        }
                        
                        subItemsTable.model.setCell(i, 4, total.getValue());
                    }
                }
            }
        }
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        kitDropdown = (AutoComplete)getWidget(InventoryItemMeta.getName());
        
        kitLocationDropdown = (AutoComplete)getWidget(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation());
         
        numRequestedText = (TextBox)getWidget("numRequested");
        
        startWidget = (ScreenInputWidget)widgets.get(InventoryItemMeta.getName());
        addToExisiting = (ScreenCheck)widgets.get("addToExisting");
        
        transferButton = (AppButton)getWidget("transferButton");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        subItemsTable = (TableWidget)getWidget("subItemsTable");
        subItemsTable.addTableWidgetListener(this);
        
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        super.afterDraw(success);
    }
    
    public void commit() {
        super.commit();
        currentKitDropdownValue = null;
    }
    
    protected SyncCallback afterCommitUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            subItemsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected SyncCallback afterCommitAdd = new SyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          subItemsTable.model.enableAutoAdd(false);
      }   
  };
    
    public void abort() {
        if(state == State.ADD){
          //unlock the record
            InvReceiptItemInfoForm iriif = new InvReceiptItemInfoForm();
            iriif.lockedIds = getLockedSetsFromTable();

            screenService.call("unlockLocations", iriif, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    superAbort();
                    
               }

               public void onFailure(Throwable caught) {
                   Window.alert(caught.getMessage());
               }
                });
        }else
            super.abort();
        currentKitDropdownValue = null;
    }
    
    private void superAbort(){
        super.abort();
    }

    //
    // start table manager methods
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        currentTableRow = row;
        if(col == 4)
            return false;
        
        return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD)           
            return true;
        return false;
    }
    
    //
    //end table manager methods
    //
    
    //
    //start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        DropDownField<Object> locationField;
        if(col == 1 && row < subItemsTable.model.numRows()){
            Integer currentLocValue = (Integer)((DropDownField)subItemsTable.model.getObject(row, col)).getSelectedKey();
            if((lastLocValue == null && currentLocValue != null) || 
                            (lastLocValue != null && !lastLocValue.equals(currentLocValue))){
                    //we need to send lock/fetch call back
                    BuildKitsForm bkf = new BuildKitsForm();
                    bkf.lastLocId = lastLocValue;
                    bkf.locId = currentLocValue;
                    screenService.call("fetchLocationAndLock", bkf, new AsyncCallback<BuildKitsForm>() {
                        public void onSuccess(BuildKitsForm result) {
                            TableDataRow<Integer> tableRow = subItemsTable.model.getRow(row);
                            TableDataRow<Integer> selectedRow = ((ArrayList<TableDataRow<Integer>>)((DropDownField<Integer>)tableRow.getCells().get(1)).getValue()).get(0);
                            
                            subItemsTable.model.setCell(row, 2, ((StringObject)selectedRow.getCells().get(1)).getValue());
                            subItemsTable.model.setCell(row, 5, result.qtyOnHand);
                            
                            if(subItemsTable.model.getCell(row, 4) != null && ((Integer)subItemsTable.model.getCell(row, 5)).compareTo((Integer)subItemsTable.model.getCell(row, 4)) < 0){
                                subItemsTable.model.clearCellError(row, 4);
                                subItemsTable.model.setCellError(row, 4, consts.get("totalIsGreaterThanOnHandException"));
                            }else
                                subItemsTable.model.clearCellError(row, 4);
                        }

                        public void onFailure(Throwable caught) {
                            Window.alert(caught.getMessage());
                            //clear row
                            TableDataRow<Integer> tableRow = subItemsTable.model.getRow(row);
                            ((DropDownField<Integer>)tableRow.cells[1]).setModel(null);
                            ((DropDownField<Integer>)tableRow.cells[1]).clear();
                            tableRow.cells[2].setValue(null);
                            tableRow.cells[5].setValue(null);
                            
                            subItemsTable.model.refresh();
                        }
                    });
                }
            }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(col == 1 && row < subItemsTable.model.numRows())
            lastLocValue = (Integer)((DropDownField<Integer>)subItemsTable.model.getObject(row, col)).getSelectedKey();
         
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table widget listener methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
    	SubLocationAutoRPC autoRPC = new SubLocationAutoRPC();
    	autoRPC.cat = widget.cat;
    	autoRPC.match = text;
    	
        if(widget == kitLocationDropdown){
            autoRPC.addToExisting = ((CheckBox)addToExisiting.getWidget()).getState();    
        }else{
            autoRPC.id = (Integer) subItemsTable.model.getRow(currentTableRow).key;
        }
        
        screenService.call("getMatchesObj", autoRPC, new AsyncCallback<SubLocationAutoRPC>() {
            public void onSuccess(SubLocationAutoRPC result) {
                widget.showAutoMatches(result.matchesModel);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }
    
    protected void validate(AbstractField field) {
        if("numRequested".equals(field.key)){
            try{
                Integer requested = Integer.valueOf(numRequestedText.getText());
                boolean setError = false;
                TableModel model = (TableModel)subItemsTable.model;
                
                for(int i=0; i<model.numRows(); i++){
                    Integer unit = new Integer((int)((Double)model.getCell(i, 3)).doubleValue());
                    Integer qtyOnHand = (Integer)model.getCell(i, 5);
                    Integer totalProposed = unit * requested;
                    
                    if(totalProposed.compareTo(qtyOnHand) > 0){
                        setError = true;
                        break;
                    }
                }
                    
                if(setError)
                    form.numRequested.addError("Transfer in more components or lower the number requested. Not enough quantity on hand.");
            }catch(Exception e){
                //do nothing
            }
        }
    }
    
    private void onTransferRowButtonClick() {
        Object[] args = new Object[1];
        args[0] = new StringObject("transfer");
        /*
        PopupPanel inventoryTransferPopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(inventoryTransferPopupPanel,
                                                     "Inventory Transfer",
                                                     "inventoryTransferScreen",
                                                     "Loading...");
        
        pickerWindow.setContent(new InventoryReceiptScreen(args));

        inventoryTransferPopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        inventoryTransferPopupPanel.setPopupPosition(left, top);
        inventoryTransferPopupPanel.show();

        ScreenWindow modal = new ScreenWindow(null,"Inventory Transfer","inventoryTransferScreen","Loading...",true);
        modal.setName(consts.get("inventoryTransfer"));
        modal.setContent(new InventoryReceiptScreen(args));
        
    }
    
    private TableDataModel<TableDataRow<Integer>> getLockedSetsFromTable(){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        for(int i=0; i<subItemsTable.model.numRows(); i++){
            ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)subItemsTable.model.getObject(i, 1)).getValue();
            
            if(selections != null && selections.size() == 1){
                returnModel.add(new TableDataRow<Integer>(selections.get(0).key));
            }
        }
            
        return returnModel;
    }
*/