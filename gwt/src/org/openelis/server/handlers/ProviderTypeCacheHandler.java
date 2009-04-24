/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/

package org.openelis.server.handlers;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.Message;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

import java.util.ArrayList;

public class ProviderTypeCacheHandler implements MessageHandler {

    public static int version = 0;
    
    public void handle(Message message) {
        CachingManager.remove("InitialData", "providerTypeDropDown");
    }
    
    public static TableDataModel<TableDataRow<Integer>> getProviderTypes() {
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel<TableDataRow<Integer>>)CachingManager.getElement("InitialData", "providerTypeDropDown");
        if(model == null) {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
            Integer id = remote.getCategoryId("provider_type");
        
            ArrayList<IdNameDO> entries = (ArrayList<IdNameDO>)remote.getDropdownValues(id);
        
            //  we need to build the model to return
            model = new TableDataModel<TableDataRow<Integer>>();
        
            model.add(new TableDataRow<Integer>(null, new StringObject("")));
            for(IdNameDO resultDO :  entries){
                model.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            }   
            CachingManager.putElement("InitialData", "providerTypeDropDown", model);
            version++;
        }
        return model;
        
    }

}
