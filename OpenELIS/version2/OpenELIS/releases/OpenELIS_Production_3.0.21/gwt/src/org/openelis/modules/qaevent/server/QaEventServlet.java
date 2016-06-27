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
package org.openelis.modules.qaevent.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.QaEventBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.qaevent.client.QAEventServiceInt;

@WebServlet("/openelis/qa")
public class QaEventServlet extends RemoteServlet implements QAEventServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    QaEventBean qaEvent;

    public QaEventViewDO fetchById(Integer id) throws Exception {
        try {        
            return qaEvent.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<QaEventDO> fetchByNames(ArrayList<String> names) throws Exception {
        try {        
            return qaEvent.fetchByNames(names);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<QaEventDO> fetchByTestId(Integer id) throws Exception {
        try {        
            return qaEvent.fetchByTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<QaEventDO> fetchByCommon() throws Exception {
        try {        
            return qaEvent.fetchByCommon();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<QaEventDO> fetchAll() throws Exception {
        try {        
            return qaEvent.fetchAll();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return qaEvent.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public QaEventViewDO add(QaEventViewDO data) throws Exception {
        try {        
            return (QaEventViewDO)qaEvent.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public QaEventViewDO update(QaEventViewDO data) throws Exception {
        try {        
            return (QaEventViewDO)qaEvent.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public QaEventViewDO fetchForUpdate(Integer id) throws Exception {
        try {        
            return qaEvent.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public QaEventViewDO abortUpdate(Integer id) throws Exception {
        try {        
            return qaEvent.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}