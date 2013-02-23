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

import java.util.EnumSet;

import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.meta.InventoryReceiptMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class VendorAddressTab extends Screen {
    
    private InventoryReceiptManager manager;
    private TextBox                 organizationAddressMultipleUnit, organizationAddressStreetAddress,
                                    organizationAddressCity, organizationAddressState,
                                    organizationAddressZipCode;
    private int                     index;
    private boolean                 loaded;  

    public VendorAddressTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        organizationAddressMultipleUnit = (TextBox)def.getWidget(InventoryReceiptMeta.getOrganizationAddressMultipleUnit());
        addScreenHandler(organizationAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                data = null;
                if(manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index).getOrganization();  
                    if(data != null)
                        organizationAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    else
                        organizationAddressMultipleUnit.setValue(null);
                } else {
                    organizationAddressMultipleUnit.setValue(null);
                }
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressMultipleUnit.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                organizationAddressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressStreetAddress = (TextBox)def.getWidget(InventoryReceiptMeta.getOrganizationAddressStreetAddress());
        addScreenHandler(organizationAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                
                OrganizationDO data;
                data = null;
                if(manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index).getOrganization();  
                    if(data != null)
                        organizationAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    else
                        organizationAddressStreetAddress.setValue(null);
                } else {
                    organizationAddressStreetAddress.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressStreetAddress.enable(false);
            }
        });

        organizationAddressCity = (TextBox)def.getWidget(InventoryReceiptMeta.getOrganizationAddressCity());
        addScreenHandler(organizationAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                data = null;
                if(manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index).getOrganization();  
                    if(data != null)
                        organizationAddressCity.setValue(data.getAddress().getCity());
                    else
                        organizationAddressCity.setValue(null);
                } else {
                    organizationAddressCity.setValue(null);
                }                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
               // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressCity.enable(false);
            }
        });

        organizationAddressState = (TextBox)def.getWidget(InventoryReceiptMeta.getOrganizationAddressState());
        addScreenHandler(organizationAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationDO data;
                data = null;                
                if(manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index).getOrganization();  
                    if(data != null)
                        organizationAddressState.setValue(data.getAddress().getState());
                    else
                        organizationAddressState.setValue(null);
                } else {
                    organizationAddressState.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressState.enable(false);
            }
        });

        organizationAddressZipCode = (TextBox)def.getWidget(InventoryReceiptMeta.getOrganizationAddressZipCode());
        addScreenHandler(organizationAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                
                OrganizationDO data;
                data = null;
                if(manager != null && index != -1) {                    
                    data = manager.getReceiptAt(index).getOrganization();  
                    if(data != null)
                        organizationAddressZipCode.setValue(data.getAddress().getZipCode());
                    else
                        organizationAddressZipCode.setValue(null);
                } else {
                    organizationAddressZipCode.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field is never edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressZipCode.enable(false);
            }
        });
        
        index = -1;
    }
    
    public void setManager(InventoryReceiptManager manager, int index) {
        this.manager = manager;
        this.index = index; 
        loaded = false;        
    }
                           
    public void draw() {
        if ( !loaded) 
            DataChangeEvent.fire(this);        

        loaded = true;        
    }

}
