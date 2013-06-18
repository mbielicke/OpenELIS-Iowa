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

import org.openelis.bean.SampleManager1Bean;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.modules.sample1.client.SampleServiceInt1;
import org.openelis.ui.common.data.QueryData;

@WebServlet("/openelis/sample1")
public class SampleServlet1 extends RemoteServlet implements SampleServiceInt1 {

    private static final long serialVersionUID = 1L;
    
    @EJB
    private SampleManager1Bean sampleManager1;
    
    public SampleManager1 getInstance(String domain) throws Exception {
        return sampleManager1.getInstance(domain);
    }

    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load ...elements) throws Exception {
        return sampleManager1.fetchByIds(sampleIds, elements);
    }
    
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception {
        return sampleManager1.fetchByQuery(fields, first, max, elements);
    }
    
    public SampleManager1 fetchForUpdate(Integer sampleId, Load... elements) throws Exception {
        return sampleManager1.fetchForUpdate(sampleId, elements);
    }
    
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        return sampleManager1.fetchForUpdate(sampleIds, elements);
    }
    
    public SampleManager1 unlock(Integer sampleId, Load... elements) throws Exception {
        return sampleManager1.unlock(sampleId, elements);
    }
    
    public ArrayList<SampleManager1> unlock(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        return sampleManager1.unlock(sampleIds, elements);
    }
    
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        return sampleManager1.fetchByAnalyses(analysisIds, elements);
    }
    
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        return sampleManager1.update(sm, ignoreWarnings);
    }

    public SampleManager1 setAccessionNumber(SampleManager1 sm, Integer accession) throws Exception {
        return sampleManager1.setAccessionNumber(sm, accession);
    }
    
    public SampleTestReturnVO setOrderId(SampleManager1 sm, Integer orderId) throws Exception {
        return sampleManager1.setOrderId(sm, orderId);
    }
    
    public SampleTestReturnVO addTest(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        return sampleManager1.addTest(sm, test);
    }
    
    public SampleTestReturnVO addTests(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
        return sampleManager1.addTests(sm, tests);
    }
    
    public SampleManager1 addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        return sampleManager1.addAuxGroups(sm, groupIds);
    }
}