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
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.server.ScreenControllerServlet;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AppScreenServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.util.SessionManager;

public class OpenELISServlet extends ScreenControllerServlet implements OpenELISServiceInt<RPC,RPC>, AutoCompleteServiceInt {

    private static final long serialVersionUID = 1L; 

    public RPC abort(RPC rpc) throws Exception {
        return getService().abort(rpc);
    }

    public RPC  commitAdd(RPC rpc) throws Exception {
        return getService().commitAdd(rpc);
    }

    public RPC commitDelete(RPC rpc) throws Exception {
        return getService().commitDelete(rpc);
    }

    public RPC commitQuery(RPC query) throws Exception {
        return getService().commitQuery(query);
    }

    public RPC commitUpdate(RPC rpc) throws Exception {
        return getService().commitUpdate(rpc);
    }

    public RPC fetch(RPC rpc) throws Exception {
        return getService().fetch(rpc);
    }

    public RPC fetchForUpdate(RPC rpc) throws Exception {
        return getService().fetchForUpdate(rpc);
    }
    
    public RPC getScreen(RPC rpc) throws Exception {
        return ((AppScreenServiceInt<RPC>)getService()).getScreen(rpc);
    }
    
    public <T extends FieldType> T getObject(String method, FieldType[] args) throws Exception {
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
                    throw (Exception)er.getCause();
            }

            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
    
    private AppScreenFormServiceInt<RPC,RPC> getService() throws Exception {
        try {
            return (AppScreenFormServiceInt<RPC,RPC>)Class.forName(getThreadLocalRequest().getParameter("service")).newInstance();
        }catch(Exception e){
            if(e instanceof FormErrorException)
                throw new FormErrorException(e.getMessage());
            else{
                e.printStackTrace();
                throw new Exception(e.getMessage());
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

	public TableDataModel<TableDataRow<Object>> getMatches(String cat, TableDataModel model, String match, HashMap<String,FieldType> params) throws Exception{
		return ((AutoCompleteServiceInt)getService()).getMatches(cat,model,match,params);
	}

}
