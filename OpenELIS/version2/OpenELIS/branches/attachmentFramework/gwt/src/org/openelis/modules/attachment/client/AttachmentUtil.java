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
package org.openelis.modules.attachment.client;

import org.openelis.meta.AttachmentMeta;
import org.openelis.modules.report.attachment.client.AttachmentReportScreen;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.WindowInt;

public class AttachmentUtil {

    private static AttachmentReportScreen attachmentReportScreen;
    /**
     * Shows the file linked to the attachment with the passed id, in the
     * browser
     */
    public static void displayAttachment(Integer id, String name, WindowInt window) throws Exception {
        Query query;
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.setKey(AttachmentMeta.getId());
        field.setQuery(id.toString());
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);
        
        if (attachmentReportScreen == null)
            attachmentReportScreen = new AttachmentReportScreen(window);
        
        attachmentReportScreen.setName(name);
        attachmentReportScreen.runReport(query);
    }
}