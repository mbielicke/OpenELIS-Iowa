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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.WorksheetResultDO;
import org.openelis.entity.WorksheetResult;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetResultLocal;
import org.openelis.metamap.WorksheetResultMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetResultBean implements WorksheetResultLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    private static final WorksheetResultMetaMap meta = new WorksheetResultMetaMap();

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetResultDO> fetchByWorksheetAnalysisId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetResult.FetchByWorksheetAnalysisId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public WorksheetResultDO add(WorksheetResultDO data) throws Exception {
        WorksheetResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetResult();
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetResultDO update(WorksheetResultDO data) throws Exception {
        WorksheetResult entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetResult.class, data.getId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(WorksheetResultDO data) throws Exception {
        WorksheetResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetResult.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetResultDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getWorksheetAnalysisId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getWorksheetAnalysisId()));
        if (DataBaseUtil.isEmpty(data.getTestAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getTestAnalyteId()));
        if (DataBaseUtil.isEmpty(data.getIsColumn()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getIsColumn()));
        if (DataBaseUtil.isEmpty(data.getSortOrder()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getSortOrder()));
        if (DataBaseUtil.isEmpty(data.getAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getAnalyteId()));
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getTypeId()));
        
        if (list.size() > 0)
            throw list;
    }
}