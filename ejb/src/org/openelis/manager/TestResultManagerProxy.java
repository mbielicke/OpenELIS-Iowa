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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.DictionaryDO;
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
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;
import org.openelis.utilcommon.TestResultValidator;

public class TestResultManagerProxy {

    private static final TestMetaMap meta = new TestMetaMap();
    
    private static int               typeDict, typeNumeric, typeTiter, typeDate,
                                     typeDateTime, typeTime, typeDefault; 
    
    private static final Logger      log  = Logger.getLogger(TestResultManagerProxy.class.getName());
    
    public TestResultManagerProxy() {
        DictionaryDO data;
        DictionaryLocal dl;
        
        dl = dictLocal();
        
        try {
            data = dl.fetchBySystemName("test_res_type_dictionary");
            typeDict = data.getId();
        } catch (Throwable e) {
            typeDict = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_dictionary'", e);
        }
        
        try {
            data = dl.fetchBySystemName("test_res_type_numeric");
            typeNumeric = data.getId();
        } catch (Throwable e) {
            typeNumeric = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_numeric'", e);
        }
        
        try {
            data = dl.fetchBySystemName("test_res_type_titer");
            typeTiter = data.getId();
        } catch (Throwable e) {
            typeTiter = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_titer'", e);
        }
        
        try {
            data = dl.fetchBySystemName("test_res_type_date");
            typeDate = data.getId();
        } catch (Throwable e) {
            typeDate = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_date'", e);
        }
        
        try {
            data = dl.fetchBySystemName("test_res_type_date_time");
            typeDateTime = data.getId();
        } catch (Throwable e) {
            typeDateTime = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_date_time'", e);
        }
        
        try {
            data = dl.fetchBySystemName("test_res_type_time");
            typeTime = data.getId();
        } catch (Throwable e) {
            typeTime = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_time'", e);
        }   
        
        try {
            data = dl.fetchBySystemName("test_res_type_default");
            typeDefault = data.getId();
        } catch (Throwable e) {
            typeDefault = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_res_type_default'", e);
        }
                
    }


    public TestResultManager fetchByTestId(Integer testId) throws Exception {
        TestResultManager trm;
        ArrayList<ArrayList<TestResultViewDO>> list;

        list = local().fetchByTestId(testId);
        trm = TestResultManager.getInstance();
        trm.setTestId(testId);
        trm.setResults(list);

        return trm;
    }

    public TestResultManager add(TestResultManager man,
                                 HashMap<Integer, Integer> idMap) throws Exception {
        TestResultLocal rl;
        TestResultViewDO data;
        int i, j, size, negId;

        rl = local();

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

    public TestResultManager update(TestResultManager man,
                                    HashMap<Integer, Integer> idMap) throws Exception {
        TestResultLocal rl;
        TestResultViewDO data;
        int i, j, size, negId;

        rl = local();

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
    
    public void validate(TestResultManager trm,TestTypeOfSampleManager ttsm,
                         HashMap<Integer, List<Integer>> resGrpRsltMap) throws Exception{
        ValidationErrorsList list;
        TestResultViewDO data;
        Integer typeId, unitId, entryId;
        int i, j;
        String value, fieldName, unitText;
        boolean hasDateType;
        ResultRangeNumeric nr;
        ResultRangeTiter tr;
        HashMap<Integer, List<ResultRangeTiter>> trMap;
        HashMap<Integer, List<ResultRangeNumeric>> nrMap;
        List<Integer> dictList, unitsWithDefault;
        List<Integer> resIdList;
        DictionaryLocal dl;
        TestResultLocal rl;
        
        list = new ValidationErrorsList();                
        value = null;
        dl = dictLocal();
        rl = local();

        trMap = new HashMap<Integer, List<ResultRangeTiter>>();
        nrMap = new HashMap<Integer, List<ResultRangeNumeric>>();
        dictList = new ArrayList<Integer>();
        unitsWithDefault = new ArrayList<Integer>();
        hasDateType = false;
        

        for (i = 0; i < trm.groupCount(); i++ ) {
            trMap.clear();
            nrMap.clear();
            dictList.clear();
            hasDateType = false;
            unitsWithDefault.clear();
            resIdList = new ArrayList<Integer>();        
            resGrpRsltMap.put(i+1, resIdList);
            
            for (j = 0; j < trm.getResultGroupSize(i+1); j++ ) {
                data = trm.getResultAt(i+1, j);
                value = data.getValue();
                typeId = data.getTypeId();
                unitId = data.getUnitOfMeasureId();    
                
                resIdList.add(data.getId());
                
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
                    rl.validate(data);                    
                } catch(Exception e) {
                    DataBaseUtil.mergeException(list, e, "resultTable", i, j);
                    continue;
                }

                try {
                    if (DataBaseUtil.isSame(typeNumeric,typeId)) {
                        nr = new ResultRangeNumeric();
                        nr.setRange(value);
                        addNumericIfNoOverLap(nrMap, unitId, nr);
                    } else if (DataBaseUtil.isSame(typeTiter,typeId)) {
                        tr = new ResultRangeTiter();
                        tr.setRange(value);
                        addTiterIfNoOverLap(trMap, unitId, tr);
                    } else if (DataBaseUtil.isSame(typeDate,typeId)) {
                        TestResultValidator.validateDate(value);
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (DataBaseUtil.isSame(typeDateTime,typeId)) {                        
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (DataBaseUtil.isSame(typeTime,typeId)) {                        
                        if (hasDateType) {
                            fieldName = meta.TEST_RESULT.getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (DataBaseUtil.isSame(typeDict,typeId)) {
                        entryId = Integer.parseInt(value);
                        if (entryId == null)
                            throw new ParseException("illegalDictEntryException");

                        if (!dictList.contains(entryId))
                            dictList.add(entryId);
                        else
                            throw new InconsistencyException("testDictEntryNotUniqueException");
                    } else if (DataBaseUtil.isSame(typeDefault,typeId)) {
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
                                     List<ResultRangeTiter>> trMap,Integer unitId,
                                     ResultRangeTiter tr) throws InconsistencyException {
        ResultRangeTiter lr;
        List<ResultRangeTiter> trList;

        trList = trMap.get(unitId);
        if (trList != null) {
            for (int i = 0; i < trList.size(); i++ ) {
                lr = trList.get(i);
                if (lr.intersects(tr))
                    throw new InconsistencyException("testTiterRangeOverlapException");
            }
            trList.add(tr);
        } else {
            trList = new ArrayList<ResultRangeTiter>();
            trList.add(tr);
            trMap.put(unitId, trList);
        }
    }

    private void addNumericIfNoOverLap(HashMap<Integer,
                                       List<ResultRangeNumeric>> nrMap,Integer unitId,
                                       ResultRangeNumeric nr) throws InconsistencyException {
        ResultRangeNumeric lr;
        List<ResultRangeNumeric> nrList;

        nrList = nrMap.get(unitId);
        if (nrList != null) {
            for (int i = 0; i < nrList.size(); i++ ) {
                lr = nrList.get(i);
                if (lr.intersects(nr))
                    throw new InconsistencyException("testNumRangeOverlapException");
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
