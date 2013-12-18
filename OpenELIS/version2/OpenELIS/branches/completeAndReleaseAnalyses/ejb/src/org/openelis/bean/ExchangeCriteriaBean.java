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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.ExchangeCriteria;
import org.openelis.meta.ExchangeCriteriaMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Stateless
@SecurityDomain("openelis")
public class ExchangeCriteriaBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                     manager;

    private static final ExchangeCriteriaMeta meta = new ExchangeCriteriaMeta();

    private static final Logger               log  = Logger.getLogger("openelis");

    public ExchangeCriteriaViewDO fetchById(Integer id) throws Exception {
        Query query;
        ExchangeCriteriaViewDO data;

        query = manager.createNamedQuery("ExchangeCriteria.FetchById");
        query.setParameter("id", id);

        try {
            data = (ExchangeCriteriaViewDO)query.getSingleResult();
            data.setFields(decodeQuery(data.getQuery()));
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ExchangeCriteriaViewDO fetchByName(String name) throws Exception {
        Query query;
        ExchangeCriteriaViewDO data;

        query = manager.createNamedQuery("ExchangeCriteria.FetchByName");
        query.setParameter("name", name);

        try {
            data = (ExchangeCriteriaViewDO)query.getSingleResult();
            data.setFields(decodeQuery(data.getQuery()));
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          ExchangeCriteriaMeta.getId() + ", " + ExchangeCriteriaMeta.getName() +
                          ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ExchangeCriteriaMeta.getName());

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

    public ExchangeCriteriaViewDO add(ExchangeCriteriaViewDO data) throws Exception {
        ExchangeCriteria entity;
        String query;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new ExchangeCriteria();
        entity.setName(data.getName());
        entity.setEnvironmentId(data.getEnvironmentId());
        entity.setDestinationUri(data.getDestinationUri());
        entity.setIsAllAnalysesIncluded(data.getIsAllAnalysesIncluded());
        entity.setQuery(encodeQuery(data.getFields()));

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ExchangeCriteriaViewDO update(ExchangeCriteriaViewDO data) throws Exception {
        ExchangeCriteria entity;
        String query;

        /*
         * the check for isChanged isn't performed here because even if all the
         * other fields have the same values, the query may be different and
         * there isn't any way to determine that before the query is generated
         * from the query fields
         */
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ExchangeCriteria.class, data.getId());
        entity.setName(data.getName());
        entity.setEnvironmentId(data.getEnvironmentId());
        entity.setDestinationUri(data.getDestinationUri());
        entity.setIsAllAnalysesIncluded(data.getIsAllAnalysesIncluded());
        entity.setQuery(encodeQuery(data.getFields()));

        return data;
    }

    public void delete(ExchangeCriteriaViewDO data) throws Exception {
        ExchangeCriteria entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(ExchangeCriteria.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(ExchangeCriteriaViewDO data) throws Exception {
        boolean found;
        ValidationErrorsList list;
        ExchangeCriteriaViewDO dup;
        ArrayList<QueryData> fields;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ExchangeCriteriaMeta.getName()));
        } else {
            try {
                dup = fetchByName(data.getName());
                if (!dup.getId().equals(data.getId()))
                    list.add(new FieldErrorException(Messages.get().fieldUniqueException(),
                                                     ExchangeCriteriaMeta.getName()));
            } catch (NotFoundException e) {
                // ignore
            }
        }

        validateDestinationURI(data.getDestinationUri(), list);

        fields = data.getFields();
        if (fields == null || fields.size() == 0) {
            list.add(new FieldErrorException(Messages.get().atleastOneFieldFilledException(), null));
        } else if ("N".equals(data.getIsAllAnalysesIncluded())) {
            /*
             * at least one test must be specified in the query if all analyses
             * are not to be included
             */
            found = false;
            for (QueryData f : fields) {
                if (SampleMeta.getAnalysisTestId().equals(f.getKey())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                list.add(new FieldErrorException(Messages.get()
                                                         .noTestForNotIncludeAllAnalysesException(),
                                                 SampleMeta.getAnalysisTestId()));
        }

        if (list.size() > 0)
            throw list;
    }

    public void validateDestinationURI(String destination, ValidationErrorsList errors) {
        URI uri;

        if (DataBaseUtil.isEmpty(destination))
            return;

        try {
            uri = new URI(destination);
            if (!"file".equals(uri.getScheme()) && !"socket".equals(uri.getScheme())) {
                errors.add(new FormErrorException(Messages.get()
                                                          .destURIMustHaveFileOrSocketException()));
            } else if ("socket".equals(uri.getScheme()) &&
                       (uri.getHost() == null || uri.getPort() == 0)) {
                errors.add(new FieldErrorException(Messages.get()
                                                           .socketURIMustHaveHostAndPortException(),
                                                   ExchangeCriteriaMeta.getDestinationUri()));
            }
        } catch (Exception e) {
            errors.add(new Exception(e.getMessage()));
        }
    }

    private String encodeQuery(ArrayList<QueryData> fields) throws Exception {
        Document doc;
        Element root, e;

        if (fields != null) {
            doc = XMLUtil.createNew("query");
            root = doc.getDocumentElement();
            for (QueryData f : fields) {
                e = doc.createElement("field");
                e.setAttribute("key", f.getKey());
                e.setAttribute("type", f.getType().name());
                e.setTextContent(f.getQuery());
                root.appendChild(e);
            }
            return XMLUtil.toString(doc);
        }

        return null;
    }

    private ArrayList<QueryData> decodeQuery(String xml) throws Exception {
        Document doc;
        Node node;
        NodeList nodes;
        NamedNodeMap attrs;
        QueryData field;
        ArrayList<QueryData> fields;

        if (DataBaseUtil.isEmpty(xml))
            return null;

        doc = XMLUtil.parse(xml);
        nodes = doc.getDocumentElement().getChildNodes();
        fields = new ArrayList<QueryData>();
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);
            if ("field".equals(node.getNodeName())) {
                attrs = node.getAttributes();
                field = new QueryData();
                field.setKey(attrs.getNamedItem("key").getNodeValue());
                field.setType(QueryData.Type.valueOf(attrs.getNamedItem("type").getNodeValue()));
                field.setQuery(node.getTextContent());
                fields.add(field);
            }
        }

        return fields;
    }
}