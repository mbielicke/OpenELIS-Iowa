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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.Constants;
import org.openelis.entity.AnalysisReportFlags;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.LockLocal;

@Stateless
@SecurityDomain("openelis")
public class AnalysisReportFlagsBean implements AnalysisReportFlagsLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private LockLocal     lock;

    public AnalysisReportFlagsDO fetchByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        AnalysisReportFlagsDO data;

        data = null;
        query = manager.createNamedQuery("AnalysisReportFlags.FetchByAnalysisId");
        query.setParameter("id", analysisId);

        try {
            data = (AnalysisReportFlagsDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    public AnalysisReportFlagsDO add(AnalysisReportFlagsDO data) throws Exception {
        AnalysisReportFlags entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AnalysisReportFlags();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setNotifiedReceived(data.getNotifiedReceived());
        entity.setNotifiedReleased(data.getNotifiedReleased());
        entity.setBilledDate(data.getBilledDate());
        entity.setBilledAnalytes(data.getBilledAnalytes());
        entity.setBilledZero(data.getBilledZero());

        manager.persist(entity);

        return data;
    }

    public AnalysisReportFlagsDO update(AnalysisReportFlagsDO data) throws Exception {
        AnalysisReportFlags entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().ANALYSIS_REPORT_FLAGS, data.getAnalysisId());
            return data;
        }

        validate(data);

        lock.validateLock(Constants.table().ANALYSIS_REPORT_FLAGS, data.getAnalysisId());

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AnalysisReportFlags.class, data.getAnalysisId());
        entity.setNotifiedReceived(data.getNotifiedReceived());
        entity.setNotifiedReleased(data.getNotifiedReleased());
        entity.setBilledDate(data.getBilledDate());
        entity.setBilledAnalytes(data.getBilledAnalytes());
        entity.setBilledZero(data.getBilledZero());

        lock.unlock(Constants.table().ANALYSIS_REPORT_FLAGS, data.getAnalysisId());

        return data;
    }

    public ArrayList<AnalysisReportFlagsDO> fetchBySampleAccessionNumbers(ArrayList<Integer> ids) throws Exception {
        Query query;

        if (ids.size() == 0)
            return new ArrayList<AnalysisReportFlagsDO>();

        query = manager.createNamedQuery("AnalysisReportFlags.FetchBySampleAccessionNumbers");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public AnalysisReportFlagsDO fetchForUpdateByAnalysisId(Integer analysisId) throws Exception {
        lock.lock(Constants.table().ANALYSIS_REPORT_FLAGS, analysisId);
        return fetchByAnalysisId(analysisId);
    }

    public AnalysisReportFlagsDO abortUpdate(Integer analysisId) throws Exception {
        lock.unlock(Constants.table().ANALYSIS_REPORT_FLAGS, analysisId);
        return fetchByAnalysisId(analysisId);
    }

    public void delete(AnalysisReportFlagsDO data) throws Exception {
        AnalysisReportFlags entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AnalysisReportFlags.class, data.getAnalysisId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(AnalysisReportFlagsDO data) throws Exception {
    }
}