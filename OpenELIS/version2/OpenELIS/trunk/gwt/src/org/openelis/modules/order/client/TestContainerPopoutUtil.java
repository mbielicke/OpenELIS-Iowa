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

import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.OrderManager;

/** 
 * This class is used to maintain a single instance of TestContainerPopoutLookup
 * for all the tabs on Send-out Order screen. It also allows the tabs to add 
 * handlers to the pop-out to respond to any events fired by it e.g. when it's closed. 
 * Because of this class the order screen doesn't need to maintain the instance 
 * and can stay independent of that functionality. 
 */
public class TestContainerPopoutUtil {
    private TestContainerPopoutLookup popoutLookup;
    private ArrayList<ActionHandler<TestContainerPopoutLookup.Action>> handlers;

    public void addActionHandler(ActionHandler<TestContainerPopoutLookup.Action> handler) {
        if (handlers == null)
            handlers = new ArrayList<ActionHandler<TestContainerPopoutLookup.Action>>();
        handlers.add(handler);
    }
    
    public void showPopout(String name, State state, OrderManager manager) throws Exception {
        ScreenWindow modal;
        
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        if (popoutLookup == null) {
            popoutLookup = new TestContainerPopoutLookup(modal);
            
            if (handlers != null) {
                for (ActionHandler<TestContainerPopoutLookup.Action> handler : handlers)
                    popoutLookup.addActionHandler(handler);
            }
        }
        modal.setName(name);
        modal.setContent(popoutLookup);
        popoutLookup.setScreenState(state);
        popoutLookup.setManager(manager);
    }
}