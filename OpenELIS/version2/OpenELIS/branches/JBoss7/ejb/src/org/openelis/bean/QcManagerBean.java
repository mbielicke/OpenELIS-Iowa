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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;

@Stateless
@SecurityDomain("openelis")

@TransactionManagement(TransactionManagementType.BEAN)

public class QcManagerBean {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lockBean;
    
    @EJB
    private UserCacheBean  userCache;

    public QcManager fetchById(Integer id) throws Exception {
        return QcManager.fetchById(id);
    }

    public QcManager fetchWithAnalytes(Integer id) throws Exception {
        return QcManager.fetchWithAnalytes(id);
    }
    
    public QcManager fetchWithLots(Integer id) throws Exception {
        return QcManager.fetchWithLots(id);
    }

    public QcManager add(QcManager man) throws Exception {
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

    public QcManager update(QcManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.QC, man.getQc().getId());
            man.update();
            lockBean.unlock(ReferenceTable.QC, man.getQc().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public QcManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        QcManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(ReferenceTable.QC, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public QcManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(ReferenceTable.QC, id);
        return fetchById(id);
    }
    
    public QcManager duplicate(Integer id) throws Exception {
        QcManager oldMan, newMan;
        
        oldMan = fetchById(id);
        newMan = QcManager.getInstance();      
        duplicateQc(oldMan, newMan);
        
        return newMan;
    }

    public QcAnalyteManager fetchAnalyteByQcId(Integer id) throws Exception {
        return QcAnalyteManager.fetchByQcId(id);
    }
    
    public QcLotManager fetchLotByQcId(Integer id) throws Exception {
        return QcLotManager.fetchByQcId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("qc", flag);
    }
    

    private void duplicateQc(QcManager oldMan, QcManager newMan) throws Exception {
        QcViewDO oldData, newData;
        
        oldData = oldMan.getQc();
        newData = newMan.getQc();
        
        newData.setName(oldData.getName());
        newData.setTypeId(oldData.getTypeId());
        newData.setInventoryItemId(oldData.getInventoryItemId());
        newData.setSource(oldData.getSource());
        newData.setIsActive(oldData.getIsActive());
        
        duplicateAnalytes(oldMan.getAnalytes(), newMan.getAnalytes());
    }

    private void duplicateAnalytes(QcAnalyteManager oldMan, QcAnalyteManager newMan) {
        QcAnalyteViewDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getAnalyteAt(i);
            newData = new QcAnalyteViewDO();
            newData.setSortOrder(oldData.getSortOrder());
            newData.setAnalyteId(oldData.getAnalyteId());
            newData.setAnalyteName(oldData.getAnalyteName());
            newData.setTypeId(oldData.getTypeId());
            newData.setValue(oldData.getValue());
            newData.setIsTrendable(oldData.getIsTrendable());
            newMan.addAnalyte(newData);
        }
    }
}