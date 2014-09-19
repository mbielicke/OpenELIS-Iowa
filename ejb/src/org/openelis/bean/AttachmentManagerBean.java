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
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SectionPermission;
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
    private SampleBean          sample;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private UserCacheBean       userCache;

    @EJB
    private SectionCacheBean    sectionCache;

    private static final Logger log = Logger.getLogger("openelis");

    /**
     * Returns an attachment manager for specified primary id and requested load
     * elements
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
     * Returns attachment managers for specified primary ids. The returned
     * managers are sorted in descending order of the ids because this method
     * can be called for the front-end and there the most recent attachments are
     * shown first.
     */
    public ArrayList<AttachmentManager> fetchByIds(ArrayList<Integer> attachmentIds) throws Exception {
        AttachmentManager am;
        SampleDO s;
        ArrayList<Integer> ids, sids;
        ArrayList<AttachmentManager> ams;
        ArrayList<AttachmentDO> attachments;
        ArrayList<SampleDO> samples;
        HashMap<Integer, AttachmentManager> map;
        HashMap<Integer, SampleDO> smap;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        ams = new ArrayList<AttachmentManager>();

        /*
         * if there are no attachments, then return an empty list;
         * AttachmentBean's fetchByIdsDescending is called instead of fetchByIds
         * , because the current method needs to return the managers sorted in
         * descending order as explained at the top
         */
        attachments = attachment.fetchByIdsDescending(attachmentIds);
        if (attachments.size() < 1)
            return ams;

        /*
         * build level 1, everything is based on attachment ids
         */
        ids = new ArrayList<Integer>();
        map = new HashMap<Integer, AttachmentManager>();

        for (AttachmentDO data : attachments) {
            am = new AttachmentManager();
            setAttachment(am, data);
            ams.add(am);

            ids.add(data.getId()); // for fetch
            map.put(data.getId(), am); // for linking
        }

        sids = new ArrayList<Integer>();
        for (AttachmentItemViewDO data : attachmentItem.fetchByAttachmentIds(ids)) {
            am = map.get(data.getAttachmentId());
            addItem(am, data);
            if (Constants.table().SAMPLE.equals(data.getReferenceTableId()))
                sids.add(data.getReferenceId());
        }

        smap = new HashMap<Integer, SampleDO>();
        if (sids.size() > 0) {
            /*
             * fetch all the samples linked to the attachments
             */
            samples = sample.fetchByIds(sids);
            for (SampleDO data : samples)
                smap.put(data.getId(), data);
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
                        data.setReferenceDescription("Sample # " + s.getAccessionNumber());
                    }
                }
            }
        }

        return ams;
    }

    /**
     * Returns attachment managers based on the specified query. The returned
     * managers are sorted in descending order of the ids because this method
     * can be called from the front-end and there the most recent attachments
     * are shown first.
     */
    public ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : attachment.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns attachment managers based on the specified description. The
     * returned managers are sorted in descending order of the ids because this
     * method can be called from the front-end and there the most recent
     * attachments are shown first.
     */
    public ArrayList<AttachmentManager> fetchUnattachedByDescription(String description, int first,
                                                                     int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (AttachmentDO data : attachment.fetchUnattachedByDescription(description, first, max))
            ids.add(data.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns a locked attachment manager with specified attachment id
     */
    public AttachmentManager fetchForUpdate(Integer attachmentId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AttachmentManager> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(attachmentId);
        ams = fetchForUpdate(ids);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Returns a list of locked attachment managers with specified sample ids
     * and requested load elements
     */
    public ArrayList<AttachmentManager> fetchForUpdate(ArrayList<Integer> attachmentIds) throws Exception {
        lock.lock(Constants.table().ATTACHMENT, attachmentIds);
        return fetchByIds(attachmentIds);
    }

    /**
     * Returns the manager for the passed id if the attachment has either been
     * attached at least once or if it can be locked
     */
    public AttachmentManager getReserved(Integer attachmentId) throws Exception {
        AttachmentManager am;

        am = fetchById(attachmentId);
        if (getItems(am) != null)
            return am;
        return fetchForUpdate(attachmentId);
    }

    /**
     * Adds/updates the attachment and related records in the database. The
     * records are validated before add/update and the attachment record must
     * have a lock record if it has an id.
     */
    public AttachmentManager update(AttachmentManager am) throws Exception {
        ArrayList<AttachmentManager> ams;

        ams = new ArrayList<AttachmentManager>(1);
        ams.add(am);
        update(ams);
        return ams.get(0);
    }

    /**
     * Adds/updates all the attachments and related records in the database. All
     * the records are validated before add/update and the attachment records
     * must each have a lock record if they have an id.
     */
    public ArrayList<AttachmentManager> update(ArrayList<AttachmentManager> ams) throws Exception {
        ValidationErrorsList e;
        HashSet<Integer> ids;
        ArrayList<Integer> locks;

        e = new ValidationErrorsList();
        /*
         * validate each sample and re-evaluate its status
         */
        for (AttachmentManager am : ams) {
            try {
                validate(am);
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
        ids = new HashSet<Integer>();
        for (AttachmentManager am : ams) {
            if (getAttachment(am).getId() != null)
                ids.add(getAttachment(am).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().ATTACHMENT, locks);
        } else {
            locks = null;
        }
        ids = null;

        for (AttachmentManager am : ams)
            attachment.update(getAttachment(am));

        if (locks != null)
            lock.unlock(Constants.table().ATTACHMENT, locks);

        return ams;
    }

    /**
     * Creates attachment records using the passed paths where the file name is
     * set as the description. Returns the managers for the attachment records
     * created.
     */
    public ArrayList<AttachmentManager> put(List<String> paths) throws Exception {
        String base;
        AttachmentDO data;
        ArrayList<Integer> ids;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }
        ids = new ArrayList<Integer>();
        /*
         * create attachment records by saving the uploaded files and fetch the
         * managers for those records
         */
        for (String p : paths) {
            data = put(base, p, true, null, null, null);
            ids.add(data.getId());
        }

        return fetchByIds(ids);
    }

    /**
     * Creates an attachment record using the filename and description. The file
     * specified by file is renamed and moved to the attachment directory. The
     * filename is stored in storage reference field.
     */
    public AttachmentDO put(String file, boolean getNameFromFile, String filename,
                            String description, Integer sectionId) throws Exception {
        String base;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }

        return put(base, file, getNameFromFile, filename, description, sectionId);
    }

    /**
     * Creates an attachment record using the filename and description. The file
     * specified by file is renamed and moved to the attachment directory, which
     * is specified by base. The filename is stored in storage reference field.
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
            log.severe("Can't move file '" + src.toString() + "' to '" + dst.toString());
            anyE.printStackTrace();
            throw new DatabaseException(Messages.get().attachment_moveFileException(dst.toString()));
        }

        return data;
    }

    /**
     * Returns the ReportStatus containing the path and name of the file that is
     * linked to the attachment with the passed id
     */
    public ReportStatus get(Integer id) throws Exception {
        String base;

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPathException());
        }

        return get(base, id);
    }

    /**
     * Returns the ReportStatus containing the path and name of the file that is
     * linked to the attachment with the passed id. The attachment directory is
     * specified by base.
     */
    public ReportStatus get(String base, Integer id) throws Exception {
        Integer index;
        String ref, prefix, suffix;
        AttachmentDO data;
        Path src, dst;
        ReportStatus status;
        SystemUserPermission perm;
        SectionPermission sp;
        SectionViewDO sect;
        OutputStream out;

        data = attachment.fetchById(id);

        /*
         * check whether the user has permission to view this attachment and
         * throw an exception if this isn't the case
         */
        if (data.getSectionId() != null) {
            sect = sectionCache.getById(data.getSectionId());
            perm = userCache.getPermission();
            sp = perm.getSection(sect.getName());

            if (sp == null || !sp.hasViewPermission())
                throw new InconsistencyException(Messages.get()
                                                         .attachment_viewPermException(data.getStorageReference()));
        }

        src = Paths.get(base, ReportUtil.getAttachmentSubdirectory(id), id.toString());
        ref = data.getStorageReference();
        index = ref.lastIndexOf(".");
        /*
         * Get the file name and extension from the storage reference if there
         * is a "." in the reference and there's something to the left and right
         * of it; otherwise, use the whole reference as the prefix and let the
         * suffix be empty. Last index is used instead of splitting on the "."
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
     * Unlocks and returns an attachment manager with specified attachment id
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
     * Unlocks and returns list of attachment managers with specified attachment
     * ids
     */
    public ArrayList<AttachmentManager> unlock(ArrayList<Integer> attachmentIds) throws Exception {
        lock.unlock(Constants.table().ATTACHMENT, attachmentIds);
        return fetchByIds(attachmentIds);
    }

    private void validate(AttachmentManager am) throws Exception {
        // TODO Auto-generated method stub
    }
}