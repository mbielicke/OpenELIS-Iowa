/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public Software License(the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. All Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be used under the terms of a UIRF Software license ("UIRF Software License"), in which case the provisions of a UIRF Software License are applicable instead of those above.
 */
package org.openelis.modules.orderFill.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.order.client.CustomerNoteTab;
import org.openelis.modules.order.client.ShipNoteTab;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.utilcommon.DataBaseUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class OrderFillScreen extends Screen {
    private SecurityModule                     security;

    private ItemTab                            itemTab;
    private CustomerNoteTab                    custNoteTab;
    private ShipNoteTab                        shipNoteTab;
    private Tabs                               tab;

    private Dropdown<Integer>                  status;
    private AppButton                          queryButton, updateButton, processButton,
                                               commitButton, abortButton;
    private TableWidget                        orderTable;
    private TabPanel                           tabPanel;
    private TableDataRow                       prevSelRow;               
    
    private ShippingScreen                     shippingScreen; 

    private Integer                            status_pending, status_back_ordered;
    private HashMap<TableDataRow, OrderViewDO> orderMap;
    private HashMap<Integer, OrderManager>     combinedMap;
        
    
    private enum Tabs {
        ITEM, SHIP_NOTE, CUSTOMER_NOTE
    };
    
    public OrderFillScreen() throws Exception {
        super((ScreenDefInt)GWT.create(OrderFillDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
    
        security = OpenELIS.security.getModule("order");
        if (security == null)
            throw new SecurityException("screenPermException", "Order Fill Screen");


        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred command.
     */
    private void postConstructor() {
        tab = Tabs.ITEM;
        initialize(); 
        setState(State.DEFAULT);
        initializeDropdowns();
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
        
        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState())
                                    && security.hasUpdatePermission());
               if (event.getState() == State.UPDATE)
                   updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });
        
        processButton = (AppButton)def.getWidget("process");
        addScreenHandler(processButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                process();
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                processButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.UPDATE).contains(event.getState()));
            }
        });

        orderTable = (TableWidget)def.getWidget("orderTable");
        addScreenHandler(orderTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                TableColumn process;
                ArrayList list;               
                
                orderTable.enable(true);                
                orderTable.setQueryMode(event.getState() == State.QUERY);
                
                if(state == State.QUERY) {                   
                    process = orderTable.getColumns().get(0);
                    process.enable(false);        
                    
                    list = new ArrayList<Integer>();
                    list.add(status_pending);
                    orderTable.setCell(0, 2, list);
                }               
            }
        });     
        
        orderTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                prevSelRow = event.getUnselectedItem();                
            }            
        });
        
        orderTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {     
                int r,c;
                TableDataRow row;
                OrderManager man;
                OrderViewDO data, prevSelData;
                String val, prevSelVal;
                boolean cancel;
                Integer status;
                
                if(orderMap == null) { 
                    event.cancel();
                    return;
                }
                
                c = event.getCol();
                r = event.getRow();
                row = orderTable.getRow(r);
                data = orderMap.get(row);
                val = (String)orderTable.getCell(r, 0).getValue();
                cancel = false; 
                status = data.getStatusId();
                
                if (state != State.UPDATE || (c > 0)) {                                        
                    cancel = true;
                } else if(!status_pending.equals(status) && !status_back_ordered.equals(status)){
                    Window.alert(consts.get("onlyPendingBackOrderedProcessed"));  
                    cancel = true;
                } else if(c == 0 && prevSelRow != null) {                    
                    prevSelData = orderMap.get(prevSelRow);   
                    prevSelVal = (String)prevSelRow.cells.get(0).getValue();
                    if(("Y".equals(prevSelVal)) && ("N".equals(val)) &&
                                    DataBaseUtil.isDifferent(data.getOrganizationId(), prevSelData.getOrganizationId())) {
                        Window.alert(consts.get("sameShipToOrderCombined"));                        
                        cancel = true;                           
                    }
                } 
                
                if(cancel) {
                    man = null;                    
                                        
                    if(row.data == null) 
                        row.data = fetchById(data);                        
                               
                    man = (OrderManager)row.data;
                    itemTab.setManager(man, combinedMap);                    
                    shipNoteTab.setManager(man);
                    custNoteTab.setManager(man);
                    
                    drawTabs();
                    event.cancel();        
                }
            }                         
        });
        
        
        orderTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                TableDataRow row;
                OrderViewDO data;
                OrderManager man;

                r = event.getRow();
                c = event.getCol();

                if (c == 0) {
                    val = (String)orderTable.getCell(r, c).getValue();
                    row = orderTable.getRow(r);
                    data = orderMap.get(row);

                    if ("Y".equals(val) && (combinedMap.get(data.getId()) == null)) {
                        try {
                            man = (OrderManager)row.data;                            
                            if(man == null) {
                                man = OrderManager.getInstance();
                                man.setOrder(data);                                     
                            }               
                            
                            window.setBusy(consts.get("lockForUpdate"));
                            row.data = man.fetchForUpdate();
                            window.clearStatus();
                            
                            combinedMap.put(data.getId(), (OrderManager)row.data);
                            itemTab.setManager(man, combinedMap);
                            shipNoteTab.setManager(man);
                            custNoteTab.setManager(man);
                            drawTabs();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            e.printStackTrace();
                        }
                    } else if ("N".equals(val) && (combinedMap.get(data.getId()) != null)) {
                        removeFromCombined(row, data);
                        window.setDone(consts.get("updateAborted"));
                    }
                }              
            }
        });
        
        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        itemTab = new ItemTab(def, window);
        addScreenHandler(itemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.ITEM)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTab.setState(event.getState());
            }
        });

        shipNoteTab = new ShipNoteTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(shipNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.SHIP_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shipNoteTab.setState(event.getState());
            }
        });

        custNoteTab = new CustomerNoteTab(def, window, "customerNotesPanel", "editNoteButton");
        addScreenHandler(custNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.CUSTOMER_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                custNoteTab.setState(event.getState());
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
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_status");
        
        for (DictionaryDO resultDO : list)          
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));      
        
        status = ((Dropdown<Integer>)orderTable.getColumnWidget(OrderMeta.getStatusId()));        
        status.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_ship_from");
        
        for (DictionaryDO resultDO : list) 
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));        
        
        ((Dropdown<Integer>)orderTable.getColumnWidget(OrderMeta.getShipFromId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));   
        model.add(new TableDataRow(OrderManager.TYPE_INTERNAL, consts.get("internal")));
        model.add(new TableDataRow(OrderManager.TYPE_KIT, consts.get("kit")));
        
        ((Dropdown<Integer>)orderTable.getColumnWidget(OrderMeta.getType())).setModel(model);
        
        try {
            status_pending = DictionaryCache.getIdFromSystemName("order_status_pending");
            status_back_ordered = DictionaryCache.getIdFromSystemName("order_status_back_ordered");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    protected void query() {
        OrderManager man;
        
        man = OrderManager.getInstance();
        
        setState(State.QUERY);
        DataChangeEvent.fire(this);
        
        prevSelRow = null;
        
        itemTab.setManager(man, combinedMap);
        shipNoteTab.setManager(man);
        custNoteTab.setManager(man);
        
        itemTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();
        
        orderMap = null;
        window.setDone(consts.get("enterFieldsToQuery"));       
    }
    
    protected void update() {
        setState(State.UPDATE);  
        prevSelRow = null;
    }
    
    private void process() {
        ScreenWindow modal;
        ShippingManager manager;
        ShippingViewDO shipping;
        OrderViewDO data;
        
        if (!validate()) {
            window.setError(consts.get("correctErrorsProcess"));
            return;
        }
        
        try {
            data = getProcessShipData();
            
            if(data == null) {
                Window.alert(consts.get("noOrdersSelectForProcess"));
                return;
            } else if (OrderManager.TYPE_INTERNAL.equals(data.getType())) {
                processIternalOrders();
                return;
            }              
            
            //
            // here we try to find out if all the top level nodes in the items tree
            // have child nodes or not, i.e. whether each order item has at least 
            // one inventory_x_use record associated with it or not and if this is
            // not the case we don't allow the orders to be processed            
            //
            if (!allItemsHaveFills(combinedMap)) {
                Window.alert("All items don't have sub items");
                return;
            }
                        
            shipping = new ShippingViewDO();
            shipping.setShippedFromId(data.getShipFromId());
            shipping.setShippedToId(data.getOrganizationId()); 
            shipping.setShippedTo(data.getOrganization());
            manager = ShippingManager.getInstance();
            manager.setShipping(shipping);
            createShippingItems(manager, combinedMap);
            
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(consts.get("shipping"));
            
            if(shippingScreen == null)
                shippingScreen = new ShippingScreen();
            shippingScreen.window = modal;
            modal.setContent(shippingScreen);            
            shippingScreen.setManager(manager);           
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
    
   private void processIternalOrders() {
        // TODO Auto-generated method stub
        
    }

   protected void commit() {        
        Query query;
        
        clearErrors();
        if (!validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {            
            query = new Query();
            query.setFields(getQueryFields());           
            
            executeQuery(query); 
            combinedMap = new HashMap<Integer, OrderManager>();
            setState(State.DISPLAY);
        }
        
    }
    
    protected void abort() {
        OrderManager man;
        
        man = OrderManager.getInstance();
        
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            itemTab.setManager(man, combinedMap);
            shipNoteTab.setManager(man);
            custNoteTab.setManager(man);
            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {
            releaseLocks();
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            
            window.setDone(consts.get("updateAborted"));
        }      
        
        prevSelRow = null;
    } 
    
    private OrderManager fetchById(OrderViewDO data) {
        OrderManager man;
        
        man = null;
        window.setBusy(consts.get("fetching"));
        try {                            
            man = OrderManager.fetchById(data.getId());                                    
        }catch (NotFoundException e) {
            man = OrderManager.getInstance();
            window.setDone(consts.get("noRecordsFound"));                            
        }  catch (Exception e) {
            Window.alert(consts.get("fetchFailed") + e.getMessage());
            e.printStackTrace();
        }    
        window.clearStatus();
        
        return man;
    }
    
    private void drawTabs() {
        switch (tab) {
            case ITEM:
                itemTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;
            case CUSTOMER_NOTE:
                custNoteTab.draw();
                break;   
        }
    }
    
    private void executeQuery(Query query) {                
        window.setBusy(consts.get("querying"));

        service.callList("queryOrderFill", query, new AsyncCallback<ArrayList<OrderViewDO>>() {
            public void onSuccess(ArrayList<OrderViewDO> result) {
                ArrayList<TableDataRow> model;
                Datetime now;
                TableDataRow row;

                orderTable.setQueryMode(false);
                model = null;
                
                now = null;
                
                if (result != null) {
                    model = new ArrayList<TableDataRow>();                    
                    try {
                        now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
                    } catch (Exception e) {
                        Window.alert("OrderAdd Datetime: " +e.getMessage());
                    }
                    
                    orderMap = new HashMap<TableDataRow, OrderViewDO>();
                    
                    for (OrderViewDO data : result) {
                        row = addByLeastNumDaysLeft(data, model, now);
                        orderMap.put(row, data);                        
                    }
                    
                    orderTable.load(model);
                }
                
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                orderTable.load(null);
                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    window.setError(consts.get("noMoreRecordInDir"));
                } else {
                    Window.alert("Error: Order call query failed; " +
                                 error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }
    
    private int daysBetween(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));  
    } 
    
    /**
     * This method adds new rows to the model for the table displaying order records
     * by the number of days left between the desired order processing date and 
     * the current date, in a descending order; it also return the newly added row 
     */
    private TableDataRow addByLeastNumDaysLeft(OrderViewDO data, ArrayList<TableDataRow> model, Datetime now) {
        TableDataRow row, modelRow;
        OrganizationDO organization;
        Datetime ordDate;
        int num,diff,val,mrowVal,size;
        
        row = new TableDataRow(10);
        row.cells.get(0).setValue("N");
        row.cells.get(1).setValue(data.getId());
        row.cells.get(2).setValue(data.getStatusId());
        
        ordDate = data.getOrderedDate();                        
        row.cells.get(3).setValue(ordDate);
        row.cells.get(4).setValue(data.getShipFromId());                        
        organization = data.getOrganization();
        
        if(organization != null)                        
            row.cells.get(5).setValue(organization.getName());                        
        row.cells.get(6).setValue(data.getDescription());
        
        num = data.getNeededInDays();
        row.cells.get(7).setValue(num);
        
        diff = daysBetween(ordDate.getDate(), now.getDate());
        
        // days left between the desired order processing date and the current date
        val = num - diff;
        
        row.cells.get(8).setValue(val);
        row.cells.get(9).setValue(data.getType());
        
        size = model.size();
        
        if(size == 0) {                   
            model.add(row);            
        } else {
            for(int i = 0; i < size; i++) {
                modelRow = model.get(i);
                mrowVal = (Integer)modelRow.cells.get(8).getValue();
                if(val <= mrowVal) {                    
                    model.add(i, row);
                    break;
                } else if(i == size -1) {
                    model.add(row);
                }
            }
        }
        return row;
    }      
    
    private void releaseLocks() {
        String val;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        OrderViewDO data;
        
        model = orderTable.getData();
        
        for(int i = 0; i < model.size(); i++) {
            row = model.get(i);
            val = (String)row.cells.get(0).getValue();
            
            if("Y".equals(val)) {
                data = orderMap.get(row);
                removeFromCombined(row, data);
                orderTable.setCell(i, 0, "N");                
            }
        }        
    }
    
    private void removeFromCombined(TableDataRow row, OrderViewDO data) {
        OrderManager man;        
        try {
            man = (OrderManager)row.data;
            man = man.abortUpdate();
            row.data = man;
            combinedMap.remove(data.getId());             
            itemTab.setManager(man, combinedMap);
            shipNoteTab.setManager(man);
            custNoteTab.setManager(man);
            drawTabs();            
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }    
    
    private OrderViewDO getProcessShipData() {
        OrderViewDO data;
        Set<Integer> set; 
        Iterator<Integer> iter;
        OrderManager man;
        
        data = null;
        
        if(combinedMap == null || combinedMap.size() == 0) 
            return data;        
        
        set = combinedMap.keySet();
        iter = set.iterator(); 
                                                                           
        man = combinedMap.get(iter.next());
        return man.getOrder();                        
    }
    
    private void createShippingItems(ShippingManager manager, HashMap<Integer, OrderManager> combinedMap) {
        ShippingItemManager man;
        OrderManager order;
        OrderFillManager fills;
        InventoryXUseViewDO data;
        ShippingItemDO item;
        Set<Integer> set; 
        Iterator<Integer> iter;
        int count, i;
        
        if(combinedMap == null)
            return;              
        
        try {                   
            man = manager.getItems();
            
            set = combinedMap.keySet();
            iter = set.iterator();            
            
            while (iter.hasNext())  {                                                                        
                order = combinedMap.get(iter.next());
                fills = order.getFills();
                count = fills.count();                

                for (i = 0; i < count; i++) {
                    data = fills.getFillAt(i);
                    item = man.getItemAt(man.addItem());
                    item.setQuantity(data.getQuantity());
                    item.setDescription(data.getInventoryItemName());
                    item.setReferenceId(data.getOrderItemId());
                    item.setReferenceTableId(ReferenceTable.ORDER_ITEM);
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }
    }
    
    private boolean allItemsHaveFills(HashMap<Integer, OrderManager> combinedMap) {
        OrderManager order;
        OrderFillManager fills;
        ArrayList<InventoryXUseViewDO> list;
        OrderItemManager items;
        OrderItemViewDO item;
        Set<Integer> set; 
        Iterator<Integer> iter;
        int count, i;
                
        try {            
            set = combinedMap.keySet();
            iter = set.iterator();
            
            while (iter.hasNext())  {
                order = combinedMap.get(iter.next());
                fills = order.getFills();
                items = order.getItems();
                
                count = items.count();
                
                for (i = 0; i < count; i++) {
                    item = items.getItemAt(i);
                    list = fills.getFillsByItemId(item.getId());
                    if(list == null || list.size() == 0) {
                        return false;   
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }
        
        return true;        
    }
}
/*
    
    private Dropdown            costCenter;
    private TreeWidget          orderItemsTree;
    private ResultsTable        fillItemsTable;
    //private QueryTable          fillItemsQueryTable;
    private AppButton           removeRowButton, addLocationButton;
    private DataSorter sorter;

    private TreeDataModel       combinedTree, emptyTreeModel;
    private List<Integer>                combinedTreeIds;
    private TableDataModel<TableDataRow<Integer>>  lockedIndexes = new TableDataModel<TableDataRow<Integer>>();
    private Integer             lastShippedTo   = null;
    private Integer             lastShippedFrom = null;
    private Object              lastTreeValue;
    private Integer             orderPendingValue;

    private FillOrderMetaMap    OrderMeta       = new FillOrderMetaMap();

    private KeyListManager      keyList         = new KeyListManager();

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new FillOrderForm());
    }

    public void performCommand(Enum action, Object obj) {
        if (action == KeyListManager.Action.FETCH) {
            if (state == State.DISPLAY || state == State.DEFAULT) {
                if (keyList.getList().size() > 0)
                    setState(State.DISPLAY);
                else
                    setState(State.DEFAULT);
            }
        } else if (action == KeyListManager.Action.SELECTION) {
            // do nothing for now
        } else if (action == KeyListManager.Action.GETPAGE) {
            // do nothing for now
        } else if (action == ShippingScreen.Action.Commited) {
            setOrdersToProcessedCommit();
        } else if (action == ShippingScreen.Action.Aborted) {
            onShippingScreenAbort();
        } else {
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
        else if (sender == addLocationButton)
            onAddLocationButtonClick();
    }

    public void afterDraw(boolean success) {
        ScreenResultsTable sw = (ScreenResultsTable)widgets.get("fillItemsTable");
        fillItemsTable = (ResultsTable)sw.getWidget();
        //fillItemsQueryTable = (QueryTable)((ScreenQueryTable)sw.getQueryWidget()).getWidget();
        fillItemsTable.table.addTableWidgetListener(this);
        fillItemsTable.model.addTableModelListener(this);
        sorter = new DataSorter();
        ((TableModel)fillItemsTable.model).sorter = this;
        
        orderItemsTree = (TreeWidget)getWidget("orderItemsTree");
        orderItemsTree.addTreeWidgetListener(this);
        orderItemsTree.model.addTreeModelListener(this);
        
        removeRowButton = (AppButton)getWidget("removeRowButton");
        addLocationButton = (AppButton)getWidget("addLocationButton");

        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");

        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        chain.addCommand(fillItemsTable);

        costCenter = (Dropdown)getWidget(OrderMeta.getCostCenterId());

        combinedTree = (TreeDataModel)orderItemsTree.model.getData().clone();
        combinedTreeIds = new ArrayList<Integer>();

        emptyTreeModel = orderItemsTree.model.getData();
        form.itemInformation.originalOrderItemsTree.setValue(emptyTreeModel);
        form.itemInformation.displayOrderItemsTree.setValue(emptyTreeModel);
        
        super.afterDraw(success);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("cost_centers");
        model = getDictionaryIdEntryList(cache);
        costCenter.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("order_status");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)fillItemsTable.table.columns.get(2).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("shipFrom");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)fillItemsTable.table.columns.get(4).getColumnWidget()).setModel(model);
        
        orderPendingValue = null; //DictionaryCache.getIdFromSystemName("order_status_pending");
    }

    public void add() {
        // super.add();
        form.entityKey = null;
        enable(true);
        setState(State.ADD);
        window.setStatus(consts.get("enterInformationPressCommit"), "");

        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    public void query() {
        super.query();

        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);

        fillItemsTable.table.model.setCell(0, 2, new TableDataRow<Integer>(orderPendingValue));
        fillItemsTable.table.select(0, 1);

        orderItemsTree.model.clear();
        lockedIndexes.clear();
        combinedTree.clear();
        
    }

    public void commit() {
        if (state == State.ADD) {
            if(lockedIndexes.size() > 0 && (fillItemsTable.model.getSelectedIndex() == -1 || !isRowChecked(fillItemsTable.model.getSelectedIndex())))
                fillItemsTable.model.selectRow(lockedIndexes.get(0).key.intValue());
                
            clearErrors();
            submitForm();
            form.validate();
            if (form.status == Form.Status.valid && validate()) {
                clearErrors();
                window.setStatus("", "");
                lastShippedFrom = new Integer(-1);
                lastShippedTo = new Integer(-1);
                onProcessingCommitClick();
            } else {
                drawErrors();
                window.setStatus(consts.get("correctErrors"), "ErrorPanel");
            }
        } else
            super.commit();

    }

    public void abort() {
        if (state == State.ADD) {
            clearErrors();
            enable(false);
            setState(State.DISPLAY);
            //clear the tree so we dont have to piecemeal clear it
            combinedTree.clear();
            
            unlockRows((TableDataModel<TableDataRow<Integer>>)lockedIndexes.clone(), true);
            
        } else
            super.abort();
    }
    
    public void setOrdersToProcessedCommit(){
        TableDataModel<TableDataRow<Integer>> lockedSets = getLockedSetsFromLockedList(lockedIndexes);

        form.itemInformation.originalOrderItemsTree.setValue(combinedTree);
        
        form.itemInformation.tableData = lockedSets;

        screenService.call("setOrderToProcessed", form.itemInformation, new AsyncCallback<FillOrderItemInfoForm>() {
            public void onSuccess(FillOrderItemInfoForm result) {
                //set the rows that were selected because we refetched the data
                for(int i=0; i<lockedIndexes.size(); i++){
                    int row = lockedIndexes.get(i).key.intValue();
                    result.tableData.get(i).setData((FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData());
                    fillItemsTable.model.setRow(row, result.tableData.get(i));
                    fillItemsTable.model.setCell(row, 0, CheckBox.UNCHECKED);
                }
                
                unlockRows(lockedIndexes, true);
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });

    }
    
    public void onShippingScreenAbort(){
        //clear the tree so we dont have to piecemeal clear it
        combinedTree.clear();
        
        unlockRows(lockedIndexes, true);
        
        //reassert the subform so it shows the current selected row
        loadSubForm(fillItemsTable.model.getSelectedIndex());
        
        window.setStatus(consts.get("shippingScreenAbort"),"ErrorPanel");
    }

    //
    // start table manager methods
    //
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
        return state == State.QUERY || (state == State.ADD && col == 0);

    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        return state == State.QUERY || state == State.ADD || state == State.DISPLAY;

    }

    public boolean canDrag(TableWidget widget, TableDataRow item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           TableDataRow dropTarget,
                           int targetRow) {
        return false;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     TableDataRow dropTarget,
                     int targetRow) {
    }

    public void drop(TableWidget widget, Widget dragWidget) {
    }

    //
    // end table manager methods
    //
    @SuppressWarnings(value={"unchecked"})
    private void onProcessingCommitClick() {
        if(lockedIndexes.size()  == 0){
            //throw error and return
            window.setStatus(consts.get("fillOrderNoneChecked"), "ErrorPanel");
            return;
        }

        //FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        //foiirpc.form = form.itemInformation;
        form.itemInformation.originalOrderItemsTree.setValue(combinedTree);
        form.itemInformation.status = Form.Status.valid;
        
        screenService.call("validateOrders", form.itemInformation, new SyncCallback() {
            public void onSuccess(Object obj) {
                FillOrderItemInfoForm rForm = (FillOrderItemInfoForm)obj;
                if(rForm.status.equals(Form.Status.invalid)){
                    if(rForm.getErrors().size() > 0){
                        if(rForm.getErrors().size() > 1){
                            window.setMessagePopup(rForm.getErrors(), "ErrorPanel");
                            window.setStatus("(Error 1 of "+rForm.getErrors().size()+") "+(String)rForm.getErrors().get(0), "ErrorPanel");
                        }else
                            window.setStatus((String)rForm.getErrors().get(0),"ErrorPanel");
                    }
                    return;
                }
                //get the first row that is checked
                TableDataRow<Integer> row = fillItemsTable.model.getRow(lockedIndexes.get(0).key.intValue());

                if (((CheckField)row.cells[9]).isChecked())
                    setOrdersToProcessedCommit();   
                else    
                    createShippingScreen(row);
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void createShippingScreen(TableDataRow<Integer> row){
        if (lockedIndexes.size() == 0 || row == null)
            return;
        
        
        //PopupPanel shippingPopupPanel = new PopupPanel(false, true);
        //ScreenWindow pickerWindow = new ScreenWindow(shippingPopupPanel, "Shipping", "shippingScreen", "Loading...");

        FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)row.getData();
        ShippingScreen shippingScreen = new ShippingScreen();

        ShippingDataService data = new ShippingDataService();
        data.setShipFromId((Integer)((DropDownField<Integer>)row.cells[4]).getSelectedKey());
        data.setShipToId((Integer)((DropDownField<Integer>)row.cells[5]).getSelectedKey());
        data.setShipToText((String)((DropDownField<Integer>)row.cells[5]).getTextValue());
        data.setMultUnitText(tableRowSubRPC.multUnit.getValue());
        data.setStreetAddressText(tableRowSubRPC.streetAddress.getValue());
        data.setCityText(tableRowSubRPC.city.getValue());
        data.setStateText(tableRowSubRPC.state.getValue());
        data.setZipCodeText(tableRowSubRPC.zipCode.getValue());
        data.setItemsShippedModel(createDataModelFromTree((TreeDataModel)combinedTree.clone()));
        
        shippingScreen.setTarget(this);
        shippingScreen.setShippingData(data);

        ScreenWindow modal = new ScreenWindow(null,"Shipping","shippingScreen","Loading...",true);
        modal.setName(consts.get("shipping"));
        modal.setContent(shippingScreen);

    }

    private void rebuildOrderItemsTree(int row) {
        boolean isChecked, alreadyCombined;
        TableDataRow<Integer> tableRow = fillItemsTable.model.getRow(row);
        
        FillOrderItemInfoForm subRpc = (FillOrderItemInfoForm)tableRow.getData();
        
        if (subRpc == null)
            return;
       
        TreeDataModel orgTreeModel = subRpc.originalOrderItemsTree.getValue();
        isChecked = isRowChecked(row);
        alreadyCombined = combinedTreeIds.contains(getOrderId(row));
        
        if (isChecked && !alreadyCombined) {
            // take the treefield in the form and move it to the original param in the form for safe keeping if the original is null
            for (int i = 0; i < orgTreeModel.size(); i++) {
                TreeDataItem set = (TreeDataItem)orgTreeModel.get(i);

                TreeDataItem childRow = orderItemsTree.model.createTreeItem("orderItem");
                //TreeDataItem parentItemRow = orderItemsTree.model.createTreeItem("top");
                // TreeDataItem locRow = orderItemsTree.model.createTreeItem("orderItem");
                set.addItem(childRow);
                //parentItemRow.addItem(set);
                childRow.cells[0].setValue(set.cells[0].getValue());
                childRow.cells[1].setValue(set.cells[1].getValue());
                childRow.setData(set.getData());
                set.setData(new IntegerObject((Integer)set.cells[0].getValue()));

                if (set.cells[2].getValue() != null) {
                    ((DropDownField)childRow.cells[2]).setModel(((DropDownField)set.cells[2]).getModel());
                    ((DropDownField)childRow.cells[2]).setValue(((DropDownField)set.cells[2]).getValue());
                }

                combinedTree.add(set);
                
            }
            combinedTreeIds.add(getOrderId(row));
            
        } else if(!isChecked && alreadyCombined){
            int k = 0;
            while (k < combinedTree.size()) {

                if (((Integer)combinedTree.get(k).cells[1].getValue()).equals(tableRow.cells[1].getValue())) { // we need to remove this row and children
                    combinedTree.delete(k);
                    k--;
                }
                k++;
            }
            combinedTreeIds.remove(getOrderId(row));
        }
        
        if(combinedTreeIds.contains(getOrderId(row)) && 
               (lastShippedFrom == null || lastShippedTo == null)){
            lastShippedFrom = (Integer)((DropDownField)tableRow.cells[4]).getSelectedKey();
            lastShippedTo = (Integer)((DropDownField)tableRow.cells[5]).getSelectedKey();
            
        }else if (lockedIndexes.size() == 0) {
            lastShippedFrom = null;
            lastShippedTo = null;
            
        }
    }

    //
    // start table model listener methods
    //
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {
    }

    public void dataChanged(SourcesTableModelEvents sender) {
    }

    public void rowAdded(SourcesTableModelEvents sender, int rows) {
    }

    public void rowDeleted(SourcesTableModelEvents sender, int row) {
    }

    public void rowSelected(SourcesTableModelEvents sender, final int row) {
        if(state != State.QUERY)
            fetchSubForm(row, false);
       
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {
    }

    public void rowUpdated(SourcesTableModelEvents sender, int row) {
    }

    public void unload(SourcesTableModelEvents sender) {
    }
    //
    // end table model listener methods
    //

    //
    // start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
          if(col == 0 && state == State.ADD){ 
              String checkedValue = (String)fillItemsTable.model.getCell(row, 0);

              if(CheckBox.CHECKED.equals(checkedValue)){
                  if(((DropDownField<Integer>)fillItemsTable.model.getObject(row, 2)).getSelectedKey().equals(orderPendingValue))
                      lockAndReloadRow(row);
                  else{
                      window.setStatus(consts.get("fillOrderOnlyPendingRowsCanBeChecked"), "ErrorPanel");
                      fillItemsTable.model.setCell(row, 0, CheckBox.UNCHECKED);
                  }
              }else if(CheckBox.UNCHECKED.equals(checkedValue))
                  unlockRow(row, false);
  
          }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
    }
    //
    // end table widget listener methods
    //
    
    public void lockAndReloadRow(final int row){
        if(ModelUtil.getRowByKey(lockedIndexes,row) != null)
            return;
        
        // ship from and ship to need to be the same since we are sending a 
        // single mailing out. Also will not allow mixed internal and external 
        // orders since ship from/to on internal orders are null
        Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
        Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
        boolean haveMultiRow = lockedIndexes.size() > 0;
        if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
            window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
            fillItemsTable.model.setCell(row, 0, "N");
            return;
        }
        
        // fetch the row and lock it
        Integer orderId = (Integer)fillItemsTable.model.getCell(row, 1);
        FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        foiirpc.entityKey = orderId;
        
        screenService.call("fetchOrderItemAndLock", foiirpc, new SyncCallback() {
               public void onSuccess(Object obj) {
                   FillOrderItemInfoForm result = (FillOrderItemInfoForm)obj;

                   // load the data in the table
                   //TableDataRow<Integer> newTableRow = result.tableData.get(0);
                   //newTableRow.get(0).setValue("Y");
                   //Window.alert("["+row+"]");
                   result.tableData.get(0).setData((FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData());
                   fillItemsTable.model.setRow(row, result.tableData.get(0));
                   fillItemsTable.model.setCell(row, 0, CheckBox.CHECKED);
                   //fillItemsTable.model.setRow(row, result.tableData.get(0));
                   
                   //add the order id to the checked order ids model
                   TableDataRow<Integer> set = new TableDataRow<Integer>();
                   set.key = new Integer(row);
                   lockedIndexes.add(set);
                   
                   Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
                   Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
                   boolean haveMultiRow = lockedIndexes.size() > 1;
                   if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
                       // uncheck the row and throw a form error
                       window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
                       fillItemsTable.model.setCell(row, 0, "N");
                       unlockRow(row, false);
                   } else {
                       window.setStatus("", "");
                       fetchSubForm(row, true);
                       //FIXME not sure where this call gets put... fillOrderCheck(row, true);
                   }

               }

               public void onFailure(Throwable caught) {
                   // we need to uncheck the checkbox
                   fillItemsTable.model.setCell(row, 0, "N");

                   Window.alert(caught.getMessage());
               }
           });
    }
    
    public void unlockRow(final int row, final boolean forced){
        if(ModelUtil.getRowByKey(lockedIndexes,row) == null)
            return;
        
        TableDataModel<TableDataRow<Integer>> unlockModel = new TableDataModel<TableDataRow<Integer>>();
        unlockModel.add(new TableDataRow<Integer>(new Integer(row)));
        
        unlockRows(unlockModel, forced);
    }
    
    public void unlockRows(final TableDataModel<TableDataRow<Integer>> lockedRowIndexes, final boolean forced){
        if(lockedRowIndexes.size() == 0)
            return;
        
        boolean unlock;
        TableDataModel<TableDataRow<Integer>> lockedSets = getLockedSetsFromLockedList(lockedRowIndexes);
        //unlock the record
        FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        foiirpc.tableData = lockedSets;

        FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)lockedSets.get(0).getData();
        
        unlock = true;
        if (tableRowSubRPC != null && tableRowSubRPC.changed && !forced) 
            unlock = Window.confirm(consts.get("fillOrderItemsChangedConfirm"));
        
        if(unlock){
            screenService.call("unlockOrders", foiirpc, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    for(int i=0; i<lockedRowIndexes.size(); i++){
                        int key=lockedRowIndexes.get(i).key.intValue();
                        //uncheck the row
                        fillItemsTable.model.setCell(lockedRowIndexes.get(i).key.intValue(), 0, CheckBox.UNCHECKED);
                        
                        lockedIndexes.delete(ModelUtil.getRowByKey(lockedIndexes, key));
                        
                        //rebuild tree for selected row
                        rebuildOrderItemsTree(key);
                    }
                    
                    //load sub form for selected table row
                    loadSubForm(fillItemsTable.model.getSelectedIndex());
                    
               }

               public void onFailure(Throwable caught) {
                   Window.alert(caught.getMessage());
               }
            });
        } else {
            // set the check box back to checked
            fillItemsTable.model.setCell(lockedRowIndexes.get(0).key.intValue(), 0, CheckBox.CHECKED);
        }
    }
    
    public void fetchSubForm(final int row, boolean refresh){
        TableDataRow<FillOrderItemInfoForm> tableRow = null;
        
        if(row >= 0){
            tableRow = fillItemsTable.model.getRow(row);

            FillOrderItemInfoForm foii = (FillOrderItemInfoForm)tableRow.getData();
            
            if(foii == null || refresh){
                //fetch the data
                FillOrderItemInfoForm foirpc = new FillOrderItemInfoForm(form.itemInformation.node);
                foirpc.entityKey = getOrderId(row);
                foirpc.originalOrderItemsTree.setValue(emptyTreeModel.clone());
                
                
                screenService.call("getOrderItemsOrderNotes", foirpc, new AsyncCallback<FillOrderItemInfoForm>() {
                   public void onSuccess(FillOrderItemInfoForm result) {
                       fillItemsTable.model.getRow(row).setData(result);

                       //rebuild the tree
                       rebuildOrderItemsTree(row);
                       
                       loadSubForm(row);
                     
                   }

                   public void onFailure(Throwable caught) {
                       Window.alert(caught.getMessage());
                   }
               });
            }else{
                loadSubForm(row);
            }
        }

    }
    
    public void loadSubForm(int row){
        if(row == fillItemsTable.model.getSelectedIndex()){
            FillOrderItemInfoForm subRPC = (FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData();
            
            orderItemsTree.model.unload();
            if(isRowChecked(row)){
                subRPC.displayOrderItemsTree.setValue(combinedTree);
            }else{
                subRPC.displayOrderItemsTree.setValue(subRPC.originalOrderItemsTree.getValue());
            }
            
            load(subRPC);
        }
    }
   
    //
    // start tree manager methods
    //
    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        return false;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           TreeDataItem dropTarget,
                           int targetRow) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        // make sure the row is checked, we arent a parent leaf, and its col 0 or 3
        return (col == 0 || col == 3) && orderItemsTree.model.getData() == combinedTree && "orderItem".equals(set.leafType);

    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        if (state == State.ADD)
            return true;
        return false;
    }

    public void drop(TreeWidget widget,
                     Widget dragWidget,
                     TreeDataItem dropTarget,
                     int targetRow) {
    }

    public void drop(TreeWidget widget, Widget dragWidget) {
    }
    //
    // end tree manager methods
    //

    //
    // start tree widget listener methods
    //
    public void finishedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        // we need to set the changed flag is the value is changed
        // we also need to save the new model if the value is changed
        if (orderItemsTree.model.getCell(row, col) != null && !orderItemsTree.model.getCell(row, col).equals(lastTreeValue)) {
            if (col == 0) {
                // we need to add up all the child nodes and get a new parent quantity, if necessary
                TreeDataItem item = orderItemsTree.model.getRow(row);
                Integer newTotalQuantity = new Integer(0);
                if (item.parent != null) {
                    for (int i = 0; i < item.parent.getItems().size(); i++) {
                        newTotalQuantity += (Integer)item.parent.getItems()
                                                                .get(i)
                                                                .cells[0]
                                                                .getValue();
                    }

                    item.parent.setData(new IntegerObject(newTotalQuantity));
                    // orderItemsTree.model.refresh();
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload();

            } else if (col == 3) {
                TreeDataItem item = orderItemsTree.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)item.cells[3]).getValue();

                TableDataRow<Field> set = null;
                if (selections.size() > 0)
                    set = (TableDataRow<Field>)selections.get(0);

                if (set != null && set.size() > 1) {
                    // set the lot num and quantity on hand
                    orderItemsTree.model.setCell(row, 4, (String)((DataObject)set.cells[1]).getValue());
                    orderItemsTree.model.setCell(row, 5, (Integer)((DataObject)set.cells[2]).getValue());
                }else{
                    orderItemsTree.model.setCell(row, 4, null);
                    orderItemsTree.model.setCell(row, 5, null);
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload();

            }

            // need to add a changed flag to the fill order table row
            int currentTableRow = fillItemsTable.model.getSelectedIndex();
            FillOrderOrderItemsKey hiddenData = (FillOrderOrderItemsKey)orderItemsTree.model.getRow(row).getData();
            FillOrderItemInfoForm tableRowSubRpc = (FillOrderItemInfoForm)fillItemsTable.model.getRow(currentTableRow).getData();
            tableRowSubRpc.changed = true;
        }

        lastTreeValue = null;

    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
            lastTreeValue = orderItemsTree.model.getCell(row, col);
    }

    public void stopEditing(SourcesTreeWidgetEvents sender, int row, int col) {
    }

    //
    // end tree widget listener methods
    //

    //
    // auto complete method
    //
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
        Integer currentTreeRow = orderItemsTree.model.getSelectedIndex();
        FillOrderLocationAutoRPC folarpc = new FillOrderLocationAutoRPC();
        folarpc.cat = widget.cat;
        folarpc.match = text;
        folarpc.id = (Integer)((DropDownField)orderItemsTree.model.getObject(currentTreeRow, 2)).getSelectedKey();

        screenService.call("getMatchesObj",
                           folarpc,
                           new AsyncCallback<FillOrderLocationAutoRPC>() {
                               public void onSuccess(FillOrderLocationAutoRPC result) {
                                   widget.showAutoMatches(result.autoMatches);
                               }

                               public void onFailure(Throwable caught) {
                                   if (caught instanceof FormErrorException) {
                                       window.setStatus(caught.getMessage(),
                                                        "ErrorPanel");
                                   } else
                                       Window.alert(caught.getMessage());
                               }
                           });
    }

    private TableDataModel createDataModelFromTree(TreeDataModel treeModel) {
        TableDataModel model = new TableDataModel();
        for (int i = 0; i < treeModel.size(); i++) {
            TreeDataItem row = treeModel.get(i);

            if (row.getItems().size() > 0) {
                for (int j = 0; j < row.getItems().size(); j++) {
                    TableDataRow<Field> tableRow = new TableDataRow<Field>(2);
                    TreeDataItem childRow = row.getItems().get(j);
                    if (!((Integer)childRow.cells[0].getValue()).equals(new Integer(0))) {
                        tableRow.cells[0] = (childRow.cells[0]);
                        tableRow.cells[1] = (childRow.cells[2]);

                        FillOrderOrderItemsKey key = (FillOrderOrderItemsKey)childRow.getData();
                        ShippingItemsData rowData = new ShippingItemsData();
                        rowData.referenceTableId = key.referenceTableId;
                        rowData.referenceId = key.referenceId;
                        tableRow.setData(rowData);

                        model.add(tableRow);
                    }
                }
            } else {
                if (!((Integer)row.cells[0].getValue()).equals(new Integer(0))) {
                    TableDataRow tableRow = new TableDataRow(2);
                    tableRow.cells[0] = (row.cells[0]);
                    tableRow.cells[1] = (row.cells[2]);

                    FillOrderOrderItemsKey key = (FillOrderOrderItemsKey)row.getData();
                    ShippingItemsData rowData = new ShippingItemsData();
                    rowData.referenceTableId = key.referenceTableId;
                    rowData.referenceId = key.referenceId;
                    tableRow.setData(rowData);

                    model.add(tableRow);
                }
            }
        }
        return model;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           Widget dropWidget) {
        return false;
    }

    public void onRemoveRowButtonClick() {
        orderItemsTree.model.deleteRow(orderItemsTree.model.getSelectedIndex());
        combinedTree = (TreeDataModel)orderItemsTree.model.unload().clone();
    }

    public void onAddLocationButtonClick() {
        TreeDataItem selectedRow = null;
        int currentTreeRow = orderItemsTree.model.getSelectedIndex();
        // make sure the selected row is at the lowest level
        if (orderItemsTree.model.getRow(currentTreeRow).getItems().size() == 0)
            selectedRow = orderItemsTree.model.getRow(currentTreeRow);
        else
            selectedRow = orderItemsTree.model.getRow(currentTreeRow)
                                              .getItem(0);

        TreeDataItem item = orderItemsTree.model.createTreeItem("orderItem");
        // item.get(0).setValue(selectedRow.get(0).getValue());
        item.cells[1].setValue(selectedRow.cells[1].getValue());
        ((DropDownField)item.cells[2]).setModel(((DropDownField)selectedRow.cells[2]).getModel());
        item.cells[2].setValue(selectedRow.cells[2].getValue());

        FillOrderOrderItemsKey treeRowHiddenData = (FillOrderOrderItemsKey)((FillOrderOrderItemsKey)selectedRow.getData()).clone();
        item.setData(treeRowHiddenData);

        if (selectedRow.parent == null) {
            selectedRow.addItem(item);
            if (!selectedRow.open)
                selectedRow.toggle();
        } else {
            selectedRow.parent.addItem(item);

        }

        // TODO eventually select the first column of the added row

        orderItemsTree.model.refresh();
    }

    //
    // start tree model listener methods
    //
    public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {
    }

    public void dataChanged(SourcesTreeModelEvents sender) {
    }

    public void rowAdded(SourcesTreeModelEvents sender, int rows) {
    }

    public void rowClosed(SourcesTreeModelEvents sender,
                          int row,
                          TreeDataItem item) {
    }

    public void rowDeleted(SourcesTreeModelEvents sender, int row) {
    }

    public void rowOpened(SourcesTreeModelEvents sender,
                          int row,
                          TreeDataItem item) {
    }

    public void rowSelectd(SourcesTreeModelEvents sender, int row) {
        boolean parentNull = ((TreeDataItem)orderItemsTree.model.getRow(row)).parent == null;
        if (parentNull || (!parentNull && ((TreeDataItem)orderItemsTree.model.getRow(row)).childIndex == 0))
            removeRowButton.changeState(ButtonState.DISABLED);
        else
            removeRowButton.changeState(ButtonState.UNPRESSED);

        if (state == State.ADD && CheckBox.CHECKED.equals(fillItemsTable.table.model.getCell(fillItemsTable.model.getSelectedIndex(), 0)))
            addLocationButton.changeState(ButtonState.UNPRESSED);

    }

    public void rowUnselected(SourcesTreeModelEvents sender, int row) {
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {
    }

    public void unload(SourcesTreeModelEvents sender) {
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    //
    // end tree model listener
    //

    protected boolean validate() {
        boolean valid = true;
        // we need to iterate through the tree and make sure there is enough qty set to fill the orders
        for (int i = 0; i < combinedTree.size(); i++) {
            TreeDataItem row = combinedTree.get(i);
            if (row.getData() != null) {
                Integer totalQty = ((IntegerObject)row.getData()).getValue();
                Integer currentQty = (Integer)row.cells[0].getValue();
                if (currentQty > 0 && totalQty.compareTo((Integer)row.cells[0].getValue()) < 0) {
                    valid = false;
                    ((IntegerField)row.cells[0]).addError(consts.get("fillOrderQtyException"));
                }
            }
            
            //iterate through the children to make sure we will have enough qty on hand
            for(int j=0; j<row.getItems().size(); j++){
                TreeDataItem childRow = row.getItem(j);
                if(childRow.cells[5].getValue() != null){
                    int qtyOnHand = ((Integer)childRow.cells[5].getValue()).intValue();
                    int qtyRequested = ((Integer)childRow.cells[5].getValue()).intValue();
                    if((qtyOnHand - qtyRequested < 0)){
                        valid = false;
                        ((IntegerField)childRow.cells[5]).addError(consts.get("notEnoughQuantityOnHand"));
                    }
                    
                    if(qtyRequested < 0){
                        valid = false;
                        ((IntegerField)childRow.cells[5]).addError(consts.get("invalidQuantityException"));
                    }
                }
            }
        }
        
        if(!valid)
            ((TreeRenderer)orderItemsTree.renderer).dataChanged(orderItemsTree.model);
        
        return valid;
    }

    public boolean ifDifferent(Object arg1, Object arg2){
        return (arg1 == null && arg2 != null) || (arg1 != null && !arg1.equals(arg2));
        
    }
    
    public boolean isRowChecked(int row){
        return "Y".equals(fillItemsTable.model.getCell(row, 0));
    }
    
    public Integer getOrderId(int row){
        return (Integer)fillItemsTable.table.model.getCell(row, 1);
    }
    
    public TableDataModel<TableDataRow<Integer>> getLockedSetsFromLockedList(TableDataModel<TableDataRow<Integer>> lockedList){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        for(int i=0; i<lockedList.size(); i++)
            returnModel.add(fillItemsTable.model.getRow(lockedList.get(i).key.intValue()));
        
        return returnModel;
    }

    public void sort(TableDataModel data, int col, SortDirection direction) {
        if(state != State.ADD)
            sorter.sort(data, col, direction);
        else
            window.setStatus("cant sort", "ErrorPanel");
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
*/
