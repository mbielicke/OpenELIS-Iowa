package org.openelis.bean;

import javax.naming.InitialContext;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleEnvironmentalManagerIOInt;

public class SampleEnvironmentalManagerIOEJB implements SampleEnvironmentalManagerIOInt {

    public SampleEnvironmentalDO fetch(Integer sampleId) {
        SampleEnvironmentalLocal sel = getSampleEnvironmentalLocal();
        return sel.getEnvBySampleId(sampleId);
    }

    public Integer update(SampleDomainInt domainManager) {
        SampleEnvironmentalLocal sel = getSampleEnvironmentalLocal();
        return sel.update(domainManager);
    }
    
    private SampleEnvironmentalLocal getSampleEnvironmentalLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleEnvironmentalLocal)ctx.lookup("openelis/SampleEnvironmentalBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
