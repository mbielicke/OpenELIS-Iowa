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
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.ui.common.data.Query;
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
    public void getInstance(String domain, AsyncCallback<SampleManager1> callback) {
        service.getInstance(domain, callback);
    }

    @Override
    public SampleManager1 fetchById(Integer sampleId, Load... elements) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.fetchById(sampleId, elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchById(Integer sampleId, Load[] elements, AsyncCallback<SampleManager1> callback) {
        service.fetchById(sampleId, elements, callback);
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
                           AsyncCallback<ArrayList<SampleManager1>> callback) {
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
                             AsyncCallback<ArrayList<SampleManager1>> callback) {
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
                                AsyncCallback<ArrayList<SampleManager1>> callback) {
        service.fetchByAnalyses(analysisIds, elements, callback);
    }
    
    @Override
    public SampleManager1 fetchByAccession(Integer accessionNum, Load... elements) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.fetchByAccession(accessionNum, elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByAccession(Integer accessionNum, Load[] elements, AsyncCallback<SampleManager1> callback) {
        service.fetchByAccession(accessionNum, elements, callback);
    }

    @Override
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {
        Callback<ArrayList<IdAccessionVO>> callback;

        callback = new Callback<ArrayList<IdAccessionVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdAccessionVO>> callback) {
        service.query(query, callback);
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
                               AsyncCallback<SampleManager1> callback) {
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
                               AsyncCallback<ArrayList<SampleManager1>> callback) {
        service.fetchForUpdate(sampleIds, elements, callback);
    }

    @Override
    public void unlock(Integer sampleId, Load[] elements, AsyncCallback<SampleManager1> callback) {
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
                       AsyncCallback<ArrayList<SampleManager1>> callback) {
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
                       AsyncCallback<SampleManager1> callback) {
        service.update(sm, ignoreWarnings, callback);
    }

    @Override
    public void validateAccessionNumber(SampleManager1 sm,
                                   AsyncCallback<Void> callback) {
        service.validateAccessionNumber(sm, callback);
    }

    @Override
    public void validateAccessionNumber(SampleManager1 sm) throws Exception {
        Callback<Void> callback;

        callback = new Callback<Void>();
        service.validateAccessionNumber(sm, callback);
    }
    
    @Override
    public void mergeQuickEntry(SampleManager1 sm,
                                   AsyncCallback<SampleManager1> callback) {
        service.mergeQuickEntry(sm, callback);
    }

    @Override
    public SampleManager1 mergeQuickEntry(SampleManager1 sm) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.mergeQuickEntry(sm, callback);
        return callback.getResult();
    }

    @Override
    public void importOrder(SampleManager1 sm, Integer orderId,
                           AsyncCallback<SampleTestReturnVO> callback) {
        service.importOrder(sm, orderId, callback);

    }

    @Override
    public SampleTestReturnVO importOrder(SampleManager1 sm, Integer orderId) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.importOrder(sm, orderId, callback);
        return callback.getResult();
    }

    @Override
    public void duplicate(Integer sampleId, AsyncCallback<SampleManager1> callback) {
        service.duplicate(sampleId, callback);
    }

    @Override
    public SampleManager1 duplicate(Integer sampleId) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.duplicate(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public void addAnalysis(SampleManager1 sm, SampleTestRequestVO test,
                        AsyncCallback<SampleTestReturnVO> callback) {
        service.addAnalysis(sm, test, callback);

    }

    @Override
    public SampleTestReturnVO addAnalysis(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.addAnalysis(sm, test, callback);
        return callback.getResult();
    }

    @Override
    public void addAnalyses(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests,
                         AsyncCallback<SampleTestReturnVO> callback) {
        service.addAnalyses(sm, tests, callback);
    }

    @Override
    public SampleTestReturnVO addAnalyses(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.addAnalyses(sm, tests, callback);
        return callback.getResult();
    }
    

    @Override
    public void removeAnalysis(SampleManager1 sm, Integer analysisId,
                               AsyncCallback<SampleManager1> callback) {
        service.removeAnalysis(sm, analysisId, callback);
    }

    @Override
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.removeAnalysis(sm, analysisId, callback);
        return callback.getResult();
    }

    @Override
    public void addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                               ArrayList<TestAnalyteViewDO> analytes, ArrayList<Integer> indexes,
                               AsyncCallback<SampleManager1> callback) {
        service.addRowAnalytes(sm, analysis, analytes, indexes, callback);
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
    public void changeAnalysisMethod(SampleManager1 sm, Integer analysisId, Integer methodId,
                                       AsyncCallback<SampleTestReturnVO> callback) {
        service.changeAnalysisMethod(sm, analysisId, methodId, callback);
    }

    @Override
    public SampleTestReturnVO changeAnalysisMethod(SampleManager1 sm, Integer analysisId, Integer methodId) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.changeAnalysisMethod(sm, analysisId, methodId, callback);
        return callback.getResult();
    }
    
    @Override
    public void changeAnalysisStatus(SampleManager1 sm, Integer analysisId, Integer statusId,
                                       AsyncCallback<SampleManager1> callback) {
        service.changeAnalysisStatus(sm, analysisId, statusId, callback);
    }

    @Override
    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId, Integer statusId) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.changeAnalysisStatus(sm, analysisId, statusId, callback);
        return callback.getResult();
    }
    
    
    @Override
    public void changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId,
                                       AsyncCallback<SampleManager1> callback) {
        service.changeAnalysisUnit(sm, analysisId, unitId, callback);
    }

    @Override
    public SampleManager1 changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.changeAnalysisUnit(sm, analysisId, unitId, callback);
        return callback.getResult();
    }

    @Override
    public void changeAnalysisPrep(SampleManager1 sm, Integer analysisId, Integer preAnalysisId,
                                   AsyncCallback<SampleManager1> callback) {
        service.changeAnalysisPrep(sm, analysisId, preAnalysisId, callback);
    }

    @Override
    public SampleManager1 changeAnalysisPrep(SampleManager1 sm, Integer analysisId,
                                             Integer preAnalysisId) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.changeAnalysisPrep(sm, analysisId, preAnalysisId, callback);
        return callback.getResult();
    }
    

    @Override
    public void addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                             AsyncCallback<SampleTestReturnVO> callback) {
        service.addAuxGroups(sm, groupIds, callback);
    }

    @Override
    public SampleTestReturnVO addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        Callback<SampleTestReturnVO> callback;

        callback = new Callback<SampleTestReturnVO>();
        service.addAuxGroups(sm, groupIds, callback);
        return callback.getResult();
    }

    @Override
    public void removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                                AsyncCallback<SampleManager1> callback) {
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