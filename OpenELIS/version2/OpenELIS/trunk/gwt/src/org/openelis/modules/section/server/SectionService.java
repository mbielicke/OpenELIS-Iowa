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
package org.openelis.modules.section.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.project.client.ProjectForm;
import org.openelis.modules.section.client.SectionForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class SectionService implements
                           AppScreenFormServiceInt<SectionForm, Query<TableDataRow<Integer>>>,
                           AutoCompleteServiceInt {

    private static final int leftTableRowsPerPage = 9;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List<IdNameDO> sectionNames;
        IdNameDO resultDO;
        StringObject name;
        SectionRemote remote;        
        int i;
        
        try {
            remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
            sectionNames = remote.query(query.fields,query.page * leftTableRowsPerPage,
                                        leftTableRowsPerPage);
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        // fill the model with the query results
        i = 0;
        if (query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while (i < sectionNames.size() && i < leftTableRowsPerPage) {
            resultDO = (IdNameDO)sectionNames.get(i);
            name = new StringObject(resultDO.getName());
            query.results.add(new TableDataRow<Integer>(resultDO.getId(), name));
            i++;
        }

        return query; 
    }
    
    public SectionForm abort(SectionForm rpc) throws RPCException {                
        SectionRemote remote;
        SectionDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        try{
            sectionDO = remote.getSectionAndUnlock(rpc.entityKey,
                                                 SessionManager.getSession().getId());
            setFieldsInRPC(rpc, sectionDO);            
        } catch(Exception ex) {
            throw new RPCException(ex.getMessage());
        }
                
        return rpc;
    }

    public SectionForm commitAdd(SectionForm rpc) throws RPCException {
        SectionRemote remote;
        SectionDO sectionDO;
        Integer sectId;
        
        remote  = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        sectionDO = getSectionDOFromRPC(rpc);
        try {
            sectId = remote.updateSection(sectionDO);
            sectionDO = remote.getSection(sectId);
            setFieldsInRPC(rpc,sectionDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());            
        }
        return rpc;
    }

    public SectionForm commitUpdate(SectionForm rpc) throws RPCException {
        SectionRemote remote;
        SectionDO sectionDO;
        
        remote  = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        sectionDO = getSectionDOFromRPC(rpc);
        try {
            remote.updateSection(sectionDO);
            sectionDO = remote.getSection(rpc.entityKey);
            setFieldsInRPC(rpc,sectionDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());            
        }
        return rpc;
    }
    
    public SectionForm commitDelete(SectionForm rpc) throws RPCException {       
        return null;
    }

    public SectionForm fetch(SectionForm rpc) throws RPCException {
        SectionRemote remote;
        SectionDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        sectionDO = remote.getSection(rpc.entityKey);
        setFieldsInRPC(rpc, sectionDO);
        
        return rpc;
    }

    public SectionForm fetchForUpdate(SectionForm rpc) throws RPCException {
        SectionRemote remote;
        SectionDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        try{
            sectionDO = remote.getSectionAndLock(rpc.entityKey,
                                                 SessionManager.getSession().getId());
            setFieldsInRPC(rpc, sectionDO);
        } catch(Exception ex) {
            throw new RPCException(ex.getMessage());
        }
                
        return rpc;
    }

    public SectionForm getScreen(SectionForm rpc) throws RPCException {        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/section.xsl");
        return rpc;
    }
    
    public TableDataModel getMatches(String cat,
                                     TableDataModel model,
                                     String match,
                                     HashMap<String, FieldType> params) throws RPCException {
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList;
    
        try{
            int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
            //lookup by id...should only bring back 1 result
            autoCompleteList = remote.autoCompleteLookupById(id);
            
        }catch(NumberFormatException e){
            //it isnt an id
            //lookup by name
            autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            OrganizationAutoDO resultDO = (OrganizationAutoDO) autoCompleteList.get(i);
            //org id
            Integer orgId = resultDO.getId();
            //org name
            String name = resultDO.getName();
            //org street address
            String address = resultDO.getAddress();
            //org city
            String city = resultDO.getCity();
            //org state
            String state = resultDO.getState();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(orgId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(address),
                                                                                    new StringObject(city),
                                                                                    new StringObject(state)
                                                                   }
                                         );
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;   
    }
    
    private void setFieldsInRPC(SectionForm rpc, SectionDO sectionDO) {
        TableDataModel<TableDataRow<Integer>> model;
        
        rpc.id.setValue(sectionDO.getId());
        rpc.description.setValue(sectionDO.getDescription());
        rpc.isExternal.setValue(sectionDO.getIsExternal());
        rpc.name.setValue(sectionDO.getName());
        rpc.parentSectionId.setValue(new TableDataRow<Integer>(sectionDO.getParentSectionId()));
        
        model = new TableDataModel<TableDataRow<Integer>>();
        if(sectionDO.getOrganizationId() != null) {
            model.add(new TableDataRow<Integer>(sectionDO.getOrganizationId(),
                            new StringObject(sectionDO.getOrganizationName())));
        }
        rpc.organizationId.setModel(model);
        rpc.organizationId.setValue(model.get(0));
    }

    private void loadDropDown(List<IdNameDO> list, TableDataModel model) {
        TableDataRow<Integer> blankset = new TableDataRow<Integer>(null,
                                                                   new StringObject(""));
        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            TableDataRow<Integer> set = new TableDataRow<Integer>(methodDO.getId(),
                                                                  new StringObject(methodDO.getName()));
            model.add(set);
        }
    }
    
    private SectionDO getSectionDOFromRPC(SectionForm rpc) {
        SectionDO sectionDO;
        
        sectionDO = new SectionDO();
        sectionDO.setDescription(rpc.description.getValue());
        sectionDO.setName(rpc.name.getValue());
        sectionDO.setId(rpc.id.getValue());
        sectionDO.setIsExternal(rpc.isExternal.getValue());
        sectionDO.setOrganizationId((Integer)rpc.organizationId.getSelectedKey());
        sectionDO.setParentSectionId((Integer)rpc.parentSectionId.getSelectedKey());
     
        return sectionDO; 
     
    }
    
    
    private void setRpcErrors(ArrayList<RPCException> exceptionList, SectionForm form) {
        HashMap<String,AbstractField> map;
        
        map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
                  //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof FieldErrorException) {
                 map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                //if the error is on the entire form
            } else if(exceptionList.get(i) instanceof FormErrorException) {
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }
        }   
        form.status = Form.Status.invalid;
    }
}
