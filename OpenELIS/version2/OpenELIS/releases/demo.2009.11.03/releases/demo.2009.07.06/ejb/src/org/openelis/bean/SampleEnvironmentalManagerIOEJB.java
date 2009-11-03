package org.openelis.bean;

import javax.naming.InitialContext;

import org.openelis.domain.AddressDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.local.AddressLocal;
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
    
    public AddressDO getAddressById(Integer id) {
        AddressLocal al = getAddressLocal();
        return al.getAddress(id);
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
    
    private AddressLocal getAddressLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (AddressLocal)ctx.lookup("openelis/AddressBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
