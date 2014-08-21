package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleDataBundle;


@Remote
public interface CompleteReleaseRemote {
	public ArrayList<SampleDataBundle> query(ArrayList<QueryData> fields, int page, int maxRows) throws Exception;
	
}
