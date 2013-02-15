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

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends sample_item DO and carries several commonly used fields such
 * as type, source, and container names. The additional fields are for read/display
 * only and do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class SampleItemViewDO extends SampleItemDO {

    private static final long serialVersionUID = 1L;

    protected String          typeOfSample, sourceOfSample, container;

    public SampleItemViewDO() {
    }

    public SampleItemViewDO(Integer id, Integer sampleId, Integer sampleItemId, Integer itemSequence,
                            Integer typeOfSampleId, Integer sourceOfSampleId, String sourceOther,
                            Integer containerId, String containerReference, Double quantity,
                            Integer unitOfMeasureId, String typeOfSample, String sourceOfSample, String container) {
        super(id, sampleId, sampleItemId, itemSequence, typeOfSampleId, sourceOfSampleId,sourceOther,
              containerId, containerReference, quantity, unitOfMeasureId);

        setTypeOfSample(typeOfSample);
        setSourceOfSample(sourceOfSample);
        setContainer(container);
    }

    public String getTypeOfSample() {
        return typeOfSample;
    }

    public void setTypeOfSample(String typeOfSample) {
        this.typeOfSample = DataBaseUtil.trim(typeOfSample);
    }

    public String getSourceOfSample() {
        return sourceOfSample;
    }

    public void setSourceOfSample(String sourceOfSample) {
        this.sourceOfSample = DataBaseUtil.trim(sourceOfSample);
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = DataBaseUtil.trim(container);
    }
}
