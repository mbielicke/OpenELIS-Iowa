/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.LabelReportLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.remote.BuildKitsReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class BuildKitsReportBean implements BuildKitsReportRemote {
    
    @EJB
    private SessionCacheLocal session;
    
    @EJB
    private PrinterCacheLocal printer;
    
    @EJB
    private DictionaryLocal   dictionary;
     
    @EJB   
    private LabelReportLocal labelReport;
    
    @EJB
    private OrderLocal        order;  
    
    private static final Logger log = Logger.getLogger(BuildKitsReportBean.class);

    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> list;
        ArrayList<Prompt> p;
        ArrayList<DictionaryDO> d;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("LOT_NUMBER", Prompt.Type.STRING).setPrompt("Lot #:")
                                                               .setHidden(true));
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
            d = dictionary.fetchByCategorySystemName("kit_special_instructions");
            list = getInstructions(d);
            p.add(new Prompt("SPECIAL_INSTRUCTIONS", Prompt.Type.ARRAY).setPrompt("Special Instructions:")
                                                                        .setWidth(200)
                                                                        .setOptionList(list)
                                                                        .setMutiSelect(false));
            list = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Barcode Printer:")
                                                          .setWidth(150)
                                                          .setOptionList(list)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i,j, startNum, endNum, numLabels;
        ReportStatus status;
        HashMap<String, QueryData> param;
        String lotNumber, orderId, createdDate, expiredDate, kitDesc, specInstr,
               printer, printstat;
        PrintStream ps;
        File tempFile;
        
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
        lotNumber = ReportUtil.getSingleParameter(param, "LOT_NUMBER");
        orderId = ReportUtil.getSingleParameter(param, "ORDER_ID");
        createdDate = ReportUtil.getSingleParameter(param, "CREATED_DATE");
        expiredDate = ReportUtil.getSingleParameter(param, "EXPIRED_DATE");
        kitDesc = ReportUtil.getSingleParameter(param, "KIT_DESCRIPTION");
        specInstr = ReportUtil.getSingleParameter(param, "SPECIAL_INSTRUCTIONS");
        printer = ReportUtil.getSingleParameter(param, "BARCODE");
        startNum = 0;
        numLabels = 0;
        
        if (DataBaseUtil.isEmpty(orderId) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the order id and printer for this report");
        
        //
        // find the order record
        //
        try {
            order.fetchById(Integer.parseInt(orderId));
        } catch (NotFoundException e) {
            throw new NotFoundException("An order record with id " + orderId + " does not exist");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        try {
            startNum = Integer.parseInt(ReportUtil.getSingleParameter(param, "STARTING_NUMBER"));
        } catch (Exception e) {
            throw new InconsistencyException("You must specify a valid starting number");
        } finally {
            if (startNum < 1)
                throw new InconsistencyException("Starting number must be at least 1");
        }
        
        try {
            numLabels = Integer.parseInt(ReportUtil.getSingleParameter(param, "NUMBER_OF_LABELS_PER_KIT"));
        } catch (Exception e) {
            throw new InconsistencyException("You must specify a valid number of labels per kit");
        } finally {
            if (numLabels < 1)
                throw new InconsistencyException("Number of labels per kit must be at least 1");
        }
        
        endNum = Integer.parseInt(ReportUtil.getSingleParameter(param, "ENDING_NUMBER"));
        
        log.info("Printing labels for order # "+orderId+" starting at "+ startNum);

        status.setPercentComplete(50);
        /*
         * print the labels and send it to printer
         */
        tempFile = File.createTempFile("loginlabel", ".txt", new File("/tmp"));
        ps = new PrintStream(tempFile);
        for (i = startNum; i <= endNum; i++) {
            for (j = 0; j < numLabels; j++) {
                labelReport.kitLabel(ps, "State Hygienic Laboratory", "Iowa City 319-335-4500",
                                     "Ankeny 515-725-1600", lotNumber, createdDate,
                                     orderId + "." + i, expiredDate, kitDesc, specInstr);                
            }
        }
        ps.close();
        
        printstat = ReportUtil.print(tempFile, printer, 1);
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