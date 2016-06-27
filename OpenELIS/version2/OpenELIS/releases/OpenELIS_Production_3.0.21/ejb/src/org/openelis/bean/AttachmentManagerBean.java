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

import static org.openelis.manager.AttachmentManagerAccessor.*;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SectionPermission.SectionFlags;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class AttachmentManagerBean {

    @EJB
    private LockBean            lock;

    @EJB
    private AttachmentBean      attachment;

    @EJB
    private AttachmentItemBean  attachmentItem;

    @EJB
    private AttachmentIssueBean attachmentIssue;

    @EJB
    private SampleBean          sample;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private UserCacheBean       userCache;

    @EJB
    private SectionCacheBean    sectionCache;

    @EJB
    private WorksheetBean       worksheet;

    @EJB
    private IOrderBean          iorder;

    private static final Logger log                      = Logger.getLogger("openelis");

    protected static long       RESERVE_LOCK_TIME_MILLIS = 30 * 60 * 1000;

    /**
     * Fetches the attachment whose id is "attachmentId" and its attachment
     * items; fills a manager from those
     * 
     * @param attachmentId
     *        the id of an attachment
     * @return the filled manager
     * @throws Exception
     */
    public AttachmentManager fetchById(Integer attachmentId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentManager> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(attachmentId);
        oms = fetchByIds(ids);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Fetches the attachments whose ids are in "attachmentIds" and their
     * attachment items; fills managers from those; the attachments are sorted
     * in ascending order of their ids
     * 
     * @param attachmentIds
     *        the ids of attachments whose managers are to be returned
     * @return the filled managers
     * @throws Exception
     */
    public ArrayList<AttachmentManager> fetchByIds(ArrayList<Integer> attachmentIds) throws Exception {
        return fetchByIds(attachmentIds, false);
    }

    /**
     * Fetches attachments and their attachment items based on the query
     * specified in "fields"; fills managers from those
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the filled managers
     * @throws Exception
     */
    public ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        return fetchByQuery(fields, first, max, false, false);
    }

    /**
     * Fetches attachments and their attachment items based on the query
     * specified in "fields"; fills managers from those; the attachments don't
     * have any attachment items
     * 
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the filled managers
     * @throws Exception
     */
    public ArrayList<AttachmentManager> fetchByQueryUnattached(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        return fetchByQuery(fields, first, max, true, false);
    }

    /**
     * Fetches attachments and their attachment items based on the query
     * specified in "fields"; fills managers from those; the attachments are
     * sorted in descending order of their ids
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @return the filled managers
     * @throws Exception
     */
    public ArrayList<AttachmentManager> fetchByQueryDescending(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        return fetchByQuery(fields, first, max, false, true);
    }

    /**
     * Fetches and locks the attachment whose id is "attachmentId"; also fetches
     * its attachment items; fills a manager from those; the attachment is
     * locked for the default duration
     * 
     * @param attachmentId
     *        the id of the attachment record whose manager is to be returned
     * @return the filled manager
     * @throws Exception
     */
    public AttachmentManager fetchForUpdate(Integer attachmentId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentManager> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(attachmentId);
        ams = fetchForUpdate(ids, null);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Fetches and locks the attachment whose id is "attachmentId"; also fetches
     * its attachment items; fills a manager from those; the attachment is
     * locked for longer than the default duration
     * 
     * @param attachmentId
     *        the id of the attachment record whose manager is to be returned
     * @return the filled manager
     * @throws Exception
     */
    public AttachmentManager fetchForReserve(Integer attachmentId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentManager> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(attachmentId);
        ams = fetchForUpdate(ids, RESERVE_LOCK_TIME_MILLIS);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Adds/updates the attachment and related records in the database; the
     * records are validated before add/update and the attachment record must
     * have a lock record if it has an id
     * 
     * @param am
     *        the manager whose attachment and related records are to be
     *        added/updated
     * @return a manager with the added/updated records; added records in the
     *         manager have ids
     * @throws Exception
     */
    public AttachmentManager update(AttachmentManager am) throws Exception {
        ArrayList<AttachmentManager> ams;

        ams = new ArrayList<AttachmentManager>(1);
        ams.add(am);
        update(ams);
        return ams.get(0);
    }

    /**
     * Adds/updates the attachment and related records in the database; the
     * records are validated before add/update and the attachment record must
     * have a lock record if it has an id
     * 
     * @param ams
     *        the managers whose attachment and related records are to be
     *        added/updated
     * @return a list of manager with the added/updated records; added records
     *         in the managers have ids
     * @throws Exception
     */
    public ArrayList<AttachmentManager> update(ArrayList<AttachmentManager> ams) throws Exception {
        Integer sid;
        String sname;
        ValidationErrorsList e;
        HashSet<Integer> ids;
        ArrayList<Integer> fetchIds, locks;
        ArrayList<AttachmentDO> as;
        HashMap<Integer, AttachmentDO> amap;

        /*
         * get the id of the "system" section
         */
        try {
            sname = systemVariable.fetchByName("system_section").getValue();
            sid = sectionCache.getByName(sname).getId();
        } catch (Exception ex) {
            log.severe("No 'system_section' system variable defined");
            throw new InconsistencyException(Messages.get()
                                                     .systemVariable_missingInvalidSystemVariable("system_section"));
        }

        ids = new HashSet<Integer>();
        for (AttachmentManager am : ams) {
            if (getAttachment(am).getId() != null)
                ids.add(getAttachment(am).getId());
        }

        amap = new HashMap<Integer, AttachmentDO>();
        fetchIds = null;
        /*
         * fetch from the database, the data for the records being updated
         */
        if (ids.size() > 0) {
            fetchIds = new ArrayList<Integer>(ids);
            as = attachment.fetchByIds(fetchIds);
            for (AttachmentDO data : as)
                amap.put(data.getId(), data);
        }

        e = new ValidationErrorsList();
        /*
         * validate each attachment
         */
        for (AttachmentManager am : ams) {
            try {
                validate(am, amap.get(getAttachment(am).getId()), sid);
            } catch (ValidationErrorsList err) {
                if (err.hasErrors())
                    DataBaseUtil.mergeException(e, err);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        if (e.size() > 0)
            throw e;

        /*
         * check all the locks
         */
        if (ids.size() > 0) {
            locks = fetchIds;
            lock.validateLock(Constants.table().ATTACHMENT, locks);
        } else {
            locks = null;
        }

        ids = null;
        fetchIds = null;
        amap = null;

        /*
         * add/update attachment
         */
        for (AttachmentManager am : ams) {
            if (getAttachment(am).getId() == null)
                attachment.add(getAttachment(am));
            else
                attachment.update(getAttachment(am));
        }

        if (locks != null)
            lock.unlock(Constants.table().ATTACHMENT, locks);

        return ams;
    }

    /**
     * Creates attachment records using "paths"; the description of each created
     * attachment is the name of the file that it points to
     * 
     * @param paths
     *        list of paths to specific files
     * @param sectionId
     *        the id used to set the section permission for each created
     *        attachment
     * @return the list of managers containing the created attachment records
     * @throws Exception
     */
    public ArrayList<AttachmentManager> put(List<String> paths, Integer sectionId) throws Exception {
        String base, name;
        AttachmentDO data;
        ArrayList<Integer> ids;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }

        if (sectionId == null) {
            try {
                name = ReportUtil.getSystemVariableValue("internal_section");
                sectionId = sectionCache.getByName(name).getId();
            } catch (Exception e) {
                log.severe("No 'internal_section' system variable defined");
                throw new InconsistencyException(Messages.get()
                                                         .systemVariable_missingInvalidSystemVariable("internal_section"));
            }
        }

        ids = new ArrayList<Integer>();
        /*
         * create attachment records by saving the uploaded files and fetch the
         * managers for those records
         */
        for (String p : paths) {
            data = put(base, p, true, null, null, sectionId);
            ids.add(data.getId());
        }

        return fetchByIds(ids);
    }

    /**
     * Creates an attachment record; the file specified by "file" is renamed and
     * moved to the attachment directory; if "getNameFromFile" is true, the
     * attachment record's storage reference field is set to the name of the
     * file at "file", otherwise the field is set as "fileName"; the attachment
     * record's description field is set as "description" if it's not null,
     * otherwise the field is set to the same value as the storage reference
     * 
     * @param file
     *        the full path to a file
     * @param getNameFromFile
     *        the flag that determines what should be set as the storage
     *        reference of the created record
     * @param filename
     *        the value that could be set as the storage reference of the
     *        created record
     * @param description
     *        if not null, this is set as the description of the created record
     * @param sectionId
     *        the id used to set the section permission for the created record
     * @return the DO corresponding to the created record
     * @throws Exception
     */
    public AttachmentDO put(String file, boolean getNameFromFile, String filename,
                            String description, Integer sectionId) throws Exception {
        String base, name;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }

        if (sectionId == null) {
            try {
                name = ReportUtil.getSystemVariableValue("internal_section");
                sectionId = sectionCache.getByName(name).getId();
            } catch (Exception e) {
                log.severe("No 'internal_section' system variable defined");
                throw new InconsistencyException(Messages.get()
                                                         .systemVariable_missingInvalidSystemVariable("internal_section"));
            }
        }

        return put(base, file, getNameFromFile, filename, description, sectionId);
    }

    /**
     * Creates an attachment record; the file specified by "file" is renamed and
     * moved to the directory specified by "base"; if "getNameFromFile" is true,
     * the attachment record's storage reference field is set to the name of the
     * file at "file", otherwise the field is set as "fileName"; the attachment
     * record's description field is set as "description" if it's not null,
     * otherwise the field is set to the same value as the storage reference
     * 
     * @param base
     *        the directory to which the file specified by "file" is moved
     * @param file
     *        the full path to a file
     * @param getNameFromFile
     *        the flag that determines what should be set as the storage
     *        reference of the created record
     * @param filename
     *        the value that could be set as the storage reference of the
     *        created record
     * @param description
     *        if not null, this is set as the description of the created record
     * @param sectionId
     *        the id used to set the section permission for the created record
     * @return the DO corresponding to the created record
     * @throws Exception
     */
    public AttachmentDO put(String base, String file, boolean getNameFromFile, String filename,
                            String description, Integer sectionId) throws Exception {
        Path src, dst;
        AttachmentDO data;

        src = Paths.get(file);
        if (getNameFromFile)
            filename = src.getFileName().toString();
        if (description == null)
            description = filename;

        /*
         * insert the attachment and move the file to the right location
         */
        data = attachment.add(new AttachmentDO(0, null, null, sectionId, description, filename));
        dst = Paths.get(base, ReportUtil.getAttachmentSubdirectory(data.getId()), data.getId()
                                                                                      .toString());
        try {
            Files.move(src, dst);
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Can't move file '" + src.toString() + "' to '" + dst.toString() +
                                  "'", anyE);
            throw new DatabaseException(Messages.get().attachment_moveFileException(dst.toString()));
        }

        return data;
    }

    /**
     * Copies to the upload directory, the file linked to the attachment record
     * whose id is "attachmentId"; creates a ReportStatus containing the path
     * and name of the copied file
     * 
     * @param attachmentId
     *        the id of an attachment record
     * @return the created ReportStatus
     * @throws Exception
     */
    public ReportStatus get(Integer attachmentId) throws Exception {
        String base;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }

        return get(base, attachmentId);
    }

    /**
     * Copies to the upload directory, the file linked to the attachment record
     * whose id is "attachmentId"; creates a ReportStatus containing the path
     * and name of the copied file
     * 
     * @param base
     *        the name of the attachment directory
     * @param attachmentId
     *        the id of an attachment record
     * @return the created ReportStatus
     * @throws Exception
     */
    public ReportStatus get(String base, Integer attachmentId) throws Exception {
        Integer index;
        String ref, prefix, suffix;
        AttachmentDO data;
        Path src, dst;
        ReportStatus status;
        SystemUserPermission perm;
        SectionPermission sp;
        ModulePermission mp;
        SectionViewDO sect;
        OutputStream out;
        ArrayList<Integer> ids;
        ArrayList<SampleDO> samples;
        ArrayList<AttachmentItemViewDO> items;

        /*
         * make sure that the attachment wasn't deleted
         */
        data = attachment.fetchById(attachmentId);

        /*
         * find out if this attachment is attached to a sample; don't show the
         * attachment if the sample contains patient data and the user doesn't
         * have permission to view patients
         */
        ids = new ArrayList<Integer>();
        ids.add(attachmentId);
        items = attachmentItem.fetchByAttachmentIds(ids);
        perm = userCache.getPermission();
        if (items.size() > 0) {
            ids.clear();
            for (AttachmentItemViewDO ai : items) {
                if (Constants.table().SAMPLE.equals(ai.getReferenceTableId()))
                    ids.add(ai.getReferenceId());
            }
            if (ids.size() > 0) {
                samples = sample.fetchByIds(ids);
                mp = perm.getModule("patient");
                for (SampleDO s : samples) {
                    if ((Constants.domain().CLINICAL.equals(s.getDomain()) ||
                        Constants.domain().NEONATAL.equals(s.getDomain())) &&
                        (mp == null || !mp.hasSelectPermission()))
                        throw new InconsistencyException(Messages.get()
                                                                 .attachment_viewPermException(data.getDescription()));
                }
            }
        }
        ids = null;
        items = null;
        samples = null;

        /*
         * don't show the attachment if the user doesn't have permission to view
         * it
         */
        if (data.getSectionId() != null) {
            sect = sectionCache.getById(data.getSectionId());
            sp = perm.getSection(sect.getName());

            if (sp == null || !sp.hasViewPermission())
                throw new InconsistencyException(Messages.get()
                                                         .attachment_viewPermException(data.getDescription()));
        }

        src = Paths.get(base,
                        ReportUtil.getAttachmentSubdirectory(attachmentId),
                        attachmentId.toString());
        ref = data.getStorageReference();
        index = ref.lastIndexOf(".");
        /*
         * get the file name and extension from the storage reference if there
         * is a "." in the reference and there's something to the left and right
         * of it; otherwise, use the whole reference as the prefix and let the
         * suffix be empty; last index is used instead of splitting on the "."
         * because the reference could have multiple of them.
         */
        if (index > 0 && index < ref.length() - 1) {
            prefix = ref.substring(0, index);
            suffix = ref.substring(index, ref.length());
        } else {
            prefix = ref;
            suffix = "";
        }
        /*
         * copy the contents of the file linked to the attachment, to the upload
         * directory
         */
        dst = ReportUtil.createTempFile(prefix, suffix, "upload_stream_directory");
        out = Files.newOutputStream(dst);
        try {
            Files.copy(src, out);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException(e.getMessage());
        } finally {
            if (out != null)
                out.close();
        }
        status = new ReportStatus();
        status.setMessage(dst.getFileName().toString())
              .setPath(dst.toString())
              .setStatus(ReportStatus.Status.SAVED);
        return status;
    }

    /**
     * Finds the TRF for the sample with the passed id and copies the file to
     * the upload directory; creates a ReportStatus containing the path and name
     * of the copied file
     * 
     * @param sampleId
     *        the id of a sample
     * @return the created ReportStatus
     * @throws Exception
     */
    public ReportStatus getTRF(Integer sampleId) throws Exception {
        String pattern;
        ArrayList<AttachmentDO> attachments;

        try {
            pattern = systemVariable.fetchByName("attachment_pattern_gen_sql").getValue();
            pattern = pattern.replaceAll("\\*", "\\%");
        } catch (Exception e) {
            log.severe("No 'attachment_pattern_gen_sql' system variable defined");
            throw new InconsistencyException(Messages.get()
                                                     .attachment_missingGenericPatternException());
        }

        try {
            attachments = attachment.fetchByDescriptionReferenceIdReferenceTableId(pattern,
                                                                                   sampleId,
                                                                                   Constants.table().SAMPLE);
        } catch (NotFoundException e) {
            throw new InconsistencyException(Messages.get().attachment_trfNotFoundException());
        }

        return get(attachments.get(0).getId());
    }

    /**
     * Unlocks the attachment record whose id is "attachmentId"
     * 
     * @param attachmentId
     *        the id of the attachment record to be unlocked
     * @return the manager for the unlocked attachment record
     * @throws Exception
     */
    public AttachmentManager unlock(Integer attachmentId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentManager> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(attachmentId);
        ams = unlock(ids);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Unlocks the attachment record whose ids are "attachmentIds"
     * 
     * @param attachmentIds
     *        the ids of the attachment records to be unlocked
     * @return the managers for the unlocked attachment records
     * @throws Exception
     */
    public ArrayList<AttachmentManager> unlock(ArrayList<Integer> attachmentIds) throws Exception {
        lock.unlock(Constants.table().ATTACHMENT, attachmentIds);
        return fetchByIds(attachmentIds);
    }

    /**
     * Deletes the attachment specified by "data" and the file linked to it
     * 
     * @param base
     *        the top-level directory that contains the file
     * @param data
     *        the DO for the attachment to be deleted
     * @throws Exception
     */
    public void delete(String base, AttachmentDO data) throws Exception {
        Path src;

        src = Paths.get(base, ReportUtil.getAttachmentSubdirectory(data.getId()), data.getId()
                                                                                      .toString());
        Files.delete(src);
        attachment.delete(data);
    }

    /**
     * Deletes the attachments and issues in "ams" from the database; also
     * deletes the files linked to the attachments
     * 
     * @param ams
     *        the managers containing the attachments, items and issues to be
     *        deleted
     * @throws Exception
     */
    public void delete(ArrayList<AttachmentManager> ams) throws Exception {
        Integer count;
        String base;
        SystemUserPermission permission;
        ValidationErrorsList e;
        HashSet<Integer> ids;
        ArrayList<Integer> fetchIds, locks;
        HashMap<Integer, Integer> imap;

        ids = new HashSet<Integer>();
        for (AttachmentManager am : ams)
            ids.add(getAttachment(am).getId());

        /*
         * fetch attachment items for the attachments to be deleted; keep track
         * of how many attachment items are there for each attachment; this is
         * used to make sure that attached attachments don't get deleted; that
         * can happen if an attachment isn't attached when Delete is clicked on
         * Attachment screen but gets attached later e.g. on a login screen and
         * the sample is committed before the attachment can be deleted here
         */
        imap = new HashMap<Integer, Integer>();
        fetchIds = new ArrayList<Integer>(ids);
        if (ids.size() > 0) {
            for (AttachmentItemViewDO data : attachmentItem.fetchByAttachmentIds(fetchIds)) {
                count = imap.get(data.getAttachmentId());
                if (count == null)
                    count = 0;
                imap.put(data.getAttachmentId(), ++count);
            }
        }

        permission = userCache.getPermission();
        e = new ValidationErrorsList();
        /*
         * validate each attachment to make sure that it can be deleted
         */
        for (AttachmentManager am : ams) {
            try {
                validateForDelete(am, imap, permission);
            } catch (ValidationErrorsList err) {
                if (err.hasErrors())
                    DataBaseUtil.mergeException(e, err);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        if (e.size() > 0)
            throw e;

        /*
         * check all the locks; there must be a lock on the issue even if an
         * attachment doesn't have an issue; this is to prevent the user from
         * adding/updating an issue while its attachment is being deleted
         */
        if (ids.size() > 0) {
            locks = fetchIds;
            lock.validateLock(Constants.table().ATTACHMENT, locks);
            lock.validateLock(Constants.table().ATTACHMENT_ISSUE, locks);
        } else {
            locks = null;
        }
        ids = null;
        fetchIds = null;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'attachment_directory'", any);
            throw new FormErrorException(Messages.get()
                                                 .systemVariable_missingInvalidSystemVariable("attachment_directory"));
        }

        /*
         * delete the attachments and issues while maintaining referential
         * integrity; also delete the files linked to the attachments
         */
        for (AttachmentManager am : ams) {
            if (getIssue(am) != null)
                attachmentIssue.delete(getIssue(am));
            delete(base, getAttachment(am));
        }

        if (locks != null) {
            lock.unlock(Constants.table().ATTACHMENT, locks);
            lock.unlock(Constants.table().ATTACHMENT_ISSUE, locks);
        }
    }

    /**
     * Fetches the attachment whose ids are "attachmentIds"; also fetches their
     * attachment items; fills a manager from those; if "isDescending" is true,
     * the returned managers are sorted in descending order of the ids;
     * otherwise they're sorted in ascending order
     * 
     * @param attachmentIds
     *        the ids of the attachment records whose managers are to be
     *        returned
     * @param isDescending
     *        a flag that determines whether the returned managers are sorted in
     *        descending or ascending order
     * @return the created managers
     * @throws Exception
     */
    private ArrayList<AttachmentManager> fetchByIds(ArrayList<Integer> attachmentIds,
                                                    boolean isDescending) throws Exception {
        String idStr;
        AttachmentManager am;
        SampleDO s;
        WorksheetViewDO w;
        IOrderViewDO o;
        ArrayList<Integer> ids, sids, wids, oids;
        ArrayList<AttachmentManager> ams;
        ArrayList<AttachmentDO> as;
        ArrayList<SampleDO> samples;
        ArrayList<WorksheetViewDO> ws;
        ArrayList<IOrderViewDO> os;
        HashMap<Integer, AttachmentManager> map;
        HashMap<Integer, SampleDO> smap;
        HashMap<Integer, WorksheetViewDO> wmap;
        HashMap<Integer, IOrderViewDO> omap;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        ams = new ArrayList<AttachmentManager>();

        /*
         * return an empty list if there are no attachments; AttachmentBean's
         * fetchByIdsDescending is called instead of fetchByIds, because the
         * current method needs to return the managers sorted in descending
         * order as explained at the top
         */
        if (isDescending)
            as = attachment.fetchByIds(attachmentIds, true);
        else
            as = attachment.fetchByIds(attachmentIds);

        if (as.size() == 0) {
            /*
             * no managers were fetched because none of the attachments with the
             * passed ids exist
             */
            idStr = DataBaseUtil.concatWithSeparator(attachmentIds, ", ");
            throw new InconsistencyException(Messages.get()
                                                     .attachment_attachmentsNotExistException(idStr));
        }

        /*
         * build level 1, everything is based on attachment ids
         */
        ids = new ArrayList<Integer>();
        map = new HashMap<Integer, AttachmentManager>();

        for (AttachmentDO data : as) {
            am = new AttachmentManager();
            setAttachment(am, data);
            ams.add(am);

            ids.add(data.getId()); // for fetch
            map.put(data.getId(), am); // for linking
        }

        sids = new ArrayList<Integer>();
        wids = new ArrayList<Integer>();
        oids = new ArrayList<Integer>();
        for (AttachmentItemViewDO data : attachmentItem.fetchByAttachmentIds(ids)) {
            am = map.get(data.getAttachmentId());
            addItem(am, data);
            if (Constants.table().SAMPLE.equals(data.getReferenceTableId()))
                sids.add(data.getReferenceId());
            else if (Constants.table().WORKSHEET.equals(data.getReferenceTableId()))
                wids.add(data.getReferenceId());
            else if (Constants.table().IORDER.equals(data.getReferenceTableId()))
                oids.add(data.getReferenceId());
        }

        for (AttachmentIssueViewDO data : attachmentIssue.fetchByAttachmentIds(ids)) {
            am = map.get(data.getAttachmentId());
            setIssue(am, data);
        }

        /*
         * fetch all the samples, worksheets, iorders linked to the attachments
         */
        smap = new HashMap<Integer, SampleDO>();
        if (sids.size() > 0) {
            samples = sample.fetchByIds(sids);
            for (SampleDO data : samples)
                smap.put(data.getId(), data);
        }

        wmap = new HashMap<Integer, WorksheetViewDO>();
        if (wids.size() > 0) {
            ws = worksheet.fetchByIds(wids);
            for (WorksheetViewDO data : ws)
                wmap.put(data.getId(), data);
        }

        omap = new HashMap<Integer, IOrderViewDO>();
        if (oids.size() > 0) {
            os = iorder.fetchByIds(oids);
            for (IOrderViewDO data : os)
                omap.put(data.getId(), data);
        }

        /*
         * go through the items in all managers and set the reference
         * description based on the reference table
         */
        for (AttachmentManager am1 : ams) {
            if (getItems(am1) != null) {
                for (AttachmentItemViewDO data : getItems(am1)) {
                    am = map.get(data.getAttachmentId());
                    if (Constants.table().SAMPLE.equals(data.getReferenceTableId())) {
                        s = smap.get(data.getReferenceId());
                        data.setReferenceDescription(Messages.get()
                                                             .attachment_sampleDescription(s.getAccessionNumber()));
                    } else if (Constants.table().WORKSHEET.equals(data.getReferenceTableId())) {
                        w = wmap.get(data.getReferenceId());
                        data.setReferenceDescription(Messages.get()
                                                             .attachment_worksheetDescription(w.getId()));
                    } else if (Constants.table().IORDER.equals(data.getReferenceTableId())) {
                        o = omap.get(data.getReferenceId());
                        data.setReferenceDescription(Messages.get()
                                                             .attachment_iorderDescription(o.getId()));
                    }

                }
            }
        }

        return ams;
    }

    /**
     * Fetches the attachments and their attachment items based on the query
     * specified in "fields"; fills managers from those; if "isDescending" is
     * true, the returned managers are sorted in descending order of the ids;
     * otherwise they're sorted in ascending order
     * 
     * @param fields
     *        the fields used in the query
     * @param first
     *        the index of the first record to be returned by the query i.e. the
     *        first record in the current "page"
     * @param max
     *        the maximum number of records to be returned by the query
     * @param isDescending
     *        a flag that determines whether the returned managers are sorted in
     *        descending or ascending order
     * @return the filled managers
     * @throws Exception
     */
    private ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                      int max, boolean isUnattached,
                                                      boolean isDescending) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentDO> attachments;

        ids = new ArrayList<Integer>();
        /*
         * if (isDescending) attachments = attachment.query(fields, first, max,
         * false, true); else attachments = attachment.query(fields, first,
         * max);
         */
        attachments = attachment.query(fields, first, max, isUnattached, isDescending);
        for (AttachmentDO data : attachments)
            ids.add(data.getId());

        return fetchByIds(ids, isDescending);
    }

    /**
     * Fetches and locks the attachment whose ids are "attachmentIds"; also
     * fetches its attachment items and issues; fills a manager from those; if
     * "expires" is not null, the attachment is locked for the duration
     * specified by it; otherwise it's locked for the default duration
     * 
     * @param attachmentIds
     *        the ids of the attachment records whose managers are to be
     *        returned
     * @param expires
     *        a number that specifies for how many milliseconds records should
     *        be locked
     * @return the filled managers
     * @throws Exception
     */
    private ArrayList<AttachmentManager> fetchForUpdate(ArrayList<Integer> attachmentIds,
                                                        Long expires) throws Exception {
        if (expires == null)
            lock.lock(Constants.table().ATTACHMENT, attachmentIds);
        else
            lock.lock(Constants.table().ATTACHMENT, attachmentIds, expires);
        return fetchByIds(attachmentIds);
    }

    /**
     * Validates the attachment record in "am"
     * 
     * @param am
     *        the manager whose attachment record is to be validated
     * @param dbData
     *        the DO containing the data from the database for the attachment
     *        record with the same id as "data"; this will be null if a record
     *        doesn't exist for "data" yet, i.e. if "data" has a null id
     * @param systemId
     *        the id of the "system" section
     * @throws Exception
     */
    private void validate(AttachmentManager am, AttachmentDO dbData, Integer systemId) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        if (getAttachment(am).isChanged())
            try {
                attachment.validate(getAttachment(am), dbData, systemId);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }

        if (e.size() > 0)
            throw e;
    }

    /**
     * Validates whether the attachment and issue records in "am" can be
     * deleted; attached attachments can't be deleted; the user must have the
     * permission to delete the attachment
     * 
     * @param am
     *        the manager whose attachment and issue records are to be deleted
     * @param imap
     *        the map where the key is an attachment's id and the value is the
     *        number of attachment items that the attachment has
     * @param permission
     *        the SystemUserPermission for the logged in user
     */
    private void validateForDelete(AttachmentManager am, HashMap<Integer, Integer> imap,
                                   SystemUserPermission permission) throws Exception {
        Integer count;
        String description, name;
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        /*
         * attached attachments can't be deleted
         */
        description = getAttachment(am).getDescription();
        count = imap.get(getAttachment(am).getId());
        if (count != null && count > 0)
            e.add(new FormErrorException(Messages.get()
                                                 .attachment_cantDeleteAttachedException(description)));

        /*
         * to delete the attachment, the user must have "Cancel" permission to
         * its section
         */
        name = sectionCache.getById(am.getAttachment().getSectionId()).getName();
        if ( !permission.has(name, SectionFlags.CANCEL))
            e.add(new FormErrorException(Messages.get().attachment_deletePermException(description)));

        if (e.size() > 0)
            throw e;
    }
}