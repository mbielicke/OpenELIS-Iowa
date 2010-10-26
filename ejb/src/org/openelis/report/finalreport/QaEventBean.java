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
package org.openelis.report.finalreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QaEventBean {

    public static String getAnalysisQaeventText(Connection con, Integer analysisId) {        
        StringBuffer text;
        PreparedStatement a_st;
        ResultSet a_rs;
                
        a_st = null;
        a_rs = null;
        text = new StringBuffer();
        try {
            a_st = con.prepareStatement("select q.reporting_text "+
                                        " from analysis_qaevent aq, qaevent q"+
                                        " where q.id = aq.qaevent_id" +
                                        " and aq.analysis_id = ?" +
                                        " and aq.type_id not in" +
                                        " (select d.id from dictionary d where d.system_name = 'qaevent_internal')");
            a_st.setObject(1, analysisId);
            a_rs = a_st.executeQuery();
            while (a_rs.next()) {
                if (text.length() > 0)
                    text.append(" ");    
                text.append(a_rs.getString(1));
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
        
        if (text.length() > 0)
            return text.toString();
        else 
            return null; 
    }
    
    public static String getSampleQaeventText(Connection con, Integer sampleId) {        
        StringBuffer text;
        PreparedStatement a_st;
        ResultSet a_rs;
                
        a_st = null;
        a_rs = null;
        text = new StringBuffer();              
        try {            
            a_st = con.prepareStatement("select q.reporting_text "+
                                        " from sample_qaevent sq, qaevent q"+
                                        " where q.id = sq.qaevent_id" +
                                        " and sq.sample_id = ?" +
                                        " and sq.type_id not in" +
                                        " (select d.id from dictionary d where d.system_name = 'qaevent_internal')");
            a_st.setObject(1, sampleId);
            a_rs = a_st.executeQuery();            
            while (a_rs.next()) {
                if (text.length() > 0)
                    text.append(" ");                    
                text.append(a_rs.getString(1));
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
        
        if (text.length() > 0)
            return text.toString();
        else 
            return null; 
    }
    
}
