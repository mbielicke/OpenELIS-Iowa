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
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Attachment;
import org.openelis.meta.AttachmentMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

/**
 * The class manages attachment record and storage name/location. The actual
 * data file is stored within the file system (rather than the database) with
 * attachment id as the name, i.e. "1387" or "875461". The original filename is
 * kept in storage reference.
 * 
 * To reduce the number of attachments per directory, a series of sub
 * directories named 0-9 are nested within each other; currently there are 4
 * levels starting 0/0/0/0 through 9/9/9/9 allowing a million files to be
 * organized in groups of 100/directory using the last 4 digits of attachment
 * id. The path to the base directory is controlled with system variable
 * "attachment_directory".
 */

@Stateless
@SecurityDomain("openelis")
public class AttachmentBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

    @EJB
    private LockBean                    lock;
    
    private static final AttachmentMeta meta = new AttachmentMeta();

    /**
     * Returns the attachment record using its id
     */
    public AttachmentDO fetchById(Integer id) throws Exception {
        Query query;
        AttachmentDO data;

        query = manager.createNamedQuery("Attachment.FetchById");
        query.setParameter("id", id);

        try {
            data = (AttachmentDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    /**
     * Returns a distinct list of attachment records for given list of ids.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("Attachment.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    /**
     * Returns a distinct list of attachment records for given list of ids,
     * sorted in descending order of the ids.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchByIdsDescending(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("Attachment.FetchByIdsDescending");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    /**
     * Returns a distinct list of attachment records that don't have any
     * attachment items and match the given description, sorted in descending
     * order of the ids.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchUnattachedByDescription(String description, int first, int max) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("Attachment.FetchUnattachedByDescription");
        query.setParameter("description", description);
        query.setMaxResults(first + max);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();
        
        return DataBaseUtil.toArrayList(list);
    }

    public AttachmentDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().ATTACHMENT, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + AttachmentMeta.getId() +
                          ", " + AttachmentMeta.getDescription() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AttachmentMeta.getId() + " DESC");
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

    /**
     * Adds the specified attachment record to the database. If also sets
     * created date if one is not specified within the record.
     */
    public AttachmentDO add(AttachmentDO data) throws Exception {
        Attachment entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Attachment();
        if (data.getCreatedDate() == null)
            data.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        entity.setCreatedDate(data.getCreatedDate());
        entity.setTypeId(data.getTypeId());
        entity.setSectionId(data.getSectionId());
        entity.setDescription(data.getDescription());
        entity.setStorageReference(data.getStorageReference());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    /**
     * Updates the attachment record.
     */
    public AttachmentDO update(AttachmentDO data) throws Exception {
        Attachment entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Attachment.class, data.getId());
        entity.setCreatedDate(data.getCreatedDate());
        entity.setTypeId(data.getTypeId());
        entity.setSectionId(data.getSectionId());
        entity.setDescription(data.getDescription());
        entity.setStorageReference(data.getStorageReference());

        return data;
    }

    public AttachmentDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().ATTACHMENT, id);
        return fetchById(id);
    }

    /**
     * Removes the attachment record and data file from the file system.
     */
    public void delete(AttachmentDO data) throws Exception {
        if (data.getId() != null)
            delete(data.getId());
    }

    public void delete(Integer id) throws Exception {
        Attachment entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Attachment.class, id);
        if (entity != null) {

            manager.remove(entity);
        }
    }
}