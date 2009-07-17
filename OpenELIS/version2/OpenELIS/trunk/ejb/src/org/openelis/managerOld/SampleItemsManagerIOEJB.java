package org.openelis.managerOld;

import java.util.List;

import javax.naming.InitialContext;

import org.openelis.local.SampleItemLocal;
import org.openelis.managerOld.SampleItemsManager;
import org.openelis.managerOld.SampleItemsManagerIOInt;

public class SampleItemsManagerIOEJB implements SampleItemsManagerIOInt {
    
    public List fetch(Integer sampleId) {
        SampleItemLocal sil = getSampleItemLocal();
        
        return sil.getItemsBySampleId(sampleId);
    }

    public Integer update(SampleItemsManager sampleItems) {
        SampleItemLocal sil = getSampleItemLocal();
        
        return sil.update(sampleItems);
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
