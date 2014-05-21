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
package org.openelis.modules.method.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.MethodBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.method.client.MethodServiceInt;

@WebServlet("/openelis/method")
public class MethodServlet extends RemoteServlet implements MethodServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    MethodBean method;

    public ArrayList<MethodDO> fetchByName(String search) throws Exception {
        try {
            return method.fetchByName(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<MethodDO> fetchActiveByName(String search) throws Exception {
        try {
            return method.fetchActiveByName(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public MethodDO fetchById(Integer id) throws Exception {
        try {
            return method.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return method.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public MethodDO add(MethodDO data) throws Exception {
        try {
            return method.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public MethodDO update(MethodDO data) throws Exception {
        try {
            return method.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public MethodDO fetchForUpdate(Integer id) throws Exception {
        try {
            return method.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public MethodDO abortUpdate(Integer id) throws Exception {
        try {
            return method.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
