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
package org.openelis.modules.provider.server;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ProviderManagerRemote;
import org.openelis.remote.ProviderRemote;


public class ProviderService {
    
    private static final int rowPP = 18;

    public ProviderManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithLocations(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithNotes(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdFirstLastNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }

    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }


    //
    // support for ProviderContactManager and ProviderParameterManager
    //
    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception {
        try {
            return remoteManager().fetchLocationByProviderId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    private ProviderRemote remote() {
        return (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
    }

    private ProviderManagerRemote remoteManager() {
        return (ProviderManagerRemote)EJBFactory.lookup("openelis/ProviderManagerBean/remote");
    }
}
