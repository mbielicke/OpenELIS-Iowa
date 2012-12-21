package org.openelis.modules.sampleTracking.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleTrackingBean;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sampleTracking.client.SampleTrackingServiceInt;

@WebServlet("/openelis/sampleTracking")
public class SampleTrackingServlet extends AppServlet implements SampleTrackingServiceInt {
	
    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleTrackingBean sampleTracking;

    public ArrayList<SampleManager> query(Query query) throws Exception{
		 return sampleTracking.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
	}
}
