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
package org.openelis.modules.test1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.TestManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen Widget. GWT
 * RemoteServiceServlets that want to provide server side logic for Screens must
 * implement this interface.
 */
@RemoteServiceRelativePath("test1")
public interface TestServiceInt1 {

    public TestManager1 getInstance() throws Exception;

    public TestManager1 fetchById(Integer testId, TestManager1.Load... elements) throws Exception;

    public ArrayList<TestManager1> fetchByIds(ArrayList<Integer> testIds,
                                              TestManager1.Load... elements) throws Exception;

    public ArrayList<TestManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                TestManager1.Load... elements) throws Exception;

    public TestManager1 fetchWith(TestManager1 tm, TestManager1.Load... elements) throws Exception;

    public ArrayList<IdNameVO> query(Query query) throws Exception;

    public TestManager1 fetchForUpdate(Integer testId) throws Exception;

    public TestManager1 fetchForUpdate(Integer testId, TestManager1.Load... elements) throws Exception;

    public ArrayList<TestManager1> fetchForUpdate(ArrayList<Integer> testIds,
                                                  TestManager1.Load... elements) throws Exception;

    public TestManager1 unlock(Integer testId, TestManager1.Load... elements) throws Exception;

    public ArrayList<TestManager1> unlock(ArrayList<Integer> testIds,
                                          TestManager1.Load... elements) throws Exception;

    public TestManager1 duplicate(Integer id) throws Exception;

    public TestManager1 update(TestManager1 tm, boolean ignoreWarnings) throws Exception;

    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception;
}
