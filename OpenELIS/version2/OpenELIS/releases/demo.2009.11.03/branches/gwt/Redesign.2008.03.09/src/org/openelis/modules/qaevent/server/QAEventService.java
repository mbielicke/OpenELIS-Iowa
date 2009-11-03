package org.openelis.modules.qaevent.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.QaEventDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.QaEventRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

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
        Integer qaEventId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        QaEventDO qaeDO = new QaEventDO();
         try{
          qaeDO = remote.getQaEventAndUnlock(qaEventId);
         } catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("qaeId", qaeDO.getId());
        rpcReturn.setFieldValue("name",qaeDO.getName());
        rpcReturn.setFieldValue("sequence",qaeDO.getReportingSequence());
        rpcReturn.setFieldValue("billable",qaeDO.getIsBillable());     
        rpcReturn.setFieldValue("description",qaeDO.getDescription());
        rpcReturn.setFieldValue("reportingText",qaeDO.getReportingText());   
        rpcReturn.setFieldValue("testId",qaeDO.getTest());
        rpcReturn.setFieldValue("qaEventTypeId",qaeDO.getType());
        return rpcReturn;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        QaEventDO qaeDO = new QaEventDO();
        
        qaeDO.setDescription((String)rpcSend.getFieldValue("description"));
        
        CheckField billable = (CheckField)rpcSend.getField("billable");
        Boolean billableVal = (Boolean)billable.getValue();      
        
        qaeDO.setIsBillable(billableVal?"Y":"N");        
        qaeDO.setName((String)rpcSend.getFieldValue("name"));         
        qaeDO.setReportingSequence((Integer)rpcSend.getFieldValue("sequence"));
        qaeDO.setReportingText((String)rpcSend.getFieldValue("reportingText"));
        qaeDO.setTest((Integer)rpcSend.getFieldValue("testId"));
        qaeDO.setType((Integer)rpcSend.getFieldValue("qaEventTypeId"));
        
        Integer qaeId = remote.updateQaEvent(qaeDO);
        
        qaeDO = remote.getQaEvent((Integer)qaeId);
        rpcReturn.setFieldValue("qaeId", qaeId);
        rpcReturn.setFieldValue("name",qaeDO.getName());
        rpcReturn.setFieldValue("sequence",qaeDO.getReportingSequence());
        rpcReturn.setFieldValue("billable",qaeDO.getIsBillable());     
        rpcReturn.setFieldValue("description",qaeDO.getDescription());
        rpcReturn.setFieldValue("reportingText",qaeDO.getReportingText());   
        rpcReturn.setFieldValue("testId",qaeDO.getTest());
        rpcReturn.setFieldValue("qaEventTypeId",qaeDO.getType());
        
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
                 System.out.println("qaEvents.size() "+qaEvents.size());
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
         
         int i=0;
         model.clear();
        // List providers = new ArrayList();
         
         //while(i < organizations.size() && i < leftTableRowsPerPage) {
         while(i < qaEvents.size() && i < leftTableRowsPerPage) {
             Object[] result = (Object[])qaEvents.get(i);
             //qaEvent id
             Integer idResult = (Integer)result[0];
             //qaEvent name
             String nameResult = (String)result[1];
             String tnameResult = (String)result[2];
             String mnameResult = (String)result[3];

             DataSet row = new DataSet();
             
             NumberObject id = new NumberObject();

             StringObject qaname = new StringObject();
             StringObject tname = new StringObject();
             StringObject mname = new StringObject();
             id.setType("integer");
             //lname.setValue(lnameResult);
             id.setValue(idResult);
             //if(tnameResult!=null && mnameResult!=null){
              //name.setValue(nameResult+" , "+tnameResult+" , "+mnameResult);                
             //}else {
                 qaname.setValue(nameResult);  
                 tname.setValue(tnameResult);
                 mname.setValue(mnameResult);
             //} 
             row.addObject(id);          
             //row.addObject(lname);
             //row.addObject(fname);
             row.addObject(qaname);
             row.addObject(tname);
             row.addObject(mname);
             i++;
          }
         //} 
         //if(systemUserId.equals("")){
             //systemUserId = remote.getSystemUserId().toString();
         //CachingManager.putElement("screenQueryRpc", systemUserId+":Organization", rpcSend);
                 
         return model;   
         } else{
             QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
             
             HashMap<String,AbstractField> fields = rpcSend.getFieldMap();

             //contacts table
              

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
                 Object[] result = (Object[])(Object[])itraaa.next();
                 //qaEvent id
                 Integer idResult = (Integer)result[0];
                 //qaEvent name
                 String nameResult = (String)result[1];
                 String tnameResult = (String)result[2];
                 String mnameResult = (String)result[3];

                 DataSet row = new DataSet();
                 
                 NumberObject id = new NumberObject();

                 StringObject qaname = new StringObject();
                 StringObject tname = new StringObject();
                 StringObject mname = new StringObject();
                 id.setType("integer");
                 //lname.setValue(lnameResult);
                 id.setValue(idResult);
                 //if(tnameResult!=null && mnameResult!=null){
                  //name.setValue(nameResult+" , "+tnameResult+" , "+mnameResult);                
                 //}else {
                     qaname.setValue(nameResult);  
                     tname.setValue(tnameResult);
                     mname.setValue(mnameResult);
                 //} 
                 row.addObject(id);          
                 //row.addObject(lname);
                 //row.addObject(fname);
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
        QaEventDO qaeDO = new QaEventDO();
        NumberField qaeId = (NumberField) rpcSend.getField("qaeId");
        
        qaeDO.setId((Integer)qaeId.getValue());
        qaeDO.setDescription((String)rpcSend.getFieldValue("description"));
        
        CheckField billable = (CheckField)rpcSend.getField("billable");
        Boolean billableVal = (Boolean)billable.getValue();      
        
        qaeDO.setIsBillable(billableVal?"Y":"N");        
        qaeDO.setName((String)rpcSend.getFieldValue("name"));         
        qaeDO.setReportingSequence((Integer)rpcSend.getFieldValue("sequence"));
        qaeDO.setReportingText((String)rpcSend.getFieldValue("reportingText"));
        qaeDO.setTest((Integer)rpcSend.getFieldValue("testId"));
        qaeDO.setType((Integer)rpcSend.getFieldValue("qaEventTypeId"));
        
        remote.updateQaEvent(qaeDO);
        
        qaeDO = remote.getQaEvent((Integer)qaeId.getValue());
        rpcReturn.setFieldValue("qaeId", qaeDO.getId());
        rpcReturn.setFieldValue("name",qaeDO.getName());
        rpcReturn.setFieldValue("sequence",qaeDO.getReportingSequence());
        rpcReturn.setFieldValue("billable",qaeDO.getIsBillable());     
        rpcReturn.setFieldValue("description",qaeDO.getDescription());
        rpcReturn.setFieldValue("reportingText",qaeDO.getReportingText());   
        rpcReturn.setFieldValue("testId",qaeDO.getTest());
        rpcReturn.setFieldValue("qaEventTypeId",qaeDO.getType());
        
        return rpcReturn;
    }
    

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer categoryId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        QaEventDO qaeDO = remote.getQaEvent(categoryId);
//      set the fields in the RPC
        rpcReturn.setFieldValue("qaeId", qaeDO.getId());
        rpcReturn.setFieldValue("name",qaeDO.getName());
        rpcReturn.setFieldValue("sequence",qaeDO.getReportingSequence());
        rpcReturn.setFieldValue("billable",qaeDO.getIsBillable());     
        rpcReturn.setFieldValue("description",qaeDO.getDescription());
        rpcReturn.setFieldValue("reportingText",qaeDO.getReportingText());   
        rpcReturn.setFieldValue("testId",qaeDO.getTest());
        rpcReturn.setFieldValue("qaEventTypeId",qaeDO.getType());
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaEventId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        QaEventDO qaeDO = new QaEventDO();
         try{
          qaeDO = remote.getQaEventAndLock(qaEventId);
         } catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("qaeId", qaeDO.getId());
        rpcReturn.setFieldValue("name",qaeDO.getName());
        rpcReturn.setFieldValue("sequence",qaeDO.getReportingSequence());
        rpcReturn.setFieldValue("billable",qaeDO.getIsBillable());     
        rpcReturn.setFieldValue("description",qaeDO.getDescription());
        rpcReturn.setFieldValue("reportingText",qaeDO.getReportingText());   
        rpcReturn.setFieldValue("testId",qaeDO.getTest());
        rpcReturn.setFieldValue("qaEventTypeId",qaeDO.getType());
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
        
        System.out.println("cat "+ cat +" id "+ id);
        //if(id >-1){
            
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
            BooleanObject blankSelected = new BooleanObject();               
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject();
            blankNumberId.setType("integer");
            blankNumberId.setValue(new Integer(0));
            //if(cat.equals("providerType")){
              blankset.addObject(blankNumberId);
            /*} else{
            //  blankset.addObject(blankStringId);  
            }*/            
            
            blankSelected.setValue(new Boolean(false));
            blankset.addObject(blankSelected);
            
            model.add(blankset);        
       // System.out.println("typeId "+provDO.getTypeId());
        //System.out.println("type "+provDO.getType()); 
       // List<Object[]> provTypes = remote.getProviderTypes();        
        
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            Object[] idType = (Object[])iter.next();
            //System.out.println("typeId "+idType[0]);
            //System.out.println("type "+idType[1]); 
            DataSet set = new DataSet();
            //Object[] result = (Object[]) entries.get(i);
            //id
            Integer dropdownId = (Integer)idType[0];
            //entry
            String dropDownText = (String)idType[1];
            String methodName = null;
            
            if(cat.equals("test")){ 
              methodName = (String)idType[2];
            }
            
            StringObject textObject = new StringObject();
            
            
            BooleanObject selected = new BooleanObject();
            
            if(methodName!=null){
             textObject.setValue(dropDownText.trim()+" , "+methodName.trim());
            }else{
             textObject.setValue(dropDownText.trim());
            }
            
            set.addObject(textObject);
            
            //if(cat.equals("providerType")){
                NumberObject numberId = new NumberObject();
                numberId.setType("integer");
                numberId.setValue(dropdownId);
                set.addObject(numberId);
            /* }else{
               StringObject stringId = new StringObject();
               stringId.setValue(dropdownText);
               set.addObject(stringId);            
            }*/
            
            selected.setValue(new Boolean(false));
            set.addObject(selected);
            
            model.add(set);
            
         }
       
        // }        
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
        

    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl"));    
        
        DataModel qaTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "qaTypeDropDown");
        DataModel testDropDownField = (DataModel)CachingManager.getElement("InitialData", "testDropDown");
        
        if(qaTypeDropDownField ==null)
            qaTypeDropDownField = getInitialModel("qaEventType");
        
        if(testDropDownField ==null)
            testDropDownField = getInitialModel("test");
        
        return new DataObject[] {xml,qaTypeDropDownField,testDropDownField};
    }

}
