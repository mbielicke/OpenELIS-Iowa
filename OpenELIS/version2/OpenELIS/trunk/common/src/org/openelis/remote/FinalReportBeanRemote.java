package org.openelis.remote;

import javax.ejb.Remote;

@Remote
public interface FinalReportBeanRemote {

	public byte[]doFinalReport() throws Exception;
	
	public int getProgress();
}
