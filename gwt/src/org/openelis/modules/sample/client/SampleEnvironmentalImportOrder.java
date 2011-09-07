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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;

public class SampleEnvironmentalImportOrder extends ImportOrder {
    protected static final String  PROJECT_SERVICE_URL  = "org.openelis.modules.project.server.ProjectService";
    protected ScreenService        projectService;

    public SampleEnvironmentalImportOrder() {
        projectService = new ScreenService("controller?service=" + PROJECT_SERVICE_URL);
    }
    
    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        return super.importOrderInfo(orderId, manager, "sample_env_aux_data");
    }

    protected ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId, SampleManager manager) throws Exception {
        AuxDataViewDO data;
        String analyteId;        
        SampleDO sample;
        ProjectDO proj;
        SampleProjectViewDO smplProj;
        SampleEnvironmentalDO env;
        AddressDO locAddr;
        DateField df;
        ValidationErrorsList errorsList;

        errorsList = new ValidationErrorsList();
        sample = manager.getSample();
        env = ((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental();
        locAddr = env.getLocationAddress();
        
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
                } else if ("is_hazardous".equals(analyteId)) {
                    if (data.getValue() != null &&
                        "yes".equals(DictionaryCache.getSystemNameById(new Integer(data.getValue()))))
                        env.setIsHazardous("Y");
                    else
                        env.setIsHazardous("N");
                } else if ("collector".equals(analyteId)) {
                    env.setCollector(data.getValue());
                } else if ("location".equals(analyteId)) {
                    env.setLocation(data.getValue());
                } else if ("loc_mult_unit".equals(analyteId)) {
                    locAddr.setMultipleUnit(data.getValue());
                } else if ("loc_street_address".equals(analyteId)) {
                    locAddr.setStreetAddress(data.getValue());
                } else if ("loc_city".equals(analyteId)) {
                    locAddr.setCity(data.getValue());
                } else if ("loc_state".equals(analyteId)) {
                    if (getDropdownByValue(data.getValue(), "state") != null)
                        locAddr.setState(data.getValue());
                    else if (data.getValue() != null)
                        errorsList.add(new FormErrorException("orderImportError", "state",
                                                              data.getValue()));
                } else if (analyteId.equals("loc_zip_code")) {
                    locAddr.setZipCode(data.getValue());
                } else if ("loc_country".equals(analyteId)) {
                    if (getDropdownByValue(data.getValue(), "country") != null)
                        locAddr.setCountry(data.getValue());
                    else if (data.getValue() != null)
                        errorsList.add(new FormErrorException("orderImportError", "country",
                                                              data.getValue()));
                } else if ("priority".equals(analyteId)) {
                    env.setPriority(new Integer(data.getValue()));
                } else if ("collector_phone".equals(analyteId)) {
                    env.setCollectorPhone(data.getValue());
                } else if ("description".equals(analyteId)) {
                    env.setDescription(data.getValue());
                } else if ("project_name".equals(analyteId) && data.getValue() != null) {
                    proj = projectService.call("fetchSingleByName", data.getValue());
                    if (proj != null) {
                        smplProj = new SampleProjectViewDO();
                        smplProj.setIsPermanent("Y");
                        smplProj.setProjectId(proj.getId());
                        smplProj.setProjectName(proj.getName());
                        smplProj.setProjectDescription(proj.getDescription());

                        manager.getProjects().addFirstPermanentProject(smplProj);

                    } else
                        errorsList.add(new FormErrorException("orderImportError", "project",
                                                              data.getValue()));
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