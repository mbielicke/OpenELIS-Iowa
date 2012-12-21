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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
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
import org.openelis.modules.order.client.OrderService;
import org.openelis.modules.order.client.SendoutOrderScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;

public class ItemTab extends Screen {
    
    private ShippingManager                manager;
    private OrderManager                   orderManager;
    private TableWidget                    itemTable, trackingTable;
    private AppButton                      addItemButton, removeItemButton, addTrackingButton,
                                           removeTrackingButton, lookupItemButton;
    private SendoutOrderScreen             sendoutOrderScreen;    
    private boolean                        loaded;

    public ItemTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);        
        
        initialize();
    }
    
    private void initialize() {
        itemTable = (TableWidget)def.getWidget("itemTable");
        addScreenHandler(itemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                itemTable.load(getItemTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTable.enable(true);
                itemTable.setQueryMode(false);
            }
        });
        
        itemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                event.cancel();           
            }            
        });
        
        itemTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                lookupItemButton.enable(true);   
            }           
        });
        
        itemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ShippingItemDO data;
                
                r = event.getRow();
                c = event.getCol();
                
                val = itemTable.getObject(r,c);
                
                try {
                    data = manager.getItems().getItemAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                    Window.alert(e.getMessage());
                }
            }
        });

        itemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        addItemButton = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) { 
                int n;

                itemTable.addRow();
                n = itemTable.numRows() - 1;
                itemTable.selectRow(n);
                itemTable.scrollToSelection();
                itemTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.enable(false);
            }
        });

        removeItemButton = (AppButton)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {       
                int r;

                r = itemTable.getSelectedRow();
                if (r > -1 && itemTable.numRows() > 0)
                    itemTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.enable(false);
            }
        });
        
        lookupItemButton = (AppButton)def.getWidget("lookupItemButton");
        addScreenHandler(lookupItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {                       
                showParent();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lookupItemButton.enable(false);
            }
        });

        trackingTable = (TableWidget)def.getWidget("trackingTable");
        addScreenHandler(trackingTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    trackingTable.load(getTrackingNumbersTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingTable.enable(true);
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
                
                val = (String)trackingTable.getObject(r,c);
                
                try {
                    data = manager.getTrackings().getTrackingAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                    Window.alert(e.getMessage());
                }
            }
        });

        trackingTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTrackings().removeTrackingAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        addTrackingButton = (AppButton)def.getWidget("addTrackingButton");
        addScreenHandler(addTrackingButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                trackingTable.addRow();
                n = trackingTable.numRows() - 1;
                trackingTable.selectRow(n);
                trackingTable.scrollToSelection();
                trackingTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTrackingButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeTrackingButton = (AppButton)def.getWidget("removeTrackingButton");
        addScreenHandler(removeTrackingButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = trackingTable.getSelectedRow();
                if (r > -1 && trackingTable.numRows() > 0)
                    trackingTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTrackingButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
    }    
    
    private ArrayList<TableDataRow> getTrackingNumbersTableModel() {
        int i;
        ShippingTrackingDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        ShippingTrackingManager man;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
         
        try {
            man = manager.getTrackings();
            for(i = 0; i < man.count(); i++) {
                data = man.getTrackingAt(i);
                row = new TableDataRow(null, data.getTrackingNumber());  
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
    
    private ArrayList<TableDataRow> getItemTableModel(){
        ArrayList<TableDataRow> model;
        ShippingItemManager man;
        ShippingItemDO data;
        TableDataRow row;

        int count, i;

        model = new ArrayList<TableDataRow>();

        try {
            man = manager.getItems();
            count = man.count();

            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                row = new TableDataRow(2);
                row.cells.get(0).setValue(data.getQuantity());
                row.cells.get(1).setValue(data.getDescription());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }

        return model;
    }
    
    private void showParent() {
        TableDataRow row;
        ShippingItemDO data;
        
        row = itemTable.getSelection();
        
        if(row == null) 
            return;
        
        data = (ShippingItemDO)row.data;
        if(data.getReferenceTableId().equals(ReferenceTable.ORDER_ITEM)) {
            showOrder(data);
        } else if(data.getReferenceTableId().equals(ReferenceTable.SAMPLE_ITEM)) {
            showSample(data);   
        }        
    }

    private void showOrder(ShippingItemDO data) {
        ScreenWindow modal;
        
        try {
            window.setBusy(consts.get("fetching"));
            OrderService.get().fetchByShippingItemId(data.getId(), new SyncCallback<OrderViewDO>() {
                public void onSuccess(OrderViewDO result) {                                    
                    try {
                        if(result != null)
                            orderManager = OrderManager.fetchById(result.getId());
                        else
                            orderManager = null;
                    } catch (Throwable e) {
                        orderManager = null;
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                        window.clearStatus();
                    }                                        
                }
                public void onFailure(Throwable error) {    
                    orderManager = null;
                    error.printStackTrace();
                    Window.alert("Error: Fetch failed; " + error.getMessage());                    
                    window.clearStatus();
                }
            });    
            
            if(orderManager != null) {
                modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
                modal.setName(consts.get("sendoutOrder"));
                if (sendoutOrderScreen == null)
                    sendoutOrderScreen = new SendoutOrderScreen(modal);
                                
                modal.setContent(sendoutOrderScreen);
                sendoutOrderScreen.setManager(orderManager);                        
                window.clearStatus();
            }
            
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
            return;
        }
        
    }

    private void showSample(ShippingItemDO data) {
        // TODO Auto-generated method stub        
    }
}
