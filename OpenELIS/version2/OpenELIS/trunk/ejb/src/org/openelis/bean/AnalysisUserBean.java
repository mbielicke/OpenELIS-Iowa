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
package org.openelis.bean;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.entity.AnalysisUser;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.local.AnalysisUserLocal;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class AnalysisUserBean implements AnalysisUserLocal {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    public AnalysisUserViewDO fetchById(Integer id) throws Exception {
        Query query;
        AnalysisUserViewDO data;
        SystemUserVO user;
        
        query = manager.createNamedQuery("AnalysisUser.FetchById");
        query.setParameter("id", id);
        
        try {
            data = (AnalysisUserViewDO)query.getSingleResult();
            
            if (data.getSystemUserId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUser(user.getLoginName());
            }
            return data;
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<AnalysisUserViewDO> fetchByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        ArrayList<AnalysisUserViewDO> returnList;
        AnalysisUserViewDO data;
        SystemUserVO user;
        
        query = manager.createNamedQuery("AnalysisUser.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        
        returnList = DataBaseUtil.toArrayList(query.getResultList());
        for (int i = 0; i < returnList.size(); i++ ) {
            data = returnList.get(i);

            if (data.getSystemUserId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUser(user.getLoginName());
            }
        }
        
        if(returnList.size() == 0)
            throw new NotFoundException();

        return returnList;
    }
    
    public ArrayList<AnalysisUserViewDO> fetchByActionAndAnalysisId(Integer analysisId,
                                                                    Integer actionId) throws Exception {
        Query query;
        ArrayList<AnalysisUserViewDO> returnList;
        AnalysisUserViewDO data;
        SystemUserVO user;
        
        query = manager.createNamedQuery("AnalysisUser.FetchByActionAndAnalysisId");
        query.setParameter("analysisId", analysisId);
        query.setParameter("actionId", actionId);
        
        returnList = DataBaseUtil.toArrayList(query.getResultList());
        for (int i = 0; i < returnList.size(); i++ ) {
            data = returnList.get(i);

            if (data.getSystemUserId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUser(user.getLoginName());
            }
        }
        
        if(returnList.size() == 0)
            throw new NotFoundException();

        return returnList;
    }  

    public AnalysisUserViewDO add(AnalysisUserViewDO data) {
        AnalysisUser entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AnalysisUser();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setActionId(data.getActionId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AnalysisUserViewDO update(AnalysisUserViewDO data) {
        AnalysisUser entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AnalysisUser.class, data.getId());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setActionId(data.getActionId());
        
        return data;
    }

    public void delete(AnalysisUserViewDO data) {
        AnalysisUser entity;
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AnalysisUser.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}