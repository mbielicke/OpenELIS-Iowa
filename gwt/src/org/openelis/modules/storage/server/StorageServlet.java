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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.StorageLocationBean;
import org.openelis.bean.StorageManagerBean;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.StorageManager;
import org.openelis.modules.storage.client.StorageServiceInt;

@WebServlet("/openelis/storage")
public class StorageServlet extends RemoteServlet implements StorageServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    StorageManagerBean storageManager;
    
    @EJB
    StorageLocationBean storageLocation;
    

    public StorageManager fetchById(Query query) throws Exception {
        try {        
            return storageManager.fetchById(new Integer(query.getFields().get(0).getQuery()),
                                            new Integer(query.getFields().get(1).getQuery()));
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {
        try {        
            return storageManager.fetchCurrentByLocationId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageManager fetchHistoryByLocationId(Query query) throws Exception {        
        try {        
            return storageManager.fetchHistoryByLocationId(new Integer(query.getFields().get(0).getQuery()), 
                                                                       query.getPage(),query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<StorageLocationViewDO> fetchAvailableByName(String search) throws Exception {
        try {        
            return storageLocation.fetchAvailableByName(search + "%", 50);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageManager update(StorageManager man) throws Exception {
        try {        
            return storageManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
