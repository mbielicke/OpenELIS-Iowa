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

import java.util.ArrayList;

import org.openelis.domain.ExchangeExternalTermDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ExchangeExternalTermLocal;
import org.openelis.meta.ExchangeLocalTermMeta;
import org.openelis.utils.EJBFactory;

public class ExchangeExternalTermManagerProxy {

    public ExchangeExternalTermManager fetchByExchangeLocalTermId(Integer id) throws Exception {
        ExchangeExternalTermManager etm;
        ArrayList<ExchangeExternalTermDO> list;
        
        list = EJBFactory.getExchangeExternalTerm().fetchByExchangeLocalTermId(id);
        etm = ExchangeExternalTermManager.getInstance();
        etm.setExchangeLocalTermId(id);
        etm.setExternalTerms(list);
        
        return etm;
    }
    
    public ExchangeExternalTermManager add(ExchangeExternalTermManager man) throws Exception {
        ExchangeExternalTermLocal tl;
        ExchangeExternalTermDO data;
        
        tl = EJBFactory.getExchangeExternalTerm();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getExternalTermAt(i);
            data.setExchangeLocalTermId(man.getExchangeLocalTermId());
            tl.add(data);
        }

        return man;
    }
    
    public ExchangeExternalTermManager update(ExchangeExternalTermManager man) throws Exception {
        int i;
        ExchangeExternalTermLocal tl;
        ExchangeExternalTermDO data;
        
        tl = EJBFactory.getExchangeExternalTerm();
        for (i = 0; i < man.deleteCount(); i++ )
            tl.delete(man.getDeletedAt(i));
        
        for (i = 0; i < man.count(); i++ ) {
            data = man.getExternalTermAt(i);
            
            if (data.getId() == null) {
                data.setExchangeLocalTermId(man.getExchangeLocalTermId());
                tl.add(data);
            } else {
                tl.update(data);
            }
        }

        return man;
    }
    
    public void validate(ExchangeExternalTermManager man) throws Exception {
        Integer profileId;
        String active;
        ArrayList<Integer> profiles;
        ExchangeExternalTermDO data;
        ExchangeExternalTermLocal tl;   
        ValidationErrorsList list;

        tl = EJBFactory.getExchangeExternalTerm();
        list = new ValidationErrorsList();
        profiles = new ArrayList<Integer>();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getExternalTermAt(i);
            try {
                tl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "termMappingTable", i);
            }
            
            profileId = data.getProfileId();
            active = data.getIsActive();
            /*
             * only one active external term is allowed per profile for a given
             * local term
             */
            if (profileId != null && "Y".equals(active)) {
                if (profiles.contains(profileId))
                    list.add(new TableFieldErrorException("onlyOneActiveExtTermPerProfileException", i,
                                                          ExchangeLocalTermMeta.getExternalTermExchangeProfileId(), "termMappingTable"));
                else
                    profiles.add(profileId);                
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
