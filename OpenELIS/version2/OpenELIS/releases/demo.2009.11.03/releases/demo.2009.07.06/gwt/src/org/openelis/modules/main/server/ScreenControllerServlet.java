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
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.Preferences;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AppScreenServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.services.ScreenServiceInt;
import org.openelis.modules.favorites.client.FavoritesServiceInt;
import org.openelis.modules.favorites.server.FavoritesService;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.util.SessionManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class ScreenControllerServlet extends AppServlet implements OpenELISServiceInt<RPC,RPC>, AutoCompleteServiceInt, FavoritesServiceInt, ScreenServiceInt {

    private static final long serialVersionUID = 1L; 

    public RPC abort(RPC rpc) throws RPCException {
        return getService().abort(rpc);
    }

    public RPC  commitAdd(RPC rpc) throws RPCException {
        return getService().commitAdd(rpc);
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
        return getService().commitDelete(rpc);
    }

    public RPC commitQuery(RPC query) throws RPCException {
        return getService().commitQuery(query);
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        return getService().commitUpdate(rpc);
    }

    public RPC fetch(RPC rpc) throws RPCException {
        return getService().fetch(rpc);
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        return getService().fetchForUpdate(rpc);
    }
    
    public RPC getScreen(RPC rpc) throws RPCException {
        return ((AppScreenServiceInt<RPC>)getService()).getScreen(rpc);
    }
    
    public <T extends FieldType> T getObject(String method, FieldType[] args) throws RPCException {
        AppScreenFormServiceInt service = (AppScreenFormServiceInt) getService();
        Class[] params = null;
        if(args != null){
            params = new Class[args.length];
            for(int i = 0; i < args.length; i++){
                params[i] = args[i].getClass();
            }
        }
        try {
            return (T)service.getClass().getMethod(method,params).invoke(service, (Object[])args);
        }catch(Exception e){
            if(e instanceof InvocationTargetException){
                InvocationTargetException er = (InvocationTargetException)e;
                if(er.getCause() != null)
                    throw (RPCException)er.getCause();
            }

            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
    }
    
    private AppScreenFormServiceInt<RPC,RPC> getService() throws RPCException {
        try {
            return (AppScreenFormServiceInt<RPC,RPC>)Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
        }catch(Exception e){
            if(e instanceof FormErrorException)
                throw new FormErrorException(e.getMessage());
            else{
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }
        }
    }
    
    private ScreenServiceInt getScreenService() throws RPCException {
        try {
            return (ScreenServiceInt)Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
        }catch(Exception e){
            if(e instanceof FormErrorException)
                throw new FormErrorException(e.getMessage());
            else{
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }
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
        } catch (Exception e) {
            e.getMessage();
        }
        
    }
    
    public String getLanguage(){
        return getThreadLocalRequest().getLocale().getLanguage();
    }

	public TableDataModel<TableDataRow<Object>> getMatches(String cat, TableDataModel model, String match, HashMap<String,FieldType> params) throws RPCException{
		return ((AutoCompleteServiceInt)getService()).getMatches(cat,model,match,params);
	}

    public String getEditFavorites() {
        return new FavoritesService().getEditFavorites();
    }

    public String getFavorites(Preferences prefs) {
        // TODO Auto-generated method stub
        return new FavoritesService().getFavorites(prefs);
    }

    public String saveFavorites(Form form) {
        // TODO Auto-generated method stub
        return new FavoritesService().saveFavorites(form);
    }

    public <T extends RPC> T call(String method, T rpc) throws Exception {
        AppScreenFormServiceInt service = (AppScreenFormServiceInt) getService();
        try {
            return (T)service.getClass().getMethod(method,new Class[] {rpc.getClass()}).invoke(service, new Object[]{rpc});
            //return (T)Class.forName(getThreadLocalRequest().getParameter("service")).newInstance().getClass().
            //getMethod(method,new Class[] {rpc.getClass()}).invoke(Class.forName(getThreadLocalRequest().getParameter("service")).newInstance(), new Object[]{rpc});
        }catch(Exception e){
            if(e instanceof InvocationTargetException){
                InvocationTargetException er = (InvocationTargetException)e;
                if(er.getCause() != null)
                    throw (RPCException)er.getCause();
            }

            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
    }

	public String getScreen() throws RPCException {
		 return ((ScreenServiceInt)getScreenService()).getScreen();
	}

	public <T extends RPC> T callScreen(String method, T rpc) throws Exception {
		ScreenServiceInt service = getScreenService();
		try{
			return (T)service.getClass().getMethod(method,new Class[] {rpc.getClass()}).invoke(service, new Object[]{rpc});
		}catch(Exception e){
			if(e instanceof InvocationTargetException){
				InvocationTargetException er = (InvocationTargetException)e;
				if(er.getCause() != null)
					throw (RPCException)er.getCause();
			}

			e.printStackTrace();
			throw new RPCException(e.getMessage());
		}
	}

}
