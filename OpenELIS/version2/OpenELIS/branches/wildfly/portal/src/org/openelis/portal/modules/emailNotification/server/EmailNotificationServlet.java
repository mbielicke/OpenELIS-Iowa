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
package org.openelis.portal.modules.emailNotification.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.OrganizationBean;
import org.openelis.bean.OrganizationManagerBean;
import org.openelis.bean.OrganizationParameterBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.portal.modules.emailNotification.client.EmailNotificationServiceInt;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelisweb/emailNotification")
public class EmailNotificationServlet extends RemoteServlet implements EmailNotificationServiceInt {

    private static final long         serialVersionUID = 1L;

    @EJB
    private OrganizationManagerBean   organizationManager;

    @EJB
    private OrganizationBean          organization;

    @EJB
    private OrganizationParameterBean organizationParameter;

    public ArrayList<OrganizationViewDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        try {
            return organization.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception {
        int id;
        ArrayList<OrganizationDO> list;

        try {
            id = Integer.parseInt(search);
            list = new ArrayList<OrganizationDO>(1);
            list.add(organization.fetchActiveById(id));
        } catch (NumberFormatException e) {
            list = organization.fetchActiveByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<OrganizationDO>(0);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        return list;
    }

    public ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception {
        try {
            return organizationParameter.fetchByDictionarySystemName(systemName);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<OrganizationParameterDO> fetchParametersByOrganizationId(Integer id) throws Exception {
        try {
            return organizationParameter.fetchByOrganizationId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return organization.query(query.getFields(),
                                      query.getPage() * query.getRowsPerPage(),
                                      query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<OrganizationParameterDO> updateForNotify(ArrayList<OrganizationParameterDO> parameters) throws Exception {
        try {
            return organizationManager.updateForNotify(parameters);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}