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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen Widget. GWT
 * RemoteServiceServlets that want to provide server side logic for Screens must
 * implement this interface.
 */
@RemoteServiceRelativePath("sample1")
public interface SampleServiceInt1 extends RemoteService {
    public SampleManager1 getInstance(String domain) throws Exception;
    
    public SampleManager1 fetchById(Integer sampleId, SampleManager1.Load... elements) throws Exception;

    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load... elements) throws Exception;

    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception;
    
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception;
    
    public ArrayList<IdAccessionVO> query(Query query) throws Exception;

    public SampleManager1 fetchForUpdate(Integer sampleId, Load... elements) throws Exception;

    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds,
                                                    SampleManager1.Load... elements) throws Exception;

    public SampleManager1 unlock(Integer sampleId, Load... elements) throws Exception;

    public ArrayList<SampleManager1> unlock(ArrayList<Integer> sampleIds, Load... elements) throws Exception;

    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception;

    public SampleManager1 mergeQuickEntry(SampleManager1 sm, Integer accession) throws Exception;
    
    public SampleTestReturnVO setOrderId(SampleManager1 sm, Integer orderId) throws Exception;
    
    public SampleManager1 duplicate(Integer sampleId) throws Exception;
    
    public SampleTestReturnVO addTest(SampleManager1 sm, SampleTestRequestVO test) throws Exception;
    
    public SampleTestReturnVO addTests(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception;
    
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception;
    
    public SampleManager1 addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                                         ArrayList<TestAnalyteViewDO> analytes,
                                         ArrayList<Integer> indexes) throws Exception;
            
    public SampleTestReturnVO changeAnalysisMethod(SampleManager1 sm, Integer analysisId, Integer methodId) throws Exception;
    
    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId, Integer statusId) throws Exception ;
    
    public SampleManager1 changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception;
    
    public SampleManager1 changeAnalysisPrep(SampleManager1 sm, Integer analysisId,
                                             Integer preAnalysisId) throws Exception;
    
    public SampleTestReturnVO addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception;
    
    public SampleManager1 removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception;
}