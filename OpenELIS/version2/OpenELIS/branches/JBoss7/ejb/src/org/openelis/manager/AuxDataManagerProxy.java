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

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.utils.EJBFactory;

public class AuxDataManagerProxy {
    
    public AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        int                            i;
        ArrayList<AuxDataViewDO>       data;
        ArrayList<AuxFieldValueViewDO> values;
        AuxDataManager                 man;
        AuxDataViewDO                  dataDO;
        AuxFieldViewDO                 fieldDO;       
        AuxFieldLocal                  fl;
        AuxFieldValueLocal             vl;

        man = AuxDataManager.getInstance();
        
        data = EJBFactory.getAuxData().fetchById(referenceId, referenceTableId);        
        fl = EJBFactory.getAuxField();
        vl = EJBFactory.getAuxFieldValue();
        for (i = 0; i < data.size(); i++) {
            dataDO = data.get(i);
            try {
                /*
                 * it can happen that after an aux data for an aux field has been
                 * created for a parent like sample or order, the aux field gets
                 * removed from the aux field group, we still want to show 
                 * aux data linked to existing aux fields     
                 */
                fieldDO = fl.fetchById(dataDO.getAuxFieldId());
                values = vl.fetchByFieldId(fieldDO.getId());
                man.addAuxDataFieldAndValues(dataDO, fieldDO, values);
            } catch (NotFoundException ignE) {
                // ignore
            }
        }
        
        return man;
    }
    
    public AuxDataManager add(AuxDataManager man) throws Exception {
        AuxDataLocal l;
        AuxDataViewDO data;

        l = EJBFactory.getAuxData();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getAuxDataAt(i);
            data.setReferenceTableId(man.getReferenceTableId());
            data.setReferenceId(man.getReferenceId());
            data.setSortOrder(i);
            
            l.add(data);
        }

        return man;
    }
    
    public AuxDataManager update(AuxDataManager man) throws Exception {
        int i;
        AuxDataLocal l;
        AuxDataViewDO data;

        l = EJBFactory.getAuxData();
        for (i = 0; i < man.deleteCount(); i++ )
            l.delete(man.getDeletedAuxDataAt(i));

        for (i = 0; i < man.count(); i++ ) {
            data = man.getAuxDataAt(i);
            data.setSortOrder(i);
            
            if (data.getId() == null) {
                data.setReferenceTableId(man.getReferenceTableId());
                data.setReferenceId(man.getReferenceId());
                l.add(data);
            } else {
                l.update(data);
            }
        }

        return man;
    }
    
    public void validate(AuxDataManager man, ValidationErrorsList errorsList) throws Exception {        
    }
}
