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
package org.openelis.modules.eventLog.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.EventLogBean;
import org.openelis.domain.EventLogDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.eventLog.client.EventLogServiceInt;

@WebServlet("/openelis/eventLog")
public class EventLogServlet extends RemoteServlet implements EventLogServiceInt {

    private static final long serialVersionUID = 1L;

    @EJB
    EventLogBean              eventLog;

    public ArrayList<EventLogDO> query(Query query) throws Exception {
        try {
            return eventLog.query(query.getFields(),
                                  query.getPage() * query.getRowsPerPage(),
                                  query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public EventLogDO add(Integer typeId, String source, Integer referenceTableId,
                          Integer referenceId, Integer levelId, String text) throws Exception {
        try {
            return eventLog.add(typeId, source, referenceTableId, referenceId, levelId, text);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
