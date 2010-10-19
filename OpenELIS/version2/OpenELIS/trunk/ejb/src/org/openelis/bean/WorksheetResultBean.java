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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.entity.WorksheetResult;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetResultLocal;
import org.openelis.meta.WorksheetCompletionMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetResultBean implements WorksheetResultLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetResultViewDO> fetchByWorksheetAnalysisId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetResult.FetchByWorksheetAnalysisId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public WorksheetResultViewDO add(WorksheetResultViewDO data) throws Exception {
        WorksheetResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetResult();
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setResultRow(data.getResultRow());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue1(data.getValueAt(0));
        entity.setValue2(data.getValueAt(1));
        entity.setValue3(data.getValueAt(2));
        entity.setValue4(data.getValueAt(3));
        entity.setValue5(data.getValueAt(4));
        entity.setValue6(data.getValueAt(5));
        entity.setValue7(data.getValueAt(6));
        entity.setValue8(data.getValueAt(7));
        entity.setValue9(data.getValueAt(8));
        entity.setValue10(data.getValueAt(9));
        entity.setValue11(data.getValueAt(10));
        entity.setValue12(data.getValueAt(11));
        entity.setValue13(data.getValueAt(12));
        entity.setValue14(data.getValueAt(13));
        entity.setValue15(data.getValueAt(14));
        entity.setValue16(data.getValueAt(15));
        entity.setValue17(data.getValueAt(16));
        entity.setValue18(data.getValueAt(17));
        entity.setValue19(data.getValueAt(18));
        entity.setValue20(data.getValueAt(19));
        entity.setValue21(data.getValueAt(20));
        entity.setValue22(data.getValueAt(21));
        entity.setValue23(data.getValueAt(22));
        entity.setValue24(data.getValueAt(23));
        entity.setValue25(data.getValueAt(24));
        entity.setValue26(data.getValueAt(25));
        entity.setValue27(data.getValueAt(26));
        entity.setValue28(data.getValueAt(27));
        entity.setValue29(data.getValueAt(28));
        entity.setValue30(data.getValueAt(29));

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetResultViewDO update(WorksheetResultViewDO data) throws Exception {
        WorksheetResult entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetResult.class, data.getId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setResultRow(data.getResultRow());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue1(data.getValueAt(0));
        entity.setValue2(data.getValueAt(1));
        entity.setValue3(data.getValueAt(2));
        entity.setValue4(data.getValueAt(3));
        entity.setValue5(data.getValueAt(4));
        entity.setValue6(data.getValueAt(5));
        entity.setValue7(data.getValueAt(6));
        entity.setValue8(data.getValueAt(7));
        entity.setValue9(data.getValueAt(8));
        entity.setValue10(data.getValueAt(9));
        entity.setValue11(data.getValueAt(10));
        entity.setValue12(data.getValueAt(11));
        entity.setValue13(data.getValueAt(12));
        entity.setValue14(data.getValueAt(13));
        entity.setValue15(data.getValueAt(14));
        entity.setValue16(data.getValueAt(15));
        entity.setValue17(data.getValueAt(16));
        entity.setValue18(data.getValueAt(17));
        entity.setValue19(data.getValueAt(18));
        entity.setValue20(data.getValueAt(19));
        entity.setValue21(data.getValueAt(20));
        entity.setValue22(data.getValueAt(21));
        entity.setValue23(data.getValueAt(22));
        entity.setValue24(data.getValueAt(23));
        entity.setValue25(data.getValueAt(24));
        entity.setValue26(data.getValueAt(25));
        entity.setValue27(data.getValueAt(26));
        entity.setValue28(data.getValueAt(27));
        entity.setValue29(data.getValueAt(28));
        entity.setValue30(data.getValueAt(29));

        return data;
    }

    public void delete(WorksheetResultViewDO data) throws Exception {
        WorksheetResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetResult.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetResultViewDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getTestAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetResultTestAnalyteId()));
        if (DataBaseUtil.isEmpty(data.getResultRow()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetResultResultRow()));
        if (DataBaseUtil.isEmpty(data.getAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetResultAnalyteId()));
        
        if (list.size() > 0)
            throw list;
    }
}
