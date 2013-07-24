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
package org.openelis.web.modules.notificationPreference.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.constants.Messages;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AddEditEmailScreen extends Screen implements
                                                 HasActionHandlers<AddEditEmailScreen.Action> {

    private ArrayList<OrganizationViewDO>  organizationList;
    private AddEditEmailVO                 data;
    private Dropdown<Integer>              organization;
    private TextBox                        email;
    private CheckBox                       forReleased, forReceived;
    private AppButton                      okButton, cancelButton;
 
    public enum Action {
        OK, CANCEL
    };
    
    public AddEditEmailScreen(ArrayList<OrganizationViewDO> organizationList) throws Exception {
        super((ScreenDefInt)GWT.create(AddEditEmailDef.class));
        
        this.organizationList = organizationList;
        // Setup link between Screen and widget Handlers
        initialize();
        initializeDropdowns(); 
                  
        setState(State.DEFAULT); 
    }

    private void initialize() {        
        organization = (Dropdown)def.getWidget("organization");
        addScreenHandler(organization, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                organization.setValue(data.getOrganizationId());
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setOrganizationId(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                organization.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        forReceived = (CheckBox)def.getWidget("forReceived");
        addScreenHandler(forReceived, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                forReceived.setValue(data.getForReceived());
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setForReceived(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                forReceived.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        forReleased = (CheckBox)def.getWidget("forReleased");
        addScreenHandler(forReleased, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                forReleased.setValue(data.getForReleased());
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setForReleased(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                forReleased.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        email = (TextBox<String>)def.getWidget("email");
        addScreenHandler(email, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                email.setValue(data.getEmail());
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                String val;
                
                val = event.getValue();
                data.setEmail(val);
                if (!DataBaseUtil.isEmpty(val))
                    email.clearExceptions();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                email.enable(true);
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
        ArrayList<TableDataRow> model;        

        model = new ArrayList<TableDataRow>();
        for (OrganizationViewDO org : organizationList)            
            model.add(new TableDataRow(org.getId(), org.getName()));        

        organization.setModel(model);
    }
    
    public void clearFields(AddEditEmailVO data) {       
        this.data = data;
        DataChangeEvent.fire(this);
        setFocus(email);
    }
    
    public void ok() {     
        if (DataBaseUtil.isEmpty(data.getEmail())) { 
           email.addException(new Exception(Messages.get().enterEmailOrCancel()));
           return;
        }
        if (!validate())
            return;
        setFocus(null);
        ActionEvent.fire(this, Action.OK, data);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}