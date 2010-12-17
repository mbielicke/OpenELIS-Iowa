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
package org.openelis.report.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionLocationBean {
    public static String getLocation (Connection con, Integer sectionId) {
        String text;
        PreparedStatement a_st;
        ResultSet a_rs;
                
        text = "";
        if (sectionId == null)
            return text;
        
        a_st = null;
        a_rs = null;
        
        try {
            a_st = con.prepareStatement("select s.name from section s"+
                                        " where s.id = ? ");
            a_st.setObject(1, sectionId);
            a_rs = a_st.executeQuery();
            a_rs.next();
            text  = (String)a_rs.getObject(1);

            if (text == null)
                return text;
            text = text.trim();
            if (text.endsWith("-ank")) {
                text = "Ankeny";
            } else if (text.endsWith("-ic")) {
                text = "Iowa City";
            } else if (text.endsWith("-lk")) {
                text = "Lakeside";
            }
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
