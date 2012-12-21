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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.InstrumentBean;
import org.openelis.bean.InstrumentManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;
import org.openelis.modules.instrument.client.InstrumentServiceInt;

@WebServlet("/openelis/instrument")
public class InstrumentServlet extends AppServlet implements InstrumentServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    InstrumentManagerBean instrumentManager;
    
    @EJB
    InstrumentBean        instrument;

    public InstrumentManager fetchById(Integer id) throws Exception {
        return instrumentManager.fetchById(id);
    }

    public ArrayList<InstrumentViewDO> fetchByName(String name) throws Exception {
        return instrument.fetchByName(name + "%", 10);
    }

    public ArrayList<InstrumentViewDO> fetchActiveByName(String name) throws Exception {
        return instrument.fetchActiveByName(name + "%", 10);
    }

    public InstrumentManager fetchWithLogs(Integer id) throws Exception {
        return instrumentManager.fetchWithLogs(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return instrument.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public InstrumentManager add(InstrumentManager man) throws Exception {
        return instrumentManager.add(man);
    }

    public InstrumentManager update(InstrumentManager man) throws Exception {
        return instrumentManager.update(man);
    }

    public InstrumentManager fetchForUpdate(Integer id) throws Exception {
        return instrumentManager.fetchForUpdate(id);
    }

    public InstrumentManager abortUpdate(Integer id) throws Exception {
        return instrumentManager.abortUpdate(id);
    }

    public InstrumentLogManager fetchLogByInstrumentId(Integer id) throws Exception {
        return instrumentManager.fetchLogByInstrumentId(id);
    }
}
