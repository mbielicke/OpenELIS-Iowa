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

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AuxFieldGroupBean;
import org.openelis.bean.AuxFieldGroupManagerBean;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.modules.auxiliary.client.AuxiliaryServiceInt;

@WebServlet("/openelis/auxiliary")
public class AuxiliaryService extends RemoteServlet implements AuxiliaryServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    AuxFieldGroupBean auxFieldGroup;
    
    @EJB
    AuxFieldGroupManagerBean auxFieldGroupManager;
    
    public ArrayList<AuxFieldGroupDO> fetchActive() throws Exception {
        return auxFieldGroup.fetchActive();
    }

    // manager methods
    public AuxFieldGroupManager fetchGroupById(Integer id) throws Exception {
        return auxFieldGroupManager.fetchById(id);
    }

    public AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception {
        return auxFieldGroupManager.fetchByIdWithFields(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return auxFieldGroup.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        return auxFieldGroupManager.add(man);
    }

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        return auxFieldGroupManager.update(man);
    }

    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        return auxFieldGroupManager.fetchForUpdate(id);
    }

    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        return auxFieldGroupManager.abortUpdate(id);
    }

    // other managers
    public AuxFieldManager fetchFieldById(Integer id) throws Exception {
        return auxFieldGroupManager.fetchFieldById(id);
    }

    public AuxFieldManager fetchFieldByGroupId(Integer groupId) throws Exception {
        return auxFieldGroupManager.fetchFieldByGroupId(groupId);
    }

    public AuxFieldManager fetchFieldByGroupIdWithValues(Integer groupId) throws Exception {
        return auxFieldGroupManager.fetchFieldByGroupIdWithValues(groupId);
    }
    
    public AuxFieldValueManager fetchFieldValueByFieldId(Integer fieldId) throws Exception {
        return auxFieldGroupManager.fetchFieldValueByFieldId(fieldId);
    }
}