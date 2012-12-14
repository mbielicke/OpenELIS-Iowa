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
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.entity.Instrument;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.InstrumentLocal;
import org.openelis.meta.InstrumentMeta;
import org.openelis.remote.InstrumentRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class InstrumentBean implements InstrumentRemote , InstrumentLocal{

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    private static final InstrumentMeta meta = new InstrumentMeta();
    
    public InstrumentViewDO fetchById(Integer id) throws Exception {
        Query query;
        InstrumentViewDO data;        
        
        query = manager.createNamedQuery("Instrument.FetchById");
        query.setParameter("id", id);
        try {
            data = (InstrumentViewDO) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<InstrumentViewDO> fetchByName(String name, int max) throws Exception{
        Query query = manager.createNamedQuery("Instrument.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<InstrumentViewDO> fetchActiveByName(String name, int max) throws Exception{
        Query query = manager.createNamedQuery("Instrument.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO("
                     + InstrumentMeta.getId()+", "+InstrumentMeta.getName()
                     +", "+InstrumentMeta.getSerialNumber()+ ") ");        
        builder.constructWhere(fields);
        builder.setOrderBy(InstrumentMeta.getName()+", "+InstrumentMeta.getSerialNumber());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }
    
    public InstrumentDO add(InstrumentDO data) throws Exception {
        Instrument entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Instrument();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setModelNumber(data.getModelNumber());
        entity.setSerialNumber(data.getSerialNumber());
        entity.setTypeId(data.getTypeId());
        entity.setLocation(data.getLocation());
        entity.setIsActive(data.getIsActive());
        entity.setScriptletId(data.getScriptletId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;        
    }

    public InstrumentDO update(InstrumentDO data) throws Exception {
        Instrument entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Instrument.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setModelNumber(data.getModelNumber());
        entity.setSerialNumber(data.getSerialNumber());
        entity.setTypeId(data.getTypeId());
        entity.setLocation(data.getLocation());
        entity.setIsActive(data.getIsActive());
        entity.setScriptletId(data.getScriptletId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        
        return data;        
    }

    public void validate(InstrumentDO data) throws Exception {
        String name, serialNumber,location;
        boolean checkDuplicate;
        Datetime activeBegin, activeEnd;
        Query query;
        List<InstrumentViewDO> instruments;
        InstrumentViewDO inst;
        Integer id;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        name = data.getName();
        serialNumber = data.getSerialNumber();
        activeBegin = data.getActiveBegin();
        activeEnd = data.getActiveEnd();
        location = data.getLocation();
        id = data.getId();
        checkDuplicate = true;
        
        if(DataBaseUtil.isEmpty(name)){
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getName()));
            checkDuplicate = false;
        } 
        
        if(DataBaseUtil.isEmpty(serialNumber)){
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getSerialNumber()));
            checkDuplicate = false;
        } 
        
        if(DataBaseUtil.isEmpty(location)) 
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getLocation()));        
        
        if(DataBaseUtil.isEmpty(data.getTypeId())) 
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getTypeId()));                                       
        
        if(checkDuplicate) {
            if(!DataBaseUtil.isEmpty(activeEnd) && DataBaseUtil.isAfter(activeBegin,activeEnd))
                list.add(new FieldErrorException("endDateAfterBeginDateException",null));                              
            
            query = manager.createNamedQuery("Instrument.FetchByNameSerialNumber");
            query.setParameter("name", name);
            query.setParameter("serialNumber", serialNumber);
            instruments = query.getResultList();
            
            for(int i = 0; i < instruments.size(); i++) {
                inst = instruments.get(i);
                if((!inst.getId().equals(id))) {
                    list.add(new FieldErrorException("instrumentUniqueException",null));                                   
                    break; 
                }
            }
        }
        
        if (list.size() > 0)
            throw list;       
    }   
}
