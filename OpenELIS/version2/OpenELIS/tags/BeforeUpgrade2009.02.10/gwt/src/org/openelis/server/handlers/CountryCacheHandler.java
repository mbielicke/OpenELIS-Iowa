package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.messages.CountryCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

import java.util.ArrayList;

public class CountryCacheHandler implements MessageHandler<CountryCacheMessage> {
    
    static CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    
    public static int version = 0;

    public void handle(CountryCacheMessage message) {
        CachingManager.remove("InitialData", "countryDropdown");
        
    }
    
    public static DataModel<DataSet> getCountries() {
        DataModel<DataSet> model = (DataModel<DataSet>)CachingManager.getElement("InitialData", "countryDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("country");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new DataModel<DataSet>();
        
            model.add(new DataSet(new StringObject(" "),new StringObject(" ")));
            for(IdNameDO resultDO :  entries){
                model.add(new DataSet(new StringObject(resultDO.getName()),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "countryDropdown", model);
            version++;
        }
        return model;
        
    }

}
