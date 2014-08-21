package org.openelis.modules.storageunit.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class StorageUnitEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"StorageUnitDescTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new StorageUnitDescTable();
                                  }
                               }
       );
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
