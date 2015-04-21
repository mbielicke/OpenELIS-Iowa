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

import org.openelis.bean.IOrderTestAnalyteBean;
import org.openelis.bean.IOrderTestBean;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.manager.IOrderTestManager.IOrderTestListItem;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class IOrderTestManagerProxy {
    
    public IOrderTestManager fetchByIorderId(Integer id) throws Exception {
        IOrderTestManager m;
        ArrayList<IOrderTestViewDO> tests;
        
        tests = EJBFactory.getIOrderTest().fetchByIorderId(id);
        m = IOrderTestManager.getInstance();
        m.setIorderId(id);
        
        for (int i = 0; i < tests.size(); i++ )
            m.addTest(tests.get(i));

        return m;
    }
    
    public IOrderTestManager add(IOrderTestManager man) throws Exception {
        IOrderTestBean tl;
        IOrderTestViewDO data;
        IOrderTestAnalyteManager anaMan;

        tl = EJBFactory.getIOrderTest();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            data.setIorderId(man.getIorderId());
            tl.add(data);
            
            anaMan = man.getAnalytesAt(i);
            anaMan.setIorderTestId(data.getId());
            anaMan.add();
        }

        return man;
    }

    public IOrderTestManager update(IOrderTestManager man) throws Exception {
        IOrderTestBean tl;
        IOrderTestAnalyteBean al;
        IOrderTestViewDO data;
        IOrderTestAnalyteManager anaMan;
        IOrderTestListItem item;
        
        tl = EJBFactory.getIOrderTest();
        al = EJBFactory.getIOrderTestAnalyte();
        
        for (int j = 0; j < man.deleteCount(); j++ ) {            
            item = man.getDeletedAt(j);
            /*
             * The analytes are deleted through the local interface and not the 
             * manager because the OrderTestManager may not have the analyte loaded
             * because the user never looked them up. Also some of the analytes 
             * may never have been marked as reportable, so they may not have any
             * ids.   
             */
            al.deleteByOrderTestId(item.test.getId());                       
            tl.delete(item.test);
        }

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            if (data.getId() == null) {
                data.setIorderId(man.getIorderId());
                tl.add(data);
            } else {
                tl.update(data);
            }
            
            anaMan = man.getAnalytesAt(i);
            anaMan.setIorderTestId(data.getId());
            anaMan.update();
        }

        return man;
    }
    
    public void validate(IOrderTestManager man) throws Exception {
        ValidationErrorsList list;
        IOrderTestBean tl;
        IOrderTestViewDO data;

        tl = EJBFactory.getIOrderTest();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            try {
                tl.validate(data, i+1);
            } catch (Exception e) {
                list.add(e);
            }
            
            try {
                if (man.items.get(i).analytes != null)
                    man.getAnalytesAt(i).validate(data, i+1);
            } catch (Exception e) {
                list.add(e);
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
