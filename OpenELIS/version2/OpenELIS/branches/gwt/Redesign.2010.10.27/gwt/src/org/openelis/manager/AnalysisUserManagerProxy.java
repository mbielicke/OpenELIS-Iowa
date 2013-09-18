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
package org.openelis.manager;

import org.openelis.cache.DictionaryCache;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class AnalysisUserManagerProxy {
    protected static final String ANALYSIS_USER_SERVICE_URL = "org.openelis.modules.analysisUser.server.AnalysisUserService";
    protected ScreenService       service;

    public AnalysisUserManagerProxy() {
        service = new ScreenService("controller?service=" + ANALYSIS_USER_SERVICE_URL);
    }

    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return service.call("fetchByAnalysisId", analysisId);
    }

    public AnalysisUserManager add(AnalysisUserManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public AnalysisUserManager update(AnalysisUserManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(AnalysisUserManager man, ValidationErrorsList errorsList) throws Exception {

    }

    protected void loadDictionaryEntries(AnalysisUserManager m) throws Exception {
        if (m.actionCompletedId == null) {
            m.actionCompletedId = DictionaryCache.getIdFromSystemName("an_user_ac_completed");
            m.actionReleasedId = DictionaryCache.getIdFromSystemName("an_user_ac_released");
        }
    }

    protected SystemUserVO getSystemUser() {
        return OpenELIS.getSystemUserPermission().getUser();
    }

}