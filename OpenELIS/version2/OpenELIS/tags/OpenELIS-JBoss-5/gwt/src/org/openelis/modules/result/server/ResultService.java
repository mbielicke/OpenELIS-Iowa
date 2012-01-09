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

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.ResultManagerRemote;

public class ResultService {
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        try{
            return remote().fetchByAnalysisIdForDisplay(analysisId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        try{
            return remote().fetchForUpdateWithAnalysisId(analysisId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public AnalysisResultManager fetchByTestId(AnalysisDO anDO) throws Exception {
        try {
            return remote().fetchForUpdateWithTestId(anDO.getTestId(), anDO.getUnitOfMeasureId());
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public AnalysisResultManager merge(AnalysisResultManager manager) throws Exception {
        try {
            return remote().merge(manager);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<AnalyteDO> getAliasList(Query query) throws Exception {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<QueryData> fields = query.getFields();
        
        for(int i=0; i<fields.size(); i++)
            ids.add(new Integer(fields.get(i).query));
        
        return analyteRemote().getAlias(ids);
    }

    private ResultManagerRemote remote() {
        return (ResultManagerRemote)EJBFactory.lookup("openelis/ResultManagerBean/remote");
    }
    
    private AnalyteRemote analyteRemote() {
        return (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    }
}
