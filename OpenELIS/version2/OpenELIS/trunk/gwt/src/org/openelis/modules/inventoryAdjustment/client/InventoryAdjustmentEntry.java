package org.openelis.modules.inventoryAdjustment.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class InventoryAdjustmentEntry implements AppModule {

    public void onModuleLoad() {
        OpenELIS.modules.addItem(new StringObject(getModuleName()));
        
        ClassFactory.addClass(new String[] {"InventoryAdjustmentScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new InventoryAdjustmentScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "InventoryAdjustment";
    }
}
