/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.modules.inventoryAdjustment.client;

import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class InventoryAdjustmentScreen extends OpenELISScreenForm implements ClickListener {

        
    private static boolean loaded = false;
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService",false);
    }

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }
    
    public void afterDraw(boolean sucess) {
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(sucess);
    }

}
