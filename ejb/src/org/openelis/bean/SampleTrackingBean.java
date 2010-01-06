package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleTrackingRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class SampleTrackingBean implements SampleTrackingRemote {

	@PersistenceContext(name = "openelis")
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	public ArrayList<SampleManager> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		Query query;
		QueryBuilderV2 builder;
		List list;
		ArrayList<SampleManager> managers;

		builder = new QueryBuilderV2();
		builder.setMeta(new SampleMeta());
		builder.setSelect("distinct " + SampleMeta.getId() + " ");
		builder.constructWhere(fields);
		builder.setOrderBy(SampleMeta.getId());

		query = manager.createQuery(builder.getEJBQL());
		query.setMaxResults(first + max);
		builder.setQueryParams(query, fields);

		list = query.getResultList();

		if (list.isEmpty())
			throw new NotFoundException();
		
		if (list == null)
			throw new LastPageException();
		
		managers = new ArrayList<SampleManager>(max);
		
		for(int i = first; i < list.size(); i++) {
			managers.add(SampleManager.fetchByIdWithItemsAnalyses((Integer)list.get(i)));
		}

		return managers;
	}

}
