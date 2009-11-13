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

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;

public class SystemVariableService {

    private static final int rowPP = 9;

    public SystemVariableDO fetchById(Integer id) throws Exception {
        try {
            return remote().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        try {
            return remote().add(data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        try {
            return remote().update(data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        try {
            return remote().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public void delete(SystemVariableDO data) throws Exception {
        try {
            remote().delete(data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        try {
            return remote().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    private SystemVariableRemote remote() {
        return (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
    }
}