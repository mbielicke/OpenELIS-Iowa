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

import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.PanelManager;
import org.openelis.server.EJBFactory;

/*
 * This class provides service for ExchangeCriteriaManager and ExchangeProfileManager.
 */
public class ExchangeDataSelectionService {

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchById(id);
    }
    
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchByName(name);
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchWithProfiles(id);
    }
    
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchWithProfilesByName(name);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getExchangeCriteria().query(query.getFields(),
                                                      query.getPage() * query.getRowsPerPage(),
                                                      query.getRowsPerPage());
    }
    
    public ArrayList<IdAccessionVO> dataExchangeQuery(Query query) throws Exception {          
        return EJBFactory.getSample().dataExchangeQuery(query.getFields());
    }

    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().add(man);
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().update(man);
    }
    
    public void delete(ExchangeCriteriaManager man) throws Exception {
        EJBFactory.getExchangeCriteriaManager().delete(man);
    }

    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchForUpdate(id);
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().abortUpdate(id);
    }
    
    public ExchangeCriteriaManager duplicate(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().duplicate(id);
    }

    //
    // support for ExchangeProfileManager
    //
    public ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception {
        return EJBFactory.getExchangeCriteriaManager().fetchProfileByExchangeCriteriaId(id);
    }
}
