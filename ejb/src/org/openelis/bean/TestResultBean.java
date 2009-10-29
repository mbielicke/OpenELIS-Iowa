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
import java.util.Iterator;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.entity.TestResult;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestResultBean implements TestResultLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    private static final TestMetaMap meta = new TestMetaMap();

    public ArrayList<ArrayList<TestResultViewDO>> fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestResultViewDO>> listCollection;
        List<TestResultViewDO> reslist;
        ArrayList<TestResultViewDO> viewlist;
        TestResultDO resDO;
        TestResultViewDO viewDO;
        Integer typeId, val, resultGroup;
        String sysName, entry;
        Query query;
        Iterator iter;
        DictionaryViewDO snDO,entDO;        

        reslist = null;
        viewlist = null;
        listCollection = new ArrayList<ArrayList<TestResultViewDO>>();
        resultGroup = 1;

        while (resultGroup != null) {
            query = manager.createNamedQuery("TestResult.FetchByTestIdResultGroup");
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", resultGroup);

            try {
                reslist = query.getResultList();
                if (reslist.size() == 0) {
                    resultGroup = null;
                    break;
                }

                viewlist = new ArrayList<TestResultViewDO>();
                for (iter = reslist.iterator(); iter.hasNext();) {
                    resDO = (TestResultDO)iter.next();
                    viewDO = createTestResultViewDO(resDO);
                    typeId = resDO.getTypeId();
                    query = manager.createNamedQuery("Dictionary.FetchById");
                    query.setParameter("id", typeId);
                    snDO = (DictionaryViewDO)query.getResultList().get(0);
                    sysName = snDO.getSystemName();
                    if ("test_res_type_dictionary".equals(sysName)) {
                        val = Integer.parseInt(resDO.getValue());
                        query = manager.createNamedQuery("Dictionary.FetchById");
                        query.setParameter("id", val);
                        entDO = (DictionaryViewDO)query.getResultList().get(0);
                        entry = entDO.getEntry();
                        viewDO.setDictionary(entry);
                    }
                    viewlist.add(viewDO);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            listCollection.add(viewlist);
            resultGroup++ ;
        }
        return listCollection;
    }

    public TestResultViewDO add(TestResultViewDO data) throws Exception {
        TestResult testResult;

        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = new TestResult();

        testResult.setFlagsId(data.getFlagsId());
        testResult.setResultGroup(data.getResultGroup());
        testResult.setRoundingMethodId(data.getRoundingMethodId());
        testResult.setSignificantDigits(data.getSignificantDigits());
        testResult.setSortOrder(data.getSortOrder());
        testResult.setTestId(data.getTestId());
        testResult.setTypeId(data.getTypeId());
        testResult.setUnitOfMeasureId(data.getUnitOfMeasureId());
        testResult.setValue(data.getValue());

        manager.persist(testResult);
        data.setId(testResult.getId());
        
        return data;
    }

    public TestResultViewDO update(TestResultViewDO data) throws Exception {
        TestResult testResult;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = manager.find(TestResult.class, data.getId());

        testResult.setFlagsId(data.getFlagsId());
        testResult.setResultGroup(data.getResultGroup());
        testResult.setRoundingMethodId(data.getRoundingMethodId());
        testResult.setSignificantDigits(data.getSignificantDigits());
        testResult.setSortOrder(data.getSortOrder());
        testResult.setTestId(data.getTestId());
        testResult.setTypeId(data.getTypeId());
        testResult.setUnitOfMeasureId(data.getUnitOfMeasureId());
        testResult.setValue(data.getValue());
        
        return data;

    }

    public void delete(TestResultViewDO deletedAt) throws Exception {
        TestResult testResult;

        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = manager.find(TestResult.class, deletedAt.getId());

        if (testResult != null)
            manager.remove(testResult);

    }

    public void validate(TestResultViewDO resDO) throws Exception {
        ValidationErrorsList list;
        Integer numId, dictId, titerId, typeId, dateId, dtId, timeId, defId;
        String value, fieldName;
        DictionaryLocal dl;

        value = null;
        dl = dictLocal();
        numId = null;
        titerId = null;
        dictId = null;
        defId = null;
        dtId = null;
        timeId = null;
        dateId = null;
        
        try {
            dictId = (dl.fetchBySystemName("test_res_type_dictionary")).getId();
            numId = (dl.fetchBySystemName("test_res_type_numeric")).getId();
            titerId = (dl.fetchBySystemName("test_res_type_titer")).getId();
            dateId = (dl.fetchBySystemName("test_res_type_date")).getId();
            dtId = (dl.fetchBySystemName("test_res_type_date_time")).getId();
            timeId = (dl.fetchBySystemName("test_res_type_time")).getId();
            defId = (dl.fetchBySystemName("test_res_type_default")).getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        list = new ValidationErrorsList();

        value = resDO.getValue();
        typeId = resDO.getTypeId();

        fieldName = meta.TEST_RESULT.getValue();
        //
        // dictionary, titers, numeric require a value
        //
        if (DataBaseUtil.isEmpty(value) &&
            (numId.equals(typeId) || titerId.equals(typeId) || dictId.equals(typeId) || defId.equals(typeId))) {
            list.add(new FieldErrorException("fieldRequiredException", fieldName));
        } else if (!DataBaseUtil.isEmpty(value) &&
                   (dtId.equals(typeId) || timeId.equals(typeId) || dateId.equals(typeId))) {
            list.add(new FieldErrorException("valuePresentForDateTypesException", fieldName));
        }

        if (list.size() > 0)
            throw list;

    }

    private TestResultViewDO createTestResultViewDO(TestResultDO resDO) {
        TestResultViewDO viewDO;

        viewDO = new TestResultViewDO();

        viewDO.setId(resDO.getId());
        viewDO.setFlagsId(resDO.getFlagsId());
        viewDO.setResultGroup(resDO.getResultGroup());
        viewDO.setRoundingMethodId(resDO.getRoundingMethodId());
        viewDO.setSignificantDigits(resDO.getSignificantDigits());
        viewDO.setSortOrder(resDO.getSortOrder());
        viewDO.setTestId(resDO.getTestId());
        viewDO.setTypeId(resDO.getTypeId());
        viewDO.setUnitOfMeasureId(resDO.getUnitOfMeasureId());
        viewDO.setValue(resDO.getValue());

        return viewDO;
    }

    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
