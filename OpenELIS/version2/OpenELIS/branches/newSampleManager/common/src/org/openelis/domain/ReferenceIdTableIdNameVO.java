/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.gwt.common.DataBaseUtil;

/**
 *  The class is used to return the data to be shown as the result of querying
 *  the records in the table analyte_parameter. The fields are considered read/display
 *  and do not get committed to the database.
 */
public class ReferenceIdTableIdNameVO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer         referenceId, referenceTableId;

    protected String          referenceName, referenceDescription;
    
    public ReferenceIdTableIdNameVO() {
    }
    
    public ReferenceIdTableIdNameVO(Integer referenceId, Integer referenceTableId,
                                    String referenceName, String referenceDescription) {
        setReferenceId(referenceId);
        setReferenceTableId(referenceTableId);
        setReferenceName(referenceName);     
        setReferenceDescription(referenceDescription);
    }
    
    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = DataBaseUtil.trim(referenceName);
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = DataBaseUtil.trim(referenceDescription);
    }

}
