package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class InventoryReceiptEntry implements AppModule {

    public void onModuleLoad() {
        OpenELIS.modules.addItem(getModuleName());
        
        ClassFactory.addClass(new String[] {"InventoryReceiptScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new InventoryReceiptScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "InventoryReceipt";
    }
}
