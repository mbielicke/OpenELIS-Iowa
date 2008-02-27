package org.openelis.modules.main.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.util.SessionManager;

import javax.servlet.http.HttpSession;

public class ScreenControllerServlet extends AppServlet implements OpenELISServiceInt {

    private static final long serialVersionUID = 1L;

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        return getService().abort(key, rpcReturn);
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        return getService().commitAdd(rpcSend, rpcReturn);
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return getService().commitDelete(key, rpcReturn);
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        return getService().commitQuery(rpcSend, model);
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        return getService().commitUpdate(rpcSend, rpcReturn);
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        return getService().fetch(key, rpcReturn);
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        return getService().fetchForUpdate(key, rpcReturn);
    }

    public String getXML() throws RPCException {
        return getService().getXML();
    }
    
    public DataObject getObject(String method, DataObject[] args) throws RPCException {
        AppScreenFormServiceInt service = getService();
        Class[] params = null;
        if(args != null){
            params = new Class[args.length];
            for(int i = 0; i < args.length; i++){
                params[i] = args[i].getClass();
            }
        }
        try {
            return (DataObject)service.getClass().getMethod(method,params).invoke(service, (Object[])args);
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        
    }
    
    private AppScreenFormServiceInt getService() throws RPCException {
        try {
            return (AppScreenFormServiceInt)Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
    }
    
    public void logout() {
        HttpSession session;
        try {
            session = getThreadLocalRequest().getSession();
            SessionManager.removeSession(session.getId());
            // Clear out the existing session for the user
            if (session != null) {
                session.invalidate();
            }
            // redirect to CAS logout servlet for deletion of CAS cookies
            getThreadLocalResponse().sendRedirect("https://www.uhl.uiowa.edu/cas/logout");
        } catch (Exception e) {
            e.getMessage();
        }
        
    }
    
    public String getLanguage(){
        return getThreadLocalRequest().getLocale().getLanguage();
    }

    public String getMenuList() throws RPCException {
        return ((OpenELISServiceInt)getService()).getMenuList();
    }

    public DataModel getInitialModel(String cat) throws RPCException {
        return getService().getInitialModel(cat);
    }
    
}
