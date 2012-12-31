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
import org.openelis.domain.Constants;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.entity.TestWorksheetItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.TestMeta;

@Stateless
@SecurityDomain("openelis")
public class TestWorksheetItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;

    public ArrayList<TestWorksheetItemDO> fetchByTestWorksheetId(Integer twsId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("TestWorksheetItem.FetchByTestWorksheetId");
        query.setParameter("testWorksheetId", twsId);

        list = query.getResultList();

        return DataBaseUtil.toArrayList(list);
    }

    public TestWorksheetItemDO add(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;

        manager.setFlushMode(FlushModeType.COMMIT);

        testWorksheetItem = new TestWorksheetItem();

        testWorksheetItem.setTestWorksheetId(data.getTestWorksheetId());
        testWorksheetItem.setSortOrder(data.getSortOrder());
        testWorksheetItem.setPosition(data.getPosition());
        testWorksheetItem.setTypeId(data.getTypeId());
        testWorksheetItem.setQcName(data.getQcName());

        manager.persist(testWorksheetItem);

        data.setId(testWorksheetItem.getId());

        return data;
    }

    public TestWorksheetItemDO update(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        testWorksheetItem = manager.find(TestWorksheetItem.class, data.getId());

        testWorksheetItem.setTestWorksheetId(data.getTestWorksheetId());
        testWorksheetItem.setSortOrder(data.getSortOrder());
        testWorksheetItem.setPosition(data.getPosition());
        testWorksheetItem.setTypeId(data.getTypeId());
        testWorksheetItem.setQcName(data.getQcName());

        return data;
    }

    public void delete(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;

        manager.setFlushMode(FlushModeType.COMMIT);

        testWorksheetItem = manager.find(TestWorksheetItem.class, data.getId());

        if (testWorksheetItem != null)
            manager.remove(testWorksheetItem);

    }

    public void validate(TestWorksheetItemDO data) throws Exception {
        String name;
        ValidationErrorsList list;
        Integer position, typeId;

        list = new ValidationErrorsList();
        name = data.getQcName();
        position = data.getPosition();
        typeId = data.getTypeId();

        if (DataBaseUtil.isEmpty(data.getTypeId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetItemTypeId()));

        }
        if (DataBaseUtil.isDifferent(Constants.dictionary().POS_DUPLICATE, typeId) &&
            DataBaseUtil.isEmpty(name)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetItemQcName()));
        }

        if (position == null) {
            if (DataBaseUtil.isSame(Constants.dictionary().POS_DUPLICATE, typeId) ||
                DataBaseUtil.isSame(Constants.dictionary().POS_FIXED, typeId) ||
                DataBaseUtil.isSame(Constants.dictionary().POS_FIXED_ALWAYS, typeId)) {
                list.add(new FieldErrorException("fixedDuplicatePosException",
                                                 TestMeta.getWorksheetItemPosition()));
            }
        } else {
            if (position == 1 &&
                DataBaseUtil.isSame(Constants.dictionary().POS_DUPLICATE, typeId)) {
                list.add(new FieldErrorException("posOneDuplicateException",
                                                 TestMeta.getWorksheetItemTypeId()));
            } else if (DataBaseUtil.isDifferent(Constants.dictionary().POS_DUPLICATE,
                                                typeId) &&
                       DataBaseUtil.isDifferent(Constants.dictionary().POS_FIXED, typeId) &&
                       DataBaseUtil.isDifferent(Constants.dictionary().POS_FIXED_ALWAYS,
                                                typeId)) {
                list.add(new FieldErrorException("posSpecifiedException",
                                                 TestMeta.getWorksheetItemPosition()));
            }
        }

        if (list.size() > 0)
            throw list;
    }
}