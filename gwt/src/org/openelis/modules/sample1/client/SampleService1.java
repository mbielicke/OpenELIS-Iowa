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

package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class SampleService1 implements SampleServiceInt1, SampleServiceInt1Async {

    /**
     * GWT.created service to make calls to the server
     */
    private SampleServiceInt1Async service;

    private static SampleService1  instance;

    public static SampleService1 get() {
        if(instance == null)
            instance = new SampleService1();
        
        return instance;
    }
    
    private SampleService1() {
        service = (SampleServiceInt1Async)GWT.create(SampleServiceInt1.class);
    }
    
    @Override
    public SampleManager1 getInstance(String domain) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.getInstance(domain, callback);
        return callback.getResult();
    }
    
    @Override
    public void getInstance(String domain, AsyncCallback<SampleManager1> callback) throws Exception {
        service.getInstance(domain, callback);
    }
    
    @Override
    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                      SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByIds(sampleIds, elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByIds(ArrayList<Integer> sampleIds,
                                 SampleManager1.Load elements[],
                                 AsyncCallback<ArrayList<SampleManager1>> callback)  throws Exception {
        service.fetchByIds(sampleIds, elements, callback);
    }
    
    @Override
    public ArrayList<SampleManager1> fetchByQuery(Query query, SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByQuery(query, elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByQuery(Query query, SampleManager1.Load elements[], AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception { 
        service.fetchByQuery(query, elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchByAnalyses(analysisIds,elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByAnalyses(ArrayList<Integer> analysisIds, Load elements[],
                                AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchByAnalyses(analysisIds,elements, callback);
    }

    @Override
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds, SampleManager1.Load ...elements) throws Exception {
        Callback<ArrayList<SampleManager1>> callback;

        callback = new Callback<ArrayList<SampleManager1>>();
        service.fetchForUpdate(sampleIds,elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchForUpdate(ArrayList<Integer> sampleIds, SampleManager1.Load elements[], 
                               AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception {
        service.fetchForUpdate(sampleIds,elements, callback);
    }
    
    @Override
    public SampleManager1 add(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.add(sm, ignoreWarnings, callback);
        return callback.getResult();
    }
    
    @Override
    public void add(SampleManager1 sm, boolean ignoreWarnings, AsyncCallback<SampleManager1> callback) throws Exception {
        service.add(sm, ignoreWarnings, callback);
    }
    
    @Override
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        Callback<SampleManager1> callback;

        callback = new Callback<SampleManager1>();
        service.update(sm, ignoreWarnings, callback);
        return callback.getResult();
    }
    
    @Override
    public void update(SampleManager1 sm, boolean ignoreWarnings, AsyncCallback<SampleManager1> callback) throws Exception {
        service.update(sm, ignoreWarnings, callback);
    }
}