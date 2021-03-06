package org.openelis.bean;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

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
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.ShippingViewDO;
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
public class ShippingReportBean {

    @EJB
    private SessionCacheBean      session;

    @EJB
    private ShippingBean          shipping;

    @EJB
    private IOrderBean            iorder;

    @EJB
    private IOrderItemBean        iorderItem;

    @EJB
    private RequestformReportBean requestFormReport;

    @EJB
    private LabelReportBean       labelReport;

    @EJB
    private DictionaryCacheBean   dictionaryCache;

    @EJB
    private PrinterCacheBean      printer;

    @EJB
    private OrganizationBean      organization;

    private static final String   PRN_PREFIX = "print://";
    
    private static final Logger       log = Logger.getLogger("openelis");

    @Resource
    private SessionContext        ctx;

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
                                                          .setMultiSelect(false)
                                                          .setRequired(true));
            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Barcode Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
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
        String printer, barcodePrinter, dir, printstat, itemUri, uriPath, method, costCenter, userName;
        URL url;
        Path path;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        HashMap<String, QueryData> param;
        JasperReport jreport;
        JasperPrint jprint;
        ShippingViewDO shipData;
        ArrayList<Integer> orderIds;
        ArrayList<IOrderViewDO> orderList;
        ArrayList<IOrderItemViewDO> itemList;
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

        shippingId = ReportUtil.getIntegerParameter(param, "SHIPPING_ID");
        printer = ReportUtil.getStringParameter(param, "PRINTER");
        barcodePrinter = ReportUtil.getStringParameter(param, "BARCODE");
        printManifest = ReportUtil.getBooleanParameter(param, "MANIFEST");
        printLabel = ReportUtil.getBooleanParameter(param, "SHIPPING_LABEL");
        printReqform = ReportUtil.getBooleanParameter(param, "REQUESTFORM");
        printInstr = ReportUtil.getBooleanParameter(param, "INSTRUCTION");

        if (shippingId == null || DataBaseUtil.isEmpty(printer) ||
            DataBaseUtil.isEmpty(barcodePrinter))
            throw new InconsistencyException("You must specify the shipping id, printer and barcode printer for this report");

        //
        // find the shipping record
        //
        try {
            shipData = shipping.fetchById(shippingId);
        } catch (NotFoundException e) {
            throw new NotFoundException("A shipping record with id " + shippingId +
                                        " does not exist");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        /*
         * start the report
         */
        con = null;
        userName = User.getName(ctx);
        try {
            if (printManifest) {
                status.setMessage("Initializing report");

                con = ReportUtil.getConnection(ctx);
                url = ReportUtil.getResourceURL("org/openelis/report/shipping/main.jasper");
                dir = ReportUtil.getResourcePath(url);

                jparam = new HashMap<String, Object>();
                jparam.put("SHIPPING_ID", shippingId);
                jparam.put("SUBREPORT_DIR", dir);

                status.setMessage("Outputing report").setPercentComplete(20);

                jreport = (JasperReport)JRLoader.loadObject(url);
                jprint = JasperFillManager.fillReport(jreport, jparam, con);
                if (ReportUtil.isPrinter(printer))
                    path = export(jprint, null);
                else
                    path = export(jprint, "upload_stream_directory");

                status.setPercentComplete(100);

                if (ReportUtil.isPrinter(printer)) {
                    //
                    // print the manifest
                    //
                    printstat = ReportUtil.print(path, userName, printer, 1, true);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                } else {
                    status.setMessage(path.getFileName().toString())
                          .setPath(path.toString())
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
                        throw new InconsistencyException("Illegal ship from id specified in the dictionary for location " +
                                                         labLocDict.getEntry());
                    }

                    methodId = shipData.getShippedMethodId();
                    method = methodId != null ? dictionaryCache.getById(methodId).getEntry() : "";

                    shipFromAddr = shipFrom.getAddress();

                    shipTo = shipData.getShippedTo();
                    shipToAddr = shipTo.getAddress();

                    orderList = iorder.fetchByShippingId(shippingId);
                    orderIds = new ArrayList<Integer>();
                    costCenter = null;
                    /*
                     * find the cost center from the first order that has one
                     * defined and create a list of the order ids that may be
                     * needed later
                     */
                    for (IOrderViewDO o : orderList) {
                        orderIds.add(o.getId());
                        if (o.getCostCenterId() != null && costCenter == null)
                            costCenter = dictionaryCache.getById(o.getCostCenterId()).getEntry();
                    }

                    path = ReportUtil.createTempFile("shippingAddresslabel", ".txt", null);
                    ps = new PrintStream(Files.newOutputStream(path));
                    for (int i = 0; i < numpkg; i++ ) {
                        labelReport.shippingAddressLabel(ps,
                                                         shipFrom.getName(),
                                                         method,
                                                         costCenter,
                                                         shipFromAddr.getMultipleUnit(),
                                                         "SH" + shippingId,
                                                         shipFromAddr.getStreetAddress(),
                                                         shipFromAddr.getCity(),
                                                         shipFromAddr.getState(),
                                                         shipFromAddr.getZipCode(),
                                                         shipData.getShippedToAttention(),
                                                         shipTo.getName(),
                                                         shipToAddr.getMultipleUnit(),
                                                         shipToAddr.getStreetAddress(),
                                                         shipToAddr.getCity(),
                                                         shipToAddr.getState(),
                                                         shipToAddr.getZipCode());
                    }
                    ps.close();
                    printstat = ReportUtil.print(path, userName, barcodePrinter, 1, true);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                }
            }

            if (printReqform || printInstr) {
                uriPath = ReportUtil.getSystemVariableValue("inventory_uri_directory");

                if (orderIds == null) {
                    orderList = iorder.fetchByShippingId(shippingId);
                    orderIds = new ArrayList<Integer>();
                    for (IOrderViewDO o : orderList)
                        orderIds.add(o.getId());
                }

                itemList = iorderItem.fetchByIorderIds(orderIds);
                prevOrderId = null;
                for (IOrderItemViewDO data : itemList) {
                    orderId = data.getIorderId();
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
                            path = Paths.get(uriPath, itemUri);
                            ReportUtil.print(path, userName, printer, data.getQuantity(), false);
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
        field.setKey("ORDER_ID");
        field.setQuery(orderId.toString());
        field.setType(QueryData.Type.INTEGER);
        list.add(field);

        field = new QueryData();
        field.setKey("PRINTER");
        field.setQuery(printer);
        field.setType(QueryData.Type.STRING);
        list.add(field);

        field = new QueryData();
        field.setKey("USE_NUM_FORMS");
        field.setType(QueryData.Type.STRING);
        field.setQuery("Y");
        list.add(field);

        requestFormReport.runReport(list);
    }


    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("shippingManifest", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for shipping report");
            }
        }

        return path;
    }
}