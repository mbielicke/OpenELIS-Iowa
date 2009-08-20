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

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.environmentalSampleLogin.client.SampleEnvQuery;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ProjectRemote;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.utilgwt.AutocompleteRPC;

public class EnvironmentalSampleLoginService {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    private static final int leftTableRowsPerPage = 12;
    
    public SampleEnvQuery query(SampleEnvQuery query) throws RPCException {
        SampleEnvironmentalRemote remote = (SampleEnvironmentalRemote)EJBFactory.lookup("openelis/SampleEnvironmentalBean/remote");

        try{    
            query.results = new ArrayList<IdNameDO>();
            ArrayList<IdNameDO> results = (ArrayList<IdNameDO>)remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            for(IdNameDO result : results) {
                query.results.add(result);
            }
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        return query;
    }
    
    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/environmentalSampleLogin.xsl");      
    }
    
    /*
    public AutocompleteRPC getMatches(AutocompleteRPC rpc) throws RPCException {
        if("project".equals(rpc.cat))
            rpc.model = getProjectMatches(rpc.match);
        else if("organization".equals(rpc.cat))
            rpc.model = getOrganizationMatches(rpc.match);
        else if("testMethod".equals(rpc.cat))
            rpc.model = getTestMethodMatches(rpc.match);
        else if("section".equals(rpc.cat))
            rpc.model = getSectionMatches(rpc.match);
        
        return rpc;
    }*/
    
    public AutocompleteRPC getProjectMatches(AutocompleteRPC rpc) throws Exception {
        ProjectRemote remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        rpc.model = (ArrayList)remote.autoCompleteLookupByName(rpc.match+"%", 10);
        return rpc;
    }
    
    public AutocompleteRPC getOrganizationMatches(AutocompleteRPC rpc) throws Exception {
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        rpc.model = (ArrayList)remote.autoCompleteLookupByName(rpc.match+"%", 10);
        
        return rpc;
    }
    
    public AutocompleteRPC getTestMethodMatches(AutocompleteRPC rpc) throws Exception {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        rpc.model = (ArrayList)remote.getTestAutoCompleteByName(rpc.match+"%", 10);
        
        return rpc;
    }
    
    public AutocompleteRPC getSectionMatches(AutocompleteRPC rpc) throws Exception {
        SectionRemote remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        rpc.model = (ArrayList)remote.getAutoCompleteSectionByName(rpc.match+"%", 10);
        
        return rpc;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    private void loadSampleItemsForm(SampleItemAndAnalysisForm form, SampleItemManager itemsManager) {
        int i, j;
        AnalysisManager am;
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
                    analysisSubForm.startedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getStartedDate().getDate()));
                
                if (aDO.getCompletedDate() != null && aDO.getCompletedDate().getDate() != null)
                    analysisSubForm.completedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getCompletedDate().getDate()));
                
                if (aDO.getReleasedDate() != null && aDO.getReleasedDate().getDate() != null)
                    analysisSubForm.releasedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getReleasedDate().getDate()));
                
                if (aDO.getPrintedDate() != null && aDO.getPrintedDate().getDate() != null)
                    analysisSubForm.printedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, aDO.getPrintedDate().getDate()));
                
                treeModelItem.setData(analysisSubForm);
                
                treeModelItem.key = aDO.getId();
                treeModelItem.cells[0] = new StringObject(aDO.test.getName() + " : " + aDO.test.getMethodName());
                treeModelItem.cells[1] = new DropDownField<Integer>(new TableDataRow<Integer>(aDO.getStatusId()));
                
                row.addItem(treeModelItem);
            }
        }
    }*/
        
    /*
    private TableDataModel<TableDataRow<Integer>> buildSampleOrganizationTable(SampleOrganizationManager orgManager){
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
    }*/

 
    
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