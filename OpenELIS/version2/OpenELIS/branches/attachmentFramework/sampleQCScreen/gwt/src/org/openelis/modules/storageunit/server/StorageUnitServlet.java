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
package org.openelis.modules.storageunit.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.StorageUnitBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageUnitDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.storageunit.client.StorageUnitServiceInt;

@WebServlet("/openelis/storageUnit")
public class StorageUnitServlet extends RemoteServlet implements StorageUnitServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    StorageUnitBean storageUnit;

    public StorageUnitDO fetchById(Integer id) throws Exception {
        try {        
            return storageUnit.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        try {        
            return storageUnit.fetchByDescription(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return storageUnit.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageUnitDO add(StorageUnitDO data) throws Exception {
        try {        
            return storageUnit.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageUnitDO update(StorageUnitDO data) throws Exception {
        try {        
            return storageUnit.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageUnitDO fetchForUpdate(Integer id) throws Exception {
        try {        
            return storageUnit.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void delete(StorageUnitDO data) throws Exception {
        try {        
            storageUnit.delete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StorageUnitDO abortUpdate(Integer id) throws Exception {
        try {        
            return storageUnit.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
