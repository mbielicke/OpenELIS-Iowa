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
package org.openelis.modules.main.server;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.ConstantMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

public class OpenELISService implements OpenELISServiceInt<RPC,Integer> {
   
    private static final long serialVersionUID = 1L;
    

    public String getXML() throws RPCException {
        try {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
	}
    
    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
        HashMap<String,FieldType> map = new HashMap<String,FieldType>();
        map.put("xml",xml);
        map.put("AppConstants", getConstants());
        return map;
    }

    public RPC abort(RPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitDelete(RPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetch(RPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getInitialModel(String cat) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FieldType getObject(String method, FieldType[] args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public void logout() {
        // TODO Auto-generated method stub
        
    }
    
    public ConstantMap getConstants() {
        UTFResource resource = UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
        ConstantMap cmap = new ConstantMap();
        Enumeration<String> bundleKeys = resource.getKeys();
        while(bundleKeys.hasMoreElements()){
            String key = bundleKeys.nextElement();
            cmap.put(key, resource.getString(key));
        }
        return cmap;
    }

	public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
		try {
			StringObject xml = new StringObject();
			xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
            HashMap<String,FieldType> map = new HashMap<String,FieldType>();
            map.put("xml", xml);
            map.put("AppConstants",getConstants());
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
    
    public RPC getScreen(RPC rpc) {
        return null;
    }

    public <T extends RPC> T call(String method, T rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
