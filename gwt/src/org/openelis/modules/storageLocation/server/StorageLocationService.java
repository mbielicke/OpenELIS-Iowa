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
package org.openelis.modules.storageLocation.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationManagerRemote;
import org.openelis.remote.StorageLocationRemote;

public class StorageLocationService {

    public StorageLocationManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }

    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        return remoteManager().fetchWithChildren(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        return remoteManager().add(man);
    }

    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        return remoteManager().update(man);
    }

    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    //
    // support for StorageLocationChildManager
    //
    public StorageLocationChildManager fetchChildByParentStorageLocationId(Integer id)
                                                                                      throws Exception {
        return remoteManager().fetchChildByParentStorageLocationId(id);
    }

    public StorageLocationViewDO validateForDelete(StorageLocationViewDO data) throws Exception {
        remote().validateForDelete(data);
        return data;
    }

    private StorageLocationRemote remote() {
        return (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    }

    private StorageLocationManagerRemote remoteManager() {
        return (StorageLocationManagerRemote)EJBFactory.lookup("openelis/StorageLocationManagerBean/remote");
    }

}
