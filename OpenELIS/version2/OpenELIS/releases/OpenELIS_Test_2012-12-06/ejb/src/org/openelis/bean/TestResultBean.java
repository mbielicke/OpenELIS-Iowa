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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.entity.TestResult;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utilcommon.ResultValidator.Type;

@Stateless
@SecurityDomain("openelis")

public class TestResultBean implements TestResultLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager   manager;

    @EJB
    private DictionaryLocal dictionary;

    private static HashMap<Integer, Type> types;
    
    @PostConstruct
    public void init() {        
        if (types == null) {
            types = new HashMap<Integer, Type>();
            try {
                types.put(dictionary.fetchBySystemName("test_res_type_dictionary").getId(), Type.DICTIONARY);
                types.put(dictionary.fetchBySystemName("test_res_type_numeric").getId(), Type.NUMERIC);
                types.put(dictionary.fetchBySystemName("test_res_type_titer").getId(), Type.TITER);
                types.put(dictionary.fetchBySystemName("test_res_type_date").getId(), Type.DATE);
                types.put(dictionary.fetchBySystemName("test_res_type_date_time").getId(), Type.DATE_TIME);
                types.put(dictionary.fetchBySystemName("test_res_type_time").getId(), Type.TIME);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_lower").getId(), Type.ALPHA_LOWER);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_upper").getId(), Type.ALPHA_UPPER);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_mixed").getId(), Type.ALPHA_MIXED);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ArrayList<TestResultViewDO>> fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestResultViewDO>> grid;
        List<TestResultViewDO> list;
        TestResultViewDO data;
        Integer rg;
        Query query;
        DictionaryViewDO dict;
        Type type;

        list = null;
        grid = new ArrayList<ArrayList<TestResultViewDO>>();
        rg = 1;

        while (rg != null) {
            query = manager.createNamedQuery("TestResult.FetchByTestIdResultGroup");
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", rg);

            try {
                list = query.getResultList();
                if (list.isEmpty()) {
                    rg = null;
                    break;
                }

                list = DataBaseUtil.toArrayList(list);
                for (int i = 0; i < list.size(); i++ ) {
                    data = (TestResultViewDO)list.get(i);
                    
                    /*
                     *  for entries that are dictionary, we want to fetch the
                     * dictionary text and set it for display
                     */
                    type = types.get(data.getTypeId());
                    if (type == Type.DICTIONARY) {
                        dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                        if (dict != null)
                            data.setDictionary(dict.getEntry());
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            grid.add((ArrayList<TestResultViewDO>)list);
            rg++ ;
        }
        return grid;
    }

    public TestResultViewDO add(TestResultViewDO data) throws Exception {
        TestResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new TestResult();

        entity.setFlagsId(data.getFlagsId());
        entity.setResultGroup(data.getResultGroup());
        entity.setRoundingMethodId(data.getRoundingMethodId());
        entity.setSignificantDigits(data.getSignificantDigits());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public TestResultViewDO update(TestResultViewDO data) throws Exception {
        TestResult entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestResult.class, data.getId());

        entity.setFlagsId(data.getFlagsId());
        entity.setResultGroup(data.getResultGroup());
        entity.setRoundingMethodId(data.getRoundingMethodId());
        entity.setSignificantDigits(data.getSignificantDigits());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setValue(data.getValue());

        return data;

    }

    public void delete(TestResultViewDO data) throws Exception {
        TestResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestResult.class, data.getId());

        if (entity != null)
            manager.remove(entity);

    }

    public void validate(TestResultViewDO data) throws Exception {
        ValidationErrorsList list;
        Integer typeId;
        String value;
        Type type;
        
        list = new ValidationErrorsList();
        value = data.getValue();
        typeId = data.getTypeId();

        if (DataBaseUtil.isEmpty(typeId))
            list.add(new FieldErrorException("fieldRequiredException", TestMeta.getResultTypeId()));
        //
        // dictionary, titers, numeric require a value
        //
        type = types.get(data.getTypeId());
        if (DataBaseUtil.isEmpty(value) &&
            (type == Type.NUMERIC || type == Type.TITER || type == Type.DICTIONARY)) {
            list.add(new FieldErrorException("fieldRequiredException", TestMeta.getResultValue()));
        } else if ( !DataBaseUtil.isEmpty(value) &&
                   (type == Type.DATE_TIME || type == Type.TIME || type == Type.DATE || type == Type.ALPHA_LOWER ||
                   type == Type.ALPHA_UPPER || type == Type.ALPHA_MIXED)) {
            list.add(new FieldErrorException("valuePresentForTypeException",
                                             TestMeta.getResultValue()));
        }

        if (list.size() > 0)
            throw list;

    }

}
