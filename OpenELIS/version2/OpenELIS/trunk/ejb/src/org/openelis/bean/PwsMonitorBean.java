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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.PwsMonitorDO;
import org.openelis.entity.PwsMonitor;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.PwsMonitorLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("pws-select")
public class PwsMonitorBean implements PwsMonitorLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    @SuppressWarnings("unchecked")
    public ArrayList<PwsMonitorDO> fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {        
        Query query;
        List list;

        query = manager.createNamedQuery("PwsMonitor.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public PwsMonitorDO add(PwsMonitorDO data) throws Exception {
        PwsMonitor entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new PwsMonitor();
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

    public PwsMonitorDO update(PwsMonitorDO data) throws Exception {        
        PwsMonitor entity;
        
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PwsMonitor.class, data.getId());
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
    
    public void delete(PwsMonitorDO data) throws Exception {
        PwsMonitor entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PwsMonitor.class, data.getId());
        
        if (entity != null)
            manager.remove(entity);
    }



}
