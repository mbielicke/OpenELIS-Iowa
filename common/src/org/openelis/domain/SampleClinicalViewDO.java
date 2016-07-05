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
package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;

/**
 * The class extends sample_clinical DO and carries several commonly used fields
 * such as paperOrderValidator. The additional fields are for read/display only
 * and do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class SampleClinicalViewDO extends SampleClinicalDO {

    private static final long serialVersionUID = 1L;

    protected String          paperOrderValidator;

    public SampleClinicalViewDO() {
    }

    public SampleClinicalViewDO(Integer id, Integer sampleId, Integer patientId,
                                Integer providerId, String providerPhone) {
        super(id, sampleId, patientId, providerId, providerPhone);
    }

    public String getPaperOrderValidator() {
        return paperOrderValidator;
    }

    public void setPaperOrderValidator(String paperOrderValidator) {
        this.paperOrderValidator = DataBaseUtil.trim(paperOrderValidator);
    }
}