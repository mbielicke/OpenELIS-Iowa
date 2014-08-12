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

import org.openelis.bean.AuxFieldGroupBean;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class AuxFieldGroupManagerProxy {
    public AuxFieldGroupManager fetchById(Integer id) throws Exception {
        AuxFieldGroupBean l;
        AuxFieldGroupDO data;
        AuxFieldGroupManager m;

        l = EJBFactory.getAuxFieldGroup();
        data = l.fetchById(id);
        m = AuxFieldGroupManager.getInstance();
        
        m.setGroup(data);

        return m;
    }
    
    public AuxFieldGroupManager fetchByIdWithFields(Integer id) throws Exception{
        AuxFieldGroupManager m;

        m = fetchById(id);
        m.getFields();

        return m;
    }
    
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        Integer id;
        AuxFieldGroupBean l;

        l = EJBFactory.getAuxFieldGroup();
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
        AuxFieldGroupBean l;

        l = EJBFactory.getAuxFieldGroup();
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
            EJBFactory.getAuxFieldGroup().validate(man.getGroup());
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
}
