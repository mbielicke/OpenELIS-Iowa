package org.openelis.remote;

import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.CompleteReleaseVO;
import org.openelis.gwt.common.data.QueryData;


@Remote
public interface ReviewReleaseRemote {
	
	public ArrayList<CompleteReleaseVO> query(ArrayList<QueryData> fields, int page, int maxRows) throws Exception;
	

}
