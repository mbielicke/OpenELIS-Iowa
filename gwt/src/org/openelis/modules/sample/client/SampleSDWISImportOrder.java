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
import java.util.Date;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;

public class SampleSDWISImportOrder extends ImportOrder {
    protected static final String AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    protected static final String PWS_SERVICE_URL      = "org.openelis.modules.pws.server.PWSService";

    protected ScreenService       auxDataService, pwsService;

    public SampleSDWISImportOrder() {
        auxDataService = new ScreenService("controller?service=" + AUX_DATA_SERVICE_URL);
        pwsService = new ScreenService("controller?service=" + PWS_SERVICE_URL);
    }

    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        Integer auxGroupId;
        AuxDataDO auxData;
        ArrayList<AuxDataViewDO> auxDataList;

        if (orderId == null)
            return null;

        auxData = new AuxDataDO();
        auxData.setReferenceId(orderId);
        auxData.setReferenceTableId(ReferenceTable.ORDER);

        orderMan = null;
        auxDataList = auxDataService.callList("fetchByRefId", auxData);

        // we don't want to use a hard-coded reference to aux group (language).
        // Use one level indirect by looking up system variable that points to
        // the aux group
        auxGroupId = ((IdVO)auxDataService.call("getAuxGroupIdFromSystemVariable",
                                                "sample_sdwis_aux_data")).getId();

        // grab order for report to/bill to
        loadReportToBillTo(orderId, manager);

        // grab order tests including number of bottles
        loadSampleItems(orderId, manager);

        // inject the data into the manager
        return importData(auxDataList, auxGroupId, manager);
    }

    private ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId, SampleManager manager) throws Exception {
        int i;
        AuxDataViewDO auxData;
        String analyteId;
        DictionaryDO dictDO;
        Integer value;
        PWSDO pwsDo;
        ValidationErrorsList errorsList;

        errorsList = new ValidationErrorsList();
        for (i = 0; i < auxDataList.size(); i++ ) {
            auxData = auxDataList.get(i);
            try {
                if (auxData.getGroupId().equals(envAuxGroupId)) {
                    analyteId = auxData.getAnalyteExternalId();

                    if (analyteId.equals("smpl_collected_date")) {
                        manager.getSample()
                               .setCollectionDate(Datetime.getInstance(Datetime.YEAR,
                                                                       Datetime.DAY,
                                                                       new Date(auxData.getValue())));
                    } else if (analyteId.equals("smpl_collected_time")) {
                        DateField df = new DateField();
                        df.setBegin(Datetime.HOUR);
                        df.setEnd(Datetime.MINUTE);
                        df.setStringValue(auxData.getValue());
                        manager.getSample().setCollectionTime(df.getValue());
                    } else if (analyteId.equals("smpl_client_ref")) {
                        manager.getSample().setClientReference(auxData.getValue());
                    } else if (analyteId.equals("pws_id")) {
                        if (auxData.getValue() != null) {
                            try {
                                pwsDo = pwsService.call("fetchPwsByNumber0", auxData.getValue());
                                ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                                .setPwsId(pwsDo.getId());
                                ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                                .setPwsName(pwsDo.getName());
                                ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                                .setPwsNumber0(pwsDo.getNumber0());
                            } catch (NotFoundException e) {
                                errorsList.add(new FormErrorException("orderImportError",
                                                                      "pws id",
                                                                      auxData.getValue()));
                            }
                        }
                    } else if (analyteId.equals("state_lab_num")) {
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setStateLabId(new Integer(auxData.getValue()));
                    } else if (analyteId.equals("facility_id")) {
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setFacilityId(auxData.getValue());
                    } else if (analyteId.equals("sample_type")) {
                        value = null;
                        dictDO = getDropdownByKey(auxData.getValue(), "sdwis_sample_type");
                        if (dictDO != null)
                            value = dictDO.getId();
                        else if (auxData.getValue() != null)
                            errorsList.add(new FormErrorException("orderImportError",
                                                                  "sample type",
                                                                  auxData.getValue()));
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setSampleTypeId(value);
                    } else if (analyteId.equals("sample_cat")) {
                        value = null;
                        dictDO = getDropdownByKey(auxData.getValue(), "sdwis_sample_category");
                        if (dictDO != null)
                            value = dictDO.getId();
                        else if (auxData.getValue() != null)
                            errorsList.add(new FormErrorException("orderImportError",
                                                                  "sample category",
                                                                  auxData.getValue()));

                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setSampleCategoryId(value);
                    } else if (analyteId.equals("sample_pt_id")) {
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setSamplePointId(auxData.getValue());
                    } else if (analyteId.equals("location")) {
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setLocation(auxData.getValue());
                    } else if (analyteId.equals("collector")) {
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS()
                                                                        .setCollector(auxData.getValue());
                    }
                } else {
                    manager.getAuxData().addAuxData(auxData);
                }
            } catch (Exception e) {
                // problem with aux input, ignore
            }
        }

        if (errorsList.size() > 0)
            return errorsList;

        return null;
    }

    private DictionaryDO getDropdownByKey(String key, String dictSystemName) {
        Integer id;
        ArrayList<DictionaryDO> entries;

        if (key != null) {
            try {
                id = new Integer(key);
                entries = CategoryCache.getBySystemName(dictSystemName);
                for (DictionaryDO data : entries) {
                    if (id.equals(data.getId()))
                        return data;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}