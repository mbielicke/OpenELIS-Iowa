package org.openelis.modules.analyte.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class AnalyteEntry implements AppModule {
	
	public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"AnalyteScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new AnalyteScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Analyte";
    }
}
