package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.SampleTrackingVO;
import org.openelis.gwt.common.data.QueryData;


@Remote
public interface SampleTrackingRemote {
	
	public ArrayList<SampleTrackingVO> query(ArrayList<QueryData> fields, int page, int maxRows) throws Exception;

}
