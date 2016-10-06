package org.openelis.bean;

import static org.openelis.manager.ProviderManager1Accessor.addLocation;
import static org.openelis.manager.ProviderManager1Accessor.addNote;
import static org.openelis.manager.ProviderManager1Accessor.addAnalyte;
import static org.openelis.manager.ProviderManager1Accessor.getLocations;
import static org.openelis.manager.ProviderManager1Accessor.getNotes;
import static org.openelis.manager.ProviderManager1Accessor.getAnalytes;
import static org.openelis.manager.ProviderManager1Accessor.getProvider;
import static org.openelis.manager.ProviderManager1Accessor.getRemoved;
import static org.openelis.manager.ProviderManager1Accessor.setProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ProviderAnalyteViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class ProviderManager1Bean {

    @EJB
    private LockBean             lock;

    @EJB
    private ProviderBean         provider;

    @EJB
    private ProviderLocationBean providerLocation;

    @EJB
    private NoteBean             note;

    @EJB
    private ProviderAnalyteBean  providerAnalyte;

    public ProviderManager1 getInstance() throws Exception {
        ProviderManager1 pm;
        ProviderDO p;

        pm = new ProviderManager1();
        p = new ProviderDO();
        setProvider(pm, p);

        return pm;
    }

    /**
     * Returns a provider manager for specified primary id
     */
    public ProviderManager1 fetchById(Integer providerId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<ProviderManager1> pms;

        ids = new ArrayList<Integer>(1);
        ids.add(providerId);
        pms = fetchByIds(ids);
        return pms.size() == 0 ? null : pms.get(0);
    }

    /**
     * Returns provider managers for specified primary ids
     */
    public ArrayList<ProviderManager1> fetchByIds(ArrayList<Integer> providerIds) throws Exception {
        ProviderManager1 pm;
        ArrayList<Integer> ids1, ids2;
        ArrayList<ProviderManager1> pms;
        ArrayList<ProviderDO> providers;
        HashMap<Integer, ProviderManager1> map;

        pms = new ArrayList<ProviderManager1>();

        /*
         * if there are no providers, then return an empty list
         */
        providers = provider.fetchByIds(providerIds);
        if (providers.size() < 1)
            return pms;

        /*
         * everything is based on provider ids
         */
        ids1 = new ArrayList<Integer>();
        ids2 = new ArrayList<Integer>();
        map = new HashMap<Integer, ProviderManager1>();

        for (ProviderDO data : providers) {
            pm = new ProviderManager1();
            setProvider(pm, data);
            pms.add(pm);

            ids1.add(data.getId()); // for fetch
            map.put(data.getId(), pm); // for linking
        }

        ids2 = new ArrayList<Integer>();
        ids2.add(Constants.table().PROVIDER);
        ids2.add(Constants.table().NOTE);
        for (NoteViewDO data : note.fetchByIdsAndTables(ids1, ids2)) {
            pm = map.get(data.getReferenceId());
            addNote(pm, data);
        }

        for (ProviderLocationDO data : providerLocation.fetchByProviderIds(ids1)) {
            pm = map.get(data.getProviderId());
            addLocation(pm, data);
        }
        
        for (ProviderAnalyteViewDO data : providerAnalyte.fetchByProviderIds(ids1)) {
            pm = map.get(data.getProviderId());
            addAnalyte(pm, data);
        }

        return pms;
    }

    /**
     * Returns a provider manager based on the specified query
     */
    public ArrayList<ProviderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdFirstLastNameVO vo : provider.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns a locked provider manager with specified provider id
     */
    @RolesAllowed("provider-update")
    public ProviderManager1 fetchForUpdate(Integer providerId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<ProviderManager1> pms;

        ids = new ArrayList<Integer>(1);
        ids.add(providerId);
        pms = fetchForUpdate(ids);
        return pms.size() == 0 ? null : pms.get(0);
    }

    /**
     * Returns a list of locked provider managers with specified provider ids
     */
    @RolesAllowed("provider-update")
    public ArrayList<ProviderManager1> fetchForUpdate(ArrayList<Integer> providerIds) throws Exception {
        lock.lock(Constants.table().PROVIDER, providerIds);
        return fetchByIds(providerIds);
    }

    /**
     * Unlocks and returns a provider manager with specified provider id
     */
    @RolesAllowed({"provier-add", "provider-update"})
    public ProviderManager1 unlock(Integer providerId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<ProviderManager1> pms;

        ids = new ArrayList<Integer>(1);
        ids.add(providerId);
        pms = unlock(ids);
        return pms.size() == 0 ? null : pms.get(0);
    }

    /**
     * Unlocks and returns list of provider managers with specified provider ids
     */
    @RolesAllowed({"provider-add", "provider-update"})
    public ArrayList<ProviderManager1> unlock(ArrayList<Integer> providerIds) throws Exception {
        lock.unlock(Constants.table().PROVIDER, providerIds);
        return fetchByIds(providerIds);
    }

    /**
     * Adds/updates the provider and related records in the database. The
     * records are validated before add/update and the provider record must have
     * a lock record if it has an id.
     */
    @RolesAllowed({"provider-add", "provider-update"})
    public ProviderManager1 update(ProviderManager1 pm, boolean ignoreWarnings) throws Exception {
        ArrayList<ProviderManager1> pms;

        pms = new ArrayList<ProviderManager1>(1);
        pms.add(pm);
        update(pms, ignoreWarnings);
        return pms.get(0);
    }

    /**
     * Adds/updates all the providers and related records in the database. All
     * the records are validated before add/update and the provider records must
     * each have a lock record if they have an id.
     */
    @RolesAllowed({"provider-add", "provider-update"})
    public ArrayList<ProviderManager1> update(ArrayList<ProviderManager1> pms,
                                              boolean ignoreWarnings) throws Exception {
        Integer so;
        HashSet<Integer> ids;
        ArrayList<Integer> locks;

        ids = new HashSet<Integer>();
        validate(pms, ignoreWarnings);
        /*
         * check all the locks
         */
        for (ProviderManager1 pm : pms) {
            if (getProvider(pm).getId() != null)
                ids.add(getProvider(pm).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().PROVIDER, locks);
        } else {
            locks = null;
        }
        ids = null;

        for (ProviderManager1 pm : pms) {
            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(pm) != null) {
                for (DataObject data : getRemoved(pm)) {
                    if (data instanceof ProviderLocationDO)
                        providerLocation.delete( ((ProviderLocationDO)data));
                    else if (data instanceof NoteViewDO)
                        note.delete( ((NoteViewDO)data));
                    else if (data instanceof ProviderAnalyteViewDO)
                        providerAnalyte.delete( ((ProviderAnalyteViewDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update provider
            if (getProvider(pm).getId() == null)
                provider.add(getProvider(pm));
            else
                provider.update(getProvider(pm));

            if (getLocations(pm) != null) {
                for (ProviderLocationDO data : getLocations(pm)) {
                    if (data.getId() == null) {
                        data.setProviderId(getProvider(pm).getId());
                        providerLocation.add(data);
                    } else {
                        providerLocation.update(data);
                    }
                }
            }

            if (getNotes(pm) != null) {
                for (NoteViewDO data : getNotes(pm)) {
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().PROVIDER);
                        data.setReferenceId(getProvider(pm).getId());
                        note.add(data);
                    } else {
                        note.update(data);
                    }
                }
            }
            
            so = 0;
            if (getAnalytes(pm) != null) {
                for (ProviderAnalyteViewDO data : getAnalytes(pm)) {
                    data.setSortOrder( ++so);
                    if (data.getId() == null) {
                        data.setProviderId(getProvider(pm).getId());
                        providerAnalyte.add(data);
                    } else {
                        providerAnalyte.update(data);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().PROVIDER, locks);

        return pms;
    }

    public ProviderManager1 abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().PROVIDER, id);
        return fetchById(id);
    }

    /**
     * Validates the provider manager for add or update. The routine throws a
     * list of exceptions/warnings listing all the problems for each provider.
     */
    private void validate(ArrayList<ProviderManager1> pms, boolean ignoreWarning) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        for (ProviderManager1 pm : pms) {
            if (getProvider(pm) != null) {
                if (getProvider(pm).isChanged()) {
                    try {
                        provider.validate(getProvider(pm));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getLocations(pm) != null) {
                for (ProviderLocationDO data : getLocations(pm)) {
                    try {
                        providerLocation.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getAnalytes(pm) != null) {
                for (ProviderAnalyteViewDO data : getAnalytes(pm)) {
                    try {
                        providerAnalyte.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }
}
