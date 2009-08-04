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

import org.openelis.gwt.common.RPC;

public class SecuritySystemUserDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          loginName, lastName, firstName, initials;
    protected boolean         isEmployee, isActive;

    public SecuritySystemUserDO() {

    }

    public SecuritySystemUserDO(Integer id,
                                String loginName,
                                String lastName,
                                String firstName,
                                String initials,
                                String isEmployee,
                                String isActive) {
        this.id = id;
        this.loginName = loginName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.initials = initials;
        if ("Y".equals(isEmployee))
            this.isEmployee = true;
        if ("Y".equals(isActive))
            this.isActive = true;
    }

    public SecuritySystemUserDO(Integer id,
                                String loginName,
                                String lastName,
                                String firstName,
                                String initials,
                                boolean isEmployee,
                                boolean isActive) {
        this.id = id;
        this.loginName = loginName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.initials = initials;
        if ("Y".equals(isEmployee))
            this.isEmployee = true;
        if ("Y".equals(isActive))
            this.isActive = true;
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getId() {
        return id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsEmployee() {
        return isEmployee;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getInitials() {
        return initials;
    }

}
