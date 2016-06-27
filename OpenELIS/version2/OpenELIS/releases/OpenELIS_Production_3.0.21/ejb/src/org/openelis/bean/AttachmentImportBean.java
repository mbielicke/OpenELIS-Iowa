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

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AttachmentDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class AttachmentImportBean {

    @EJB
    private AttachmentManagerBean attachmentManager;

    @EJB
    private SectionCacheBean      section;

    @EJB
    private AttachmentBean        attachment;

    @EJB
    private SystemVariableBean    systemVariable;

    private static final Logger   log = Logger.getLogger("openelis");

    /**
     * Creates attachments from the files in the directory specified by the
     * passed system variable and sets their section as the passed value. Moves
     * the files to the attachment directory.
     */
    public void importAttachments(String systemVariableDirectory, String sectionName) throws Exception {
        String src, dst;
        Integer sectionId;
        DirectoryStream<Path> dirStream;

        /*
         * get the import directory
         */
        try {
            src = ReportUtil.getSystemVariableValue(systemVariableDirectory);
        } catch (Exception e) {
            log.severe("No '" + systemVariableDirectory + "' system variable defined");
            return;
        }

        try {
            dst = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            return;
        }

        dirStream = null;
        try {
            /*
             * get the id of the passed section; use the section "internal" if
             * the name is not specified
             */
            if (DataBaseUtil.isEmpty(sectionName)) {
                try {
                    sectionName = ReportUtil.getSystemVariableValue("internal_section");
                } catch (Exception e) {
                    log.severe("No 'internal_section' system variable defined");
                    return;
                }
            }
            sectionId = section.getByName(sectionName).getId();
            /*
             * go through the files in the directory and create attachments from
             * them
             */
            dirStream = Files.newDirectoryStream(Paths.get(src));
            for (Path path : dirStream) {
                log.fine("Importing " + path.toString() + " attachment");
                attachmentManager.put(dst, path.toString(), true, null, null, sectionId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (dirStream != null)
                    dirStream.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Removes unattached attachments from the database that were created more
     * than the passed number of days ago and have no attachment issues
     */
    public void removeImportedAttachments(String days) throws Exception {
        String base;
        Calendar cal;
        List<AttachmentDO> attachments;

        /*
         * get the date older than the current time by the passed number of days
         */
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -Integer.valueOf(days));

        try {
            base = systemVariable.fetchByName("attachment_directory").getValue();
        } catch (Exception e) {
            log.severe("No 'attachment_directory' system variable defined");
            return;
        }

        try {
            /*
             * fetch attachments that are unattached, have no attachment issues
             * and are older than the date obtained above; delete those
             * attachments and the files linked to them
             */
            attachments = attachment.fetchForRemove(cal.getTime());
            for (AttachmentDO data : attachments) {
                log.fine("Deleting attachment id: " + data.getId());
                attachmentManager.delete(base, data);
            }
        } catch (NotFoundException e) {
            log.fine("No unattached attachments older than " + days + " found");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}