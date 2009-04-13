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
package org.openelis.modules.label.server;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.LabelDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.modules.label.client.LabelForm;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.PrinterTypeCacheHandler;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LabelService implements AppScreenFormServiceInt<LabelForm,Query<TableDataRow<Integer>>> {   
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9; 
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    private static final LabelMetaMap Meta = new LabelMetaMap();  
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List labels = new ArrayList();
        /*if(qList == null){           
            
            qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("LabelQuery");
    
            if(qList == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));
                 
             try{
                 
                 LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
                 labels = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
            
         } else{*/
             LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
             
             //HashMap<String,AbstractField> fields = qList.getFieldMap();             
              
             try{
                     labels = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
             }catch(LastPageException e) {
                 throw new LastPageException(openElisConstants.getString("lastPageException"));
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }
    
//           need to save the rpc used to the encache
           // SessionManager.getSession().setAttribute("LabelQuery", qList); 
        //}
        
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
   
        while(i < labels.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)labels.get(i);
            //qaEvent id
            Integer idResult = resultDO.getId();
            //qaEvent name
            String nameResult = resultDO.getName();             
      
            query.results.add(new TableDataRow<Integer>(idResult,new StringObject(nameResult)));
            i++;
         }         
               
        
        return query;
    }

    public LabelForm commitAdd(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpc);
        Integer labelId = null;
        List<Exception> exceptionList = remote.validateForAdd(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc);   
            return rpc;
        } 
                         
        try{
            labelId = (Integer)remote.updateLabel(labelDO);        
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc);
            
            return rpc;
        }
        
        labelDO.setId(labelId);
        
         setFieldsInRPC(rpc, labelDO);
        return rpc;
    }

    public LabelForm commitUpdate(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpc);
        
        List<Exception> exceptionList = remote.validateForUpdate(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc);              
            return rpc;
        } 
                         
        try{
            remote.updateLabel(labelDO);        
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc);
            
            return rpc;
        }
        
         setFieldsInRPC(rpc, labelDO);
        return rpc;
    }

    public LabelForm commitDelete(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        List<Exception> exceptionList = remote.validateForDelete(rpc.entityKey);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc);              
            return rpc;
        }
        try{
            remote.deleteLabel(rpc.entityKey);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc);
            
            return rpc;
        }
        setFieldsInRPC(rpc, new LabelDO());
        return rpc;
    }

    public LabelForm abort(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndUnlock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc, labelDO);   
         return rpc;
    }

    public LabelForm fetch(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;

        LabelDO labelDO = remote.getLabel(labelId);
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, labelDO);
                  
        return rpc;
    }

    public LabelForm fetchForUpdate(LabelForm rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndLock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc, labelDO);   
         return rpc;
    }

    public LabelForm getScreen(LabelForm rpc) throws RPCException{
     rpc.xml  = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl");    
        
     TableDataModel<TableDataRow<Integer>> scriptletDropDownField = (TableDataModel<TableDataRow<Integer>>)CachingManager.getElement("InitialData", "scriptletDropDown");
        
     rpc.printerType = PrinterTypeCacheHandler.getPrinterTypes();
     SessionManager.getSession().setAttribute("printerTypesVersion",PrinterTypeCacheHandler.version);
        
     if(scriptletDropDownField ==null)
         rpc.scriptlet = getInitialModel("scriptlet");

     return rpc;
    }
    
    public void checkModels(LabelForm rpc) {
        int printerTypes = (Integer)SessionManager.getSession().getAttribute("printerTypesVersion");
        
        if(printerTypes != PrinterTypeCacheHandler.version) {
            rpc.printerType = PrinterTypeCacheHandler.getPrinterTypes();
            SessionManager.getSession().setAttribute("printerTypesVersion",PrinterTypeCacheHandler.version);
        } 
    }
    
    public TableDataModel<TableDataRow<Integer>> getInitialModel(String cat) {
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = new ArrayList(); 
                
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
               
        LabelRemote labelRemote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        entries = labelRemote.getScriptlets();
                                            
        model.add(new TableDataRow<Integer>(0,new StringObject(" ")));
                
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            IdNameDO resultDO = (IdNameDO)iter.next();
            
            model.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));            
         }
           
        return model;
    }
    
    
    private void setFieldsInRPC(LabelForm form, LabelDO qaeDO){
        form.id.setValue(qaeDO.getId());
        form.name.setValue(qaeDO.getName());
        form.description.setValue(qaeDO.getDescription());
        form.printerTypeId.setValue(new TableDataRow<Integer>(qaeDO.getPrinterType()));     
        form.scriptletId.setValue(new TableDataRow<Integer>(qaeDO.getScriptlet()));        
    }
    
    private LabelDO getLabelDOFromRPC(LabelForm form){
        LabelDO labelDO = new LabelDO();
        
        labelDO.setId(form.id.getValue());
        labelDO.setName(form.name.getValue());
        labelDO.setDescription(form.description.getValue());
              
        labelDO.setPrinterType((Integer)(form.printerTypeId.getSelectedKey()));   
        labelDO.setScriptlet((Integer)(form.scriptletId.getSelectedKey()));        
     
       return labelDO;
    }

	private void setRpcErrors(List exceptionList, LabelForm form){
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
