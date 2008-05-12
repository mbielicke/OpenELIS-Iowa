package org.openelis.modules.qaevent.server;

import org.openelis.domain.QaEventDO;
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
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.QaEventMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.QaEventRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class QAEventService implements
                                   AppScreenFormServiceInt,
                                   AutoCompleteServiceInt{

    /**
     * 
     */    
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;  
    
    private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
                                                                new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                                                                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaEventId = (Integer)key.getKey().getValue();

        QaEventDO qaeDO = new QaEventDO();
         try{
          qaeDO = remote.getQaEventAndUnlock(qaEventId);
         } catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
         setFieldsInRPC(rpcReturn, qaeDO);
        return rpcReturn;
        
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        QaEventDO qaeDO =  getQaEventDOFromRPC(rpcSend);
        
        List<Exception> exceptionList = remote.validateForAdd(qaeDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);   
            return rpcSend;
        }  
        Integer qaeId = null;
        try{ 
         qaeId = remote.updateQaEvent(qaeDO);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        }

        qaeDO.setId(qaeId);
        
        setFieldsInRPC(rpcReturn, qaeDO);        
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        if(rpcSend == null){           
                
         
            FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":QaEvent");

            if(rpc == null)
                throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

             List qaEvents = null;
                 
             try{
                 
                 QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
                 qaEvents = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
         
         int i=0;
         model.clear();

         while(i < qaEvents.size() && i < leftTableRowsPerPage) {
             Object[] result = (Object[])qaEvents.get(i);
             //qaEvent id
             Integer idResult = (Integer)result[0];
             //qaEvent name
             String nameResult = (String)result[1];
             String tnameResult = (String)result[2];
             String mnameResult = (String)result[3];

             DataSet row = new DataSet();
             
             NumberObject id = new NumberObject(NumberObject.INTEGER);

             StringObject qaname = new StringObject();
             StringObject tname = new StringObject();
             StringObject mname = new StringObject();
  
             id.setValue(idResult);

                 qaname.setValue(nameResult);  
                 tname.setValue(tnameResult);
                 mname.setValue(mnameResult);
             row.setKey(id);          

             row.addObject(qaname);
             row.addObject(tname);
             row.addObject(mname);
             model.add(row);
             i++;
          }         
                 
         return model;   
         } else{
             QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
             
             HashMap<String,AbstractField> fields = rpcSend.getFieldMap();             
              

             List qaEventNames = new ArrayList();
                 try{
                     qaEventNames = remote.query(fields,0,leftTableRowsPerPage);

             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }

             Iterator itraaa = qaEventNames.iterator();
             model=  new DataModel();
             while(itraaa.hasNext()){
                 Object[] result = (Object[])itraaa.next();
                 //qaEvent id
                 Integer idResult = (Integer)result[0];
                 //qaEvent name
                 String nameResult = (String)result[1];
                 String tnameResult = (String)result[2];
                 String mnameResult = (String)result[3];

                 DataSet row = new DataSet();
                 
                 NumberObject id = new NumberObject(NumberObject.INTEGER);

                 StringObject qaname = new StringObject();
                 StringObject tname = new StringObject();
                 StringObject mname = new StringObject();
                 id.setValue(idResult);
     
                     qaname.setValue(nameResult);  
                     tname.setValue(tnameResult);
                     mname.setValue(mnameResult);

                     row.setKey(id);          

                 row.addObject(qaname);
                 row.addObject(tname);
                 row.addObject(mname);
                 model.add(row);

             } 
             if(SessionManager.getSession().getAttribute("systemUserId") == null)
                 SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
             CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":QaEvent", rpcSend);          
        }
         return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        QaEventDO qaeDO =  getQaEventDOFromRPC(rpcSend);
        
        List<Exception> exceptionList = remote.validateForUpdate(qaeDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);   
            return rpcSend;
        } 
        try{ 
            remote.updateQaEvent(qaeDO);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        }
        
        setFieldsInRPC(rpcReturn, qaeDO);
        
        return rpcReturn;
    }
    

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaeId = (Integer)key.getKey().getValue();

        QaEventDO qaeDO = remote.getQaEvent(qaeId);
//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, qaeDO);      
            
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaEventId = (Integer)key.getKey().getValue();

        QaEventDO qaeDO = new QaEventDO();
         try{
          qaeDO = remote.getQaEventAndLock(qaEventId);
         } catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
         //  set the fields in the RPC
         setFieldsInRPC(rpcReturn, qaeDO);        
            
        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl"); 
    }

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getInitialModel(String cat) {
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        int id = -1;
                
        DataModel model = new DataModel();
        
        if(cat.equals("qaEventType")){
            id = catRemote.getCategoryId("qaevent_type"); 
            entries = catRemote.getDropdownValues(id);
        }else if(cat.equals("test")){
            QaEventRemote qaeRemote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
            entries = qaeRemote.getTestNames();
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
            Object[] idType = (Object[])iter.next();

            DataSet set = new DataSet();

            //id
            Integer dropdownId = (Integer)idType[0];
            //entry
            String dropDownText = (String)idType[1];
            String methodName = null;
            
            if(cat.equals("test")){ 
              methodName = (String)idType[2];
            }
            
            StringObject textObject = new StringObject();
            
            

            
            if(methodName!=null){
             textObject.setValue(dropDownText.trim()+" , "+methodName.trim());
            }else{
             textObject.setValue(dropDownText.trim());
            }
            
            set.addObject(textObject);
            

                NumberObject numberId = new NumberObject(NumberObject.INTEGER);

                numberId.setValue(dropdownId);

                set.setKey(numberId);           
            
            model.add(set);
            
         }
           
        return model;
    }

    public DataModel getMatches(String cat, DataModel model, String match) {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
        

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl"));    
        
        DataModel qaTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "qaTypeDropDown");
        DataModel testDropDownField = (DataModel)CachingManager.getElement("InitialData", "testDropDown");
        
        if(qaTypeDropDownField ==null)
            qaTypeDropDownField = getInitialModel("qaEventType");
        
        if(testDropDownField ==null)
            testDropDownField = getInitialModel("test");
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("qaevent", qaTypeDropDownField);
        map.put("tests",testDropDownField);
        
        return map;
    }
    
    private void setFieldsInRPC(FormRPC rpcReturn, QaEventDO qaeDO){
        rpcReturn.setFieldValue(QaEventMeta.ID, qaeDO.getId());
        rpcReturn.setFieldValue(QaEventMeta.NAME,qaeDO.getName());
        rpcReturn.setFieldValue(QaEventMeta.REPORTING_SEQUENCE,qaeDO.getReportingSequence());
        rpcReturn.setFieldValue(QaEventMeta.IS_BILLABLE,qaeDO.getIsBillable());     
        rpcReturn.setFieldValue(QaEventMeta.DESCRIPTION,qaeDO.getDescription());
        rpcReturn.setFieldValue(QaEventMeta.REPORTING_TEXT,qaeDO.getReportingText());   
        rpcReturn.setFieldValue(QaEventMeta.TEST_ID,qaeDO.getTest());        
        rpcReturn.setFieldValue(QaEventMeta.TYPE,qaeDO.getType());
    }
    
    private QaEventDO getQaEventDOFromRPC(FormRPC rpcSend){
        QaEventDO qaeDO = new QaEventDO();
        NumberField qaeIdField = (NumberField) rpcSend.getField(QaEventMeta.ID);
        
        qaeDO.setId((Integer)qaeIdField.getValue());
        qaeDO.setDescription(((String)rpcSend.getFieldValue(QaEventMeta.DESCRIPTION)).trim());
        

        qaeDO.setIsBillable(((String)rpcSend.getFieldValue(QaEventMeta.IS_BILLABLE)).trim());                                     
        qaeDO.setName(((String)rpcSend.getFieldValue(QaEventMeta.NAME)).trim());         
        qaeDO.setReportingSequence((Integer)rpcSend.getFieldValue(QaEventMeta.REPORTING_SEQUENCE));
        qaeDO.setReportingText(((String)rpcSend.getFieldValue(QaEventMeta.REPORTING_TEXT)).trim());    
                
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(QaEventMeta.TEST_ID)))
            qaeDO.setTest((Integer)rpcSend.getFieldValue(QaEventMeta.TEST_ID));   
        if(!(new Integer(-1)).equals(rpcSend.getFieldValue(QaEventMeta.TYPE)))
            qaeDO.setType((Integer)rpcSend.getFieldValue(QaEventMeta.TYPE));        
     
       return qaeDO;
    }

	public HashMap getXMLData(HashMap args) throws RPCException {
		// TODO Auto-generated method stub
		return null;
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
         rpcSend.status = IForm.INVALID_FORM;
     }

}
