package org.openelis.modules.completeRelease.server;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleDataBundle;
import org.openelis.server.EJBFactory;

public class CompleteReleaseService {
	public ArrayList<SampleDataBundle> query(Query query) throws Exception {
		 return EJBFactory.getCompleteRelease().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
	}     
}
