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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
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
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestService implements AppScreenFormServiceInt<FormRPC,DataSet,DataModel>,
                                    AutoCompleteServiceInt{

    private static final int leftTableRowsPerPage = 24;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    private static final TestMetaMap TestMeta = new TestMetaMap();
    
    

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List testNames;
        // if the rpc is null then we need to get the page
        if (rpcSend == null) {

            FormRPC rpc = (FormRPC)SessionManager.getSession()
                                                 .getAttribute("TestQuery");

            if (rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

            try {
                testNames = remote.query(rpc.getFieldMap(),
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

            HashMap<String, AbstractField> fields = rpcSend.getFieldMap();
            // fields.remove("contactsTable");

            try {
                testNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("TestQuery", rpcSend);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel();
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
    
    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
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
    
        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        // if(((FormRPC)rpcSend.getField("details")).load){
        testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));
        // }
        testSectionDOList = getTestSectionsFromRPC((FormRPC)rpcReturn.getField("details"), null) ;
            
        sampleTypeDOList = getSampleTypesFromRPC((FormRPC)rpcReturn.getField("sampleType"),
                                                  null);
        
        prepTestDOList = getPrepTestsFromRPC((FormRPC)rpcReturn.getField("prepAndReflex"),
                                              null);
        testReflexDOList = getTestReflexesFromRPC((FormRPC)rpcReturn.getField("prepAndReflex")
                                                  ,null);
        
        
        //if (((FormRPC)rpcSend.getField("worksheet")).load) {
         itemsDOList = getWorksheetItemsFromRPC((FormRPC)rpcReturn.getField("worksheet"));               
         worksheetDO = getTestWorkSheetFromRPC((FormRPC)rpcReturn.getField("worksheet"),
                                             null);
        //}
        
       // if (((FormRPC)rpcSend.getField("testAnalyte")).load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC((FormRPC)rpcReturn.getField("testAnalyte"),
                                                        null);
            resultDOList = getTestResultDOListFromRPC((FormRPC)rpcSend.getField("testAnalyte"),
                                                      null);
        //}
        
        List exceptionList = remote.validateForAdd(testDO,testDetailsDO,
                                                   prepTestDOList,sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList,testAnalyteDOList,
                                                   testSectionDOList,resultDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
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
    
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }
    
        setFieldsInRPC(rpcReturn, testDO);
        return rpcReturn;
    }
    
    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
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

        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        NumberField testId = (NumberField)rpcSend.getField(TestMeta.getId());
        
        if (((FormRPC)rpcSend.getField("details")).load){
            testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));
            testSectionDOList = getTestSectionsFromRPC((FormRPC)rpcReturn.getField("details"), 
                                                       (Integer)testId.getValue());  
        } 

        if (((FormRPC)rpcSend.getField("prepAndReflex")).load) {
            
            testPrepDOList = getPrepTestsFromRPC((FormRPC)rpcReturn.getField("prepAndReflex"),
                                                      (Integer)testId.getValue());
            
            testReflexDOList = getTestReflexesFromRPC((FormRPC)rpcReturn.getField("prepAndReflex")
                                                    ,(Integer)testId.getValue());
            
            
        }
        
        if (((FormRPC)rpcSend.getField("sampleType")).load) {            
            sampleTypeDOList = getSampleTypesFromRPC((FormRPC)rpcReturn.getField("sampleType"),
                                                      (Integer)testId.getValue());
        }
        
        if (((FormRPC)rpcSend.getField("worksheet")).load) {
            itemsDOList = getWorksheetItemsFromRPC((FormRPC)rpcReturn.getField("worksheet"));
            worksheetDO = getTestWorkSheetFromRPC((FormRPC)rpcReturn.getField("worksheet"),
                                                 (Integer)testId.getValue());
        }
        
        if (((FormRPC)rpcSend.getField("testAnalyte")).load) {
            testAnalyteDOList = getTestAnalyteDOListFromRPC((FormRPC)rpcReturn.getField("testAnalyte"),
                                                        (Integer)testId.getValue());
            resultDOList = getTestResultDOListFromRPC((FormRPC)rpcSend.getField("testAnalyte"),
                                       (Integer)testId.getValue());
            
            
        }
        
        List exceptionList = remote.validateForUpdate(testDO,testDetailsDO,
                                                      testPrepDOList,sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList,testAnalyteDOList,
                                                      testSectionDOList,resultDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);

            return rpcSend;
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
            
            setRpcErrors(exceptionList,rpcSend);
            
            return rpcSend;
            
        }
        setFieldsInRPC(rpcReturn, testDO);
        return rpcReturn;
    }
    
    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod( (Integer)((DataObject)key.getKey()).getValue());
        setFieldsInRPC(rpcReturn, testDO);

        if (((FormRPC)rpcReturn.getField("details")).load) {
            FormRPC detailsRPC = (FormRPC)rpcReturn.getField("details");
            loadTestDetails(key, detailsRPC);
        }

        if (((FormRPC)rpcReturn.getField("sampleType")).load) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("sampleType");
            loadSampleTypes(key, samplePrepRPC);
        }

        if (((FormRPC)rpcReturn.getField("prepAndReflex")).load) {
            FormRPC prepAndReflexRPC = (FormRPC)rpcReturn.getField("prepAndReflex");
            loadPrepTestsReflexTests(key, prepAndReflexRPC);
        }
        
        if (((FormRPC)rpcReturn.getField("worksheet")).load) {
            FormRPC worksheetRPC = (FormRPC)rpcReturn.getField("worksheet");
            loadWorksheetLayout(key, worksheetRPC);
        }
        
        if (((FormRPC)rpcReturn.getField("testAnalyte")).load) {
            FormRPC analyteRPC = (FormRPC)rpcReturn.getField("testAnalyte");
            loadTestAnalyte(key, analyteRPC);
        }

        return rpcReturn;
    }   

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod((Integer)((DataObject)key.getKey()).getValue());
        setFieldsInRPC(rpcReturn, testDO);

        String tab = (String)rpcReturn.getFieldValue("testTabPanel");
        if (tab.equals("detailsTab")) {
            loadTestDetails(key, (FormRPC)rpcReturn.getField("details"));
        }

        if (tab.equals("sampleTypeTab")) {
            FormRPC sampleTypeRPC = (FormRPC)rpcReturn.getField("sampleType");
            loadSampleTypes(key, sampleTypeRPC);
        }

        if (tab.equals("prepAndReflexTab")) {
            FormRPC prepReflexRPC = (FormRPC)rpcReturn.getField("prepAndReflex");
            loadPrepTestsReflexTests(key, prepReflexRPC);
        }
        
        if (tab.equals("worksheetTab")) {
            FormRPC worksheetRPC = (FormRPC)rpcReturn.getField("worksheet");
            loadWorksheetLayout(key, worksheetRPC);
        }
        
        if (tab.equals("analyteTab")) {
            FormRPC analyteRPC = (FormRPC)rpcReturn.getField("testAnalyte");
            loadTestAnalyte(key, analyteRPC);
        }
        
        return rpcReturn;
    }
    
    public FormRPC loadTestDetails(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDetailsDO testDetailsDO = remote.getTestDetails((Integer)((NumberObject)key.getKey()).getValue());
        fillTestDetails(testDetailsDO, rpcReturn);
        List<TestSectionDO> tsDOList = remote.getTestSections((Integer)((NumberObject)key.getKey()).getValue());
        fillTestSections(tsDOList,rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }
    
    public FormRPC loadSampleTypes(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestTypeOfSampleDO> list = remote.getTestTypeOfSamples((Integer)((NumberObject)key.getKey()).getValue());
        fillSampleTypes(list, rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }

    public FormRPC loadPrepTestsReflexTests(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestPrepDO> prepList = remote.getTestPreps((Integer)((NumberObject)key.getKey()).getValue());
        fillPrepTests(prepList, rpcReturn);
        List<TestReflexDO>  reflexList = remote.getTestReflexes((Integer)((NumberObject)key.getKey()).getValue());    
        fillTestReflexes(reflexList,rpcReturn,(Integer)((NumberObject)key.getKey()).getValue());
        rpcReturn.load = true;
        return rpcReturn;
    }
        
    public FormRPC loadWorksheetLayout(DataSet key, FormRPC rpcReturn){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestWorksheetDO worksheetDO = remote.getTestWorksheet((Integer)((NumberObject)key.getKey()).getValue());
        List<TestWorksheetItemDO> itemDOList = remote.getTestWorksheetItems((Integer)((NumberObject)key.getKey()).getValue());
        fillWorksheet(worksheetDO, itemDOList, rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }
    
    public FormRPC loadTestAnalyte(DataSet key,FormRPC rpcReturn){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<TestAnalyteDO> taList = remote.getTestAnalytes((Integer)((NumberObject)key.getKey()).getValue());
        fillAnalyteTree(taList, rpcReturn);
        fillTestResults(key,rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }        
    
    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        try {
            testDO = remote.getTestIdNameMethodAndLock((Integer)((DataObject)key.getKey()).getValue(),
                                                       SessionManager.getSession()
                                                                     .getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        setFieldsInRPC(rpcReturn, testDO);

        String tab = (String)rpcReturn.getFieldValue("testTabPanel");
        if (tab.equals("detailsTab")) {
            loadTestDetails(key, (FormRPC)rpcReturn.getField("details"));
        }

        if (tab.equals("sampleTypeTab")) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("sampleType");
            loadSampleTypes(key, samplePrepRPC);
        }

        if (tab.equals("prepAndReflexTab")) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("prepAndReflex");
            loadPrepTestsReflexTests(key, samplePrepRPC);
        }
        
        if (tab.equals("worksheetTab")) {
            FormRPC worksheetRPC = (FormRPC)rpcReturn.getField("worksheet");
            loadWorksheetLayout(key, worksheetRPC);
        }
        
        if (tab.equals("analyteTab")) {
            FormRPC analyteRPC = (FormRPC)rpcReturn.getField("testAnalyte");
            loadTestAnalyte(key, analyteRPC);
        }

        return rpcReturn;
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
    
    public ModelField getResultGroupModel(NumberObject testId,FormRPC rpcReturn){
        ModelField field  = (ModelField)rpcReturn.getField("resultGroupDropDown");
        DataModel model = new DataModel();
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest((Integer)testId.getValue());
       
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO rgDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();
            // id
            Integer dropdownId = rgDO.getId();
            // entry
            String dropdownText = dropdownId.toString();

            StringObject textObject = new StringObject(dropdownText);
            NumberObject numberId = new NumberObject(dropdownId);

            set.add(textObject);

            set.setKey(numberId);

            model.add(set);
        }
        
        field.setValue(model);
        return field;
        
    }
    
    public NumberObject getGroupCountForTest(NumberObject testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getResultGroupsForTest((Integer)testId.getValue());        
        return new NumberObject(list.size());
    }
    
    public DataModel getTestResultModel(NumberObject testId,FormRPC rpcReturn){
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
  
    public DataMap getResultGroupAnalytesMap(NumberObject testId, DataMap dataMap) {
        return dataMap;
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
    
    private List<TestPrepDO> getPrepTestsFromRPC(FormRPC rpcSend,Integer testId) {

        DataModel model = (DataModel)rpcSend.getField("testPrepTable").getValue();

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {
            DataSet row = model.get(j);
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

    private List<TestTypeOfSampleDO> getSampleTypesFromRPC(FormRPC rpcSend,
                                                           Integer testId) {
        DataModel model = (DataModel)rpcSend.getField("sampleTypeTable")
                                              .getValue();
        

        List<TestTypeOfSampleDO> typeOfSampleDOList = new ArrayList<TestTypeOfSampleDO>();

        for (int i = 0; i < model.size(); i++) {
            DataSet row = model.get(i);
            TestTypeOfSampleDO testTypeOfSampleDO = new TestTypeOfSampleDO();

            testTypeOfSampleDO.setDelete(false);
            
            if(row.getData()!=null){
             NumberField id = (NumberField)((DataMap)row.getData()).get("id");
             testTypeOfSampleDO.setId((Integer)id.getValue());             
            }
            
            testTypeOfSampleDO.setTestId(testId);
            
            testTypeOfSampleDO.setTypeOfSampleId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                                        
            testTypeOfSampleDO.setUnitOfMeasureId((Integer)((DropDownField)row.get(1)).getSelectedKey());
              
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet row = (DataSet)model.getDeletions().get(i);
            TestTypeOfSampleDO testTypeOfSampleDO = new TestTypeOfSampleDO();
            
            testTypeOfSampleDO.setDelete(true);
            
            if(row.getData()!=null){
                NumberField id = (NumberField)((DataMap)row.getData()).get("id");
                testTypeOfSampleDO.setId((Integer)id.getValue());             
            }
                        
            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        model.getDeletions().clear();
        return typeOfSampleDOList;
    }
    
    private List<TestReflexDO> getTestReflexesFromRPC(FormRPC rpcSend,Integer testId){
        DataModel model = (DataModel)rpcSend.getField("testReflexTable").getValue();
        List<TestReflexDO> list = new ArrayList<TestReflexDO>();
         for (int i = 0; i < model.size(); i++) {
            DataSet row = model.get(i);
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
    
    private TestWorksheetDO getTestWorkSheetFromRPC(FormRPC rpcSend, Integer testId){
      TestWorksheetDO worksheetDO = null;
      Integer bc = (Integer)rpcSend.getFieldValue(TestMeta.getTestWorksheet().getBatchCapacity());
      if(bc!=null){
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();   
        worksheetDO.setBatchCapacity(bc);
      }  
      
      Integer id = (Integer)rpcSend.getFieldValue(TestMeta.getTestWorksheet().getId());
      if(id!=null){
       if(worksheetDO==null)
           worksheetDO = new TestWorksheetDO();   
       worksheetDO.setId(id);
      } 
      
      DropDownField numFormId = (DropDownField)rpcSend.getField(TestMeta.getTestWorksheet().getFormatId());       
         if(numFormId.getSelectedKey()!=null ){ 
             if(worksheetDO==null)
                 worksheetDO = new TestWorksheetDO();  
             worksheetDO.setNumberFormatId((Integer)numFormId.getSelectedKey());
         }  
   
       
      if(testId!=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();  
          worksheetDO.setTestId(testId);
      } 
      
      DropDownField scrId = (DropDownField)rpcSend.getField(TestMeta.getTestWorksheet().getScriptletId());      
          if(scrId.getValue()!=null){
              if(worksheetDO==null)
                  worksheetDO = new TestWorksheetDO();  
              worksheetDO.setScriptletId((Integer)scrId.getSelectedKey());
          }  

      
      Integer tc = (Integer)rpcSend.getFieldValue(TestMeta.getTestWorksheet().getTotalCapacity());      
      if(tc !=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();   
          worksheetDO.setTotalCapacity(tc);
      }
       
      return worksheetDO;
    }
    
    private List<TestSectionDO> getTestSectionsFromRPC(FormRPC rpcSend,Integer testId){
        DataModel model = (DataModel)rpcSend.getField("sectionTable").getValue();
        
        List<TestSectionDO> tsDOList =  new ArrayList<TestSectionDO>();
        
        for(int iter = 0; iter < model.size(); iter++){
            DataSet row = model.get(iter);
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
    
    private List<TestWorksheetItemDO> getWorksheetItemsFromRPC(FormRPC rpcSend) {
        DataModel model = (DataModel)rpcSend.getField("worksheetTable")
                                              .getValue();
        
        List<TestWorksheetItemDO> worksheetItemDOList = new ArrayList<TestWorksheetItemDO>();

        for (int i = 0; i < model.size(); i++) {
            DataSet row = model.get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(false);            

            
            if (row.getData() != null) {
                NumberField id = (NumberField)((DataMap)row.getData()).get("id");
                worksheetItemDO.setId((Integer)id.getValue());
            }
         
            worksheetItemDO.setTestWorksheetId((Integer)((NumberField)rpcSend.
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
    

    private void setFieldsInRPC(FormRPC rpcReturn, TestIdNameMethodIdDO testDO) {
        rpcReturn.setFieldValue(TestMeta.getId(), testDO.getId());
        rpcReturn.setFieldValue(TestMeta.getName(), testDO.getName());
        //rpcReturn.setFieldValue(TestMeta.getMethodId(), 
          //                      new DataSet(new NumberObject(testDO.getMethodId())));
        DataModel model = new DataModel();
        model.add(new NumberObject(testDO.getMethodId()),new StringObject(testDO.getMethodName()));
        ((DropDownField)rpcReturn.getField(TestMeta.getMethodId())).setModel(model);
        rpcReturn.setFieldValue(TestMeta.getMethodId(), model.get(0));
    }

    private void fillTestDetails(TestDetailsDO testDetailsDO, FormRPC rpcReturn) {
        rpcReturn.setFieldValue(TestMeta.getDescription(),
                                testDetailsDO.getDescription());
        rpcReturn.setFieldValue(TestMeta.getReportingDescription(),
                                testDetailsDO.getReportingDescription());
        rpcReturn.setFieldValue(TestMeta.getIsActive(),
                                testDetailsDO.getIsActive());        
        rpcReturn.setFieldValue(TestMeta.getActiveBegin(),
                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveBegin()
                                                                     .getDate()));
        rpcReturn.setFieldValue(TestMeta.getActiveEnd(),
                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        testDetailsDO.getActiveEnd()
                                                                     .getDate()));        
        rpcReturn.setFieldValue(TestMeta.getIsReportable(),
                                testDetailsDO.getIsReportable()); 
        
        if(testDetailsDO.getTestTrailerId()!=null)
         rpcReturn.setFieldValue(TestMeta.getTestTrailerId(),
                                new DataSet(new NumberObject(testDetailsDO.getTestTrailerId())));
        
        rpcReturn.setFieldValue(TestMeta.getTimeTransit(),
                                testDetailsDO.getTimeTransit());
        rpcReturn.setFieldValue(TestMeta.getTimeHolding(),
                                testDetailsDO.getTimeHolding());
        rpcReturn.setFieldValue(TestMeta.getTimeTaAverage(),
                                testDetailsDO.getTimeTaAverage());                
        rpcReturn.setFieldValue(TestMeta.getTimeTaWarning(),
                                testDetailsDO.getTimeTaWarning());
        rpcReturn.setFieldValue(TestMeta.getTimeTaMax(),
                                testDetailsDO.getTimeTaMax()); 
        
        if(testDetailsDO.getLabelId()!=null)
         rpcReturn.setFieldValue(TestMeta.getLabelId(),
                                new DataSet(new NumberObject(testDetailsDO.getLabelId())));
        rpcReturn.setFieldValue(TestMeta.getLabelQty(),
                               testDetailsDO.getLabelQty());                 
        
        if(testDetailsDO.getScriptletId()!=null)
        rpcReturn.setFieldValue(TestMeta.getScriptletId(),
                                new DataSet(new NumberObject(testDetailsDO.getScriptletId())));
        
        if(testDetailsDO.getTestFormatId()!=null)
        rpcReturn.setFieldValue(TestMeta.getTestFormatId(),
                                new DataSet(new NumberObject(testDetailsDO.getTestFormatId())));   

        if(testDetailsDO.getRevisionMethodId()!=null)
        rpcReturn.setFieldValue(TestMeta.getRevisionMethodId(),
                                new DataSet(new NumberObject(testDetailsDO.getRevisionMethodId())));
        
        if(testDetailsDO.getSortingMethodId()!=null)
            rpcReturn.setFieldValue(TestMeta.getSortingMethodId(),
                                    new DataSet(new NumberObject(testDetailsDO.getSortingMethodId())));
        
        if(testDetailsDO.getReportingMethodId()!=null)
            rpcReturn.setFieldValue(TestMeta.getReportingMethodId(),
                                    new DataSet(new NumberObject(testDetailsDO.getReportingMethodId())));
        
         rpcReturn.setFieldValue(TestMeta.getReportingSequence(),
                                    testDetailsDO.getReportingSequence());
        
        
    }
    
    private void fillTestSections(List<TestSectionDO> testSectionDOList, FormRPC rpcReturn){
        DataModel model = (DataModel)rpcReturn.getField("sectionTable").getValue();
        model.clear();
        
        if(testSectionDOList.size()>0){
            for(int iter = 0; iter < testSectionDOList.size(); iter++){
                TestSectionDO sectionDO = (TestSectionDO)testSectionDOList.get(iter);
                
                DataSet row = model.createNewSet();
                
                NumberField id = new NumberField(sectionDO.getId());
                
                DataMap data = new DataMap();
                
                data.put("id", id);
                
                row.setData(data);
                               
                row.get(0).setValue(new DataSet(new NumberObject(sectionDO.getSectionId())));

                row.get(1).setValue(new DataSet(new NumberObject(sectionDO.getFlagId())));

                model.add(row);                              
                
            }
        }
    }

    private void fillPrepTests(List<TestPrepDO> testPrepDOList,
                               FormRPC rpcReturn) {

        DataModel model = (DataModel)rpcReturn.getField("testPrepTable")
                                                .getValue();
        model.clear();
        if (testPrepDOList.size() > 0) {
            for (int iter = 0; iter < testPrepDOList.size(); iter++) {
                TestPrepDO testPrepDO = (TestPrepDO)testPrepDOList.get(iter);

                DataSet row = model.createNewSet();
                
                NumberField id = new NumberField(testPrepDO.getId());

                DataMap data = new DataMap();
                
                data.put("id", id);
                
                row.setData(data);
                
               if(testPrepDO.getPrepTestId()!=null) 
                 row.get(0).setValue(new DataSet(new NumberObject(testPrepDO.getPrepTestId())));

                row.get(1).setValue(testPrepDO.getIsOptional());

                model.add(row);
            }
            
        }
    }
    
    private void fillTestReflexes(List<TestReflexDO> testReflexDOList,
                                  FormRPC rpcReturn, Integer testId){
        DataModel model = (DataModel)rpcReturn.getField("testReflexTable")
                                                .getValue();
        model.clear();        
        if(testReflexDOList.size() > 0){
            Integer unselVal = new Integer(-1);
            for(int iter = 0; iter < testReflexDOList.size(); iter++){
              TestReflexDO refDO = testReflexDOList.get(iter);
              DataSet row = model.createNewSet();
              
              NumberField id = new NumberField(refDO.getId());
              DataMap data = new DataMap();
              
              data.put("id", id);
              row.setData(data);
              
              if(refDO.getAddTestId()!=null)
               row.get(0).setValue(new DataSet(new NumberObject(refDO.getAddTestId())));

              if(refDO.getTestAnalyteId()!=null) {
               row.get(1).setValue(new DataSet(new NumberObject(refDO.getTestAnalyteId())));
               if(! (unselVal).equals(refDO.getTestAnalyteId())) {
                DataModel dmodel = getTestResultModel(new NumberObject(testId),
                                                      new NumberObject(refDO.getTestAnalyteId()));
                ((DropDownField)row.get(2)).setModel(dmodel);
               } 
              } 
              
              if(refDO.getTestResultId()!=null)
               row.get(2).setValue(new DataSet(new NumberObject(refDO.getTestResultId())));
              
              if(refDO.getFlagsId()!=null)
               row.get(3).setValue(new DataSet(new NumberObject(refDO.getFlagsId())));                                                                    
                           
               model.add(row);
            }
        }
        
    }
        

    private void fillSampleTypes(List<TestTypeOfSampleDO> sampleTypeDOList,
                                 FormRPC rpcReturn) {

        DataModel model = (DataModel)rpcReturn.getField("sampleTypeTable")
                                                .getValue();
        model.clear();

        if (sampleTypeDOList.size() > 0) {
            for (int iter = 0; iter < sampleTypeDOList.size(); iter++) {
                TestTypeOfSampleDO testTypeOfSampleDO = (TestTypeOfSampleDO)sampleTypeDOList.get(iter);

                DataSet row = model.createNewSet();
                // new TableRow();
                NumberField id = new NumberField(testTypeOfSampleDO.getId());
                
                DataMap data = new DataMap();
                data.put("id", id);
                
                row.setData(data);
                
                if(testTypeOfSampleDO.getTypeOfSampleId()!=null)
                 row.get(0).setValue(new DataSet(new NumberObject(testTypeOfSampleDO.getTypeOfSampleId())));

                if(testTypeOfSampleDO.getUnitOfMeasureId()!=null)
                 row.get(1).setValue(new DataSet(new NumberObject(testTypeOfSampleDO.getUnitOfMeasureId())));

                model.add(row);
            }

        }

    }
    
    private void fillWorksheet(TestWorksheetDO worksheetDO, 
                               List<TestWorksheetItemDO> worksheetItemList,
                               FormRPC rpcReturn){
       if(worksheetDO!=null){ 
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getBatchCapacity(),
                                worksheetDO.getBatchCapacity());
        
        if(worksheetDO.getNumberFormatId()!=null)
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getFormatId(),
                                new DataSet(new NumberObject(worksheetDO.getNumberFormatId())));    
        
        if(worksheetDO.getScriptletId()!=null)
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getScriptletId(),
                                new DataSet(new NumberObject(worksheetDO.getScriptletId())));    
        
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getId(),
                                worksheetDO.getId());  
        
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getTotalCapacity(),
                                worksheetDO.getTotalCapacity());
       } 
        DataModel model = (DataModel)rpcReturn.getField("worksheetTable").getValue();
                 model.clear();

        if (worksheetItemList.size() > 0) {
         for (int iter = 0; iter < worksheetItemList.size(); iter++) {
             TestWorksheetItemDO worksheetItemDO = (TestWorksheetItemDO)worksheetItemList.get(iter);

           DataSet row = model.createNewSet();                     

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
    
    private void fillAnalyteTree(List<TestAnalyteDO> analyteDOList, FormRPC rpcReturn){        
        TreeDataModel model =  (TreeDataModel)(rpcReturn.getFieldValue("analyteTree"));
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
    
    public FormRPC fillTestResults(DataSet key, FormRPC rpcReturn){       
        DataSet row = null;
        DataMap data = null;
        NumberField id = null;        
        NumberField rg = null;
        TestResultDO resultDO = null;
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");               
        DataModel model = (DataModel)rpcReturn.getField("testResultsTable").getValue();
        List<IdNameDO> list = remote.getResultGroupsForTest((Integer)((NumberObject)key.getKey()).getValue());                
        
        model.clear();
        
        if(list.size() > 0) {        
         List<TestResultDO> resultDOList = remote.getTestResults((Integer)((NumberObject)key.getKey()).getValue(),1);                          
          for(int iter = 0; iter < resultDOList.size(); iter++){
            row = model.createNewSet();
            resultDO = resultDOList.get(iter);  
            id = new NumberField(resultDO.getId());
            data = new DataMap();  
            rg = new NumberField(resultDO.getResultGroup());
            data.put("id", id);
            data.put("resGrp", rg);
                                     
            row.get(1).setValue(new DataSet(new NumberObject(resultDO.getTypeId())));
            
            if(resultDO.getDictEntry() == null) {
             row.get(2).setValue(resultDO.getValue());     
             data.put("value", new NumberObject(-999));
            } else {
             data.put("value", new NumberObject(new Integer(resultDO.getValue())));   
             row.get(2).setValue(resultDO.getDictEntry());
            } 
            
            row.setData(data);
            
            row.get(3).setValue(resultDO.getSignificantDigits());
            
            if(resultDO.getFlagsId()!=null)
             row.get(4).setValue(new DataSet(new NumberObject(resultDO.getFlagsId())));
            
            if(resultDO.getRoundingMethodId()!=null)
             row.get(5).setValue(new DataSet(new NumberObject(resultDO.getRoundingMethodId())));
            
            row.get(6).setValue(resultDO.getQuantLimit());
            row.get(7).setValue(resultDO.getContLevel());
            row.get(8).setValue(resultDO.getHazardLevel());  
            model.add(row);
        }
      }          
       return rpcReturn;
    }

    public DataModel loadTestResultsByGroup(NumberObject testId, NumberObject resultGroup,DataModel model){
        DataSet row = null;
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
            
            row.get(1).setValue(new DataSet(new NumberObject(resultDO.getTypeId())));
            
            if(resultDO.getDictEntry() == null) {
               row.get(2).setValue(resultDO.getValue()); 
               data.put("value", new NumberObject(-999));
            } else {
               data.put("value", new NumberObject(new Integer(resultDO.getValue())));   
               row.get(2).setValue(resultDO.getDictEntry());
            } 
               
            row.setData(data);
            
            row.get(3).setValue(resultDO.getSignificantDigits());
            
            if(resultDO.getFlagsId()!= null)
                row.get(4).setValue(new DataSet(new NumberObject(resultDO.getFlagsId())));
               
            if(resultDO.getRoundingMethodId()!= null)
                row.get(5).setValue(new DataSet(new NumberObject(resultDO.getRoundingMethodId())));
            
            row.get(6).setValue(resultDO.getQuantLimit());
            row.get(7).setValue(resultDO.getContLevel());
            row.get(8).setValue(resultDO.getHazardLevel());  
            model.add(row);
        }
        
       return model; 
    }
    
    private List<TestAnalyteDO> getTestAnalyteDOListFromRPC(FormRPC rpcReturn,Integer testId){
        TreeDataModel model =  (TreeDataModel)(rpcReturn.getFieldValue("analyteTree"));
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
    
    private List<TestResultDO> getTestResultDOListFromRPC(FormRPC rpcSend,Integer testId){
        
      CollectionField field = (CollectionField)rpcSend.getField("resultModelCollection");
      ArrayList<DataModel> list = (ArrayList<DataModel>)field.getValue(); 
      NumberField id = null;
      NumberField rg = null;
      NumberObject valueObj = null;
      NumberObject valObj = new NumberObject(-999);
      
      List<TestResultDO> trDOlist = new ArrayList<TestResultDO>();
      
      for(int fiter = 0; fiter < list.size(); fiter++){
        DataModel model = list.get(fiter);
                
        for(int iter = 0; iter < model.size(); iter++){
            DataSet row = model.get(iter);
            TestResultDO resultDO = new TestResultDO();
            
            if(row.getData()!=null){ 
                id = (NumberField)((DataMap)row.getData()).get("id");
                resultDO.setId((Integer)id.getValue()); 
                valueObj = (NumberObject)((DataMap)row.getData()).get("value");
                rg = (NumberField)((DataMap)row.getData()).get("resGrp");
            }
            
            resultDO.setDelete(false);
                                   
            resultDO.setTypeId((Integer)((DropDownField)row.get(1)).getSelectedKey());
            
            if(valueObj.equals(valObj)) {
             resultDO.setValue((String)((StringField)row.get(2)).getValue());
             resultDO.setDictEntry(null);
            } 
            else {
             resultDO.setValue(((Integer)valueObj.getValue()).toString());
             resultDO.setDictEntry((String)((StringField)row.get(2)).getValue());
            } 
            
            resultDO.setSignificantDigits((Integer)((NumberField)row.get(3)).getValue());                       
            resultDO.setFlagsId((Integer)((DropDownField)row.get(4)).getSelectedKey());
           
            resultDO.setRoundingMethodId((Integer)((DropDownField)row.get(5)).getSelectedKey());
            
            resultDO.setQuantLimit((String)((StringField)row.get(6)).getValue());
            
            resultDO.setContLevel((String)((StringField)row.get(7)).getValue());
            
            resultDO.setHazardLevel((String)((StringField)row.get(8)).getValue());
                        
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
        item.get(0).setValue(openElisConstants.getString("group"));           
        return item;
      }
        

    private TestIdNameMethodIdDO getTestIdNameMethodIdDOFromRPC(FormRPC rpcSend) {
        NumberField testId = (NumberField)rpcSend.getField(TestMeta.getId());
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        testDO.setId((Integer)testId.getValue());
        testDO.setName(((String)rpcSend.getFieldValue(TestMeta.getName())));        
        testDO.setMethodId((Integer)((DropDownField)rpcSend.
                                      getField(TestMeta.getMethodId())).getSelectedKey());

        return testDO;
    }

    private TestDetailsDO getTestDetailsDOFromRPC(FormRPC rpcSend) {
        TestDetailsDO testDetailsDO = new TestDetailsDO();
        DatetimeRPC activeBegin = (DatetimeRPC)rpcSend.getFieldValue(TestMeta.getActiveBegin());
        if (activeBegin != null)
            testDetailsDO.setActiveBegin(activeBegin.getDate());

        DatetimeRPC activeEnd = (DatetimeRPC)rpcSend.getFieldValue(TestMeta.getActiveEnd());
        if (activeEnd != null)
            testDetailsDO.setActiveEnd(activeEnd.getDate());

        testDetailsDO.setDescription((String)rpcSend.getFieldValue(TestMeta.getDescription()));
        testDetailsDO.setIsActive((String)rpcSend.getFieldValue(TestMeta.getIsActive()));
        testDetailsDO.setIsReportable((String)rpcSend.getFieldValue(TestMeta.getIsReportable()));

        testDetailsDO.setLabelId((Integer)((DropDownField)rpcSend.getField(TestMeta.getLabelId())).getSelectedKey());

        testDetailsDO.setLabelQty((Integer)rpcSend.getFieldValue(TestMeta.getLabelQty()));
        testDetailsDO.setReportingDescription((String)rpcSend.getFieldValue(TestMeta.getReportingDescription()));

        testDetailsDO.setRevisionMethodId((Integer)((DropDownField)rpcSend.
                             getField(TestMeta.getRevisionMethodId())).getSelectedKey());

        testDetailsDO.setScriptletId((Integer)((DropDownField)rpcSend.
                            getField(TestMeta.getScriptletId())).getSelectedKey());
               
        testDetailsDO.setTestFormatId((Integer)((DropDownField)rpcSend.
                            getField(TestMeta.getTestFormatId())).getSelectedKey());
        
        testDetailsDO.setTestTrailerId((Integer)((DropDownField)rpcSend.
                            getField(TestMeta.getTestTrailerId())).getSelectedKey());
        
        testDetailsDO.setTimeHolding((Integer)rpcSend.getFieldValue(TestMeta.getTimeHolding()));
        testDetailsDO.setTimeTaAverage((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaAverage()));
        testDetailsDO.setTimeTaMax((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaMax()));
        testDetailsDO.setTimeTaWarning((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaWarning()));
        testDetailsDO.setTimeTransit((Integer)rpcSend.getFieldValue(TestMeta.getTimeTransit()));
       
        testDetailsDO.setSortingMethodId((Integer)((DropDownField)rpcSend.
                            getField(TestMeta.getSortingMethodId())).getSelectedKey());

        testDetailsDO.setReportingMethodId((Integer)((DropDownField)rpcSend.
                            getField(TestMeta.getReportingMethodId())).getSelectedKey());
        
        testDetailsDO.setReportingSequence((Integer)rpcSend.getFieldValue(TestMeta.getReportingSequence()));
        return testDetailsDO;
    }
    
    

    private void setRpcErrors(List exceptionList, FormRPC rpcSend) {
        TableField sampleTypeTable = (TableField)((FormRPC)rpcSend.getField("sampleType")).getField("sampleTypeTable");
        TableField prepTestTable = (TableField)((FormRPC)rpcSend.getField("prepAndReflex")).getField("testPrepTable");        
        TableField testReflexTable = (TableField)((FormRPC)rpcSend.getField("prepAndReflex")).getField("testReflexTable");        
        TableField worksheetTable = (TableField)((FormRPC)rpcSend.getField("worksheet")).getField("worksheetTable");        
        TableField testSectionTable = (TableField)((FormRPC)rpcSend.getField("details")).getField("sectionTable");         
        TableField testResultsTable = (TableField)((FormRPC)rpcSend.getField("testAnalyte")).getField("testResultsTable");        
        CollectionField cfield = (CollectionField)((FormRPC)rpcSend.getField("testAnalyte")).getField("resultModelCollection");

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
                    FormRPC workSheetRPC = (FormRPC)rpcSend.getField("worksheet");
                    
                    workSheetRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    
               } else if(nameWithRPC.startsWith("details:")) {
                    String fieldName = nameWithRPC.substring(8);
                    FormRPC detailsRPC = (FormRPC)rpcSend.getField("details");
                    
                    detailsRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               } else {
                    rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName())
                       .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               }   
            }
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        rpcSend.status = Status.invalid;
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
                                       String exc, CollectionField cfield) {
        int findex = findexes.indexOf(fieldName);                
        int llim = 0;
        int ulim = 0;
        
        ArrayList<DataModel> mlist = (ArrayList<DataModel>)cfield.getValue();
        DataModel m = null;
        
        DataSet errRow = null;       
        
        for(int i = 0; i < mlist.size(); i++) {
             ulim = getUpperLimit(i,mlist);             
             if(llim <= row && row < ulim) {
               m = mlist.get(i);
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
