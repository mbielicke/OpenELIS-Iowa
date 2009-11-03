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

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;

public class AuxFieldManagerProxy {
    public AuxFieldManager fetchById(Integer id) throws Exception {
        AuxFieldLocal l;
        ArrayList<AuxFieldViewDO> data;
        AuxFieldManager m;

        l = local();
        data = l.fetchById(id);
        m = AuxFieldManager.getInstance();
        
        for(int i=0; i<data.size(); i++)
            m.addAuxField(data.get(i));

        return m;
    }
    
    public AuxFieldManager fetchByGroupIdWithValues(Integer groupId) throws Exception {
        AuxFieldLocal l;
        AuxFieldValueLocal vl;
        
        AuxFieldViewDO dataDO;
        ArrayList<AuxFieldViewDO> fields;
        ArrayList<AuxFieldValueViewDO> values, tmpValue;
        int fieldId;
        HashMap<Integer, ArrayList<AuxFieldValueViewDO>> valueHash;
        AuxFieldManager m;
        
        l = local();
        fields = l.fetchByGroupId(groupId);
        
        vl = valueLocal();
        values = vl.fetchByGroupId(groupId);
        
        //split the values up by field id
        valueHash = new HashMap<Integer, ArrayList<AuxFieldValueViewDO>>();
        tmpValue = null;
        fieldId = -1;
        for(int j=0; j<values.size(); j++){
            if(fieldId == values.get(j).getAuxFieldId()){
                tmpValue.add(values.get(j));
            }else{
                if(fieldId > -1)
                    valueHash.put(fieldId, tmpValue);
                
                tmpValue = new ArrayList<AuxFieldValueViewDO>();
                tmpValue.add(values.get(j));
                fieldId = values.get(j).getAuxFieldId();
            }
        }
        
        //put one more in the hash to catch the last one
        valueHash.put(fieldId, tmpValue);
        
        m = AuxFieldManager.getInstance();
        m.setAuxFieldGroupId(groupId);
        for(int k=0; k<fields.size(); k++){
            dataDO = fields.get(k);
            m.addAuxFieldAndValues(dataDO, valueHash.get(dataDO.getId()));
        }
        
        return m;
    }
    
    public AuxFieldManager fetchByGroupId(Integer auxFieldGroupId) throws Exception {
        AuxFieldLocal l;
        ArrayList<AuxFieldViewDO> data;
        AuxFieldManager m;

        l = local();
        data = l.fetchByGroupId(auxFieldGroupId);
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
