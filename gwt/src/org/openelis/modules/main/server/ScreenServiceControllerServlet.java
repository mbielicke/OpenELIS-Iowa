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

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.services.ScreenServiceInt;

public class ScreenServiceControllerServlet extends AppServlet implements ScreenServiceInt {

    private static final long serialVersionUID = 1L; 


    
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
