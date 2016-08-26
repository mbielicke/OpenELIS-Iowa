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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.utilcommon.TurnaroundUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class ToDoExcelHelperBean {
    @EJB
    CategoryCacheBean                categoryCache;

    @EJB
    SessionCacheBean                 session;

    @EJB
    ToDoBean                         toDo;

    @EJB
    UserCacheBean                    userCache;

    private static final Logger      log = Logger.getLogger("openelis");

    private CellStyle                headerCellStyle, numberCellStyle;

    private SimpleDateFormat         dateTimeFormat;

    private DecimalFormat            decimalFormat;

    private HashMap<String, String>  domains;

    private HashMap<Integer, String> statuses;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus exportToExcel(boolean mySection) throws Exception {
        Path path;
        XSSFWorkbook wb;
        ReportStatus status;

        try {
            /*
             * defaults
             */
            dateTimeFormat = new SimpleDateFormat(Messages.get().dateTimePattern());
            decimalFormat = new DecimalFormat("########.#");

            domains = new HashMap<String, String>();
            for (DictionaryDO d : categoryCache.getBySystemName("sample_domain")
                                               .getDictionaryList()) {
                if ("Y".equals(d.getIsActive()))
                    domains.put(d.getCode(), d.getEntry());
            }

            statuses = new HashMap<Integer, String>();
            for (DictionaryDO d : categoryCache.getBySystemName("analysis_status")
                                               .getDictionaryList())
                statuses.put(d.getId(), d.getEntry());

            status = new ReportStatus();
            status.setPercentComplete(0).setMessage("Exporting to Excel: Initializing");
            session.setAttribute("ToDoExportToExcelStatus", status);

            wb = new XSSFWorkbook();
            createStyles(wb);

            status.setPercentComplete(20);
            status.setMessage("Creating sheet 1 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillLoggedInSheet(wb, toDo.getLoggedIn(), mySection);

            status.setPercentComplete(30);
            status.setMessage("Creating sheet 2 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillInitiatedSheet(wb, toDo.getInitiated(), mySection);

            status.setPercentComplete(40);
            status.setMessage("Creating sheet 3 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillWorksheetSheet(wb, toDo.getWorksheet(), mySection);

            status.setPercentComplete(50);
            status.setMessage("Creating sheet 4 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillCompletedSheet(wb, toDo.getCompleted(), mySection);

            status.setPercentComplete(60);
            status.setMessage("Creating sheet 5 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillReleasedSheet(wb, toDo.getReleased(), mySection);

            status.setPercentComplete(70);
            status.setMessage("Creating sheet 6 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillToBeVerifiedSheet(wb, toDo.getToBeVerified());

            status.setPercentComplete(80);
            status.setMessage("Creating sheet 7 of 7...");
            session.setAttribute("ToDoExportToExcelStatus", status);
            fillOtherSheet(wb, toDo.getOther(), mySection);

            status.setPercentComplete(90);
            status.setMessage("Exporting to Excel: Finalizing");
            session.setAttribute("ToDoExportToExcelStatus", status);

            path = export(wb, "upload_stream_directory");
            status.setMessage(path.getFileName().toString())
                  .setPercentComplete(100)
                  .setPath(path.toString())
                  .setStatus(ReportStatus.Status.SAVED);
        } finally {
            headerCellStyle = null;
            numberCellStyle = null;
            dateTimeFormat = null;
            decimalFormat = null;
            statuses = null;
            domains = null;
        }

        return status;
    }

    private void fillLoggedInSheet(XSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                   boolean mySection) {
        int r;
        SystemUserPermission perm;
        ModulePermission modPerm;
        XSSFSheet sheet;

        perm = userCache.getPermission();
        modPerm = perm.getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();

        sheet = wb.createSheet(Messages.get().todo_loggedIn());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_priority(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_section(),
                     Messages.get().todo_test(),
                     Messages.get().todo_method(),
                     Messages.get().todo_collected(),
                     Messages.get().todo_received(),
                     Messages.get().todo_override(),
                     Messages.get().todo_holding(),
                     Messages.get().todo_expCompletion(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (AnalysisViewVO a : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this analysis only if
             * doesn't belong to a deactivated domain e.g. private well
             */
            if (mySection && perm.getSection(a.getSectionName()) == null ||
                !canViewSample(a.getDomain(), modPerm) || domains.get(a.getDomain()) == null)
                continue;

            addDataRow(sheet.createRow(r++ ),
                       a.getAccessionNumber(),
                       a.getPriority(),
                       domains.get(a.getDomain()),
                       a.getSectionName(),
                       a.getTestName(),
                       a.getMethodName(),
                       TurnaroundUtil.getCombinedYM(a.getCollectionDate(), a.getCollectionTime()),
                       a.getReceivedDate(),
                       a.getAnalysisResultOverride(),
                       TurnaroundUtil.getPercentHoldingUsed(a.getStartedDate(),
                                                            a.getCollectionDate(),
                                                            a.getCollectionTime(),
                                                            a.getTimeHolding()),
                       TurnaroundUtil.getPercentExpectedCompletion(a.getCollectionDate(),
                                                                   a.getCollectionTime(),
                                                                   a.getReceivedDate(),
                                                                   a.getPriority(),
                                                                   a.getTimeTaAverage()),
                       a.getToDoDescription(),
                       a.getPrimaryOrganizationName());
        }

        setSize(sheet, 13);
    }

    private void fillInitiatedSheet(XSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                    boolean mySection) {
        int r, initDays;
        SystemUserPermission perm;
        ModulePermission modPerm;
        XSSFSheet sheet;
        Datetime now;

        perm = userCache.getPermission();
        modPerm = perm.getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();
        now = Datetime.getInstance();
        sheet = wb.createSheet(Messages.get().todo_initiated());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_priority(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_section(),
                     Messages.get().todo_test(),
                     Messages.get().todo_method(),
                     Messages.get().todo_holding(),
                     Messages.get().todo_expCompletion(),
                     Messages.get().todo_daysInInitiated(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (AnalysisViewVO a : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this analysis only if
             * doesn't belong to a deactivated domain e.g. private well
             */
            if (mySection && perm.getSection(a.getSectionName()) == null ||
                !canViewSample(a.getDomain(), modPerm) || domains.get(a.getDomain()) == null)
                continue;

            initDays = 0;
            if (a.getStartedDate() != null)
                initDays = TurnaroundUtil.diffDays(a.getStartedDate(), now);

            addDataRow(sheet.createRow(r++ ),
                       a.getAccessionNumber(),
                       a.getPriority(),
                       domains.get(a.getDomain()),
                       a.getSectionName(),
                       a.getTestName(),
                       a.getMethodName(),
                       TurnaroundUtil.getPercentHoldingUsed(a.getStartedDate(),
                                                            a.getCollectionDate(),
                                                            a.getCollectionTime(),
                                                            a.getTimeHolding()),
                       TurnaroundUtil.getPercentExpectedCompletion(a.getCollectionDate(),
                                                                   a.getCollectionTime(),
                                                                   a.getReceivedDate(),
                                                                   a.getPriority(),
                                                                   a.getTimeTaAverage()),
                       initDays,
                       a.getToDoDescription(),
                       a.getPrimaryOrganizationName());
        }

        setSize(sheet, 11);
    }

    private void fillWorksheetSheet(XSSFWorkbook wb, ArrayList<ToDoWorksheetVO> list,
                                    boolean mySection) {
        int r;
        SystemUserPermission perm;
        XSSFSheet sheet;

        perm = userCache.getPermission();

        sheet = wb.createSheet(Messages.get().todo_worksheet());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_worksheetNum(),
                     Messages.get().gen_username(),
                     Messages.get().todo_section(),
                     Messages.get().gen_description(),
                     Messages.get().todo_created());

        for (ToDoWorksheetVO w : list) {
            if (mySection && perm.getSection(w.getSectionName()) == null)
                continue;

            addDataRow(sheet.createRow(r++ ),
                       w.getId(),
                       w.getSystemUserName(),
                       w.getSectionName(),
                       w.getDescription(),
                       w.getCreatedDate());
        }

        setSize(sheet, 5);
    }

    private void fillCompletedSheet(XSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                    boolean mySection) {
        int r;
        SystemUserPermission perm;
        ModulePermission modPerm;
        XSSFSheet sheet;

        perm = userCache.getPermission();
        modPerm = perm.getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();

        sheet = wb.createSheet(Messages.get().todo_completed());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_priority(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_section(),
                     Messages.get().todo_test(),
                     Messages.get().todo_method(),
                     Messages.get().todo_override(),
                     Messages.get().todo_completed(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (AnalysisViewVO a : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this analysis only if
             * doesn't belong to a deactivated domain e.g. private well
             */
            if (mySection && perm.getSection(a.getSectionName()) == null ||
                !canViewSample(a.getDomain(), modPerm) || domains.get(a.getDomain()) == null)
                continue;

            addDataRow(sheet.createRow(r++ ),
                       a.getAccessionNumber(),
                       a.getPriority(),
                       domains.get(a.getDomain()),
                       a.getSectionName(),
                       a.getTestName(),
                       a.getMethodName(),
                       a.getAnalysisResultOverride(),
                       a.getCompletedDate(),
                       a.getToDoDescription(),
                       a.getPrimaryOrganizationName());
        }

        setSize(sheet, 10);
    }

    private void fillReleasedSheet(XSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                   boolean mySection) {
        int r;
        SystemUserPermission perm;
        ModulePermission modPerm;
        XSSFSheet sheet;

        perm = userCache.getPermission();
        modPerm = perm.getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();

        sheet = wb.createSheet(Messages.get().todo_released());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_section(),
                     Messages.get().todo_test(),
                     Messages.get().todo_method(),
                     Messages.get().todo_collected(),
                     Messages.get().todo_released(),
                     Messages.get().todo_override(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (AnalysisViewVO a : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this analysis only if
             * doesn't belong to a deactivated domain e.g. private well
             */
            if (mySection && perm.getSection(a.getSectionName()) == null ||
                !canViewSample(a.getDomain(), modPerm) || domains.get(a.getDomain()) == null)
                continue;

            addDataRow(sheet.createRow(r++ ),
                       a.getAccessionNumber(),
                       domains.get(a.getDomain()),
                       a.getSectionName(),
                       a.getTestName(),
                       a.getMethodName(),
                       TurnaroundUtil.getCombinedYM(a.getCollectionDate(), a.getCollectionTime()),
                       a.getReleasedDate().getDate(),
                       a.getAnalysisResultOverride(),
                       a.getToDoDescription(),
                       a.getPrimaryOrganizationName());
        }

        setSize(sheet, 10);
    }

    private void fillToBeVerifiedSheet(XSSFWorkbook wb, ArrayList<ToDoSampleViewVO> list) {
        int r;
        XSSFSheet sheet;
        ModulePermission modPerm;
        
        modPerm = userCache.getPermission().getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();
        sheet = wb.createSheet(Messages.get().todo_toBeVerified());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_collected(),
                     Messages.get().todo_received(),
                     Messages.get().todo_override(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (ToDoSampleViewVO s : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this sample only if doesn't
             * belong to a deactivated domain e.g. private well
             */
            if ( !canViewSample(s.getDomain(), modPerm) || domains.get(s.getDomain()) == null)
                continue;
            addDataRow(sheet.createRow(r++ ),
                       s.getAccessionNumber(),
                       domains.get(s.getDomain()),
                       TurnaroundUtil.getCombinedYM(s.getCollectionDate(), s.getCollectionTime()),
                       s.getReceivedDate(),
                       s.getSampleResultOverride(),
                       s.getDescription(),
                       s.getPrimaryOrganizationName());
        }

        setSize(sheet, 7);
    }

    private void fillOtherSheet(XSSFWorkbook wb, ArrayList<AnalysisViewVO> list, boolean mySection) {
        int r;
        SystemUserPermission perm;
        ModulePermission modPerm;
        XSSFSheet sheet;

        perm = userCache.getPermission();
        modPerm = perm.getModule("patient");
        if (modPerm == null)
            modPerm = new ModulePermission();

        sheet = wb.createSheet(Messages.get().todo_other());
        r = 0;

        addHeaderRow(sheet.createRow(r++ ),
                     Messages.get().todo_accNum(),
                     Messages.get().todo_domain(),
                     Messages.get().todo_section(),
                     Messages.get().gen_status(),
                     Messages.get().todo_test(),
                     Messages.get().todo_method(),
                     Messages.get().todo_collected(),
                     Messages.get().todo_received(),
                     Messages.get().todo_override(),
                     Messages.get().todo_domainSpecField(),
                     Messages.get().todo_reportTo());

        for (AnalysisViewVO a : list) {
            /*
             * if this sample has patient info, the user must have the right
             * permission to see it; also, show this analysis only if doesn't
             * belong to a deactivated domain e.g. private well
             */
            if (mySection && perm.getSection(a.getSectionName()) == null ||
                !canViewSample(a.getDomain(), modPerm) || domains.get(a.getDomain()) == null)
                continue;

            addDataRow(sheet.createRow(r++ ),
                       a.getAccessionNumber(),
                       domains.get(a.getDomain()),
                       a.getSectionName(),
                       statuses.get(a.getAnalysisStatusId()),
                       a.getTestName(),
                       a.getMethodName(),
                       TurnaroundUtil.getCombinedYM(a.getCollectionDate(), a.getCollectionTime()),
                       a.getReceivedDate(),
                       a.getAnalysisResultOverride(),
                       a.getToDoDescription(),
                       a.getPrimaryOrganizationName());
        }

        setSize(sheet, 7);
    }

    /*
     * add header row
     */
    private void addHeaderRow(Row row, String... headers) {
        int i;
        Cell cell;

        i = 0;
        for (String h : headers) {
            cell = row.createCell(i++ );
            cell.setCellValue(h);
            cell.setCellStyle(headerCellStyle);
        }
    }

    /*
     * add data row
     */
    private void addDataRow(Row row, Object... data) {
        int i;
        Cell cell;

        i = 0;
        for (Object d : data) {
            cell = row.createCell(i++ );
            if (d instanceof Datetime) {
                cell.setCellValue(dateTimeFormat.format( ((Datetime)d).getDate()));
            } else if (d instanceof Double) {
                cell.setCellValue(decimalFormat.format(d));
                cell.setCellStyle(numberCellStyle);
            } else if (d instanceof Integer) {
                cell.setCellValue((Integer)d);
            } else {
                cell.setCellValue(DataBaseUtil.toString(d));
            }
        }
    }

    private void setSize(XSSFSheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++ )
            sheet.autoSizeColumn(i);
    }

    /*
     * creates the styles needed for header and row data
     */
    private void createStyles(XSSFWorkbook wb) {
        headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        headerCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        numberCellStyle = wb.createCellStyle();
        numberCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    }

    /*
     * Exports the filled workbook to a temp file for printing or faxing.
     */
    private Path export(XSSFWorkbook wb, String systemVariableDirectory) throws Exception {
        Path path;
        OutputStream out;

        out = null;
        try {
            path = ReportUtil.createTempFile("todo", ".xlsx", systemVariableDirectory);
            out = Files.newOutputStream(path);
            wb.write(out);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for todo");
            }
        }
        return path;
    }

    /**
     * Returns true if either the passed domain doesn't have patient data or the
     * permission allows the user has permission to view patients; returns false otherwise
     */
    private boolean canViewSample(String domain, ModulePermission permission) {
        if (Constants.domain().CLINICAL.equals(domain) ||
            Constants.domain().NEONATAL.equals(domain))
            return permission.hasSelectPermission();
        else
            return true;
    }
}