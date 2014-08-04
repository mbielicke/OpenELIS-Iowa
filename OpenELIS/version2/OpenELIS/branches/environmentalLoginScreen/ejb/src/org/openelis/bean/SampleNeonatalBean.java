/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
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
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.entity.SampleNeonatal;
import org.openelis.gwt.common.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleNeonatalBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private PatientBean   patient;

    @EJB
    private ProviderBean  provider;

    @SuppressWarnings("unchecked")
    public ArrayList<SampleNeonatalDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleNeonatalDO> result;
        ArrayList<SampleNeonatalDO> list;
        HashMap<Integer, ArrayList<SampleNeonatalDO>> pat, pro;

        query = manager.createNamedQuery("SampleNeonatal.FetchBySampleIds");
        query.setParameter("ids", sampleIds);
        result = query.getResultList();

        pat = new HashMap<Integer, ArrayList<SampleNeonatalDO>>();
        pro = new HashMap<Integer, ArrayList<SampleNeonatalDO>>();
        for (SampleNeonatalDO data : result) {
            if (data.getPatientId() != null) {
                list = pat.get(data.getPatientId());
                if (list == null) {
                    list = new ArrayList<SampleNeonatalDO>();
                    pat.put(data.getPatientId(), list);
                }
                list.add(data);
            }
            if (data.getNextOfKinId() != null) {
                list = pat.get(data.getNextOfKinId());
                if (list == null) {
                    list = new ArrayList<SampleNeonatalDO>();
                    pat.put(data.getNextOfKinId(), list);
                }
                list.add(data);
            }
            if (data.getProviderId() != null) {
                list = pro.get(data.getProviderId());
                if (list == null) {
                    list = new ArrayList<SampleNeonatalDO>();
                    pro.put(data.getProviderId(), list);
                }
                list.add(data);
            }
        }

        for (PatientDO p : patient.fetchByIds(pat.keySet())) {
            for (SampleNeonatalDO data : pat.get(p.getId())) {
                if (p.getId().equals(data.getPatientId()))
                    data.setPatient(p);
                if (p.getId().equals(data.getNextOfKinId()))
                    data.setNextOfKin(p);
            }
        }

        for (ProviderDO p : provider.fetchByIds(pro.keySet())) {
            for (SampleNeonatalDO data : pro.get(p.getId())) {
                if (p.getId().equals(data.getProviderId()))
                    data.setProvider(p);
            }
        }

        return DataBaseUtil.toArrayList(result);
    }

    public SampleNeonatalDO add(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleNeonatal();
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatient().getId());
        entity.setBirthOrder(data.getBirthOrder());
        entity.setGestationalAge(data.getGestationalAge());
        entity.setNextOfKinId(data.getNextOfKin().getId());
        entity.setNextOfKinRelationId(data.getNextOfKinRelationId());
        entity.setIsRepeat(data.getIsRepeat());
        entity.setIsNicu(data.getIsNicu());
        entity.setFeedingId(data.getFeedingId());
        entity.setWeightSign(data.getWeightSign());
        entity.setWeight(data.getWeight());
        entity.setIsTransfused(data.getIsTransfused());
        entity.setTransfusionDate(data.getTransfusionDate());
        entity.setCollectionAge(data.getCollectionAge());
        entity.setIsCollectionValid(data.getIsCollectionValid());
        entity.setProviderId(data.getProviderId());
        entity.setFormNumber(data.getFormNumber());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public SampleNeonatalDO update(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;

        if ( !data.isChanged() && !data.getPatient().isChanged() &&
            !data.getNextOfKin().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleNeonatal.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatient().getId());
        entity.setBirthOrder(data.getBirthOrder());
        entity.setGestationalAge(data.getGestationalAge());
        entity.setNextOfKinId(data.getNextOfKin().getId());
        entity.setNextOfKinRelationId(data.getNextOfKinRelationId());
        entity.setIsRepeat(data.getIsRepeat());
        entity.setIsNicu(data.getIsNicu());
        entity.setFeedingId(data.getFeedingId());
        entity.setWeightSign(data.getWeightSign());
        entity.setWeight(data.getWeight());
        entity.setIsTransfused(data.getIsTransfused());
        entity.setTransfusionDate(data.getTransfusionDate());
        entity.setCollectionAge(data.getCollectionAge());
        entity.setIsCollectionValid(data.getIsCollectionValid());
        entity.setProviderId(data.getProviderId());
        entity.setFormNumber(data.getFormNumber());

        return data;
    }

    public void delete(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleNeonatal.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(SampleNeonatalDO data) throws Exception {
        // TODO add logic for validation
    }
}