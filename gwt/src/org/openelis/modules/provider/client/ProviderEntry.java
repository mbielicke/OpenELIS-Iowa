package org.openelis.modules.provider.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class ProviderEntry implements AppModule {

    public void onModuleLoad() {   
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"ProviderAddressesTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new ProviderAddressesTable();
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
        return "Provider";
    }

}
