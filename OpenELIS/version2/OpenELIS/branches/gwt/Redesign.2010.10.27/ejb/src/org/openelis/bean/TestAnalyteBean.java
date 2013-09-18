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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.entity.TestAnalyte;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.meta.TestMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestAnalyteBean implements TestAnalyteLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;

    public ArrayList<ArrayList<TestAnalyteViewDO>> fetchByTestId(Integer testId) throws Exception {
        int i, j, rg;
        Query query;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        ArrayList<TestAnalyteViewDO> list, ar;
        TestAnalyteViewDO data;

        j = -1;
        ar = null;
        grid = null;
        query = manager.createNamedQuery("TestAnalyte.FetchByTestId");

        query.setParameter("testId", testId);
        list = DataBaseUtil.toArrayList(query.getResultList());

        if (list.isEmpty())
            throw new NotFoundException();

        grid = new ArrayList<ArrayList<TestAnalyteViewDO>>();

        //
        // This code creates a two dimensional "grid" of TestAnalyteViewDOs. 
        // If a test analyte is marked as not being a column analyte, a list of 
        // DOs is created with it being the first element in the the list and the
        // list is added as a new row to the grid. If a test analyte is marked as 
        // being a column analyte it is added to an existing row that has as its
        // first element a DO representing a test analyte with the same row group
        // as this one and which is marked as not being a column analyte. 
        //
        for (i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            rg = data.getRowGroup();

            if (j != rg) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(data);
                grid.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(data.getIsColumn())) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(data);
                grid.add(ar);
                continue;
            }

            ar.add(data);
        }

        return grid;
    }

    public TestAnalyteViewDO add(TestAnalyteViewDO data) throws Exception {
        TestAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new TestAnalyte();

        entity.setRowGroup(data.getRowGroup());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setIsReportable(data.getIsReportable());
        entity.setResultGroup(data.getResultGroup());
        entity.setScriptletId(data.getScriptletId());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setIsColumn(data.getIsColumn());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;

    }

    public TestAnalyteViewDO update(TestAnalyteViewDO data) throws Exception {
        TestAnalyte entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestAnalyte.class, data.getId());
        entity.setRowGroup(data.getRowGroup());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setIsReportable(data.getIsReportable());
        entity.setResultGroup(data.getResultGroup());
        entity.setScriptletId(data.getScriptletId());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setIsColumn(data.getIsColumn());

        return data;
    }

    public void delete(TestAnalyteViewDO analyteDO) throws Exception {
        TestAnalyte entity;
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestAnalyte.class, analyteDO.getId());
        if (entity != null)
            manager.remove(entity);

    }

    public void validate(TestAnalyteViewDO anaDO) throws Exception {
        FieldErrorException exc;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        
        if (DataBaseUtil.isEmpty(anaDO.getAnalyteId())) {
            exc = new FieldErrorException("fieldRequiredException",
                                          TestMeta.getAnalyteAnalyteId());
            list.add(exc);
        }
        
        if (DataBaseUtil.isEmpty(anaDO.getTypeId())) {
            exc = new FieldErrorException("analyteTypeRequiredException",
                                          TestMeta.getAnalyteTypeId());
            list.add(exc);
        }
        
        if (DataBaseUtil.isEmpty(anaDO.getResultGroup())) {
            exc = new FieldErrorException("fieldRequiredException",
                                          TestMeta.getAnalyteResultGroup());
            list.add(exc);
        }

        if (list.size() > 0)
            throw list;
    }

}