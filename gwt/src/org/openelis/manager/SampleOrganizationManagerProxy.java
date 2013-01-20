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

import org.openelis.domain.Constants;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.SampleService;

public class SampleOrganizationManagerProxy {

    public SampleOrganizationManagerProxy() {
    }

    public SampleOrganizationManager fetchBySampleId(Integer sampleId) throws Exception {
        return SampleService.get().fetchSampleOrganizationsBySampleId(sampleId);
    }

    public SampleOrganizationManager add(SampleOrganizationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public SampleOrganizationManager update(SampleOrganizationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(SampleOrganizationManager man, boolean validateReportTo,
                         ValidationErrorsList errorsList) throws Exception {
        int numBillTo, numReportTo;

        numBillTo = 0;
        numReportTo = 0;
        for (int i = 0; i < man.count(); i++ ) {
            SampleOrganizationDO orgDO = man.getOrganizationAt(i);
            if (Constants.dictionary().ORG_BILL_TO.equals(orgDO.getTypeId()))
                numBillTo++ ;

            if (Constants.dictionary().ORG_REPORT_TO.equals(orgDO.getTypeId()))
                numReportTo++ ;
        }

        if (numBillTo > 1)
            errorsList.add(new FormErrorException("multipleBillToException"));

        if (numReportTo > 1)
            errorsList.add(new FormErrorException("multipleReportToException"));

        if (validateReportTo && numReportTo == 0)
            errorsList.add(new FieldErrorWarning("reportToMissingWarning",
                                                 SampleMeta.getOrgName()));
    }
}
