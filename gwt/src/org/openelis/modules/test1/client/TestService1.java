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
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.TestManager1;
import org.openelis.manager.TestManager1.Load;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class TestService1 implements TestServiceInt1, TestServiceInt1Async {
    /**
     * GWT.created service to make calls to the server
     */
    private TestServiceInt1Async service;

    private static TestService1  instance;

    public static TestService1 get() {
        if (instance == null)
            instance = new TestService1();

        return instance;
    }

    private TestService1() {
        service = (TestServiceInt1Async)GWT.create(TestServiceInt1.class);
    }

    @Override
    public TestManager1 getInstance() throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.getInstance(callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(AsyncCallback<TestManager1> callback) {
        service.getInstance(callback);
    }

    @Override
    public TestManager1 fetchById(Integer testId, Load... elements) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.fetchById(testId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer testId, Load[] elements, AsyncCallback<TestManager1> callback) {
        service.fetchById(testId, elements, callback);
    }

    @Override
    public void fetchByIds(ArrayList<Integer> testIds, Load[] elements,
                           AsyncCallback<ArrayList<TestManager1>> callback) {
        service.fetchByIds(testIds, elements, callback);
    }

    @Override
    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max, Load[] elements,
                             AsyncCallback<ArrayList<TestManager1>> callback) {
        service.fetchByQuery(fields, first, max, elements, callback);
    }

    @Override
    public void fetchWith(TestManager1 tm, Load[] elements, AsyncCallback<TestManager1> callback) {
        service.fetchWith(tm, elements, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void fetchForUpdate(Integer testId, AsyncCallback<TestManager1> callback) {
        service.fetchForUpdate(testId, callback);
    }

    @Override
    public void fetchForUpdate(Integer testId, Load[] elements, AsyncCallback<TestManager1> callback) {
        service.fetchForUpdate(testId, elements, callback);
    }

    @Override
    public void fetchForUpdate(ArrayList<Integer> testIds, Load[] elements,
                               AsyncCallback<ArrayList<TestManager1>> callback) {
        service.fetchForUpdate(testIds, elements, callback);
    }

    @Override
    public void unlock(Integer testId, Load[] elements, AsyncCallback<TestManager1> callback) {
        service.unlock(testId, elements, callback);
    }

    @Override
    public void unlock(ArrayList<Integer> testIds, Load[] elements,
                       AsyncCallback<ArrayList<TestManager1>> callback) {
        service.unlock(testIds, elements, callback);
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<TestManager1> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public void update(TestManager1 tm, boolean ignoreWarnings, AsyncCallback<TestManager1> callback) {
        service.update(tm, ignoreWarnings, callback);

    }

    @Override
    public ArrayList<TestManager1> fetchByIds(ArrayList<Integer> testIds, Load... elements) throws Exception {
        Callback<ArrayList<TestManager1>> callback;

        callback = new Callback<ArrayList<TestManager1>>();
        service.fetchByIds(testIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                Load... elements) throws Exception {
        Callback<ArrayList<TestManager1>> callback;

        callback = new Callback<ArrayList<TestManager1>>();
        service.fetchByQuery(fields, first, max, elements, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 fetchWith(TestManager1 tm, Load... elements) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.fetchWith(tm, elements, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 fetchForUpdate(Integer testId) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.fetchForUpdate(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 fetchForUpdate(Integer testId, Load... elements) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.fetchForUpdate(testId, elements, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestManager1> fetchForUpdate(ArrayList<Integer> testIds, Load... elements) throws Exception {
        Callback<ArrayList<TestManager1>> callback;

        callback = new Callback<ArrayList<TestManager1>>();
        service.fetchForUpdate(testIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 unlock(Integer testId, Load... elements) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.unlock(testId, elements, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestManager1> unlock(ArrayList<Integer> testIds, Load... elements) throws Exception {
        Callback<ArrayList<TestManager1>> callback;

        callback = new Callback<ArrayList<TestManager1>>();
        service.unlock(testIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 duplicate(Integer id) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.duplicate(id, callback);
        return callback.getResult();
    }

    @Override
    public TestManager1 update(TestManager1 tm, boolean ignoreWarnings) throws Exception {
        Callback<TestManager1> callback;

        callback = new Callback<TestManager1>();
        service.update(tm, ignoreWarnings, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;

        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchByName(name, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByName(String name, AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchByName(name, callback);
    }
}
