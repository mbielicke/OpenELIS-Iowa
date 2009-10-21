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

import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldLocal;

public class AuxFieldManagerProxy {
    public AuxFieldManager fetchByAuxFieldGroupId(Integer auxFieldGroupId) throws Exception {
        AuxFieldLocal l;
        ArrayList<AuxFieldViewDO> data;
        AuxFieldManager m;

        l = local();
        data = l.fetchById(auxFieldGroupId);
        m = AuxFieldManager.getInstance();
        m.setAuxFieldGroupId(auxFieldGroupId);
        
        for(int i=0; i<data.size(); i++)
            m.addAuxField(data.get(i));

        return m;
    }
    
    public AuxFieldManager add(AuxFieldManager man) throws Exception {
        Integer id;
        AuxFieldViewDO auxDO;
        AuxFieldLocal l;

        l = local();
        for(int i=0; i<man.count(); i++){
            auxDO = man.getAuxFieldAt(i);
            auxDO.setAuxFieldGroupId(man.getAuxFieldGroupId());
            l.add(auxDO);
            
            id = auxDO.getId();
            man.getValuesAt(i).setAuxiliaryFieldId(id);
            man.getValuesAt(i).add();
        }
        
        return man;
    }
    
    public AuxFieldManager update(AuxFieldManager man) throws Exception {
        Integer id;
        AuxFieldViewDO auxDO;
        AuxFieldLocal l;

        l = local();
        for(int i=0; i<man.count(); i++){
            auxDO = man.getAuxFieldAt(i);
            
            if(auxDO.getId() == null){
                auxDO.setAuxFieldGroupId(man.getAuxFieldGroupId());
                l.add(auxDO);
            }else
                l.update(auxDO);
            
            id = auxDO.getId();
            man.getValuesAt(i).setAuxiliaryFieldId(id);
            man.getValuesAt(i).add();
        }
        
        return man;
    }
    
    public void validate(AuxFieldManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private AuxFieldLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (AuxFieldLocal)ctx.lookup("openelis/AuxFieldBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
