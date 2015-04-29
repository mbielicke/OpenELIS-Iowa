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

import javax.ejb.SessionContext;

/**
 * The name in caller principal is overloaded with EJB session id and local.
 * This class provides convenient methods to unwrap user information from
 * context.
 */
public class User {
	
    /**
     * Returns the system user's login name associated with this context. Please
     * note that we concat username, sessionId, and locale on initial login and
     * you will need a special login class for JBOSS to parse the username.
     */
    /*
     * This method has been altered to work for jboss 7 bug where the
     * unauthenticatedIdentity is always being set to anonymous instead of the
     * configured value of 'system'. We change the user name to 'system' so that
     * cron jobs that call managers or access beens that use OpenELIS security
     * checks will work properly. More info can found at these two links
     * 
     * https://community.jboss.org/thread/175405
     * 
     * https://issues.jboss.org/browse/AS7-3154
     */
    public static String getName(SessionContext ctx) {
        String parts[];

        parts = ctx.getCallerPrincipal().getName().split(";", 3);
        if (parts.length > 0) {
            if (parts[0].equals("anonymous"))
                return "system";
            else
                return parts[0];
        }

        return null;
    }

    /**
     * Returns the system user's session id associated with this context. Please
     * note that we concat username, sessionId, and locale on initial login and
     * you will need a special login class for JBOSS to parse the username.
     */
    public static String getSessionId(SessionContext ctx) {
        String parts[];

        parts = ctx.getCallerPrincipal().getName().split(";", 3);
        // the user 'system' will not have session id
        if (parts.length > 1)
            return parts[1];

        return "";
    }

    /**
     * Returns the system user's locale associated with this context. Please
     * note that we concat username, sessionId, and locale on initial login and
     * you will need a special login class for JBOSS to parse the username.
     */
    public static String getLocale(SessionContext ctx) {
        String parts[];

        parts = ctx.getCallerPrincipal().getName().split(";", 3);
        // the user 'system' will not have locale
        if (parts.length > 2)
            return parts[2];

        return "";
    }
}