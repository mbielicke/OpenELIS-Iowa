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
package org.openelis.modules.result.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AnalyteBean;
import org.openelis.bean.ResultManagerBean;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.modules.result.client.ResultServiceInt;

@WebServlet("/openelis/result")
public class ResultServlet extends RemoteServlet implements ResultServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    AnalyteBean       analyte;
    
    @EJB
    ResultManagerBean resultManager;
    
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        try{
            return resultManager.fetchByAnalysisIdForDisplay(analysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        try{
            return resultManager.fetchForUpdateWithAnalysisId(analysisId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AnalysisResultManager fetchByTestId(AnalysisDO anDO) throws Exception {
        try {
            return resultManager.fetchForUpdateWithTestId(anDO.getTestId(), anDO.getUnitOfMeasureId());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalysisResultManager fetchByTestIdForOrderImport(AnalysisDO anDO) throws Exception {
        try {
            return resultManager.fetchByTestIdForOrderImport(anDO.getTestId(), anDO.getUnitOfMeasureId());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalysisResultManager merge(AnalysisResultManager manager) throws Exception {
        try {
            return resultManager.merge(manager);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<AnalyteDO> getAliasList(Query query) throws Exception {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<QueryData> fields = query.getFields();
        
        for(int i=0; i<fields.size(); i++)
            ids.add(new Integer(fields.get(i).getQuery()));
        
        try {        
            return analyte.getAlias(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

}
