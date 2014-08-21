package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.ProviderDO;

@Local
public interface ProviderLocal {
    public ProviderDO fetchById(Integer id) throws Exception;

    public ProviderDO add(ProviderDO data) throws Exception;

    public ProviderDO update(ProviderDO data) throws Exception;

    public void validate(ProviderDO data) throws Exception;
}
