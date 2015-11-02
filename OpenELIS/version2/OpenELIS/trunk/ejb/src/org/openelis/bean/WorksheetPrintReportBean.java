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
package org.openelis.bean;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1Accessor;
import org.openelis.report.worksheetPrint.DiagramDataSource;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class WorksheetPrintReportBean {

    @Resource
    private SessionContext        ctx;

    @EJB
    private SessionCacheBean      session;

    @EJB
    private PrinterCacheBean      printers;

    @EJB
    private SampleManager1Bean    sampleManager;

    @EJB
    private SystemVariableBean    systemVariable;

    @EJB
    private WorksheetManager1Bean worksheetManager;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, format;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("WORKSHEET_ID", Prompt.Type.INTEGER).setPrompt("Worksheet #:")
                                                                 .setWidth(130)
                                                                 .setHidden(true)
                                                                 .setRequired(true));

            format = new ArrayList<OptionListItem>();
            format.add(new OptionListItem("4x12B", "4 Slides 12 Blank Wells"));
            format.add(new OptionListItem("6x10", "6 Slides 10 Wells"));
            format.add(new OptionListItem("6x12", "6 Slides 12 Wells"));
            format.add(new OptionListItem("8x4Arbo", "8 Slides 4 Wells Arbovirus"));
            format.add(new OptionListItem("96H", "96 Well Plate Horizontal"));
            format.add(new OptionListItem("96V", "96 Well Plate Vertical"));
            format.add(new OptionListItem("AFBCulture", "AFB Culture"));
            format.add(new OptionListItem("EntericCulture", "Enteric Culture"));
            format.add(new OptionListItem("LLS", "Line List Single Line"));
            format.add(new OptionListItem("LLM", "Line List Multi Line"));
            format.add(new OptionListItem("QFTG", "QFTG Plate"));
            format.add(new OptionListItem("WNV", "WNV Plate"));

            p.add(new Prompt("FORMAT", Prompt.Type.ARRAY).setPrompt("Format:")
                                                         .setWidth(200)
                                                         .setOptionList(format)
                                                         .setMultiSelect(false)
                                                         .setRequired(true));

            prn = printers.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
                                                          .setRequired(true));

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
        Connection con;
        HashMap<String, Object> jparam;
        HashMap<String, QueryData> param;
        JasperPrint jprint;
        JasperReport jreport;
        Path path;
        DiagramDataSource dDS;
        ReportStatus status;
        String format, dir, printer, printstat, userName;
        Integer worksheetId;
        URL url;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("WorksheetPrintReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        worksheetId = ReportUtil.getIntegerParameter(param, "WORKSHEET_ID");
        format = ReportUtil.getStringParameter(param, "FORMAT");
        printer = ReportUtil.getStringParameter(param, "PRINTER");

        if (worksheetId == null || DataBaseUtil.isEmpty(format) ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the worksheet id, format and printer for this report");

        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);

            userName = User.getName(ctx);

            jparam = new HashMap<String, Object>();
            jparam.put("USER_NAME", userName);

            jprint = null;
            path = null;
            switch (format) {
                case "4x12B":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide4x12Blank.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;

                case "6x10":
                    dDS = DiagramDataSource.getInstance(worksheetId, 60);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide6x10.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                case "6x12":
                    dDS = DiagramDataSource.getInstance(worksheetId, 72);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide6x12.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                case "8x4Arbo":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide8x4Arbo.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;

                case "96H":
                    dDS = DiagramDataSource.getInstance(worksheetId, 96);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plate96Horizontal.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                case "96V":
                    dDS = DiagramDataSource.getInstance(worksheetId, 96);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plate96Vertical.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                case "AFBCulture":
                case "EntericCulture":
                    path = fillPDFWorksheet(worksheetId, format);
                    break;
                    
                case "LLS":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/lineListSingleLine.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    jparam.put("SUBREPORT_DIR", dir);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;

                case "LLM":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/lineListMultiLine.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    jparam.put("SUBREPORT_DIR", dir);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;

                case "QFTG":
                    dDS = DiagramDataSource.getInstance(worksheetId, 36);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plateQFTG.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                case "WNV":
                    dDS = DiagramDataSource.getInstance(worksheetId, 15);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plateWNV.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;

                default:
                    throw new InconsistencyException("An invalid format was specified for this report");
            }

            if (path == null) {
                if (ReportUtil.isPrinter(printer))
                    path = export(jprint, null);
                else
                    path = export(jprint, "upload_stream_directory");
            }

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(path, userName, printer, 1, true);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                status.setMessage(path.getFileName().toString())
                      .setPath(path.toString())
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

    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("worksheetPrint", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for worksheet print report");
            }
        }
        return path;
    }
    
    private Path fillPDFWorksheet(Integer worksheetId, String templateName) throws Exception {
        int i, formCapacity;
        AcroFields form;
        AnalysisViewDO aVDO;
        ArrayList<AnalysisQaEventViewDO> aqeVDOs;
        ArrayList<Integer> analysisIds;
        ArrayList<NoteViewDO> nVDOs;
        ArrayList<SampleManager1> sMans;
        ArrayList<SampleOrganizationViewDO> sOrgs;
        ArrayList<SampleQaEventViewDO> sqeVDOs;
        ArrayList<SystemVariableDO> sysVars;
        ArrayList<WorksheetAnalysisViewDO> wAnalyses; 
        ByteArrayOutputStream page;
        Document doc;
        HashMap<Integer, SampleManager1> sMap;
        OutputStream out;
        Path path;
        PatientDO patDO;
        PdfCopy writer;
        PdfReader reader;
        PdfStamper stamper;
        ProviderDO proDO;
        RandomAccessFileOrArray original;
        SampleDO sDO;
        SampleItemViewDO siVDO;
        SampleManager1 sMan;
        String collectionDateTime, dirName;
        StringBuilder aNotes, aQaevents, sNotes, sQaevents;
        WorksheetItemDO wiDO;
        WorksheetManager1 wMan;

        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: " +
                                anyE.getMessage());
        }

        analysisIds = new ArrayList<Integer>();
        wMan = worksheetManager.fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
        wAnalyses = WorksheetManager1Accessor.getAnalyses(wMan);
        for (WorksheetAnalysisViewDO data : WorksheetManager1Accessor.getAnalyses(wMan)) {
            if (data.getAnalysisId() != null)
                analysisIds.add(data.getAnalysisId());
        }
        
        sMap = new HashMap<Integer, SampleManager1>();
        sMans = sampleManager.fetchByAnalyses(analysisIds, SampleManager1.Load.ORGANIZATION,
                                              SampleManager1.Load.QA, SampleManager1.Load.NOTE,
                                              SampleManager1.Load.SINGLEANALYSIS,
                                              SampleManager1.Load.PROVIDER);
        for (SampleManager1 data : sMans) {
            for (AnalysisViewDO data1 : SampleManager1Accessor.getAnalyses(data))
                sMap.put(data1.getId(), data);
        }
        
        formCapacity = 1;
        out = null;
        try {
            path = ReportUtil.createTempFile("worksheetPrint", ".pdf", null);
            out = Files.newOutputStream(path);

            reader = new PdfReader(dirName + "OEWorksheet" + templateName + ".pdf");
            original = reader.getSafeFile();
            doc = new Document(reader.getPageSizeWithRotation(1));
            writer = new PdfCopy(doc, out);
            doc.open();
            reader.close();
            
            i = -1;
            form = null;
            page = null;
            stamper = null;
            for (WorksheetAnalysisViewDO waVDO : wAnalyses) {
                if (waVDO.getAnalysisId() == null)
                    continue;
                
                if (i == -1 || i == formCapacity) {
                    if (i != -1) {
                        stamper.setFormFlattening(true); 
                        stamper.close();
                        reader.close();
                        
                        reader = new PdfReader(page.toByteArray());
                        writer.addPage(writer.getImportedPage(reader, 1));
                        reader.close();
                    }
                    reader = new PdfReader(original, null);
                    page = new ByteArrayOutputStream();
                    stamper = new PdfStamper(reader, page);
                    form = stamper.getAcroFields();
                    i = 0;
                }
                
                wiDO = (WorksheetItemDO) wMan.getObject(wMan.getWorksheetItemUid(waVDO.getWorksheetItemId()));
                sMan = sMap.get(waVDO.getAnalysisId());
                sDO = sMan.getSample();
                patDO = null;
                proDO = null;
                if (Constants.domain().CLINICAL.equals(sDO.getDomain())) {
                    patDO = sMan.getSampleClinical().getPatient();
                    proDO = sMan.getSampleClinical().getProvider();
                } else if (Constants.domain().NEONATAL.equals(sDO.getDomain())) {
                    patDO = sMan.getSampleNeonatal().getPatient();
                    proDO = sMan.getSampleNeonatal().getProvider();
                }
                sOrgs = SampleManager1Accessor.getOrganizations(sMan);
                aVDO = (AnalysisViewDO) sMan.getObject(Constants.uid().getAnalysis(waVDO.getAnalysisId()));
                siVDO = (SampleItemViewDO) sMan.getObject(Constants.uid().getSampleItem(aVDO.getSampleItemId()));
                
                sQaevents = new StringBuilder();
                sqeVDOs = SampleManager1Accessor.getSampleQAs(sMan);
                if (sqeVDOs != null && sqeVDOs.size() > 0) {
                    for (SampleQaEventViewDO sqeVDO : sqeVDOs) {
                        if (sQaevents.length() > 0)
                            sQaevents.append(" | ");
                        sQaevents.append(sqeVDO.getQaEventName());
                    }
                }
                
                aQaevents = new StringBuilder();
                aqeVDOs = SampleManager1Accessor.getAnalysisQAs(sMan);
                if (aqeVDOs != null && aqeVDOs.size() > 0) {
                    for (AnalysisQaEventViewDO aqeVDO : aqeVDOs) {
                        if (!waVDO.getAnalysisId().equals(aqeVDO.getAnalysisId()))
                            continue;
                        if (aQaevents.length() > 0)
                            aQaevents.append(" | ");
                        aQaevents.append(aqeVDO.getQaEventName());
                    }
                }
                
                sNotes = new StringBuilder();
                nVDOs = new ArrayList<NoteViewDO>();
                if (SampleManager1Accessor.getSampleInternalNotes(sMan) != null)
                    nVDOs.addAll(SampleManager1Accessor.getSampleInternalNotes(sMan));
                if (SampleManager1Accessor.getSampleExternalNote(sMan) != null)
                    nVDOs.add(SampleManager1Accessor.getSampleExternalNote(sMan));
                if (nVDOs != null && nVDOs.size() > 0) {
                    for (NoteViewDO nVDO : nVDOs) {
                        if (sNotes.length() > 0)
                            sNotes.append(" | ");
                        sNotes.append(nVDO.getText());
                    }
                }
                
                aNotes = new StringBuilder();
                nVDOs = new ArrayList<NoteViewDO>();
                if (SampleManager1Accessor.getAnalysisInternalNotes(sMan) != null)
                    nVDOs.addAll(SampleManager1Accessor.getAnalysisInternalNotes(sMan));
                if (SampleManager1Accessor.getAnalysisExternalNotes(sMan) != null)
                    nVDOs.addAll(SampleManager1Accessor.getAnalysisExternalNotes(sMan));
                if (nVDOs != null && nVDOs.size() > 0) {
                    for (NoteViewDO nVDO : nVDOs) {
                        if (!waVDO.getAnalysisId().equals(nVDO.getReferenceId()))
                            continue;
                        if (aNotes.length() > 0)
                            aNotes.append(" | ");
                        aNotes.append(nVDO.getText());
                    }
                }
                
                form.setField("worksheet_id_"+(i + 1), worksheetId.toString());
                form.setField("position_"+(i + 1), wiDO.getPosition().toString());
                form.setField("accession_number_"+(i + 1), sDO.getAccessionNumber().toString());
                if (sDO.getCollectionDate() != null) {
                    collectionDateTime = ReportUtil.toString(sDO.getCollectionDate(), Messages.get().datePattern());
                    if (sDO.getCollectionTime() != null) {
                        collectionDateTime += " ";
                        collectionDateTime += ReportUtil.toString(sDO.getCollectionTime(), Messages.get().timePattern());
                    }
                    form.setField("collection_date_"+(i + 1), collectionDateTime);
                }
                form.setField("received_date_"+(i + 1), ReportUtil.toString(sDO.getReceivedDate(), Messages.get().dateTimePattern()));
                if (patDO != null) {
                    form.setField("patient_last_"+(i + 1), patDO.getLastName());
                    form.setField("patient_first_"+(i + 1), patDO.getFirstName());
                }
                if (proDO != null) {
                    form.setField("provider_last_"+(i + 1), proDO.getLastName());
                    form.setField("provider_first_"+(i + 1), proDO.getFirstName());
                }
                if (sOrgs != null && sOrgs.size() > 0) {
                    for (SampleOrganizationViewDO soVDO : sOrgs) {
                        if (Constants.dictionary().ORG_REPORT_TO.equals(soVDO.getTypeId()))
                            form.setField("organization_name_"+(i + 1), soVDO.getOrganizationName());
                        else if (Constants.dictionary().ORG_BILL_TO.equals(soVDO.getTypeId()))
                            form.setField("bill_to_name_"+(i + 1), soVDO.getOrganizationName());
                    }
                }
                form.setField("type_of_sample_"+(i + 1), siVDO.getTypeOfSample());
                form.setField("source_of_sample_"+(i + 1), siVDO.getSourceOfSample());
                form.setField("source_other_"+(i + 1), siVDO.getSourceOther());
                form.setField("sample_qaevent_"+(i + 1), sQaevents.toString());
                form.setField("analysis_qaevent_"+(i + 1), aQaevents.toString());
                form.setField("sample_note_"+(i + 1), sNotes.toString());
                form.setField("analysis_note_"+(i + 1), aNotes.toString());
                i++;
            }
            stamper.setFormFlattening(true); 
            stamper.close();
            reader.close();
            
            reader = new PdfReader(page.toByteArray());
            writer.addPage(writer.getImportedPage(reader, 1));
            reader.close();
            writer.close();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for worksheet print report");
            }
        }
        return path;
    }
}