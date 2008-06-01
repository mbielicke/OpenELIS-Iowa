package org.openelis.modules.order.client;

import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class OrderEntry implements AppModule {

    public void onModuleLoad() {
        OpenELIS.modules.addItem(new StringObject(getModuleName()));
    
        ClassFactory.addClass(new String[] {"OrderScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new OrderScreen((DataObject[])args);
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Order";
    }

}
