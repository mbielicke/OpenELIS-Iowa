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

import javax.naming.InitialContext;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;

public class AuxDataManagerProxy {
    
    public AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        AuxDataLocal l;
        ArrayList<AuxDataDO> data;
        AuxDataManager m;

        l = local();
        data = l.fetchById(referenceId, referenceTableId);
        m = AuxDataManager.getInstance();
        
        for(int i=0; i<data.size(); i++)
            m.addAuxData(data.get(i));

        return m;
    }
    
    public AuxDataManager fetchByIdForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        AuxDataLocal l;
        AuxFieldLocal fl;
        AuxFieldValueLocal fvl;
        AuxDataDO dataDO;
        ArrayList<AuxDataDO> data;
        ArrayList<AuxFieldViewDO> fields;
        ArrayList<AuxFieldValueDO> values, tmpValue;
        int fieldId;
        HashMap<Integer, AuxFieldViewDO> fieldHash;
        HashMap<Integer, ArrayList<AuxFieldValueDO>> valueHash;
        
        
        AuxDataManager m;
        
        l = local();
        data = l.fetchById(referenceId, referenceTableId);
        
        fl = fieldLocal();
        fields = fl.fetchByAuxDataRefIdRefTableId(referenceId, referenceTableId); //ordered by groupid
        
        //put the fields in the hash to guarantee they match up with data entries
        fieldHash = new HashMap<Integer, AuxFieldViewDO>();
        for(int i=0; i<fields.size(); i++)
            fieldHash.put(fields.get(i).getId(), fields.get(i));
        
        fvl = valueLocal();
        values = fvl.fetchByAuxDataRefIdRefTableId(referenceId, referenceTableId);
        
        //split the values up by field id
        valueHash = new HashMap<Integer, ArrayList<AuxFieldValueDO>>();
        tmpValue = null;
        fieldId = -1;
        for(int j=0; j<values.size(); j++){
            if(fieldId == values.get(j).getAuxFieldId()){
                tmpValue.add(values.get(j));
            }else{
                valueHash.put(fieldId, tmpValue);
                tmpValue = new ArrayList<AuxFieldValueDO>();
                tmpValue.add(values.get(j));
                fieldId = values.get(j).getAuxFieldId();
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
        return null;
    }
    
    public AuxDataManager update(AuxDataManager man) throws Exception {
        return null;
    }
    
    public void validate(AuxDataManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private AuxDataLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (AuxDataLocal)ctx.lookup("openelis/AuxDataBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private AuxFieldLocal fieldLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (AuxFieldLocal)ctx.lookup("openelis/AuxFieldBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private AuxFieldValueLocal valueLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (AuxFieldValueLocal)ctx.lookup("openelis/AuxFieldValueBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
