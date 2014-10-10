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
package org.openelis.portal.modules.sampleStatus.server;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.portal.modules.sampleStatus.client.SampleStatusServiceInt;
import org.openelis.stfu.bean.SampleStatusPortalBean;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/portal/sampleStatus")
public class SampleStatusServlet extends RemoteServlet implements SampleStatusServiceInt {

    private static final long      serialVersionUID = 1L;

    @EJB
    private SampleStatusPortalBean sampleStatusPortal;

    public ArrayList<SampleViewVO> getSampleListForSampleStatusReport(Query query) throws Exception {
        try {
            return sampleStatusPortal.getSampleListForSampleStatusReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        try {
            return sampleStatusPortal.getProjectList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public HashMap<Integer, ArrayList<String>> getSampleQaEvents(ArrayList<Integer> sampleIds) throws Exception {
        return sampleStatusPortal.getSampleQaEvents(sampleIds);
    }

    @Override
    public HashMap<Integer, ArrayList<String>> getAnalysisQaEvents(ArrayList<Integer> analysisIds) throws Exception {
        return sampleStatusPortal.getAnalysisQaEvents(analysisIds);
    }

}