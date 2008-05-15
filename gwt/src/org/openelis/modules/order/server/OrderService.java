package org.openelis.modules.order.server;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.server.constants.Constants;

public class OrderService implements AppScreenFormServiceInt {

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
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
        return null;
    }

    public HashMap getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        String action = (String)((StringObject)args.get("xml")).getValue();
        HashMap returnMap = new HashMap();
        StringObject xmlString = new StringObject();
        
        if("internal".equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/internalOrder.xsl"));
        else if("external".equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/externalOrder.xsl"));
        else if("kits".equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/kitOrder.xsl"));
        
        returnMap.put("xml", xmlString);
        
        return returnMap;
    }

}
