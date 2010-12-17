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
package org.openelis.report.verification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemUserBean {
    
    public static String getLoginName(Connection con, Integer createdById,
                                      Integer createdForId) {
        String text;
        PreparedStatement a_st;
        ResultSet a_rs;                
        
        a_st = null;
        a_rs = null;
        text = null;
        
        try {
            a_st = con.prepareStatement("select s.login_name" +
            		                    " from security_dev:system_user s"+
                                        " where s.id = ? ");
            if (createdForId != null)
                a_st.setObject(1, createdForId);
            else 
                a_st.setObject(1, createdById);
            a_rs = a_st.executeQuery();
            a_rs.next();
            text  = (String)a_rs.getObject(1);          
        } catch (SQLException sqlE) {            
            sqlE.printStackTrace();            
        } finally {
            try {
                if (a_rs != null)
                    a_rs.close();
                if (a_st != null)
                    a_st.close();
            } catch (SQLException ignE) {
                ignE.printStackTrace();                
            }
        }
        return text;   
    }
}
