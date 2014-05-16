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
package org.openelis.modules.sampleQC.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class SampleQCService implements SampleQCServiceInt, SampleQCServiceIntAsync {
    /**
     * GWT.created service to make calls to the server
     */
    private SampleQCServiceIntAsync service;

    private static SampleQCService  instance;

    public static SampleQCService get() {
        if (instance == null)
            instance = new SampleQCService();

        return instance;
    }

    private SampleQCService() {
        service = (SampleQCServiceIntAsync)GWT.create(SampleQCServiceInt.class);
    }

    @Override
    public ArrayList<Object> fetchByAccessionNumber(Integer accession) throws Exception {
        Callback<ArrayList<Object>> callback;

        callback = new Callback<ArrayList<Object>>();
        service.fetchByAccessionNumber(accession, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByAccessionNumber(Integer accession, AsyncCallback<ArrayList<Object>> callback) {
        service.fetchByAccessionNumber(accession, callback);
    }
}