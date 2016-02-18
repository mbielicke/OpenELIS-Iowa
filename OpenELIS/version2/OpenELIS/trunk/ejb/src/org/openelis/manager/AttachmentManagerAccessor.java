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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.domain.AttachmentItemViewDO;

/**
 * This class is used to bulk load attachment manager.
 */
public class AttachmentManagerAccessor {
    /**
     * Set/get objects from attachment manager
     */
    public static AttachmentDO getAttachment(AttachmentManager am) {
        return am.attachment;
    }
    
    public static void setAttachment(AttachmentManager am, AttachmentDO attachment) {
        am.attachment = attachment;
    }
    
    public static ArrayList<AttachmentItemViewDO> getItems(AttachmentManager am) {
        return am.items;
    }

    public static void setItems(AttachmentManager am, ArrayList<AttachmentItemViewDO> items) {
        am.items = items;
    }
    
    public static void addItem(AttachmentManager am, AttachmentItemViewDO item) {
        if (am.items == null)
            am.items = new ArrayList<AttachmentItemViewDO>();
        am.items.add(item);
    }
    
    public static AttachmentIssueViewDO getIssue(AttachmentManager am) {
        return am.issue;
    }
    
    public static void setIssue(AttachmentManager am, AttachmentIssueViewDO issue) {
        am.issue = issue;
    }
}