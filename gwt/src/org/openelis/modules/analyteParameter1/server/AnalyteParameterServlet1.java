/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.analyteParameter1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AnalyteParameterBean;
import org.openelis.bean.AnalyteParameterManager1Bean;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.modules.analyteParameter1.client.AnalyteParameterServiceInt1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/analyteParameter1")
public class AnalyteParameterServlet1 extends RemoteServlet implements AnalyteParameterServiceInt1 {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private AnalyteParameterManager1Bean analyteParameterManager;
    
    @EJB
    private AnalyteParameterBean        analyteParameter;
    
    public AnalyteParameterManager1 getInstance(Integer referenceId, Integer referenceTableId) throws Exception {
        try {
            return analyteParameterManager.getInstance(referenceId, referenceTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager1 fetchByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        try {
            return analyteParameterManager.fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception {
        try {
            return analyteParameter.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager1 fetchForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        try {
            return analyteParameterManager.fetchForUpdate(referenceId, referenceTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager1 update(AnalyteParameterManager1 apm) throws Exception {
        try {
            return analyteParameterManager.update(apm);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager1 unlock(Integer referenceId, Integer referenceTableId) throws Exception {
        try {
            return analyteParameterManager.unlock(referenceId, referenceTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}