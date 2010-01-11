package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface SampleRemote {
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception;
}
