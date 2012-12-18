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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.server.EJBFactory;

public class StandardNoteService {
    public StandardNoteDO fetchById(Integer id) throws Exception {
        return EJBFactory.getStandardNote().fetchById(id);
    }

    public ArrayList<StandardNoteDO> fetchByNameOrDescription(Query query) throws Exception {
        String name, description;

        name = null;
        description = null;
        for (QueryData field : query.getFields()) {
            if (field.key != null) {
                if (StandardNoteMeta.getName().equals(field.key))
                    name = field.query;
                else if (StandardNoteMeta.getDescription().equals(field.key))
                    description = field.query;
            }
        }
        return EJBFactory.getStandardNote().fetchByNameOrDescription(name, description, 1000);
    }
    
    public StandardNoteDO fetchBySystemVariableName(String name) throws Exception {        
        return EJBFactory.getStandardNote().fetchBySystemVariableName(name);
    }

    public ArrayList<StandardNoteDO> fetchByType(Integer typeId) throws Exception {
        return EJBFactory.getStandardNote().fetchByType(typeId, 1000);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getStandardNote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public StandardNoteDO add(StandardNoteDO data) throws Exception {
        return EJBFactory.getStandardNote().add(data);
    }

    public StandardNoteDO update(StandardNoteDO data) throws Exception {
        return EJBFactory.getStandardNote().update(data);
    }

    public StandardNoteDO fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getStandardNote().fetchForUpdate(id);
    }

    public void delete(StandardNoteDO data) throws Exception {
        EJBFactory.getStandardNote().delete(data);
    }

    public StandardNoteDO abortUpdate(Integer id) throws Exception {
        return EJBFactory.getStandardNote().abortUpdate(id);
    }
}
