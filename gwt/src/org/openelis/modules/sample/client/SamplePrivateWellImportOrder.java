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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.Date;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SamplePrivateWellManager;

public class SamplePrivateWellImportOrder extends ImportOrder {
    protected static final String AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    protected static final String PROJECT_SERVICE_URL = "org.openelis.modules.project.server.ProjectService";
    protected ScreenService auxDataService, projectService;

    public SamplePrivateWellImportOrder(){
        auxDataService = new ScreenService("OpenELISServlet?service="+AUX_DATA_SERVICE_URL);
        projectService = new ScreenService("controller?service="+PROJECT_SERVICE_URL);
    }
    
    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        //grab order aux data
        AuxDataDO auxData = new AuxDataDO();
        auxData.setReferenceId(orderId);
        auxData.setReferenceTableId(ReferenceTable.ORDER);
        ArrayList<AuxDataViewDO> auxDataList;
        Integer auxGroupId;
        
        orderMan = null;
        auxDataList = auxDataService.callList("fetchByRefId", auxData);
        
        // grab aux group id from sys variable ish
        auxGroupId = ((IdVO)auxDataService.call("getAuxGroupIdFromSystemVariable", "sample_well_aux_data")).getId();

        //load order report to/bill to
        loadReportToBillTo(orderId, manager);
        
        //grab order tests including number of bottles
        loadSampleItems(orderId, manager);
        
        //inject the data into the manager
        return importData(auxDataList, auxGroupId, manager);
    }
    
    private ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList, Integer envAuxGroupId, SampleManager manager) throws Exception {
        AuxDataViewDO auxData;
        String analyteId;
        ValidationErrorsList errorsList;
        
        errorsList = new ValidationErrorsList();
        //aux data
        for(int i=0; i<auxDataList.size(); i++){
            auxData = auxDataList.get(i);
            
            try{
                if(auxData.getGroupId().equals(envAuxGroupId)){
                    analyteId = auxData.getAnalyteExternalId();
                    
                    if(analyteId.equals("smpl_collected_date")){
                        manager.getSample().setCollectionDate(
                                            Datetime.getInstance(Datetime.YEAR, Datetime.DAY, new Date(auxData.getValue())));
                    }else if(analyteId.equals("smpl_collected_time")){
                        DateField df = new DateField();
                        df.setBegin(Datetime.HOUR);
                        df.setEnd(Datetime.MINUTE);
                        df.setStringValue(auxData.getValue());
                        manager.getSample().setCollectionTime(df.getValue());
                    }else if(analyteId.equals("smpl_client_ref")){
                        manager.getSample().setClientReference(auxData.getValue());
                    }else if(analyteId.equals("report_to")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().setReportToName(auxData.getValue());
/*                    }else if(analyteId.equals("report_to_mult_unit")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().getasetReportToName(auxData.getValue());
                        report_to_mult_unit
                    }else if(analyteId.equals("report_to_street_add")){
                        report_to_street_add
                    }else if(analyteId.equals("report_to_city")){
                        report_to_city
                    }else if(analyteId.equals("report_to_state")){
                        report_to_state
                    }else if(analyteId.equals("report_to_zip_code")){
                        report_to_zip_code
                    }else if(analyteId.equals("report_to_phone_num")){
                        report_to_phone_num
                    }else if(analyteId.equals("report_to_fax_num")){
                        report_to_fax_num*/
                    }else if(analyteId.equals("location")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().setLocation(auxData.getValue());
                    }else if(analyteId.equals("loc_mult_unit")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().
                        getLocationAddressDO().setMultipleUnit(auxData.getValue());
                    }else if(analyteId.equals("loc_street_address")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().
                        getLocationAddressDO().setStreetAddress(auxData.getValue());
                    }else if(analyteId.equals("loc_city")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().
                        getLocationAddressDO().setCity(auxData.getValue());
                    }else if(analyteId.equals("loc_state")){
                        if(validateDropdownValue(auxData.getValue(), "state"))
                            ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().
                                getLocationAddressDO().setState(auxData.getValue());
                        else if(auxData.getValue() != null)
                            errorsList.add(new FormErrorException("orderImportError", "state", auxData.getValue()));
                    }else if(analyteId.equals("loc_zip_code")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().
                        getLocationAddressDO().setZipCode(auxData.getValue());
                    }else if(analyteId.equals("owner")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().setOwner(auxData.getValue());
                    }else if(analyteId.equals("collector")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().setCollector(auxData.getValue());
                    }else if(analyteId.equals("well_number")){
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell().setWellNumber(new Integer(auxData.getValue()));
                    }else if(analyteId.equals("project_name") && auxData.getValue() != null){
                        ProjectDO proj;
                        SampleProjectViewDO projectDO;
                        
                        proj = projectService.call("fetchSingleByName", auxData.getValue());
                        
                        if(proj != null){
                            projectDO = new SampleProjectViewDO();
                            projectDO.setIsPermanent("Y");
                            projectDO.setProjectId(proj.getId());
                            projectDO.setProjectName(proj.getName());
                            projectDO.setProjectDescription(proj.getDescription());
                                
                            manager.getProjects().addFirstPermanentProject(projectDO);
                             
                        }else
                            errorsList.add(new FormErrorException("orderImportError", "project", auxData.getValue()));
                    }
                        
                }else{
                    manager.getAuxData().addAuxData(auxData);
                }
            }catch(Exception e){
                //problem with aux input, ignore
            }
        }
        
        if(errorsList.size() > 0)
            return errorsList;
        
        return null;
    }
    
    private boolean validateDropdownValue(String entry, String dictSystemName){
        ArrayList<DictionaryDO> entries;
        DictionaryDO dictDO;
        boolean valid = false;
        
        entries = DictionaryCache.getListByCategorySystemName(dictSystemName);
        
        for(int i=0; i<entries.size(); i++){
            dictDO = entries.get(i);
            
            if(entry.equals(dictDO.getEntry())){
                valid = true;
                break;
            }
        }
        
        return valid;
    }
}
