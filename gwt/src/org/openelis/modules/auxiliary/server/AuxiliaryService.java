/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
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
        return remote().fetchActive();
    }

    // manager methods
    public AuxFieldGroupManager fetchGroupById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }

    public AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception {
        return remoteManager().fetchByIdWithFields(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }

    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        return remoteManager().add(man);
    }

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        return remoteManager().update(man);
    }

    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    // other managers
    public AuxFieldManager fetchFieldById(Integer id) throws Exception {
        return remoteManager().fetchFieldById(id);
    }

    public AuxFieldManager fetchFieldByGroupId(Integer groupId) throws Exception {
        return remoteManager().fetchFieldByGroupId(groupId);
    }

    public AuxFieldManager fetchFieldByGroupIdWithValues(Integer groupId) throws Exception {
        return remoteManager().fetchFieldByGroupIdWithValues(groupId);
    }
    
    public AuxFieldValueManager fetchFieldValueByFieldId(Integer fieldId) throws Exception {
        return remoteManager().fetchFieldValueByFieldId(fieldId);
    }

    private AuxFieldGroupRemote remote() {
        return (AuxFieldGroupRemote)EJBFactory.lookup("openelis/AuxFieldGroupBean/remote");
    }

    private AuxFieldGroupManagerRemote remoteManager() {
        return (AuxFieldGroupManagerRemote)EJBFactory.lookup("openelis/AuxFieldGroupManagerBean/remote");
    }
}