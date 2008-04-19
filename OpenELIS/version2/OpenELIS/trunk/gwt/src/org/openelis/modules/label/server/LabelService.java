package org.openelis.modules.label.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.LabelDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
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
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class LabelService implements AppScreenFormServiceInt {

    /**
     * 
     */    
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9; 
    
    private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
                                                                new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                                                                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
    
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

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpcSend);
        Integer labelId = null;
         try{
             labelId = remote.updateLabel(labelDO);             
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }
        
         labelDO = remote.getLabel(labelId);
         setFieldsInRPC(rpcReturn, labelDO);
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        try{
            remote.deleteLabel((Integer)key.getKey().getValue());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        setFieldsInRPC(rpcReturn, new LabelDO());
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        if(rpcSend == null){           
            
            
            FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Label");

            if(rpc == null)
                throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

             List labels = null;
                 
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
         
         int i=0;
         model.clear();

         while(i < labels.size() && i < leftTableRowsPerPage) {
             Object[] result = (Object[])labels.get(i);
             //qaEvent id
             Integer idResult = (Integer)result[0];
             //qaEvent name
             String nameResult = (String)result[1];             

             DataSet row = new DataSet();
             
             NumberObject id = new NumberObject();

             StringObject svname = new StringObject();
             
             id.setType("integer");
  
             id.setValue(idResult);

              svname.setValue(nameResult);                   
             row.setKey(id);          

             row.addObject(svname);

             model.add(row);
             i++;
          }         
                 
         return model;   
         } else{
             LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
             
             HashMap<String,AbstractField> fields = rpcSend.getFieldMap();             
              

             List sysVarNames = new ArrayList();
                 try{
                     sysVarNames = remote.query(fields,0,leftTableRowsPerPage);

             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }

             Iterator itraaa = sysVarNames.iterator();
             model=  new DataModel();
             while(itraaa.hasNext()){
                 Object[] result = (Object[])itraaa.next();
                 //qaEvent id
                 Integer idResult = (Integer)result[0];
                 //qaEvent name
                 String nameResult = (String)result[1];
                 

                 DataSet row = new DataSet();
                 
                 NumberObject id = new NumberObject();

                 StringObject svname = new StringObject();
                 
                 id.setType("integer");
                 id.setValue(idResult);
     
                 svname.setValue(nameResult);                       

                     row.setKey(id);          

                 row.addObject(svname);                 
                 model.add(row);

             } 
             if(SessionManager.getSession().getAttribute("systemUserId") == null)
                 SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
             CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Label", rpcSend);          
        }
         return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        LabelDO labelDO = getLabelDOFromRPC(rpcSend);
        Integer labelId = null;
         try{
             labelId = remote.updateLabel(labelDO);             
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }
        
         labelDO = remote.getLabel(labelId);
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

    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl"));    
        
        DataModel printertypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "printertypeDropDown");
        DataModel scriptletDropDownField = (DataModel)CachingManager.getElement("InitialData", "scriptletDropDown");
        
        if(printertypeDropDownField ==null)
            printertypeDropDownField = getInitialModel("printerType");
        
        if(scriptletDropDownField ==null)
            scriptletDropDownField = getInitialModel("scriptlet");
        
        return new DataObject[] {xml,printertypeDropDownField,scriptletDropDownField};
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
            LabelRemote qaeRemote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
            entries = qaeRemote.getScriptlets();
        }                
            
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
             
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject();
            blankNumberId.setType("integer");
            blankNumberId.setValue(new Integer(-1));
            

            blankset.setKey(blankNumberId);
    
            model.add(blankset);        
   
        
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            Object[] idType = (Object[])iter.next();

            DataSet set = new DataSet();

            //id
            Integer dropdownId = (Integer)idType[0];
            //entry
            String dropDownText = (String)idType[1];
         
            
            StringObject textObject = new StringObject();
             textObject.setValue(dropDownText.trim());
            //}
            
            set.addObject(textObject);
            

                NumberObject numberId = new NumberObject();
                numberId.setType("integer");
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
        labelDO.setName(((String)rpcSend.getFieldValue(LabelMeta.NAME)).trim());
        labelDO.setDescription(((String)rpcSend.getFieldValue(LabelMeta.DESCRIPTION)).trim());
              
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(LabelMeta.PRINTER_TYPE)))
            labelDO.setPrinterType((Integer)rpcSend.getFieldValue(LabelMeta.PRINTER_TYPE));   
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(LabelMeta.SCRIPTLET)))
            labelDO.setScriptlet((Integer)rpcSend.getFieldValue(LabelMeta.SCRIPTLET));        
     
       return labelDO;
    }

	public DataObject[] getXMLData(DataObject[] args) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

}
