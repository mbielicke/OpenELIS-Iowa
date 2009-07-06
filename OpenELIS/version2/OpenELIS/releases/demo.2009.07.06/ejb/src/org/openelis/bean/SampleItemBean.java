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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleItemDO;
import org.openelis.entity.Sample;
import org.openelis.entity.SampleItem;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.manager.SampleItemsManager;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.remote.SampleItemRemote;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class SampleItemBean implements SampleItemRemote, SampleItemLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final InventoryItemMetaMap invItemMap = new InventoryItemMetaMap();
   
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    public List getItemsBySampleId(Integer sampleId) {
        Query query = manager.createNamedQuery("SampleItem.SampleItemBySampleId");
        query.setParameter("id", sampleId);
 
        return query.getResultList();
    }

    public Integer update(SampleItemsManager sampleItems) {
        System.out.println("start sample item up method");
        //validate items
        
        //validate lock
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        for(int i=0; i<sampleItems.count(); i++){
            //update the sample item
            SampleItemDO itemDO = sampleItems.getSampleItemAt(i);
            SampleItem item = null;
            if (itemDO.getId() == null)
                item = new SampleItem();
            else
                item = manager.find(SampleItem.class, itemDO.getId());
            
            item.setContainerId(itemDO.getContainerId());
            item.setContainerReference(itemDO.getContainerReference());
            item.setItemSequence(itemDO.getItemSequence());
            item.setQuantity(itemDO.getQuantity());
            item.setSampleId(sampleItems.getSampleId());
            item.setSampleItemId(itemDO.getSampleItemId());
            item.setSourceOfSampleId(itemDO.getSourceOfSampleId());
            item.setSourceOther(itemDO.getSourceOther());
            item.setTypeOfSampleId(itemDO.getTypeOfSampleId());
            item.setUnitOfMeasureId(itemDO.getUnitOfMeasureId());
            
            if(item.getId() == null)
                manager.persist(item);
            
            //set the child records parent id
            //sampleItems.getAnalysisAt(i).setSampleItemId(item.getId());
            //sampleItems.getStorageAt(i).setSampleItemId(item.getId());
            
            //update child records
            // sampleItems.getAnalysisAt(0).update();
            // sampleItems.getStorageAt(0).update();
        }
        
        //return new sample item id
        return null;
    }
    
    public void validate() throws Exception {
        
    }

    //sample items manager methods
    public void fetch() {
        // TODO Auto-generated method stub
        
    }

    public Integer update() {
        System.out.println("test");
        // TODO Auto-generated method stub
        return null;
    }
}
