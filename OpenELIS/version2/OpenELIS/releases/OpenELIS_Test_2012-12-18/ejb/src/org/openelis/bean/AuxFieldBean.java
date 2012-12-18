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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.entity.AuxField;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldLocal;
import org.openelis.meta.AuxFieldGroupMeta;

@Stateless
@SecurityDomain("openelis")
public class AuxFieldBean implements AuxFieldLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    public AuxFieldViewDO fetchById(Integer id) throws Exception {
        Query query;
        AuxFieldViewDO data;

        query = manager.createNamedQuery("AuxField.FetchById");
        query.setParameter("id", id);
        try {
            data = (AuxFieldViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<AuxFieldViewDO> fetchByGroupId(Integer groupId) throws Exception {
        Query query;
        ArrayList<AuxFieldViewDO> data;

        query = manager.createNamedQuery("AuxField.FetchAllActiveByGroupId");
        query.setParameter("auxFieldGroupId", groupId);
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public AuxFieldViewDO add(AuxFieldViewDO data) throws Exception {
        AuxField entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AuxField();
        entity.setAuxFieldGroupId(data.getAuxFieldGroupId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setDescription(data.getDescription());
        entity.setMethodId(data.getMethodId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setIsRequired(data.getIsRequired());
        entity.setIsActive(data.getIsActive());
        entity.setIsReportable(data.getIsReportable());
        entity.setScriptletId(data.getScriptletId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public AuxFieldViewDO update(AuxFieldViewDO data) throws Exception {
        AuxField entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxField.class, data.getId());
        entity.setAuxFieldGroupId(data.getAuxFieldGroupId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setDescription(data.getDescription());
        entity.setMethodId(data.getMethodId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setIsRequired(data.getIsRequired());
        entity.setIsActive(data.getIsActive());
        entity.setIsReportable(data.getIsReportable());
        entity.setScriptletId(data.getScriptletId());

        return data;
    }
    
    public void delete(AuxFieldViewDO data) throws Exception {
        AuxField entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxField.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(AuxFieldViewDO data) throws Exception {
        ValidationErrorsList list;
    
        list = new ValidationErrorsList();
        if(DataBaseUtil.isEmpty(data.getAnalyteId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             AuxFieldGroupMeta.getFieldAnalyteName()));
            throw list;
        }        
    }
    
}
