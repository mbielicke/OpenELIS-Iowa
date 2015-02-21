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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.messages.Messages;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SectionPermission.SectionFlags;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.User;

/**
 * This class provides application level cache handling for user and permission
 */

@SecurityDomain("openelis")
@Singleton
@Lock(LockType.READ)
public class UserCacheBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private LockBean            lock;

    private Cache               cache, permCache;

    private static final Logger log = Logger.getLogger("openelis");

    @PostConstruct
    public void init() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("user");
        permCache = cm.getCache("userperm");
    }

    /**
     * Returns the system user id associated with this context.
     */
    public Integer getId() {
        SystemUserVO data;

        data = getSystemUser();
        if (data != null)
            return data.getId();

        return null;
    }

    /**
     * Returns the system user data associated with this context.
     */
    public SystemUserVO getSystemUser() {
        Element e;
        String name;

        name = User.getName(ctx);
        e = cache.get(name);
        if (e != null)
            return (SystemUserVO)e.getValue();

        return getSystemUser(name);
    }

    /**
     * Returns the system user data for the specified id.
     */
    public SystemUserVO getSystemUser(Integer id) {
        Element e;
        SystemUserVO data;

        e = cache.get(id);
        if (e != null)
            return (SystemUserVO)e.getValue();

        try {
            data = EJBFactory.getSecurity().fetchById(id);
            cache.put(new Element(data.getId(), data));
            cache.put(new Element(data.getLoginName(), data));
        } catch (Exception e1) {
            data = null;
        }

        return data;
    }

    /**
     * Returns the system user data for the specified name.
     */
    public SystemUserVO getSystemUser(String name) {
        Element e;
        SystemUserVO data;
        ArrayList<SystemUserVO> list;

        e = cache.get(name);
        if (e != null)
            return (SystemUserVO)e.getValue();

        data = null;
        try {
            list = getSystemUsers(name, 1);
            if (list != null && list.size() > 0) {
                data = list.get(0);
                cache.put(new Element(data.getId(), data));
                cache.put(new Element(data.getLoginName(), data));
            }
        } catch (Exception e1) {
            log.log(Level.SEVERE, "Can't find user '" + name + "'", e1);
            data = null;
        }

        return data;
    }

    /*
     * Permission
     */

    /**
     * Throws permission exception if the user does not have permission to the
     * module for the specified operation.
     */
    public void applyPermission(String module, ModuleFlags flag) throws Exception {
        if (!getPermission().has(module, flag))
            throw new PermissionException(Messages.get().modulePermException(flag.name(), module));
    }

    /**
     * Throws permission exception if the user does not have permission to the
     * section for the specified operation.
     */
    public void applyPermission(String section, SectionFlags flag) throws Exception {
        if (!getPermission().has(section, flag))
            throw new PermissionException(Messages.get().sectionPermException(flag.name(), section));
    }

    /**
     * Returns the user permission for initial application login
     */
    public SystemUserPermission login() {
        try {
            return getPermission();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Removes the permission and various user caches for current user. This
     * method should be called on user logout.
     */
    public void logout() {
        String name;
        Element e;
        SystemUserPermission data;

        name = null;
        try {
            name = User.getName(ctx);
            e = permCache.get(name);
            if (e != null) {
                data = (SystemUserPermission)e.getValue();
                permCache.remove(name);
                cache.remove(data.getLoginName());
                cache.remove(data.getSystemUserId());
            }
            lock.removeLocks();
        } catch (Exception e1) {
            log.log(Level.SEVERE, "Can't logout '" + name + "'", e1);
        }
    }

    /**
     * Returns the system user data for matching name.
     */
    public ArrayList<SystemUserVO> getSystemUsers(String name, int max) {
        ArrayList<SystemUserVO> list;

        try {
            list = EJBFactory.getSecurity().fetchByLoginName(name, max);
        } catch (Exception e1) {
            list = null;
        }

        return list;
    }

    public ArrayList<SystemUserVO> getEmployees(String name, int max) {
        ArrayList<SystemUserVO> list;

        try {
            list = EJBFactory.getSecurity().fetchEmployeeByLoginName(name, max);
        } catch (Exception e1) {
            list = null;
        }

        return list;
    }

    /**
     * Returns the user permission for current user.
     */
    public SystemUserPermission getPermission() {
        Element e;
        String name;
        SystemUserPermission data;

        name = User.getName(ctx);
        e = permCache.get(name);
        if (e != null) {
            return (SystemUserPermission)e.getValue();
        }

        try {
            data = EJBFactory.getSecurity()
                             .fetchByApplicationAndLoginName(Constants.systemProperty().SECURITY_APPLICATION,
                                                             name);
            if (data != null) {
                permCache.put(new Element(name, data));
                cache.put(new Element(data.getLoginName(), data.getUser()));
                cache.put(new Element(data.getSystemUserId(), data.getUser()));
            }
        } catch (Exception e1) {
            log.log(Level.SEVERE, "Can't get permissions for user '" + name + "'", e1);
            data = null;
        }

        return data;
    }
    
    /**
     * Returns the list of valid users out of the list specified.
     */
    public ArrayList<SystemUserVO> validateSystemUsers(ArrayList<String> users) {
        ArrayList<SystemUserVO> validUsers;
        SystemUserVO userVO;
        
        validUsers = new ArrayList<SystemUserVO>();
        for (String user : users) {
            userVO = getSystemUser(user);
            if (userVO != null)
                validUsers.add(userVO);
        }
        
        return validUsers;
    }
}