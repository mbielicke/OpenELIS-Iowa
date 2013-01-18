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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.PWSMonitorDO;
import org.openelis.entity.PWSMonitor;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.PWSMonitorLocal;

@Stateless
@SecurityDomain("openelis")

public class PWSMonitorBean implements PWSMonitorLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    @SuppressWarnings("unchecked")
    public ArrayList<PWSMonitorDO> fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {        
        Query query;
        List list;

        query = manager.createNamedQuery("PWSMonitor.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public PWSMonitorDO add(PWSMonitorDO data) throws Exception {
        PWSMonitor entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new PWSMonitor();
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setStAsgnIdentCd(data.getStAsgnIdentCd());
        entity.setName(data.getName());
        entity.setTiaanlgpTiaanlytName(data.getTiaanlgpTiaanlytName());
        entity.setNumberSamples(data.getNumberSamples());
        entity.setCompBeginDate(data.getCompBeginDate());
        entity.setCompEndDate(data.getCompEndDate());
        entity.setFrequencyName(data.getFrequencyName());
        entity.setPeriodName(data.getPeriodName());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public PWSMonitorDO update(PWSMonitorDO data) throws Exception {        
        PWSMonitor entity;
        
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PWSMonitor.class, data.getId());
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setStAsgnIdentCd(data.getStAsgnIdentCd());
        entity.setName(data.getName());
        entity.setTiaanlgpTiaanlytName(data.getTiaanlgpTiaanlytName());
        entity.setNumberSamples(data.getNumberSamples());
        entity.setCompBeginDate(data.getCompBeginDate());
        entity.setCompEndDate(data.getCompEndDate());
        entity.setFrequencyName(data.getFrequencyName());
        entity.setPeriodName(data.getPeriodName());
        
        return data;
    }
    
    public void delete(PWSMonitorDO data) throws Exception {
        PWSMonitor entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PWSMonitor.class, data.getId());
        
        if (entity != null)
            manager.remove(entity);
    }



}