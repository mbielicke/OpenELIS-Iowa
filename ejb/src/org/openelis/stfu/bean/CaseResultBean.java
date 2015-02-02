package org.openelis.stfu.bean;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.stfu.domain.CaseResultDO;
import org.openelis.stfu.entity.CaseResult;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseResultBean {
	
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;

	public CaseResultDO fetchById(Integer id) throws Exception {
		Query query = manager.createNamedQuery("CaseResult.fetchById");
		query.setParameter("id",id);
		
		try {
			return (CaseResultDO)query.getSingleResult();
		} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
	}
	
	public ArrayList<CaseResultDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
		Query query = manager.createNamedQuery("CaseResult.fetchByIds");
		query.setParameter("ids",ids);
		
		return toArrayList(query.getResultList());
	}
	
	
	public ArrayList<CaseResultDO> fetchByAnalysisId(Integer id) throws Exception {
		Query query = manager.createNamedQuery("CaseResult.fetchByAnalysisId");
		query.setParameter("id",id);
		
		return toArrayList(query.getResultList());
	}
	
	public ArrayList<CaseResultDO> fetchByAnalysisIds(ArrayList<Integer> ids) throws Exception {
		Query query = manager.createNamedQuery("CaseResult.fetchByAnalysisIds");
		query.setParameter("ids",ids);
		
		return toArrayList(query.getResultList());
	}
	
	public CaseResultDO add(CaseResultDO data) throws Exception {
		CaseResult entity;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = new CaseResult();
		entity.setCaseAnalysisId(data.getCaseAnalysisId());
		entity.setTestAnalyteId(data.getTestAnalyteId());
		entity.setTestResultId(data.getTestResultId());
		entity.setRow(data.getRow());
		entity.setCol(data.getCol());
		entity.setIsReportable(data.getIsReportable());
		entity.setAnalyteId(data.getAnalyteId());
		entity.setTypeId(data.getTypeId());
		entity.setValue(data.getValue());
		
		manager.persist(entity);
		data.setId(data.getId());
		
		return data;
	}
	
	public CaseResultDO update(CaseResultDO data) throws Exception {
		CaseResult entity;
		
		if(!data.isChanged())
			return data;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = manager.find(CaseResult.class,data.getId());
		entity.setCaseAnalysisId(data.getCaseAnalysisId());
		entity.setTestAnalyteId(data.getTestAnalyteId());
		entity.setTestResultId(data.getTestResultId());
		entity.setRow(data.getRow());
		entity.setCol(data.getCol());
		entity.setIsReportable(data.getIsReportable());
		entity.setAnalyteId(data.getAnalyteId());
		entity.setTypeId(data.getTypeId());
		entity.setValue(data.getValue());
		
		return data;
	}
	
	public void delete(CaseResultDO data) throws Exception {
		CaseResult entity;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = manager.find(CaseResult.class, data.getId());
		if(entity != null)
			manager.remove(entity);
	}
}
