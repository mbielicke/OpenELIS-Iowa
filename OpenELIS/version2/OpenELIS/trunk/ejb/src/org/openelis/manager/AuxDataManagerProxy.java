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
import java.util.HashMap;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxDataLocal;
import org.openelis.utils.EJBFactory;

public class AuxDataManagerProxy {
    
    public AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        AuxDataViewDO dataDO;
        ArrayList<AuxDataViewDO> data;
        ArrayList<AuxFieldViewDO> fields;
        ArrayList<AuxFieldValueViewDO> values, tmpValue;
        int fieldId;
        HashMap<Integer, AuxFieldViewDO> fieldHash;
        HashMap<Integer, ArrayList<AuxFieldValueViewDO>> valueHash;
        
        
        AuxDataManager m;
        
        data = EJBFactory.getAuxData().fetchById(referenceId, referenceTableId);
        fields = EJBFactory.getAuxField().fetchByAuxDataRefIdRefTableId(referenceId, referenceTableId); //ordered by groupid
        
        //put the fields in the hash to guarantee they match up with data entries
        fieldHash = new HashMap<Integer, AuxFieldViewDO>();
        for(int i=0; i<fields.size(); i++)
            fieldHash.put(fields.get(i).getId(), fields.get(i));
        
        values = EJBFactory.getAuxFieldValue().fetchByAuxDataRefIdRefTableId(referenceId, referenceTableId);
        
        //split the values up by field id
        valueHash = new HashMap<Integer, ArrayList<AuxFieldValueViewDO>>();
        tmpValue = null;
        fieldId = -1;
        for(int j=0; j<values.size(); j++){
            if(fieldId == values.get(j).getAuxFieldId()){
                tmpValue.add(values.get(j));
            }else{
                fieldId = values.get(j).getAuxFieldId();
                tmpValue = new ArrayList<AuxFieldValueViewDO>();
                valueHash.put(fieldId, tmpValue);
                
                tmpValue.add(values.get(j));
            }
        }
        
        m = AuxDataManager.getInstance();
        for(int k=0; k<data.size(); k++){
            dataDO = data.get(k);
            m.addAuxDataFieldsAndValues(dataDO, fieldHash.get(dataDO.getAuxFieldId()), valueHash.get(dataDO.getAuxFieldId()));
        }
        
        return m;
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
