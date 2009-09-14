package org.openelis.server;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.openelis.persistence.CachingManager;

public class OpenELISCacheManager extends CachingManager {
    
    public static Object getDatabaseElement(String cacheName, Serializable key) {
        Cache cache = null;
        Object value;
        Element element;
        if (!cacheManager.getStatus().equals(Status.STATUS_ALIVE))
            return null;
        try {
            cache = cacheManager.getCache(cacheName);
            value = null;
            if (cache == null) {
                return null;
            }
            if (cache.getStatus().equals(Status.STATUS_ALIVE)) {
                try {
                    element = cache.get(key);
                    if (element != null) {
                        value = element.getValue();
                    }
                } catch (IllegalStateException e) {
                    log.debug("IllegalStateException");
                } catch (CacheException e) {
                    log.debug("CacheException");
                }
            }
            return value;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
    

}
