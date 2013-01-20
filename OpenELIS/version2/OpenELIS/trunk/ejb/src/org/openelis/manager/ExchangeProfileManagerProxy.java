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

import org.openelis.bean.ExchangeProfileBean;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ExchangeProfileManagerProxy {

    public ExchangeProfileManager fetchByExchangeCriteriaId(Integer id) throws Exception {
        ExchangeProfileManager cm;
        ArrayList<ExchangeProfileDO> list;

        list = EJBFactory.getExchangeProfile().fetchByExchangeCriteriaId(id);
        cm = ExchangeProfileManager.getInstance();
        cm.setExchangeCriteriaId(id);
        cm.setProfiles(list);

        return cm;
    }

    public ExchangeProfileManager add(ExchangeProfileManager man) throws Exception {
        ExchangeProfileBean cl;
        ExchangeProfileDO data;

        cl = EJBFactory.getExchangeProfile();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getProfileAt(i);
            data.setSortOrder(i + 1);
            data.setExchangeCriteriaId(man.getExchangeCriteriaId());
            cl.add(data);
        }

        return man;
    }

    public ExchangeProfileManager update(ExchangeProfileManager man) throws Exception {
        int i;
        ExchangeProfileBean cl;
        ExchangeProfileDO data;

        cl = EJBFactory.getExchangeProfile();
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            data = man.getProfileAt(i);
            data.setSortOrder(i + 1);
            if (data.getId() == null) {
                data.setExchangeCriteriaId(man.getExchangeCriteriaId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }

        return man;
    }
    
    public void delete(ExchangeProfileManager man) throws Exception {
        int i;
        ExchangeProfileBean pl;
        ExchangeProfileDO data;
        
        pl = EJBFactory.getExchangeProfile();
        for (i = 0; i < man.deleteCount(); i++) {
            data = man.getDeletedAt(i);
            if (data.getId() != null)               
                pl.delete(data);
        }
        
        for (i = 0; i < man.count(); i++) {
            data = man.getProfileAt(i);            
            if (data.getId() != null)               
                pl.delete(data);                                
        }
    }
    
    public void validate(ExchangeProfileManager man) throws Exception {
        ValidationErrorsList list;
        ExchangeProfileBean cl;

        cl = EJBFactory.getExchangeProfile();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getProfileAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "profileTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
