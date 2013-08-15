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
package org.openelis.modules.provider.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ProviderBean;
import org.openelis.bean.ProviderManagerBean;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;
import org.openelis.modules.provider.client.ProviderServiceInt;

@WebServlet("/openelis/provider")
public class ProviderServlet extends RemoteServlet implements ProviderServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    ProviderManagerBean providerManager;
    
    @EJB
    ProviderBean        provider;

    public ProviderManager fetchById(Integer id) throws Exception {
        try {        
            return providerManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        try {        
            return providerManager.fetchWithLocations(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        try {        
            return providerManager.fetchWithNotes(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdFirstLastNameVO> query(Query query) throws Exception {
        try {        
            return provider.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        try {        
            return providerManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        try {        
            return providerManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return providerManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        try {        
            return providerManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    //
    // support for ProviderContactManager and ProviderParameterManager
    //
    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception {
        try {        
            return providerManager.fetchLocationByProviderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
