package org.openelis.modules.storage.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class StorageEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"ChildStorageLocsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new ChildStorageLocsTable();
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
