package org.openelis.bean;

import static org.openelis.manager.OrganizationManager1Accessor.addContact;
import static org.openelis.manager.OrganizationManager1Accessor.addNote;
import static org.openelis.manager.OrganizationManager1Accessor.addParameter;
import static org.openelis.manager.OrganizationManager1Accessor.getContacts;
import static org.openelis.manager.OrganizationManager1Accessor.getNotes;
import static org.openelis.manager.OrganizationManager1Accessor.getOrganization;
import static org.openelis.manager.OrganizationManager1Accessor.getParameters;
import static org.openelis.manager.OrganizationManager1Accessor.getRemoved;
import static org.openelis.manager.OrganizationManager1Accessor.setOrganization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.manager.OrganizationManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
public class OrganizationManager1Bean {
    @EJB
    private LockBean                  lock;

    @EJB
    private UserCacheBean             userCache;

    @EJB
    private OrganizationBean          organization;

    @EJB
    private OrganizationContactBean   organizationContact;

    @EJB
    private OrganizationParameterBean organizationParameter;

    @EJB
    private NoteBean                  note;

    /**
     * Returns an organization manager for specified primary id and requested
     * load elements
     */
    public OrganizationManager1 fetchById(Integer orgId, OrganizationManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrganizationManager1> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(orgId);
        oms = fetchByIds(ids, false, elements);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Returns organization managers for specified primary ids and requested
     * load elements
     */
    public ArrayList<OrganizationManager1> fetchByIds(ArrayList<Integer> orgIds,
                                                      OrganizationManager1.Load... elements) throws Exception {
        return fetchByIds(orgIds, false, elements);
    }

    /**
     * Returns an organization manager based on the specified query and
     * requested load elements
     */
    public ArrayList<OrganizationManager1> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                        int max,
                                                        OrganizationManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : organization.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids, false, elements);
    }

    /**
     * Returns a locked organization manager with specified organization id
     */
    @RolesAllowed("organization-update")
    public OrganizationManager1 fetchForUpdate(Integer orgId) throws Exception {
        return fetchForUpdate(orgId,
                              OrganizationManager1.Load.CONTACTS,
                              OrganizationManager1.Load.PARAMETERS,
                              OrganizationManager1.Load.NOTES);
    }

    /**
     * Returns a locked organization manager with specified organization id and
     * requested load elements
     */
    @RolesAllowed("organization-update")
    public OrganizationManager1 fetchForUpdate(Integer organizationId,
                                               OrganizationManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrganizationManager1> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(organizationId);
        oms = fetchForUpdate(ids, elements);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Returns a list of locked organization managers with specified
     * organization ids and requested load elements
     */
    @RolesAllowed("organization-update")
    public ArrayList<OrganizationManager1> fetchForUpdate(ArrayList<Integer> organizationIds,
                                                          OrganizationManager1.Load... elements) throws Exception {
        lock.lock(Constants.table().ORGANIZATION, organizationIds);
        return fetchByIds(organizationIds, true, elements);
    }

    /**
     * Unlocks and returns a organization manager with specified organization id
     * and requested load elements
     */
    @RolesAllowed({"organization-add", "organization-update"})
    public OrganizationManager1 unlock(Integer organizationId,
                                       OrganizationManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrganizationManager1> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(organizationId);
        oms = unlock(ids, elements);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Unlocks and returns list of organization managers with specified
     * organization ids and requested load elements
     */
    @RolesAllowed({"organization-add", "organization-update"})
    public ArrayList<OrganizationManager1> unlock(ArrayList<Integer> organizationIds,
                                                  OrganizationManager1.Load... elements) throws Exception {
        lock.unlock(Constants.table().ORGANIZATION, organizationIds);
        return fetchByIds(organizationIds, false, elements);
    }

    /**
     * Adds/updates the organization and related records in the database. The
     * records are validated before add/update and the organization record must
     * have a lock record if it has an id.
     */
    @RolesAllowed({"organization-add", "organization-update"})
    public OrganizationManager1 update(OrganizationManager1 om) throws Exception {
        ArrayList<OrganizationManager1> oms;

        oms = new ArrayList<OrganizationManager1>(1);
        oms.add(om);
        update(oms);
        return oms.get(0);
    }

    /**
     * Adds/updates all the organizations and related records in the database.
     * All the records are validated before add/update and the organization
     * records must each have a lock record if they have an id.
     */
    @RolesAllowed({"organization-add", "organization-update"})
    public ArrayList<OrganizationManager1> update(ArrayList<OrganizationManager1> oms) throws Exception {
        HashSet<Integer> ids;
        ArrayList<Integer> locks;

        validate(oms);
        /*
         * check all the locks
         */
        ids = new HashSet<Integer>();
        for (OrganizationManager1 om : oms) {
            if (getOrganization(om).getId() != null)
                ids.add(getOrganization(om).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().ORGANIZATION, locks);
        } else {
            locks = null;
        }
        ids = null;

        for (OrganizationManager1 om : oms) {
            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(om) != null) {
                for (DataObject data : getRemoved(om)) {
                    if (data instanceof OrganizationContactDO)
                        organizationContact.delete( ((OrganizationContactDO)data));
                    else if (data instanceof OrganizationParameterDO)
                        organizationParameter.delete( ((OrganizationParameterDO)data));
                    else if (data instanceof NoteViewDO)
                        note.delete( ((NoteViewDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update organization
            if (getOrganization(om).getId() == null)
                organization.add(getOrganization(om));
            else
                organization.update(getOrganization(om));

            if (getContacts(om) != null) {
                for (OrganizationContactDO data : getContacts(om)) {
                    if (data.getId() == null) {
                        data.setOrganizationId(getOrganization(om).getId());
                        organizationContact.add(data);
                    } else {
                        organizationContact.update(data);
                    }
                }
            }

            if (getParameters(om) != null) {
                for (OrganizationParameterDO data : getParameters(om)) {
                    if (data.getId() == null) {
                        data.setOrganizationId(getOrganization(om).getId());
                        organizationParameter.add(data);
                    } else {
                        organizationParameter.update(data);
                    }
                }
            }

            if (getNotes(om) != null) {
                for (NoteViewDO data : getNotes(om)) {
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().ORGANIZATION);
                        data.setReferenceId(getOrganization(om).getId());
                        note.add(data);
                    } else {
                        note.update(data);
                    }
                }
            }

        }

        if (locks != null)
            lock.unlock(Constants.table().ORGANIZATION, locks);

        return oms;
    }

    public ArrayList<OrganizationParameterDO> updateForNotify(ArrayList<OrganizationParameterDO> parameters) throws Exception {
        ArrayList<OrganizationParameterDO> returnParameters;
        ArrayList<Integer> orgIds;
        HashSet<Integer> orgIdSet;
        ValidationErrorsList list;

        checkSecurityForNotify(ModuleFlags.SELECT);

        list = new ValidationErrorsList();
        for (OrganizationParameterDO param : parameters) {
            /*
             * existing parameters (id != null) with null values are not
             * validated because they are to be deleted
             */
            if (param.getId() != null && param.getValue() == null)
                continue;
            try {
                organizationParameter.validate(param);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e);
            }
        }

        if (list.size() > 0)
            throw list;

        orgIdSet = new HashSet<Integer>();
        for (OrganizationParameterDO param : parameters)
            orgIdSet.add(param.getOrganizationId());
        orgIds = new ArrayList<Integer>(orgIdSet);
        lock.lock(Constants.table().ORGANIZATION, orgIds);

        returnParameters = new ArrayList<OrganizationParameterDO>();
        for (OrganizationParameterDO param : parameters) {

            if (param.getId() != null) {
                /*
                 * delete existing parameters with null values
                 */
                if (param.getValue() == null)
                    organizationParameter.delete(param);
                else
                    returnParameters.add(organizationParameter.update(param));
            } else if (param.getValue() != null) {
                returnParameters.add(organizationParameter.add(param));
            }
        }
        lock.unlock(Constants.table().ORGANIZATION, orgIds);

        return returnParameters;
    }

    private ArrayList<OrganizationManager1> fetchByIds(ArrayList<Integer> organizationIds,
                                                       boolean isUpdate,
                                                       OrganizationManager1.Load... elements) throws Exception {
        OrganizationManager1 om;
        EnumSet<OrganizationManager1.Load> el;
        ArrayList<Integer> ids1, ids2;
        ArrayList<OrganizationManager1> oms;
        ArrayList<OrganizationViewDO> orgs;
        HashMap<Integer, OrganizationManager1> map;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        oms = new ArrayList<OrganizationManager1>();
        if (elements != null && elements.length > 0)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(OrganizationManager1.Load.class);

        /*
         * if there are no organizations, then return an empty list
         */
        orgs = organization.fetchByIds(organizationIds);
        if (orgs.size() < 1)
            return oms;

        /*
         * build level 1, everything is based on organization ids
         */
        ids1 = new ArrayList<Integer>();
        ids2 = new ArrayList<Integer>();
        map = new HashMap<Integer, OrganizationManager1>();

        for (OrganizationViewDO data : orgs) {
            om = new OrganizationManager1();
            setOrganization(om, data);
            oms.add(om);

            ids1.add(data.getId()); // for fetch
            map.put(data.getId(), om); // for linking
        }

        /*
         * various lists for each organization
         */
        if (el.contains(OrganizationManager1.Load.CONTACTS)) {
            for (OrganizationContactDO data : organizationContact.fetchByOrganizationIds(ids1)) {
                om = map.get(data.getOrganizationId());
                addContact(om, data);
            }
        }

        if (el.contains(OrganizationManager1.Load.PARAMETERS)) {
            for (OrganizationParameterDO data : organizationParameter.fetchByOrganizationIds(ids1)) {
                om = map.get(data.getOrganizationId());
                addParameter(om, data);
            }
        }

        if (el.contains(OrganizationManager1.Load.NOTES)) {
            ids2 = new ArrayList<Integer>();
            ids2.add(Constants.table().ORGANIZATION);
            for (NoteViewDO data : note.fetchByIdsAndTables(ids1, ids2)) {
                om = map.get(data.getReferenceId());
                addNote(om, data);
            }
        }
        return oms;
    }

    /**
     * Validates the organization manager for add or update. The routine throws
     * a list of exceptions/warnings listing all the problems for each
     * organization.
     */
    private void validate(ArrayList<OrganizationManager1> oms) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        for (OrganizationManager1 om : oms) {
            /*
             * organization level
             */
            if (getOrganization(om) != null) {
                if (getOrganization(om).isChanged()) {
                    try {
                        organization.validate(getOrganization(om));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getContacts(om) != null) {
                for (OrganizationContactDO data : getContacts(om)) {
                    try {
                        organizationContact.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getParameters(om) != null) {
                for (OrganizationParameterDO data : getParameters(om)) {
                    try {
                        organizationParameter.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }

    private void checkSecurityForNotify(ModuleFlags flag) throws Exception {
        userCache.applyPermission("w_notify", flag);
    }
}
