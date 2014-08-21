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
package org.openelis.modules.shipping.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
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
import org.openelis.manager.OrderManager;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.modules.order.client.SendoutOrderScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.SyncCallback;

public class ItemTab extends Screen {
    
    private ShippingManager                manager;
    private OrderManager                   orderManager;
    private Table                          itemTable, trackingTable;
    private Button                         addItemButton, removeItemButton, addTrackingButton,
                                           removeTrackingButton, lookupItemButton;
    private ScreenService                  orderService;    
    private SendoutOrderScreen             sendoutOrderScreen;    
    private boolean                        loaded;

    public ItemTab(ScreenDefInt def, Window window) {
        this.orderService = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        setDefinition(def);
        setWindow(window);        
        
        initialize();
    }
    
    private void initialize() {
        itemTable = (Table)def.getWidget("itemTable");
        addScreenHandler(itemTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                itemTable.setModel(getItemTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTable.setEnabled(true);
                itemTable.setQueryMode(false);
            }
        });
        
        itemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                event.cancel();           
            }            
        });
        
        itemTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                lookupItemButton.setEnabled(true);   
            }           
        });
        
        itemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ShippingItemDO data;
                
                r = event.getRow();
                c = event.getCol();
                
                val = itemTable.getValueAt(r,c);
                
                try {
                    data = manager.getItems().getItemAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        data.setQuantity((Integer)val);
                        break;
                    case 1:
                        data.setDescription((String)val);
                        break;
                }

            }
        });

        itemTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getItems().addItem();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        itemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });
        
        addItemButton = (Button)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) { 
                int n;

                itemTable.addRow();
                n = itemTable.getRowCount() - 1;
                itemTable.selectRowAt(n);
                itemTable.scrollToVisible(n);
                itemTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.setEnabled(false);
            }
        });

        removeItemButton = (Button)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {       
                int r;

                r = itemTable.getSelectedRow();
                if (r > -1 && itemTable.getRowCount() > 0)
                    itemTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.setEnabled(false);
            }
        });
        
        lookupItemButton = (Button)def.getWidget("lookupItemButton");
        addScreenHandler(lookupItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {                       
                showParent();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lookupItemButton.setEnabled(false);
            }
        });

        trackingTable = (Table)def.getWidget("trackingTable");
        addScreenHandler(trackingTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    trackingTable.setModel(getTrackingNumbersTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingTable.setEnabled(true);
                trackingTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        trackingTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY) { 
                    event.cancel();
                } 
            }           
        });

        trackingTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                ShippingTrackingDO data;
                
                r = event.getRow();
                c = event.getCol();
                
                val = (String)trackingTable.getValueAt(r,c);
                
                try {
                    data = manager.getTrackings().getTrackingAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        data.setTrackingNumber(val);
                        break;
                }

            }
        });

        trackingTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getTrackings().addTracking();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        trackingTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTrackings().removeTrackingAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });
        
        addTrackingButton = (Button)def.getWidget("addTrackingButton");
        addScreenHandler(addTrackingButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                trackingTable.addRow();
                n = trackingTable.getRowCount() - 1;
                trackingTable.selectRowAt(n);
                trackingTable.scrollToVisible(n);
                trackingTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTrackingButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeTrackingButton = (Button)def.getWidget("removeTrackingButton");
        addScreenHandler(removeTrackingButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = trackingTable.getSelectedRow();
                if (r > -1 && trackingTable.getRowCount() > 0)
                    trackingTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTrackingButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
    }    
    
    private ArrayList<Row> getTrackingNumbersTableModel() {
        int i;
        ShippingTrackingDO data;
        ArrayList<Row> model;
        Row row;
        ShippingTrackingManager man;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;
         
        try {
            man = manager.getTrackings();
            for(i = 0; i < man.count(); i++) {
                data = man.getTrackingAt(i);
                row = new Row(null, data.getTrackingNumber());  
                
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }

    public void setManager(ShippingManager manager) {        
        this.manager = manager;
        loaded = false;        
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;        
    }
    
    private ArrayList<Row> getItemTableModel(){
        ArrayList<Row> model;
        ShippingItemManager man;
        ShippingItemDO data;
        Row row;

        int count, i;

        model = new ArrayList<Row>();

        try {
            man = manager.getItems();
            count = man.count();

            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                row = new Row(2);
                row.setCell(0,data.getQuantity());
                row.setCell(1,data.getDescription());
                row.setData(data);
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.toString());
        }

        return model;
    }
    
    private void showParent() {
        Row row;
        ShippingItemDO data;
        
        row = itemTable.getRowAt(itemTable.getSelectedRow());
        
        if(row == null) 
            return;
        
        data = (ShippingItemDO)row.getData();
        if(data.getReferenceTableId().equals(ReferenceTable.ORDER_ITEM)) {
            showOrder(data);
        } else if(data.getReferenceTableId().equals(ReferenceTable.SAMPLE_ITEM)) {
            showSample(data);   
        }        
    }

    private void showOrder(ShippingItemDO data) {
        ModalWindow modal;
        
        try {
            window.setBusy(consts.get("fetching"));
            orderService.call("fetchByShippingItemId", data.getReferenceId(), new SyncCallback<OrderViewDO>() {
                public void onSuccess(OrderViewDO result) {                                    
                    try {
                        if(result != null)
                            orderManager = OrderManager.fetchById(result.getId());
                        else
                            orderManager = null;
                    } catch (Throwable e) {
                        orderManager = null;
                        e.printStackTrace();
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                        window.clearStatus();
                    }                                        
                }
                public void onFailure(Throwable error) {    
                    orderManager = null;
                    error.printStackTrace();
                    com.google.gwt.user.client.Window.alert("Error: Fetch failed; " + error.getMessage());                    
                    window.clearStatus();
                }
            });    
            
            if(orderManager != null) {
                modal = new ModalWindow();
                modal.setName(consts.get("kitOrder"));
                if (sendoutOrderScreen == null)
                    sendoutOrderScreen = new SendoutOrderScreen(modal);
                                
                modal.setContent(sendoutOrderScreen);
                sendoutOrderScreen.setManager(orderManager);                        
                window.clearStatus();
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.clearStatus();
            return;
        }
        
    }

    private void showSample(ShippingItemDO data) {
        // TODO Auto-generated method stub        
    }
}
