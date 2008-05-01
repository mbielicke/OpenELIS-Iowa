package org.openelis.modules.inventory.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class InventoryEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());

        ClassFactory.addClass(new String[] {"InventoryScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new InventoryScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Inventory";
    }
}
