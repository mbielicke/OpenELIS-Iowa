package org.openelis.server.handlers;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.messages.TestReportingMethodCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

public class TestReportingMethodCacheHandler implements
                                            MessageHandler<TestReportingMethodCacheMessage> {

static CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    
    public static int version = 0;
    
    public void handle(TestReportingMethodCacheMessage message) {
        CachingManager.remove("InitialData", "testReportingMethodDropdown");        
    }
    
    public static TableDataModel<TableDataRow<Integer>> getTestReportingMethods() {
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel<TableDataRow<Integer>>)CachingManager.getElement("InitialData", "testReportingMethodDropdown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("test_reporting_method");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new TableDataModel<TableDataRow<Integer>>();
        
            model.add(new TableDataRow<Integer>(null,new StringObject(" ")));
            for(IdNameDO resultDO :  entries){
                model.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "testReportingMethodDropdown", model);
            version++;
        }
        return model;
        
    }
}
