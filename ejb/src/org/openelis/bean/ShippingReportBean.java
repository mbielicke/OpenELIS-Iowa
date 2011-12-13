package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
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

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.InventoryItemCacheLocal;
import org.openelis.local.LabelReportLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.RequestformReportLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.ShippingLocal;
import org.openelis.remote.ShippingReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.PrinterList;
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
    private OrderItemLocal          orderItem;

    @EJB
    private RequestformReportLocal  requestFormReport;

    @EJB
    private InventoryItemCacheLocal inventoryItemCache;
    
    @EJB
    private LabelReportLocal         labelReport;  
    
    @EJB
    private DictionaryCacheLocal     dictionaryCache;

    private final static String      PRN_PREFIX = "prn://";
    
    private static Integer           shipFromICId, shipFromAnkId;
    
    private static final Logger log  = Logger.getLogger(ShippingReportBean.class);

    @Resource
    private SessionContext          ctx;
    
    @PostConstruct
    public void init() {
        try {
            if (shipFromICId == null) {
                shipFromICId = dictionaryCache.getIdBySystemName("order_ship_from_ic");
                shipFromAnkId = dictionaryCache.getIdBySystemName("order_ship_from_ank");
            }
        }  catch (Exception e) {
            log.error("Failed to lookup constants for dictionary entries", e);
        }
    }

    /**
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SHIPPING_ID", Prompt.Type.INTEGER).setPrompt("Shipping Id:")
                                                                .setWidth(75)
                                                                .setRequired(true));
            prn = PrinterList.getInstance().getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            prn = PrinterList.getInstance().getListByType("zpl");
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
     * Just prints the manifest report
     */
    public ReportStatus runReportForManifest(ArrayList<QueryData> paramList) throws Exception {
        Integer shippingId;
        String shippingIdStr, printer, dir, printstat;
        URL url;
        File tempFile;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        HashMap<String, QueryData> param;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;

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
        if (DataBaseUtil.isEmpty(shippingIdStr) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException(
                                             "You must specify the shipping id and printer for this report");
        /*
         * find the shipping record
         */
        try {
            shippingId = Integer.parseInt(shippingIdStr);
            shipping.fetchById(shippingId);
        } catch (NotFoundException e) {
            throw new NotFoundException("A shipping record with id " + shippingIdStr +
                                        " does not exists");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        /*
         * start the report
         */
        con = null;
        try {
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
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setMessage("Outputing report").setPercentComplete(20);

            jexport.exportReport();

            status.setPercentComplete(100);

            //
            // print the manifest
            //
            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(tempFile, printer, 1);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
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

    /**
     * Prints the manifest report as well as all the other forms related to the 
     * shipping record, like order request forms and instructions
     */
    public ReportStatus runReportForProcessing(ArrayList<QueryData> paramList) throws Exception {
        int numpkg;
        Integer shippingId, orderId, prevOrderId, itemId, methodId;
        String shippingIdStr, printer, barcodePrinter, dir, printstat, itemUri, uriPath, 
               name, method, fromStreetAddress1, fromStreetAddress2, fromCity, fromState,
               fromZip, attention, toStreetAddress1, toAptSuite, toStreetAddress2, toCity, toState, toZip;
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
        ArrayList<OrderItemDO> orderItems;
        HashMap<Integer, InventoryItemDO> itemMap;
        InventoryItemDO item;
        AddressDO address;
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
        if (DataBaseUtil.isEmpty(shippingIdStr) || DataBaseUtil.isEmpty(printer) ||
            DataBaseUtil.isEmpty(barcodePrinter))
            throw new InconsistencyException("You must specify the shipping id, printer and barcode printer for this report");

        //
        // find the shipping record
        //
        try {
            shippingId = Integer.parseInt(shippingIdStr);
            shipData = shipping.fetchById(shippingId);
        } catch (NotFoundException e) {
            throw new NotFoundException("A shipping record with id " + shippingIdStr +
                                        " does not exists");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        /*
         * start the report
         */
        con = null;
        try {
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
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
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

                numpkg = shipData.getNumberOfPackages();
                //
                // initialize all parts of the text to be printed on barcode labels
                //
                name = "State Hygienic Laboratory";
                method = "";
                methodId = shipData.getShippedMethodId();
                if (methodId != null)
                    method = dictionaryCache.getById(methodId).getEntry();
                fromStreetAddress1 = null;
                fromStreetAddress2 = null;
                fromCity = null;
                fromZip = null;
                
                if (shipFromICId.equals(shipData.getShippedFromId())) {
                    fromStreetAddress1 = "University of Iowa Research Park";
                    fromStreetAddress2 = "2490 Crosspark Rd";
                    fromCity = "Coralville";
                    fromZip = "52241-4721";
                } else if (shipFromAnkId.equals(shipData.getShippedFromId())) { 
                    fromStreetAddress1 = "Iowa Laboratories Complex";
                    fromStreetAddress2 = "2220 S. Ankeny Blvd";
                    fromCity = "Ankeny";
                    fromZip = "50023";
                }
                
                fromState = "IA";
                shipTo = shipData.getShippedTo();
                address = shipTo.getAddress();
                attention = toStreetAddress1 = shipTo.getName();
                toAptSuite = address.getMultipleUnit();
                toStreetAddress2 =  address.getStreetAddress();
                if (toAptSuite != null)
                    toStreetAddress2 = toAptSuite+" "+toStreetAddress2;
                toCity = address.getCity();
                toState = address.getState();
                toZip = address.getZipCode(); 
                
                //
                // print the barcode labels                
                //
                tempFile = File.createTempFile("shippingAddresslabel", ".txt", new File("/tmp"));
                ps = new PrintStream(tempFile);
                for (int i = 0; i < numpkg; i++) {
                    labelReport.shippingAddressLabel(ps, name, method, fromStreetAddress1, "SH"+shippingIdStr,
                                                     fromStreetAddress2, fromCity, fromState, fromZip, attention,
                                                     toStreetAddress1, toStreetAddress2, toCity, toState, toZip);
                }
                
                ps.close();
                
                printstat = ReportUtil.print(tempFile, barcodePrinter, 1);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
            }

            orderItems = null;
            itemMap = new HashMap<Integer, InventoryItemDO>();
            orderItems = orderItem.fetchByShippingId(shippingId);
            prevOrderId = null;
            uriPath = ReportUtil.getSystemVariableValue("inventory_uri_directory");
            for (OrderItemDO data : orderItems) {
                orderId = data.getOrderId();
                if ( !orderId.equals(prevOrderId))
                    //
                    // print the order request form
                    //
                    runRequestFormReport(orderId, printer);
                itemId = data.getInventoryItemId();
                item = itemMap.get(itemId);
                if (item == null) {
                    item = inventoryItemCache.getById(itemId);
                    itemMap.put(itemId, item);
                }
                //
                // print the file linked to this item if any
                //
                itemUri = item.getProductUri();
                if (itemUri != null && itemUri.startsWith(PRN_PREFIX) && data.getQuantity() > 0) {
                    itemUri = itemUri.replaceAll(PRN_PREFIX, "");
                    tempFile = new File(uriPath, itemUri);
                    ReportUtil.print(tempFile, printer, data.getQuantity());
                }
                prevOrderId = orderId;
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
         * will return null and this parameter won't have any effect on the report 
         */
        field.query = "USE_NUM_FORMS";
        list.add(field);

        requestFormReport.runReport(list);
    }
}