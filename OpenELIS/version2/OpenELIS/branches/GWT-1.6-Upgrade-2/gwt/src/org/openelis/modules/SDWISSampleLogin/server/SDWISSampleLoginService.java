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

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SDWISSampleLoginService implements AppScreenFormServiceInt<DefaultRPC, Integer>{

    public DefaultRPC abort(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitAdd(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitDelete(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitUpdate(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetch(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetchForUpdate(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/SDWISSampleLogin.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/SDWISSampleLogin.xsl"));
        
        DataModel sampleStatusDropdownField = (DataModel)CachingManager.getElement("InitialData", "sampleStatusDropdown");
        DataModel sdwisSampleTypeDropdownField = (DataModel)CachingManager.getElement("InitialData", "sdwisSampleTypeDropdown");
        DataModel sdwisSampleCategoryDropdownField = (DataModel)CachingManager.getElement("InitialData", "sdwisSampleCategoryDropdown");
        DataModel sdwisLeadSampleTypeDropdownField = (DataModel)CachingManager.getElement("InitialData", "sdwisLeadSampleTypeDropdown");
                
        //sample status dropdown
        if(sampleStatusDropdownField == null){
            sampleStatusDropdownField = getInitialModel("statuses");
            CachingManager.putElement("InitialData", "sampleStatusDropdown", sampleStatusDropdownField);
        }
        //sdwis sample type dropdown
        if(sdwisSampleTypeDropdownField == null){
            sdwisSampleTypeDropdownField = getInitialModel("sampleType");
            CachingManager.putElement("InitialData", "sdwisSampleTypeDropdown", sdwisSampleTypeDropdownField);
        }
        //sdwis sample category dropdown
        if(sdwisSampleCategoryDropdownField == null){
            sdwisSampleCategoryDropdownField = getInitialModel("category");
            CachingManager.putElement("InitialData", "sdwisSampleCategoryDropdown", sdwisSampleCategoryDropdownField);
            }
        //sdwis lead sample type dropdown
        if(sdwisLeadSampleTypeDropdownField == null){
            sdwisLeadSampleTypeDropdownField = getInitialModel("leadSampleType");
            CachingManager.putElement("InitialData", "sdwisLeadSampleTypeDropdown", sdwisLeadSampleTypeDropdownField);
        }
        
        HashMap<String,FieldType> map = new HashMap<String,FieldType>();
        map.put("xml", xml);
        map.put("sampleStatus", sampleStatusDropdownField);
        map.put("sampleTypes", sdwisSampleTypeDropdownField);
        map.put("sampleCats", sdwisSampleCategoryDropdownField);
        map.put("leadSampleTypes", sdwisLeadSampleTypeDropdownField);
        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DefaultRPC getScreen(DefaultRPC rpc) {
        return rpc;
    }
    
    public DataModel<String> getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("statuses"))
            id = remote.getCategoryId("sample_status");
        else if(cat.equals("sampleType"))
            id = remote.getCategoryId("sdwis_sample_type");
        else if(cat.equals("category"))
            id = remote.getCategoryId("sdwis_sample_category");
        else if(cat.equals("leadSampleType"))
            id = remote.getCategoryId("sdwis_lead_sample_type");
        
        List entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        DataModel<String> returnModel = new DataModel<String>();
        
        if(entries.size() > 0){          
            
            returnModel.add(new DataSet<String>(" ",new StringObject(" ")));
        }
        int i=0;
        while(i < entries.size()){
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            //entry
            returnModel.add(new DataSet<String>(resultDO.getName(),new StringObject(resultDO.getName())));
            
            i++;
        }       
        
        return returnModel;
    }
}