/**
 * 
 */
package org.openelis.bean;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;

/**
 * This class provides application level cache handling for sessions
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=SessionCacheBean")
public class SessionCacheBean {

    @Resource
    private SessionContext ctx;

    private Cache          cache;

    public SessionCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("session");
    }

    /**
     * Set an attribute in current user's session 
     */
    public void setAttribute(String key, Object value) {
        Element e;
        String name;
        HashMap<String, Object> session;
        
        name = ctx.getCallerPrincipal().getName();
        e = cache.get(name);
        if (e != null)
            session = (HashMap) e.getValue(); 
        else
            session = new HashMap<String, Object>();
        session.put(key, value);

        cache.put(new Element(name, session));
    }
    
    /**
     * Returns an attribute from user user's session 
     */
    public Object getAttribute(String key) {
        Element e;
        String name;
        
        name = ctx.getCallerPrincipal().getName();
        e = cache.get(name);
        if (e != null)
            return ((HashMap)e.getValue()).get(key);
        
        return null;
    }
    
    /**
     * Removes the attribute from user's session
     */
    public void removeAttribute(String key) {
        Element e;
        String name;
        HashMap<String, Object> session;
        
        name = ctx.getCallerPrincipal().getName();
        e = cache.get(name);
        if (e != null) {
            session = (HashMap) e.getValue(); 
            if (session.remove(key) != null)
                cache.put(new Element(name, session));
        }
    }
    
    public void evict() {
        String name;

        name = ctx.getCallerPrincipal().getName();
        cache.remove(name);
    }
}
