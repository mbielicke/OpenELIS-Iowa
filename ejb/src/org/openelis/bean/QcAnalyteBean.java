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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.entity.QcAnalyte;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.meta.QcMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("qc-select")
public class QcAnalyteBean implements QcAnalyteLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private DictionaryLocal     dictionary;

    private static int          typeDict;
    private static final Logger log  = Logger.getLogger(QcAnalyteBean.class.getName());
    
    @PostConstruct
    public void init() {
        DictionaryDO data;

        try {
            data = dictionary.fetchBySystemName("qc_analyte_dictionary");
            typeDict = data.getId();
        } catch (Throwable e) {
            typeDict = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='qc_analyte_dictionary'", e);
        }
    }
        

    public QcAnalyteViewDO fetchById(Integer id) throws Exception {
        Query query;
        QcAnalyteViewDO data;
        DictionaryViewDO dict;      
        
        query = manager.createNamedQuery("QcAnalyte.FetchById");
        query.setParameter("id", id);
        try {
            data = (QcAnalyteViewDO)query.getSingleResult();
            if (typeDict == data.getTypeId()) {
                dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                if (dict != null)
                    data.setDictionary(dict.getEntry());
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QcAnalyteViewDO> fetchByQcId(Integer id) throws Exception {
        Query query;
        List list;
        QcAnalyteViewDO data;
        DictionaryViewDO dict;      

        query = manager.createNamedQuery("QcAnalyte.FetchByQcId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        //
        // for entries that are dictionary, we want to fetch the dictionary
        // text and set it for display
        //
        try {                       
            for (int i = 0 ; i < list.size(); i++) {
                data = (QcAnalyteViewDO)list.get(i);                
                if (typeDict == data.getTypeId()) {
                    dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                    if (dict != null)
                        data.setDictionary(dict.getEntry());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList) list;
    }

    public QcAnalyteViewDO add(QcAnalyteViewDO data) throws Exception {
        QcAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new QcAnalyte();
        entity.setQcId(data.getQcId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        entity.setIsTrendable(data.getIsTrendable());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public QcAnalyteViewDO update(QcAnalyteViewDO data) throws Exception {
        QcAnalyte entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcAnalyte.class, data.getId());
        entity.setQcId(data.getQcId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        entity.setIsTrendable(data.getIsTrendable());

        return data;
    }

    public void delete(QcAnalyteDO data) throws Exception {
        QcAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcAnalyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(QcAnalyteDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             QcMeta.getQcAnalyteId()));
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             QcMeta.getQcAnalyteTypeId()));
        if (list.size() > 0)
            throw list;
    }
}
