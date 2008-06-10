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
