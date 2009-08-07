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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.main.client.openelis.OpenELISRPC;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class OpenELISScreenService {
   
    public void logout() {
        // TODO Auto-generated method stub
        
    }
    
    public String getScreen() {
        try {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/main.xsl");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public OpenELISRPC initialData() {
    	 OpenELISRPC rpc = new OpenELISRPC();
    	 rpc.appConstants = getConstants();
         rpc.security = (SecurityUtil)SessionManager.getSession().getAttribute("security");
         return rpc;
    }
    
    private HashMap<String,String> getConstants() {
        UTFResource resource = UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
        HashMap<String,String> cmap = new HashMap<String,String>();
        Enumeration<String> bundleKeys = resource.getKeys();
        while(bundleKeys.hasMoreElements()){
            String key = bundleKeys.nextElement();
            cmap.put(key, resource.getString(key));
        }
        return cmap;
    }

}
