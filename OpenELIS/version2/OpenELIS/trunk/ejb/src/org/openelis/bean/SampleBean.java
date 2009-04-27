package org.openelis.bean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleDO;
import org.openelis.entity.Sample;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.manager.SampleManager;
import org.openelis.remote.SampleRemote;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleBean implements SampleRemote, SampleLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    public SampleDO getSampleByAccessionLabNumber(Integer accessionLabNumber) {
        Query query = manager.createNamedQuery("Sample.SampleByAccessionNumber");
        query.setParameter("id", accessionLabNumber);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (SampleDO)results.get(0);
    }

    public SampleDO getSampleById(Integer sampleId) {
        Query query = manager.createNamedQuery("Sample.SampleById");
        query.setParameter("id", sampleId);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (SampleDO)results.get(0);
    }
    
    public SampleDO getSampleByIdAndLock(Integer sampleId) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "sample");
        lockBean.getLock((Integer)query.getSingleResult(),sampleId);
        
        return getSampleById(sampleId);
    }
    
    public SampleDO getSampleByIdAndUnlock(Integer sampleId) {
      //unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "sample");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),sampleId);
        
        return getSampleById(sampleId);
    }
    
    public Integer update(SampleManager sm){
        System.out.println("start sample up method");
        SampleDO sampleDO = sm.getSample();
        if(sampleDO == null)
            return null;
        
        //validate lock
        
        //validate entire sample structure
        //  validateSample();
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //update the sample
        Sample sample = null;
        
        if (sampleDO.getId() == null)
            sample = new Sample();
        else
           sample = manager.find(Sample.class, sampleDO.getId());
        System.out.println("["+sampleDO.getAccessionNumber()+"]");
        sample.setAccessionNumber(sampleDO.getAccessionNumber());
        System.out.println("["+sampleDO.getEnteredDate()+"]");
        sample.setClientReference(sampleDO.getClientReference());
        sample.setCollectionDate(sampleDO.getCollectionDate());
        sample.setCollectionTime(sampleDO.getCollectionTime());
        sample.setDomain(sampleDO.getDomain());
        sample.setEnteredDate(sampleDO.getEnteredDate());
        sample.setNextItemSequence(sampleDO.getNextItemSequence());
        sample.setPackageId(sampleDO.getPackageId());
        sample.setReceivedById(sampleDO.getReceivedById());
        sample.setReceivedDate(sampleDO.getReceivedDate());
        sample.setReleasedDate(sampleDO.getReleasedDate());
        sample.setRevision(sampleDO.getRevision());
        sample.setStatusId(sampleDO.getStatusId());
        
        if(sample.getId() == null)
            manager.persist(sample);
        
        //set the new parent id to children
        sm.getSampleItems().setSampleId(sample.getId());
        sm.getAdditionalDomain().setSampleId(sample.getId());
        sm.getOrganizations().setSampleId(sample.getId());
        sm.getProjects().setSampleId(sample.getId());
        sm.getQaEvents().setSampleId(sample.getId());
        
        //call the children update methods
        sm.getSampleItems().update();
        sm.getAdditionalDomain().update();
        //sample.getOrganizations().update();
        //sample.getProjects().update();
        //sample.getQaEvents().update();

        //return the sample id
        return sample.getId();
    }
    
    private void validateSample() throws Exception {
        
    }
}
