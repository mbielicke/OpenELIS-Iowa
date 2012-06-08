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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class OrganizationTab extends Screen {
    
    private OrderManager          manager;    
    private TableWidget           table;
    private AutoComplete<Integer> organizationName;
    private AppButton             removeOrganizationButton, addOrganizationButton;
    
    private boolean               loaded;

    protected ScreenService       organizationService;
    
    public OrganizationTab(ScreenDefInt def, ScreenWindowInt window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
        setDefinition(def);
        setWindow(window);
        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("organizationTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });      
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if((state != State.ADD && state != State.UPDATE)) 
                        event.cancel();
            }            
        });

        organizationName = ((AutoComplete<Integer>)table.getColumns().get(2).colWidget);
        organizationName.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = organizationService.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (OrganizationDO data : list) {
                        row = new TableDataRow(4);
                        
                        row.key = data.getId();
                        row.data = data;                        
                        row.cells.get(0).setValue(data.getName());
                        row.cells.get(1).setValue(data.getAddress().getStreetAddress());
                        row.cells.get(2).setValue(data.getAddress().getCity());
                        row.cells.get(3).setValue(data.getAddress().getState());  
                        
                        model.add(row);
                    }
                    organizationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() > 2)
                    event.cancel();
            }
        });
        
        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r,c;
                Object val;
                OrganizationDO org;
                AddressDO addr;
                OrderOrganizationViewDO data;
                TableDataRow row;
                
                r = event.getRow();
                c = event.getCol();
                
                try {
                    data = manager.getOrganizations().getOrganizationAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                    
                val = table.getObject(r,c);
                
                switch (c) {
                    case 0:                        
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        row = (TableDataRow)val;
                        if (row != null) {
                            org = (OrganizationDO)row.data;
                            data.setOrganizationId(org.getId());
                            data.setOrganizationName(org.getName());
                            
                            addr = org.getAddress();
                            data.setOrganizationAddressMultipleUnit(addr.getMultipleUnit());
                            data.setOrganizationAddressStreetAddress(addr.getStreetAddress());
                            data.setOrganizationAddressCity(addr.getCity());
                            data.setOrganizationAddressState(addr.getState());
                            data.setOrganizationAddressZipCode(addr.getZipCode());
                            data.setOrganizationAddressCountry(addr.getCountry());
                            
                            table.setCell(r, 3, addr.getMultipleUnit());
                            table.setCell(r, 4, addr.getStreetAddress());
                            table.setCell(r, 5, addr.getCity());
                            table.setCell(r, 6, addr.getState());
                            table.setCell(r, 7, addr.getZipCode());
                            table.setCell(r, 8, addr.getCountry());
                        } else {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationAddressMultipleUnit(null);
                            data.setOrganizationAddressStreetAddress(null);
                            data.setOrganizationAddressCity(null);
                            data.setOrganizationAddressState(null);
                            data.setOrganizationAddressZipCode(null);
                            data.setOrganizationAddressCountry(null);
                            
                            table.setCell(r, 3, null);
                            table.setCell(r, 4, null);
                            table.setCell(r, 5, null);
                            table.setCell(r, 6, null);
                            table.setCell(r, 7, null);
                            table.setCell(r, 8, null);
                        }
                        break;
                }
            }
        });
        
        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getOrganizations().addOrganization(new OrderOrganizationViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getOrganizations().removeOrganizationAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        addOrganizationButton  = (AppButton)def.getWidget("organizationAddButton");
        addScreenHandler(addOrganizationButton,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                                
                table.addRow();                
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addOrganizationButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });    
        
        removeOrganizationButton = (AppButton)def.getWidget("organizationRemoveButton");
        addScreenHandler(removeOrganizationButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0) 
                    table.deleteRow(r);                
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                removeOrganizationButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }            
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> list;
        ArrayList<TableDataRow> model;

        try {
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            list = CategoryCache.getBySystemName("organization_type");
            for (DictionaryDO data : list) {
                if ("Y".equals(data.getIsActive()))
                    model.add(new TableDataRow(data.getId(), data.getEntry()));
            }
            ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget()).setModel(model);
            
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            list = CategoryCache.getBySystemName("state");
            for (DictionaryDO data : list) {
                if ("Y".equals(data.getIsActive()))
                    model.add(new TableDataRow(data.getEntry(), data.getEntry()));
            }
            ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);
            
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            list = CategoryCache.getBySystemName("country");
            for (DictionaryDO data : list) {
                if ("Y".equals(data.getIsActive()))
                    model.add(new TableDataRow(data.getEntry(), data.getEntry()));
            }
            ((Dropdown<Integer>)table.getColumns().get(8).getColumnWidget()).setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        OrderOrganizationViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {            
            for (int i = 0; i < manager.getOrganizations().count(); i++ ) {
                data = manager.getOrganizations().getOrganizationAt(i);
                row = new TableDataRow(9);

                row.cells.get(0).setValue(data.getTypeId());
                row.cells.get(1).setValue(data.getOrganizationAttention());
                row.cells.get(2).setValue(new TableDataRow(data.getOrganizationId(), data.getOrganizationName()));
                row.cells.get(3).setValue(data.getOrganizationAddressMultipleUnit());
                row.cells.get(4).setValue(data.getOrganizationAddressStreetAddress());
                row.cells.get(5).setValue(data.getOrganizationAddressCity());
                row.cells.get(6).setValue(data.getOrganizationAddressState());
                row.cells.get(7).setValue(data.getOrganizationAddressZipCode());
                row.cells.get(8).setValue(data.getOrganizationAddressCountry());
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    public void setManager(OrderManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}