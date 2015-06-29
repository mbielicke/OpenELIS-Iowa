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

import static org.openelis.manager.WorksheetManager1Accessor.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class WorksheetExcelHelperBean {
    @EJB
    AnalyteParameterBean               analyteParameter;
    @EJB
    DictionaryCacheBean                dictionaryCache;
    @EJB
    CategoryCacheBean                  categoryCache;
    @EJB
    SessionCacheBean                   session;
    @EJB
    SystemVariableBean                 systemVariable;
    @EJB
    WorksheetManager1Bean              worksheetManager;
    @EJB
    UserCacheBean                      userCache;

    private static final Logger        log = Logger.getLogger("openelis");

    private HashMap<Integer, String>   statusIdNameMap;
    private HashMap<String, CellStyle> styles;
    private HashMap<String, Integer>   statusNameIdMap;
    private String                     statuses[];
                    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public WorksheetManager1 exportToExcel(WorksheetManager1 manager) throws Exception {
        boolean isEditable;
        int r, i, a, o, aCount, aTotal;
        ArrayList<WorksheetAnalysisViewDO> waList;
        ArrayList<WorksheetResultViewDO> wrList;
        ArrayList<WorksheetQcResultViewDO> wqrList;
        String cellNameIndex, posNum, outFileName;
        File outFile;
        FileInputStream in;
        FileOutputStream out;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> waMap;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrMap;
        HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> wqrMap;
        HashMap<Integer, WorksheetAnalysisViewDO> waLinkMap;
        HashMap<String, HashMap<Integer, ArrayList<AnalyteParameterViewDO>>> apMap;
        HashMap<String, String> tCellNames;
        Cell cell;
        CellRangeAddressList /*statusCells, */reportableColumn;
        DVConstraint /*statusConstraint, */reportableConstraint;
        HSSFDataValidation /*statusValidation, */reportableValidation;
        HSSFSheet resultSheet, overrideSheet;
        HSSFWorkbook wb;
        Name cellName;
        Row row, oRow, tRow;
        DictionaryDO formatDO;
        ReportStatus status;
        SimpleDateFormat dateTimeFormat;
        WorksheetAnalysisDO waLinkDO;


        status = new ReportStatus();
        status.setMessage("Exporting to Excel: Initializing");
        status.setPercentComplete(0);
        session.setAttribute("ExportToExcelStatus", status);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        outFileName = getWorksheetOutputFileName(manager.getWorksheet().getId(),
                                                 manager.getWorksheet().getSystemUserId());
        outFile = new File(outFileName);
        if (outFile.exists())
            throw new Exception("An Excel file for this worksheet already exists, please delete it before trying to export");

        try {
            formatDO = dictionaryCache.getById(manager.getWorksheet().getFormatId());
        } catch (NotFoundException nfE) {
            formatDO = new DictionaryDO();
            formatDO.setEntry("DefaultTotal");
            formatDO.setSystemName("wf_total");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: " + anyE.getMessage());
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatDO));
            wb = new HSSFWorkbook(in, true);
            createStyles(wb);
        } catch (FileNotFoundException fnfE) {
            try {
                wb = buildTemplate(formatDO);
                createStyles(wb);
            } catch (FileNotFoundException fnfE2) {
                throw new Exception("Error loading template file: " + fnfE2.getMessage());
            } catch (IOException ioE) {
                throw new Exception("Error loading workbook from template file: " +
                                    ioE.getMessage());
            }
        }

        loadStatuses();
//        statusCells = new CellRangeAddressList();

        tCellNames = loadNamesByCellReference(wb);

        resultSheet = wb.getSheet("Worksheet");

        tRow = resultSheet.getRow(1);
        resultSheet.removeRow(tRow);

        overrideSheet = wb.getSheet("Overrides");

        status.setPercentComplete(5);
        session.setAttribute("ExportToExcelStatus", status);

        r = 1;
        o = 1;
        i = 0;
        aCount = 0;
        apMap = new HashMap<String, HashMap<Integer, ArrayList<AnalyteParameterViewDO>>>();
        waMap = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
        waLinkMap = new HashMap<Integer, WorksheetAnalysisViewDO>();
        wrMap = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        wqrMap = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
        loadMaps(manager, waMap, waLinkMap, wrMap, wqrMap);
        aTotal = getAnalyses(manager).size();
        if (getItems(manager) != null) {
            for (WorksheetItemDO wiDO : getItems(manager)) {
                a = 0;
                waList = waMap.get(wiDO.getId());
                if (waList != null && waList.size() > 0) {
                    for (WorksheetAnalysisViewDO waVDO : waList) {
                        aCount++;
                        status.setMessage("Exporting to Excel: Analysis "+aCount+" of "+aTotal);
                        status.setPercentComplete((int)(((double) (aCount - 1) / aTotal) * 90) + 5);
                        session.setAttribute("ExportToExcelStatus", status);

                        waLinkDO = waLinkMap.get(waVDO.getWorksheetAnalysisId());

                        row = resultSheet.createRow(r);
        
                        // position number
                        posNum = wiDO.getPosition().toString();
                        cell = row.createCell(0);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        if (a == 0)
                            cell.setCellValue(posNum);
        
                        // accession number
                        cell = row.createCell(1);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        cell.setCellValue(waVDO.getAccessionNumber());
        
                        cellNameIndex = i + "." + a;
                        if (waVDO.getAnalysisId() != null) {
                            isEditable = (waVDO.getFromOtherId() == null &&
                                          !Constants.dictionary().ANALYSIS_INPREP.equals(waVDO.getStatusId()) &&
                                          !Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) &&
                                          !Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()));
        
                            // description
                            cell = row.createCell(2);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getDescription());
        
                            // qc link
                            cell = row.createCell(3);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            if (waLinkDO != null)
                                cell.setCellValue(waLinkDO.getAccessionNumber());
                            else
                                cell.setCellValue("");
        
                            // test name
                            cell = row.createCell(4);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getTestName());
        
                            // method name
                            cell = row.createCell(5);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getMethodName());
        
                            // analysis status
                            cell = row.createCell(6);
//                            if (isEditable)
//                                cell.setCellStyle(styles.get("row_edit"));
//                            else
                                cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(statusIdNameMap.get(waVDO.getStatusId()));
                            cellName = wb.createName();
                            cellName.setNameName("analysis_status." + i + "." + a);
                            cellName.setRefersToFormula("Worksheet!$" +
                                                        CellReference.convertNumToColString(6) +
                                                        "$" + (row.getRowNum() + 1));
//                            statusCells.addCellRangeAddress(r, 6, r, 6);
        
                            wrList = wrMap.get(waVDO.getId());
                            if (wrList == null || wrList.size() == 0) {
                                // analyte
                                cell = row.createCell(7);
                                cell.setCellStyle(styles.get("row_no_edit"));
                                cell.setCellValue("NO ANALYTES DEFINED");
        
                                // reportable
                                cell = row.createCell(8);
                                cell.setCellStyle(styles.get("row_no_edit"));
                                cell.setCellValue("N");
        
                                createEmptyCellsForFormat(row, tRow);
                                r++;
                            } else {
                                r = createResultCellsForFormat(resultSheet,
                                                               row,
                                                               tRow,
                                                               cellNameIndex,
                                                               tCellNames,
                                                               manager.getWorksheet(),
                                                               waVDO,
                                                               wrList,
                                                               isEditable,
                                                               apMap);
                            }
        
                            //
                            // Add override row to override sheet
                            //
                            oRow = overrideSheet.createRow(o);
        
                            // position number
                            cell = oRow.createCell(0);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            if (a == 0)
                                cell.setCellValue(posNum);
        
                            // accession number
                            cell = oRow.createCell(1);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getAccessionNumber());
        
                            // description (override)
                            cell = oRow.createCell(2);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getDescription());
        
                            // test name (overrride)
                            cell = oRow.createCell(3);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getTestName());
        
                            // method name (override)
                            cell = oRow.createCell(4);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getMethodName());
        
                            // users (override)
                            cell = oRow.createCell(5);
                            if (isEditable)
                                cell.setCellStyle(styles.get("row_edit"));
                            else
                                cell.setCellStyle(styles.get("row_no_edit"));
                            if (waVDO.getSystemUsers() != null)
                                cell.setCellValue(waVDO.getSystemUsers());
                            cellName = wb.createName();
                            cellName.setNameName("analysis_users." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(5) +
                                                        "$" + (oRow.getRowNum() + 1));
        
                            // started (override)
                            cell = oRow.createCell(6);
                            if (isEditable)
                                cell.setCellStyle(styles.get("datetime_edit"));
                            else
                                cell.setCellStyle(styles.get("datetime_no_edit"));
                            if (waVDO.getStartedDate() != null)
                                cell.setCellValue(dateTimeFormat.format(waVDO.getStartedDate().getDate()));
                            cellName = wb.createName();
                            cellName.setNameName("analysis_started." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(6) +
                                                        "$" + (oRow.getRowNum() + 1));
        
                            // completed (override)
                            cell = oRow.createCell(7);
                            if (isEditable)
                                cell.setCellStyle(styles.get("datetime_edit"));
                            else
                                cell.setCellStyle(styles.get("datetime_no_edit"));
                            if (waVDO.getCompletedDate() != null)
                                cell.setCellValue(dateTimeFormat.format(waVDO.getCompletedDate().getDate()));
                            cellName = wb.createName();
                            cellName.setNameName("analysis_completed." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(7) +
                                                        "$" + (oRow.getRowNum() + 1));
                            o++ ;
                        } else if (waVDO.getQcLotId() != null) {
                            // description
                            cell = row.createCell(2);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getDescription());
        
                            // qc link
                            cell = row.createCell(3);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            if (waLinkDO != null)
                                cell.setCellValue(waLinkDO.getAccessionNumber());
                            else
                                cell.setCellValue("");
        
                            // test name
                            cell = row.createCell(4);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue("");
        
                            // method name
                            cell = row.createCell(5);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue("");
        
                            // analysis status
                            cell = row.createCell(6);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue("");
        
                            wqrList = wqrMap.get(waVDO.getId());
                            if (wqrList == null || wqrList.size() == 0) {
                                // analyte
                                cell = row.createCell(7);
                                cell.setCellStyle(styles.get("row_no_edit"));
                                cell.setCellValue("NO ANALYTES DEFINED");
        
                                // reportable
                                cell = row.createCell(8);
                                cell.setCellStyle(styles.get("row_no_edit"));
                                cell.setCellValue("N");
        
                                createEmptyCellsForFormat(row, tRow);
        
                                r++;
                            } else {
                                cellNameIndex = i + "." + a;
                                r = createQcResultCellsForFormat(resultSheet,
                                                                 row,
                                                                 tRow,
                                                                 cellNameIndex,
                                                                 tCellNames,
                                                                 manager.getWorksheet(),
                                                                 waVDO.getQcId(),
                                                                 wqrList,
                                                                 apMap);
                            }
        
                            //
                            // Add override row to override sheet
                            //
                            oRow = overrideSheet.createRow(o);
        
                            // position number
                            cell = oRow.createCell(0);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            if (a == 0)
                                cell.setCellValue(posNum);
        
                            // accession number
                            cell = oRow.createCell(1);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getAccessionNumber());
        
                            // description (override)
                            cell = oRow.createCell(2);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue(waVDO.getDescription());
        
                            // test name (overrride)
                            cell = oRow.createCell(3);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue("");
        
                            // method name (override)
                            cell = oRow.createCell(4);
                            cell.setCellStyle(styles.get("row_no_edit"));
                            cell.setCellValue("");
        
                            // users (override)
                            cell = oRow.createCell(5);
                            cell.setCellStyle(styles.get("row_edit"));
                            if (waVDO.getSystemUsers() != null)
                                cell.setCellValue(waVDO.getSystemUsers());
                            cellName = wb.createName();
                            cellName.setNameName("analysis_users." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(5) +
                                                        "$" + (oRow.getRowNum() + 1));
        
                            // started (override)
                            cell = oRow.createCell(6);
                            cell.setCellStyle(styles.get("datetime_edit"));
                            if (waVDO.getStartedDate() != null)
                                cell.setCellValue(dateTimeFormat.format(waVDO.getStartedDate().getDate()));
                            cellName = wb.createName();
                            cellName.setNameName("analysis_started." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(6) +
                                                        "$" + (oRow.getRowNum() + 1));
        
                            // completed (override)
                            cell = oRow.createCell(7);
                            cell.setCellStyle(styles.get("datetime_no_edit"));
                            cellName = wb.createName();
                            cellName.setNameName("analysis_completed." + cellNameIndex);
                            cellName.setRefersToFormula("Overrides!$" +
                                                        CellReference.convertNumToColString(7) +
                                                        "$" + (oRow.getRowNum() + 1));
                            o++;
                        }
                        
                        a++;
                    }
                }
                
                i++;
            }
        }

        status.setMessage("Exporting to Excel: Finalizing");
        status.setPercentComplete(95);
        session.setAttribute("ExportToExcelStatus", status);

        //
        // Create validators
        //
//        statusConstraint = DVConstraint.createExplicitListConstraint(statuses);
//        statusValidation = new HSSFDataValidation(statusCells, statusConstraint);
//        statusValidation.setEmptyCellAllowed(true);
//        statusValidation.setSuppressDropDownArrow(false);
//        statusValidation.createPromptBox("Statuses", formatTooltip(statuses));
//        statusValidation.setShowPromptBox(false);
//        resultSheet.addValidationData(statusValidation);

        reportableColumn = new CellRangeAddressList(1,
                                                    resultSheet.getPhysicalNumberOfRows() - 1,
                                                    8,
                                                    8);
        reportableConstraint = DVConstraint.createExplicitListConstraint(new String[] {"Y", "N"});
        reportableValidation = new HSSFDataValidation(reportableColumn,
                                                      reportableConstraint);
        reportableValidation.setSuppressDropDownArrow(false);
        resultSheet.addValidationData(reportableValidation);

        //
        // Auto resize columns on result sheet and override sheet
        //
        resultSheet.autoSizeColumn(2, true);            // Description
        resultSheet.autoSizeColumn(4, true);            // Test
        resultSheet.autoSizeColumn(5, true);            // Method
        resultSheet.autoSizeColumn(7, true);            // Analyte

        overrideSheet.autoSizeColumn(2, true);          // Description
        overrideSheet.autoSizeColumn(3, true);          // Test
        overrideSheet.autoSizeColumn(4, true);          // Method

        try {
            out = new FileOutputStream(outFileName);
            wb.write(out);
            out.close();
            Runtime.getRuntime().exec("chmod go+rw " + outFileName);
        } catch (Exception anyE) {
            throw new Exception("Error writing Excel file: " + anyE.getMessage());
        }

        status.setMessage("Exporting to Excel: Done");
        status.setPercentComplete(100);
        session.setAttribute("ExportToExcelStatus", status);

        return manager;
    }

    @TransactionTimeout(600)
    public WorksheetManager1 importFromExcel(WorksheetManager1 manager) throws Exception {
        boolean editLocked;
        int a, i, c, r, rowIndex;
        ArrayList<String> userNames;
        ArrayList<SystemUserVO> validUserVOs;
        ArrayList<WorksheetAnalysisViewDO> waList;
        ArrayList<WorksheetResultViewDO> wrList;
        ArrayList<WorksheetQcResultViewDO> wqrList;
        File file;
        FileInputStream in;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> waMap;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrMap;
        HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> wqrMap;
        HashMap<Integer, WorksheetAnalysisViewDO> waLinkMap;
        HSSFWorkbook wb;
        Object value;
        SectionPermission perm;
        SimpleDateFormat format;
        String validUsers;
        ValidationErrorsList errorList;

        manager = worksheetManager.fetchForUpdate(manager.getWorksheet().getId());
        
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        errorList = new ValidationErrorsList();

        try {
            file = new File(getWorksheetOutputFileName(manager.getWorksheet().getId(),
                                                       manager.getWorksheet()
                                                              .getSystemUserId()));
            in = new FileInputStream(file);
            wb = new HSSFWorkbook(in);
        } catch (Exception anyE) {
            worksheetManager.unlock(manager.getWorksheet().getId());
            throw anyE;
        }
        
        loadStatuses();

        i = 0;
        r = 0;
        rowIndex = 1;
        waMap = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
        waLinkMap = new HashMap<Integer, WorksheetAnalysisViewDO>();
        wrMap = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        wqrMap = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
        loadMaps(manager, waMap, waLinkMap, wrMap, wqrMap);
        if (getItems(manager) != null) {
            for (WorksheetItemDO wiDO : getItems(manager)) {
                a = 0;
                waList = waMap.get(wiDO.getId());
                if (waList != null && waList.size() > 0) {
                    for (WorksheetAnalysisViewDO waVDO : waList) {
                        //
                        // increment rowIndex if there were no result records for the
                        // previous analysis
                        if (r == 0 && a != 0)
                            rowIndex++;
        
                        if (waVDO.getAnalysisId() != null) {
                            perm = userCache.getPermission().getSection(waVDO.getSectionName());
                            editLocked = (waVDO.getFromOtherId() != null ||
                                          Constants.dictionary().ANALYSIS_INPREP.equals(waVDO.getStatusId()) ||
                                          Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                                          Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()) ||
                                          perm == null || !perm.hasCompletePermission());
        
                            wrList = wrMap.get(waVDO.getId());
                            if (!editLocked) {
                                value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                               "analysis_users." + i + "." + a);
                                validUsers = "";
                                if (value != null) {
                                    userNames = new ArrayList<String>(Arrays.asList(((String)value).split(",")));
                                    try {
                                        validUserVOs = userCache.validateSystemUsers(userNames);
                                        for (SystemUserVO userVO : validUserVOs) {
                                            if (userNames.contains(userVO.getLoginName())) {
                                                if (validUsers.length() > 0)
                                                    validUsers += ",";
                                                validUsers += userVO.getLoginName();
                                                userNames.remove(userVO.getLoginName());
                                            }
                                        }
                                        if (userNames.size() > 0)
                                            errorList.add(new FormErrorException(Messages.get().worksheet_illegalWorksheetUserFormException(
                                                                                 userNames.toString(),
                                                                                 String.valueOf(wiDO.getPosition()),
                                                                                 String.valueOf(a + 1))));
                                    } catch (Exception anyE) {
                                        errorList.add(new FormErrorException(Messages.get().worksheet_illegalWorksheetUserFormException(
                                                                             userNames.toString(),
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             String.valueOf(a + 1))));
                                    }
                                }
                                waVDO.setSystemUsers(validUsers);
        
                                value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                               "analysis_started." + i + "." + a);
                                if (value != null) {
                                    if (value instanceof Datetime) {
                                        waVDO.setStartedDate((Datetime)value);
                                    } else if (value instanceof String) {
                                        try {
                                            waVDO.setStartedDate(new Datetime(Datetime.YEAR,
                                                                              Datetime.MINUTE,
                                                                              format.parse((String)value)));
                                        } catch (ParseException parE) {
                                            errorList.add(new FormErrorException(Messages.get().worksheet_unparseableStartedDate(
                                                                                 String.valueOf(wiDO.getPosition()),
                                                                                 String.valueOf(a + 1))));
                                        }
                                    }
                                } else {
                                    waVDO.setStartedDate(null);
                                }
        
                                value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                               "analysis_completed." + i +
                                                               "." + a);
                                if (value != null) {
                                    if (value instanceof Datetime) {
                                        waVDO.setCompletedDate((Datetime)value);
                                    } else if (value instanceof String) {
                                        try {
                                            waVDO.setCompletedDate(new Datetime(Datetime.YEAR,
                                                                               Datetime.MINUTE,
                                                                               format.parse((String)value)));
                                        } catch (ParseException anyE) {
                                            errorList.add(new FormErrorException(Messages.get().worksheet_unparseableCompletedDate(
                                                                                 String.valueOf(wiDO.getPosition()),
                                                                                 String.valueOf(a + 1))));
                                        }
                                    }
                                } else {
                                    waVDO.setCompletedDate(null);
                                }

                                r = 0;
                                if (wrList != null && wrList.size() > 0) {
                                    for (WorksheetResultViewDO wrVDO : wrList) {
                                        for (c = 0; c < 30; c++ ) {
                                            value = getValueFromCellByCoords(wb.getSheet("Worksheet"),
                                                                             rowIndex,
                                                                             9 + c);
                                            if (value != null && !value.equals(wrVDO.getValueAt(c)))
                                                wrVDO.setValueAt(c, value.toString());
                                            else if (value == null && wrVDO.getValueAt(c) != null)
                                                wrVDO.setValueAt(c, null);
                                        }
            
                                        r++;
                                        rowIndex++;
                                    }
                                }
                            } else {
                                //
                                // increment rowIndex and r since we skipped running
                                // through the result records due to permissions or
                                // status
                                //
                                r = wrList.size();
                                rowIndex += r;
                            }
                        } else if (waVDO.getQcLotId() != null) {
                            wqrList = wqrMap.get(waVDO.getId());
                            r = 0;
                            if (wqrList != null && wqrList.size() > 0) {
                                for (WorksheetQcResultViewDO wqrVDO : wqrList) {
                                    for (c = 0; c < 30; c++) {
                                        value = getValueFromCellByCoords(wb.getSheet("Worksheet"),
                                                                         rowIndex, 9 + c);
                                        if (value != null && !value.equals(wqrVDO.getValueAt(c)))
                                            wqrVDO.setValueAt(c, value.toString());
                                        else if (value == null && wqrVDO.getValueAt(c) != null)
                                            wqrVDO.setValueAt(c, null);
                                    }
                                    
                                    r++;
                                    rowIndex++;
                                }
                            }
        
                            value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                           "analysis_users." + i + "." +
                                                           a);
                            validUsers = "";
                            if (value != null) {
                                userNames = new ArrayList<String>(Arrays.asList(((String)value).split(",")));
                                try {
                                    validUserVOs = userCache.validateSystemUsers(userNames);
                                    for (SystemUserVO userVO : validUserVOs) {
                                        if (userNames.contains(userVO.getLoginName())) {
                                            if (validUsers.length() > 0)
                                                validUsers += ",";
                                            validUsers += userVO.getLoginName();
                                            userNames.remove(userVO.getLoginName());
                                        }
                                    }
                                    if (userNames.size() > 0)
                                        errorList.add(new FormErrorException(Messages.get().worksheet_illegalWorksheetUserFormException(
                                                                             userNames.toString(),
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             String.valueOf(a + 1))));
                                } catch (Exception anyE) {
                                    errorList.add(new FormErrorException(Messages.get().worksheet_illegalWorksheetUserFormException(
                                                                         userNames.toString(),
                                                                         String.valueOf(wiDO.getPosition()),
                                                                         String.valueOf(a + 1))));
                                }
                            }
                            waVDO.setSystemUsers(validUsers);

// TODO: Need to move the following code to commit.
//                            } else if (waVDO.getSystemUsers() == null) {
//                                try {
//                                    userVO = userCache.getSystemUser();
//                                    waVDO.setSystemUsers(userVO.getLoginName());
//                                } catch (Exception anyE) {
//                                    errorList.add(new FormErrorException(Messages.get().defaultWorksheetQcUserFormException(
//                                                                         String.valueOf(wiDO.getPosition()),
//                                                                         String.valueOf(a + 1))));
//                                }
//                            }
        
                            value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                           "analysis_started." + i + "." + a);
                            if (value != null) {
                                if (value instanceof Datetime) {
                                    waVDO.setStartedDate((Datetime)value);
                                } else if (value instanceof String) {
                                    try {
                                        waVDO.setStartedDate(new Datetime(Datetime.YEAR,
                                                                          Datetime.MINUTE,
                                                                          format.parse((String)value)));
                                    } catch (ParseException anyE) {
                                        errorList.add(new FormErrorException(Messages.get().worksheet_unparseableStartedDate(
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             String.valueOf(a + 1))));
                                    }
                                }
                            } else {
                                waVDO.setStartedDate(null);
                            }
                            
                            value = getValueFromCellByName(wb.getSheet("Overrides"),
                                                           "analysis_completed." + i + "." + a);
                            if (value != null) {
                                if (value instanceof Datetime) {
                                    waVDO.setCompletedDate((Datetime)value);
                                } else if (value instanceof String) {
                                    try {
                                        waVDO.setCompletedDate(new Datetime(Datetime.YEAR,
                                                                            Datetime.MINUTE,
                                                                            format.parse((String)value)));
                                    } catch (ParseException anyE) {
                                        errorList.add(new FormErrorException(Messages.get().worksheet_unparseableCompletedDate(
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             String.valueOf(a + 1))));
                                    }
                                }
                            } else {
                                waVDO.setCompletedDate(null);
                            }
                        }
                    }
                    
                    a++;
                }
                //
                // increment rowIndex if there were no result records for the
                // last analysis or there were no analyses for this item
                if (r == 0)
                    rowIndex++;
                
                i++;
            }
        }

        if (errorList.getErrorList().size() > 0) {
            try {
                worksheetManager.unlock(manager.getWorksheet().getId());
            } catch (Exception anyE) {
                errorList.add(anyE);
            }
            throw errorList;
        } else {
            manager = worksheetManager.update(manager, null);
        }

        file.delete();

        return manager;
    }
    
    private HSSFWorkbook buildTemplate(DictionaryDO formatDO) throws Exception {
        int c;
        ArrayList<DictionaryDO> formatColumns;
        Cell hCell, tCell;
        FileInputStream in;
        HSSFSheet resultSheet;
        HSSFWorkbook wb;
        Name cellName;
        Row hRow, tRow;
        
        in = new FileInputStream(getWorksheetTemplateFileName("Base"));
        wb = new HSSFWorkbook(in, true);
        createStyles(wb);
        
        try {
            formatColumns = categoryCache.getBySystemName(formatDO.getSystemName())
                                              .getDictionaryList();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: " + anyE.getMessage());
        }
        
        c = 9;
        resultSheet = wb.getSheet("Worksheet");
        hRow = resultSheet.getRow(0);
        tRow = resultSheet.getRow(1);
        for (DictionaryDO columnDO : formatColumns) {
            hCell = hRow.createCell(c);
            hCell.setCellStyle(styles.get("header"));
            hCell.setCellValue(columnDO.getEntry());

            tCell = tRow.createCell(c);
            tCell.setCellStyle(styles.get("row_edit"));
            
            cellName = wb.createName();
            cellName.setNameName(columnDO.getSystemName().substring(formatDO.getSystemName().length()));
            cellName.setRefersToFormula("Worksheet!$" +
                                        CellReference.convertNumToColString(c) +
                                        "$" + (tRow.getRowNum() + 1));
            
            resultSheet.autoSizeColumn(c, true);
            c++;
        }
        
        return wb;
    }

    private void createStyles(HSSFWorkbook wb) {
        CellStyle dateTimeEditStyle, dateTimeNoEditStyle, headerStyle, rowEditStyle,
                  rowNoEditStyle;
        CreationHelper helper;
        Font font;

        helper = wb.getCreationHelper();
        styles = new HashMap<String, CellStyle>();

        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        headerStyle.setFont(font);
        headerStyle.setLocked(true);
        styles.put("header", headerStyle);

        rowEditStyle = wb.createCellStyle();
        rowEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowEditStyle.setLocked(false);
        styles.put("row_edit", rowEditStyle);

        rowNoEditStyle = wb.createCellStyle();
        rowNoEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowNoEditStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        rowNoEditStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        rowNoEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowNoEditStyle.setLocked(true);
        styles.put("row_no_edit", rowNoEditStyle);

        dateTimeEditStyle = wb.createCellStyle();
        dateTimeEditStyle.setDataFormat(helper.createDataFormat()
                                              .getFormat("yyyy-MM-dd hh:mm"));
        dateTimeEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dateTimeEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        dateTimeEditStyle.setLocked(false);
        styles.put("datetime_edit", dateTimeEditStyle);

        dateTimeNoEditStyle = wb.createCellStyle();
        dateTimeNoEditStyle.setDataFormat(helper.createDataFormat()
                                                .getFormat("yyyy-MM-dd hh:mm"));
        dateTimeNoEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dateTimeNoEditStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        dateTimeNoEditStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        dateTimeNoEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        dateTimeNoEditStyle.setLocked(true);
        styles.put("datetime_no_edit", dateTimeNoEditStyle);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private int createResultCellsForFormat(HSSFSheet sheet, Row row, Row tRow,
                                           String nameIndexPrefix,
                                           HashMap<String, String> cellNames,
                                           WorksheetViewDO wVDO,
                                           WorksheetAnalysisViewDO waVDO,
                                           ArrayList<WorksheetResultViewDO> wrList,
                                           boolean isEditable,
                                           HashMap<String, HashMap<Integer, ArrayList<AnalyteParameterViewDO>>> apMap) {
        int c, i, r;
        String cellNameIndex, name;
        ArrayList<AnalyteParameterViewDO> anaParams, apList;
        DecimalFormat df;
        HashMap<Integer, ArrayList<AnalyteParameterViewDO>> pMap;
        Cell cell, tCell;
        Name cellName;
        AnalyteParameterViewDO apVDO;

        df = new DecimalFormat();
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(10);
        
        i = 0;
        r = row.getRowNum();
        for (WorksheetResultViewDO wrVDO : wrList) {
            if (i != 0) {
                row = sheet.createRow(r);
                for (c = 0; c < 7; c++) {
                    cell = row.createCell(c);
                    cell.setCellStyle(styles.get("row_no_edit"));
                }
            }

            cellNameIndex = nameIndexPrefix + "." + i;

            // analyte
            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(wrVDO.getAnalyteName());

            // reportable
            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(wrVDO.getIsReportable());
            cellName = sheet.getWorkbook().createName();
            cellName.setNameName("analyte_reportable." + cellNameIndex);
            cellName.setRefersToFormula("Worksheet!$" +
                                        CellReference.convertNumToColString(8) + "$" +
                                        (row.getRowNum() + 1));

            apVDO = null;
            for (c = 9; c < tRow.getLastCellNum() && c < 39; c++) {
                tCell = tRow.getCell(c);

                cell = row.createCell(c);
                if (isEditable)
                    cell.setCellStyle(tCell.getCellStyle());
                else
                    cell.setCellStyle(styles.get("row_no_edit"));
                name = cellNames.get(sheet.getSheetName() +
                                     "!$" +
                                     CellReference.convertNumToColString(tCell.getColumnIndex()) +
                                     "$" + (tCell.getRowIndex() + 1));
                if (name != null) {
                    cellName = row.getSheet().getWorkbook().createName();
                    cellName.setNameName(name + "." + cellNameIndex);
                    cellName.setRefersToFormula(sheet.getSheetName() +
                                                "!$" +
                                                CellReference.convertNumToColString(cell.getColumnIndex()) +
                                                "$" + (row.getRowNum() + 1));
                }
                if (tCell.getCellType() == Cell.CELL_TYPE_FORMULA &&
                    tCell.getCellFormula() != null) {
                    cell.setCellFormula(tCell.getCellFormula());
                } else {
                    setCellValue(cell, wrVDO.getValueAt(c - 9));
                }
                if ("p1".equals(name) || "p2".equals(name) || "p3".equals(name) ||
                    "p_1".equals(name) || "p_2".equals(name) || "p_3".equals(name)) {
                    if (wrVDO.getValueAt(c - 9) == null) {
                        pMap = apMap.get("T"+waVDO.getTestId());
                        if (pMap == null) {
                            pMap = new HashMap<Integer, ArrayList<AnalyteParameterViewDO>>();
                            apMap.put("T"+waVDO.getTestId(), pMap);
                            try {
                                anaParams = analyteParameter.fetchByActiveDate(waVDO.getTestId(),
                                                                                    Constants.table().TEST,
                                                                                    wVDO.getCreatedDate().getDate());
                                for (AnalyteParameterViewDO anaParam : anaParams) {
                                    apList = pMap.get(anaParam.getAnalyteId());
                                    if (apList == null) {
                                        apList = new ArrayList<AnalyteParameterViewDO>();
                                        pMap.put(anaParam.getAnalyteId(), apList);
                                    }
                                    apList.add(anaParam);
                                }
                            } catch (NotFoundException nfE) {
                                continue;
                            } catch (Exception anyE) {
                                log.log(Level.SEVERE,
                                        "Error retrieving analyte parameters for an analysis on worksheet.",
                                        anyE);
                                continue;
                            }
                        }

                        apList = pMap.get(wrVDO.getAnalyteId());
                        apVDO = null;
                        if (apList != null && apList.size() > 0) {
                            for (AnalyteParameterViewDO ap: apList) {
                                if (ap.getUnitOfMeasureId() == null || ap.getUnitOfMeasureId().equals(waVDO.getUnitOfMeasureId())) {
                                    if (ap.getUnitOfMeasureId() != null) {
                                        apVDO = ap;
                                        break;
                                    } else if (apVDO == null) {
                                        apVDO = ap;
                                    }
                                }
                            }
                        }
                        if (apVDO != null) {
                            if (("p1".equals(name) || "p_1".equals(name)) && apVDO.getP1() != null) {
                                setCellValue(cell, df.format(apVDO.getP1()));
                            } else if (("p2".equals(name) || "p_2".equals(name)) && apVDO.getP2() != null) {
                                setCellValue(cell, df.format(apVDO.getP2()));
                            } else if (("p3".equals(name) || "p_3".equals(name)) && apVDO.getP3() != null) {
                                setCellValue(cell, df.format(apVDO.getP3()));
                            }
                        }
                    }
                }
            }

            i++;
            r++;
        }

        return r;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private int createQcResultCellsForFormat(HSSFSheet sheet, Row row, Row tRow,
                                             String nameIndexPrefix,
                                             HashMap<String, String> cellNames,
                                             WorksheetViewDO wVDO,
                                             Integer qcId,
                                             ArrayList<WorksheetQcResultViewDO> wqrList,
                                             HashMap<String, HashMap<Integer, ArrayList<AnalyteParameterViewDO>>> apMap) {
        int c, i, r;
        String cellNameIndex, name;
        ArrayList<AnalyteParameterViewDO> anaParams, apList;
        DecimalFormat df;
        HashMap<Integer, ArrayList<AnalyteParameterViewDO>> pMap;
        Cell cell, tCell;
        Name cellName;
        AnalyteParameterViewDO apVDO;

        df = new DecimalFormat();
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(10);

        i = 0;
        r = row.getRowNum();
        for (WorksheetQcResultViewDO wqrVDO : wqrList) {
            if (i != 0) {
                row = sheet.createRow(r);
                for (c = 0; c < 7; c++) {
                    cell = row.createCell(c);
                    cell.setCellStyle(styles.get("row_no_edit"));
                }
            }

            cellNameIndex = nameIndexPrefix + "." + i;

            // analyte
            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(wqrVDO.getAnalyteName());

            // reportable
            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue("N");

            apVDO = null;
            for (c = 9; c < tRow.getLastCellNum() && c < 39; c++) {
                tCell = tRow.getCell(c);

                cell = row.createCell(c);
                cell.setCellStyle(tCell.getCellStyle());
                name = cellNames.get(sheet.getSheetName() +
                                     "!$" +
                                     CellReference.convertNumToColString(tCell.getColumnIndex()) +
                                     "$" + (tCell.getRowIndex() + 1));
                if (name != null) {
                    cellName = row.getSheet().getWorkbook().createName();
                    cellName.setNameName(name + "." + cellNameIndex);
                    cellName.setRefersToFormula(sheet.getSheetName() +
                                                "!$" +
                                                CellReference.convertNumToColString(cell.getColumnIndex()) +
                                                "$" + (row.getRowNum() + 1));
                }
                if (tCell.getCellType() == Cell.CELL_TYPE_FORMULA &&
                    tCell.getCellFormula() != null) {
                    cell.setCellFormula(tCell.getCellFormula());
                } else {
                    setCellValue(cell, wqrVDO.getValueAt(c - 9));
                }
                if ("p1".equals(name) || "p2".equals(name) || "p3".equals(name) ||
                    "p_1".equals(name) || "p_2".equals(name) || "p_3".equals(name)) {
                    if (wqrVDO.getValueAt(c - 9) == null) {
                        pMap = apMap.get("Q"+qcId);
                        if (pMap == null) {
                            pMap = new HashMap<Integer, ArrayList<AnalyteParameterViewDO>>();
                            apMap.put("Q"+qcId, pMap);
                            try {
                                anaParams = analyteParameter.fetchByActiveDate(qcId,
                                                                               Constants.table().QC,
                                                                               wVDO.getCreatedDate().getDate());
                                for (AnalyteParameterViewDO anaParam : anaParams) {
                                    apList = pMap.get(anaParam.getAnalyteId());
                                    if (apList == null) {
                                        apList = new ArrayList<AnalyteParameterViewDO>();
                                        pMap.put(anaParam.getAnalyteId(), apList);
                                    }
                                    apList.add(anaParam);
                                }
                            } catch (NotFoundException nfE) {
                                continue;
                            } catch (Exception anyE) {
                                log.log(Level.SEVERE,
                                        "Error retrieving analyte parameters for a qc on worksheet.",
                                        anyE);
                                continue;
                            }
                        }

                        apList = pMap.get(wqrVDO.getAnalyteId());
                        apVDO = null;
                        if (apList != null && apList.size() > 0)
                            apVDO = apList.get(0);
                        if (apVDO != null) {
                            if (("p1".equals(name) || "p_1".equals(name)) && apVDO.getP1() != null) {
                                setCellValue(cell, String.valueOf(apVDO.getP1()));
                            } else if (("p2".equals(name) || "p_2".equals(name)) && apVDO.getP2() != null) {
                                setCellValue(cell, String.valueOf(apVDO.getP2()));
                            } else if (("p3".equals(name) || "p_3".equals(name)) && apVDO.getP3() != null) {
                                setCellValue(cell, String.valueOf(apVDO.getP3()));
                            }
                        }
                    }
                }
            }

            i++;
            r++;
        }

        return r;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void createEmptyCellsForFormat(Row row, Row tRow) {
        int c;
        Cell cell;

        // analyte
        cell = row.createCell(7);
        cell.setCellStyle(styles.get("row_no_edit"));
        cell.setCellValue("NO ANALYTES DEFINED");

        // reportable
        cell = row.createCell(8);
        cell.setCellStyle(styles.get("row_no_edit"));
        cell.setCellValue("N");

        for (c = 9; c < tRow.getLastCellNum(); c++) {
            cell = row.createCell(c);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue("");
        }
    }

    private void loadStatuses() {
        int i;
        ArrayList<DictionaryDO> statusDOs;
        DictionaryDO dictDO;

        if (statusIdNameMap == null && statuses == null) {
            statusDOs = new ArrayList<DictionaryDO>();
            try {
                statusDOs = categoryCache.getBySystemName("analysis_status")
                                              .getDictionaryList();
                statuses = new String[statusDOs.size()];
                statusIdNameMap = new HashMap<Integer, String>();
                statusNameIdMap = new HashMap<String, Integer>();
                for (i = 0; i < statusDOs.size(); i++) {
                    dictDO = statusDOs.get(i);
                    statusIdNameMap.put(dictDO.getId(), dictDO.getEntry());
                    statusNameIdMap.put(dictDO.getEntry(), dictDO.getId());
                    statuses[i] = dictDO.getEntry();
                }
            } catch (Exception anyE) {
                log.log(Level.SEVERE, "Could not fetch dictionary entries", anyE);
            }
        }
    }

    private HashMap<String, String> loadNamesByCellReference(HSSFWorkbook wb) {
        int i;
        HSSFName name;
        HashMap<String, String> names;

        names = new HashMap<String, String>();

        for (i = 0; i < wb.getNumberOfNames(); i++) {
            name = wb.getNameAt(i);
            names.put(name.getRefersToFormula(), name.getNameName());
        }

        return names;
    }

    private Cell getCellForName(HSSFSheet sheet, String name) {
        AreaReference aref;
        Cell cell;
        CellReference cref[];
        HSSFName cellName;

        cell = null;
        cellName = sheet.getWorkbook().getName(name);
        if (cellName != null && !cellName.isDeleted()) {
            aref = new AreaReference(cellName.getRefersToFormula());
            cref = aref.getAllReferencedCells();
            cell = sheet.getRow(cref[0].getRow()).getCell((int)cref[0].getCol());
        }

        return cell;
    }

    private String getWorksheetOutputFileName(Integer worksheetNumber, Integer userId) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String dirName;
        SystemUserVO userVO;

        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("worksheet_output_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: " +
                                anyE.getMessage());
        }

        userVO = null;
        try {
            userVO = userCache.getSystemUser(userId);
        } catch (Exception anyE) {
            throw new Exception("Error retrieving username for worksheet: " +
                                anyE.getMessage());
        }

        return dirName + worksheetNumber + "_" + userVO.getLoginName() + ".xls";
    }

    private String getWorksheetTemplateFileName(DictionaryDO formatDO) throws Exception {
        return getWorksheetTemplateFileName(formatDO.getEntry());
    }
    
    private String getWorksheetTemplateFileName(String format) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String dirName;

        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: " +
                                anyE.getMessage());
        }

        return dirName + "OEWorksheet" + format + ".xls";
    }

    private Object getValueFromCellByCoords(HSSFSheet sheet, int row, int col) {
        return getCellValue(sheet.getRow(row).getCell(col));
    }

    private Object getValueFromCellByName(HSSFSheet sheet, String name) {
        return getCellValue(getCellForName(sheet, name));
    }

    private Object getCellValue(Cell cell) {
        Object value;
        FormulaEvaluator eval;

        value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_FORMULA:
                    eval = cell.getSheet()
                               .getWorkbook()
                               .getCreationHelper()
                               .createFormulaEvaluator();
                    switch (eval.evaluateFormulaCell(cell)) {
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                value = new Datetime(Datetime.YEAR,
                                                     Datetime.MINUTE,
                                                     cell.getDateCellValue());
                            } else {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                value = cell.getStringCellValue();
                                if (((String)value).trim().length() == 0)
                                    value = null;
                            }
                            break;

                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            if (((String)value).trim().length() == 0)
                                value = null;
                            break;
                    }
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = new Datetime(Datetime.YEAR,
                                             Datetime.MINUTE,
                                             cell.getDateCellValue());
                    } else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        value = cell.getStringCellValue();
                        if (((String)value).trim().length() == 0)
                            value = null;
                    }
                    break;

                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    if (((String)value).trim().length() == 0)
                        value = null;
                    break;
            }
        }

        return value;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void setCellValue(Cell cell, String value) {
        Date tempDate;

        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value,
                                                                          new ParsePosition(1));
                if (tempDate != null) {
                    cell.setCellValue(tempDate);
                    return;
                }
            }
            cell.setCellValue(Double.parseDouble(value));
            return;
        } catch (Exception ignE) {
            // ignore exceptions from trying to parse special values
        }

        cell.setCellValue(value);
    }

    private void loadMaps(WorksheetManager1 manager,
                          HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> waMap,
                          HashMap<Integer, WorksheetAnalysisViewDO> waLinkMap,
                          HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrMap,
                          HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> wqrMap) {
        ArrayList<WorksheetAnalysisViewDO> waList;
        ArrayList<WorksheetResultViewDO> wrList;
        ArrayList<WorksheetQcResultViewDO> wqrList;
        
        if (getAnalyses(manager) != null) {
            for (WorksheetAnalysisViewDO waDO : getAnalyses(manager)) {
                waList = waMap.get(waDO.getWorksheetItemId());
                if (waList == null) {
                    waList = new ArrayList<WorksheetAnalysisViewDO>();
                    waMap.put(waDO.getWorksheetItemId(), waList);
                }
                waList.add(waDO);
                waLinkMap.put(waDO.getId(), waDO);
            }
        }
        if (getResults(manager) != null) {
            for (WorksheetResultViewDO wrDO : getResults(manager)) {
                wrList = wrMap.get(wrDO.getWorksheetAnalysisId());
                if (wrList == null) {
                    wrList = new ArrayList<WorksheetResultViewDO>();
                    wrMap.put(wrDO.getWorksheetAnalysisId(), wrList);
                }
                wrList.add(wrDO);
            }
        }
        if (getQcResults(manager) != null) {
            for (WorksheetQcResultViewDO wqrDO : getQcResults(manager)) {
                wqrList = wqrMap.get(wqrDO.getWorksheetAnalysisId());
                if (wqrList == null) {
                    wqrList = new ArrayList<WorksheetQcResultViewDO>();
                    wqrMap.put(wqrDO.getWorksheetAnalysisId(), wqrList);
                }
                wqrList.add(wqrDO);
            }
        }
    }
}