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
package org.openelis.modules.main.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.ConstantMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

public class OpenELISService implements OpenELISServiceInt {
   
    private static final long serialVersionUID = 1L;
    

    public String getXML() throws RPCException {
        try {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
	}
    
    public HashMap<String,DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
        HashMap<String,DataObject> map = new HashMap<String,DataObject>();
        map.put("xml",xml);
        map.put("AppConstants", getConstants());
        return map;
    }

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

    public DataModel getInitialModel(String cat) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataObject getObject(String method, DataObject[] args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public void logout() {
        // TODO Auto-generated method stub
        
    }
    
    public ConstantMap getConstants() {
        UTFResource resource = UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
        HashMap<String,String> map = new HashMap<String,String>();
        Enumeration<String> bundleKeys = resource.getKeys();
        while(bundleKeys.hasMoreElements()){
            String key = bundleKeys.nextElement();
            map.put(key, resource.getString(key));
        }
        ConstantMap cmap = new ConstantMap();
        cmap.setValue(map);
        return cmap;
    }

	public HashMap<String,DataObject> getXMLData(HashMap<String,DataObject> args) throws RPCException {
		try {
			StringObject xml = new StringObject();
			xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
            HashMap<String,DataObject> map = new HashMap<String,DataObject>();
            map.put("xml", xml);
            map.put("AppConstants",getConstants());
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
