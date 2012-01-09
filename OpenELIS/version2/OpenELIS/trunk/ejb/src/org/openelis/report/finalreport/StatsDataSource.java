package org.openelis.report.finalreport;

import java.util.*;
import net.sf.jasperreports.engine.*;

public class StatsDataSource implements JRDataSource {
    protected ArrayList<OrganizationPrint> stats;
    protected Iterator<OrganizationPrint> iter;
    protected String           organizationName;
    protected Integer          pageCount, organizationId;
    
    public ArrayList<OrganizationPrint> getStats() {
        return stats;
    }
    public void setStats(ArrayList<OrganizationPrint> stats) {
        this.stats = stats;
    }
    
    public boolean next() throws JRException {
        OrganizationPrint ci;
        
        if (iter == null && stats != null)
            iter = stats.iterator();
        
        if (iter.hasNext()) {
            ci = (OrganizationPrint) iter.next();               
            organizationName = ci.getOrganizationName();
            pageCount = ci.getJprint().getPages().size();
            organizationId = ci.getOrganizationId();
        } else {
            return false;
        }
            
        return true;
    }

    public Object getFieldValue(JRField field) throws JRException {
        if ("CLIENT_NAME".equals(field.getName()))
            return organizationName;
        else if ("CLIENT_ID".equals(field.getName()))
            return organizationId;
        else if ("PAGES".equals(field.getName()))
            return pageCount;
        return null;
    }

}
