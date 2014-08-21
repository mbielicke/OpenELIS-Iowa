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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.local.SystemUserPermissionProxyLocal;
import org.openelis.remote.SystemUserPermissionProxyRemote;
import org.openelis.security.remote.SystemUserPermissionRemote;

@Stateless
@SecurityDomain("openelis")
public class SystemUserPermissionProxyBean implements SystemUserPermissionProxyRemote,
                                                      SystemUserPermissionProxyLocal {

    @EJB
    SessionCacheInt session;

    @EJB
    ApplicationCacheInt application;

    @Resource
    private SessionContext ctx;

    public SystemUserPermission login() {
        InitialContext rctx;
        SystemUserPermission perm;
        SystemUserPermissionRemote user;
        
        try {
            rctx = new InitialContext();
            user = (SystemUserPermissionRemote) rctx.lookup("security/SystemUserPermissionBean/remote");
            perm = user.fetchByApplicationAndLoginName("openelis", ctx.getCallerPrincipal().getName());
            session.setAttribute("UserPermission", perm);
            application.setAttribute("UserList", perm.getSystemUserId(), perm.getUser());
        } catch (Exception e) {
            e.printStackTrace();
            perm = null;
        }
        
        return perm;
    }

    public void logout() {
        session.destroySession();
    }
    
    public SystemUserVO fetchById(Integer id) throws Exception {
        InitialContext rctx;
        SystemUserVO data;
        SystemUserPermissionRemote user;

        try {
            rctx = new InitialContext();
            user = (SystemUserPermissionRemote) rctx.lookup("security/SystemUserPermissionBean/remote");
            data = user.fetchById(id);
        } catch (Exception e) {
            e.printStackTrace();
            data = null;
        }
        
        return data;
    }
    
    public ArrayList<SystemUserVO> fetchByLoginName(String loginName, int max) throws Exception {
        InitialContext rctx;
        ArrayList<SystemUserVO> dataList;
        SystemUserPermissionRemote user;

        try {
            rctx = new InitialContext();
            user = (SystemUserPermissionRemote) rctx.lookup("security/SystemUserPermissionBean/remote");
            dataList = user.fetchByLoginName(loginName, max);
        } catch (Exception e) {
            e.printStackTrace();
            dataList = null;
        }
        
        return dataList;
    }
}
