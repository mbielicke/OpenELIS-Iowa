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
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.Sample;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.remote.SampleEnvironmentalRemote;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleEnvironmentalBean implements SampleEnvironmentalRemote, SampleEnvironmentalLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    public SampleEnvironmentalDO getEnvBySampleId(Integer sampleId){
        Query query = manager.createNamedQuery("SampleEnvironmental.SampleEnvironmentalBySampleId");
        query.setParameter("id", sampleId);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (SampleEnvironmentalDO)results.get(0);
    }
    
    public Integer update(SampleDomainInt sampleDomain){
        System.out.println("start env up method");
        SampleEnvironmentalManager envManager = (SampleEnvironmentalManager)sampleDomain;
        SampleEnvironmentalDO envDO = envManager.getEnvironmental();
        
        //validate the sample domain
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //update the sample domain
        SampleEnvironmental environmental = null;
        
        if (envDO.getId() == null)
            environmental = new SampleEnvironmental();
       else
           environmental = manager.find(SampleEnvironmental.class, envDO.getId());
        
        environmental.setAddressId(envDO.getAddressId());
        environmental.setCollector(envDO.getCollector());
        environmental.setCollectorPhone(envDO.getCollectorPhone());
        environmental.setDescription(envDO.getDescription());
        environmental.setIsHazardous(envDO.getIsHazardous());
        environmental.setSampleId(envManager.getSampleId());
        environmental.setSamplingLocation(envDO.getSamplingLocation());
        
        if(environmental.getId() == null)
            manager.persist(environmental);
        
        return environmental.getId();
    }
    
    public void validateDomain() throws Exception {
        
    }
}
