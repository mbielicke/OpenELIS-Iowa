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
package org.openelis.web.modules.dataView.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.DataViewBean;
import org.openelis.bean.SystemVariableBean;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.web.modules.dataView.client.DataViewServiceInt;

@WebServlet("/openelisweb/dataViewReport")
public class DataViewReportServlet extends RemoteServlet implements DataViewServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    SystemVariableBean systemVariable;
    
    @EJB
    DataViewBean       dataView;
    
    public ArrayList<IdNameVO> fetchEnvironmentalProjectListForWeb() throws Exception {
        return dataView.fetchEnvironmentalProjectListForWeb();  
    }
    
    public DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception {
        return dataView.fetchAnalyteAndAuxField(data);
    }
    
    public DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data) throws Exception {
        return dataView.fetchAnalyteAndAuxFieldForWebEnvironmental(data);
    }
    
    public ReportStatus runReport(DataViewVO data) throws Exception {
        ReportStatus st;

        st = dataView.runReport(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception {
        ReportStatus st;

        st = dataView.runReportForWebEnvironmental(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
}