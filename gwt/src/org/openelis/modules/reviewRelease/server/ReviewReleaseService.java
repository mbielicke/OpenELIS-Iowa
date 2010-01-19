package org.openelis.modules.reviewRelease.server;

import java.util.ArrayList;

import org.openelis.domain.ReviewReleaseVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ReviewReleaseRemote;

public class ReviewReleaseService {
	private static final int rowPP = 500;

	public ArrayList<ReviewReleaseVO> query(Query query) throws Exception{
		 return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
	}
	
    private ReviewReleaseRemote remote() {
        return (ReviewReleaseRemote)EJBFactory.lookup("openelis/ReviewReleaseBean/remote");
    }
}
