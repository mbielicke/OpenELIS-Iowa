package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.ProviderLocationDO;

public class ProviderLocationManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer                          providerId;
	protected ArrayList<ProviderLocationDO>    locations;
	protected ArrayList<ProviderLocationDO>    deleted;
	
    protected transient static ProviderLocationManagerProxy proxy;

    protected ProviderLocationManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    
    public static ProviderLocationManager getInstance() {
        return new ProviderLocationManager();
    }

    public int count() {
        if (locations == null)
            return 0;

        return locations.size();
    }

    public ProviderLocationDO getLocationAt(int i) {
        return locations.get(i);
    }

    public void setLocationAt(ProviderLocationDO contact, int i) {
        if (locations == null)
            locations = new ArrayList<ProviderLocationDO>();
        locations.set(i, contact);
    }

    public void addLocation(ProviderLocationDO contact) {
        if (locations == null)
            locations = new ArrayList<ProviderLocationDO>();
        locations.add(contact);
    }

    public void addLocationAt(ProviderLocationDO contact, int i) {
        if (locations == null)
            locations = new ArrayList<ProviderLocationDO>();
        locations.add(i, contact);
    }

    public void removeLocationAt(int i) {
        ProviderLocationDO tmp;

        if (locations == null || i >= locations.size())
            return;
        
        tmp = locations.remove(i);
        if (tmp != null) {
            if (deleted == null)
                deleted = new ArrayList<ProviderLocationDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static ProviderLocationManager fetchByProviderId(Integer id) throws Exception {
        return proxy().fetchByProviderId(id);
    }

    public ProviderLocationManager add() throws Exception {
        return proxy().add(this);
    }

    public ProviderLocationManager update() throws Exception {
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

    ArrayList<ProviderLocationDO> getLocations() {
        return locations;
    }

    void setLocations(ArrayList<ProviderLocationDO> locations) {
        this.locations = locations;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    ProviderLocationDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ProviderLocationManagerProxy proxy() {
        if (proxy == null)
            proxy = new ProviderLocationManagerProxy();
        return proxy;
    }

}
