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
package org.openelis.modules.project.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.project.client.ProjectForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ProjectRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class ProjectService implements AppScreenFormServiceInt<ProjectForm, Query<TableDataRow<Integer>>>,
                                       AutoCompleteServiceInt{

    private static final int leftTableRowsPerPage = 20;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List<IdNameDO> projectNames;
        ProjectRemote remote;
        IdNameDO resultDO;
        StringObject name;
        int i;
        try {
            remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
            projectNames = remote.query(query.fields,
                                     query.page * leftTableRowsPerPage,
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
        while (i < projectNames.size() && i < leftTableRowsPerPage) {
            resultDO = (IdNameDO)projectNames.get(i);
            name = new StringObject(resultDO.getName());
            query.results.add(new TableDataRow<Integer>(resultDO.getId(), name));
            i++;
        }

        return query;        
    }
    
    public ProjectForm commitAdd(ProjectForm rpc) throws RPCException {
        ProjectRemote remote;
        ProjectDO projectDO;
        Integer projId;
        List<ProjectParameterDO> list;
        
        remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        projectDO = getProjectDOFromRPC(rpc);
        list = getParameterListFromRPC(rpc);
        try {
            projId = remote.updateProject(projectDO, list); 
            projectDO = remote.getProject(projId);
            setFieldsInRPC(rpc, projectDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());            
        }
        return rpc;
    }   

    public ProjectForm commitUpdate(ProjectForm rpc) throws RPCException {
        ProjectRemote remote;
        ProjectDO projectDO;
        List<ProjectParameterDO> list;
        
        remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        projectDO = getProjectDOFromRPC(rpc);
        list = getParameterListFromRPC(rpc);
        try {
            remote.updateProject(projectDO, list);    
            projectDO = remote.getProject(rpc.entityKey);
            setFieldsInRPC(rpc, projectDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());            
        }
        return rpc;
    }
    
    public ProjectForm commitDelete(ProjectForm rpc) throws RPCException {        
        return null;
    }
    
    public ProjectForm abort(ProjectForm rpc) throws RPCException {
        ProjectRemote remote;
        ProjectDO projectDO;
        List<ProjectParameterDO> paramDOList;

        remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        try {
            projectDO = remote.getProjectAndUnlock(rpc.entityKey,
                                          SessionManager.getSession().getId());
            paramDOList = remote.getProjectParameters(rpc.entityKey);
            setFieldsInRPC(rpc,projectDO);
            fillParameterTable(paramDOList, rpc);
        } catch(Exception ex) {
            throw new RPCException(ex.getMessage());
        }
        
        return rpc;
    }


    public ProjectForm fetch(ProjectForm rpc) throws RPCException {
        ProjectRemote remote;
        ProjectDO projectDO;
        List<ProjectParameterDO> paramDOList;
                
        remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        projectDO = remote.getProject(rpc.entityKey);
        paramDOList = remote.getProjectParameters(rpc.entityKey);
        setFieldsInRPC(rpc,projectDO);
        fillParameterTable(paramDOList, rpc);
        return rpc;
    }

    public ProjectForm fetchForUpdate(ProjectForm rpc) throws RPCException {
        ProjectRemote remote;
        ProjectDO projectDO;
        List<ProjectParameterDO> paramDOList;
                
        remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        try {
            projectDO = remote.getProjectAndLock(rpc.entityKey,
                                          SessionManager.getSession().getId());
            paramDOList = remote.getProjectParameters(rpc.entityKey);
            setFieldsInRPC(rpc,projectDO);
            fillParameterTable(paramDOList, rpc);
        } catch(Exception ex) {
            throw new RPCException(ex.getMessage());
        }
        
        return rpc;
    }

    public ProjectForm getScreen(ProjectForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/project.xsl");        
        
        return rpc;
    }
    
    public TableDataModel getMatches(String cat,
                                     TableDataModel model,
                                     String match,
                                     HashMap<String, FieldType> params) throws RPCException {
        TableDataModel<TableDataRow<Integer>> dataModel;
        List<SecuritySystemUserDO> list;
        List<IdNameDO> entries;
        ProjectRemote remote;
        ScriptletRemote sremote;
        
        dataModel = null;
        if(("owner").equals(cat)) {
            remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
            list = remote.ownerAutocompleteByName(match.trim() + "%", 10);
            dataModel = getOwnerAutocompleteModel(list);
        } else if(("scriptlet").equals(cat)) {
            sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
            entries = sremote.getScriptletAutoCompleteByName(match.trim() + "%", 10);
            dataModel = getAutocompleteModel(entries);
        }
        return dataModel;
    }
    
    
    private TableDataModel<TableDataRow<Integer>> getOwnerAutocompleteModel(List<SecuritySystemUserDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel;
        SecuritySystemUserDO element;
        Integer entryId;
        String entryText;
        TableDataRow<Integer> data;
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            element = (SecuritySystemUserDO)iter.next();
            entryId = element.getId();
            entryText = element.getLoginName();
            data = new TableDataRow<Integer>(entryId,new StringObject(entryText));
            dataModel.add(data);
        }
        
        return dataModel;
    }
    
    private TableDataModel<TableDataRow<Integer>> getAutocompleteModel(List<IdNameDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {

            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();
            String entryText = element.getName();

            TableDataRow<Integer> data = new TableDataRow<Integer>(entryId,
                                                                   new StringObject(entryText));
            dataModel.add(data);
        }
        
        return dataModel;
    }
    
    private void setFieldsInRPC(ProjectForm rpc,ProjectDO projectDO) {
        TableDataModel<TableDataRow<Integer>> model;
        Datetime date;
        rpc.id.setValue(projectDO.getId());
        rpc.name.setValue(projectDO.getName());
        rpc.description.setValue(projectDO.getDescription());
        rpc.referenceTo.setValue(projectDO.getReferenceTo());
        rpc.isActive.setValue(projectDO.getIsActive());
      
        date = projectDO.getCompletedDate();
        if(date != null && date.getDate() != null) {
            rpc.completedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,
                                                               date.getDate()));
        }
        
        date = projectDO.getStartedDate();
        if(date != null && date.getDate() != null) {
            rpc.startedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,
                                                             date.getDate()));
        }
        
        model = new TableDataModel();
        if(projectDO.getScriptletId() != null) {
            model.add(new TableDataRow<Integer>(projectDO.getScriptletId(),
                            new StringObject(projectDO.getScriptletName())));
            rpc.scripletId.setValue(model.get(0));
        }
        rpc.scripletId.setModel(model);
     
        model = new TableDataModel<TableDataRow<Integer>>();
        model.add(new TableDataRow<Integer>(projectDO.getOwnerId(),
                            new StringObject(projectDO.getOwnerName())));
        rpc.ownerId.setModel(model);
        rpc.ownerId.setValue(model.get(0));
    }
    
    private ProjectDO getProjectDOFromRPC(ProjectForm rpc) {
        ProjectDO projectDO;
        DatetimeRPC complDate,startDate;
        
        projectDO = new ProjectDO();
        
        projectDO.setId(rpc.id.getValue());
        projectDO.setName(rpc.name.getValue());
        projectDO.setIsActive(rpc.isActive.getValue());
        projectDO.setDescription(rpc.description.getValue());
        projectDO.setOwnerId((Integer)rpc.ownerId.getSelectedKey());
        projectDO.setReferenceTo(rpc.referenceTo.getValue());
        projectDO.setScriptletId((Integer)rpc.scripletId.getSelectedKey());
        
        complDate = rpc.completedDate.getValue();
        startDate = rpc.startedDate.getValue();
        
        if(complDate != null)
            projectDO.setCompletedDate(complDate.getDate());
        if(startDate != null)
            projectDO.setStartedDate(startDate.getDate());
        
        return projectDO;
    }
    
    private void fillParameterTable(List<ProjectParameterDO> paramDOList, ProjectForm rpc) {
        TableDataModel<TableDataRow<Integer>> model;
        TableDataRow<Integer> row;
        ProjectParameterDO paramDO;
        
        model = rpc.parameterTable.getValue();
        
        model.clear();
        for(int i = 0; i < paramDOList.size();i++) {
            paramDO = paramDOList.get(i);
            row = model.createNewSet();
            row.key = paramDO.getId();
            row.cells[0].setValue(paramDO.getParameter());
            ((DropDownField<Integer>)row.cells[1]).setValue(new TableDataRow(paramDO.getOperationId()));
            row.cells[2].setValue(paramDO.getValue());
            model.add(row);
        }
        
    } 
    
    private List<ProjectParameterDO> getParameterListFromRPC(ProjectForm rpc) {
        ProjectParameterDO paramDO;
        TableDataRow<Integer> row;
        TableDataModel<TableDataRow<Integer>> model;
        List<ProjectParameterDO> paramDOList;
        List<TableDataRow<Integer>> deletions;
        int i;
        
        model = rpc.parameterTable.getValue();
        paramDOList = new ArrayList<ProjectParameterDO>();
        for(i = 0; i < model.size(); i++) {
            row = model.get(i);
            paramDO = new ProjectParameterDO();            
            paramDO.setDelete(false);
            paramDO.setId(row.key);
            paramDO.setProjectId(rpc.entityKey);
            paramDO.setParameter((String)row.cells[0].getValue());
            paramDO.setOperationId((Integer)((DropDownField<Integer>)row.cells[1]).getSelectedKey());
            paramDO.setValue((String)row.cells[2].getValue());
            paramDOList.add(paramDO);
        }
        
        deletions = model.getDeletions();
        if(deletions != null) {
            for(i = 0; i < deletions.size(); i++) {
                row = deletions.get(i);
                paramDO = new ProjectParameterDO();
                paramDO.setDelete(true);
                paramDO.setId(row.key);
                paramDOList.add(paramDO);
            }            
           deletions.clear();
        }     
        
        return paramDOList;
    }
    
    private void setRpcErrors(ArrayList<RPCException> exceptionList, ProjectForm form) {
        HashMap<String,AbstractField> map;
        String fieldName;
        int index;
        TableFieldErrorException exc;
        
        map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
                  //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof TableFieldErrorException){       
                exc = (TableFieldErrorException)exceptionList.get(i);
                fieldName = exc.getFieldName();                    
                index =  exc.getRowIndex();                    
                form.parameterTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exc).getMessage()));
                //if the error is on the field
            } else if(exceptionList.get(i) instanceof FieldErrorException) {
                 map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                //if the error is on the entire form
            } else if(exceptionList.get(i) instanceof FormErrorException) {
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }
        }   
        form.status = Form.Status.invalid;
        
    }

}
