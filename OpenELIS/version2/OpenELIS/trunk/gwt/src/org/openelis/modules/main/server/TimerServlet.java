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
package org.openelis.modules.main.server;

import javax.servlet.annotation.WebServlet;

import org.openelis.modules.main.client.TimerServiceInt;
import org.openelis.ui.common.Datetime;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/*
 * This servlet needs to extend RemoteServiceServlet instead of RemoteServlet so that it does not update
 *last_access when checking for last_access;
 */
@WebServlet("/openelis/timer")
public class TimerServlet extends RemoteServiceServlet implements TimerServiceInt {
    
    private static final long serialVersionUID = 1L;

    public void keepAlive() {
        getThreadLocalRequest().getSession().setAttribute("last_access",
                                                          Datetime.getInstance(Datetime.YEAR,
                                                                               Datetime.MINUTE));
    }

    public Datetime getLastAccess() {
        return (Datetime)getThreadLocalRequest().getSession().getAttribute("last_access");
    }

}