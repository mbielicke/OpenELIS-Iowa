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

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.services.ScreenService;
import org.openelis.meta.NoteMeta;

public class NoteManagerProxy {
    protected static final String SERVICE_URL = "org.openelis.modules.note.server.NoteService";
    protected ScreenService service;
    
    public NoteManagerProxy(){
        service = new ScreenService("controller?service="+SERVICE_URL);
    }
    
    public NoteManager fetchByRefTableRefId(Integer tableId, Integer id) throws Exception {
        Query query;
        QueryData field;
        
        query = new Query();

        field = new QueryData();
        field.key = NoteMeta.getReferenceId();
        field.type = QueryData.Type.INTEGER;
        field.query = id.toString();
        query.setFields(field);
        
        field = new QueryData();
        field.key = NoteMeta.getReferenceTableId();
        field.type = QueryData.Type.INTEGER;
        field.query = tableId.toString();
        query.setFields(field);

        return service.call("fetchByRefTableRefId", query);
    }
    
    public NoteManager fetchByRefTableRefIdIsExt(Integer tableId, Integer id, String isExternal) throws Exception {
        Query query;
        QueryData field;
        
        query = new Query();

        field = new QueryData();
        field.key = NoteMeta.getReferenceId();
        field.type = QueryData.Type.INTEGER;
        field.query = id.toString();
        query.setFields(field);
        
        field = new QueryData();
        field.key = NoteMeta.getReferenceTableId();
        field.type = QueryData.Type.INTEGER;
        field.query = tableId.toString();
        query.setFields(field);
        
        field = new QueryData();
        field.key = NoteMeta.getIsExternal();
        field.type = QueryData.Type.STRING;
        field.query = isExternal;
        query.setFields(field);

        return service.call("fetchByRefTableRefIdIsExt", query);
    }

    public NoteManager add(NoteManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public NoteManager update(NoteManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    
    public void validate(NoteManager man) throws Exception {
    }
}
