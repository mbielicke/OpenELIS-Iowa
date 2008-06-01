package org.openelis.modules.main.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.CollectionField;
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
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Locale;

public class OpenELISService implements OpenELISServiceInt {
   
    private static final long serialVersionUID = 1L;
    
    private static String[] CONSTS = new String[] {"active",
                                                   "description",
                                                   "greeting",
                                                   "favorites",
                                                   "firstName",
                                                   "lastName",
                                                   "lastPageException",
                                                   "middleName",
                                                   "name",
                                                   "id",
                                                   "queryExpiredException",
                                                   "removeRow",
                                                   "section",
                                                   "sequence",
                                                   "test",
                                                   "find",
                                                   "lastPageException",
                                                   "firstPageException",
                                                   "beginningQueryException",
                                                   "endingQueryException",
                                                   "storageUnitDeleteException",
                                                   "storageLocDeleteException",
                                                   "analyteDeleteException",
                                                   "abort",
                                                   "add",
                                                   "cancel",
                                                   "commit",
                                                   "delete",
                                                   "next",
                                                   "previous",
                                                   "query",
                                                   "reload",
                                                   "select",
                                                   "update",
                                                   "value",
                                                   "addAborted",
                                                   "adding",
                                                   "addingFailed",
                                                   "addingComplete",
                                                   "deleteComplete",
                                                   "deleteMessage",
                                                   "deleteAborted",
                                                   "deleting",
                                                   "loadCompleteMessage",
                                                   "enterFieldsToQuery",
                                                   "enterInformationPressCommit",
                                                   "loadingMessage",
                                                   "correctErrors",
                                                   "queryAborted",
                                                   "querying",
                                                   "queryingComplete",
                                                   "updateFailed",
                                                   "updateFieldsPressCommit",
                                                   "updating",
                                                   "updateAborted",
                                                   "updatingComplete",
                                                   "mustCommitOrAbort",
                                                   "lockForUpdate",
                                                   "updateFields"};

    public String getXML() throws RPCException {
        try {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
	}
    
    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
        HashMap map = new HashMap();
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
        HashMap map = new HashMap();
        for(int i = 0; i < CONSTS.length; i++){
            map.put(CONSTS[i], resource.getString(CONSTS[i]));
        }
        ConstantMap cmap = new ConstantMap();
        cmap.setValue(map);
        return cmap;
    }

	public HashMap<String,DataObject> getXMLData(HashMap<String,DataObject> args) throws RPCException {
		try {
			Document doc = XMLUtil.createNew("doc");
			Element root = doc.getDocumentElement();
			Element modules = doc.createElement("modules");
			String modu = ((CollectionField)args.get("modules")).toString();
			System.out.println("modules : "+modu);
			modules.appendChild(doc.createTextNode(modu));
			root.appendChild(modules);
			StringObject xml = new StringObject();
			xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl",doc));
            HashMap map = new HashMap();
            map.put("xml", xml);
            map.put("AppConstants",getConstants());
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
