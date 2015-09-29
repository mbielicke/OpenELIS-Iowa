package org.openelis.bean;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class ToDoExcelHelperBean {
    @EJB
    DictionaryCacheBean                dictionaryCache;

    @EJB
    CategoryCacheBean                  categoryCache;

    @EJB
    SessionCacheBean                   session;

    @EJB
    SystemVariableBean                 systemVariable;

    @EJB
    ToDoBean                           toDo;

    @EJB
    UserCacheBean                      userCache;

    private static final Logger        log = Logger.getLogger("openelis");

    private HashMap<String, CellStyle> styles;

    private SimpleDateFormat           dateTimeFormat;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus exportToExcel(boolean mySection) throws Exception {
        Path path;
        HSSFWorkbook wb;
        ReportStatus status;

        status = new ReportStatus();
        status.setPercentComplete(0);
        status.setMessage("Exporting to Excel: Initializing");
        session.setAttribute("ToDoExportToExcelStatus", status);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        wb = new HSSFWorkbook();
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

        // fillInstrumentSheet(wb, toDo.getInstrument(), mySection);

        status.setPercentComplete(90);
        status.setMessage("Exporting to Excel: Finalizing");
        session.setAttribute("ToDoExportToExcelStatus", status);

        path = export(wb, "upload_stream_directory");
        status.setMessage(path.getFileName().toString())
              .setPath(path.toString())
              .setStatus(ReportStatus.Status.SAVED);

        styles = null;
        dateTimeFormat = null;

        return status;
    }

    private void createStyles(HSSFWorkbook wb) {
        CellStyle dateTimeEditStyle, dateTimeNoEditStyle, headerStyle, rowEditStyle, rowNoEditStyle;
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
        dateTimeEditStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd hh:mm"));
        dateTimeEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dateTimeEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        dateTimeEditStyle.setLocked(false);
        styles.put("datetime_edit", dateTimeEditStyle);

        dateTimeNoEditStyle = wb.createCellStyle();
        dateTimeNoEditStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd hh:mm"));
        dateTimeNoEditStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dateTimeNoEditStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        dateTimeNoEditStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        dateTimeNoEditStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        dateTimeNoEditStyle.setLocked(true);
        styles.put("datetime_no_edit", dateTimeNoEditStyle);
    }

    private void fillLoggedInSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                   boolean mySection) {
        Integer accNum, prevAccNum;
        String secName;
        SystemUserPermission perm;
        Datetime scd, sct, scdt;
        Date temp;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        AnalysisViewVO data;
        ArrayList<String> headers;

        accNum = null;
        prevAccNum = null;
        scdt = null;
        perm = userCache.getPermission();
        sheet = wb.createSheet("LoggedIn");

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().gen_priority());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_test());
        headers.add(Messages.get().gen_method());
        headers.add(Messages.get().sample_collected());
        headers.add(Messages.get().sample_received());
        headers.add(Messages.get().todo_override());
        headers.add(Messages.get().todo_holding());
        headers.add(Messages.get().todo_expCompletion());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);

        // row = sheet.createRow(0);
        // cell = row.createCell(0);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().sample_accessionNum());
        //
        // cell = row.createCell(1);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().gen_priority());
        //
        // cell = row.createCell(2);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().todo_domain());
        //
        // cell = row.createCell(3);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().gen_section());
        //
        // cell = row.createCell(4);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().gen_test());
        //
        // cell = row.createCell(5);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().gen_method());
        //
        // cell = row.createCell(6);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().sample_collected());
        //
        // cell = row.createCell(7);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().sample_received());
        //
        // cell = row.createCell(8);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().todo_override());
        //
        // cell = row.createCell(9);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().todo_holding());
        //
        // cell = row.createCell(10);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().todo_expCompletion());
        //
        // cell = row.createCell(11);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().todo_domainSpecField());
        //
        // cell = row.createCell(12);
        // cell.setCellStyle(styles.get("header"));
        // cell.setCellValue(Messages.get().sampleOrganization_reportTo());

        for (int r = 1; r < list.size(); r++ ) {
            data = list.get(r - 1);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r);

            accNum = data.getAccessionNumber();
            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(accNum);

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPriority() != null)
                cell.setCellValue(data.getPriority());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getTestName());

            cell = row.createCell(5);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getMethodName() != null)
                cell.setCellValue(data.getMethodName());

            if ( !accNum.equals(prevAccNum)) {
                scd = data.getCollectionDate();
                sct = data.getCollectionTime();
                if (scd != null) {
                    temp = scd.getDate();
                    if (sct == null) {
                        temp.setHours(0);
                        temp.setMinutes(0);
                    } else {
                        temp.setHours(sct.getDate().getHours());
                        temp.setMinutes(sct.getDate().getMinutes());
                    }
                    scdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp);
                } else {
                    scdt = null;
                }
            }

            cell = row.createCell(6);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (scdt != null)
                cell.setCellValue(dateTimeFormat.format(scdt.getDate()));

            cell = row.createCell(7);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (data.getReceivedDate() != null)
                cell.setCellValue(dateTimeFormat.format(data.getReceivedDate().getDate()));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getAnalysisResultOverride() != null)
                cell.setCellValue(data.getAnalysisResultOverride());

            cell = row.createCell(9);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(DataBaseUtil.getPercentHoldingUsed(data.getStartedDate(),
                                                                 data.getCollectionDate(),
                                                                 data.getCollectionTime(),
                                                                 data.getTimeHolding()));

            cell = row.createCell(10);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(DataBaseUtil.getPercentExpectedCompletion(data.getCollectionDate(),
                                                                        data.getCollectionTime(),
                                                                        data.getReceivedDate(),
                                                                        data.getPriority(),
                                                                        data.getTimeTaAverage()));

            cell = row.createCell(11);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getToDoDescription() != null)
                cell.setCellValue(data.getToDoDescription());

            cell = row.createCell(12);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
    }

    private void fillInitiatedSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                    boolean mySection) {
        Long day, hour, diff;
        Double units;
        String secName;
        SystemUserPermission perm;
        Datetime now;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        AnalysisViewVO data;
        ArrayList<String> headers;

        perm = userCache.getPermission();
        sheet = wb.createSheet("Initiated");
        hour = 3600000L;
        day = 24 * hour;
        now = Datetime.getInstance();

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().gen_priority());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_test());
        headers.add(Messages.get().gen_method());
        headers.add(Messages.get().todo_holding());
        headers.add(Messages.get().todo_expCompletion());
        headers.add(Messages.get().todo_daysInInitiated());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);

        for (int r = 1; r < list.size(); r++ ) {
            data = list.get(r - 1);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r);

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAccessionNumber());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPriority() != null)
                cell.setCellValue(data.getPriority());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getTestName());

            cell = row.createCell(5);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getMethodName() != null)
                cell.setCellValue(data.getMethodName());

            cell = row.createCell(6);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(DataBaseUtil.getPercentHoldingUsed(data.getStartedDate(),
                                                                 data.getCollectionDate(),
                                                                 data.getCollectionTime(),
                                                                 data.getTimeHolding()));

            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(DataBaseUtil.getPercentExpectedCompletion(data.getCollectionDate(),
                                                                        data.getCollectionTime(),
                                                                        data.getReceivedDate(),
                                                                        data.getPriority(),
                                                                        data.getTimeTaAverage()));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getStartedDate() == null) {
                cell.setCellValue(0);
            } else {
                /*
                 * Days in initiated are calculated based on started date.
                 * Math.ceil() returns the value closest to an integer that's
                 * greater than or equal to the argument("units" here). For
                 * example 3.4 is converted to 4.0 and 5.0 stays the same.
                 */
                diff = now.getDate().getTime() - data.getStartedDate().getDate().getTime();
                units = diff.doubleValue() / day.doubleValue();
                cell.setCellValue(Math.ceil(units));
            }

            cell = row.createCell(9);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getToDoDescription() != null)
                cell.setCellValue(data.getToDoDescription());

            cell = row.createCell(10);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
    }

    private void fillWorksheetSheet(HSSFWorkbook wb, ArrayList<ToDoWorksheetVO> list,
                                    boolean mySection) {
        String secName;
        SystemUserPermission perm;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        ToDoWorksheetVO data;
        ArrayList<String> headers;

        perm = userCache.getPermission();
        sheet = wb.createSheet("Worksheet");

        headers = new ArrayList<String>();
        headers.add(Messages.get().gen_id());
        headers.add(Messages.get().gen_user());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_description());
        headers.add(Messages.get().gen_created());
        createHeaders(sheet, headers);

        for (int r = 1; r < list.size(); r++ ) {
            data = list.get(r - 1);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r);

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getId());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getSystemUserName());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getDescription() != null)
                cell.setCellValue(data.getDescription());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            cell.setCellValue(dateTimeFormat.format(data.getCreatedDate().getDate()));
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
    }

    private void fillCompletedSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                    boolean mySection) {
        int r;
        String secName;
        SystemUserPermission perm;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        AnalysisViewVO data;
        ArrayList<String> headers;

        perm = userCache.getPermission();
        sheet = wb.createSheet("Completed");

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().gen_priority());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_test());
        headers.add(Messages.get().gen_method());
        headers.add(Messages.get().todo_override());
        headers.add(Messages.get().analysis_completed());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);
        r = 1;

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r++ );

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAccessionNumber());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPriority() != null)
                cell.setCellValue(data.getPriority());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getTestName());

            cell = row.createCell(5);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getMethodName() != null)
                cell.setCellValue(data.getMethodName());

            cell = row.createCell(6);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getAnalysisResultOverride() != null)
                cell.setCellValue(data.getAnalysisResultOverride());

            cell = row.createCell(7);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (data.getCompletedDate() != null)
                cell.setCellValue(dateTimeFormat.format(data.getCompletedDate().getDate()));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getToDoDescription() != null)
                cell.setCellValue(data.getToDoDescription());

            cell = row.createCell(9);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
    }

    private void fillReleasedSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                   boolean mySection) {
        int r;
        String secName;
        SystemUserPermission perm;
        Datetime scd, sct;
        Date temp;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        AnalysisViewVO data;
        ArrayList<String> headers;

        perm = userCache.getPermission();
        sheet = wb.createSheet("Released");

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_test());
        headers.add(Messages.get().gen_method());
        headers.add(Messages.get().sample_collected());
        headers.add(Messages.get().gen_released());
        headers.add(Messages.get().todo_override());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);
        r = 1;

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r++ );

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAccessionNumber());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getTestName());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getMethodName() != null)
                cell.setCellValue(data.getMethodName());

            scd = data.getCollectionDate();
            sct = data.getCollectionTime();
            if (scd != null) {
                temp = scd.getDate();
                if (sct == null) {
                    temp.setHours(0);
                    temp.setMinutes(0);
                } else {
                    temp.setHours(sct.getDate().getHours());
                    temp.setMinutes(sct.getDate().getMinutes());
                }

                cell = row.createCell(5);
                cell.setCellStyle(styles.get("datetime_no_edit"));
                cell.setCellValue(dateTimeFormat.format(Datetime.getInstance(Datetime.YEAR,
                                                                             Datetime.MINUTE,
                                                                             temp).getDate()));
            }

            cell = row.createCell(6);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (data.getReleasedDate() != null)
                cell.setCellValue(dateTimeFormat.format(data.getReleasedDate().getDate()));

            cell = row.createCell(7);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getAnalysisResultOverride() != null)
                cell.setCellValue(data.getAnalysisResultOverride());

            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getToDoDescription() != null)
                cell.setCellValue(data.getToDoDescription());

            cell = row.createCell(9);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
    }

    private void fillToBeVerifiedSheet(HSSFWorkbook wb, ArrayList<ToDoSampleViewVO> list) {
        Datetime scd, sct;
        Date temp;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        ToDoSampleViewVO data;
        ArrayList<String> headers;

        sheet = wb.createSheet("ToBeVerified");

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().sample_collected());
        headers.add(Messages.get().sample_received());
        headers.add(Messages.get().todo_override());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);

        for (int r = 1; r < list.size(); r++ ) {
            data = list.get(r - 1);
            row = sheet.createRow(r);

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAccessionNumber());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            scd = data.getCollectionDate();
            sct = data.getCollectionTime();
            if (scd != null) {
                temp = scd.getDate();
                if (sct == null) {
                    temp.setHours(0);
                    temp.setMinutes(0);
                } else {
                    temp.setHours(sct.getDate().getHours());
                    temp.setMinutes(sct.getDate().getMinutes());
                }

                cell = row.createCell(2);
                cell.setCellStyle(styles.get("datetime_no_edit"));
                cell.setCellValue(dateTimeFormat.format(Datetime.getInstance(Datetime.YEAR,
                                                                             Datetime.MINUTE,
                                                                             temp).getDate()));
            }

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (data.getReceivedDate() != null)
                cell.setCellValue(dateTimeFormat.format(data.getReceivedDate().getDate()));

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSampleResultOverride() != null)
                cell.setCellValue(data.getSampleResultOverride());

            cell = row.createCell(5);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getDescription() != null)
                cell.setCellValue(data.getDescription());

            cell = row.createCell(6);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    private void fillOtherSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list, boolean mySection) {
        int r;
        String secName;
        SystemUserPermission perm;
        Datetime scd, sct;
        Date temp;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        AnalysisViewVO data;
        ArrayList<String> headers;

        perm = userCache.getPermission();
        sheet = wb.createSheet("Other");

        headers = new ArrayList<String>();
        headers.add(Messages.get().sample_accessionNum());
        headers.add(Messages.get().todo_domain());
        headers.add(Messages.get().gen_section());
        headers.add(Messages.get().gen_status());
        headers.add(Messages.get().gen_test());
        headers.add(Messages.get().gen_method());
        headers.add(Messages.get().sample_collected());
        headers.add(Messages.get().sample_received());
        headers.add(Messages.get().todo_override());
        headers.add(Messages.get().todo_domainSpecField());
        headers.add(Messages.get().sampleOrganization_reportTo());
        createHeaders(sheet, headers);
        r = 1;

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            secName = data.getSectionName();
            if (mySection && perm.getSection(secName) == null)
                continue;
            row = sheet.createRow(r++ );

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAccessionNumber());

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getDomain());

            cell = row.createCell(2);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getSectionName() != null)
                cell.setCellValue(data.getSectionName());

            cell = row.createCell(3);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getAnalysisStatusId());

            cell = row.createCell(4);
            cell.setCellStyle(styles.get("row_no_edit"));
            cell.setCellValue(data.getTestName());

            cell = row.createCell(5);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getMethodName() != null)
                cell.setCellValue(data.getMethodName());

            scd = data.getCollectionDate();
            sct = data.getCollectionTime();
            if (scd != null) {
                temp = scd.getDate();
                if (sct == null) {
                    temp.setHours(0);
                    temp.setMinutes(0);
                } else {
                    temp.setHours(sct.getDate().getHours());
                    temp.setMinutes(sct.getDate().getMinutes());
                }

                cell = row.createCell(6);
                cell.setCellStyle(styles.get("datetime_no_edit"));
                cell.setCellValue(dateTimeFormat.format(Datetime.getInstance(Datetime.YEAR,
                                                                             Datetime.MINUTE,
                                                                             temp).getDate()));
            }

            cell = row.createCell(7);
            cell.setCellStyle(styles.get("datetime_no_edit"));
            if (data.getReceivedDate() != null)
                cell.setCellValue(dateTimeFormat.format(data.getReceivedDate().getDate()));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getAnalysisResultOverride() != null)
                cell.setCellValue(data.getAnalysisResultOverride());

            cell = row.createCell(9);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getToDoDescription() != null)
                cell.setCellValue(data.getToDoDescription());

            cell = row.createCell(10);
            cell.setCellStyle(styles.get("row_no_edit"));
            if (data.getPrimaryOrganizationName() != null)
                cell.setCellValue(data.getPrimaryOrganizationName());
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
    }

    private void fillInstrumentSheet(HSSFWorkbook wb, ArrayList<AnalysisViewVO> list,
                                     boolean mySection) {
        // TODO
    }

    private void createHeaders(HSSFSheet sheet, ArrayList<String> headers) {
        String header;
        Cell cell;
        Row row;

        row = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++ ) {
            header = headers.get(i);
            cell = row.createCell(i);
            cell.setCellStyle(styles.get("header"));
            cell.setCellValue(header);
        }
    }

    /*
     * Exports the filled workbook to a temp file for printing or faxing.
     */
    private Path export(HSSFWorkbook wb, String systemVariableDirectory) throws Exception {
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
}
