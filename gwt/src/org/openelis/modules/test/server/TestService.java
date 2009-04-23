/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.test.server;

import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetAnalyteDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestReflexMetaMap;
import org.openelis.metamap.TestResultMetaMap;
import org.openelis.metamap.TestSectionMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.metamap.TestWorksheetAnalyteMetaMap;
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.modules.test.client.PrepAndReflexForm;
import org.openelis.modules.test.client.SampleTypeForm;
import org.openelis.modules.test.client.TestAnalyteForm;
import org.openelis.modules.test.client.TestForm;
import org.openelis.modules.test.client.TestGeneralPurposeRPC;
import org.openelis.modules.test.client.WorksheetForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class TestService implements AppScreenFormServiceInt<TestForm,Query<TableDataRow<Integer>>>,
                                    AutoCompleteServiceInt{

    private static final int leftTableRowsPerPage = 27;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    private static final TestMetaMap TestMeta = new TestMetaMap();       

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List testNames;
            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
            try {
                testNames = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }
            
        // fill the model with the query results
        int i = 0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else 
            query.results.clear();
        while (i < testNames.size() && i < leftTableRowsPerPage) {
            IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)testNames.get(i);

            StringObject name = new StringObject(resultDO.getLastName()
                                                 +", "+resultDO.getFirstName());

            query.results.add(new TableDataRow<Integer>(resultDO.getId(),name));
            i++;
        }

        return query;
    }
    
    public TestForm commitAdd(TestForm rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDO testDO = null;
        List<TestPrepDO> prepTestDOList = null;
        List<TestTypeOfSampleDO> sampleTypeDOList = null;
        List<TestReflexDO> testReflexDOList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;                
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
        List<TestWorksheetAnalyteDO> twsaList = null;
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;       
    
        Integer testId;
    
        //rpc.details.duplicate = false;
        rpc.duplicate = false;
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;
        
        testDO = getTestDOFromRPC(rpc);
        // if(((FormRPC)rpcSend.getField("details")).load){        
        // }
        testSectionDOList = getTestSectionsFromRPC(rpc,null) ;
            
        sampleTypeDOList = getSampleTypesFromRPC(rpc.sampleType,null);
        
        prepTestDOList = getPrepTestsFromRPC(rpc.prepAndReflex,null);
        testReflexDOList = getTestReflexesFromRPC(rpc.prepAndReflex,null);
        
        
        //if (((FormRPC)rpcSend.getField("worksheet")).load) {
         itemsDOList = getWorksheetItemsFromRPC(rpc.worksheet);               
         worksheetDO = getTestWorkSheetFromRPC(rpc.worksheet,null);
         twsaList = getWorksheetAnalytesFromRPC(rpc.worksheet,null);
        //}
        
       // if (((FormRPC)rpcSend.getField("testAnalyte")).load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.testAnalyte,null);
            resultDOList = getTestResultDOListFromRPC(rpc.testAnalyte,null);
        //}
        
        List exceptionList = remote.validateForAdd(testDO,prepTestDOList,
                                                   sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList,twsaList,
                                                   testAnalyteDOList,testSectionDOList,
                                                   resultDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc);
    
            return rpc;
        }
    
        try {
            testId = remote.updateTest(testDO,prepTestDOList,
                                       sampleTypeDOList,testReflexDOList,worksheetDO,
                                       itemsDOList,twsaList,
                                       testAnalyteDOList,testSectionDOList,
                                       resultDOList);
            testDO = remote.getTest(testId);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc);
    
            return rpc;
        }
    
        testDO.setId(testId);
        setFieldsInRPC(rpc, testDO,false);
        return rpc;
    }
    
    public TestForm commitUpdate(TestForm rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDO testDO = new TestDO();        
        List<TestPrepDO> testPrepDOList = null;
        List<TestTypeOfSampleDO> sampleTypeDOList = null;
        List<TestReflexDO> testReflexDOList= null;
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
        List<TestWorksheetAnalyteDO> twsaList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;  
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;

        rpc.duplicate = false; 
        testDO = getTestDOFromRPC(rpc);
        
        IntegerField testId = rpc.id;
                 
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;
                
        testSectionDOList = getTestSectionsFromRPC(rpc,testId.getValue());  


        if (rpc.prepAndReflex.load) {
            
            testPrepDOList = getPrepTestsFromRPC(rpc.prepAndReflex,
                                                     testId.getValue());
            
            testReflexDOList = getTestReflexesFromRPC(rpc.prepAndReflex,
                                                      testId.getValue());                       
        }
        
        if (rpc.sampleType.load) {            
            sampleTypeDOList = getSampleTypesFromRPC(rpc.sampleType,
                                                      testId.getValue());
        }
        
        if (rpc.worksheet.load) {
            itemsDOList = getWorksheetItemsFromRPC(rpc.worksheet);
            worksheetDO = getTestWorkSheetFromRPC(rpc.worksheet,
                                                  testId.getValue());
            twsaList = getWorksheetAnalytesFromRPC(rpc.worksheet,
                                                   testId.getValue());
        }
        
        if (rpc.testAnalyte.load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.testAnalyte,
                                                        testId.getValue());
            resultDOList = getTestResultDOListFromRPC(rpc.testAnalyte,
                                       testId.getValue());
            
            
        }
        
        List exceptionList = remote.validateForUpdate(testDO,testPrepDOList,
                                                      sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList,twsaList,
                                                      testAnalyteDOList,
                                                      testSectionDOList,resultDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc);

            return rpc;
        }

        try {
            remote.updateTest(testDO,testPrepDOList,sampleTypeDOList,
                              testReflexDOList,worksheetDO,itemsDOList,twsaList,
                              testAnalyteDOList, testSectionDOList,resultDOList);
            testDO = remote.getTest(testId.getValue());
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList,rpc);
            
            return rpc;
            
        }
        setFieldsInRPC(rpc, testDO,false);
        return rpc;
    }
    
    public TestForm commitDelete(TestForm rpc) throws RPCException {
        return null;
    }
    
    public TestForm abort(TestForm rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestDO testDO = remote.getTest(rpc.entityKey);
        List<TestSectionDO> tsDOList = remote.getTestSections(rpc.entityKey);
        rpc.duplicate = false; 
        setFieldsInRPC(rpc, testDO,false);                   
        fillTestSections(tsDOList,rpc);       
        
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;      

        if (rpc.sampleType.load) {            
            loadSampleTypes(rpc.entityKey, rpc.sampleType);
        }

        if (rpc.prepAndReflex.load) {
            loadPrepTestsReflexTests(rpc.entityKey, rpc.prepAndReflex);
        }
        
        if (rpc.worksheet.load) {
            loadWorksheetLayout(rpc.entityKey,rpc.worksheet);
        }
        
        if (rpc.testAnalyte.load) {            
            loadTestAnalyte(rpc.entityKey, rpc.testAnalyte);
        }

        return rpc;
    }   

    public TestForm fetch(TestForm rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestDO testDO = remote.getTest(rpc.entityKey);
        List<TestSectionDO> tsDOList = remote.getTestSections(rpc.entityKey);
        rpc.duplicate = false; 
        setFieldsInRPC(rpc, testDO,false);                   
        fillTestSections(tsDOList,rpc);
          
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;

        String tab = (String)rpc.testTabPanel;

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes(rpc.entityKey, rpc.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests(rpc.entityKey, rpc.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout(rpc.entityKey, rpc.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte(rpc.entityKey, rpc.testAnalyte);
        }
        
        return rpc;
    }
    

    
    private void loadSampleTypes(Integer key, SampleTypeForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestTypeOfSampleDO> list = remote.getTestTypeOfSamples(key);
        fillSampleTypes(list, form);
        form.load = true;        
    }
    
    public SampleTypeForm loadSampleTypes(SampleTypeForm rpc) {
        loadSampleTypes(rpc.entityKey,rpc);
        return rpc;
    }

    private void loadPrepTestsReflexTests(Integer key, PrepAndReflexForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestPrepDO> prepList = remote.getTestPreps(key);
        List<TestReflexDO>  reflexList = remote.getTestReflexes(key);    
        fillPrepTests(prepList, form);        
        fillTestReflexes(reflexList,form,key);
        form.load = true;        
    }
    
    public PrepAndReflexForm loadPrepTestsReflexTests(PrepAndReflexForm rpc) {
        loadPrepTestsReflexTests(rpc.entityKey, rpc);
        return rpc;       
    }
        
    private void loadWorksheetLayout(Integer key, WorksheetForm form){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestWorksheetDO worksheetDO = remote.getTestWorksheet(key);
        List<TestWorksheetItemDO> itemDOList = null;
        List<TestWorksheetAnalyteDO> anaDOList = remote.getTestWorksheetAnalytes(key);
        List<IdNameDO> anaIdNameDOList = remote.getTestAnalytesNotAddedToWorksheet(key);
        
        if(worksheetDO != null)
          itemDOList = remote.getTestWorksheetItems(worksheetDO.getId());                
                
        fillWorksheet(worksheetDO,itemDOList,anaDOList,anaIdNameDOList,form);
        form.load = true;
    }
    
    public WorksheetForm loadWorksheetLayout(WorksheetForm rpc){
        loadWorksheetLayout(rpc.entityKey,rpc);
        return rpc;
    }
    
    private void loadTestAnalyte(Integer key,TestAnalyteForm form){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes(key);
        fillAnalyteTree(taList, form);
        fillTestResults(key,form);
        form.load = true;                   
    }        
    
    public TestAnalyteForm loadTestAnalyte(TestAnalyteForm rpc) {
       loadTestAnalyte(rpc.entityKey,rpc);
       return rpc;
    }
    
    public TestAnalyteForm getAnalyteTreeModel(TestAnalyteForm rpc) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes(rpc.entityKey);
        fillAnalyteTree(taList, rpc);
        return rpc;
    }
    
    public TestForm fetchForUpdate(TestForm rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestDO testDO = new TestDO();
        try {
            testDO = remote.getTestAndLock(rpc.entityKey, SessionManager.getSession().getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        List<TestSectionDO> tsDOList = remote.getTestSections(rpc.entityKey);
        rpc.duplicate = false; 
        setFieldsInRPC(rpc, testDO,false);                   
        fillTestSections(tsDOList,rpc);
                  
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;

        String tab = rpc.testTabPanel;

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes(rpc.entityKey, rpc.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests(rpc.entityKey, rpc.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout(rpc.entityKey, rpc.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte(rpc.entityKey, rpc.testAnalyte);
        }

        return rpc;
    }
    
    public TestForm getScreen(TestForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");
        return rpc;
    }
    
    public TestAnalyteForm getTestAnalyteModel(TestAnalyteForm rpc){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        IdNameDO methodDO = null;

        List<IdNameDO> list = remote.getTestAnalyteDropDownValues(rpc.entityKey);
        rpc.model = new TableDataModel<TableDataRow<Integer>>();

        rpc.model.add(new TableDataRow<Integer>(-1,new StringObject("")));

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            methodDO = (IdNameDO)iter.next();
            rpc.model.add(new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
        }
        
        return rpc;
    }
    
    
    
    public TableDataModel<TableDataRow<Integer>> getTestResultModel(Integer testId, Integer analyteId,boolean forDuplicate) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsForTestAnalyte(testId,analyteId);
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();

        model.add(new TableDataRow<Integer>(-1,new StringObject("")));

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            if(!forDuplicate) 
             model.add(new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
            else 
             model.add(new TableDataRow<Integer>(methodDO.getId()*(-2),new StringObject(methodDO.getName())));    
        }
        
        return model; 
    }
    
    public TestGeneralPurposeRPC getResultGroupModel(TestGeneralPurposeRPC rpc) {       
        rpc.model = new TableDataModel<TableDataRow<Integer>>();
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest(rpc.key);
        rpc.model.add(new TableDataRow<Integer>(-1,new StringObject("")));
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO rgDO = (IdNameDO)iter.next();
            rpc.model.add(new TableDataRow<Integer>(rgDO.getId(),new StringObject(rgDO.getId().toString())));
        }
        
        return rpc;
        
    }
    
    public TestGeneralPurposeRPC getGroupCountForTest(TestGeneralPurposeRPC rpc){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest(rpc.key);  
        rpc.integerValue = list.size();        
        return rpc;
    }
    
    
    public TableDataModel<TableDataRow<Integer>> getTestResultModel(Integer testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
          List<IdNameDO> list = remote.getTestResultsforTest(testId);
          TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
          model.add(new TableDataRow<Integer>(-1,new StringObject("")));
             for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                 IdNameDO methodDO = (IdNameDO)iterator.next();
                 model.add(new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
             }
         return model;
    }    
              
    
    public TestGeneralPurposeRPC getMapsAndUnitDropDownModel(TestGeneralPurposeRPC rpc) {
      loadTestResultModelMap(rpc);
      loadResultGroupAnalyteMap(rpc);
      loadUnitIdNumResMapForTest(rpc);
      loadUnitDropdownModel(rpc);
      return rpc;
    }
    
    private void loadUnitDropdownModel(TestGeneralPurposeRPC rpc) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> unitList = remote.getUnitsOfMeasureForTest(rpc.key);        
        if(unitList.size() > 0) {
            rpc.model = new TableDataModel<TableDataRow<Integer>>();    
            loadDropDown(unitList, rpc.model);
        }       
               
    } 
    
    public TestGeneralPurposeRPC getInitialModel(TestGeneralPurposeRPC rpc) { 
        rpc.model = getInitialModel(rpc.fieldName);
        return rpc;
    }
    
    public TableDataModel getInitialModel(String cat) {
        //String cat = obj.getValue();
        TableDataModel model = new TableDataModel<TableDataRow<Integer>>();
        List<IdNameDO> values = null;

        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if (cat.equals(TestMeta.getMethodId())) {
            values = remote.getMethodDropDownValues();
        } else if (cat.equals(TestMeta.getLabelId())) {
            values = remote.getLabelDropDownValues();
        } else if (cat.equals(TestMeta.getTestTrailerId())) {
            values = remote.getTestTrailerDropDownValues();
        } else if (cat.equals(TestMeta.getScriptletId())|| 
                   cat.equals(TestMeta.getTestWorksheet().getScriptletId())||
                   cat.equals(TestMeta.getTestAnalyte().getScriptletId())) {
            values = remote.getScriptletDropDownValues();
        }else if (cat.equals(TestMeta.getTestSection().getSectionId())) {
            values = remote.getSectionDropDownValues();
        }else if (cat.equals(TestMeta.getRevisionMethodId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_revision_method"));
        }else if (cat.equals(TestMeta.getTestResult().getRoundingMethodId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("rounding_method"));
        }else if (cat.equals(TestMeta.getReportingMethodId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_reporting_method"));
        }else if (cat.equals(TestMeta.getSortingMethodId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_sorting_method"));
        }else if (cat.equals(TestMeta.getTestSection().getFlagId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_section_flags"));
        }else if (cat.equals(TestMeta.getTestResult().getTypeId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_result_type"));
        }else if (cat.equals(TestMeta.getTestResult().getFlagsId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_result_flags"));
        }else if (cat.equals(TestMeta.getTestFormatId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_format"));
        } else if (cat.equals(TestMeta.getTestTypeOfSample().getUnitOfMeasureId())) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("unit_of_measure"));
        } else if (cat.equals(TestMeta.getTestTypeOfSample().getTypeOfSampleId())) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("type_of_sample"));
        } else if (cat.equals(TestMeta.getTestPrep().getPrepTestId())) {            
            List<QaEventTestDropdownDO> qaedDOList = remote.getPrepTestDropDownValues();
            loadPrepTestDropDown(qaedDOList, model);
        }else if (cat.equals(TestMeta.getTestReflex().getFlagsId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_reflex_flags"));
        }else if (cat.equals(TestMeta.getTestWorksheet().getFormatId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_worksheet_number_format"));
        }else if (cat.equals(TestMeta.getTestWorksheetItem().getTypeId())) {            
            values = remote.getTestWSItemTypeDropDownValues();
        }else if (cat.equals(TestMeta.getTestAnalyte().getTypeId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_analyte_type"));
        }else if (cat.equals(TestMeta.getTestWorksheetAnalyte().getFlagId())) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_worksheet_analyte_flags"));
        }

        if (values != null) {
            loadDropDown(values, model);
        }

        return model;
    }    

    public TableDataModel<TableDataRow<Integer>> getMatches(String cat, TableDataModel model, String match, HashMap params) {         
        
        //if(("analyte").equals(cat)){ 
         TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
         List entries = remote.getMatchingEntries(match.trim()+"%", 10,cat);
         TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
         for (Iterator iter = entries.iterator(); iter.hasNext();) {
             
             IdNameDO element = (IdNameDO)iter.next();
             Integer entryId = element.getId();                   
             String entryText = element.getName();
             
             TableDataRow<Integer> data = new TableDataRow<Integer>(entryId,new StringObject(entryText));                                      
             dataModel.add(data);
         }       
         
         return dataModel;

     }
    
    public TestGeneralPurposeRPC getCategorySystemName(TestGeneralPurposeRPC rpc){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
        try{
            rpc.stringValue = remote.getSystemNameForEntryId(rpc.key);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return rpc;
    }
    
    public TestGeneralPurposeRPC getEntryIdForEntryText(TestGeneralPurposeRPC rpc){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        try{
            rpc.key = remote.getEntryIdForEntry(rpc.stringValue);
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return rpc;
    }
    
    public TestGeneralPurposeRPC getEntryIdForSystemName(TestGeneralPurposeRPC rpc){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        try{
            rpc.key = remote.getEntryIdForSystemName(rpc.stringValue);
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return rpc;
    }
    
    public TestAnalyteForm fillTestResults(Integer key, TestAnalyteForm form){     
        try{ 
         TableDataRow<Integer> row = null;
         
         TestResultDO resultDO = null;
         TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");               
         TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.testResultsTable.getValue();
         List<IdNameDO> rglist = remote.getResultGroupsForTest(key);                
         
         model.clear();
         
         if(rglist.size() > 0) {        
          List<TestResultDO> resultDOList = remote.getTestResults(key,1);                          
           for(int iter = 0; iter < resultDOList.size(); iter++){
             row = model.createNewSet();
             resultDO = resultDOList.get(iter);               
               
             
             if(!form.duplicate) 
              row.key = resultDO.getId();              
              else 
                row.key = (resultDO.getId() * -2);                                                         
                         
             if(resultDO.getUnitOfMeasureId() != null) {                 
              ((DropDownField<Integer>)row.cells[0]).setValue(new TableDataRow<Integer>(resultDO.getUnitOfMeasureId()));
              row.setData(new IntegerObject(resultDO.getUnitOfMeasureId()));
             } 
             
             ((DropDownField<IntegerObject>)row.cells[1]).setValue(new TableDataRow<Integer>(resultDO.getTypeId()));
             
             if(resultDO.getDictEntry() == null) {
              ((StringField)row.cells[2]).setValue(resultDO.getValue());     
             } else {   
              ((StringField)row.cells[2]).setValue(resultDO.getDictEntry());
             } 
             
             
                                 
             ((StringField)row.cells[3]).setValue(resultDO.getQuantLimit());
             ((StringField)row.cells[4]).setValue(resultDO.getContLevel());
             ((StringField)row.cells[5]).setValue(resultDO.getHazardLevel()); 
             
             if(resultDO.getFlagsId()!=null)
              ((DropDownField)row.cells[6]).setValue(new TableDataRow<Integer>(resultDO.getFlagsId()));                                              
             
             ((IntegerField)row.cells[7]).setValue(resultDO.getSignificantDigits());   
             
             if(resultDO.getRoundingMethodId()!=null)
              ((DropDownField)row.cells[8]).setValue(new TableDataRow<Integer>(resultDO.getRoundingMethodId()));
             
             model.add(row);
          }
         }          
        } catch(Exception ex) {
            ex.printStackTrace();
        }    
        return form;
     }
            
     
     public TestAnalyteForm loadTestResultModellist(TestAnalyteForm rpc){ 
         TestAnalyteForm form = rpc;
         for(int iter = 1 ; iter < rpc.integerValue+1; iter++) {
             form.resultModelCollection.add(getTestResultsByGroup(rpc.entityKey, iter,(TableDataModel)rpc.model.clone(),form.duplicate));
         }
         return rpc;
     }
     
     
    public TestForm getDuplicateRPC(TestForm rpc) throws RPCException{
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
        
        TestDO testDO = remote.getTest(rpc.entityKey);                       
        List<TestSectionDO> tsDOList = remote.getTestSections(rpc.entityKey);
        rpc.duplicate = true; 
        setFieldsInRPC(rpc, testDO, true);
        fillTestSections(tsDOList,rpc);
                  
        rpc.sampleType.duplicate = true;
        rpc.prepAndReflex.duplicate = true;
        rpc.worksheet.duplicate = true;
        rpc.testAnalyte.duplicate = true;
        
        loadSampleTypes(rpc.entityKey, rpc.sampleType);
        loadPrepTestsReflexTests(rpc.entityKey, rpc.prepAndReflex);                
        loadWorksheetLayout(rpc.entityKey, rpc.worksheet);                
        loadTestAnalyte(rpc.entityKey, rpc.testAnalyte);
        
        rpc.testAnalyte.resultModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();        
        
        for(int iter = 1 ; iter < rpc.numGroups+1; iter++) {
            rpc.testAnalyte.resultModelCollection.add(
                 getTestResultsByGroup(rpc.entityKey,iter,(TableDataModel)rpc.resultTableModel.clone(),true));
        }        
        
        rpc.entityKey = null;
        return rpc;
    }   
    
    private void loadTestResultModelMap(TestGeneralPurposeRPC rpc) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        HashMap<Integer,List<IdNameDO>> listMap = remote.getAnalyteResultsMap(rpc.key); 
        Entry<Integer,List<IdNameDO>> entry = null;
        List<IdNameDO> list = null;
        TableDataModel<TableDataRow<Integer>> dataModel = null;
         for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
                entry = (Entry<Integer,List<IdNameDO>>)iter.next();
                list = (List<IdNameDO>)listMap.get(entry.getKey());              
                dataModel = new TableDataModel<TableDataRow<Integer>>();
                loadDropDown(list, dataModel);
                rpc.resultDropdownModelMap.put(entry.getKey(), dataModel);             
            }
        }
      
      
      private void loadResultGroupAnalyteMap(TestGeneralPurposeRPC rpc) {       
          TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
          HashMap<Integer,List<Integer>> listMap = remote.getResultGroupAnalytesMap(rpc.key); 
           for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
             Entry<Integer,List<IdNameDO>> entry = (Entry<Integer,List<IdNameDO>>)iter.next();
             List<Integer> list = (List<Integer>)listMap.get(entry.getKey());                         
             rpc.resGroupAnalyteIdMap.put(entry.getKey(), list);             
         }               
      }
      
      private void loadUnitIdNumResMapForTest(TestGeneralPurposeRPC rpc) {
       TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
       HashMap<Integer,Integer> listMap  = null;     
       try{
        listMap = remote.getUnitIdNumResMapForTest(rpc.key);
       } catch(Exception ex) {
           ex.printStackTrace();
       }
       for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
         Entry<Integer,Integer> entry = (Entry<Integer,Integer>)iter.next();
         Integer numRes = (Integer)listMap.get(entry.getKey());                                                        
         rpc.unitIdNumResMap.put(entry.getKey(), numRes);             
       }
          
      }
    
    private TableDataModel<TableDataRow<Integer>> getTestResultsByGroup(Integer key, Integer resultGroup, TableDataModel model, boolean forDuplicate){
        TableDataRow<Integer> row = null;        
        TestResultDO resultDO = null;
        IntegerField id = null;
        IntegerField rg = null;
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestResultDO> resultDOList = remote.getTestResults(key, resultGroup);
        
        model.clear(); 
        
        for(int iter = 0; iter < resultDOList.size(); iter++){
            row = model.createNewSet();
            resultDO = resultDOList.get(iter);  
                           
            rg = new IntegerField(resultDO.getResultGroup());   
            
            if(!forDuplicate) 
              row.key = resultDO.getId();   
             else 
              row.key = (resultDO.getId() * -2);                  
                                        
            
            if(resultDO.getUnitOfMeasureId() != null) {
              ((DropDownField<Integer>)row.cells[0]).setValue(new TableDataRow<Integer>(resultDO.getUnitOfMeasureId()));
              row.setData(new IntegerObject(resultDO.getUnitOfMeasureId()));
            }
            
            ((DropDownField<IntegerObject>)row.cells[1]).setValue(new TableDataRow<Integer>(resultDO.getTypeId()));
            
            if(resultDO.getDictEntry() == null) {
               ((StringField)row.cells[2]).setValue(resultDO.getValue());                
            } else {   
               ((StringField)row.cells[2]).setValue(resultDO.getDictEntry());
            }               
            
            ((StringField)row.cells[4]).setValue(resultDO.getContLevel());
            ((StringField)row.cells[5]).setValue(resultDO.getHazardLevel()); 
            
            if(resultDO.getFlagsId()!=null)
             ((DropDownField<IntegerObject>)row.cells[6]).setValue(new TableDataRow<Integer>(resultDO.getFlagsId()));                                              
            
            ((IntegerField)row.cells[7]).setValue(resultDO.getSignificantDigits());   
            
            if(resultDO.getRoundingMethodId()!=null)
             ((DropDownField<IntegerObject>)row.cells[8]).setValue(new TableDataRow<Integer>(resultDO.getRoundingMethodId()));
            
            model.add(row);
        }
        
       return model; 
    }                 
    
    
    private List<TestPrepDO> getPrepTestsFromRPC(PrepAndReflexForm form,Integer testId) {


        TableDataModel<TableDataRow<Integer>> model = (TableDataModel<TableDataRow<Integer>>)form.testPrepTable.getValue();
        TableDataRow<Integer> row = null; 
        TestPrepDO testPrepDO = null;
        
        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {
          row = model.get(j);
          testPrepDO = new TestPrepDO();
            
          testPrepDO.setDelete(false);           
                                                                         
          testPrepDO.setId(row.key);                                 
                
          testPrepDO.setTestId(testId);
            
          testPrepDO.setPrepTestId((Integer)((DropDownField)row.cells[0]).getSelectedKey());           

          testPrepDO.setIsOptional((String)((CheckField)row.cells[1]).getValue());

          testPrepDOList.add(testPrepDO);
        }
        
       if(model.getDeletions() != null) { 
        for (int j = 0; j < model.getDeletions().size(); j++) {
            row = (TableDataRow)model.getDeletions().get(j);
            testPrepDO = new TestPrepDO();
            
            testPrepDO.setDelete(true);                                                                                      
            testPrepDO.setId(row.key);
            
            testPrepDOList.add(testPrepDO);
          }         
        model.getDeletions().clear();
       } 
        return testPrepDOList;
    }

    private List<TestTypeOfSampleDO> getSampleTypesFromRPC(SampleTypeForm form,
                                                           Integer testId) {

        TableDataRow<Integer> row = null;
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.sampleTypeTable.getValue();        
        TestTypeOfSampleDO testTypeOfSampleDO = null;
        
        List<TestTypeOfSampleDO> typeOfSampleDOList = new ArrayList<TestTypeOfSampleDO>();

        for (int i = 0; i < model.size(); i++) {
            row = model.get(i);
            testTypeOfSampleDO = new TestTypeOfSampleDO();

            testTypeOfSampleDO.setDelete(false);            
             
            testTypeOfSampleDO.setId(row.key);             
            
            testTypeOfSampleDO.setTestId(testId);
            
            testTypeOfSampleDO.setTypeOfSampleId((Integer)((DropDownField)row.cells[0]).getSelectedKey());
                                        
            testTypeOfSampleDO.setUnitOfMeasureId((Integer)((DropDownField)row.cells[1]).getSelectedKey());
              
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
       
       if(model.getDeletions() != null) {        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            row = (TableDataRow)model.getDeletions().get(i);
            testTypeOfSampleDO = new TestTypeOfSampleDO();
            
            testTypeOfSampleDO.setDelete(true);
            
            testTypeOfSampleDO.setId(row.key); 
                        
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        model.getDeletions().clear();
       } 
        return typeOfSampleDOList;
    }
    
    private List<TestReflexDO> getTestReflexesFromRPC(PrepAndReflexForm form,Integer testId){
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.testReflexTable.getValue();
        List<TestReflexDO> list = new ArrayList<TestReflexDO>();
         for (int i = 0; i < model.size(); i++) {
            TableDataRow<Integer> row = model.get(i);
            TestReflexDO refDO = new TestReflexDO();
                        
            refDO.setId(row.key);            
                        
            refDO.setTestId(testId);
            
            refDO.setDelete(false);            
                                      
            refDO.setAddTestId((Integer)((DropDownField)row.cells[0]).getSelectedKey());                    
             
            refDO.setTestAnalyteId((Integer)((DropDownField)row.cells[1]).getSelectedKey());                                    
        
            refDO.setTestResultId((Integer)((DropDownField)row.cells[2]).getSelectedKey());                    
      
            refDO.setFlagsId((Integer)((DropDownField)row.cells[3]).getSelectedKey());                                
                
            list.add(refDO);
         }
        
         if(model.getDeletions() != null) {     
          for (int i = 0; i < model.getDeletions().size(); i++) {
             TableDataRow<Integer>row = (TableDataRow)model.getDeletions().get(i);
             TestReflexDO refDO = new TestReflexDO();
                          
             refDO.setId(row.key);                                              
             
             refDO.setDelete(true);                                                      
             
             list.add(refDO);
          }
          model.getDeletions().clear();
         }  
         return list;
    }
    
    private TestWorksheetDO getTestWorkSheetFromRPC(WorksheetForm form, Integer testId){
      TestWorksheetDO worksheetDO = null;
      Integer bc = form.batchCapacity.getValue();      
      Integer tc = form.totalCapacity.getValue();    
      
      if(form.id!=null){
          if(worksheetDO == null)
              worksheetDO = new TestWorksheetDO();   
          worksheetDO.setId(form.id);
      }
      
      if(bc!=null){
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();   
        worksheetDO.setBatchCapacity(bc);
      }      
      
      if(form.formatId.getSelectedKey()!=null ){ 
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();  
       worksheetDO.setNumberFormatId((Integer)form.formatId.getSelectedKey());
      }  
      
      if(form.scriptletId.getSelectedKey() != null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();  
          worksheetDO.setScriptletId((Integer)form.scriptletId.getSelectedKey());
      }  
              
      if(tc !=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();   
          worksheetDO.setTotalCapacity(tc);
      }
      
       if(worksheetDO != null)              
        worksheetDO.setTestId(testId);
      
       
      return worksheetDO;
    }
    
    private List<TestSectionDO> getTestSectionsFromRPC(TestForm form,Integer testId){
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.sectionTable.getValue();
        
        List<TestSectionDO> tsDOList =  new ArrayList<TestSectionDO>();
        
        for(int iter = 0; iter < model.size(); iter++){

            TableDataRow<Integer> row = model.get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            tsDO.setDelete(false);
                                           
            tsDO.setId(row.key);                                       
                       
            tsDO.setSectionId((Integer)((DropDownField)row.cells[0]).getSelectedKey());       
            tsDO.setFlagId((Integer)((DropDownField)row.cells[1]).getSelectedKey());            
                        
            tsDO.setTestId(testId);
            tsDOList.add(tsDO);            
        }
        
        if(model.getDeletions() != null) {     
         for(int iter = 0; iter < model.getDeletions().size(); iter++){
            TableDataRow<Integer>row = (TableDataRow)model.getDeletions().get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            
            tsDO.setDelete(true);
            
            tsDO.setId(row.key);                     

            tsDOList.add(tsDO);               
         }        
         model.getDeletions().clear();
        } 
        return tsDOList;
    }
    
    private List<TestWorksheetItemDO> getWorksheetItemsFromRPC(WorksheetForm form) {
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.worksheetTable.getValue();
        
        List<TestWorksheetItemDO> worksheetItemDOList = new ArrayList<TestWorksheetItemDO>();

        for (int i = 0; i < model.size(); i++) {

            TableDataRow<Integer> row = model.get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(false);            
                                   
            worksheetItemDO.setId(row.key);
         
            worksheetItemDO.setTestWorksheetId(form.id);      
            worksheetItemDO.setPosition(((IntegerField)row.cells[0]).getValue());               
            worksheetItemDO.setTypeId((Integer)((DropDownField)row.cells[1]).getSelectedKey());                         
            worksheetItemDO.setQcName((String)((StringField)row.cells[2]).getValue()); 
                        
            worksheetItemDOList.add(worksheetItemDO);
        }
        
       if(model.getDeletions() != null) {     
        for (int i = 0; i < model.getDeletions().size(); i++) {
            TableDataRow<Integer>row = (TableDataRow)model.getDeletions().get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(true);            

            worksheetItemDO.setId(row.key); 
            
            worksheetItemDOList.add(worksheetItemDO);
        }
        model.getDeletions().clear();
       } 
        return worksheetItemDOList;
    }
    
    private List<TestWorksheetAnalyteDO> getWorksheetAnalytesFromRPC(WorksheetForm form,Integer testId) {
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.worksheetAnalyteTable.getValue();
        
        List<TestWorksheetAnalyteDO> worksheetItemDOList = new ArrayList<TestWorksheetAnalyteDO>();

        for (int i = 0; i < model.size(); i++) {
            TableDataRow<Integer> row = model.get(i);
            TestWorksheetAnalyteDO worksheetAnaDO = new TestWorksheetAnalyteDO();
            IntegerField id = null;
            IntegerField anaId = null;
            if(!"Y".equals(row.cells[1].getValue())) {
             worksheetAnaDO.setDelete(true);                                                                                                                         
           } else {
               worksheetAnaDO.setDelete(false);           
               worksheetAnaDO.setTestId(testId);
               worksheetAnaDO.setRepeat(((IntegerField)row.cells[2]).getValue());               
               worksheetAnaDO.setFlagId((Integer)((DropDownField)row.cells[3]).getSelectedKey());    
           } 
                       
           worksheetAnaDO.setId(row.key);
           anaId = (IntegerField)row.getData();
           worksheetAnaDO.setAnalyteId(anaId.getValue());
            
           worksheetItemDOList.add(worksheetAnaDO);
        }
        
        if(model.getDeletions() != null) {     
         for (int i = 0; i < model.getDeletions().size(); i++) {
            TableDataRow<Integer> row = (TableDataRow)model.getDeletions().get(i);
            TestWorksheetAnalyteDO worksheetAnaDO = new TestWorksheetAnalyteDO();
            IntegerField id = null;
            IntegerField anaId = null;
            
            worksheetAnaDO.setDelete(true);                                                                                                                         
             
            worksheetAnaDO.setId(row.key);
            anaId = (IntegerField)row.getData();
            worksheetAnaDO.setAnalyteId(anaId.getValue());
            
            worksheetItemDOList.add(worksheetAnaDO);
        }
         model.getDeletions().clear();
        } 
        return worksheetItemDOList;
    }
    

    private void setFieldsInRPC(TestForm form, TestDO testDO, boolean forDuplicate) {
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel();
        
        if(!forDuplicate)
         form.id.setValue(testDO.getId());
        else
         form.id.setValue(null);
        
        form.name.setValue(testDO.getName());        

        model.add(new TableDataRow<Integer>(testDO.getMethodId(),new StringObject(testDO.getMethodName())));
        form.methodId.setModel(model);
        form.methodId.setValue(model.get(0)); 
        
        form.description.setValue(testDO.getDescription());
        form.reportingDescription.setValue(testDO.getReportingDescription());
        
        
        if(!form.duplicate) {
         form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDO.getActiveBegin()
                                                                     .getDate()));
         form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDO.getActiveEnd()
                                                                     .getDate()));
         
         form.isActive.setValue(testDO.getIsActive());         
        } else {
            form.activeBegin.setValue(null);
            form.activeEnd.setValue(null);               
            form.isActive.setValue("N"); 
        }
    
                
        if(testDO.getTestTrailerId()!=null) {
         form.testTrailerId.setValue(new TableDataRow<Integer>
                    (testDO.getTestTrailerId()));
        }
        
        form.isReportable.setValue(testDO.getIsReportable()); 
        
        form.timeTransit.setValue(testDO.getTimeTransit());
        form.timeHolding.setValue(testDO.getTimeHolding());
        form.timeTaAverage.setValue(testDO.getTimeTaAverage());                
        form.timeTaWarning.setValue(testDO.getTimeTaWarning());
        form.timeTaMax.setValue(testDO.getTimeTaMax()); 
        
        if(testDO.getLabelId()!=null) {
         form.labelId.setValue(new TableDataRow<Integer>(testDO.getLabelId()));
        } 
        
        form.labelQty.setValue(testDO.getLabelQty());                 
        
        if(testDO.getScriptletId()!=null) {
         form.scriptletId.setValue(new TableDataRow<Integer>(testDO.getScriptletId()));
        } 
        
        if(testDO.getTestFormatId()!=null) {
         form.testFormatId.setValue(new TableDataRow<Integer>(testDO.getTestFormatId()));   
        }
        
        if(testDO.getRevisionMethodId()!=null) {
         form.revisionMethodId.setValue(new TableDataRow<Integer>(testDO.getRevisionMethodId()));
        }
        
        if(testDO.getSortingMethodId()!=null) {
          form.sortingMethodId.setValue(new TableDataRow<Integer>(testDO.getSortingMethodId()));
        }   
        
        if(testDO.getReportingMethodId()!=null) {
          form.reportingMethodId.setValue(new TableDataRow<Integer>(testDO.getReportingMethodId()));
        }    
        
         form.reportingSequence.setValue(testDO.getReportingSequence());
        
        
    }
    
    private void fillTestSections(List<TestSectionDO> testSectionDOList, TestForm form){
        TableDataModel<TableDataRow<Integer>> model = form.sectionTable.getValue();
        model.clear();
        
        if(testSectionDOList.size()>0){
            for(int iter = 0; iter < testSectionDOList.size(); iter++){
                TestSectionDO sectionDO = (TestSectionDO)testSectionDOList.get(iter);

                TableDataRow<Integer> row = model.createNewSet();
                                
                if(!form.duplicate) 
                 row.key = sectionDO.getId();                                
                
                ((DropDownField)row.cells[0]).setValue(new TableDataRow<Integer>(sectionDO.getSectionId()));

                ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>(sectionDO.getFlagId()));

                model.add(row);                              
                
            }
        }
    }

    private void fillPrepTests(List<TestPrepDO> testPrepDOList,
                               PrepAndReflexForm form) {

        TableDataModel<TableDataRow<Integer>> model = form.testPrepTable.getValue();
        model.clear();
        if (testPrepDOList.size() > 0) {
            for (int iter = 0; iter < testPrepDOList.size(); iter++) {
                TestPrepDO testPrepDO = (TestPrepDO)testPrepDOList.get(iter);

                TableDataRow<Integer> row = model.createNewSet();
                                                                                                                              
                if(!form.duplicate)
                 row.key = testPrepDO.getId();
                
                
                if(testPrepDO.getPrepTestId()!=null) { 
                 ((DropDownField)row.cells[0]).setValue(new TableDataRow<Integer>
                   (testPrepDO.getPrepTestId()));
               }
               
                ((CheckField)row.cells[1]).setValue(testPrepDO.getIsOptional());

                model.add(row);
            }
            
        }
    }
    
    private void fillTestReflexes(List<TestReflexDO> testReflexDOList,
                                  PrepAndReflexForm form, Integer testId){
        TableDataModel<TableDataRow<Integer>> model = form.testReflexTable.getValue();
        model.clear();        
        if(testReflexDOList.size() > 0){
            Integer unselVal = new Integer(-1);
            for(int iter = 0; iter < testReflexDOList.size(); iter++){
              TestReflexDO refDO = testReflexDOList.get(iter);

              TableDataRow<Integer> row = model.createNewSet();
                            
              if(!form.duplicate) {
               row.key = refDO.getId();
              }
              
              if(refDO.getAddTestId()!=null) {
               ((DropDownField)row.cells[0]).setValue(new TableDataRow<Integer>
                 (refDO.getAddTestId()));
              } 

             if(refDO.getTestAnalyteId()!=null) {
              if(!form.duplicate) {   
                ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>
                     (refDO.getTestAnalyteId()));
               } else {
                ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>
                   (new Integer(refDO.getTestAnalyteId()*(-2))));   
               }
              
               if(! (unselVal).equals(refDO.getTestAnalyteId())) {                   
                TableDataModel<TableDataRow<Integer>> dmodel = getTestResultModel(testId,refDO.getTestAnalyteId(),form.duplicate);
                ((DropDownField)row.cells[2]).setModel(dmodel);
               } 
              } 
              
              if(refDO.getTestResultId()!=null) {
               if(!form.duplicate) {      
                ((DropDownField)row.cells[2]).setValue(new TableDataRow<Integer>
                  (refDO.getTestResultId()));
               } else {
                ((DropDownField)row.cells[2]).setValue(new TableDataRow<Integer>
                   (new Integer(refDO.getTestResultId() * (-2))));  
               } 
              } 
              
              if(refDO.getFlagsId()!=null) {
                ((DropDownField)row.cells[3]).setValue(new TableDataRow<Integer>
                             (refDO.getFlagsId()));
              } 
                           
               model.add(row);
            }
        }
        
    }
        

    private void fillSampleTypes(List<TestTypeOfSampleDO> sampleTypeDOList,
                                 SampleTypeForm form) {

        TableDataModel<TableDataRow<Integer>> model = form.sampleTypeTable.getValue();
        model.clear();

        if (sampleTypeDOList.size() > 0) {
            for (int iter = 0; iter < sampleTypeDOList.size(); iter++) {
                TestTypeOfSampleDO testTypeOfSampleDO = (TestTypeOfSampleDO)sampleTypeDOList.get(iter);
                TableDataRow<Integer> row = model.createNewSet();
                // new TableRow();                
                                
                if(!form.duplicate) 
                   row.key = testTypeOfSampleDO.getId();                                                         
                
                if(testTypeOfSampleDO.getTypeOfSampleId()!=null)
                 ((DropDownField)row.cells[0]).setValue(new TableDataRow<Integer>(testTypeOfSampleDO.getTypeOfSampleId()));

                if(testTypeOfSampleDO.getUnitOfMeasureId()!=null) {
                 ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>(testTypeOfSampleDO.getUnitOfMeasureId()));
                 row.setData(new IntegerField(testTypeOfSampleDO.getUnitOfMeasureId()));
                } 
                                
                model.add(row);
            }

        }

    }
    
    private void fillWorksheet(TestWorksheetDO worksheetDO, 
                               List<TestWorksheetItemDO> worksheetItemList,
                               List<TestWorksheetAnalyteDO> worksheetAnalyteList,
                               List<IdNameDO> anaIdNameDOList,
                               WorksheetForm form){
        
       TableDataModel<TableDataRow<Integer>> itemModel = null; 
       TableDataModel<TableDataRow<Integer>> anaModel = null; 
       
       if(worksheetDO!=null){ 
        form.batchCapacity.setValue(worksheetDO.getBatchCapacity());
        
        if(worksheetDO.getNumberFormatId()!=null)
         form.formatId.setValue(new TableDataRow<Integer>(worksheetDO.getNumberFormatId()));    
        
        if(worksheetDO.getScriptletId()!=null)
         form.scriptletId.setValue(new TableDataRow<Integer>(worksheetDO.getScriptletId()));                    
        
        if(!form.duplicate) 
            form.id = worksheetDO.getId();
        
        form.totalCapacity.setValue(worksheetDO.getTotalCapacity());
       } else {
           form.id = null;
       }
          
       itemModel = form.worksheetTable.getValue();
       itemModel.clear();
       
        if (worksheetItemList != null) {
         for (int iter = 0; iter < worksheetItemList.size(); iter++) {
           TestWorksheetItemDO worksheetItemDO = (TestWorksheetItemDO)worksheetItemList.get(iter);
           TableDataRow<Integer> row = itemModel.createNewSet();                                
           
           if(!form.duplicate)  
            row.key = worksheetItemDO.getId();                                    
           
           ((IntegerField)row.cells[0]).setValue(worksheetItemDO.getPosition());

           if(worksheetItemDO.getTypeId()!=null) 
            ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>(worksheetItemDO.getTypeId()));
           
           ((StringField)row.cells[2]).setValue(worksheetItemDO.getQcName());
          
           itemModel.add(row);
         }
        }
        
        anaModel = form.worksheetAnalyteTable.getValue();
        anaModel.clear();                  
         
         for (int iter = 0; iter < worksheetAnalyteList.size(); iter++) {
             TestWorksheetAnalyteDO worksheetAnalyteDO = (TestWorksheetAnalyteDO)worksheetAnalyteList.get(iter);
             TableDataRow<Integer> row = anaModel.createNewSet();                     
             IntegerField id = new IntegerField(worksheetAnalyteDO.getId());
             
             if(!form.duplicate)                         
              row.key = worksheetAnalyteDO.getId();                                   
                                     
             row.setData(new IntegerField(worksheetAnalyteDO.getAnalyteId()));            
                                             
             ((StringField)row.cells[0]).setValue(worksheetAnalyteDO.getAnalyteName());
             
             ((CheckField)row.cells[1]).setValue("Y");
             
             ((IntegerField)row.cells[2]).setValue(worksheetAnalyteDO.getRepeat());
             
             if(worksheetAnalyteDO.getFlagId()!=null) 
               ((DropDownField)row.cells[3]).setValue(new TableDataRow<Integer>(worksheetAnalyteDO.getFlagId()));
            
             anaModel.add(row);
          } 
         
         for (int iter = 0; iter < anaIdNameDOList.size(); iter++) {
             IdNameDO idNameDO = (IdNameDO)anaIdNameDOList.get(iter);
             TableDataRow<Integer> row = anaModel.createNewSet();                    
      
             row.setData(new IntegerField(idNameDO.getId()));            
                                             
             ((StringField)row.cells[0]).setValue(idNameDO.getName());
             
             ((CheckField)row.cells[1]).setValue("N");                      
            
             anaModel.add(row);
          }
       
    }
    
    private void fillAnalyteTree(List<TestAnalyteDO> analyteDOList,TestAnalyteForm form) {        
        TreeDataModel model =  form.analyteTree.getValue();
        model.clear();            
        TreeDataItem currGroupItem = null;        
        Integer analyteGroup = new Integer(-999);
        int numGroups = 0;
        for(int iter = 0 ; iter < analyteDOList.size(); iter++){                                    
            TestAnalyteDO analyteDO = analyteDOList.get(iter);
            if(analyteDO.getAnalyteGroup()!=null){                              
                  if(analyteDO.getAnalyteGroup().equals(analyteGroup)){                      
                      currGroupItem.addItem(createAnalyteNode(model,analyteDO, form.duplicate));
                  }else{                        
                      currGroupItem = createGroupNode(numGroups,model);
                      currGroupItem.open = true;
                      numGroups++;
                      model.add(currGroupItem);                                                
                      currGroupItem.addItem(createAnalyteNode(model,analyteDO,form.duplicate));                      
                  }                                                                         
          }else{
              model.add(createAnalyteNode(model,analyteDO,form.duplicate));                   
          }
            
            analyteGroup = analyteDO.getAnalyteGroup();
        }
    }    
   
    
    private List<TestAnalyteDO> getTestAnalyteDOListFromRPC(TestAnalyteForm form,Integer testId){
        TreeDataModel model =  (TreeDataModel)form.analyteTree.getValue();
        int  currGroup = 0;
        int sortOrder = 0;
        List<TestAnalyteDO> analyteDOList = new ArrayList<TestAnalyteDO>();
        for(int iter = 0; iter < model.size(); iter++){
         TreeDataItem item = model.get(iter);
         if("top".equals(item.leafType)){
           currGroup++;
           List<TreeDataItem> itemList = item.getItems();
           TreeDataModel dmodel = (TreeDataModel)item.getData();
            for(int ctr = 0; ctr < itemList.size(); ctr++){
             TreeDataItem chItem = itemList.get(ctr);         
             sortOrder++;
             TestAnalyteDO anaDO = getTestAnalyteDO(chItem,currGroup, testId,sortOrder,false);             
             analyteDOList.add(anaDO);                
          }
            if(dmodel!=null){
               for(int ctr = 0; ctr < dmodel.size(); ctr++){
                TreeDataItem chItem = dmodel.get(ctr);
                TestAnalyteDO anaDO = getTestAnalyteDO(chItem,null, testId,0,true);             
                analyteDOList.add(anaDO);
               }  
            }
         }
         else{
           sortOrder++;
           
           TestAnalyteDO anaDO = getTestAnalyteDO(item,null, testId,sortOrder,false);
           analyteDOList.add(anaDO);
           
         } 
        }
       
        if(model.getDeletions() != null) {
         for(int iter = 0; iter < model.getDeletions().size(); iter++){
            TreeDataItem item = model.getDeletions().get(iter);
            if("top".equals(item.leafType)){
              List<TreeDataItem> itemList = item.getItems();
               for(int ctr = 0; ctr < itemList.size(); ctr++){
                TreeDataItem chItem = itemList.get(ctr);   
                TestAnalyteDO anaDO = getTestAnalyteDO(chItem,null, testId,0,true);
                analyteDOList.add(anaDO);                               
             }
            }
            else{              
                TestAnalyteDO anaDO = getTestAnalyteDO(item,null, testId,0,true);
                analyteDOList.add(anaDO);                               
            } 
           }        
         model.getDeletions().clear();     
        } 
        return analyteDOList;
    }
    
    private TestAnalyteDO getTestAnalyteDO(TreeDataItem chItem,Integer analyteGroup, Integer testId,Integer sortOrder,boolean delete){
        TestAnalyteDO analyteDO = new TestAnalyteDO();         
        analyteDO.setId(chItem.key);        
       
        analyteDO.setAnalyteId((Integer)((DropDownField)chItem.cells[0]).getSelectedKey());
              
        analyteDO.setTypeId((Integer)((DropDownField)chItem.cells[1]).getSelectedKey());
       
        analyteDO.setIsReportable((String)((CheckField)chItem.cells[2]).getValue());                             
              
        analyteDO.setScriptletId((Integer)((DropDownField)chItem.cells[3]).getSelectedKey());       
             
        analyteDO.setResultGroup((Integer)((DropDownField)chItem.cells[4]).getSelectedKey());
        
        analyteDO.setDelete(delete);
        
        analyteDO.setSortOrder(sortOrder);
        
        analyteDO.setAnalyteGroup(analyteGroup);   
        
        analyteDO.setTestId(testId);  
        
        return analyteDO;
    }
    
    private List<TestResultDO> getTestResultDOListFromRPC(TestAnalyteForm form,Integer testId){       
      ArrayList<TableDataModel<TableDataRow<Integer>>> list = form.resultModelCollection;        
      IntegerField id = null;
      IntegerField rg = null;
      IntegerObject valueObj = null;
      IntegerObject valObj = new IntegerObject(-999);
      TableDataModel<TableDataRow<Integer>> model =  null;
            
      CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
      Integer dictId = null;
      Integer entryId = null;
      try {
        dictId = catRemote.getEntryIdForSystemName("test_res_type_dictionary");
      }catch (Exception ex) {
          ex.printStackTrace();
      }
      
      List<TestResultDO> trDOlist = new ArrayList<TestResultDO>();
      
      for(int fiter = 0; fiter < list.size(); fiter++){
          model = (TableDataModel<TableDataRow<Integer>>)list.get(fiter);
                
        for(int iter = 0; iter < model.size(); iter++){
            TableDataRow<Integer> row = model.get(iter);
            TestResultDO resultDO = new TestResultDO();
                      
            resultDO.setId(row.key); 
            
            resultDO.setDelete(false);
            
            resultDO.setUnitOfMeasureId((Integer)((DropDownField)row.cells[0]).getSelectedKey());
                                   
            resultDO.setTypeId((Integer)((DropDownField)row.cells[1]).getSelectedKey());         
            
            if(dictId.equals(resultDO.getTypeId())) {
              try {
                  entryId = catRemote.getEntryIdForEntry((String)((StringField)row.cells[2]).getValue());
               }catch (Exception ex) {
                      ex.printStackTrace();
               }
                resultDO.setValue(entryId.toString());
                resultDO.setDictEntry((String)((StringField)row.cells[2]).getValue());
             } 
             else {
                resultDO.setValue(((StringField)row.cells[2]).getValue());
                resultDO.setDictEntry(null);                 
             }           
            
            resultDO.setQuantLimit((String)((StringField)row.cells[3]).getValue());
            
            resultDO.setContLevel((String)((StringField)row.cells[4]).getValue());
            
            resultDO.setHazardLevel((String)((StringField)row.cells[5]).getValue());                         
            
            resultDO.setFlagsId((Integer)((DropDownField)row.cells[6]).getSelectedKey());
            
            resultDO.setSignificantDigits(((IntegerField)row.cells[7]).getValue());
           
            resultDO.setRoundingMethodId((Integer)((DropDownField)row.cells[8]).getSelectedKey());            
                                    
            resultDO.setResultGroup(fiter+1);
            
            resultDO.setTestId(testId);
            
            resultDO.setSortOrder(iter);
            
            trDOlist.add(resultDO);
        }
        
        if(model.getDeletions() != null) {
         for(int iter = 0; iter < model.getDeletions().size(); iter++){
            TableDataRow<Integer>row = (TableDataRow)model.getDeletions().get(iter);
            TestResultDO resultDO = new TestResultDO();
            
            resultDO.setId(row.key); 
            
            resultDO.setResultGroup(fiter+1);                                    
            resultDO.setDelete(true);                        
            
            trDOlist.add(resultDO);                       
        }
        
        model.getDeletions().clear();
       }  
      }  
        return trDOlist;
    }
    
    private TreeDataItem createAnalyteNode(TreeDataModel model,TestAnalyteDO analyteDO, boolean forDuplicate) {
        TreeDataItem item = model.createTreeItem("analyte");
        TableDataRow<Integer> analyteSet = new TableDataRow<Integer>(analyteDO.getAnalyteId(),new StringObject(analyteDO.getAnalyteName()));       
        TableDataModel<TableDataRow<Integer>> autoModel = new TableDataModel<TableDataRow<Integer>>();
        autoModel.add(analyteSet);
        //autoModel.add(new TableDataRow<Integer>(-1,new StringObject("")));
        ((DropDownField)item.cells[0]).setModel(autoModel);
        item.cells[0].setValue(analyteSet);
        TableDataModel typeModel = getInitialModel(TestMeta.getTestAnalyte().getTypeId());
        ((DropDownField)item.cells[1]).setModel(typeModel);
        item.cells[1].setValue(new TableDataRow<Integer>(analyteDO.getTypeId()));
        item.cells[2].setValue(analyteDO.getIsReportable());
        TableDataModel scrModel = getInitialModel(TestMeta.getTestAnalyte().getScriptletId());
        ((DropDownField)item.cells[3]).setModel(scrModel);
        
        if(analyteDO.getScriptletId()!=null)
         item.cells[3].setValue(new TableDataRow<Integer>(analyteDO.getScriptletId()));       
        
        if(analyteDO.getResultGroup()!=null)
         item.cells[4].setValue(new TableDataRow<Integer>(analyteDO.getResultGroup()));       
        
        if(!forDuplicate)
         item.key = analyteDO.getId();         
        else
         item.key = (analyteDO.getId() * -2);  
                       
        item.setData(new IntegerObject(analyteDO.getAnalyteId()));
        return item;
    }
    
    private TreeDataItem createGroupNode(int id ,TreeDataModel model){
        TreeDataItem item = model.createTreeItem("top");
        item.cells[0].setValue(openElisConstants.getString("analyteGroup"));           
        return item;
      }
        

    private TestDO getTestDOFromRPC(TestForm form) {
        TestDO testDO = new TestDO();
        testDO.setId(form.id.getValue());
        testDO.setName(((String)form.name.getValue()));        
        testDO.setMethodId((Integer)(form.methodId.getSelectedKey()));
        DatetimeRPC activeBegin = form.activeBegin.getValue();
        if (activeBegin != null)
            testDO.setActiveBegin(activeBegin.getDate());

        DatetimeRPC activeEnd = form.activeEnd.getValue();
        if (activeEnd != null)
            testDO.setActiveEnd(activeEnd.getDate());

        testDO.setDescription(form.description.getValue());
        testDO.setIsActive(form.isActive.getValue());
        testDO.setIsReportable(form.isReportable.getValue());

        testDO.setLabelId((Integer)(form.labelId.getSelectedKey()));

        testDO.setLabelQty(form.labelQty.getValue());
        testDO.setReportingDescription((String)form.reportingDescription.getValue());

        testDO.setRevisionMethodId((Integer)form.revisionMethodId.getSelectedKey());

        testDO.setScriptletId((Integer)form.scriptletId.getSelectedKey());
               
        testDO.setTestFormatId((Integer)form.testFormatId.getSelectedKey());
        
        testDO.setTestTrailerId((Integer)form.testTrailerId.getSelectedKey());
        
        testDO.setTimeHolding(form.timeHolding.getValue());
        testDO.setTimeTaAverage(form.timeTaAverage.getValue());
        testDO.setTimeTaMax(form.timeTaMax.getValue());
        testDO.setTimeTaWarning(form.timeTaWarning.getValue());
        testDO.setTimeTransit(form.timeTransit.getValue());
       
        testDO.setSortingMethodId((Integer)form.sortingMethodId.getSelectedKey());

        testDO.setReportingMethodId((Integer)form.reportingMethodId.getSelectedKey());
        
        testDO.setReportingSequence(form.reportingSequence.getValue());
        return testDO;
    }    
    

    private void setRpcErrors(List exceptionList, TestForm form) {
        TableField sampleTypeTable = (TableField)form.sampleType.sampleTypeTable;
        TableField prepTestTable = (TableField)form.prepAndReflex.testPrepTable;        
        TableField testReflexTable = (TableField)form.prepAndReflex.testReflexTable;        
        TableField worksheetTable = (TableField)form.worksheet.worksheetTable; 
        TableField worksheetAnaTable = (TableField)form.worksheet.worksheetAnalyteTable;
        TableField testSectionTable = (TableField)form.sectionTable;         
        TableField testResultsTable = (TableField)form.testAnalyte.testResultsTable;                
        ArrayList<TableDataModel<TableDataRow<Integer>>> cfield = (ArrayList<TableDataModel<TableDataRow<Integer>>>)form.testAnalyte.resultModelCollection;

        // we need to get the keys and look them up in the resource bundle for
        // internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                FieldErrorException ferrex = (FieldErrorException)exceptionList.get(i);

                if (ferrex.getFieldName()
                          .startsWith(TestPrepMetaMap.getTableName() + ":")) {
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                       .substring(TestPrepMetaMap.getTableName()
                                                                                                                 .length() + 1);
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   prepTestTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));                                        
                } else if (ferrex.getFieldName()
                                 .startsWith(TestTypeOfSampleMetaMap.getTableName() + ":")) {
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                       .substring(TestTypeOfSampleMetaMap.getTableName()
                                                                                                                         .length() + 1);
                    int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                    sampleTypeTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                } else if (ferrex.getFieldName()
                                .startsWith(TestReflexMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                      .substring(TestReflexMetaMap.getTableName()
                                                                                                                        .length() + 1);
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   testReflexTable.getField(index, fieldName)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               } else if (ferrex.getFieldName()
                                .startsWith(TestWorksheetItemMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                        .substring(TestWorksheetItemMetaMap.getTableName().length() + 1);
                   
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   worksheetTable.getField(index, fieldName)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage())); 
               } else if (ferrex.getFieldName()
                                .startsWith(TestWorksheetAnalyteMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                        .substring(TestWorksheetAnalyteMetaMap.getTableName().length() + 1);
                   
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   String error = openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage());
                   worksheetAnaTable.getField(index, fieldName).addError(error);
               } else if (ferrex.getFieldName()
                                .startsWith(TestSectionMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                        .substring(TestSectionMetaMap.getTableName().length() + 1);
                   
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   testSectionTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage())); 
               } else if (ferrex.getFieldName()
                                .startsWith(TestResultMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                        .substring(TestResultMetaMap.getTableName().length() + 1);
                   List<String> findexes = testResultsTable.getFieldIndex();
                   int row =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   addErrorToResultField(row, fieldName,findexes,
                      openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage())
                                                         ,cfield);                                         
               }                                 
            } else if (exceptionList.get(i) instanceof FieldErrorException) {
                String nameWithRPC = ((FieldErrorException)exceptionList.get(i)).getFieldName();
                if(nameWithRPC.startsWith("worksheet:")){
                    HashMap<String,AbstractField> map = FormUtil.createFieldMap(form.worksheet);
                    String fieldName = nameWithRPC.substring(10);
                    
                    map.get(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    
               } else {
                    HashMap<String,AbstractField> map = FormUtil.createFieldMap(form);
                    map.get(((FieldErrorException)exceptionList.get(i)).getFieldName())
                       .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               }   
            }
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        form.status = Form.Status.invalid;
    }       

    private void loadDropDown(List<IdNameDO> list, TableDataModel model) {
        TableDataRow<Integer> blankset = new TableDataRow<Integer>(-1,new StringObject(""));
        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            TableDataRow<Integer> set = new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName()));
            model.add(set);
        }
    }    

    private void loadPrepTestDropDown(List<QaEventTestDropdownDO> qaedDOList,
                                      TableDataModel model) {

        TableDataRow<Integer> blankset = new TableDataRow<Integer>(-1,new StringObject(""));
        TableDataRow<Integer> set = null;
        QaEventTestDropdownDO resultDO = null;
        model.add(blankset);

        int i = 0;
        while (i < qaedDOList.size()) {
            resultDO = (QaEventTestDropdownDO)qaedDOList.get(i);
            set = new TableDataRow<Integer>(resultDO.getId(),
                    new StringObject(resultDO.getTest() + " , " + resultDO.getMethod()));                                  
            model.add(set);
            i++;
        }
    }
     
    
    private void addErrorToResultField(int row, String fieldName,List<String> findexes,
                                       String exc, ArrayList<TableDataModel<TableDataRow<Integer>>> cfield) {
        int findex = findexes.indexOf(fieldName);                
        int llim = 0;
        int ulim = 0;
        
        TableDataModel<TableDataRow<Integer>> m = null;
        
        TableDataRow<Integer>errRow = null;       
        
        for(int i = 0; i < cfield.size(); i++) {
             ulim = getUpperLimit(i,cfield);             
             if(llim <= row && row < ulim) {
               m = (TableDataModel<TableDataRow<Integer>>)cfield.get(i);
               errRow = m.get(row - llim);               
               break;    
             }   
             llim = ulim;
        }
        
        System.out.println("error: "+exc);
        System.out.println("row: "+row);
        System.out.println("findex: "+findex);        
        ((AbstractField)errRow.cells[findex]).addError(exc);               
    }
    
    private int getUpperLimit(int i, ArrayList<TableDataModel<TableDataRow<Integer>>> mlist) {
      int ulim = 0; 
        for(int j = -1; j < i; j++) {
         ulim += mlist.get(j+1).size();    
       } 
      return ulim;  
    }    
}