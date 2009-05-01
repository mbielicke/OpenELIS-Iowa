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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.AnalysisTestDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.QueryIntegerField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.manager.AnalysesManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemsManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManagerIOClient;
import org.openelis.manager.SampleOrganizationsManager;
import org.openelis.manager.SampleProjectsManager;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginForm;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSubForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleItemsForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleOrgProjectForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleTreeForm;
import org.openelis.modules.order.client.OrderForm;
import org.openelis.modules.organization.client.OrganizationForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.AnalysisStatusCacheHandler;
import org.openelis.server.handlers.CostCentersCacheHandler;
import org.openelis.server.handlers.OrderStatusCacheHandler;
import org.openelis.server.handlers.SampleContainerCacheHandler;
import org.openelis.server.handlers.SampleStatusCacheHandler;
import org.openelis.server.handlers.SampleTypeCacheHandler;
import org.openelis.server.handlers.ShipFromCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class EnvironmentalSampleLoginService implements AppScreenFormServiceInt<EnvironmentalSampleLoginForm,Query<TableDataRow<Integer>>>{

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
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        SampleManager manager = SampleManager.findById(rpc.entityKey);
        SampleDO sampleDO = manager.getSample();
        sampleDO.setId(rpc.entityKey);
        manager.fetch();
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, ((SampleEnvironmentalManager)manager.getAdditionalDomainManager()).getEnvironmental());
        loadSampleItemsForm(rpc.sampleItemsForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm fetchForUpdate(EnvironmentalSampleLoginForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        SampleManager manager = SampleManager.findById(rpc.entityKey);
        SampleDO sampleDO = manager.getSample();
        sampleDO.setId(rpc.entityKey);
        
        try{
            manager.fetchForUpdate();
            
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, ((SampleEnvironmentalManager)manager.getAdditionalDomainManager()).getEnvironmental());
        loadSampleItemsForm(rpc.sampleItemsForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm abort(EnvironmentalSampleLoginForm rpc) throws RPCException {
        SampleManager manager = SampleManager.findById(rpc.entityKey);
        SampleDO sampleDO = manager.getSample();
        sampleDO.setId(rpc.entityKey);
        
        manager.fetchAndUnlock();
        
        setFieldsInRPC(rpc, sampleDO);
        loadDomainForm(rpc.envInfoForm, ((SampleEnvironmentalManager)manager.getAdditionalDomainManager()).getEnvironmental());
        loadSampleItemsForm(rpc.sampleItemsForm, manager.getSampleItemsManager());
        loadOrgProjectForm(rpc.orgProjectForm, manager.getOrganizationsManager(), manager.getProjectsManager());
        
        //save the new manager in the session
        SessionManager.getSession().setAttribute("envScreenSampleManager", manager);
        
        return rpc;
    }

    public EnvironmentalSampleLoginForm getScreen(EnvironmentalSampleLoginForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/environmentalSampleLogin.xsl");
        
        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.analysisStatuses = AnalysisStatusCacheHandler.getAnalysisStatuses();
        SessionManager.getSession().setAttribute("analysisStatusesVersion",AnalysisStatusCacheHandler.version);
        rpc.sampleContainers = SampleContainerCacheHandler.getSampleContainers();
        SessionManager.getSession().setAttribute("sampleContainerVersion",SampleContainerCacheHandler.version);
        //rpc.sampleTypes = SampleTypeCacheHandler.getSampleTypes();
        //SessionManager.getSession().setAttribute("sampleTypeVersion",SampleTypeCacheHandler.version);
        rpc.sampleStatuses = SampleStatusCacheHandler.getSampleStatuses();
        SessionManager.getSession().setAttribute("sampleStatusVersion",SampleStatusCacheHandler.version);
        
        return rpc;
    }
    
    public void checkModels(EnvironmentalSampleLoginForm rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int analysisStatuses = (Integer)SessionManager.getSession().getAttribute("analysisStatusesVersion");
        int sampleContainers = (Integer)SessionManager.getSession().getAttribute("sampleContainerVersion");
        int sampleStatuses = (Integer)SessionManager.getSession().getAttribute("sampleStatusVersion");
        //int sampleTypes = (Integer)SessionManager.getSession().getAttribute("sampleTypeVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(analysisStatuses != AnalysisStatusCacheHandler.version){
            rpc.analysisStatuses = AnalysisStatusCacheHandler.getAnalysisStatuses();
            SessionManager.getSession().setAttribute("analysisStatusesVersion",AnalysisStatusCacheHandler.version);
        }
        if(sampleContainers != SampleContainerCacheHandler.version){
            rpc.sampleContainers = SampleContainerCacheHandler.getSampleContainers();
            SessionManager.getSession().setAttribute("sampleContainerVersion",SampleContainerCacheHandler.version);
        }
        if(sampleStatuses != SampleStatusCacheHandler.version){
            rpc.sampleStatuses = SampleStatusCacheHandler.getSampleStatuses();
            SessionManager.getSession().setAttribute("sampleStatusVersion",SampleStatusCacheHandler.version);
        }
        //if(sampleTypes != SampleTypeCacheHandler.version){
        //    rpc.sampleTypes = SampleTypeCacheHandler.getSampleTypes();
        //    SessionManager.getSession().setAttribute("sampleTypeVersion",SampleTypeCacheHandler.version);
        //}
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
    }
    
    private void loadDomainForm(EnvironmentalSubForm form, SampleEnvironmentalDO envDO) {
        form.collector.setValue(envDO.getCollector());
        form.collectorPhone.setValue(envDO.getCollectorPhone());
        form.description.setValue(envDO.getDescription());
        form.isHazardous.setValue(envDO.getIsHazardous());
        form.samplingLocation.setValue(envDO.getSamplingLocation());
    }
    
    private void loadSampleItemsForm(SampleItemsForm form, SampleItemsManager itemsManager) {
        TreeDataModel treeModel = form.itemsTestsTree.getValue();
        treeModel.clear();
        
        for(int i=0; i<itemsManager.count(); i++){
            SampleItemDO itemDO = (SampleItemDO)itemsManager.getSampleItemAt(i);
            TreeDataItem row = treeModel.createTreeItem("sampleItem");
            row.lazy = true;
            
            //container
            row.cells[0].setValue(new TableDataRow<Integer>(itemDO.getContainerId()));
            //source,type
            row.cells[1].setValue(itemDO.getTypeOfSample()+" | "+itemDO.getSourceOfSample());
            
            treeModel.add(row);
        }
    }
    
    private void loadOrgProjectForm(SampleOrgProjectForm form, SampleOrganizationsManager orgManager, SampleProjectsManager projManager) {
        if(projManager.count() > 0)
            form.projectName.setValue(projManager.getSampleProjectAt(0).getProject().getName());
        
        if(orgManager.count() > 0)
            form.reportToName.setValue(orgManager.getSampleOrganizationAt(0).getOrganization().getName());
    }
    
    public SampleTreeForm getSampleItemAnalysesTreeModel(SampleTreeForm rpc) throws RPCException{
        //get manager from session
        SampleManager manager = (SampleManager)SessionManager.getSession().getAttribute("envScreenSampleManager");
        AnalysesManager am = manager.getSampleItemsManager().getAnalysisAt(rpc.treeRow);
        
        TreeDataModel treeModel = new TreeDataModel();
        for(int i=0; i<am.count(); i++){
            AnalysisTestDO aDO = (AnalysisTestDO)am.getAnalysisAt(i);
                    
            TreeDataItem treeModelItem = new TreeDataItem(2);
            treeModelItem.leafType = "analysis";
            treeModelItem.key = (aDO.getId());
            treeModelItem.cells[0] = new StringObject(aDO.test.getName() + " : " + aDO.test.getMethodName());
            treeModelItem.cells[1] = new DropDownField<Integer>(new TableDataRow<Integer>(aDO.getStatusId()));
            
            treeModel.add(treeModelItem);
        }
        
        rpc.treeModel = treeModel;
        return rpc;
    }
}