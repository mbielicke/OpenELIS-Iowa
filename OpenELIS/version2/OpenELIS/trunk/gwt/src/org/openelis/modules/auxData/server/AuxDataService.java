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
package org.openelis.modules.auxData.server;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;
import org.openelis.manager.AuxDataManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AuxDataManagerRemote;
import org.openelis.remote.AuxDataRemote;

public class AuxDataService {
    public AuxDataManager fetchById(AuxDataDO auxData) throws Exception {
        return managerRemote().fetchById(auxData.getReferenceId(), auxData.getReferenceTableId());
    }   
    
    public ArrayList<AuxDataViewDO> fetchByRefId(AuxDataDO auxData) throws Exception {
        return remote().fetchById(auxData.getReferenceId(), auxData.getReferenceTableId());
    }
    
    public IdVO getAuxGroupIdFromSystemVariable(String sysVariableKey) throws Exception {
        return remote().fetchGroupIdBySystemVariable(sysVariableKey);
    }
    
    private AuxDataRemote remote(){
        return (AuxDataRemote)EJBFactory.lookup("openelis/AuxDataBean/remote");
    }
    
    private AuxDataManagerRemote managerRemote(){
        return (AuxDataManagerRemote)EJBFactory.lookup("openelis/AuxDataManagerBean/remote");
    }
}
