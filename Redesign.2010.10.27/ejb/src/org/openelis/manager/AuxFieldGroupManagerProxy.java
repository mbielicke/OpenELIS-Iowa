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

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldGroupLocal;

public class AuxFieldGroupManagerProxy {
    public AuxFieldGroupManager fetchById(Integer id) throws Exception {
        AuxFieldGroupLocal l;
        AuxFieldGroupDO data;
        AuxFieldGroupManager m;

        l = local();
        data = l.fetchById(id);
        m = AuxFieldGroupManager.getInstance();
        
        m.setGroup(data);

        return m;
    }
    
    public AuxFieldGroupManager fetchWithFields(Integer id) throws Exception{
        AuxFieldGroupManager m;

        m = fetchById(id);
        m.getFields();

        return m;
    }
    
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        Integer id;
        AuxFieldGroupLocal l;

        l = local();
        l.add(man.getGroup());
        id = man.getGroup().getId();

        if(man.fields != null) {
            man.getFields().setAuxFieldGroupId(id);
            man.getFields().add();
        }
        
        return man;
    }
    
    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        Integer id;
        AuxFieldGroupLocal l;

        l = local();
        l.update(man.getGroup());
        id = man.getGroup().getId();

        if(man.fields != null) {
            man.getFields().setAuxFieldGroupId(id);
            man.getFields().update();
        }

        return man;
    }
    
    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(AuxFieldGroupManager man, ValidationErrorsList list) throws Exception {       
        try {
            local().validate(man.getGroup());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.fields != null)
                man.getFields().validate(list);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
    }
    
    private AuxFieldGroupLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (AuxFieldGroupLocal)ctx.lookup("openelis/AuxFieldGroupBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
