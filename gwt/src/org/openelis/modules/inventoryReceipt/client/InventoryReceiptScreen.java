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
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.modules.inventoryReceipt.client.ItemTab.Action;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.order.client.ShipNoteTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;

public class InventoryReceiptScreen extends Screen implements ActionHandler<ItemTab.Action> {
    
    private SecurityModule                            security;

    private InventoryReceiptScreen                    screen;
    private ItemTab                                   itemTab;
    private VendorAddressTab                          vendorTab;
    private ShipNoteTab                               shipNoteTab;
    private Tabs                                      tab;

    private TableWidget                               receiptTable;
    private AppButton                                 queryButton, addButton, updateButton,
                                                      commitButton, abortButton, addReceiptButton,
                                                      removeReceiptButton;
    private TableColumn                               dateRecColumn, upcColumn, numRecColumn,
                                                      costColumn, itemColumn, orgColumn;
    private AutoComplete<Integer>                     upc, inventoryItem, organization;
    private TabPanel                                  tabPanel;
    
    private ArrayList<TableDataRow>                   receiptModel; 
    private HashMap<Integer, InventoryItemViewDO>     inventoryItemMap;  
    private Query                                     query;
    private boolean                                   newQuery; 
    private String                                    upcQuery;
    private int                                       newManagerIndex;              
    
    private ScreenService                             inventoryItemService, organizationService;        
    
    private enum Tabs {
        ITEM, VENDOR_ADDRESS, SHIP_NOTE
    };
    
    public InventoryReceiptScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryReceiptDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");
        inventoryItemService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        security = OpenELIS.security.getModule("inventoryreceipt");
        if (security == null)
            throw new SecurityException("screenPermException", "Inventory Receipt Screen");

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
        tab = Tabs.ITEM;
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
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
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

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        receiptTable = (TableWidget)def.getWidget("receiptTable");
        addScreenHandler(receiptTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state == State.ADD || newQuery)          
                    receiptTable.load(receiptModel);                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receiptTable.enable(true);
                receiptTable.setQueryMode(event.getState() == State.QUERY);
                upcQuery = null;
            }
        });

        receiptTable.addSelectionHandler(new SelectionHandler<TableRow>(){
            public void onSelection(SelectionEvent<TableRow> event) {                                               
                int index;
                InventoryReceiptManager man;
                InventoryReceiptDataBundle bundle;
                OrderManager order;
                TableDataRow row;
                
                row = receiptTable.getSelection();                
                bundle = (InventoryReceiptDataBundle)row.data;                                
                man = bundle.getManager();
                index = bundle.getManagerIndex();
                
                itemTab.setManager(man, index, screen);
                vendorTab.setManager(man, index); 
                
                try { 
                    order = man.getOrder();
                    if(state == State.ADD || state == State.UPDATE) {
                        if(order != null) 
                            shipNoteTab.setState(state);
                        else
                            shipNoteTab.setState(State.DISPLAY);                            
                    } else {
                        shipNoteTab.setState(State.DISPLAY);
                    }
                    shipNoteTab.setManager(order);
                } catch(Exception ex) {
                    ex.printStackTrace();
                    Window.alert(ex.getMessage());
                }                
                itemTab.setState(state);
                drawTabs();
            }            
        });
        
        receiptTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) { 
                int c;

                c = event.getCol();

                if (state == State.UPDATE) {
                    if (c != 2 && c != 7 && c != 8)
                        event.cancel();
                } else if (state == State.ADD) {
                    if (c == 0 || c == 1 || c == 6)
                        event.cancel();
                } else {
                    event.cancel();
                }
            }            
        });
        
        receiptTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow tableRow, valRow, tmpRow;
                Integer numRec, index;
                InventoryReceiptViewDO data;
                InventoryReceiptManager man;
                InventoryReceiptDataBundle bundle;
                InventoryItemDO item;
                OrganizationDO org;
                IdNameVO upcData;
                Datetime dateRec;
                LocalizedException ex;
                ArrayList<LocalizedException> exceptions;

                r = event.getRow();
                c = event.getCol();
                tableRow = receiptTable.getRow(r);
                val = receiptTable.getObject(r,c);
                
                bundle = (InventoryReceiptDataBundle)tableRow.data;
                index = bundle.getManagerIndex();
                man = bundle.getManager();                
                data = man.getReceiptAt(index);
                
                switch(c) {                        
                    case 2:
                        data.setReceivedDate((Datetime)val);   
                        break;
                    case 3:
                        valRow = (TableDataRow)val; 
                        if(valRow != null) {
                            upcData = (IdNameVO)valRow.data;
                            data.setUpc(upcData.getName());
                            if(!upcData.getId().equals(-1)) {
                                tmpRow = new TableDataRow(upcData.getId(), upcData.getDescription());
                                item = getInventoryItem(upcData.getId());
                                tmpRow.data = item;
                                exceptions = tableRow.cells.get(4).getExceptions();
                                if (exceptions != null) {
                                    for (int i = 0; i < exceptions.size(); i++) {
                                        ex = exceptions.get(i);
                                        if ("fieldRequiredException".equals(ex.getKey())) {
                                            exceptions.remove(i);
                                            break;
                                        }                                        
                                    }
                                    
                                    if(exceptions.size() == 0) 
                                        tableRow.cells.get(4).clearExceptions();
                                }
                                receiptTable.setCell(r, 4, tmpRow);
                            }
                        } else {
                            data.setUpc(null);
                        }
                        break;
                    case 4:
                        valRow = (TableDataRow)val; 
                        if(valRow != null) {
                            item = (InventoryItemDO)valRow.data;
                            data.setInventoryItemId(item.getId());                                                       
                        } else {                                                       
                            data.setInventoryItemId(null);                                                       
                        }   
                        itemTab.setManager(man, index, screen);
                        drawTabs();
                        break;
                    case 5:
                        valRow = (TableDataRow)val; 
                        if(valRow != null) {
                            org = (OrganizationDO)valRow.data;                            
                            data.setOrganizationId(org.getId());
                            data.setOrganization(org);
                        } else {                                                        
                            data.setOrganizationId(null);
                            data.setOrganization(null);
                        }
                        vendorTab.setManager(man, index);
                        drawTabs();
                        break;                    
                    case 7:
                        numRec = (Integer)val;
                        data.setQuantityReceived(numRec);
                        if (numRec != null && numRec > 0) {                                                                                            
                            dateRec = data.getReceivedDate();
                            if (dateRec == null) {
                                try {
                                    dateRec = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
                                } catch (Exception e) {
                                    Window.alert("Inventory Receipt Datetime: " +e.getMessage());
                                    return;
                                }
                                data.setReceivedDate(dateRec);
                                //
                                // this will get rid of any exceptions that were
                                // added to the coulmn showing date recieved
                                // because of wrong formatting or a value not 
                                // being present
                                //
                                tableRow.cells.get(2).clearExceptions();
                                receiptTable.setCell(r, 2, dateRec);
                            }         
                            
                            if (data.getUnitCost() == null) {
                                data.setUnitCost(data.getOrderItemUnitCost());
                                receiptTable.setCell(r, 8, data.getUnitCost());
                            }
                        }
                        break;
                    case 8:
                        data.setUnitCost((Double)val);
                        break;
                }                
            }
        });

        receiptTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow row;
                InventoryReceiptDataBundle bundle;
                InventoryReceiptManager manager;
                
                row = event.getRow();
                manager = InventoryReceiptManager.getInstance();
                manager.addReceipt(new InventoryReceiptViewDO());
                bundle = new InventoryReceiptDataBundle(0, null, manager);
                row.data = bundle;
            }
        });
        
        dateRecColumn = receiptTable.getColumns().get(2);
        upcColumn = receiptTable.getColumns().get(3);
        itemColumn = receiptTable.getColumns().get(4);
        orgColumn = receiptTable.getColumns().get(5);
        numRecColumn = receiptTable.getColumns().get(7);
        costColumn = receiptTable.getColumns().get(8);
            
        upc = (AutoComplete<Integer>)receiptTable.getColumnWidget(InventoryReceiptMeta.getUpc());
        upc.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ArrayList<TableDataRow> model;                
                QueryFieldUtil parser;
                IdNameVO data;
                ArrayList<IdNameVO> list;  
                String match;
                
                list = null;
                match = event.getMatch();
                window.setBusy();
                
                try {
                    model = new ArrayList<TableDataRow>();
                    
                    row = new TableDataRow(-1, match);
                    row.data = new IdNameVO(-1, match, null);
                    model.add(row);
                    
                    if(upcQuery == null || (!(match.indexOf(upcQuery) == 0))) {
                        parser = new QueryFieldUtil();
                        parser.parse(match);
                        list = service.callList("fetchByUpc", parser.getParameter().get(0));
                        for (int i = 0; i < list.size(); i++ ) {
                            data = list.get(i);
                            row = new TableDataRow(data.getId(), data.getName(), data.getDescription());                  
                            row.data = data;
                            model.add(row);
                        } 
                        
                        if(list.size() == 0)
                            upcQuery = match;
                    }                                                                                    
                    
                    upc.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();                            
            }
        });
            
        inventoryItem = (AutoComplete<Integer>)receiptTable.getColumnWidget(InventoryReceiptMeta.getInventoryItemName());
        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
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
                        row = new TableDataRow(data.getId(), data.getName(),
                                               store.getEntry(), units.getEntry());
                        row.data = data;
                        model.add(row);
                    }
                    inventoryItem.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        organization = (AutoComplete<Integer>)receiptTable.getColumnWidget(InventoryReceiptMeta.getOrganizationName());
        organization.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();
                try {
                    list = organizationService.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        
                        row.data = data;
                        
                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();               
            }            
        });
        
        addReceiptButton = (AppButton)def.getWidget("addReceiptButton");
        addScreenHandler(addReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;       
                OrderManager order;
                
                order = null;
                receiptTable.addRow();                
                n = receiptTable.numRows() - 1;
                receiptTable.selectRow(n);
                receiptTable.scrollToSelection();
                receiptTable.startEditing(n, 2);
                
                itemTab.setManager(null, -1, screen);
                vendorTab.setManager(null, -1);
                shipNoteTab.setManager(order);
                drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReceiptButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        removeReceiptButton = (AppButton)def.getWidget("removeReceiptButton");
        addScreenHandler(removeReceiptButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                OrderManager order;
                
                r = receiptTable.getSelectedRow();
                order = null;
                if (r > -1 && receiptTable.numRows() > 0) {
                    receiptTable.deleteRow(r);
                    //
                    // after a row is removed no row is in selected state,
                    // thus there's no information to be shown in the tabs,
                    // hence the values in the widgets in them need to be cleared out
                    //
                    itemTab.setManager(null, -1, screen);
                    vendorTab.setManager(null, receiptTable.getSelectedRow());
                    shipNoteTab.setManager(order);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReceiptButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

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
        
        itemTab.addActionHandler(this);

        vendorTab = new VendorAddressTab(def, window);
        addScreenHandler(vendorTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.VENDOR_ADDRESS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                vendorTab.setState(event.getState());
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
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
        
        screen = this;
        inventoryItemMap = new HashMap<Integer, InventoryItemViewDO>();
    }
    
    protected void query() {    
        OrderManager order;
        
        order = OrderManager.getInstance();     
        newQuery = false;
                
        setState(State.QUERY);        
        DataChangeEvent.fire(this);
        
        itemTab.setManager(null, -1, screen);
        vendorTab.setManager(null, -1);
        shipNoteTab.setManager(order);
        
        drawTabs();
        
        enableColumns(false);
        window.setDone(consts.get("enterFieldsToQuery"));       
    }
    
    protected void add() {
        OrderManager order;
        
        order = OrderManager.getInstance();
        query = null;
        receiptModel =  new ArrayList<TableDataRow>();
        setState(State.ADD);
        DataChangeEvent.fire(this);
        
        itemTab.setManager(null, -1, screen);
        vendorTab.setManager(null, -1);
        shipNoteTab.setManager(order);
        shipNoteTab.setState(State.DISPLAY);
        
        drawTabs();
        
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    protected void update() {
        OrderManager order;
        
        if (query == null) {
            Window.alert(consts.get("queryExeBeforeUpdate"));
            return;
        }
        order = OrderManager.getInstance();         
        window.setBusy(consts.get("lockForUpdate"));           
        executeQuery(query);  
        setState(State.UPDATE);
        DataChangeEvent.fire(this);
        
        itemTab.setManager(null, -1, screen);
        vendorTab.setManager(null, -1);
        shipNoteTab.setManager(order);
        shipNoteTab.setState(State.DISPLAY);
        
        drawTabs();
        
        window.clearStatus();
    }    

    protected void commit() {
        int i;
        boolean success;
        ArrayList<QueryData> fields;
        InventoryReceiptDataBundle bundle;
        InventoryReceiptManager prevMan, currMan;                
        TableDataRow row;     
        OrderManager order;
        
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
        
        if (state == State.QUERY) {
            query = new Query();
            fields = getQueryFields();

            query.setFields(fields);
            setState(State.DISPLAY);
            executeQuery(query);
            DataChangeEvent.fire(this);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            success = true;            
            for (i = 0; i < receiptTable.numRows(); i++ ) {
                row = receiptTable.getRow(i);
                bundle = (InventoryReceiptDataBundle)row.data;
                currMan = bundle.getManager();
                newManagerIndex = i;
                try {
                    bundle.setManager(currMan.add());
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                    success = false;
                } catch (Exception e) {
                    Window.alert("commitAdd(): " + e.getMessage());
                    window.clearStatus();
                    success = false;
                }                
            }            
            if (success) {
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            }                          
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));

            prevMan = null;
            success = true;
            for (i = 0; i < receiptTable.numRows(); i++ ) {
                row = receiptTable.getRow(i);
                bundle = (InventoryReceiptDataBundle)row.data;
                currMan = bundle.getManager();

                try {
                    if ( !currMan.equals(prevMan)) {
                        newManagerIndex = i;
                        bundle.setManager(currMan.update());
                    }
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                    success = false;
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    window.clearStatus();
                    success = false;
                }
                prevMan = currMan;
            }
            
            if (success) {
                order = OrderManager.getInstance();
                itemTab.setManager(null, -1, screen);
                vendorTab.setManager(null, receiptTable.getSelectedRow());
                shipNoteTab.setManager(order);
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            }
        }
    }    

    protected void abort() {
        OrderManager order;
        
        order = OrderManager.getInstance();
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        itemTab.setManager(null, -1, screen);
        vendorTab.setManager(null, receiptTable.getSelectedRow());
        shipNoteTab.setManager(order);
        
        if (state == State.QUERY) {            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            drawTabs();
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {            
            receiptModel = null;
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            drawTabs();
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {           
            executeQuery(query); 
            setState(State.DISPLAY);           
            DataChangeEvent.fire(this);
            drawTabs();
            window.setDone(consts.get("updateAborted"));
        }
    }
    
    protected InventoryItemViewDO getInventoryItem(Integer id) {
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
    
    private void drawTabs() {                
        switch (tab) {
            case ITEM:  
                itemTab.draw();                 
                break;
            case VENDOR_ADDRESS:
                vendorTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;    
        }
    }
    
    public void onAction(ActionEvent<ItemTab.Action> event) {
        int i,r;
        LocalizedException ex;
        ArrayList<LocalizedException> exceptions;
        TableDataRow row, val;        

        r = receiptTable.getSelectedRow();
        row = receiptTable.getSelection();
        val = (TableDataRow)receiptTable.getObject(r, 4);
        exceptions = row.cells.get(4).getExceptions();        
        if (event.getAction() == Action.LOT_NUMBER_CHANGED) {
            if(exceptions != null && event.getData() != null) {
                for (i = 0; i < exceptions.size(); i++) {
                    ex = exceptions.get(i);
                    if ("lotNumRequiredForOrderItemException".equals(ex.getKey())) {
                        exceptions.remove(i);
                        break;
                    }                                        
                }                
                
                //
                // this will redraw the exceptions
                //
                receiptTable.setCell(r, 4, val);    
                
                //
                // the list of exceptions for this  cell is set to null if it is
                // empty to make sure that in the method validate(), when checkValue()
                // is called for this cell, an empty but non-null list of exceptions
                // isn't confused with a list that contains exceptions and validate()
                // isn't made to return false 
                //
                if (exceptions.size() == 0) 
                    row.cells.get(4).clearExceptions();                    
            }
        } else if (event.getAction() == Action.STORAGE_LOCATION_CHANGED) {            
            if(exceptions != null && event.getData() != null) {
                for (i = 0; i < exceptions.size(); i++) {
                    ex = exceptions.get(i);
                    if ("storageLocReqForItemException".equals(ex.getKey())) {
                        exceptions.remove(i);
                        break;
                    }                                        
                }
                
                //
                // this will redraw the exceptions
                //
                receiptTable.setCell(r, 4, val);
                
                //
                // the list of exceptions for this  cell is set to null if it is
                // empty to make sure that in the method validate(), when checkValue()
                // is called for this cell, an empty but non-null list of exceptions
                // isn't confused with a list that contains exceptions and validate()
                // isn't made to return false 
                //
                if (exceptions.size() == 0) 
                    row.cells.get(4).clearExceptions();
            }
        }
    }
    
    public void showErrors(ValidationErrorsList list) {
        ArrayList<LocalizedException> formErrors;
        TableFieldErrorException tableE;
        FormErrorException formE;
        FieldErrorException fieldE;
        TableWidget tableWid;
        HasField field;

        formErrors = new ArrayList<LocalizedException>();
        for (Exception ex : list.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException) ex;
                tableWid = (TableWidget)def.getWidget(tableE.getTableKey());
                tableWid.setCellException(tableE.getRowIndex()+newManagerIndex, tableE.getFieldName(), tableE);
            } else if (ex instanceof FormErrorException) {
                formE = (FormErrorException)ex;
                formErrors.add(formE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasField)def.getWidget(fieldE.getFieldName());
                
                if(field != null)
                    field.addException(fieldE);
            }
        }

        if (formErrors.size() == 0)
            window.setError(consts.get("correctErrors"));
        else if (formErrors.size() == 1)
            window.setError(formErrors.get(0).getMessage());
        else {
            window.setError("(Error 1 of " + formErrors.size() + ") " +
                            formErrors.get(0).getMessage());
            window.setMessagePopup(formErrors, "ErrorPanel");
        }
    }
     
    private void executeQuery(Query query) {
        window.setBusy(consts.get("querying"));

        service.callList("query", query, new SyncCallback<ArrayList<InventoryReceiptManager>>() {
            public void onSuccess(ArrayList<InventoryReceiptManager> result) {
                int i, j, k, count;
                TableDataRow row;
                InventoryReceiptViewDO data;
                OrganizationDO organization;
                InventoryItemDO invItem;
                InventoryReceiptManager manager; 
                InventoryReceiptDataBundle bundle;
                Integer orderId;
                              
                invItem = null;                                
                newQuery = true;
                try {
                    if (result != null) {                          
                        receiptModel = new ArrayList<TableDataRow>();                              
                        k = 0;
                        
                        for (i = 0; i < result.size(); i++) {                            
                            manager = result.get(i);
                            count = manager.count();
                            
                            for (j = 0; j < count; j++ ) {                                
                                row = new TableDataRow(9);
                                data = manager.getReceiptAt(j);                                
                                orderId = data.getOrderItemOrderId();                                
                                                                
                                row.cells.get(0).setValue(orderId);
                                row.cells.get(1).setValue(data.getOrderItemOrderExternalOrderNumber());
                                row.cells.get(2).setValue(data.getReceivedDate());

                                invItem = getInventoryItem(data.getInventoryItemId());
                                if (invItem != null) {
                                    row.cells.get(3).setValue(new TableDataRow(invItem.getId(), data.getUpc()));
                                    row.cells.get(4).setValue(new TableDataRow(invItem.getId(), invItem.getName()));
                                } else {
                                    row.cells.get(3).setValue(new TableDataRow(-1, data.getUpc()));
                                }

                                organization = data.getOrganization();
                                if (organization != null)
                                    row.cells.get(5).setValue(new TableDataRow(organization.getId(),
                                                                        organization.getName()));

                                row.cells.get(6).setValue(data.getOrderItemQuantity());         
                                row.cells.get(7).setValue(data.getQuantityReceived());                                                                                      
                                row.cells.get(8).setValue(data.getUnitCost());

                                if(state == State.UPDATE)
                                    manager = manager.abortUpdate();
                                else if(state == State.DISPLAY)
                                    manager = manager.fetchForUpdate();
                                bundle = new InventoryReceiptDataBundle(j, data.getOrderItemOrderId(), manager);
                                row.data = bundle;                                                                 
                                receiptModel.add(row);
                                k++;
                            }
                        }                        
                    } else {
                        receiptModel = null;
                    }
                } catch (Exception ex) {
                    receiptModel = null;
                    ex.printStackTrace();
                    Window.alert(ex.getMessage());
                    window.clearStatus();
                }

                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                receiptTable.load(null);
                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    window.setError(consts.get("noMoreRecordInDir"));
                } else {
                    Window.alert("Error: Order call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }
    
    private void enableColumns(boolean enable) {
        dateRecColumn.enable(enable);
        upcColumn.enable(enable);
        numRecColumn.enable(enable);
        costColumn.enable(enable);
        itemColumn.enable(enable);
        orgColumn.enable(enable);
    } 
    
    private class InventoryReceiptDataBundle {
        
        private int                     managerIndex;
        private Integer                 orderId;
        private InventoryReceiptManager manager;   
        
        public InventoryReceiptDataBundle(int managerIndex, Integer orderId,
                                          InventoryReceiptManager manager) {
            this.managerIndex = managerIndex;
            this.orderId = orderId;
            this.manager = manager;            
        }
        
        protected void setManagerIndex(int managerIndex) {
            this.managerIndex = managerIndex;
        }

        public int getManagerIndex() {
            return managerIndex;
        }  
        
        public Integer getOrderId() {
            return orderId;
        }

        protected void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public InventoryReceiptManager getManager() {
            return manager;
        }

        protected void setManager(InventoryReceiptManager manager) {
            this.manager = manager;
        }
    }
}