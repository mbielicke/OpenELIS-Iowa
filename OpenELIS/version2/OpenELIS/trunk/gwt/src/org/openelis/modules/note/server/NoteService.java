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
package org.openelis.modules.note.server;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.NoteManager;
import org.openelis.meta.NoteMeta;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.NoteManagerRemote;

public class NoteService {

    public NoteManager fetchByRefTableRefId(Query query) throws Exception {
        Integer refTableId = null, refId = null;

        // parse the query to find refTableId and refId
        for (QueryData field : query.getFields()) {
            if (field.key != null) {
                if (NoteMeta.getReferenceId().equals(field.key))
                    refId = Integer.valueOf(field.query);
                else if (NoteMeta.getReferenceTableId().equals(field.key))
                    refTableId = Integer.valueOf(field.query);
            }
        }

        return remote().fetchByRefTableRefId(refTableId, refId);
    }
    
    private NoteManagerRemote remote() {
        return (NoteManagerRemote)EJBFactory.lookup("openelis/NoteManagerBean/remote");
    }
}
