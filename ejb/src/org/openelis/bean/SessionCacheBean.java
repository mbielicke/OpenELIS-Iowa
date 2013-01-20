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

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.security.annotation.SecurityDomain;

/**
 * This class provides application level cache handling for sessions
 */

@SecurityDomain("openelis")
@Singleton

public class SessionCacheBean {

    @Resource
    private SessionContext ctx;

    private Cache          cache;

    @PostConstruct
    public void init() {
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
