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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.entity.TestAnalyte;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestAnalyteBean implements TestAnalyteLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;

    public ArrayList<ArrayList<TestAnalyteViewDO>> fetchByTestId(Integer testId) throws Exception {
        Query query;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        ArrayList<TestAnalyteViewDO> list;
        int i, j, rg;
        TestAnalyteViewDO ado;
        ArrayList<TestAnalyteViewDO> ar;

        j = -1;
        ar = null;
        grid = null;
        query = manager.createNamedQuery("TestAnalyte.FetchByTestId");

        query.setParameter("testId", testId);
        list = DataBaseUtil.toArrayList(query.getResultList());

        if (list.isEmpty())
            throw new NotFoundException();

        grid = new ArrayList<ArrayList<TestAnalyteViewDO>>();

        for (i = 0; i < list.size(); i++ ) {
            ado = list.get(i);
            rg = ado.getRowGroup();

            if (j != rg) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(ado);
                grid.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(ado.getIsColumn())) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(ado);
                grid.add(ar);
                continue;
            }

            ar.add(ado);
        }

        return grid;
    }

    public TestAnalyteViewDO add(TestAnalyteViewDO data) throws Exception {
        TestAnalyte analyte;

        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = new TestAnalyte();

        analyte.setRowGroup(data.getRowGroup());
        analyte.setAnalyteId(data.getAnalyteId());
        analyte.setIsReportable(data.getIsReportable());
        analyte.setResultGroup(data.getResultGroup());
        analyte.setScriptletId(data.getScriptletId());
        analyte.setSortOrder(data.getSortOrder());
        analyte.setTestId(data.getTestId());
        analyte.setTypeId(data.getTypeId());
        analyte.setIsColumn(data.getIsColumn());

        manager.persist(analyte);
        data.setId(analyte.getId());

        return data;

    }

    public TestAnalyteViewDO update(TestAnalyteViewDO data) throws Exception {
        TestAnalyte analyte;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = manager.find(TestAnalyte.class, data.getId());
        analyte.setRowGroup(data.getRowGroup());
        analyte.setAnalyteId(data.getAnalyteId());
        analyte.setIsReportable(data.getIsReportable());
        analyte.setResultGroup(data.getResultGroup());
        analyte.setScriptletId(data.getScriptletId());
        analyte.setSortOrder(data.getSortOrder());
        analyte.setTestId(data.getTestId());
        analyte.setTypeId(data.getTypeId());
        analyte.setIsColumn(data.getIsColumn());

        return data;
    }

    public void delete(TestAnalyteViewDO analyteDO) throws Exception {
        TestAnalyte analyte;
        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = manager.find(TestAnalyte.class, analyteDO.getId());
        if (analyte != null)
            manager.remove(analyte);

    }

    public void validate(TestAnalyteViewDO anaDO) throws Exception {
        FieldErrorException exc;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        
        if (anaDO.getAnalyteId() == null) {
            exc = new FieldErrorException("fieldRequiredException",
                                          TestMeta.getAnalyteAnalyteId());
            list.add(exc);
        }
        if (anaDO.getTypeId() == null) {
            exc = new FieldErrorException("analyteTypeRequiredException",
                                          TestMeta.getAnalyteTypeId());
            list.add(exc);
        }
        if (anaDO.getResultGroup() == null) {
            exc = new FieldErrorException("fieldRequiredException",
                                          TestMeta.getAnalyteResultGroup());
            list.add(exc);
        }

        if (list.size() > 0)
            throw list;
    }

}
