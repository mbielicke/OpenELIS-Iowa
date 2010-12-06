package org.openelis.modules.completeRelease.server;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleDataBundle;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CompleteReleaseRemote;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.ReportStatus;

public class CompleteReleaseService {
	private static final int rowPP = 500;

	public ArrayList<SampleDataBundle> query(Query query) throws Exception {
		 return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
	}
	
    private CompleteReleaseRemote remote() {
        return (CompleteReleaseRemote)EJBFactory.lookup("openelis/CompleteReleaseBean/remote");
    }       
}
