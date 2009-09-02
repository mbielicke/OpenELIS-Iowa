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
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.local.LockLocal;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.remote.TestManagerRemote;
import org.openelis.utils.ReferenceTableCache;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)

@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestManagerBean implements TestManagerRemote {

    @PersistenceContext(name = "openelis")
    
    @Resource
    private SessionContext ctx;
    
    @EJB private LockLocal lockBean;
    
    private static int testRefTableId;
    
    public TestManagerBean() {
        testRefTableId = ReferenceTableCache.getReferenceTable("test");
    }    

    public TestManager add(TestManager man) throws Exception {
        man.validate();
        
        UserTransaction ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();
        
        return man;
    }
    
    public TestManager update(TestManager man) throws Exception {
        man.validate();
        
        UserTransaction ut = ctx.getUserTransaction();
        ut.begin();
        man.update();
        ut.commit();
        
        return man;
    }

    public TestManager fetch(Integer testId) throws Exception {
        TestManager man = TestManager.findById(testId);
        
        return man;
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        TestManager man = TestManager.findByIdWithSampleTypes(testId);
        
        return man;
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        TestManager man = TestManager.findByIdWithAnalytesAndResults(testId);
        
        return man;
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        lockBean.getLock(testRefTableId, testId);
        
        return fetch(testId);
    }
    
    public TestManager abortUpdate(Integer testId) throws Exception {
        lockBean.giveUpLock(testRefTableId, testId);
        
        return fetch(testId);
    }

    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        TestTypeOfSampleManager ttsm = TestTypeOfSampleManager.findByTestId(testId);
        
        return ttsm;
    }

    public TestAnalyteManager fetchTestAnalytesByTestId(Integer testId) throws Exception {
        TestAnalyteManager tam = TestAnalyteManager.findByTestId(testId);
        
        return tam;
    }

    public TestResultManager fetchTestResultsByTestId(Integer testId) throws Exception {
        TestResultManager trm = TestResultManager.findByTestId(testId);
        
        return trm;
    }

}
