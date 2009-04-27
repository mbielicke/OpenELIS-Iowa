package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.ProviderDO;

@Local
public interface ProviderLocal {
    public ProviderDO getProvider(Integer providerId);
}
