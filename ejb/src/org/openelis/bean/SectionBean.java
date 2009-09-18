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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SectionViewDO;
import org.openelis.entity.Section;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.local.LockLocal;
import org.openelis.messages.SectionCacheMessage;
import org.openelis.metamap.SectionMetaMap;
import org.openelis.remote.SectionRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class SectionBean implements SectionRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private static final SectionMetaMap SectMeta = new SectionMetaMap();
    
    @EJB
    private LockLocal lockBean;
    
    @EJB
    private JMSMessageProducerLocal jmsProducer;
    
    private static Integer sectRefTableId;
    
    private SectionBean(){
        sectRefTableId = ReferenceTableCache.getReferenceTable("section");
    }
    
    public List getAutoCompleteSectionByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Section.AutoByName");
        query.setParameter("name", name);       
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    public SectionViewDO getSection(Integer sectionId) {
        SectionViewDO sectionDO;
        Query query;
        
        query = manager.createNamedQuery("Section.SectionDOById");
        query.setParameter("id", sectionId);
        sectionDO = (SectionViewDO)query.getSingleResult();
        return sectionDO;
    }

    public SectionViewDO getSectionAndLock(Integer sectionId, String session) throws Exception {
        lockBean.getLock(sectRefTableId, sectionId);
        return getSection(sectionId);
    }

    public SectionViewDO getSectionAndUnlock(Integer sectionId, String session) {
        lockBean.giveUpLock(sectRefTableId, sectionId);
        return getSection(sectionId);
    }

    public List<SectionViewDO> getSectionDOList() {
        Query query;
        List<SectionViewDO> sections;
        
        query = manager.createNamedQuery("Section.SectionDOList");       
        sections = query.getResultList();
        return sections;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(SectMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + SectMeta.getId()
                     + ", "
                     + SectMeta.getName()
                     + ") ");

        qb.addWhere(fields);

        qb.setOrderBy(SectMeta.getName());

        sb.append(qb.getEJBQL());
        
        Query query = manager.createQuery(sb.toString());
       
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList; 
    }

    public Integer updateSection(SectionViewDO sectionDO) throws Exception {
        Query query;
        Integer sectReferenceId,sectId;
        Section section;
        SectionCacheMessage msg;
        
        sectId = sectionDO.getId();
        
        if (sectId != null) {
            // we need to call lock one more time to make sure their lock
            // didnt expire and someone else grabbed the record
            lockBean.validateLock(sectRefTableId, sectId);
        }

        validateSection(sectionDO);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        section = null;
        
        if(sectId == null) {
            section = new Section();
        } else {
            section = manager.find(Section.class, sectId);
        }
        
        section.setDescription(sectionDO.getDescription());    
        section.setOrganizationId(sectionDO.getOrganizationId());
        section.setName(sectionDO.getName());
        section.setIsExternal(sectionDO.getIsExternal());
        section.setParentSectionId(sectionDO.getParentSectionId());
        
        sectId = section.getId();        
        if(sectId == null) {
            manager.persist(section);
        }
        lockBean.giveUpLock(sectRefTableId, sectId);     
        
        //invalidate the cache
        msg = new SectionCacheMessage();        
        msg.action = SectionCacheMessage.Action.UPDATED;
        jmsProducer.writeMessage(msg);
        return section.getId();
    }
    
    private void validateSection(SectionViewDO sectionDO) throws Exception{
        String name;
        ValidationErrorsList exceptionList;
        Query query;
        List<Section> sections;
        Section section;
        int i;
        Integer psecId;
        
        name = sectionDO.getName();
        exceptionList = new ValidationErrorsList();
        if(name == null || "".equals(name)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      SectMeta.getName()));
        } else {
            query = manager.createNamedQuery("Section.SectionsByName");
            query.setParameter("name", name);
            sections = query.getResultList();
            for(i = 0; i < sections.size();i++) {
                section = sections.get(i);
                if(!section.getId().equals(sectionDO.getId())) {
                    exceptionList.add(new FieldErrorException("fieldUniqueException",
                                                              SectMeta.getName()));
                    break;
                }
                
            }
        }
        
        if("Y".equals(sectionDO.getIsExternal()) && sectionDO.getOrganizationId() == null){
            exceptionList.add(new FormErrorException("orgNotSpecForExtSectionException"));
        }
        
        psecId = sectionDO.getParentSectionId();
        if(psecId != null && psecId.equals(sectionDO.getId())) {
            exceptionList.add(new FieldErrorException("sectItsOwnParentException",
                                                      SectMeta.getParentSectionId()));
        }
        
        if(exceptionList.size() > 0)
            throw exceptionList;
    }

}
