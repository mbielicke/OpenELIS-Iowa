package org.openelis.modules.qaevent.client;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class QAEventEntry implements AppModule {

    
    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
        ClassFactory.addClass(new String[] {"QAEventScreen"},
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new org.openelis.modules.qaevent.client.QAEventScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "QAEvent";
    }

}
