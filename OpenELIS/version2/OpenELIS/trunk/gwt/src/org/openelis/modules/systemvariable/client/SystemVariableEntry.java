package org.openelis.modules.systemvariable.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class SystemVariableEntry implements AppModule {

    public String getModuleName() {
        return "SystemVariable";
    }

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"SystemVariableScreen"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new SystemVariableScreen();
                                  }
           }
       );

    }

}
