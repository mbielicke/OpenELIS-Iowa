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

import org.openelis.bean.ExchangeCriteriaBean;
import org.openelis.bean.ExchangeCriteriaManagerBean;
import org.openelis.bean.SampleBean;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.modules.exchangeDataSelection.client.ExchangeDataSelectionServiceInt;

/*
 * This class provides service for ExchangeCriteriaManager and ExchangeProfileManager.
 */
@WebServlet("/openelis/exchangeData")
public class ExchangeDataSelectionServlet extends AppServlet implements ExchangeDataSelectionServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    ExchangeCriteriaManagerBean exchangeCriteriaManager;
    
    @EJB
    ExchangeCriteriaBean        exchangeCriteria;
    
    @EJB
    SampleBean                  sample;

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        return exchangeCriteriaManager.fetchById(id);
    }
    
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        return exchangeCriteriaManager.fetchByName(name);
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        return exchangeCriteriaManager.fetchWithProfiles(id);
    }
    
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        return exchangeCriteriaManager.fetchWithProfilesByName(name);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return exchangeCriteria.query(query.getFields(),
                                      query.getPage() * query.getRowsPerPage(),
                                      query.getRowsPerPage());
    }
    
    public ArrayList<IdAccessionVO> dataExchangeQuery(Query query) throws Exception {          
        return sample.dataExchangeQuery(query.getFields());
    }

    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        return exchangeCriteriaManager.add(man);
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        return exchangeCriteriaManager.update(man);
    }
    
    public void delete(ExchangeCriteriaManager man) throws Exception {
        exchangeCriteriaManager.delete(man);
    }

    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        return exchangeCriteriaManager.fetchForUpdate(id);
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        return exchangeCriteriaManager.abortUpdate(id);
    }
    
    public ExchangeCriteriaManager duplicate(Integer id) throws Exception {
        return exchangeCriteriaManager.duplicate(id);
    }

    //
    // support for ExchangeProfileManager
    //
    public ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception {
        return exchangeCriteriaManager.fetchProfileByExchangeCriteriaId(id);
    }
}
