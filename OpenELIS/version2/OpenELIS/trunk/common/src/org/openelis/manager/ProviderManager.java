package org.openelis.manager;

import org.openelis.domain.ProviderDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class ProviderManager implements HasNotesInt,RPC {
	
	protected ProviderDO 								provider;
	protected ProviderAddressManager                    addresses;
    protected NoteManager                               notes;
    
    protected transient static ProviderManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected ProviderManager() {
        addresses = null;
        notes = null;
        provider = null;
    }

    /**
     * Creates a new instance of this object. A default Provider object is
     * also created.
     */
    public static ProviderManager getInstance() {
        ProviderManager manager;

        manager = new ProviderManager();
        manager.provider = new ProviderDO();

        return manager;
    }

    public ProviderDO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDO Provider) {
        this.provider = Provider;
    }

    // service methods
    public static ProviderManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static ProviderManager fetchWithAddresses(Integer id) throws Exception {
        return proxy().fetchWithAddresses(id);
    }

    public static ProviderManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public ProviderManager add() throws Exception {
        return proxy().add(this);
    }

    public ProviderManager update() throws Exception {
        return proxy().update(this);
    }

    public ProviderManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(provider.getId());
    }

    public ProviderManager abortUpdate() throws Exception {
        return proxy().abortUpdate(provider.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public ProviderAddressManager getAddresses() throws Exception {
        if (addresses == null) {
            if (provider.getId() != null) {
                try {
                    addresses = ProviderAddressManager.fetchByProviderId(provider.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (addresses == null)
                addresses = ProviderAddressManager.getInstance();
        }
        return addresses;
    }

    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (provider.getId() != null) {
                try {
                    notes = NoteManager.findByRefTableRefId(ReferenceTable.PROVIDER, provider.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null)
                notes = NoteManager.getInstance();
        }
        return notes;
    }

    private static ProviderManagerProxy proxy() {
        if (proxy == null)
            proxy = new ProviderManagerProxy();

        return proxy;
    }

}
