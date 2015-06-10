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

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;

/**
 * Class represents the fields in database table sample_neonatal.
 */

public class SampleNeonatalViewDO extends SampleNeonatalDO {

    private static final long serialVersionUID = 1L;

    protected String          paperOrderValidator;

    public SampleNeonatalViewDO() {
    }

    public SampleNeonatalViewDO(Integer id, Integer sampleId, Integer patientId,
                                Integer birthOrder, Integer gestationalAge, Integer nextOfKinId,
                                Integer nextOfKinRelationId, String isRepeat, String isNicu,
                                Integer feedingId, String weightSign, Integer weight,
                                String isTransfused, Date transfusionDate, String isCollectionValid,
                                Integer collectionAge, Integer providerId, String formNumber) {
        super(id, sampleId, patientId, birthOrder, gestationalAge, nextOfKinId,
              nextOfKinRelationId, isRepeat, isNicu, feedingId, weightSign, weight,
              isTransfused, transfusionDate, isCollectionValid, collectionAge, providerId,
              formNumber);
    }

    public String getPaperOrderValidator() {
        return paperOrderValidator;
    }

    public void setPaperOrderValidator(String paperOrderValidator) {
        this.paperOrderValidator = DataBaseUtil.trim(paperOrderValidator);
    }
}