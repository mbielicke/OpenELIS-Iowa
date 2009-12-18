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
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.remote.TestManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
@TransactionManagement(TransactionManagementType.BEAN)
public class TestManagerBean implements TestManagerRemote {

    @PersistenceContext(name = "openelis")
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public TestManagerBean() {
    }

    public TestManager fetchById(Integer testId) throws Exception {
        return TestManager.fetchById(testId);
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return TestManager.fetchWithSampleTypes(testId);
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return TestManager.fetchWithAnalytesAndResults(testId);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        return TestManager.fetchWithPrepTestsSampleTypes(testId);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        return TestManager.fetchWithPrepTestAndReflexTests(testId);
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        return TestManager.fetchWithWorksheet(testId);
    }

    public TestManager add(TestManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();

        return man;
    }

    public TestManager update(TestManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        lockBean.validateLock(ReferenceTable.TEST, man.getTest().getId());
        man.update();
        lockBean.giveUpLock(ReferenceTable.TEST, man.getTest().getId());
        ut.commit();

        return man;
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        lockBean.getLock(ReferenceTable.TEST, testId);
        return fetchById(testId);
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        lockBean.giveUpLock(ReferenceTable.TEST, testId);
        return fetchById(testId);
    }

    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        return TestTypeOfSampleManager.fetchByTestId(testId);
    }

    public TestAnalyteManager fetchTestAnalytesByTestId(Integer testId) throws Exception {
        return TestAnalyteManager.fetchByTestId(testId);
    }

    public TestResultManager fetchTestResultsByTestId(Integer testId) throws Exception {
        return TestResultManager.fetchByTestId(testId);
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        return TestPrepManager.fetchByTestId(testId);
    }

    public TestReflexManager fetchReflexiveTestsByTestId(Integer testId) throws Exception {
        return TestReflexManager.fetchByTestId(testId);
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        return TestWorksheetManager.fetchByTestId(testId);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "test", flag);
    }

}
