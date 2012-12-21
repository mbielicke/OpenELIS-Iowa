package org.openelis.report.finalreport;

import java.util.*;
import java.util.logging.Logger;

import org.openelis.gwt.common.DataBaseUtil;

import net.sf.jasperreports.engine.*;

public class OrganizationPrintDataSource implements JRDataSource {
    protected int                          i;
    protected Integer                      lastOrgId, zero;
    protected String                       lastFaxedPrinted, lastFaxNumber;
    protected Type                         type;
    protected ArrayList<OrganizationPrint> data;
    protected Iterator<OrganizationPrint>  iter;
    protected OrganizationPrint            op;
    
    public enum Type {FAX, PRINT, STATS};
    
    private static final Logger      log = Logger.getLogger("openelis");

    public OrganizationPrintDataSource(Type type) {
        this.type = type;
        zero = new Integer(0);
    }
    
    public ArrayList<OrganizationPrint> getData() {
        return data;
    }
    public void setData(ArrayList<OrganizationPrint> data) {
        this.data = data;
        if (data != null) {
            iter = data.iterator();
            i = 0;
            lastOrgId = new Integer(-1);
            lastFaxedPrinted = "";
            lastFaxNumber = "";
        }
    }
    
    public boolean next() throws JRException {
        while (iter != null && iter.hasNext()) {
            i++;
            log.fine("Processed "+i+" records in report");
            op = (OrganizationPrint) iter.next();
            if ((Type.PRINT.equals(type) && DataBaseUtil.isEmpty(op.getFaxNumber())) ||
                (Type.FAX.equals(type) && !DataBaseUtil.isEmpty(op.getFaxNumber()))) {
                return true;
            } else if (Type.STATS.equals(type)) {
                try {
                    while (lastOrgId.equals(op.getOrganizationId()) && !op.getOrganizationId().equals(zero) &&
                           lastFaxedPrinted.equals(op.getFaxNumber() != null ? "Faxed" : "Printed") &&
                           "Printed".equals(lastFaxedPrinted)) {
                        op = (OrganizationPrint) iter.next();
                    }
                    lastOrgId = op.getOrganizationId();
                    lastFaxedPrinted = op.getFaxNumber() != null ? "Faxed" : "Printed";
                    if (op.getFaxNumber() != null)
                        lastFaxNumber = op.getFaxNumber();
                    else
                        lastFaxNumber = "";
                    return true;
                } catch (NoSuchElementException nseE) {
                    // reached end of list
                }
            }
        }
        return false;
    }

    public Object getFieldValue(JRField field) throws JRException {
        if ("organization_name".equals(field.getName()))
            return op.getOrganizationName();
        else if ("organization_id".equals(field.getName()))
            return op.getOrganizationId();
        else if ("sample_id".equals(field.getName()))
            return op.getSampleId();
        else if ("domain".equals(field.getName()))
            return op.getDomain();
        else if ("accession_number".equals(field.getName()))
            return op.getAccessionNumber();
        else if ("revision".equals(field.getName()))
            return op.getRevision();
        else if ("faxed_printed".equals(field.getName()))
            return op.getFaxNumber() != null ? "Faxed" : "Printed";
        else if ("fax_number".equals(field.getName()))
            return op.getFaxNumber();
        return null;
    }
}