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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.Preferences;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.favorites.client.FavoritesServiceInt;
import org.openelis.modules.favorites.server.FavoritesService;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.util.SessionManager;

import com.google.gwt.user.client.rpc.RemoteService;

public class ScreenControllerServlet extends AppServlet implements OpenELISServiceInt, AutoCompleteServiceInt, FavoritesServiceInt {

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

    public HashMap getXMLData() throws RPCException {
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
            if(e instanceof InvocationTargetException){
                InvocationTargetException er = (InvocationTargetException)e;
                if(er.getCause() != null)
                    throw (RPCException)er.getCause();
            }

            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
    }
    
    private RemoteService getService() throws RPCException {
        try {
            return (RemoteService) Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
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

	public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException{
		return ((AutoCompleteServiceInt)getService()).getMatches(cat,model,match,params);
	}

    public String getEditFavorites() {
        return new FavoritesService().getEditFavorites();
    }

    public String getFavorites(Preferences prefs) {
        // TODO Auto-generated method stub
        return new FavoritesService().getFavorites(prefs);
    }

    public String saveFavorites(FormRPC rpc) {
        // TODO Auto-generated method stub
        return new FavoritesService().saveFavorites(rpc);
    }

	public HashMap getXMLData(HashMap args) throws RPCException {
		return ((AppScreenFormServiceInt)getService()).getXMLData(args);
	}    
}
