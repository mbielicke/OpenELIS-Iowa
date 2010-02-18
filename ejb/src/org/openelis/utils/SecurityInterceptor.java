/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.utils;

import javax.naming.InitialContext;

import org.openelis.bean.SessionManagerInt;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.SecuritySection.SectionFlags;

public class SecurityInterceptor {
	
	private static SessionManagerInt session;
	
    public static void applySecurity(String module, ModuleFlags flag) throws Exception {
        if (! getSecurity().has(module, flag))
            throw new SecurityException("modulePermException", flag.name(), module);
    }

    public static void applySecurity(String section, SectionFlags flag) throws Exception {
        if (! getSecurity().has(section, flag))
            throw new SecurityException("sectionPermException", flag.name(), section);
    }

    private static SecurityUtil getSecurity() throws Exception {
    	if(session == null){
    		InitialContext ctx = new InitialContext();
    		session = (SessionManagerInt)ctx.lookup("openelis/SessionManager/local");
    	}
   		return (SecurityUtil)session.getAttribute("security");
    }
    
}
