package org.openelis.report.sdwisunload;

import java.util.*;
import net.sf.jasperreports.engine.*;

public class StatusDataSource implements JRDataSource {
    protected ArrayList<HashMap<String, Object>> statusList;
    protected HashMap<String, Object>            statusRow;
    protected Iterator<HashMap<String, Object>>  iter;
    
    public void setStatusList(ArrayList<HashMap<String, Object>> statusList) {
        this.statusList = statusList;
    }
    
    public boolean next() throws JRException {
        if (iter == null && statusList != null)
            iter = statusList.iterator();
        
        if (iter.hasNext())
            statusRow = (HashMap<String, Object>) iter.next();               
        else
            return false;
            
        return true;
    }

    public Object getFieldValue(JRField field) throws JRException {
        Object                  objValue = null;

        if (field != null && statusRow != null) {
            if (!statusRow.containsKey(field.getName()))
                throw new JRException("Unable to get value for field '" + field.getName());
            objValue = statusRow.get(field.getName());
        }
        
        return objValue;
    }

}
