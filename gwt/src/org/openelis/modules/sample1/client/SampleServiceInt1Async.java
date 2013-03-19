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

import org.openelis.ui.common.data.Query;
import org.openelis.manager.SampleManager1;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ScreenServiceIntAsync is the Asynchronous version of the ScreenServiceInt
 * interface.
 */
public interface SampleServiceInt1Async {

    public void fetchByIds(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                           AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception;

    public void fetchForUpdate(ArrayList<Integer> sampleIds, SampleManager1.Load elements[],
                               AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception;

    public void fetchByAnalyses(ArrayList<Integer> analysisIds, SampleManager1.Load elements[],
                                AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception;

    public void getInstance(String domain, AsyncCallback<SampleManager1> callback) throws Exception;

    public void add(SampleManager1 sm, boolean ignoreWarnings,
                    AsyncCallback<SampleManager1> callback) throws Exception;

    public void update(SampleManager1 sm, boolean ignoreWarnings,
                       AsyncCallback<SampleManager1> callback) throws Exception;
    
    public void fetchByQuery(Query query, SampleManager1.Load elements[],
                              AsyncCallback<ArrayList<SampleManager1>> callback) throws Exception;
}
