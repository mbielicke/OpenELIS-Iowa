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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.modules.analyteParameter.client.AnalyteParameterService;

public class AnalyteParameterManagerProxy {
    
    public AnalyteParameterManagerProxy() {
    }   
    
    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;
        
        query = new Query();
        fields = new ArrayList<QueryData>();
        
        field = new QueryData();
        if (referenceId == null)
            field.setQuery(null);
        else
            field.setQuery(referenceId.toString());
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);
            
        field = new QueryData();
        if (referenceTableId == null)            
            field.setQuery(null);
        else
            field.setQuery(referenceTableId.toString());            
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);
        query.setFields(fields);        
        
        return AnalyteParameterService.get().fetchActiveByReferenceIdReferenceTableId(query);
    }
    
    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        return AnalyteParameterService.get().add(man);
    }
    
    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        return AnalyteParameterService.get().update(man);
    }
    
    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        return AnalyteParameterService.get().fetchForUpdate(man);
    }
    
    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        return AnalyteParameterService.get().abortUpdate(man);
    }
    
    public void validate(AnalyteParameterManager man) throws Exception {

    }
    
}
