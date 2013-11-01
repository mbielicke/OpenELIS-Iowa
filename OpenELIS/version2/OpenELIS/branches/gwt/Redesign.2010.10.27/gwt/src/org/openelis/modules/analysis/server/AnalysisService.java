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

import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalysisManagerRemote;
import org.openelis.remote.AnalysisQAEventManagerRemote;
import org.openelis.remote.PanelRemote;

public class AnalysisService {
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return remote().fetchBySampleItemId(sampleItemId);
    }

    // qa method
    public AnalysisQaEventManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return qaRemote().fetchByAnalysisId(analysisId);
    }

    public ArrayList<TestMethodVO> getTestMethodMatches(Query query) throws Exception {
        ArrayList<TestMethodVO> resultList;
        if(query.getFields().size() > 1)
            resultList = panelRemote().fetchByNameSampleTypeWithTests(query.getFields().get(0).getQuery()+"%", new Integer(query.getFields().get(1).getQuery()), 10);
        else
            resultList = panelRemote().fetchByNameWithTests(query.getFields().get(0).getQuery()+"%", 10);
        return resultList;
    }

    private AnalysisManagerRemote remote() {
        return (AnalysisManagerRemote)EJBFactory.lookup("openelis/AnalysisManagerBean/remote");
    }

    private AnalysisQAEventManagerRemote qaRemote() {
        return (AnalysisQAEventManagerRemote)EJBFactory.lookup("openelis/AnalysisQAEventManagerBean/remote");
    }

    private PanelRemote panelRemote() {
        return (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
    }
}