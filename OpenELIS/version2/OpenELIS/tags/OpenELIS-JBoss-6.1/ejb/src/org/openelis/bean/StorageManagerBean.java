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
package org.openelis.bean;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.manager.StorageManager;
import org.openelis.remote.StorageManagerRemote;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)

public class StorageManagerBean implements StorageManagerRemote {
    
    public StorageManager fetchById(Integer referenceTableId, Integer referenceId) throws Exception {
        return StorageManager.fetchByRefTableRefId(referenceTableId, referenceId);
    }
    
    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {        
        return StorageManager.fetchCurrentByLocationId(id);
    }

    public StorageManager fetchHistoryByLocationId(Integer id, int first, int max) throws Exception { 
        return StorageManager.fetchHistoryByLocationId(id, first, max);
    }

    public StorageManager update(StorageManager man) throws Exception {
        man.validate();
        try {     
            man.update();
        } catch (Exception e) {
            throw e;
        }

        return man;
    }
}
