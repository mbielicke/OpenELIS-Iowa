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

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for dictionary
 */

@SecurityDomain("openelis")
@Singleton
@Lock(LockType.READ)
public class DictionaryCacheBean {

    private Cache cache;

    @PostConstruct
    public void init() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("dictionary");
    }

    /*
     * Dictionary Cache
     */
    public DictionaryDO getById(Integer id) throws Exception {
        Element e;
        DictionaryDO data;

        e = cache.get(id);
        if (e != null)
            return (DictionaryDO)e.getValue();

        data = EJBFactory.getDictionary().fetchById(id);
        cache.put(new Element(id, data));
        if (data.getSystemName() != null)
            cache.put(new Element(data.getSystemName(), data));

        return data;
    }

    public ArrayList<DictionaryDO> getByIds(ArrayList<Integer> ids) throws Exception {
        int i;
        Element e;
        ArrayList<DictionaryDO> list;

        i = 0;
        /*
         * only keep the ids that are not present in the cache
         */
        list = new ArrayList<DictionaryDO>();
        while (i < ids.size()) {
            e = cache.get(ids.get(i));
            if (e != null) {
                ids.remove(i);
                list.add((DictionaryDO)e.getValue());
            } else {
                i++ ;
            }
        }

        if (ids.size() > 0) {
            /*
             * fetch the dictionary records not in the cache 
             */
            for (DictionaryDO data : EJBFactory.getDictionary().fetchByIds(ids)) {
                cache.put(new Element(data.getId(), data));
                if (data.getSystemName() != null)
                    cache.put(new Element(data.getSystemName(), data));
                list.add(data);
            }
        }

        return list;
    }

    public Integer getIdBySystemName(String systemName) throws Exception {
        DictionaryDO data;

        data = getBySystemName(systemName);
        if (data != null)
            return data.getId();

        return null;
    }

    public DictionaryDO getBySystemName(String systemName) throws Exception {
        Element e;
        DictionaryDO data;

        e = cache.get(systemName);
        if (e != null)
            return (DictionaryDO)e.getValue();

        data = EJBFactory.getDictionary().fetchBySystemName(systemName);
        cache.put(new Element(systemName, data));
        cache.put(new Element(data.getId(), data));

        return data;
    }

    public void evict(Object key) throws Exception {
        cache.remove(key);
    }
}
