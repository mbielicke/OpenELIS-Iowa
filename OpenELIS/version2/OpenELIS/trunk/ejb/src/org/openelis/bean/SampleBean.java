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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleDO;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.metamap.SampleMetaMap;
import org.openelis.remote.SampleRemote;
import org.openelis.utils.ReferenceTableCache;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleBean implements SampleRemote, SampleLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    private static int sampleRefTableId;
    
    private static final SampleMetaMap sampleMeta = new SampleMetaMap();
    
    public SampleBean(){
        sampleRefTableId = ReferenceTableCache.getReferenceTable("sample");
    }
    
    public SampleDO fetchById(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("Sample.SampleById");
        query.setParameter("id", sampleId);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Query query = manager.createNamedQuery("Sample.SampleByAccessionNumber");
        query.setParameter("id", accessionNumber);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public void add(SampleDO sampleDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Sample sample = new Sample();
        

        sample.setAccessionNumber(sampleDO.getAccessionNumber());
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
        
        manager.persist(sample);
        
        sampleDO.setId(sample.getId());
    }

    public void update(SampleDO sampleDO) throws Exception {
        lockBean.validateLock(sampleRefTableId, sampleDO.getId());
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Sample sample = manager.find(Sample.class, sampleDO.getId());

        sample.setAccessionNumber(sampleDO.getAccessionNumber());
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
        
        lockBean.giveUpLock(sampleRefTableId, sampleDO.getId());
    }
    
    public void validateAccessionNumber(Integer accessionNumber) throws Exception {
        Query query = manager.createNamedQuery("Sample.AccessionNumberCheck");
        query.setParameter("id", accessionNumber);
        
        List list = query.getResultList();
        
        if(list.size() > 0){
            ValidationErrorsList errorsList = new ValidationErrorsList();
            errorsList.add(new FieldErrorException("accessionNumberDuplicate", sampleMeta.getAccessionNumber()));
            
            throw errorsList;
        }
    }
    
    private void validateSample() throws Exception {
        
    }
}
