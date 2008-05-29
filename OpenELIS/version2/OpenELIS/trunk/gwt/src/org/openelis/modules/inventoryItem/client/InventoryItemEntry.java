package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.organization.client.OrganizationContactsTable;

public class InventoryItemEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());

        ClassFactory.addClass(new String[] {"InventoryComponentsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryComponentsTable();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryLocationsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryLocationsTable();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryComponentAutoParams"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new InventoryComponentAutoParams();
                                  }
                               }
       );
        
        ClassFactory.addClass(new String[] {"InventoryItemScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new InventoryItemScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Inventory";
    }
}
