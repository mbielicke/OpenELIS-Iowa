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
package org.openelis.cache;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */
public class UserCache {

    protected static SystemUserPermission          perm;
    protected static HashMap<Integer, SystemUserVO> users;
    
    static {
        users = new HashMap<Integer, SystemUserVO>();        
    }
    
    public static Integer getId() throws Exception {
        getPermission();
        return perm.getSystemUserId();
    }
    
    public static String getName() throws Exception {
        getPermission();
        return perm.getLoginName();
    }
    
    public static SystemUserVO getSystemUser(Integer id) throws Exception {
        SystemUserVO data;
        
        data = users.get(id);
        if (data == null) {
            data = UserCacheService.get().getSystemUser(id);
            if (data != null) 
                users.put(id, data);            
        } 
        return data;
    }

    public static ArrayList<SystemUserVO> getSystemUsers(String name) throws Exception {
        return UserCacheService.get().getSystemUsers(name);
    } 
    
    public static ArrayList<SystemUserVO> getEmployees(String name) throws Exception {
        return UserCacheService.get().getEmployees(name);
    }      

    public static SystemUserPermission getPermission() {
        try {
            if (perm == null)
                perm = UserCacheService.get().getPermission();
        } catch (Exception e) {        
            e.printStackTrace();
        } finally {
            if (perm == null)
                perm = new SystemUserPermission();
        }
        return perm;
    }
    
    public static ArrayList<SystemUserVO> validateSystemUsers(ArrayList<String> names) throws Exception {
        return UserCacheService.get().validateSystemUsers(names);
    } 
}