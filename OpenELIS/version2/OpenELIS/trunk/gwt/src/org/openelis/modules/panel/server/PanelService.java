/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.panel.server;

import java.util.HashMap;
import java.util.List;

import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PanelRemote;
import org.openelis.server.constants.Constants;

public class PanelService implements AppScreenFormServiceInt {

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl");
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl"));
                        
        HashMap<String, DataObject> map = new HashMap<String, DataObject>();
        map.put("xml", xml);
        map.put("allTests", getTestMethodNames());
        return map;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DataModel getTestMethodNames(){
       PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
       DataModel model = new DataModel();
       List<QaEventTestDropdownDO> list = remote.getTestMethodNames();
       for(int iter = 0; iter < list.size(); iter++){
           QaEventTestDropdownDO qaeDO = list.get(iter);
           DataSet set = new DataSet();
           set.addObject(new StringObject(qaeDO.getTest()+", "+qaeDO.getMethod()));
           set.setKey(new StringObject(qaeDO.getTest()));
           model.add(set);
       }
       return model;
    }

}
