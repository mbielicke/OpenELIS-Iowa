package org.openelis.modules.sampleTracking.server;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleTrackingRemote;

public class SampleTrackingService {
	
	public ArrayList<SampleManager> query(Query query) throws Exception{
		 return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
	}
	
    private SampleTrackingRemote remote() {
        return (SampleTrackingRemote)EJBFactory.lookup("openelis/SampleTrackingBean/remote");
    }
}
