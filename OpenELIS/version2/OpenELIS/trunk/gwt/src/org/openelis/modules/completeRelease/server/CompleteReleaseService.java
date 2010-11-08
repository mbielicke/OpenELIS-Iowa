package org.openelis.modules.completeRelease.server;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ReportProgress;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleDataBundle;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CompleteReleaseRemote;
import org.openelis.remote.FinalReportBeanRemote;
import org.openelis.util.SessionManager;

public class CompleteReleaseService {
	private static final int rowPP = 500;

	public ArrayList<SampleDataBundle> query(Query query) throws Exception{
		 return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
	}
	
    private CompleteReleaseRemote remote() {
        return (CompleteReleaseRemote)EJBFactory.lookup("openelis/CompleteReleaseBean/remote");
    }
    
    private static FinalReportBeanRemote reportRemote() {
    	return (FinalReportBeanRemote)EJBFactory.lookup("openelis/FinalReportBean/remote");
    }
    
    public ReportProgress doFinalReport() throws Exception {
		ReportProgress rp = new ReportProgress();
		rp.name = "finalreport";
		rp.progress = 0;
		File tempFile = new File("/tmp/"+rp.name+SessionManager.getSession().getId()+".pdf");
		SessionManager.getSession().setAttribute(rp.name, rp);		
		FileOutputStream out = new FileOutputStream(tempFile);
		out.write(reportRemote().doFinalReport());
		out.close();
		rp.size = tempFile.length();
		return rp;
    }
    
    public ReportProgress getProgress() {
    	ReportProgress rp = (ReportProgress)SessionManager.getSession().getAttribute("finalreport");
    	rp.generated = reportRemote().getProgress();
    	return rp;
    }
    
    public RPC runReport(Integer in) {
        ArrayList<QueryData> paramList;
        QueryData param;       
        
        paramList = new ArrayList<QueryData>();
        param = new QueryData();
        param.key = "ACCESSION_NUMBER"; 
        param.query = "40";
        paramList.add(param);
        
        param = new QueryData();
        param.key = "ORGANIZATION_ID"; 
        param.query = "529";        
        paramList.add(param);
        
        try {
            reportRemote().runReport(paramList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return param;
    }
    
}
