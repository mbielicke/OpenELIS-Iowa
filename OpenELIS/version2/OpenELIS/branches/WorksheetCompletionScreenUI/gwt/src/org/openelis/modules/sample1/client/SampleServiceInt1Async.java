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
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ScreenServiceIntAsync is the Asynchronous version of the ScreenServiceInt
 * interface.
 */
public interface SampleServiceInt1Async {
    public void getInstance(String domain, AsyncCallback<SampleManager1> callback);
    
    public void fetchById(Integer sampleId, SampleManager1.Load elements[], AsyncCallback<SampleManager1> callback);

    public void fetchByIds(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                           AsyncCallback<ArrayList<SampleManager1>> callback);

    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             SampleManager1.Load elements[],
                             AsyncCallback<ArrayList<SampleManager1>> callback);
    
    public void fetchByAnalyses(ArrayList<Integer> analysisIds, SampleManager1.Load elements[],
                                AsyncCallback<ArrayList<SampleManager1>> callback);
    
    public void fetchByAccession(Integer accessionNum, SampleManager1.Load elements[], AsyncCallback<SampleManager1> callback);

    public void query(Query query, AsyncCallback<ArrayList<IdAccessionVO>> callback);
    
    public void fetchForUpdate(Integer sampleId, SampleManager1.Load elements[],
                               AsyncCallback<SampleManager1> callback);

    public void fetchForUpdate(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                               AsyncCallback<ArrayList<SampleManager1>> callback);

    public void unlock(Integer sampleId, Load elements[], AsyncCallback<SampleManager1> callback);

    public void unlock(ArrayList<Integer> sampleIds, Load elements[],
                       AsyncCallback<ArrayList<SampleManager1>> callback);

    public void update(SampleManager1 sm, boolean ignoreWarnings,
                       AsyncCallback<SampleManager1> callback);

    public void update(ArrayList<SampleManager1> sms, boolean ignoreWarnings,
                       AsyncCallback<ArrayList<SampleManager1>> callback);

    public void validateAccessionNumber(SampleManager1 sm,
                                AsyncCallback<Void> callback);

    public void mergeQuickEntry(SampleManager1 sm,
                                   AsyncCallback<SampleManager1> callback);

    public void importOrder(SampleManager1 sm, Integer orderId,
                           AsyncCallback<SampleTestReturnVO> callback);
    
    public void duplicate(Integer sampleId, AsyncCallback<SampleManager1> callback);

    public void addAnalysis(SampleManager1 sm, SampleTestRequestVO test,
                        AsyncCallback<SampleTestReturnVO> callback);

    public void addAnalyses(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests,
                         AsyncCallback<SampleTestReturnVO> callback);
    
    public void removeAnalysis(SampleManager1 sm, Integer analysisId,
                               AsyncCallback<SampleManager1> callback);

    public void addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                               ArrayList<TestAnalyteViewDO> analytes, ArrayList<Integer> indexes,
                               AsyncCallback<SampleManager1> callback);

    public void changeAnalysisMethod(SampleManager1 sm, Integer analysisId, Integer unitId,
                                     AsyncCallback<SampleTestReturnVO> callback);
    
    public void changeAnalysisStatus(SampleManager1 sm, Integer analysisId, Integer statusId,
                                     AsyncCallback<SampleManager1> callback);

    public void changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId,
                                AsyncCallback<SampleManager1> callback);

    public void changeAnalysisPrep(SampleManager1 sm, Integer analysisId, Integer preAnalysisId,
                            AsyncCallback<SampleManager1> callback);
    
    public void addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                             AsyncCallback<SampleTestReturnVO> callback);

    public void removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds,
                                AsyncCallback<SampleManager1> callback);
}