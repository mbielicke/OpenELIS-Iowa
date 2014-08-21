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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ScreenServiceIntAsync is the Asynchronous version of the ScreenServiceInt
 * interface.
 */
public interface TestServiceInt1Async {

    public void getInstance(AsyncCallback<TestManager1> callback);

    public void fetchById(Integer testId, TestManager1.Load elements[],
                          AsyncCallback<TestManager1> callback);

    public void fetchByIds(ArrayList<Integer> testIds, TestManager1.Load elements[],
                           AsyncCallback<ArrayList<TestManager1>> callback);

    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             TestManager1.Load elements[],
                             AsyncCallback<ArrayList<TestManager1>> callback);

    public void fetchWith(TestManager1 tm, TestManager1.Load elements[],
                          AsyncCallback<TestManager1> callback);

    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    public void fetchForUpdate(Integer testId, AsyncCallback<TestManager1> callback);

    public void fetchForUpdate(Integer testId, TestManager1.Load elements[],
                               AsyncCallback<TestManager1> callback);

    public void fetchForUpdate(ArrayList<Integer> testIds, TestManager1.Load elements[],
                               AsyncCallback<ArrayList<TestManager1>> callback);

    public void unlock(Integer testId, TestManager1.Load elements[],
                       AsyncCallback<TestManager1> callback);

    public void unlock(ArrayList<Integer> testIds, TestManager1.Load elements[],
                       AsyncCallback<ArrayList<TestManager1>> callback);

    public void duplicate(Integer id, AsyncCallback<TestManager1> callback);

    public void update(TestManager1 tm, boolean ignoreWarnings, AsyncCallback<TestManager1> callback);

    public void fetchByName(String name, AsyncCallback<ArrayList<TestMethodVO>> callback);
}
