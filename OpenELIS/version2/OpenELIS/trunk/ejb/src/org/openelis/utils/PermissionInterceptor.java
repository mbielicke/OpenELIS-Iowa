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
package org.openelis.utils;

import javax.naming.InitialContext;

import org.openelis.bean.ApplicationCacheInt;
import org.openelis.bean.SessionCacheInt;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.SectionPermission.SectionFlags;
import org.openelis.local.SystemUserPermissionProxyLocal;

/**
 * The class provides an interface to permission information for logged-in and system
 * users. Beans and Managers should call this class to lookup permission information and
 * enforce authorization.
 */
public class PermissionInterceptor {

    private static ApplicationCacheInt application;
    private static SessionCacheInt session;

    static {
        InitialContext ctx;

        try {
            ctx = new InitialContext();
            application = (ApplicationCacheInt)ctx.lookup("openelis/ApplicationCache/local");
            session = (SessionCacheInt)ctx.lookup("openelis/SessionCache/local");
        } catch (Exception e) {
            application = null;
            session = null;
            e.printStackTrace();
        }
    }
    
    /**
     * Throws permission exception if the user does not have permission to the module for
     * the specified operation.
     */
    public static void applyPermission(String module, ModuleFlags flag) throws Exception {
        if ( !getSystemUserPermission().has(module, flag))
            throw new PermissionException("modulePermException", flag.name(), module);
    }

    /**
     * Throws permission exception if the user does not have permission to the section for
     * the specified operation.
     */
    public static void applyPermission(String section, SectionFlags flag) throws Exception {
        if ( !getSystemUserPermission().has(section, flag))
            throw new PermissionException("sectionPermException", flag.name(), section);
    }
    
    /**
     * Returns the system user id associated with this context.
     */
    public static Integer getSystemUserId() throws Exception {
        return getSystemUserPermission().getSystemUserId();
    }
    
    /**
     * Returns the system user data associated with this context.
     */
    public static SystemUserVO getSystemUser() throws Exception {
        return getSystemUserPermission().getUser();
    }
    
    /**
     * Returns the system user data for the specified id.
     */
    public static SystemUserVO getSystemUser(Integer id) throws Exception {
        SystemUserVO user;
        
        user = (SystemUserVO) application.getAttribute("UserList", id);
        if (user == null)
            user = local().fetchById(id);
        return user;
    }
    
    public static SystemUserPermission getSystemUserPermission() {
        return (SystemUserPermission)session.getAttribute("UserPermssion");
    }
    
    private static SystemUserPermissionProxyLocal local() {
        InitialContext ctx;

        try {
            ctx = new InitialContext();
            return (SystemUserPermissionProxyLocal)ctx.lookup("openelis/SystemUserPermissionProxyBean/local");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
