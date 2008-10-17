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
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;

import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestReflexMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.security.domain.SectionIdNameDO;
import org.openelis.security.remote.SystemUserUtilRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestService implements AppScreenFormServiceInt {

    private static final int leftTableRowsPerPage = 19;
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
        
        TestWorksheetDO worksheetDO = null;
        List<TestWorksheetItemDO> itemsDOList = null;
    
        Integer testId;
    
        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        // if(((FormRPC)rpcSend.getField("details")).load){
        testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));
        // }
        sampleTypeDOList = getSampleTypesFromRPC((FormRPC)rpcReturn.getField("sampleType"),
                                                  null);
        
        prepTestDOList = getPrepTestsFromRPC((FormRPC)rpcReturn.getField("prepAndReflex"),
                                              null);
        //testReflexDOList = getTestReflexesFromRPC((FormRPC)rpcReturn.getField("prepAndReflex")
                                                  //,null);
        if (((FormRPC)rpcSend.getField("worksheet")).load) {
         itemsDOList = getWorksheetItemsFromRPC((FormRPC)rpcReturn.getField("worksheet"));               
         worksheetDO = getTestWorkSheetFromRPC((FormRPC)rpcReturn.getField("worksheet"),
                                             null);
        }
        
        List exceptionList = remote.validateForAdd(testDO,testDetailsDO,
                                                   prepTestDOList,sampleTypeDOList,
                                                   testReflexDOList,worksheetDO,
                                                   itemsDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }
    
        try {
            testId = remote.updateTest(testDO,testDetailsDO,prepTestDOList,
                                       sampleTypeDOList,testReflexDOList,worksheetDO,
                                       itemsDOList);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }
    
        testDO.setId(testId);
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

        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        NumberField testId = (NumberField)rpcSend.getField(TestMeta.getId());
        
        if (((FormRPC)rpcSend.getField("details")).load)
            testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));

        if (((FormRPC)rpcSend.getField("prepAndReflex")).load) {
            
            testPrepDOList = getPrepTestsFromRPC((FormRPC)rpcReturn.getField("prepAndReflex"),
                                                      (Integer)testId.getValue());
            
           // testReflexDOList = getTestReflexesFromRPC((FormRPC)rpcReturn.getField("prepAndReflex")
             //                                         ,(Integer)testId.getValue());
            
            
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
        
        List exceptionList = remote.validateForUpdate(testDO,testDetailsDO,
                                                      testPrepDOList,sampleTypeDOList,
                                                      testReflexDOList,worksheetDO,
                                                      itemsDOList);        

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);

            return rpcSend;
        }

        try {
            remote.updateTest(testDO,testDetailsDO,
                              testPrepDOList,sampleTypeDOList,
                              testReflexDOList,worksheetDO,
                              itemsDOList);
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
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod((Integer)key.getKey()
                                                                             .getValue());
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

        return rpcReturn;
    }   

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod((Integer)key.getKey()
                                                                             .getValue());
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
        
        return rpcReturn;
    }
    
    public FormRPC loadTestDetails(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDetailsDO testDetailsDO = remote.getTestDetails((Integer)((NumberObject)key.getKey()).getValue());
        fillTestDetails(testDetailsDO, rpcReturn);
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
        //List<TestReflexDO>  reflexList = remote.getTestReflexes((Integer)((NumberObject)key.getKey()).getValue());    
        //fillTestReflexes(reflexList,rpcReturn);
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

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        try {
            testDO = remote.getTestIdNameMethodAndLock((Integer)key.getKey()
                                                                   .getValue(),
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

        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl");
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/test.xsl"));

        DataModel methodDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                             "methodDropDown");
        DataModel labelDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                            "labelDropDown");
        DataModel testTrailerDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                                  "testTrailerDropDown");
        DataModel scriptletDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                                "scriptletDropDown");
        DataModel sectionDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                              "sectionDropDown");
        DataModel sampleTypeDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                                 "sampleTypeDropDown");
        DataModel measureUnitDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                                  "measureUnitDropDown");
        DataModel prepTestDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                               "prepTestDropDown");
        
        DataModel testReflexFlagsDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                               "testReflexFlagsDropDown");

        DataModel testFormatDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                         "testFormatDropDown");
        
        DataModel revisionMethodDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                           "revisionMethodDropDown");
        
        DataModel testWSNumFormatDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                        "testWSNumFormatDropDown");
        
        DataModel testWSItemTypeDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                                          "testWSItemTypeDropDown");
        
        DataModel testAnalyteTypeDropDownField = (DataModel)CachingManager.getElement("InitialData",
                                                   "testAnalyteTypeDropDown");
        
        if (methodDropDownField == null) {
            methodDropDownField = getInitialModel("method");
            CachingManager.putElement("InitialData",
                                      "methodDropDown",
                                      methodDropDownField);
        }

        if (labelDropDownField == null) {
            labelDropDownField = getInitialModel("label");
            CachingManager.putElement("InitialData",
                                      "labelDropDown",
                                      labelDropDownField);
        }

        if (testTrailerDropDownField == null) {
            testTrailerDropDownField = getInitialModel("testTrailer");
            CachingManager.putElement("InitialData",
                                      "testTrailerDropDown",
                                      testTrailerDropDownField);
        }

        if (sectionDropDownField == null) {
            sectionDropDownField = getInitialModel("section");
            CachingManager.putElement("InitialData",
                                      "sectionDropDown",
                                      sectionDropDownField);
        }
        
        if (testFormatDropDownField == null) {
            testFormatDropDownField = getInitialModel("testFormat");
            CachingManager.putElement("InitialData",
                                      "testFormatDropDown",
                                      testFormatDropDownField);
        }
        
        if (revisionMethodDropDownField == null) {
            revisionMethodDropDownField = getInitialModel("revisionMethod");
            CachingManager.putElement("InitialData",
                                      "revisionMethodDropDown",
                                       revisionMethodDropDownField);
        }

        if (scriptletDropDownField == null) {
            scriptletDropDownField = getInitialModel("scriptlet");
            CachingManager.putElement("InitialData",
                                      "scriptletDropDown",
                                      scriptletDropDownField);
        }

        if (sampleTypeDropDownField == null) {
            sampleTypeDropDownField = getInitialModel("sampleType");
            CachingManager.putElement("InitialData",
                                      "sampleTypeDropDown",
                                      sampleTypeDropDownField);
        }

        if (measureUnitDropDownField == null) {
            measureUnitDropDownField = getInitialModel("unitOfMeasure");
            CachingManager.putElement("InitialData",
                                      "measureUnitDropDown",
                                      measureUnitDropDownField);
        }

        if (prepTestDropDownField == null) {
            prepTestDropDownField = getInitialModel("prepTest");
            CachingManager.putElement("InitialData",
                                      "prepTestDropDown",
                                      prepTestDropDownField);
        }
        
        if (testReflexFlagsDropDownField == null) {
            testReflexFlagsDropDownField = getInitialModel("testReflexFlags");
            CachingManager.putElement("InitialData",
                                      "testReflexFlagsDropDown",
                                      testReflexFlagsDropDownField);
        }
        
        if (testReflexFlagsDropDownField == null) {
            testReflexFlagsDropDownField = getInitialModel("testReflexFlags");
            CachingManager.putElement("InitialData",
                                      "testReflexFlagsDropDown",
                                      testReflexFlagsDropDownField);
        }
        
        if (testWSNumFormatDropDownField == null) {           
          testWSNumFormatDropDownField = getInitialModel("testWSNumFormat");  
          CachingManager.putElement("InitialData",
                                      "testWSNumFormatDropDown",
                                      testWSNumFormatDropDownField);
        
        }
        
        if (testWSItemTypeDropDownField == null) {           
            testWSItemTypeDropDownField = getInitialModel("testWSItemType");  
            CachingManager.putElement("InitialData",
                                        "testWSItemTypeDropDown",
                                        testWSItemTypeDropDownField);
          
          }
        
        if (testAnalyteTypeDropDownField == null) {           
            testAnalyteTypeDropDownField = getInitialModel("testAnalyteType");  
            CachingManager.putElement("InitialData",
                                        "testAnalyteTypeDropDown",
                                        testAnalyteTypeDropDownField);
          
          }

        HashMap<String, DataObject> map = new HashMap<String, DataObject>();
        map.put("xml", xml);
        map.put("methods", methodDropDownField);
        map.put("labels", labelDropDownField);
        map.put("testTrailers", testTrailerDropDownField);
        map.put("scriptlets", scriptletDropDownField);
        map.put("sections", sectionDropDownField);
        map.put("testFormats", testFormatDropDownField);
        map.put("revisionMethods", revisionMethodDropDownField);
        map.put("sampleTypes", sampleTypeDropDownField);
        map.put("unitsOfMeasure", measureUnitDropDownField);
        map.put("prepTests", prepTestDropDownField);
        map.put("testReflexFlags", testReflexFlagsDropDownField);
        map.put("testWSNumFormats", testWSNumFormatDropDownField);
        map.put("testWSItemTypes", testWSItemTypeDropDownField);
        map.put("testAnalyteTypes", testAnalyteTypeDropDownField);
        return map;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
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
    
    public DataModel getTestResultModel(NumberObject testId){
        TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        List<IdNameDO> list = remote.getTestResultsforTest((Integer)testId.getValue());
        //List<DataModel> modelList = new ArrayList<DataModel>();
         //for(int iter = 0;iter < list.size(); iter++){
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
             //modelList.add(model);
         //}
         //return modelList;
         return model;
    }
    
    public DataMap getTestResultModelMap(NumberObject testId){
      TestRemote remote  = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
      HashMap<Integer,List<IdNameDO>> listMap = remote.getAnalyteResultsMap((Integer)testId.getValue());
      HashMap<String, DataModel> modelMap = new HashMap<String, DataModel>();            
      DataMap dataMap = new DataMap();      
       for (Iterator iter = listMap.entrySet().iterator(); iter.hasNext();){ 
              Entry<Integer,List<IdNameDO>> entry = (Entry<Integer,List<IdNameDO>>)iter.next();
              List<IdNameDO> list = (List<IdNameDO>)listMap.get(entry.getKey());              
              DataModel dataModel = new DataModel();
              loadDropDown(list, dataModel);
              modelMap.put(entry.getKey().toString(), dataModel);             
          }
        dataMap.setValue(modelMap); 
        return dataMap;
      }

    public DataModel getInitialModel(String cat) {
        DataModel model = new DataModel();
        List<IdNameDO> values = null;

        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if (cat.equals("method")) {
            values = remote.getMethodDropDownValues();
        } else if (cat.equals("label")) {
            values = remote.getLabelDropDownValues();
        } else if (cat.equals("testTrailer")) {
            values = remote.getTestTrailerDropDownValues();
        } else if (cat.equals("scriptlet")) {
            values = remote.getScriptletDropDownValues();
        }else if (cat.equals("revisionMethod")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("revision_method"));
        }else if (cat.equals("testFormat")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_format"));
        } else if (cat.equals("unitOfMeasure")) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("unit_of_measure"));
        } else if (cat.equals("sampleType")) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("type_of_sample"));
        } else if (cat.equals("prepTest")) {            
            List<QaEventTestDropdownDO> qaedDOList = remote.getPrepTestDropDownValues();
            loadPrepTestDropDown(qaedDOList, model);
        }else if (cat.equals("testReflexFlags")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_reflex_flags"));
        }else if (cat.equals("testWSNumFormat")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_worksheet_number_format"));
        }else if (cat.equals("testWSItemType")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_worksheet_item_type"));
        }else if (cat.equals("testAnalyteType")) {            
            values = catRemote.getDropdownValues(catRemote.getCategoryId("test_analyte_type"));
        }else if (cat.equals("section")) {
            SystemUserUtilRemote utilRemote = (SystemUserUtilRemote)EJBFactory.lookup("SystemUserUtilBean/remote");
            List<SectionIdNameDO> sections = utilRemote.getSections("openelis");

            if (sections != null) {
                loadSectionDropDown(sections, model);
            }            
        }

        if (values != null) {
            loadDropDown(values, model);
        }

        return model;
    }    

    private List<TestPrepDO> getPrepTestsFromRPC(FormRPC rpcSend,Integer testId) {

        DataModel model = (DataModel)rpcSend.getField("testPrepTable").getValue();

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.size(); j++) {
            DataSet row = model.get(j);
            TestPrepDO testPrepDO = new TestPrepDO();
            
                testPrepDO.setDelete(false);           

            NumberField id = (NumberField)((DataMap)row.getData()).get("id");

            if (id != null) {
                if (id.getValue() != null) {
                    testPrepDO.setId((Integer)id.getValue());
                }
            }

            testPrepDO.setTestId(testId);

            DropDownField prepTestId = (DropDownField)row.get(0);
            if (prepTestId != null) {
                if (prepTestId.getValue() != null && !prepTestId.getValue()
                                                                .equals(new Integer(-1))) {
                    testPrepDO.setPrepTestId((Integer)prepTestId.getValue());
                }
            }

            CheckField isOptional = (CheckField)row.get(1);
            testPrepDO.setIsOptional((String)isOptional.getValue());

            testPrepDOList.add(testPrepDO);
        }
        
        for (int j = 0; j < model.getDeletions().size(); j++) {
            DataSet row = (DataSet)model.getDeletions().get(j);
            TestPrepDO testPrepDO = new TestPrepDO();

            
            testPrepDO.setDelete(true);            

            NumberField id = (NumberField)((DataMap)row.getData()).get("id");

            if (id != null) {
                if (id.getValue() != null) {
                    testPrepDO.setId((Integer)id.getValue());
                }
            }

            testPrepDO.setTestId(testId);

            DropDownField prepTestId = (DropDownField)row.get(0);
            if (prepTestId != null) {
                if (prepTestId.getValue() != null && !prepTestId.getValue()
                                                                .equals(new Integer(-1))) {
                    testPrepDO.setPrepTestId((Integer)prepTestId.getValue());
                }
            }

            CheckField isOptional = (CheckField)row.get(1);
            testPrepDO.setIsOptional((String)isOptional.getValue());

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
            
            NumberField id = (NumberField)((DataMap)row.getData()).get("id");
            if (id != null) {
                if (id.getValue() != null) {
                    testTypeOfSampleDO.setId((Integer)id.getValue());
                }
            }

            testTypeOfSampleDO.setTestId(testId);

            DropDownField typeOfSampleId = (DropDownField)row.get(0);
            if (typeOfSampleId != null) {
                if (typeOfSampleId.getValue() != null && !typeOfSampleId.getValue()
                                                                        .equals(new Integer(-1))) {
                    testTypeOfSampleDO.setTypeOfSampleId((Integer)typeOfSampleId.getValue());
                }
            }

            DropDownField unitOfMeasureId = (DropDownField)row.get(1);
            if (unitOfMeasureId != null) {
                if (unitOfMeasureId.getValue() != null && !unitOfMeasureId.getValue()
                                                                          .equals(new Integer(-1))) {
                    testTypeOfSampleDO.setUnitOfMeasureId((Integer)unitOfMeasureId.getValue());
                }
            }

            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet row = (DataSet)model.getDeletions().get(i);
            TestTypeOfSampleDO testTypeOfSampleDO = new TestTypeOfSampleDO();
            
            testTypeOfSampleDO.setDelete(true);
            
            NumberField id = (NumberField)((DataMap)row.getData()).get("id");
            if (id != null) {
                if (id.getValue() != null) {
                    testTypeOfSampleDO.setId((Integer)id.getValue());
                }
            }

            testTypeOfSampleDO.setTestId(testId);

            DropDownField typeOfSampleId = (DropDownField)row.get(0);
            if (typeOfSampleId != null) {
                if (typeOfSampleId.getValue() != null && !typeOfSampleId.getValue()
                                                                        .equals(new Integer(-1))) {
                    testTypeOfSampleDO.setTypeOfSampleId((Integer)typeOfSampleId.getValue());
                }
            }

            DropDownField unitOfMeasureId = (DropDownField)row.get(1);
            if (unitOfMeasureId != null) {
                if (unitOfMeasureId.getValue() != null && !unitOfMeasureId.getValue()
                                                                          .equals(new Integer(-1))) {
                    testTypeOfSampleDO.setUnitOfMeasureId((Integer)unitOfMeasureId.getValue());
                }
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
            
            NumberField id = (NumberField)((DataMap)row.getData()).get("id");
            if (id != null) {
                if (id.getValue() != null) {
                    refDO.setId((Integer)id.getValue());
                }
            }

            refDO.setTestId(testId);
            
            refDO.setDelete(false);            
            
            DropDownField addTestId = (DropDownField)row.get(0);
            if (addTestId != null) {
                if (addTestId.getValue() != null && !addTestId.getValue()
                                                                        .equals(new Integer(-1))) {
                    refDO.setAddTestId((Integer)addTestId.getValue());                    
                }
            }
            
            DropDownField analyteId = (DropDownField)row.get(1);
            if (analyteId != null) {
                if (analyteId.getValue() != null && !analyteId.getValue()
                                                                        .equals(new Integer(-1))) {
                    refDO.setTestAnalyteId((Integer)analyteId.getValue());                    
                }
            }
            
            DropDownField resultId = (DropDownField)row.get(2);
            if (resultId != null) {
                if (resultId.getValue() != null && !resultId.getValue()
                                                                        .equals(new Integer(-1))) {
                    refDO.setTestResultId((Integer)resultId.getValue());                    
                }
            }
            
            DropDownField flagsId = (DropDownField)row.get(3);
            if (flagsId != null) {
                if (flagsId.getValue() != null && !flagsId.getValue()
                                                  .equals(new Integer(-1))) {
                    refDO.setFlagsId((Integer)flagsId.getValue());                    
                }
            }
            list.add(refDO);
         }
         for (int i = 0; i < model.getDeletions().size(); i++) {
             DataSet row = (DataSet)model.getDeletions().get(i);
             TestReflexDO refDO = new TestReflexDO();
             
             NumberField id = (NumberField)((DataMap)row.getData()).get("id");
             if (id != null) {
                 if (id.getValue() != null) {
                     refDO.setId((Integer)id.getValue());
                 }
             }

             refDO.setTestId(testId);
             
             refDO.setDelete(true);            
             
             DropDownField addTestId = (DropDownField)row.get(0);
             if (addTestId != null) {
                 if (addTestId.getValue() != null && !addTestId.getValue()
                                                                         .equals(new Integer(-1))) {
                     refDO.setAddTestId((Integer)addTestId.getValue());                    
                 }
             }
             
             DropDownField analyteId = (DropDownField)row.get(1);
             if (analyteId != null) {
                 if (analyteId.getValue() != null && !analyteId.getValue()
                                                                         .equals(new Integer(-1))) {
                     refDO.setTestAnalyteId((Integer)analyteId.getValue());                    
                 }
             }
             
             DropDownField resultId = (DropDownField)row.get(2);
             if (resultId != null) {
                 if (resultId.getValue() != null && !resultId.getValue()
                                                                         .equals(new Integer(-1))) {
                     refDO.setTestResultId((Integer)resultId.getValue());                    
                 }
             }
             
             DropDownField flagsId = (DropDownField)row.get(3);
             if (flagsId != null) {
                 if (flagsId.getValue() != null && !flagsId.getValue()
                                                   .equals(new Integer(-1))) {
                     refDO.setFlagsId((Integer)flagsId.getValue());                    
                 }
             }
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
      
      DropDownField numFormId = (DropDownField)rpcSend.getField(TestMeta.getTestWorksheet().getNumberFormatId());
       if(numFormId!=null){
         if(numFormId.getValue()!=null && !numFormId.getValue().equals(new Integer(-1))){ 
             if(worksheetDO==null)
                 worksheetDO = new TestWorksheetDO();  
             worksheetDO.setNumberFormatId((Integer)numFormId.getValue());
         }  
       }   
       
      if(testId!=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();  
          worksheetDO.setTestId(testId);
      } 
      
      DropDownField scrId = (DropDownField)rpcSend.getField(TestMeta.getTestWorksheet().getScriptletId());
      if(scrId!=null){
          if(scrId.getValue()!=null && !scrId.getValue().equals(new Integer(-1))){
              if(worksheetDO==null)
                  worksheetDO = new TestWorksheetDO();  
              worksheetDO.setScriptletId((Integer)scrId.getValue());
          }  
        }
      
      Integer tc = (Integer)rpcSend.getFieldValue(TestMeta.getTestWorksheet().getTotalCapacity());      
      if(tc !=null){
          if(worksheetDO==null)
              worksheetDO = new TestWorksheetDO();   
          worksheetDO.setTotalCapacity(tc);
      }
       
      return worksheetDO;
    }
    
    private List<TestWorksheetItemDO> getWorksheetItemsFromRPC(FormRPC rpcSend) {
        DataModel model = (DataModel)rpcSend.getField("worksheetTable")
                                              .getValue();
        
        List<TestWorksheetItemDO> worksheetItemDOList = new ArrayList<TestWorksheetItemDO>();

        for (int i = 0; i < model.size(); i++) {
            DataSet row = model.get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
            worksheetItemDO.setDelete(false);            

            NumberField id = (NumberField)((DataMap)row.getData()).get("id");
            if (id != null) {
                if (id.getValue() != null) {
                    worksheetItemDO.setId((Integer)id.getValue());
                }
            }

            NumberField worksheetId  = (NumberField)rpcSend.getField(TestMeta.getTestWorksheet().getId());
            if (worksheetId != null) {
                if (worksheetId.getValue() != null) {
                    worksheetItemDO.setTestWorksheetId((Integer)worksheetId.getValue());
                }
            }

            NumberField position = (NumberField)row.get(0);
            if (position != null) {
                worksheetItemDO.setPosition((Integer)position.getValue());
             }
            

            DropDownField typeId = (DropDownField)row.get(1);
            if (typeId != null) {
                if (typeId.getValue() != null && !typeId.getValue().equals(new Integer(-1))) {
                    worksheetItemDO.setTypeId((Integer)typeId.getValue());
                }
            }
            
            StringField qcName = (StringField)row.get(2);
            if(qcName!=null){
                worksheetItemDO.setQcName((String)qcName.getValue()); 
            }
            
            worksheetItemDOList.add(worksheetItemDO);
        }
        
        for (int i = 0; i < model.getDeletions().size(); i++) {
            DataSet row = (DataSet)model.getDeletions().get(i);
            TestWorksheetItemDO worksheetItemDO = new TestWorksheetItemDO();
            
                worksheetItemDO.setDelete(true);            

            NumberField id = (NumberField)((DataMap)row.getData()).get("id");
            if (id != null) {
                if (id.getValue() != null) {
                    worksheetItemDO.setId((Integer)id.getValue());
                }
            }

            NumberField worksheetId  = (NumberField)rpcSend.getField(TestMeta.getTestWorksheet().getId());
            if (worksheetId != null) {
                if (worksheetId.getValue() != null) {
                    worksheetItemDO.setTestWorksheetId((Integer)worksheetId.getValue());
                }
            }

            NumberField position = (NumberField)row.get(0);
            if (position != null) {
                worksheetItemDO.setPosition((Integer)position.getValue());
             }
            

            DropDownField typeId = (DropDownField)row.get(1);
            if (typeId != null) {
                if (typeId.getValue() != null && !typeId.getValue().equals(new Integer(-1))) {
                    worksheetItemDO.setTypeId((Integer)typeId.getValue());
                }
            }
            
            StringField qcName = (StringField)row.get(2);
            if(qcName!=null){
                worksheetItemDO.setQcName((String)qcName.getValue()); 
            }
            
            worksheetItemDOList.add(worksheetItemDO);
        }
        model.getDeletions().clear();
        return worksheetItemDOList;
    }
    

    private void setFieldsInRPC(FormRPC rpcReturn, TestIdNameMethodIdDO testDO) {
        rpcReturn.setFieldValue(TestMeta.getId(), testDO.getId());
        rpcReturn.setFieldValue(TestMeta.getName(), testDO.getName());
        rpcReturn.setFieldValue(TestMeta.getMethodId(), 
                                new DataSet(new NumberObject(testDO.getMethodId())));           
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
        
        if(testDetailsDO.getSectionId()!=null)
        rpcReturn.setFieldValue(TestMeta.getSectionId(),
                                new DataSet(new NumberObject(testDetailsDO.getSectionId())));
        
        if(testDetailsDO.getScriptletId()!=null)
        rpcReturn.setFieldValue(TestMeta.getScriptletId(),
                                new DataSet(new NumberObject(testDetailsDO.getScriptletId())));
        
        if(testDetailsDO.getTestFormatId()!=null)
        rpcReturn.setFieldValue(TestMeta.getTestFormatId(),
                                new DataSet(new NumberObject(testDetailsDO.getTestFormatId())));   

        if(testDetailsDO.getRevisionMethodId()!=null)
        rpcReturn.setFieldValue(TestMeta.getRevisionMethodId(),
                                new DataSet(new NumberObject(testDetailsDO.getRevisionMethodId())));                  
        
        
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
                // new TableRow();
                NumberField id = new NumberField(testPrepDO.getId());

                DataMap data = new DataMap();
                
                data.put("id", id);
                
                row.setData(data);;
                
               if(testPrepDO.getPrepTestId()!=null) 
                 row.get(0).setValue(new DataSet(new NumberObject(testPrepDO.getPrepTestId())));

                row.get(1).setValue(testPrepDO.getIsOptional());

                model.add(row);
            }
            
        }
    }
    
    private void fillTestReflexes(List<TestReflexDO> testReflexDOList,
                                  FormRPC rpcReturn){
        DataModel model = (DataModel)rpcReturn.getField("testReflexTable")
                                                .getValue();
        model.clear();        
        if(testReflexDOList.size() > 0){
           //modelList = new ArrayList<DataModel>();
            for(int iter = 0; iter < testReflexDOList.size(); iter++){
              TestReflexDO refDO = testReflexDOList.get(iter);
              DataSet row = model.createNewSet();
              
              NumberField id = new NumberField(refDO.getId());
              DataMap data = new DataMap();
              
              data.put("id", id);
              row.setData(data);
              
              if(refDO.getAddTestId()!=null)
               row.get(0).setValue(new DataSet(new NumberObject(refDO.getAddTestId())));

              if(refDO.getTestAnalyteId()!=null)
               row.get(1).setValue(new DataSet(new NumberObject(refDO.getTestAnalyteId())));
              
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
        
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getNumberFormatId(),
                                worksheetDO.getNumberFormatId());    
        
        rpcReturn.setFieldValue(TestMeta.getTestWorksheet().getScriptletId(),
                                worksheetDO.getScriptletId());    
        
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
        TreeDataModel model =  (TreeDataModel)((FormRPC)rpcReturn.getField("testAnalyte")).getFieldValue("analyteTree");
        TreeDataItem currGroupItem = null;
        for(int iter = 0 ; iter < analyteDOList.size(); iter++){            
            Integer analyteGroup = new Integer(-999);
            boolean newGroup = true;
            int numGroups = 0;
            TestAnalyteDO analyteDO = analyteDOList.get(iter);
            if(analyteDO.getAnalyteGroup()!=null){
             //if(analyteGroup.equals(analyteDO.getAnalyteGroup())){ 
                              
                  if(analyteGroup.equals(analyteDO.getAnalyteGroup())){                      
                      currGroupItem.addItem(createAnalyteNode(analyteDO));
                  }else{
                      //if(currGroupItem == null){  
                          currGroupItem = createGroupNode(numGroups,model);
                          numGroups++;
                          model.add(currGroupItem);                          
                      //}
                      currGroupItem.addItem(createAnalyteNode(analyteDO));
                  }                  
                                         
              analyteGroup = analyteDO.getAnalyteGroup();
            //}
            //else{
              //  analyteGroup = analyteDO.getAnalyteGroup();                
                //currGroupItem = null;
            //}
          }else{
              model.add(createAnalyteNode(analyteDO));
          }    
        }
    }
    
    private TreeDataItem createAnalyteNode(TestAnalyteDO analyteDO){
      TreeDataItem item = new TreeDataItem();
        item.setLabel(new StringObject(""));       
        /*DataSet set = new DataSet();
        set.setKey(new NumberField(-1));
        set.add(new StringField(""));
        DropDownField field = new DropDownField();
        field.add(set);
        field.getSelections().add(set);
        item.add(field);*/
        item.add(new DropDownField(new DataSet(new NumberField(analyteDO.getAnalyteId()))));
        item.add(new DropDownField(new DataSet(new NumberField(analyteDO.getTypeId()))));
        CheckField chfield = new CheckField();
        chfield.setValue(analyteDO.getIsReportable());
        item.add(chfield);
        item.add(new DropDownField(new DataSet(new NumberField(analyteDO.getScriptletId()))));         
        //item.add(new DataSet(new NumberObject(-1)));
        item.add(new NumberField(analyteDO.getResultGroup()));       
        return item;
    }
    
    private TreeDataItem createGroupNode(int id ,TreeDataModel model){
        TreeDataItem item = model.createTreeItem(new NumberObject(id));
          item.setLabel(new StringObject("Group"));       
          /*DataSet set = new DataSet();
          set.setKey(new NumberField(-1));
          set.add(new StringField(""));
          DropDownField field = new DropDownField();
          field.add(set);
          field.getSelections().add(set);
          item.add(field);*/
          item.add(new DropDownField(new DataSet(new NumberField(-1))));
          item.add(new DropDownField(new DataSet(new NumberField(-1))));          
          item.add(new CheckField());
          item.add(new DropDownField(new DataSet(new NumberField(-1))));         
          //item.add(new DataSet(new NumberObject(-1)));
          item.add(new NumberField());       
          return item;
      }
        

    private TestIdNameMethodIdDO getTestIdNameMethodIdDOFromRPC(FormRPC rpcSend) {
        NumberField testId = (NumberField)rpcSend.getField(TestMeta.getId());
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        testDO.setId((Integer)testId.getValue());
        testDO.setName(((String)rpcSend.getFieldValue(TestMeta.getName())));
        Integer methodId = ((Integer)rpcSend.getFieldValue(TestMeta.getMethodId()));
        if (methodId != null && !methodId.equals(new Integer(-1))) {
            testDO.setMethodId(methodId);
        }

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

        Integer labelId = (Integer)rpcSend.getFieldValue(TestMeta.getLabelId());
        if (labelId != null && !labelId.equals(new Integer(-1))) {
            testDetailsDO.setLabelId(labelId);
        }

        // testDetailsDO.setLabelId((Integer)rpcSend.getFieldValue(TestMeta.getLabelId()));
        testDetailsDO.setLabelQty((Integer)rpcSend.getFieldValue(TestMeta.getLabelQty()));
        testDetailsDO.setReportingDescription((String)rpcSend.getFieldValue(TestMeta.getReportingDescription()));

        Integer rmId = (Integer)rpcSend.getFieldValue(TestMeta.getRevisionMethodId());
        if (rmId != null && !rmId.equals(new Integer(-1))) {
            testDetailsDO.setRevisionMethodId(rmId);
        }

        // testDetailsDO.setRevisionMethodId((Integer)rpcSend.getFieldValue(TestMeta.getRevisionMethodId()));
        Integer scrId = (Integer)rpcSend.getFieldValue(TestMeta.getScriptletId());
        if (scrId != null && !scrId.equals(new Integer(-1))) {
            testDetailsDO.setScriptletId(scrId);
        }
        // testDetailsDO.setScriptletId((Integer)rpcSend.getFieldValue(TestMeta.getScriptletId()));
        Integer sectionId = (Integer)rpcSend.getFieldValue(TestMeta.getSectionId());
        if (sectionId != null && !sectionId.equals(new Integer(-1))) {
            testDetailsDO.setSectionId(sectionId);
        }

        // testDetailsDO.setSectionId((Integer)rpcSend.getFieldValue(TestMeta.getSectionId()));
        Integer tfId = (Integer)rpcSend.getFieldValue(TestMeta.getTestFormatId());
        if (tfId != null && !tfId.equals(new Integer(-1))) {
            testDetailsDO.setTestFormatId(tfId);
        }
        // testDetailsDO.setTestFormatId((Integer)rpcSend.getFieldValue(TestMeta.getTestFormatId()));

        Integer ttId = (Integer)rpcSend.getFieldValue(TestMeta.getTestTrailerId());
        if (ttId != null && !ttId.equals(new Integer(-1))) {
            testDetailsDO.setTestTrailerId(ttId);
        }

        // testDetailsDO.setTestTrailerId((Integer)rpcSend.getFieldValue(TestMeta.getTestTrailerId()));
        testDetailsDO.setTimeHolding((Integer)rpcSend.getFieldValue(TestMeta.getTimeHolding()));
        testDetailsDO.setTimeTaAverage((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaAverage()));
        testDetailsDO.setTimeTaMax((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaMax()));
        testDetailsDO.setTimeTaWarning((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaWarning()));
        testDetailsDO.setTimeTransit((Integer)rpcSend.getFieldValue(TestMeta.getTimeTransit()));
        return testDetailsDO;
    }

    private void setRpcErrors(List exceptionList, FormRPC rpcSend) {
        TableField sampleTypeTable = (TableField)((FormRPC)rpcSend.getField("sampleType")).getField("sampleTypeTable");
        TableField prepTestTable = (TableField)((FormRPC)rpcSend.getField("prepAndReflex")).getField("testPrepTable");
        
        TableField testReflexTable = (TableField)((FormRPC)rpcSend.getField("prepAndReflex")).getField("testReflexTable");
        
        TableField worksheetTable = (TableField)((FormRPC)rpcSend.getField("worksheet")).getField("worksheetTable");

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
                    //DataSet row = prepTestTable.get(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                    //row.getField(prepTestTable.getColumnIndexByFieldName(fieldName))
                      // .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   prepTestTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    
                    
                } else if (ferrex.getFieldName()
                                 .startsWith(TestTypeOfSampleMetaMap.getTableName() + ":")) {
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                       .substring(TestTypeOfSampleMetaMap.getTableName()
                                                                                                                         .length() + 1);
                    //TableRow row = sampleTypeTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                   // row.getColumn(sampleTypeTable.getColumnIndexByFieldName(fieldName))
                   //    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                    sampleTypeTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                }
                else if (ferrex.getFieldName()
                                .startsWith(TestReflexMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                      .substring(TestReflexMetaMap.getTableName()
                                                                                                                        .length() + 1);
                   //TableRow row = testReflexTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                   //row.getColumn(testReflexTable.getColumnIndexByFieldName(fieldName))
                     // .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   testReflexTable.getField(index, fieldName)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
               }
                else if (ferrex.getFieldName()
                                .startsWith(TestWorksheetItemMetaMap.getTableName() + ":")) {
                   String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                        .substring(TestWorksheetItemMetaMap.getTableName().length() + 1);
                   
                   //TableRow row = worksheetTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                   //row.getColumn(worksheetTable.getColumnIndexByFieldName(fieldName))
                     // .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   worksheetTable.getField(index, fieldName)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage())); 
               } 
            } else if (exceptionList.get(i) instanceof FieldErrorException) {
                String nameWithRPC = ((FieldErrorException)exceptionList.get(i)).getFieldName();
                if(nameWithRPC.startsWith("worksheet:")){
                    String fieldName = nameWithRPC.substring(10);
                    FormRPC workSheetRPC = (FormRPC)rpcSend.getField("worksheet");
                    
                    workSheetRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                    
                }else if(nameWithRPC.startsWith("details:")){
                    String fieldName = nameWithRPC.substring(8);
                    FormRPC detailsRPC = (FormRPC)rpcSend.getField("details");
                    
                    detailsRPC.getField(fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                }
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName())
                       .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
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

    private void loadSectionDropDown(List<SectionIdNameDO> sections,
                                     DataModel model) {
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(-1);
        blankset.setKey(blankNumberId);

        model.add(blankset);

        for (Iterator iter = sections.iterator(); iter.hasNext();) {
            SectionIdNameDO sectionDO = (SectionIdNameDO)iter.next();

            DataSet set = new DataSet();
            // id
            Integer dropdownId = sectionDO.getId();
            // entry
            String dropdownText = sectionDO.getName();

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
        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);

        NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
        blankNumberId.setValue(new Integer(-1));

        blankset.setKey(blankNumberId);

        model.add(blankset);

        int i = 0;
        while (i < qaedDOList.size()) {
            DataSet set = new DataSet();

            // id
            Integer dropdownId = null;
            // entry
            String dropDownText = null;
            // method
            String methodName = null;

            QaEventTestDropdownDO resultDO = (QaEventTestDropdownDO)qaedDOList.get(i);
            dropdownId = resultDO.getId();
            dropDownText = resultDO.getTest();
            methodName = resultDO.getMethod();

            StringObject textObject = new StringObject();

            textObject.setValue(dropDownText + " , " + methodName);

            set.add(textObject);

            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);

            numberId.setValue(dropdownId);

            set.setKey(numberId);

            model.add(set);
            i++;
        }
    }
}
