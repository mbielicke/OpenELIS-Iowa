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
package org.openelis.modules.sampleManagerTest.server;

import java.util.ArrayList;

import javax.ejb.EJB;

import org.openelis.bean.SampleManager1Bean;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;

public class SampleManagerTestServlet extends AppServlet {//implements SampleManagerTestServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleManager1Bean sampleManager1;

    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load ...elements) throws Exception {
        return sampleManager1.fetchByIds(sampleIds, elements);
    }
    
    public ArrayList<SampleManager> fetchByIds(ArrayList<Integer> sampleIds) throws Exception {
        return sampleManager1.fetchByIds(sampleIds);               
    }
    
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds, Load... elements) throws Exception {
        return sampleManager1.fetchForUpdate(sampleIds, elements);
    }
    
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        return sampleManager1.fetchByAnalyses(analysisIds, elements);
    }
}
