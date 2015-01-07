package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleClinicalDO;
import org.openelis.entity.SampleClinical;
import org.openelis.gwt.common.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleClinicalBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private PatientBean   patient;

    @EJB
    private ProviderBean  provider;
    
    @SuppressWarnings("unchecked")
    public ArrayList<SampleClinicalDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleClinicalDO> result;
        ArrayList<SampleClinicalDO> list;
        HashMap<Integer, ArrayList<SampleClinicalDO>> pat, pro;

        query = manager.createNamedQuery("SampleClinical.FetchBySampleIds");
        query.setParameter("ids", sampleIds);
        result = query.getResultList();

        pat = new HashMap<Integer, ArrayList<SampleClinicalDO>>();
        pro = new HashMap<Integer, ArrayList<SampleClinicalDO>>();
        for (SampleClinicalDO data : result) {
            if (data.getPatientId() != null) {
                list = pat.get(data.getPatientId());
                if (list == null) {
                    list = new ArrayList<SampleClinicalDO>();
                    pat.put(data.getPatientId(), list);
                }
                list.add(data);
            }
            if (data.getProviderId() != null) {
                list = pro.get(data.getProviderId());
                if (list == null) {
                    list = new ArrayList<SampleClinicalDO>();
                    pro.put(data.getProviderId(), list);
                }
                list.add(data);
            }
        }
        
        for (PatientDO p : patient.fetchByIds(pat.keySet())) {
            for (SampleClinicalDO data : pat.get(p.getId())) {
                if (p.getId().equals(data.getPatientId()))
                    data.setPatient(p);                
            }
        }

        for (ProviderDO p : provider.fetchByIds(pro.keySet())) {
            for (SampleClinicalDO data : pro.get(p.getId())) {
                if (p.getId().equals(data.getProviderId()))
                    data.setProvider(p);
            }
        }

        return DataBaseUtil.toArrayList(result);
    }

    public SampleClinicalDO add(SampleClinicalDO data) throws Exception {
        SampleClinical entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleClinical();
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatient().getId());
        entity.setProviderId(data.getProviderId());
        entity.setProviderPhone(data.getProviderPhone());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public SampleClinicalDO update(SampleClinicalDO data) throws Exception {
        SampleClinical entity;

        if (!data.isChanged() && !data.getPatient().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleClinical.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatient().getId());
        entity.setProviderId(data.getProviderId());
        entity.setProviderPhone(data.getProviderPhone());

        return data;
    }

    public void delete(SampleClinicalDO data) throws Exception {
        SampleClinical entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleClinical.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(SampleClinicalDO data) throws Exception {
        // TODO add logic for validation
    }
}
