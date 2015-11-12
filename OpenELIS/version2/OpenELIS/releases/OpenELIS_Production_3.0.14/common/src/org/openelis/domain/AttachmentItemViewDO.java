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
package org.openelis.domain;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * The class extends attachment item DO and carries several commonly used fields
 * such as attachment description, section id and created date. The additional
 * fields are for read/display only and do not get committed to the database.
 * Note: isChanged will reflect any changes to read/display fields.
 */
public class AttachmentItemViewDO extends AttachmentItemDO {

    private static final long serialVersionUID = 1L;
    
    protected Datetime        attachmentCreatedDate;

    protected Integer         attachmentSectionId;

    public String             attachmentDescription, referenceDescription;

    public AttachmentItemViewDO() {
    }

    public AttachmentItemViewDO(Integer id, Integer referenceId, Integer referenceTableId,
                                Integer attachmentId, Date attachmentCreatedDate, Integer attachmentSectionId,
                                String attachmentDescription, String referenceDescription) {
        super(id, referenceId, referenceTableId, attachmentId);
        setAttachmentCreatedDate(DataBaseUtil.toYM(attachmentCreatedDate));
        setAttachmentSectionId(attachmentSectionId);
        setAttachmentDescription(attachmentDescription);
        setReferenceDescription(referenceDescription);
    }
    
    public Datetime getAttachmentCreatedDate() {
        return attachmentCreatedDate;
    }

    public void setAttachmentCreatedDate(Datetime attachmentCreatedDate) {
        this.attachmentCreatedDate = DataBaseUtil.toYM(attachmentCreatedDate);
    }

    public Integer getAttachmentSectionId() {
        return attachmentSectionId;
    }

    public void setAttachmentSectionId(Integer sectionId) {
        this.attachmentSectionId = sectionId;
    }

    public String getAttachmentDescription() {
        return attachmentDescription;
    }

    public void setAttachmentDescription(String description) {
        this.attachmentDescription = DataBaseUtil.trim(description);
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = DataBaseUtil.trim(referenceDescription);
    }
}