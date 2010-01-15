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

import javax.naming.InitialContext;

import org.openelis.domain.PanelDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.PanelLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class PanelManagerProxy {

    public PanelManager fetchById(Integer id) throws Exception {
        PanelDO data;
        PanelManager m;
        
        data = local().fetchById(id);
        m = PanelManager.getInstance();
        
        m.setPanel(data);

        return m;        
    }
    
    public PanelManager fetchWithItems(Integer id) throws Exception {
        PanelManager m;
        
        m = fetchById(id);
        m.getItems();
        
        return m;       
    }
    
    public PanelManager add(PanelManager man) throws Exception {
        Integer id;
        
        local().add(man.getPanel());
        id = man.getPanel().getId();
        
        if (man.items != null) {
            man.getItems().setPanelId(id);
            man.getItems().add();
        }
        
        return man;
    }
    
    public PanelManager update(PanelManager man) throws Exception {
        Integer id;
        
        local().update(man.getPanel());
        id = man.getPanel().getId();
        
        if (man.items != null) {
            man.getItems().setPanelId(id);
            man.getItems().update();
        }
        
        return man;
    }
    
    public void delete(PanelManager man) throws Exception {
        
        man.getItems().setPanelId(man.getPanel().getId());
        man.getItems().delete();

        local().delete(man.getPanel());
    }
    
    public PanelManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public PanelManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(PanelManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        try {
            local().validate(man.getPanel());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.items != null)
                man.getItems().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
    
    private PanelLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (PanelLocal)ctx.lookup("openelis/PanelBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
