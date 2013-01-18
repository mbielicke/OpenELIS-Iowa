/**
 * 
 */
package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.DictionaryDO;

@Remote
public interface DictionaryCacheRemote {

    public DictionaryDO getBySystemName(String systemName) throws Exception;

    public DictionaryDO getById(Integer id) throws Exception;

    public Integer getIdBySystemName(String systemName) throws Exception;
}
