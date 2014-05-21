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
package org.openelis.modules.analyte.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AnalyteBean;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.modules.analyte.client.AnalyteServiceInt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/analyte")
public class AnalyteServlet extends RemoteServlet implements AnalyteServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    AnalyteBean analyte;
    
    public AnalyteViewDO fetchById(Integer id) throws Exception {
        try {        
            return analyte.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return analyte.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AnalyteDO add(AnalyteViewDO data) throws Exception {
        try {        
            return analyte.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AnalyteDO update(AnalyteViewDO data) throws Exception {
        try {        
            return analyte.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public AnalyteViewDO fetchForUpdate(Integer id) throws Exception {
        try {        
            return analyte.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void delete(AnalyteDO data) throws Exception {
    	analyte.delete(data);
    }

    public AnalyteViewDO abortUpdate(Integer id) throws Exception {
        try {        
            return analyte.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<AnalyteDO> fetchByName(String search) throws Exception {
        try {        
            return analyte.fetchByName(search + "%", 100);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}