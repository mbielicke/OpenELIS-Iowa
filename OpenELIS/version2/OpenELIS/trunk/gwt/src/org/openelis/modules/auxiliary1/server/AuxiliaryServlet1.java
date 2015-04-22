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

package org.openelis.modules.auxiliary1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AuxDataBean;
import org.openelis.bean.AuxFieldBean;
import org.openelis.bean.AuxFieldGroupBean;
import org.openelis.bean.AuxFieldGroupManager1Bean;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.modules.auxiliary1.client.AuxiliaryService1;
import org.openelis.ui.common.data.Query;

@WebServlet("/openelis/auxiliary1")
public class AuxiliaryServlet1 extends RemoteServlet implements AuxiliaryService1 {

    private static final long serialVersionUID = 1L;

    @EJB
    AuxFieldGroupBean         auxFieldGroup;

    @EJB
    AuxFieldBean              auxField;

    @EJB
    AuxDataBean               auxData;

    @EJB
    AuxFieldGroupManager1Bean auxFieldGroupManager1;

    public ArrayList<AuxFieldGroupDO> fetchActive() throws Exception {
        try {
            return auxFieldGroup.fetchActive();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<AuxFieldViewDO> fetchAll() throws Exception {
        try {
            return auxField.fetchAll();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<AuxFieldViewDO> fetchByAnalyteName(String search) throws Exception {
        try {
            return auxField.fetchByAnalyteName(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // manager methods
    public AuxFieldGroupManager1 fetchById(Integer id) throws Exception {
        try {
            return auxFieldGroupManager1.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AuxFieldGroupManager1> fetchByIds(ArrayList<Integer> ids) throws Exception {
        try {
            return auxFieldGroupManager1.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return auxFieldGroup.query(query.getFields(),
                                       query.getPage() * query.getRowsPerPage(),
                                       query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AuxFieldGroupManager1 add(AuxFieldGroupManager1 man) throws Exception {
        try {
            return auxFieldGroupManager1.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AuxFieldGroupManager1 update(AuxFieldGroupManager1 man) throws Exception {
        try {
            return auxFieldGroupManager1.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AuxFieldGroupManager1 fetchForUpdate(Integer id) throws Exception {
        try {
            return auxFieldGroupManager1.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AuxFieldGroupManager1 abortUpdate(Integer id) throws Exception {
        try {
            return auxFieldGroupManager1.unlock(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AuxFieldViewDO validateForDelete(AuxFieldViewDO data) throws Exception {
        try {
            auxData.validateForDelete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        return data;
    }
}