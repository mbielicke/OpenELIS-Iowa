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

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.AnalyteParameterManager;
import org.openelis.server.EJBFactory;

public class AnalyteParameterService {
    
    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
        Integer refId, refTableId;
        
        fields = query.getFields();
        field = fields.get(0);
        if (field.query != null)
            refId = Integer.parseInt(field.query);
        else
            refId = null; 
        field = fields.get(1);
        if (field.query != null)
            refTableId = Integer.parseInt(field.query);
        else
            refTableId = null;
        return EJBFactory.getAnalyteParameterManager().fetchActiveByReferenceIdReferenceTableId(refId, refTableId);
    }
    
    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
        Integer anaId, refId, refTableId;
        
        fields = query.getFields();
        field = fields.get(0);
        if (field.query != null)
            anaId = Integer.parseInt(field.query);
        else
            anaId = null;
        
        field = fields.get(1);
        if (field.query != null)
            refId = Integer.parseInt(field.query);
        else
            refId = null;
        
        field = fields.get(2);
        if (field.query != null)
            refTableId = Integer.parseInt(field.query);
        else
            refTableId = null;
        
        
        return EJBFactory.getAnalyteParameter().fetchByAnalyteIdReferenceIdReferenceTableId(anaId, refId, refTableId);
    }
    
    public ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception {
        return EJBFactory.getAnalyteParameter().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        return EJBFactory.getAnalyteParameterManager().add(man);
    }
    
    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        return EJBFactory.getAnalyteParameterManager().update(man);
    }
    
    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        return EJBFactory.getAnalyteParameterManager().fetchForUpdate(man);
    }
    
    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        return EJBFactory.getAnalyteParameterManager().abortUpdate(man);
    }
}