package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.messages.ContactTypeCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

import java.util.ArrayList;

public class ContactTypeCacheHandler implements MessageHandler<ContactTypeCacheMessage> {
    
    static CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    
    public static int version = 0;

    public void handle(ContactTypeCacheMessage message) {
        CachingManager.remove("InitialData", "contactTypeDropdown");
        
    }
    
    public static DataModel<DataSet> getContactTypes() {
        DataModel<DataSet> model = (DataModel<DataSet>)CachingManager.getElement("InitialData", "contactTypeDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("contact_type");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new DataModel<DataSet>();
        
            model.add(new DataSet(new NumberObject(0),new StringObject("")));
            for(IdNameDO resultDO :  entries){
                model.add(new DataSet(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "contactTypeDropdown", model);
            version++;
        }
        return model;
        
    }

}
