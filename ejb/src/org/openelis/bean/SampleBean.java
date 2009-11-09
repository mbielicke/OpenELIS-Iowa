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
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.metamap.SampleMetaMap;
import org.openelis.remote.SampleRemote;

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
        sampleRefTableId = ReferenceTable.SAMPLE;
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
    
    public SampleDO add(SampleDO data) {
        Sample entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Sample();
        
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setClientReference(data.getClientReference());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setDomain(data.getDomain());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setPackageId(data.getPackageId());
        entity.setReceivedById(data.getReceivedById());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setStatusId(data.getStatusId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public SampleDO update(SampleDO data) throws Exception {
        Sample entity;
        
        if (!data.isChanged())
            return data;
        
        lockBean.validateLock(sampleRefTableId, data.getId());
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Sample.class, data.getId());

        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setClientReference(data.getClientReference());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setDomain(data.getDomain());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setPackageId(data.getPackageId());
        entity.setReceivedById(data.getReceivedById());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setStatusId(data.getStatusId());
        
        lockBean.giveUpLock(sampleRefTableId, data.getId());
        
        return data;
    }
    
    private void validate() throws Exception {
        
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
}
