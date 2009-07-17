package org.openelis.managerOld;

import javax.naming.InitialContext;

import org.openelis.domain.SampleDO;
import org.openelis.local.SampleLocal;
import org.openelis.managerOld.SampleManager;
import org.openelis.managerOld.SampleManagerIOInt;

public class SampleManagerIOEJB implements SampleManagerIOInt {

    public SampleDO fetch(Integer sampleId) {
        SampleLocal sl = getSampleLocal();
        
        return sl.getSampleById(sampleId);
    }
    
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) {
        SampleLocal sl = getSampleLocal();

        return sl.getSampleByAccessionLabNumber(accessionNumber);
    }

    public SampleDO fetchForUpdate(Integer sampleId) throws Exception {
        SampleLocal sl = getSampleLocal();
        
        return sl.getSampleByIdAndLock(sampleId);
    }

    public Integer update(SampleManager sample) {
        SampleLocal sl = getSampleLocal();
        
        return sl.update(sample);
        
    }
    
    public SampleDO fetchAndUnlock(Integer sampleId) {
        SampleLocal sl = getSampleLocal();
        
        return sl.getSampleByIdAndUnlock(sampleId);
    }
    
    private SampleLocal getSampleLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleLocal)ctx.lookup("openelis/SampleBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }    
}
