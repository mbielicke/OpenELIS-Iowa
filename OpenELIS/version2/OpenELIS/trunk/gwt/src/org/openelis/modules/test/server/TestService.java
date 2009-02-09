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
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
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
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.modules.test.client.DetailsForm;
import org.openelis.modules.test.client.PrepAndReflexForm;
import org.openelis.modules.test.client.SampleTypeForm;
import org.openelis.modules.test.client.TestAnalyteForm;
import org.openelis.modules.test.client.TestForm;
import org.openelis.modules.test.client.TestRPC;
import org.openelis.modules.test.client.WorksheetForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class TestService implements AppScreenFormServiceInt<TestRPC,DataModel<DataSet>>,
                                    AutoCompleteServiceInt{

    private static final int leftTableRowsPerPage = 29;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    private static final TestMetaMap TestMeta = new TestMetaMap();
    
    

    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List testNames;
        // if the rpc is null then we need to get the page
        if (form == null) {

            form = (Form)SessionManager.getSession()
                                                 .getAttribute("TestQuery");

            if (form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

            try {
                testNames = remote.query(form.getFieldMap(),
                                         (model.getPage() * leftTableRowsPerPage),
                                         leftTableRowsPerPage + 1);
            } catch (Exception e) {
                if (e instanceof LastPageException) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                } else {
                    e.printStackTrace();
                    throw new RPCException(e.getMessage());
                }
            }
        } else {
            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

            HashMap<String, AbstractField> fields = form.getFieldMap();
            // fields.remove("contactsTable");

            try {
                testNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("TestQuery", form);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel<DataSet>();
        else 
            model.clear();
        while (i < testNames.size() && i < leftTableRowsPerPage) {
            IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)testNames.get(i);

            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getLastName()
                                                 +", "+resultDO.getFirstName());

            row.setKey(id);
            row.add(name);
            model.add(row);
            i++;
        }

        return model;
    }
    
    public TestRPC commitAdd(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = null;
        TestDetailsDO testDetailsDO = null;        
        List<TestPrepDO> prepTestDOList = null;
        List<TestTypeOfSampleDO> sampleTypeDOList = null;
        List<TestReflexDO> testReflexDOList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;                
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;
    
        Integer testId;
    
        testDO = getTestIdNameMethodIdDOFromRPC(rpc.form);
        // if(((FormRPC)rpcSend.getField("details")).load){
        testDetailsDO = getTestDetailsDOFromRPC(rpc.form.details);
        // }
        testSectionDOList = getTestSectionsFromRPC(rpc.form.details,null) ;
            
        sampleTypeDOList = getSampleTypesFromRPC(rpc.form.sampleType,null);
        
        prepTestDOList = getPrepTestsFromRPC(rpc.form.prepAndReflex,null);
        testReflexDOList = getTestReflexesFromRPC(rpc.form.prepAndReflex,null);
        
        
        //if (((FormRPC)rpcSend.getField("worksheet")).load) {
         itemsDOList = getWorksheetItemsFromRPC(rpc.form.worksheet);               
         worksheetDO = getTestWorkSheetFromRPC(rpc.form.worksheet,null);
        //}
        
       // if (((FormRPC)rpcSend.getField("testAnalyte")).load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.form.testAnalyte,null);
            resultDOList = getTestResultDOListFromRPC(rpc.form.testAnalyte,null);
        //}
        
        List exceptionList = remote.validateForAdd(testDO,testDetailsDO,
                                                   prepTestDOList,sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList,testAnalyteDOList,
                                                   testSectionDOList,resultDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        try {
            testId = remote.updateTest(testDO,testDetailsDO,prepTestDOList,
                                       sampleTypeDOList,testReflexDOList,worksheetDO,
                                       itemsDOList,testAnalyteDOList,testSectionDOList,
                                       resultDOList);
            testDO = remote.getTestIdNameMethod(testId);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        testDO.setId(testId);
        setFieldsInRPC(rpc.form, testDO);
        return rpc;
    }
    
    public TestRPC commitUpdate(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        TestDetailsDO testDetailsDO = null;
        List<TestPrepDO> testPrepDOList = null;
        List<TestTypeOfSampleDO> sampleTypeDOList = null;
        List<TestReflexDO> testReflexDOList= null;
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;  
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;

        testDO = getTestIdNameMethodIdDOFromRPC(rpc.form);
        NumberField testId = rpc.form.id;
        
        if (rpc.form.details.load){
            testDetailsDO = getTestDetailsDOFromRPC(rpc.form.details);
            testSectionDOList = getTestSectionsFromRPC(rpc.form.details,
                                                      (Integer)testId.getValue());  
        } 

        if (rpc.form.prepAndReflex.load) {
            
            testPrepDOList = getPrepTestsFromRPC(rpc.form.prepAndReflex,
                                                      (Integer)testId.getValue());
            
            testReflexDOList = getTestReflexesFromRPC(rpc.form.prepAndReflex
                                                    ,(Integer)testId.getValue());
            
            
        }
        
        if (rpc.form.sampleType.load) {            
            sampleTypeDOList = getSampleTypesFromRPC(rpc.form.sampleType,
                                                      (Integer)testId.getValue());
        }
        
        if (rpc.form.worksheet.load) {
            itemsDOList = getWorksheetItemsFromRPC(rpc.form.worksheet);
            worksheetDO = getTestWorkSheetFromRPC(rpc.form.worksheet,
                                                 (Integer)testId.getValue());
        }
        
        if (rpc.form.testAnalyte.load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.form.testAnalyte,
                                                        (Integer)testId.getValue());
            resultDOList = getTestResultDOListFromRPC(rpc.form.testAnalyte,
                                       (Integer)testId.getValue());
            
            
        }
        
        List exceptionList = remote.validateForUpdate(testDO,testDetailsDO,
                                                      testPrepDOList,sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList,testAnalyteDOList,
                                                      testSectionDOList,resultDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);

            return rpc;
        }

        try {
            remote.updateTest(testDO,testDetailsDO,
                              testPrepDOList,sampleTypeDOList,
                              testReflexDOList,worksheetDO,
                              itemsDOList,testAnalyteDOList, testSectionDOList,
                              resultDOList);
            testDO = remote.getTestIdNameMethod((Integer)testId.getValue());
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList,rpc.form);
            
            return rpc;
            
        }
        setFieldsInRPC(rpc.form, testDO);
        return rpc;
    }
    
    public TestRPC commitDelete(TestRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public TestRPC abort(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod( (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());
        setFieldsInRPC(rpc.form, testDO);

        if (rpc.form.details.load) {            
            loadTestDetails((DataSet)rpc.key, rpc.form.details);
        }

        if (rpc.form.sampleType.load) {            
            loadSampleTypes((DataSet)rpc.key, rpc.form.sampleType);
        }

        if (rpc.form.prepAndReflex.load) {
            loadPrepTestsReflexTests((DataSet)rpc.key, rpc.form.prepAndReflex);
        }
        
        if (rpc.form.worksheet.load) {
            loadWorksheetLayout((DataSet)rpc.key,rpc.form.worksheet);
        }
        
        if (rpc.form.testAnalyte.load) {            
            loadTestAnalyte((DataSet)rpc.key, rpc.form.testAnalyte);
        }

        return rpc;
    }   

    public TestRPC fetch(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());
        setFieldsInRPC(rpc.form, testDO);

        String tab = (String)rpc.form.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails((DataSet)rpc.key, rpc.form.details);
        }

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes((DataSet)rpc.key, rpc.form.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests((DataSet)rpc.key, rpc.form.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout((DataSet)rpc.key, rpc.form.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte((DataSet)rpc.key, rpc.form.testAnalyte);
        }
        
        return rpc;
    }
    
    public Form loadTestDetails(DataSet key, DetailsForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDetailsDO testDetailsDO = remote.getTestDetails((Integer)((NumberObject)key.getKey()).getValue());
        List<TestSectionDO> tsDOList = remote.getTestSections((Integer)((NumberObject)key.getKey()).getValue());
        fillTestDetails(testDetailsDO, form);        
        fillTestSections(tsDOList,form);
        form.load = true;
        return form;
    }
    
    public Form loadSampleTypes(DataSet key, SampleTypeForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestTypeOfSampleDO> list = remote.getTestTypeOfSamples((Integer)((NumberObject)key.getKey()).getValue());
        fillSampleTypes(list, form);
        form.load = true;
        return form;
    }

    public Form loadPrepTestsReflexTests(DataSet key, PrepAndReflexForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestPrepDO> prepList = remote.getTestPreps((Integer)((NumberObject)key.getKey()).getValue());
        List<TestReflexDO>  reflexList = remote.getTestReflexes((Integer)((NumberObject)key.getKey()).getValue());    
        fillPrepTests(prepList, form);        
        fillTestReflexes(reflexList,form,(Integer)((NumberObject)key.getKey()).getValue());
        form.load = true;
        return form;
    }
        
    public Form loadWorksheetLayout(DataSet key, WorksheetForm form){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestWorksheetDO worksheetDO = remote.getTestWorksheet((Integer)((NumberObject)key.getKey()).getValue());
        List<TestWorksheetItemDO> itemDOList = remote.getTestWorksheetItems((Integer)((NumberObject)key.getKey()).getValue());
        fillWorksheet(worksheetDO, itemDOList, form);
        form.load = true;
        return form;
    }
    
    public Form loadTestAnalyte(DataSet key,TestAnalyteForm form){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes((Integer)((NumberObject)key.getKey()).getValue());
        fillAnalyteTree(taList, form);
        fillTestResults(key,form);
        form.load = true;
        return form;
    }        
    
    public TestRPC fetchForUpdate(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        try {
            testDO = remote.getTestIdNameMethodAndLock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(),
                                                       SessionManager.getSession()
                                                                     .getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        setFieldsInRPC(rpc.form, testDO);

        String tab = rpc.form.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails((DataSet)rpc.key, rpc.form.details);
        }

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes((DataSet)rpc.key, rpc.form.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests((DataSet)rpc.key, rpc.form.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout((DataSet)rpc.key, rpc.form.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte((DataSet)rpc.key, rpc.form.testAnalyte);
        }

        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl"));       

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public TestRPC getScreen(TestRPC rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");
        return rpc;
    }
    
    public DataModel getTestAnalyteModel(NumberObject key){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestAnalyteDropDownValues((Integer)key.getValue());
        DataModel model = new DataModel();
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();
            // id
            Integer dropdownId = methodDO.getId();
            // entry
            String dropdownText = methodDO.getName();

            StringObject textObject = new StringObject(dropdownText);
            NumberObject numberId = new NumberObject(dropdownId);

            set.add(textObject);

            set.setKey(numberId);

            model.add(set);
        }
        
        return model;
    }
    
    
    
    public DataModel getTestResultModel(NumberObject testId, NumberObject analyteId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsForTestAnalyte((Integer)testId.getValue(),(Integer)analyteId.getValue());
        DataModel model = new DataModel();
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();
            // id
            Integer dropdownId = methodDO.getId();
            // entry
            String dropdownText = methodDO.getName();

            StringObject textObject = new StringObject(dropdownText);
            NumberObject numberId = new NumberObject(dropdownId);

            set.add(textObject);

            set.setKey(numberId);

            model.add(set);
        }
        
        return model; 
    }
    
    public DataModel<DataSet> getResultGroupModel(NumberObject testId,TestAnalyteForm form){
        //ModelField field  = (ModelField)form.getField("resultGroupDropDown");       
        DataModel<DataSet> model = new DataModel<DataSet>();
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest((Integer)testId.getValue());
       
        DataSet blankset = new DataSet();

        blankset.add(new StringObject(""));

        blankset.setKey(new NumberObject(-1));

        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO rgDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();           

            set.add(new StringObject(rgDO.getId().toString()));

            set.setKey(new NumberObject(rgDO.getId()));

            model.add(set);
        }
        
        //field.setValue(model);
        return model;
        
    }
    
    public NumberObject getGroupCountForTest(NumberObject testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest((Integer)testId.getValue());        
        return new NumberObject(list.size());
    }
    
    public DataModel getTestResultModel(NumberObject testId,Form form){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsforTest((Integer)testId.getValue());
             DataModel dmodel = new DataModel();
             DataSet blankset = new DataSet();             
               
             StringObject blankStringId = new StringObject();

             blankStringId.setValue("");
             blankset.add(blankStringId);

             NumberObject blankNumberId = new NumberObject(-1);
             blankset.setKey(blankNumberId);

             dmodel.add(blankset);

             for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                 IdNameDO methodDO = (IdNameDO)iterator.next();

                 DataSet set = new DataSet();
                 // id
                 Integer dropdownId = methodDO.getId();
                 // entry
                 String dropdownText = methodDO.getName();

                 StringObject textObject = new StringObject(dropdownText);
                 NumberObject numberId = new NumberObject(dropdownId);

                 set.add(textObject);

                 set.setKey(numberId);

                 dmodel.add(set);
             }
         return dmodel;
    }
    
    public DataModel getTestResultModel(NumberObject testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsforTest((Integer)testId.getValue());
             DataModel model = new DataModel();
             DataSet blankset = new DataSet();

             StringObject blankStringId = new StringObject();

             blankStringId.setValue("");
             blankset.add(blankStringId);

             NumberObject blankNumberId = new NumberObject(-1);
             blankset.setKey(blankNumberId);

             model.add(blankset);

             for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                 IdNameDO methodDO = (IdNameDO)iterator.next();

                 DataSet set = new DataSet();
                 // id
                 Integer dropdownId = methodDO.getId();
                 // entry
                 String dropdownText = methodDO.getName();

                 StringObject textObject = new StringObject(dropdownText);
                 NumberObject numberId = new NumberObject(dropdownId);

                 set.add(textObject);

                 set.setKey(numberId);

                 model.add(set);
             }
         return model;
    }    
    
    public DataMap getTestResultModelMap(NumberObject testId,DataMap dataMap) {
      TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
      HashMap<Integer,List<IdNameDO>> listMap = remote.getAnalyteResultsMap((Integer)testId.getValue()); 
       for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
              Entry<Integer,List<IdNameDO>> entry = (Entry<Integer,List<IdNameDO>>)iter.next();
              List<IdNameDO> list = (List<IdNameDO>)listMap.get(entry.getKey());              
              DataModel dataModel = new DataModel();
              loadDropDown(list, dataModel);
              dataMap.put(entry.getKey().toString(), dataModel);             
          }
        return dataMap;
      }
    
    public DataMap getResultGroupAnalyteMap(NumberObject testId,DataMap dataMap) {       
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        HashMap<Integer,List<Integer>> listMap = remote.getResultGroupAnalytesMap((Integer)testId.getValue()); 
         for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
           Entry<Integer,List<IdNameDO>> entry = (Entry<Integer,List<IdNameDO>>)iter.next();
           List<Integer> list = (List<Integer>)listMap.get(entry.getKey());              
           DataSet set = new DataSet();                
           for(int i = 0; i < list.size(); i++) {
             set.add(new NumberObject(list.get(i)));  
           }
           dataMap.put(entry.getKey().toString(), set);             
       }
          return dataMap;       
    }
    
    public DataMap getUnitIdNumResMapForTest(NumberObject testId,DataMap dataMap) {
     TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
     HashMap<Integer,Integer> listMap  = null;     
     try{
      listMap = remote.getUnitIdNumResMapForTest((Integer)testId.getValue());
     } catch(Exception ex) {
         ex.printStackTrace();
     }
     for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
       Entry<Integer,Integer> entry = (Entry<Integer,Integer>)iter.next();
       Integer numRes = (Integer)listMap.get(entry.getKey());                                                        
       dataMap.put(entry.getKey().toString(), new NumberObject(numRes));             
     }
        return dataMap; 
    }
    
    public DataModel getUnitDropdownModel(NumberObject testId) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> unitList = remote.getUnitsOfMeasureForTest((Integer)testId.getValue());
        DataModel unitModel = null;
        if(unitList.size() > 0) {
            unitModel = new DataModel<DataSet>();    
            loadDropDown(unitList, unitModel);
        }       
        return unitModel;        
    } 
    
    public DataModel getInitialModel(StringObject catObj) {
        String cat = (String)catObj.getValue();
        DataModel model = new DataModel();
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
        }

        if (values != null) {
            loadDropDown(values, model);
        }

        return model;
    }    

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {         
        
        //if(("analyte").equals(cat)){ 
         TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
         List entries = remote.getMatchingEntries(match.trim()+"%", 10,cat);
         DataModel dataModel = new DataModel();
         for (Iterator iter = entries.iterator(); iter.hasNext();) {
             
             IdNameDO element = (IdNameDO)iter.next();
             Integer entryId = element.getId();                   
             String entryText = element.getName();
             
             DataSet data = new DataSet();
             
             NumberObject idObject = new NumberObject(entryId);
             data.setKey(idObject);
             
             StringObject nameObject = new StringObject(entryText);
             data.add(nameObject);
                          
             dataModel.add(data);
         }       
         
         return dataModel;
        //}

     }
    
    public StringObject getCategorySystemName(NumberObject entryId){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        String systemName = null;
        try{
          systemName = remote.getSystemNameForEntryId((Integer)entryId.getValue());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        StringObject strObj = new StringObject(systemName);
        return strObj;
    }
    
    public NumberObject getEntryId(StringObject entry){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer entryId = null;
        NumberObject numObj = null;
        try{
            entryId = remote.getEntryIdForEntry((String)entry.getValue());
            numObj = new NumberObject(entryId);
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return numObj;
    }
    
    public Form fillTestResults(DataSet key, TestAnalyteForm form){     
        try{ 
         DataSet<Data> row = null;
         DataMap data = null;
         NumberField id = null;        
         NumberField rg = null;
         TestResultDO resultDO = null;
         TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");               
         DataModel<DataSet> model = form.testResultsTable.getValue();
         List<IdNameDO> rglist = remote.getResultGroupsForTest((Integer)((NumberObject)key.getKey()).getValue());                
         
         model.clear();
         
         if(rglist.size() > 0) {        
          List<TestResultDO> resultDOList = remote.getTestResults((Integer)((NumberObject)key.getKey()).getValue(),1);                          
           for(int iter = 0; iter < resultDOList.size(); iter++){
             row = model.createNewSet();
             resultDO = resultDOList.get(iter);  
             id = new NumberField(resultDO.getId());
             data = new DataMap();  
             rg = new NumberField(resultDO.getResultGroup());
             data.put("id", id);
             data.put("resGrp", rg);
                         
             if(resultDO.getUnitOfMeasureId() != null) {                 
              row.get(0).setValue(new DataSet<NumberObject>(new NumberObject(resultDO.getUnitOfMeasureId())));
              data.put("unitId", new NumberObject(resultDO.getUnitOfMeasureId()));
             } 
             
             row.get(1).setValue(new DataSet<NumberObject>(new NumberObject(resultDO.getTypeId())));
             
             if(resultDO.getDictEntry() == null) {
              row.get(2).setValue(resultDO.getValue());     
              data.put("value", new NumberObject(-999));
             } else {
              data.put("value", new NumberObject(new Integer(resultDO.getValue())));   
              row.get(2).setValue(resultDO.getDictEntry());
             } 
             
             row.setData(data);
                                 
             row.get(3).setValue(resultDO.getQuantLimit());
             row.get(4).setValue(resultDO.getContLevel());
             row.get(5).setValue(resultDO.getHazardLevel()); 
             
             if(resultDO.getFlagsId()!=null)
              row.get(6).setValue(new DataSet<NumberObject>(new NumberObject(resultDO.getFlagsId())));                                              
             
             row.get(7).setValue(resultDO.getSignificantDigits());   
             
             if(resultDO.getRoundingMethodId()!=null)
                 row.get(8).setValue(new DataSet<NumberObject>(new NumberObject(resultDO.getRoundingMethodId())));
             
             model.add(row);
          }
         }          
        } catch(Exception ex) {
            ex.printStackTrace();
        }    
        return form;
     }
     
     public DataModel loadTestResultsByGroup(NumberObject testId, NumberObject resultGroup,DataModel<DataSet> model){
         DataSet<Data> row = null;
         DataMap data = null;
         TestResultDO resultDO = null;
         NumberField id = null;
         NumberField rg = null;
         TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
         List<TestResultDO> resultDOList = remote.getTestResults((Integer)testId.getValue(), (Integer)resultGroup.getValue());
         
         model.clear(); 
         
         for(int iter = 0; iter < resultDOList.size(); iter++){
             row = model.createNewSet();
             resultDO = resultDOList.get(iter);  
             id = new NumberField(resultDO.getId());
             data = new DataMap();     
             rg = new NumberField(resultDO.getResultGroup());
             data.put("id", id);   
             data.put("resGrp", rg);
             
             if(resultDO.getUnitOfMeasureId() != null) {
               row.get(0).setValue(new DataSet(new NumberObject(resultDO.getUnitOfMeasureId())));
               data.put("unitId", new NumberObject(resultDO.getUnitOfMeasureId()));
             }
             
             row.get(1).setValue(new DataSet(new NumberObject(resultDO.getTypeId())));
             
             if(resultDO.getDictEntry() == null) {
                row.get(2).setValue(resultDO.getValue()); 
                data.put("value", new NumberObject(-999));
             } else {
                data.put("value", new NumberObject(new Integer(resultDO.getValue())));   
                row.get(2).setValue(resultDO.getDictEntry());
             } 
                
             row.setData(data);
             
             row.get(4).setValue(resultDO.getContLevel());
             row.get(5).setValue(resultDO.getHazardLevel()); 
             
             if(resultDO.getFlagsId()!=null)
              row.get(6).setValue(new DataSet(new NumberObject(resultDO.getFlagsId())));                                              
             
             row.get(7).setValue(resultDO.getSignificantDigits());   
             
             if(resultDO.getRoundingMethodId()!=null)
              row.get(8).setValue(new DataSet(new NumberObject(resultDO.getRoundingMethodId())));
             
             model.add(row);
         }
         
        return model; 
     }    
    
    private List<TestPrepDO> getPrepTestsFromRPC(PrepAndReflexForm form,Integer testId) {

        DataModel<DataSet> model = (DataModel)form.testPrepTable.getValue();

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {
            DataSet<Data> row = model.get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
                testPrepDO.setDelete(false);           

                if(row.getData()!=null){
                    NumberField id = (NumberField)((DataMap)row.getData()).get("id");                                             
                    testPrepDO.setId((Integer)id.getValue());                                 
                }

            testPrepDO.setTestId(testId);
            
            testPrepDO.setPrepTestId((Integer)((DropDownField)row.get(0)).getSelectedKey());           

            testPrepDO.setIsOptional((String)((CheckField)row.get(1)).getValue());

            testPrepDOList.add(testPrepDO);
        }
        
        for (int j = 0; j < model.getDeletions().size(); j++) {
            DataSet row = (DataSet)model.getDeletions().get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
            testPrepDO.setDelete(true);            

            if(row.getData()!=null){
              NumberField id = (NumberField)((DataMap)row.getData()).get("id");            
              testPrepDO.setId((Integer)id.getValue());                           
            }
            
            testPrepDOList.add(testPrepDO);
        }

        model.getDeletions().clear(); 
        return testPrepDOList;
    }

    private List<TestTypeOfSampleDO> getSampleTypesFromRPC(SampleTypeForm form,
                                                           Integer testId) {
        NumberField id = null;
        DataSet<Data> row = null;
        DataModel<DataSet> model = (DataModel)form.sampleTypeTable.getValue();        
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
              id = (NumberField)(data.get("id"));
              testTypeOfSampleDO.setId((Integer)id.getValue());
             } 
            }
            
            testTypeOfSampleDO.setTestId(testId);
            
            testTypeOfSampleDO.setTypeOfSampleId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                                        
            testTypeOfSampleDO.setUnitOfMeasureId((Integer)((DropDownField)row.get(1)).getSelectedKey());
              
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            row = (DataSet)model.getDeletions().get(i);
            testTypeOfSampleDO = new TestTypeOfSampleDO();
            
            testTypeOfSampleDO.setDelete(true);
            
            data = (DataMap)row.getData();
            if(data != null) {
             if(data.get("id") != null) {    
              id = (NumberField)(data.get("id"));
              testTypeOfSampleDO.setId((Integer)id.getValue());
             } 
            }
                        
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        model.getDeletions().clear();
        return typeOfSampleDOList;
    }
    
    private List<TestReflexDO> getTestReflexesFromRPC(PrepAndReflexForm form,Integer testId){
        DataModel<DataSet> model = (DataModel)form.testReflexTable.getValue();
        List<TestReflexDO> list = new ArrayList<TestReflexDO>();
         for (int i = 0; i < model.size(); i++) {
            DataSet<Data> row = model.get(i);
            TestReflexDO refDO = new TestReflexDO();
            
            if(row.getData()!=null) {
             NumberField id = (NumberField)((DataMap)row.getData()).get("id");
             refDO.setId((Integer)id.getValue());            
            }
            
            refDO.setTestId(testId);
            
            refDO.setDelete(false);            
                                      
            refDO.setAddTestId((Integer)((DropDownField)row.get(0)).getSelectedKey());                    
             
            refDO.setTestAnalyteId((Integer)((DropDownField)row.get(1)).getSelectedKey());                                    
        
            refDO.setTestResultId((Integer)((DropDownField)row.get(2)).getSelectedKey());                    
      
            refDO.setFlagsId((Integer)((DropDownField)row.get(3)).getSelectedKey());                                
                
            list.add(refDO);
         }
         for (int i = 0; i < model.getDeletions().size(); i++) {
             DataSet row = (DataSet)model.getDeletions().get(i);
             TestReflexDO refDO = new TestReflexDO();
             
             if(row.getData()!=null) {
                 NumberField id = (NumberField)((DataMap)row.getData()).get("id");
                 refDO.setId((Integer)id.getValue());            
             }                      
             
             refDO.setDelete(true);                                                      
             
             list.add(refDO);
          }
         model.getDeletions().clear();
         return list;
    }
    
    private TestWorksheetDO getTestWorkSheetFromRPC(WorksheetForm form, Integer testId){
      TestWorksheetDO worksheetDO = null;
      Integer bc = (Integer)form.getFieldValue(TestMeta.getTestWorksheet().getBatchCapacity());
      if(bc!=null){
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();   
        worksheetDO.setBatchCapacity(bc);
      }  
      
      if(form.id!=null){
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();   
       worksheetDO.setId(form.id);
      } 
      
      if(form.formatId.getSelectedKey()!=null ){ 
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();  
       worksheetDO.setNumberFormatId((Integer)form.formatId.getSelectedKey());
      }  
         
      if(testId!=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();  
          worksheetDO.setTestId(testId);
      } 
         
      if(form.scriptletId.getValue()!=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();  
          worksheetDO.setScriptletId((Integer)form.scriptletId.getSelectedKey());
      }  

      
      Integer tc = (Integer)form.totalCapacity.getValue();      
      if(tc !=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();   
          worksheetDO.setTotalCapacity(tc);
      }
       
      return worksheetDO;
    }
    
    private List<TestSectionDO> getTestSectionsFromRPC(DetailsForm form,Integer testId){
        DataModel<DataSet> model = (DataModel)form.sectionTable.getValue();
        
        List<TestSectionDO> tsDOList =  new ArrayList<TestSectionDO>();
        
        for(int iter = 0; iter < model.size(); iter++){
            DataSet<Data> row = model.get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            tsDO.setDelete(false);
            
            if(row.getData()!=null){
             NumberField id = (NumberField)((DataMap)row.getData()).get("id");                          
             tsDO.setId((Integer)id.getValue());
                           
            }
                       
            tsDO.setSectionId((Integer)((DropDownField)row.get(0)).getSelectedKey());       
            tsDO.setFlagId((Integer)((DropDownField)row.get(1)).getSelectedKey());            
                        
            tsDO.setTestId(testId);
            tsDOList.add(tsDO);            
        }
        
        for(int iter = 0; iter < model.getDeletions().size(); iter++){
            DataSet row = (DataSet)model.getDeletions().get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            
            tsDO.setDelete(true);
            
            if(row.getData()!=null){
                NumberField id = (NumberField)((DataMap)row.getData()).get("id");
                tsDO.setId((Integer)id.getValue());                  
            }                       

            tsDOList.add(tsDO);               
        }
        
        model.getDeletions().clear();
        return tsDOList;
    }
    
    private List<TestWorksheetItemDO> getWorksheetItemsFromRPC(WorksheetForm form) {
        DataModel<DataSet> model = (DataModel)form.worksheetTable.getValue();
        
        List<TestWorksheetItemDO> worksheetItemDOList = new ArrayList<TestWorksheetItemDO>();

        for (int i = 0; i < model.size(); i++) {
            DataSet<Data> row = model.get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(false);            

            
            if (row.getData() != null) {
                NumberField id = (NumberField)((DataMap)row.getData()).get("id");
                worksheetItemDO.setId((Integer)id.getValue());
            }
         
            worksheetItemDO.setTestWorksheetId((Integer)((NumberField)form.
                             getField(TestMeta.getTestWorksheet().getId())).getValue());      
            worksheetItemDO.setPosition((Integer)((NumberField)row.get(0)).getValue());               
            worksheetItemDO.setTypeId((Integer)((DropDownField)row.get(1)).getSelectedKey());                         
            worksheetItemDO.setQcName((String)((StringField)row.get(2)).getValue()); 
                        
            worksheetItemDOList.add(worksheetItemDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet row = (DataSet)model.getDeletions().get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(true);            

            if (row.getData() != null) {
              NumberField id = (NumberField)((DataMap)row.getData()).get("id");
              worksheetItemDO.setId((Integer)id.getValue());
            } 
            
            worksheetItemDOList.add(worksheetItemDO);
        }
        model.getDeletions().clear();
        return worksheetItemDOList;
    }
    

    private void setFieldsInRPC(TestForm form, TestIdNameMethodIdDO testDO) {
        DataModel model = new DataModel();
        form.id.setValue(testDO.getId());
        form.name.setValue(testDO.getName());        
        model.add(new NumberObject(testDO.getMethodId()),new StringObject(testDO.getMethodName()));
        form.methodId.setModel(model);
        form.methodId.setValue(model.get(0));       
    }

    private void fillTestDetails(TestDetailsDO testDetailsDO, DetailsForm form) {
        form.description.setValue(testDetailsDO.getDescription());
        form.reportingDescription.setValue(testDetailsDO.getReportingDescription());
        form.isActive.setValue(testDetailsDO.getIsActive());        
        form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveBegin()
                                                                     .getDate()));
        form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveEnd()
                                                                     .getDate()));        
        form.isReportable.setValue(testDetailsDO.getIsReportable()); 
        
        if(testDetailsDO.getTestTrailerId()!=null) {
         form.testTrailerId.setValue(new DataSet<NumberObject>
                    (new NumberObject(testDetailsDO.getTestTrailerId())));
        }
        
        form.timeTransit.setValue(testDetailsDO.getTimeTransit());
        form.timeHolding.setValue(testDetailsDO.getTimeHolding());
        form.timeTaAverage.setValue(testDetailsDO.getTimeTaAverage());                
        form.timeTaWarning.setValue(testDetailsDO.getTimeTaWarning());
        form.timeTaMax.setValue(testDetailsDO.getTimeTaMax()); 
        
        if(testDetailsDO.getLabelId()!=null) {
         form.labelId.setValue(new DataSet<NumberObject>
                           (new NumberObject(testDetailsDO.getLabelId())));
        } 
        
        form.labelQty.setValue(testDetailsDO.getLabelQty());                 
        
        if(testDetailsDO.getScriptletId()!=null) {
         form.scriptletId.setValue(new DataSet<NumberObject>
                   (new NumberObject(testDetailsDO.getScriptletId())));
        } 
        
        if(testDetailsDO.getTestFormatId()!=null) {
         form.testFormatId.setValue(new DataSet<NumberObject>
                  (new NumberObject(testDetailsDO.getTestFormatId())));   
        }
        
        if(testDetailsDO.getRevisionMethodId()!=null) {
         form.revisionMethodId.setValue(new DataSet<NumberObject>
               (new NumberObject(testDetailsDO.getRevisionMethodId())));
        }
        
        if(testDetailsDO.getSortingMethodId()!=null) {
          form.sortingMethodId.setValue(new DataSet<NumberObject>
             (new NumberObject(testDetailsDO.getSortingMethodId())));
        }   
        
        if(testDetailsDO.getReportingMethodId()!=null) {
          form.reportingMethodId.setValue(new DataSet<NumberObject>
              (new NumberObject(testDetailsDO.getReportingMethodId())));
        }    
        
         form.reportingSequence.setValue(testDetailsDO.getReportingSequence());
        
        
    }
    
    private void fillTestSections(List<TestSectionDO> testSectionDOList, DetailsForm form){
        DataModel<DataSet> model = form.sectionTable.getValue();
        model.clear();
        
        if(testSectionDOList.size()>0){
            for(int iter = 0; iter < testSectionDOList.size(); iter++){
                TestSectionDO sectionDO = (TestSectionDO)testSectionDOList.get(iter);
                
                DataSet<Data> row = model.createNewSet();
                
                NumberField id = new NumberField(sectionDO.getId());
                
                DataMap data = new DataMap();
                
                data.put("id", id);
                
                row.setData(data);
                               
                row.get(0).setValue(new DataSet<NumberObject>
                           (new NumberObject(sectionDO.getSectionId())));

                row.get(1).setValue(new DataSet<NumberObject>
                               (new NumberObject(sectionDO.getFlagId())));

                model.add(row);                              
                
            }
        }
    }

    private void fillPrepTests(List<TestPrepDO> testPrepDOList,
                               PrepAndReflexForm form) {

        DataModel<DataSet> model = form.testPrepTable.getValue();
        model.clear();
        if (testPrepDOList.size() > 0) {
            for (int iter = 0; iter < testPrepDOList.size(); iter++) {
                TestPrepDO testPrepDO = (TestPrepDO)testPrepDOList.get(iter);

                DataSet<Data> row = model.createNewSet();
                
                NumberField id = new NumberField(testPrepDO.getId());

                DataMap data = new DataMap();
                
                data.put("id", id);
                
                row.setData(data);
                
               if(testPrepDO.getPrepTestId()!=null) { 
                 row.get(0).setValue(new DataSet<NumberObject>
                   (new NumberObject(testPrepDO.getPrepTestId())));
               }
               
                row.get(1).setValue(testPrepDO.getIsOptional());

                model.add(row);
            }
            
        }
    }
    
    private void fillTestReflexes(List<TestReflexDO> testReflexDOList,
                                  PrepAndReflexForm form, Integer testId){
        DataModel<DataSet> model = form.testReflexTable.getValue();
        model.clear();        
        if(testReflexDOList.size() > 0){
            Integer unselVal = new Integer(-1);
            for(int iter = 0; iter < testReflexDOList.size(); iter++){
              TestReflexDO refDO = testReflexDOList.get(iter);
              DataSet<Data> row = model.createNewSet();
              
              NumberField id = new NumberField(refDO.getId());
              DataMap data = new DataMap();
              
              data.put("id", id);
              row.setData(data);
              
              if(refDO.getAddTestId()!=null) {
               row.get(0).setValue(new DataSet<NumberObject>
                 (new NumberObject(refDO.getAddTestId())));
              } 

              if(refDO.getTestAnalyteId()!=null) {
               row.get(1).setValue(new DataSet<NumberObject>
                     (new NumberObject(refDO.getTestAnalyteId())));
               if(! (unselVal).equals(refDO.getTestAnalyteId())) {
                DataModel dmodel = getTestResultModel(new NumberObject(testId),
                                                      new NumberObject(refDO.getTestAnalyteId()));
                ((DropDownField)row.get(2)).setModel(dmodel);
               } 
              } 
              
              if(refDO.getTestResultId()!=null) {
               row.get(2).setValue(new DataSet<NumberObject>
                  (new NumberObject(refDO.getTestResultId())));
              } 
              
              if(refDO.getFlagsId()!=null) {
               row.get(3).setValue(new DataSet<NumberObject>
                             (new NumberObject(refDO.getFlagsId())));
              } 
                           
               model.add(row);
            }
        }
        
    }
        

    private void fillSampleTypes(List<TestTypeOfSampleDO> sampleTypeDOList,
                                 SampleTypeForm form) {

        DataModel<DataSet> model = form.sampleTypeTable.getValue();
        model.clear();

        if (sampleTypeDOList.size() > 0) {
            for (int iter = 0; iter < sampleTypeDOList.size(); iter++) {
                TestTypeOfSampleDO testTypeOfSampleDO = (TestTypeOfSampleDO)sampleTypeDOList.get(iter);

                DataSet<Data> row = model.createNewSet();
                // new TableRow();
                NumberField id = new NumberField(testTypeOfSampleDO.getId());
                NumberField unitId = null;
                
                DataMap data = new DataMap();
                data.put("id", id);                                                               
                
                if(testTypeOfSampleDO.getTypeOfSampleId()!=null)
                 row.get(0).setValue(new DataSet(new NumberObject(testTypeOfSampleDO.getTypeOfSampleId())));

                if(testTypeOfSampleDO.getUnitOfMeasureId()!=null) {
                 row.get(1).setValue(new DataSet(new NumberObject(testTypeOfSampleDO.getUnitOfMeasureId())));
                 unitId = new NumberField(testTypeOfSampleDO.getUnitOfMeasureId());
                } 
                
                data.put("unitId", unitId);
                row.setData(data);
                model.add(row);
            }

        }

    }
    
    private void fillWorksheet(TestWorksheetDO worksheetDO, 
                               List<TestWorksheetItemDO> worksheetItemList,
                               WorksheetForm form){
       if(worksheetDO!=null){ 
        form.batchCapacity.setValue(worksheetDO.getBatchCapacity());
        
        if(worksheetDO.getNumberFormatId()!=null)
         form.formatId.setValue(new DataSet(new NumberObject(worksheetDO.getNumberFormatId())));    
        
        if(worksheetDO.getScriptletId()!=null)
         form.scriptletId.setValue(new DataSet(new NumberObject(worksheetDO.getScriptletId())));    
        
        form.id = worksheetDO.getId();  
        
        form.totalCapacity.setValue(worksheetDO.getTotalCapacity());
       } 
        DataModel<DataSet> model = form.worksheetTable.getValue();
        model.clear();

        if (worksheetItemList.size() > 0) {
         for (int iter = 0; iter < worksheetItemList.size(); iter++) {
             TestWorksheetItemDO worksheetItemDO = (TestWorksheetItemDO)worksheetItemList.get(iter);

           DataSet<Data> row = model.createNewSet();                     

           NumberField id = new NumberField(worksheetItemDO.getId());

           DataMap data = new DataMap();
           
           data.put("id", id);
           row.setData(data);                         

           row.get(0).setValue(worksheetItemDO.getPosition());

           if(worksheetItemDO.getTypeId()!=null) 
            row.get(1).setValue(new DataSet(new NumberObject(worksheetItemDO.getTypeId())));
           
           row.get(2).setValue(worksheetItemDO.getQcName());

           model.add(row);
        }

      }
        
    }
    
    private void fillAnalyteTree(List<TestAnalyteDO> analyteDOList,TestAnalyteForm form){        
        TreeDataModel model =  form.analyteTree.getValue();
        model.clear();
        TreeDataItem currGroupItem = null;
        
        Integer analyteGroup = new Integer(-999);
        int numGroups = 0;
        for(int iter = 0 ; iter < analyteDOList.size(); iter++){                                    
            TestAnalyteDO analyteDO = analyteDOList.get(iter);
            if(analyteDO.getAnalyteGroup()!=null){                              
                  if(analyteDO.getAnalyteGroup().equals(analyteGroup)){                      
                      currGroupItem.addItem(createAnalyteNode(model,analyteDO));
                  }else{                        
                          currGroupItem = createGroupNode(numGroups,model);
                          currGroupItem.open = true;
                          numGroups++;
                          model.add(currGroupItem);                                                
                      currGroupItem.addItem(createAnalyteNode(model,analyteDO));
                      
                  }                  
                                                       
          }else{
              model.add(createAnalyteNode(model,analyteDO));
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
        return analyteDOList;
    }
    
    private TestAnalyteDO getTestAnalyteDO(TreeDataItem chItem,Integer analyteGroup, Integer testId,Integer sortOrder,boolean delete){
        TestAnalyteDO analyteDO = new TestAnalyteDO(); 
        if(chItem.getData()!=null){ 
         NumberObject id = (NumberObject)((DataMap)chItem.getData()).get("id");
         analyteDO.setId((Integer)id.getValue());
        }
       
        analyteDO.setAnalyteId((Integer)((DropDownField)chItem.get(0)).getSelectedKey());
              
        analyteDO.setTypeId((Integer)((DropDownField)chItem.get(1)).getSelectedKey());
       
        analyteDO.setIsReportable((String)((CheckField)chItem.get(2)).getValue());                             
              
        analyteDO.setScriptletId((Integer)((DropDownField)chItem.get(3)).getSelectedKey());       
             
        analyteDO.setResultGroup((Integer)((DropDownField)chItem.get(4)).getSelectedKey());
        
        analyteDO.setDelete(delete);
        
        analyteDO.setSortOrder(sortOrder);
        
        analyteDO.setAnalyteGroup(analyteGroup);   
        
        analyteDO.setTestId(testId);  
        
        return analyteDO;
    }
    
    private List<TestResultDO> getTestResultDOListFromRPC(TestAnalyteForm form,Integer testId){
        
      //CollectionField field = (CollectionField)form.getField("resultModelCollection");
      ArrayList<DataModel<DataSet>> list = form.resultModelCollection;        
      //ArrayList list = field.getValue(); 
      NumberField id = null;
      NumberField rg = null;
      NumberObject valueObj = null;
      NumberObject valObj = new NumberObject(-999);
      
      List<TestResultDO> trDOlist = new ArrayList<TestResultDO>();
      
      for(int fiter = 0; fiter < list.size(); fiter++){
        DataModel<DataSet> model = (DataModel<DataSet>)list.get(fiter);
                
        for(int iter = 0; iter < model.size(); iter++){
            DataSet<Data> row = model.get(iter);
            TestResultDO resultDO = new TestResultDO();
            
            if(row.getData()!=null){ 
                id = (NumberField)((DataMap)row.getData()).get("id");
                resultDO.setId((Integer)id.getValue()); 
                valueObj = (NumberObject)((DataMap)row.getData()).get("value");
                rg = (NumberField)((DataMap)row.getData()).get("resGrp");
            }
            
            resultDO.setDelete(false);
            
            resultDO.setUnitOfMeasureId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                                   
            resultDO.setTypeId((Integer)((DropDownField)row.get(1)).getSelectedKey());
            
            if(valueObj.equals(valObj)) {
             resultDO.setValue((String)((StringField)row.get(2)).getValue());
             resultDO.setDictEntry(null);
            } 
            else {
             resultDO.setValue(((Integer)valueObj.getValue()).toString());
             resultDO.setDictEntry((String)((StringField)row.get(2)).getValue());
            } 
            
            resultDO.setQuantLimit((String)((StringField)row.get(3)).getValue());
            
            resultDO.setContLevel((String)((StringField)row.get(4)).getValue());
            
            resultDO.setHazardLevel((String)((StringField)row.get(5)).getValue());                         
            
            resultDO.setFlagsId((Integer)((DropDownField)row.get(6)).getSelectedKey());
            
            resultDO.setSignificantDigits((Integer)((NumberField)row.get(7)).getValue());
           
            resultDO.setRoundingMethodId((Integer)((DropDownField)row.get(8)).getSelectedKey());            
                                    
            //resultDO.setResultGroup(fiter+1);
            resultDO.setResultGroup((Integer)rg.getValue());
            
            resultDO.setTestId(testId);
            
            resultDO.setSortOrder(iter);
            
            trDOlist.add(resultDO);
        }
        
        for(int iter = 0; iter < model.getDeletions().size(); iter++){
            DataSet row = (DataSet)model.getDeletions().get(iter);
            TestResultDO resultDO = new TestResultDO();
            
            if(row.getData()!=null){ 
                id = (NumberField)((DataMap)row.getData()).get("id");
                resultDO.setId((Integer)id.getValue());         
                rg = (NumberField)((DataMap)row.getData()).get("resGrp");
            }
            
            //resultDO.setResultGroup(fiter+1);
            resultDO.setResultGroup((Integer)rg.getValue());
            resultDO.setDelete(true);                        
            
            trDOlist.add(resultDO);                       
        }
        
        model.getDeletions().clear();
      }  
        return trDOlist;
    }
    
    private TreeDataItem createAnalyteNode(TreeDataModel model,TestAnalyteDO analyteDO){
      TreeDataItem item = model.createTreeItem("analyte",new NumberObject(analyteDO.getId()));
        DataSet analyteSet = new DataSet();
        analyteSet.setKey(new NumberObject(analyteDO.getAnalyteId()));
        analyteSet.add(new StringObject(analyteDO.getAnalyteName()));
        DataModel autoModel = new DataModel();
        autoModel.add(analyteSet);
        autoModel.add(new NumberObject(-1),new StringObject(""));
        ((DropDownField)item.get(0)).setModel(autoModel);
        item.get(0).setValue(analyteSet);
        DataModel typeModel = getInitialModel(new StringObject(TestMeta.getTestAnalyte().getTypeId()));
        ((DropDownField)item.get(1)).setModel(typeModel);
        item.get(1).setValue(new DataSet(new NumberObject(analyteDO.getTypeId())));
        item.get(2).setValue(analyteDO.getIsReportable());
        DataModel scrModel = getInitialModel(new StringObject(TestMeta.getTestAnalyte().getScriptletId()));
        ((DropDownField)item.get(3)).setModel(scrModel);
        
        if(analyteDO.getScriptletId()!=null)
         item.get(3).setValue(new DataSet(new NumberObject(analyteDO.getScriptletId())));       
        
        if(analyteDO.getResultGroup()!=null)
         item.get(4).setValue(new DataSet(new NumberObject(analyteDO.getResultGroup())));       
        
        DataMap data = new DataMap();
        data.put("id", new NumberObject(analyteDO.getId()));
        item.setData(data);
        return item;
    }
    
    private TreeDataItem createGroupNode(int id ,TreeDataModel model){
        TreeDataItem item = model.createTreeItem("top",new NumberObject(id));
        item.get(0).setValue(openElisConstants.getString("analyteGroup"));           
        return item;
      }
        

    private TestIdNameMethodIdDO getTestIdNameMethodIdDOFromRPC(TestForm form) {
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        testDO.setId((Integer)form.id.getValue());
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

        testDetailsDO.setLabelQty((Integer)form.labelQty.getValue());
        testDetailsDO.setReportingDescription((String)form.reportingDescription.getValue());

        testDetailsDO.setRevisionMethodId((Integer)form.revisionMethodId.getSelectedKey());

        testDetailsDO.setScriptletId((Integer)form.scriptletId.getSelectedKey());
               
        testDetailsDO.setTestFormatId((Integer)form.testFormatId.getSelectedKey());
        
        testDetailsDO.setTestTrailerId((Integer)form.testTrailerId.getSelectedKey());
        
        testDetailsDO.setTimeHolding((Integer)form.timeHolding.getValue());
        testDetailsDO.setTimeTaAverage((Integer)form.timeTaAverage.getValue());
        testDetailsDO.setTimeTaMax((Integer)form.timeTaMax.getValue());
        testDetailsDO.setTimeTaWarning((Integer)form.timeTaWarning.getValue());
        testDetailsDO.setTimeTransit((Integer)form.timeTransit.getValue());
       
        testDetailsDO.setSortingMethodId((Integer)form.sortingMethodId.getSelectedKey());

        testDetailsDO.setReportingMethodId((Integer)form.reportingMethodId.getSelectedKey());
        
        testDetailsDO.setReportingSequence((Integer)form.reportingSequence.getValue());
        return testDetailsDO;
    }
    
    

    private void setRpcErrors(List exceptionList, TestForm form) {
        TableField sampleTypeTable = (TableField)form.sampleType.sampleTypeTable;
        TableField prepTestTable = (TableField)form.prepAndReflex.testPrepTable;        
        TableField testReflexTable = (TableField)form.prepAndReflex.testReflexTable;        
        TableField worksheetTable = (TableField)form.worksheet.worksheetTable;        
        TableField testSectionTable = (TableField)form.details.sectionTable;         
        TableField testResultsTable = (TableField)form.testAnalyte.testResultsTable;        
        //CollectionField cfield = (CollectionField)((Form)form.getField("testAnalyte")).getField("resultModelCollection");
        ArrayList<DataModel> cfield = (ArrayList<DataModel>)form.testAnalyte.resultModelCollection;

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
                    String fieldName = nameWithRPC.substring(10);
                    Form workSheetRPC = (Form)form.getField("worksheet");
                    
                    workSheetRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    
               } else if(nameWithRPC.startsWith("details:")) {
                    String fieldName = nameWithRPC.substring(8);
                    Form detailsRPC = (Form)form.getField("details");
                    
                    detailsRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               } else {
                    form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName())
                       .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               }   
            }
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        form.status = Form.Status.invalid;
    }       

    private void loadDropDown(List<IdNameDO> list, DataModel model) {
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();
            // id
            Integer dropdownId = methodDO.getId();
            // entry
            String dropdownText = methodDO.getName();

            StringObject textObject = new StringObject(dropdownText);
            NumberObject numberId = new NumberObject(dropdownId);

            set.add(textObject);

            set.setKey(numberId);

            model.add(set);
        }
    }    

    private void loadPrepTestDropDown(List<QaEventTestDropdownDO> qaedDOList,
                                      DataModel model) {

        DataSet blankset = new DataSet();
        StringObject blankStringId = new StringObject("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        int i = 0;
        while (i < qaedDOList.size()) {
            DataSet set = new DataSet();                      
            QaEventTestDropdownDO resultDO = (QaEventTestDropdownDO)qaedDOList.get(i);
            StringObject textObject = new StringObject(resultDO.getTest() + " , " + resultDO.getMethod());
            set.add(textObject);
            NumberObject numberId = new NumberObject(resultDO.getId());
            set.setKey(numberId);
            model.add(set);
            i++;
        }
    }
     
    
    private void addErrorToResultField(int row, String fieldName,List<String> findexes,
                                       String exc, ArrayList<DataModel> cfield) {
        int findex = findexes.indexOf(fieldName);                
        int llim = 0;
        int ulim = 0;
        
        //ArrayList mlist = cfield.getValue();
        DataModel<DataSet> m = null;
        
        DataSet errRow = null;       
        
        for(int i = 0; i < cfield.size(); i++) {
             ulim = getUpperLimit(i,cfield);             
             if(llim <= row && row < ulim) {
               m = (DataModel<DataSet>)cfield.get(i);
               errRow = m.get(row - llim);               
               break;    
             }   
             llim = ulim;
        }
        
        System.out.println("error: "+exc);
        System.out.println("row: "+row);
        System.out.println("findex: "+findex);        
        ((AbstractField)errRow.get(findex)).addError(exc);               
    }
    
    private int getUpperLimit(int i, ArrayList<DataModel> mlist) {
      int ulim = 0; 
        for(int j = -1; j < i; j++) {
         ulim += mlist.get(j+1).size();    
       } 
      return ulim;  
    }
}