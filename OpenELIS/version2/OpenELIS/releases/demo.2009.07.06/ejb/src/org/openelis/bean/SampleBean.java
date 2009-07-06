/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
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
        sm.getSampleItemsManager().setSampleId(sample.getId());
        sm.getAdditionalDomainManager().setSampleId(sample.getId());
        sm.getOrganizationsManager().setSampleId(sample.getId());
        sm.getProjectsManager().setSampleId(sample.getId());
        sm.getQaEventsManager().setSampleId(sample.getId());
        
        //call the children update methods
        sm.getSampleItemsManager().update();
        sm.getAdditionalDomainManager().update();
        //sample.getOrganizations().update();
        //sample.getProjects().update();
        //sample.getQaEvents().update();

        //return the sample id
        return sample.getId();
    }
    
    private void validateSample() throws Exception {
        
    }
}
