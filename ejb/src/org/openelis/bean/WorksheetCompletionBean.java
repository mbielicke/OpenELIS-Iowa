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
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

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
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.QcLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SectionLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AnalysisUserManager;
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
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-update")

public class WorksheetCompletionBean implements WorksheetCompletionRemote {

    @EJB
    AnalyteLocal analyteLocal;
    @EJB
    AnalyteParameterLocal analyteParameterLocal;
    @EJB
    DictionaryLocal dictionaryLocal;
    @EJB
    QcLocal qcLocal;
    @EJB
    QcAnalyteLocal qcAnalyteLocal;
    @EJB
    SampleManagerLocal sampleManagerLocal;
    @EJB
    SectionLocal sectionLocal;
    @EJB
    SystemVariableLocal systemVariableLocal;
    @EJB
    WorksheetAnalysisLocal worksheetAnalysisLocal;

    private HashMap<String,CellStyle>    styles;

    @TransactionTimeout(600)
    public WorksheetManager saveForEdit(WorksheetManager manager) throws Exception {
        boolean                  isEditable;
        int                      r, i, a, o;
        String                   statuses[], cellNameIndex, posNum, outFileName;
        File                     outFile;
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
        Row                      row, oRow, tRow;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisViewDO           aVDO;
        DictionaryViewDO         formatVDO;
        QcViewDO                 qcVDO;
        SampleDataBundle         bundle;
        SampleDomainInt          sDomain;
        SampleItemManager        siManager;
        SampleManager            sManager;
        SystemUserVO             userVO;
        WorksheetAnalysisDO      waDO, waLinkDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetResultManager   wrManager;

        outFileName = getWorksheetOutputFileName(manager.getWorksheet().getId(),
                                                 manager.getWorksheet().getSystemUserId());
        outFile = new File(outFileName);
        if (outFile.exists())
            throw new Exception("An Excel file for this worksheet already exists, please delete it before trying to export");

        try {
            formatVDO = dictionaryLocal.fetchById(manager.getWorksheet().getFormatId());
        } catch (NotFoundException nfE) {
            formatVDO = new DictionaryViewDO();
            formatVDO.setEntry("DefaultTotal");
            formatVDO.setSystemName("wformat_total");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: "+anyE.getMessage());
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatVDO));
        } catch (FileNotFoundException fnfE) {
            throw new Exception("Error loading template file: "+fnfE.getMessage());
        }
        
        try {
            wb = new HSSFWorkbook(in, true);
        } catch (IOException ioE) {
            throw new Exception("Error loading workbook from template file: "+ioE.getMessage());
        }

        statusMap = getStatusMap();
        statuses  = getStatusArray();
        
        createStyles(wb);
        tCellNames = loadNamesByCellReference(wb);

        resultSheet = wb.getSheet("Worksheet");

        tRow = resultSheet.getRow(1);
        resultSheet.removeRow(tRow);
        
        overrideSheet = wb.getSheet("Overrides");

        r = 1;
        o = 1;
        for (i = 0; i < manager.getItems().count(); i++) {
            wiDO      = manager.getItems().getWorksheetItemAt(i);
            waManager = manager.getItems().getWorksheetAnalysisAt(i);

            for (a = 0; a < waManager.count(); a++) {
                waDO = waManager.getWorksheetAnalysisAt(a);
                
                if (waDO.getWorksheetAnalysisId() != null) {
                    waLinkDO = worksheetAnalysisLocal.fetchById(waDO.getWorksheetAnalysisId());
                } else {
                    waLinkDO = null;
                }

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
                cell.setCellValue(waDO.getAccessionNumber());
                
                cellNameIndex = i+"."+a;
                if (waDO.getAnalysisId() != null) {
                    isEditable = "N".equals(waDO.getIsFromOther());
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
                    if (waLinkDO != null)
                        cell.setCellValue(waLinkDO.getAccessionNumber());
                    else
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
                    if (isEditable)
                        cell.setCellStyle(styles.get("row_edit"));
                    else
                        cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(statusMap.get(aVDO.getStatusId()));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_status."+i+"."+a);
                    cellName.setRefersToFormula("Worksheet!$"+CellReference.convertNumToColString(6)+
                                                "$"+(row.getRowNum()+1));

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
                                                       tCellNames, aVDO.getTestId(),
                                                       arManager, wrManager, isEditable);
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
                    cell.setCellValue(waDO.getAccessionNumber());

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
                    
                    // users (override)
                    cell = oRow.createCell(5);
                    if (isEditable)
                        cell.setCellStyle(styles.get("row_edit"));
                    else
                        cell.setCellStyle(styles.get("row_no_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_users."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(5)+
                                                "$"+(oRow.getRowNum()+1));
                    
                    // started (override)
                    cell = oRow.createCell(6);
                    if (isEditable)
                        cell.setCellStyle(styles.get("datetime_edit"));
                    else
                        cell.setCellStyle(styles.get("datetime_no_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_started."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(6)+
                                                "$"+(oRow.getRowNum()+1));

                    // completed (override)
                    cell = oRow.createCell(7);
                    if (isEditable)
                        cell.setCellStyle(styles.get("datetime_edit"));
                    else
                        cell.setCellStyle(styles.get("datetime_no_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_completed."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(7)+
                                                "$"+(oRow.getRowNum()+1));
                    o++;
                } else if (waDO.getQcId() != null) {
                    qcVDO = qcLocal.fetchById(waDO.getQcId());

                    // description
                    cell = row.createCell(2);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(qcVDO.getName());
    
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
                                                         waDO.getQcId(), wqrManager);
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
                    cell.setCellValue(waDO.getAccessionNumber());

                    // description (override)
                    cell = oRow.createCell(2);
                    cell.setCellStyle(styles.get("row_no_edit"));
                    cell.setCellValue(qcVDO.getName());
    
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
                    try {
                        userVO = EJBFactory.getUserCache().getSystemUser(waDO.getQcSystemUserId());
                        if (userVO != null)
                            cell.setCellValue(userVO.getLoginName());
                    } catch (Exception anyE) {
                        System.out.println("Error loading QC System User: "+anyE.getMessage());
                    }
                    cellName = wb.createName();
                    cellName.setNameName("analysis_users."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(5)+
                                                "$"+(oRow.getRowNum()+1));
                    
                    // started (override)
                    cell = oRow.createCell(6);
                    cell.setCellStyle(styles.get("datetime_edit"));
                    if (waDO.getQcStartedDate() != null)
                        cell.setCellValue(waDO.getQcStartedDate().toString());
                    cellName = wb.createName();
                    cellName.setNameName("analysis_started."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(6)+
                                                "$"+(oRow.getRowNum()+1));

                    // completed (override)
                    cell = oRow.createCell(7);
                    cell.setCellStyle(styles.get("datetime_no_edit"));
                    cellName = wb.createName();
                    cellName.setNameName("analysis_completed."+cellNameIndex);
                    cellName.setRefersToFormula("Overrides!$"+CellReference.convertNumToColString(7)+
                                                "$"+(oRow.getRowNum()+1));
                    o++;
                }
            }
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
            Runtime.getRuntime().exec("chmod go+rw "+outFileName);
        } catch (Exception anyE) {
            throw new Exception("Error writing Excel file: "+anyE.getMessage());
        }

        return manager;
    }

    @TransactionTimeout(600)
    public WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception {
        boolean                  anaModified, editLocked, newSampleLock, permLocked,
                                 statusLocked;
        int                      a, i, c, r, s, rowIndex;
        Object                   value;
        Integer                  anCancelledId, anCompletedId, anInitiatedId, anInPrepId,
                                 anLoggedInId, anOnHoldId, anReleasedId, anRequeueId, 
                                 testResultId;
        String                   userToken;
        ArrayList<DictionaryDO>  statusList;
        HashMap<Integer,String>  statusMap;
        Iterator<SampleManager>  iter;
        File                     file;
        FileInputStream          in;
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
        SectionPermission        perm;
        SectionViewDO            sectionVDO;
        SystemUserVO             userVO;
        TestResultDO             trDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemManager     wiManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetQcResultViewDO  wqrVDO;
        WorksheetResultManager   wrManager;
        WorksheetResultViewDO    wrVDO;
        
        anCancelledId = dictionaryLocal.fetchBySystemName("analysis_cancelled").getId();
        anCompletedId = dictionaryLocal.fetchBySystemName("analysis_completed").getId();
        anInitiatedId = dictionaryLocal.fetchBySystemName("analysis_initiated").getId();
        anInPrepId = dictionaryLocal.fetchBySystemName("analysis_inprep").getId();
        anLoggedInId = dictionaryLocal.fetchBySystemName("analysis_logged_in").getId();
        anOnHoldId = dictionaryLocal.fetchBySystemName("analysis_on_hold").getId();
        anRequeueId = dictionaryLocal.fetchBySystemName("analysis_requeue").getId();
        anReleasedId = dictionaryLocal.fetchBySystemName("analysis_released").getId();
        
        errorList = new ValidationErrorsList();
        file = new File(getWorksheetOutputFileName(manager.getWorksheet().getId(),
                                                   manager.getWorksheet().getSystemUserId()));
        in         = new FileInputStream(file);
        wb         = new HSSFWorkbook(in);
        statusList = getStatuses();
        statusMap  = getStatusMap();

        r = 0;
        rowIndex = 1;
        wiManager = manager.getItems();
        for (i = 0; i < wiManager.count(); i++) {
            wiDO = wiManager.getWorksheetItemAt(i);
            waManager = wiManager.getWorksheetAnalysisAt(i);
            for (a = 0; a < waManager.count(); a++) {
                //
                // increment rowIndex if there were no result records for the
                // previous analysis
                if (r == 0 && a != 0)
                    rowIndex++;

                waDO = waManager.getWorksheetAnalysisAt(a);
                if (waDO.getAnalysisId() != null) {
                    anaModified = false;
                    editLocked = "Y".equals(waDO.getIsFromOther());

                    bundle = waManager.getBundleAt(a);
                    newBundle = lockManagerIfNeeded(manager, waDO, bundle);
                    if (newBundle != null) {
                        waManager.setBundleAt(newBundle, a);
                        bundle = newBundle;
                        newSampleLock = true;
                    } else {
                        newSampleLock = false;
                    }
                    
                    sManager = bundle.getSampleManager();
                    siManager = sManager.getSampleItems();
                    aManager = siManager.getAnalysisAt(bundle.getSampleItemIndex());
                    aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                    arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());
                    wrManager = waManager.getWorksheetResultAt(a);

                    if (anInPrepId.equals(aVDO.getStatusId()) ||
                        anReleasedId.equals(aVDO.getStatusId()) ||
                        anCancelledId.equals(aVDO.getStatusId()))
                        statusLocked = true;
                    else
                        statusLocked = false;

                    sectionVDO = sectionLocal.fetchById(aVDO.getSectionId());
                    perm = EJBFactory.getUserCache().getPermission().getSection(sectionVDO.getName());
                    if (perm == null || !perm.hasCompletePermission())
                        permLocked = true;
                    else
                        permLocked = false;
                    
                    if (!statusLocked && !permLocked && !editLocked) {
                        value = getValueFromCellByName(wb.getSheet("Worksheet"), "analysis_status."+i+"."+a);
                        if (!statusMap.get(aVDO.getStatusId()).equals(value)) {
                            for (s = 0; s < statusList.size(); s++) {
                                statusDO = statusList.get(s);
                                if (statusDO.getEntry().equals(value)) {
                                    if (!anLoggedInId.equals(statusDO.getId()) &&
                                        !anInitiatedId.equals(statusDO.getId()) &&            
                                        !anOnHoldId.equals(statusDO.getId()) &&
                                        !anRequeueId.equals(statusDO.getId()) &&
                                        !anCompletedId.equals(statusDO.getId())) {
                                        errorList.add(new FormErrorException("invalidAnalysisStatusChange",
                                                      (String)value, String.valueOf(wiDO.getPosition()),
                                                      String.valueOf(a+1)));
                                    } else {
                                        aVDO.setStatusId(statusDO.getId());
                                        anaModified = true;
                                    }
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
                                try {
                                    userVO = EJBFactory.getUserCache().getSystemUser(userToken);
                                    if (userVO != null) {
                                        auManager.addCompleteRecord(userVO);
                                        anaModified = true;
                                    } else {
                                        errorList.add(new FormErrorException("illegalWorksheetUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                                    }
                                } catch (Exception anyE) {
                                    errorList.add(new FormErrorException("illegalWorksheetUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                                }
                            }
                        }
                        
                        value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_started."+i+"."+a);
                        if (value != null) {
                            aVDO.setStartedDate((Datetime)value);
                            anaModified = true;
                        }
                        
                        value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_completed."+i+"."+a);
                        if (value != null) {
                            aVDO.setCompletedDate((Datetime)value);
                            anaModified = true;
                        }
                        
                        for (r = 0; r < wrManager.count(); r++, rowIndex++) {
                            wrVDO = wrManager.getWorksheetResultAt(r);
                            for (c = 0; c < 30; c++) {
                                value = getValueFromCellByCoords(wb.getSheet("Worksheet"), rowIndex, 9 + c);
                                if (value != null && !value.equals(wrVDO.getValueAt(c))) {
                                    wrVDO.setValueAt(c, value.toString());
                                    anaModified = true;
                                }
                            }   
    
                            // only save data back to the analysis record if the
                            // status has not been changed to Requeue
                            if (!anRequeueId.equals(aVDO.getStatusId())) {
                                for (c = 0; c < arManager.getRowAt(wrVDO.getResultRow()).size(); c++) {
                                    rVDO = arManager.getResultAt(wrVDO.getResultRow(), c);
                                    try {
                                        if (c == 0) {
                                            value = getValueFromCellByName(wb.getSheet("Worksheet"), "analyte_reportable."+i+"."+a+"."+r);
                                            if (value != null && !rVDO.getIsReportable().equals(value)) {
                                                rVDO.setIsReportable((String)value);
                                                anaModified = true;
                                            }
        
                                            value = getValueFromCellByName(wb.getSheet("Worksheet"), "final_value."+i+"."+a+"."+r);
                                        } else {
                                            try {
                                                aDO = analyteLocal.fetchById(rVDO.getAnalyteId());
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
                                            
                                            rVDO.setTestResultId(testResultId);
                                            rVDO.setTypeId(trDO.getTypeId());
                                            rVDO.setValue(arManager.formatResultValue(rVDO.getResultGroup(),
                                                                                      aVDO.getUnitOfMeasureId(),
                                                                                      testResultId,
                                                                                      value.toString()));
                                            manager.addReflexBundle(bundle, rVDO);
                                            anaModified = true;
                                        }
                                    } catch (ParseException parE) {
                                        errorList.add(new FormErrorException("illegalResultValueFormException",
                                                                             String.valueOf(value),
                                                                             String.valueOf(wiDO.getPosition()),
                                                                             wrVDO.getAnalyteName(),
                                                                             (c == 0 ? "Final Value" : rVDO.getAnalyte())));
                                    }
                                }
                            }
                        }
                        if (!anaModified && newSampleLock) {
                            sampleManagerLocal.abortUpdate(sManager.getSample().getId());
                            manager.getLockedManagers().remove(sManager.getSample().getAccessionNumber());
                        }
                    } else {
                        //
                        // increment rowIndex and r since we skipped running through
                        // the result records due to permissions or status
                        //
                        r = wrManager.count();
                        rowIndex += r;
                        
                        if (newSampleLock) {
                            sampleManagerLocal.abortUpdate(sManager.getSample().getId());
                            manager.getLockedManagers().remove(sManager.getSample().getAccessionNumber());
                        }
                    }
                } else if (waDO.getQcId() != null) {
                    wqrManager = waManager.getWorksheetQcResultAt(a);
                    for (r = 0; r < wqrManager.count(); r++, rowIndex++) {
                        wqrVDO = wqrManager.getWorksheetQcResultAt(r);
                        for (c = 0; c < 30; c++) {
                            value = getValueFromCellByCoords(wb.getSheet("Worksheet"), rowIndex, 9 + c);
                            if (value != null)
                                wqrVDO.setValueAt(c, value.toString());
                        }
                    }

                    if (waDO.getQcSystemUserId() == null) {
                        value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_users."+i+"."+a);
                        if (value != null) {
                            tokenizer = new StringTokenizer((String)value, ",");
                            if (tokenizer.hasMoreTokens()) {
                                userToken = tokenizer.nextToken();
                                try {
                                    userVO = EJBFactory.getUserCache().getSystemUser(userToken);
                                    if (userVO != null) {
                                        waDO.setQcSystemUserId(userVO.getId());
                                    } else {
                                        errorList.add(new FormErrorException("illegalWorksheetUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                                    }
                                } catch (Exception anyE) {
                                    errorList.add(new FormErrorException("illegalWorksheetUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                                }
                            }
                        } else {
                            try {
                                userVO = EJBFactory.getUserCache().getSystemUser();
                                waDO.setQcSystemUserId(userVO.getId());
                            } catch (Exception anyE) {
                                errorList.add(new FormErrorException("defaultWorksheetQcUserFormException", String.valueOf(wiDO.getPosition()), String.valueOf(a+1)));
                            }
                        }
                    }
                    
                    value = getValueFromCellByName(wb.getSheet("Overrides"), "analysis_started."+i+"."+a);
                    if (value != null && waDO.getQcStartedDate() == null)
                        waDO.setQcStartedDate((Datetime)value);
                }
            }
            //
            // increment rowIndex if there were no result records for the
            // last analysis or there were no analyses for this item
            if (r == 0)
                rowIndex++;
        }
        
        if (errorList.getErrorList().size() > 0) {
            // abort update on any samples we may have locked when loading data
            iter = manager.getLockedManagers().values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                sampleManagerLocal.abortUpdate(sManager.getSample().getId());
            }
            manager.getLockedManagers().clear();
            
            throw errorList;
        }
        
        file.delete();
        
        return manager;
    }

    @TransactionTimeout(600)
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager manager) throws Exception {
        int                 i;
        ArrayList<IdNameVO> headers;
        DictionaryViewDO    formatVDO;
        FileInputStream     in;
        HSSFWorkbook        wb;
        Row                 hRow;

        headers = new ArrayList<IdNameVO>();
        
        try {
            formatVDO = dictionaryLocal.fetchById(manager.getWorksheet().getFormatId());
        } catch (NotFoundException nfE) {
            formatVDO = new DictionaryViewDO();
            formatVDO.setEntry("DefaultTotal");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: "+anyE.getMessage());
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatVDO));
        } catch (FileNotFoundException fnfE) {
            throw new Exception("Error loading template file: "+fnfE.getMessage());
        }
        
        try {
            wb = new HSSFWorkbook(in, true);
        } catch (IOException ioE) {
            throw new Exception("Error loading workbook from template file: "+ioE.getMessage());
        }

        hRow = wb.getSheet("Worksheet").getRow(0);
        for (i = 0; i < hRow.getLastCellNum() && i < 39; i++)
            headers.add(new IdNameVO(i, hRow.getCell(i).getStringCellValue()));
        
        return headers;
    }
    
    private void createStyles(HSSFWorkbook wb) {
        CellStyle      dateTimeEditStyle, dateTimeNoEditStyle, headerStyle,
                       rowEditStyle, rowNoEditStyle;
        CreationHelper helper;
        Font           font;

        helper = wb.getCreationHelper();
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
        
        dateTimeEditStyle = wb.createCellStyle();
        dateTimeEditStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd hh:mm"));
        rowEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowEditStyle.setLocked(false);
        styles.put("datetime_edit", dateTimeEditStyle);

        dateTimeNoEditStyle = wb.createCellStyle();
        dateTimeNoEditStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd hh:mm"));
        rowNoEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowNoEditStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        rowNoEditStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        rowNoEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowNoEditStyle.setLocked(true);
        styles.put("datetime_no_edit", dateTimeNoEditStyle);
    }

    private int createResultCellsForFormat(HSSFSheet sheet, Row row, Row tRow, String nameIndexPrefix,
                                           HashMap<String,String> cellNames, Integer testId,
                                           AnalysisResultManager arManager, WorksheetResultManager wrManager,
                                           boolean isEditable) {
        int                    c, i, r;
        Integer                resultTypeDictionary;
        String                 cellNameIndex, name;
        Cell                   cell, tCell;
        Name                   cellName;
        AnalyteDO              aDO;
        AnalyteParameterViewDO apVDO;
        DictionaryViewDO       dVDO;
        ResultViewDO           rVDO;
        WorksheetResultViewDO  wrVDO;
        
        r = row.getRowNum();
        
        try {
            resultTypeDictionary = dictionaryLocal.fetchBySystemName("test_res_type_dictionary").getId();
        } catch (Exception anyE) {
            // TODO: Code proper exception handling
            anyE.printStackTrace();
            return r;
        }

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
            if (isEditable)
                cell.setCellStyle(styles.get("row_edit"));
            else
                cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(rVDO.getIsReportable());
            cellName = sheet.getWorkbook().createName();
            cellName.setNameName("analyte_reportable."+cellNameIndex);
            cellName.setRefersToFormula("Worksheet!$"+CellReference.convertNumToColString(8)+
                                        "$"+(row.getRowNum()+1));
            
            apVDO = null;
            for (c = 9; c < tRow.getLastCellNum() && c < 39; c++) {
                tCell = tRow.getCell(c);
                
                cell = row.createCell(c);
                if (isEditable)
                    cell.setCellStyle(tCell.getCellStyle());
                else
                    cell.setCellStyle(styles.get("row_no_edit"));
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
                } else {
                    setCellValue(cell, wrVDO.getValueAt(c-9));
                }
                if ("p_1".equals(name) || "p_2".equals(name) || "p_3".equals(name)) {
                    if (wrVDO.getValueAt(c-9) == null) {
                        if (apVDO == null) {
                            try {
                                apVDO = analyteParameterLocal.fetchActiveByAnalyteIdReferenceIdReferenceTableId(wrVDO.getAnalyteId(),
                                                                                                                testId,
                                                                                                                ReferenceTable.TEST);
                            } catch (Exception anyE) {
                                // TODO: Code proper exception handling
                                anyE.printStackTrace();
                                continue;
                            }
                        }
                        
                        if ("p_1".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP1()));
                        } else if ("p_2".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP2()));
                        } else if ("p_3".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP3()));
                        }
                    }
                }
            }
            
            for (c = 0; c < arManager.getRowAt(wrVDO.getResultRow()).size(); c++) {
                rVDO = arManager.getResultAt(wrVDO.getResultRow(), c);
                try {
                    aDO = analyteLocal.fetchById(rVDO.getAnalyteId());
                    if (!"Y".equals(rVDO.getIsColumn()))
                        cellName = sheet.getWorkbook().getName("final_value."+cellNameIndex);
                    else
                        cellName = sheet.getWorkbook().getName(aDO.getExternalId()+"."+cellNameIndex);

                    if (cellName != null && !cellName.isDeleted()) {
                        cell = getCellForName(sheet, cellName.getNameName());
                        if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                            if (resultTypeDictionary.equals(rVDO.getTypeId())) {
                                dVDO = dictionaryLocal.fetchById(Integer.valueOf(rVDO.getValue()));
                                cell.setCellValue(dVDO.getEntry());
                            } else {
                                setCellValue(cell, rVDO.getValue());
                            }
                        }
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
                                             Integer qcId, WorksheetQcResultManager wqrManager) {
        int                     c, i, r;
        Object                  value;
        String                  cellNameIndex, name;
        Cell                    cell, tCell;
        Name                    cellName;
        AnalyteParameterViewDO  apVDO;
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
            
            apVDO = null;
            for (c = 9; c < tRow.getLastCellNum() && c < 39; c++) {
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
                } else {
                    setCellValue(cell, wqrVDO.getValueAt(c-9));
                }
                if ("p_1".equals(name) || "p_2".equals(name) || "p_3".equals(name)) {
                    if (wqrVDO.getValueAt(c-9) == null) {
                        if (apVDO == null) {
                            try {
                                apVDO = analyteParameterLocal.fetchActiveByAnalyteIdReferenceIdReferenceTableId(wqrVDO.getAnalyteId(),
                                                                                                                qcId,
                                                                                                                ReferenceTable.QC);
                            } catch (Exception anyE) {
                                // TODO: Code proper exception handling
                                anyE.printStackTrace();
                                continue;
                            }
                        }
                        
                        if ("p_1".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP1()));
                        } else if ("p_2".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP2()));
                        } else if ("p_3".equals(name)) {
                            setCellValue(cell, String.valueOf(apVDO.getP3()));
                        }
                    }
                }
            }
            
            try {
                qcaVDO = qcAnalyteLocal.fetchById(wqrVDO.getQcAnalyteId());
                cellName = sheet.getWorkbook().getName("expected_value."+cellNameIndex);
                if (cellName != null && !cellName.isDeleted()) {
                    cell = getCellForName(sheet, cellName.getNameName());
                    if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                        value = getCellValue(cell);
                        if (value == null)
                            setCellValue(cell, qcaVDO.getValue());
                        else if (value instanceof String && ((String)value).length() == 0)
                            setCellValue(cell, qcaVDO.getValue());
                        else if (value instanceof Double && ((Double)value).doubleValue() == 0.0)
                            setCellValue(cell, qcaVDO.getValue());
                    }
                }
            } catch (Exception anyE) {
                // TODO: Code proper exception handling
                anyE.printStackTrace();
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
    
    private ArrayList<DictionaryDO> getStatuses() {
        ArrayList<DictionaryDO> statusDOs;

        statusDOs = new ArrayList<DictionaryDO>();
        try {
            statusDOs = dictionaryLocal.fetchByCategorySystemName("analysis_status");
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
    
    private String getWorksheetOutputFileName(Integer worksheetNumber, Integer userId) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        SystemUserVO                userVO;
        
        dirName = "";
        try {
            sysVars = systemVariableLocal.fetchByName("worksheet_output_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        userVO = null;
        try {
            userVO = EJBFactory.getUserCache().getSystemUser(userId);
        } catch (Exception anyE) {
            throw new Exception("Error retrieving username for worksheet: "+anyE.getMessage());
        }
        
        return dirName+worksheetNumber+"_"+userVO.getLoginName()+".xls";
    }
    
    private String getWorksheetTemplateFileName(DictionaryViewDO formatVDO) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = systemVariableLocal.fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        return dirName+"OEWorksheet"+formatVDO.getEntry()+".xls";
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

    private Object getValueFromCellByCoords(HSSFSheet sheet, int row, int col) {
        return getCellValue(sheet.getRow(row).getCell(col));
    }
    
    private Object getValueFromCellByName(HSSFSheet sheet, String name) {
        return getCellValue(getCellForName(sheet, name));
    }
    
    private Object getCellValue(Cell cell) {
        Object           value;
        FormulaEvaluator eval;

        value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_ERROR:
                    value = null;
                    break;
                    
                case Cell.CELL_TYPE_FORMULA:
                    eval = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    switch (eval.evaluateFormulaCell(cell)) {
                        case Cell.CELL_TYPE_ERROR:
                            value = null;
                            break;
                            
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell))
                                value = new Datetime(Datetime.YEAR, Datetime.MINUTE, cell.getDateCellValue());
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
                        value = new Datetime(Datetime.YEAR, Datetime.MINUTE, cell.getDateCellValue());
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
    
    private SampleDataBundle lockManagerIfNeeded(WorksheetManager manager, WorksheetAnalysisDO waDO,
                                                 SampleDataBundle bundle) throws Exception {
        int                     i, j;
        String                  params[];
        Iterator<SampleManager> iter;
        AnalysisManager         aManager;
        AnalysisViewDO          aVDO;
        SampleDataBundle        newBundle;
        SampleItemManager       siManager;
        SampleManager           sManager, tempManager;
        
        newBundle = null;
        sManager = bundle.getSampleManager();
        if (!manager.getLockedManagers().containsKey(sManager.getSample().getAccessionNumber())) {
            try {
                sManager = sampleManagerLocal.fetchForUpdate(sManager.getSample().getId());
                manager.getLockedManagers().put(sManager.getSample().getAccessionNumber(), sManager);
                manager.getSampleManagers().put(sManager.getSample().getAccessionNumber(), sManager);
                siManager = sManager.getSampleItems();
                items: for (i = 0; i < siManager.count(); i++) {
                    aManager = siManager.getAnalysisAt(i);
                    for (j = 0; j < aManager.count(); j++) {
                        aVDO = aManager.getAnalysisAt(j);
                        if (waDO.getAnalysisId().equals(aVDO.getId())) {
                            newBundle = aManager.getBundleAt(j);
                            break items;
                        }
                    }
                }
            } catch (Exception anyE) {
                // abort update on any samples we may have locked when loading data
                iter = manager.getLockedManagers().values().iterator();
                while (iter.hasNext()) {
                    tempManager = (SampleManager) iter.next();
                    sampleManagerLocal.abortUpdate(tempManager.getSample().getId());
                }
                manager.getLockedManagers().clear();
                if (anyE instanceof EntityLockedException) {
                    params = new String[3];
                    params[0] = sManager.getSample().getAccessionNumber().toString();
                    params[1] = ((EntityLockedException)anyE).getParams()[0];
                    params[2] = ((EntityLockedException)anyE).getParams()[1];
                    throw new LocalizedException("worksheetSampleLockException", params);
                } else {
                    throw anyE;
                }
            }
        }
        return newBundle;
    }
}