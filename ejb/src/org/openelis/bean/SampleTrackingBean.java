package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalysisVO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleItemVO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTrackingVO;
import org.openelis.entity.Analysis;
import org.openelis.entity.SampleItem;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleTrackingRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sampleenvironmental-select")
public class SampleTrackingBean implements SampleTrackingRemote {
	@PersistenceContext(name = "openelis")
	private EntityManager manager;



	@SuppressWarnings("unchecked")
	public ArrayList<SampleTrackingVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		Query query;
		QueryBuilderV2 builder;
		List list;

		builder = new QueryBuilderV2();
		builder.setMeta(new SampleMeta());
		builder.setSelect("distinct new org.openelis.domain.SampleTrackingVO(" + SampleMeta.getId() + ","+SampleMeta.getAccessionNumber()+") ");
		builder.constructWhere(fields);
		builder.addWhere(SampleMeta.getDomain() + "='" + SampleManager.ENVIRONMENTAL_DOMAIN_FLAG + "'");
		builder.setOrderBy(SampleMeta.getId());

		query = manager.createQuery(builder.getEJBQL());
		query.setMaxResults(first + max);
		builder.setQueryParams(query, fields);

		list = query.getResultList();

		if (list.isEmpty())
			throw new NotFoundException();
		list = (ArrayList<SampleTrackingVO>)DataBaseUtil.subList(list, first, max);
		if (list == null)
			throw new LastPageException();
		try {
		for(int i = 0; i < list.size(); i++) {	
			ArrayList<SampleItemVO> itemList = null;
			SampleTrackingVO vo = (SampleTrackingVO)list.get(i);
			Query siQuery = manager.createQuery("select new org.openelis.domain.SampleItemVO(s.id,s.itemSequence,s.containerDict.entry, s.sourceDict.entry, s.typeDict.entry) from SampleItem s where s.sampleId = :id order by s.itemSequence");
			siQuery.setParameter("id", vo.getId());
			List sis = siQuery.getResultList();
			if(!sis.isEmpty()) {
				itemList = (ArrayList<SampleItemVO>)DataBaseUtil.subList(sis,0,sis.size());
				ArrayList<AnalysisVO> analList = null; 
				for(int j = 0; j < sis.size(); j++) {
					SampleItemVO si = (SampleItemVO)sis.get(j);
					Query anQuery = manager.createQuery("select new org.openelis.domain.AnalysisVO(a.id,a.test.name, a.test.method.name,a.statusId) from Analysis a where a.sampleItemId = :id");
					anQuery.setParameter("id", si.getId());
					List ans = anQuery.getResultList();
					if(!ans.isEmpty())
						analList = (ArrayList<AnalysisVO>)DataBaseUtil.subList(ans, 0, ans.size());
					si.setAnalysis(analList);
				}
			}
			vo.setItems(itemList);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return (ArrayList<SampleTrackingVO>)list;
	}

}
