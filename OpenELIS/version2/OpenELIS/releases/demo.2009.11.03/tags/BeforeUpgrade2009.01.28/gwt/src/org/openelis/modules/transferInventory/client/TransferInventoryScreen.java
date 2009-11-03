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
package org.openelis.modules.transferInventory.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class TransferInventoryScreen extends OpenELISScreenForm implements ClickListener, AutoCompleteCallInt {

    private KeyListManager keyList = new KeyListManager();
    
    public TransferInventoryScreen() {
        super("org.openelis.modules.transferInventory.server.TransferInventoryService", false);
    }
    
    public void onClick(Widget sender) {
   
    }
   
    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
   
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        super.afterDraw(success);
    }

    public void callForMatches(AutoComplete widget, DataModel model, String text) {
        // TODO Auto-generated method stub
        
    }
}
