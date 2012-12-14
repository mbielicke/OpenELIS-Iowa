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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.domain.InventoryAdjustmentViewDO;
import org.openelis.entity.InventoryAdjustment;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.meta.InventoryAdjustmentMeta;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class InventoryAdjustmentBean implements InventoryAdjustmentRemote, InventoryAdjustmentLocal{

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
       
    private static final InventoryAdjustmentMeta meta = new InventoryAdjustmentMeta();
    
    public InventoryAdjustmentViewDO fetchById(Integer id) throws Exception {
        Query query;
        InventoryAdjustmentViewDO data;
        SystemUserVO user;
        
        query = manager.createNamedQuery("InventoryAdjustment.FetchById");
        query.setParameter("id", id);
        try {
            data = (InventoryAdjustmentViewDO)query.getSingleResult();            
            user = EJBFactory.getUserCache().getSystemUser(data.getSystemUserId());
            data.setSystemUserName(user.getLoginName());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<InventoryAdjustmentDO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.InventoryAdjustmentDO(" +
                          InventoryAdjustmentMeta.getId() + ", " +
                          InventoryAdjustmentMeta.getDescription() + ", " +
                          InventoryAdjustmentMeta.getSystemUserId() + ", " +
                          InventoryAdjustmentMeta.getAdjustmentDate() +  ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(InventoryAdjustmentMeta.getId() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<InventoryAdjustmentDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<InventoryAdjustmentDO>)list;
    }
    
    public InventoryAdjustmentDO add(InventoryAdjustmentDO data) throws Exception {
        InventoryAdjustment entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new InventoryAdjustment();     
        entity.setDescription(data.getDescription());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setAdjustmentDate(data.getAdjustmentDate());
               
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public InventoryAdjustmentDO update(InventoryAdjustmentDO data) throws Exception {
        InventoryAdjustment entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(InventoryAdjustment.class, data.getId());
        entity.setDescription(data.getDescription());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setAdjustmentDate(data.getAdjustmentDate());
        
        return data;
    }

    public void validate(InventoryAdjustmentDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getDescription()));
        
        if (DataBaseUtil.isEmpty(data.getAdjustmentDate()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getAdjustmentDate()));
        
        if (DataBaseUtil.isEmpty(data.getSystemUserId()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getSystemUserId()));
               
        if (list.size() > 0)
            throw list;                
    }   
}
