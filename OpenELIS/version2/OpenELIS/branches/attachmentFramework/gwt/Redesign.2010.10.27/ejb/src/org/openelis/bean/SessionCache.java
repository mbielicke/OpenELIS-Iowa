package org.openelis.bean;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

import org.jboss.ejb3.annotation.Service;

/**
 * This class provides similar functionality to servlet sessions. The set/get
 * attributes are per user basis (getCallerPrincipal()).
 */

@Service(objectName = "jboss:custom=SessionCache")
public class SessionCache implements SessionCacheInt {

    @Resource
    private SessionContext                           ctx;

    private HashMap<String, HashMap<String, Object>> cache;

    public SessionCache() {
        cache = new HashMap<String, HashMap<String, Object>>();
    }

    /**
     * Returns the object bound with the specified name in this session, or null
     * if no object is bound under the name.
     */
    public Object getAttribute(String key) {
        return getCache().get(key);
    }

    /**
     * Binds an object to this session, using the name specified.
     */
    public void setAttribute(String key, Object value) {
        getCache().put(key, value);
    }

    /**
     * Removes the object bound with the specified name from this session.
     */
    public void removeAttribute(String key) {
        getCache().remove(key);
    }

    /**
     * Removes all the user objects from this session.
     */
    public void destroySession() {
        cache.remove(ctx.getCallerPrincipal().getName());
    }

    protected HashMap<String, Object> getCache() {
        String u;
        HashMap<String, Object> s;

        u = ctx.getCallerPrincipal().getName();
        s = cache.get(u);
        if (s == null) {
            s = new HashMap<String, Object>();
            cache.put(u, s);
        }
        return s;
    }
}
