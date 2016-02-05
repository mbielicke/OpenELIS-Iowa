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
import java.util.Date;
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
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Attachment;
import org.openelis.meta.AttachmentMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
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
     * Fetches the attachment records whose ids are "attachmentId"
     * 
     * @param attachmentId
     *        the id of the attachment record to be returned
     * @return the DO corresponding to the fetched attachment record
     * @throws Exception
     */
    public AttachmentDO fetchById(Integer attachmentId) throws Exception {
        Query query;
        AttachmentDO data;

        query = manager.createNamedQuery("Attachment.FetchById");
        query.setParameter("id", attachmentId);

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
     * Fetches the attachment records whose ids are "attachmentIds"; if
     * "isDescending" is true, the returned DOs are sorted in descending order
     * of the ids; otherwise they're sorted in ascending order
     * 
     * @param attachmentIds
     *        the ids of the attachment records to be returned
     * @param isDescending
     *        a flag that determines whether the returned DOs are sorted in
     *        descending or ascending order
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    public ArrayList<AttachmentDO> fetchByIds(ArrayList<Integer> attachmentIds) throws Exception {
        return fetchByIds(attachmentIds, false);
    }

    /**
     * Fetches the attachment whose ids are "attachmentIds"; if "isDescending"
     * is true, the returned DOs are sorted in descending order of the ids;
     * otherwise they're sorted in ascending order
     * 
     * @param attachmentIds
     *        the ids of the attachment records to be returned
     * @param isDescending
     *        a flag that determines whether the returned DOs are sorted in
     *        descending or ascending order
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchByIds(ArrayList<Integer> attachmentIds, boolean isDescending) throws Exception {
        Query query;
        List<AttachmentDO> a;
        ArrayList<Integer> r;

        if (isDescending)
            query = manager.createNamedQuery("Attachment.FetchByIdsDescending");
        else
            query = manager.createNamedQuery("Attachment.FetchByIds");
        a = new ArrayList<AttachmentDO>();
        r = DataBaseUtil.createSubsetRange(attachmentIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", attachmentIds.subList(r.get(i), r.get(i + 1)));
            a.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(a);
    }

    /**
     * Fetches attachments that don't have attachment items, based on
     * "description"
     * 
     * @param description
     *        the value used to find attachments with matching description
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchUnattachedByDescription(String description, int first,
                                                                int max) throws Exception {
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

    /**
     * Fetches the attachment records that don't have any attachment items and
     * issues and were created before "createdDate" so that they can be removed
     * from the system
     * 
     * @param createdDate
     *        all fetched attachment records were created before this date
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchForRemove(Date createdDate) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("Attachment.FetchForRemove");
        query.setParameter("createdDate", createdDate);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    /**
     * Fetches the attachment records whose description matches "description"
     * and whose attachment items are linked to the record with the passed
     * reference id and reference table id
     * 
     * @param description
     *        the value used to find attachments with matching description
     * @param referenceId
     *        the id of a particular record (e.g. a sample); used to find
     *        attachment items linked to that record
     * @param referenceTableId
     *        the id of paticular table (e.g sample); used to find attachment
     *        items linked to records from that table
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> fetchByDescriptionReferenceIdReferenceTableId(String description,
                                                                                 Integer referenceId,
                                                                                 Integer referenceTableId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("Attachment.FetchByDescriptionReferenceIdReferenceTableId");
        query.setParameter("description", description);
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    /**
     * Fetches attachments based on the query specified in "fields"
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    public ArrayList<AttachmentDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        return query(fields, first, max, false, false);
    }

    /**
     * Fetches attachments based on the query specified in "fields"; if
     * "isDescending" is true, the returned attachments are sorted in descending
     * order of the ids; otherwise they're sorted in ascending order
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the DOs corresponding to the fetched attachment records
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentDO> query(ArrayList<QueryData> fields, int first, int max,
                                         boolean isUnattached, boolean isDescending) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.AttachmentDO(" +
                          AttachmentMeta.getId() + ", " + AttachmentMeta.getCreatedDate() + ", " +
                          AttachmentMeta.getTypeId() + ", " + AttachmentMeta.getSectionId() + ", " +
                          AttachmentMeta.getDescription() + ", " +
                          AttachmentMeta.getStorageReference() + ") ");
        builder.constructWhere(fields);
        if (isUnattached)
            builder.addWhere(AttachmentMeta.getId() +
                             " not in (select i.attachmentId from AttachmentItem i where i.attachmentId = " +
                             AttachmentMeta.getId() + ")");
        if (isDescending)
            builder.setOrderBy(AttachmentMeta.getId() + " DESC");
        else
            builder.setOrderBy(AttachmentMeta.getId() + " ASC");
        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<AttachmentDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<AttachmentDO>)list;
    }

    /**
     * Adds the attachment record corresponding to "data", to the database; also
     * sets created date if it's not specified within "data"
     * 
     * @param "data" the DO representing the attachment record to be added
     * @return the DO corresponding to the added attachment
     * @throws Exception
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
     * Updates the attachment record corresponding to "data"
     * 
     * @param "data" the DO representing the attachment record to be updated
     * @return the DO corresponding to the updated attachment
     * @throws Exception
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

    /**
     * Unlocks the attachment record whose id is "attachmentId"
     * 
     * @param attachmentId
     *        the id of the attachment record to be unlocked
     * @return the DO corresponding to the unlocked attachment
     * @throws Exception
     */
    public AttachmentDO abortUpdate(Integer attachmentId) throws Exception {
        lock.unlock(Constants.table().ATTACHMENT, attachmentId);
        return fetchById(attachmentId);
    }

    /**
     * Removes the attachment record corresponding to "data" from the database,
     * if its id is not null
     * 
     * @param "data" the DO representing the attachment record to be deleted
     * @throws Exception
     */
    public void delete(AttachmentDO data) throws Exception {
        if (data.getId() != null)
            delete(data.getId());
    }

    /**
     * Removes the attachment record whose id is "attachmentId" from the
     * database
     * 
     * @param attachmentId
     *        the id of the attachment record to be deleted
     * @throws Exception
     */
    public void delete(Integer attachmentId) throws Exception {
        Attachment entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Attachment.class, attachmentId);
        if (entity != null)
            manager.remove(entity);
    }

    /**
     * Validates "data"; validation includes checking whether description is
     * empty; it also includes checking if either the section for this record in
     * the database is "system" and is being changed to something else or if
     * it's something else in the database, it's being changed to "system"
     * 
     * @param data
     *        the DO for the attachment record to be validated; the data in it
     *        has not been committed to the database yet
     * @param dbData
     *        the DO containing the data from the database for the attachment
     *        record with the same id as "data"; this will be null if a record
     *        doesn't exist for "data" yet, i.e. if "data" has a null id
     * @param systemId
     *        the id of the "system" section
     * @throws Exception
     */
    public void validate(AttachmentDO data, AttachmentDO dbData, Integer systemId) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getDescription()))
            e.add(new FormErrorException(Messages.get()
                                                 .attachment_descRequiredException(data.getDescription())));

        if (data.getSectionId() == null)
            e.add(new FormErrorException(Messages.get()
                                                 .attachment_sectRequiredException(data.getDescription())));

        /*
         * validate the section if the two DOs have different sections
         */
        if (dbData != null && DataBaseUtil.isDifferent(data.getSectionId(), dbData.getSectionId())) {
            if (systemId.equals(dbData.getSectionId()))
                e.add(new FormErrorException(Messages.get()
                                                     .attachment_cantChangeFromSystemException(data.getDescription())));
            else if (systemId.equals(data.getSectionId()))
                e.add(new FormErrorException(Messages.get()
                                                     .attachment_cantChangeToSystemException(data.getDescription())));
        }

        if (e.size() > 0)
            throw e;
    }
}