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
package org.openelis.modules.instrument.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InstrumentManagerRemote;
import org.openelis.remote.InstrumentRemote;

public class InstrumentService {

    private static final int rowPP = 20;
    
    public InstrumentManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<InstrumentViewDO> fetchByName(String name) throws Exception {
        try {
            return remote().fetchByName(name + "%", 10);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InstrumentManager fetchWithLogs(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithLogs(id);
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
    
    public InstrumentManager add(InstrumentManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public InstrumentManager update(InstrumentManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public InstrumentManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public InstrumentManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public InstrumentLogManager fetchLogByInstrumentId(Integer id) throws Exception {
        try {
            return remoteManager().fetchLogByInstrumentId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    private InstrumentRemote remote() {
        return (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
    }
    
    private InstrumentManagerRemote remoteManager() {
        return (InstrumentManagerRemote)EJBFactory.lookup("openelis/InstrumentManagerBean/remote");
    }

}
