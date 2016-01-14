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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;

/**
 * This class encapsulates an attachment and all its items. Although the class
 * provides some basic functions internally, it is designed to interact with EJB
 * methods to provide majority of the operations needed to manage an attachment.
 */
public class AttachmentManager implements Serializable {
    private static final long                 serialVersionUID = 1L;

    protected AttachmentDO                    attachment;
    protected ArrayList<AttachmentItemViewDO> items;

    transient public final AttachmentItem     item             = new AttachmentItem();

    /**
     * Initialize an empty attachment manager
     */
    public AttachmentManager() {
    }

    /**
     * Returns the attachment DO
     */
    public AttachmentDO getAttachment() {
        return attachment;
    }

    /**
     * Class to manage Attachment Item information
     */
    public class AttachmentItem {
        /**
         * Returns the item at specified index.
         */
        public AttachmentItemViewDO get(int i) {
            return items.get(i);
        }

        /**
         * Returns the number of items associated with this attachment-
         */
        public int count() {
            if (items != null)
                return items.size();
            return 0;
        }
    }
}