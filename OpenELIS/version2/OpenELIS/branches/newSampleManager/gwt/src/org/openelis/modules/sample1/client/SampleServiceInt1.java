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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.manager.SampleManager1;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen
 * Widget.  GWT RemoteServiceServlets that want to provide server
 * side logic for Screens must implement this interface.
 */
@RemoteServiceRelativePath("sample1")
public interface SampleServiceInt1 extends RemoteService {
    public SampleManager1 getInstance(String domain) throws Exception;
    
    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load... elements) throws Exception;
    
    public ArrayList<SampleManager1> fetchByQuery(Query query, 
                                                  SampleManager1.Load... elements) throws Exception;

    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                               SampleManager1.Load... elements) throws Exception;

    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds,
                                              SampleManager1.Load... elements) throws Exception;
    
    public SampleManager1 add(SampleManager1 sm, boolean ignoreWarnings) throws Exception;
    
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception;
}
