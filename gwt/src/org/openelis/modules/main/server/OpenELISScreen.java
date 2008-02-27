package org.openelis.modules.main.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenELISScreen implements OpenELISServiceInt {
   
    private static final long serialVersionUID = 1L;

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
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
    
    public String getMenuList() {
        try {
        //  System.out.println((SessionManager.getSession().getAttribute("locale") == null ? "en" : (String)SessionManager.getSession().getAttribute("locale")));
            Document doc = XMLUtil.createNew("list");
            Element root = doc.getDocumentElement();
            root.setAttribute("key", "menuList");
            root.setAttribute("height", "100%");
            root.setAttribute("vertical","true");
            
            //sample management section
            Element elem = doc.createElement("label");
            elem.setAttribute("text", "Favorites");
            elem.setAttribute("constant", "true");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Dictionary");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftDictionary");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Organization");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftOrganization");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Organize Favorites...");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "organizeFavoritesLeft");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Provider");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftProvider");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "QA Events");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftqaEvents");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Standard Note");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStandardNote");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Storage");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStorage");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Storage Unit");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStorageUnit");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
                    
            return XMLUtil.toString(doc);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public DataObject getObject(String method, DataObject[] args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public void logout() {
        // TODO Auto-generated method stub
        
    }

}
