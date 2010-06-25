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
package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.data.Query;
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
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.meta.InventoryReceiptMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ItemTab extends Screen implements HasActionHandlers<ItemTab.Action>{

    private InventoryReceiptManager               manager;
    private InventoryReceiptScreen                inventoryReceiptScreen;
    private ItemTab                               screen; 
    private CalendarLookUp                        inventoryLocationExpirationDate;
    private AutoComplete<Integer>                 inventoryLocationStorageLocationName;
    private TextBox                               inventoryItemDescription, 
                                                  inventoryLocationLotNumber, qcReference, 
                                                  inventoryItemStore, inventoryItemDispensedUnits;
    private CheckBox                              addToExisting;
    private int                                   index;
    private boolean                               loaded;
    
    private ScreenService                         orderFillService, storageService;
    
    public enum Action {
        STORAGE_LOCATION_CHANGED, LOT_NUMBER_CHANGED 
    }
    
    public ItemTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");              
        orderFillService = new ScreenService("controller?service=org.openelis.modules.orderFill.server.OrderFillService");
        storageService = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        
        setDefinition(def);
        setWindow(window);
        initialize();       
    }

    private void initialize() {
        inventoryItemDescription = (TextBox)def.getWidget(InventoryReceiptMeta.getInventoryItemDescription());
        addScreenHandler(inventoryItemDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;      
                
                item = null;
                if (manager != null && index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());                        
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }                      
                    if (item != null)
                        inventoryItemDescription.setValue(item.getDescription());
                    else
                        inventoryItemDescription.setValue(null);
                } else {
                    inventoryItemDescription.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemDescription.enable(false);                
            }
        });

        addToExisting = (CheckBox)def.getWidget("addToExisting");
        addScreenHandler(addToExisting, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;      
                boolean enable;
                
                item = null;
                if (manager != null && index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId()); 
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }
                                                            
                    if (state == State.ADD || state == State.UPDATE) {
                        if(item != null) {
                            enable = "Y".equals(item.getIsBulk());
                            addToExisting.enable(enable);
                            inventoryLocationLotNumber.enable(!enable);
                            inventoryLocationExpirationDate.enable(!enable);
                        } else {
                            addToExisting.enable(false);
                            inventoryLocationLotNumber.enable(false);
                            inventoryLocationExpirationDate.enable(false);
                        }
                    }
                } else {
                    addToExisting.setValue("N");
                }                                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                InventoryReceiptViewDO data;
                
                try {
                    data = manager.getReceiptAt(index);  
                    data.setAddToExistingLocation(event.getValue());
                } catch(Exception ex) {
                    ex.printStackTrace();
                    Window.alert(ex.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;
                                           
                item = null;
                if (state != State.ADD && state != State.UPDATE) {
                    addToExisting.enable(false);
                } else if (manager != null &&  index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());                        
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }
                    
                    if (item != null)  
                        addToExisting.enable("Y".equals(item.getIsBulk()));                                            
                } else {
                    addToExisting.enable(false);
                }
            }
        });

        inventoryItemStore = (TextBox)def.getWidget(InventoryReceiptMeta.getInventoryItemStoreId());
        addScreenHandler(inventoryItemStore, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;      
                DictionaryDO dict;
                
                item = null;
                dict = null;
                if (manager != null && index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());
                        if (item != null)
                            dict = DictionaryCache.getEntryFromId(item.getStoreId());
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }                         
                    if (dict != null)
                        inventoryItemStore.setValue(dict.getEntry());
                    else
                        inventoryItemStore.setValue(null);
                } else {
                    inventoryItemStore.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemStore.enable(false);
            }
        });

        inventoryItemDispensedUnits = (TextBox)def.getWidget(InventoryReceiptMeta.getInventoryItemDispensedUnitsId());
        addScreenHandler(inventoryItemDispensedUnits, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;      
                DictionaryDO dict;
                
                item = null;
                dict = null;
                if (manager != null && index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());
                        if (item != null && item.getDispensedUnitsId() != null)
                            dict = DictionaryCache.getEntryFromId(item.getDispensedUnitsId());
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }                        
                    if (dict != null)                     
                        inventoryItemDispensedUnits.setValue(dict.getEntry());
                    else 
                        inventoryItemDispensedUnits.setValue(null);                    
                } else {
                    inventoryItemDispensedUnits.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItemDispensedUnits.enable(false);
            }
        });

        inventoryLocationStorageLocationName = (AutoComplete)def.getWidget(InventoryReceiptMeta.getInventoryLocationStorageLocationId());
        addScreenHandler(inventoryLocationStorageLocationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryLocationViewDO location;
                InventoryReceiptViewDO data;
                Integer storLocId;

                data = null;
                storLocId = null;
                if (manager != null && index != -1) {
                    if(state == State.ADD || state == State.UPDATE)
                        inventoryLocationStorageLocationName.enable(true);
                    
                    data = manager.getReceiptAt(index);
                    if (data.getInventoryLocations() == null) {
                        data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
                        data.getInventoryLocations().add(new InventoryLocationViewDO());
                    }
                    location = data.getInventoryLocations().get(0);
                    storLocId = location.getStorageLocationId();

                    if (storLocId != null)
                        inventoryLocationStorageLocationName.setSelection(storLocId,
                                                                          location.getStorageLocationName() + ", " +
                                                                          location.getStorageLocationUnitDescription() + " " +
                                                                          location.getStorageLocationLocation());
                    else
                        inventoryLocationStorageLocationName.setSelection(null, "");
                } else {
                    inventoryLocationStorageLocationName.setSelection(null, "");
                }
                
                
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                InventoryReceiptViewDO data; 
                InventoryLocationViewDO location;
                TableDataRow row;               
                
                data = manager.getReceiptAt(index);
                row = inventoryLocationStorageLocationName.getSelection();
                
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
                inventoryLocationLotNumber.setValue(location.getLotNumber());
                ActionEvent.fire(screen, Action.STORAGE_LOCATION_CHANGED, location.getStorageLocationId());
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                if(state != State.ADD && state != State.UPDATE) {
                    inventoryLocationStorageLocationName.enable(false);
                } else if(manager != null &&  index != -1) {
                    inventoryLocationStorageLocationName.enable(true);
                } else {
                    inventoryLocationStorageLocationName.enable(false);
                }                
            }
        });
                
        inventoryLocationStorageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                Integer itemId;
                InventoryReceiptViewDO data;
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
                ArrayList<Integer> storLocIdList;
                
                if(index == -1)
                    return;
                               
                data = manager.getReceiptAt(index);
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());  
                param = parser.getParameter().get(0); 
                                
                window.setBusy();
                model = new ArrayList<TableDataRow>();
                try {
                    storLocIdList = new ArrayList<Integer>();
                    itemId = data.getInventoryItemId();
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
                            storLocIdList.add(invLoc.getStorageLocationId());

                            model.add(row);
                        }
                    } else {
                        storLocList = storageService.callList("fetchAvailableByName", param);
                        for (i = 0; i < storLocList.size(); i++ ) {
                            row = new TableDataRow(4);
                            storLoc = storLocList.get(i);
                            if ( !storLocIdList.contains(storLoc.getId())) {
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
                    }
                    inventoryLocationStorageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }            
        });

        inventoryLocationLotNumber = (TextBox)def.getWidget(InventoryReceiptMeta.getInventoryLocationLotNumber());
        addScreenHandler(inventoryLocationLotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                
                InventoryReceiptViewDO data; 
                InventoryLocationViewDO location;

                data = null;
                if (manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index);      
                    if (data.getInventoryLocations() == null) {
                        data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
                        data.getInventoryLocations().add(new InventoryLocationViewDO());
                    }
                    location = data.getInventoryLocations().get(0);                                          
                    inventoryLocationLotNumber.setValue(location.getLotNumber());                    
                } else {
                    inventoryLocationLotNumber.setValue(null);                
                }                               
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                InventoryReceiptViewDO data;   
                InventoryLocationViewDO location;
                
                data = manager.getReceiptAt(index);
                location = data.getInventoryLocations().get(0);
                location.setLotNumber(event.getValue());     
                
                ActionEvent.fire(screen, Action.LOT_NUMBER_CHANGED, location.getLotNumber());
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;                     
                
                item = null;
                if (state != State.ADD && state != State.UPDATE) {
                    inventoryLocationLotNumber.enable(false);
                } else if (manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index);
                    item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());                                                                
                    if(item != null) 
                        inventoryLocationLotNumber.enable("N".equals(item.getIsBulk()));                    
                } else {
                    inventoryLocationLotNumber.enable(false);
                }
            }
        });

        qcReference = (TextBox)def.getWidget(InventoryReceiptMeta.getQcReference());
        addScreenHandler(qcReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryReceiptViewDO data;      

                data = null;
                if (manager != null && index != -1) {  
                    if (state == State.ADD || state == State.UPDATE)
                        qcReference.enable(true);
                    data = manager.getReceiptAt(index);                                                                   
                    qcReference.setValue(data.getQcReference());                    
                } else {
                    qcReference.setValue(null);                
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                InventoryReceiptViewDO data;                
                
                data = manager.getReceiptAt(index);
                data.setQcReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                if (state != State.ADD && state != State.UPDATE) {
                    qcReference.enable(false);
                } else if(manager != null &&  index != -1) {
                    qcReference.enable(true);
                } else {
                    qcReference.enable(false);
                }                
            }
        });

        inventoryLocationExpirationDate = (CalendarLookUp)def.getWidget(InventoryReceiptMeta.getInventoryLocationExpirationDate());
        addScreenHandler(inventoryLocationExpirationDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                InventoryReceiptViewDO data;      
                InventoryLocationViewDO location;

                data = null;
                if (manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index);    
                    if (data.getInventoryLocations() == null) {
                        data.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
                        data.getInventoryLocations().add(new InventoryLocationViewDO());
                    }
                    location = data.getInventoryLocations().get(0);                                           
                    inventoryLocationExpirationDate.setValue(location.getExpirationDate());                    
                } else {
                    inventoryLocationExpirationDate.setValue(null);                
                }
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                InventoryReceiptViewDO data;        
                InventoryLocationViewDO location;
                
                data = manager.getReceiptAt(index);
                location = data.getInventoryLocations().get(0);
                location.setExpirationDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                InventoryItemViewDO item;
                InventoryReceiptViewDO data;                     
                
                item = null;
                if (state != State.ADD && state != State.UPDATE) {
                    inventoryLocationExpirationDate.enable(false);
                } else if(manager != null && index != -1) {
                    try {
                        data = manager.getReceiptAt(index);
                        item = inventoryReceiptScreen.getInventoryItem(data.getInventoryItemId());                        
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        Window.alert(ex.getMessage());
                    }
                    
                    if (item != null) 
                        inventoryLocationExpirationDate.enable("N".equals(item.getIsBulk()));                    
                } else {
                    inventoryLocationExpirationDate.enable(false);
                }
            }
        });
        
        index = -1;
        screen = this;
    }   
    
    public void setManager(InventoryReceiptManager manager, int index, 
                           InventoryReceiptScreen inventoryReceiptScreen) {                
        this.manager = manager;
        this.index = index;  
        this.inventoryReceiptScreen = inventoryReceiptScreen;
                      
        loaded = false;
    }
                           
    public void draw() {
        if ( !loaded) 
            DataChangeEvent.fire(this);        

        loaded = true;        
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<ItemTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

}
