package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.messages.StateCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

import java.util.ArrayList;

public class StatesCacheHandler implements MessageHandler<StateCacheMessage> {
    
    static CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

    public void handle(StateCacheMessage message) {
        CachingManager.remove("InitialData", "stateDropdown");
        
    }
    
    public static DataModel getStates() {
        DataModel model = (DataModel)CachingManager.getElement("InitialData", "stateDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("state");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new DataModel();
        
            model.add(new DataSet(new StringObject(" "),new StringObject(" ")));
            for(IdNameDO resultDO :  entries){
                model.add(new DataSet(new StringObject(resultDO.getName()),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "stateDropdown", model);
        }
        return model;
        
    }

}
