/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.exchangeVocabularyMap.server;

import java.util.ArrayList;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;
import org.openelis.server.EJBFactory;


/*
 * This class provides service for ExchangeExternalTermManager and ExchangeLocalTermManager
 */
public class ExchangeVocabularyMapService {
    public static ExchangeLocalTermManager fetchById(Integer id) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().fetchById(id);
    }
    
    public static ExchangeLocalTermManager fetchWithExternalTerms(Integer id) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().fetchWithExternalTerms(id);
    }
    
    public ArrayList<ExchangeLocalTermViewDO> query(Query query) throws Exception {
        return EJBFactory.getExchangeLocalTerm().query(query.getFields(),
                                                 query.getPage() * query.getRowsPerPage(),
                                                 query.getRowsPerPage());
    }
    
    public ExchangeLocalTermManager add(ExchangeLocalTermManager man) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().add(man);
    }
    
    public ExchangeLocalTermManager update(ExchangeLocalTermManager man) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().update(man);
    }
    
    public ExchangeLocalTermManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().fetchForUpdate(id);
    }
    
    public ExchangeLocalTermManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().abortUpdate(id);
    }
    
    public ExchangeExternalTermManager fetchExternalTermByExchangeLocalTermId(Integer id) throws Exception {
        return EJBFactory.getExchangeLocalTermManager().fetchExternalTermByExchangeLocalTermId(id);
    }
}