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
package org.openelis.modules.report.secondaryLabel.client;

import java.util.ArrayList;

import org.openelis.constants.Messages;
import org.openelis.domain.SecondaryLabelVO;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.modules.report.client.ReportScreen;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is used to execute reports on behalf of those screens that don't
 * implement ReportScreen like Secondary Label
 */
public class SecondaryLabelReportScreen extends ReportScreen<ArrayList<SecondaryLabelVO>> {

    public SecondaryLabelReportScreen(WindowInt window) throws Exception {
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

    @Override
    public void runReport(ArrayList<SecondaryLabelVO> labels, AsyncCallback<ReportStatus> callback) {
        SecondaryLabelReportService.get().runReport(labels, callback);
    }

    public void runReport(ArrayList<SecondaryLabelVO> labels) {
        window.setBusy(Messages.get().genReportMessage());

        runReport(labels, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                if (status.getStatus() == ReportStatus.Status.SAVED) {
                    url = "/openelis/openelis/report?file=" + status.getMessage();
                    if (attachmentName != null)
                        url += "&attachment=" + attachmentName;

                    Window.open(URL.encode(url), name, null);
                    window.setDone("Generated file " + status.getMessage());
                } else {
                    window.setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                ValidationErrorsList e;
                StringBuilder sb;

                if (caught instanceof ValidationErrorsList) {
                    e = (ValidationErrorsList)caught;
                    sb = new StringBuilder();

                    /*
                     * the method for printing labels in the back-end throws a
                     * ValidationErrorsList if some labels have errors while
                     * others can be printed; showErrors() is not used here
                     * because the errors are shown in an alert which can't be
                     * done with that method
                     */
                    sb.append(Messages.get().secondaryLabel_errorsPrintingLabels()).append(":\n");

                    for (Exception ex : e.getErrorList())
                        sb.append(" * ").append(ex.getMessage()).append("\n");
                    Window.alert(sb.toString());
                    window.setError(Messages.get().secondaryLabel_errorsPrintingLabels());
                } else {
                    window.setError("Failed");
                    Window.alert(caught.getMessage());
                }
            }
        });
    }
}