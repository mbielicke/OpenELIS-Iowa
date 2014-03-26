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
package org.openelis.modules.exchangeDataSelection.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.DataExchangeReportBean;
import org.openelis.bean.ExchangeCriteriaBean;
import org.openelis.bean.ExchangeCriteriaManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.modules.exchangeDataSelection.client.ExchangeDataSelectionServiceInt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

/*
 * This class provides service for ExchangeCriteriaManager and ExchangeProfileManager.
 */
@WebServlet("/openelis/exchangeData")
public class ExchangeDataSelectionServlet extends RemoteServlet implements ExchangeDataSelectionServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    ExchangeCriteriaManagerBean exchangeCriteriaManager;
    
    @EJB
    ExchangeCriteriaBean        exchangeCriteria;
    
    @EJB
    DataExchangeReportBean      dataExchangeReport;

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchByName(name);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchWithProfiles(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchWithProfilesByName(name);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return exchangeCriteria.query(query.getFields(),
                                          query.getPage() * query.getRowsPerPage(),
                                          query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        try {        
            return exchangeCriteriaManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        try {        
            return exchangeCriteriaManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public void delete(ExchangeCriteriaManager man) throws Exception {
        exchangeCriteriaManager.delete(man);
    }

    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ExchangeCriteriaManager duplicate(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.duplicate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    //
    // support 
    //
    public ArrayList<Integer> getSamples(ExchangeCriteriaManager man) throws Exception {          
        try {        
            return dataExchangeReport.getSamples(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ReportStatus export(ArrayList<Integer> accessions, ExchangeCriteriaManager man) throws Exception {          
        try {        
            return dataExchangeReport.export(accessions, man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception {
        try {        
            return exchangeCriteriaManager.fetchProfileByExchangeCriteriaId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
