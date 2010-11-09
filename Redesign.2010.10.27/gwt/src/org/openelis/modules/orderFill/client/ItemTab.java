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
import org.openelis.cache.InventoryItemCache;
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
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenHandler;
import org.openelis.gwt.widget.tree.event.NodeAddedEvent;
import org.openelis.gwt.widget.tree.event.NodeAddedHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class ItemTab extends Screen {

    private OrderManager                   manager;
    private OrderFillScreen                orderFillScreen;
    private Tree                           itemsTree;
    private Dropdown<Integer>              costCenterId;
    private TextBox                        organizationAttention,
                                           organizationAddressMultipleUnit, organizationAddressStreetAddress,
                                           organizationAddressCity, organizationAddressState, organizationAddressZipCode;
    private AutoComplete                   storageLocationName;
    private Button                         removeItemButton, addItemButton;

    private boolean                        loaded;
    private HashMap<Integer, OrderManager> combinedMap;   
    private ScreenService                  inventoryLocationService;                 
    
    public ItemTab(ScreenDefInt def, Window window, OrderFillScreen orderFillScreen) {
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
                    costCenterId.setValue(manager.getOrder().getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {               
                manager.getOrder().setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                costCenterId.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));                
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
                organizationAttention.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));                
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
                organizationAddressMultipleUnit.setEnabled(false);
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
                organizationAddressStreetAddress.setEnabled(false);
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
                organizationAddressCity.setEnabled(false);
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
                organizationAddressState.setEnabled(false);
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
                organizationAddressZipCode.setEnabled(false);
                organizationAddressZipCode.setQueryMode(false);
            }            
        });
        
        itemsTree = (Tree)def.getWidget("itemsTree");
        addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                boolean present;
                
                itemsTree.finishEditing();
                itemsTree.setRoot(getTreeModel());                 
                if(state == State.UPDATE) {
                    //
                    // we don't allow users to add or remove items to or from 
                    // the tree if the order represented by manager isn't selected
                    // to be processed  
                    //
                    present = combinedMap.containsKey(manager.getOrder().getId());                    
                    removeItemButton.setEnabled(present);
                    addItemButton.setEnabled(present);                    
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTree.setEnabled(true);
            }
        });
        
        itemsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                Node item;
                OrderItemViewDO data;
                OrderManager man;
                int c;

                if(state != State.UPDATE) {
                    event.cancel();
                    return;
                }
                
                item = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                c = event.getCol();
                
                if("top".equals(item.getType())) {
                    data = (OrderItemViewDO)item.getData();
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
                AutoCompleteValue row;
                InventoryLocationViewDO loc;
                InventoryXUseViewDO fill;
                OrderItemViewDO item;
                Node child, parent;
                Integer qty;
                int r, c;

                c = event.getCol();
                r = itemsTree.getSelectedNode();
                child = itemsTree.getNodeAt(event.getRow());
                
                if ("orderItem".equals(child.getType())) {                    
                    fill = (InventoryXUseViewDO)child.getData();
                    parent = child.getParent();
                    item = (OrderItemViewDO)parent.getData();

                    if (c == 1) {
                        qty = (Integer)child.getCell(1);
                        fill.setQuantity(qty);                        
                        validateLocationRow(child, r);                        
                        
                        //itemsTree.refreshRow(child);
                    } else {
                        row = (AutoCompleteValue)child.getCell(3);
                        if (row != null) {
                            loc = (InventoryLocationViewDO)row.getData();
                            child.setCell(4,loc.getLotNumber());
                            child.setCell(5,loc.getExpirationDate());
                            fill.setInventoryItemId(item.getInventoryItemId());
                            fill.setStorageLocationId(loc.getStorageLocationId());
                            fill.setStorageLocationName(loc.getStorageLocationName());
                            fill.setInventoryLocationId(loc.getId());
                            fill.setInventoryLocationLotNumber(loc.getLotNumber());
                            fill.setInventoryLocationQuantityOnhand(loc.getQuantityOnhand());                            
                        } else {
                            child.setCell(4,null);
                            child.setCell(5,null);
                            fill.setInventoryItemId(null);
                            fill.setStorageLocationId(null);
                            fill.setStorageLocationName(null);
                            fill.setInventoryLocationId(null);
                            fill.setInventoryLocationLotNumber(null);
                            fill.setInventoryLocationQuantityOnhand(null);
                        }
                        
                        validateLocationRow(child, r);                        
                        //itemsTree.refreshRow(child);
                    }
                } else if (c == 1) {
                    qty = (Integer)child.getCell(1);
                    item = (OrderItemViewDO)child.getData();
                    item.setQuantity(qty);
                    validateItemRow(child, r);
                    //itemsTree.refreshRow(child);
                }
            }            
        });
        
        itemsTree.addBeforeNodeOpenHandler(new BeforeNodeOpenHandler() {
            public void onBeforeNodeOpen(BeforeNodeOpenEvent event) {
                Node item;
                
                item = event.getNode();         
                
                if("top".equals(item.getType()) && item.getData() == null) 
                    loadChildItems(item);                                                           
            }            
        });      
        
        itemsTree.addNodeAddedHandler(new NodeAddedHandler() {
            public void onNodeAdded(NodeAddedEvent event) {
                OrderManager man;
                OrderItemViewDO item;
                InventoryXUseViewDO fill;
                OrderFillManager fills;
                Node row, parent;
                Integer sum, quantity, difference;
                
                row = event.getNode();
                parent = row.getParent();
                item = (OrderItemViewDO)parent.getData();                
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
                    
                    row.setData(fill);        
                    
                    row.setCell(1,difference);
                    itemsTree.clearExceptions(parent, 1);
                    //itemsTree.refreshRow(row);
                    //itemsTree.refreshRow(parent);                                                       
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.toString());
                }
            }            
        });
        
        storageLocationName = (AutoComplete)itemsTree.getNodeDefinitionAt("orderItem",3).getCellEditor().getWidget();
        storageLocationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                Node item;
                InventoryLocationViewDO data;
                ArrayList<InventoryLocationViewDO> list;
                ArrayList<Item<Integer>> model;
                ArrayList<QueryData> fields;
                Query query;
                QueryData field;
                OrderItemViewDO key;                
                
                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }
                                
                field = new QueryData();
                if(!event.getMatch().equals(""))
                	field.query = parser.getParameter().get(0);
                else
                	field.query = "=";
                fields.add(field);
                
                item = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                key = (OrderItemViewDO)item.getParent().getData();
                
                field = new QueryData();
                field.query = Integer.toString(key.getInventoryItemId());
                fields.add(field);
                
                query.setFields(fields);

                window.setBusy();
                try {
                    
                    list = inventoryLocationService.callList("fetchByLocationNameInventoryItemId", query);
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getStorageLocationName() +", "+ 
                                                  data.getStorageLocationUnitDescription()+" "+
                                                  data.getStorageLocationLocation());
                        row.setCell(1,data.getLotNumber());
                        row.setCell(2,data.getQuantityOnhand());
                        row.setCell(3,data.getExpirationDate());
                                                
                        row.setData(data);
                        
                        model.add(row);
                    }
                    
                    storageLocationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }            
        });
        
        removeItemButton = (Button)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Node item, parent;
                InventoryXUseViewDO data;     
                OrderManager man;
                OrderFillManager fills;
                
                item = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                
                if(item == null) 
                    return;
                
                if("top".equals(item.getType())) {                    
                    window.setStatus(consts.get("qtyAdjustedItemNotRemoved"), "");
                } else {
                    data = (InventoryXUseViewDO)item.getData();
                    man = combinedMap.get(data.getOrderItemOrderId());
                    itemsTree.removeNode(item);
                    try {
                        fills = man.getFills();
                        fills.removeFill(data);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                    }
                    parent = item.getParent();
                    validateItemRow(parent,-1); 
                    if(parent.getChildCount() > 0) 
                        parent.setDeferLoadingUntilExpand(false);
                    
                    //itemsTree.refreshRow(parent);
                }                                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.setEnabled(false);
            }
        });

        addItemButton = (Button)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Node item, child, parent;
                OrderItemViewDO data;
                InventoryItemDO invItem;
                int row;
                                
                item = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                invItem = null;
                
                if(item == null) 
                    return;           
                
                itemsTree.finishEditing();
                if("top".equals(item.getType())) {
                    data = (OrderItemViewDO)item.getData();                  
                    try {
                        invItem = InventoryItemCache.getActiveInventoryItemFromId(data.getInventoryItemId());
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                        e.printStackTrace();
                    }
                    if(!item.isOpen())
                        itemsTree.toggle(item);
                    
                    if (invItem != null && "Y".equals(invItem.getIsNotInventoried())) {
                        com.google.gwt.user.client.Window.alert(consts.get("itemFlagDontInvCantBeFilled"));
                        return;
                    }
                                                            
                    child = createAndAddChildToParent((OrderItemViewDO)item.getData(), item);                    
                    itemsTree.selectNodeAt(child);    
                    row = itemsTree.getSelectedNode();
                    validateLocationRow(child, row);
                    itemsTree.startEditing(row, 3);        
                } else {
                    parent = item.getParent();
                    child  = createAndAddChildToParent((OrderItemViewDO)parent.getData(), parent);
                    itemsTree.selectNodeAt(child);
                    row = itemsTree.getSelectedNode();
                    validateLocationRow(child, row);
                    itemsTree.startEditing(row, 3);                    
                }                                               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.setEnabled(false);
            }
        });
    }
        
    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;
        
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("cost_centers"); 
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
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
        Node parent, child, model;
        ArrayList<Node> items;
        OrderItemViewDO item;
        InventoryItemDO invItem;
        Integer quantity;
        boolean validate;
        int i;
        
        validate = true;
        invItem = null;
        
        if (combinedMap != null && combinedMap.get(manager.getOrder().getId()) != null) {
            itemsTree.finishEditing();
            model = itemsTree.getRoot();
                        
            for (i = 0; i < model.getChildCount(); i++ ) {
                parent = model.getChildAt(i);
                item = (OrderItemViewDO)parent.getData();
                try {
                    invItem = InventoryItemCache.getActiveInventoryItemFromId(item.getInventoryItemId());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
                if (invItem != null && "Y".equals(invItem.getIsNotInventoried())) 
                    continue;
                
                if ( !parent.isOpen())
                    itemsTree.toggle(parent);

                items = parent.children();                                
                quantity = item.getQuantity();

                if (items != null && items.size() > 0) {
                    for (int j = 0; j < items.size(); j++ ) {
                        child = items.get(j);
                        if ( !validateLocationRow(child, -1)) {
                            //itemsTree.refreshRow(child);
                            //itemsTree.refreshRow(parent);
                            validate = false;
                        }
                    }
                } else if ( (quantity == null) || (quantity != null && quantity > 0)) {
                	itemsTree.addException(parent, 1, new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
                    //itemsTree.refreshRow(parent);
                    validate = false;
                }
            }
        }
        return validate;
    }
    
    private Node getTreeModel() {
        Node model,item;
        OrderManager man;
        OrderItemManager itemMan;        
        OrderItemViewDO data, key;
        HashMap<Integer, Boolean> itemPresentMap;
        Boolean itemPresent;        
        Set<Integer> set; 
        Iterator<Integer> iter;
        int count, i, j;        
        
        if(combinedMap == null)
            return new Node();
        
        man = combinedMap.get(manager.getOrder().getId());
        model = new Node();
        
        try {            
            if(man == null) {                    
                organizationAttention.setEnabled(false);
                costCenterId.setEnabled(false);
                
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
            organizationAttention.setEnabled(true);
            costCenterId.setEnabled(true);
            
            set = combinedMap.keySet();
            iter = set.iterator();
            itemPresent = true;
            
            while (iter.hasNext())  {                              
                itemPresentMap = new HashMap<Integer, Boolean>();                
                
                man = combinedMap.get(iter.next());
                itemMan = man.getItems();
                count = itemMan.count();                

                for (j = 0; j < model.getChildCount(); j++ ) {
                    item = model.getChildAt(j);
                    for (i = 0; i < count; i++ ) {
                        data = itemMan.getItemAt(i);
                        key = (OrderItemViewDO)item.getData();
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
            com.google.gwt.user.client.Window.alert(e.toString());
        }

        return model;
    }    
    
    private void loadChildItems(Node item) {  
        OrderFillManager fills;       
        InventoryXUseViewDO fill;
        ArrayList<InventoryXUseViewDO> list;       
        Node child;
        OrderManager man;
        OrderItemViewDO data;
        
        list = new ArrayList<InventoryXUseViewDO>();
        
        window.setBusy();        
        try {
            data = (OrderItemViewDO)item.getData();            
            man = combinedMap.get(data.getOrderId());
            
            if(man == null)
                man = manager;
            
            fills = man.getFills();
            
            for (int i = 0; i < fills.count(); i++ ) {
                fill = fills.getFillAt(i);
                if(fill.getOrderItemId().equals(data.getId())) {
                    child = new Node(6);                
                    child.setType("orderItem");
                    child.setCell(1,fill.getQuantity());
                    child.setCell(3,new AutoCompleteValue(fill.getStorageLocationId(),
                                                                 fill.getStorageLocationName()));
                    child.setCell(4,fill.getInventoryLocationLotNumber());
                    child.setCell(5,fill.getInventoryLocationExpirationDate());
                    child.setDeferLoadingUntilExpand(false);
    
                    child.setData(fill);
                    item.add(child);   
                    list.add(fill);
                }
            }                                  
        } catch (NotFoundException e) {
            // ignore       
        } catch (Throwable e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        
        window.clearStatus(); 
                
        item.setDeferLoadingUntilExpand(false);
        
        item.setData(list);                      
    }
    
    private Node getTopLevelItem(OrderItemViewDO data) {
        Node item;
        
        item = new Node(3);
        item.setType("top");
        item.setOpen(false);
        item.setData(data);
        item.setCell(0,data.getOrderId());
        item.setCell(1,data.getQuantity());        
        item.setCell(2,new AutoCompleteValue(data.getInventoryItemId(),
                                                    data.getInventoryItemName()));

        item.setDeferLoadingUntilExpand(true);        
        
        return item;
    }    
    
    private Node createAndAddChildToParent(OrderItemViewDO data, Node parent) {
        Node child;
        
        child = new Node(6);
        child.setType("orderItem");
        itemsTree.addNodeAt(parent, child);
        return child;
    }
    
    /** 
     * This method calculates the sum of the quantities specified in the
     * inventory-x-use records associated with an order item
     */
    private int getSumOfFillQuantityForItem(Node parent) {
        ArrayList<Node> items;
        InventoryXUseViewDO data;
        Node child;
        int i, count, sum;
        Integer quantity;
        
        sum = 0;
        
        try {
            items = parent.children();
            if(items == null)
                return sum;
            
            count = items.size();
            
            for(i = 0; i < count; i++) {
                child = items.get(i);
                data = (InventoryXUseViewDO)child.getData();
                if(data != null) {
                    quantity = data.getQuantity();
                    if(quantity != null)
                        sum += quantity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
         
        return sum;
    }
    
    private boolean validateItemRow(Node item, int r) {
        int sum;
        boolean valid;
        Integer qty;
        OrderItemViewDO data;
        InventoryItemDO invItem;
        
        invItem = null;
        data = (OrderItemViewDO)item.getData();
        try {
            invItem = InventoryItemCache.getActiveInventoryItemFromId(data.getInventoryItemId());
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        qty = data.getQuantity();
        
        itemsTree.clearExceptions(item, 1);
        
        if (invItem != null && "Y".equals(invItem.getIsNotInventoried())) {
            if (qty == null || qty < 0) {
                if (r > -1)
                    itemsTree.addException(r,1, new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
                else
                	itemsTree.addException(item,1,new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));         
                return false;
            }      
            return true;
        }
        
        sum = getSumOfFillQuantityForItem(item);
        valid = true;
                        
        if (qty == null || sum > qty) {    
            if(r > -1)
                itemsTree.addException(r,1, new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            else 
                itemsTree.addException(item,1,new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            valid = false;
        } else if(sum < qty) {
            if(r > -1)
                itemsTree.addException(r,1, new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            else
                itemsTree.addException(item,1,new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            valid = false;
        }
        
        return valid;
    }
    
    private boolean validateLocationRow(Node child, int row) {
        InventoryXUseViewDO fill;
        OrderItemViewDO item;
        Node parent;
        Integer qty, qtyOnHand, qtyOrdered, difference, invLocationId;
        int sum;
        boolean valid;
        
        fill = (InventoryXUseViewDO)child.getData();
        qtyOnHand = fill.getInventoryLocationQuantityOnhand();
        parent = child.getParent();
        item = (OrderItemViewDO)parent.getData();
        qtyOrdered = item.getQuantity();
        invLocationId = fill.getInventoryLocationId();
        difference = null;
        
        qty = (Integer)child.getCell(1);
        if (qty == null) {
            if(row > -1)
                itemsTree.addException(row, 1,new LocalizedException("fieldRequiredException"));
            else
            	itemsTree.addException(child,1,new LocalizedException("fieldRequiredException")); 
            
            return false;
        }

        valid = true; 
        itemsTree.clearExceptions(child, 1);
                
        if(qty < 1) {
            if(row > -1)
                itemsTree.addException(row, 1,new LocalizedException("invalidLocationQuantityException"));
            else
            	itemsTree.addException(child,1,new LocalizedException("invalidLocationQuantityException"));  
            
            valid = false;
        }
        
        if(invLocationId == null) {
            if(row > -1) {
                itemsTree.addException(row,1, new LocalizedException("noLocationSelectedForRowException"));                                         
            } else {
                itemsTree.addException(child,1,new LocalizedException("noLocationSelectedForRowException"));
                itemsTree.addException(child,3,new LocalizedException("fieldRequiredException"));
            }
            
            valid = false;
        } else {
            if (qtyOnHand != null)
                difference = qtyOnHand - qty;

            if (difference == null || difference < 0) {  
                if(row > -1)
                    itemsTree.addException(row,1 ,new LocalizedException("qtyMoreThanQtyOnhandException"));
                else
                    itemsTree.addException(child,1,new LocalizedException("qtyMoreThanQtyOnhandException"));
                
                valid = false;
            }
        }
        
        sum = getSumOfFillQuantityForItem(parent);
        itemsTree.clearExceptions(parent, 1);
        
        if (qtyOrdered == null || sum > qtyOrdered) {
            itemsTree.addException(parent,1,new LocalizedException("sumOfQtyMoreThanQtyOrderedException"));
            valid = false;
        } else if(sum < qtyOrdered) {
            itemsTree.addException(parent,1,new LocalizedException("sumOfQtyLessThanQtyOrderedException"));
            valid = false;
        }
                
        return valid;
    }        
}
