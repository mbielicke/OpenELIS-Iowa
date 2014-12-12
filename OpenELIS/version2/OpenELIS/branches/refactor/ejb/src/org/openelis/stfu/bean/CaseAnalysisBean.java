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
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.entity.CaseAnalysis;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseAnalysisBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CaseAnalysisDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseAnalysis.fetchById");
    	query.setParameter("id",id);
    	
    	try {
    		return (CaseAnalysisDO)query.getSingleResult();
    	}catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseAnalysisDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseAnalysis.fetchByIds");
    	query.setParameter("ids", ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public ArrayList<CaseAnalysisDO> fetchByCaseId(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseAnalysis.fetchByCaseId");
    	query.setParameter("id", id);
    	
    	return toArrayList(query.getResultList());
    }
    
    public ArrayList<CaseAnalysisDO> fetchByCaseIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseAnalysis.fetchByCaseIds");
    	query.setParameter("ids", ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseAnalysisDO add(CaseAnalysisDO data) throws Exception {
    	CaseAnalysis entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = new CaseAnalysis();
    	entity.setAccessionNumber(data.getAccession());
    	entity.setOrganizationId(data.getOrganizationId());
    	entity.setTestId(data.getTestId());
    	entity.setStatusId(data.getStatusId());
    	entity.setCollectionDate(data.getCollectionDate());
    	entity.setCompletedDate(data.getCompletedDate());
    	entity.setConditionId(data.getConditionId());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CaseAnalysisDO update(CaseAnalysisDO data) throws Exception {
    	CaseAnalysis entity;
    	
    	if(!data.isChanged())
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CaseAnalysis.class,data.getId());
    	entity.setAccessionNumber(data.getAccession());
    	entity.setOrganizationId(data.getOrganizationId());
    	entity.setTestId(data.getTestId());
    	entity.setStatusId(data.getStatusId());
    	entity.setCollectionDate(data.getCollectionDate());
    	entity.setCompletedDate(data.getCompletedDate());
    	entity.setConditionId(data.getConditionId());
    	
    	return data;
    }
    
    public void delete(CaseAnalysisDO data) throws Exception {
    	CaseAnalysis entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CaseAnalysis.class, data.getId());
    	if(entity != null)
    		manager.remove(entity);
    }
}
