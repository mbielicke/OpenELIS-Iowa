package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.QueryData;

@Remote
public interface FinalReportBeanRemote {

	public byte[]doFinalReport() throws Exception;
	
	public int getProgress();
	
	public void runReport(ArrayList<QueryData> paramList) throws Exception;
}
