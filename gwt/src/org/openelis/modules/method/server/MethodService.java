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
package org.openelis.modules.method.server;

import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.IntegerField;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.modules.method.client.MethodForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.MethodRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class MethodService implements AppScreenFormServiceInt<MethodForm, Query<TableDataRow<Integer>>> {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 9;   
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        List methodNames;        
            MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
            
            try{    
                methodNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new Exception(e.getMessage());
            }
    
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while(i < methodNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)methodNames.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return query;
    }
    
    public MethodForm abort(MethodForm rpc) throws Exception {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.entityKey;
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndUnlock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        } 
        setFieldsInRPC(rpc, methodDO);
        return rpc;
    }

    public MethodForm commitAdd(MethodForm rpc) throws Exception {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpc);

        Integer methodId =null;
        try{
            methodId = remote.updateMethod(methodDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
        methodDO.setId(methodId);
        setFieldsInRPC(rpc, methodDO);
        return rpc;
    }

    public MethodForm commitDelete(MethodForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    public MethodForm commitUpdate(MethodForm rpc) throws Exception {       
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpc);
        try{
            remote.updateMethod(methodDO);
        }catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
        setFieldsInRPC(rpc, methodDO);        
        return rpc;
    }

    public MethodForm fetch(MethodForm rpc) throws Exception {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.entityKey;
        MethodDO methodDO = remote.getMethod(methodId);
        setFieldsInRPC(rpc, methodDO);
        return rpc;
    }

    public MethodForm fetchForUpdate(MethodForm rpc) throws Exception {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.entityKey;
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndLock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        } 
        setFieldsInRPC(rpc, methodDO);
        return rpc;
    }

    public String getXML() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws Exception {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl"));
                        
        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    public MethodForm getScreen(MethodForm rpc) throws Exception{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl");
        return rpc;
    }

    private void setFieldsInRPC(MethodForm form, MethodDO methodDO) {
        form.id.setValue(methodDO.getId());
        form.name.setValue(methodDO.getName());
        form.description.setValue(methodDO.getDescription());
        form.reportingDescription.setValue(methodDO.getReportingDescription());
        form.isActive.setValue(methodDO.getIsActive());        
        form.activeBegin.setValue(Datetime.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveBegin()
                                                                     .getDate()));
        form.activeEnd.setValue(Datetime.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveEnd()
                                                                     .getDate()));        
    }
    
    private MethodDO getMethodDOFromRPC(MethodForm form){
      IntegerField methodId = form.id;
      MethodDO methodDO = new MethodDO();
      methodDO.setId(methodId.getValue());
      methodDO.setName(form.name.getValue());
      Datetime activeBegin = (Datetime)form.activeBegin.getValue();
      if (activeBegin != null)
          methodDO.setActiveBegin(activeBegin.getDate());

      Datetime activeEnd = (Datetime)form.activeEnd.getValue();
      if (activeEnd != null)
          methodDO.setActiveEnd(activeEnd.getDate());

      methodDO.setDescription(form.description.getValue());
      methodDO.setIsActive(form.isActive.getValue());
      methodDO.setReportingDescription(form.reportingDescription.getValue());
      return methodDO;
    }
    
    private void setRpcErrors(List exceptionList, MethodForm form) {
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
      for (int i = 0; i < exceptionList.size(); i++) { 
        if (exceptionList.get(i) instanceof FieldErrorException) {
            String nameWithRPC = ((FieldErrorException)exceptionList.get(i)).getFieldName();            
            map.get(nameWithRPC)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
        }
        // if the error is on the entire form
        else if (exceptionList.get(i) instanceof FormErrorException)
            form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
       }
      form.status = Form.Status.invalid;
    }
}
