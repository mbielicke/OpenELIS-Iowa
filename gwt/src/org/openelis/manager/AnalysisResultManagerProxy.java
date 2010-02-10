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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.services.ScreenService;

public class AnalysisResultManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.result.server.ResultService";
    protected ScreenService       service;

    public AnalysisResultManagerProxy() {
        service = new ScreenService("OpenELISServlet?service=" + SAMPLE_SERVICE_URL);
    }

    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        return service.call("fetchByAnalysisIdForDisplay", analysisId);
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId) throws Exception {
        AnalysisDO anDO;
        
        anDO = new AnalysisDO();
        anDO.setTestId(testId);
        anDO.setId(analysisId);

        return service.call("fetchByAnalysisId", anDO);
    }

    public AnalysisResultManager fetchByTestId(Integer testId) throws Exception {
        return service.call("fetchByTestId", testId);
    }

    public AnalysisResultManager add(AnalysisResultManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public AnalysisResultManager update(AnalysisResultManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ArrayList<AnalyteDO> getAliasList(ArrayList<TestAnalyteViewDO> analytes) throws Exception {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;
        
        
        fields = new ArrayList<QueryData>();
        query = new Query();
        
        for(int i=0; i<analytes.size(); i++){
            field = new QueryData();
            field.query = analytes.get(i).getAnalyteId().toString();
            fields.add(field);
        }
        
        query.setFields(fields);
        return service.callList("getAliasList", query);
    }
    
    public void validate(AnalysisResultManager man, ValidationErrorsList errorsList)
                                                                                    throws Exception {

    }
}
