package org.openelis.modules.systemvariable.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class SystemVariableEntry implements AppModule {

    public String getModuleName() {
        return "SystemVariable";
    }

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"SystemVariableScreen"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new SystemVariableScreen();
                                  }
           }
       );

    }

}
