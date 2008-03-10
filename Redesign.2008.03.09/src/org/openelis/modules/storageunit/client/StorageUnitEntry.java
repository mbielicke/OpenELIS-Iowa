package org.openelis.modules.storageunit.client;

import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.storage.client.ChildStorageLocsTable;
import org.openelis.modules.storage.client.StorageLocationScreen;
import org.openelis.modules.storage.client.StorageNameTable;
import org.openelis.modules.storageunit.client.StorageUnitDescTable;
import org.openelis.modules.storageunit.client.StorageUnitScreen;

public class StorageUnitEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"StorageUnitDescTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                        return new StorageUnitDescTable();
                                  }
                               }
       );
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
        ClassFactory.addClass(new String[] {"StorageUnitScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                         return new StorageUnitScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Supply";
    }

    public void onClick(Widget sender) {
    	String key = ((ScreenWidget)sender).key;
        if(key.equals("storageLocationRow") || key.equals("favLeftStorageLocationRow"))
            OpenELIS.browser.addScreen(new StorageLocationScreen(), "Storage Location", "Storage", "Loading");
        
        if(key.equals("storageUnitRow") || key.equals("favLeftStorageUnitRow"))
            OpenELIS.browser.addScreen(new StorageUnitScreen(), "Storage Unit", "Storage", "Loading");		
    }
}
