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

import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class ReportToBillToTab extends Screen {
    
    private OrderManager          manager;    
    private TextBox               reportToAddressMultipleUnit, reportToAddressStreetAddress,
                                  reportToAddressCity, reportToAddressState, reportToAddressZipCode,
                                  billToAddressMultipleUnit, billToAddressStreetAddress,
                                  billToAddressCity, billToAddressState, billToAddressZipCode;
    private AutoComplete<Integer> reportToName, billToName;    
    private boolean               loaded;
            
    protected ScreenService       organizationService;
    
    public ReportToBillToTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        reportToName = (AutoComplete)def.getWidget(OrderMeta.getReportToName());
        addScreenHandler(reportToName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null)
                    reportToName.setSelection(data.getReportToId(), data.getReportTo().getName());
                else 
                    reportToName.setSelection(null,"");
                    
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;                             
                if(reportToName.getSelection() != null) {
                    data = (OrganizationDO) reportToName.getSelection().data;
                    
                    manager.getOrder().setReportToId(data.getId());
                    manager.getOrder().setReportTo(data);
                    
                    reportToAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    reportToAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    reportToAddressCity.setValue(data.getAddress().getCity());
                    reportToAddressState.setValue(data.getAddress().getState());
                    reportToAddressZipCode.setValue(data.getAddress().getZipCode());    
                } else {
                    manager.getOrder().setReportToId(null);
                    manager.getOrder().setReportTo(null);
                    
                    reportToAddressMultipleUnit.setValue(null);
                    reportToAddressStreetAddress.setValue(null);
                    reportToAddressCity.setValue(null);
                    reportToAddressState.setValue(null);
                    reportToAddressZipCode.setValue(null);    
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                reportToName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        reportToName.addGetMatchesHandler(new GetMatchesHandler() {
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
                    reportToName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }
            
        });

        reportToAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getReportToAddressMultipleUnit());
        addScreenHandler(reportToAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null)
                    reportToAddressMultipleUnit.setValue(data.getReportTo().getAddress().getMultipleUnit());
                else 
                    reportToAddressMultipleUnit.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAddressMultipleUnit.enable(false);
                reportToAddressMultipleUnit.setQueryMode(false);
            }
        });

        reportToAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getReportToAddressStreetAddress());
        addScreenHandler(reportToAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null) 
                    reportToAddressStreetAddress.setValue(data.getReportTo().getAddress().getStreetAddress());
                else
                    reportToAddressStreetAddress.setValue(null);
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAddressStreetAddress.enable(false);
                reportToAddressStreetAddress.setQueryMode(false);
            }
        });

        reportToAddressCity = (TextBox)def.getWidget(OrderMeta.getReportToAddressCity());
        addScreenHandler(reportToAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null) 
                    reportToAddressCity.setValue(data.getReportTo().getAddress().getCity());
                else
                    reportToAddressCity.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAddressCity.enable(false);
                reportToAddressCity.setQueryMode(false);
            }
        });

        reportToAddressState = (TextBox)def.getWidget(OrderMeta.getReportToAddressState());
        addScreenHandler(reportToAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null)
                    reportToAddressState.setValue(data.getReportTo().getAddress().getState());
                else
                    reportToAddressState.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAddressState.enable(false);
                reportToAddressState.setQueryMode(false);
            }
        });

        reportToAddressZipCode = (TextBox)def.getWidget(OrderMeta.getReportToAddressZipCode());
        addScreenHandler(reportToAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getReportTo() != null)
                    reportToAddressZipCode.setValue(data.getReportTo().getAddress().getZipCode());
                else
                    reportToAddressZipCode.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAddressZipCode.enable(false);
                reportToAddressZipCode.setQueryMode(false);
            }
        });

        billToName = (AutoComplete)def.getWidget(OrderMeta.getBillToName());
        addScreenHandler(billToName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToName.setSelection(data.getBillToId(), data.getBillTo().getName());
                else
                    billToName.setSelection(null,"");
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {                                
                OrganizationDO data;                             
                if(billToName.getSelection() != null) {
                    data = (OrganizationDO) billToName.getSelection().data;                    
                    manager.getOrder().setBillToId(data.getId());
                    manager.getOrder().setBillTo(data);
                    
                    billToAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    billToAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    billToAddressCity.setValue(data.getAddress().getCity());
                    billToAddressState.setValue(data.getAddress().getState());
                    billToAddressZipCode.setValue(data.getAddress().getZipCode());   
                } else {
                    manager.getOrder().setBillToId(null);
                    manager.getOrder().setBillTo(null);
                    
                    billToAddressMultipleUnit.setValue(null);
                    billToAddressStreetAddress.setValue(null);
                    billToAddressCity.setValue(null);
                    billToAddressState.setValue(null);
                    billToAddressZipCode.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                billToName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        billToName.addGetMatchesHandler(new GetMatchesHandler() {
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
                    billToName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }
            
        });

        billToAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getBillToAddressMultipleUnit());
        addScreenHandler(billToAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToAddressMultipleUnit.setValue(data.getBillTo().getAddress().getMultipleUnit());
                else 
                    billToAddressMultipleUnit.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToAddressMultipleUnit.enable(false);
                billToAddressMultipleUnit.setQueryMode(false);
            }
        });

        billToAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getBillToAddressStreetAddress());
        addScreenHandler(billToAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToAddressStreetAddress.setValue(data.getBillTo().getAddress().getStreetAddress());
                else
                    billToAddressStreetAddress.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToAddressStreetAddress.enable(false);
                billToAddressStreetAddress.setQueryMode(false);
            }
        });

        billToAddressCity = (TextBox)def.getWidget(OrderMeta.getBillToAddressCity());
        addScreenHandler(billToAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToAddressCity.setValue(data.getBillTo().getAddress().getCity());
                else
                    billToAddressCity.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToAddressCity.enable(false);
                billToAddressCity.setQueryMode(false);
            }
        });

        billToAddressState = (TextBox)def.getWidget(OrderMeta.getBillToAddressState());
        addScreenHandler(billToAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToAddressState.setValue(data.getBillTo().getAddress().getState());
                else 
                    billToAddressState.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToAddressState.enable(false);
                billToAddressState.setQueryMode(false);
            }
        });

        billToAddressZipCode = (TextBox)def.getWidget(OrderMeta.getBillToAddressZipCode());
        addScreenHandler(reportToAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();                
                if(data.getBillTo() != null)
                    billToAddressZipCode.setValue(data.getBillTo().getAddress().getZipCode());
                else
                    billToAddressZipCode.setValue(null);
                    
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToAddressZipCode.enable(false);
                billToAddressZipCode.setQueryMode(false);
            }
        });       
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
