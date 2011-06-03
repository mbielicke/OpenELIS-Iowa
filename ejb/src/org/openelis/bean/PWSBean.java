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
import org.openelis.domain.PWSDO;
import org.openelis.entity.PWS;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.PWSLocal;
import org.openelis.meta.PWSMeta;
import org.openelis.remote.PWSRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("pws-select")
public class PWSBean implements PWSLocal, PWSRemote {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    private static final PWSMeta             meta = new PWSMeta();

    public PWSDO fetchById(Integer id) throws Exception {
        Query query;
        PWSDO data;
        
        query = manager.createNamedQuery("PWS.FetchById");
        query.setParameter("id", id);
        try {
            data = (PWSDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data; 
    }
    
    public PWSDO fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Query query;
        PWSDO data;
        
        query = manager.createNamedQuery("PWS.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);
        try {
            data = (PWSDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data; 
    }
    
    public PWSDO fetchByNumber0(String number0) throws Exception {
        Query query;
        PWSDO data;
        
        query = manager.createNamedQuery("PWS.FetchByNumber0");
        query.setParameter("number0", number0);
        try {
            data = (PWSDO)query.getSingleResult();
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
                          PWSMeta.getTinwsysIsNumber() + ", " +
                          PWSMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(PWSMeta.getName());

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
        
    public PWSDO add(PWSDO data) throws Exception {
        PWS entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new PWS();
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

    public PWSDO update(PWSDO data) throws Exception {
        PWS entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PWS.class, data.getId());
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
