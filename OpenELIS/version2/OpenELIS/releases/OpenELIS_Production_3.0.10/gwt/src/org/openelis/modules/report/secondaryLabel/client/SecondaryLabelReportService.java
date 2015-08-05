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
package org.openelis.modules.report.secondaryLabel.client;

import java.util.ArrayList;

import org.openelis.domain.SecondaryLabelVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class SecondaryLabelReportService implements SecondaryLabelReportServiceInt,
                                        SecondaryLabelReportServiceIntAsync {

    /**
     * GWT.created service to make calls to the server
     */ 
    private SecondaryLabelReportServiceIntAsync service;
    
    private static SecondaryLabelReportService instance;

    public static SecondaryLabelReportService get() {
        if (instance == null)
            instance = new SecondaryLabelReportService();

        return instance;
    }
    
    private SecondaryLabelReportService() {
        service = (SecondaryLabelReportServiceIntAsync)GWT.create(SecondaryLabelReportServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public ReportStatus runReport(ArrayList<SecondaryLabelVO> labels) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReport(labels, callback);
        return callback.getResult();
    }
    
    @Override
    public void runReport(ArrayList<SecondaryLabelVO> labels, AsyncCallback<ReportStatus> callback) {
        service.runReport(labels, callback);
    }
}
