/**
 * 
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.SectionViewDO;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SectionLocal;
import org.openelis.remote.SectionCacheRemote;

/**
 * This class provides application level cache handling for section
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=SectionCacheBean")
public class SectionCacheBean implements SectionCacheLocal, SectionCacheRemote {

    @EJB
    private SectionLocal section;

    private Cache        cache;

    public SectionCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("section");
    }

    /*
     * Section Cache
     */
    public SectionViewDO getById(Integer id) throws Exception {
        Element e;
        
        e = cache.get(id);
        if (e != null)
            return (SectionViewDO) e.getValue();

        //
        // since section is a small list, we are going to load everything
        // rather than one lookup at a time.
        //
        getList();
        
        e = cache.get(id);
        if (e != null)
            return (SectionViewDO) e.getValue();

        return null;
    }  

    public ArrayList<SectionViewDO> getList() throws Exception {
        Element e;
        ArrayList<SectionViewDO> list;
        
        if (cache.getSize() == 0) {
            list = section.fetchList();
            for (SectionViewDO data : list)
                cache.put(new Element(data.getId(), data));
        } else {
            list = new ArrayList<SectionViewDO>(cache.getSize());
            for (Integer id: (List<Integer>) cache.getKeys()) {
                e = cache.get(id);
                if (e != null)
                    list.add((SectionViewDO) e.getValue());
            }
        }

        return list;
    }

    public void evict(Integer id) {
        cache.remove(id);
    }
}
