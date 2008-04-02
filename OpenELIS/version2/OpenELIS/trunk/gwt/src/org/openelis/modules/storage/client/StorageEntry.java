package org.openelis.modules.storage.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class StorageEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"ChildStorageLocsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new ChildStorageLocsTable();
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
