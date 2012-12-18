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
package org.openelis.web.modules.notificationPreference.client;

import org.openelis.gwt.common.DataBaseUtil;

public class AddEditEmailVO {        
    private Integer organizationId;
    private String  email, forReceived, forReleased;
    
    public AddEditEmailVO() {
        forReceived = "Y";
        forReleased = "Y";            
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = DataBaseUtil.trim(email);
    }

    public String getForReceived() {
        return forReceived;
    }             
            
    public void setForReceived(String forReceived) {
        this.forReceived = DataBaseUtil.trim(forReceived);
    }

    public String getForReleased() {
        return forReleased;
    }

    public void setForReleased(String forReleased) {
        this.forReleased = DataBaseUtil.trim(forReleased);
    }
}
