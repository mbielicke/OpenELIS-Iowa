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
package org.openelis.meta;

/**
 * Attachment META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class AttachmentMeta implements Meta, MetaMap {
    private static final String    ID = "_attachment.id", CREATED_DATE = "_attachment.createdDate",
                    TYPE_ID = "_attachment.typeId", SECTION_ID = "_attachment.sectionId",
                    DESCRIPTION = "_attachment.description",
                    STORAGE_REFERENCE = "_attachment.storageReference",
                    ITEM_ID = "_attachmentItem.id",
                    ITEM_REFERENCE_ID = "_attachmentItem.referenceId",
                    ITEM_REFERENCE_TABLE_ID = "_attachmentItem.referenceTableId",
                    ITEM_ATTACHMENT_ID = "_attachmentItem.attachmentId";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID,
                                                  CREATED_DATE,
                                                  TYPE_ID,
                                                  SECTION_ID,
                                                  DESCRIPTION,
                                                  STORAGE_REFERENCE,
                                                  ITEM_ID,
                                                  ITEM_REFERENCE_ID,
                                                  ITEM_REFERENCE_TABLE_ID,
                                                  ITEM_ATTACHMENT_ID));
    }

    public static String getId() {
        return ID;
    }

    public static String getCreatedDate() {
        return CREATED_DATE;
    }

    public static String getTypeId() {
        return TYPE_ID;
    }

    public static String getSectionId() {
        return SECTION_ID;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getStorageReference() {
        return STORAGE_REFERENCE;
    }

    public static String getItemId() {
        return ITEM_ID;
    }

    public static String getItemReferenceId() {
        return ITEM_REFERENCE_ID;
    }

    public static String getItemReferenceTableId() {
        return ITEM_REFERENCE_TABLE_ID;
    }

    public static String getItemAttachmentId() {
        return ITEM_ATTACHMENT_ID;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    @Override
    public String buildFrom(String where) {
        String from;
        
        from = "Attachment _attachment ";
        if (where.indexOf("attachmentItem.") > -1)
            from += ",IN (_attachment.attachmentItem) _attachmentItem ";

        return from;
    }
}