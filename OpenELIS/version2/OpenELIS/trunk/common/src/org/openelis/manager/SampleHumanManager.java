package org.openelis.manager;

import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleHumanDO;
import org.openelis.gwt.common.RPC;

public class SampleHumanManager implements RPC, SampleDomainInt {

    private static final long                    serialVersionUID = 1L;

    protected Integer                            sampleId;
    protected ProviderDO                         provider;
    protected PatientDO                          patient;
    protected SampleHumanDO                      human;

    protected transient SampleHumanManagerIOInt manager;

    /**
     * Creates a new instance of this object.
     */
    public static SampleHumanManager getInstance() {
        SampleHumanManager shm;

        shm = new SampleHumanManager();

        return shm;
    }

    public void setSampleId(Integer id) {
        sampleId = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public ProviderDO getProvider() {
        fetch();
        if(provider == null)
            provider = manager().fetchProvider(human.getProviderId());
        
        return provider;
    }

    public void setProvider(ProviderDO provider) {
        this.provider = provider;
    }

    public PatientDO getPatient() {
        fetch();
        
        if(patient == null)
            patient = manager().fetchPatient(human.getPatientId());
        
        return patient;
    }

    public void setPatient(PatientDO patient) {
        this.patient = patient;
    }

    public SampleHumanDO getHuman() {
        fetch();
        return human;
    }

    //manager methods
    public Integer update() {
        Integer newId = manager().update(this);
        human.setId(newId);
        
        return newId;
    }
    
    public SampleHumanDO fetch(){
        human = manager().fetch(sampleId);
        return human;
    }
    
    private SampleHumanManagerIOInt manager(){
        if(manager == null)
            manager = ManagerFactory.getSampleHumanManagerIO();
        
        return manager;
    }
}
