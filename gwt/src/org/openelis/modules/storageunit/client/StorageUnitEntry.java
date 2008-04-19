package org.openelis.modules.storageunit.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class StorageUnitEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());
        ClassFactory.addClass(new String[] {"StorageUnitScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                         return new StorageUnitScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "StorageUnit";
    }
}
