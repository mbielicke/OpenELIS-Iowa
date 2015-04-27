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
package org.openelis.modules.standardnote.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.StandardNoteBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.modules.standardnote.client.StandardNoteServiceInt;

@WebServlet("/openelis/standardNote")
public class StandardNoteServlet extends RemoteServlet implements StandardNoteServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    StandardNoteBean standardNote;

    public StandardNoteDO fetchById(Integer id) throws Exception {
        try {        
            return standardNote.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<StandardNoteDO> fetchByNameOrDescription(Query query) throws Exception {
        String name, description;

        name = null;
        description = null;
        for (QueryData field : query.getFields()) {
            if (field.getKey() != null) {
                if (StandardNoteMeta.getName().equals(field.getKey()))
                    name = field.getQuery();
                else if (StandardNoteMeta.getDescription().equals(field.getKey()))
                    description = field.getQuery();
            }
        }
        try {        
            return standardNote.fetchByNameOrDescription(name, description, 1000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public StandardNoteDO fetchBySystemVariableName(String name) throws Exception {        
        try {        
            return standardNote.fetchBySystemVariableName(name);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<StandardNoteDO> fetchByType(Integer typeId) throws Exception {
        try {        
            return standardNote.fetchByType(typeId, 1000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return standardNote.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StandardNoteDO add(StandardNoteDO data) throws Exception {
        try {        
            return standardNote.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StandardNoteDO update(StandardNoteDO data) throws Exception {
        try {        
            return standardNote.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StandardNoteDO fetchForUpdate(Integer id) throws Exception {
        try {        
            return standardNote.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void delete(StandardNoteDO data) throws Exception {
        try {        
            standardNote.delete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public StandardNoteDO abortUpdate(Integer id) throws Exception {
        try {        
            return standardNote.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
