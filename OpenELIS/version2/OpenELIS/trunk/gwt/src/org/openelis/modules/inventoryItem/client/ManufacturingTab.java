/*
 * Created on Nov 30, 2009
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.InventoryItemManager;
import org.openelis.modules.note.client.RichTextTab;

import com.google.gwt.user.client.Window;

public class ManufacturingTab extends RichTextTab {

    public ManufacturingTab(ScreenDefInt def, ScreenWindow window, String richtextPanelKey,
                            String editButtonKey) {
        super(def, window, richtextPanelKey, editButtonKey);
    }

    public void draw() {
        if (parentManager != null && !loaded) {
            try {
                manager = ((InventoryItemManager)parentManager).getManufacturing();
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
