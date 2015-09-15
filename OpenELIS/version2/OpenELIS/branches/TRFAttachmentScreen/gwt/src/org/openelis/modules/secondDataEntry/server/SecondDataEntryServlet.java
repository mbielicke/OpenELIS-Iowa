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
package org.openelis.modules.secondDataEntry.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SecondDataEntryBean;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.modules.secondDataEntry.client.SecondDataEntryService;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

/*
 * This class provides service for Second data Entry screen
 */
@WebServlet("/openelis/secondDataEntry")
public class SecondDataEntryServlet extends RemoteServlet implements SecondDataEntryService {

    private static final long   serialVersionUID = 1L;

    @EJB
    private SecondDataEntryBean secondDataEntry;

    @Override
    public ArrayList<SecondDataEntryVO> query(Query query) throws Exception {
        try {
            return secondDataEntry.query(query.getFields(),
                                         query.getPage() * query.getRowsPerPage(),
                                         query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ReportStatus getFields(String systemVariableName) throws Exception {
        try {
            return secondDataEntry.getFields(systemVariableName);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}