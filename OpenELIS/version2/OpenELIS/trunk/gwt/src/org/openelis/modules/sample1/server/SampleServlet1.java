/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.modules.sample1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleBean;
import org.openelis.bean.SampleManager1Bean;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.modules.sample1.client.SampleServiceInt1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;

/*
 * This class provides service for SampleManager1
 */
@WebServlet("/openelis/sample1")
public class SampleServlet1 extends RemoteServlet implements SampleServiceInt1 {

    private static final long  serialVersionUID = 1L;
    
    @EJB
    private SampleBean sample;

    @EJB
    private SampleManager1Bean sampleManager1;

    public SampleManager1 getInstance(String domain) throws Exception {
        try {
            return sampleManager1.getInstance(domain);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 fetchById(Integer sampleId, SampleManager1.Load... elements) throws Exception {
        try {
            return sampleManager1.fetchById(sampleId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load... elements) throws Exception {
        try {
            return sampleManager1.fetchByIds(sampleIds, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception {
        try {
            return sampleManager1.fetchByQuery(fields, first, max, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        try {
            return sampleManager1.fetchByAnalyses(analysisIds, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {
        try {
            return sample.query(query.getFields(),
                               query.getPage() * query.getRowsPerPage(),
                               query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 fetchForUpdate(Integer sampleId, Load... elements) throws Exception {
        try {
            return sampleManager1.fetchForUpdate(sampleId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        try {
            return sampleManager1.fetchForUpdate(sampleIds, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 unlock(Integer sampleId, Load... elements) throws Exception {
        try {
            return sampleManager1.unlock(sampleId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

    }

    public ArrayList<SampleManager1> unlock(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        try {
            return sampleManager1.unlock(sampleIds, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        try {
            return sampleManager1.update(sm, ignoreWarnings);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void validateAccessionNumber(SampleManager1 sm) throws Exception {
        try {
            sampleManager1.validateAccessionNumber(sm);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 mergeQuickEntry(SampleManager1 sm) throws Exception {
        try {
            return sampleManager1.mergeQuickEntry(sm);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleTestReturnVO importOrder(SampleManager1 sm, Integer orderId) throws Exception {
        try {
            return sampleManager1.importOrder(sm, orderId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 duplicate(Integer sampleId) throws Exception {
        try {
            return sampleManager1.duplicate(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleTestReturnVO addAnalysis(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        try {
            return sampleManager1.addAnalysis(sm, test);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleTestReturnVO addAnalyses(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
        try {
            return sampleManager1.addAnalyses(sm, tests);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception {
        try {
            return sampleManager1.removeAnalysis(sm, analysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                                         ArrayList<TestAnalyteViewDO> analytes,
                                         ArrayList<Integer> indexes) throws Exception {
        try {
            return sampleManager1.addRowAnalytes(sm, analysis, analytes, indexes);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleTestReturnVO changeAnalysisMethod(SampleManager1 sm, Integer analysisId,
                                                   Integer methodId) throws Exception {
        try {
            return sampleManager1.changeAnalysisMethod(sm, analysisId, methodId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId,
                                               Integer methodId) throws Exception {
        try {
            return sampleManager1.changeAnalysisStatus(sm, analysisId, methodId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception {
        try {
            return sampleManager1.changeAnalysisUnit(sm, analysisId, unitId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 changeAnalysisPrep(SampleManager1 sm, Integer analysisId,
                                             Integer preAnalysisId) throws Exception {
        try {
            return sampleManager1.changeAnalysisPrep(sm, analysisId, preAnalysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleTestReturnVO addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        try {
            return sampleManager1.addAuxGroups(sm, groupIds);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager1 removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        try {
            return sampleManager1.removeAuxGroups(sm, groupIds);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}