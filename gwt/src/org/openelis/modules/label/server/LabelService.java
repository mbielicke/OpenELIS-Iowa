package org.openelis.modules.label.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.LabelDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.meta.LabelMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class LabelService implements AppScreenFormServiceInt {   
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9; 
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List labels = new ArrayList();
        if(rpcSend == null){           
            
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("LabelQuery");
    
            if(rpc == null)
                throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
                 
             try{
                 
                 LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
                 labels = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
            
         } else{
             LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
             
             HashMap<String,AbstractField> fields = rpcSend.getFieldMap();             
              
             try{
                     labels = remote.query(fields,0,leftTableRowsPerPage);
    
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }
    
//           need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("LabelQuery", rpcSend); 
        }
        
        int i=0;
        model.clear();
   
        while(i < labels.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)labels.get(i);
            //qaEvent id
            Integer idResult = resultDO.getId();
            //qaEvent name
            String nameResult = resultDO.getName();             
   
            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject(NumberObject.INTEGER);
   
            StringObject svname = new StringObject();
   
            id.setValue(idResult);
   
            svname.setValue(nameResult);                   
            row.setKey(id);          
   
            row.addObject(svname);
   
            model.add(row);
            i++;
         }         
               
        
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpcSend);
        Integer labelId = null;
        List<Exception> exceptionList = remote.validateForAdd(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);   
            return rpcSend;
        } 
                         
        try{
            labelId = (Integer)remote.updateLabel(labelDO);        
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        }
        
        labelDO.setId(labelId);
        
         setFieldsInRPC(rpcReturn, labelDO);
        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpcSend);
        
        List<Exception> exceptionList = remote.validateForUpdate(labelDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);              
            return rpcSend;
        } 
                         
        try{
            remote.updateLabel(labelDO);        
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        }
        
         setFieldsInRPC(rpcReturn, labelDO);
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        List<Exception> exceptionList = remote.validateForDelete((Integer)key.getKey().getValue());
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcReturn);              
            return rpcReturn;
        }
        try{
            remote.deleteLabel((Integer)key.getKey().getValue());
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcReturn);
            
            return rpcReturn;
        }
        setFieldsInRPC(rpcReturn, new LabelDO());
        return rpcReturn;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = (Integer)key.getKey().getValue();
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndUnlock(labelId);
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpcReturn, labelDO);   
         return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = (Integer)key.getKey().getValue();

        LabelDO labelDO = remote.getLabel(labelId);
        
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, labelDO);
                  
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = (Integer)key.getKey().getValue();
        LabelDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndLock(labelId);
           } catch(Exception ex){
               throw new RPCException(ex.getMessage());
           }  
           
         setFieldsInRPC(rpcReturn, labelDO);   
         return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl"); 
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl"));    
        
        DataModel printertypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "printertypeDropDown");
        DataModel scriptletDropDownField = (DataModel)CachingManager.getElement("InitialData", "scriptletDropDown");
        
        if(printertypeDropDownField ==null)
            printertypeDropDownField = getInitialModel("printerType");
        
        if(scriptletDropDownField ==null)
            scriptletDropDownField = getInitialModel("scriptlet");
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("printer",printertypeDropDownField);
        map.put("scriptlet", scriptletDropDownField);
        
        return map;
    }
    
    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DataModel getInitialModel(String cat) {
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        int id = -1;
                
        DataModel model = new DataModel();
        
        if(cat.equals("printerType")){
            id = catRemote.getCategoryId("printer_type"); 
            entries = catRemote.getDropdownValues(id);
        }else if(cat.equals("scriptlet")){
            LabelRemote labelRemote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
            entries = labelRemote.getScriptlets();
        }                
            
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
             
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject(NumberObject.INTEGER);
            blankNumberId.setValue(new Integer(-1));
            

            blankset.setKey(blankNumberId);
    
            model.add(blankset);        
   
        
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            IdNameDO resultDO = (IdNameDO)iter.next();

            DataSet set = new DataSet();

            //id
            Integer dropdownId = resultDO.getId();
            //entry
            String dropDownText = resultDO.getName();
         
            
            StringObject textObject = new StringObject();
             textObject.setValue(dropDownText);
             
            set.addObject(textObject);
            
            NumberObject numberId = new NumberObject(NumberObject.INTEGER);
            numberId.setValue(dropdownId);

            set.setKey(numberId);           
            
            model.add(set);            
         }
           
        return model;
    }
    
    
    private void setFieldsInRPC(FormRPC rpcReturn, LabelDO qaeDO){
        rpcReturn.setFieldValue(LabelMeta.ID, qaeDO.getId());
        rpcReturn.setFieldValue(LabelMeta.NAME,qaeDO.getName());
        rpcReturn.setFieldValue(LabelMeta.DESCRIPTION,qaeDO.getDescription());
        rpcReturn.setFieldValue(LabelMeta.PRINTER_TYPE,qaeDO.getPrinterType());     
        rpcReturn.setFieldValue(LabelMeta.SCRIPTLET,qaeDO.getScriptlet());        
    }
    
    private LabelDO getLabelDOFromRPC(FormRPC rpcSend){
        LabelDO labelDO = new LabelDO();
        NumberField labelIdField = (NumberField) rpcSend.getField(LabelMeta.ID);
        
        labelDO.setId((Integer)labelIdField.getValue());
        labelDO.setName(((String)rpcSend.getFieldValue(LabelMeta.NAME)));
        labelDO.setDescription(((String)rpcSend.getFieldValue(LabelMeta.DESCRIPTION)));
              
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(LabelMeta.PRINTER_TYPE)))
            labelDO.setPrinterType((Integer)rpcSend.getFieldValue(LabelMeta.PRINTER_TYPE));   
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(LabelMeta.SCRIPTLET)))
            labelDO.setScriptlet((Integer)rpcSend.getFieldValue(LabelMeta.SCRIPTLET));        
     
       return labelDO;
    }

	private void setRpcErrors(List exceptionList, FormRPC rpcSend){
            //we need to get the keys and look them up in the resource bundle for internationalization
            for (int i=0; i<exceptionList.size();i++) {
                //if the error is inside the org contacts table
                 if(exceptionList.get(i) instanceof FieldErrorException)
                    rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                //if the error is on the entire form
                else if(exceptionList.get(i) instanceof FormErrorException)
                    rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }   
            rpcSend.status = IForm.Status.invalid;
        }
}
