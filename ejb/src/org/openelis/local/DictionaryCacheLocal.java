/**
 * 
 */
package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.DictionaryDO;

@Local
public interface DictionaryCacheLocal {

    public DictionaryDO getBySystemName(String systemName) throws Exception;

    public DictionaryDO getById(Integer id) throws Exception;

    public Integer getIdBySystemName(String systemName) throws Exception;

    public void evict(Object key) throws Exception;
}
