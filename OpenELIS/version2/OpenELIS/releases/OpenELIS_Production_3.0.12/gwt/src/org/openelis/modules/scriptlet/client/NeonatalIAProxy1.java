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
package org.openelis.modules.scriptlet.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.data.QueryData;
import org.openelis.scriptlet.NeonatalIAScriptlet1;

/**
 * This class is used for providing the front-end functionality for neonatal
 * iowa scriptlet
 */
public class NeonatalIAProxy1 implements NeonatalIAScriptlet1.Proxy {
    @Override
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception {

        return SampleService1.get().fetchByQuery(fields, first, max, elements);
    }

    @Override
    public ArrayList<TestManager> fetchTestManagersByIds(ArrayList<Integer> ids) throws Exception {
        return TestService.get().fetchByIds(ids);
    }

    @Override
    public DictionaryDO getDictionaryById(Integer id) throws Exception {
        return DictionaryCache.getById(id);
    }

    @Override
    public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception {
        return DictionaryCache.getBySystemName(systemName);
    }

    @Override
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception {
        return SampleService1.get().removeAnalysis(sm, analysisId);
    }

    @Override
    public void log(Level level, String message) {
        logger.log(level, message);
    }
}