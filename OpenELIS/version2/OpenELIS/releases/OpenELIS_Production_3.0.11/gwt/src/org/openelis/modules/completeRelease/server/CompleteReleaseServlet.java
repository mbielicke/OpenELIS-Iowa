package org.openelis.modules.completeRelease.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.CompleteReleaseBean;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.SampleDataBundle;
import org.openelis.modules.completeRelease.client.CompleteReleaseServiceInt;

@WebServlet("/openelis/completeRelease")
public class CompleteReleaseServlet extends RemoteServlet implements CompleteReleaseServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    CompleteReleaseBean completeRelease;

    public ArrayList<SampleDataBundle> query(Query query) throws Exception {
        try {		 
            return completeRelease.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }     
}
