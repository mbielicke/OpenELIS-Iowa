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

package org.openelis.modules.systemvariable1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SystemVariableBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.modules.systemvariable1.client.SystemVariableService1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/systemvariable1")
public class SystemVariableServlet1 extends RemoteServlet implements SystemVariableService1 {

    private static final long serialVersionUID = 1L;

    @EJB
    SystemVariableBean        systemVariable;

    public SystemVariableDO fetchById(Integer id) throws Exception {
        try {
            return systemVariable.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SystemVariableDO> fetchByName(String name) throws Exception {
        try {
            return systemVariable.fetchByName(name + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SystemVariableDO fetchByExactName(String name) throws Exception {
        try {
            return systemVariable.fetchByName(name);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return systemVariable.query(query.getFields(),
                                        query.getPage() * query.getRowsPerPage(),
                                        query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        try {
            return systemVariable.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        try {
            return systemVariable.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        try {
            return systemVariable.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void delete(SystemVariableDO data) throws Exception {
        systemVariable.delete(data);
    }

    public SystemVariableDO unlock(Integer id) throws Exception {
        try {
            return systemVariable.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
