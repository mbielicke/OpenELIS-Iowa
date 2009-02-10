package org.openelis.server.handlers;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.Message;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

public class QAEventTypeCacheHandler implements MessageHandler {

    public void handle(Message message) {
        CachingManager.remove("InitialData", "qaTypeDropDown");

    }
    
    public static DataModel getQAEventTypes() {
        DataModel model = (DataModel)CachingManager.getElement("InitialData", "qaTypeDropDown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("qaevent_type");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new DataModel();
        
            model.add(new DataSet(new NumberObject(0),new StringObject("")));
            for(IdNameDO resultDO :  entries){
                model.add(new DataSet(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "qaTypeDropDown", model);
        }
        return model;
        
    }

}
