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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PwsDO;
import org.openelis.entity.Pws;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.PwsLocal;
import org.openelis.meta.PwsMeta;
import org.openelis.remote.PwsRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("pws-select")
public class PwsBean implements PwsLocal, PwsRemote {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    private static final PwsMeta             meta = new PwsMeta();

    public PwsDO fetchById(Integer id) throws Exception {
        Query query;
        PwsDO data;
        
        query = manager.createNamedQuery("Pws.FetchById");
        query.setParameter("id", id);
        try {
            data = (PwsDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data; 
    }
    
    public PwsDO fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Query query;
        PwsDO data;
        
        query = manager.createNamedQuery("Pws.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);
        try {
            data = (PwsDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data; 
    }
    
    public PwsDO fetchByNumber0(String number0) throws Exception {
        Query query;
        PwsDO data;
        
        query = manager.createNamedQuery("Pws.FetchByNumber0");
        query.setParameter("number0", number0);
        try {
            data = (PwsDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data; 
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + 
                          PwsMeta.getTinwsysIsNumber() + ", " +
                          PwsMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(PwsMeta.getName());

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
        
    public PwsDO add(PwsDO data) throws Exception {
        Pws entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Pws();
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setNumber0(data.getNumber0());
        entity.setAlternateStNum(data.getAlternateStNum());
        entity.setName(data.getName());
        entity.setActivityStatusCd(data.getActivityStatusCd());
        entity.setDPrinCitySvdNm(data.getDPrinCitySvdNm());
        entity.setDPrinCntySvdNm(data.getDPrinCntySvdNm());
        entity.setDPopulationCount(data.getDPopulationCount());
        entity.setDPwsStTypeCd(data.getDPwsStTypeCd());
        entity.setActivityRsnTxt(data.getActivityRsnTxt());
        entity.setStartDay(data.getStartDay());
        entity.setStartMonth(data.getStartMonth());
        entity.setEndDay(data.getEndDay());
        entity.setEndMonth(data.getEndMonth());
        entity.setEffBeginDt(data.getEffBeginDt());
        entity.setEffEndDt(data.getEffEndDt());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public PwsDO update(PwsDO data) throws Exception {
        Pws entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Pws.class, data.getId());
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setNumber0(data.getNumber0());
        entity.setAlternateStNum(data.getAlternateStNum());
        entity.setName(data.getName());
        entity.setActivityStatusCd(data.getActivityStatusCd());
        entity.setDPrinCitySvdNm(data.getDPrinCitySvdNm());
        entity.setDPrinCntySvdNm(data.getDPrinCntySvdNm());
        entity.setDPopulationCount(data.getDPopulationCount());
        entity.setDPwsStTypeCd(data.getDPwsStTypeCd());
        entity.setActivityRsnTxt(data.getActivityRsnTxt());
        entity.setStartDay(data.getStartDay());
        entity.setStartMonth(data.getStartMonth());
        entity.setEndDay(data.getEndDay());
        entity.setEndMonth(data.getEndMonth());
        entity.setEffBeginDt(data.getEffBeginDt());
        entity.setEffEndDt(data.getEffEndDt());
        
        return data;
    }

}
