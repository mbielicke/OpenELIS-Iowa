package org.openelis.modules.test.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class TestEntry implements AppModule {

    public String getModuleName() {        
        return "Test";
    }

    public void onModuleLoad() {
        OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"TestScreen"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new TestScreen();
                                  }
           }
       );

    }

}
