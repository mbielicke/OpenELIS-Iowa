package org.openelis.modules.label.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;


public class LabelEntry implements AppModule {

    public String getModuleName() {        
        return "Label";
    }

    public void onModuleLoad() {
       ClassFactory.addClass(new String[] {"LabelScreen"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new LabelScreen();
                                  }
                              }
       );    

    }
        

}
