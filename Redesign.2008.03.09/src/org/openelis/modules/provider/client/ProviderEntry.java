package org.openelis.modules.provider.client;

import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.organization.client.OrganizationContactsTable;
import org.openelis.modules.organization.client.OrganizationNameTable;
import org.openelis.modules.organization.client.OrganizationScreen;
import org.openelis.modules.provider.client.ProviderAddressesTable;
import org.openelis.modules.provider.client.ProviderNamesTable;
import org.openelis.modules.provider.client.ProviderScreen;

public class ProviderEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"OrganizationNameTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new OrganizationNameTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"OrganizationContactsTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new OrganizationContactsTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"ProviderNamesTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new ProviderNamesTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"ProviderAddressesTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new ProviderAddressesTable();
                                  }
                               }
       );
        ClassFactory.addClass(new String[] {"OrganizationScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new OrganizationScreen();
                                   }
                                }
        );
        ClassFactory.addClass(new String[] {"ProviderScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new ProviderScreen();
                                   }
            }
        );
    }

    public String getModuleName() {
        return "DataEntry";
    }

    public void onClick(Widget sender) {
    	 String key = ((ScreenWidget)sender).key;
	     if(key.equals("organizationRow") || key.equals("favLeftOrganizationRow"))  
	            OpenELIS.browser.addScreen(new OrganizationScreen(), "Organization", "Organization", "Loading");
	     
	     if(key.equals("providerRow") || key.equals("favLeftProviderRow"))  
	    	 	OpenELIS.browser.addScreen(new ProviderScreen(), "Provider", "Provider", "Loading");		
    }
}
