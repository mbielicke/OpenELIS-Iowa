package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.PatientDO;

@Remote
public interface PatientRemote {
    
    public PatientDO getPatientById(Integer patientId);
}
