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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.entity.TestReflex;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class TestReflexBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager        manager;

    @EJB
    private DictionaryCacheBean  dictionaryCache;

    public ArrayList<TestReflexViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        List<TestReflexViewDO> list;
        TestReflexViewDO data;
        DictionaryDO dict;

        query = manager.createNamedQuery("TestReflex.FetchByTestId");
        query.setParameter("testId", testId);
        list = query.getResultList();

        try {
            for (int i = 0; i < list.size(); i++ ) {
                data = list.get(i);
                //
                // for entries that are dictionary, we want to fetch the
                // dictionary text and set it for display
                //
                if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DICTIONARY, data.getTestResultTypeId())) {
                    dict = dictionaryCache.getById(Integer.parseInt(data.getTestResultValue()));
                    data.setTestResultValue(dict.getEntry());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return DataBaseUtil.toArrayList(list);
    }

    public TestReflexDO add(TestReflexDO data) throws Exception {
        TestReflex entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new TestReflex();

        entity.setTestId(data.getTestId());
        entity.setAddTestId(data.getAddTestId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setFlagsId(data.getFlagsId());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public TestReflexDO update(TestReflexDO data) throws Exception {
        TestReflex entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestReflex.class, data.getId());

        entity.setTestId(data.getTestId());
        entity.setAddTestId(data.getAddTestId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setFlagsId(data.getFlagsId());

        return data;
    }

    public void delete(TestReflexDO data) throws Exception {
        TestReflex entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestReflex.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void validate(TestReflexDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (data.getAddTestId() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getReflexAddTestName()));
        }

        if (data.getTestAnalyteId() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getReflexTestAnalyteName()));
        }

        if (data.getTestResultId() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getReflexTestResultValue()));
        }

        if (data.getFlagsId() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), TestMeta.getReflexFlagsId()));
        }

        if (list.size() > 0)
            throw list;

    }

}
