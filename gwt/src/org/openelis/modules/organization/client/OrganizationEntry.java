package org.openelis.modules.organization.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class OrganizationEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());
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
