package org.openelis.server.constants;

import java.util.*;

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
