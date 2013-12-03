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
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.ExchangeLocalTerm;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.meta.ExchangeLocalTermMeta;
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
    private TestAnalyteBean                    testAnalyte;

    private static final ExchangeLocalTermMeta meta = new ExchangeLocalTermMeta();

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

    public ArrayList<ExchangeLocalTermViewDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        QueryBuilderV2 builder;
        Integer refTableId;
        QueryData refName;
        List results;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        refTableId = null;
        results = null;
        refName = null;
        for (QueryData field : fields) {
            if (ExchangeLocalTermMeta.getReferenceTableId().equals(field.getKey()))
                refTableId = Integer.parseInt(field.getQuery());
            else if (ExchangeLocalTermMeta.getReferenceName().equals(field.getKey()))
                refName = field;
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
        if (Constants.table().ANALYTE.equals(refTableId)) {
            setReferenceQuery(refName, fields, ExchangeLocalTermMeta.getAnalyteName());
            results = analyteQuery(fields, builder, first, max);
        } else if (Constants.table().DICTIONARY.equals(refTableId)) {
            setReferenceQuery(refName, fields, ExchangeLocalTermMeta.getDictionaryEntry());
            results = dictionaryQuery(fields, builder, first, max);
        } else if (Constants.table().METHOD.equals(refTableId)) {
            setReferenceQuery(refName, fields, ExchangeLocalTermMeta.getMethodName());
            results = methodQuery(fields, builder, first, max);
        } else if (Constants.table().ORGANIZATION.equals(refTableId)) {
            setReferenceQuery(refName, fields, ExchangeLocalTermMeta.getOrganizationName());
            results = organizationQuery(fields, builder, first, max);
        } else if (Constants.table().TEST.equals(refTableId)) {
            setReferenceQuery(refName, fields, ExchangeLocalTermMeta.getTestName());
            results = testQuery(fields, builder, first, max);
        }

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
        AnalyteViewDO ana;
        DictionaryViewDO dict;
        TestViewDO t;
        MethodDO m;
        TestAnalyteViewDO ta;
        OrganizationViewDO org;
        AddressDO addr;
        StringBuilder sb;
        ArrayList<ArrayList<TestAnalyteViewDO>> tas;
        ArrayList<String> list;

        if (Constants.table().ANALYTE.equals(data.getReferenceTableId())) {
            ana = analyte.fetchById(data.getReferenceId());
            data.setReferenceName(ana.getName());
        } else if (Constants.table().DICTIONARY.equals(data.getReferenceTableId())) {
            dict = dictionary.fetchById(data.getReferenceId());
            data.setReferenceName(dict.getEntry());
            data.setReferenceDescription(dict.getCategoryName());
        } else if (Constants.table().METHOD.equals(data.getReferenceTableId())) {
            m = method.fetchById(data.getReferenceId());
            data.setReferenceName(m.getName());
        } else if (Constants.table().ORGANIZATION.equals(data.getReferenceTableId())) {
            org = organization.fetchById(data.getReferenceId());
            addr = org.getAddress();
            data.setReferenceName(org.getName());
            list = new ArrayList<String>();
            list.add(addr.getStreetAddress());
            list.add(addr.getCity());
            list.add(addr.getState());
            data.setReferenceDescription(DataBaseUtil.concatWithSeparator(list, ", "));
        } else if (Constants.table().TEST.equals(data.getReferenceTableId())) {
            t = test.fetchById(data.getReferenceId());
            data.setReferenceName(t.getName());
            data.setReferenceDescription(t.getMethodName());
        } else if (Constants.table().TEST_ANALYTE.equals(data.getReferenceTableId())) {
            ta = testAnalyte.fetchById(data.getReferenceTableId());
            t = test.fetchById(ta.getTestId());
            tas = testAnalyte.fetchByTestId(t.getId());
            /*
             * find the row and column for this test analyte 
             */
            for (int i = 0; i < tas.size(); i++) {
                for (int j = 0; j < tas.get(i).size(); j++) {
                    if (tas.get(i).get(j).getId().equals(ta.getId())) {
                        list = new ArrayList<String>();
                        list.add(t.getName());
                        list.add(t.getMethodName());
                        list.add(ta.getAnalyteName());
                        list.add(String.valueOf(i+1));
                        list.add(String.valueOf(j+1));
                        data.setReferenceName(DataBaseUtil.concatWithSeparator(list, ", "));
                        break;
                    }
                }
            }
        }
    }

    private void setReferenceQuery(QueryData refName, ArrayList<QueryData> fields, String key) {
        if (refName != null) {
            refName.setKey(key);
        } else {
            refName = new QueryData();
            refName.setKey(key);
            refName.setQuery("*");
            refName.setType(QueryData.Type.STRING);
            fields.add(refName);
        }
    }

    private List analyteQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first,
                              int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getAnalyteName() + ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getAnalyteName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List dictionaryQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first,
                                 int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getDictionaryEntry() + ", " +
                          ExchangeLocalTermMeta.getDictionaryCategoryName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getDictionaryEntry() + ", " +
                           ExchangeLocalTermMeta.getDictionaryCategoryName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List methodQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first, int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getMethodName() + ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getMethodName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List organizationQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first,
                                   int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getOrganizationName() + ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getOrganizationName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List testQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first, int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getTestName() + ", " +
                          ExchangeLocalTermMeta.getTestMethodName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getTestName() + ", " +
                           ExchangeLocalTermMeta.getTestMethodName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }
    
    private List testAnalyteQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first, int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ExchangeLocalTermViewDO(" +
                          ExchangeLocalTermMeta.getId() + ", " +
                          ExchangeLocalTermMeta.getReferenceTableId() + ", " +
                          ExchangeLocalTermMeta.getReferenceId() + ", " +
                          ExchangeLocalTermMeta.getTestAnalyteAnalyteName() + ", " +
                          ExchangeLocalTermMeta.getTestMethodName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeLocalTermMeta.getTestName() + ", " +
                           ExchangeLocalTermMeta.getTestMethodName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }
}