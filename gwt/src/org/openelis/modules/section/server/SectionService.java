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
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.modules.section.client.SectionForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import com.google.gwt.user.client.Window;

public class SectionService implements
                           AppScreenFormServiceInt<SectionForm, Query<TableDataRow<Integer>>>,
                           AutoCompleteServiceInt {

    private static final int leftTableRowsPerPage = 9;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
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
            throw new Exception(e.getMessage());
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
    
    public SectionForm abort(SectionForm rpc) throws Exception {                
        SectionRemote remote;
        SectionViewDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        try{
            sectionDO = remote.getSectionAndUnlock(rpc.entityKey,
                                                 SessionManager.getSession().getId());
            setFieldsInRPC(rpc, sectionDO);            
        } catch(Exception ex) {
            throw new Exception(ex.getMessage());
        }
                
        return rpc;
    }

    public SectionForm commitAdd(SectionForm rpc) throws Exception {
        SectionRemote remote;
        SectionViewDO sectionDO;
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
            throw new Exception(e.getMessage());            
        }
        return rpc;
    }

    public SectionForm commitUpdate(SectionForm rpc) throws Exception {
        SectionRemote remote;
        SectionViewDO sectionDO;
        
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
            throw new Exception(e.getMessage());            
        }
        return rpc;
    }
    
    public SectionForm commitDelete(SectionForm rpc) throws Exception {       
        return null;
    }

    public SectionForm fetch(SectionForm rpc) throws Exception {
        SectionRemote remote;
        SectionViewDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        sectionDO = remote.getSection(rpc.entityKey);
        setFieldsInRPC(rpc, sectionDO);
        
        return rpc;
    }

    public SectionForm fetchForUpdate(SectionForm rpc) throws Exception {
        SectionRemote remote;
        SectionViewDO sectionDO;
        
        remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        try{
            sectionDO = remote.getSectionAndLock(rpc.entityKey,
                                                 SessionManager.getSession().getId());
            setFieldsInRPC(rpc, sectionDO);
        } catch(Exception ex) {
            throw new Exception(ex.getMessage());
        }
                
        return rpc;
    }

    public SectionForm getScreen(SectionForm rpc) throws Exception {        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/section.xsl");
        return rpc;
    }
    
    public TableDataModel getMatches(String cat,
                                     TableDataModel model,
                                     String match,
                                     HashMap<String, FieldType> params) throws Exception {
        OrganizationRemote oremote;
        TableDataModel<TableDataRow<Integer>> dataModel;
        List autoCompleteList = null;
        int i;
        OrganizationDO resultDO;
        Integer orgId, id;        
        String name,address,city,state;
        TableDataRow<Integer> data;
        SectionRemote sremote;
        IdNameDO sectDO;
 
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        
        if("organization".equals(cat)) {
            oremote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

            try{
                //this will throw an exception if it isnt an id
                //lookup by id...should only bring back 1 result
                id = Integer.parseInt(match);
            }catch(NumberFormatException e){
                id = null;
            }
            
            try {
                if (id != null) {
                    autoCompleteList = new ArrayList<OrganizationDO>(1);
                    autoCompleteList.add(oremote.fetchActiveById(id));
                } else {
                    autoCompleteList = oremote.fetchActiveByName(match+"%", 10);
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
            
            for(i=0; i < autoCompleteList.size(); i++){
                resultDO = (OrganizationDO) autoCompleteList.get(i);
                //org id
                orgId = resultDO.getId();
                //org name
                name = resultDO.getName();
                //org street address
                address = resultDO.getAddress().getStreetAddress();
                //org city
                city = resultDO.getAddress().getCity();
                //org state
                state = resultDO.getAddress().getState();
                
                data = new TableDataRow<Integer>(orgId,new FieldType[] {new StringObject(name),
                                                                        new StringObject(address),
                                                                        new StringObject(city),
                                                                        new StringObject(state)}
                );
                
                //add the dataset to the datamodel
                dataModel.add(data);                            
            }       
        } else if("parentSection".equals(cat)) {
            sremote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
            autoCompleteList = sremote.getAutoCompleteSectionByName(match+"%", 10);
            
            for (i = 0; i < autoCompleteList.size(); i++) {

                sectDO = (IdNameDO)autoCompleteList.get(i);
                id = sectDO.getId();
                name = sectDO.getName();

                data = new TableDataRow<Integer>(id, new StringObject(name));
                dataModel.add(data);
            }
        }
        
        return dataModel;   
    }
    
    private void setFieldsInRPC(SectionForm rpc, SectionViewDO sectionDO) {
        TableDataModel<TableDataRow<Integer>> model;
        
        rpc.id.setValue(sectionDO.getId());
        rpc.description.setValue(sectionDO.getDescription());
        rpc.isExternal.setValue(sectionDO.getIsExternal());
        rpc.name.setValue(sectionDO.getName());
        
        model = new TableDataModel<TableDataRow<Integer>>();
        if(sectionDO.getParentSectionId() != null) {
            model.add(new TableDataRow<Integer>(sectionDO.getParentSectionId(),
                            new StringObject(sectionDO.getParentSectionName())));
        }
        rpc.parentSectionId.setModel(model);
        rpc.parentSectionId.setValue(model.get(0));
        
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
    
    private SectionViewDO getSectionDOFromRPC(SectionForm rpc) {
        SectionViewDO sectionDO;
        
        sectionDO = new SectionViewDO();
        sectionDO.setDescription(rpc.description.getValue());
        sectionDO.setName(rpc.name.getValue());
        sectionDO.setId(rpc.id.getValue());
        sectionDO.setIsExternal(rpc.isExternal.getValue());
        sectionDO.setOrganizationId((Integer)rpc.organizationId.getSelectedKey());
        sectionDO.setParentSectionId((Integer)rpc.parentSectionId.getSelectedKey());
     
        return sectionDO; 
     
    }
    
    
    private void setRpcErrors(ArrayList<Exception> exceptionList, SectionForm form) {
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
