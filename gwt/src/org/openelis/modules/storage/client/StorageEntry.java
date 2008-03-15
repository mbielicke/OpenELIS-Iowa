package org.openelis.modules.storage.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.storageunit.client.StorageUnitDescTable;

public class StorageEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"ChildStorageLocsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new StorageUnitDescTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"StorageNameTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new StorageNameTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"StorageLocationScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new StorageLocationScreen();
                                   }
                               }
        );
    }

    public String getModuleName() {
        return "Storage";
    }

}
