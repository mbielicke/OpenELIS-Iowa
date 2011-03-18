/**
 * 
 */
package org.openelis.bean;

import javax.ejb.EJB;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.DictionaryDO;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.remote.DictionaryCacheRemote;

/**
 * This class provides application level cache handling for dictionary
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=DictionaryCacheBean")
public class DictionaryCacheBean implements DictionaryCacheLocal, DictionaryCacheRemote {

    @EJB
    private DictionaryLocal dictionary;

    private Cache           cache;

    public DictionaryCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("dictionary");
    }

    /*
     * Dictionary Cache
     */
    public DictionaryDO getBySystemName(String systemName) throws Exception {
        Element e;
        DictionaryDO data;

        e = cache.get(systemName);
        if (e != null)
            return (DictionaryDO)e.getValue();

        data = dictionary.fetchBySystemName(systemName);
        cache.put(new Element(systemName, data));
        cache.put(new Element(data.getId(), data));

        return data;
    }

    public DictionaryDO getById(Integer id) throws Exception {
        Element e;
        DictionaryDO data;

        e = cache.get(id);
        if (e != null)
            return (DictionaryDO)e.getValue();

        data = dictionary.fetchById(id);
        cache.put(new Element(id, data));
        if (data.getSystemName() != null)
            cache.put(new Element(data.getSystemName(), data));

        return data;
    }

    public Integer getIdBySystemName(String systemName) throws Exception {
        DictionaryDO data;

        data = getBySystemName(systemName);
        if (data != null)
            return data.getId();

        return null;
    }

    public void evict(Object key) throws Exception {
        cache.remove(key);
    }
}
