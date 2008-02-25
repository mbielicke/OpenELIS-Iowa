package org.openelis.modules.supply.client;

import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.supply.client.storage.ChildStorageLocsTable;
import org.openelis.modules.supply.client.storage.StorageLocationScreen;
import org.openelis.modules.supply.client.storage.StorageNameTable;
import org.openelis.modules.supply.client.storageUnit.StorageUnitDescTable;
import org.openelis.modules.supply.client.storageUnit.StorageUnitScreen;

public class Supply implements AppModule {

    public void onModuleLoad() {
        ScreenBase.getWidgetMap().addWidget("StorageUnitDescTable", new StorageUnitDescTable());
        ScreenBase.getWidgetMap().addWidget("ChildStorageLocsTable", new ChildStorageLocsTable());
        ScreenBase.getWidgetMap().addWidget("StorageNameTable", new StorageNameTable());
        ScreenBase.getWidgetMap().addWidget("StorageUnitDescTable", new StorageUnitDescTable());
        ScreenBase.getWidgetMap().addWidget("SupplyModule", this);
    }

    public String getModuleName() {
        return "Supply";
    }

    public void onClick(Widget sender) {
        String key = ((ScreenWidget)sender).key;
        if(key.equals("storageIcon") || key.equals("storageLabel") || key.equals("storageDescription") || 
        		key.equals("favLeftStorage"))
            OpenELIS.browser.addScreen(new StorageLocationScreen(), "Storage Location", "Storage", "Loading");
        if(key.equals("storageUnitIcon") || key.equals("storageUnitLabel") || key.equals("storageUnitDescription") || 
        		key.equals("favLeftStorageUnit"))
            OpenELIS.browser.addScreen(new StorageUnitScreen(), "Storage Unit", "Storage", "Loading");
    }

}
