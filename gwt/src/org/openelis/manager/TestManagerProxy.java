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
package org.openelis.manager;

import org.openelis.cache.UserCache;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.modules.test.client.TestService;

public class TestManagerProxy {


    public TestManagerProxy() {
    }

    public TestManager fetchById(Integer testId) throws Exception {
        return TestService.get().fetchById(testId);
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return TestService.get().fetchWithSampleTypes(testId);
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return TestService.get().fetchWithAnalytesAndResults(testId);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        return TestService.get().fetchWithPrepTestsSampleTypes(testId);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        return TestService.get().fetchWithPrepTestsAndReflexTests(testId);
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        return TestService.get().fetchWithWorksheet(testId);
    }

    public TestManager add(TestManager man) throws Exception {
        return TestService.get().add(man);
    }

    public TestManager update(TestManager man) throws Exception {
        return TestService.get().update(man);
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        return TestService.get().fetchForUpdate(testId);
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        return TestService.get().abortUpdate(testId);
    }

    public void validate(TestManager man) throws Exception {
    }

    protected SystemUserPermission getSystemUserPermission() {
        return UserCache.getPermission();       
    }
}
