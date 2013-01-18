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

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.utils.EJBFactory;

public class AuxFieldValueManagerProxy {
    public AuxFieldValueManager fetchByFieldId(Integer auxFieldId) throws Exception {
        AuxFieldValueLocal l;
        ArrayList<AuxFieldValueViewDO> data;
        AuxFieldValueManager m;

        l = EJBFactory.getAuxFieldValue();
        data = l.fetchByFieldId(auxFieldId);
        m = AuxFieldValueManager.getInstance();
        m.setAuxiliaryFieldId(auxFieldId);
        
        for(int i=0; i<data.size(); i++)
            m.addAuxFieldValue(data.get(i));
        
        return m;
    }
    
    public AuxFieldValueManager add(AuxFieldValueManager man) throws Exception {
        AuxFieldValueViewDO data;
        AuxFieldValueLocal l;

        l = EJBFactory.getAuxFieldValue();
        for(int i=0; i<man.count(); i++){
            data = man.getAuxFieldValueAt(i);
            data.setAuxFieldId(man.getAuxiliaryFieldId());
            l.add(data);
        }
        
        return man;
    }
    
    public AuxFieldValueManager update(AuxFieldValueManager man) throws Exception {
        int i;
        AuxFieldValueViewDO data;
        AuxFieldValueLocal l;        

        l = EJBFactory.getAuxFieldValue();
        for(i = 0; i < man.deleteCount(); i++) 
            l.delete(man.getDeletedAt(i));
        
        for(i=0; i < man.count(); i++){
            data = man.getAuxFieldValueAt(i);
            
            if(data.getId() == null){
                data.setAuxFieldId(man.getAuxiliaryFieldId());
                l.add(data);
            }else
                l.update(data);
        }
        
        return man;
    }
    
    public void validate(AuxFieldValueManager man, ValidationErrorsList list) throws Exception {       
    }
}