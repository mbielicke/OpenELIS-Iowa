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
        
    }

	public void onMouseDown(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseEnter(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseLeave(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseMove(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseUp(Widget sender, int x, int y) {
		String key = ((ScreenWidget)sender).key;
        if(key.equals("storageLocationRow") || key.equals("favLeftStorageLocationRow"))
            OpenELIS.browser.addScreen(new StorageLocationScreen(), "Storage Location", "Storage", "Loading");
        
        if(key.equals("storageUnitRow") || key.equals("favLeftStorageUnitRow"))
            OpenELIS.browser.addScreen(new StorageUnitScreen(), "Storage Unit", "Storage", "Loading");		
	}

}
