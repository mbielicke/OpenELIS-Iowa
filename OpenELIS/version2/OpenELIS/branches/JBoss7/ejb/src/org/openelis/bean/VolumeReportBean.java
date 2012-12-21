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
package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.OptionListItem;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class VolumeReportBean {

    @Resource
    private SessionContext  ctx;

    @EJB
    private SessionCacheBean session;

    @EJB
    private SectionBean     section;
    
    @EJB
    private UserCacheBean   userCache;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {       
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM", Prompt.Type.DATETIME).setPrompt("Starting Entered Date:")
                                                          .setWidth(150)
                                                          .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                          .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                          .setDefaultValue(Datetime.getInstance(Datetime.YEAR,
                                                                                                Datetime.MINUTE)
                                                                                   .toString())
                                                          .setRequired(true));        

            p.add(new Prompt("TO", Prompt.Type.DATETIME).setPrompt("Ending Entered Date:")
                                                        .setWidth(150)
                                                        .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                        .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                        .setDefaultValue(Datetime.getInstance(Datetime.YEAR,
                                                                                              Datetime.MINUTE)
                                                                                 .toString())
                                                        .setRequired(true));

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(150)
                                                          .setOptionList(getSections())
                                                          .setMutiSelect(true));

            
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        URL url;
        File tempFile;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        String frDate, tDate, fromDate, toDate, section, loginName, dir, printstat;
        fromDate = toDate = null;
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("VolumeReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        loginName = userCache.getName();

        frDate = ReportUtil.getSingleParameter(param, "FROM");
        tDate = ReportUtil.getSingleParameter(param, "TO");
        section = ReportUtil.getListParameter(param, "SECTION");        

        if (DataBaseUtil.isEmpty(frDate) || DataBaseUtil.isEmpty(tDate))
            throw new InconsistencyException("You must specify From Date and To Date for this report");

        if (frDate != null && frDate.length() > 0) {
            fromDate = frDate + ":00";
        }
        if (tDate != null && tDate.length() > 0) {
            toDate = tDate + ":59";

        }

        if ( !DataBaseUtil.isEmpty(section))
            section = " and se.id " + section;
        else
            section = "";
        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/volume/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            tempFile = File.createTempFile("volume", ".xls", new File("/tmp"));

            jparam = new HashMap<String, Object>();
            jparam.put("FROM", fromDate);
            jparam.put("TO", toDate);
            jparam.put("SECTION", section);
            jparam.put("LOGIN_NAME", loginName);

            status.setMessage("Loading report");

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
           
            jexport = new JRXlsExporter();
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setMessage("Outputing report").setPercentComplete(20);

            jexport.exportReport();

            status.setPercentComplete(100);
            
            tempFile = ReportUtil.saveForUpload(tempFile);
            status.setMessage(tempFile.getName())
                  .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                  .setStatus(ReportStatus.Status.SAVED);            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                // ignore
            }
        }

        return status;
    }

    private ArrayList<OptionListItem> getSections() {
        ArrayList<SectionViewDO> s;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            s = section.fetchList();
            for (SectionViewDO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }

}