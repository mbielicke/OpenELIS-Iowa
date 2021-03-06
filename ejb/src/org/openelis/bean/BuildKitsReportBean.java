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

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class BuildKitsReportBean {
    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private PrinterCacheBean    printer;

    @EJB
    private CategoryCacheBean   categoryCache;

    @EJB
    private LabelReportBean     labelReport;

    @EJB
    private IOrderBean           iorder;

    private static final Logger log = Logger.getLogger("openelis");

    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> list;
        ArrayList<Prompt> p;
        ArrayList<DictionaryDO> d;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("LOT_NUMBER", Prompt.Type.STRING).setPrompt("Lot #:").setHidden(true));
            p.add(new Prompt("ORDER_ID", Prompt.Type.INTEGER).setPrompt("Order Id:")
                                                             .setHidden(true));
            p.add(new Prompt("CREATED_DATE", Prompt.Type.DATETIME).setPrompt("Created Date:")
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                  .setHidden(true));
            p.add(new Prompt("EXPIRED_DATE", Prompt.Type.DATETIME).setPrompt("Expired Date:")
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                  .setHidden(true));
            p.add(new Prompt("ENDING_NUMBER", Prompt.Type.INTEGER).setPrompt("Ending #:")
                                                                  .setHidden(true));
            p.add(new Prompt("STARTING_NUMBER", Prompt.Type.INTEGER).setPrompt("Starting #:")
                                                                    .setDefaultValue("1")
                                                                    .setWidth(30)
                                                                    .setRequired(true));
            p.add(new Prompt("NUMBER_OF_LABELS_PER_KIT", Prompt.Type.INTEGER).setPrompt("# Labels/Kit:")
                                                                             .setDefaultValue("1")
                                                                             .setWidth(30)
                                                                             .setRequired(true));
            p.add(new Prompt("KIT_DESCRIPTION", Prompt.Type.STRING).setPrompt("Description:")
                                                                   .setHidden(true));
            d = categoryCache.getBySystemName("kit_special_instructions").getDictionaryList();
            list = getInstructions(d);
            p.add(new Prompt("SPECIAL_INSTRUCTIONS", Prompt.Type.ARRAY).setPrompt("Special Instructions:")
                                                                       .setWidth(200)
                                                                       .setOptionList(list)
                                                                       .setMultiSelect(false));
            list = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Barcode Printer:")
                                                          .setWidth(150)
                                                          .setOptionList(list)
                                                          .setMultiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create result prompts", e);
            throw e;
        }
    }

    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i, j;
        Integer startNum, endNum, numLabels, orderId;
        ReportStatus status;
        HashMap<String, QueryData> param;
        String lotNumber, createdDate, expiredDate, kitDesc, specInstr, printer, printstat;
        PrintStream ps;
        Path path;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("BuildKitsLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        lotNumber = ReportUtil.getStringParameter(param, "LOT_NUMBER");
        orderId = ReportUtil.getIntegerParameter(param, "ORDER_ID");
        createdDate = ReportUtil.getStringParameter(param, "CREATED_DATE");
        expiredDate = ReportUtil.getStringParameter(param, "EXPIRED_DATE");
        kitDesc = ReportUtil.getStringParameter(param, "KIT_DESCRIPTION");
        specInstr = ReportUtil.getStringParameter(param, "SPECIAL_INSTRUCTIONS");
        printer = ReportUtil.getStringParameter(param, "BARCODE");
        startNum = ReportUtil.getIntegerParameter(param, "STARTING_NUMBER");
        endNum = ReportUtil.getIntegerParameter(param, "ENDING_NUMBER");
        numLabels = ReportUtil.getIntegerParameter(param, "NUMBER_OF_LABELS_PER_KIT");

        if (orderId == null || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the order id and printer for this report");

        //
        // find the order record
        //
        try {
            iorder.fetchById(orderId);
        } catch (NotFoundException e) {
            throw new NotFoundException("An order record with id " + orderId + " does not exist");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Iorder lookup failed", e);
            throw e;
        }

        if (startNum == null || startNum < 1)
            throw new InconsistencyException("You must specify a valid starting number of at least 1");

        if (numLabels == null || numLabels < 1)
            throw new InconsistencyException("You must specify a valid number of labels per kit of at least 1");

        log.info("Printing labels for order # " + orderId + " starting at " + startNum);

        status.setPercentComplete(50);

        /*
         * print the labels and send it to printer
         */
        path = ReportUtil.createTempFile("kitlabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        for (i = startNum; i <= endNum; i++) {
            for (j = 0; j < numLabels; j++) {
                labelReport.kitLabel(ps,
                                     "State Hygienic Laboratory",
                                     "Iowa City 319-335-4500",
                                     "Ankeny 515-725-1600",
                                     lotNumber,
                                     createdDate,
                                     orderId + "." + i,
                                     expiredDate,
                                     kitDesc,
                                     specInstr);
            }
        }
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    private ArrayList<OptionListItem> getInstructions(ArrayList<DictionaryDO> entries) {
        ArrayList<OptionListItem> list;

        list = new ArrayList<OptionListItem>();
        list.add(new OptionListItem(null, ""));
        for (DictionaryDO data : entries) {
            if ("Y".equals(data.getIsActive()))
                list.add(new OptionListItem(data.getEntry(), data.getEntry()));
        }

        return list;
    }
}