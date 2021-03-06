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
package org.openelis.modules.history.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.HistoryBean;
import org.openelis.domain.HistoryVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.history.client.HistoryServiceInt;

@WebServlet("/openelis/history")
public class HistoryServlet extends RemoteServlet implements HistoryServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    HistoryBean history;

    public ArrayList<HistoryVO> fetchByReferenceIdAndTable(Query query) throws Exception {
        Integer referenceId, referenceTableId;
        QueryData field;

        field = query.getFields().get(0);
        referenceId = Integer.parseInt(field.getQuery());

        field = query.getFields().get(1);
        referenceTableId = Integer.parseInt(field.getQuery());

        try {        
            return history.fetchByReferenceIdAndTable(referenceId, referenceTableId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
