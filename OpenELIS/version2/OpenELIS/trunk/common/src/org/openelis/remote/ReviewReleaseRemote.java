package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.ReviewReleaseVO;
import org.openelis.gwt.common.data.QueryData;


@Remote
public interface ReviewReleaseRemote {
	
	public ArrayList<ReviewReleaseVO> query(ArrayList<QueryData> fields, int page, int maxRows) throws Exception;

}
