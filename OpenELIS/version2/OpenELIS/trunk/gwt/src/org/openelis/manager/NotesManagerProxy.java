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

import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.NotesManager;
import org.openelis.modules.note.client.NoteServiceParams;

public class NotesManagerProxy {
    protected static final String NOTE_SERVICE_URL = "org.openelis.modules.note.server.NoteService";
    protected ScreenService service;
    
    public NotesManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+NOTE_SERVICE_URL);
    }
    
    public NotesManager add(NotesManager man) throws Exception {
        throw new UnsupportedOperationException();
    }

    public NotesManager update(NotesManager man) throws Exception {
        throw new UnsupportedOperationException();
    }

    public NotesManager fetch(Integer tableId, Integer id) throws Exception {
        NoteServiceParams p = new NoteServiceParams();
        p.referenceId = id;
        p.referenceTableId = tableId;
        
        return service.call("fetch", p);
    }
}
