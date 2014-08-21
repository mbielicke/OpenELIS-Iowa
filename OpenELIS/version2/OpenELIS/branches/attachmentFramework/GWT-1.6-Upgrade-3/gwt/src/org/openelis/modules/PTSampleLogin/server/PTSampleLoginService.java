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
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.PTSampleLogin.client.PTSampleLoginForm;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PTSampleLoginService implements AppScreenFormServiceInt<PTSampleLoginForm, Query<TableDataRow<Integer>>>{

    public PTSampleLoginForm abort(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public PTSampleLoginForm commitAdd(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public PTSampleLoginForm commitDelete(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public PTSampleLoginForm commitUpdate(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public PTSampleLoginForm fetch(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public PTSampleLoginForm fetchForUpdate(PTSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public PTSampleLoginForm getScreen(PTSampleLoginForm rpc){
        try {
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/PTSampleLogin.xsl");
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        rpc.sampleStatus = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "sampleStatusDropdown");
        rpc.ptProviderNames = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "ptProviderNamesDropdown");
        rpc.ptDepartmentNames = (TableDataModel<TableDataRow<String>>)CachingManager.getElement("InitialData", "ptDepartmentNamesDropdown");
        
        //sample status dropdown
        if(rpc.sampleStatus == null){
            rpc.sampleStatus = getInitialModel("statuses");
            CachingManager.putElement("InitialData", "sampleStatusDropdown", rpc.sampleStatus);
        }
        //pt provider names dropdown
        if(rpc.ptProviderNames == null){
            rpc.ptProviderNames = getInitialModel("ptProviders");
            CachingManager.putElement("InitialData", "ptProviderNamesDropdown", rpc.ptProviderNames);
            }
        //pt department names dropdown
        if(rpc.ptDepartmentNames == null){
            rpc.ptDepartmentNames= getInitialModel("ptDepartments");
            CachingManager.putElement("InitialData", "ptDepartmentNamesDropdown", rpc.ptDepartmentNames);
        }
        
        return rpc;
    }
    
    public TableDataModel<TableDataRow<String>> getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("statuses"))
            id = remote.getCategoryId("sample_status");
        else if(cat.equals("ptProviders"))
            id = remote.getCategoryId("pt_provider_names");
        else if(cat.equals("ptDepartments"))
            id = remote.getCategoryId("pt_department_names");
        
        List<IdNameDO> entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        TableDataModel<TableDataRow<String>> returnModel = new TableDataModel<TableDataRow<String>>();
        
        if(entries.size() > 0){ 
            returnModel.add(new TableDataRow<String>("",new StringObject("")));
        }
        for(IdNameDO resultDO : entries){
            returnModel.add(new TableDataRow<String>(resultDO.getName(),new StringObject(resultDO.getName())));
        }       
        
        return returnModel;
    }
}
