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
 * The class extends exchange external term DO and carries the fields reference
 * name and one or more reference description(s). The extra description fields
 * are used for row and column analyte names if the reference table is
 * "test_analyte". The additional fields are for read/display only and do not
 * get committed to the database. Note: isChanged will reflect any changes to
 * read/display fields.
 */

public class ExchangeLocalTermViewDO extends ExchangeLocalTermDO {

    private static final long serialVersionUID = 1L;

    protected String          referenceName, referenceDescription1, referenceDescription2,
                    referenceDescription3;

    public ExchangeLocalTermViewDO() {
    }

    public ExchangeLocalTermViewDO(Integer id, Integer referenceTableId, Integer referenceId,
                                   String referenceName, String referenceDescription1,
                                   String referenceDescription2, String referenceDescription3) {
        super(id, referenceTableId, referenceId);
        setReferenceName(referenceName);
        setReferenceDescription1(referenceDescription1);
        setReferenceDescription2(referenceDescription2);
        setReferenceDescription3(referenceDescription3);
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = DataBaseUtil.trim(referenceName);
    }

    public String getReferenceDescription1() {
        return referenceDescription1;
    }

    public void setReferenceDescription1(String referenceDescription1) {
        this.referenceDescription1 = DataBaseUtil.trim(referenceDescription1);
    }
    
    public String getReferenceDescription2() {
        return referenceDescription2;
    }

    public void setReferenceDescription2(String referenceDescription2) {
        this.referenceDescription2 = DataBaseUtil.trim(referenceDescription2);
    }
    
    public String getReferenceDescription3() {
        return referenceDescription3;
    }

    public void setReferenceDescription3(String referenceDescription3) {
        this.referenceDescription3 = DataBaseUtil.trim(referenceDescription3);
    }
}