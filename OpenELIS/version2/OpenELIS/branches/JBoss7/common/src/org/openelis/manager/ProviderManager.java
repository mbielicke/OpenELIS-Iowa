package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.ProviderDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.NotFoundException;

public class ProviderManager implements Serializable, HasNotesInt {

    private static final long                       serialVersionUID = 1L;

    protected ProviderDO                            provider;
    protected ProviderLocationManager               locations;
    protected NoteManager                           notes;

    protected transient static ProviderManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected ProviderManager() {
        locations = null;
        notes = null;
        provider = null;
    }

    /**
     * Creates a new instance of this object. A default Provider object is also
     * created.
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

    public static ProviderManager fetchWithLocations(Integer id) throws Exception {
        return proxy().fetchWithLocations(id);
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
    public ProviderLocationManager getLocations() throws Exception {
        if (locations == null) {
            if (provider.getId() != null) {
                try {
                    locations = ProviderLocationManager.fetchByProviderId(provider.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (locations == null)
                locations = ProviderLocationManager.getInstance();
        }
        return locations;
    }

    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (provider.getId() != null) {
                try {
                    notes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.PROVIDER,
                                                                  provider.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null){
                notes = NoteManager.getInstance();
                notes.setIsExternal(false);
            }
        }
        return notes;
    }

    private static ProviderManagerProxy proxy() {
        if (proxy == null)
            proxy = new ProviderManagerProxy();

        return proxy;
    }
}
