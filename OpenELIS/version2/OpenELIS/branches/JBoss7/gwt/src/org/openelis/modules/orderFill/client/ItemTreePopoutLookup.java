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

import java.util.HashMap;

import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.manager.OrderManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ItemTreePopoutLookup extends Screen implements 
                                                    HasActionHandlers<ItemTreePopoutLookup.Action> {

    private OrderManager                   manager;
    private ItemTab                        itemTab;
    private AppButton                      okButton;
    private HashMap<Integer, OrderManager> combinedMap;
    
    public enum Action {
        OK
    };
    
    public ItemTreePopoutLookup(ScreenWindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(ItemTreePopoutDef.class));
        
        /*
         * This window is needed here in order to make sure that when the tab
         * is initialized, it gets passed a non-null window, because at that time
         * this screen's window won't have been initialized. A post-contructor
         * can't be used here because it'll need to call both initialize() and setState()
         * that could override any value set through setScreenState().
         */
        this.window = window;
        initialize();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });
        
        itemTab = new ItemTab(def, window);
        addScreenHandler(itemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                itemTab.setManager(manager, combinedMap);
                itemTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTab.setState(event.getState());
            }
        });
    }
    
    public void ok() {
        setFocus(null);
        ActionEvent.fire(this, Action.OK, null);
        window.close();
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<ItemTreePopoutLookup.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    public void setManager(OrderManager manager, HashMap<Integer, OrderManager> combinedMap) {
        this.manager = manager;
        this.combinedMap = combinedMap;
        DataChangeEvent.fire(this);
    }
}