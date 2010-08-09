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
package org.openelis.modules.orderFill.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class ItemTab extends Screen {

    private OrderManager                   manager;
    private OrderFillScreen                orderFillScreen;
    private TreeWidget                     itemsTree;
    private Dropdown<Integer>              costCenterId;
    private TextBox                        organizationAttention,
                                           organizationAddressMultipleUnit, organizationAddressStreetAddress,
                                           organizationAddressCity, organizationAddressState, organizationAddressZipCode;
    private AutoComplete<Integer>          storageLocationName;
    private AppButton                      removeItemButton, addItemButton;

    private boolean                        loaded;
    private HashMap<Integer, OrderManager> combinedMap;   
    private ScreenService                  inventoryLocationService;                 
    
    public ItemTab(ScreenDefInt def, ScreenWindow window, OrderFillScreen orderFillScreen) {
        inventoryLocationService = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryLocationService");              
        this.orderFillScreen = orderFillScreen;
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        costCenterId = (Dropdown)def.getWidget(OrderMeta.getCostCenterId());
        addScreenHandler(costCenterId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                if(manager != null)
                    costCenterId.setSelection(manager.getOrder().getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {               
                manager.getOrder().setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                costCenterId.enable(EnumSet.of(State.UPDATE).contains(event.getState()));                
            }
        });

        organizationAttention = (TextBox)def.getWidget(OrderMeta.getOrganizationAttention());
        addScreenHandler(organizationAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if(manager != null)
                    organizationAttention.setValue(manager.getOrder().getOrganizationAttention());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setOrganizationAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAttention.enable(EnumSet.of(State.UPDATE).contains(event.getState()));                
            }
        });

        organizationAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressMultipleUnit());
        addScreenHandler(organizationAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                if(manager != null) {
                    data = manager.getOrder().getOrganization();
                    if(data != null)
                        organizationAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    else 
                        organizationAddressMultipleUnit.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressMultipleUnit.enable(false);
                organizationAddressMultipleUnit.setQueryMode(false);
            }
        });

        organizationAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressStreetAddress());
        addScreenHandler(organizationAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                
                if (manager != null) {
                    data = manager.getOrder().getOrganization();
                    if (data != null)
                        organizationAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    else 
                        organizationAddressStreetAddress.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressStreetAddress.enable(false);
                organizationAddressStreetAddress.setQueryMode(false);
            }
        });

        organizationAddressCity = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressCity());
        addScreenHandler(organizationAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                
                if (manager != null) {
                    data = manager.getOrder().getOrganization();
                    if (data != null)
                        organizationAddressCity.setValue(data.getAddress().getCity());
                    else
                        organizationAddressCity.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressCity.enable(false);
                organizationAddressCity.setQueryMode(false);
            }
        });

        organizationAddressState = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressState());
        addScreenHandler(organizationAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                
                if (manager != null) {
                    data = manager.getOrder().getOrganization();
                    if (data != null)
                        organizationAddressState.setValue(data.getAddress().getState());
                    else 
                        organizationAddressState.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressState.enable(false);
                organizationAddressState.setQueryMode(false);
            }
        });

        organizationAddressZipCode = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressZipCode());
        addScreenHandler(organizationAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                
                if (manager != null) {
                    data = manager.getOrder().getOrganization();
                    if (data != null)
                        organizationAddressZipCode.setValue(data.getAddress().getZipCode());
                    else
                        organizationAddressZipCode.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressZipCode.enable(false);
                organizationAddressZipCode.setQueryMode(false);
            }            
        });
        
        itemsTree = (TreeWidget)def.getWidget("itemsTree");
        addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                boolean present;
                
                itemsTree.finishEditing();
                itemsTree.load(getTreeModel());                 
                if(state == State.UPDATE) {
                    //
                    // we don't allow users to add or remove items to or from 
                    // the tree if the order represented by manager isn't selected
                    // to be processed  
                    //
                    present = combinedMap.containsKey(manager.getOrder().getId());                    
                    removeItemButton.enable(present);
                    addItemButton.enable(present);                    
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTree.enable(true);
            }
        });
        
        itemsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                TreeDataItem item;
                OrderItemViewDO data;
                OrderManager man;
                int c;

                if(state != State.UPDATE) {
                    event.cancel();
                    return;
                }
                
                item = itemsTree.getSelection();
                c = event.getCol();
                
                if("top".equals(item.leafType)) {
                    data = (OrderItemViewDO)item.key;
                    if(combinedMap == null) {                     
                        event.cancel();
                    } else { 
                        man = combinedMap.get(data.getOrderId());
                        if(c != 1 || man == null)
                            event.cancel();
                    }
                } else if(c != 1 && c != 3){
                    event.cancel();
                }                                            
            }            
        });
        
        itemsTree.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {                
                TableDataRow row;
                InventoryLocationViewDO loc;
                InventoryXUseViewDO fill;
                OrderItemViewDO item;
                TreeDataItem child, parent;
                Integer qty;
                int r, c;

                c = event.getCol();
                r = itemsTree.getSelectedRow();
                child = itemsTree.getRow(event.getRow());
                
                if ("orderItem".equals(child.leafType)) {                    
                    fill = (InventoryXUseViewDO)child.data;
                    parent = child.parent;
                    item = (OrderItemViewDO)parent.key;

                    if (c == 1) {
                        qty = (Integer)child.cells.get(1).getValue();
                        fill.setQuantity(qty);                        
                        validateLocationRow(child, r);                        
                        
                        itemsTree.refreshRow(child);
                    } else {
                        row = (TableDataRow)child.cells.get(3).getValue();
                        if (row != null) {
                            loc = (InventoryLocationViewDO)row.data;
                            child.cells.get(4).setValue(loc.getLotNumber());
                            child.cells.get(5).setValue(loc.getExpirationDate());
                            fill.setInventoryItemId(item.getInventoryItemId());
                            fill.setStorageLocationId(loc.getStorageLocationId());
                            fill.setStorageLocationName(loc.getStorageLocationName());
                            fill.setInventoryLocationId(loc.getId());
                            fill.setInventoryLocationLotNumber(loc.getLotNumber());
                            fill.setInventoryLocationQuantityOnhand(loc.getQuantityOnhand());                            
                        } else {
                            child.cells.get(4).setValue(null);
                            child.cells.get(5).setValue(null);
                            fill.setInventoryItemId(null);
                            fill.setStorageLocationId(null);
                            fill.setStorageLocationName(null);
                            fill.setInventoryLocationId(null);
                            fill.setInventoryLocationLotNumber(null);
                            fill.setInventoryLocationQuantityOnhand(null);
                        }
                        
                        validateLocationRow(child, r);                        
                        itemsTree.refreshRow(child);
                    }
                } else if (c == 1) {
                    qty = (Integer)child.cells.get(1).getValue();
                    item = (OrderItemViewDO)child.key;
                    item.setQuantity(qty);
                    validateItemRow(child, r);
                    itemsTree.refreshRow(child);
                }
            }            
        });
        
        itemsTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                TreeDataItem item;
                
                item = event.getItem();         
                
                if("top".equals(item.leafType) && item.data == null) 
                    loadChildItems(item);                                                           
            }            
        });      
        
        itemsTree.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                OrderManager man;
                OrderItemViewDO item;
                InventoryXUseViewDO fill;
                OrderFillManager fills;
                TreeDataItem row, parent;
                Integer sum, quantity, difference;
                
                row = (TreeDataItem)event.getRow();
                parent = row.parent;
                item = (OrderItemViewDO)parent.key;                
                man = combinedMap.get(item.getOrderId());
                
                try {
                    sum = getSumOfFillQuantityForItem(parent);
                    
                    fills = man.getFills();
                    fill = fills.getFillAt(fills.addFill());
                    
                    quantity = item.getQuantity();
                    if(quantity != null && quantity >= sum)
                        difference = quantity - sum;
                    else 
                        difference = 0;
                    
                    fill.setQuantity(difference);                    
                    fill.setInventoryItemId(item.getInventoryItemId());
                    fill.setInventoryItemName(item.getInventoryItemName());
                    fill.setOrderItemId(item.getId());
                    fill.setOrderItemOrderId(item.getOrderId());
                    
                    row.data = fill;        
                    
                    row.cells.get(1).setValue(difference);
                    parent.cells.get(1).clearExceptions();
                    itemsTree.refreshRow(row);
                    itemsTree.refreshRow(parent);                                                       
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }            
        });
        
        storageLocationName = (AutoComplete<Integer>)itemsTree.getColumns().get("orderItem").get(3).getColumnWidget();
        storageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                TreeDataItem item;
                InventoryLocationViewDO data;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<TableDataRow> model;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                OrderItemViewDO key;                
                
                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());               
                                
                field = new QueryData();
                field.query = parser.getParameter().get(0);
                fields.add(field);
                
                item = itemsTree.getSelection();
                key = (OrderItemViewDO)item.parent.key;
                
                field = new QueryData();
                field.query = Integer.toString(key.getInventoryItemId());
                fields.add(field);
                
                query.setFields(fields);

                window.setBusy();
                try {
                    
                    list = inventoryLocationService.callList("fetchByLocationNameInventoryItemId", query);
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).setValue(data.getStorageLocationName() +", "+ 
                                                  data.getStorageLocationUnitDescription()+" "+
                                                  data.getStorageLocationLocation());
                        row.cells.get(1).setValue(data.getLotNumber());
                        row.cells.get(2).setValue(data.getQuantityOnhand());
                        row.cells.get(3).setValue(data.getExpirationDate());
                                                
                        row.data = data;
                        
                        model.add(row);
                    }
                    
                    storageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }            
        });
        
        removeItemButton = (AppButton)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                TreeDataItem item, parent;
                InventoryXUseViewDO data;     
                OrderManager man;
                OrderFillManager fills;
                
                item = itemsTree.getSelection();
                
                if(item == null) 
                    return;
                
                if("top".equals(item.leafType)) {                    
                    window.setStatus(consts.get("qtyAdjustedItemNotRemoved"), "");
                } else {
                    data = (InventoryXUseViewDO)item.data;
                    man = combinedMap.get(data.getOrderItemOrderId());
                    itemsTree.deleteRow(item);
                    try {
                        fills = man.getFills();
                        fills.removeFill(data);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                    parent = item.parent;
                    validateItemRow(parent,-1); 
                    if(parent.getItems().size() > 0) 
                        parent.checkForChildren(false);
                    
                    itemsTree.refreshRow(parent);
                }                                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.enable(false);
            }
        });

        addItemButton = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                TreeDataItem item, child, parent;
                OrderItemViewDO data;
                InventoryItemDO invItem;
                int row;
                                
                item = itemsTree.getSelection();
                if(item == null) 
                    return;           
                
                itemsTree.finishEditing();
                if("top".equals(item.leafType)) {
                    data = (OrderItemViewDO)item.key;
                    invItem = orderFillScreen.getInventoryItem(data.getInventoryItemId());                    
                    if(!item.open)
                        itemsTree.toggle(item);
                    
                    if ("Y".equals(invItem.getIsNotInventoried())) {
                        Window.alert(consts.get("itemFlagDontInvCantBeFilled"));
                        return;
                    }
                                                            
                    child = createAndAddChildToParent((OrderItemViewDO)item.key, item);                    
                    itemsTree.select(child);    
                    row = itemsTree.getSelectedRow();
                    validateLocationRow(child, row);
                    itemsTree.startEditing(row, 3);        
                } else {
                    parent = item.parent;
                    child  = createAndAddChildToParent((OrderItemViewDO)parent.key, parent);
                    itemsTree.select(child);
                    row = itemsTree.getSelectedRow();
                    validateLocationRow(child, row);
                    itemsTree.startEditing(row, 3);                    
                }                                               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.enable(false);
            }
        });
    }
        
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("cost_centers"); 
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        costCenterId.setModel(model);
    }
    
    public void setManager(OrderManager manager, HashMap<Integer, OrderManager> combinedMap) {        
        this.manager = manager;
        this.combinedMap = combinedMap;        
        loaded = false;
    }

    public void draw() {
        if ( !loaded) 
            DataChangeEvent.fire(this);        

        loaded = true;
    }
    
    public boolean validate() {
        TreeDataItem parent, child;
        ArrayList<TreeDataItem> model, items;
        OrderItemViewDO item;
        InventoryItemDO invItem;
        Integer quantity;
        boolean validate;
        int i;
        
        validate = true;
        
        if (combinedMap != null && combinedMap.get(manager.getOrder().getId()) != null) {
            itemsTree.finishEditing();
            model = itemsTree.getData();
                        
            for (i = 0; i < model.size(); i++ ) {
                parent = model.get(i);
                item = (OrderItemViewDO)parent.key;
                invItem = orderFillScreen.getInventoryItem(item.getInventoryItemId());
                
                if ("Y".equals(invItem.getIsNotInventoried())) 
                    continue;
                
                if ( !parent.open)
                    itemsTree.toggle(parent);

                items = parent.getItems();                                
                quantity = item.getQuantity();

                if (items != null && items.size() > 0) {
                    for (int j = 0; j < items.size(); j++ ) {
                        child = items.get(j);
                        if ( !validateLocationRow(child, -1)) {
                            itemsTree.refreshRow(child);
                            itemsTree.refreshRow(parent);
                            validate = false;
                        }
                    }
                } else if ( (quantity == null) || (quantity != null && quantity > 0)) {
                    parent.cells.get(1).addException(new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
                    itemsTree.refreshRow(parent);
                    validate = false;
                }
            }
        }
        return validate;
    }
    
    private ArrayList<TreeDataItem> getTreeModel() {
        ArrayList<TreeDataItem> model;
        TreeDataItem item;
        OrderManager man;
        OrderItemManager itemMan;        
        OrderItemViewDO data, key;
        HashMap<Integer, Boolean> itemPresentMap;
        Boolean itemPresent;        
        Set<Integer> set; 
        Iterator<Integer> iter;
        int count, i, j;        
        
        if(combinedMap == null)
            return new ArrayList<TreeDataItem>();
        
        man = combinedMap.get(manager.getOrder().getId());
        model = new ArrayList<TreeDataItem>();
        
        try {            
            if(man == null) {                    
                organizationAttention.enable(false);
                costCenterId.enable(false);
                
                itemMan = manager.getItems();
                count = itemMan.count();                
                for (i = 0; i < count; i++ ) {
                    data = itemMan.getItemAt(i);
                    item = getTopLevelItem(data);
                    model.add(item);
                }                          
                return model;                
            }           

            //
            // we have to make sure that "manager" belongs to the list of  managers 
            // being processed before allowing users to edit the data in these two
            // widgets, because only then the record represented by "manager" will             
            // have been locked 
            //
            organizationAttention.enable(true);
            costCenterId.enable(true);
            
            set = combinedMap.keySet();
            iter = set.iterator();
            itemPresent = true;
            
            while (iter.hasNext())  {                              
                itemPresentMap = new HashMap<Integer, Boolean>();                
                
                man = combinedMap.get(iter.next());
                itemMan = man.getItems();
                count = itemMan.count();                

                for (j = 0; j < model.size(); j++ ) {
                    item = model.get(j);
                    for (i = 0; i < count; i++ ) {
                        data = itemMan.getItemAt(i);
                        key = (OrderItemViewDO)item.key;
                        if (data.getId().equals(key.getId()))
                            itemPresentMap.put(data.getId(), true);
                    }
                }

                for (i = 0; i < count; i++ ) {
                    data = itemMan.getItemAt(i);
                    if (!itemPresent.equals(itemPresentMap.get(data.getId()))) {
                        item = getTopLevelItem(data);                        
                        model.add(item);   
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }

        return model;
    }    
    
    private void loadChildItems(TreeDataItem item) {  
        OrderFillManager fills;       
        InventoryXUseViewDO fill;
        ArrayList<InventoryXUseViewDO> list;       
        TreeDataItem child;
        OrderManager man;
        OrderItemViewDO data;
        
        list = new ArrayList<InventoryXUseViewDO>();
        
        window.setBusy();        
        try {
            data = (OrderItemViewDO)item.key;            
            man = combinedMap.get(data.getOrderId());
            
            if(man == null)
                man = manager;
            
            fills = man.getFills();
            
            for (int i = 0; i < fills.count(); i++ ) {
                fill = fills.getFillAt(i);
                if(fill.getOrderItemId().equals(data.getId())) {
                    child = new TreeDataItem(6);                
                    child.leafType = "orderItem";
                    child.cells.get(1).setValue(fill.getQuantity());
                    child.cells.get(3).setValue(new TableDataRow(fill.getStorageLocationId(),
                                                                 fill.getStorageLocationName()));
                    child.cells.get(4).setValue(fill.getInventoryLocationLotNumber());
                    child.cells.get(5).setValue(fill.getInventoryLocationExpirationDate());
                    child.checkForChildren(false);
    
                    child.data = fill;
                    item.addItem(child);   
                    list.add(fill);
                }
            }                                  
        } catch (NotFoundException e) {
            // ignore       
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        
        window.clearStatus(); 
                
        item.checkForChildren(false);
        
        item.data = list;                      
    }
    
    private TreeDataItem getTopLevelItem(OrderItemViewDO data) {
        TreeDataItem item;
        
        item = new TreeDataItem(3);
        item.leafType = "top";
        item.close();
        item.key = data;
        item.cells.get(0).setValue(data.getOrderId());
        item.cells.get(1).setValue(data.getQuantity());        
        item.cells.get(2).setValue(new TableDataRow(data.getInventoryItemId(),
                                                    data.getInventoryItemName()));

        item.checkForChildren(true);        
        
        return item;
    }    
    
    private TreeDataItem createAndAddChildToParent(OrderItemViewDO data, TreeDataItem parent) {
        TreeDataItem child;
        
        child = new TreeDataItem(6);
        child.leafType = "orderItem";
        itemsTree.addChildItem(parent, child);
        return child;
    }
    
    /** 
     * This method calculates the sum of the quantities specified in the
     * inventory-x-use records associated with an order item
     */
    private int getSumOfFillQuantityForItem(TreeDataItem parent) {
        ArrayList<TreeDataItem> items;
        InventoryXUseViewDO data;
        TreeDataItem child;
        int i, count, sum;
        Integer quantity;
        
        sum = 0;
        
        try {
            items = parent.getItems();
            if(items == null)
                return sum;
            
            count = items.size();
            
            for(i = 0; i < count; i++) {
                child = items.get(i);
                data = (InventoryXUseViewDO)child.data;
                if(data != null) {
                    quantity = data.getQuantity();
                    if(quantity != null)
                        sum += quantity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
         
        return sum;
    }
    
    private boolean validateItemRow(TreeDataItem item, int r) {
        int sum;
        boolean valid;
        Integer qty;
        OrderItemViewDO data;
        InventoryItemDO invItemData;
        
        data = (OrderItemViewDO)item.key;
        invItemData = orderFillScreen.getInventoryItem(data.getInventoryItemId());
        qty = data.getQuantity();
        
        item.cells.get(1).clearExceptions();
        
        if ("Y".equals(invItemData.getIsNotInventoried())) {
            if (qty == null || qty < 0) {
                if (r > -1)
                    itemsTree.setCellException(r,1, new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
                else
                    item.cells.get(1).addException(new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));         
                return false;
            }      
            return true;
        }
        
        sum = getSumOfFillQuantityForItem(item);
        valid = true;
                        
        if (qty == null || sum > qty) {    
            if(r > -1)
                itemsTree.setCellException(r,1, new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            else 
                item.cells.get(1).addException(new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            valid = false;
        } else if(sum < qty) {
            if(r > -1)
                itemsTree.setCellException(r,1, new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            else
                item.cells.get(1).addException(new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            valid = false;
        }
        
        return valid;
    }
    
    private boolean validateLocationRow(TreeDataItem child, int row) {
        InventoryXUseViewDO fill;
        OrderItemViewDO item;
        TreeDataItem parent;
        Integer qty, qtyOnHand, qtyOrdered, difference, invLocationId;
        int sum;
        boolean valid;
        
        fill = (InventoryXUseViewDO)child.data;
        qtyOnHand = fill.getInventoryLocationQuantityOnhand();
        parent = child.parent;
        item = (OrderItemViewDO)parent.key;
        qtyOrdered = item.getQuantity();
        invLocationId = fill.getInventoryLocationId();
        difference = null;
        
        qty = (Integer)child.cells.get(1).getValue();
        if (qty == null) {
            if(row > -1)
                itemsTree.setCellException(row, 1,new LocalizedException("fieldRequiredException"));
            else
                child.cells.get(1).addException(new LocalizedException("fieldRequiredException")); 
            
            return false;
        }

        valid = true; 
        child.cells.get(1).clearExceptions();
                
        if(qty < 1) {
            if(row > -1)
                itemsTree.setCellException(row, 1,new LocalizedException("invalidLocationQuantityException"));
            else
                child.cells.get(1).addException(new LocalizedException("invalidLocationQuantityException"));  
            
            valid = false;
        }
        
        if(invLocationId == null) {
            if(row > -1) {
                itemsTree.setCellException(row,1, new LocalizedException("noLocationSelectedForRowException"));                                         
            } else {
                child.cells.get(1).addException(new LocalizedException("noLocationSelectedForRowException"));
                child.cells.get(3).addException(new LocalizedException("fieldRequiredException"));
            }
            
            valid = false;
        } else {
            if (qtyOnHand != null)
                difference = qtyOnHand - qty;

            if (difference == null || difference < 0) {  
                if(row > -1)
                    itemsTree.setCellException(row,1 ,new LocalizedException("qtyMoreThanQtyOnhandException"));
                else
                    child.cells.get(1).addException(new LocalizedException("qtyMoreThanQtyOnhandException"));
                
                valid = false;
            }
        }
        
        sum = getSumOfFillQuantityForItem(parent);
        parent.cells.get(1).clearExceptions();
        
        if (qtyOrdered == null || sum > qtyOrdered) {
            parent.cells.get(1).addException(new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            valid = false;
        } else if(sum < qtyOrdered) {
            parent.cells.get(1).addException(new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            valid = false;
        }
                
        return valid;
    }        
}
