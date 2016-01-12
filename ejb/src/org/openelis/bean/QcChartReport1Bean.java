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

import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcChartReportViewVO;
import org.openelis.domain.QcChartReportViewVO.Value;
import org.openelis.domain.QcChartResultVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.meta.QcChartMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

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
    private DictionaryCacheBean                         dictionaryCache;

    @EJB
    private AnalyteParameterBean                        analyteParameter;
    
    @EJB
    private SystemVariableBean                          systemVariable;

    protected ArrayList<Integer>                        maxChars;
    protected ArrayList<DictionaryDO>                   qcColumns;
    protected ArrayList<String>                         worksheetHeaders, worksheetHeaderNames;
    protected CellStyle                                 baseStyle, headerStyle;
    protected Font                                      baseFont, headerFont;
    protected HashMap<String, HashMap<String, Integer>> worksheetColumnMap;

    private static final Logger                         log = Logger.getLogger("openelis");

    public QcChartReportViewVO fetchData(ArrayList<QueryData> paramList) throws Exception {
        ArrayList<QcChartResultVO> resultList;
        ArrayList<Value> qcList;
        Date startDate, endDate;
        DictionaryDO qcTypeDO;
        HashMap<String, QueryData> param;
        Integer plot, qc, number, location;
        QcChartReportViewVO data;
        String qcName;
        Value vo;

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
            qcTypeDO = dictionaryCache.getById(qc);
            qcColumns = categoryCache.getBySystemName(qcTypeDO.getSystemName()).getDictionaryList();

            worksheetColumnMap = new HashMap<String, HashMap<String, Integer>>();
            data = new QcChartReportViewVO();
            qcList = new ArrayList<Value>();

            Collections.sort(resultList, new ResultComparator());
            for (QcChartResultVO result : resultList) {
                vo = getCommonFields(result);
                vo = loadScreenValues(vo, qcTypeDO.getSystemName(), result.getWorksheetFormat());
                if (vo != null)
                    qcList.add(vo);
            }
        } finally {
            qcColumns = null;
            worksheetColumnMap = null;
        }

        data.setQcList(qcList);
        data.setPlotType(plot);
        data.setQcType(qc);
        data.setQcName(qcName);
        return data;
    }

    public ReportStatus runReport(QcChartReportViewVO dataPoints) throws Exception {
        ArrayList<Value> list;
        DictionaryDO qcTypeDO;
        FileInputStream in;
        HSSFWorkbook wb;
        Integer plotType;
        Path path;
        ReportStatus status;
        String qcName, qcType;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("qcChartReport", status);

        list = dataPoints.getQcList();
        if (list.size() > 1)
            Collections.sort(list, new ValueComparator());

        plotType = dataPoints.getPlotType();
        if (Constants.dictionary().CHART_TYPE_FIXED.equals(plotType))
            calculateStaticStatistics(dataPoints);
        
        qcName = dataPoints.getQcName();
        wb = null;
        try {
            status.setMessage("Initializing report");
            session.setAttribute("qcChartReport", status);

            qcTypeDO = dictionaryCache.getById(dataPoints.getQcType());
            qcType = qcTypeDO.getEntry();
            qcColumns = categoryCache.getBySystemName(qcTypeDO.getSystemName()).getDictionaryList();
            
            if (qcColumns != null && qcColumns.size() > 0) {
                in = new FileInputStream(getChartTemplateFileName(qcType));
                wb = new HSSFWorkbook(in);
            } else {
                wb = new HSSFWorkbook();
            }

            status.setMessage(Messages.get().report_outputReport()).setPercentComplete(20);
            session.setAttribute("qcChartReport", status);
            
            fillWorkbook(wb, list, qcName, qcType, qcTypeDO.getSystemName(), plotType, status);
            path = export(wb, "upload_stream_directory");

            status.setPercentComplete(100)
                  .setMessage(path.getFileName().toString())
                  .setPath(path.toString())
                  .setStatus(ReportStatus.Status.SAVED);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            qcColumns = null;
        }
        return status;
    }

    /*
     * Copies common fields to the data VO for plotting
     */
    protected Value getCommonFields(QcChartResultVO result) throws Exception {
        int i;
        Value vo;

        vo = new Value();
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
    protected Value loadScreenValues(Value value, String qcFormat, String worksheetFormat) throws Exception {
        if (qcColumns == null || qcColumns.size() == 0)
            return value;
        
        for (DictionaryDO dict : qcColumns) {
            if (!DataBaseUtil.isEmpty(dict.getCode())) {
                switch (dict.getCode()) {
                    case "Value1":
                        value.setValue1(getValue(qcFormat, worksheetFormat, dict.getSystemName(), value));
                        break;
                        
                    case "Value2":
                        value.setValue2(getValue(qcFormat, worksheetFormat, dict.getSystemName(), value));
                        break;
                        
                    case "PlotValue":
                        try {
                            value.setPlotValue(Double.parseDouble(getValue(qcFormat, worksheetFormat, dict.getSystemName(), value)));
                        } catch (Exception e) {
                            value.setPlotValue(null);
                            value.setIsPlot("N");
                        }
                        break;
                }
            }
        }

        return value;
    }

    protected String getValue(String qcFormat, String worksheetFormat, String columnName, Value data) throws Exception {
        HashMap<String, Integer> columnMap;
        Integer column;
        String value;
        
        value = null;
        
        columnMap = worksheetColumnMap.get(worksheetFormat);
        if (columnMap == null)
            columnMap = loadWorksheetFormat(worksheetFormat);
        
        if (qcFormat != null)
            column = columnMap.get(worksheetFormat + "_" + columnName.substring(qcFormat.length() + 1));
        else
            column = columnMap.get(columnName);
        
        if (column != null)
            value = data.getValueAt(column);
        
        return value;
    }

    protected HashMap<String, Integer> loadWorksheetFormat(String worksheetFormat) throws Exception {
        int i;
        ArrayList<DictionaryDO> list;
        CategoryCacheVO vo;
        DictionaryDO column;
        HashMap<String, Integer> columnMap;

        vo = categoryCache.getBySystemName(worksheetFormat);
        list = vo.getDictionaryList();
        columnMap = new HashMap<String, Integer>();
        for (i = 0; i < list.size(); i++) {
            column = list.get(i);
            columnMap.put(column.getSystemName(), i);
            if (worksheetHeaders != null && worksheetHeaderNames != null) {
                if (!worksheetHeaders.contains(column.getEntry())) {
                    worksheetHeaders.add(column.getEntry());
                    worksheetHeaderNames.add(column.getSystemName());
                }
            }
        }
        worksheetColumnMap.put(worksheetFormat, columnMap);

        return columnMap;
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

    private HSSFWorkbook fillWorkbook(HSSFWorkbook wb, ArrayList<Value> values,
                                      String qcName, String qcType, String qcFormat,
                                      Integer plotType, ReportStatus status) throws Exception {
        int rowIndex, sheetIndex, valueIndex;
        HSSFSheet sheet;
        Row row;
        String lastAnalyte, lastFormat;
        
        lastAnalyte = "___";
        lastFormat = "___";
        sheet = null;
        sheetIndex = 1;
        valueIndex = 0;

        try {
            baseFont = wb.createFont();
            baseFont.setFontName("Arial");
            baseFont.setFontHeightInPoints((short)8);
            baseStyle = wb.createCellStyle();
            baseStyle.setFont(baseFont);

            headerFont = wb.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontName("Arial");
            headerFont.setFontHeightInPoints((short)8);
            headerStyle = wb.createCellStyle();
            headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
            headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
            headerStyle.setFont(headerFont);

            maxChars = new ArrayList<Integer>();
            worksheetColumnMap = new HashMap<String, HashMap<String, Integer>>();
            worksheetHeaders = new ArrayList<String>();
            worksheetHeaderNames = new ArrayList<String>();

            if (qcColumns != null && !qcColumns.isEmpty())
                rowIndex = 32;
            else
                rowIndex = 3;

            for (Value value : values) {
                valueIndex++;
                
                if ("N".equals(value.getIsPlot()))
                    continue;
                
                if (!lastAnalyte.equals(value.getAnalyteName())) {
                    if (!"___".equals(lastAnalyte)) {
                        while (rowIndex < sheet.getLastRowNum()) {
                            sheet.removeRow(sheet.getRow(rowIndex));
                            rowIndex++;
                        }
                        finishSheet(sheet, wb, qcName, qcType, lastAnalyte);
                    }
                    sheet = wb.getSheet("Sheet"+(sheetIndex++));
                    if (sheet == null)
                        sheet = wb.createSheet();
                    lastAnalyte = value.getAnalyteName();
                    if (qcColumns != null && !qcColumns.isEmpty())
                        rowIndex = 32;
                    else
                        rowIndex = 3;
                    lastFormat = "___";
                    
                    if (Constants.dictionary().CHART_TYPE_FIXED.equals(plotType))
                        setStatisticCells(wb, sheet, value);
                }
                
                if (!lastFormat.equals(value.getWorksheetFormat())) {
                    lastFormat = value.getWorksheetFormat();
                    if (qcColumns == null || qcColumns.isEmpty())
                        loadWorksheetFormat(lastFormat);    
                }
                
                row = sheet.createRow(rowIndex++);
                setBaseCells(value, row);
                setResultCells(value, row, qcFormat, lastFormat);
    
                status.setPercentComplete(70 * (valueIndex / values.size()) + 20);
                session.setAttribute("qcChartReport", status);
            }
            
            finishSheet(sheet, wb, qcName, qcType, lastAnalyte);
            
            while (sheetIndex < wb.getNumberOfSheets())
                wb.removeSheetAt(sheetIndex);
        } finally {
            baseFont = null;
            baseStyle = null;
            headerFont = null;
            headerStyle = null;
            maxChars = null;
            worksheetColumnMap = null;
            worksheetHeaders = null;
            worksheetHeaderNames = null;
        }
        
        return wb;
    }
    
    private void finishSheet(HSSFSheet sheet, HSSFWorkbook wb, String qcName, String qcType,
                             String sheetName) {
        int i, columnIndex;
        ArrayList<DictionaryDO> tempQcColumns;
        DictionaryDO dict;
        HashSet<Integer> emptyColumns;
        Name rangeName;
        Row row;
        String rangeFormula;
        
        if (qcColumns != null && !qcColumns.isEmpty())
            row = sheet.getRow(32);
        else
            row = sheet.getRow(3);
        emptyColumns = new HashSet<Integer>();
        for (i = 0; i < row.getLastCellNum(); i++) {
            if (i >= maxChars.size() || maxChars.get(i) == 0)
                emptyColumns.add(i);
        }
        
        setHeaderCells(sheet, qcName, qcType, sheetName);

        if (qcColumns != null && !qcColumns.isEmpty()) {
            tempQcColumns = new ArrayList<DictionaryDO>();
            tempQcColumns.addAll(qcColumns);
            for (i = tempQcColumns.size() - 1; i > -1; i--) {
                if (emptyColumns.contains(i + 3)) {
                    tempQcColumns.remove(i);
                    removeColumn(sheet, i + 3);
                    maxChars.remove(i + 3);
                }
            }
            
            /*
             * Create named ranges for the graph to be able to locate the appropriate
             * data
             */
            columnIndex = 3;
            for (i = 0; i < tempQcColumns.size(); i++) {
                dict = tempQcColumns.get(i);
                if (!DataBaseUtil.isEmpty(dict.getCode())) {
                    rangeName = getName(wb, sheet, dict.getCode());
                    if (rangeName == null) {
                        rangeName = wb.createName();
                        rangeName.setSheetIndex(wb.getSheetIndex(sheet));
                        rangeName.setNameName(dict.getCode());
                    }
                    rangeFormula = rangeName.getRefersToFormula();
                    if (rangeFormula != null && rangeFormula.length() > 0 && !"$A$2".equals(rangeFormula.substring(rangeFormula.indexOf("!") + 1)))
                        rangeFormula += ",";
                    else
                        rangeFormula = "";
                    rangeFormula += sheet.getSheetName()+"!$" + CellReference.convertNumToColString(columnIndex) +
                                    "$33:" + "$" + CellReference.convertNumToColString(columnIndex) +
                                    "$" + (sheet.getLastRowNum() + 1);
                    rangeName.setRefersToFormula(rangeFormula);
                }
                columnIndex++;
            }
            /*
             * make each column wide enough to show the longest string in it; the
             * width for each column is set as the maximum number of characters in
             * that column multiplied by 256; this is because the default width of
             * one character is 1/256 units in Excel
             */
            for (i = 3; i < maxChars.size(); i++)
                sheet.setColumnWidth(i, maxChars.get(i) * 256);
        } else if (worksheetHeaders != null && worksheetHeaders.size() > 0) {
            /*
             * make each column wide enough to show the longest string in it; the
             * width for each column is set as the maximum number of characters in
             * that column multiplied by 256; this is because the default width of
             * one character is 1/256 units in Excel
             */
            for (i = 0; i < maxChars.size(); i++)
                sheet.setColumnWidth(i, maxChars.get(i) * 256);
        }
        
        wb.setSheetName(wb.getSheetIndex(sheet), sheetName);
        sheet.setForceFormulaRecalculation(true);
        maxChars.clear();
    }
    
    private void setStatisticCells(HSSFWorkbook wb, HSSFSheet sheet, Value value) {
        Cell cell;
        
        cell = getCellForName(sheet, getName(wb, sheet, "Mean"));
        if (cell != null) {
            cell.setCellFormula(null);
            setCellValue(cell, DataBaseUtil.toString(value.getMean()));
            setMaxChars(cell, maxChars);
        }
        
        cell = getCellForName(sheet, getName(wb, sheet, "UCL"));
        if (cell != null) {
            cell.setCellFormula(null);
            setCellValue(cell, DataBaseUtil.toString(value.getUCL()));
            setMaxChars(cell, maxChars);
        }
        
        cell = getCellForName(sheet, getName(wb, sheet, "LCL"));
        if (cell != null) {
            cell.setCellFormula(null);
            setCellValue(cell, DataBaseUtil.toString(value.getLCL()));
            setMaxChars(cell, maxChars);
        }
    }
    
    private void setHeaderCells(HSSFSheet sheet, String qcName, String qcType, String analyteName) {
        int i, startRow;
        Cell cell;
        Row row;

        if (qcColumns != null && qcColumns.size() > 0)
            startRow = 29;
        else
            startRow = 0;
        
        row = sheet.createRow(startRow);

        cell = row.createCell(0);
        cell.setCellStyle(headerStyle);
        setCellValue(cell, "QC Name");
        setMaxChars(cell, maxChars);

        cell = row.createCell(1);
        cell.setCellStyle(baseStyle);
        setCellValue(cell, qcName);
        setMaxChars(cell, maxChars);
        
        cell = row.createCell(2);
        cell.setCellStyle(baseStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 1, 2));

        row = sheet.createRow(startRow + 1);

        cell = row.createCell(0);
        cell.setCellStyle(headerStyle);
        setCellValue(cell, "QC Type: Analyte");
        setMaxChars(cell, maxChars);

        cell = row.createCell(1);
        cell.setCellStyle(baseStyle);
        setCellValue(cell, qcType + ": " + analyteName);
        setMaxChars(cell, maxChars);

        cell = row.createCell(2);
        cell.setCellStyle(baseStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow + 1, startRow + 1, 1, 2));

        row = sheet.createRow(startRow + 2);

        cell = row.createCell(0);
        cell.setCellStyle(headerStyle);
        setCellValue(cell, "Accession # / Worksheet #");
        setMaxChars(cell, maxChars);
        
        cell = row.createCell(1);
        cell.setCellStyle(headerStyle);
        setCellValue(cell, "Lot #");
        setMaxChars(cell, maxChars);
        
        cell = row.createCell(2);
        cell.setCellStyle(headerStyle);
        setCellValue(cell, "Created Date");
        setMaxChars(cell, maxChars);

        if (qcColumns != null && !qcColumns.isEmpty()) {
            for (i = 0; i < qcColumns.size(); i++) {
                cell = row.createCell(i + 3);
                cell.setCellStyle(headerStyle);
                setCellValue(cell, qcColumns.get(i).getEntry());
                setMaxChars(cell, maxChars);
            }
        } else if (worksheetHeaders != null && !worksheetHeaders.isEmpty()) {
            for (i = 0; i < worksheetHeaders.size(); i++) {
                cell = row.createCell(i + 3);
                cell.setCellStyle(headerStyle);
                setCellValue(cell, worksheetHeaders.get(i));
                setMaxChars(cell, maxChars);
            }
        }
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
    private void setBaseCells(Value value, Row row) throws Exception {
        Cell cell;
        
        cell = row.createCell(0);
        cell.setCellStyle(baseStyle);
        setCellValue(cell, value.getAccessionNumber() + " / " + value.getWId());
        setMaxChars(cell, maxChars);
        
        cell = row.createCell(1);
        cell.setCellStyle(baseStyle);
        setCellValue(cell, value.getLotNumber());
        setMaxChars(cell, maxChars);

        cell = row.createCell(2);
        cell.setCellStyle(baseStyle);
        setCellValue(cell, getDateTimeLabel(value.getWorksheetCreatedDate(), Messages.get().gen_dateTimePattern()));
        setMaxChars(cell, maxChars);
    }

    private void setResultCells(Value value, Row row, String qcFormat, String worksheetFormat) throws Exception {
        int i;
        Cell cell;
        DictionaryDO column;
        
        if (qcColumns != null && !qcColumns.isEmpty()) {
            for (i = 0; i < qcColumns.size(); i++) {
                column = qcColumns.get(i);
                cell = row.createCell(i + 3);
                cell.setCellStyle(baseStyle);
                setCellValue(cell, getValue(qcFormat, worksheetFormat, column.getSystemName(), value));
                setMaxChars(cell, maxChars);
            }
        } else if (worksheetHeaders != null && !worksheetHeaders.isEmpty()) {
            for (i = 0; i < worksheetHeaders.size(); i++) {
                cell = row.createCell(i + 3);
                cell.setCellStyle(baseStyle);
                setCellValue(cell, getValue(null, worksheetFormat, worksheetHeaderNames.get(i), value));
                setMaxChars(cell, maxChars);
            }
        }
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
    private void setCellValue(Cell cell, String value) {
        try {
            cell.setCellValue(Double.parseDouble(value));
        } catch (Exception ignE) {
            if (value != null && value.length() > 255)
                value = value.substring(0, 255);
            cell.setCellValue(value);
        }
    }

    /*
     * Exports the workbook to an Excel file
     */
    private Path export(HSSFWorkbook wb, String systemVariableDirectory) throws Exception {
        Path path;
        OutputStream out;

        out = null;
        try {
            path = ReportUtil.createTempFile("qcchart", ".xls", systemVariableDirectory);
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

    private String getChartTemplateFileName(String qcType) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String dirName;

        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("qc_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: " +
                                anyE.getMessage());
        }

        return dirName + "QcChart" + qcType.replaceAll(" ", "") + ".xls";
    }
    
    private HSSFName getName(HSSFWorkbook wb, HSSFSheet sheet, String nameString) {
        int i;
        HSSFName name;
        
        for (i = 0; i < wb.getNumberOfNames(); i++) {
            name = wb.getNameAt(i);
            if (name.getNameName().equals(nameString) && name.getSheetName().equals(sheet.getSheetName()))
                return name;
        }
        
        return null;
    }

    private Cell getCellForName(HSSFSheet sheet, HSSFName name) {
        AreaReference aref;
        Cell cell;
        CellReference cref[];

        cell = null;
        if (name != null && !name.isDeleted()) {
            aref = new AreaReference(name.getRefersToFormula());
            cref = aref.getAllReferencedCells();
            cell = sheet.getRow(cref[0].getRow()).getCell((int)cref[0].getCol());
        }

        return cell;
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
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell))
                val = ReportUtil.toString(cell.getDateCellValue(), Messages.get().dateTimePattern());
            else
                val = Double.toString(cell.getNumericCellValue());
        } else {
            val = cell.getStringCellValue();
        }
        chars = !DataBaseUtil.isEmpty(val) ? val.length() : 0;
        maxChars.set(col, Math.max(chars, maxChars.get(col)));
    }
    
    private void removeColumn(HSSFSheet sheet, Integer columnIndex) {
        int i, j;
        Cell cell;
        Row row;
        
        for (i = 31; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            cell = row.getCell(columnIndex);
            if (cell != null)
                row.removeCell(row.getCell(columnIndex));
            for (j = columnIndex + 1; j < row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell != null)
                    ((HSSFRow)row).moveCell((HSSFCell)cell, (short)(j - 1));
            }
        }
    }

    class ResultComparator implements Comparator<QcChartResultVO> {
        public int compare(QcChartResultVO v1, QcChartResultVO v2) {
            return v1.getAnalyteName().compareTo(v2.getAnalyteName());
        }
    }

    class ValueComparator implements Comparator<Value> {
        public int compare(Value v1, Value v2) {
            return v1.getAnalyteName().compareTo(v2.getAnalyteName());
        }
    }
}