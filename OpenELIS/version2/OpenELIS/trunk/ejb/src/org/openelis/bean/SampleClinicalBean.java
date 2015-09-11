package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SampleClinicalDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.entity.SampleClinical;
import org.openelis.ui.common.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleClinicalBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @SuppressWarnings("unchecked")
    public ArrayList<SampleClinicalViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleClinicalViewDO> c;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleClinical.FetchBySampleIds");
        c = new ArrayList<SampleClinicalViewDO>();         
        r = DataBaseUtil.createSubsetRange(sampleIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", sampleIds.subList(r.get(i), r.get(i + 1)));
            c.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(c);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<SampleClinicalViewDO> fetchByPatientIds(ArrayList<Integer> patientIds) {
        Query query;
        List<SampleClinicalViewDO> c;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleClinical.FetchByPatientIds");
        c = new ArrayList<SampleClinicalViewDO>();         
        r = DataBaseUtil.createSubsetRange(patientIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", patientIds.subList(r.get(i), r.get(i + 1)));
            c.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(c);
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
