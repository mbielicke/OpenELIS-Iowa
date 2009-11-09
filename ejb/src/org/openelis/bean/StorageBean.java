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

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.StorageViewDO;
import org.openelis.entity.Storage;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.LoginLocal;
import org.openelis.local.StorageLocal;

@Stateless

@SecurityDomain("openelis")
public class StorageBean implements StorageLocal{
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB LoginLocal login;

    public ArrayList<StorageViewDO> fetchByRefId(Integer refTableId, Integer refId) throws Exception {
        Query query = manager.createNamedQuery("Storage.StorageById");
        query.setParameter("referenceTable", refTableId);
        query.setParameter("id", refId);
        
        ArrayList<StorageViewDO> list = (ArrayList<StorageViewDO>)query.getResultList();
        
        if(list.size() == 0)
            throw new NotFoundException();
        
        return list;
    }

    public StorageViewDO add(StorageViewDO data) throws Exception {
        Storage entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Storage();
        entity.setCheckin(data.getCheckin());
        entity.setCheckout(data.getCheckout());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setSystemUserId(login.getSystemUserId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public StorageViewDO update(StorageViewDO data) throws Exception {
        Storage entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Storage.class, data.getId());

        entity.setCheckin(data.getCheckin());
        entity.setCheckout(data.getCheckout());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setSystemUserId(data.getSystemUserId());
        
        return data;
    }
    
    public void delete(StorageViewDO data) throws Exception {
        Storage entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Storage.class, data.getId());
        
        if (entity != null)
            manager.remove(entity);
    }
}
