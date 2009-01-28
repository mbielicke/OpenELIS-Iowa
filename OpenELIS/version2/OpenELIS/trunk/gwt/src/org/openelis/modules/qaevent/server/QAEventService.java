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
package org.openelis.modules.qaevent.server;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameTestMethodDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
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
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.QaEventRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.QAEventTypeCacheHandler;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QAEventService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>{
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;  
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final QaEventMetaMap QAEMeta = new QaEventMetaMap();
    
    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List qaEventNames = new ArrayList();
        
        if(form == null){           
            form = (Form)SessionManager.getSession().getAttribute("QaEventQuery");
    
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));
                     
             try{
                 
                 QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
                 qaEventNames = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
          
         } else{
             QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
             
             HashMap<String,AbstractField> fields = form.getFieldMap();             

                 try{
                     qaEventNames = remote.query(fields,0,leftTableRowsPerPage);
    
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             }
    
             //need to save the rpc used to the encache
             SessionManager.getSession().setAttribute("QaEventQuery", form);
        }
        
        int i=0;
        if(model == null)
            model = new DataModel<DataSet>();
        else
            model.clear();    
        while(i < qaEventNames.size() && i < leftTableRowsPerPage) {
            IdNameTestMethodDO resultDO = (IdNameTestMethodDO)qaEventNames.get(i);
            //qaEvent id
            Integer idResult = resultDO.getId();
            //qaEvent name
            String nameResult = resultDO.getName();
            String tnameResult = resultDO.getTest();
            String mnameResult = resultDO.getMethod();
   
            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
   
            StringObject qaname = new StringObject();
            StringObject tname = new StringObject();
            StringObject mname = new StringObject();
   
            id.setValue(idResult);
   
            qaname.setValue(nameResult);  
            tname.setValue(tnameResult);
            mname.setValue(mnameResult);
            row.setKey(id);          
   
            row.add(qaname);
            row.add(tname);
            row.add(mname);
            model.add(row);
            i++;
         }         
        
         return model;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        QaEventDO qaeDO =  getQaEventDOFromRPC(rpc.form);
        
        List<Exception> exceptionList = remote.validateForAdd(qaeDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            return rpc;
        }  
        Integer qaeId = null;
        try{ 
         qaeId = remote.updateQaEvent(qaeDO);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }

        qaeDO.setId(qaeId);
        
        setFieldsInRPC(rpc.form, qaeDO);        
        return rpc;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        QaEventDO qaeDO =  getQaEventDOFromRPC(rpc.form);
        
        List<Exception> exceptionList = remote.validateForUpdate(qaeDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            return rpc;
        } 
        try{ 
            remote.updateQaEvent(qaeDO);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }
        
        setFieldsInRPC(rpc.form, qaeDO);
        
        return rpc;
    }
    

    public RPC commitDelete(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC abort(RPC rpc) throws RPCException {
            QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
            Integer qaEventId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();
    
            QaEventDO qaeDO = new QaEventDO();
             try{
              qaeDO = remote.getQaEventAndUnlock(qaEventId, SessionManager.getSession().getId());
             } catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpc.form, qaeDO);
            return rpc;
            
        }

    public RPC fetch(RPC rpc) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaeId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();
        QaEventDO qaeDO = remote.getQaEvent(qaeId);
//      set the fields in the RPC
        setFieldsInRPC(rpc.form, qaeDO);      
            
        return rpc;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaEventId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();

        QaEventDO qaeDO = new QaEventDO();
         try{
          qaeDO = remote.getQaEventAndLock(qaEventId, SessionManager.getSession().getId());
         } catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
         //  set the fields in the RPC
         setFieldsInRPC(rpc.form, qaeDO);        
            
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl"); 
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl"));    
        
        //DataModel qaTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "qaTypeDropDown");
        DataModel testDropDownField = (DataModel)CachingManager.getElement("InitialData", "testDropDown");
        
        DataModel qaTypeDropDownField = QAEventTypeCacheHandler.getQAEventTypes();
        
        //if(qaTypeDropDownField ==null)
          //  qaTypeDropDownField = getInitialModel("qaEventType");
        
        if(testDropDownField ==null)
            testDropDownField = getInitialModel("test");
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("qaevent", qaTypeDropDownField);
        map.put("tests",testDropDownField);
        
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    public RPC getScreen(RPC rpc){
        return rpc;
    }

    public DataModel getInitialModel(String cat) {
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        Integer id = null;
                
        DataModel model = new DataModel();
        
        if(cat.equals("qaEventType")){
            id = catRemote.getCategoryId("qaevent_type"); 
            entries = catRemote.getDropdownValues(id);
        }else if(cat.equals("test")){
            QaEventRemote qaeRemote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
            entries = qaeRemote.getTestNames();
        }            
        
        if(entries.size() > 0){ 
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
             
            blankStringId.setValue("");
            blankset.add(blankStringId);
            
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
            blankNumberId.setValue(new Integer(-1));
            

            blankset.setKey(blankNumberId);
    
            model.add(blankset);        
        }
    
        int i=0;
        while(i < entries.size()){
            DataSet set = new DataSet();
            
        //id
        Integer dropdownId = null;
        //entry
        String dropDownText = null;
        //method
        String methodName = null;
        
        if(cat.equals("test")){ 
            QaEventTestDropdownDO resultDO = (QaEventTestDropdownDO) entries.get(i);
            dropdownId = resultDO.getId();
            dropDownText = resultDO.getTest();
            methodName = resultDO.getMethod();
        }else{
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            dropdownId = resultDO.getId();
            dropDownText = resultDO.getName();
        }
        
        StringObject textObject = new StringObject();
                    
        if(methodName!=null){
         textObject.setValue(dropDownText+" , "+methodName);
        }else{
         textObject.setValue(dropDownText);
        }
        
        set.add(textObject);            

            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);

            numberId.setValue(dropdownId);

            set.setKey(numberId);           
        
        model.add(set);
        i++;
         }
           
        return model;
    }

    private void setFieldsInRPC(Form form, QaEventDO qaeDO){
        form.setFieldValue(QAEMeta.getId(), qaeDO.getId());
        form.setFieldValue(QAEMeta.getName(),qaeDO.getName());
        form.setFieldValue(QAEMeta.getReportingSequence(),qaeDO.getReportingSequence());
        form.setFieldValue(QAEMeta.getIsBillable(),qaeDO.getIsBillable());     
        form.setFieldValue(QAEMeta.getDescription(),qaeDO.getDescription());
        form.setFieldValue(QAEMeta.getReportingText(),qaeDO.getReportingText());   
        form.setFieldValue(QAEMeta.getTestId(),new DataSet(new NumberObject(qaeDO.getTest())));        
        form.setFieldValue(QAEMeta.getTypeId(),new DataSet(new NumberObject(qaeDO.getType())));
    }
    
    private QaEventDO getQaEventDOFromRPC(Form form){
        QaEventDO qaeDO = new QaEventDO();
        NumberField qaeIdField = (NumberField) form.getField(QAEMeta.getId());
        
        qaeDO.setId((Integer)qaeIdField.getValue());
        qaeDO.setDescription(((String)form.getFieldValue(QAEMeta.getDescription())));
        

        qaeDO.setIsBillable(((String)form.getFieldValue(QAEMeta.getIsBillable())));                                     
        qaeDO.setName(((String)form.getFieldValue(QAEMeta.getName())));         
        qaeDO.setReportingSequence((Integer)form.getFieldValue(QAEMeta.getReportingSequence()));
        qaeDO.setReportingText(((String)form.getFieldValue(QAEMeta.getReportingText())));    
                
        if(!(new Integer(-1)).equals(form.getFieldValue(QAEMeta.getTestId())))           
            qaeDO.setTest((Integer)form.getFieldValue(QAEMeta.getTestId()));   
        if(!(new Integer(-1)).equals(form.getFieldValue(QAEMeta.getTypeId())))
            qaeDO.setType((Integer)form.getFieldValue(QAEMeta.getTypeId()));        
     
       return qaeDO;
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
