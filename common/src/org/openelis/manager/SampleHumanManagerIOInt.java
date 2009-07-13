package org.openelis.manager;

import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleHumanDO;

public interface SampleHumanManagerIOInt {
    public Integer update(SampleDomainInt domainManager);
    public SampleHumanDO fetch(Integer sampleId);
    public PatientDO fetchPatient(Integer patientId);
    public ProviderDO fetchProvider(Integer providerId);
}
