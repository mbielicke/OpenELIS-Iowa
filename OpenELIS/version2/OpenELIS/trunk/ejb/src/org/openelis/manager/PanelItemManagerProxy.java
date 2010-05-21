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

import javax.naming.InitialContext;

import org.openelis.domain.PanelItemDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.PanelItemLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class PanelItemManagerProxy {
    
    public PanelItemManager fetchByPanelId(Integer id) throws Exception {
        PanelItemManager man;
        ArrayList<PanelItemDO> items;
        
        items = local().fetchByPanelId(id);
        man = PanelItemManager.getInstance();
        man.setPanelId(id);
        man.setItems(items);
        
        return man;
    } 
    
    public PanelItemManager add(PanelItemManager man) throws Exception {
        PanelItemLocal pl;
        PanelItemDO data;
        
        pl = local();
        for(int i = 0 ; i < man.count(); i++) {
            data = man.getItemAt(i);
            data.setSortOrder(i + 1);
            data.setPanelId(man.getPanelId());
            pl.add(data);
        } 
        
        return man;       
    }
    
    public PanelItemManager update(PanelItemManager man) throws Exception {
        PanelItemLocal pl;
        PanelItemDO data;
        
        pl = local();
        for(int j = 0; j < man.deleteCount(); j++) {
            pl.delete(man.getDeletedAt(j));
        }
        
        for(int i = 0; i < man.count(); i++) {
            data = man.getItemAt(i);
            data.setSortOrder(i + 1);
            if(data.getId() == null) {
                data.setPanelId(man.getPanelId());
                pl.add(data);
            } else {
                pl.update(data);
            }                       
        }
        
        return man;        
    }
    
    public PanelItemManager delete(PanelItemManager man) throws Exception {
        PanelItemLocal pl;
        PanelItemDO data;
        
        pl = local();
        for(int j = 0; j < man.deleteCount(); j++) {
            pl.delete(man.getDeletedAt(j));
        }
        
        for(int i = 0; i < man.count(); i++) {
            data = man.getItemAt(i);            
            if(data.getId() != null)               
                pl.delete(data);                                
        }
        
        return man;        
    }
    
    public void validate(PanelItemManager man) throws Exception {        
        ValidationErrorsList list;
        PanelItemLocal pl;
        int count;
        
        pl = local();
        list = new ValidationErrorsList();
        count = man.count();
        
        if(count == 0)
            list.add(new FieldErrorException("noTestAssignedToPanelException", null));
        
        for (int i = 0; i < count; i++ ) {
            try {
                pl.validate(man.getItemAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "panelItemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
        
    }
    
    private PanelItemLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (PanelItemLocal)ctx.lookup("openelis/PanelItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
 