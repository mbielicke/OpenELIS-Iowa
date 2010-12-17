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
package org.openelis.modules.storage.server;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageManagerRemote;

public class StorageService {

    private static final int rowPP = 100;

    public StorageManager fetchById(Query query) throws Exception {
        return remoteManager().fetchById(new Integer(query.getFields().get(0).getQuery()),
                                         new Integer(query.getFields().get(1).getQuery()));
    }

    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {
        return remoteManager().fetchCurrentByLocationId(id);
    }

    public StorageManager fetchHistoryByLocationId(Query query) throws Exception {
        query.setPage(query.getPage() * rowPP);
        return remoteManager().fetchHistoryByLocationId(query, rowPP);
    }

    public ArrayList<StorageLocationViewDO> fetchAvailableByName(String search) throws Exception {
        return storageLocationRemote().fetchAvailableByName(search + "%", 10);
    }

    public StorageManager update(StorageManager man) throws Exception {
        return remoteManager().update(man);
    }

    private StorageManagerRemote remoteManager() {
        return (StorageManagerRemote)EJBFactory.lookup("openelis/StorageManagerBean/remote");
    }

    private StorageLocationRemote storageLocationRemote() {
        return (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    }
}
