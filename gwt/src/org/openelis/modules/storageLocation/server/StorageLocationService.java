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
import org.openelis.server.EJBFactory;

public class StorageLocationService {

    public StorageLocationManager fetchById(Integer id) throws Exception {
        return EJBFactory.getStorageLocationManager().fetchById(id);
    }

    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        return EJBFactory.getStorageLocationManager().fetchWithChildren(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getStorageLocation().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        return EJBFactory.getStorageLocationManager().add(man);
    }

    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        return EJBFactory.getStorageLocationManager().update(man);
    }

    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getStorageLocationManager().fetchForUpdate(id);
    }

    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getStorageLocationManager().abortUpdate(id);
    }

    //
    // support for StorageLocationChildManager
    //
    public StorageLocationChildManager fetchChildByParentStorageLocationId(Integer id)
                                                                                      throws Exception {
        return EJBFactory.getStorageLocationManager().fetchChildByParentStorageLocationId(id);
    }

    public StorageLocationViewDO validateForDelete(StorageLocationViewDO data) throws Exception {
        EJBFactory.getStorageLocation().validateForDelete(data);
        return data;
    }

}
