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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.entity.OrganizationParameter;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.OrganizationMeta;
import org.openelis.utils.EmailUtil;

@Stateless
@SecurityDomain("openelis")

public class OrganizationParameterBean  {

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;
    
    @EJB
    private DictionaryBean             dictionary;
    
    private static final Logger      log = Logger.getLogger("openelis");
    
    private static Integer            receivableReportToId, releasedReportToId;
    
    @PostConstruct
    public void init() {
        try {
            receivableReportToId = dictionary.fetchBySystemName("receivable_reportto_email").getId();
            releasedReportToId = dictionary.fetchBySystemName("released_reportto_email").getId();
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
        }
    }

    public ArrayList<OrganizationParameterDO> fetchByOrganizationId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrganizationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<OrganizationParameterDO> fetchByOrgIdAndDictSystemName(Integer id, String systemName) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrgIdDictSystemName");
        query.setParameter("id", id);
        query.setParameter("systemName", systemName);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<OrganizationParameterDO> fetchByDictionarySystemName(String systemName) {
        Query query;

        query = manager.createNamedQuery("OrganizationParameter.FetchByDictionarySystemName");
        query.setParameter("systemName", systemName);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrganizationParameterDO add(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new OrganizationParameter();
        entity.setOrganizationId(data.getOrganizationId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationParameterDO update(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(OrganizationParameterDO data) throws Exception {
        Integer typeId;
        String value;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        typeId = data.getTypeId();
        value = data.getValue();
        
        if (DataBaseUtil.isEmpty(typeId))
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getOrganizationParameterTypeId()));
        if (DataBaseUtil.isEmpty(value)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getOrganizationParameterValue()));
        } else if (receivableReportToId.equals(typeId) || releasedReportToId.equals(typeId)) {
            try {
                EmailUtil.validateAddress(value);
            } catch (AddressException e) {
                list.add(new FieldErrorException("invalidFormatEmailException",
                                                 OrganizationMeta.getOrganizationParameterValue(), value));
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}