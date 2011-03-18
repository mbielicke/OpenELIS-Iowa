/**
 * 
 */
package org.openelis.bean;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SectionPermission.SectionFlags;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.remote.UserCacheRemote;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for user and permission
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=UserCacheBean")
public class UserCacheBean implements UserCacheRemote {

    @Resource
    private SessionContext ctx;

    private Cache          cache, permCache;

    public UserCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("user");
        permCache = cm.getCache("userperm");
    }

    /**
     * Returns the system user id associated with this context.
     */
    public Integer getId() throws Exception {
        SystemUserVO data;

        data = getSystemUser();
        if (data != null)
            return data.getId();

        return null;
    }

    /**
     * Returns the system user's login name associated with this context.
     */
    public String getName() throws Exception {
        SystemUserVO data;

        data = getSystemUser();
        if (data != null)
            return data.getLoginName();

        return null;
    }

    /**
     * Returns the system user data associated with this context.
     */
    public SystemUserVO getSystemUser() throws Exception {
        Element e;
        String name;

        name = ctx.getCallerPrincipal().getName();
        e = cache.get(name);
        if (e != null)
            return (SystemUserVO)e.getValue();

        return getSystemUser(name);
    }

    /**
     * Returns the system user data for the specified id.
     */
    public SystemUserVO getSystemUser(Integer id) throws Exception {
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
            e1.printStackTrace();
            data = null;
        }

        return data;
    }

    /**
     * Returns the system user data for the specified name.
     */
    public SystemUserVO getSystemUser(String name) throws Exception {
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
            e1.printStackTrace();
        }

        return data;
    }

    /**
     * Returns the system user data for matching name.
     */
    @Override
    public ArrayList<SystemUserVO> getSystemUsers(String name, int max) throws Exception {
        ArrayList<SystemUserVO> list;

        try {
            list = EJBFactory.getSecurity().fetchByLoginName(name, max);
        } catch (Exception e1) {
            e1.printStackTrace();
            list = null;
        }

        return list;
    }

    /*
     * Permission
     */

    /**
     * Throws permission exception if the user does not have permission to the
     * module for the specified operation.
     */
    public void applyPermission(String module, ModuleFlags flag) throws Exception {
        if ( !getPermission().has(module, flag))
            throw new PermissionException("modulePermException", flag.name(), module);
    }

    /**
     * Throws permission exception if the user does not have permission to the
     * section for the specified operation.
     */
    public void applyPermission(String section, SectionFlags flag) throws Exception {
        if ( !getPermission().has(section, flag))
            throw new PermissionException("sectionPermException", flag.name(), section);
    }

    /**
     * Returns the user permission for current user.
     */
    @Override
    public SystemUserPermission getPermission() throws Exception {
        Element e;
        String name;
        SystemUserPermission data;

        name = ctx.getCallerPrincipal().getName();
        e = permCache.get(name);
        if (e != null)
            return (SystemUserPermission)e.getValue();

        data = null;
        try {
            data = EJBFactory.getSecurity().fetchByApplicationAndLoginName("openelis", name);
            permCache.put(new Element(name, data));
            cache.put(new Element(data.getLoginName(), data.getUser()));
            cache.put(new Element(data.getSystemUserId(), data.getUser()));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return data;
    }

    /**
     * Returns the user permission for initial application login
     */
    @Override
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
    @Override
    public void logout() {
        String name;
        Element e;
        SystemUserPermission data;

        name = ctx.getCallerPrincipal().getName();
        e = permCache.get(name);
        if (e != null) {
            data = (SystemUserPermission)e.getValue();
            permCache.remove(name);
            cache.remove(data.getLoginName());
            cache.remove(data.getSystemUserId());
            //sessionC.remove(name);
        }
    }

}
