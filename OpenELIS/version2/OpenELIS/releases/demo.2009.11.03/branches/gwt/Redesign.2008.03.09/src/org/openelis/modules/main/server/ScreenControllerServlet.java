package org.openelis.modules.main.server;

import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.services.TableServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;

import com.google.gwt.user.client.rpc.RemoteService;

import javax.servlet.http.HttpSession;

public class ScreenControllerServlet extends AppServlet implements OpenELISServiceInt, AutoCompleteServiceInt, TableServiceInt {

    private static final long serialVersionUID = 1L;

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).abort(key, rpcReturn);
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).commitAdd(rpcSend, rpcReturn);
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).commitDelete(key, rpcReturn);
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).commitQuery(rpcSend, model);
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).commitUpdate(rpcSend, rpcReturn);
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).fetch(key, rpcReturn);
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        return ((AppScreenFormServiceInt)getService()).fetchForUpdate(key, rpcReturn);
    }

    public String getXML() throws RPCException {
        return ((AppScreenFormServiceInt)getService()).getXML();
    }

    public DataObject[] getXMLData() throws RPCException {
        return ((AppScreenFormServiceInt)getService()).getXMLData();
    }
    
    public DataObject getObject(String method, DataObject[] args) throws RPCException {
        AppScreenFormServiceInt service = (AppScreenFormServiceInt) getService();
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
    
    private RemoteService getService() throws RPCException {
        try {
            return (RemoteService) Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
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
    

    //auto complete service methods
	public DataModel getDisplay(String cat, DataModel model, AbstractField value) throws RPCException{
		return ((AutoCompleteServiceInt)getService()).getDisplay(cat,model,value);
	}

	public DataModel getMatches(String cat, DataModel model, String match) throws RPCException{
		return ((AutoCompleteServiceInt)getService()).getMatches(cat,model,match);
	}

	//table service methods
	public TableModel filter(int col, Filter[] filters, int index, int selected) throws RPCException {
		return ((TableServiceInt)getService()).filter(col, filters, index, selected);
	}

	public Filter[] getFilter(int col) {
		try{
			return ((TableServiceInt)getService()).getFilter(col);
		}catch(Exception e){
            e.printStackTrace();
            return null;
        }
	}

	public TableModel getModel(TableModel model) throws RPCException {
		return ((TableServiceInt)getService()).getModel(model);
	}

	public TableModel getPage(int page, int selected) throws RPCException {
		return ((TableServiceInt)getService()).getPage(page, selected);
	}

	public String getTip(AbstractField key) throws RPCException {
		return ((TableServiceInt)getService()).getTip(key);
	}

	public TableModel saveModel(TableModel model) throws RPCException {
		return ((TableServiceInt)getService()).saveModel(model);
	}

	public TableModel sort(int col, boolean down, int index, int selected) throws RPCException {
		return ((TableServiceInt)getService()).sort(col, down, index, selected);
	}    
}
