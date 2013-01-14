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
package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.SampleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ChangeDomainScreen extends Screen implements
                                                 HasActionHandlers<ChangeDomainScreen.Action> {

    private Dropdown<String> domain;
    private AppButton        okButton, cancelButton;
    private String           domainKey;
 
    public enum Action {
        OK, CANCEL
    };
    
    public ChangeDomainScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ChangeDomainDef.class));
        
        // Setup link between Screen and widget Handlers
        initialize();
        initializeDropdowns(); 
                  
        setState(State.DEFAULT); 
    }

    private void initialize() {        
        domain = (Dropdown<String>)def.getWidget("domain");
        addScreenHandler(domain, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                domain.setValue(domainKey);
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                domain.enable(true);
            }
        });       
        
        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });        
    }
    
    private void initializeDropdowns() {
        String sname, dom;
        ArrayList<TableDataRow> model;  
        List<DictionaryDO> list;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO resultDO : list) {
            sname = resultDO.getSystemName();
            dom = null;
            if ("environmental".equals(sname)) {
                dom = SampleManager.ENVIRONMENTAL_DOMAIN_FLAG;
            } else if ("private_well".equals(sname)) {
                dom = SampleManager.WELL_DOMAIN_FLAG;
            } else if ("sdwis".equals(sname)) {
                dom = SampleManager.SDWIS_DOMAIN_FLAG;
            }
            
            if (dom != null) {
                row = new TableDataRow(dom, resultDO.getEntry());
                row.enabled = ("Y".equals(resultDO.getIsActive()));
                model.add(row);
            }
        }
        
        domain.setModel(model);                
    }
    
    
    public void ok() {     
        TableDataRow row;
        
        row = domain.getSelection();
        if (row != null)
            ActionEvent.fire(this, Action.OK, row.key);
        else 
            ActionEvent.fire(this, Action.OK, null);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void setDomain(String domain) {
       if (domain != null)
           domainKey = domain;
       DataChangeEvent.fire(this);
    }
}