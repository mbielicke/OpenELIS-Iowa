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

import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InstrumentLocal;


public class InstrumentManagerProxy {

    public InstrumentManager fetchById(Integer id) throws Exception {
        InstrumentLocal il;
        InstrumentViewDO data;
        InstrumentManager m;
        
        il = local();
        data = il.fetchById(id);
        m = InstrumentManager.getInstance();
        
        m.setInstrument(data);
        
        return m;
    }
    
    public InstrumentManager fetchWithLogs(Integer id) throws Exception {
        InstrumentManager m;
        
        m = fetchById(id);
        m.getLogs();

        return m;
    }
    
    public InstrumentManager add(InstrumentManager man) throws Exception {
        Integer id;
        InstrumentLocal il;
        
        il = local();
        il.add(man.getInstrument());
        id = man.getInstrument().getId();
        
        man.getLogs().setInstrumentId(id);
        man.getLogs().add();
        
        return man;
    }
    
    public InstrumentManager update(InstrumentManager man) throws Exception {
        Integer id;
        InstrumentLocal il;
        
        il = local();
        il.update(man.getInstrument());
        id = man.getInstrument().getId();
        
        man.getLogs().setInstrumentId(id);
        man.getLogs().update();
        
        return man;
    }
    
    public InstrumentManager fetchForUpdate(InstrumentManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public InstrumentManager abortUpdate(InstrumentManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(InstrumentManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            local().validate(man.getInstrument());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            man.getLogs().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;        
    }
    
    private InstrumentLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InstrumentLocal)ctx.lookup("openelis/InstrumentBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
