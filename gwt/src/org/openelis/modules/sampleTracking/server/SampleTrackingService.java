package org.openelis.modules.sampleTracking.server;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleManager;
import org.openelis.server.EJBFactory;

public class SampleTrackingService {
	
	public ArrayList<SampleManager> query(Query query) throws Exception{
		 return EJBFactory.getSampleTracking().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
	}
}
