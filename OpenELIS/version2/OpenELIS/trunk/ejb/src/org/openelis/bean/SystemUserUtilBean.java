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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserUtilRemote;

@Stateless
@SecurityDomain("openelis")
public class SystemUserUtilBean implements org.openelis.remote.SystemUserUtilRemote {

    @EJB (mappedName="security/SystemUserUtilBean") private SystemUserUtilRemote sysUser;
    
    
    public ArrayList<SecuritySystemUserDO> fetchByLogin(String loginName, int numResult) {
        SecuritySystemUserDO secUserDO;
        SystemUserDO userDO;
        List<SystemUserDO> userDOList;
        ArrayList<SecuritySystemUserDO> secUserDOList;
                        
        userDOList = sysUser.systemUserAutocompleteByLoginName(loginName,numResult);
        secUserDOList = new ArrayList<SecuritySystemUserDO>();
        for(int i=0; i < userDOList.size(); i++) {
            userDO = userDOList.get(i);
            secUserDO = new SecuritySystemUserDO(userDO.getId(),userDO.getLoginName(),
                                                 userDO.getLastName(),userDO.getFirstName(),
                                                 userDO.getInitials(),userDO.getIsEmployee(),
                                                 userDO.getIsActive());
            secUserDOList.add(secUserDO);
        } 
        return secUserDOList;
    }

}
