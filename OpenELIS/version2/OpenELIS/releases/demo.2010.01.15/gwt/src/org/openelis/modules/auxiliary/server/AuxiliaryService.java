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

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AuxFieldGroupManagerRemote;
import org.openelis.remote.AuxFieldGroupRemote;

public class AuxiliaryService {
    
    private static final int rowPP = 26;
    
    public ArrayList<AuxFieldGroupDO> fetchActive() throws Exception {
        try {
            return remote().fetchActive();
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    // manager methods
    public AuxFieldGroupManager fetchGroupById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithFields(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldManager fetchAuxFieldById(Integer id) throws Exception {
        try {
            return remoteManager().fetchAuxFieldById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }

    }

    public AuxFieldManager fetchByGroupIdWithValues(Integer groupId) throws Exception {
        try {
            return remoteManager().fetchByGroupIdWithValues(groupId);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public AuxFieldManager fetchByGroupId(Integer auxFieldGroupId) throws Exception {
        try {
            return remoteManager().fetchByGroupId(auxFieldGroupId);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public AuxFieldValueManager fetchByAuxFieldId(Integer auxFieldId) throws Exception {
        try {
            return remoteManager().fetchFieldValueByFieldId(auxFieldId);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
    
    private AuxFieldGroupRemote remote() {
        return (AuxFieldGroupRemote)EJBFactory.lookup("openelis/AuxFieldGroupBean/remote");
    }
    
    private AuxFieldGroupManagerRemote remoteManager() {
        return (AuxFieldGroupManagerRemote)EJBFactory.lookup("openelis/AuxFieldGroupManagerBean/remote");
    }

}
