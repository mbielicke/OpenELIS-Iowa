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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.naming.InitialContext;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SystemUserPermissionProxyLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AnalysisUserManager;
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.remote.WorksheetCompletionRemote;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-update")
public class WorksheetCompletionBean implements WorksheetCompletionRemote {

    private HashMap<String,CellStyle>    styles;
//    private HashMap<String,FormatColumn> columnMasterMap;

    public WorksheetCompletionBean() {
//        createColumnMasterMap();
    }

    public WorksheetManager saveForEdit(WorksheetManager manager) throws Exception {
        int                      r, i, a, c, o, itemMergeStart, anaMergeStart;
        String                   statuses[], cellNameIndex, posNum;
        FileInputStream          in;
        FileOutputStream         out;
        HashMap<Integer,String>  statusMap;
        HashMap<String,String>   tCellNames;
        Cell                     cell;
        CellRangeAddressList     statusColumn, reportableColumn, dateTimeColumn;
        DVConstraint             statusConstraint, reportableConstraint, dateTimeConstraint;
        HSSFDataValidation       statusValidation, reportableValidation, dateTimeValidation;
        HSSFSheet                resultSheet, overrideSheet;
        HSSFWorkbook             wb;
        Name                     cellName;
        Row                      row, hRow, oRow, tRow;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisViewDO           aVDO;
        DictionaryViewDO         formatVDO;
        QcManager                qcManager;
        SampleDataBundle         bundle;
        SampleDomainInt          sDomain;
        SampleItemManager        siManager;
        SampleManager            sManager;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetResultManager   wrManager;

        try {
            formatVDO = dictLocal().fetchById(manager.getWorksheet().getFormatId());
        } catch (NotFoundException nfE) {
            formatVDO = new DictionaryViewDO();
            formatVDO.setLocalAbbrev("Total");
            formatVDO.setSystemName("wformat_total");
        } catch (Exception anyE) {
            System.out.println("Error retrieving worksheet format: "+anyE.getMessage());
            return manager;
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatVDO));
        } catch (FileNotFoundException fnfE) {
            System.out.println("Error loading template file: "+fnfE.getMessage());
            return manager;
        }
        
        try {
            wb = new HSSFWorkbook(in, true);
        } catch (IOException ioE) {
            System.out.println("Error loading workbook from template file: "+ioE.getMessage());
            return manager;
        }

        statusMap = getStatusMap();
        statuses  = getStatusArray();
        
        createStyles(wb);
        tCellNames = loadNamesByCellReference(wb);

        resultSheet = wb.getSheet("Worksheet");

        hRow = resultSheet.getRow(0);
        tRow = resultSheet.getRow(1);
        resultSheet.removeRow(tRow);
        
        overrideSheet = wb.getSheet("Overrides");

        r = 1;
        o = 1;
        for (i = 0; i < manager.getItems().count(); i++) {
            itemMergeStart = r;
            wiDO           = manager.getItems().getWorksheetItemAt(i);
            waManager      = manager.getItems().getWorksheetAnalysisAt(i);

            for (a = 0; a < waManager.count(); a++) {
                anaMergeStart = r;
                waDO          = waManager.getWorksheetAnalysisAt(a);

                row = resultSheet.createRow(r);
                oRow = overrideSheet.createRow(o);

                // position number
                posNum = getPositionNumber(wiDO.getPosition(), formatVDO.getSystemName(),
                                           manager.getWorksheet().getBatchCapacity());
                cell = row.createCell(0);
                cell.setCellStyle(styles.get("row_no_edit"));
                if (a == 0)
                    cell.setCellValue(posNum);
                cell = oRow.createCell(0);
                cell.setCellStyle(styles.get("row_no_edit"));
                if (a == 0)
                    cell.setCellValue(posNum);
                
                // accession number
                cell = row.createCell(1);
                cell.setCellStyle(styles.get("row_no_edit"));
                cell.setCellValue(waDO.getAccessionNumber());
                cell = oRow.createCell(1);
                cell.setCellStyle(styles.get("row_no_edit"));
                cell.setCellValue(waDO.getAccessionNumber());
                
                if (waDO.getAnalysisId() != null) {
                    bundle = waManager.getBundleAt(a);
                    sManager = bundle.getSampleManager();
                    sDomain = sManager.getDomainManager();
                    siManager = sManager.getSampleItems();
                    aManager = siManager.getAnalysisAt(bundle.getSampleItemIndex());
                    aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                    arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());

                    // description
                    cell = row.createCell(2);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    if (sDomain != null)
                        cell.setCellValue(sDomain.getDomainDescription());
                    else
                        cell.setCellValue("");
    
                    // qc link
                    cell = row.createCell(3);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue("");

                    // test name
                    cell = row.createCell(4);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(aVDO.getTestName());
                    
                    // method name
                    cell = row.createCell(5);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(aVDO.getMethodName());
                    
                    // analysis status
                    cell = row.createCell(6);
                    cell.setCellStyle(styles.get("row_edit"));
                    cell.setCellValue(statusMap.get(aVDO.getStatusId()));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_status."+i+"."+a);
                    cellName.setRefersToFormula("Worksheet!$"+CellReference.convertNumToColString(6)+
                                                "$"+(row.getRowNum()+1));

                    // description (override)
                    cell = oRow.createCell(2);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    if (sDomain != null)
                        cell.setCellValue(sDomain.getDomainDescription());
                    else
                        cell.setCellValue("");
    
                    // test name (overrride)
                    cell = oRow.createCell(3);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(aVDO.getTestName());
                    
                    // method name (override)
                    cell = oRow.createCell(4);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(aVDO.getMethodName());
                    
                    cellNameIndex = i+"."+a;
                    wrManager = waManager.getWorksheetResultAt(a);
                    if (wrManager.count() == 0) {
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
                        r = createResultCellsForFormat(resultSheet, row, tRow, cellNameIndex,
                                                       tCellNames, arManager, wrManager);
                    }

                    // users (override)
                    cell = oRow.createCell(5);
                    cell.setCellStyle(styles.get("row_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_users."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(5)+
                                                "$"+(oRow.getRowNum()+1));
                    
                    // started (override)
                    cell = oRow.createCell(6);
                    cell.setCellStyle(styles.get("row_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_started."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(6)+
                                                "$"+(oRow.getRowNum()+1));

                    // completed (override)
                    cell = oRow.createCell(7);
                    cell.setCellStyle(styles.get("row_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_completed."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(7)+
                                                "$"+(oRow.getRowNum()+1));
                    o++;
                } else if (waDO.getQcId() != null) {
                    qcManager = QcManager.fetchById(waDO.getQcId());

                    // description
                    cell = row.createCell(2);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(qcManager.getQc().getName());
    
                    // qc link
                    cell = row.createCell(3);
                    cell.setCellStyle(styles.get("row_no_edit"));
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

                    wqrManager = waManager.getWorksheetQcResultAt(a);
                    if (wqrManager.count() == 0) {
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
                        cellNameIndex = i+"."+a;
                        r = createQcResultCellsForFormat(resultSheet, row, tRow,
                                                         cellNameIndex, tCellNames,
                                                         wqrManager);
                    }
                }
                for (c = 3; c < 6; c++)
                    resultSheet.addMergedRegion(new CellRangeAddress(anaMergeStart, r - 1, c, c));
            }
            
            for (c = 0; c < 3; c++)
                resultSheet.addMergedRegion(new CellRangeAddress(itemMergeStart, r - 1, c, c));
        }
        
        //
        // Create validators
        //
        statusColumn = new CellRangeAddressList(1,resultSheet.getPhysicalNumberOfRows()-1,6,6);
        statusConstraint = DVConstraint.createExplicitListConstraint(statuses);
        statusValidation = new HSSFDataValidation(statusColumn,statusConstraint);
        statusValidation.setEmptyCellAllowed(true);
        statusValidation.setSuppressDropDownArrow(false);
        statusValidation.createPromptBox("Statuses", formatTooltip(statuses));
        statusValidation.setShowPromptBox(false);
        resultSheet.addValidationData(statusValidation);
        
        reportableColumn = new CellRangeAddressList(1,resultSheet.getPhysicalNumberOfRows()-1,8,8);
        reportableConstraint = DVConstraint.createExplicitListConstraint(new String[]{"Y","N"});
        reportableValidation = new HSSFDataValidation(reportableColumn,reportableConstraint);
        reportableValidation.setSuppressDropDownArrow(false);
        resultSheet.addValidationData(reportableValidation);

        dateTimeColumn = new CellRangeAddressList(1,overrideSheet.getPhysicalNumberOfRows()-1,6,7);
        dateTimeConstraint = DVConstraint.createDateConstraint(DVConstraint.OperatorType.IGNORED, "1900-01-01 00:00", "2099-12-31 23:59", "yyyy-MM-dd HH:mm");
        dateTimeValidation = new HSSFDataValidation(dateTimeColumn,dateTimeConstraint);
        dateTimeValidation.setEmptyCellAllowed(true);
        overrideSheet.addValidationData(dateTimeValidation);

        //
        // Auto resize columns on result sheet and overriddes sheet
        //
        for (c = 0; c < hRow.getLastCellNum(); c++)
            resultSheet.autoSizeColumn(c, true);
        for (c = 0; c < 8; c++)
            overrideSheet.autoSizeColumn(c, true);
        
        //
        // Remove names of template cells from workbook
        //
        for (c = 0; c < tRow.getLastCellNum(); c++) {
            i = wb.getNameIndex(tCellNames.get(resultSheet.getSheetName()+"!$"+
                                              CellReference.convertNumToColString(c+1)+
                                              "$"+(tRow.getRowNum()+1)));
            if (i != -1)
                wb.removeName(i);
        }
            
        try {
            out = new FileOutputStream(getWorksheetOutputFileName(manager.getWorksheet().getId()));
            wb.write(out);
            out.close();
            Runtime.getRuntime().exec("chmod go+rw "+getWorksheetOutputFileName(manager.getWorksheet().getId()));
        } catch (Exception anyE) {
            System.out.println("Error writing Excel file: "+anyE.getMessage());
        }

        return manager;
    }

    public WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception {
        boolean                  keepLock;
        int                      a, i, j, k, c, r, s, rowIndex;
        Object                   value;
        Integer                  testResultId;
        String                   userToken;
        ArrayList<DictionaryDO>  statusList;
//        ArrayList<FormatColumn>  columnList;
        ArrayList<SystemUserVO>  userList;
        HashMap<Integer,String>  statusMap;
        File                     file;
        FileInputStream          in;
//        FormatColumn             formatColumn;
        StringTokenizer          tokenizer;
        ValidationErrorsList     errorList;
        Cell                     cell;
        HSSFWorkbook             wb;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisUserManager      auManager;
        AnalysisViewDO           aVDO;
        AnalyteDO                aDO;
        DictionaryDO             statusDO;
        ResultViewDO             rVDO;
        SampleDataBundle         bundle, newBundle;
        SampleItemManager        siManager;
        SampleManager            sManager;
        TestResultDO             trDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemManager     wiManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetQcResultViewDO  wqrVDO;
        WorksheetResultManager   wrManager;
        WorksheetResultViewDO    wrVDO;
        
        errorList = new ValidationErrorsList();
        file = new File(getWorksheetOutputFileName(manager.getWorksheet().getId()));
        in   = new FileInputStream(file);
        wb   = new HSSFWorkbook(in);
//        columnList = getColumnListForFormat(formatVDO.getSystemName());
        statusList = getStatuses();
        statusMap = getStatusMap();

        r = 0;
        rowIndex = 1;
        wiManager = manager.getItems();
        for (i = 0; i < wiManager.count(); i++) {
            wiDO = wiManager.getWorksheetItemAt(i);
            waManager = wiManager.getWorksheetAnalysisAt(i);
            for (a = 0; a < waManager.count(); a++) {
                waDO = waManager.getWorksheetAnalysisAt(a);
                if (waDO.getAnalysisId() != null) {
                    keepLock = false;
                    bundle = waManager.getBundleAt(a);
                    sManager = bundle.getSampleManager();
                    siManager = sManager.getSampleItems();
                    aManager = siManager.getAnalysisAt(bundle.getSampleItemIndex());
                    aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                    arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());

                    if (!manager.getLockedManagers().containsKey(sManager.getSample().getAccessionNumber())) {
                        sManager = sampManLocal().fetchForUpdate(sManager.getSample().getId());
                        manager.getLockedManagers().put(sManager.getSample().getAccessionNumber(), sManager);
                        manager.getSampleManagers().put(sManager.getSample().getAccessionNumber(), sManager);
                        siManager = sManager.getSampleItems();
                        newBundle = null;
                        for (j = 0; j < siManager.count(); j++) {
                            aManager = siManager.getAnalysisAt(j);
                            for (k = 0; k < aManager.count(); k++) {
                                aVDO = aManager.getAnalysisAt(k);
                                if (waDO.getAnalysisId().equals(aVDO.getId())) {
                                    newBundle = aManager.getBundleAt(k);
                                    arManager = aManager.getAnalysisResultAt(k);
                                    break;
                                }
                            }
                            if (newBundle != null) {
                                waManager.setBundleAt(newBundle, a);
                                bundle = newBundle;
                                break;
                            }
                        }
                    }

                    value = getValueFromCellByName(wb.getSheet("Worksheet"), "analysis_status."+i+"."+a);
                    if (!statusMap.get(aVDO.getStatusId()).equals(value)) {
                        for (s = 0; s < statusList.size(); s++) {
                            statusDO = statusList.get(s);
                            if (statusDO.getEntry().equals(value)) {
                                aVDO.setStatusId(statusDO.getId());
                                keepLock = true;
                                break;
                            }
                        }
                    }
                    
                    value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_users."+i+"."+a);
                    if (value != null) {
                        auManager = aManager.getAnalysisUserAt(bundle.getAnalysisIndex());
                        tokenizer = new StringTokenizer((String)value, ",");
                        while (tokenizer.hasMoreTokens()) {
                            userToken = tokenizer.nextToken();
                            userList = sysUserLocal().fetchByLoginName(userToken, 1);
                            if (userList.size() > 0) {
                                auManager.addCompleteRecord(userList.get(0));
                                keepLock = true;
                            } else {
                                errorList.add(new FormErrorException("illegalWorksheetUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                            }
                        }
                    }
                    
                    value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_started."+i+"."+a);
                    if (value != null) {
                        aVDO.setStartedDate(new Datetime(Datetime.YEAR, Datetime.MINUTE, (Date)value));
                        keepLock = true;
                    }
                    
                    value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_completed."+i+"."+a);
                    if (value != null) {
                        aVDO.setCompletedDate(new Datetime(Datetime.YEAR, Datetime.MINUTE, (Date)value));
                        keepLock = true;
                    }
                    
                    wrManager = waManager.getWorksheetResultAt(a);
                    for (r = 0; r < wrManager.count(); r++, rowIndex++) {
                        wrVDO = wrManager.getWorksheetResultAt(r);
                        for (c = 0; c < 30; c++) {
                            value = getValueFromCellByCoords(wb.getSheet("Worksheet"), rowIndex, 9 + c);
                            if (value != null)
                                wrVDO.setValueAt(c, value.toString());
                        }   

                        for (c = 0; c < arManager.getRowAt(wrVDO.getResultRow()).size(); c++) {
                            rVDO = arManager.getResultAt(wrVDO.getResultRow(), c);
                            try {
                                if (c == 0) {
                                    value = getValueFromCellByName(wb.getSheet("Worksheet"), "analyte_reportable."+i+"."+a);
                                    if (value != null && !rVDO.getIsReportable().equals(value)) {
                                        rVDO.setIsReportable((String)value);
                                        keepLock = true;
                                    }

                                    value = getValueFromCellByName(wb.getSheet("Worksheet"), "final_value."+i+"."+a+"."+r);
                                } else {
                                    try {
                                        aDO = analyteLocal().fetchById(rVDO.getAnalyteId());
                                        cell = getCellForName(wb.getSheet("Worksheet"), aDO.getExternalId()+"."+i+"."+a+"."+r);
                                        if (cell == null)
                                            continue;
                                        value = getValueFromCellByName(wb.getSheet("Worksheet"), aDO.getExternalId()+"."+i+"."+a+"."+r);
                                    } catch (Exception anyE) {
                                        errorList.add(new FormErrorException("columnAnalyteLookupFormException",
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             wrVDO.getAnalyteName(),
                                                                             rVDO.getAnalyte()));
                                    }
                                }
                                if (value != null) {
                                    testResultId = arManager.validateResultValue(rVDO.getResultGroup(),
                                                                                 aVDO.getUnitOfMeasureId(),
                                                                                 value.toString());
                                    trDO = arManager.getTestResultList().get(testResultId);
                                    
                                    rVDO = arManager.getResultAt(wrVDO.getResultRow(), c);
                                    rVDO.setTestResultId(testResultId);
                                    rVDO.setTypeId(trDO.getTypeId());
                                    rVDO.setValue(formatValue(trDO, value.toString()));
                                    keepLock = true;
                                }
                            } catch (ParseException parE) {
                                errorList.add(new FormErrorException("illegalResultValueFormException",
                                                                     String.valueOf(wiDO.getPosition()),
                                                                     wrVDO.getAnalyteName(),
                                                                     (c == 0 ? "Final Value" : rVDO.getAnalyte())));
                            }
                        }
                    }
                    
                    if (!keepLock)
                        sManager.abortUpdate();
                } else if (waDO.getQcId() != null) {
                    wqrManager = waManager.getWorksheetQcResultAt(a);
                    for (r = 0; r < wqrManager.count(); r++, rowIndex++) {
                        wqrVDO = wqrManager.getWorksheetQcResultAt(r);
                        
                        value = getValueFromCellByName(wb.getSheet("Worksheet"), "final_value."+i+"."+a+"."+r);
                        if (value != null) {
                            wqrVDO.setValue(value.toString());
                        }
                    }
                }
                
                //
                // increment rowIndex if there are no result records for the
                // analysis
                if (r == 0)
                    rowIndex++;
            }
        }
        
        if (errorList.getErrorList().size() > 0)
            throw errorList;
        
        file.delete();
        
        return manager;
    }
/*    
    private void createColumnMasterMap() {
        columnMasterMap = new HashMap<String,FormatColumn>();
        
        columnMasterMap.put("raw_value1", new FormatColumn("raw_value1", null, null));
        columnMasterMap.put("raw_value2", new FormatColumn("raw_value2", null, null));
        columnMasterMap.put("dilution_factor", new FormatColumn("dilution_factor", null, null));
        columnMasterMap.put("final_value1", new FormatColumn("final_value1", "%s*%s", new String[]{"raw_value1", "dilution_factor"}));
        columnMasterMap.put("final_value2", new FormatColumn("final_value2", "%s*%s", new String[]{"raw_value2", "dilution_factor"}));
        columnMasterMap.put("final_value", new FormatColumn("final_value", null, null));
        columnMasterMap.put("range_low", new FormatColumn("range_low", null, null));
        columnMasterMap.put("range_high", new FormatColumn("range_high", null, null));
        columnMasterMap.put("quant_limit", new FormatColumn("quant_limit", null, null));
        columnMasterMap.put("final_quant_limit", new FormatColumn("final_quant_limit", "%s*%s", new String[]{"quant_limit", "dilution_factor"}));
        columnMasterMap.put("expected_value", new FormatColumn("expected_value", null, null));
        columnMasterMap.put("expected_value_dilut", new FormatColumn("expected_value_dilut", "%s*%s", new String[]{"expected_value", "dilution_factor"}));
        columnMasterMap.put("percent_recovery", new FormatColumn("percent_recovery", "(%s/%s)*100", new String[]{"final_value","expected_value"}));
        columnMasterMap.put("sample_volume", new FormatColumn("sample_volume", null, null));
        columnMasterMap.put("extract_volume", new FormatColumn("extract_volume", null, null));
        columnMasterMap.put("instrument_run_id", new FormatColumn("instrument_run_id", null, null));
        columnMasterMap.put("retention_time", new FormatColumn("retention_time", null, null));
        columnMasterMap.put("response", new FormatColumn("response", null, null));
        columnMasterMap.put("molecular_weight", new FormatColumn("molecular_weight", null, null));
        columnMasterMap.put("desorp_efficiency", new FormatColumn("desorp_efficiency", null, null));
    }
*/    
    private void createStyles(HSSFWorkbook wb) {
        CellStyle headerStyle, rowEditStyle, rowNoEditStyle;
        Font      font;

        styles = new HashMap<String,CellStyle>();

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
    }
/*
    private void updateHeader(HSSFSheet sheet, Row row, HashMap<String,String> cellNames) {
        int                  i;
        Cell                 cell;
        FormatColumn         column;
        ArrayList<AnalyteDO> analytes;

        row = sheet.getRow(0);

        // Add additional header cells for analytes not in format, but in master list
        for (i = 0; i < formatColumns.size(); i++) {
            column = formatColumns.get(i);
            cell = row.createCell(i+9);
            cell.setCellStyle(styles.get("header"));
            
            try {
                analytes = analyteLocal().fetchByExternalId(column.getName(), 10);
                cell.setCellValue(analytes.get(0).getName());
            } catch (Exception anyE) {
                cell.setCellValue(column.getName());
            }
        }
    }
*/
    private int createResultCellsForFormat(HSSFSheet sheet, Row row, Row tRow, String nameIndexPrefix,
                                           HashMap<String,String> cellNames, AnalysisResultManager arManager,
                                           WorksheetResultManager wrManager) {
        int                   c, i, /*j, */r;
        String                cellNameIndex, name;
        Cell                  cell, tCell;
        Date                  tempDate;
        Name                  cellName;
        AnalyteDO             aDO;
        ResultViewDO          rVDO;
        WorksheetResultViewDO wrVDO;
        
        r = row.getRowNum();
        for (i = 0; i < wrManager.count(); i++, r++) {
            wrVDO = wrManager.getWorksheetResultAt(i);
            if (i != 0) {
                row = sheet.createRow(r);
                for (c = 0; c < 7; c++) {
                    cell = row.createCell(c);
                    cell.setCellStyle(styles.get("row_no_edit"));
                }                            
            }
            
            rVDO = arManager.getResultAt(wrVDO.getResultRow(), 0);
            cellNameIndex = nameIndexPrefix+"."+i;
            
            // analyte
            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(wrVDO.getAnalyteName());
            
            // reportable
            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_edit"));
            cell.setCellValue(rVDO.getIsReportable());
            cellName = sheet.getWorkbook().createName();
            cellName.setNameName("analyte_reportable."+cellNameIndex);
            cellName.setRefersToFormula("Worksheet!$"+CellReference.convertNumToColString(8)+
                                        "$"+(row.getRowNum()+1));
            
            for (c = 9; c < tRow.getLastCellNum(); c++) {
                tCell = tRow.getCell(c);
                
                cell = row.createCell(c);
                cell.setCellStyle(tCell.getCellStyle());
                name = cellNames.get(sheet.getSheetName()+"!$"+
                                     CellReference.convertNumToColString(tCell.getColumnIndex())+
                                     "$"+(tCell.getRowIndex()+1));
                if (name != null) {
                    cellName = row.getSheet().getWorkbook().createName();
                    cellName.setNameName(name+"."+cellNameIndex);
                    cellName.setRefersToFormula(sheet.getSheetName()+"!$"+
                                                CellReference.convertNumToColString(cell.getColumnIndex())+
                                                "$"+(row.getRowNum()+1));
                }
                if (tCell.getCellType() == Cell.CELL_TYPE_FORMULA && tCell.getCellFormula() != null) {
                    cell.setCellFormula(tCell.getCellFormula());

                    try {
                        tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(wrVDO.getValueAt(c-9),
                                                                                  new ParsePosition(1));
                        if (tempDate != null)
                            cell.setCellValue(tempDate);
                        else
                            throw new Exception("Invalid Date");
                    } catch (Exception anyE) {
                        try {
                            cell.setCellValue(Double.parseDouble(wrVDO.getValueAt(c-9)));
                        } catch (Exception ignE) {
                            // we won't be getting boolean values and string values
                            // will wipe the formula
                        }
                    }
                } else {
                    cell.setCellValue(wrVDO.getValueAt(c-9));
                }
            }
            
            for (c = 0; c < arManager.getRowAt(wrVDO.getResultRow()).size(); c++) {
                rVDO = arManager.getResultAt(wrVDO.getResultRow(), c);
                try {
                    aDO = analyteLocal().fetchById(rVDO.getAnalyteId());
                    if (!"Y".equals(rVDO.getIsColumn()))
                        cellName = sheet.getWorkbook().getName("final_value."+cellNameIndex);
                    else
                        cellName = sheet.getWorkbook().getName(aDO.getExternalId()+"."+cellNameIndex);

                    if (cellName != null && !cellName.isDeleted()) {
                        cell = getCellForName(sheet, cellName.getNameName());
                        if (cell.getStringCellValue() == null || cell.getStringCellValue().length() == 0)
                            cell.setCellValue(rVDO.getValue());
//                    } else {
//                        formatColumn = columnMasterMap.get(aDO.getExternalId());
//                        if (formatColumn != null) {
//                            j = row.getLastCellNum();
//                            cell = row.createCell(j);
//                            if (formatColumn.getEquation() != null && formatColumn.getEquation().length() > 0) {
//                                cell.setCellStyle(styles.get("row_no_edit"));
//                                try {
//                                    cell.setCellFormula(formatCellFormula(row, formatColumn, formatColumns));
//                                } catch (Exception anyE1) {
//                                    // TODO: Code proper exception handling
//                                    anyE1.printStackTrace();
//                                }
//                            } else {
//                                cell.setCellStyle(styles.get("row_edit"));
//                            }
//                            if (wrVDO.getValueAt(j-9) != null && wrVDO.getValueAt(j-9).length() > 0)
//                                cell.setCellValue(wrVDO.getValueAt(j-9));
//                            else
//                                cell.setCellValue(rVDO.getValue());
//    
//                            cellName = sheet.getWorkbook().createName();
//                            cellName.setNameName(formatColumn.getName()+"."+cellNameIndex);
//                            cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(j)+
//                                                        (row.getRowNum()+1));
//                        }
                    }
                } catch (Exception anyE) {
                    // TODO: Code proper exception handling
                    anyE.printStackTrace();
                }
            }
        }
        
        return r;
    }

    private int createQcResultCellsForFormat(HSSFSheet sheet, Row row, Row tRow,
                                             String nameIndexPrefix, HashMap<String,String> cellNames,
                                             WorksheetQcResultManager wqrManager) {
        boolean                 addName;
        int                     c, i, r;
        String                  cellNameIndex, name;
        Cell                    cell, tCell;
        Name                    cellName;
        QcAnalyteViewDO         qcaVDO;
        WorksheetQcResultViewDO wqrVDO;
        
        r = row.getRowNum();
        for (i = 0; i < wqrManager.count(); i++, r++) {
            wqrVDO = wqrManager.getWorksheetQcResultAt(i);
            if (i != 0) {
                row = sheet.createRow(r);
                for (c = 0; c < 7; c++) {
                    cell = row.createCell(c);
                    cell.setCellStyle(styles.get("row_no_edit"));
                }                            
            }
            
            cellNameIndex = nameIndexPrefix+"."+i;
            
            // analyte
            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(wqrVDO.getAnalyteName());
            
            // reportable
            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue("N");
            
            for (c = 9; c < tRow.getLastCellNum(); c++) {
                addName = false;
                tCell = tRow.getCell(c);
                
                cell = row.createCell(c);
                name = cellNames.get(sheet.getSheetName()+"!$"+
                                     CellReference.convertNumToColString(tCell.getColumnIndex())+
                                     "$"+(tCell.getRowIndex()+1));
                if (name != null) {
                    if ("final_value".equals(name)) {
                        cell.setCellStyle(styles.get("row_edit"));
                        if (wqrVDO.getValue() != null && wqrVDO.getValue().length() > 0) {
                            cell.setCellValue(wqrVDO.getValue());
                        }
                        addName = true;
                    } else if ("expected_value".equals(name)) {
                        cell.setCellStyle(styles.get("row_edit"));
                        try {
                            qcaVDO = qcAnaLocal().fetchById(wqrVDO.getQcAnalyteId());
                            cell.setCellValue(qcaVDO.getValue());
                        } catch (Exception anyE) {
                            // TODO: Code proper exception handling
                            anyE.printStackTrace();
                        }
                        addName = true;
                    } else {
                        cell.setCellStyle(styles.get("row_no_edit"));
                    }

                    if (addName) {
                        cellName = row.getSheet().getWorkbook().createName();
                        cellName.setNameName(name+"."+cellNameIndex);
                        cellName.setRefersToFormula(sheet.getSheetName()+"!$"+
                                                    CellReference.convertNumToColString(cell.getColumnIndex())+
                                                    "$"+(row.getRowNum()+1));
                    }
                }
            }
        }
        
        return r;
    }

    private void createEmptyCellsForFormat(Row row, Row tRow) {
        int  c;
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
    
    private AnalyteLocal analyteLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (AnalyteLocal)ctx.lookup("openelis/AnalyteBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private QcAnalyteLocal qcAnaLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (QcAnalyteLocal)ctx.lookup("openelis/QcAnalyteBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private SampleManagerLocal sampManLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SampleManagerLocal)ctx.lookup("openelis/SampleManagerBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private SystemUserPermissionProxyLocal sysUserLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SystemUserPermissionProxyLocal)ctx.lookup("openelis/SystemUserPermissionProxyBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private SystemVariableLocal sysVarLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SystemVariableLocal)ctx.lookup("openelis/SystemVariableBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
/*    
    private ArrayList<FormatColumn> getColumnListForFormat(String formatName) {
        ArrayList<FormatColumn> colList;
        
        colList = new ArrayList<FormatColumn>();
        if ("wsheet_num_format_total".equals(formatName) || "wsheet_num_format_batch".equals(formatName)) {
            colList.add(columnMasterMap.get("raw_value1"));
            colList.add(columnMasterMap.get("dilution_factor"));
            colList.add(columnMasterMap.get("final_value1"));
            colList.add(columnMasterMap.get("final_value"));
            colList.add(columnMasterMap.get("expected_value"));
        } else if ("format_terrycain1".equals(formatName)) {
            colList.add(columnMasterMap.get("raw_value1"));
            colList.add(columnMasterMap.get("raw_value2"));
            colList.add(columnMasterMap.get("dilution_factor"));
            colList.add(columnMasterMap.get("final_value1"));
            colList.add(columnMasterMap.get("final_value2"));
            colList.add(columnMasterMap.get("final_value"));
            colList.add(columnMasterMap.get("range_low"));
            colList.add(columnMasterMap.get("range_high"));
            colList.add(columnMasterMap.get("quant_limit"));
            colList.add(columnMasterMap.get("final_quant_limit"));
            colList.add(columnMasterMap.get("expected_value"));
            colList.add(columnMasterMap.get("expected_value_dilut"));
            colList.add(columnMasterMap.get("percent_recovery"));
            colList.add(columnMasterMap.get("sample_volume"));
            colList.add(columnMasterMap.get("extract_volume"));
            colList.add(columnMasterMap.get("instrument_run_id"));
            colList.add(columnMasterMap.get("retention_time"));
            colList.add(columnMasterMap.get("response"));
            colList.add(columnMasterMap.get("molecular_weight"));
            colList.add(columnMasterMap.get("desorp_efficiency"));
        } else {
            colList.add(columnMasterMap.get("raw_value1"));
            colList.add(columnMasterMap.get("final_value"));
            colList.add(columnMasterMap.get("expected_value"));
        }
        
        return colList;
    }
*/    
    private ArrayList<DictionaryDO> getStatuses() {
        ArrayList<DictionaryDO> statusDOs;

        statusDOs = new ArrayList<DictionaryDO>();
        try {
            statusDOs = dictLocal().fetchByCategorySystemName("analysis_status");
        } catch (Exception anyE) {
            System.out.println(anyE.getMessage());
        }
        
        return statusDOs; 
    }
    
    private HashMap<Integer,String> getStatusMap() {
        int                     i;
        ArrayList<DictionaryDO> statusDOs;
        HashMap<Integer,String> statusMap;
        
        statusDOs = getStatuses();
        statusMap = new HashMap<Integer,String>();
        for (i = 0; i < statusDOs.size(); i++)
            statusMap.put(statusDOs.get(i).getId(), statusDOs.get(i).getEntry());

        return statusMap;
    }
    
    private String[] getStatusArray() {
        int                     i;
        ArrayList<DictionaryDO> statusDOs;
        String                  statuses[];
        DictionaryDO            dictDO;

        statusDOs = getStatuses();
        statuses = new String[statusDOs.size()];
        for (i = 0; i < statusDOs.size(); i++) {
            dictDO = statusDOs.get(i);
            statuses[i] = dictDO.getEntry();
        }

        return statuses;
    }
    
    private HashMap<String,String> loadNamesByCellReference(HSSFWorkbook wb) {
        int                    i;
        HSSFName               name;
        HashMap<String,String> names;
        
        names = new HashMap<String,String>();
        
        for (i = 0; i < wb.getNumberOfNames(); i++) {
            name = wb.getNameAt(i);
            names.put(name.getRefersToFormula(), name.getNameName());
        }
        
        return names;
    }
    
    private Cell getCellForName(HSSFSheet sheet, String name) {
        AreaReference aref;
        Cell          cell;
        CellReference cref[];
        HSSFName      cellName;

        cell     = null;
        cellName = sheet.getWorkbook().getName(name);
        if (cellName != null && !cellName.isDeleted()) {
            aref = new AreaReference(cellName.getRefersToFormula());
            cref = aref.getAllReferencedCells();
            cell = sheet.getRow(cref[0].getRow()).getCell((int)cref[0].getCol());
        }
        
        return cell;
    }
    
    private String getWorksheetOutputFileName(Integer worksheetNumber) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = sysVarLocal().fetchByName("worksheet_output_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }
        
        return dirName+"Worksheet"+worksheetNumber+".xls";
    }
    
    private String getWorksheetTemplateFileName(DictionaryViewDO formatVDO) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = sysVarLocal().fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        return dirName+"OEWorksheet"+formatVDO.getLocalAbbrev()+".xls";
    }
    
    private String getPositionNumber(int position, String format, Integer batchCapacity) {
        int    major, minor;
        String positionNumber;
        
        positionNumber = "";
        if ("wsheet_num_format_batch".equals(format)) {
            major = getPositionMajorNumber(position, batchCapacity);
            minor = getPositionMinorNumber(position, batchCapacity);
            positionNumber = major+"-"+minor;
        } else {
            positionNumber = String.valueOf(position);
        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
    private int getPositionMajorNumber(int position, Integer batchCapacity) {
        return (int) (position / (double)batchCapacity + .99);
    }

    /**
      * Parses the position number and returns the minor number
      * for batch numbering.
      */
    private int getPositionMinorNumber(int position, Integer batchCapacity) {
        return position - (getPositionMajorNumber(position, batchCapacity) - 1) * batchCapacity;
    }

    private String formatTooltip(String ranges[]) {
        int          i;
        StringBuffer tooltip;
        
        tooltip = new StringBuffer();
        for (i = 0; i < ranges.length; i++) {
            if (tooltip.length() > 0)
                tooltip.append("\n");
            tooltip.append(ranges[i]);
        }
        
        return tooltip.toString();
    }
/*
    private String formatCellFormula(Row row, FormatColumn column, ArrayList<FormatColumn> columnList) throws Exception {
        int          i, j;
        String       eColumns[];
        FormatColumn tempColumn;
        
        eColumns = (String[]) column.getEquationColumns().clone();
        for (i = 0; i < eColumns.length; i++) {
            tempColumn = columnMasterMap.get(eColumns[i]);
            if (tempColumn != null) {
                j = columnList.indexOf(tempColumn);
                if (j == -1) {
                    columnList.add(tempColumn);
                    j = columnList.size() - 1;
                }
            } else {
                throw new Exception("Invalid column name in equation: '"+eColumns[i]+"'");
            }
            eColumns[i] = CellReference.convertNumToColString(9+j)+(row.getRowNum()+1);
        }
        
        return String.format(column.getEquation(), eColumns);
    }
*/    
    private String formatValue(TestResultDO testResultDO, String value) {
        DictionaryViewDO typeVDO;

        try {
            typeVDO = dictLocal().fetchById(testResultDO.getTypeId());
            if ("test_res_type_alpha_upper".equals(typeVDO.getSystemName()))
                value = value.toUpperCase();
            else if("test_res_type_alpha_lower".equals(typeVDO.getSystemName()))
                value =  value.toLowerCase();
        } catch (Exception anyE) {
            System.out.println(anyE.getMessage());
            value = "ERROR";
        }
        
        return value;
    }

    private Object getValueFromCellByCoords(HSSFSheet sheet, int row, int col) {
        Object           value;
        Cell             cell;
        FormulaEvaluator eval;

        value = null;
        cell = sheet.getRow(row).getCell(col);
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_ERROR:
                    value = null;
                    break;
                    
                case Cell.CELL_TYPE_FORMULA:
                    eval = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                    switch (eval.evaluateFormulaCell(cell)) {
                        case Cell.CELL_TYPE_ERROR:
                            value = null;
                            break;
                            
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell))
                                value = cell.getDateCellValue();
                            else
                                value = cell.getNumericCellValue();
                            break;
                            
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            if (((String)value).length() == 0)
                                value = null;
                            break;
                    }
                    break;
                    
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        value = cell.getDateCellValue();
                    else
                        value = cell.getNumericCellValue();
                    break;
                
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    if (((String)value).length() == 0)
                        value = null;
                    break;
            }
        }
        
        return value;
    }
    
    private Object getValueFromCellByName(HSSFSheet sheet, String name) {
        Object           value;
        Cell             cell;
        FormulaEvaluator eval;

        value = null;
        cell  = getCellForName(sheet, name);
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_ERROR:
                    value = null;
                    break;
                    
                case Cell.CELL_TYPE_FORMULA:
                    eval = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                    switch (eval.evaluateFormulaCell(cell)) {
                        case Cell.CELL_TYPE_ERROR:
                            value = null;
                            break;
                            
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell))
                                value = cell.getDateCellValue();
                            else
                                value = cell.getNumericCellValue();
                            break;
                            
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            if (((String)value).length() == 0)
                                value = null;
                            break;
                    }
                    break;
                    
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        value = cell.getDateCellValue();
                    else
                        value = cell.getNumericCellValue();
                    break;
                    
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    if (((String)value).length() == 0)
                        value = null;
                    break;
            }
        }
        
        return value;
    }
/*    
    class FormatColumn {
        String name, equation, eColumns[];
        
        FormatColumn(String name, String equation, String eColumns[]) {
            this.name = name;
            this.equation = equation;
            this.eColumns = eColumns;
        }
        
        String getName() {
            return name;
        }
        
        String getEquation() {
            return equation;
        }
        
        String[] getEquationColumns() {
            return eColumns;
        }
    }
*/
}