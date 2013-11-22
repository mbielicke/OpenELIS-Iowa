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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.entity.AnalysisQaevent;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class AnalysisQAEventBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public ArrayList<AnalysisQaEventViewDO> fetchByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("AnalysisQaevent.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaEventViewDO> fetchByAnalysisIds(ArrayList<Integer> analysisIds) {
        Query query;

        query = manager.createNamedQuery("AnalysisQaevent.FetchByAnalysisIds");
        query.setParameter("ids", analysisIds);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<AnalysisQaEventViewDO> fetchInternalByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("AnalysisQaevent.FetchInternalByAnalysisId");
        query.setParameter("id", analysisId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaEventViewDO> fetchExternalByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("AnalysisQaevent.FetchExternalByAnalysisId");
        query.setParameter("id", analysisId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaEventDO> fetchResultOverrideByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("AnalysisQaevent.FetchResultOverrideByAnalysisId");
        query.setParameter("id", analysisId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaEventDO> fetchResultOverrideByAnalysisIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List returnList;

        if (ids.size() == 0)
            throw new NotFoundException();

        query = manager.createNamedQuery("AnalysisQaevent.FetchResultOverrideByAnalysisIds");
        query.setParameter("ids", ids);

        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaevent> fetchResultOverrideBySampleIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List returnList;

        if (ids.size() == 0)
            throw new NotFoundException();

        query = manager.createNamedQuery("AnalysisQaevent.FetchResultOverrideBySampleIds");
        query.setParameter("ids", ids);

        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisQaevent> fetchResultOverrideBySampleId(Integer id) throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("AnalysisQaevent.FetchResultOverrideBySampleId");
        query.setParameter("id", id);

        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public AnalysisQaEventDO add(AnalysisQaEventDO data) throws Exception {
        AnalysisQaevent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AnalysisQaevent();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;

    }

    public AnalysisQaEventDO update(AnalysisQaEventDO data) throws Exception {
        AnalysisQaevent entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AnalysisQaevent.class, data.getId());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());

        return data;
    }

    public void delete(AnalysisQaEventDO data) throws Exception {
        AnalysisQaevent entity;
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AnalysisQaevent.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void validate(AnalysisQaEventViewDO data, Integer accession, Integer itemSequence,
                         AnalysisViewDO analysis)  throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        if (data.getTypeId() == null) {
            e.add(new FormErrorException(Messages.get()
                                                 .analysisQAEvent_typeRequiredException(accession,
                                                                                        itemSequence,
                                                                                        analysis.getTestName(),
                                                                                        analysis.getMethodName(),
                                                                                        data.getQaEventName())));
        }

        if (e.size() > 0)
            throw e;

    }
}