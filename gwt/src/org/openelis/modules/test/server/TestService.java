/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.modules.test.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.domain.SampleTypePrepTestListDO;
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestTypeOfSampleDO;
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
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.gwt.widget.pagedtree.TreeModelItem;

import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.security.domain.SectionIdNameDO;
import org.openelis.security.meta.SystemUserSectionMeta;
import org.openelis.security.remote.SystemUserUtilRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestService implements AppScreenFormServiceInt {

    private static final int leftTableRowsPerPage = 20;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    private static final TestMetaMap TestMeta = new TestMetaMap();

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = remote.getTestIdNameMethod((Integer)key.getKey()
                                                                             .getValue());
        setFieldsInRPC(rpcReturn, testDO);

        if (((FormRPC)rpcReturn.getField("details")).load) {
            FormRPC detailsRPC = (FormRPC)rpcReturn.getField("details");
            loadTestDetails(key, detailsRPC);
        }

        if (((FormRPC)rpcReturn.getField("sampleAndPrep")).load) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("sampleAndPrep");
            loadSampleTypesPrepTests(key, samplePrepRPC);
        }

        return rpcReturn;
    }

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
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("TestQuery", rpcSend);
        }

        // fill the model with the query results
        int i = 0;
        // model.clear();
        model = new DataModel();
        while (i < testNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)testNames.get(i);

            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());

            row.setKey(id);
            row.addObject(name);
            model.add(row);
            i++;
        }

        return model;
    }
    
    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        TestDetailsDO testDetailsDO = new TestDetailsDO();
        SampleTypePrepTestListDO listDO = null;

        Integer testId;

        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        // if(((FormRPC)rpcSend.getField("details")).load){
        testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));
        // }
        listDO = getSampleTypeAndPrepTestsFromRPC((FormRPC)rpcReturn.getField("sampleAndPrep"),
                                                  null);
        List exceptionList = remote.validateForUpdate(testDO,
                                                      testDetailsDO,
                                                      listDO);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);

            return rpcSend;
        }

        try {
            testId = remote.updateTest(testDO, testDetailsDO, listDO);
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

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestIdNameMethodIdDO testDO = new TestIdNameMethodIdDO();
        TestDetailsDO testDetailsDO = null;
        SampleTypePrepTestListDO listDO = null;

        testDO = getTestIdNameMethodIdDOFromRPC(rpcSend);
        if (((FormRPC)rpcSend.getField("details")).load)
            testDetailsDO = getTestDetailsDOFromRPC((FormRPC)rpcReturn.getField("details"));

        if (((FormRPC)rpcSend.getField("sampleAndPrep")).load) {
            NumberField testId = (NumberField)rpcSend.getField(TestMeta.getId());
            listDO = getSampleTypeAndPrepTestsFromRPC((FormRPC)rpcReturn.getField("sampleAndPrep"),
                                                      (Integer)testId.getValue());
        }
        List exceptionList = remote.validateForUpdate(testDO,
                                                      testDetailsDO,
                                                      listDO);

        System.out.println("exceptionList.size " + exceptionList.size());

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);

            return rpcSend;
        }

        try {
            remote.updateTest(testDO, testDetailsDO, listDO);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            else {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }
            // return rpcSend;
        }
        setFieldsInRPC(rpcReturn, testDO);
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

        if (tab.equals("sampleAndPrepTab")) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("sampleAndPrep");
            loadSampleTypesPrepTests(key, samplePrepRPC);
        }
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

        if (tab.equals("sampleAndPrepTab")) {
            FormRPC samplePrepRPC = (FormRPC)rpcReturn.getField("sampleAndPrep");
            loadSampleTypesPrepTests(key, samplePrepRPC);
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

        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("methods", methodDropDownField);
        map.put("labels", labelDropDownField);
        map.put("testTrailers", testTrailerDropDownField);
        map.put("scriptlets", scriptletDropDownField);
        map.put("sections", sectionDropDownField);
        map.put("sampleTypes", sampleTypeDropDownField);
        map.put("unitsOfMeasure", measureUnitDropDownField);
        map.put("prepTests", prepTestDropDownField);
        return map;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
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
        } else if (cat.equals("unitOfMeasure")) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("unit_of_measure"));
        } else if (cat.equals("sampleType")) {
            values = catRemote.getDropdownValues(catRemote.getCategoryId("type_of_sample"));
        } else if (cat.equals("prepTest")) {
            //values = remote.getPrepTestDropDownValues();
            List<QaEventTestDropdownDO> qaedDOList = remote.getPrepTestDropDownValues();
            loadPrepTestDropDown(qaedDOList,model);
        } else if (cat.equals("section")) {
            SystemUserUtilRemote utilRemote = (SystemUserUtilRemote)EJBFactory.lookup("SystemUserUtilBean/remote");
            List<SectionIdNameDO> sections = utilRemote.getSections("openelis");

            if (sections != null) {
                loadSectionDropDown(sections, model);
            }
            // return model;
        }

        if (values != null) {
            loadDropDown(values, model);
        }

        return model;
    }

    public FormRPC loadTestDetails(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TestDetailsDO testDetailsDO = remote.getTestDetails((Integer)((NumberObject)key.getKey()).getValue());
        fillTestDetails(testDetailsDO, rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }

    public FormRPC loadSampleTypesPrepTests(DataSet key, FormRPC rpcReturn) {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        SampleTypePrepTestListDO listDO = remote.getSampleTypesAndPrepTests((Integer)((NumberObject)key.getKey()).getValue());
        fillSampleTypePrepTest(listDO, rpcReturn);
        rpcReturn.load = true;
        return rpcReturn;
    }

    private SampleTypePrepTestListDO getSampleTypeAndPrepTestsFromRPC(FormRPC rpcSend,
                                                                      Integer testId) {
        TableModel model = (TableModel)rpcSend.getField("sampleTypeTable")
                                              .getValue();

        SampleTypePrepTestListDO listDO = new SampleTypePrepTestListDO();

        List<TestTypeOfSampleDO> typeOfSampleDOList = new ArrayList<TestTypeOfSampleDO>();

        for (int i = 0; i < model.numRows(); i++) {
            TableRow row = model.getRow(i);
            TestTypeOfSampleDO testTypeOfSampleDO = new TestTypeOfSampleDO();

            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if (deleteFlag == null) {
                testTypeOfSampleDO.setDelete(false);
            } else {
                testTypeOfSampleDO.setDelete("Y".equals(deleteFlag.getValue()));
            }

            NumberField id = (NumberField)row.getHidden("id");
            if (id != null) {
                if (id.getValue() != null) {
                    testTypeOfSampleDO.setId((Integer)id.getValue());
                }
            }

            testTypeOfSampleDO.setTestId(testId);

            DropDownField typeOfSampleId = (DropDownField)row.getColumn(0);
            if (typeOfSampleId != null) {
                if (typeOfSampleId.getValue() != null && !typeOfSampleId.getValue().equals(new Integer(-1))) {
                    testTypeOfSampleDO.setTypeOfSampleId((Integer)typeOfSampleId.getValue());
                }
            }

            DropDownField unitOfMeasureId = (DropDownField)row.getColumn(1);
            if (unitOfMeasureId != null) {
                if (unitOfMeasureId.getValue() != null && !unitOfMeasureId.getValue().equals(new Integer(-1))) {
                    testTypeOfSampleDO.setUnitOfMeasureId((Integer)unitOfMeasureId.getValue());
                }
            }

            typeOfSampleDOList.add(testTypeOfSampleDO);
        }
        listDO.setTypeOfSampleDOList(typeOfSampleDOList);

        model = (TableModel)rpcSend.getField("testPrepTable").getValue();

        List<TestPrepDO> testPrepDOList = new ArrayList<TestPrepDO>();
        for (int j = 0; j < model.numRows(); j++) {
            TableRow row = model.getRow(j);
            TestPrepDO testPrepDO = new TestPrepDO();

            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");

            if (deleteFlag == null) {
                testPrepDO.setDelete(false);
            } else {
                testPrepDO.setDelete("Y".equals(deleteFlag.getValue()));
            }

            NumberField id = (NumberField)row.getHidden("id");

            if (id != null) {
                if (id.getValue() != null) {
                    testPrepDO.setId((Integer)id.getValue());
                }
            }

            testPrepDO.setTestId(testId);

            DropDownField prepTestId = (DropDownField)row.getColumn(0);
            if (prepTestId != null) {
                if (prepTestId.getValue() != null && !prepTestId.getValue().equals(new Integer(-1))) {
                    testPrepDO.setPrepTestId((Integer)prepTestId.getValue());
                }
            }

            CheckField isOptional = (CheckField)row.getColumn(1);
            testPrepDO.setIsOptional((String)isOptional.getValue());

            testPrepDOList.add(testPrepDO);
        }

        listDO.setTestPrepDOList(testPrepDOList);
        
        return listDO;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, TestIdNameMethodIdDO testDO) {
        rpcReturn.setFieldValue(TestMeta.getId(), testDO.getId());
        rpcReturn.setFieldValue(TestMeta.getName(), testDO.getName());
        rpcReturn.setFieldValue(TestMeta.getMethodId(), testDO.getMethodId());
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
        rpcReturn.setFieldValue(TestMeta.getTestTrailerId(),
                                testDetailsDO.getTestTrailerId());
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
        rpcReturn.setFieldValue(TestMeta.getLabelId(),
                                testDetailsDO.getLabelId());
        rpcReturn.setFieldValue(TestMeta.getLabelQty(),
                                testDetailsDO.getLabelQty());
        rpcReturn.setFieldValue(TestMeta.getSectionId(),
                                testDetailsDO.getSectionId());
        rpcReturn.setFieldValue(TestMeta.getScriptletId(),
                                testDetailsDO.getScriptletId());
        rpcReturn.setFieldValue(TestMeta.getTestFormatId(),
                                testDetailsDO.getTestFormatId());
        rpcReturn.setFieldValue(TestMeta.getRevisionMethodId(),
                                testDetailsDO.getRevisionMethodId());
    }

    private void fillSampleTypePrepTest(SampleTypePrepTestListDO listDO,
                                        FormRPC rpcReturn) {
        List<TestPrepDO> testPrepDOList = listDO.getTestPrepDOList();
        TableModel model = (TableModel)rpcReturn.getField("testPrepTable")
                                                .getValue();
        // new TableModel();
        model.reset();
        if (testPrepDOList.size() > 0) {
            for (int iter = 0; iter < testPrepDOList.size(); iter++) {
                TestPrepDO testPrepDO = (TestPrepDO)testPrepDOList.get(iter);

                TableRow row = model.createRow();
                // new TableRow();
                NumberField id = new NumberField(testPrepDO.getId());

                row.addHidden("id", id);

                row.getColumn(0).setValue(testPrepDO.getPrepTestId());

                row.getColumn(1).setValue(testPrepDO.getIsOptional());

                model.addRow(row);
            }
        }

        List<TestTypeOfSampleDO> sampleTypeDOList = listDO.getTypeOfSampleDOList();

        // model = new TableModel();
        model = (TableModel)rpcReturn.getField("sampleTypeTable").getValue();
        model.reset();

        if (sampleTypeDOList.size() > 0) {
            for (int iter = 0; iter < sampleTypeDOList.size(); iter++) {
                TestTypeOfSampleDO testTypeOfSampleDO = (TestTypeOfSampleDO)sampleTypeDOList.get(iter);

                TableRow row = model.createRow();
                // new TableRow();
                NumberField id = new NumberField(testTypeOfSampleDO.getId());

                row.addHidden("id", id);

                row.getColumn(0)
                   .setValue(testTypeOfSampleDO.getTypeOfSampleId());

                row.getColumn(1)
                   .setValue(testTypeOfSampleDO.getUnitOfMeasureId());

                model.addRow(row);
            }

        }

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
        
        //testDetailsDO.setLabelId((Integer)rpcSend.getFieldValue(TestMeta.getLabelId()));
        testDetailsDO.setLabelQty((Integer)rpcSend.getFieldValue(TestMeta.getLabelQty()));
        testDetailsDO.setReportingDescription((String)rpcSend.getFieldValue(TestMeta.getReportingDescription()));
        
        Integer rmId = (Integer)rpcSend.getFieldValue(TestMeta.getRevisionMethodId());                
            if (rmId != null && !rmId.equals(new Integer(-1))) {
                testDetailsDO.setRevisionMethodId(rmId);
            }
                
        //testDetailsDO.setRevisionMethodId((Integer)rpcSend.getFieldValue(TestMeta.getRevisionMethodId()));
        Integer scrId = (Integer)rpcSend.getFieldValue(TestMeta.getScriptletId());
             if (scrId != null && !scrId.equals(new Integer(-1))) {
                testDetailsDO.setScriptletId(scrId);
          }           
        //testDetailsDO.setScriptletId((Integer)rpcSend.getFieldValue(TestMeta.getScriptletId()));
        Integer sectionId = (Integer)rpcSend.getFieldValue(TestMeta.getSectionId());        
            if (sectionId != null && !sectionId.equals(new Integer(-1))) {
                testDetailsDO.setSectionId(sectionId);
            }
                          
        //testDetailsDO.setSectionId((Integer)rpcSend.getFieldValue(TestMeta.getSectionId()));
        Integer tfId  = (Integer)rpcSend.getFieldValue(TestMeta.getTestFormatId());        
         if (tfId != null && !tfId.equals(new Integer(-1))) {
               testDetailsDO.setTestFormatId(tfId);
         }  
        //testDetailsDO.setTestFormatId((Integer)rpcSend.getFieldValue(TestMeta.getTestFormatId()));        
        
        Integer ttId  = (Integer)rpcSend.getFieldValue(TestMeta.getTestTrailerId());                
          if (ttId != null && !ttId.equals(new Integer(-1))) {
               testDetailsDO.setTestFormatId(ttId);            
         }
        
        //testDetailsDO.setTestTrailerId((Integer)rpcSend.getFieldValue(TestMeta.getTestTrailerId()));
        testDetailsDO.setTimeHolding((Integer)rpcSend.getFieldValue(TestMeta.getTimeHolding()));
        testDetailsDO.setTimeTaAverage((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaAverage()));
        testDetailsDO.setTimeTaMax((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaMax()));
        testDetailsDO.setTimeTaWarning((Integer)rpcSend.getFieldValue(TestMeta.getTimeTaWarning()));
        testDetailsDO.setTimeTransit((Integer)rpcSend.getFieldValue(TestMeta.getTimeTransit()));
        return testDetailsDO;
    }

    private void setRpcErrors(List exceptionList, FormRPC rpcSend) {
        TableModel sampleTypeTable = (TableModel)((FormRPC)rpcSend.getField("sampleAndPrep")).getField("sampleTypeTable")
                                                                                        .getValue();
        TableModel prepTestTable = (TableModel)((FormRPC)rpcSend.getField("sampleAndPrep")).getField("testPrepTable")
                                                                                      .getValue();
        
        // we need to get the keys and look them up in the resource bundle for
        // internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                FieldErrorException ferrex = (FieldErrorException)exceptionList.get(i);
                
                if (ferrex.getFieldName().startsWith(TestPrepMetaMap.getTableName()+":")){
                  String fieldName =   ((TableFieldErrorException)exceptionList.get(i)).getFieldName().substring(TestPrepMetaMap.getTableName().length()+1);  
                  TableRow row = prepTestTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                  row.getColumn(prepTestTable.getColumnIndexByFieldName(fieldName))
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                }else if (ferrex.getFieldName().startsWith(TestTypeOfSampleMetaMap.getTableName()+":")){
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName().substring(TestTypeOfSampleMetaMap.getTableName().length()+1);  
                    TableRow row = sampleTypeTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                    row.getColumn(sampleTypeTable.getColumnIndexByFieldName(fieldName))
                     .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                  }
              } else if (exceptionList.get(i) instanceof FieldErrorException){
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
        blankset.addObject(blankStringId);

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

            set.addObject(textObject);

            set.setKey(numberId);

            model.add(set);
        }
    }

    private void loadSectionDropDown(List<SectionIdNameDO> sections,
                                     DataModel model) {
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.addObject(blankStringId);

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

            set.addObject(textObject);

            set.setKey(numberId);

            model.add(set);
        }
    }
    
    private void loadPrepTestDropDown(List<QaEventTestDropdownDO> qaedDOList,DataModel model){
       
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
             
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
            blankNumberId.setValue(new Integer(-1));
            

            blankset.setKey(blankNumberId);
    
            model.add(blankset);                
    
        int i=0;
        while(i < qaedDOList.size()){
            DataSet set = new DataSet();
            
        //id
        Integer dropdownId = null;
        //entry
        String dropDownText = null;
        //method
        String methodName = null;
        
        
            QaEventTestDropdownDO resultDO = (QaEventTestDropdownDO) qaedDOList.get(i);
            dropdownId = resultDO.getId();
            dropDownText = resultDO.getTest();
            methodName = resultDO.getMethod();
                
        StringObject textObject = new StringObject();
                    
        
        textObject.setValue(dropDownText+" / "+methodName);       
        
        set.addObject(textObject);            

        NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);

        numberId.setValue(dropdownId);

            set.setKey(numberId);           
        
        model.add(set);
        i++;
       }
    }
}
