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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.util.JRLoader;

import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.SystemVariableLocal;

public class ReportUtil {

    /**
     * Converts the arraylist of QueryData to a hashmap with querydata.key as
     * the hashmap key.
     */
    public static HashMap<String, QueryData> parameterMap(ArrayList<QueryData> list) {
        HashMap<String, QueryData> p;

        p = new HashMap<String, QueryData>();
        for (QueryData q : list)
            p.put(q.key, q);

        return p;
    }

    /**
     * Returns the query part of QueryData for the specified key. This method is
     * used to get the value for a given Prompt key when it has a single value.
     */
    public static String getSingleParameter(HashMap<String, QueryData> parameter, String key) {
        QueryData q;

        q = parameter.get(key);
        if (q != null)
            return q.query;

        return null;
    }

    /**
     * Return the query part of QueryData as a "in (1,2,3,...)" or "= 1"
     * depending on the contents of the query. This method is used to get the
     * value(s) for a given Prompt key when it can have multiple values, i.e.
     * multi select dropdowns.
     */
    public static String getListParameter(HashMap<String, QueryData> parameter, String key) {
        QueryData q;

        q = parameter.get(key);
        if (q != null) {
            if (q.query.contains(","))
                return "in (" + q.query + ")";
            else if ( !DataBaseUtil.isEmpty(q.query))
                return " = " + q.query;
        }
        return null;
    }

    /**
     * Returns the URL for the specified jasper report. Use this method to find
     * the jasper report within the J2EE ear structure.
     */
    public static URL getResourceURL(String reportName) {
        return JRLoader.getResource(reportName);
    }

    /**
     * Returns the path for given URL. Use this method to get the path to the
     * jasper report directory where other resources are located, i.e.
     * sub-reports.
     */
    public static String getResourcePath(URL url) {
        int i;
        String path;

        path = url.toExternalForm();
        i = path.lastIndexOf('/');
        if (i > 0)
            return path.substring(0, i + 1);

        return path;
    }

    /**
     * Convenience method to get a jdbc connection for jasper reports.
     */
    public static Connection getConnection(SessionContext ctx) throws Exception {
        return ((DataSource)ctx.lookup("jdbc/OpenELISDB")).getConnection();
    }

    /**
     * Determines if the specified destination is a printer.
     */
    public static boolean isPrinter(String destination) {
        return PrinterList.getInstance().getPrinterByName(destination) != null;
    }

    /**
     * Sends the file to specified printer, delete the file and returns the printer's status.
     * NOTE: This method is very "lpr" and "cups" dependent and will not work on non unix platforms.
     */
    public static String print(File file, String destination, int copies, String... options) throws Exception {
        String status;
        StringBuffer sb;

        sb = new StringBuffer();
        /*
         * UNIX style CUPS printing on LINUX.
         */
        try {
            sb.append("lpr")
              .append(" -U ").append(PermissionInterceptor.getSystemUserName());// username

            if ( !DataBaseUtil.isEmpty(destination))
                sb.append(" -P ").append(destination);  // printer
            if (copies > 1)
                sb.append(" -# ").append(copies);       // copies
            for (int i = 0; i < options.length; i++ )
                sb.append(" -o ").append(options[i]);   // -o option[=value]

            sb.append(' ').append(file); // file to print

            exec(sb.toString());
            status = "file " + file + " queued to " + destination;
        } catch (Exception e) {
            throw e;
        } finally {
            file.delete();
        }

        return status;
    }

    /**
     * Moves the specified file to upload save directory and changes its permission
     * to read/writable by all. Use this method to allow a JBOSS server and Tomcat server
     * to access a shared directory (and the file) even when they are on two different
     * physical servers. NOTE: This method uses "chmod" and will not work
     * on non unix systems.
     */
    public static File saveForUpload(File file) throws Exception {
        File movedFile;
        String saveDir;

        /*
         * directory where we save files for uploading
         */
        saveDir = ReportUtil.getSystemVariableValue("upload_save_directory");
        if (saveDir == null)
            throw new IOException("You need to define upload_save_directory in SystemVariable");

        movedFile = new File(saveDir, file.getName());
        copy(file, movedFile);
        exec("chmod 666 "+movedFile.getAbsolutePath());
        file.delete();
        
        return movedFile;
    }
    
    /**
     * Convenience method to get the value of a system variable
     */
    public static String getSystemVariableValue(String variableName) {
        String value;
        InitialContext ctx;
        SystemVariableLocal local;
        ArrayList<SystemVariableDO> sysVars;

        value = null;
        try {
            ctx = new InitialContext();
            local = (SystemVariableLocal)ctx.lookup("openelis/SystemVariableBean/local");

            sysVars = local.fetchByName(variableName, 1);
            if (sysVars.size() != 1)
                System.out.println("Could not find one System Variable with name="+variableName);
            value = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
        
    /**
     * Copies from the src file to dst
     */
    public static void copy(File src, File dst) throws IOException {
        copy(new FileInputStream(src), new FileOutputStream(dst));
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int l;
        byte b[];

        b = new byte[1024];
        while ((l = in.read(b)) > 0) 
            out.write(b, 0, l);

        in.close();
        out.close(); 
    }
    
    /**
     * Executes a system command and waits for its exit status. The method
     * throws the subprocess's error string as an exception if the exit code is not 0.
     */
    protected static void exec(String command) throws Exception {
        Process p;
        byte err[];

        p = Runtime.getRuntime().exec(command);
        p.waitFor();
        if (p.exitValue() != 0) {
            err = new byte[256];
            p.getErrorStream().read(err);
            throw new Exception(err.toString());
        }
    }
}