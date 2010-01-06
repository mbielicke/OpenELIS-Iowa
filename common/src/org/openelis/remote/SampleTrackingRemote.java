package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;


@Remote
public interface SampleTrackingRemote {
	
	public ArrayList<SampleManager> query(ArrayList<QueryData> fields, int page, int maxRows) throws Exception;

}
