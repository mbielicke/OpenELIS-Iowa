package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
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
    
    public static TableDataModel<TableDataRow<String>> getCountries() {
        TableDataModel<TableDataRow<String>> model = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "countryDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("country");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new TableDataModel<TableDataRow<String>>();
        
            model.add(new TableDataRow<String>("",new StringObject(" ")));
            for(IdNameDO resultDO :  entries){
                model.add(new TableDataRow<String>(resultDO.getName(),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "countryDropdown", model);
            version++;
        }
        return model;
        
    }

}
