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
package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.meta.AttachmentMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentService;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttachmentReportScreen extends ReportScreen<Query> {

    public AttachmentReportScreen(WindowInt window) throws Exception {
        drawScreen(new ScreenDef());
        this.window = window;
    }

    /**
     * This method is overridden in order to make sure that the super class's
     * method with the same name doesn't get called, because the screens using
     * this class won't get prompts
     */
    protected void getReportParameters() {
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return null;
    }

    /**
     * If the id in the query is the id of an attachment, shows that attachment;
     * otherwise if it's the id of a sample, shows the TRF for that sample
     */
    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        boolean isSample;
        Integer id;

        id = null;
        isSample = false;
        for (QueryData field : query.getFields()) {
            if (AttachmentMeta.getId().equals(field.getKey())) {
                id = Integer.valueOf(field.getQuery());
                break;
            } else if (SampleMeta.getId().equals(field.getKey())) {
                id = Integer.valueOf(field.getQuery());
                isSample = true;
                break;
            }
        }
        if (isSample)
            AttachmentService.get().getTRF(id, callback);
        else
            AttachmentService.get().get(id, callback);
    }
}