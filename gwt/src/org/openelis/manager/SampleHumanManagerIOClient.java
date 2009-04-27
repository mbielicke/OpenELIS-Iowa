package org.openelis.manager;

import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleHumanDO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PatientRemote;
import org.openelis.remote.ProviderRemote;
import org.openelis.remote.SampleHumanRemote;

public class SampleHumanManagerIOClient implements SampleHumanManagerIOInt {

    public SampleHumanDO fetch(Integer sampleId) {
        SampleHumanRemote shr = getSampleHumanRemote();
        return shr.getHumanBySampleId(sampleId);
    }

    public Integer update(SampleDomainInt domainManager) {
        SampleHumanRemote shr = getSampleHumanRemote();
        return shr.update(domainManager);
    }

    public PatientDO fetchPatient(Integer patientId) {
        PatientRemote pr = getPatientRemote();
        return pr.getPatientById(patientId);
    }

    public ProviderDO fetchProvider(Integer providerId) {
        ProviderRemote pr = getProviderRemote();
        return pr.getProvider(providerId);
    }
    
    private SampleHumanRemote getSampleHumanRemote(){
        return (SampleHumanRemote)EJBFactory.lookup("openelis/SampleHumanBean/remote");
    }
    
    private ProviderRemote getProviderRemote(){
        return (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
    }
    
    private PatientRemote getPatientRemote(){
        return (PatientRemote)EJBFactory.lookup("openelis/PatientBean/remote");
    }

}
