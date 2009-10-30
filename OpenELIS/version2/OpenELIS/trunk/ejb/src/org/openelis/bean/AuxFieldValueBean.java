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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.entity.AuxFieldValue;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
//@RolesAllowed("organization-select")
public class AuxFieldValueBean implements AuxFieldValueLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;

    public ArrayList<AuxFieldValueDO> fetchById(Integer id) throws Exception {
        Query query;
        ArrayList<AuxFieldValueDO> data;
        
        query = manager.createNamedQuery("AuxFieldValue.AuxFieldValueDOList");
        query.setParameter("auxFieldId", id);
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<AuxFieldValueDO> fetchByAuxDataRefIdRefTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        ArrayList<AuxFieldValueDO> data;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByDataRefId");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);
        
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public AuxFieldValueDO add(AuxFieldValueDO data) throws Exception {
        AuxFieldValue entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AuxFieldValue();
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public AuxFieldValueDO update(AuxFieldValueDO data) throws Exception {
        AuxFieldValue entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxFieldValue.class, data.getId());
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }
}
