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
import java.util.List;

import javax.naming.InitialContext;

import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.NumericRange;
import org.openelis.utilcommon.TestResultValidator;
import org.openelis.utilcommon.TiterRange;

public class TestResultManagerProxy {

    private static final TestMetaMap meta = new TestMetaMap();

    public TestResultManager fetchByTestId(Integer testId) throws Exception {
        TestResultManager trm;
        ArrayList<ArrayList<TestResultViewDO>> results;

        results = local().fetchByTestId(testId);
        trm = TestResultManager.getInstance();
        trm.setTestId(testId);
        trm.setResults(results);

        return trm;
    }

    public TestResultManager add(TestResultManager man,
                                 HashMap<Integer, Integer> idMap) throws Exception {
        TestResultLocal rl;
        TestResultViewDO testResult;
        int i, j, size, negId;

        rl = local();

        for (i = 0; i < man.groupCount(); i++ ) {
            size = man.getResultGroupSize(i + 1);
            for (j = 0; j < size; j++ ) {
                testResult = man.getResultAt(i + 1, j);
                negId = testResult.getId();
                testResult.setTestId(man.getTestId());
                testResult.setResultGroup(i + 1);
                testResult.setSortOrder(j);

                rl.add(testResult);
                idMap.put(negId, testResult.getId());
            }
        }
        return man;
    }       

    public TestResultManager update(TestResultManager man,
                                    HashMap<Integer, Integer> idMap) throws Exception {
        TestResultLocal rl;
        TestResultViewDO testResult;
        int i, j, size, negId;

        rl = local();

        for (i = 0; i < man.deleteCount(); i++ )
            rl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.groupCount(); i++ ) {
            size = man.getResultGroupSize(i + 1);
            for (j = 0; j < size; j++ ) {
                testResult = man.getResultAt(i + 1, j);
                testResult.setResultGroup(i + 1);
                testResult.setSortOrder(j);
                negId = testResult.getId();
                if (negId < 0) {
                    testResult.setTestId(man.getTestId());
                    rl.add(testResult);
                    idMap.put(negId, testResult.getId());
                } else {
                    rl.update(testResult);
                }
            }
        }
        return man;
    }
    
    public void validate(TestResultManager trm,TestTypeOfSampleManager ttsm,
                         HashMap<Integer, List<Integer>> resGrpRsltMap) throws Exception{
        ValidationErrorsList list;
        TestResultViewDO resDO;
        Integer numId, dictId, titerId, typeId, dateId, dtId, timeId, unitId, entryId, defId;
        int i, j;
        String value, fieldName, unitText;
        boolean hasDateType;
        NumericRange nr;
        TiterRange tr;
        HashMap<Integer, List<TiterRange>> trMap;
        HashMap<Integer, List<NumericRange>> nrMap;
        List<Integer> dictList, unitsWithDefault;
        List<Integer> resIdList;
        ArrayList<ArrayList<TestResultViewDO>> results;
        DictionaryLocal dl;
        TestResultLocal rl;
        
        list = new ValidationErrorsList();                
        value = null;
        dl = dictLocal();
        results = trm.getResults();
        rl = local();
        
        dictId = (dl.fetchBySystemName("test_res_type_dictionary")).getId();
        numId = (dl.fetchBySystemName("test_res_type_numeric")).getId();
        titerId = (dl.fetchBySystemName("test_res_type_titer")).getId();
        dateId = (dl.fetchBySystemName("test_res_type_date")).getId();
        dtId = (dl.fetchBySystemName("test_res_type_date_time")).getId();
        timeId = (dl.fetchBySystemName("test_res_type_time")).getId();
        defId = (dl.fetchBySystemName("test_res_type_default")).getId();

        trMap = new HashMap<Integer, List<TiterRange>>();
        nrMap = new HashMap<Integer, List<NumericRange>>();
        dictList = new ArrayList<Integer>();
        unitsWithDefault = new ArrayList<Integer>();
        hasDateType = false;
        

        for (i = 0; i < results.size(); i++ ) {
            trMap.clear();
            nrMap.clear();
            dictList.clear();
            hasDateType = false;
            unitsWithDefault.clear();
            resIdList = new ArrayList<Integer>();        
            resGrpRsltMap.put(i+1, resIdList);
            
            for (j = 0; j < results.get(i).size(); j++ ) {
                resDO = results.get(i).get(j);
                value = resDO.getValue();
                typeId = resDO.getTypeId();
                unitId = resDO.getUnitOfMeasureId();    
                
                resIdList.add(resDO.getId());
                
                //
                // units need to be valid for every result type because
                // their use is dependent on the unit
                //
                if (!unitIsValid(unitId, ttsm.getTypes())) {                    
                    unitText = dl.fetchById(unitId).getEntry();
                    
                    list.add(new GridFieldErrorException("illegalUnitOfMeasureException", i, j,
                                                                  meta.TEST_RESULT
                                                                          .getUnitOfMeasureId(),
                                                                  "resultTable",unitText));    
                    continue;
                }

                fieldName = meta.TEST_RESULT.getValue();
                
                try {                    
                    rl.validate(resDO);                    
                } catch(Exception e) {
                    DataBaseUtil.mergeException(list, e, "resultTable", i, j);
                    continue;
                }

                try {
                    if (numId.equals(typeId)) {
                        nr = new NumericRange(value);
                        addNumericIfNoOverLap(nrMap, unitId, nr);
                    } else if (titerId.equals(typeId)) {
                        tr = new TiterRange(value);
                        addTiterIfNoOverLap(trMap, unitId, tr);
                    } else if (dateId.equals(typeId)) {
                        TestResultValidator.validateDate(value);
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (dtId.equals(typeId)) {                        
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (timeId.equals(typeId)) {                        
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (dictId.equals(typeId)) {
                        entryId = Integer.parseInt(value);
                        if (entryId == null)
                            throw new ParseException("illegalDictEntryException");

                        if (!dictList.contains(entryId))
                            dictList.add(entryId);
                        else
                            throw new InconsistencyException("testDictEntryNotUniqueException");
                    } else if (defId.equals(typeId)) {
                        if (unitsWithDefault.indexOf(unitId) == -1)
                            unitsWithDefault.add(unitId);
                        else
                            throw new InconsistencyException("testMoreThanOneDefaultForUnitException");
                    } 
                } catch (ParseException ex) {
                    list.add(new GridFieldErrorException(ex.getMessage(), i, j, fieldName,
                                                                  "resultTable"));

                } catch (InconsistencyException ex) {
                    list.add(new GridFieldErrorException(ex.getMessage(), i, j, fieldName,
                                                                  "resultTable"));

                }
            }
        }
                        
        if (list.size() > 0)
            throw list;
    }  
    

    private TestResultLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestResultLocal)ctx.lookup("openelis/TestResultBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private void addTiterIfNoOverLap(HashMap<Integer, 
                                     List<TiterRange>> trMap,Integer unitId,
                                     TiterRange tr) throws InconsistencyException {
        TiterRange lr;
        List<TiterRange> trList;

        trList = trMap.get(unitId);
        if (trList != null) {
            for (int i = 0; i < trList.size(); i++ ) {
                lr = trList.get(i);
                if (lr.isOverlapping(tr))
                    throw new InconsistencyException("testTiterRangeOverlapException");
            }
            trList.add(tr);
        } else {
            trList = new ArrayList<TiterRange>();
            trList.add(tr);
            trMap.put(unitId, trList);
        }
    }

    private void addNumericIfNoOverLap(HashMap<Integer,
                                       List<NumericRange>> nrMap,Integer unitId,
                                       NumericRange nr) throws InconsistencyException {
        NumericRange lr;
        List<NumericRange> nrList;

        nrList = nrMap.get(unitId);
        if (nrList != null) {
            for (int i = 0; i < nrList.size(); i++ ) {
                lr = nrList.get(i);
                if (lr.isOverlapping(nr))
                    throw new InconsistencyException("testNumRangeOverlapException");
            }
            nrList.add(nr);
        } else {
            nrList = new ArrayList<NumericRange>();
            nrList.add(nr);
            nrMap.put(unitId, nrList);
        }
    }
    
    /**
     * This method checks to see if a unit of measure (resultUnitId) assigned to
     * a test result belongs to the list of units added to the test
     */
    private boolean unitIsValid(Integer resultUnitId, List<TestTypeOfSampleDO> sampleTypeDOList) {
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
