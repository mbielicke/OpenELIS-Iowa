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

package org.openelis.modules.systemvariable.server;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.systemvariable.client.SystemVariableForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemVariableService implements AppScreenFormServiceInt<SystemVariableForm,Query<TableDataRow<Integer>>> {

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9;  
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
     public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List sysVars = new ArrayList();
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote"); 
                          
        try{
            sysVars = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        } 
                  
     int i=0;
     
     if(query.results == null)
         query.results = new TableDataModel<TableDataRow<Integer>>();
     else
         query.results.clear();

     while(i < sysVars.size() && i < leftTableRowsPerPage) {
         IdNameDO resultDO = (IdNameDO)sysVars.get(i);
         query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
         i++;
      }  
     
         return query;
    }

    public SystemVariableForm commitAdd(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote;
        SystemVariableDO sysVarDO;
        Integer svId;

        remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        sysVarDO = getSystemVariableDOFromRPC(rpc);
        try{ 
            svId = remote.updateSystemVariable(sysVarDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {            
            throw new RPCException(e.getMessage());            
        } 
        
        sysVarDO.setId(svId);
        
        setFieldsInRPC(rpc, sysVarDO);
        return rpc;
    }

    public SystemVariableForm commitUpdate(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote;
        SystemVariableDO sysVarDO;
        
        remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        sysVarDO = getSystemVariableDOFromRPC(rpc);
        try{ 
            remote.updateSystemVariable(sysVarDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {            
            throw new RPCException(e.getMessage());            
        }
        
        setFieldsInRPC(rpc, sysVarDO);
        
        return rpc;
    }


    public SystemVariableForm commitDelete(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote;
        
        remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        try{
            remote.deleteSystemVariable(rpc.entityKey);
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        
        setFieldsInRPC(rpc, new SystemVariableDO());
        return rpc;
    }


    public SystemVariableForm abort(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.entityKey;
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndUnlock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        
        setFieldsInRPC(rpc, svDO);
        return rpc;
    }


    public SystemVariableForm fetch(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.entityKey;
        
        SystemVariableDO svDO = remote.getSystemVariable(svId);
        setFieldsInRPC(rpc, svDO);
        return rpc;
    }


    public SystemVariableForm fetchForUpdate(SystemVariableForm rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.entityKey;
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndLock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        setFieldsInRPC(rpc, svDO);
        return rpc;
    }


    public SystemVariableForm getScreen(SystemVariableForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/systemVariable.xsl");
        return rpc;
    }

    private void setFieldsInRPC(SystemVariableForm form, SystemVariableDO svDO){
        form.id.setValue(svDO.getId());
        form.name.setValue(svDO.getName());
        form.value.setValue(svDO.getValue());        
    }
    
    
    private SystemVariableDO getSystemVariableDOFromRPC(SystemVariableForm form){
        SystemVariableDO sysVarDO = new SystemVariableDO();       
        sysVarDO.setId(form.id.getValue());
        sysVarDO.setName(form.name.getValue());
        sysVarDO.setValue(form.value.getValue());
        return sysVarDO;
    }


	private void setRpcErrors(List exceptionList, SystemVariableForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
             if(exceptionList.get(i) instanceof FieldErrorException)
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        form.status = Form.Status.invalid;
    }
}