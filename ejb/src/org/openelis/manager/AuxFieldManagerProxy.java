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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.manager.AuxFieldManager.AuxFieldListItem;
import org.openelis.meta.AuxFieldGroupMeta;
import org.openelis.utilcommon.ResultRangeNumeric;  

public class AuxFieldManagerProxy { 
    
    private static int                        typeDict, typeNumeric, typeDefault;
    
    private static final Logger               log  = Logger.getLogger(AuxFieldManagerProxy.class.getName());
    
    public AuxFieldManagerProxy() {
        DictionaryDO data;
        DictionaryLocal dl;
        
        dl = dictLocal();
        
        try {
            data = dl.fetchBySystemName("aux_dictionary");
            typeDict = data.getId();
        } catch (Throwable e) {
            typeDict = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='aux_dictionary'", e);
        }
        
        try {
            data = dl.fetchBySystemName("aux_numeric");
            typeNumeric = data.getId();
        } catch (Throwable e) {
            typeNumeric = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='aux_numeric'", e);
        }
        
        try {
            data = dl.fetchBySystemName("aux_default");
            typeDefault = data.getId();
        } catch (Throwable e) {
            typeDefault = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='aux_default'", e);
        }
    }
    
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
        
        AuxFieldViewDO data;
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
            data = fields.get(k);
            m.addAuxFieldAndValues(data, valueHash.get(data.getId()));
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
        AuxFieldViewDO data;
        AuxFieldLocal l;

        l = local();
        for(int i=0; i<man.count(); i++){
            data = man.getAuxFieldAt(i);
            data.setAuxFieldGroupId(man.getAuxFieldGroupId());
            data.setSortOrder(i+1);
            l.add(data);
            
            id = data.getId();
            man.getValuesAt(i).setAuxiliaryFieldId(id);
            man.getValuesAt(i).add();
        }
        
        return man;
    }
    
    public AuxFieldManager update(AuxFieldManager man) throws Exception {
        Integer id;
        AuxFieldViewDO data;
        AuxFieldLocal l;
        AuxFieldValueLocal vl;        
        AuxFieldListItem item;
        

        l = local();
        vl = valueLocal();
        
        try {
        for(int j = 0; j < man.deleteCount(); j++) {
            item = man.getDeletedAt(j);               
            for(int k = 0; k < item.values.count(); k++) 
                vl.delete(item.values.getAuxFieldValueAt(k));   
            
            l.delete(item.field);                        
        }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        for(int i=0; i<man.count(); i++){
            data = man.getAuxFieldAt(i);
            data.setSortOrder(i+1);
            if(data.getId() == null){
                data.setAuxFieldGroupId(man.getAuxFieldGroupId());
                l.add(data);
            }else
                l.update(data);
            
            id = data.getId();
            man.getValuesAt(i).setAuxiliaryFieldId(id);
            man.getValuesAt(i).update();
        }
        
        return man;
    }
    
    public void validate(AuxFieldManager man, ValidationErrorsList list) throws Exception {
        AuxFieldLocal al;
        AuxFieldValueLocal vl;
        AuxFieldValueManager vm;
        AuxFieldValueViewDO data;
        List<ResultRangeNumeric> nrList;
        String value, fieldName;
        ResultRangeNumeric nr;
        Integer typeId,entryId, firstTypeId;
        ArrayList<Integer> dictList;
        int numDefault, numOther, count;

        al = local();
        vl = valueLocal();
        value = null;      
        fieldName = null;
        numDefault = 0;
        numOther = 0;            
        
        for (int i = 0; i < man.count(); i++ ) {
            try {
                al.validate(man.getAuxFieldAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "auxFieldTable", i);
            }
            
            vm = man.getValuesAt(i);
            dictList = new ArrayList<Integer>();
            nrList = new ArrayList<ResultRangeNumeric>();
            firstTypeId = 0;
            numDefault = 0;
            numOther = 0;
            count = vm.count();
            
            for (int j = 0; j < count; j++ ) {
                data = vm.getAuxFieldValueAt(j);
                typeId = data.getTypeId();
                value = data.getValue();
                try {
                    vl.validate(data);
                } catch (Exception e) {
                    DataBaseUtil.mergeException(list, e, "auxFieldValueTable", i, j);
                }
                
                try {
                    if (DataBaseUtil.isSame(typeDefault, typeId)) {                        
                        numDefault++ ;
                    } else if (!DataBaseUtil.isEmpty(typeId)) {
                        numOther++ ;
                        if (DataBaseUtil.isSame(0,firstTypeId))
                            //
                            // Assign the first non-null selected type to firstTypeId
                            // if the type is not "Default".                            
                            //
                            firstTypeId = typeId;
                    }
                    
                    if(numDefault > 1) {
                        fieldName = AuxFieldGroupMeta.getFieldValueTypeId();
                        throw new InconsistencyException("auxMoreThanOneDefaultException");
                    } else if(count == 1 && numDefault == 1) {                        
                        fieldName = AuxFieldGroupMeta.getFieldValueTypeId();
                        throw new InconsistencyException("auxDefaultWithNoOtherTypeException");                        
                    }
                                                              
                    if(DataBaseUtil.isDifferent(firstTypeId,typeId) &&
                                    DataBaseUtil.isDifferent(typeDefault, typeId)) {
                        //
                        // If dissimilar types have been selected for different 
                        // aux field values for an aux field than they cannot be
                        // of more than two kinds. Also one of the kind must be
                        // the type "Default".    
                        //
                        fieldName = AuxFieldGroupMeta.getFieldValueTypeId();
                        throw new InconsistencyException("auxMoreThanOneTypeException");
                    }
                    
                    if (DataBaseUtil.isSame(typeNumeric,typeId)) {
                        nr = new ResultRangeNumeric();
                        fieldName = AuxFieldGroupMeta.getFieldValueValue();
                        nr.setRange(value);
                        addNumericIfNoOverLap(nrList, nr);
                    } else if (DataBaseUtil.isSame(typeDict,typeId)) {
                        entryId = Integer.parseInt(value);   
                        fieldName = AuxFieldGroupMeta.getFieldValueValue();
                        if (entryId == null)
                            throw new ParseException("illegalDictEntryException");

                        if (!dictList.contains(entryId))
                            dictList.add(entryId);
                        else
                            throw new InconsistencyException("auxDictEntryNotUniqueException");
                    }
                } catch (ParseException pe) {
                    list.add(new GridFieldErrorException(pe.getKey(), i, j, fieldName,"auxFieldValueTable"));
                } catch (InconsistencyException ie) {
                    list.add(new GridFieldErrorException(ie.getMessage(), i, j, fieldName,"auxFieldValueTable"));
                }
            }           
        }
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
    
    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private void addNumericIfNoOverLap(List<ResultRangeNumeric> nrList,
                                       ResultRangeNumeric nr) throws InconsistencyException {
         ResultRangeNumeric lr;                  
         for(int i = 0; i < nrList.size(); i++) {
             lr = nrList.get(i);
             if(lr.intersects(nr))                    
                 throw new InconsistencyException("auxNumRangeOverlapException");                                 
         }
         
         nrList.add(nr);                        
    }
    
}
