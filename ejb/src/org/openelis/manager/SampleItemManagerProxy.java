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
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleItemLocal;
import org.openelis.manager.SampleItemManager.SampleItemListItem;

public class SampleItemManagerProxy {
    public SampleItemManager fetchBySampleId(Integer sampleId) throws Exception {
        SampleItemLocal sil = getSampleItemLocal();

        ArrayList<SampleItemViewDO> items = (ArrayList<SampleItemViewDO>)sil.fetchBySampleId(sampleId);
        SampleItemManager sim = SampleItemManager.getInstance();

        sim.addSampleItems(items);
        sim.setSampleId(sampleId);
        
        return sim;
    }
    
    public SampleItemManager add(SampleItemManager man) throws Exception {
        Integer sampleItemRefTableId;
        SampleItemLocal sil = getSampleItemLocal();
        SampleItemViewDO itemDO;
        SampleItemListItem item;
        
        sampleItemRefTableId = ReferenceTable.SAMPLE_ITEM;
        
        for(int i=0; i<man.count(); i++){
            itemDO = man.getSampleItemAt(i);
            itemDO.setSampleId(man.getSampleId());
            sil.add(itemDO);
            item = man.getItemAt(i);
            if(item.storage != null){
                man.getStorageAt(i).setReferenceId(itemDO.getId());
                man.getStorageAt(i).setReferenceTableId(sampleItemRefTableId);
                man.getStorageAt(i).add();
            }
            if(item.analysis != null){
                man.getAnalysisAt(i).setSampleItemId(itemDO.getId());
                man.getAnalysisAt(i).add();
            }
        }
        
        return man;
    }

    public SampleItemManager update(SampleItemManager man) throws Exception {
        Integer sampleItemRefTableId;
        SampleItemLocal sil = getSampleItemLocal();
        SampleItemViewDO itemDO;
        SampleItemListItem item;
        
        sampleItemRefTableId = ReferenceTable.SAMPLE_ITEM;
        
        for(int j=0; j<man.deleteCount(); j++)
            sil.delete(man.getDeletedAt(j).sampleItem);
        
        for(int i=0; i<man.count(); i++){
            itemDO = man.getSampleItemAt(i);
            
            if(itemDO.getId() == null){
                itemDO.setSampleId(man.getSampleId());
                sil.add(itemDO);
            }else
                sil.update(itemDO);

            item = man.getItemAt(i);
            if(item.storage != null){
                man.getStorageAt(i).setReferenceId(itemDO.getId());
                man.getStorageAt(i).setReferenceTableId(sampleItemRefTableId);
                man.getStorageAt(i).update();
            }
            
            if(item.analysis != null){
                man.getAnalysisAt(i).setSampleItemId(itemDO.getId());
                man.getAnalysisAt(i).update();
            }
        }

        return man;
    }
    
    public void validate(SampleItemManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private SampleItemLocal getSampleItemLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleItemLocal)ctx.lookup("openelis/SampleItemBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
