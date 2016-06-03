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

import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.ui.common.ModulePermission;

/**
 * This class is used in the front-end to determine if the logged in user has
 * permission to view a sample based on whether it contains patient data
 */
public class PatientPermission {
    protected ModulePermission permission;

    public PatientPermission() {
        permission = UserCache.getPermission().getModule("patient");
        if (permission == null)
            permission = new ModulePermission();
    }

    /**
     * Returns true if either the sample doesn't contain patient data or the
     * user has permission to view patients; returns false otherwise
     */
    public boolean canViewSample(SampleDO data) {
        return canViewSample(data.getDomain());
    }

    /**
     * Returns true if either the sample doesn't contain patient data or the
     * user has permission to view patients; returns false otherwise
     */
    public boolean canViewSample(SecondDataEntryVO data) {
        return canViewSample(data.getSampleDomain());
    }

    /**
     * Returns true if either the sample doesn't contain patient data or the
     * user has permission to view patients; returns false otherwise
     */
    public boolean canViewSample(ToDoSampleViewVO data) {
        return canViewSample(data.getDomain());
    }

    /**
     * Returns true if either the analysis' sample doesn't contain patient data
     * or the user has permission to view patients; returns false otherwise
     */
    public boolean canViewSample(AnalysisViewVO data) {
        return canViewSample(data.getDomain());
    }

    private boolean canViewSample(String domain) {
        if (Constants.domain().CLINICAL.equals(domain) ||
            Constants.domain().NEONATAL.equals(domain))
            return permission.hasSelectPermission();
        else
            return true;
    }
}