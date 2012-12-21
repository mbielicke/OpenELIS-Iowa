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

package org.openelis.modules.systemvariable.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SystemVariableBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.modules.systemvariable.client.SystemVariableServiceInt;

@WebServlet("/openelis/systemVariable")
public class SystemVariableServlet extends AppServlet implements SystemVariableServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SystemVariableBean systemVariable;

    public SystemVariableDO fetchById(Integer id) throws Exception {
        return systemVariable.fetchById(id);
    }

    public ArrayList<SystemVariableDO> fetchByName(String name) throws Exception {
        return systemVariable.fetchByName(name + "%", 10);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return systemVariable.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        return systemVariable.add(data);
    }

    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        return systemVariable.update(data);
    }

    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        return systemVariable.fetchForUpdate(id);
    }

    public void delete(SystemVariableDO data) throws Exception {
        systemVariable.delete(data);
    }

    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        return systemVariable.abortUpdate(id);
    }
}