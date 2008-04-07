package org.openelis.modules.testTrailer.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class TestTrailerEntry implements AppModule {
	
	public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"TestTrailerScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new TestTrailerScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "TestTrailer";
    }
}
