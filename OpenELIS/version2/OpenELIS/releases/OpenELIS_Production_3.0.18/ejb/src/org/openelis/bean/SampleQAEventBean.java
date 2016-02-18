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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.entity.SampleQaevent;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class SampleQAEventBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public ArrayList<SampleQaEventViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("SampleQaevent.FetchBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<SampleQaEventViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleQaEventViewDO> q;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleQaevent.FetchBySampleIds");
        q = new ArrayList<SampleQaEventViewDO>();
        r = DataBaseUtil.createSubsetRange(sampleIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", sampleIds.subList(r.get(i), r.get(i + 1)));
            q.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(q);
    }

    public ArrayList<SampleQaEventViewDO> fetchInternalBySampleId(Integer sampleId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("SampleQaevent.FetchInternalBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<SampleQaEventViewDO> fetchExternalBySampleId(Integer sampleId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("SampleQaevent.FetchExternalBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<SampleQaEventDO> fetchResultOverrideBySampleId(Integer id) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("SampleQaevent.FetchResultOverrideBySampleId");
        query.setParameter("id", id);

        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public SampleQaEventDO add(SampleQaEventDO data) throws Exception {
        SampleQaevent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleQaevent();
        entity.setSampleId(data.getSampleId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public SampleQaEventDO update(SampleQaEventDO data) throws Exception {
        SampleQaevent entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleQaevent.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        
        return data;
    }

    public void delete(SampleQaEventDO data) throws Exception {
        SampleQaevent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleQaevent.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }
    
    public void validate(SampleQaEventViewDO data, Integer accession) throws Exception {
        ValidationErrorsList e;
        
        e = new ValidationErrorsList();
        if (data.getTypeId() == null)
            e.add(new FormErrorException(Messages.get()
                                                 .sampleQAEvent_typeRequiredException(accession,
                                                                                      data.getQaEventName())));

        if (e.size() > 0)
            throw e;
    }
}