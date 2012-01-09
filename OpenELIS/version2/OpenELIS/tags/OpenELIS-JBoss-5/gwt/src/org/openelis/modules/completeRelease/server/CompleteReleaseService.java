package org.openelis.modules.completeRelease.server;

import java.util.ArrayList;

import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleDataBundle;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CompleteReleaseRemote;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;

public class CompleteReleaseService {
	public ArrayList<SampleDataBundle> query(Query query) throws Exception {
		 return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
	}
	
    private CompleteReleaseRemote remote() {
        return (CompleteReleaseRemote)EJBFactory.lookup("openelis/CompleteReleaseBean/remote");
    }       
}
