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
package org.openelis.modules.PTSampleLogin.server;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
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

public class PTSampleLoginService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>{

    public RPC abort(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetch(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/PTSampleLogin.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/PTSampleLogin.xsl"));
        
        DataModel sampleStatusDropdownField = (DataModel)CachingManager.getElement("InitialData", "sampleStatusDropdown");
        DataModel ptProviderNamesDropdownField = (DataModel)CachingManager.getElement("InitialData", "ptProviderNamesDropdown");
        DataModel ptDepartmentNamesDropdownField = (DataModel)CachingManager.getElement("InitialData", "ptDepartmentNamesDropdown");
        
        //sample status dropdown
        if(sampleStatusDropdownField == null){
            sampleStatusDropdownField = getInitialModel("statuses");
            CachingManager.putElement("InitialData", "sampleStatusDropdown", sampleStatusDropdownField);
        }
        //pt provider names dropdown
        if(ptProviderNamesDropdownField == null){
            ptProviderNamesDropdownField = getInitialModel("ptProviders");
            CachingManager.putElement("InitialData", "ptProviderNamesDropdown", ptProviderNamesDropdownField);
            }
        //pt department names dropdown
        if(ptDepartmentNamesDropdownField == null){
            ptDepartmentNamesDropdownField = getInitialModel("ptDepartments");
            CachingManager.putElement("InitialData", "ptDepartmentNamesDropdown", ptDepartmentNamesDropdownField);
        }
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("sampleStaus", sampleStatusDropdownField);
        map.put("providerNames", ptProviderNamesDropdownField);
        map.put("departmentNames", ptDepartmentNamesDropdownField);
        
        return map;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC getScreen(RPC rpc){
        return rpc;
    }
    
    public DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("statuses"))
            id = remote.getCategoryId("sample_status");
        else if(cat.equals("ptProviders"))
            id = remote.getCategoryId("pt_provider_names");
        else if(cat.equals("ptDepartments"))
            id = remote.getCategoryId("pt_department_names");
        
        List entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        DataModel returnModel = new DataModel();
        
        if(entries.size() > 0){ 
            //create a blank entry to begin the list
            DataSet blankset = new DataSet();
            
            StringObject blankStringId = new StringObject("");
            NumberObject blankNumberId = new NumberObject(0);
            BooleanObject blankSelected = new BooleanObject();
            
            blankset.add(blankStringId);
            
            
            if(cat.equals("contactType"))
                blankset.setKey(blankNumberId);
            else
                blankset.setKey(blankStringId);         
            
            returnModel.add(blankset);
        }
        int i=0;
        while(i < entries.size()){
            DataSet set = new DataSet();
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            //id
            Integer dropdownId = resultDO.getId();
            //entry
            String dropdownText = resultDO.getName();
            
            StringObject textObject = new StringObject();
            StringObject stringId = new StringObject();
            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
        
            textObject.setValue(dropdownText);
            set.add(textObject);
            
            if(cat.equals("contactType")){
                numberId.setValue(dropdownId);
                set.setKey(numberId);
            }else{
                stringId.setValue(dropdownText);
                set.setKey(stringId);           
            }
            
            returnModel.add(set);
            
            i++;
        }       
        
        return returnModel;
    }
}
