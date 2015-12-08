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

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcChartReportViewVO;
import org.openelis.domain.QcChartReportViewVO.ReportType;
import org.openelis.domain.QcChartReportViewVO.Value;
import org.openelis.domain.QcChartResultVO;
import org.openelis.meta.QcChartMeta;
import org.openelis.report.qcchart.QcChartDataSource;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class QcChartReport1Bean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                               manager;

    @Resource
    private SessionContext                              ctx;

    @EJB
    private SessionCacheBean                            session;

    @EJB
    private CategoryCacheBean                           categoryCache;

    @EJB
    private AnalyteParameterBean                        analyteParameter;

    protected HashMap<String, ArrayList<String>>        formatHeaderMap;
    protected HashMap<String, HashMap<String, Integer>> formatColumnMap;

    private static final Logger                         log = Logger.getLogger("openelis");

    public QcChartReportViewVO fetchData(ArrayList<QueryData> paramList) throws Exception {
        Integer plot, qc, number, location;
        String qcName;
        Date startDate, endDate;
        QcChartReportViewVO.Value vo;
        QcChartReportViewVO data;
        ArrayList<Value> qcList;
        ArrayList<QcChartResultVO> resultList;
        HashMap<String, QueryData> param;

        param = ReportUtil.getMapParameter(paramList);

        startDate = ReportUtil.getDateParameter(param, QcChartMeta.getWorksheetCreatedDateFrom());
        endDate = ReportUtil.getDateParameter(param, QcChartMeta.getWorksheetCreatedDateTo());
        number = ReportUtil.getIntegerParameter(param, QcChartMeta.getNumInstances());
        qcName = ReportUtil.getStringParameter(param, QcChartMeta.getQCName());
        qc = ReportUtil.getIntegerParameter(param, QcChartMeta.getQCType());
        plot = ReportUtil.getIntegerParameter(param, QcChartMeta.getPlotType());
        location = ReportUtil.getIntegerParameter(param, QcChartMeta.getLocationId());

        if (plot == null || qc == null || location == null)
            throw new InconsistencyException("You must specify a valid plot type, qc type, or location.");

        /*
         * The report can be run either by dates or number of instances going
         * back from now.
         */
        resultList = null;
        try {
            if (startDate != null && endDate != null)
                resultList = fetchByDate(startDate, endDate, qcName, location);
            else if (number != null)
                resultList = fetchByInstances(number, qcName, location);

            if (resultList.size() == 0)
                throw new NotFoundException("No data found for the query. Please change your query parameters.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not fetch worksheet analyses", e);
            throw e;
        }

        try {
            formatColumnMap = new HashMap<String, HashMap<String, Integer>>();
            data = new QcChartReportViewVO();
            qcList = new ArrayList<Value>();

            Collections.sort(resultList, new ResultComparator());
            for (QcChartResultVO result : resultList) {
                vo = getCommonFields(result);
                if (DataBaseUtil.isSame(Constants.dictionary().QC_SPIKE, qc)) {
                    if ("wf_rad1".equals(result.getWorksheetFormat())) {
                        vo = getQCSpikePercent(result, vo);
                        data.setReportType(ReportType.SPIKE_PERCENT);
                    } else {
                        vo = getQCSpikeConc(result, vo);
                        data.setReportType(ReportType.SPIKE_CONC);
                    }
                } else {
                    data.setReportType(ReportType.EXCEL);
                }
    
                if (vo != null)
                    qcList.add(vo);
            }
        } finally {
            formatColumnMap = null;
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
                data.setMeanRecovery(null);
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
                voList.setReportType(dataPoints.getReportType());
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
            if (DataBaseUtil.isSame(Constants.dictionary().CHART_TYPE_DYNAMIC,
                                    dataPoints.getPlotType()))
                calculateDynamicStatistics(entry.getValue());
            else if (DataBaseUtil.isSame(Constants.dictionary().CHART_TYPE_FIXED,
                                         dataPoints.getPlotType()))
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
        ArrayList<Value> list;
        HashMap<String, Object> jparam;
        JasperPrint jprint;
        JasperReport jreport;
        Path path;
        QcChartDataSource ds;
        QcChartReportViewVO result;
        ReportStatus status;
        String qcName, userName;
        URL url;
        XSSFWorkbook wb;

        result = recompute(dataPoints);

        qcName = result.getQcName();

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("qcChartReport", status);
        list = result.getQcList();

        if (list.size() > 1)
            Collections.sort(list, new ValueComparator());
        ds = new QcChartDataSource();
        ds.setValues(list);

        url = null;
        wb = null;
        try {
            status.setMessage("Initializing report");
            session.setAttribute("qcChartReport", status);
            if ((ReportType.SPIKE_CONC).equals(result.getReportType()))
                url = ReportUtil.getResourceURL("org/openelis/report/qcchart/spikeConc.jasper");
            else if ((ReportType.SPIKE_PERCENT).equals(result.getReportType()))
                url = ReportUtil.getResourceURL("org/openelis/report/qcchart/spikePercent.jasper");

            status.setMessage(Messages.get().report_outputReport()).setPercentComplete(20);
            session.setAttribute("qcChartReport", status);
            
            if (url != null) {
                userName = User.getName(ctx);
    
                jparam = new HashMap<String, Object>();
                jparam.put("LOGNAME", userName);
                jparam.put("QCNAME", qcName);
                jparam.put("USER_NAME", userName);
    
                jreport = (JasperReport)JRLoader.loadObject(url);
                jprint = JasperFillManager.fillReport(jreport, jparam, ds);
                path = export(jprint, "upload_stream_directory");
            } else {
                wb = getWorkbook(list, status);
                for (XSSFSheet sheet : wb)
                    setTitleCell(sheet, wb, qcName);
                path = export(wb, "upload_stream_directory");
            }

            status.setPercentComplete(100)
                  .setMessage(path.getFileName().toString())
                  .setPath(path.toString())
                  .setStatus(ReportStatus.Status.SAVED);
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
        int i;
        QcChartReportViewVO.Value vo;

        vo = new QcChartReportViewVO.Value();
        vo.setIsPlot("Y");
        vo.setAccessionNumber(result.getAccessionNumber());
        vo.setLotNumber(result.getLotNumber());
        vo.setWId(result.getWId());
        vo.setQcId(result.getQcId());
        vo.setAnalyteId(result.getAnalyteId());
        vo.setAnalyteName(result.getAnalyteName());
        vo.setWorksheetCreatedDate(result.getWorksheetCreatedDate());
        vo.setWorksheetFormat(result.getWorksheetFormat());
        for (i = 0; i < 30; i++)
            vo.setValueAt(i, result.getValueAt(i));

        return vo;
    }

    /*
     * For spike QCs the program calculates the % recovery from 2 values.
     * Depending on the worksheet format, the data is recovered from different
     * fields and computed/stored in plotValue.
     */
    protected QcChartReportViewVO.Value getQCSpikeConc(QcChartResultVO result,
                                                       QcChartReportViewVO.Value value) throws Exception {

        String worksheetFormat;

        worksheetFormat = result.getWorksheetFormat();
        value.setValue1(getValue(worksheetFormat, "expected_value", result));
        value.setValue2(getValue(worksheetFormat, "percent_recovery", result));
        try {
            value.setPlotValue(Double.parseDouble(getValue(worksheetFormat,
                                                           "final_value",
                                                           result)));
        } catch (Exception e) {
            value.setPlotValue(null);
            value.setIsPlot("N");
        }
        if (value.getPlotValue() == null)
            value = null;

        return value;
    }

    protected QcChartReportViewVO.Value getQCSpikePercent(QcChartResultVO result,
                                                          QcChartReportViewVO.Value value) throws Exception {
        Double plotValue;
        String value1, value2;
        String worksheetFormat;

        plotValue = null;
        worksheetFormat = result.getWorksheetFormat();

        value1 = getValue(worksheetFormat, "final_value", result);
        value2 = getValue(worksheetFormat, "expected_value", result);
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

    /*
     * Retrieves values from columns of the worksheet depending on the
     * appropriate worksheet format and the column name.
     */
    protected String getValue(String worksheetFormat, String columnName, QcChartResultVO result) throws Exception {
        int i;
        ArrayList<DictionaryDO> list;
        CategoryCacheVO vo;
        HashMap<String, Integer> columnMap;
        Integer column;
        String value;

        columnMap = formatColumnMap.get(worksheetFormat);
        if (columnMap == null) {
            vo = categoryCache.getBySystemName(worksheetFormat);
            list = vo.getDictionaryList();
            columnMap = new HashMap<String, Integer>();
            for (i = 0; i < list.size(); i++)
                columnMap.put(list.get(i).getSystemName(), i);
            formatColumnMap.put(worksheetFormat, columnMap);
        }
        
        value = null;
        column = columnMap.get(worksheetFormat + "_" + columnName);
        if (column != null)
            value = result.getValueAt(column);

        return value;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<QcChartResultVO> fetchByDate(Date dateFrom, Date dateTo, String qcName, Integer qcLocation) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("WorksheetAnalysis.FetchByDateForQcChart");
        query.setParameter("startedDate", dateFrom);
        query.setParameter("endDate", dateTo);
        query.setParameter("qcName", qcName);
        query.setParameter("qcLocation", qcLocation);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<QcChartResultVO> fetchByInstances(Integer numInstances, String qcName, Integer qcLocation) throws Exception {
        Integer id;
        Query query;
        ArrayList<Object[]> list;
        ArrayList<Integer> ids;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByInstancesForQcChart");
        query.setParameter("qcName", qcName);
        query.setParameter("qcLocation", qcLocation);
        query.setMaxResults(numInstances);

        list = DataBaseUtil.toArrayList(query.getResultList());
        
        ids = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++ ) {
            id = (Integer)(list.get(i))[1];
            if (id != null)
                ids.add(id);
        }
        if (ids.size() == 0)
            return new ArrayList<QcChartResultVO>();

        query = manager.createNamedQuery("WorksheetAnalysis.FetchAnalytesForQcChart");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
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
                                                               Constants.table().QC,
                                                               vo.getWorksheetCreatedDate()
                                                                 .getDate());
                if (apVDO != null) {
                    vo.setMean(apVDO.getP3());
                    vo.setUCL(apVDO.getP2());
                    vo.setLCL(apVDO.getP1());
                }
            } catch (NotFoundException ignE) {
                // ignore not found exception
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Error retrieving analyte parameters for an analysis on worksheet",
                        e);
            }
        }
    }

    private void calculateDynamicStatistics(QcChartReportViewVO list) throws Exception {
        int i, numValue;
        Double mean, meanRecovery, sd, diff, sqDiffSum, sum, recovery, uWL, uCL, lWL, lCL;
        Value value;
        ArrayList<Value> qcList;

        qcList = list.getQcList();

        numValue = qcList.size();
        sum = 0.0;
        recovery = 0.0;
        for (i = 0; i < numValue; i++ )
            sum += qcList.get(i).getPlotValue();

        if (QcChartReportViewVO.ReportType.SPIKE_CONC.equals(list.getReportType())) {
            for (i = 0; i < numValue; i++ ) {
                if (qcList.get(i).getValue2() != null)
                    recovery += Double.valueOf(qcList.get(i).getValue2());
            }
        } else if (QcChartReportViewVO.ReportType.SPIKE_PERCENT.equals(list.getReportType())) {
            for (i = 0; i < numValue; i++ )
                recovery += qcList.get(i).getPlotValue();
        }

        mean = sum / numValue;
        meanRecovery = recovery / numValue;
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
            value.setMeanRecovery(meanRecovery);
            value.setUWL(uWL);
            value.setUCL(uCL);
            value.setLWL(lWL);
            value.setLCL(lCL);
            value.setSd(sd);
        }
    }
    
    private XSSFWorkbook getWorkbook(ArrayList<Value> values, ReportStatus status) throws Exception {
        int i, colIndex, rowIndex, valueIndex;
        ArrayList<Integer> maxChars;
        ArrayList<String> headers, formatHeaders;
        Cell cell;
        HashMap<String, Integer> analyteColumnMap;
        Row row;
        String lastAnalyte, lastFormat;
        XSSFSheet sheet;
        XSSFWorkbook wb;
        
        analyteColumnMap = new HashMap<String, Integer>();
        formatHeaders = null;
        headers = new ArrayList<String>();
        lastAnalyte = "___";
        lastFormat = "___";
        maxChars = new ArrayList<Integer>();
        rowIndex = 32;
        sheet = null;
        valueIndex = 0;
        wb = new XSSFWorkbook();

        try {
            formatHeaderMap = new HashMap<String, ArrayList<String>>();
            for (Value value : values) {
                valueIndex++;
                
                if ("N".equals(value.getIsPlot()))
                    continue;
                
                if (!lastAnalyte.equals(value.getAnalyteName())) {
                    if (!"___".equals(lastAnalyte))
                        finishSheet(sheet, wb, maxChars, headers, analyteColumnMap);
    
                    sheet = wb.createSheet(value.getAnalyteName());
                    lastAnalyte = value.getAnalyteName();
                    rowIndex = 32;
                    lastFormat = "___";
                }
                
                if (!lastFormat.equals(value.getWorksheetFormat())) {
                    lastFormat = value.getWorksheetFormat();
                    formatHeaders = loadHeaders(headers, analyteColumnMap, lastFormat);    
                }
                
                row = sheet.createRow(rowIndex++);
                /*
                 * fill the passed row's cells for all columns except the ones for
                 * result values
                 */
                setBaseCells(value, row, maxChars);
                
                for (i = 0; i < formatHeaders.size(); i++) {
                    colIndex = analyteColumnMap.get(formatHeaders.get(i));
                    cell = row.createCell(colIndex);
                    setCellValue(cell, value.getValueAt(i));
                    setMaxChars(cell, maxChars);
                }
    
                status.setPercentComplete(70 * (valueIndex / values.size()) + 20);
                session.setAttribute("qcChartReport", status);
            }
            
            finishSheet(sheet, wb, maxChars, headers, analyteColumnMap);
        } finally {
            formatHeaderMap = null;
        }
        
        return wb;
    }
    
    /**
     * Loads the list of header labels for the sheet based on the specified worksheet
     * format.  If the passed list is empty, we first add the static headers.
     * 
     * @param headers
     *        the list that will be loaded with the headers for the sheet
     * @param analyteColumnMap
     *        the map of column names to indices for the current sheet
     * @param format
     *        the name of the worksheet format from which to load columns
     * @return formatHeaders
     *         the list of headers, in order, for the specified format
     */
    private ArrayList<String> loadHeaders(ArrayList<String> headers, HashMap<String, Integer> analyteColumnMap,
                                          String format) throws Exception {
        ArrayList<String> formatHeaders;
        
        if (headers.isEmpty()) {
            headers.add("Accession #");
            headers.add("Lot #");
            headers.add("Created Date");
        }
        
        formatHeaders = getFormatHeaders(format);
        for (String header : formatHeaders) {
            if (!analyteColumnMap.containsKey(header)) {
                analyteColumnMap.put(header, headers.size());
                headers.add(header);
            }
        }
        
        return formatHeaders;
    }

    /**
     * Fills all cells in "row" except the ones for result values.
     * 
     * @param value
     *        the object that contains data for the row
     * @param row
     *        the row whose cells are being filled
     * @param maxChars
     *        the list containing the maximum number of characters in each
     *        column; it's updated a when new value is set in a cell
     * @throws Exception
     */
    private void setBaseCells(Value value, Row row, ArrayList<Integer> maxChars) throws Exception {
        Cell cell;
        
        cell = row.createCell(0);
        setCellValue(cell, value.getAccessionNumber());
        setMaxChars(cell, maxChars);
        
        cell = row.createCell(1);
        setCellValue(cell, value.getLotNumber());
        setMaxChars(cell, maxChars);

        cell = row.createCell(2);
        setCellValue(cell, getDateTimeLabel(value.getWorksheetCreatedDate(), Messages.get().gen_dateTimePattern()));
        setMaxChars(cell, maxChars);
    }

    private void setTitleCell(XSSFSheet sheet, XSSFWorkbook wb, String title) {
        Cell cell;
        Row row;
        Font font;
        CellStyle style;

        /*
         * create the style to distinguish the title row from the other rows in
         * the output
         */
        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFont(font);
        
        row = sheet.createRow(29);

        cell = row.createCell(0);
        cell.setCellStyle(style);
        setCellValue(cell, "QC");

        cell = row.createCell(1);
        setCellValue(cell, title);

        row = sheet.createRow(30);

        cell = row.createCell(0);
        cell.setCellStyle(style);
        setCellValue(cell, "Analyte");

        cell = row.createCell(1);
        setCellValue(cell, sheet.getSheetName());
    }
    
    /**
     * Creates the header row in "sheet" from "headers"; sets a style on the
     * header row to distinguish it from the other rows; updates "maxChars" to
     * account for the header labels because the header row is added after the
     * other rows have been added
     * 
     * @param sheet
     *        the sheet that contains all rows in "wb"
     * @param wb
     *        the workbook that gets converted to an Excel file
     * @param headers
     *        the list of labels to be shown in the header row
     * @param maxChars
     *        the list containing the maximum number of characters in each
     *        column of "sheet"
     */
    private void setHeaderCells(XSSFSheet sheet, XSSFWorkbook wb, ArrayList<String> headers,
                                ArrayList<Integer> maxChars) {
        Cell cell;
        Row row;
        Font font;
        CellStyle style;

        /*
         * create the style to distinguish the header row from the other rows in
         * the output
         */
        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFont(font);

        row = sheet.createRow(31);
        for (int i = 0; i < headers.size(); i++ ) {
            cell = row.createCell(i);
            cell.setCellStyle(style);
            setCellValue(cell, headers.get(i));
            setMaxChars(cell, maxChars);
        }
    }
    
    private void finishSheet(XSSFSheet sheet, XSSFWorkbook wb, ArrayList<Integer> maxChars,
                             ArrayList<String> headers, HashMap<String, Integer> analyteColumnMap) {
        int i;
        
        /*
         * add the header row and set the header labels for all columns
         */
        setHeaderCells(sheet, wb, headers, maxChars);
        headers.clear();
        analyteColumnMap.clear();

        /*
         * make each column wide enough to show the longest string in it; the
         * width for each column is set as the maximum number of characters in
         * that column multiplied by 256; this is because the default width of
         * one character is 1/256 units in Excel
         */
        for (i = 0; i < maxChars.size(); i++ )
            sheet.setColumnWidth(i, maxChars.get(i) * 256);
        maxChars.clear();
    }
    
    private ArrayList<String> getFormatHeaders(String worksheetFormat) throws Exception {
        int i;
        ArrayList<DictionaryDO> list;
        ArrayList<String> formatHeaders;
        CategoryCacheVO vo;

        formatHeaders = formatHeaderMap.get(worksheetFormat);
        if (formatHeaders == null) {
            vo = categoryCache.getBySystemName(worksheetFormat);
            list = vo.getDictionaryList();
            formatHeaders = new ArrayList<String>();
            for (i = 0; i < list.size(); i++)
                formatHeaders.add(list.get(i).getEntry());
            formatHeaderMap.put(worksheetFormat, formatHeaders);
        }
        
        return formatHeaders;
    }

    /**
     * Converts the date and time in "dateTime" to a string formatted using
     * "pattern"
     * 
     * @param dateTime
     *        the Datetime object whose date and time is converted to a
     *        formatted string
     * @param pattern
     *        the pattern used to format the date and time in "dateTime"
     * @return the formatted string; null if "dateTime" is null
     */
    private String getDateTimeLabel(Datetime dateTime, String pattern) {
        String val;

        val = null;
        if (dateTime != null)
            val = ReportUtil.toString(dateTime, pattern);

        return val;
    }

    /**
     * Sets the string version of "value" as the value of "cell"; if the string
     * is longer than 255 characters, shortens it to 255 characters before
     * setting it; this is done to avoid exceeding the limit for the maximum
     * number of characters allowed in a cell by Excel  
     * 
     * @param cell
     *        the cell whose value is to be set
     * @param value
     *         the value to be set in "cell" 
     */
    private void setCellValue(Cell cell, Object value) {
        String val;

        val = DataBaseUtil.toString(value);
        if (val.length() > 255)
            val = val.substring(0, 255);
        cell.setCellValue(val);
    }

    /**
     * Keeps track of the maximum number of characters in each column of the
     * spreadsheet; if "cell" has more characters than the number in "maxChars"
     * for the cell's column, the number in "maxChars" is updated
     * 
     * @param cell
     *        a cell in a row in the spreadsheet
     * @param maxChars
     *        the list containing the maximum number of characters in each
     *        column of the spreadsheet
     */
    private void setMaxChars(Cell cell, ArrayList<Integer> maxChars) {
        int col, chars;
        String val;

        col = cell.getColumnIndex();
        if (col > maxChars.size() - 1)
            maxChars.add(0);
        val = cell.getStringCellValue();
        chars = !DataBaseUtil.isEmpty(val) ? val.length() : 0;
        maxChars.set(col, Math.max(chars, maxChars.get(col)));
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
            path = ReportUtil.createTempFile("qcchart", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close outout stream for qc chart report");
            }
        }
        return path;
    }

    /*
     * Exports the workbook to an Excel file
     */
    private Path export(XSSFWorkbook wb, String systemVariableDirectory) throws Exception {
        Path path;
        OutputStream out;

        out = null;
        try {
            path = ReportUtil.createTempFile("qcchart", ".xlsx", systemVariableDirectory);
            out = Files.newOutputStream(path);
            wb.write(out);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close outout stream for qc chart report");
            }
        }
        return path;
    }

    class ResultComparator implements Comparator<QcChartResultVO> {
        public int compare(QcChartResultVO v1, QcChartResultVO v2) {
            return v1.getAnalyteName().compareTo(v2.getAnalyteName());
        }
    }

    class ValueComparator implements Comparator<QcChartReportViewVO.Value> {
        public int compare(Value v1, Value v2) {
            return v1.getAnalyteName().compareTo(v2.getAnalyteName());
        }
    }
}