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
package org.openelis.scriptlet.nbs;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.ui.common.data.QueryData;

/**
 * This interface is implemented by the proxies for the newborn screening
 * scriptlets
 */
public interface ScriptletProxy {
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                  int max, SampleManager1.Load... elements) throws Exception;

    public ArrayList<TestManager> fetchTestManagersByIds(ArrayList<Integer> ids) throws Exception;
    
    public DictionaryDO getDictionaryById(Integer id) throws Exception;

    public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;
    
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception;
    
    public ArrayList<QaEventDO> fetchByNames(ArrayList<String> names) throws Exception;

    public void log(Level level, String message, Exception e);
}
