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
package org.openelis.web.server.constants;

import java.util.Hashtable;

/**
 * @author fyu
 */
public class Constants {

    public static String CAS_FILTER_USER = "edu.yale.its.tp.cas.client.filter.user";

    public static String CAS_FILTER_PASS = "edu.yale.its.tp.cas.client.filter.pass";

    public static String CAS_LOGOUT_URL = "https://www.uhl.uiowa.edu/cas/logout?destination=http://erudite.uhl.uiowa.edu/nmsp/exit.html";

    public static String CHANGE_PASSWORD_URL = "https://www.uhl.uiowa.edu/ldap/loginView.jsp";

    public static String TARGET_CLASS_ROOT = "org.openelis.server.action";

    public static String FILE_SEPARATOR = System.getProperty("file.separator");

    /*
     * Initialized in the Controller initialization routine with values from the
     * WEB.XML file
     */
    public static String APP_ROOT = null;

    public static String APPLICATION_PROPERTIES_PATH = null;

    public static String TEST_REPORT_FORMAT = null;

    public static String SERVICE_NOT_AVALIABLE = "Service Unavailable, Please Try Again Later.";

    public static String PERMISSION_DENIED = "Permission Denied. Please Contact System Administrator";

    public static String REPORT_THIS_ERROR = "An Error Has Occured. Please Report This Error";

    public static final int SPREADSHEET = 1;

    public static final int XML = 2;

    public static final int PIPE_DELIMITED = 3;

    public static final int HTML = 4;

    public static final short SELECT = 1, ADD = 2, UPDATE = 3, DELETE = 4,
                    CANCEL = 5, RELEASE = 6, TRANSFER = 7, REJECT = 8,
                    UNRELEASE = 9, EMAILED = 10, PRINTED = 11, ESAVE = 12;

    public static Hashtable HEADER_FORMATS = new Hashtable();
}
