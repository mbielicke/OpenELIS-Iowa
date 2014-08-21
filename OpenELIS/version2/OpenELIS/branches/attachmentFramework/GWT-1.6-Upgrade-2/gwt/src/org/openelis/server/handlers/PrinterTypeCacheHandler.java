package org.openelis.server.handlers;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.Message;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

public class PrinterTypeCacheHandler implements MessageHandler {

    public static int version = 0;
    
    public void handle(Message message) {
        CachingManager.remove("InitialData", "printertypeDropDown");
    }
    
    public static DataModel<Integer> getPrinterTypes() {
        DataModel model = (DataModel)CachingManager.getElement("InitialData", "printertypeDropDown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("printer_type");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new DataModel<Integer>();
        
            model.add(new DataSet<Integer>(0,new StringObject("")));
            for(IdNameDO resultDO :  entries){
                model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "printertypeDropDown", model);
            version++;
        }
        return model;
    }

}
