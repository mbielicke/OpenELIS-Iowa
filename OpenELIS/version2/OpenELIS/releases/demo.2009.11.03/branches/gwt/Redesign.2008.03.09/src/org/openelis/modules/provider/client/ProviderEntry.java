package org.openelis.modules.provider.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class ProviderEntry implements AppModule {

    public void onModuleLoad() {
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
