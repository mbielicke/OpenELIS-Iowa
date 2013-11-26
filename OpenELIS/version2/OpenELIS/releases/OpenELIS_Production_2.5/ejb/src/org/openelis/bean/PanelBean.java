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
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.Panel;
import org.openelis.meta.PanelMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class PanelBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager          manager;

    private static final PanelMeta meta = new PanelMeta();

    public PanelDO fetchById(Integer id) throws Exception {
        Query query;
        PanelDO data;

        query = manager.createNamedQuery("Panel.FetchById");
        query.setParameter("id", id);
        try {
            data = (PanelDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<PanelDO> fetchByIds(Collection<Integer> ids) {
        Query query;

        if (ids.size() == 0)
            return new ArrayList<PanelDO>();

        query = manager.createNamedQuery("Panel.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<PanelDO> fetchByName(String name, int maxResults) throws Exception {
        Query query;

        query = manager.createNamedQuery("Panel.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<TestMethodVO> fetchByNameSampleTypeWithTests(String name,
                                                                  Integer sampleItemType,
                                                                  int maxResults) throws Exception {
        List<PanelDO> panelList;
        List<TestMethodVO> testList;
        ArrayList<TestMethodVO> returnList;
        PanelDO panelDO;
        TestMethodVO testDO;
        int i, j;

        // check for panels first
        Query query = manager.createNamedQuery("Panel.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        panelList = query.getResultList();

        // if the list isnt full find tests
        testList = new ArrayList<TestMethodVO>();
        if (panelList.size() < maxResults) {
            query = manager.createNamedQuery("Test.FetchByNameSampleItemType");
            query.setParameter("name", name);
            query.setParameter("typeId", sampleItemType);
            query.setMaxResults(maxResults);
            testList = query.getResultList();
        }

        returnList = new ArrayList<TestMethodVO>();
        for (i = 0; i < panelList.size(); i++ ) {
            panelDO = panelList.get(i);
            testDO = new TestMethodVO();

            testDO.setTestId(panelDO.getId());
            testDO.setTestName(panelDO.getName());
            testDO.setTestDescription(panelDO.getDescription());

            returnList.add(testDO);
        }

        j = 0;
        while (i < maxResults && j < testList.size()) {
            testDO = testList.get(j);
            returnList.add(testDO);
            i++ ;
            j++ ;
        }

        return returnList;
    }

    public ArrayList<TestMethodVO> fetchByNameWithTests(String name, int maxResults) throws Exception {
        List<PanelDO> panelList;
        List<TestMethodVO> testList;
        ArrayList<TestMethodVO> returnList;
        PanelDO panelDO;
        TestMethodVO testDO;
        int i, j;

        // check for panels first
        Query query = manager.createNamedQuery("Panel.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        panelList = query.getResultList();

        // if the list isnt full find tests
        testList = new ArrayList<TestMethodVO>();
        if (panelList.size() < maxResults) {
            query = manager.createNamedQuery("Test.FetchWithMethodByName");
            query.setParameter("name", name);
            query.setMaxResults(maxResults);
            testList = query.getResultList();
        }

        returnList = new ArrayList<TestMethodVO>();
        for (i = 0; i < panelList.size(); i++ ) {
            panelDO = panelList.get(i);
            testDO = new TestMethodVO();

            testDO.setTestId(panelDO.getId());
            testDO.setTestName(panelDO.getName());
            testDO.setTestDescription(panelDO.getDescription());

            returnList.add(testDO);
        }

        j = 0;
        while (i < maxResults && j < testList.size()) {
            testDO = testList.get(j);
            returnList.add(testDO);
            i++ ;
            j++ ;
        }

        return returnList;
    }

    public ArrayList<IdVO> fetchTestIdsFromPanel(Integer panelId) throws Exception {
        int i;
        boolean found;
        PanelItemDO panelItem;
        IdVO idVO;
        Query query;
        List<TestViewDO> tests;
        ArrayList<IdVO> returnList;
        List<PanelItemDO> panelItemList;

        // fetch the panelitems
        query = manager.createNamedQuery("PanelItem.FetchByPanelId");
        query.setParameter("id", panelId);
        panelItemList = query.getResultList();

        // fetch the testid from each row by test name, method name
        returnList = new ArrayList<IdVO>();
        query = manager.createNamedQuery("Test.FetchByNameMethodName");
        for (i = 0; i < panelItemList.size(); i++ ) {
            panelItem = panelItemList.get(i);

            if ( !"T".equals(panelItem.getType()))
                continue;

            query.setParameter("name", panelItem.getName());
            query.setParameter("methodName", panelItem.getMethodName());

            tests = query.getResultList();
            /*
             * find an active test and add its id; if such a test can't be found
             * then add the id of the first test returned by the query
             */
            found = false;
            if (tests.size() > 0) {
                idVO = new IdVO();
                for (TestViewDO t : tests) {
                    if ("Y".equals(t.getIsActive())) {
                        idVO.setId(t.getId());
                        found = true;
                        break;
                    }
                }
                if (!found)
                    idVO.setId(tests.get(0).getId());
                
                returnList.add(idVO);
            }
        }

        return returnList;
    }

    public ArrayList<IdVO> fetchAuxIdsFromPanel(Integer panelId) throws Exception {
        int i;
        List<PanelItemDO> panelItemList;
        PanelItemDO panelItem;
        AuxFieldGroupDO auxDO;
        ArrayList<IdVO> returnList;
        IdVO idVO;
        Query query;

        // fetch the panelitems
        query = manager.createNamedQuery("PanelItem.FetchByPanelId");
        query.setParameter("id", panelId);
        panelItemList = query.getResultList();

        // fetch the testid from each row by test name, method name
        returnList = new ArrayList<IdVO>();
        query = manager.createNamedQuery("AuxFieldGroup.FetchActiveByName");
        for (i = 0; i < panelItemList.size(); i++) {
            panelItem = panelItemList.get(i);

            if (!"A".equals(panelItem.getType()))
                continue;

            query.setParameter("name", panelItem.getName());
            try {
                auxDO = (AuxFieldGroupDO)query.getSingleResult();
                idVO = new IdVO();
                idVO.setId(auxDO.getId());

                returnList.add(idVO);
            } catch (NoResultException e) {
                // do nothing
            }
        }

        return returnList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PanelDO> fetchAll() throws Exception {
        Query query;
        List<PanelDO> panels;

        query = manager.createNamedQuery("Panel.FetchAll");
        panels = query.getResultList();
        return DataBaseUtil.toArrayList(panels);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + PanelMeta.getId() + ", " +
                          PanelMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(PanelMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public PanelDO add(PanelDO data) throws Exception {
        Panel entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Panel();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public PanelDO update(PanelDO data) throws Exception {
        Panel entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Panel.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());

        return data;
    }

    public void delete(PanelDO data) throws Exception {
        Panel entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Panel.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(PanelDO data) throws Exception {
        String name;
        ValidationErrorsList list;
        Query query;
        PanelDO panel;

        list = new ValidationErrorsList();
        name = data.getName();
        if (DataBaseUtil.isEmpty(name)) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             PanelMeta.getName()));
            throw list;
        }

        query = manager.createNamedQuery("Panel.FetchByName");
        query.setParameter("name", name);
        try {
            panel = (PanelDO)query.getSingleResult();
            if (DataBaseUtil.isDifferent(panel.getId(), data.getId()))
                list.add(new FieldErrorException(Messages.get().fieldUniqueException(),
                                                 PanelMeta.getName()));
        } catch (NoResultException ignE) {
            // do nothing
        }

        if (list.size() > 0)
            throw list;
    }

}
