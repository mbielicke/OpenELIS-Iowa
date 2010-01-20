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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.entity.Section;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.local.LockLocal;
import org.openelis.messages.SectionCacheMessage;
import org.openelis.meta.SectionMeta;
import org.openelis.remote.SectionRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("section-select")
public class SectionBean implements SectionRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager               manager;

    @Resource
    private SessionContext              ctx;

    private static final SectionMeta    meta = new SectionMeta();

    @EJB
    private LockLocal                   lockBean;

    @EJB
    private JMSMessageProducerLocal     jmsProducer;

    public SectionViewDO fetchById(Integer id) throws Exception {
        SectionViewDO data;
        Query query;

        query = manager.createNamedQuery("Section.FetchById");
        query.setParameter("id", id);
        try {
            data = (SectionViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<IdNameVO> fetchByName(String name, int maxResults) throws Exception {
        Query query = manager.createNamedQuery("Section.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<SectionDO> fetchList() throws Exception {
        Query query;
        List<SectionViewDO> sections;

        query = manager.createNamedQuery("Section.FetchList");
        sections = query.getResultList();
        return DataBaseUtil.toArrayList(sections);
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + SectionMeta.getId() + ", " +
                          SectionMeta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(SectionMeta.getName());

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

    public SectionViewDO add(SectionViewDO data) throws Exception {
        Section section;
        SectionCacheMessage msg;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        section = new Section();

        section.setDescription(data.getDescription());
        section.setOrganizationId(data.getOrganizationId());
        section.setName(data.getName());
        section.setIsExternal(data.getIsExternal());
        section.setParentSectionId(data.getParentSectionId());
        manager.persist(section);

        data.setId(section.getId());

        // invalidate the cache
        msg = new SectionCacheMessage();
        msg.action = SectionCacheMessage.Action.UPDATED;
        msg.setSectionDO(data);
        jmsProducer.writeMessage(msg);
        return data;
    }

    public SectionViewDO update(SectionViewDO data) throws Exception {
        Section section;
        SectionCacheMessage msg;

        if ( !data.isChanged())
            return data;

        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lockBean.validateLock(ReferenceTable.SECTION, data.getId());
        manager.setFlushMode(FlushModeType.COMMIT);

        section = manager.find(Section.class, data.getId());

        section.setDescription(data.getDescription());
        section.setOrganizationId(data.getOrganizationId());
        section.setName(data.getName());
        section.setIsExternal(data.getIsExternal());
        section.setParentSectionId(data.getParentSectionId());

        lockBean.giveUpLock(ReferenceTable.SECTION, data.getId());

        // invalidate the cache
        msg = new SectionCacheMessage();
        msg.action = SectionCacheMessage.Action.UPDATED;
        msg.setSectionDO(data);
        jmsProducer.writeMessage(msg);
        return data;
    }

    public SectionViewDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.SECTION, id);
        return fetchById(id);
    }

    public SectionViewDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.SECTION, id);
        return fetchById(id);
    }

    private void validate(SectionViewDO data) throws Exception {
        String name;
        ValidationErrorsList exceptionList;
        Query query;
        List<IdNameVO> list;
        IdNameVO sectDO;
        int i;
        Integer psecId;

        name = data.getName();
        exceptionList = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(name)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException", meta.getName()));
        } else {
            query = manager.createNamedQuery("Section.FetchByName");
            query.setParameter("name", name);
            list = query.getResultList();
            for (i = 0; i < list.size(); i++) {
                sectDO = list.get(i);
                if(!sectDO.getId().equals(data.getId())) {
                    exceptionList.add(new FieldErrorException("fieldUniqueException", meta.getName()));
                    break;
                }
            }
        }

        if ("Y".equals(data.getIsExternal()) && data.getOrganizationId() == null) {
            exceptionList.add(new FormErrorException("orgNotSpecForExtSectionException"));
        }

        psecId = data.getParentSectionId();
        if (psecId != null && psecId.equals(data.getId())) {
            exceptionList.add(new FieldErrorException("sectItsOwnParentException",
                                                      meta.getParentSectionName()));
        }

        if (exceptionList.size() > 0)
            throw exceptionList;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "section", flag);
    }

}
