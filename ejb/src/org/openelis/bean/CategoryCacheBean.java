/**
 * 
 */
package org.openelis.bean;

import java.util.ArrayList;

import javax.ejb.EJB;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.DictionaryCacheCategoryListVO;
import org.openelis.domain.DictionaryCacheCategoryVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.local.DictionaryLocal;

/**
 * This class provides application level cache handling for category
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=CategoryCacheBean")
public class CategoryCacheBean {

    @EJB
    private DictionaryLocal dictionary;

    private Cache           cache, dictCache;

    public CategoryCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("category");
        dictCache = cm.getCache("dictionary");
    }

    /*
     * Category Cache
     */
    public ArrayList<DictionaryDO> getBySystemName(String systemName) throws Exception {
        Element e;
        ArrayList<DictionaryDO> list;

        e = cache.get(systemName);
        if (e != null)
            return (ArrayList<DictionaryDO>)e.getValue();

        list = dictionary.fetchByCategorySystemName(systemName);
        if (list != null) {
            cache.put(new Element(systemName, list));
            //
            // add it to dictionary cache
            //
            for (DictionaryDO data : list) {
                dictCache.put(new Element(data.getId(), data));
                if (data.getSystemName() != null)
                    dictCache.put(new Element(data.getSystemName(), data));
            }
        }

        return list;
    }

    public DictionaryCacheCategoryListVO getBySystemNames(String systemNames[]) throws Exception {
        DictionaryCacheCategoryVO data;
        DictionaryCacheCategoryListVO list;

        list = new DictionaryCacheCategoryListVO();
        list.setList(new ArrayList<DictionaryCacheCategoryVO>());

        for (String name : systemNames) {
            data = new DictionaryCacheCategoryVO();
            data.setSystemName(name);
            data.setDictionaryList(getBySystemName(name));
            list.getList().add(data);
        }

        return list;
    }

    public void evict(String systemName) throws Exception {
        cache.remove(systemName);
    }
}
