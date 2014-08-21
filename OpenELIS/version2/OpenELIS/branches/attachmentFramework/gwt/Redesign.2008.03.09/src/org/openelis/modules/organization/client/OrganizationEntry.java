package org.openelis.modules.organization.client;

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

public class OrganizationEntry implements AppModule {

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
        ClassFactory.addClass(new String[] {"OrganizationScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new OrganizationScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Organization";
    }

}
