package org.openelis.bean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PatientDO;

@Stateless
@SecurityDomain("openelis")

public class PatientBean {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    public PatientDO getPatientById(Integer patientId){
        Query query = manager.createNamedQuery("Patient.PatientById");
        query.setParameter("id", patientId);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (PatientDO)results.get(0);
    }
}