package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.PatientDO;

@Local
public interface PatientLocal {
    
    public PatientDO getPatientById(Integer patientId);
}
