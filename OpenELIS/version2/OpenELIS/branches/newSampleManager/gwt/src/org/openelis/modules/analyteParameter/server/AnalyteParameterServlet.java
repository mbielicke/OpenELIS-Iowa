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
package org.openelis.modules.analyteParameter.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AnalyteParameterBean;
import org.openelis.bean.AnalyteParameterManagerBean;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.AnalyteParameterManager;
import org.openelis.modules.analyteParameter.client.AnalyteParameterServiceInt;

@WebServlet("/openelis/analyteParameter")
public class AnalyteParameterServlet extends RemoteServlet implements AnalyteParameterServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    AnalyteParameterManagerBean analyteParameterManager;
    
    @EJB
    AnalyteParameterBean        analyteParameter;

    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
        Integer refId, refTableId;
        
        fields = query.getFields();
        field = fields.get(0);
        if (field.getQuery() != null)
            refId = Integer.parseInt(field.getQuery());
        else
            refId = null; 
        field = fields.get(1);
        if (field.getQuery() != null)
            refTableId = Integer.parseInt(field.getQuery());
        else
            refTableId = null;
        try {
            return analyteParameterManager.fetchActiveByReferenceIdReferenceTableId(refId, refTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
        Integer anaId, refId, refTableId;
        
        fields = query.getFields();
        field = fields.get(0);
        if (field.getQuery() != null)
            anaId = Integer.parseInt(field.getQuery());
        else
            anaId = null;
        
        field = fields.get(1);
        if (field.getQuery() != null)
            refId = Integer.parseInt(field.getQuery());
        else
            refId = null;
        
        field = fields.get(2);
        if (field.getQuery() != null)
            refTableId = Integer.parseInt(field.getQuery());
        else
            refTableId = null;
        
        
        try {
            return analyteParameter.fetchByAnalyteIdReferenceIdReferenceTableId(anaId, refId, refTableId);
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
    
    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        try {
            return analyteParameterManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        try {
            return analyteParameterManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        try {
            return analyteParameterManager.fetchForUpdate(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        try {
            return analyteParameterManager.abortUpdate(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
