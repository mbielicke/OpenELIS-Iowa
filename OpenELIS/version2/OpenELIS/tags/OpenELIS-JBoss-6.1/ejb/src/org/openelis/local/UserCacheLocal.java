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
package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.SectionPermission.SectionFlags;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;

@Local
public interface UserCacheLocal {

    public Integer getId();
    
    public String getName();
    
    public String getSessionId();
    
    public String getLocale();
    
    public SystemUserVO getSystemUser();
    
    public SystemUserVO getSystemUser(Integer id);

    public SystemUserVO getSystemUser(String name);
    
    public ArrayList<SystemUserVO> getSystemUsers(String name, int max);
    
    public ArrayList<SystemUserVO> getEmployees(String name, int max);

    public void applyPermission(String module, ModuleFlags flag) throws Exception;

    public void applyPermission(String section, SectionFlags flag) throws Exception;
    
    public SystemUserPermission getPermission();
}
