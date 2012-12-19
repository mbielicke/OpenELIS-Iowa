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
package org.openelis.manager;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ExchangeLocalTermManagerProxy {

    public ExchangeLocalTermManager fetchById(Integer id) throws Exception {
        ExchangeLocalTermViewDO data;
        ExchangeLocalTermManager man;        

        data = EJBFactory.getExchangeLocalTerm().fetchById(id);
        man = ExchangeLocalTermManager.getInstance();

        man.setExchangeLocalTerm(data);

        return man;
    }

    public ExchangeLocalTermManager fetchWithExternalTerms(Integer id) throws Exception {
        ExchangeLocalTermManager man;        

        man = fetchById(id);
        man.getExternalTerms();

        return man;
    }
    
    public ExchangeLocalTermManager add(ExchangeLocalTermManager man) throws Exception {
        Integer id;

        EJBFactory.getExchangeLocalTerm().add(man.getExchangeLocalTerm());
        id = man.getExchangeLocalTerm().getId();

        if (man.externalTerms != null) {
            man.getExternalTerms().setExchangeLocalTermId(id);
            man.getExternalTerms().add();
        }

        return man;
    }

    public ExchangeLocalTermManager update(ExchangeLocalTermManager man) throws Exception {
        Integer id;

        EJBFactory.getExchangeLocalTerm().update(man.getExchangeLocalTerm());
        id = man.getExchangeLocalTerm().getId();

        if (man.externalTerms != null) {
            man.getExternalTerms().setExchangeLocalTermId(id);
            man.getExternalTerms().update();
        }

        return man;
    }

    public ExchangeLocalTermManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ExchangeLocalTermManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(ExchangeLocalTermManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            EJBFactory.getExchangeLocalTerm().validate(man.getExchangeLocalTerm());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.externalTerms != null)
                man.getExternalTerms().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
}
