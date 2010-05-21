package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.CompleteReleaseVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.ReviewReleaseMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.ReviewReleaseRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class ReviewReleaseBean implements ReviewReleaseRemote {

	@PersistenceContext(name = "openelis")
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	public ArrayList<CompleteReleaseVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		Query query;
		QueryBuilderV2 builder;
		List list;
	
		builder = new QueryBuilderV2();
		builder.setMeta(new ReviewReleaseMeta());
		builder.setSelect("distinct new org.openelis.domain.CompleteReleaseVO(" + SampleMeta.getId() + ", "+
																				SampleMeta.getAnalysisId() +", " +
																				SampleMeta.getAccessionNumber()+", " +
																				SampleMeta.getAnalysisTestName()+"," +
																			    SampleMeta.getAnalysisMethodName()+","+
																				SampleMeta.getAnalysisStatusId()+","+
																				SampleMeta.getStatusId()+") ");
		builder.constructWhere(fields);
		builder.setOrderBy(SampleMeta.getAccessionNumber() + ", " + SampleMeta.getAnalysisTestName() + ", "+ SampleMeta.getAnalysisMethodName());
		query = manager.createQuery(builder.getEJBQL());  		
         																					   
		query.setMaxResults(first + max);
		builder.setQueryParams(query, fields);

		list = query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<CompleteReleaseVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<CompleteReleaseVO>)list;
	}

}
