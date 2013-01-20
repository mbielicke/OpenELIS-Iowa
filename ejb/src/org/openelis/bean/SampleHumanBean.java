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

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SampleHumanDO;
import org.openelis.entity.SampleHuman;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleHumanManager;

@Stateless
@SecurityDomain("openelis")

public class SampleHumanBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;

    //declare the locals
    @EJB private LockBean lockBean;
    
    public SampleHumanDO getHumanBySampleId(Integer sampleId) {
        Query query = manager.createNamedQuery("SampleHuman.SampleHumanBySampleId");
        query.setParameter("id", sampleId);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (SampleHumanDO)results.get(0);
    }

    public Integer update(SampleDomainInt sampleDomain) {
        SampleHumanManager humanManager = (SampleHumanManager)sampleDomain;
        SampleHumanDO humanDO = humanManager.getHuman();
        
        //validate the sample domain
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //update the sample domain
        SampleHuman human = null;
        
        if (humanDO.getId() == null)
            human = new SampleHuman();
       else
           human = manager.find(SampleHuman.class, humanDO.getId());
        
        human.setPatientId(humanDO.getPatientId());
        human.setProviderId(humanDO.getProviderId());
        human.setProviderPhone(humanDO.getProviderPhone());
        human.setSampleId(humanDO.getSampleId());
        
        if(human.getId() == null)
            manager.persist(human);
        
        return human.getId();
    }
    
    public void validateDomain() throws Exception {
        
    }
}
