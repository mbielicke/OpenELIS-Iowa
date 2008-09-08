package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.ShippingAddAutoFillDO;

@Remote
public interface ShippingRemote {

    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception;
}
