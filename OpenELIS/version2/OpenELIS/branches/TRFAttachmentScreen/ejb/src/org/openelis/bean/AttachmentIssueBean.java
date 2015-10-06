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
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.domain.Constants;
import org.openelis.entity.AttachmentIssue;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;

@Stateless
@SecurityDomain("openelis")
public class AttachmentIssueBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private UserCacheBean userCache;

    @EJB
    private LockBean      lock;

    public AttachmentIssueViewDO fetchByAttachmentId(Integer attachmentId) throws Exception {
        Query query;
        AttachmentIssueViewDO data;
        SystemUserVO user;

        query = manager.createNamedQuery("AttachmentIssue.FetchByAttachmentId");
        query.setParameter("id", attachmentId);
        try {
            data = (AttachmentIssueViewDO)query.getSingleResult();
            user = userCache.getSystemUser(data.getSystemUserId());
            if (user != null)
                data.setSystemUserLoginName(user.getLoginName());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    public ArrayList<AttachmentIssueViewDO> fetchList() throws Exception {
        Query query;
        SystemUserVO user;
        AttachmentIssueViewDO data;
        List<AttachmentIssueViewDO> list;
        ArrayList<AttachmentIssueViewDO> issues;

        query = manager.createNamedQuery("AttachmentIssue.FetchList");
        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();
        
        issues = DataBaseUtil.toArrayList(list);

        for (int i = 0; i < issues.size(); i++ ) {
            data = issues.get(i);

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUserLoginName(user.getLoginName());
            }
        }

        return issues;
    }

    public ArrayList<AttachmentIssueViewDO> fetchByAttachmentIds(ArrayList<Integer> attachmentIds) {
        int i;
        Query query;
        SystemUserVO user;
        AttachmentIssueViewDO data;
        List<AttachmentIssueViewDO> a;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("AttachmentIssue.FetchByAttachmentIds");
        a = new ArrayList<AttachmentIssueViewDO>();
        r = DataBaseUtil.createSubsetRange(attachmentIds.size());
        for (i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", attachmentIds.subList(r.get(i), r.get(i + 1)));
            a.addAll(query.getResultList());
        }

        for (i = 0; i < a.size(); i++ ) {
            data = a.get(i);

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUserLoginName(user.getLoginName());
            }
        }

        return DataBaseUtil.toArrayList(a);
    }
    
    public AttachmentIssueViewDO add(AttachmentIssueViewDO data) throws Exception {
        AttachmentIssue entity;
        SystemUserVO user;
        
        //checkSecurity(ModuleFlags.ADD);
    
        validate(data);
        
        lock.validateLock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());

        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AttachmentIssue();
        entity.setAttachmentId(data.getAttachmentId());
        entity.setTimestamp(Datetime.getInstance());
        entity.setSystemUserId(userCache.getId());
        entity.setText(data.getText());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        data.setTimestamp(entity.getTimestamp());
        data.setSystemUserId(entity.getSystemUserId());
        
        user = userCache.getSystemUser(data.getSystemUserId());
        if (user != null)
            data.setSystemUserLoginName(user.getLoginName());
        
        lock.unlock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());

        return data;
    }
    
    public AttachmentIssueViewDO update(AttachmentIssueViewDO data) throws Exception {
        AttachmentIssue entity;
        
        if ( !data.isChanged()) {
            lock.unlock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());
            return data;
        }
        
        //checkSecurity(ModuleFlags.UPDATE);
    
        validate(data);
        
        lock.validateLock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());

        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AttachmentIssue.class, data.getId());
        entity.setAttachmentId(data.getAttachmentId());
        entity.setTimestamp(data.getTimestamp());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setText(data.getText());
        
        lock.unlock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());

        return data;
    }

    public AttachmentIssueViewDO fetchForUpdate(Integer attachmentId) throws Exception {
        AttachmentIssueViewDO data;

        try {
            lock.lock(Constants.table().ATTACHMENT_ISSUE, attachmentId);
            data = fetchByAttachmentId(attachmentId);
        } catch (NotFoundException e) {
            /*
             * ignore because an attachment issue may not yet exist for this
             * attachment
             */
            data = null;
        }

        return data;
    }

    public AttachmentIssueViewDO unlock(Integer attachmentId) throws Exception {
        AttachmentIssueViewDO data;

        lock.unlock(Constants.table().ATTACHMENT_ISSUE, attachmentId);
        try {
            data = fetchByAttachmentId(attachmentId);
        } catch (NotFoundException e) {
            /*
             * ignore because an attachment issue may not yet exist for this
             * attachment
             */
            data = null;
        }

        return data;
    }
    
    public void delete(AttachmentIssueViewDO data) throws Exception {
        AttachmentIssue entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AttachmentIssue.class, data.getId());
        if (entity != null)
            manager.remove(entity);
        
        lock.unlock(Constants.table().ATTACHMENT_ISSUE, data.getAttachmentId());
    }

    /*private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("attachment", flag);
    }*/
    
    private void validate(AttachmentIssueViewDO data) {
        // TODO Auto-generated method stub
    }
}