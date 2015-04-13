package org.openelis.stfu.bean;

import java.io.File;
import java.util.Scanner;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.SystemVariableBean;

@Stateless
@SecurityDomain("openelis")
public class MessageBean {
    @EJB
    private SystemVariableBean systemVariable;

    /**
     * Attempts to find and read all contents of a text file at the path
     * specified by the system variable, then returns the file as a string
     */
    public String getMessage() throws Exception {
        String fileName;
        Scanner fileScanner;
        StringBuffer sb;

        fileName = systemVariable.fetchByName("message_location").getValue();
        sb = new StringBuffer();
        fileScanner = new Scanner(new File(fileName));
        while (fileScanner.hasNextLine()) {
            sb.append(fileScanner.nextLine());
        }
        return sb.toString();
    }
}