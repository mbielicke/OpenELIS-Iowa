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
package org.openelis.modules.inventoryItem.client;

import org.openelis.common.NotesTab;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TabPanel;

public class InventoryItemScreen extends Screen {

    private InventoryItemMetaMap  meta = new InventoryItemMetaMap();
//    private InventoryItemManager  manager;
    private SecurityModule        security;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private NotesTab              notesTab;
    private Tabs                  tab;

    private TabPanel              tabPanel;

    private enum Tabs {
        CONTACTS, PARAMETERS, NOTES
    };

    public InventoryItemScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryItemDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

//        security = OpenELIS.security.getModule("inventoryitem");
//        if (security == null)
//            throw new SecurityException("screenPermException", "Inventory Item Screen");

        // Setup link between Screen and widget Handlers
        initialize();

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
        tab = Tabs.CONTACTS;
//        contactTab.setWindow(window);
        
//        manager = InventoryItemManager.getInstance();

//        setState(State.DEFAULT);
//        initializeDropdowns();

        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
    }
}
