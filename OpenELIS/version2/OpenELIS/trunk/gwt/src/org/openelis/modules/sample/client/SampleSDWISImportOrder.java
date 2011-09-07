/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;

public class SampleSDWISImportOrder extends ImportOrder {
    protected static final String PWS_SERVICE_URL      = "org.openelis.modules.pws.server.PWSService";
    
    protected ScreenService       pwsService;
    
    public SampleSDWISImportOrder() {
        pwsService = new ScreenService("controller?service=" + PWS_SERVICE_URL);
    }

    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        return super.importOrderInfo(orderId, manager, "sample_sdwis_aux_data");
    }
    
    protected ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId, SampleManager manager) throws Exception {
        AuxDataViewDO data;
        String analyteId;
        DictionaryDO dict;
        Integer value;
        SampleDO sample;
        PWSDO pws;
        SampleSDWISViewDO sdwis;
        DateField df;
        ValidationErrorsList errorsList;

        errorsList = new ValidationErrorsList();
        sample = manager.getSample();
        sdwis = ((SampleSDWISManager)manager.getDomainManager()).getSDWIS();
        
        for (int i = 0; i < auxDataList.size(); i++ ) {
            data = auxDataList.get(i);
            try {
                if ( !data.getGroupId().equals(envAuxGroupId)) {
                    saveAuxData(data, errorsList, manager);
                    continue;
                }
                analyteId = data.getAnalyteExternalId();
                if ("smpl_collected_date".equals(analyteId)) {
                    df = new DateField();
                    df.setBegin(Datetime.YEAR);
                    df.setEnd(Datetime.DAY);
                    df.setStringValue(data.getValue());
                    sample.setCollectionDate(df.getValue());
                } else if ("smpl_collected_time".equals(analyteId)) {
                    df = new DateField();
                    df.setBegin(Datetime.HOUR);
                    df.setEnd(Datetime.MINUTE);
                    df.setStringValue(data.getValue());
                    sample.setCollectionTime(df.getValue());
                } else if ("smpl_client_ref".equals(analyteId)) {
                    sample.setClientReference(data.getValue());
                } else if ("pws_id".equals(analyteId)) {
                    if (data.getValue() != null) {
                        try {
                            pws = pwsService.call("fetchPwsByNumber0", data.getValue());
                            sdwis.setPwsId(pws.getId());
                            sdwis.setPwsName(pws.getName());
                            sdwis.setPwsNumber0(pws.getNumber0());
                        } catch (NotFoundException e) {
                            errorsList.add(new FormErrorException("orderImportError", "pws id",
                                                                  data.getValue()));
                        }
                    }
                } else if ("state_lab_num".equals(analyteId)) {
                    sdwis.setStateLabId(new Integer(data.getValue()));
                } else if ("facility_id".equals(analyteId)) {
                    sdwis.setFacilityId(data.getValue());
                } else if ("sample_type".equals(analyteId)) {
                    value = null;
                    dict = getDropdownByKey(data.getValue(), "sdwis_sample_type");
                    if (dict != null)
                        value = dict.getId();
                    else if (data.getValue() != null)
                        errorsList.add(new FormErrorException("orderImportError", "sample type",
                                                              data.getValue()));
                    sdwis.setSampleTypeId(value);
                } else if ("sample_cat".equals(analyteId)) {
                    value = null;
                    dict = getDropdownByKey(data.getValue(), "sdwis_sample_category");
                    if (dict != null)
                        value = dict.getId();
                    else if (data.getValue() != null)
                        errorsList.add(new FormErrorException("orderImportError",
                                                              "sample category", data.getValue()));

                    sdwis.setSampleCategoryId(value);
                } else if ("sample_pt_id".equals(analyteId)) {
                    sdwis.setSamplePointId(data.getValue());
                } else if ("location".equals(analyteId)) {
                    sdwis.setLocation(data.getValue());
                } else if ("collector".equals(analyteId)) {
                    sdwis.setCollector(data.getValue());
                }
            } catch (Exception e) {
                // problem with aux input, ignore
            }
        }

        if (errorsList.size() > 0)
            return errorsList;

        return null;
    }
}