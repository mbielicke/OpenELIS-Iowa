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
package org.openelis.modules.SDWISSampleLogin.server;

import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginForm;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.DictionaryRemote;
import org.openelis.server.constants.Constants;

public class SDWISSampleLoginService implements AppScreenFormServiceInt<SDWISSampleLoginForm, Query<TableDataRow<Integer>>>{

    public SDWISSampleLoginForm abort(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public SDWISSampleLoginForm commitAdd(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public SDWISSampleLoginForm commitDelete(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> data) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public SDWISSampleLoginForm commitUpdate(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public SDWISSampleLoginForm fetch(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public SDWISSampleLoginForm fetchForUpdate(SDWISSampleLoginForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    
    public SDWISSampleLoginForm getScreen(SDWISSampleLoginForm rpc) {
        try {
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/SDWISSampleLogin.xsl");
        }catch(Exception e) {
            e.printStackTrace();
        }
        rpc.sampleStatus = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "sampleStatusDropdown");
        rpc.sampleTypes = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "sdwisSampleTypeDropdown");
        rpc.sampleCats = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "sdwisSampleCategoryDropdown");
        rpc.leadSampleTypes = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "sdwisLeadSampleTypeDropdown");
                
        //sample status dropdown
        if(rpc.sampleStatus == null){
            rpc.sampleStatus = getInitialModel("statuses");
            CachingManager.putElement("InitialData", "sampleStatusDropdown", rpc.sampleStatus);
        }
        //sdwis sample type dropdown
        if(rpc.sampleTypes == null){
            rpc.sampleTypes = getInitialModel("sampleType");
            CachingManager.putElement("InitialData", "sdwisSampleTypeDropdown", rpc.sampleTypes);
        }
        //sdwis sample category dropdown
        if(rpc.sampleCats == null){
            rpc.sampleCats = getInitialModel("category");
            CachingManager.putElement("InitialData", "sdwisSampleCategoryDropdown", rpc.sampleCats);
            }
        //sdwis lead sample type dropdown
        if(rpc.leadSampleTypes == null){
            rpc.leadSampleTypes = getInitialModel("leadSampleType");
            CachingManager.putElement("InitialData", "sdwisLeadSampleTypeDropdown", rpc.leadSampleTypes);
        }
        return rpc;
    }
    
    public TableDataModel<TableDataRow<String>> getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        DictionaryRemote dictRemote = (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");
        
        try {
            if (cat.equals("statuses"))
                id = (remote.fetchBySystemName("sample_status")).getId();
            else if (cat.equals("sampleType"))
                id = (remote.fetchBySystemName("sdwis_sample_type")).getId();
            else if (cat.equals("category"))
                id = (remote.fetchBySystemName("sdwis_sample_category")).getId();
            else if (cat.equals("leadSampleType"))
                id = (remote.fetchBySystemName("sdwis_lead_sample_type")).getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        List<IdNameVO> entries = new ArrayList();
        if(id != null) {            
            try {
                entries = dictRemote.fetchIdEntryByCategoryId(id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }
        
        //we need to build the model to return
        TableDataModel<TableDataRow<String>> returnModel = new TableDataModel<TableDataRow<String>>();
        
        if(entries.size() > 0){          
            
            returnModel.add(new TableDataRow<String>(" ",new StringObject(" ")));
        }

        for(IdNameVO resultDO : entries) {
            returnModel.add(new TableDataRow<String>(resultDO.getName(),new StringObject(resultDO.getName())));
            
        }       
        
        return returnModel;
    }
}
