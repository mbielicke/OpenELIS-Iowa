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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openelis.bean.DictionaryBean;
import org.openelis.bean.DictionaryCacheBean;
import org.openelis.bean.TestResultBean;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.exception.ParseException;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.GridFieldErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;
import org.openelis.utils.EJBFactory;

public class TestResultManagerProxy {

    public TestResultManager fetchByTestId(Integer testId) throws Exception {
        TestResultManager trm;
        ArrayList<ArrayList<TestResultViewDO>> list;

        list = EJBFactory.getTestResult().fetchByTestId(testId);
        trm = TestResultManager.getInstance();
        trm.setTestId(testId);
        trm.setResults(list);

        return trm;
    }

    public TestResultManager add(TestResultManager man, HashMap<Integer, Integer> idMap) throws Exception {
        int i, j, size, negId;
        TestResultBean rl;
        TestResultViewDO data;

        rl = EJBFactory.getTestResult();

        for (i = 0; i < man.groupCount(); i++ ) {
            size = man.getResultGroupSize(i + 1);
            for (j = 0; j < size; j++ ) {
                data = man.getResultAt(i + 1, j);
                negId = data.getId();
                data.setTestId(man.getTestId());
                data.setResultGroup(i + 1);
                data.setSortOrder(j);

                rl.add(data);
                idMap.put(negId, data.getId());
            }
        }
        return man;
    }

    public TestResultManager update(TestResultManager man, HashMap<Integer, Integer> idMap) throws Exception {
        int i, j, size, negId;
        TestResultBean rl;
        TestResultViewDO data;

        rl = EJBFactory.getTestResult();

        for (i = 0; i < man.deleteCount(); i++ )
            rl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.groupCount(); i++ ) {
            size = man.getResultGroupSize(i + 1);
            for (j = 0; j < size; j++ ) {
                data = man.getResultAt(i + 1, j);
                data.setResultGroup(i + 1);
                data.setSortOrder(j);
                negId = data.getId();
                if (negId < 0) {
                    data.setTestId(man.getTestId());
                    rl.add(data);
                    idMap.put(negId, data.getId());
                } else {
                    rl.update(data);
                }
            }
        }
        return man;
    }

    public void validate(TestResultManager trm, TestTypeOfSampleManager ttsm,
                         HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap) throws Exception {
        int i, j, k, defCount, size;
        boolean alphaPresent, alphaErrorAdded, defErrorAdded;
        ValidationErrorsList list;
        TestResultViewDO data, tmpData;
        Integer typeId, unitId, entryId;
        String value, fieldName, unitText;
        ResultRangeNumeric nr;
        ResultRangeTiter tr;
        HashMap<Integer, List<ResultRangeTiter>> trMap;
        HashMap<Integer, List<ResultRangeNumeric>> nrMap;
        HashMap<Integer, List<TestResultViewDO>> unitTypeMap;
        List<Integer> dictList;
        List<TestResultViewDO> resDataList, typeDataList;
        DictionaryCacheBean dcl;
        TestResultBean rl;
        Set<Integer> set;
        Iterator<Integer> iter;

        list = new ValidationErrorsList();
        value = null;
        dcl = EJBFactory.getDictionaryCache();
        rl = EJBFactory.getTestResult();

        trMap = new HashMap<Integer, List<ResultRangeTiter>>();
        nrMap = new HashMap<Integer, List<ResultRangeNumeric>>();
        dictList = new ArrayList<Integer>();
        typeDataList = new ArrayList<TestResultViewDO>();
        unitTypeMap = new HashMap<Integer, List<TestResultViewDO>>();

        for (i = 0; i < trm.groupCount(); i++ ) {
            trMap.clear();
            nrMap.clear();
            dictList.clear();
            unitTypeMap.clear();
            typeDataList.clear();
            resDataList = new ArrayList<TestResultViewDO>();
            resGrpRsltMap.put(i + 1, resDataList);
            alphaPresent = false;
            alphaErrorAdded = false;
            defErrorAdded = false;

            for (j = 0; j < trm.getResultGroupSize(i + 1); j++ ) {
                data = trm.getResultAt(i + 1, j);
                value = data.getValue();
                typeId = data.getTypeId();
                unitId = data.getUnitOfMeasureId();

                resDataList.add(data);

                //
                // units need to be valid for every result type because
                // their use is dependent on the unit
                //
                if ( !unitIsValid(unitId, ttsm.getTypes())) {
                    unitText = dcl.getById(unitId).getEntry();

                    list.add(new GridFieldErrorException(Messages.get().illegalUnitOfMeasureException(unitText),
                                                         i,
                                                         j,
                                                         TestMeta.getResultUnitOfMeasureId(),
                                                         "resultTable"
                                                         ));
                    continue;
                }

                fieldName = TestMeta.getResultValue();

                try {
                    rl.validate(data);
                } catch (Exception e) {
                    DataBaseUtil.mergeException(list, e, "resultTable", i, j);
                    continue;
                }

                try {
                    typeDataList = unitTypeMap.get(unitId);
                    if (typeDataList == null)
                        typeDataList = new ArrayList<TestResultViewDO>();

                    typeDataList.add(data);
                    unitTypeMap.put(unitId, typeDataList);

                    if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_NUMERIC,
                                            typeId)) {
                        nr = new ResultRangeNumeric();
                        nr.setRange(value);
                        addNumericIfNoOverLap(nrMap, unitId, nr);
                    } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_TITER,
                                                   typeId)) {
                        tr = new ResultRangeTiter();
                        tr.setRange(value);
                        addTiterIfNoOverLap(trMap, unitId, tr);
                    } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DICTIONARY,
                                                   typeId)) {
                        entryId = Integer.parseInt(value);
                        if (entryId == null)
                            throw new ParseException(Messages.get().illegalDictEntryException());

                        if ( !dictList.contains(entryId)) {
                            dictList.add(entryId);
                        } else {
                            //
                            // this exception is added directly to the list here
                            // as opposed to getting thrown and caught
                            // as in all the previous cases because we want its
                            // message to show at the bottom of Test screen
                            // and not in the table
                            //
                            list.add(new FieldErrorException(Messages.get().testDictEntryNotUniqueException(String.valueOf(i + 1)),null));
                        }
                    } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DEFAULT,
                                                   typeId)) {
                        //
                        // here we try to check whether this result group
                        // has more than one value of type default for a given
                        // unit
                        // and if it is the case then an exception is added for
                        // this DO to the list of exceptions
                        //
                        defCount = 0;
                        size = typeDataList.size();
                        for (k = 0; k < size; k++ ) {
                            tmpData = typeDataList.get(k);
                            if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DEFAULT,
                                                    tmpData.getTypeId()))
                                defCount++ ;

                            if (defCount > 1 && !defErrorAdded) {
                                fieldName = TestMeta.getResultTypeId();
                                //
                                // this exception is added directly to the list
                                // here as opposed to getting thrown and caught
                                // as in all the previous cases because we want
                                // its message to show at the bottom of Test
                                // screen
                                // and not in the table
                                //
                                list.add(new FieldErrorException(Messages.get().testMoreThanOneDefaultForUnitException(String.valueOf(i + 1)), null));

                                defErrorAdded = true;
                            }
                        }
                    } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER,
                                                   typeId) ||
                               DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER,
                                                   typeId) ||
                               DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED,
                                                   typeId)) {
                        if (alphaPresent && !alphaErrorAdded) {
                            fieldName = TestMeta.getResultTypeId();
                            //
                            // this exception is added directly to the list here
                            // as opposed to getting thrown and caught
                            // as in all the previous cases because we want its
                            // message to show at the bottom of Test screen
                            // and not in the table
                            //
                            list.add(new FieldErrorException(Messages.get().testMoreThanOneAlphaTypeException(String.valueOf(i + 1)), null));
                            alphaErrorAdded = true;
                        }
                        alphaPresent = true;
                    }
                } catch (ParseException ex) {
                    list.add(new GridFieldErrorException(ex.getMessage(),
                                                         i,
                                                         j,
                                                         fieldName,
                                                         "resultTable"));

                } catch (InconsistencyException ex) {
                    list.add(new GridFieldErrorException(ex.getMessage(),
                                                         i,
                                                         j,
                                                         fieldName,
                                                         "resultTable"));

                }
            }

            set = unitTypeMap.keySet();
            iter = set.iterator();

            //
            // Here we try to check whether for each result group and for a
            // given
            // unit it is the case that there is a value of type default but no
            // value of any other type. We have to do this check here as opposed
            // to in the loop above because we need to have the information
            // about
            // the whole result group before making any decision about whether
            // or
            // not this condition is true and this will be considerably
            // difficult
            // to do in the loop above because there we at any moment/ have the
            // information only of the records encountered upto a certain point
            // in the result group.
            //
            while (iter.hasNext()) {
                unitId = iter.next();
                typeDataList = unitTypeMap.get(unitId);
                size = typeDataList.size();
                for (k = 0; k < size; k++ ) {
                    tmpData = typeDataList.get(k);
                    if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DEFAULT,
                                            tmpData.getTypeId()) &&
                        size == 1) {
                        tmpData = typeDataList.get(0);
                        list.add(new FieldErrorException(Messages.get().testDefaultWithNoOtherTypeException(String.valueOf(tmpData.getResultGroup())),
                                                         null));
                    }
                }

            }
        }

        if (list.size() > 0)
            throw list;
    }

    private void addTiterIfNoOverLap(HashMap<Integer, List<ResultRangeTiter>> trMap,
                                     Integer unitId, ResultRangeTiter tr) throws InconsistencyException {
        ResultRangeTiter lr;
        List<ResultRangeTiter> trList;

        trList = trMap.get(unitId);
        if (trList != null) {
            for (int i = 0; i < trList.size(); i++ ) {
                lr = trList.get(i);
                if (lr.intersects(tr))
                    throw new InconsistencyException(Messages.get().testTiterRangeOverlapException());
            }
            trList.add(tr);
        } else {
            trList = new ArrayList<ResultRangeTiter>();
            trList.add(tr);
            trMap.put(unitId, trList);
        }
    }

    private void addNumericIfNoOverLap(HashMap<Integer, List<ResultRangeNumeric>> nrMap,
                                       Integer unitId, ResultRangeNumeric nr) throws InconsistencyException {
        ResultRangeNumeric lr;
        List<ResultRangeNumeric> nrList;

        nrList = nrMap.get(unitId);
        if (nrList != null) {
            for (int i = 0; i < nrList.size(); i++ ) {
                lr = nrList.get(i);
                if (lr.intersects(nr))
                    throw new InconsistencyException(Messages.get().testNumRangeOverlapException());
            }
            nrList.add(nr);
        } else {
            nrList = new ArrayList<ResultRangeNumeric>();
            nrList.add(nr);
            nrMap.put(unitId, nrList);
        }
    }

    /**
     * This method checks to see if a unit of measure (resultUnitId) assigned to
     * a test result belongs to the list of units added to the test
     */
    private boolean unitIsValid(Integer resultUnitId,
                                List<TestTypeOfSampleDO> sampleTypeDOList) {
        int i, numMatch;
        TestTypeOfSampleDO sampleDO;
        Integer unitId;

        numMatch = 0;

        if (resultUnitId == null)
            return true;

        if (sampleTypeDOList == null) {
            return false;
        } else {
            for (i = 0; i < sampleTypeDOList.size(); i++ ) {
                sampleDO = sampleTypeDOList.get(i);
                unitId = sampleDO.getUnitOfMeasureId();
                if (unitId != null && unitId.equals(resultUnitId)) {
                    numMatch++ ;
                }
            }
        }

        return (numMatch != 0);
    }

}
