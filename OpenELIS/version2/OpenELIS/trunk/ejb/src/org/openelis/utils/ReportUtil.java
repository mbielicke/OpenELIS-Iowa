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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.ejb.SessionContext;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.util.JRLoader;

import org.openelis.constants.Messages;
import org.openelis.domain.SystemVariableDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.QueryData;

public class ReportUtil {

    /**
     * Converts the arraylist of QueryData to a hashmap with querydata.key as
     * the hashmap key.
     */
    public static HashMap<String, QueryData> getMapParameter(ArrayList<QueryData> list) {
        HashMap<String, QueryData> p;

        p = new HashMap<String, QueryData>();
        for (QueryData q : list)
            p.put(q.getKey(), q);

        return p;
    }

    /**
     * Returns the query part of QueryData for the specified key. This method is
     * used to get the value for a given Prompt key when it has a single value.
     */
    public static String getSingleParameter(HashMap<String, QueryData> parameter, String key) {
        QueryData q;

        q = parameter.get(key);
        if (q != null) {
            /*
             * single quotes are replaced with two single quotes to escape the
             * character in SQL
             */
            q.setQuery(q.getQuery().replaceAll("'", "''"));
            return q.getQuery();
        }

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
            /*
             * single quotes are replaced with two single quotes to escape the
             * character in SQL
             */
            q.setQuery(q.getQuery().replaceAll("'", "''"));
            if (q.getQuery().contains(","))
                return "in (" + q.getQuery() + ")";
            else if ( !DataBaseUtil.isEmpty(q.getQuery()))
                return " = " + q.getQuery();
        }
        return null;
    }

    /**
     * Return the query part of QueryData as an array of integers
     */
    public static int[] getArrayParameter(HashMap<String, QueryData> parameter, String key) {
        int list[];
        QueryData q;
        String str[];

        q = parameter.get(key);
        if (q == null || q.getQuery() == null)
            return null;
        str = q.getQuery().split(",");
        list = new int[str.length];
        for (int i = 0; i < str.length; i++ ) {
            try {
                list[i] = Integer.parseInt(str[i]);
            } catch (Exception ex) {
                return null;
            }
        }
        return list;
    }

    /**
     * Returns a Datetime object for given string. The pattern for the date,
     * date time, and time is specified in the messages.
     */
    public static Datetime getDate(String strDate) {
        return getDatetime(Datetime.YEAR, Datetime.DAY, strDate, Messages.get().datePattern());
    }

    public static Datetime getDatetime(String strDate) {
        return getDatetime(Datetime.YEAR, Datetime.MINUTE, strDate, Messages.get()
                                                                            .dateTimePattern());
    }

    public static Datetime getTime(String strDate) {
        return getDatetime(Datetime.HOUR, Datetime.MINUTE, strDate, Messages.get().timePattern());
    }

    public static Datetime getDatetime(byte startCode, byte endCode, String strDate, String pattern) {
        Date d;
        SimpleDateFormat format;

        if (DataBaseUtil.isEmpty(strDate))
            return null;

        format = new SimpleDateFormat(pattern);
        try {
            d = format.parse(strDate);
        } catch (ParseException e) {
            return null;
        }

        return Datetime.getInstance(startCode, endCode, d);
    }

    public static String toString(Datetime datetime, String pattern) {
        if (datetime == null)
            return "";

        return toString(datetime.getDate(), pattern);
    }

    public static String toString(Date date, String pattern) {
        SimpleDateFormat format;

        if (date == null)
            return "";

        format = new SimpleDateFormat(pattern);
        return format.format(date);
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
        return EJBFactory.getPrinterCache().getPrinterByName(destination) != null;
    }

    /**
     * Sends the file to specified printer and returns the printer's status.
     * NOTE: This method is very "lpr" and "cups" dependent and will not work on
     * non unix platforms.
     */
    public static String print(Path path, String userName, String destination,
                                            int copies, boolean delete, String... options) throws Exception {
        String status;
        ArrayList<String> args;

        /*
         * UNIX style CUPS printing on LINUX.
         */
        try {
            args = new ArrayList<String>();
            args.add("lpr");
            args.add("-U");
            args.add(userName);
            args.add("-P");
            args.add(destination);
            if (copies > 1) {
                args.add("-#");
                args.add(String.valueOf(copies));
            }

            for (int i = 0; i < options.length; i++ ) {
                args.add("-o");
                args.add(String.valueOf(options[i]));
            }
            args.add(path.toString());
            System.out.println("args: "+ args);
            exec(args);
            status = "print queued to " + destination;            
            if (delete)
                Files.delete(path);
        } catch (Exception e) {
            throw new Exception("Could not print to queue " + destination + "; " + e.getMessage());
        }

        return status;
    }

    /**
     * Sends the file through cups for faxing and returns the queued status.
     * NOTE: This method is very "sendfax" and "cups" dependent and will not
     * work on non unix platforms.
     */
    public static String fax(Path path, String faxNumber, String fromName, String toName,
                             String toCompany, String faxNote, String faxOwner, String faxEmail) throws Exception {
        String status;
        ArrayList<String> args;

        try {
            args = new ArrayList<String>();
            args.add("sendfax");
            args.add("-R");

            if ( !DataBaseUtil.isEmpty(faxOwner)) {
                args.add("-o");
                args.add(faxOwner);
            }

            if ( !DataBaseUtil.isEmpty(faxEmail)) {
                args.add("-f");
                args.add(faxEmail);
            }

            if ( !DataBaseUtil.isEmpty(fromName)) {
                args.add("-X");
                args.add(fromName);
            }
            if ( !DataBaseUtil.isEmpty(toCompany)) {
                args.add("-x");
                args.add(toCompany);
            }
            if ( !DataBaseUtil.isEmpty(faxNote)) {
                args.add("-c");
                args.add(faxNote);
            }
            faxNumber = faxNumber.replaceAll("[^0-9]", "");
            args.add("-d");
            args.add( !DataBaseUtil.isEmpty(toName) ? toName + "@" + faxNumber : faxNumber);

            args.add(path.toString());
            exec(args);
            status = "fax queued for " + faxNumber;
        } catch (Exception e) {
            throw new Exception("Could not fax; " + e.getMessage());
        }

        return status;
    }

    /**
     * Moves the specified file to upload save directory and changes its
     * permission to read/writable by all. Use this method to allow a JBOSS
     * server and Tomcat server to access a shared directory (and the file) even
     * when they are on two different physical servers.
     */
    public static Path createTempFile(String prefix, String suffix, String systemVariableDirectory) throws Exception {
        String directory;

        /*
         * directory where we create the file
         */
        directory = null;
        if (systemVariableDirectory != null)
            directory = ReportUtil.getSystemVariableValue(systemVariableDirectory);

        if (directory == null)
            directory = "/tmp";

        return Files.createTempFile(Paths.get(directory), prefix, suffix);
    }

    /**
     * Convenience method to get the value of a system variable
     */
    public static String getSystemVariableValue(String variableName) throws Exception {
        SystemVariableDO data;

        data = EJBFactory.getSystemVariable().fetchByName(variableName);
        return data.getValue();
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
        while ( (l = in.read(b)) > 0)
            out.write(b, 0, l);

        in.close();
        out.close();
    }

    /**
     * Returns the sub-directory path of an attachment given the attachment id
     */
    public static String getAttachmentSubdirectory(Integer id) {
        char path[];

        path = String.format("%010d", id).toCharArray();
        if (path != null && path.length == 10) {
            return path[6] + File.separator + path[7] + File.separator + path[8] + File.separator +
                   path[9];
        }
        return ".";
    }

    /**
     * Returns the initials for the specified system user id
     */
    public static String getInitialsForUserId(Integer userId) {
        SystemUserVO userVO;

        try {
            userVO = EJBFactory.getUserCache().getSystemUser(userId);
            return userVO.getInitials();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes a system command and waits for its exit status. The method
     * throws the subprocess's error string as an exception if the exit code is
     * not 0.
     */
    protected static void exec(ArrayList<String> args) throws Exception {
        String cmd[];

        cmd = new String[args.size()];
        for (int i = 0; i < args.size(); i++ )
            cmd[i] = args.get(i);

        exec(cmd);
    }

    /**
     * Executes a system command and waits for its exit status. The method
     * throws the subprocess's error string as an exception if the exit code is
     * not 0.
     */
    protected static void exec(String... command) throws Exception {
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

    /**
     * Parses the clause in userPermission and returns the values in a format
     * which can be understood by the Query Builder.
     */
    public static HashMap<String, String> parseClauseAsString(String clause) {
        HashMap<String, String> map;
        String[] str, str1;
        String key, value;

        map = new HashMap<String, String>();
        if (DataBaseUtil.isEmpty(clause))
            return map;
        str = clause.split(";");
        for (int i = 0; i < str.length; i++ ) {
            str1 = str[i].split(":");
            if (str1.length != 2)
                continue;
            key = str1[0];
            value = str1[1];
            if (DataBaseUtil.isEmpty(key) || DataBaseUtil.isEmpty(value))
                continue;
            if (value.contains(","))
                value = " in (" + value + ")";
            else
                value = " = " + value;
            map.put(key, value);
        }
        return map;
    }

    /**
     * Parses the clause in userPermission and returns the values as a HashMap
     * where the key is the name of a field from the clause and the value is an
     * ArrayList of Integers
     */
    public static HashMap<String, ArrayList<Integer>> parseClauseAsArrayList(String clause) {
        HashMap<String, ArrayList<Integer>> map;
        ArrayList<Integer> list;
        String[] str, str1, str2;
        String key, value;

        map = new HashMap<String, ArrayList<Integer>>();
        if (DataBaseUtil.isEmpty(clause))
            return map;
        str = clause.split(";");
        for (int i = 0; i < str.length; i++ ) {
            list = new ArrayList<Integer>();
            str1 = str[i].split(":");
            if (str1.length != 2)
                continue;
            key = str1[0];
            value = str1[1];
            if (DataBaseUtil.isEmpty(key) || DataBaseUtil.isEmpty(value))
                continue;
            str2 = value.split(",");
            for (int j = 0; j < str2.length; j++ ) {
                try {
                    list.add(Integer.parseInt(str2[j]));
                } catch (Exception ex) {
                    continue;
                }
            }
            map.put(key, list);
        }
        return map;
    }

    public static void sendEmail(String from, String to, String subject, String body) throws Exception {
        Properties props;
        Session session;
        Message msg;

        props = new Properties();

        props.put("mail.smtp.host", getSystemVariableValue("mail_smtp_host"));
        props.put("mail.smtp.port", getSystemVariableValue("mail_smtp_port"));

        session = Session.getDefaultInstance(props, null);
        msg = new MimeMessage(session);
        msg.setContent(body, "text/html; charset=ISO-8859-1");
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.saveChanges();

        Transport.send(msg);
    }
}