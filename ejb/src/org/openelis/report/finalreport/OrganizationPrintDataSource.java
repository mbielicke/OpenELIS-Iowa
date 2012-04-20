package org.openelis.report.finalreport;

import java.util.*;

import org.apache.log4j.Logger;
import org.openelis.gwt.common.DataBaseUtil;

import net.sf.jasperreports.engine.*;

public class OrganizationPrintDataSource implements JRDataSource {
    private int i;
    
    protected Type                         type;
    protected ArrayList<OrganizationPrint> data;
    protected Iterator<OrganizationPrint>  iter;
    protected OrganizationPrint            op;
    
    public enum Type {FAX, PRINT, STATS};
    
    private static final Logger log = Logger.getLogger(OrganizationPrintDataSource.class);

    public OrganizationPrintDataSource(Type type) {
        this.type = type;
    }
    
    public ArrayList<OrganizationPrint> getData() {
        return data;
    }
    public void setData(ArrayList<OrganizationPrint> data) {
        this.data = data;
        if (data != null) {
            iter = data.iterator();
            i = 0;
        }
    }
    
    public boolean next() throws JRException {
        while (iter != null && iter.hasNext()) {
            i++;
System.out.println("Processed "+i+" records in report");
log.info("Processed "+i+" records in report");
            op = (OrganizationPrint) iter.next();
            if ((Type.PRINT.equals(type) && DataBaseUtil.isEmpty(op.getFaxNumber())) ||
                (Type.FAX.equals(type) && !DataBaseUtil.isEmpty(op.getFaxNumber())) ||
                Type.STATS.equals(type))
                return true;
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
