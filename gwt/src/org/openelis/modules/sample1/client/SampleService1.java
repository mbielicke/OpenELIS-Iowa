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

package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class SampleService1 implements SampleServiceInt1, SampleServiceInt1Async {

    /**
     * GWT.created service to make calls to the server
     */
    private SampleServiceInt1Async service;

    private static SampleService1  instance;

    public static SampleService1 get() {
        if (instance == null)
            instance = new SampleService1();

        return instance;
    }

    private SampleService1() {
        service = (SampleServiceInt1Async)GWT.create(SampleServiceInt1.class);
    }

    @Override
    public SampleManager1 getInstance(String domain) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.getInstance(domain, callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(String domain, AsyncCallback<SampleManager1> callback) throws Exception {
        service.getInstance(domain, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByIds(sampleIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByIds(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                           AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchByIds(sampleIds, elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByQuery(fields, first, max, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             SampleManager1.Load elements[],
                             AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchByQuery(fields, first, max, elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByAnalyses(analysisIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByAnalyses(ArrayList<Integer> analysisIds, Load elements[],
                                AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchByAnalyses(analysisIds, elements, callback);
    }

    @Override
    public SampleManager1 fetchForUpdate(Integer sampleId, SampleManager1.Load... elements) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.fetchForUpdate(sampleId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer sampleId, SampleManager1.Load elements[],
                               AsyncCallback<SampleManager1> callback) throws Exception {
        service.fetchForUpdate(sampleId, elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds,
                                                    SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchForUpdate(sampleIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                               AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchForUpdate(sampleIds, elements, callback);
    }

    @Override
    public void unlock(Integer sampleId, Load[] elements, AsyncCallback<SampleManager1> callback) throws Exception {
        service.unlock(sampleId, elements, callback);
    }

    @Override
    public SampleManager1 unlock(Integer sampleId, Load... elements) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.unlock(sampleId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(ArrayList<Integer> sampleIds, Load[] elements,
                       AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.unlock(sampleIds, elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> unlock(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.unlock(sampleIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.update(sm, ignoreWarnings, callback);
        return callback.getResult();
    }

    @Override
    public void update(SampleManager1 sm, boolean ignoreWarnings,
                       AsyncCallback<SampleManager1> callback) throws Exception {
        service.update(sm, ignoreWarnings, callback);
    }

    @Override
    public void setAccessionNumber(SampleManager1 sm, Integer accession,
                                   AsyncCallback<SampleManager1> callback) throws Exception {
        service.setAccessionNumber(sm, accession, callback);
    }

    @Override
    public SampleManager1 setAccessionNumber(SampleManager1 sm, Integer accession) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.setAccessionNumber(sm, accession, callback);
        return callback.getResult();
    }

    @Override
    public void setOrderId(SampleManager1 sm, Integer orderId,
                           AsyncCallback<SampleTestReturnVO> callback) throws Exception {
        service.setOrderId(sm, orderId, callback);

    }

    @Override
    public SampleTestReturnVO setOrderId(SampleManager1 sm, Integer orderId) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.setOrderId(sm, orderId, callback);
        return callback.getResult();
    }

    @Override
    public void addTest(SampleManager1 sm, SampleTestRequestVO test,
                        AsyncCallback<SampleTestReturnVO> callback) throws Exception {
        service.addTest(sm, test, callback);

    }

    @Override
    public SampleTestReturnVO addTest(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.addTest(sm, test, callback);
        return callback.getResult();
    }

    @Override
    public void addTests(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests,
                         AsyncCallback<SampleTestReturnVO> callback) throws Exception {
        service.addTests(sm, tests, callback);
    }

    @Override
    public SampleTestReturnVO addTests(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.addTests(sm, tests, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager1 addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                                         ArrayList<TestAnalyteViewDO> analytes,
                                         ArrayList<Integer> indexes) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.addRowAnalytes(sm, analysis, analytes, indexes, callback);
        return callback.getResult();
    }

    @Override
    public void addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                               ArrayList<TestAnalyteViewDO> analytes, ArrayList<Integer> indexes,
                               AsyncCallback<SampleManager1> callback) throws Exception {
        service.addRowAnalytes(sm, analysis, analytes, indexes, callback);
    }

    @Override
    public void addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                             AsyncCallback<SampleManager1> callback) throws Exception {
        service.addAuxGroups(sm, groupIds, callback);
    }

    @Override
    public SampleManager1 addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.addAuxGroups(sm, groupIds, callback);
        return callback.getResult();
    }

    @Override
    public void removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                                AsyncCallback<SampleManager1> callback) throws Exception {
        service.removeAuxGroups(sm, groupIds, callback);

    }

    @Override
    public SampleManager1 removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.removeAuxGroups(sm, groupIds, callback);
        return callback.getResult();
    }
}