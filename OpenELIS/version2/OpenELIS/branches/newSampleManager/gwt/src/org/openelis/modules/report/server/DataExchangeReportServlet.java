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
package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.DataExchangeReportBean;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.dataExchange.client.DataExchangeReportServiceInt;

@WebServlet("/openelis/dataExchangeReport")
public class DataExchangeReportServlet extends RemoteServlet implements DataExchangeReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    DataExchangeReportBean dataExchangeReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        return dataExchangeReport.getPrompts();      
    }    
    
    public ReportStatus exportToLocation(Query query) throws Exception {
        Integer criteriaId;
        String numQuery, uriQuery, idQuery, accNums[];
        ArrayList<Integer> accList;
        
        numQuery = null;
        uriQuery = null;
        idQuery = null;
        
        for (QueryData qd : query.getFields()) {            
            if ("EXCHANGE_CRITERIA_ID".equals(qd.getKey()))
                idQuery = qd.query;
            else if ("ACCESSION_NUMBERS".equals(qd.getKey())) 
                numQuery = qd.query;
            else if ("DESTINATION_URI".equals(qd.getKey()))
                uriQuery = qd.query;
        }
        
        accList = null;
        if (numQuery != null) {
            /*
             * convert the comma separated list of accession numbers specified by
             * the user to a list of integers 
             */
            accNums = numQuery.split(",");
            accList = new ArrayList<Integer>();
            for (String s : accNums) {
                s = DataBaseUtil.trim(s);
                if (s != null)
                    accList.add(Integer.valueOf(s));                
            }
        }
        
        criteriaId = null;
        if (idQuery != null)
            criteriaId = Integer.valueOf(idQuery);                                           
        
        return dataExchangeReport.exportToLocation(accList, uriQuery, criteriaId);
    }
   
}


