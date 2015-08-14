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
package org.openelis.modules.provider1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ProviderBean;
import org.openelis.bean.ProviderManager1Bean;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.modules.provider1.client.ProviderServiceInt1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/provider1")
public class ProviderServlet1 extends RemoteServlet implements ProviderServiceInt1 {

    private static final long    serialVersionUID = 1L;

    @EJB
    private ProviderManager1Bean providerManager;

    @EJB
    private ProviderBean         provider;

    @Override
    public ProviderManager1 getInstance() throws Exception {
        try {
            return providerManager.getInstance();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ProviderManager1 fetchById(Integer id) throws Exception {
        try {
            return providerManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<ProviderManager1> fetchByIds(ArrayList<Integer> ids) throws Exception {
        try {
            return providerManager.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<ProviderDO> fetchByLastNameNpiExternalId(String search) throws Exception {
        try {
            return provider.fetchByLastNameNpiExternalId(search + "%", 100);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<ProviderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        try {
            return providerManager.fetchByQuery(fields, first, max);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<IdFirstLastNameVO> query(Query query) throws Exception {
        try {
            return provider.query(query.getFields(),
                                  query.getPage() * query.getRowsPerPage(),
                                  query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ProviderManager1 fetchForUpdate(Integer providerId) throws Exception {
        try {
            return providerManager.fetchForUpdate(providerId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<ProviderManager1> fetchForUpdate(ArrayList<Integer> providerIds) throws Exception {
        try {
            return providerManager.fetchForUpdate(providerIds);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ProviderManager1 unlock(Integer providerId) throws Exception {

        try {
            return providerManager.unlock(providerId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<ProviderManager1> unlock(ArrayList<Integer> providerIds) throws Exception {
        try {
            return providerManager.unlock(providerIds);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ProviderManager1 update(ProviderManager1 man) throws Exception {
        try {
            return providerManager.update(man, false);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ProviderManager1 update(ProviderManager1 man, boolean ignoreWarnings) throws Exception {
        try {
            return providerManager.update(man, ignoreWarnings);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

}
