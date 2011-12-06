package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ShippingDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.SessionCacheLocal;
import org.openelis.report.Prompt;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class ShippingReportBean {

    @EJB
    private SessionCacheLocal          session;

    @Resource
    private SessionContext             ctx;

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

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
            prn = PrinterList.getInstance().getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
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
        ShippingDO data;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String shippingId, printer;
        HashMap<String, QueryData> param;
        ArrayList<Object[]> results;
        ArrayList<OrganizationPrint> orgPrintList;

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

        shippingId = ReportUtil.getSingleParameter(param, "SHIPPING_ID");
        printer = ReportUtil.getSingleParameter(param, "BARCODE");

        if (DataBaseUtil.isEmpty(shippingId) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the shipping id and printer for this report");
        /*
         * find the shipping record
         */
        
        return status;
    }
}