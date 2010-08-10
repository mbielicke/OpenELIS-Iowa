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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.naming.InitialContext;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
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
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
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
import org.openelis.util.UTFResource;
import org.openelis.utilcommon.ResultValidator;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-update")
public class WorksheetCompletionBean implements WorksheetCompletionRemote {

    private static final String OPENELIS_CONSTANTS = "openelis-common/org.openelis.constants.OpenELISConstants";

    public WorksheetCompletionBean() {
    }

    public WorksheetManager saveForEdit(WorksheetManager manager) throws Exception {
        int                       r, i, a, n, c, itemMergeStart, anaMergeStart;
        Integer                   testId, groupId;
        FileOutputStream          out;
        HashMap<Integer,String>   statusMap;
        HashMap<String,CellStyle> styles;
        HashMap<Integer,HashMap<Integer,CellAttributes>> cellAttributes;
        Iterator<Integer>         testIter, groupIter;
        String                    statuses[], cellNameIndex;
        Cell                      cell;
        CellRangeAddressList      statusColumn, rawColumn;
        DVConstraint              statusConstraint, rawConstraint1, rawConstraint2;
        HSSFDataValidation        statusValidation, rawValidation;
        HSSFSheet                 sheet;
        HSSFWorkbook              wb;
        Name                      cellName;
        Row                       row;
        AnalysisManager           aManager;
        AnalysisResultManager     arManager;
        AnalysisViewDO            aVDO;
        DictionaryViewDO          formatVDO;
        QcManager                 qcManager;
        ResultValidator           validator;
        SampleDataBundle          bundle;
        SampleDomainInt           sDomain;
        SampleItemManager         siManager;
        SampleItemViewDO          siVDO;
        SampleManager             sManager;
        WorksheetAnalysisDO       waDO;
        WorksheetAnalysisManager  waManager;
        WorksheetItemDO           wiDO;
        WorksheetQcResultManager  wqrManager;
        WorksheetQcResultViewDO   wqrVDO;
        WorksheetResultManager    wrManager;
        WorksheetResultViewDO     wrVDO;

        cellAttributes = new HashMap<Integer,HashMap<Integer,CellAttributes>>();
        
        formatVDO = dictLocal().fetchById(manager.getWorksheet().getFormatId());

        i         = 0;
        wb        = new HSSFWorkbook();
        statusMap = getStatusMap();
        statuses  = getStatusArray();
        
        sheet = wb.createSheet("Worksheet");
        sheet.protectSheet("xyzzy");
        sheet.createFreezePane(8, 1);

        styles = createStyles(wb);
        //
        // Create header row for main sheet
        //
        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Position");

        cell = row.createCell(1);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Accession #");

        cell = row.createCell(2);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Description");

        cell = row.createCell(3);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("QC Link");

        cell = row.createCell(4);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Test");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Method");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Status");

        cell = row.createCell(7);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Analyte");

        createHeaderCellsForFormat(row, "format1", styles);

        r = 1;
        for (i = 0; i < manager.getItems().count(); i++) {
            itemMergeStart = r;
            wiDO           = manager.getItems().getWorksheetItemAt(i);
            waManager      = manager.getItems().getWorksheetAnalysisAt(i);

            for (a = 0; a < waManager.count(); a++) {
                anaMergeStart = r;
                waDO          = waManager.getWorksheetAnalysisAt(a);

                row = sheet.createRow(r);

                // position number
                cell = row.createCell(0);
                cell.setCellStyle(styles.get("row_no_edit"));
                if (a == 0) {
                    cell.setCellValue(getPositionNumber(wiDO.getPosition(),
                                                        formatVDO.getSystemName(),
                                                        manager.getWorksheet().getBatchCapacity()));
                }
                
                // accession number
                cell = row.createCell(1);
                cell.setCellStyle(styles.get("row_no_edit"));
                cell.setCellValue(waDO.getAccessionNumber());
                
                if (waDO.getAnalysisId() != null) {
                    bundle = waManager.getBundleAt(a);
                    sManager = bundle.getSampleManager();
                    // TODO: Add code to lock the sample record if not already locked
                    sDomain = sManager.getDomainManager();
                    siManager = sManager.getSampleItems();
                    siVDO = siManager.getSampleItemAt(bundle.getSampleItemIndex());
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

                    wrManager = waManager.getWorksheetResultAt(a);
                    for (n = 0; n < wrManager.count(); n++, r++) {
                        wrVDO = wrManager.getWorksheetResultAt(n);
                        validator = arManager.getResultValidator(wrVDO.getResultGroup());
                        
                        createConstraint(cellAttributes, aVDO.getTestId(), wrVDO.getResultGroup(), validator);
                        
                        if (n != 0) {
                            row = sheet.createRow(r);
                            for (c = 0; c < 7; c++) {
                                cell = row.createCell(c);
                                cell.setCellStyle(styles.get("row_no_edit"));
                            }                            
                        }
                        
                        // analyte
                        cell = row.createCell(7);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        cell.setCellValue(wrVDO.getAnalyteName());
                        
                        cellNameIndex = i+"."+a+"."+n;
                        createResultCellsForFormat(wb, row, cellNameIndex, "format1", styles, cellAttributes, aVDO, wrVDO);
                    }
                    
                    if (n == 0) {
                        // analyte
                        cell = row.createCell(7);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        cell.setCellValue("NO ANALYTES DEFINED");
                        
                        createEmptyCellsForFormat(row, "format1", styles);
                        
                        r++;
                    }
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
                    cell.setCellStyle(styles.get("row_edit"));
                    cell.setCellValue("");

                    wqrManager = waManager.getWorksheetQcResultAt(a);
                    for (n = 0; n < wqrManager.count(); n++, r++) {
                        wqrVDO = wqrManager.getWorksheetQcResultAt(n);

                        if (n != 0) {
                            row = sheet.createRow(r);
                            for (c = 0; c < 7; c++) {
                                cell = row.createCell(c);
                                cell.setCellStyle(styles.get("row_no_edit"));
                            }                            
                        }
                        
                        // analyte
                        cell = row.createCell(7);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        cell.setCellValue(wqrVDO.getAnalyteName());
                        
                        cellNameIndex = i+"."+a+"."+n;
                        createQcResultCellsForFormat(wb, row, cellNameIndex, "format1", styles, cellAttributes, wqrVDO);
                    }
                    
                    if (n == 0) {
                        // analyte
                        cell = row.createCell(7);
                        cell.setCellStyle(styles.get("row_no_edit"));
                        cell.setCellValue("NO ANALYTES DEFINED");
                        
                        createEmptyCellsForFormat(row, "format1", styles);
                        
                        r++;
                    }
                }

                for (c = 3; c < 6; c++)
                    sheet.addMergedRegion(new CellRangeAddress(anaMergeStart, r - 1, c, c));
            }
            
            for (c = 0; c < 3; c++)
                sheet.addMergedRegion(new CellRangeAddress(itemMergeStart, r - 1, c, c));
        }
        
        //
        // Create validators
        //
        statusColumn = new CellRangeAddressList(1,sheet.getPhysicalNumberOfRows()-1,6,6);
        statusConstraint = DVConstraint.createExplicitListConstraint(statuses);
        statusValidation = new HSSFDataValidation(statusColumn,statusConstraint);
        statusValidation.setEmptyCellAllowed(true);
        statusValidation.setSuppressDropDownArrow(false);
        statusValidation.createPromptBox("Statuses", formatTooltip(statuses));
        statusValidation.setShowPromptBox(false);
        sheet.addValidationData(statusValidation);
        
//        rawColumn = new CellRangeAddressList(1,sheet.getPhysicalNumberOfRows()-1,8,8);
//        rawConstraint1 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
//                                                              DVConstraint.OperatorType.GREATER_OR_EQUAL,
//                                                              "0.0", null);
//        rawValidation = new HSSFDataValidation(rawColumn,rawConstraint1);
//        sheet.addValidationData(rawValidation);
//        rawConstraint2 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
//                                                              DVConstraint.OperatorType.LESS_THAN,
//                                                              "100.0", null);
//        rawValidation = new HSSFDataValidation(rawColumn,rawConstraint2);
//        sheet.addValidationData(rawValidation);

        testIter = cellAttributes.keySet().iterator();
        while (testIter.hasNext()) {
            testId = testIter.next();
            groupIter = cellAttributes.get(testId).keySet().iterator();
            while (groupIter.hasNext()) {
                groupId = groupIter.next();
                rawValidation = new HSSFDataValidation(cellAttributes.get(testId).get(groupId).cellRanges,
                                                       cellAttributes.get(testId).get(groupId).constraint);
//                rawValidation.createPromptBox("Suggestions", cellAttributes.get(testId).get(groupId).tooltip);
                sheet.addValidationData(rawValidation);
            }
        }
        
        for (c = 0; c < 11; c++)
            sheet.autoSizeColumn(c, true);
            
        sheet.groupColumn(2, 6);
        
        try {
            out = new FileOutputStream(getWorksheetTempDirectory()+"Worksheet"+manager.getWorksheet().getId()+".xls");
            wb.write(out);
            out.close();
            Runtime.getRuntime().exec("chmod go+rw "+getWorksheetTempDirectory()+"Worksheet"+manager.getWorksheet().getId()+".xls");
        } catch (IOException ioE) {
            System.out.println("Error writing Excel file: "+ioE.getMessage());
        }

        return manager;
    }

    public WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception {
        int             i, a, r;
        Object          value;
        Integer         testResultId;
        FileInputStream in;
//        ValidationErrorsList errorList;
        HSSFWorkbook    wb;
        AnalysisManager           aManager;
        AnalysisResultManager     arManager;
        AnalysisViewDO            aVDO;
        ResultViewDO              rVDO;
        SampleDataBundle          bundle;
        SampleItemManager         siManager;
        SampleItemViewDO          siVDO;
        SampleManager             sManager;
        TestResultDO             trDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager;
        WorksheetQcResultManager wqrManager;
        WorksheetQcResultViewDO  wqrVDO;
        WorksheetResultManager   wrManager;
        WorksheetResultViewDO    wrVDO;
        
//        errorList = new ValidationErrorsList();
        in = new FileInputStream(getWorksheetTempDirectory()+"Worksheet"+manager.getWorksheet().getId()+".xls");
        wb = new HSSFWorkbook(in);
        
        wiManager = manager.getItems();
        for (i = 0; i < wiManager.count(); i++) {
            wiDO = wiManager.getWorksheetItemAt(i);
            waManager = wiManager.getWorksheetAnalysisAt(i);
            for (a = 0; a < waManager.count(); a++) {
                waDO = waManager.getWorksheetAnalysisAt(a);
                if (waDO.getAnalysisId() != null) {
                    bundle = waManager.getBundleAt(a);
                    sManager = bundle.getSampleManager();
                    siManager = sManager.getSampleItems();
                    siVDO = siManager.getSampleItemAt(bundle.getSampleItemIndex());
                    aManager = siManager.getAnalysisAt(bundle.getSampleItemIndex());
                    aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                    arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());

                    wrManager = waManager.getWorksheetResultAt(a);
                    for (r = 0; r < wrManager.count(); r++) {
                        wrVDO = wrManager.getWorksheetResultAt(r);
                        
                        value = getValueFromCell(wb, "raw_value."+i+"."+a+"."+r);
                        if (value != null) {
                            try {
//                                testResultId = arManager.validateResultValue(wrVDO.getResultGroup(),
//                                                                             aVDO.getUnitOfMeasureId(),
//                                                                             value.toString());
//                                trDO = arManager.getTestResultList().get(testResultId);
                                
//                                wrVDO.setTestResultId(testResultId);
//                                wrVDO.setTypeId(trDO.getTypeId());
                                wrVDO.setValue(value.toString());
                            } catch (Exception anyE) {
//                                errorList.add(anyE);
                                throw anyE;
                            }
                        }

                        value = getValueFromCell(wb, "final_value."+i+"."+a+"."+r);
                        if (value != null) {
                            try {
//                                testResultId = arManager.validateResultValue(wrVDO.getResultGroup(),
//                                                                             aVDO.getUnitOfMeasureId(),
//                                                                             value.toString());
//                                trDO = arManager.getTestResultList().get(testResultId);
//                                
                                rVDO = arManager.getResultForWorksheet(waDO.getAnalysisId(), wrVDO.getAnalyteId());
//                                rVDO.setTestResultId(testResultId);
//                                rVDO.setTypeId(trDO.getTypeId());
//                                rVDO.setValue(formatValue(trDO, value.toString()));
                                rVDO.setValue(value.toString());
                            } catch (Exception anyE) {
//                                errorList.add(anyE);
                                throw anyE;
                            }
                        }
                    }
                } else if (waDO.getQcId() != null) {
                    wqrManager = waManager.getWorksheetQcResultAt(a);
                    for (r = 0; r < wqrManager.count(); r++) {
                        wqrVDO = wqrManager.getWorksheetQcResultAt(r);
                        
                        value = getValueFromCell(wb, "raw_value."+i+"."+a+"."+r);
                        if (value != null) {
                            wqrVDO.setValue(value.toString());
                        }
                    }
                }
            }
        }
        
//        if (errorList.getErrorList().size() > 0)
//            throw errorList;
        
        return manager;
    }
    
    private static HashMap<String,CellStyle> createStyles(HSSFWorkbook wb) {
        HashMap<String,CellStyle> styles;
        CellStyle                 headerStyle, rowEditStyle, rowNoEditStyle;
        Font                      headerFont;

        styles = new HashMap<String,CellStyle>();

        headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle = wb.createCellStyle();
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setLocked(true);
        styles.put("header", headerStyle);

        rowEditStyle = wb.createCellStyle();
        rowEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowEditStyle.setLocked(false);
        styles.put("row_edit", rowEditStyle);

        rowNoEditStyle = wb.createCellStyle();
        rowNoEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        rowNoEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        rowNoEditStyle.setLocked(true);
        styles.put("row_no_edit", rowNoEditStyle);

        return styles;
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

    private SystemVariableLocal sysVarLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SystemVariableLocal)ctx.lookup("openelis/SystemVariableBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
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
    
    private String getWorksheetTempDirectory() {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = sysVarLocal().fetchByName("worksheet_temp_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            System.out.println(anyE.getMessage());
        }
        
        return dirName; 
    }
    
    private String getPositionNumber(int position, String format, Integer batchCapacity) {
        int    major, minor;
        String positionNumber;
        
        positionNumber = "";
        if ("wsheet_num_format_batch".equals(format)) {
            major = getPositionMajorNumber(position, batchCapacity);
            minor = getPositionMinorNumber(position, batchCapacity);
            positionNumber = major+"-"+minor;
        } else if ("wsheet_num_format_total".equals(format)) {
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
    
    private void createConstraint(HashMap<Integer,HashMap<Integer,CellAttributes>> testAttributes,
                                  Integer testId, Integer resultGroup, ResultValidator validator) {
        CellAttributes attributes;
        
        attributes = getCellAttributes(testAttributes, testId, resultGroup);

        attributes.constraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
                                                                     DVConstraint.OperatorType.GREATER_OR_EQUAL,
                                                                     "0.0", null);
//        attributes.tooltip = formatTooltip(validator.getRanges(0), validator.getDictionaryRanges(0));
        return;
    }
    
    private void addCellToValidationRange(HashMap<Integer,HashMap<Integer,CellAttributes>> testAttributes,
                                          Integer testId, Integer resultGroup, Cell cell) {
        CellAttributes   attributes;
        CellRangeAddress range;

        attributes = getCellAttributes(testAttributes, testId, resultGroup);
        range = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(),
                                     cell.getColumnIndex(), cell.getColumnIndex());
        
        if (attributes.cellRanges == null)
            attributes.cellRanges = new CellRangeAddressList();
        
        attributes.cellRanges.addCellRangeAddress(range);
        return;
    }
/*    
    private String formatTooltip(ArrayList<LocalizedException> ranges, ArrayList<LocalizedException> dictRanges) {
        int          i;
        StringBuffer tooltip;
        
        tooltip = new StringBuffer();
        for (i = 0; i < ranges.size(); i++) {
            if (tooltip.length() > 0)
                tooltip.append("\n");
            tooltip.append(formatMessage(ranges.get(i).getKey(), ranges.get(i).getParams()));
        }
        for (i = 0; i < dictRanges.size(); i++) {
            if (tooltip.length() > 0)
                tooltip.append("\n");
            tooltip.append(formatMessage(dictRanges.get(i).getKey(), dictRanges.get(i).getParams()));
        }
        
        return tooltip.toString();
    }
*/
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
    private String formatMessage(String key, String[] params) {
        String      message;
        UTFResource resource;
        
        resource = UTFResource.getBundle(OPENELIS_CONSTANTS, new Locale("en"));
        message = resource.getString(key); 
        if (params != null) {
            for(int i = 0; i < params.length; i++) {
                message = message.replaceFirst("\\{"+i+"\\}", params[i]);
            }
        }
        
        return message;
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

    private CellAttributes getCellAttributes(HashMap<Integer,HashMap<Integer,CellAttributes>> testAttributes, 
                                             Integer testId, Integer resultGroup) {
        HashMap<Integer,CellAttributes> groupAttributes;
        CellAttributes                  attributes;

        groupAttributes = testAttributes.get(testId);
        if (groupAttributes == null) {
            groupAttributes = new HashMap<Integer,CellAttributes>();
            testAttributes.put(testId, groupAttributes);
        }
        
        attributes = groupAttributes.get(resultGroup);
        if (attributes == null) {
            attributes = new CellAttributes();
            groupAttributes.put(resultGroup, attributes);
        }

        return attributes;
    }
    
    private Object getValueFromCell(HSSFWorkbook wb, String name) {
        int           nameIndex;
        Object        value;
        AreaReference aref;
        Cell          cell;
        CellReference cref[];
        Name          cellName;

        value = null;
        nameIndex = wb.getNameIndex(name);
        if (nameIndex != -1) {
            cellName = wb.getNameAt(nameIndex);
            if (!cellName.isDeleted()) {
                aref = new AreaReference(cellName.getRefersToFormula());
                cref = aref.getAllReferencedCells();
                cell = wb.getSheetAt(0).getRow(cref[0].getRow()).getCell((int)cref[0].getCol());
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = cell.getStringCellValue();
                        break;
                        
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell))
                            value = cell.getDateCellValue();
                        else
                            value = cell.getNumericCellValue();
                        break;
                        
                    case Cell.CELL_TYPE_FORMULA:
                        value = cell.getNumericCellValue();
                        break;
                }
            }
        }
        
        return value;
    }
    
    /*
     * Create format specific header row cells.  Index starts at 8.
     */
    private void createHeaderCellsForFormat(Row row, String format, HashMap<String,CellStyle> styles) {
        Cell cell;
        
        cell = row.createCell(8);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Raw Value");

        cell = row.createCell(9);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Dilution Factor");

        cell = row.createCell(10);
        cell.setCellStyle(styles.get("header"));
        cell.setCellValue("Final Value");
    }
    
    private void createResultCellsForFormat(HSSFWorkbook wb, Row row, String nameIndex,
                                            String format1, HashMap<String,CellStyle> styles,
                                            HashMap<Integer,HashMap<Integer,CellAttributes>> cellAttributes,
                                            AnalysisViewDO aVDO, WorksheetResultViewDO wrVDO) {
        Cell cell;
        Name cellName;
        
        // raw value
        cell = row.createCell(8);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellValue("");
        addCellToValidationRange(cellAttributes, aVDO.getTestId(), wrVDO.getResultGroup(), cell);
        cellName = wb.createName();
        cellName.setNameName("raw_value."+nameIndex);
        cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(8)+
                                    (row.getRowNum()+1));

        // dilution factor
        cell = row.createCell(9);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellValue(0.5);
        cellName = wb.createName();
        cellName.setNameName("dilution_factor."+nameIndex);
        cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(9)+
                                    (row.getRowNum()+1));

        // final value
        cell = row.createCell(10);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellFormula("PRODUCT("+CellReference.convertNumToColString(10-2)+
                            (row.getRowNum()+1)+","+CellReference.convertNumToColString(10-1)+
                            (row.getRowNum()+1)+")");
        addCellToValidationRange(cellAttributes, aVDO.getTestId(), wrVDO.getResultGroup(), cell);
        cellName = wb.createName();
        cellName.setNameName("final_value."+nameIndex);
        cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(10)+
                                    (row.getRowNum()+1));
    }

    private void createQcResultCellsForFormat(HSSFWorkbook wb, Row row, String nameIndex,
                                              String format, HashMap<String,CellStyle> styles,
                                              HashMap<Integer,HashMap<Integer,CellAttributes>> cellAttributes,
                                              WorksheetQcResultViewDO wqrVDO) {
        Cell cell;
        Name cellName;
        
        // raw value
        cell = row.createCell(8);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellValue("");
        cellName = wb.createName();
        cellName.setNameName("raw_value."+nameIndex);
        cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(8)+
                                    (row.getRowNum()+1));

        // dilution factor
        cell = row.createCell(9);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellValue(0.5);
        cellName = wb.createName();
        cellName.setNameName("dilution_factor."+nameIndex);
        cellName.setRefersToFormula("Worksheet!"+CellReference.convertNumToColString(9)+
                                    (row.getRowNum()+1));

        // final value
        cell = row.createCell(10);
        cell.setCellStyle(styles.get("row_edit"));
        cell.setCellFormula("PRODUCT("+CellReference.convertNumToColString(10-2)+
                            (row.getRowNum()+1)+","+CellReference.convertNumToColString(10-1)+
                            (row.getRowNum()+1)+")");
    }

    private void createEmptyCellsForFormat(Row row, String format, HashMap<String,CellStyle> styles) {
        Cell cell;
        
        // raw value
        cell = row.createCell(8);
        cell.setCellStyle(styles.get("row_no_edit"));
        cell.setCellValue("");
    
        // dilution factor
        cell = row.createCell(9);
        cell.setCellStyle(styles.get("row_no_edit"));
        cell.setCellValue("");
    
        // final value
        cell = row.createCell(10);
        cell.setCellStyle(styles.get("row_no_edit"));
        cell.setCellValue("");
    }
    
    class CellAttributes {
        String tooltip;
        CellRangeAddressList cellRanges;
        DVConstraint constraint;
    }
}