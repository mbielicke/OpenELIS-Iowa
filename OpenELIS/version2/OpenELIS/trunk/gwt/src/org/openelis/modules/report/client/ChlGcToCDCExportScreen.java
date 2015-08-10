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

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.widget.WindowInt;
import org.openelis.constants.Messages;
import org.openelis.gwt.screen.ScreenDef;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is used to execute reports on behalf of those screens that don't
 * implement ReportScreen like Shipping and Fill Order
 */
public class ChlGcToCDCExportScreen extends ReportScreen<Query> {

    public ChlGcToCDCExportScreen(WindowInt window) throws Exception {
        setWindow(window);
        drawScreen(new ScreenDef());
        setName(Messages.get().chlGcToCDC_chlGcToCDCExport());
    }
    
    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return ChlGcToCDCExportService.get().getPrompts();
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        Timer exportTimer;
        
        ChlGcToCDCExportService.get().runReport(query, callback);
        /*
         * refresh the status of the report every second, until the process successfully
         * completes or is aborted because of an error
         */
        exportTimer = new Timer() {
            public void run() {
                ReportStatus status;
                try {
                    status = ChlGcToCDCExportService.get().getStatus();
                    /*
                     * the status only needs to be refreshed while the status
                     * panel is showing because once the job is finished, the
                     * panel is closed
                     */
                    if (window.asWidget().isAttached() && !ReportStatus.Status.RUNNING.equals(status.getStatus())) {
                        window.setStatus(status.getMessage(), "");
                        this.schedule(1000);
                    }
                } catch (Exception e) {
                    window.setStatus(e.getMessage(), "");
                }
            }
        };
        exportTimer.schedule(1000);
    }
}