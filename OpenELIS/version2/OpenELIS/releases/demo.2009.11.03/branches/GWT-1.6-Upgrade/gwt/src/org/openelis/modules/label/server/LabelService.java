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
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LabelService implements AppScreenFormServiceInt<DefaultRPC,Integer> {   
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9; 
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    private static final LabelMetaMap Meta = new LabelMetaMap();  
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List labels = new ArrayList();
        if(form == null){           
            
            form = (Form)SessionManager.getSession().getAttribute("LabelQuery");
    
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));
                 
             try{
                 
                 LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
                 labels = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
            
         } else{
             LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
             
             HashMap<String,AbstractField> fields = form.getFieldMap();             
              
             try{
                     labels = remote.query(fields,0,leftTableRowsPerPage);
    
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }
    
//           need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("LabelQuery", form); 
        }
        
        int i=0;
        if(model == null)
            model = new DataModel<Integer>();
        else
            model.clear();
   
        while(i < labels.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)labels.get(i);
            //qaEvent id
            Integer idResult = resultDO.getId();
            //qaEvent name
            String nameResult = resultDO.getName();             
      
            model.add(new DataSet<Integer>(idResult,new StringObject(nameResult)));
            i++;
         }         
               
        
        return model;
    }

    public DefaultRPC commitAdd(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpc.form);
        Integer labelId = null;
        List<Exception> exceptionList = remote.validateForAdd(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            return rpc;
        } 
                         
        try{
            labelId = (Integer)remote.updateLabel(labelDO);        
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }
        
        labelDO.setId(labelId);
        
         setFieldsInRPC(rpc.form, labelDO);
        return rpc;
    }

    public DefaultRPC commitUpdate(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpc.form);
        
        List<Exception> exceptionList = remote.validateForUpdate(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);              
            return rpc;
        } 
                         
        try{
            remote.updateLabel(labelDO);        
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }
        
         setFieldsInRPC(rpc.form, labelDO);
        return rpc;
    }

    public DefaultRPC commitDelete(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        List<Exception> exceptionList = remote.validateForDelete(rpc.key);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);              
            return rpc;
        }
        try{
            remote.deleteLabel(rpc.key);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }
        setFieldsInRPC(rpc.form, new LabelDO());
        return rpc;
    }

    public DefaultRPC abort(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.key;
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndUnlock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc.form, labelDO);   
         return rpc;
    }

    public DefaultRPC fetch(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.key;

        LabelDO labelDO = remote.getLabel(labelId);
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, labelDO);
                  
        return rpc;
    }

    public DefaultRPC fetchForUpdate(DefaultRPC rpc) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.key;
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndLock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc.form, labelDO);   
         return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl"); 
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl"));    
        
        DataModel printertypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "printertypeDropDown");
        DataModel scriptletDropDownField = (DataModel)CachingManager.getElement("InitialData", "scriptletDropDown");
        
        if(printertypeDropDownField ==null)
            printertypeDropDownField = getInitialModel("printerType");
        
        if(scriptletDropDownField ==null)
            scriptletDropDownField = getInitialModel("scriptlet");
        
        HashMap<String,FieldType> map = new HashMap<String,FieldType>();
        map.put("xml", xml);
        map.put("printer",printertypeDropDownField);
        map.put("scriptlet", scriptletDropDownField);
        
        return map;
    }
    
    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DefaultRPC getScreen(DefaultRPC rpc) {
        return rpc;
    }
    
    public DataModel getInitialModel(String cat) {
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = new ArrayList(); 
        Integer id = null;
                
        DataModel<Integer> model = new DataModel<Integer>();
        
        if(cat.equals("printerType")){
            id = catRemote.getCategoryId("printer_type"); 
            if(id != null)
                entries = catRemote.getDropdownValues(id);
            
        }else if(cat.equals("scriptlet")){
            LabelRemote labelRemote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
            entries = labelRemote.getScriptlets();
        }                
            
        if(entries.size() > 0){
            DataSet<Integer> blankset = new DataSet<Integer>();           
    
            model.add(new DataSet<Integer>(0,new StringObject(" ")));
        }
        
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            IdNameDO resultDO = (IdNameDO)iter.next();

            DataSet<Integer> set = new DataSet<Integer>();
            
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));            
         }
           
        return model;
    }
    
    
    private void setFieldsInRPC(Form form, LabelDO qaeDO){
        form.setFieldValue(Meta.getId(), qaeDO.getId());
        form.setFieldValue(Meta.getName(),qaeDO.getName());
        form.setFieldValue(Meta.getDescription(),qaeDO.getDescription());
        ((DropDownField<Integer>)(Field)form.getField(Meta.getPrinterTypeId())).setValue(new DataSet<Integer>(qaeDO.getPrinterType()));     
        ((DropDownField<Integer>)(Field)form.getField(Meta.getScriptletId())).setValue(new DataSet<Integer>(qaeDO.getScriptlet()));        
    }
    
    private LabelDO getLabelDOFromRPC(Form form){
        LabelDO labelDO = new LabelDO();
        NumberField labelIdField = (NumberField) form.getField(Meta.getId());
        
        labelDO.setId(labelIdField.getIntegerValue());
        labelDO.setName(((String)form.getFieldValue(Meta.getName())));
        labelDO.setDescription(((String)form.getFieldValue(Meta.getDescription())));
              
        labelDO.setPrinterType((Integer)((DropDownField)form.getField(Meta.getPrinterTypeId())).getSelectedKey());   
        labelDO.setScriptlet((Integer)((DropDownField)form.getField(Meta.getScriptletId())).getSelectedKey());        
     
       return labelDO;
    }

	private void setRpcErrors(List exceptionList, Form form){
            //we need to get the keys and look them up in the resource bundle for internationalization
            for (int i=0; i<exceptionList.size();i++) {
                //if the error is inside the org contacts table
                 if(exceptionList.get(i) instanceof FieldErrorException)
                    form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                //if the error is on the entire form
                else if(exceptionList.get(i) instanceof FormErrorException)
                    form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }   
            form.status = Form.Status.invalid;
        }
}
