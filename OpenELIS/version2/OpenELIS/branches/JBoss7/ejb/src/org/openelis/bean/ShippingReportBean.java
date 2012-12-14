package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.Prompt;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.LabelReportLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.RequestformReportLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.ShippingLocal;
import org.openelis.remote.ShippingReportRemote;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class ShippingReportBean implements ShippingReportRemote {

    @EJB
    private SessionCacheLocal       session;

    @EJB
    private ShippingLocal           shipping;

    @EJB
    private OrderLocal              order;
    
    @EJB
    private OrderItemLocal          orderItem;

    @EJB
    private RequestformReportLocal  requestFormReport;

    @EJB
    private LabelReportLocal        labelReport;

    @EJB
    private DictionaryCacheLocal    dictionaryCache;

    @EJB
    private PrinterCacheLocal       printer;
    
    @EJB
    private OrganizationLocal       organization;

    private final static String     PRN_PREFIX = "print://";

    @Resource
    private SessionContext          ctx;

    /**
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SHIPPING_ID", Prompt.Type.INTEGER).setPrompt("Shipping Id:")
                                                                .setHidden(true)
                                                                .setWidth(75));
            p.add(new Prompt("MANIFEST", Prompt.Type.CHECK).setPrompt("Print Manifest:")
                                                           .setDefaultValue("Y"));
            p.add(new Prompt("SHIPPING_LABEL", Prompt.Type.CHECK).setPrompt("Print Labels:")
                                                           .setDefaultValue("Y"));
            p.add(new Prompt("INSTRUCTION", Prompt.Type.CHECK).setPrompt("Print Instructions:")
                                                              .setDefaultValue("Y"));
            p.add(new Prompt("REQUESTFORM", Prompt.Type.CHECK).setPrompt("Print Request Form:")
                                                              .setDefaultValue("Y"));
            prn = printer.getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Barcode Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Prints the manifest report as well as all the other forms related to the
     * shipping record, like order request forms and instructions
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int numpkg;
        boolean printManifest, printLabel, printReqform, printInstr;
        Integer shippingId, orderId, prevOrderId, methodId;
        String shippingIdStr, printer, barcodePrinter, manifest, shippingLabel,
               requestForm, instruction, dir, printstat, itemUri, uriPath, method,
               costCenter,fromAptSuite, fromStreetAddr, toAptSuite, toStreetAddr;
        URL url;
        File tempFile;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        HashMap<String, QueryData> param;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        ShippingViewDO shipData;
        ArrayList<Integer> orderIds;
        ArrayList<OrderViewDO> orderList;        
        ArrayList<OrderItemViewDO> itemList;
        AddressDO shipFromAddr, shipToAddr;
        OrganizationViewDO shipFrom;   
        DictionaryDO labLocDict;
        OrganizationDO shipTo;
        PrintStream ps;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("ManifestReport", status);

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        shippingIdStr = ReportUtil.getSingleParameter(param, "SHIPPING_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
        barcodePrinter = ReportUtil.getSingleParameter(param, "BARCODE");
        manifest = ReportUtil.getSingleParameter(param, "MANIFEST");
        shippingLabel = ReportUtil.getSingleParameter(param, "SHIPPING_LABEL");
        requestForm = ReportUtil.getSingleParameter(param, "REQUESTFORM");
        instruction = ReportUtil.getSingleParameter(param, "INSTRUCTION");

        if (DataBaseUtil.isEmpty(shippingIdStr) || DataBaseUtil.isEmpty(printer) ||
            DataBaseUtil.isEmpty(barcodePrinter))
            throw new InconsistencyException("You must specify the shipping id, printer and barcode printer for this report");

        printManifest = printOption(manifest);
        printLabel = printOption(shippingLabel);
        printReqform = printOption(requestForm);
        printInstr = printOption(instruction);

        //
        // find the shipping record
        //
        try {
            shippingId = Integer.parseInt(shippingIdStr);
            shipData = shipping.fetchById(shippingId);
        } catch (NotFoundException e) {
            throw new NotFoundException("A shipping record with id " + shippingIdStr +
                                        " does not exist");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        /*
         * start the report
         */
        con = null;
        try {
            if (printManifest) {
                status.setMessage("Initializing report");

                con = ReportUtil.getConnection(ctx);
                url = ReportUtil.getResourceURL("org/openelis/report/shipping/main.jasper");
                dir = ReportUtil.getResourcePath(url);

                tempFile = File.createTempFile("shippingManifest", ".pdf", new File("/tmp"));

                jparam = new HashMap<String, Object>();
                jparam.put("SHIPPING_ID", shippingIdStr);
                jparam.put("SUBREPORT_DIR", dir);

                status.setMessage("Loading report");

                jreport = (JasperReport)JRLoader.loadObject(url);
                jprint = JasperFillManager.fillReport(jreport, jparam, con);
                jexport = new JRPdfExporter();
                jexport.setParameter(JRExporterParameter.OUTPUT_STREAM,
                                     new FileOutputStream(tempFile));
                jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

                status.setMessage("Outputing report").setPercentComplete(20);

                jexport.exportReport();

                status.setPercentComplete(100);

                if (ReportUtil.isPrinter(printer)) {
                    //
                    // print the manifest
                    //
                    printstat = ReportUtil.print(tempFile, printer, 1);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                } else {
                    tempFile = ReportUtil.saveForUpload(tempFile);
                    status.setMessage(tempFile.getName())
                          .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                          .setStatus(ReportStatus.Status.SAVED);
                }
            }

            orderIds = null;
            if (printLabel) {
                numpkg = shipData.getNumberOfPackages();
                //
                // print the barcode labels
                //
                if (numpkg > 0) {                    
                    labLocDict = dictionaryCache.getById(shipData.getShippedFromId());
                    
                    try {
                        shipFrom = organization.fetchById(Integer.parseInt(labLocDict.getCode()));                        
                    } catch (Exception e) {
                        throw new InconsistencyException("Illegal ship from id specified in the dictionary for location "+ labLocDict.getEntry());
                    }
                                        
                    methodId = shipData.getShippedMethodId();
                    method = methodId != null ? dictionaryCache.getById(methodId).getEntry() : "";
                    
                    shipFromAddr = shipFrom.getAddress();
                    fromAptSuite = shipFromAddr.getMultipleUnit() != null ? shipFromAddr.getMultipleUnit() : "" ;
                    fromStreetAddr = shipFromAddr.getStreetAddress() != null ? shipFromAddr.getStreetAddress() : "";
                    
                    shipTo = shipData.getShippedTo();
                    shipToAddr = shipTo.getAddress();
                    toAptSuite = shipToAddr.getMultipleUnit();
                    toStreetAddr = shipToAddr.getStreetAddress();
                    
                    if (toAptSuite != null)
                        toStreetAddr = DataBaseUtil.concatWithSeparator(toAptSuite, " ", toStreetAddr);
                    
                    orderList = order.fetchByShippingId(shippingId);
                    orderIds = new ArrayList<Integer>();
                    costCenter = null;
                    /*
                     * find the cost center from the first order that has one defined
                     * and create a list of the order ids that may be needed later
                     */
                    for (OrderViewDO o : orderList) {
                        orderIds.add(o.getId());
                        if (o.getCostCenterId() != null && costCenter == null)
                            costCenter = dictionaryCache.getById(o.getCostCenterId()).getEntry();
                    }
                    
                    tempFile = File.createTempFile("shippingAddresslabel", ".txt", new File("/tmp"));
                    ps = new PrintStream(tempFile);
                    for (int i = 0; i < numpkg; i++ ) {
                        labelReport.shippingAddressLabel(ps, shipFrom.getName(), method, costCenter,
                                                         fromAptSuite, "SH" + shippingIdStr,
                                                         fromStreetAddr, shipFromAddr.getCity(),
                                                         shipFromAddr.getState(),
                                                         shipFromAddr.getZipCode(),
                                                         shipData.getShippedToAttention(),
                                                         shipTo.getName(), toStreetAddr,
                                                         shipToAddr.getCity(),
                                                         shipToAddr.getState(), 
                                                         shipToAddr.getZipCode());
                    }
                    ps.close();
                    printstat = ReportUtil.print(tempFile, barcodePrinter, 1);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                }
            }

            if (printReqform || printInstr) {
                uriPath = ReportUtil.getSystemVariableValue("inventory_uri_directory");
                
                if (orderIds == null) {
                    orderList = order.fetchByShippingId(shippingId);
                    orderIds = new ArrayList<Integer>();
                    for (OrderViewDO o : orderList) 
                        orderIds.add(o.getId());                    
                }
                
                itemList = orderItem.fetchByOrderIds(orderIds);
                prevOrderId = null;                
                for (OrderItemViewDO data : itemList) {
                    orderId = data.getOrderId();
                    if ( !orderId.equals(prevOrderId) && printReqform)
                        //
                        // print the order request form
                        //
                        runRequestFormReport(orderId, printer);

                    if (printInstr) {
                        
                        //
                        // print the file linked to this item if any
                        //

                        itemUri = data.getInventoryItemProductUri();
                        if (itemUri != null && itemUri.startsWith(PRN_PREFIX) &&
                            data.getQuantity() > 0) {
                            itemUri = itemUri.replaceAll(PRN_PREFIX, "");
                            tempFile = new File(uriPath, itemUri);
                            ReportUtil.printWithoutDelete(tempFile, printer, data.getQuantity());
                        }
                    }
                    prevOrderId = orderId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private void runRequestFormReport(Integer orderId, String printer) throws Exception {
        ArrayList<QueryData> list;
        QueryData field;

        list = new ArrayList<QueryData>();
        field = new QueryData();
        field.key = "ORDERID";
        field.query = orderId.toString();
        field.type = QueryData.Type.INTEGER;
        list.add(field);

        field = new QueryData();
        field.key = "PRINTER";
        field.query = printer;
        field.type = QueryData.Type.STRING;
        list.add(field);

        field = new QueryData();
        field.key = "USE_NUM_FORMS";
        field.type = QueryData.Type.STRING;
        /*
         * query needs to be specified even if it's redundant because otherwise
         * ReportUtil.getSingleParameter() for this parameter in the other bean
         * will return null and this parameter won't have any effect on the
         * report
         */
        field.query = "USE_NUM_FORMS";
        list.add(field);

        requestFormReport.runReport(list);
    }

    private boolean printOption(String option) {
        return DataBaseUtil.trim(option) != null && "Y".equals(option);
    }
}