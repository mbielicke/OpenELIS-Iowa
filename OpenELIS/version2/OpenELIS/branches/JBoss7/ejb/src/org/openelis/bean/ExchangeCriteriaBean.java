/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.ExchangeCriteria;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.ExchangeCriteriaLocal;
import org.openelis.meta.ExchangeCriteriaMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.ExchangeCriteriaRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class ExchangeCriteriaBean implements ExchangeCriteriaLocal, ExchangeCriteriaRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                     manager;
    
    private static final ExchangeCriteriaMeta meta = new ExchangeCriteriaMeta();
    
    private static final Logger               log  = Logger.getLogger(ExchangeCriteriaBean.class);
    
    private static String                     FILE_PREFIX = "file://", SOCKET_PREFIX = "socket://"; 
   
    
    public ExchangeCriteriaViewDO fetchById(Integer id) throws Exception {
        Query query;
        ExchangeCriteriaViewDO data;

        query = manager.createNamedQuery("ExchangeCriteria.FetchById");
        query.setParameter("id", id);
        
        try {
            data = (ExchangeCriteriaViewDO)query.getSingleResult();
            data.setFields(getQueryFields(data.getQuery()));
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
            data.setFields(getQueryFields(data.getQuery()));
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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + ExchangeCriteriaMeta.getId() +
                          ", " + ExchangeCriteriaMeta.getName() + ") ");
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
        if (data.getFields() != null) {
            query = createQuery(data.getFields());
            entity.setQuery(query);
        }
        
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
        if (data.getFields() != null) {
            query = createQuery(data.getFields());
            entity.setQuery(query);
        }
        
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
        ValidationErrorsList list;
        ExchangeCriteriaViewDO dup;
        QueryData field;        
        ArrayList<QueryData> fields;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", ExchangeCriteriaMeta.getName()));
        } else {
            try {
                dup = fetchByName(data.getName());
                if (!dup.getId().equals(data.getId()))
                    list.add(new FieldErrorException("fieldUniqueException", ExchangeCriteriaMeta.getName()));
            } catch (NotFoundException e) {
                // ignore
            }
        }
        
        validateDestinationURI(data.getDestinationUri(), list);
        
        fields = data.getFields();
        if (fields == null || fields.size() == 0) {
            list.add(new FieldErrorException("atleastOneFieldFilledException", null));
        } else if ("N".equals(data.getIsAllAnalysesIncluded())) {
            /*
             * at least one test must be specified in the query if all analyses are 
             * not to be included
             */
            field = getQuery(data, SampleMeta.getAnalysisTestId());
            if (field == null || field.query == null)
                list.add(new FieldErrorException("noTestForNotIncludeAllAnalysesException", SampleMeta.getAnalysisTestId()));
        }
        
        if (list.size() > 0)
            throw list;
    }
    
    public void validateDestinationURI(final String uri, ValidationErrorsList errors)  {
        String temp;
        String socket[]; 
        
        if (DataBaseUtil.isEmpty(uri))
            return;
        
        if (uri.indexOf(FILE_PREFIX) == -1 && uri.indexOf(SOCKET_PREFIX) == -1) { 
            errors.add(new FieldErrorException("destURIMustHaveFileOrSocketException", ExchangeCriteriaMeta.getDestinationUri()));
        } else if (uri.indexOf(SOCKET_PREFIX) == 0) {
            /*
             * the original uri must remain unchanged
             */
            temp = uri.replaceAll(SOCKET_PREFIX, "");         
            socket = temp.split(":");
            if (socket.length != 2) 
                errors.add(new FieldErrorException("socketURIMustHaveHostAndPortException", ExchangeCriteriaMeta.getDestinationUri()));
        } 
    }
    
    private String createQuery(ArrayList<QueryData> fields) {
        ByteArrayOutputStream out;
        XMLEncoder enc;

        out = null;
        enc = null;
        /*
         * convert the list of QueryData into xml
         */
        out = new ByteArrayOutputStream();
        enc = new XMLEncoder(out);        
        enc.writeObject(fields);
        
        try {
            out.close();
        } catch (Exception e1) {
            log.error(e1);
        }

        try {
            enc.close();
        } catch (Exception e1) {
            log.error(e1);
        }
        
        return out != null ? out.toString() : null;
    } 
    
    private QueryData getQuery(ExchangeCriteriaViewDO data, String key) {
        ArrayList<QueryData> fields;
        
        fields = data.getFields();
        if (fields == null)
            return null;
        
        for (QueryData f : fields) {
            if (key.equals(f.key))
                return f;
        }
        
        return null;
    }
    
    private ArrayList<QueryData> getQueryFields(String query) throws Exception {
        ByteArrayInputStream in;
        XMLDecoder dec;

        if (DataBaseUtil.isEmpty(query))
            return null;

        in = null;
        dec = null;
        try {
            /*
             * convert the xml into a list of QueryData
             */
            in = new ByteArrayInputStream(query.getBytes());
            dec = new XMLDecoder(in);
            return (ArrayList<QueryData>)dec.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
                log.error(e);
            }

            try {
                if (dec != null)
                    dec.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}