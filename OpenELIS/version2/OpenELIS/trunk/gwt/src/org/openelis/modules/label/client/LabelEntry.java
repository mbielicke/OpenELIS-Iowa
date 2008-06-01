package org.openelis.modules.label.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;


public class LabelEntry implements AppModule {

    public String getModuleName() {        
        return "Label";
    }

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"LabelScreen"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new LabelScreen();
                                  }
                              }
        );    

    }
        

}
