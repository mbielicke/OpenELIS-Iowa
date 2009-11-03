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
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;
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
import org.openelis.gwt.common.data.DataMap;
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
import org.openelis.modules.test.client.DetailsForm;
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
        TestIdNameMethodIdDO testDO = null;
        TestDetailsDO testDetailsDO = null;        
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
    
        rpc.details.duplicate = false;   
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;
        
        testDO = getTestIdNameMethodIdDOFromRPC(rpc);
        // if(((FormRPC)rpcSend.getField("details")).load){
        testDetailsDO = getTestDetailsDOFromRPC(rpc.details);
        // }
        testSectionDOList = getTestSectionsFromRPC(rpc.details,null) ;
            
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
        
        List exceptionList = remote.validateForAdd(testDO,testDetailsDO,
                                                   prepTestDOList,sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList,twsaList,
                                                   testAnalyteDOList,testSectionDOList,
                                                   resultDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc);
    
            return rpc;
        }
    
        try {
            testId = remote.updateTest(testDO,testDetailsDO,prepTestDOList,
                                       sampleTypeDOList,testReflexDOList,worksheetDO,
                                       itemsDOList,twsaList,
                                       testAnalyteDOList,testSectionDOList,
                                       resultDOList);
            testDO = remote.getTestIdNameMethod(testId);
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
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        TestDetailsDO testDetailsDO = null;
        List<TestPrepDO> testPrepDOList = null;
        List<TestTypeOfSampleDO> sampleTypeDOList = null;
        List<TestReflexDO> testReflexDOList= null;
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
        List<TestWorksheetAnalyteDO> twsaList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;  
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;

        testDO = getTestIdNameMethodIdDOFromRPC(rpc);
        IntegerField testId = rpc.id;
        
        rpc.details.duplicate = false;   
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;
        
        if (rpc.details.load){
            testDetailsDO = getTestDetailsDOFromRPC(rpc.details);
            testSectionDOList = getTestSectionsFromRPC(rpc.details,
                                                      testId.getValue());  
        } 

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
        
        List exceptionList = remote.validateForUpdate(testDO,testDetailsDO,
                                                      testPrepDOList,sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList,twsaList,testAnalyteDOList,
                                                      testSectionDOList,resultDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc);

            return rpc;
        }

        try {
            remote.updateTest(testDO,testDetailsDO,
                              testPrepDOList,sampleTypeDOList,
                              testReflexDOList,worksheetDO,
                              itemsDOList,twsaList,
                              testAnalyteDOList, testSectionDOList,
                              resultDOList);
            testDO = remote.getTestIdNameMethod(testId.getValue());
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

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod(rpc.entityKey);
        setFieldsInRPC(rpc, testDO,false);
        
        rpc.details.duplicate = false;   
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;
        

        if (rpc.details.load) {            
            loadTestDetails(rpc.entityKey, rpc.details);
        }

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

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod(rpc.entityKey);
        setFieldsInRPC(rpc, testDO,false);
        
        rpc.details.duplicate = false;   
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;

        String tab = (String)rpc.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails(rpc.entityKey, rpc.details);
        }

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
    
    public void loadTestDetails(Integer key, DetailsForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDetailsDO testDetailsDO = remote.getTestDetails(key);
        List<TestSectionDO> tsDOList = remote.getTestSections(key);
        fillTestDetails(testDetailsDO, form);        
        fillTestSections(tsDOList,form);
        form.load = true;        
    }
    
    public DetailsForm loadTestDetails(DetailsForm rpc) {
        loadTestDetails(rpc.entityKey, rpc);
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

        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        try {
            testDO = remote.getTestIdNameMethodAndLock(rpc.entityKey, SessionManager.getSession().getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        setFieldsInRPC(rpc, testDO, false);
        
        rpc.details.duplicate = false;   
        rpc.sampleType.duplicate = false;
        rpc.prepAndReflex.duplicate = false;
        rpc.worksheet.duplicate = false;
        rpc.testAnalyte.duplicate = false;

        String tab = rpc.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails(rpc.entityKey, rpc.details);
        }

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
    
    public TestGeneralPurposeRPC getTestResultModelMap(TestGeneralPurposeRPC rpc) {
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
              rpc.map.put(entry.getKey().toString(), dataModel);             
          }
        return rpc;
      }
    
    
    public TestGeneralPurposeRPC getResultGroupAnalyteMap(TestGeneralPurposeRPC rpc) {       
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        HashMap<Integer,List<Integer>> listMap = remote.getResultGroupAnalytesMap(rpc.key); 
         for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
           Entry<Integer,List<IdNameDO>> entry = (Entry<Integer,List<IdNameDO>>)iter.next();
           List<Integer> list = (List<Integer>)listMap.get(entry.getKey());              
           TableDataRow<Integer> set = new TableDataRow<Integer>(list.size());                
           for(int i = 0; i < list.size(); i++) {
             set.cells[i] = (new IntegerObject(list.get(i)));  
           }
           rpc.map.put(entry.getKey().toString(), set);             
       }
          return rpc;       
    }
    
    public TestGeneralPurposeRPC getUnitIdNumResMapForTest(TestGeneralPurposeRPC rpc) {
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
       rpc.map.put(entry.getKey().toString(), new IntegerObject(numRes));             
     }
        return rpc; 
    }       
    
    public TestGeneralPurposeRPC getUnitDropdownModel(TestGeneralPurposeRPC rpc) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> unitList = remote.getUnitsOfMeasureForTest(rpc.key);        
        if(unitList.size() > 0) {
            rpc.model = new TableDataModel<TableDataRow<Integer>>();    
            loadDropDown(unitList, rpc.model);
        }       
        return rpc;        
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
         DataMap data = null;
         IntegerField id = null;        
         IntegerField rg = null;
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
             data = new DataMap();  
             rg = new IntegerField(resultDO.getResultGroup());
             
             if(!form.duplicate) {
              id = new IntegerField(resultDO.getId());
              data.put("id", id); 
             } else {
                id = new IntegerField(resultDO.getId() * -2);   
                data.put("id", id);             
             }             
                         
             if(resultDO.getUnitOfMeasureId() != null) {                 

              ((DropDownField<IntegerObject>)row.cells[0]).setValue(new TableDataRow<Integer>(resultDO.getUnitOfMeasureId()));
              data.put("unitId", new IntegerObject(resultDO.getUnitOfMeasureId()));
             } 
             
             ((DropDownField<IntegerObject>)row.cells[1]).setValue(new TableDataRow<Integer>(resultDO.getTypeId()));
             
             if(resultDO.getDictEntry() == null) {
              ((StringField)row.cells[2]).setValue(resultDO.getValue());     
             } else {   
              ((StringField)row.cells[2]).setValue(resultDO.getDictEntry());
             } 
             
             row.setData(data);
                                 
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
        
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod(rpc.entityKey);                       
        
        setFieldsInRPC(rpc, testDO, true);
        
        rpc.details.duplicate = true;   
        rpc.sampleType.duplicate = true;
        rpc.prepAndReflex.duplicate = true;
        rpc.worksheet.duplicate = true;
        rpc.testAnalyte.duplicate = true;
        
        loadTestDetails(rpc.entityKey, rpc.details);
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
    
    private TableDataModel<TableDataRow<Integer>> getTestResultsByGroup(Integer key, Integer resultGroup, TableDataModel model, boolean forDuplicate){
        TableDataRow<Integer> row = null;
        DataMap data = null;
        TestResultDO resultDO = null;
        IntegerField id = null;
        IntegerField rg = null;
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestResultDO> resultDOList = remote.getTestResults(key, resultGroup);
        
        model.clear(); 
        
        for(int iter = 0; iter < resultDOList.size(); iter++){
            row = model.createNewSet();
            resultDO = resultDOList.get(iter);  
            
            data = new DataMap();     
            rg = new IntegerField(resultDO.getResultGroup());   
            
            if(!forDuplicate) {
             id = new IntegerField(resultDO.getId());   
             data.put("id", id);
            } else {
                id = new IntegerField(resultDO.getId() * -2);   
                data.put("id", id); 
            }                            
            
            if(resultDO.getUnitOfMeasureId() != null) {
              ((DropDownField<IntegerObject>)row.cells[0]).setValue(new TableDataRow<Integer>(resultDO.getUnitOfMeasureId()));
              data.put("unitId", new IntegerObject(resultDO.getUnitOfMeasureId()));
            }
            
            ((DropDownField<IntegerObject>)row.cells[1]).setValue(new TableDataRow<Integer>(resultDO.getTypeId()));
            
            if(resultDO.getDictEntry() == null) {
               ((StringField)row.cells[2]).setValue(resultDO.getValue());                
            } else {   
               ((StringField)row.cells[2]).setValue(resultDO.getDictEntry());
            } 
              
            row.setData(data);
            
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

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {

            TableDataRow<Integer> row = model.get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
                testPrepDO.setDelete(false);           

                if(row.getData()!=null){
                    IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");                                             
                    testPrepDO.setId(id.getValue());                                 
                }

            testPrepDO.setTestId(testId);
            
            testPrepDO.setPrepTestId((Integer)((DropDownField)row.cells[0]).getSelectedKey());           

            testPrepDO.setIsOptional((String)((CheckField)row.cells[1]).getValue());

            testPrepDOList.add(testPrepDO);
        }
        
       if(model.getDeletions() != null) { 
        for (int j = 0; j < model.getDeletions().size(); j++) {
            TableDataRow<Integer>row = (TableDataRow)model.getDeletions().get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
            testPrepDO.setDelete(true);            

            if(row.getData()!=null){
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");            
              testPrepDO.setId(id.getValue());                           
            }
            
            testPrepDOList.add(testPrepDO);
          }         
        model.getDeletions().clear();
       } 
        return testPrepDOList;
    }

    private List<TestTypeOfSampleDO> getSampleTypesFromRPC(SampleTypeForm form,
                                                           Integer testId) {
        IntegerField id = null;

        TableDataRow<Integer> row = null;
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.sampleTypeTable.getValue();        
        TestTypeOfSampleDO testTypeOfSampleDO = null;
        DataMap data = null;
        List<TestTypeOfSampleDO> typeOfSampleDOList = new ArrayList<TestTypeOfSampleDO>();

        for (int i = 0; i < model.size(); i++) {
            row = model.get(i);
            testTypeOfSampleDO = new TestTypeOfSampleDO();

            testTypeOfSampleDO.setDelete(false);
            data = (DataMap)row.getData();
            if(data != null) {
             if(data.get("id") != null) {    
              id = (IntegerField)(data.get("id"));
              testTypeOfSampleDO.setId(id.getValue());
             } 
            }
            
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
            
            data = (DataMap)row.getData();
            if(data != null) {
             if(data.get("id") != null) {    
              id = (IntegerField)(data.get("id"));
              testTypeOfSampleDO.setId(id.getValue());
             } 
            }
                        
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
            
            if(row.getData()!=null) {
             IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
             refDO.setId(id.getValue());            
            }
            
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
             
             if(row.getData()!=null) {
                 IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                 refDO.setId(id.getValue());            
             }                      
             
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
    
    private List<TestSectionDO> getTestSectionsFromRPC(DetailsForm form,Integer testId){
        TableDataModel<TableDataRow<Integer>> model = (TableDataModel)form.sectionTable.getValue();
        
        List<TestSectionDO> tsDOList =  new ArrayList<TestSectionDO>();
        
        for(int iter = 0; iter < model.size(); iter++){

            TableDataRow<Integer> row = model.get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            tsDO.setDelete(false);
            
            if(row.getData()!=null){
             IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");                          
             tsDO.setId(id.getValue());
                           
            }
                       
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
            
            if(row.getData()!=null){
                IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                tsDO.setId(id.getValue());                  
            }                       

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

            
            if (row.getData() != null) {
                IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                worksheetItemDO.setId(id.getValue());
            }
         
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

            if (row.getData() != null) {
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
              worksheetItemDO.setId(id.getValue());
            } 
            
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
            
            if (row.getData() != null) {
                id = (IntegerField)((DataMap)row.getData()).get("id");
                if(id!= null)
                 worksheetAnaDO.setId(id.getValue());
                anaId = (IntegerField)((DataMap)row.getData()).get("analyteId");
                worksheetAnaDO.setAnalyteId(anaId.getValue());
            }
            
            worksheetItemDOList.add(worksheetAnaDO);
        }
        
        if(model.getDeletions() != null) {     
         for (int i = 0; i < model.getDeletions().size(); i++) {
            TableDataRow<Integer> row = (TableDataRow)model.getDeletions().get(i);
            TestWorksheetAnalyteDO worksheetAnaDO = new TestWorksheetAnalyteDO();
            IntegerField id = null;
            IntegerField anaId = null;
            
            worksheetAnaDO.setDelete(true);                                                                                                                         
             
            if (row.getData() != null) {
                id = (IntegerField)((DataMap)row.getData()).get("id");
                if(id!= null)
                 worksheetAnaDO.setId(id.getValue());
                anaId = (IntegerField)((DataMap)row.getData()).get("analyteId");
                worksheetAnaDO.setAnalyteId(anaId.getValue());
            }
            
            worksheetItemDOList.add(worksheetAnaDO);
        }
         model.getDeletions().clear();
        } 
        return worksheetItemDOList;
    }
    

    private void setFieldsInRPC(TestForm form, TestIdNameMethodIdDO testDO, boolean forDuplicate) {
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel();
        
        if(!forDuplicate)
         form.id.setValue(testDO.getId());
        else
         form.id.setValue(null);
        
        form.name.setValue(testDO.getName());        

        model.add(new TableDataRow<Integer>(testDO.getMethodId(),new StringObject(testDO.getMethodName())));
        form.methodId.setModel(model);
        form.methodId.setValue(model.get(0));       
    }

    private void fillTestDetails(TestDetailsDO testDetailsDO, DetailsForm form) {
        form.description.setValue(testDetailsDO.getDescription());
        form.reportingDescription.setValue(testDetailsDO.getReportingDescription());
        
        
        if(!form.duplicate) {
         form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveBegin()
                                                                     .getDate()));
         form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveEnd()
                                                                     .getDate()));
         
         form.isActive.setValue(testDetailsDO.getIsActive());         
        } else {
            form.activeBegin.setValue(null);
            form.activeEnd.setValue(null);               
            form.isActive.setValue("N"); 
        }
    
                
        if(testDetailsDO.getTestTrailerId()!=null) {
         form.testTrailerId.setValue(new TableDataRow<Integer>
                    (testDetailsDO.getTestTrailerId()));
        }
        
        form.isReportable.setValue(testDetailsDO.getIsReportable()); 
        
        form.timeTransit.setValue(testDetailsDO.getTimeTransit());
        form.timeHolding.setValue(testDetailsDO.getTimeHolding());
        form.timeTaAverage.setValue(testDetailsDO.getTimeTaAverage());                
        form.timeTaWarning.setValue(testDetailsDO.getTimeTaWarning());
        form.timeTaMax.setValue(testDetailsDO.getTimeTaMax()); 
        
        if(testDetailsDO.getLabelId()!=null) {
         form.labelId.setValue(new TableDataRow<Integer>(testDetailsDO.getLabelId()));
        } 
        
        form.labelQty.setValue(testDetailsDO.getLabelQty());                 
        
        if(testDetailsDO.getScriptletId()!=null) {
         form.scriptletId.setValue(new TableDataRow<Integer>(testDetailsDO.getScriptletId()));
        } 
        
        if(testDetailsDO.getTestFormatId()!=null) {
         form.testFormatId.setValue(new TableDataRow<Integer>(testDetailsDO.getTestFormatId()));   
        }
        
        if(testDetailsDO.getRevisionMethodId()!=null) {
         form.revisionMethodId.setValue(new TableDataRow<Integer>(testDetailsDO.getRevisionMethodId()));
        }
        
        if(testDetailsDO.getSortingMethodId()!=null) {
          form.sortingMethodId.setValue(new TableDataRow<Integer>(testDetailsDO.getSortingMethodId()));
        }   
        
        if(testDetailsDO.getReportingMethodId()!=null) {
          form.reportingMethodId.setValue(new TableDataRow<Integer>(testDetailsDO.getReportingMethodId()));
        }    
        
         form.reportingSequence.setValue(testDetailsDO.getReportingSequence());
        
        
    }
    
    private void fillTestSections(List<TestSectionDO> testSectionDOList, DetailsForm form){
        TableDataModel<TableDataRow<Integer>> model = form.sectionTable.getValue();
        model.clear();
        
        if(testSectionDOList.size()>0){
            for(int iter = 0; iter < testSectionDOList.size(); iter++){
                TestSectionDO sectionDO = (TestSectionDO)testSectionDOList.get(iter);

                TableDataRow<Integer> row = model.createNewSet();
                
                IntegerField id = new IntegerField(sectionDO.getId());
                if(!form.duplicate) {
                 DataMap data = new DataMap();               
                 data.put("id", id);                
                 row.setData(data);
                }                
                
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
                
                IntegerField id = new IntegerField(testPrepDO.getId());
                if(!form.duplicate) {
                 DataMap data = new DataMap();                
                 data.put("id", id);                
                 row.setData(data);
                }
                
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
              
              IntegerField id = new IntegerField(refDO.getId());
              if(!form.duplicate) {
               DataMap data = new DataMap();              
               data.put("id", id);
               row.setData(data);
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
                   (refDO.getTestAnalyteId()*(-2)));   
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
                   (refDO.getTestResultId() * (-2)));  
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
                IntegerField id = new IntegerField(testTypeOfSampleDO.getId());
                IntegerField unitId = null;
                
                DataMap data = new DataMap();
                if(!form.duplicate) 
                 data.put("id", id);                                                               
                
                if(testTypeOfSampleDO.getTypeOfSampleId()!=null)
                 ((DropDownField)row.cells[0]).setValue(new TableDataRow<Integer>(testTypeOfSampleDO.getTypeOfSampleId()));

                if(testTypeOfSampleDO.getUnitOfMeasureId()!=null) {
                 ((DropDownField)row.cells[1]).setValue(new TableDataRow<Integer>(testTypeOfSampleDO.getUnitOfMeasureId()));
                 unitId = new IntegerField(testTypeOfSampleDO.getUnitOfMeasureId());
                } 
                
                data.put("unitId", unitId);
                row.setData(data);
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
           IntegerField id = new IntegerField(worksheetItemDO.getId());
           
           if(!form.duplicate) { 
            DataMap data = new DataMap();           
            data.put("id", id);
            row.setData(data);                         
           }
           
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
             IntegerField anaId = new IntegerField(worksheetAnalyteDO.getAnalyteId());
             DataMap data = new DataMap();
             
             if(!form.duplicate) {                        
              data.put("id", id);                                   
             }
             
             data.put("analyteId", anaId);           
             row.setData(data);            
                                             
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
             IntegerField anaId = new IntegerField(idNameDO.getId());
             DataMap data = new DataMap();             
             
             data.put("analyteId", anaId);           
             row.setData(data);            
                                             
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
        if(chItem.getData()!=null){ 
         IntegerObject id = (IntegerObject)((DataMap)chItem.getData()).get("id");
         analyteDO.setId(id.getValue());
        }
       
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
            
            if(row.getData()!=null){ 
                id = (IntegerField)((DataMap)row.getData()).get("id");
                if(id != null)
                 resultDO.setId(id.getValue()); 
            }
            
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
            
            if(row.getData()!=null){ 
                id = (IntegerField)((DataMap)row.getData()).get("id");
                if(id != null)
                    resultDO.setId(id.getValue());          
            }
            
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
        
        DataMap data = new DataMap();
        
        if(!forDuplicate)
         data.put("id", new IntegerObject(analyteDO.getId()));
        else
         data.put("id", new IntegerObject(analyteDO.getId() * -2));  
        
        data.put("analyteId", new IntegerObject(analyteDO.getAnalyteId()));
        
        item.setData(data);
        return item;
    }
    
    private TreeDataItem createGroupNode(int id ,TreeDataModel model){
        TreeDataItem item = model.createTreeItem("top");
        item.cells[0].setValue(openElisConstants.getString("analyteGroup"));           
        return item;
      }
        

    private TestIdNameMethodIdDO getTestIdNameMethodIdDOFromRPC(TestForm form) {
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        testDO.setId(form.id.getValue());
        testDO.setName(((String)form.name.getValue()));        
        testDO.setMethodId((Integer)(form.methodId.getSelectedKey()));
        return testDO;
    }

    private TestDetailsDO getTestDetailsDOFromRPC(DetailsForm form) {
        TestDetailsDO testDetailsDO = new TestDetailsDO();
        DatetimeRPC activeBegin = form.activeBegin.getValue();
        if (activeBegin != null)
            testDetailsDO.setActiveBegin(activeBegin.getDate());

        DatetimeRPC activeEnd = form.activeEnd.getValue();
        if (activeEnd != null)
            testDetailsDO.setActiveEnd(activeEnd.getDate());

        testDetailsDO.setDescription(form.description.getValue());
        testDetailsDO.setIsActive(form.isActive.getValue());
        testDetailsDO.setIsReportable(form.isReportable.getValue());

        testDetailsDO.setLabelId((Integer)(form.labelId.getSelectedKey()));

        testDetailsDO.setLabelQty(form.labelQty.getValue());
        testDetailsDO.setReportingDescription((String)form.reportingDescription.getValue());

        testDetailsDO.setRevisionMethodId((Integer)form.revisionMethodId.getSelectedKey());

        testDetailsDO.setScriptletId((Integer)form.scriptletId.getSelectedKey());
               
        testDetailsDO.setTestFormatId((Integer)form.testFormatId.getSelectedKey());
        
        testDetailsDO.setTestTrailerId((Integer)form.testTrailerId.getSelectedKey());
        
        testDetailsDO.setTimeHolding(form.timeHolding.getValue());
        testDetailsDO.setTimeTaAverage(form.timeTaAverage.getValue());
        testDetailsDO.setTimeTaMax(form.timeTaMax.getValue());
        testDetailsDO.setTimeTaWarning(form.timeTaWarning.getValue());
        testDetailsDO.setTimeTransit(form.timeTransit.getValue());
       
        testDetailsDO.setSortingMethodId((Integer)form.sortingMethodId.getSelectedKey());

        testDetailsDO.setReportingMethodId((Integer)form.reportingMethodId.getSelectedKey());
        
        testDetailsDO.setReportingSequence(form.reportingSequence.getValue());
        return testDetailsDO;
    }
    
    

    private void setRpcErrors(List exceptionList, TestForm form) {
        TableField sampleTypeTable = (TableField)form.sampleType.sampleTypeTable;
        TableField prepTestTable = (TableField)form.prepAndReflex.testPrepTable;        
        TableField testReflexTable = (TableField)form.prepAndReflex.testReflexTable;        
        TableField worksheetTable = (TableField)form.worksheet.worksheetTable; 
        TableField worksheetAnaTable = (TableField)form.worksheet.worksheetAnalyteTable;
        TableField testSectionTable = (TableField)form.details.sectionTable;         
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
                    
               } else if(nameWithRPC.startsWith("details:")) {
                    HashMap<String,AbstractField> map = FormUtil.createFieldMap(form.details);
                    String fieldName = nameWithRPC.substring(8);
                    
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