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

import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

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
import org.openelis.entity.Attachment;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.ReportUtil;

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
    EntityManager               manager;

    @EJB
    SystemVariableBean          systemVariable;

    private static final Logger log = Logger.getLogger("openelis");

    /**
     * Returns the attachment record using its id
     */
    public AttachmentDO fetchById(Integer id) throws Exception {
        Query query;
        AttachmentDO data;

        query = manager.createNamedQuery("Attchment.FetchById");
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

        query = manager.createNamedQuery("Attchment.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
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

    /**
     * Removed the attachment record and data file from the file system.
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

    /**
     * Creates an attachment record using the filename and description. The file
     * specified by filename is renamed and moved to the attachment directory.
     * The filename is stored storage reference field.
     */
    public AttachmentDO add(String filename, String discription, Integer sectionId) throws Exception {
        String base;
        Path src, dst;
        AttachmentDO data;

        src = Paths.get(filename);

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            throw new InconsistencyException(Messages.get().attachment_missingPath());
        }

        /*
         * insert the attachment and move the file to the right location
         */
        data = add(new AttachmentDO(0, null, null, sectionId, discription, src.getFileName().toString()));
        dst = Paths.get(base, ReportUtil.getAttachmentSubdirectory(data.getId()), data.getId()
                                                                                      .toString());
        try {
            Files.move(src, dst);
        } catch (Exception anyE) {
            log.severe("Can't move file '" + src.toString() + "' to '" + dst.toString());
            throw new Exception(Messages.get().attachment_moveFileException(dst.toString()));
        }

        return data;
    }
}