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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;

public class SampleSDWISImportOrder {
    protected static final String AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    
    protected ScreenService auxDataService;

    public SampleSDWISImportOrder(){
        auxDataService = new ScreenService("controller?service="+AUX_DATA_SERVICE_URL);
    }
    
    public void importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        //grab order aux data
        AuxDataDO auxData = new AuxDataDO();
        auxData.setReferenceId(orderId);
        auxData.setReferenceTableId(ReferenceTable.ORDER);
        ArrayList<AuxDataViewDO> auxDataList;
        Integer auxGroupId;
        
        auxDataList = auxDataService.callList("fetchByRefId", auxData);
        
        // grab aux group id from sys variable ish
        auxGroupId = ((IdVO)auxDataService.call("getAuxGroupIdFromSystemVariable", "sample_sdwis_aux_data")).getId();

        //grab order for report to/bill to
        
        //grab order tests including number of bottles
        
        //inject the data into the manager
        importData(auxDataList, auxGroupId, manager);
    }
    
    private void importData(ArrayList<AuxDataViewDO> auxDataList, Integer envAuxGroupId, SampleManager manager) throws Exception {
        AuxDataViewDO auxData;
        String analyteId;
        DictionaryDO dictDO;
        Integer value;
        //aux data
        for(int i=0; i<auxDataList.size(); i++){
            auxData = auxDataList.get(i);
            try{
                if(auxData.getGroupId().equals(envAuxGroupId)){
                    analyteId = auxData.getAnalyteExternalId();
                    if(analyteId.equals("pws_id"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setPwsId(auxData.getValue());
                    else if(analyteId.equals("state_lab_num"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setStateLabId(new Integer(auxData.getValue()));
                    else if(analyteId.equals("facility_id"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setFacilityId(new Integer(auxData.getValue()));
                    else if(analyteId.equals("sample_type")){
                        dictDO = validateDropdownValue(auxData.getValue(), "sdwis_sample_type");
                        if(dictDO != null)
                            value = dictDO.getId();
                        else
                            value = null;
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setSampleTypeId(value);
                        
                    }else if(analyteId.equals("sample_cat")){
                        dictDO = validateDropdownValue(auxData.getValue(), "sdwis_sample_category");
                        if(dictDO != null)
                            value = dictDO.getId();
                        else
                            value = null;
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setSampleCategoryId(value);
                        
                    }else if(analyteId.equals("sample_pt_id"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setSamplePointId(auxData.getValue());
                    else if(analyteId.equals("sample_point_desc"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setLocation(auxData.getValue());
                    else if(analyteId.equals("collector"))
                        ((SampleSDWISManager)manager.getDomainManager()).getSDWIS().setCollector(auxData.getValue());
                    
                }else{
                    manager.getAuxData().addAuxData(auxData);
                }
            }catch(Exception e){
                //problem with aux input, ignore
            }
        }
    }
    
    private DictionaryDO validateDropdownValue(String entry, String dictSystemName){
        ArrayList<DictionaryDO> entries;
        DictionaryDO dictDO;
        
        entries = DictionaryCache.getListByCategorySystemName(dictSystemName);
        
        for(int i=0; i<entries.size(); i++){
            dictDO = entries.get(i);
            
            if(entry.equals(dictDO.getEntry()))
                return dictDO;
        }
        
        return null;
    }
}
