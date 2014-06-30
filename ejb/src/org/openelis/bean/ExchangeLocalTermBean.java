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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.ExchangeLocalTermDO;
import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.TestAnalyteViewVO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.ExchangeLocalTerm;
import org.openelis.meta.ExchangeLocalTermMeta;
import org.openelis.meta.TestAnalyteViewMeta;
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
public class ExchangeLocalTermBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                      manager;

    @EJB
    private AnalyteBean                        analyte;

    @EJB
    private DictionaryBean                     dictionary;

    @EJB
    private MethodBean                         method;

    @EJB
    private OrganizationBean                   organization;

    @EJB
    private TestBean                           test;

    @EJB
    private PanelBean                          panel;

    private static final ExchangeLocalTermMeta meta    = new ExchangeLocalTermMeta();

    private static final TestAnalyteViewMeta   tavMeta = new TestAnalyteViewMeta();

    public ExchangeLocalTermViewDO fetchById(Integer id) throws Exception {
        Query query;
        ExchangeLocalTermViewDO data;

        query = manager.createNamedQuery("ExchangeLocalTerm.FetchById");
        query.setParameter("id", id);
        try {
            data = (ExchangeLocalTermViewDO)query.getSingleResult();
            setReferenceNameDescription(data);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ExchangeLocalTermViewDO fetchByReferenceTableIdReferenceId(Integer referenceTableId,
                                                                      Integer referenceId) throws Exception {
        Query query;
        ExchangeLocalTermViewDO data;

        query = manager.createNamedQuery("ExchangeLocalTerm.FetchByReferenceTableIdReferenceId");
        query.setParameter("referenceTableId", referenceTableId);
        query.setParameter("referenceId", referenceId);
        try {
            data = (ExchangeLocalTermViewDO)query.getSingleResult();
            setReferenceNameDescription(data);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<TestAnalyteViewVO> fetchTestAnalytes(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(tavMeta);

        builder.setSelect("distinct new org.openelis.domain.TestAnalyteViewVO(" +
                          TestAnalyteViewMeta.getId() + ", " + TestAnalyteViewMeta.getTestId() +
                          ", " + TestAnalyteViewMeta.getTestName() + ", " +
                          TestAnalyteViewMeta.getMethodId() + ", " +
                          TestAnalyteViewMeta.getMethodName() + ", " +
                          TestAnalyteViewMeta.getTestIsActive() + ", " +
                          TestAnalyteViewMeta.getTestActiveBegin() + ", " +
                          TestAnalyteViewMeta.getTestActiveEnd() + ", " +
                          TestAnalyteViewMeta.getRowTestAnalyteId() + ", " +
                          TestAnalyteViewMeta.getRowAnalyteId() + ", " +
                          TestAnalyteViewMeta.getRowAnalyteName() + ", " +
                          TestAnalyteViewMeta.getColAnalyteId() + ", " +
                          TestAnalyteViewMeta.getColAnalyteName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(TestAnalyteViewMeta.getTestName() + ", " +
                           TestAnalyteViewMeta.getMethodName() + ", " +
                           TestAnalyteViewMeta.getRowAnalyteName() + ", " +
                           TestAnalyteViewMeta.getColAnalyteName());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<ExchangeLocalTermViewDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Integer refTableId;
        List results;

        refTableId = null;
        for (QueryData field : fields) {
            if (ExchangeLocalTermMeta.getReferenceTableId().equals(field.getKey()))
                refTableId = Integer.parseInt(field.getQuery());
        }

        /*
         * The field that shows the name of the test, method etc. on the screen
         * has the key ExchangeLocalTermMeta.getReferenceName() because it can't
         * be named "test.name" or "method.name" as it can represent different
         * reference tables at different times. Thus when a query contains that
         * field, we have to replace its key with the right field for the
         * reference table e.g "test.name" because otherwise, the query that
         * gets built below doesn't contain the name of the reference table in
         * the "from" clause.
         */
        results = null;
        if (Constants.table().ANALYTE.equals(refTableId))
            results = analyteQuery(fields, first, max);
        else if (Constants.table().DICTIONARY.equals(refTableId))
            results = dictionaryQuery(fields, first, max);
        else if (Constants.table().METHOD.equals(refTableId))
            results = methodQuery(fields, first, max);
        else if (Constants.table().ORGANIZATION.equals(refTableId))
            results = organizationQuery(fields, first, max);
        else if (Constants.table().TEST.equals(refTableId))
            results = testQuery(fields, first, max);
        else if (Constants.table().TEST_ANALYTE.equals(refTableId))
            results = testAnalyteQuery(fields, first, max);
        else if (Constants.table().PANEL.equals(refTableId))
            results = panelQuery(fields, first, max);

        if (results.isEmpty())
            throw new NotFoundException();
        results = (ArrayList<ExchangeLocalTermViewDO>)DataBaseUtil.subList(results, first, max);
        if (results == null)
            throw new LastPageException();

        return (ArrayList<ExchangeLocalTermViewDO>)results;
    }

    public ExchangeLocalTermDO add(ExchangeLocalTermDO data) throws Exception {
        ExchangeLocalTerm entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new ExchangeLocalTerm();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ExchangeLocalTermDO update(ExchangeLocalTermDO data) throws Exception {
        ExchangeLocalTerm entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ExchangeLocalTerm.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());

        return data;
    }

    public void validate(ExchangeLocalTermDO data) throws Exception {
        boolean checkDuplicate;
        ExchangeLocalTermViewDO localTerm;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        checkDuplicate = false;

        if (data.getReferenceTableId() == null)
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ExchangeLocalTermMeta.getReferenceTableId()));
        else
            checkDuplicate = true;

        if (data.getReferenceId() == null)
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ExchangeLocalTermMeta.getReferenceName()));
        else
            checkDuplicate = true;

        if (checkDuplicate) {
            try {
                localTerm = fetchByReferenceTableIdReferenceId(data.getReferenceTableId(),
                                                               data.getReferenceId());
                if ( !localTerm.getId().equals(data.getId()))
                    list.add(new FieldErrorException(Messages.get().fieldUniqueException(),
                                                     ExchangeLocalTermMeta.getReferenceName()));
            } catch (NotFoundException ignE) {
                // ignore
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private void setReferenceNameDescription(ExchangeLocalTermViewDO data) throws Exception {
        StringBuffer sb;
        AnalyteViewDO ana;
        DictionaryViewDO dict;
        TestViewDO t;
        TestAnalyteViewVO tav;
        MethodDO m;
        OrganizationViewDO org;
        AddressDO addr;
        PanelDO p;
        Query query;

        if (Constants.table().ANALYTE.equals(data.getReferenceTableId())) {
            ana = analyte.fetchById(data.getReferenceId());
            data.setReferenceName(ana.getName());
        } else if (Constants.table().DICTIONARY.equals(data.getReferenceTableId())) {
            dict = dictionary.fetchById(data.getReferenceId());
            data.setReferenceName(dict.getEntry());
            data.setReferenceDescription1(dict.getCategoryName());
        } else if (Constants.table().METHOD.equals(data.getReferenceTableId())) {
            m = method.fetchById(data.getReferenceId());
            sb = new StringBuffer();
            sb.append(m.getName());
            if ("N".equals(m.getIsActive())) {
                /*
                 * for inactive methods, show the active begin and end dates
                 */
                sb.append(" [");
                sb.append(m.getActiveBegin());
                sb.append("..");
                sb.append(m.getActiveEnd());
                sb.append("]");
            }
            /*
             * here the dates are not specified as reference description because
             * otherwise they will be separated from the name by a comma on the
             */
            data.setReferenceName(sb.toString());
        } else if (Constants.table().ORGANIZATION.equals(data.getReferenceTableId())) {
            org = organization.fetchById(data.getReferenceId());
            addr = org.getAddress();
            data.setReferenceName(org.getName());
            data.setReferenceDescription1(addr.getStreetAddress());
            data.setReferenceDescription2(addr.getCity());
            data.setReferenceDescription3(addr.getState());
        } else if (Constants.table().TEST.equals(data.getReferenceTableId())) {
            t = test.fetchById(data.getReferenceId());
            data.setReferenceName(t.getName());
            sb = new StringBuffer();
            sb.append(t.getMethodName());
            if ("N".equals(t.getIsActive())) {
                /*
                 * for inactive tests, show the active begin and end dates
                 */
                sb.append(" [");
                sb.append(t.getActiveBegin());
                sb.append("..");
                sb.append(t.getActiveEnd());
                sb.append("]");
            }
            data.setReferenceDescription1(sb.toString());
        } else if (Constants.table().TEST_ANALYTE.equals(data.getReferenceTableId())) {
            query = manager.createNamedQuery("TestAnalyteView.FetchById");
            query.setParameter("id", data.getReferenceId());
            tav = (TestAnalyteViewVO)query.getSingleResult();
            data.setReferenceName(tav.getTestName());
            data.setReferenceDescription1(tav.getMethodName());
            data.setReferenceDescription2(tav.getRowAnalyteName());
            /*
             * for row analytes, the column analyte name is the same as the row
             * analyte name, so it doesn't need to be shown twice
             */
            sb = new StringBuffer();
            if ( !tav.getRowAnalyteName().equals(tav.getColAnalyteName()))
                sb.append(tav.getColAnalyteName());

            if ("N".equals(tav.getTestIsActive())) {
                /*
                 * for inactive tests, show the active begin and end dates
                 */
                sb.append(" [");
                sb.append(tav.getTestActiveBegin());
                sb.append("..");
                sb.append(tav.getTestActiveEnd());
                sb.append("]");
            }

            data.setReferenceDescription3(sb.toString());
        } else if (Constants.table().PANEL.equals(data.getReferenceTableId())) {
            p = panel.fetchById(data.getReferenceId());
            data.setReferenceName(p.getName());
        }
    }

    private List analyteQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getAnalyteName() + ", " + "''" + ", " + "''" +
                          ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getAnalyteName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List dictionaryQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getDictionaryEntry() + ", " +
                          ExchangeLocalTermMeta.getDictionaryCategoryName() + ", " + "''" + ", " +
                          "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getDictionaryEntry() + ", " +
                           ExchangeLocalTermMeta.getDictionaryCategoryName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List methodQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getMethodName() + ", " + "''" + ", " + "''" + ", " +
                          "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getMethodName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List organizationQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getOrganizationName() + ", " + "''" + ", " + "''" +
                          ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getOrganizationName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List testQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getTestName() + ", " +
                          ExchangeLocalTermMeta.getTestMethodName() + ", " + "''" + ", " + "''" +
                          ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getTestName() + ", " +
                           ExchangeLocalTermMeta.getTestMethodName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List testAnalyteQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getTestAnalyteViewTestName() + ", " +
                          ExchangeLocalTermMeta.getTestAnalyteViewMethodName() + ", " +
                          ExchangeLocalTermMeta.getTestAnalyteViewRowAnalyteName() + ", " +
                          ExchangeLocalTermMeta.getTestAnalyteViewColAnalyteName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getTestAnalyteViewTestName() + ", " +
                           ExchangeLocalTermMeta.getTestAnalyteViewMethodName() + ", " +
                           ExchangeLocalTermMeta.getTestAnalyteViewRowAnalyteName() + ", " +
                           ExchangeLocalTermMeta.getTestAnalyteViewColAnalyteName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List panelQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getPanelName() + ", " + "''" + ", " + "''" + ", " +
                          "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getPanelName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }
}