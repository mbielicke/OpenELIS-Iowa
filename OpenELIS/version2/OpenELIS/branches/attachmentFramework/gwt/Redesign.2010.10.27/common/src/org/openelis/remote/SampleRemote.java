package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.IdAccessionVO;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface SampleRemote {
    public ArrayList<IdAccessionVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception;
}
