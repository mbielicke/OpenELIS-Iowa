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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.StorageLocationBean;
import org.openelis.bean.StorageLocationManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.modules.storageLocation.client.StorageLocationServiceInt;

@WebServlet("/openelis/storageLocation")
public class StorageLocationServlet extends RemoteServlet implements StorageLocationServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    StorageLocationManagerBean storageLocationManager;
    
    @EJB
    StorageLocationBean        storageLocation;

    public StorageLocationManager fetchById(Integer id) throws Exception {
        try {        
            return storageLocationManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        try {        
            return storageLocationManager.fetchWithChildren(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return storageLocation.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        try {        
            return storageLocationManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        try {        
            return storageLocationManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return storageLocationManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        try {        
            return storageLocationManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    //
    // support for StorageLocationChildManager
    //
    public StorageLocationChildManager fetchChildByParentStorageLocationId(Integer id)
                                                                                      throws Exception {
        try {        
            return storageLocationManager.fetchChildByParentStorageLocationId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageLocationViewDO validateForDelete(StorageLocationViewDO data) throws Exception {
        storageLocation.validateForDelete(data);
        try {        
            return data;
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

}
