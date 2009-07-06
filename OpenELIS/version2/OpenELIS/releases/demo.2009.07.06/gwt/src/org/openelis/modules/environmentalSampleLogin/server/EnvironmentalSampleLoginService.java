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
package org.openelis.modules.environmentalSampleLogin.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisTestDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleProjectDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestMethodAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.manager.AnalysesManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemsManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManagerIOClient;
import org.openelis.manager.SampleOrganizationsManager;
import org.openelis.manager.SampleProjectsManager;
import org.openelis.modules.environmentalSampleLogin.client.AnalysisForm;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginForm;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSubForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleItemAndAnalysisForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleItemForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleLocationForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleOrgProjectForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class EnvironmentalSampleLoginService implements AppScreenFormServiceInt<EnvironmentalSampleLoginForm,Query<TableDataRow<Integer>>>, AutoCompleteServiceInt {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    private static final int leftTableRowsPerPage = 12;
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        SampleEnvironmentalRemote remote = (SampleEnvironmentalRemote)EJBFactory.lookup("openelis/SampleEnvironmentalBean/remote");
        List sampleIds = null;
        try{    
            sampleIds = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }

    //fill the model with the query results
    int i=0;
    if(query.results == null)
        query.results = new TableDataModel<TableDataRow<Integer>>();
    else
        query.results.clear();
    while(i < sampleIds.size() && i < leftTableRowsPerPage) {
        IdNameDO resultDO = (IdNameDO)sampleIds.get(i); 
        query.results.add(new TableDataRow<Integer>(resultDO.getId()));
        i++;
    } 
    return query;
    }

    public EnvironmentalSampleLoginForm commitAdd(EnvironmentalSampleLoginForm rpc) throws RPCException {
        SampleManager manager = SampleManager.getInstance();
        manager.setManager(new SampleManagerIOClient());
        
        //create test sampleDO
        SampleDO smplDO = manager.getSample();
        smplDO.setAccessionNumber(1);
        smplDO.setClientReference("client ref");
        smplDO.setCollectionDate(new Date());
        smplDO.setCollectionTime(new Date());
        smplDO.setEnteredDate(new Date());
        smplDO.setNextItemSequence(2);
        smplDO.setPackageId(3);
        smplDO.setReceivedById(1234);
        smplDO.setReceivedDate(new Date());
        smplDO.setReleasedDate(new Date());
        smplDO.setRevision(12);
        smplDO.setStatusId(344);
        
        //create sample domain
        SampleEnvironmentalManager envManager = SampleEnvironmentalManager.getInstance();
        SampleEnvironmentalDO envDO = envManager.getEnvironmental();
        envDO.setAddressId(4);
        envDO.setCollector("Joe Farmer");
        envDO.setCollectorPhone("319-325-3256");
        envDO.setDescription("test env item");
        envDO.setIsHazardous("N");
        envDO.setSamplingLocation("location");
        manager.setAdditonalDomainManager(envManager);
        
        //create a few sample items
        SampleItemsManager itemsManager = manager.getSampleItemsManager();
        SampleItemDO itemDO = itemsManager.add();
        itemDO.setContainerId(1);
        itemDO.setContainerReference("ref");
        itemDO.setItemSequence(2);
        itemDO.setQuantity(new Double(3));
        itemDO.setSourceOfSampleId(4);
        itemDO.setSourceOther("other");
        itemDO.setTypeOfSampleId(5);
        itemDO.setUnitOfMeasureId(6);
        
        SampleItemDO item2DO = itemsManager.add();
        item2DO.setContainerId(7);
        item2DO.setContainerReference("ref2");
        item2DO.setItemSequence(8);
        item2DO.setQuantity(new Double(9));
        item2DO.setSourceOfSampleId(10);
        item2DO.setSourceOther("other2");
        item2DO.setTypeOfSampleId(11);
        item2DO.setUnitOfMeasureId(12);
        
        System.out.println("before update");
        manager.update();
        System.out.println("after update");
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm commitUpdate(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm commitDelete(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm fetch(EnvironmentalSampleLoginForm rpc) throws RPCException {
        SampleManager manager = SampleManager.findById(rpc.entityKey);
        SampleDO sampleDO = manager.getSample();
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, (SampleEnvironmentalManager)manager.getAdditionalDomainManager());
        loadSampleItemsForm(rpc.sampleItemAndAnalysisForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm fetchForUpdate(EnvironmentalSampleLoginForm rpc) throws RPCException {
        //get the current manager in the session
        SampleManager manager = (SampleManager)SessionManager.getSession().getAttribute("envScreenSampleManager");
        
        //set the id again and lock
        SampleDO sampleDO = manager.getSample();
        sampleDO.setId(rpc.entityKey);
        
        try{
            manager.fetchForUpdate();
            
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, (SampleEnvironmentalManager)manager.getAdditionalDomainManager());
        loadSampleItemsForm(rpc.sampleItemAndAnalysisForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm abort(EnvironmentalSampleLoginForm rpc) throws RPCException {
        //get the current manager in the session
        SampleManager manager = (SampleManager)SessionManager.getSession().getAttribute("envScreenSampleManager");
        
        //set the id again and unlock the record
        SampleDO sampleDO = manager.getSample();
        sampleDO.setId(rpc.entityKey);
        
        manager.fetchAndUnlock();
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, (SampleEnvironmentalManager)manager.getAdditionalDomainManager());
        loadSampleItemsForm(rpc.sampleItemAndAnalysisForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm getScreen(EnvironmentalSampleLoginForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/environmentalSampleLogin.xsl");
        
        return rpc;
    }
    
    private void setFieldsInRPC(EnvironmentalSampleLoginForm form, SampleDO sampleDO){
        form.accessionNumber.setValue(sampleDO.getAccessionNumber());
        form.clientReference.setValue(sampleDO.getClientReference());
        
        if(sampleDO.getCollectionDate() != null && sampleDO.getCollectionDate().getDate() != null)
            form.collectionDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, sampleDO.getCollectionDate().getDate()));
        
        if(sampleDO.getCollectionTime() != null && sampleDO.getCollectionTime().getDate() != null)
            form.collectionTime.setValue(DatetimeRPC.getInstance(Datetime.HOUR, Datetime.MINUTE, sampleDO.getCollectionTime().getDate()));
        
        form.id.setValue(sampleDO.getId());
        //form.orderNumber.setValue(sampleDO.getor);
        
        if(sampleDO.getReceivedDate() != null && sampleDO.getReceivedDate().getDate() != null)
            form.receivedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, sampleDO.getReceivedDate().getDate()));
        
        form.statusId.setValue(new TableDataRow<Integer>(sampleDO.getStatusId()));
        form.nextItemSequence = sampleDO.getNextItemSequence();
        
        form.load = true;
    }
    
    private void loadDomainForm(EnvironmentalSubForm form, SampleEnvironmentalManager envManager) {
        SampleEnvironmentalDO envDO = envManager.getEnvironmental();
        form.collector.setValue(envDO.getCollector());
        form.collectorPhone.setValue(envDO.getCollectorPhone());
        form.description.setValue(envDO.getDescription());
        form.isHazardous.setValue(envDO.getIsHazardous());
        
        form.locationForm.samplingLocation.setValue(envDO.getSamplingLocation());
    }
    
    public SampleLocationForm loadLocationForm(SampleLocationForm rpc) throws RPCException {
        SampleManager manager = (SampleManager)SessionManager.getSession().getAttribute("envScreenSampleManager");
        if(manager != null)
            loadLocationAddress(rpc, ((SampleEnvironmentalManager)manager.getAdditionalDomainManager()).getAddress());
        
        return rpc;
    }
    
    private void loadLocationAddress(SampleLocationForm form, AddressDO addressDO){
        if(addressDO != null){
            form.city.setValue(addressDO.getCity());
            form.country.setValue(new TableDataRow<String>(addressDO.getCountry()));
            form.multUnit.setValue(addressDO.getMultipleUnit());
            form.state.setValue(new TableDataRow<String>(addressDO.getState()));
            form.streetName.setValue(addressDO.getStreetAddress());
            form.zipCode.setValue(addressDO.getZipCode());
        }
        
        form.load = true;
        
    }
    
    private void loadSampleItemsForm(SampleItemAndAnalysisForm form, SampleItemsManager itemsManager) {
        int i, j;
        AnalysesManager am;
        SampleItemDO itemDO;
        TreeDataItem tmp;
        TreeDataItem treeModelItem, row;
        AnalysisForm analysisSubForm;
        SampleItemForm itemSubForm;
        Hashtable<Integer, TreeDataItem> keyTable = new Hashtable<Integer, TreeDataItem>();
        
        TreeDataModel treeModel = form.itemsTestsTree.getValue();
        treeModel.clear();
        
        for(i=0; i<itemsManager.count(); i++){
            itemDO = itemsManager.getSampleItemAt(i);
            
            row = new TreeDataItem(2);
            row.leafType = "sampleItem";
            row.toggle();
            
            itemSubForm = new SampleItemForm();
            itemSubForm.entityKey = itemDO.getId();
            itemSubForm.container.setValue(new TableDataRow<Integer>(itemDO.getContainerId(), new StringObject(itemDO.getContainer())));
            itemSubForm.containerReference.setValue(itemDO.getContainerReference());
            itemSubForm.quantity.setValue(itemDO.getQuantity());
            itemSubForm.typeOfSample.setValue(new TableDataRow<Integer>(itemDO.getTypeOfSampleId(), new StringObject(itemDO.getTypeOfSample())));
            itemSubForm.unitOfMeasure.setValue(new TableDataRow<Integer>(itemDO.getUnitOfMeasureId()));
            itemSubForm.itemSequence = itemDO.getItemSequence();
            
            row.setData(itemSubForm);
            row.key = itemDO.getId();
            //container
            row.cells[0] = new StringObject(itemDO.getItemSequence()+" - "+itemDO.getContainer());
            //source,type
            row.cells[1] = new StringObject(itemDO.getTypeOfSample());
            
            tmp = keyTable.get(itemDO.getId());
            if(tmp != null){
                tmp.addItem(row);
            }else{
                keyTable.put(itemDO.getId(), row);
                treeModel.add(row);
            }
            
            am = itemsManager.getAnalysisAt(i);
            for(j=0; j<am.count(); j++){
                AnalysisTestDO aDO = (AnalysisTestDO)am.getAnalysisAt(j);
                
                //TreeDataItemTest treeModelItem = new TreeDataItemTest(2);
                treeModelItem = new TreeDataItem(2);
                treeModelItem.leafType = "analysis";
                analysisSubForm = new AnalysisForm();
                analysisSubForm.entityKey = aDO.getId();
         
                //autocomplete
                if(aDO.getTestId() == null){
                    analysisSubForm.testId.clear();
                    analysisSubForm.testId.setModel(null);
                }else{
                    TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                    TableDataRow<Integer> testRow = new TableDataRow<Integer>(aDO.getTestId(), new FieldType[]{new StringObject(aDO.test.getName()), new StringObject(aDO.test.getMethodName())});
                    testRow.setData(new IntegerObject(aDO.test.getMethodId()));
                    model.add(testRow);
                    analysisSubForm.testId.setModel(model);
                    analysisSubForm.testId.setValue(model.get(0));
                }
                
                //autocomplete
                if(aDO.test.getMethodId() == null){
                    analysisSubForm.methodId.clear();
                    analysisSubForm.methodId.setModel(null);
                }else{
                    TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                    model.add(new TableDataRow<Integer>(aDO.test.getMethodId(), new StringObject(aDO.test.getMethodName())));
                    analysisSubForm.methodId.setModel(model);
                    analysisSubForm.methodId.setValue(model.get(0));
                }
                
                //autocomplete
                if(aDO.getSectionId() == null){
                    analysisSubForm.sectionId.clear();
                    analysisSubForm.sectionId.setModel(null);
                }else{
                    TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                    model.add(new TableDataRow<Integer>(aDO.getSectionId(), new StringObject(aDO.getSection())));
                    analysisSubForm.sectionId.setModel(model);
                    analysisSubForm.sectionId.setValue(model.get(0));
                }
               
                analysisSubForm.statusId.setValue(new TableDataRow<Integer>(aDO.getStatusId()));
                analysisSubForm.revision.setValue(aDO.getRevision());
                analysisSubForm.isReportable.setValue(aDO.getIsReportable());
                
                if (aDO.getStartedDate() != null && aDO.getStartedDate().getDate() != null)
                    analysisSubForm.startedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getStartedDate().getDate()));
                
                if (aDO.getCompletedDate() != null && aDO.getCompletedDate().getDate() != null)
                    analysisSubForm.completedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getCompletedDate().getDate()));
                
                if (aDO.getReleasedDate() != null && aDO.getReleasedDate().getDate() != null)
                    analysisSubForm.releasedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getReleasedDate().getDate()));
                
                if (aDO.getPrintedDate() != null && aDO.getPrintedDate().getDate() != null)
                    analysisSubForm.printedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getPrintedDate().getDate()));
                
                treeModelItem.setData(analysisSubForm);
                
                treeModelItem.key = aDO.getId();
                treeModelItem.cells[0] = new StringObject(aDO.test.getName() + " : " + aDO.test.getMethodName());
                treeModelItem.cells[1] = new DropDownField<Integer>(new TableDataRow<Integer>(aDO.getStatusId()));
                
                row.addItem(treeModelItem);
            }
        }
    }
    
    private void loadOrgProjectForm(SampleOrgProjectForm form, SampleOrganizationsManager orgManager, SampleProjectsManager projManager) {
        if(projManager.count() > 0){
            form.projectName.setValue(projManager.getSampleProjectAt(0).getProject().getName());
            form.sampleProjectForm.sampleProjectTable.setValue(buildSampleProjectTable(form.sampleProjectForm.sampleProjectTable.getValue(), projManager));
            form.sampleProjectForm.load = true;
        }
        
        if(orgManager.count() > 0){
            SampleOrganizationDO reportToDO = orgManager.getFirstReportTo();
            SampleOrganizationDO billToDO = orgManager.getFirstBillTo();
            
            if(reportToDO != null)
                form.reportToName.setValue(reportToDO.getOrganization().getName());
            if(billToDO != null)
                form.billToName.setValue(billToDO.getOrganization().getName());
            
            form.sampleOrgForm.sampleOrganizationTable.setValue(buildSampleOrganizationTable(orgManager));
            form.sampleOrgForm.load = true;
        }
    }
    
    private TableDataModel<TableDataRow<Integer>> buildSampleProjectTable(TableDataModel<TableDataRow<Integer>> tableModel, SampleProjectsManager projManager){

        for(int i=0; i<projManager.count(); i++){
            TableDataRow<Integer> row = tableModel.createNewSet();
            SampleProjectDO projDO = projManager.getSampleProjectAt(i);
            
            if(projDO.getProject().getId() != null){
                TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                model.add(new TableDataRow<Integer>(projDO.getProject().getId(), new FieldType[]{
                                                                                                 new StringObject(projDO.getProject().getName()),
                                                                                                 new StringObject(projDO.getProject().getDescription())}));
                ((DropDownField<Integer>)row.cells[0]).setModel(model);
                row.cells[0].setValue(model.get(0));
            }
            
            row.cells[1].setValue(projDO.getProject().getDescription());
            row.cells[2].setValue(projDO.getIsPermanent());
            
            tableModel.add(row);
        }
        
        return tableModel;
    }
    
    private TableDataModel<TableDataRow<Integer>> buildSampleOrganizationTable(SampleOrganizationsManager orgManager){
        TableDataModel<TableDataRow<Integer>> tableModel = new TableDataModel<TableDataRow<Integer>>();
        
        //create the default set
        TableDataRow<Integer> defaultRow = new TableDataRow<Integer>(5);
        defaultRow.cells[0]= new DropDownField<Integer>();
        defaultRow.cells[1] = new IntegerField();
        defaultRow.cells[2] =  new DropDownField<Integer>();
        defaultRow.cells[3] = new StringField();
        defaultRow.cells[4] = new StringField();
        tableModel.setDefaultSet(defaultRow);
        
        for(int i=0; i<orgManager.count(); i++){
            TableDataRow<Integer> row = tableModel.createNewSet();
            SampleOrganizationDO orgDO = orgManager.getSampleOrganizationAt(i);
            
            if(orgDO.getTypeId() != null)
                row.cells[0].setValue(new TableDataRow<Integer>(orgDO.getTypeId()));
            
            row.cells[1].setValue(orgDO.getOrganization().getOrganizationId());
            
            if(orgDO.getOrganization().getOrganizationId() != null){
                TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                model.add(new TableDataRow<Integer>(orgDO.getOrganization().getOrganizationId(), 
                                                    new FieldType[]{
                                                                     new StringObject(orgDO.getOrganization().getName()),
                                                                     new StringObject(orgDO.getOrganization().getAddressDO().getStreetAddress()),
                                                                     new StringObject(orgDO.getOrganization().getAddressDO().getCity()),
                                                                     new StringObject(orgDO.getOrganization().getAddressDO().getState())}));
                
                ((DropDownField<Integer>)row.cells[2]).setModel(model);
                row.cells[2].setValue(model.get(0));
            }
            
            row.cells[3].setValue(orgDO.getOrganization().getAddressDO().getCity());
            row.cells[4].setValue(orgDO.getOrganization().getAddressDO().getState());
            
            tableModel.add(row);
        }
        
        return tableModel;
    }

    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        if(cat.equals("testMethod"))
            return getTestMethodMatches(match);
        else if(cat.equals("section"))
            return getSectionMatches(match);
        return null;
    }
    
    private TableDataModel<TableDataRow<Integer>> getTestMethodMatches(String match){
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList;
    
        autoCompleteList = remote.getTestAutoCompleteByName(match+"%", 10);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            TestMethodAutoDO resultDO = (TestMethodAutoDO) autoCompleteList.get(i);
            Integer testId = resultDO.getTestId();
            String testName = resultDO.getTestName();
            Integer methodId = resultDO.getMethodId();
            String methodName = resultDO.getMethodName();

            TableDataRow<Integer> data = new TableDataRow<Integer>(testId,
                                                                   new FieldType[] {
                                                                                    new StringObject(testName),
                                                                                    new StringObject(methodName)
                                                                   }
                                         );
            data.setData(new IntegerObject(methodId));
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;       
    }
    
    private TableDataModel<TableDataRow<Integer>> getSectionMatches(String match){
        SectionRemote remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList;
    
        autoCompleteList = remote.getAutoCompleteSectionByName(match+"%", 10);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            SectionDO resultDO = (SectionDO) autoCompleteList.get(i);
            Integer id = resultDO.getId();
            String name = resultDO.getName();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(id, new StringObject(name));
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;       
    }
    
    /*
    public SampleTreeForm getSampleItemAnalysesTreeModel(SampleTreeForm rpc) throws RPCException{
        //get manager from session
        SampleManager manager = (SampleManager)SessionManager.getSession().getAttribute("envScreenSampleManager");
        SampleItemsManager sim = manager.getSampleItemsManager();
        AnalysesManager am = sim.getAnalysisAt(rpc.treeRow);
        
        //get analyses for the sample item
        TreeDataModel treeModel = new TreeDataModel();
        for(int i=0; i<am.count(); i++){
            AnalysisTestDO aDO = (AnalysisTestDO)am.getAnalysisAt(i);
                    
            //TreeDataItemTest treeModelItem = new TreeDataItemTest(2);
            TreeDataItem treeModelItem = new TreeDataItem(2);
            treeModelItem.leafType = "analysis";
            AnalysisForm analysisSubForm = new AnalysisForm();
            analysisSubForm.testName.setValue(aDO.test.getName());
            analysisSubForm.statusId.setValue(aDO.test.getMethodName());
            analysisSubForm.methodName.setValue(new TableDataRow<Integer>(aDO.getStatusId()));
            analysisSubForm.entityKey = aDO.getId();
            
            treeModelItem.setData(analysisSubForm);
            
            treeModelItem.cells[0] = new StringObject();
            //aDO.test.getName() + " : " + aDO.test.getMethodName());
            treeModelItem.cells[1] = new DropDownField<Integer>();
            //new TableDataRow<Integer>(aDO.getStatusId()));
            
            treeModel.add(treeModelItem);
        }
        
        //get child sample items
        List childItemList = sim.getChildSampleItems(rpc.sampleItemId);
        
        for(int i=0; i<childItemList.size(); i++){
            SampleItemDO itemDO = (SampleItemDO)childItemList.get(i);
            //TreeDataItemSampleItem row = new TreeDataItemSampleItem(2);
            TreeDataItem row = new TreeDataItem(2);
            row.leafType = "sampleItem";
            row.lazy = true;
            SampleItemForm itemSubForm = new SampleItemForm();
            itemSubForm.entityKey = itemDO.getId();
            itemSubForm.container.setValue(new TableDataRow<Integer>(itemDO.getContainerId(), new StringObject(itemDO.getContainer())));
            itemSubForm.containerReference.setValue(itemDO.getContainerReference());
            itemSubForm.quantity.setValue(itemDO.getQuantity());
            itemSubForm.typeOfSample.setValue(new TableDataRow<Integer>(itemDO.getTypeOfSampleId(), new StringObject(itemDO.getTypeOfSample())));
            itemSubForm.unitOfMeasure.setValue(new TableDataRow<Integer>(itemDO.getUnitOfMeasureId()));
            itemSubForm.itemSequence = itemDO.getItemSequence();
            
            row.setData(itemSubForm);
            //container
            row.cells[0] = new StringObject();
            //itemDO.getItemSequence()+" - "+itemDO.getContainer());
            //source,type
            row.cells[1] = new StringObject();
            //itemDO.getTypeOfSample());
            
            treeModel.add(row);
        }
        
        rpc.treeModel = treeModel;
        return rpc;
    }
    */
}