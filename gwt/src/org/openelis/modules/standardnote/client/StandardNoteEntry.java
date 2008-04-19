package org.openelis.modules.standardnote.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class StandardNoteEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());
        ClassFactory.addClass(new String[] {"StandardNoteScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new StandardNoteScreen();
                                   }
                               }
        );
    }

    public String getModuleName() {
        return "StandardNote";
    }

}
