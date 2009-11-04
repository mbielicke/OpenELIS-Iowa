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
import javax.ejb.EJBs;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.entity.SampleItem;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleItemLocal;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
public class SampleItemBean implements SampleItemLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    public List<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("SampleItem.SampleItemBySampleId");
        query.setParameter("id", sampleId);
 
        List<SampleItemViewDO> returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return returnList;
    }

    public void add(SampleItemViewDO itemDO){
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleItem item = new SampleItem();
        
        item.setContainerId(itemDO.getContainerId());
        item.setContainerReference(itemDO.getContainerReference());
        item.setItemSequence(itemDO.getItemSequence());
        item.setQuantity(itemDO.getQuantity());
        item.setSampleId(itemDO.getSampleId());
        item.setSampleItemId(itemDO.getSampleItemId());
        item.setSourceOfSampleId(itemDO.getSourceOfSampleId());
        item.setSourceOther(itemDO.getSourceOther());
        item.setTypeOfSampleId(itemDO.getTypeOfSampleId());
        item.setUnitOfMeasureId(itemDO.getUnitOfMeasureId());
        
       manager.persist(item);
       itemDO.setId(item.getId());
    }
    
    public void update(SampleItemViewDO itemDO){
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleItem item = manager.find(SampleItem.class, itemDO.getId());
        
        item.setContainerId(itemDO.getContainerId());
        item.setContainerReference(itemDO.getContainerReference());
        item.setItemSequence(itemDO.getItemSequence());
        item.setQuantity(itemDO.getQuantity());
        item.setSampleId(itemDO.getSampleId());
        item.setSampleItemId(itemDO.getSampleItemId());
        item.setSourceOfSampleId(itemDO.getSourceOfSampleId());
        item.setSourceOther(itemDO.getSourceOther());
        item.setTypeOfSampleId(itemDO.getTypeOfSampleId());
        item.setUnitOfMeasureId(itemDO.getUnitOfMeasureId());
    }
    
    public void delete(SampleItemViewDO itemDO){
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleItem item = manager.find(SampleItem.class, itemDO.getId());
        
        if(item != null)
            manager.remove(item);
    }
    
    public void validate() throws Exception {
        
    }
}
