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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.ui.common.NotFoundException;

public class SampleOrganizationUtility {

    public static boolean isHoldRefuseSampleForOrg(Integer orgId) throws Exception {
        ArrayList<OrganizationParameterDO> parameters;

        if (orgId == null)
            return false;

        try {
            parameters = OrganizationService1Impl.INSTANCE.fetchParametersByOrganizationId(orgId);
            for (OrganizationParameterDO data : parameters) {
                if (Constants.dictionary().ORG_HOLD_SAMPLE.equals(data.getTypeId()))
                    return true;
            }
        } catch (NotFoundException e) {
            // ignore
        }

        return false;
    }
}
