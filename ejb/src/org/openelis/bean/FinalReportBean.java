package org.openelis.bean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.remote.FinalReportBeanRemote;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class FinalReportBean implements FinalReportBeanRemote {
	
	int progress;
	
	@EJB
	SessionCacheInt session;
	
	public byte[] doFinalReport() throws Exception {
		File pdfFile = new File("/home/tschmidt/jfreechart-1.0.0-rc1-US.pdf");
		FileInputStream inStream = new FileInputStream(pdfFile);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		int next;
		long size = pdfFile.length();
		System.out.println("size = "+size);
		long count = 0l;
		while((next = inStream.read()) > -1) {
			count++;
			stream.write(next);
			session.setAttribute("progress", (int)(count/(double)size*100.0));
		}
		inStream.close();
		return stream.toByteArray();
	}
	
	public int getProgress() {
		if(session.getAttribute("progress") == null)
			return 0;
		return (Integer)session.getAttribute("progress");
	}

}
