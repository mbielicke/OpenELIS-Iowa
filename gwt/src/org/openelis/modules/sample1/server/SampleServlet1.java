/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.modules.sample1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleManager1Bean;
import org.openelis.ui.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.modules.sample1.client.SampleServiceInt1;

@WebServlet("/openelis/sample1")
public class SampleServlet1 extends RemoteServlet implements SampleServiceInt1 {

    private static final long serialVersionUID = 1L;
    
    @EJB
    private SampleManager1Bean sampleManager1;
    
    public SampleManager1 getInstance(String domain) throws Exception {
        return sampleManager1.getInstance(domain);
    }

    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load ...elements) throws Exception {
        return sampleManager1.fetchByIds(sampleIds, elements);
    }
    
    public ArrayList<SampleManager1> fetchByQuery(Query query, SampleManager1.Load... elements) throws Exception {
        return sampleManager1.fetchByQuery(query, elements);
    }
    
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        return sampleManager1.fetchForUpdate(sampleIds, elements);
    }
    
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        return sampleManager1.fetchByAnalyses(analysisIds, elements);
    }
    
    public SampleManager1 add(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        return sampleManager1.add(sm, ignoreWarnings);
    }
    
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        return sampleManager1.add(sm, ignoreWarnings);
    }
}