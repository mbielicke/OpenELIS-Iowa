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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.IdNameTestMethodDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventView;
import org.openelis.domain.QaEventViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.modules.qaevent.client.QAEventForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalysisQAEventManagerRemote;
import org.openelis.remote.QaEventRemote;
import org.openelis.remote.SampleQAEventManagerRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;


public class QAEventService implements AppScreenFormServiceInt<QAEventForm, org.openelis.gwt.common.deprecated.Query<TableDataRow<Integer>>>,
                                       AutoCompleteServiceInt {
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;  
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public org.openelis.gwt.common.deprecated.Query<TableDataRow<Integer>> commitQuery(org.openelis.gwt.common.deprecated.Query<TableDataRow<Integer>> query) throws Exception {
        List qaEventNames = new ArrayList();

        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 

        try{
            qaEventNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){            
            throw new Exception(e.getMessage());
        }
    
        //need to save the rpc used to the encache            
        
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();    
        while(i < qaEventNames.size() && i < leftTableRowsPerPage) {
            IdNameTestMethodDO resultDO = (IdNameTestMethodDO)qaEventNames.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),
                                                new FieldType[] {
                                                                 new StringObject(resultDO.getName()),
                                                                 new StringObject(resultDO.getTest()),
                                                                 new StringObject(resultDO.getMethod())
                                                }
                     )      
            );
            i++;
         }         
        
         return query;
    }

    public QAEventForm commitAdd(QAEventForm rpc) throws Exception {
        QaEventRemote remote;
        QaEventViewDO qaeDO;          
        Integer qaeId;
        
        qaeId = null;
        remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        qaeDO =  getQaEventDOFromRPC(rpc);
        try{ 
            qaeId = remote.updateQaEvent(qaeDO);
            qaeDO = remote.getQaEvent(qaeId);
        }catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {            
            throw new Exception(e.getMessage());            
        }

        qaeDO.setId(qaeId);
        
        setFieldsInRPC(rpc, qaeDO);        
        return rpc;
    }

    public QAEventForm commitUpdate(QAEventForm rpc) throws Exception {
        QaEventRemote remote;
        QaEventViewDO qaeDO;
        
        remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        qaeDO =  getQaEventDOFromRPC(rpc);       
        try{ 
            remote.updateQaEvent(qaeDO);
            qaeDO = remote.getQaEvent(rpc.entityKey);
        }catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {           
            throw new Exception(e.getMessage());            
        }
        
        setFieldsInRPC(rpc, qaeDO);
        
        return rpc;
    }
    

    public QAEventForm commitDelete(QAEventForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public QAEventForm abort(QAEventForm rpc) throws Exception {
            QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
            Integer qaEventId = rpc.entityKey;
    
            QaEventViewDO qaeDO = new QaEventViewDO();
             try{
              qaeDO = remote.getQaEventAndUnlock(qaEventId, SessionManager.getSession().getId());
             } catch(Exception ex){
                 throw new Exception(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpc, qaeDO);
            return rpc;
            
        }

    public QAEventForm fetch(QAEventForm rpc) throws Exception {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaeId = rpc.entityKey;
        QaEventViewDO qaeDO = remote.getQaEvent(qaeId);
//      set the fields in the RPC
        setFieldsInRPC(rpc, qaeDO);      
            
        return rpc;
    }

    public QAEventForm fetchForUpdate(QAEventForm rpc) throws Exception {
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote"); 
        Integer qaEventId = rpc.entityKey;

        QaEventViewDO qaeDO = new QaEventViewDO();
         try{
          qaeDO = remote.getQaEventAndLock(qaEventId, SessionManager.getSession().getId());
         } catch(Exception ex){
             throw new Exception(ex.getMessage());
         }  
         //  set the fields in the RPC
         setFieldsInRPC(rpc, qaeDO);        
            
        return rpc;
    }
    
    public QAEventForm getScreen(QAEventForm rpc) throws Exception{       
       rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/qaEvent.xsl");                     
       return rpc;
    }

    private void setFieldsInRPC(QAEventForm form, QaEventViewDO qaeDO){
        TableDataModel<TableDataRow<Integer>> model;
        
        form.id.setValue(qaeDO.getId());
        form.name.setValue(qaeDO.getName());
        form.reportingSequence.setValue(qaeDO.getReportingSequence());
        form.isBillable.setValue(qaeDO.getIsBillable());     
        form.description.setValue(qaeDO.getDescription());
        form.reportingText.setValue(qaeDO.getReportingText());           
        
        model = new TableDataModel();
        if(qaeDO.getTestId() != null) {
            model.add(new TableDataRow<Integer>(qaeDO.getTestId(),
                            new StringObject(qaeDO.getTestName()+","+
                                             qaeDO.getMethodName())));
            form.testId.setValue(model.get(0));
        }        
        form.testId.setModel(model);
        
        form.typeId.setValue(new TableDataRow<Integer>(qaeDO.getTypeId()));
    }
    
    private QaEventViewDO getQaEventDOFromRPC(QAEventForm form){
        QaEventViewDO qaeDO;

        qaeDO = new QaEventViewDO();
        qaeDO.setId(form.id.getValue());
        qaeDO.setDescription(form.description.getValue());
        qaeDO.setIsBillable(form.isBillable.getValue());
        qaeDO.setName(form.name.getValue());
        qaeDO.setReportingSequence(form.reportingSequence.getValue());
        qaeDO.setReportingText(form.reportingText.getValue());
        qaeDO.setTestId((Integer)form.testId.getSelectedKey());
        qaeDO.setTypeId((Integer)form.typeId.getSelectedKey());

        return qaeDO;
    }

	private void setRpcErrors(List exceptionList, Form<? extends Object> form){
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

    public TableDataModel getMatches(String cat,
                                     TableDataModel model,
                                     String match,
                                     HashMap<String, FieldType> params) throws Exception {
        TestRemote tremote;
        List<TestMethodVO> tmlist;
        TableDataModel<TableDataRow<Integer>> dataModel;
        
        tremote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        tmlist = tremote.fetchByName(match.trim() + "%", 10);
        dataModel = getTestMethodAutoCompleteModel(tmlist);
        return dataModel;
    }
    
    private TableDataModel<TableDataRow<Integer>> getTestMethodAutoCompleteModel(List<TestMethodVO> autoCompleteList){
        TableDataModel<TableDataRow<Integer>> dataModel;
        TableDataRow<Integer> data;
        TestMethodVO resultDO;
        Integer testId;
        String testName;
        String methodName;
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        
        for(int i=0; i < autoCompleteList.size(); i++){
            resultDO = (TestMethodVO) autoCompleteList.get(i);
            testId = resultDO.getTestId();
            testName = resultDO.getTestName();
            methodName = resultDO.getMethodName();

            data = new TableDataRow<Integer>(testId,new StringObject(testName+","+methodName));
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;       
    }
    
    
    public ArrayList<QaEventView> getQAEventMatches(Query query){
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        ArrayList<QaEventView> list;
        String value;
        Integer id;
        
        if(query.getFields().size() == 1){
            value = query.getFields().get(0).query;
            list = remote.fetchCommonByName(value+"%", 10);
        }else{
            value = query.getFields().get(0).query;
            id = new Integer(query.getFields().get(1).query);
            list = remote.fetchTestSpecificByName(value+"%", id, 10);
        }
        
        return list;
    }
    
    public ArrayList<QaEventDO> getListOfQaevents(){
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        return remote.fetchAllCommon();
    }
    
    public ArrayList<QaEventDO> getListOfQaevents(Query query){
        QaEventRemote remote = (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
        Integer testId = new Integer(query.getFields().get(0).query);
        
        return remote.fetchTestSpecificById(testId);
    }
    
    //qa event manager methods
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        SampleQAEventManagerRemote remote = (SampleQAEventManagerRemote)EJBFactory.lookup("openelis/SampleQAEventManagerBean/remote");
        
        return remote.fetchBySampleId(sampleId);
    }
    
    public AnalysisQaEventManager fetchByAnalysisId(Integer analysisId) throws Exception {
        AnalysisQAEventManagerRemote remote = (AnalysisQAEventManagerRemote)EJBFactory.lookup("openelis/AnalysisQAEventManagerBean/remote");
        return remote.fetchByAnalysisId(analysisId);
    }

}
