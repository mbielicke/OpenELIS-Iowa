/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */

package org.openelis.modules.order.client;

import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.OrderManager;
import org.openelis.modules.note.client.NotesTab;

import com.google.gwt.user.client.Window;

public class CustomerNoteTab extends NotesTab {

    protected OrderManager parentManager;
    
    public CustomerNoteTab(ScreenDefInt def, ScreenWindow window, String notesPanelKey,
                        String editButtonKey) {
        super(def, window, notesPanelKey, editButtonKey);
    }

    public void setManager(OrderManager parentManager) {
        this.parentManager = parentManager;
        loaded = false;
    }

    public void draw() {
        if (parentManager != null && !loaded) {
            try {
                manager = parentManager.getCustomerNotes();
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
