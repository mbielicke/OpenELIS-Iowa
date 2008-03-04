package org.openelis.modules.dataEntry.client;

import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.dataEntry.client.Provider.ProviderScreen;
import org.openelis.modules.dataEntry.client.Provider.ProviderAddressesTable;
import org.openelis.modules.dataEntry.client.Provider.ProviderNamesTable;
import org.openelis.modules.dataEntry.client.organization.OrganizationContactsTable;
import org.openelis.modules.dataEntry.client.organization.OrganizationNameTable;
import org.openelis.modules.dataEntry.client.organization.OrganizationScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class DataEntry implements AppModule {

    public void onModuleLoad() {
        ScreenBase.getWidgetMap().addWidget("OrganizationNameTable", new OrganizationNameTable());
        ScreenBase.getWidgetMap().addWidget("OrganizationContactsTable", new OrganizationContactsTable());
        ScreenBase.getWidgetMap().addWidget("ProviderNamesTable", new ProviderNamesTable());
        ScreenBase.getWidgetMap().addWidget("ProviderAddressesTable", new ProviderAddressesTable());
        ScreenBase.getWidgetMap().addWidget("DataEntryModule", this);
    }

    public String getModuleName() {
        return "DataEntry";
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
	     if(key.equals("organizationRow") || key.equals("favLeftOrganizationRow"))  
	            OpenELIS.browser.addScreen(new OrganizationScreen(), "Organization", "Organization", "Loading");
	     
	     if(key.equals("providerRow") || key.equals("favLeftProviderRow"))  
	    	 	OpenELIS.browser.addScreen(new ProviderScreen(), "Provider", "Provider", "Loading");		
	}

}
