package org.openelis.modules.dataEntry.client;

import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.dataEntry.client.Provider.Provider;
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
        String key = ((ScreenWidget)sender).key;
        if(key.equals("organizationIcon") || key.equals("organizationLabel") || key.equals("organizationDescription"))  
            OpenELIS.browser.addScreen(new OrganizationScreen(), "Organization", "Organization", "Loading");
        if(key.equals("providerIcon") || key.equals("providerLabel") || key.equals("providerDescription"))  
            OpenELIS.browser.addScreen(new Provider(), "Provider", "Provider", "Loading");
        
    }

}
