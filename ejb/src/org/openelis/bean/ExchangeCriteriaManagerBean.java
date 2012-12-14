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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.remote.ExchangeCriteriaManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

@TransactionManagement(TransactionManagementType.BEAN)

public class ExchangeCriteriaManagerBean implements ExchangeCriteriaManagerRemote {
    
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lock;

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        return ExchangeCriteriaManager.fetchById(id);
    }
    
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        return ExchangeCriteriaManager.fetchByName(name);
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        return ExchangeCriteriaManager.fetchWithProfiles(id);
    }
    
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        return ExchangeCriteriaManager.fetchWithProfilesByName(name);
    }

    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.add();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.validateLock(ReferenceTable.EXCHANGE_CRITERIA, man.getExchangeCriteria().getId());        
            man.update();
            lock.unlock(ReferenceTable.EXCHANGE_CRITERIA, man.getExchangeCriteria().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }
    
    public void delete(ExchangeCriteriaManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.DELETE);
    
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.validateLock(ReferenceTable.EXCHANGE_CRITERIA, man.getExchangeCriteria().getId());        
            man.delete();
            lock.unlock(ReferenceTable.EXCHANGE_CRITERIA, man.getExchangeCriteria().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        ExchangeCriteriaManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.lock(ReferenceTable.EXCHANGE_CRITERIA, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.EXCHANGE_CRITERIA, id);
        return fetchById(id);
    }
    
    public ExchangeCriteriaManager duplicate(Integer id) throws Exception {
        ExchangeCriteriaManager newMan;
        ExchangeCriteriaManager oldMan;
        
        oldMan = fetchById(id);
        newMan = ExchangeCriteriaManager.getInstance();      
        duplicateExchangeCriteria(oldMan, newMan);        
        
        return newMan;
    }

    public ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception {
        return ExchangeProfileManager.fetchByExchangeCriteriaId(id);
    }
    
    private void duplicateExchangeCriteria(ExchangeCriteriaManager oldMan,
                                           ExchangeCriteriaManager newMan) throws Exception {
        ExchangeCriteriaViewDO oldData, newData;
        
        oldData = oldMan.getExchangeCriteria();
        newData = newMan.getExchangeCriteria();
        
        newData.setName(oldData.getName());
        newData.setEnvironmentId(oldData.getEnvironmentId());
        newData.setDestinationUri(oldData.getDestinationUri());
        newData.setIsAllAnalysesIncluded(oldData.getIsAllAnalysesIncluded());
        newData.setFields(oldData.getFields());
        
        duplicateProfiles(oldMan.getProfiles(), newMan.getProfiles());
    }
    
    private void duplicateProfiles(ExchangeProfileManager oldMan, ExchangeProfileManager newMan) {
       ExchangeProfileDO oldData, newData; 
        
       for (int i = 0; i < oldMan.count(); i++) {
           oldData = oldMan.getProfileAt(i);
           newData = new ExchangeProfileDO();
           newData.setProfileId(oldData.getProfileId());
           newData.setSortOrder(oldData.getSortOrder());
           newMan.addProfile(newData);
       }
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("exchangedataselection", flag);
    }
}