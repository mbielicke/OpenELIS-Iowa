package org.openelis.bean;

import javax.naming.InitialContext;

import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleHumanDO;
import org.openelis.local.PatientLocal;
import org.openelis.local.ProviderLocal;
import org.openelis.local.SampleHumanLocal;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleHumanManagerIOInt;

public class SampleHumanManagerIOEJB implements SampleHumanManagerIOInt {

    public SampleHumanDO fetch(Integer sampleId) {
        SampleHumanLocal shl = getSampleHumanLocal();
        return shl.getHumanBySampleId(sampleId);
    }

    public Integer update(SampleDomainInt domainManager) {
        SampleHumanLocal shl = getSampleHumanLocal();
        return shl.update(domainManager);
    }

    public PatientDO fetchPatient(Integer patientId) {
        PatientLocal pl = getPatientLocal();
        return pl.getPatientById(patientId);
    }

    public ProviderDO fetchProvider(Integer providerId) {
        ProviderLocal pl = getProviderLocal();
        return pl.getProvider(providerId);
    }
    
    private SampleHumanLocal getSampleHumanLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleHumanLocal)ctx.lookup("openelis/SampleHumanBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private PatientLocal getPatientLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (PatientLocal)ctx.lookup("openelis/PatientBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private ProviderLocal getProviderLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (ProviderLocal)ctx.lookup("openelis/ProviderBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }

}
