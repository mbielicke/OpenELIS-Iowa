package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.CompleteReleaseVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.CompleteReleaseMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.CompleteReleaseRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class CompleteReleaseBean implements CompleteReleaseRemote {

	@PersistenceContext(unitName = "openelis")
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	public ArrayList<SampleDataBundle> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		Query query;
		QueryBuilderV2 builder;
		List list;
		ArrayList<SampleDataBundle> bundleList;
		ArrayList<CompleteReleaseVO> voList;
		CompleteReleaseVO vo;
		HashMap<Integer, SampleManager> managerHash;
		SampleManager man;
	
		builder = new QueryBuilderV2();
		builder.setMeta(new CompleteReleaseMeta());
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
        voList = (ArrayList<CompleteReleaseVO>)DataBaseUtil.subList(list, first, max);
        if (voList == null)
            throw new LastPageException();
        
        managerHash = new HashMap<Integer, SampleManager>();
        bundleList = new ArrayList<SampleDataBundle>();
        for(int i = 0; i < voList.size(); i++){
            vo = voList.get(i);
            
            man = managerHash.get(vo.getSampleId());
            
            if(man == null){
                man = SampleManager.fetchWithItemsAnalyses(vo.getSampleId());
                managerHash.put(vo.getSampleId(), man);
            }
            
            bundleList.add(getAnalysisBundle(man, vo.getAnalysisId()));
        }
        
        return bundleList;
	}
	
	 private SampleDataBundle getAnalysisBundle(SampleManager man, Integer id) throws Exception {
	        SampleItemManager siManager;
	        int sindex, aindex;
	        
	        siManager = man.getSampleItems();
	        sindex = -1;
	        aindex = -1;
	        
	        for (int i = 0; i < siManager.count(); i++ ) {
	            for (int j = 0; j < siManager.getAnalysisAt(i).count(); j++ ) {
	                if (siManager.getAnalysisAt(i).getAnalysisAt(j).getId().equals(id)) {
	                    sindex = i;
	                    aindex = j;
	                    break;
	                }
	            }
	        }
	        if (sindex < -1)
	            return null;
	        return siManager.getAnalysisAt(sindex).getBundleAt(aindex);   
	 }
}
