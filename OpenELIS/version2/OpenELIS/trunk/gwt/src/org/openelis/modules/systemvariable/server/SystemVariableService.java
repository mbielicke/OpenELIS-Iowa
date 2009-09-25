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

package org.openelis.modules.systemvariable.server;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class SystemVariableService {

    private static final int rowPP = 18;
    private UTFResource      openElisConstants = UTFResource.getBundle((String)SessionManager
                                                            .getSession().getAttribute("locale"));

    public Query<IdNameVO> query(Query<IdNameVO> query) throws Exception {
        try {
            query.setResults(remote().query(query.getFields(), query.getPage() * rowPP, rowPP));
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return query;
    }

    public SystemVariableDO fetchById(Integer id) throws Exception {
        return remote().fetchById(id);
    }

    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        return remote().add(data);
    }
    
    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        return remote().update(data);
    }
    
    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        return remote().fetchForUpdate(id);
    }
    
    public void delete(Integer id) throws Exception {
        remote().delete(id);
    }
    
    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        return remote().abortUpdate(id);
    }

    private SystemVariableRemote remote() {
        return (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
    }
}