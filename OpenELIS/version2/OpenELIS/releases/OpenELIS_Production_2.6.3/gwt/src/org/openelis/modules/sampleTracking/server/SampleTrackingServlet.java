package org.openelis.modules.sampleTracking.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleTrackingBean;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sampleTracking.client.SampleTrackingServiceInt;

@WebServlet("/openelis/sampleTracking")
public class SampleTrackingServlet extends RemoteServlet implements SampleTrackingServiceInt {
        
    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleTrackingBean sampleTracking;

    public ArrayList<SampleManager> query(Query query) throws Exception{
        try {            
            return sampleTracking.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
