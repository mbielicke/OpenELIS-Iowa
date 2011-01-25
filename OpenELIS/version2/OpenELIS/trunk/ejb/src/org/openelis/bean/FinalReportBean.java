package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.FinalReportLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.finalreport.OrganizationInstance;
import org.openelis.report.finalreport.StatsDataSource;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;


@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class FinalReportBean implements FinalReportRemote, FinalReportLocal {

    @EJB
    private SessionCacheInt session;

    @EJB
    private SampleLocal     sampleBean;

    @EJB
    private LockLocal       lockBean;

    @EJB
    private AnalysisLocal   analysisBean;

    @Resource
    private SessionContext  ctx;

    private static int      UNFOLDABLE_PAGE_COUNT = 6;

    /**
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPromptsForSingle() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("ACCESSION_NUMBER", Prompt.Type.INTEGER).setPrompt("Accession Number:")
                                                                     .setWidth(150)
                                                                     .setRequired(true));

            p.add(new Prompt("ORGANIZATION_ID", Prompt.Type.INTEGER).setPrompt("Organization Id:")
                                                                    .setWidth(150));

            prn = PrinterList.getInstance().getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
        int i, numPages;
        boolean needBlank;
        URL url;
        File tempFile;
        HashMap<String, QueryData> param;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JRExporter jexport;
        String orgParam, accession, printer, printstat;
        SampleDO data;
        OrganizationInstance orgInstance;
        ArrayList<Object[]> results;
        ArrayList<OrganizationInstance> orgInstanceList;
        List tempPages;
        Integer orgId;
        JasperPrint masterJprint, blankJprint;        
        

        /*
         * push status into session so we can query it while the report is
         * running
         * 
         */
        status = new ReportStatus();
        session.setAttribute("FinalReport", status);

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.parameterMap(paramList);

        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");
        orgParam = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        /*
         * start the report
         */
        con = null;
        
        try {
            data = sampleBean.fetchByAccessionNumber(Integer.parseInt(accession));
            results = sampleBean.fetchSamplesForFinalReportSingle(data.getId());
            
            status.setMessage("Initializing report");
            orgInstanceList = new ArrayList<OrganizationInstance>();
            
            /*
             * if the user didn't specify an id for an organization then a report
             * is created for all the organizations associated with the sample,
             * otherwise a report is created for the organization, the id for
             * which is specified by the user if it can be found in the list of
             * organizations for that sample    
             */
            for (Object[] result : results) {
                if (orgParam != null) {
                    orgId = Integer.parseInt(orgParam);
                    if (DataBaseUtil.isSame(orgId, result[1]))
                        orgId = (Integer)result[1];
                    else   
                        continue;
                } else {
                    orgId = (Integer)result[1];
                }

                orgInstance = fillReport(" = " + data.getId(), orgId);
                orgInstanceList.add(orgInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
            
        if (orgInstanceList.size() == 0)
            throw new NotFoundException("noRecordsFound");
        
            needBlank = true;
            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/blank.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            blankJprint = JasperFillManager.fillReport(jreport, null);
            masterJprint = JasperFillManager.fillReport(jreport, null);
            
            for (OrganizationInstance org : orgInstanceList) {
                tempPages = org.getJprint().getPages();
                numPages = tempPages.size();
                if (numPages >= UNFOLDABLE_PAGE_COUNT && needBlank) {
                    masterJprint.addPage((JRPrintPage)blankJprint.getPages().get(0));
                    needBlank = false;
                }

                /*
                 * Add blank page to delimit end of autofolded section.
                 */
                for (i = 0; i < numPages; i++ )
                    masterJprint.addPage((JRPrintPage)tempPages.get(i));
            }
            
            masterJprint.removePage(0);                       
            
            try {

                jexport = new JRPdfExporter();

                tempFile = File.createTempFile("finalreportsingle", ".pdf", new File("/tmp"));
                jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
                jexport.setParameter(JRExporterParameter.JASPER_PRINT, masterJprint);

                status.setMessage("Outputing report").setPercentComplete(20);

                jexport.exportReport();

                status.setPercentComplete(100);

                if (ReportUtil.isPrinter(printer)) {
                    printstat = ReportUtil.print(tempFile, printer, 1);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                } else {
                    tempFile = ReportUtil.saveForUpload(tempFile);
                    status.setMessage(tempFile.getName())
                          .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                          .setStatus(ReportStatus.Status.SAVED);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (con != null)
                        con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
        return status;
    }
    
    @RolesAllowed("r_final-select")
    public ReportStatus runReportForBatch(String loginName, String printer) throws Exception {
        int i, numPages;
        boolean needBlank;
        URL url;
        Connection con;
        JasperReport jreport;
        JasperPrint masterJprint, blankJprint, statsJprint;
        JRExporter jexport;
        String printstat, sparam;
        File tempFile;
        List tempPages;
        ReportStatus status;
        ArrayList<Object[]> resultList;
        Integer sampleId, prevSampleId, orgId, anaId;
        Datetime timeStamp;
        ArrayList<Integer> lockedSampleList;
        HashMap<Integer, HashMap<Integer, Integer>> orgInstanceMap;
        HashMap<Integer, Integer> anaMap, sampleMap;
        HashMap<String, Object> jparam;
        Object[] result, list;
        Iterator<Integer> orgIter;
        OrganizationInstance orgInstance;
        ArrayList<OrganizationInstance> orgInstanceList;
        StatsDataSource sds;

        //
        // obtain the list of sample ids, organization ids and analysis ids
        //
        resultList = sampleBean.fetchSamplesForFinalReportBatch();
        sampleId = null;
        prevSampleId = null;
        orgInstanceMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        anaMap = new HashMap<Integer, Integer>();
        lockedSampleList = new ArrayList<Integer>();
        sampleMap = null;

        /*
         * loop through the list and lock all the samples obtained from
         * resultList that can be locked; the ones that can't be locked and the
         * organizations associated with them are excluded from the report being
         * generated
         */
        for (i = 0; i < resultList.size(); i++ ) {
            result = resultList.get(i);
            sampleId = (Integer)result[0];
            orgId = (Integer)result[1];
            anaId = (Integer)result[2];

            if ( !sampleId.equals(prevSampleId)) {
                try {
                    lockBean.lock(ReferenceTable.SAMPLE, sampleId);
                    System.out.println("locked sample id " + sampleId);
                    lockedSampleList.add(sampleId);
                } catch (Exception e) {
                    while (sampleId.equals(resultList.get(i)[0]))
                        i++ ;
                    prevSampleId = sampleId;
                    continue;
                }
            }

            sampleMap = orgInstanceMap.get(orgId);

            if (sampleMap == null)
                sampleMap = new HashMap<Integer, Integer>();

            sampleMap.put(sampleId, sampleId);
            orgInstanceMap.put(orgId, sampleMap);
            anaMap.put(anaId, sampleId);

            prevSampleId = sampleId;
        }

        timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        for (Integer key : anaMap.keySet()) {
            System.out.println("updating analysis id " + key + " with time " + timeStamp);
            analysisBean.updatePrintedDate(key, timeStamp);
        }

        System.out.println("printing report .....");

        status = new ReportStatus();
        session.setAttribute("FinalReport", status);

        status.setMessage("Initializing report");

        /*
         * start the report
         */
        con = null;

        orgInstanceList = new ArrayList<OrganizationInstance>();
        orgIter = orgInstanceMap.keySet().iterator();

        while (orgIter.hasNext()) {
            orgId = orgIter.next();
            sampleMap = orgInstanceMap.get(orgId);
            list = sampleMap.values().toArray();            
            
            if (orgId == null) {
                for (i = 0; i < list.length; i++ ) {
                    sparam = " = " + list[i];
                    orgInstance = fillReport(sparam, orgId);
                    orgInstanceList.add(orgInstance);
                }
            } else {  
                if (list.length == 1) {
                    sparam = " = " + list[0];
                } else {
                    sparam = " in (";
                    for (i = 0; i < list.length - 1; i++ )
                        sparam += list[i] + ", ";
                    sparam += list[i] + " )";
                }
                orgInstance = fillReport(sparam, orgId);
                orgInstanceList.add(orgInstance);
            }
        }

        Collections.sort(orgInstanceList, new MyComparator());

        needBlank = true;
        url = ReportUtil.getResourceURL("org/openelis/report/finalreport/blank.jasper");
        jreport = (JasperReport)JRLoader.loadObject(url);
        blankJprint = JasperFillManager.fillReport(jreport, null);
        masterJprint = JasperFillManager.fillReport(jreport, null);

        for (OrganizationInstance org : orgInstanceList) {
            tempPages = org.getJprint().getPages();
            numPages = tempPages.size();
            if (numPages >= UNFOLDABLE_PAGE_COUNT && needBlank) {
                masterJprint.addPage((JRPrintPage)blankJprint.getPages().get(0));
                needBlank = false;
            }

            /*
             * Add blank page to delimit end of autofolded section.
             */
            for (i = 0; i < numPages; i++ )
                masterJprint.addPage((JRPrintPage)tempPages.get(i));
        }

        sds = new StatsDataSource();
        sds.setStats(orgInstanceList);
        url = ReportUtil.getResourceURL("org/openelis/report/finalreport/stats.jasper");
        jreport = (JasperReport)JRLoader.loadObject(url);
        jparam = new HashMap<String, Object>();
        jparam.put("LOGNAME", loginName);
        statsJprint = JasperFillManager.fillReport(jreport, jparam, sds);

        masterJprint.removePage(0);
        tempPages = statsJprint.getPages();
        numPages = tempPages.size();
        for (i = 0; i < numPages; i++ )
            masterJprint.addPage((JRPrintPage)tempPages.get(i));

        try {

            jexport = new JRPdfExporter();

            tempFile = File.createTempFile("finalreportbatch", ".pdf", new File("/tmp"));
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, masterJprint);

            status.setMessage("Outputing report").setPercentComplete(20);

            jexport.exportReport();

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(tempFile, printer, 1);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Integer value : lockedSampleList) {
            lockBean.unlock(ReferenceTable.SAMPLE, value);
            System.out.println("unlocked sample id " + value);
        }

        return status;
    }    
    
    private OrganizationInstance fillReport(String sampleId, Integer orgId) throws Exception {
        OrganizationInstance orgInstance;
        URL url;        
        HashMap<String, Object> jparam;
        Connection con;
        JasperReport jreport;
        JasperPrint jprint;
        String dir;
        
        orgInstance = null;
        con = null;
        try {            
            
            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/main.jasper");
            dir = ReportUtil.getResourcePath(url);
            
            orgInstance = new OrganizationInstance();
            orgInstance.setOrganizationId(orgId);
            
            jparam = new HashMap<String, Object>();
            jparam.put("REPORT_TYPE", "S");
            jparam.put("SAMPLE_ID", sampleId);
            jparam.put("ORGANIZATION_ID", orgId);
            
            //
            // this parameter is used to show the names of the organizations/
            // report to(s) with the number of pages in the report showing statistics
            //            
            jparam.put("ORGANIZATION_INSTANCE", orgInstance);
            jparam.put("SUBREPORT_DIR", dir);          

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con); 
            
            orgInstance.setJprint(jprint);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return orgInstance;
    }
    

    class MyComparator implements Comparator<OrganizationInstance> {
        
        public int compare(OrganizationInstance o1, OrganizationInstance o2) {
            OrganizationInstance rp1, rp2;
            JasperPrint    jp1,  jp2;
            List<JasperPrint> jpl1, jpl2;
            
            rp1 = (OrganizationInstance) o1;
            rp2 = (OrganizationInstance) o2;
            jp1 = (JasperPrint) rp1.getJprint();
            jp2 = (JasperPrint) rp2.getJprint();           
            jpl1 = jp1.getPages();
            jpl2 = jp2.getPages();
            
            return jpl1.size() - jpl2.size();
        }
    }
        
}
