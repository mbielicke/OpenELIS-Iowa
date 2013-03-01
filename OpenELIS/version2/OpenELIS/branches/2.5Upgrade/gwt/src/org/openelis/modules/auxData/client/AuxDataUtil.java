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
package org.openelis.modules.auxData.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.utilcommon.ResultValidator;

public class AuxDataUtil {
    
    private static HashMap<Integer, ResultValidator.Type> types;
    
    static {
    } 
    
    private static void initialize() throws Exception {
        types = new HashMap<Integer, ResultValidator.Type>();
        try {
            types.put(Constants.dictionary().AUX_ALPHA_LOWER, ResultValidator.Type.ALPHA_LOWER);
            types.put(Constants.dictionary().AUX_ALPHA_UPPER, ResultValidator.Type.ALPHA_UPPER);
            types.put(Constants.dictionary().AUX_DATE, ResultValidator.Type.DATE);
            types.put(Constants.dictionary().AUX_DATE_TIME, ResultValidator.Type.DATE_TIME);
            types.put(Constants.dictionary().AUX_DEFAULT, ResultValidator.Type.DEFAULT);
            types.put(Constants.dictionary().AUX_NUMERIC, ResultValidator.Type.NUMERIC);
            types.put(Constants.dictionary().AUX_TIME, ResultValidator.Type.TIME);
            types.put(Constants.dictionary().AUX_ALPHA_MIXED, ResultValidator.Type.ALPHA_MIXED);
            types.put(Constants.dictionary().AUX_DICTIONARY, ResultValidator.Type.DICTIONARY);
        } catch (Exception e) {
            types = null;
            throw e;
        }
    }

    public static ValidationErrorsList addAuxGroupsFromPanel(Integer panelId, AuxDataManager auxMan) throws Exception {
        ArrayList<IdVO> auxIds;

        auxIds = PanelService.get().fetchAuxIdsByPanelId(panelId);
     
        return addAuxGroupsFromAuxIds(auxIds, auxMan);
    }
    
    public static ValidationErrorsList addAuxGroupsFromAuxIds(ArrayList<IdVO> auxIds, AuxDataManager auxMan) throws Exception {
        int                            i;
        AuxFieldManager                fieldMan;
        ValidationErrorsList           errors;

        if (auxIds == null || auxMan == null)
            return null;
        
        if (types == null)
            initialize();
        
        errors = new ValidationErrorsList();
        
        for (i = 0; i < auxIds.size(); i++) {
            try {
                fieldMan = AuxFieldManager.fetchByGroupIdWithValues(auxIds.get(i).getId());
                addAuxDataFromAuxField(fieldMan, auxMan, errors);
            } catch (Exception anyE2) {
                errors.add(anyE2);
            }
        }
        
        return errors;
    }
    
    public static ValidationErrorsList addAuxGroupsFromAuxFields(ArrayList<AuxFieldManager> fields, AuxDataManager auxMan) throws Exception {
        int                            i;
        ValidationErrorsList           errors;

        if (fields == null || auxMan == null)
            return null;
        
        if (types == null)
            initialize();
        
        errors = new ValidationErrorsList();
        
        for (i = 0; i < fields.size(); i++) {
            try {
                addAuxDataFromAuxField(fields.get(i), auxMan, errors);
            } catch (Exception anyE) {
                errors.add(anyE);
            }
        }
        
        return errors;
    }
    
    public static ResultValidator getValidatorForValues(ArrayList<AuxFieldValueViewDO> values) throws Exception {
        AuxFieldValueViewDO  af;
        DictionaryDO         dict;
        ResultValidator      rv;
        ResultValidator.Type type;
        String               dictEntry;
        
        if (types == null)
            initialize();
        
        rv = new ResultValidator();
        try {
            for (int i = 0; i < values.size(); i++ ) {
                af = values.get(i);
                dictEntry = null;
                
                type = types.get(af.getTypeId());
                if (type == ResultValidator.Type.DICTIONARY) {
                    dict = DictionaryCache.getById(new Integer(af.getValue()));
                    dictEntry = dict.getEntry();
                }
                rv.addResult(af.getId(), null, type, null, null, af.getValue(), dictEntry);                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        return rv;
    }
    
    private static void addAuxDataFromAuxField(AuxFieldManager fieldMan, AuxDataManager auxMan,
                                               ValidationErrorsList errors) throws Exception {
        int                            i, j, count;
        ArrayList<AuxFieldValueViewDO> values;
        AuxDataViewDO                  data;
        AuxFieldViewDO                 field;
        AuxFieldValueViewDO            defaultVal, value;
        Integer                        validId;
        ResultValidator                validator;
        
        count = fieldMan.count();
        if (count > 0) {
            field = fieldMan.getAuxFieldAt(0);
            /*
             * check to see if these fields have already been added to the AuxDataManager
             */
            if (groupAddedToParent(field.getAuxFieldGroupId(), auxMan)) {
                errors.add(new FormErrorWarning(Messages.get().auxGrpAlreadyAddedException(field.getAuxFieldGroupName())));
                return;
            }                                       
        }
        
        for (i = 0; i < count; i++) {
            field = fieldMan.getAuxFieldAt(i);
            
            if ("Y".equals(field.getIsActive())) {
                values = fieldMan.getValuesAt(i).getValues();
                defaultVal = fieldMan.getValuesAt(i).getDefaultValue();

                data = new AuxDataViewDO();
                data.setAuxFieldId(field.getId());
                data.setIsReportable(field.getIsReportable());
                data.setGroupId(field.getAuxFieldGroupId());
                
                validator = getValidatorForValues(values);
                if (defaultVal != null) {
                    try {
                        validId = validator.validate(null, defaultVal.getValue());
                        for (j = 0; j < values.size(); j++) {
                            value = values.get(j);
                            if (value.getId().equals(validId)) {
                                if (Constants.dictionary().AUX_DICTIONARY.equals(value.getTypeId())) {
                                    data.setTypeId(value.getTypeId());
                                    data.setValue(value.getValue());
                                    data.setDictionary(value.getDictionary());
                                } else {
                                    data.setTypeId(value.getTypeId());
                                    data.setValue(defaultVal.getValue());
                                }
                                break;
                            }
                        }
                    } catch (ParseException parE) {
                        errors.add(new FormErrorWarning(Messages.get().illegalDefaultValueForAuxFieldException(
                                                            defaultVal.getValue(),
                                                            field.getAnalyteName())));
                    }
                } else {
                    data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                }

                auxMan.addAuxDataFieldAndValues(data, field, values);
            }
        }
    } 
    
    private static boolean groupAddedToParent(Integer groupId, AuxDataManager auxDataMan) {
        AuxFieldViewDO data;
        
        for (int i = 0; i < auxDataMan.count(); i++) {
            data = auxDataMan.getAuxFieldAt(i);
            if (data.getAuxFieldGroupId().equals(groupId)) 
                return true;            
        }
        
        return false;
    }
}