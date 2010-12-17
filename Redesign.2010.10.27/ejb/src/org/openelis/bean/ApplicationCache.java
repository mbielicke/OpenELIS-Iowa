package org.openelis.bean;

import java.util.HashMap;

import org.jboss.ejb3.annotation.Service;

/**
 * This class provides similar functionality to servlet application wide
 * attribute management.
 */

@Service(objectName = "jboss:custom=ApplicationCache")
public class ApplicationCache implements ApplicationCacheInt {

    private HashMap<String, HashMap<Object, Object>> cache;

    public ApplicationCache() {
        cache = new HashMap<String, HashMap<Object, Object>>();
    }

    /**
     * Returns the object bound with the specified name for the appId, or null
     * if no object is bound under the name.
     */

    public Object getAttribute(String appId, Object key) {
        return getCache(appId).get(key);
    }

    /**
     * Binds an object to this appId, using the name specified.
     */
    public void setAttribute(String appId, Object key, Object value) {
        getCache(appId).put(key, value);
    }

    /**
     * Removes the object bound with the specified name from this appId.
     */
    public void removeAttribute(String appId, Object key) {
        getCache(appId).remove(key);
    }

    /**
     * Removes all the objects bound to appId.
     */
    public void removeApplication(String appId) {
        cache.remove(appId);
    }

    protected HashMap<Object, Object> getCache(String id) {
        HashMap<Object, Object> s;
        
        s  = cache.get(id);
        if (s == null) {
            s = new HashMap<Object, Object>();
            cache.put(id, s);
        }
        return s;
    }
}
