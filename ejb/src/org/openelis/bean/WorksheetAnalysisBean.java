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
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.entity.WorksheetAnalysis;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.metamap.WorksheetAnalysisMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetAnalysisBean implements WorksheetAnalysisLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    private static final WorksheetAnalysisMetaMap meta = new WorksheetAnalysisMetaMap();

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisDO> fetchByWorksheetItemId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByWorksheetItemId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public WorksheetAnalysisDO add(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetAnalysis();
        entity.setWorksheetItemId(data.getWorksheetItemId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetAnalysisDO update(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetAnalysis.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());

        return data;
    }

    public void delete(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetAnalysis.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetAnalysisDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getReferenceId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getReferenceId()));
        if (DataBaseUtil.isEmpty(data.getReferenceTableId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getReferenceTableId()));
        
        if (list.size() > 0)
            throw list;
    }
}