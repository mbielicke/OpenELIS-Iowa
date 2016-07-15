package org.openelis.bean;

import static org.openelis.manager.AuxFieldGroupManager1Accessor.addField;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.addValue;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.getFields;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.getGroup;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.getRemoved;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.getValues;
import static org.openelis.manager.AuxFieldGroupManager1Accessor.setGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdNameVO;
import org.openelis.exception.ParseException;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utilcommon.ResultRangeNumeric;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class AuxFieldGroupManager1Bean {

    @Resource
    SessionContext    ctx;

    @EJB
    LockBean          lock;

    @EJB
    UserCacheBean     userCache;

    @EJB
    AuxFieldGroupBean auxFieldGroup;

    @EJB
    AuxFieldBean      auxField;

    @EJB
    AuxFieldValueBean auxFieldValue;

    /**
     * Returns an aux group manager for specified primary id and requested load
     * elements
     */
    public AuxFieldGroupManager1 fetchById(Integer groupId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AuxFieldGroupManager1> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(groupId);
        ams = fetchByIds(ids);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Returns aux group managers for specified primary ids
     */
    public ArrayList<AuxFieldGroupManager1> fetchByIds(ArrayList<Integer> groupIds) throws Exception {
        AuxFieldGroupManager1 am;
        ArrayList<Integer> ids1, ids2;
        ArrayList<AuxFieldGroupManager1> ams;
        ArrayList<AuxFieldGroupDO> groups;
        HashMap<Integer, AuxFieldGroupManager1> map1, map2;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        ams = new ArrayList<AuxFieldGroupManager1>();

        /*
         * if there are no groups, then return an empty list
         */
        groups = auxFieldGroup.fetchByIds(groupIds);
        if (groups.size() < 1)
            return ams;

        /*
         * build level 1, everything is based on group ids
         */
        ids1 = new ArrayList<Integer>();
        ids2 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, AuxFieldGroupManager1>();

        for (AuxFieldGroupDO data : groups) {
            am = new AuxFieldGroupManager1();
            setGroup(am, data);
            ams.add(am);

            ids1.add(data.getId()); // for fetch
            map1.put(data.getId(), am); // for linking
        }

        /*
         * build level 2, everything is based on aux field ids
         */
        ids2 = new ArrayList<Integer>();
        map2 = new HashMap<Integer, AuxFieldGroupManager1>();

        for (AuxFieldViewDO data : auxField.fetchByGroupIds(ids1)) {
            am = map1.get(data.getAuxFieldGroupId());
            addField(am, data);
            ids2.add(data.getId());
            map2.put(data.getId(), am);
        }
        if (ids2.size() > 0) {
            for (AuxFieldValueViewDO data : auxFieldValue.fetchByFieldIds(ids2)) {
                am = map2.get(data.getAuxFieldId());
                addValue(am, data);
            }
        }

        return ams;
    }

    /**
     * Returns an aux group manager based on the specified query
     */
    public ArrayList<AuxFieldGroupManager1> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                         int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : auxFieldGroup.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns a locked aux group manager with specified group id
     */
    @RolesAllowed("auxiliary-update")
    public AuxFieldGroupManager1 fetchForUpdate(Integer groupId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AuxFieldGroupManager1> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(groupId);
        ams = fetchForUpdate(ids);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Returns a list of locked aux group managers with specified group ids
     */
    @RolesAllowed("auxiliary-update")
    public ArrayList<AuxFieldGroupManager1> fetchForUpdate(ArrayList<Integer> groupIds) throws Exception {
        lock.lock(Constants.table().AUX_FIELD_GROUP, groupIds);
        return fetchByIds(groupIds);
    }

    /**
     * Unlocks and returns an aux group manager with specified group id
     */
    @RolesAllowed({"auxiliary-add", "auxiliary-update"})
    public AuxFieldGroupManager1 unlock(Integer groupId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<AuxFieldGroupManager1> ams;

        ids = new ArrayList<Integer>(1);
        ids.add(groupId);
        ams = unlock(ids);
        return ams.size() == 0 ? null : ams.get(0);
    }

    /**
     * Unlocks and returns list of aux group managers with specified group ids
     */
    @RolesAllowed({"auxiliary-add", "auxiliary-update"})
    public ArrayList<AuxFieldGroupManager1> unlock(ArrayList<Integer> groupIds) throws Exception {
        lock.unlock(Constants.table().AUX_FIELD_GROUP, groupIds);
        return fetchByIds(groupIds);
    }

    /**
     * Adds/updates the aux group and related records in the database. The
     * records are validated before add/update and the aux group record must
     * have a lock record if it has an id.
     */
    @RolesAllowed({"auxiliary-add", "auxiliary-update"})
    public AuxFieldGroupManager1 update(AuxFieldGroupManager1 am) throws Exception {
        ArrayList<AuxFieldGroupManager1> ams;

        ams = new ArrayList<AuxFieldGroupManager1>(1);
        ams.add(am);
        update(ams);
        return ams.get(0);
    }

    /**
     * Adds/updates all the aux groups and related records in the database. All
     * the records are validated before add/update and the group records must
     * each have a lock record if they have an id.
     */
    @RolesAllowed({"auxiliary-add", "auxiliary-update"})
    public ArrayList<AuxFieldGroupManager1> update(ArrayList<AuxFieldGroupManager1> ams) throws Exception {
        Integer so, tmpid;
        HashSet<Integer> ids;
        ArrayList<Integer> locks;
        HashMap<Integer, Integer> tmap;

        ids = new HashSet<Integer>();

        validate(ams);

        /*
         * check all the locks
         */
        ids.clear();
        for (AuxFieldGroupManager1 am : ams) {
            if (getGroup(am).getId() != null)
                ids.add(getGroup(am).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().AUX_FIELD_GROUP, locks);
        } else {
            locks = null;
        }
        ids = null;

        /*
         * the front code uses negative ids (temporary ids) to link fields and
         * values. The negative ids are mapped to actual database ids through
         * tmap.
         */
        tmap = new HashMap<Integer, Integer>();
        for (AuxFieldGroupManager1 am : ams) {
            tmap.clear();

            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(am) != null) {
                for (DataObject data : getRemoved(am)) {
                    if (data instanceof AuxFieldViewDO)
                        auxField.delete( ((AuxFieldViewDO)data));
                    else if (data instanceof AuxFieldValueViewDO)
                        auxFieldValue.delete( ((AuxFieldValueViewDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update group
            if (getGroup(am).getId() == null)
                auxFieldGroup.add(getGroup(am));
            else
                auxFieldGroup.update(getGroup(am));

            so = 0;
            if (getFields(am) != null) {
                for (AuxFieldViewDO data : getFields(am)) {
                    tmpid = data.getId();
                    data.setSortOrder( ++so);
                    if (data.getId() < 0) {
                        data.setAuxFieldGroupId(getGroup(am).getId());
                        auxField.add(data);
                    } else {
                        auxField.update(data);
                    }
                    tmap.put(tmpid, data.getId());
                }
            }

            if (getValues(am) != null) {
                for (AuxFieldValueViewDO data : getValues(am)) {
                    if (data.getId() < 0) {
                        data.setAuxFieldId(tmap.get(data.getAuxFieldId()));
                        auxFieldValue.add(data);
                    } else {
                        auxFieldValue.update(data);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().AUX_FIELD_GROUP, locks);

        return ams;
    }

    /**
     * Validates the aux field manager for add or update. The routine throws a
     * list of exceptions/warnings listing all the problems for each aux group.
     */
    private void validate(ArrayList<AuxFieldGroupManager1> ams) throws Exception {
        boolean typeExceptionThrown;
        int numDefault, count;
        ValidationErrorsList e;
        AuxFieldValueViewDO v;
        List<ResultRangeNumeric> nrList;
        String value;
        ResultRangeNumeric nr, lr;
        Integer typeId, entryId, firstTypeId;
        ArrayList<Integer> dictList;
        ArrayList<AuxFieldValueViewDO> values;

        e = new ValidationErrorsList();

        for (AuxFieldGroupManager1 am : ams) {
            /*
             * group level
             */
            if (getGroup(am) != null) {
                if (getGroup(am).isChanged()) {
                    try {
                        auxFieldGroup.validate(getGroup(am));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getFields(am) != null) {
                for (AuxFieldViewDO data : getFields(am)) {
                    try {
                        auxField.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                    values = getValues(am, data);
                    dictList = new ArrayList<Integer>();
                    nrList = new ArrayList<ResultRangeNumeric>();
                    firstTypeId = 0;
                    numDefault = 0;
                    count = values.size();
                    typeExceptionThrown = false;

                    for (int j = 0; j < count; j++ ) {
                        v = values.get(j);
                        typeId = v.getTypeId();
                        value = v.getValue();
                        try {
                            auxFieldValue.validate(v, data.getAnalyteName());
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }

                        try {
                            if (DataBaseUtil.isSame(Constants.dictionary().AUX_DEFAULT, typeId)) {
                                numDefault++ ;
                                if (numDefault == 2) {
                                    throw new InconsistencyException(Messages.get()
                                                                             .aux_moreThanOneDefaultException(data.getAnalyteName()));
                                } else if (count == 1 && numDefault == 1 && j > 0) {
                                    throw new InconsistencyException(Messages.get()
                                                                             .aux_defaultWithNoOtherTypeException(data.getAnalyteName()));
                                } else if (numDefault > 2) {
                                    continue;
                                }
                            } else if ( !DataBaseUtil.isEmpty(typeId)) {
                                if (DataBaseUtil.isSame(0, firstTypeId))
                                    //
                                    // Assign the first non-null selected type
                                    // to
                                    // firstTypeId if the type is not "Default".
                                    //
                                    firstTypeId = typeId;
                            }

                            if (DataBaseUtil.isDifferent(firstTypeId, typeId) &&
                                DataBaseUtil.isDifferent(Constants.dictionary().AUX_DEFAULT, typeId)) {
                                //
                                // If dissimilar types have been selected for
                                // different
                                // aux field values for an aux field than they
                                // cannot be
                                // of more than two kinds. Also one of the kind
                                // must be
                                // the type "Default".
                                //
                                if ( !typeExceptionThrown) {
                                    typeExceptionThrown = true;
                                    throw new InconsistencyException(Messages.get()
                                                                             .aux_moreThanOneTypeException(data.getAnalyteName()));
                                }
                            }

                            if (DataBaseUtil.isSame(Constants.dictionary().AUX_NUMERIC, typeId)) {
                                nr = new ResultRangeNumeric();
                                nr.setRange(value);
                                for (int i = 0; i < nrList.size(); i++ ) {
                                    lr = nrList.get(i);
                                    if (lr.intersects(nr))
                                        throw new InconsistencyException(Messages.get()
                                                                                 .aux_numRangeOverlapException(data.getAnalyteName(),
                                                                                                               v.getValue()));
                                }

                                nrList.add(nr);
                            } else if (DataBaseUtil.isSame(Constants.dictionary().AUX_DICTIONARY,
                                                           typeId)) {
                                entryId = Integer.parseInt(value);
                                if (entryId == null)
                                    throw new ParseException(Messages.get()
                                                                     .aux_illegalDictEntryException(data.getAnalyteName(),
                                                                                                    v.getValue()));

                                if ( !dictList.contains(entryId))
                                    dictList.add(entryId);
                                else
                                    throw new InconsistencyException(Messages.get()
                                                                             .aux_dictEntryNotUniqueException(data.getAnalyteName()));
                            }
                        } catch (ParseException pe) {
                            e.add(new FormErrorException(pe.getMessage()));
                        } catch (InconsistencyException ie) {
                            e.add(new FormErrorException(ie.getMessage()));
                        }
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }
}
