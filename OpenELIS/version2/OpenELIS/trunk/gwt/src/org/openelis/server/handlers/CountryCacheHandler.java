package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
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
    
    public static ArrayList<IdNameDO> getCountries() {
        ArrayList<IdNameDO> model = (ArrayList<IdNameDO>)CachingManager.getElement("InitialData", "countryDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("country");
        
            model = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
           
            CachingManager.putElement("InitialData", "countryDropdown", model);
            version++;
        }
        return model;
        
    }

}
