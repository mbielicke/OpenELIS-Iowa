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
package org.openelis.modules.analysis.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AnalysisBean;
import org.openelis.bean.AnalysisManagerBean;
import org.openelis.bean.AnalysisQAEventManagerBean;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.modules.analysis.client.AnalysisServiceInt;

@WebServlet("/openelis/analysis")
public class AnalysisServlet extends RemoteServlet implements AnalysisServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB 
    AnalysisManagerBean        analysisManager;
    
    @EJB
    AnalysisBean               analysis;
    
    @EJB
    AnalysisQAEventManagerBean analysisQAEventManager;
    

    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        try {        
            return analysisManager.fetchBySampleItemId(sampleItemId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalysisViewDO fetchById(Integer analysisId) throws Exception {
        try {        
            return analysis.fetchById(analysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<AnalysisViewVO> fetchByPatientId(Integer patientId) throws Exception {
        try {
            return analysis.fetchByPatientId(patientId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // qa method
    public AnalysisQaEventManager fetchQaByAnalysisId(Integer analysisId) throws Exception {
        try {        
            return analysisQAEventManager.fetchByAnalysisId(analysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }


}
