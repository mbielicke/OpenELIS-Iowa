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
package org.openelis.modules.editNote.server;

import java.util.ArrayList;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.rewrite.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;

public class EditNoteService {

    public Query<StandardNoteDO> query(Query<StandardNoteDO> query) throws RPCException {

        StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");

        try{    
            query.results = new ArrayList<StandardNoteDO>();
            ArrayList<StandardNoteDO> results = (ArrayList<StandardNoteDO>)remote.newQuery(query.fields);
            for(StandardNoteDO result : results) {
                query.results.add(result);
            }
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        return query;
    }
    
    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/editNote.xsl");      
    }
}
