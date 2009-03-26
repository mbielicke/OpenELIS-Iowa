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
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
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
import org.openelis.metamap.TestWorksheetAnalyteMetaMap;
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.modules.test.client.DetailsForm;
import org.openelis.modules.test.client.PrepAndReflexForm;
import org.openelis.modules.test.client.SampleTypeForm;
import org.openelis.modules.test.client.TestAnalyteForm;
import org.openelis.modules.test.client.TestForm;
import org.openelis.modules.test.client.TestGeneralPurposeRPC;
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

public class TestService implements AppScreenFormServiceInt<TestRPC,Integer>,
                                    AutoCompleteServiceInt{

    private static final int leftTableRowsPerPage = 28;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    private static final TestMetaMap TestMeta = new TestMetaMap();       

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
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
            fields.remove("worksheetTable");
            fields.remove("sampleTypeTable");
            fields.remove("testReflexTable");
            fields.remove("testPrepTable");
            fields.remove("sectionTable");

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
            model = new DataModel<Integer>();
        else 
            model.clear();
        while (i < testNames.size() && i < leftTableRowsPerPage) {
            IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)testNames.get(i);

            StringObject name = new StringObject(resultDO.getLastName()
                                                 +", "+resultDO.getFirstName());

            model.add(new DataSet<Integer>(resultDO.getId(),name));
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
        List<TestWorksheetAnalyteDO> twsaList = null;
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;       
    
        Integer testId;
    
        rpc.form.details.duplicate = false;   
        rpc.form.sampleType.duplicate = false;
        rpc.form.prepAndReflex.duplicate = false;
        rpc.form.worksheet.duplicate = false;
        rpc.form.testAnalyte.duplicate = false;
        
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
         twsaList = getWorksheetAnalytesFromRPC(rpc.form.worksheet,null);
        //}
        
       // if (((FormRPC)rpcSend.getField("testAnalyte")).load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.form.testAnalyte,null);
            resultDOList = getTestResultDOListFromRPC(rpc.form.testAnalyte,null);
        //}
        
        List exceptionList = remote.validateForAdd(testDO,testDetailsDO,
                                                   prepTestDOList,sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList,twsaList,
                                                   testAnalyteDOList,testSectionDOList,
                                                   resultDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);
    
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
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        testDO.setId(testId);
        setFieldsInRPC(rpc.form, testDO,false);
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
        List<TestWorksheetAnalyteDO> twsaList = null;
        List<TestAnalyteDO> testAnalyteDOList = null;  
        List<TestSectionDO> testSectionDOList = null;
        List<TestResultDO> resultDOList = null;

        testDO = getTestIdNameMethodIdDOFromRPC(rpc.form);
        IntegerField testId = rpc.form.id;
        
        rpc.form.details.duplicate = false;   
        rpc.form.sampleType.duplicate = false;
        rpc.form.prepAndReflex.duplicate = false;
        rpc.form.worksheet.duplicate = false;
        rpc.form.testAnalyte.duplicate = false;
        
        if (rpc.form.details.load){
            testDetailsDO = getTestDetailsDOFromRPC(rpc.form.details);
            testSectionDOList = getTestSectionsFromRPC(rpc.form.details,
                                                      testId.getValue());  
        } 

        if (rpc.form.prepAndReflex.load) {
            
            testPrepDOList = getPrepTestsFromRPC(rpc.form.prepAndReflex,
                                                     testId.getValue());
            
            testReflexDOList = getTestReflexesFromRPC(rpc.form.prepAndReflex,
                                                      testId.getValue());                       
        }
        
        if (rpc.form.sampleType.load) {            
            sampleTypeDOList = getSampleTypesFromRPC(rpc.form.sampleType,
                                                      testId.getValue());
        }
        
        if (rpc.form.worksheet.load) {
            itemsDOList = getWorksheetItemsFromRPC(rpc.form.worksheet);
            worksheetDO = getTestWorkSheetFromRPC(rpc.form.worksheet,
                                                  testId.getValue());
            twsaList = getWorksheetAnalytesFromRPC(rpc.form.worksheet,
                                                   testId.getValue());
        }
        
        if (rpc.form.testAnalyte.load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC(rpc.form.testAnalyte,
                                                        testId.getValue());
            resultDOList = getTestResultDOListFromRPC(rpc.form.testAnalyte,
                                       testId.getValue());
            
            
        }
        
        List exceptionList = remote.validateForUpdate(testDO,testDetailsDO,
                                                      testPrepDOList,sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList,twsaList,testAnalyteDOList,
                                                      testSectionDOList,resultDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);

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
            
            setRpcErrors(exceptionList,rpc.form);
            
            return rpc;
            
        }
        setFieldsInRPC(rpc.form, testDO,false);
        return rpc;
    }
    
    public TestRPC commitDelete(TestRPC rpc) throws RPCException {
        return null;
    }
    
    public TestRPC abort(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethodAndUnlock(rpc.key,SessionManager.getSession().getId());
        setFieldsInRPC(rpc.form, testDO,false);
        
        rpc.form.details.duplicate = false;   
        rpc.form.sampleType.duplicate = false;
        rpc.form.prepAndReflex.duplicate = false;
        rpc.form.worksheet.duplicate = false;
        rpc.form.testAnalyte.duplicate = false;
        

        if (rpc.form.details.load) {            
            loadTestDetails(rpc.key, rpc.form.details);
        }

        if (rpc.form.sampleType.load) {            
            loadSampleTypes(rpc.key, rpc.form.sampleType);
        }

        if (rpc.form.prepAndReflex.load) {
            loadPrepTestsReflexTests(rpc.key, rpc.form.prepAndReflex);
        }
        
        if (rpc.form.worksheet.load) {
            loadWorksheetLayout(rpc.key,rpc.form.worksheet);
        }
        
        if (rpc.form.testAnalyte.load) {            
            loadTestAnalyte(rpc.key, rpc.form.testAnalyte);
        }

        return rpc;
    }   

    public TestRPC fetch(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod(rpc.key);
        setFieldsInRPC(rpc.form, testDO,false);
        
        rpc.form.details.duplicate = false;   
        rpc.form.sampleType.duplicate = false;
        rpc.form.prepAndReflex.duplicate = false;
        rpc.form.worksheet.duplicate = false;
        rpc.form.testAnalyte.duplicate = false;

        String tab = (String)rpc.form.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails(rpc.key, rpc.form.details);
        }

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes(rpc.key, rpc.form.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests(rpc.key, rpc.form.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout(rpc.key, rpc.form.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte(rpc.key, rpc.form.testAnalyte);
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
        return;
    }
    
    public TestGeneralPurposeRPC loadTestDetails(TestGeneralPurposeRPC rpc) {
        loadTestDetails(rpc.key, (DetailsForm)rpc.form);
        return rpc;
    } 
    
    private void loadSampleTypes(Integer key, SampleTypeForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestTypeOfSampleDO> list = remote.getTestTypeOfSamples(key);
        fillSampleTypes(list, form);
        form.load = true;
        return;
    }
    
    public TestGeneralPurposeRPC loadSampleTypes(TestGeneralPurposeRPC rpc) {
        loadSampleTypes(rpc.key,(SampleTypeForm)rpc.form);
        return rpc;
    }

    private void loadPrepTestsReflexTests(Integer key, PrepAndReflexForm form) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestPrepDO> prepList = remote.getTestPreps(key);
        List<TestReflexDO>  reflexList = remote.getTestReflexes(key);    
        fillPrepTests(prepList, form);        
        fillTestReflexes(reflexList,form,key);
        form.load = true;
        return;
    }
    
    public TestGeneralPurposeRPC loadPrepTestsReflexTests(TestGeneralPurposeRPC rpc) {
        loadPrepTestsReflexTests(rpc.key, (PrepAndReflexForm)rpc.form);
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
    
    public TestGeneralPurposeRPC loadWorksheetLayout(TestGeneralPurposeRPC rpc){
        loadWorksheetLayout(rpc.key, (WorksheetForm)rpc.form);
        return rpc;
    }
    
    private void loadTestAnalyte(Integer key,TestAnalyteForm form){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes(key);
        fillAnalyteTree(taList, form);
        fillTestResults(key,form);
        form.load = true;
        return;               
    }        
    
    public TestGeneralPurposeRPC loadTestAnalyte(TestGeneralPurposeRPC rpc) {
       loadTestAnalyte(rpc.key, (TestAnalyteForm)rpc.form);
       return rpc;
    }
    
    public TestGeneralPurposeRPC getAnalyteTreeModel(TestGeneralPurposeRPC rpc) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes(rpc.key);
        fillAnalyteTree(taList, (TestAnalyteForm)rpc.form);
        return rpc;
    }
    
    public TestRPC fetchForUpdate(TestRPC rpc) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        try {
            testDO = remote.getTestIdNameMethodAndLock(rpc.key, SessionManager.getSession().getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        setFieldsInRPC(rpc.form, testDO, false);
        
        rpc.form.details.duplicate = false;   
        rpc.form.sampleType.duplicate = false;
        rpc.form.prepAndReflex.duplicate = false;
        rpc.form.worksheet.duplicate = false;
        rpc.form.testAnalyte.duplicate = false;

        String tab = rpc.form.testTabPanel;
        if (tab.equals("detailsTab")) {
            loadTestDetails(rpc.key, rpc.form.details);
        }

        if (tab.equals("sampleTypeTab")) {
            loadSampleTypes(rpc.key, rpc.form.sampleType);
        }

        if (tab.equals("prepAndReflexTab")) {
            loadPrepTestsReflexTests(rpc.key, rpc.form.prepAndReflex);
        }
        
        if (tab.equals("worksheetTab")) {
            loadWorksheetLayout(rpc.key, rpc.form.worksheet);
        }
        
        if (tab.equals("analyteTab")) {
            loadTestAnalyte(rpc.key, rpc.form.testAnalyte);
        }

        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl"));       

        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }
    
    public TestRPC getScreen(TestRPC rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");
        return rpc;
    }
    
    public TestGeneralPurposeRPC getTestAnalyteModel(TestGeneralPurposeRPC rpc){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        IdNameDO methodDO = null;

        List<IdNameDO> list = remote.getTestAnalyteDropDownValues(rpc.key);
        rpc.model = new DataModel<Integer>();

        rpc.model.add(new DataSet<Integer>(-1,new StringObject("")));

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            methodDO = (IdNameDO)iter.next();
            rpc.model.add(new DataSet<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
        }
        
        return rpc;
    }
    
    
    
    public DataModel<Integer> getTestResultModel(Integer testId, Integer analyteId,boolean forDuplicate) {
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsForTestAnalyte(testId,analyteId);
        DataModel<Integer> model = new DataModel<Integer>();

        model.add(new DataSet<Integer>(-1,new StringObject("")));

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            if(!forDuplicate) 
             model.add(new DataSet<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
            else 
             model.add(new DataSet<Integer>(methodDO.getId()*(-2),new StringObject(methodDO.getName())));    
        }
        
        return model; 
    }
    
    public TestGeneralPurposeRPC getResultGroupModel(TestGeneralPurposeRPC rpc) {       
        rpc.model = new DataModel<Integer>();
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest(rpc.key);
        rpc.model.add(new DataSet<Integer>(-1,new StringObject("")));
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO rgDO = (IdNameDO)iter.next();
            rpc.model.add(new DataSet<Integer>(rgDO.getId(),new StringObject(rgDO.getId().toString())));
        }
        
        return rpc;
        
    }
    
    public TestGeneralPurposeRPC getGroupCountForTest(TestGeneralPurposeRPC rpc){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest(rpc.key);  
        rpc.integerValue = list.size();        
        return rpc;
    }
    
    
    public DataModel<Integer> getTestResultModel(Integer testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
          List<IdNameDO> list = remote.getTestResultsforTest(testId);
          DataModel<Integer> model = new DataModel<Integer>();
          model.add(new DataSet<Integer>(-1,new StringObject("")));
             for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                 IdNameDO methodDO = (IdNameDO)iterator.next();
                 model.add(new DataSet<Integer>(methodDO.getId(),new StringObject(methodDO.getName())));
             }
         return model;
    }    
    
    public TestGeneralPurposeRPC getTestResultModelMap(TestGeneralPurposeRPC rpc) {
      TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
      HashMap<Integer,List<IdNameDO>> listMap = remote.getAnalyteResultsMap(rpc.key); 
      Entry<Integer,List<IdNameDO>> entry = null;
      List<IdNameDO> list = null;
      DataModel<Integer> dataModel = null;
       for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
              entry = (Entry<Integer,List<IdNameDO>>)iter.next();
              list = (List<IdNameDO>)listMap.get(entry.getKey());              
              dataModel = new DataModel<Integer>();
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
           DataSet set = new DataSet();                
           for(int i = 0; i < list.size(); i++) {
             set.add(new IntegerObject(list.get(i)));  
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
            rpc.model = new DataModel<Integer>();    
            loadDropDown(unitList, rpc.model);
        }       
        return rpc;        
    } 
    
    public TestGeneralPurposeRPC getInitialModel(TestGeneralPurposeRPC rpc) { 
        rpc.model = getInitialModel(rpc.fieldName);
        return rpc;
    }
    
    public DataModel getInitialModel(String cat) {
        //String cat = obj.getValue();
        DataModel model = new DataModel<Integer>();
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

    public DataModel<Integer> getMatches(String cat, DataModel model, String match, HashMap params) {         
        
        //if(("analyte").equals(cat)){ 
         TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
         List entries = remote.getMatchingEntries(match.trim()+"%", 10,cat);
         DataModel<Integer> dataModel = new DataModel<Integer>();
         for (Iterator iter = entries.iterator(); iter.hasNext();) {
             
             IdNameDO element = (IdNameDO)iter.next();
             Integer entryId = element.getId();                   
             String entryText = element.getName();
             
             DataSet<Integer> data = new DataSet<Integer>(entryId,new StringObject(entryText));                                      
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
         DataSet<Integer> row = null;
         DataMap data = null;
         IntegerField id = null;        
         IntegerField rg = null;
         TestResultDO resultDO = null;
         TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");               
         DataModel<Integer> model = form.testResultsTable.getValue();
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

              ((DropDownField<IntegerObject>)row.get(0)).setValue(new DataSet<Integer>(resultDO.getUnitOfMeasureId()));
              data.put("unitId", new IntegerObject(resultDO.getUnitOfMeasureId()));
             } 
             
             ((DropDownField<IntegerObject>)row.get(1)).setValue(new DataSet<Integer>(resultDO.getTypeId()));
             
             if(resultDO.getDictEntry() == null) {
              ((StringField)row.get(2)).setValue(resultDO.getValue());     
             } else {   
              ((StringField)row.get(2)).setValue(resultDO.getDictEntry());
             } 
             
             row.setData(data);
                                 
             ((StringField)row.get(3)).setValue(resultDO.getQuantLimit());
             ((StringField)row.get(4)).setValue(resultDO.getContLevel());
             ((StringField)row.get(5)).setValue(resultDO.getHazardLevel()); 
             
             if(resultDO.getFlagsId()!=null)
              ((DropDownField)row.get(6)).setValue(new DataSet<Integer>(resultDO.getFlagsId()));                                              
             
             ((IntegerField)row.get(7)).setValue(resultDO.getSignificantDigits());   
             
             if(resultDO.getRoundingMethodId()!=null)
              ((DropDownField)row.get(8)).setValue(new DataSet<Integer>(resultDO.getRoundingMethodId()));
             
             model.add(row);
          }
         }          
        } catch(Exception ex) {
            ex.printStackTrace();
        }    
        return form;
     }
            
     
     public TestGeneralPurposeRPC loadTestResultModellist(TestGeneralPurposeRPC rpc){ 
         TestAnalyteForm form = (TestAnalyteForm)rpc.form;
         for(int iter = 1 ; iter < rpc.integerValue+1; iter++) {
             form.resultModelCollection.add(getTestResultsByGroup(rpc.key, iter,(DataModel)rpc.model.clone(),form.duplicate));
         }
         return rpc;
     }
     
     
    public TestRPC getDuplicateRPC(TestRPC rpc) throws RPCException{
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
        
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod(rpc.key);                       
        
        setFieldsInRPC(rpc.form, testDO, true);
        
        rpc.form.details.duplicate = true;   
        rpc.form.sampleType.duplicate = true;
        rpc.form.prepAndReflex.duplicate = true;
        rpc.form.worksheet.duplicate = true;
        rpc.form.testAnalyte.duplicate = true;
        
        loadTestDetails(rpc.key, rpc.form.details);
        loadSampleTypes(rpc.key, rpc.form.sampleType);
        loadPrepTestsReflexTests(rpc.key, rpc.form.prepAndReflex);                
        loadWorksheetLayout(rpc.key, rpc.form.worksheet);                
        loadTestAnalyte(rpc.key, rpc.form.testAnalyte);
        
        rpc.form.testAnalyte.resultModelCollection = new ArrayList<DataModel<Integer>>();        
        
        for(int iter = 1 ; iter < rpc.numGroups+1; iter++) {
            rpc.form.testAnalyte.resultModelCollection.add(
                 getTestResultsByGroup(rpc.key,iter,(DataModel)rpc.resultTableModel.clone(),true));
        }        
        
        rpc.key = null;
        return rpc;
    }       
    
    private DataModel<Integer> getTestResultsByGroup(Integer key, Integer resultGroup, DataModel model, boolean forDuplicate){
        DataSet<Integer> row = null;
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
              ((DropDownField<IntegerObject>)row.get(0)).setValue(new DataSet<Integer>(resultDO.getUnitOfMeasureId()));
              data.put("unitId", new IntegerObject(resultDO.getUnitOfMeasureId()));
            }
            
            ((DropDownField<IntegerObject>)row.get(1)).setValue(new DataSet<Integer>(resultDO.getTypeId()));
            
            if(resultDO.getDictEntry() == null) {
               ((StringField)row.get(2)).setValue(resultDO.getValue());                
            } else {   
               ((StringField)row.get(2)).setValue(resultDO.getDictEntry());
            } 
              
            row.setData(data);
            
            ((StringField)row.get(4)).setValue(resultDO.getContLevel());
            ((StringField)row.get(5)).setValue(resultDO.getHazardLevel()); 
            
            if(resultDO.getFlagsId()!=null)
             ((DropDownField<IntegerObject>)row.get(6)).setValue(new DataSet<Integer>(resultDO.getFlagsId()));                                              
            
            ((IntegerField)row.get(7)).setValue(resultDO.getSignificantDigits());   
            
            if(resultDO.getRoundingMethodId()!=null)

             ((DropDownField<IntegerObject>)row.get(8)).setValue(new DataSet<Integer>(resultDO.getRoundingMethodId()));
            
            model.add(row);
        }
        
       return model; 
    }                 
    
    
    private List<TestPrepDO> getPrepTestsFromRPC(PrepAndReflexForm form,Integer testId) {


        DataModel<Integer> model = (DataModel<Integer>)form.testPrepTable.getValue();

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {

            DataSet<Integer> row = model.get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
                testPrepDO.setDelete(false);           

                if(row.getData()!=null){
                    IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");                                             
                    testPrepDO.setId(id.getValue());                                 
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
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");            
              testPrepDO.setId(id.getValue());                           
            }
            
            testPrepDOList.add(testPrepDO);
        }

        model.getDeletions().clear(); 
        return testPrepDOList;
    }

    private List<TestTypeOfSampleDO> getSampleTypesFromRPC(SampleTypeForm form,
                                                           Integer testId) {
        IntegerField id = null;

        DataSet<Integer> row = null;
        DataModel<Integer> model = (DataModel)form.sampleTypeTable.getValue();        
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
              id = (IntegerField)(data.get("id"));
              testTypeOfSampleDO.setId(id.getValue());
             } 
            }
                        
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        model.getDeletions().clear();
        return typeOfSampleDOList;
    }
    
    private List<TestReflexDO> getTestReflexesFromRPC(PrepAndReflexForm form,Integer testId){
        DataModel<Integer> model = (DataModel)form.testReflexTable.getValue();
        List<TestReflexDO> list = new ArrayList<TestReflexDO>();
         for (int i = 0; i < model.size(); i++) {
            DataSet<Integer> row = model.get(i);
            TestReflexDO refDO = new TestReflexDO();
            
            if(row.getData()!=null) {
             IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
             refDO.setId(id.getValue());            
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
                 IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                 refDO.setId(id.getValue());            
             }                      
             
             refDO.setDelete(true);                                                      
             
             list.add(refDO);
          }
         model.getDeletions().clear();
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
        DataModel<Integer> model = (DataModel)form.sectionTable.getValue();
        
        List<TestSectionDO> tsDOList =  new ArrayList<TestSectionDO>();
        
        for(int iter = 0; iter < model.size(); iter++){

            DataSet<Integer> row = model.get(iter);
            TestSectionDO tsDO = new TestSectionDO();
            tsDO.setDelete(false);
            
            if(row.getData()!=null){
             IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");                          
             tsDO.setId(id.getValue());
                           
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
                IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                tsDO.setId(id.getValue());                  
            }                       

            tsDOList.add(tsDO);               
        }
        
        model.getDeletions().clear();
        return tsDOList;
    }
    
    private List<TestWorksheetItemDO> getWorksheetItemsFromRPC(WorksheetForm form) {
        DataModel<Integer> model = (DataModel)form.worksheetTable.getValue();
        
        List<TestWorksheetItemDO> worksheetItemDOList = new ArrayList<TestWorksheetItemDO>();

        for (int i = 0; i < model.size(); i++) {

            DataSet<Integer> row = model.get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(false);            

            
            if (row.getData() != null) {
                IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
                worksheetItemDO.setId(id.getValue());
            }
         
            worksheetItemDO.setTestWorksheetId(((IntegerField)form.
                             getField(TestMeta.getTestWorksheet().getId())).getValue());      
            worksheetItemDO.setPosition(((IntegerField)row.get(0)).getValue());               
            worksheetItemDO.setTypeId((Integer)((DropDownField)row.get(1)).getSelectedKey());                         
            worksheetItemDO.setQcName((String)((StringField)row.get(2)).getValue()); 
                        
            worksheetItemDOList.add(worksheetItemDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet row = (DataSet)model.getDeletions().get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(true);            

            if (row.getData() != null) {
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
              worksheetItemDO.setId(id.getValue());
            } 
            
            worksheetItemDOList.add(worksheetItemDO);
        }
        model.getDeletions().clear();
        return worksheetItemDOList;
    }
    
    private List<TestWorksheetAnalyteDO> getWorksheetAnalytesFromRPC(WorksheetForm form,Integer testId) {
        DataModel<Integer> model = (DataModel)form.worksheetAnalyteTable.getValue();
        
        List<TestWorksheetAnalyteDO> worksheetItemDOList = new ArrayList<TestWorksheetAnalyteDO>();

        for (int i = 0; i < model.size(); i++) {
            DataSet<Integer> row = model.get(i);
            TestWorksheetAnalyteDO worksheetAnaDO = new TestWorksheetAnalyteDO();
            IntegerField id = null;
            IntegerField anaId = null;
            if(!"Y".equals(row.get(1).getValue())) {
             worksheetAnaDO.setDelete(true);                                                                                                                         
           } else {
               worksheetAnaDO.setDelete(false);           
               worksheetAnaDO.setTestId(testId);
               worksheetAnaDO.setRepeat(((IntegerField)row.get(2)).getValue());               
               worksheetAnaDO.setFlagId((Integer)((DropDownField)row.get(3)).getSelectedKey());    
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
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet<Integer> row = model.getDeletions().get(i);
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
        return worksheetItemDOList;
    }
    

    private void setFieldsInRPC(TestForm form, TestIdNameMethodIdDO testDO, boolean forDuplicate) {
        DataModel<Integer> model = new DataModel();
        
        if(!forDuplicate)
         form.id.setValue(testDO.getId());
        else
         form.id.setValue(null);
        
        form.name.setValue(testDO.getName());        

        model.add(new DataSet<Integer>(testDO.getMethodId(),new StringObject(testDO.getMethodName())));
        form.method.setModel(model);
        form.method.setValue(model.get(0));       
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
         form.testTrailerId.setValue(new DataSet<Integer>
                    (testDetailsDO.getTestTrailerId()));
        }
        
        form.isReportable.setValue(testDetailsDO.getIsReportable()); 
        
        form.timeTransit.setValue(testDetailsDO.getTimeTransit());
        form.timeHolding.setValue(testDetailsDO.getTimeHolding());
        form.timeTaAverage.setValue(testDetailsDO.getTimeTaAverage());                
        form.timeTaWarning.setValue(testDetailsDO.getTimeTaWarning());
        form.timeTaMax.setValue(testDetailsDO.getTimeTaMax()); 
        
        if(testDetailsDO.getLabelId()!=null) {
         form.labelId.setValue(new DataSet<Integer>(testDetailsDO.getLabelId()));
        } 
        
        form.labelQty.setValue(testDetailsDO.getLabelQty());                 
        
        if(testDetailsDO.getScriptletId()!=null) {
         form.scriptletId.setValue(new DataSet<Integer>(testDetailsDO.getScriptletId()));
        } 
        
        if(testDetailsDO.getTestFormatId()!=null) {
         form.testFormatId.setValue(new DataSet<Integer>(testDetailsDO.getTestFormatId()));   
        }
        
        if(testDetailsDO.getRevisionMethodId()!=null) {
         form.revisionMethodId.setValue(new DataSet<Integer>(testDetailsDO.getRevisionMethodId()));
        }
        
        if(testDetailsDO.getSortingMethodId()!=null) {
          form.sortingMethodId.setValue(new DataSet<Integer>(testDetailsDO.getSortingMethodId()));
        }   
        
        if(testDetailsDO.getReportingMethodId()!=null) {
          form.reportingMethodId.setValue(new DataSet<Integer>(testDetailsDO.getReportingMethodId()));
        }    
        
         form.reportingSequence.setValue(testDetailsDO.getReportingSequence());
        
        
    }
    
    private void fillTestSections(List<TestSectionDO> testSectionDOList, DetailsForm form){
        DataModel<Integer> model = form.sectionTable.getValue();
        model.clear();
        
        if(testSectionDOList.size()>0){
            for(int iter = 0; iter < testSectionDOList.size(); iter++){
                TestSectionDO sectionDO = (TestSectionDO)testSectionDOList.get(iter);

                DataSet<Integer> row = model.createNewSet();
                
                IntegerField id = new IntegerField(sectionDO.getId());
                if(!form.duplicate) {
                 DataMap data = new DataMap();               
                 data.put("id", id);                
                 row.setData(data);
                }                
                
                ((DropDownField)row.get(0)).setValue(new DataSet<Integer>(sectionDO.getSectionId()));

                ((DropDownField)row.get(1)).setValue(new DataSet<Integer>(sectionDO.getFlagId()));

                model.add(row);                              
                
            }
        }
    }

    private void fillPrepTests(List<TestPrepDO> testPrepDOList,
                               PrepAndReflexForm form) {

        DataModel<Integer> model = form.testPrepTable.getValue();
        model.clear();
        if (testPrepDOList.size() > 0) {
            for (int iter = 0; iter < testPrepDOList.size(); iter++) {
                TestPrepDO testPrepDO = (TestPrepDO)testPrepDOList.get(iter);

                DataSet<Integer> row = model.createNewSet();
                
                IntegerField id = new IntegerField(testPrepDO.getId());
                if(!form.duplicate) {
                 DataMap data = new DataMap();                
                 data.put("id", id);                
                 row.setData(data);
                }
                
                if(testPrepDO.getPrepTestId()!=null) { 
                 ((DropDownField)row.get(0)).setValue(new DataSet<Integer>
                   (testPrepDO.getPrepTestId()));
               }
               
                ((CheckField)row.get(1)).setValue(testPrepDO.getIsOptional());

                model.add(row);
            }
            
        }
    }
    
    private void fillTestReflexes(List<TestReflexDO> testReflexDOList,
                                  PrepAndReflexForm form, Integer testId){
        DataModel<Integer> model = form.testReflexTable.getValue();
        model.clear();        
        if(testReflexDOList.size() > 0){
            Integer unselVal = new Integer(-1);
            for(int iter = 0; iter < testReflexDOList.size(); iter++){
              TestReflexDO refDO = testReflexDOList.get(iter);

              DataSet<Integer> row = model.createNewSet();
              
              IntegerField id = new IntegerField(refDO.getId());
              if(!form.duplicate) {
               DataMap data = new DataMap();              
               data.put("id", id);
               row.setData(data);
              }
              
              if(refDO.getAddTestId()!=null) {
               ((DropDownField)row.get(0)).setValue(new DataSet<Integer>
                 (refDO.getAddTestId()));
              } 

             if(refDO.getTestAnalyteId()!=null) {
              if(!form.duplicate) {   
                ((DropDownField)row.get(1)).setValue(new DataSet<Integer>
                     (refDO.getTestAnalyteId()));
               } else {
                ((DropDownField)row.get(1)).setValue(new DataSet<Integer>
                   (refDO.getTestAnalyteId()*(-2)));   
               }
              
               if(! (unselVal).equals(refDO.getTestAnalyteId())) {                   
                DataModel<Integer> dmodel = getTestResultModel(testId,refDO.getTestAnalyteId(),form.duplicate);
                ((DropDownField)row.get(2)).setModel(dmodel);
               } 
              } 
              
              if(refDO.getTestResultId()!=null) {
               if(!form.duplicate) {      
                ((DropDownField)row.get(2)).setValue(new DataSet<Integer>
                  (refDO.getTestResultId()));
               } else {
                ((DropDownField)row.get(2)).setValue(new DataSet<Integer>
                   (refDO.getTestResultId() * (-2)));  
               } 
              } 
              
              if(refDO.getFlagsId()!=null) {
                ((DropDownField)row.get(3)).setValue(new DataSet<Integer>
                             (refDO.getFlagsId()));
              } 
                           
               model.add(row);
            }
        }
        
    }
        

    private void fillSampleTypes(List<TestTypeOfSampleDO> sampleTypeDOList,
                                 SampleTypeForm form) {

        DataModel<Integer> model = form.sampleTypeTable.getValue();
        model.clear();

        if (sampleTypeDOList.size() > 0) {
            for (int iter = 0; iter < sampleTypeDOList.size(); iter++) {
                TestTypeOfSampleDO testTypeOfSampleDO = (TestTypeOfSampleDO)sampleTypeDOList.get(iter);
                DataSet<Integer> row = model.createNewSet();
                // new TableRow();
                IntegerField id = new IntegerField(testTypeOfSampleDO.getId());
                IntegerField unitId = null;
                
                DataMap data = new DataMap();
                if(!form.duplicate) 
                 data.put("id", id);                                                               
                
                if(testTypeOfSampleDO.getTypeOfSampleId()!=null)
                 ((DropDownField)row.get(0)).setValue(new DataSet<Integer>(testTypeOfSampleDO.getTypeOfSampleId()));

                if(testTypeOfSampleDO.getUnitOfMeasureId()!=null) {
                 ((DropDownField)row.get(1)).setValue(new DataSet<Integer>(testTypeOfSampleDO.getUnitOfMeasureId()));
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
        
       DataModel<Integer> itemModel = null; 
       DataModel<Integer> anaModel = null; 
       
       if(worksheetDO!=null){ 
        form.batchCapacity.setValue(worksheetDO.getBatchCapacity());
        
        if(worksheetDO.getNumberFormatId()!=null)
         form.formatId.setValue(new DataSet<Integer>(worksheetDO.getNumberFormatId()));    
        
        if(worksheetDO.getScriptletId()!=null)
         form.scriptletId.setValue(new DataSet<Integer>(worksheetDO.getScriptletId()));                    
        
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
           DataSet<Integer> row = itemModel.createNewSet();                     
           IntegerField id = new IntegerField(worksheetItemDO.getId());
           
           if(!form.duplicate) { 
            DataMap data = new DataMap();           
            data.put("id", id);
            row.setData(data);                         
           }
           
           ((IntegerField)row.get(0)).setValue(worksheetItemDO.getPosition());

           if(worksheetItemDO.getTypeId()!=null) 
            ((DropDownField)row.get(1)).setValue(new DataSet<Integer>(worksheetItemDO.getTypeId()));
           
           ((StringField)row.get(2)).setValue(worksheetItemDO.getQcName());
          
           itemModel.add(row);
         }
        }
        
        anaModel = form.worksheetAnalyteTable.getValue();
        anaModel.clear();                  
         
         for (int iter = 0; iter < worksheetAnalyteList.size(); iter++) {
             TestWorksheetAnalyteDO worksheetAnalyteDO = (TestWorksheetAnalyteDO)worksheetAnalyteList.get(iter);
             DataSet<Integer> row = anaModel.createNewSet();                     
             IntegerField id = new IntegerField(worksheetAnalyteDO.getId());
             IntegerField anaId = new IntegerField(worksheetAnalyteDO.getAnalyteId());
             DataMap data = new DataMap();
             
             if(!form.duplicate) {                        
              data.put("id", id);                                   
             }
             
             data.put("analyteId", anaId);           
             row.setData(data);            
                                             
             ((StringField)row.get(0)).setValue(worksheetAnalyteDO.getAnalyteName());
             
             ((CheckField)row.get(1)).setValue("Y");
             
             ((IntegerField)row.get(2)).setValue(worksheetAnalyteDO.getRepeat());
             
             if(worksheetAnalyteDO.getFlagId()!=null) 
               ((DropDownField)row.get(3)).setValue(new DataSet<Integer>(worksheetAnalyteDO.getFlagId()));
            
             anaModel.add(row);
          } 
         
         for (int iter = 0; iter < anaIdNameDOList.size(); iter++) {
             IdNameDO idNameDO = (IdNameDO)anaIdNameDOList.get(iter);
             DataSet<Integer> row = anaModel.createNewSet();                    
             IntegerField anaId = new IntegerField(idNameDO.getId());
             DataMap data = new DataMap();             
             
             data.put("analyteId", anaId);           
             row.setData(data);            
                                             
             ((StringField)row.get(0)).setValue(idNameDO.getName());
             
             ((CheckField)row.get(1)).setValue("N");                      
            
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
         IntegerObject id = (IntegerObject)((DataMap)chItem.getData()).get("id");
         analyteDO.setId(id.getValue());
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
      ArrayList<DataModel<Integer>> list = form.resultModelCollection;        
      IntegerField id = null;
      IntegerField rg = null;
      IntegerObject valueObj = null;
      IntegerObject valObj = new IntegerObject(-999);
            
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
        DataModel<Integer> model = (DataModel<Integer>)list.get(fiter);
                
        for(int iter = 0; iter < model.size(); iter++){
            DataSet<Integer> row = model.get(iter);
            TestResultDO resultDO = new TestResultDO();
            
            if(row.getData()!=null){ 
                id = (IntegerField)((DataMap)row.getData()).get("id");
                if(id != null)
                 resultDO.setId(id.getValue()); 
            }
            
            resultDO.setDelete(false);
            
            resultDO.setUnitOfMeasureId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                                   
            resultDO.setTypeId((Integer)((DropDownField)row.get(1)).getSelectedKey());         
            
            if(dictId.equals(resultDO.getTypeId())) {
              try {
                  entryId = catRemote.getEntryIdForEntry((String)((StringField)row.get(2)).getValue());
                  resultDO.setValue(entryId.toString());
                  resultDO.setDictEntry((String)((StringField)row.get(2)).getValue());   
              }catch (Exception ex) {
                      ex.printStackTrace();
               }                
             } else {
                resultDO.setValue(((StringField)row.get(2)).getValue());
                resultDO.setDictEntry(null);                 
             }           
            
            resultDO.setQuantLimit((String)((StringField)row.get(3)).getValue());
            
            resultDO.setContLevel((String)((StringField)row.get(4)).getValue());
            
            resultDO.setHazardLevel((String)((StringField)row.get(5)).getValue());                         
            
            resultDO.setFlagsId((Integer)((DropDownField)row.get(6)).getSelectedKey());
            
            resultDO.setSignificantDigits(((IntegerField)row.get(7)).getValue());
           
            resultDO.setRoundingMethodId((Integer)((DropDownField)row.get(8)).getSelectedKey());            
                                    
            resultDO.setResultGroup(fiter+1);
            
            resultDO.setTestId(testId);
            
            resultDO.setSortOrder(iter);
            
            trDOlist.add(resultDO);
        }
        
        for(int iter = 0; iter < model.getDeletions().size(); iter++){
            DataSet row = (DataSet)model.getDeletions().get(iter);
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
        return trDOlist;
    }
    
    private TreeDataItem createAnalyteNode(TreeDataModel model,TestAnalyteDO analyteDO, boolean forDuplicate) {
        TreeDataItem item = model.createTreeItem("analyte");
        DataSet<Integer> analyteSet = new DataSet<Integer>(analyteDO.getAnalyteId(),new StringObject(analyteDO.getAnalyteName()));       
        DataModel<Integer> autoModel = new DataModel<Integer>();
        autoModel.add(analyteSet);
        //autoModel.add(new DataSet<Integer>(-1,new StringObject("")));
        ((DropDownField)item.get(0)).setModel(autoModel);
        item.get(0).setValue(analyteSet);
        DataModel typeModel = getInitialModel(TestMeta.getTestAnalyte().getTypeId());
        ((DropDownField)item.get(1)).setModel(typeModel);
        item.get(1).setValue(new DataSet<Integer>(analyteDO.getTypeId()));
        item.get(2).setValue(analyteDO.getIsReportable());
        DataModel scrModel = getInitialModel(TestMeta.getTestAnalyte().getScriptletId());
        ((DropDownField)item.get(3)).setModel(scrModel);
        
        if(analyteDO.getScriptletId()!=null)
         item.get(3).setValue(new DataSet<Integer>(analyteDO.getScriptletId()));       
        
        if(analyteDO.getResultGroup()!=null)
         item.get(4).setValue(new DataSet<Integer>(analyteDO.getResultGroup()));
        
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
        item.get(0).setValue(openElisConstants.getString("analyteGroup"));           
        return item;
      }
        

    private TestIdNameMethodIdDO getTestIdNameMethodIdDOFromRPC(TestForm form) {
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        testDO.setId(form.id.getValue());
        testDO.setName(((String)form.name.getValue()));        
        testDO.setMethodId((Integer)(form.method.getSelectedKey()));
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
        ArrayList<DataModel<Integer>> cfield = (ArrayList<DataModel<Integer>>)form.testAnalyte.resultModelCollection;

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
        DataSet<Integer> blankset = new DataSet<Integer>(-1,new StringObject(""));
        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            DataSet<Integer> set = new DataSet<Integer>(methodDO.getId(),new StringObject(methodDO.getName()));
            model.add(set);
        }
    }    

    private void loadPrepTestDropDown(List<QaEventTestDropdownDO> qaedDOList,
                                      DataModel model) {

        DataSet<Integer> blankset = new DataSet<Integer>(-1,new StringObject(""));
        DataSet<Integer> set = null;
        QaEventTestDropdownDO resultDO = null;
        model.add(blankset);

        int i = 0;
        while (i < qaedDOList.size()) {
            resultDO = (QaEventTestDropdownDO)qaedDOList.get(i);
            set = new DataSet<Integer>(resultDO.getId(),
                    new StringObject(resultDO.getTest() + " , " + resultDO.getMethod()));                                  
            model.add(set);
            i++;
        }
    }
     
    
    private void addErrorToResultField(int row, String fieldName,List<String> findexes,
                                       String exc, ArrayList<DataModel<Integer>> cfield) {
        int findex = findexes.indexOf(fieldName);                
        int llim = 0;
        int ulim = 0;
        
        DataModel<Integer> m = null;
        
        DataSet errRow = null;       
        
        for(int i = 0; i < cfield.size(); i++) {
             ulim = getUpperLimit(i,cfield);             
             if(llim <= row && row < ulim) {
               m = (DataModel<Integer>)cfield.get(i);
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
    
    private int getUpperLimit(int i, ArrayList<DataModel<Integer>> mlist) {
      int ulim = 0; 
        for(int j = -1; j < i; j++) {
         ulim += mlist.get(j+1).size();    
       } 
      return ulim;  
    }    
}