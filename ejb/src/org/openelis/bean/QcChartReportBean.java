package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcChartReportViewVO;
import org.openelis.domain.QcChartReportViewVO.ReportType;
import org.openelis.domain.QcChartReportViewVO.Value;
import org.openelis.domain.QcChartResultVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.remote.QcChartReportRemote;
import org.openelis.report.qcchart.QcChartDataSource;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class QcChartReportBean implements QcChartReportRemote {
    @EJB
    private SessionCacheLocal      session;

    @EJB
    private WorksheetAnalysisLocal worksheetAnalysis;

    @EJB
    private DictionaryLocal        dictionary;

    @EJB
    private CategoryCacheLocal     categoryCache;

    @EJB
    private AnalyteParameterLocal  analyteParameter;

    private static final Logger    log = Logger.getLogger(QcChartReportBean.class);

    private static Integer         typeDynamicId, typeFixedId, typeQcSpike, typeQcBlank, typeQcDuplicate;

    @PostConstruct
    public void init() {
        try {
            typeDynamicId = dictionary.fetchBySystemName("chart_type_dynamic").getId();
            typeFixedId = dictionary.fetchBySystemName("chart_type_fixed").getId();
            typeQcSpike = dictionary.fetchBySystemName("qc_spike").getId();
            typeQcBlank = dictionary.fetchBySystemName("qc_blank").getId();
            typeQcDuplicate = dictionary.fetchBySystemName("qc_duplicate").getId();
        } catch (Throwable e) {
            log.error("Failed to lookup constants for dictionary entries", e);
        }
    }

    public QcChartReportViewVO fetchForQcChart(ArrayList<QueryData> paramList) throws Exception {
        Integer plot, qc, number;
        String analyteName, worksheetCreatedDateFrom, worksheetCreatedDateTo, numInstances, qcName, plotType, systemName, qcType;
        Datetime startDate, endDate;
        QcChartReportViewVO.Value vo;
        QcChartReportViewVO data, qcChartVO;
        ArrayList<Value> qcList, list;
        ArrayList<QcChartResultVO> resultList;
        HashMap<String, QcChartReportViewVO> analyteMap;
        HashMap<String, QueryData> param;
        HashMap<String, HashMap<String, Integer>> columnMap;

        param = ReportUtil.getMapParameter(paramList);

        worksheetCreatedDateFrom = ReportUtil.getSingleParameter(param, "WORKSHEET_CREATED_DATE_FROM");
        worksheetCreatedDateTo = ReportUtil.getSingleParameter(param, "WORKSHEET_CREATED_DATE_TO");
        numInstances = ReportUtil.getSingleParameter(param, "NUM_INSTANCES");
        qcName = ReportUtil.getSingleParameter(param, "QC_NAME");
        qcType = ReportUtil.getSingleParameter(param, "QC_TYPE");
        plotType = ReportUtil.getSingleParameter(param, "PLOT_TYPE");

        try {
            plot = Integer.parseInt(plotType);
            qc = Integer.parseInt(qcType);
        } catch (Exception e) {
            throw new InconsistencyException("You must specify a valid plot type or qc type or number of instances.");
        }

        /*
         * The report can be run either by dates or number of instances going
         * back from now.
         */
        resultList = null;
        try {
            if (worksheetCreatedDateFrom != null && worksheetCreatedDateTo != null) {
                startDate = ReportUtil.getDatetime(Datetime.YEAR,
                                                   Datetime.DAY,
                                                   worksheetCreatedDateFrom);
                endDate = ReportUtil.getDatetime(Datetime.YEAR,
                                                 Datetime.DAY,
                                                 worksheetCreatedDateTo);
                resultList = worksheetAnalysis.fetchByDateForQcChart(startDate.getDate(),
                                                                     endDate.getDate(),
                                                                     qcName);
            } else if (numInstances != null) {
                number = Integer.parseInt(numInstances);
                resultList = worksheetAnalysis.fetchByInstancesForQcChart(number, qcName);
            }
            if (resultList.size() == 0)
                throw new NotFoundException("No data found for the query. Please change your query parameters.");
        } catch (Exception e) {
            log.error(e);
            throw e;
        }

        columnMap = new HashMap<String, HashMap<String, Integer>>();
        data = new QcChartReportViewVO();
        qcList = new ArrayList<Value>();
        analyteMap = new HashMap<String, QcChartReportViewVO>();
        list = null;

        for (QcChartResultVO result : resultList) {
            systemName = result.getWorksheetFormat();
            loadMapForQC(systemName, columnMap);

            vo = getCommonFields(result);
            if (DataBaseUtil.isSame(typeQcSpike, qc) && systemName.equals("wf_rad1")) {
                vo = getQCSpikePercent(result, vo, columnMap);
                data.setReportType(ReportType.SPIKE_PERCENT);
            } else if (DataBaseUtil.isSame(typeQcSpike, qc)) {
                vo = getQCSpikeConc(result, vo, columnMap);
                data.setReportType(ReportType.SPIKE_CONC);
            }

            if (vo != null) {
                qcList.add(vo);
                if (vo.getPlotValue() == null || "N".equals(vo.getIsPlot())) {
                    vo.setMean(null);
                    vo.setLCL(null);
                    vo.setUCL(null);
                    vo.setLWL(null);
                    vo.setUWL(null);
                    vo.setSd(null);
                    continue;
                }

                // for each new analyte create a new entry in map.
                analyteName = vo.getAnalyteName();
                qcChartVO = analyteMap.get(analyteName);
                if (qcChartVO == null) {
                    list = new ArrayList<Value>();
                    qcChartVO = new QcChartReportViewVO();
                    qcChartVO.setQcList(list);
                    analyteMap.put(analyteName, qcChartVO);
                } else {
                    list = qcChartVO.getQcList();
                }
                list.add(vo);
            }
        }

        if (DataBaseUtil.isSame(typeDynamicId, plot)) {
            for (Entry<String, QcChartReportViewVO> entry : analyteMap.entrySet())
                calculateDynamicStatistics(entry.getValue());

        } else if (DataBaseUtil.isSame(typeFixedId, plot)) {
            for (Entry<String, QcChartReportViewVO> entry : analyteMap.entrySet())
                calculateStaticStatistics(entry.getValue());
        }
        data.setQcList(qcList);
        data.setPlotType(plot);
        data.setQcType(qc);
        return data;
    }

    public QcChartReportViewVO recompute(QcChartReportViewVO dataPoints) throws Exception {
        String analyteName;
        QcChartReportViewVO vo;
        ArrayList<Value> list, qcList;
        QcChartReportViewVO voList;
        HashMap<String, QcChartReportViewVO> analyteMap;

        qcList = dataPoints.getQcList();
        analyteMap = new HashMap<String, QcChartReportViewVO>();
        list = null;
        for (Value data : qcList) {
            if (data.getPlotValue() == null || "N".equals(data.getIsPlot())) {
                data.setMean(null);
                data.setLCL(null);
                data.setUCL(null);
                data.setLWL(null);
                data.setUWL(null);
                data.setSd(null);
                continue;
            }

            // for each new analyte create a new entry in map.
            analyteName = data.getAnalyteName();
            voList = analyteMap.get(analyteName);
            if (voList == null) {
                list = new ArrayList<Value>();
                voList = new QcChartReportViewVO();
                voList.setQcList(list);
                analyteMap.put(analyteName, voList);
            } else {
                list = voList.getQcList();
            }
            list.add(data);
        }

        /*
         * Compute values for each analyte
         */
        for (Entry<String, QcChartReportViewVO> entry : analyteMap.entrySet())
            if (DataBaseUtil.isSame(typeDynamicId, dataPoints.getPlotType()))
                calculateDynamicStatistics(entry.getValue());
            else if (DataBaseUtil.isSame(typeFixedId, dataPoints.getPlotType()))
                calculateStaticStatistics(entry.getValue());
        vo = new QcChartReportViewVO();
        vo.setQcList(qcList);
        vo.setPlotType(dataPoints.getPlotType());
        vo.setQcType(dataPoints.getQcType());
        vo.setReportType(dataPoints.getReportType());
        vo.setQcName(dataPoints.getQcName());
        return vo;
    }

    public ReportStatus runReport(QcChartReportViewVO dataPoints) throws Exception {
        String qcName, printstat, printer;
        QcChartReportViewVO result;
        ArrayList<Value> list;
        URL url;
        File tempFile;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        QcChartDataSource ds;
        HashMap<String, Object> jparam;

        result = recompute(dataPoints);

        qcName = result.getQcName();
        // printer = ReportUtil.getSingleParameter(param, "PRINTER");
        printer = "-view-";
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("qcChartReport", status);
        list = result.getQcList();

        if (list.size() > 1)
            Collections.sort(list, new MyComparator());
        ds = new QcChartDataSource();
        ds.setValues(list);

        try {
            status.setMessage("Initializing report");
            if ( (ReportType.SPIKE_CONC).equals(result.getReportType()))
                url = ReportUtil.getResourceURL("org/openelis/report/qcchart/spikeConc.jasper");
            else
                url = ReportUtil.getResourceURL("org/openelis/report/qcchart/spikePercent.jasper");

            tempFile = File.createTempFile("qcreport", ".pdf", new File("/tmp"));
            jparam = new HashMap<String, Object>();
            jparam.put("LOGNAME", EJBFactory.getUserCache().getName());
            jparam.put("QCNAME", qcName);

            status.setMessage("Loading report");
            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, ds);
            jexport = new JRPdfExporter();
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

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
        }
        return status;
    }

    /*
     * Copies common fields to the data VO for plotting
     */
    protected QcChartReportViewVO.Value getCommonFields(QcChartResultVO result) throws Exception {
        QcChartReportViewVO.Value vo;

        vo = new QcChartReportViewVO.Value();
        vo.setIsPlot("Y");
        vo.setAccessionNumber(result.getAccessionNumber());
        vo.setLotNumber(result.getLotNumber());
        vo.setWId(result.getWId());
        vo.setQcId(result.getQcId());
        vo.setAnalyteId(result.getAnalyteId());
        vo.setAnalyteName(result.getAnalyteParameter());
        vo.setWorksheetCreatedDate(result.getWorksheetCreatedDate());

        return vo;
    }

    /*
     * For spike QCs the program calculates the % recovery from 2 values.
     * Depending on the worksheet format, the data is recovered from different
     * fields and computed/stored in plotValue.
     */
    protected QcChartReportViewVO.Value getQCSpikeConc(QcChartResultVO result,
                                                       QcChartReportViewVO.Value value,
                                                       HashMap<String, HashMap<String, Integer>> map) throws Exception {

        String worksheetFormat;

        worksheetFormat = result.getWorksheetFormat();
        value.setValue1(getValue(worksheetFormat, "expected_value", result, map));
        value.setValue2(getValue(worksheetFormat, "final_percent_recov", result, map));
        try {
            value.setPlotValue(Double.parseDouble(getValue(worksheetFormat,
                                                           "final_value",
                                                           result,
                                                           map)));
        } catch (Exception e) {
            value.setPlotValue(null);
            value.setIsPlot("N");
        }
        if (value.getPlotValue() == null)
            value = null;

        return value;
    }

    protected QcChartReportViewVO.Value getQCSpikePercent(QcChartResultVO result,
                                                          QcChartReportViewVO.Value value,
                                                          HashMap<String, HashMap<String, Integer>> map) throws Exception {
        Double plotValue;
        String value1, value2;
        String worksheetFormat;

        plotValue = null;
        worksheetFormat = result.getWorksheetFormat();

        value1 = getValue(worksheetFormat, "final_value", result, map);
        value2 = getValue(worksheetFormat, "expected_value", result, map);
        if (value1 != null && value2 != null) {
            try {
                plotValue = Double.parseDouble(value1) / Double.parseDouble(value2) * 100;
            } catch (Exception e) {
                value.setPlotValue(null);
                value.setIsPlot("N");
            }
            value.setValue1(value1);
            value.setValue2(value2);
            value.setPlotValue(plotValue);
        } else {
            // discard vo's that don't have values.
            value = null;
        }

        return value;
    }

    protected void loadMapForQC(String worksheetFormat,
                                HashMap<String, HashMap<String, Integer>> map) throws Exception {
        int i;
        CategoryCacheVO vo;
        HashMap<String, Integer> columns;
        ArrayList<DictionaryDO> list;

        columns = map.get(worksheetFormat);
        if (columns != null)
            return;

        vo = categoryCache.getBySystemName(worksheetFormat);
        list = vo.getDictionaryList();
        columns = new HashMap<String, Integer>();
        for (i = 0; i < list.size(); i++ )
            columns.put(list.get(i).getSystemName(), i);
        map.put(worksheetFormat, columns);
    }

    /*
     * Retrieves values from columns of the worksheet depending on the
     * appropriate worksheet format and the column name.
     */
    protected String getValue(String worksheetFormat, String columnName, QcChartResultVO result,
                              HashMap<String, HashMap<String, Integer>> map) throws Exception {
        Integer column;
        String value;

        value = null;
        column = map.get(worksheetFormat).get(worksheetFormat + "_" + columnName);
        if (column != null)
            value = result.getValueAt(column);

        return value;
    }

    private void calculateStaticStatistics(QcChartReportViewVO list) throws Exception {
        AnalyteParameterViewDO apVDO;
        ArrayList<Value> qcList;

        apVDO = null;
        qcList = list.getQcList();
        for (Value vo : qcList) {
            try {
                apVDO = analyteParameter.fetchForQcChartReport(vo.getAnalyteId(),
                                                               vo.getQcId(),
                                                               ReferenceTable.QC,
                                                               vo.getWorksheetCreatedDate()
                                                                 .getDate());
                if (apVDO != null) {
                    vo.setMean(apVDO.getP3());
                    vo.setUCL(apVDO.getP2());
                    vo.setLCL(apVDO.getP1());
                }
            } catch (NotFoundException e) {
                log.error("analyte parameters not found for the analysis");
            } catch (Exception e) {
                log.error("Error retrieving analyte parameters for an analysis on worksheet", e);
            }
        }
    }

    private void calculateDynamicStatistics(QcChartReportViewVO list) throws Exception {
        int i, numValue;
        Double mean, sd, diff, sqDiffSum, sum, uWL, uCL, lWL, lCL;
        Value value;
        ArrayList<Value> qcList;

        qcList = list.getQcList();

        numValue = qcList.size();
        sum = 0.0;
        for (i = 0; i < numValue; i++ )
            sum += qcList.get(i).getPlotValue();

        mean = sum / numValue;
        sqDiffSum = 0.0;
        if (numValue > 1) {
            for (i = 0; i < numValue; i++ ) {
                diff = qcList.get(i).getPlotValue() - mean;
                sqDiffSum += diff * diff;
            }
            sd = Math.sqrt(sqDiffSum / (numValue - 1));
            uWL = mean + 2 * sd;
            uCL = mean + 3 * sd;
            lWL = mean - 2 * sd;
            lCL = mean - 3 * sd;
        } else {
            sd = null;
            uWL = mean;
            uCL = mean;
            lWL = mean;
            lCL = mean;
        }
        for (i = 0; i < numValue; i++ ) {
            value = qcList.get(i);
            value.setMean(mean);
            value.setUWL(uWL);
            value.setUCL(uCL);
            value.setLWL(lWL);
            value.setLCL(lCL);
            value.setSd(sd);
        }
    }

    class MyComparator implements Comparator<QcChartReportViewVO.Value> {
        public int compare(Value v1, Value v2) {
            return v1.getAnalyteName().compareTo(v2.getAnalyteName());
        }
    }
}