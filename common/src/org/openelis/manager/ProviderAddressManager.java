package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.ProviderAddressDO;
import org.openelis.gwt.common.RPC;

public class ProviderAddressManager implements RPC {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer                         providerId;
	protected ArrayList<ProviderAddressDO>    addresses;
	protected ArrayList<ProviderAddressDO>    deleted;
	
    protected transient static ProviderAddressManagerProxy proxy;

    protected ProviderAddressManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    
    public static ProviderAddressManager getInstance() {
        return new ProviderAddressManager();
    }

    public int count() {
        if (addresses == null)
            return 0;

        return addresses.size();
    }

    public ProviderAddressDO getAddressAt(int i) {
        return addresses.get(i);
    }

    public void setAddressAt(ProviderAddressDO contact, int i) {
        if (addresses == null)
            addresses = new ArrayList<ProviderAddressDO>();
        addresses.set(i, contact);
    }

    public void addAddress(ProviderAddressDO contact) {
        if (addresses == null)
            addresses = new ArrayList<ProviderAddressDO>();
        addresses.add(contact);
    }

    public void addAddressAt(ProviderAddressDO contact, int i) {
        if (addresses == null)
            addresses = new ArrayList<ProviderAddressDO>();
        addresses.add(i, contact);
    }

    public void removeAddressAt(int i) {
        ProviderAddressDO tmp;

        if (addresses == null || i >= addresses.size())
            return;
        
        tmp = addresses.remove(i);
        if (tmp != null) {
            if (deleted == null)
                deleted = new ArrayList<ProviderAddressDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static ProviderAddressManager fetchByProviderId(Integer id) throws Exception {
        return proxy().fetchByProviderId(id);
    }

    public ProviderAddressManager add() throws Exception {
        return proxy().add(this);
    }

    public ProviderAddressManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
       // proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getProviderId() {
        return providerId;
    }
    
    void setProviderId(Integer id) {
        providerId = id;
    }

    ArrayList<ProviderAddressDO> getAddresses() {
        return addresses;
    }

    void setAddresses(ArrayList<ProviderAddressDO> addresses) {
        this.addresses = addresses;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    ProviderAddressDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ProviderAddressManagerProxy proxy() {
        if (proxy == null)
            proxy = new ProviderAddressManagerProxy();
        return proxy;
    }

}
