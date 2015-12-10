/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import static org.openelis.manager.AnalyteParameterManager1Accessor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.AnalyteParameterManager1.AnalyteCombo;
import org.openelis.manager.QcManager;
import org.openelis.manager.TestManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class AnalyteParameterManager1Bean {

    @EJB
    private LockBean             lock;

    @EJB
    private AnalyteParameterBean analyteParameter;

    @EJB
    private TestManagerBean      testManager;

    @EJB
    private QcManagerBean        qcManager;

    @EJB
    private DictionaryCacheBean  dictionaryCache;

    /**
     * Returns analyte parameter manager for specified reference id and
     * reference table id; if both arguments are not null then the manager
     * contains the analytes defined in the reference record e.g. test, but no
     * parameters; null arguments are used to refresh the screen in Add state;
     * this is because reference table or reference id can be changed in that
     * state and the data on the screen from the previous record may be invalid
     */
    public AnalyteParameterManager1 getInstance(Integer referenceId, Integer referenceTableId) throws Exception {
        int i;
        StringBuilder sb;
        TestViewDO test;
        QcViewDO qc;
        TestManager tm;
        QcManager qcm;
        QcAnalyteViewDO qca;
        AnalyteParameterManager1 apm;
        HashSet<Integer> analyteIds;

        apm = new AnalyteParameterManager1();
        setReferenceId(apm, referenceId);
        setReferenceTableId(apm, referenceTableId);
        if (referenceId == null || referenceTableId == null)
            return apm;

        /*
         * set the reference name and description; for inactive tests, show the
         * active begin and end dates; for qcs, show the id next to the name
         */
        tm = null;
        qcm = null;
        if (Constants.table().TEST.equals(referenceTableId)) {
            tm = testManager.fetchById(referenceId);
            test = tm.getTest();
            sb = new StringBuilder();
            sb.append(test.getName()).append(", ").append(test.getMethodName());
            if ("N".equals(test.getIsActive())) {
                sb.append(" [")
                  .append(test.getActiveBegin())
                  .append("..")
                  .append(test.getActiveEnd())
                  .append("]");
            }
            setReferenceName(apm, sb.toString());
        } else if (Constants.table().QC.equals(referenceTableId)) {
            qcm = qcManager.fetchById(referenceId);
            qc = qcm.getQc();

            sb = new StringBuilder();
            sb.append(qc.getName()).append(" ").append("(").append(qc.getId()).append(")");

            setReferenceName(apm, sb.toString());
        } else if (Constants.table().PROVIDER.equals(referenceTableId)) {
        }

        /*
         * add AnalyteCombos without any sample type, unit or parameters, to the
         * manager for the analytes in the test or qc definition; any
         * AnalyteCombos with sample type and/or unit will be added later if
         * there are any parameters for the analytes; don't add AnalyteCombos
         * for an analyte more than once even if it is in the definition
         * multiple times; for tests, consider only row analytes
         */
        analyteIds = new HashSet<Integer>();
        if (tm != null) {
            for (ArrayList<TestAnalyteViewDO> row : tm.getTestAnalytes().getAnalytes()) {
                for (TestAnalyteViewDO ta : row) {
                    if ("Y".equals(ta.getIsColumn()) || analyteIds.contains(ta.getAnalyteId()))
                        continue;
                    addCombination(apm, new AnalyteCombo(apm.getNextUID(),
                                                         ta.getAnalyteId(),
                                                         ta.getAnalyteName(),
                                                         null,
                                                         null,
                                                         null));
                    analyteIds.add(ta.getAnalyteId());
                }
            }
        } else if (qcm != null) {
            for (i = 0; i < qcm.getAnalytes().count(); i++ ) {
                qca = qcm.getAnalytes().getAnalyteAt(i);
                if (analyteIds.contains(qca.getAnalyteId()))
                    continue;
                addCombination(apm, new AnalyteCombo(apm.getNextUID(),
                                                     qca.getAnalyteId(),
                                                     qca.getAnalyteName(),
                                                     null,
                                                     null,
                                                     null));
                analyteIds.add(qca.getAnalyteId());
            }
        }

        return apm;
    }

    /**
     * Returns analyte parameter manager for specified reference id and
     * reference table id; parameters are grouped in the manager by their
     * analyte, sample type and unit; they are ordered the same way as the
     * analytes in the original record e.g. test or qc
     */
    public AnalyteParameterManager1 fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                       Integer referenceTableId) throws Exception {
        AnalyteParameterManager1 apm;
        ArrayList<AnalyteParameterViewDO> params;

        /*
         * it can happen on the screen that all parameters of a test or qc have
         * been removed, but it's still showing in the left panel; if the user
         * clicks its row and this exception is not thrown, a manager with only
         * analytes and no parameters will be returned; that's erroneous because
         * there are no parameters for the record so nothing should be returned
         */
        params = analyteParameter.fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
        if (params.isEmpty())
            throw new NotFoundException();

        apm = getInstance(referenceId, referenceTableId);
        addParameters(apm, params);
        return apm;
    }

    /**
     * Returns a locked analyte parameter manager with specified reference id
     * and reference table id. The reference table used to create the lock is
     * analyte parameter and not the passed value because we don't want to lock
     * the original record e.g. test or qc. This may end up locking the
     * parameters of a test if it has the same id as a qc and vice-versa, but
     * this is expected to be a very rare occurrence.
     */
    @RolesAllowed("analyteparameter-update")
    public AnalyteParameterManager1 fetchForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        lock.lock(Constants.table().ANALYTE_PARAMETER, referenceId);
        return fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
    }

    /**
     * Unlocks and returns an analyte parameter manager with specified reference
     * id and reference table id. The reference table used to unlock is analyte
     * parameter and not the passed reference table.
     * 
     * @see fetchForUpdate
     */
    @RolesAllowed({"analyteparameter-add", "analyteparameter-update"})
    public AnalyteParameterManager1 unlock(Integer referenceId, Integer referenceTableId) throws Exception {
        lock.unlock(Constants.table().ANALYTE_PARAMETER, referenceId);
        return fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
    }

    /**
     * Adds/updates the sample and related records in the database. The records
     * are validated before add/update and the sample record must have a lock
     * record if it has an id. Returns null if the manager has no parameters and
     * this is Update mode; this way the screen can be blanked and not give the
     * user any opportunity to lock the record again; otherwise returns the
     * passed manager.
     */
    @RolesAllowed({"analyteparameter-add", "analyteparameter-update"})
    public AnalyteParameterManager1 update(AnalyteParameterManager1 apm) throws Exception {
        int i;
        boolean isUpdate, hasParams;
        ValidationErrorsList e;

        /*
         * for validating the lock, it needs to be known whether the manager is
         * being updated in Update or Add mode; if either the lists of
         * parameters in the AnalyteCombos or the list of removed parameters
         * contain any DOs for existing records, it means that it's Update mode;
         * otherwise it's Add mode
         */
        isUpdate = false;
        if (getCombinations(apm) != null) {
            findExisting: for (i = 0; i < getCombinations(apm).size(); i++ ) {
                if (getParameters(apm, i) == null)
                    continue;
                for (AnalyteParameterViewDO data : getParameters(apm, i)) {
                    if (data.getId() > 0) {
                        isUpdate = true;
                        break findExisting;
                    }
                }
            }
        }

        if ( !isUpdate && getRemoved(apm) != null && getRemoved(apm).size() > 0)
            isUpdate = true;

        e = new ValidationErrorsList();
        try {
            validate(apm, isUpdate);
        } catch (Exception err) {
            DataBaseUtil.mergeException(e, err);
        }

        if (e.size() > 0)
            throw e;

        /*
         * check the lock
         */
        if (isUpdate)
            lock.validateLock(Constants.table().ANALYTE_PARAMETER, getReferenceId(apm));

        /*
         * go through remove list and delete all the unwanted records
         */
        if (getRemoved(apm) != null) {
            for (AnalyteParameterViewDO data : getRemoved(apm))
                analyteParameter.delete(data);
        }

        /*
         * add/update parameters
         */
        hasParams = false;
        if (getCombinations(apm) != null) {
            for (i = 0; i < getCombinations(apm).size(); i++ ) {
                if (getParameters(apm, i) == null)
                    continue;
                hasParams = true;
                for (AnalyteParameterViewDO data : getParameters(apm, i)) {
                    if (data.getId() < 0)
                        analyteParameter.add(data);
                    else if (data.isChanged())
                        analyteParameter.update(data);
                }
            }
        }

        if (isUpdate)
            lock.unlock(Constants.table().ANALYTE_PARAMETER, getReferenceId(apm));

        return hasParams ? apm : null;
    }

    /**
     * Adds AnalyteCombos and/or parameters for the analytes defined in the
     * original record e.g. test or qc; assumes that default (placeholder)
     * AnalyteCombos have been added to the manager for all analytes and ordered
     * the same way as in the original record; fills the default AnalyteCombos
     * with the parameters for the first combination of sample type and/or unit
     * found for an analyte; adds new AnalyteCombos for any other sample type or
     * unit
     */
    private void addParameters(AnalyteParameterManager1 apm,
                               ArrayList<AnalyteParameterViewDO> params) throws Exception {
        int i;
        boolean first;
        AnalyteParameterViewDO data, prev;
        AnalyteCombo ac;
        ArrayList<AnalyteParameterViewDO> row;
        ArrayList<ArrayList<AnalyteParameterViewDO>> table;
        HashMap<Integer, ArrayList<ArrayList<AnalyteParameterViewDO>>> apMap;

        apMap = new HashMap<Integer, ArrayList<ArrayList<AnalyteParameterViewDO>>>();

        prev = null;
        table = null;
        row = null;
        /*
         * create a mapping where the key is the analyte id and the value is a
         * two dimension list; the first dimension groups all combinations of
         * sample type and/or unit for an analyte; the second dimension groups
         * all parameters for a specific combination
         */
        for (AnalyteParameterViewDO p : params) {
            /*
             * the list of all combinations for this analyte
             */
            table = apMap.get(p.getAnalyteId());
            if (table == null) {
                table = new ArrayList<ArrayList<AnalyteParameterViewDO>>();
                apMap.put(p.getAnalyteId(), table);
            }
            /*
             * 
             * the list of parameters for a specific combination
             */
            if (prev == null || !DataBaseUtil.isSame(prev.getAnalyteId(), p.getAnalyteId()) ||
                !DataBaseUtil.isSame(prev.getTypeOfSampleId(), p.getTypeOfSampleId()) ||
                !DataBaseUtil.isSame(prev.getUnitOfMeasureId(), p.getUnitOfMeasureId())) {
                row = new ArrayList<AnalyteParameterViewDO>();
                table.add(row);
            }
            row.add(p);
            prev = p;
        }

        /*
         * go through the default AnalyteCombos in the manager; set paramaters
         * for the first combination of sample type and/or unit found for an
         * analyte in its default AnalyteCombo; add new AnalyteCombos to the
         * manager for any other sample type and/or unit
         */
        i = 0;
        while (i < getCombinations(apm).size()) {
            ac = getCombinations(apm).get(i);
            table = apMap.get(ac.getAnalyteId());
            if (table != null) {
                first = true;
                for (ArrayList<AnalyteParameterViewDO> row1 : table) {
                    data = row1.get(0);
                    if (first) {
                        first = false;
                        ac.setTypeOfSampleId(data.getTypeOfSampleId());
                        ac.setUnitOfMeasureId(data.getUnitOfMeasureId());
                    } else {
                        ac = new AnalyteCombo(apm.getNextUID(),
                                              data.getAnalyteId(),
                                              data.getAnalyteName(),
                                              data.getTypeOfSampleId(),
                                              data.getUnitOfMeasureId(),
                                              null);
                        addCombination(apm, ac, ++i);
                    }
                    setParameters(apm, row1, i);
                }
            }
            i++ ;
        }
    }

    /**
     * Validates the analyte paramter manager for add or update. The routine
     * throws a list of exceptions/warnings listing all the problems for the
     * manager.
     */
    private void validate(AnalyteParameterManager1 apm, boolean isUpdate) throws Exception {
        int i;
        boolean hasParams;
        String combo, refName;
        AnalyteCombo ac;
        AnalyteParameterViewDO prev;
        ValidationErrorsList e;
        Datetime pab, dae;
        HashSet<String> combos;
        ArrayList<AnalyteParameterViewDO> params;

        e = new ValidationErrorsList();
        if (getReferenceTableId(apm) == null)
            e.add(new FormErrorException(Messages.get().analyteParameter_typeRequiredException()));

        if (getReferenceId(apm) == null)
            e.add(new FormErrorException(Messages.get().analyteParameter_nameRequiredException()));

        hasParams = false;
        if (getCombinations(apm) != null) {
            combos = new HashSet<String>();
            for (i = 0; i < getCombinations(apm).size(); i++ ) {
                ac = getCombinations(apm).get(i);
                combo = getCombination(ac);
                /*
                 * a combination of analyte, sample type and/or unit must not be
                 * repeated
                 */
                if (combos.contains(combo))
                    e.add(new FormErrorException(Messages.get()
                                                         .analyteParameter_repeatedComboException(combo)));
                else
                    combos.add(combo);

                if (getParameters(apm, i) == null)
                    continue;

                prev = null;
                hasParams = true;
                for (AnalyteParameterViewDO data : getParameters(apm, i)) {
                    /*
                     * begin and end dates are required for a parameter and at
                     * least one of P1, P2, P3 must be specified
                     */
                    if (data.isChanged()) {
                        try {
                            analyteParameter.validate(data, combo);
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }
                    }

                    /*
                     * the begin date for the previous parameter in the list,
                     * (which is more recent than the current parameter) must be
                     * after the end date for the current one
                     */
                    if (prev != null) {
                        pab = prev.getActiveBegin();
                        dae = data.getActiveEnd();
                        if (pab != null && dae != null && !DataBaseUtil.isAfter(pab, dae))
                            e.add(new FormErrorException(Messages.get()
                                                                 .analyteParameter_beginDateAfterPrevEndDateException(combo)));
                    }
                    prev = data;
                }
            }
        }

        if ( !isUpdate) {
            /*
             * the user is trying to commit in Add mode; show an error if some
             * parameters already exist in the database for the record; don't
             * allow committing without any parameters
             */
            refName = DataBaseUtil.toString(getReferenceName(apm));
            if (hasParams) {
                /*
                 * a dynamic query is not used here because the "query" method
                 * in AnalyteParameterBean uses reference name and not reference
                 * id; to get the name, the test or qc will have to be fetched
                 * here first; the manager doesn't have just the name, but a
                 * description e.g. name and id for qcs
                 */
                params = analyteParameter.fetchByReferenceIdReferenceTableId(getReferenceId(apm),
                                                                             getReferenceTableId(apm),
                                                                             1);
                if ( !params.isEmpty())
                    e.add(new FormErrorException(Messages.get()
                                                         .analyteParameter_parametersExistException(refName)));
            } else {
                e.add(new FormErrorException(Messages.get()
                                                     .analyteParameter_recordHasNoParametersException(refName)));
            }
        }

        if (e.size() > 0)
            throw e;
    }

    /**
     * Returns a string created by concatinating the analyte name and dictionary
     * entries for the passed combo's sample type and unit
     */
    private String getCombination(AnalyteCombo ac) throws Exception {
        String sampleType, unit, su;

        sampleType = null;
        unit = null;
        if (ac.getTypeOfSampleId() != null)
            sampleType = dictionaryCache.getById(ac.getTypeOfSampleId()).getEntry();

        if (ac.getUnitOfMeasureId() != null)
            unit = dictionaryCache.getById(ac.getUnitOfMeasureId()).getEntry();

        su = DataBaseUtil.concatWithSeparator(sampleType, ", ", unit);
        /*
         * if both sample type and unit are null, concatWithSeparator() returns
         * an empty string, not null; trim() is used below to pass a null, so
         * that the returned string doesn't contain the delimiter
         */
        return DataBaseUtil.concatWithSeparator(ac.getAnalyteName(), ", ", DataBaseUtil.trim(su));
    }
}