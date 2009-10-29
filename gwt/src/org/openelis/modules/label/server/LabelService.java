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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.modules.label.client.LabelForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.LabelRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class LabelService implements AppScreenFormServiceInt<LabelForm,Query<TableDataRow<Integer>>>,
                                        AutoCompleteServiceInt {   
    
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9; 
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));    
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        List labels;
        LabelRemote remote; 
        IdNameDO resultDO;
        Integer idResult;
        String nameResult;
        int i;
         
        remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");         
        
        try{
            labels = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){            
            throw new Exception(e.getMessage());
        }
            
        i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
   
        while(i < labels.size() && i < leftTableRowsPerPage) {
            resultDO = (IdNameDO)labels.get(i);            
            idResult = resultDO.getId();
            nameResult = resultDO.getName();             
      
            query.results.add(new TableDataRow<Integer>(idResult,new StringObject(nameResult)));
            i++;
         }         
                      
        return query;
    }

    public LabelForm commitAdd(LabelForm rpc) throws Exception {
        LabelRemote remote; 
        LabelViewDO labelDO;
        Integer labelId; 
                  
        remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        labelDO = getLabelDOFromRPC(rpc);
      //  labelDO.setDelete(false);
        try{
            labelId = (Integer)remote.updateLabel(labelDO);
            labelDO = remote.getLabel(labelId);
        }catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
        
        labelDO.setId(labelId);
        
        setFieldsInRPC(rpc, labelDO);
        return rpc;
    }

    public LabelForm commitUpdate(LabelForm rpc) throws Exception {
        LabelRemote remote; 
        LabelViewDO labelDO;         
          
        remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");
        labelDO = getLabelDOFromRPC(rpc);
   //     labelDO.setDelete(false);
        try{
            remote.updateLabel(labelDO);        
            labelDO = remote.getLabel(rpc.entityKey);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
        
        setFieldsInRPC(rpc, labelDO);
        return rpc;
    }

    public LabelForm commitDelete(LabelForm rpc) throws Exception {
        LabelViewDO labelDO;         
        LabelRemote remote; 
        
        remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");
        labelDO = getLabelDOFromRPC(rpc);
    //    labelDO.setDelete(true);
        try{
            remote.deleteLabel(labelDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
        setFieldsInRPC(rpc, new LabelViewDO());
        return rpc;
    }

    public LabelForm abort(LabelForm rpc) throws Exception {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;
        LabelViewDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndUnlock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new Exception(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc, labelDO);   
         return rpc;
    }

    public LabelForm fetch(LabelForm rpc) throws Exception {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;

        LabelViewDO labelDO = remote.getLabel(labelId);
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, labelDO);
                  
        return rpc;
    }

    public LabelForm fetchForUpdate(LabelForm rpc) throws Exception {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        Integer labelId = rpc.entityKey;
        LabelViewDO labelDO =null;
        try{
            labelDO  = remote.getLabelAndLock(labelId, SessionManager.getSession().getId());
           } catch(Exception ex){
               throw new Exception(ex.getMessage());
           }  
           
         setFieldsInRPC(rpc, labelDO);   
         return rpc;
    }

    public LabelForm getScreen(LabelForm rpc) throws Exception{
        rpc.xml  = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/label.xsl");                 
        return rpc;
    }   
    
    private void setFieldsInRPC(LabelForm form, LabelViewDO qaeDO){
        TableDataModel<TableDataRow<Integer>> model;
        
        form.id.setValue(qaeDO.getId());
        form.name.setValue(qaeDO.getName());
        form.description.setValue(qaeDO.getDescription());
        form.printerTypeId.setValue(new TableDataRow<Integer>(qaeDO.getPrinterTypeId()));     
        
        model = new TableDataModel<TableDataRow<Integer>>();
        if(qaeDO.getScriptletId() != null) {
            model.add(new TableDataRow<Integer>(qaeDO.getScriptletId(),
                            new StringObject(qaeDO.getScriptletName())));
            form.scriptletId.setValue(model.get(0));
        }
        form.scriptletId.setModel(model);        
    }
    
    private LabelViewDO getLabelDOFromRPC(LabelForm form){
        LabelViewDO labelDO;

        labelDO = new LabelViewDO();
        labelDO.setId(form.id.getValue());
        labelDO.setName(form.name.getValue());
        labelDO.setDescription(form.description.getValue());
        labelDO.setPrinterType((Integer)(form.printerTypeId.getSelectedKey()));
        labelDO.setScriptletId((Integer)(form.scriptletId.getSelectedKey()));

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

    public TableDataModel getMatches(String cat,TableDataModel model,String match,
                                     HashMap<String, FieldType> params) throws Exception {        
        TableDataModel<TableDataRow<Integer>> dataModel;
        ScriptletRemote sremote;
        List<IdNameDO> entries;
        
        sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
        //entries = sremote.findByName(match.trim() + "%", 10);
        //dataModel = getAutocompleteModel(entries);        
        
        //return dataModel;
        return null;
    }
    
    public ArrayList<IdNameVO> fetchByName(String search) throws Exception {
        LabelRemote remote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote"); 
        
        try {
            return remote.fetchByName(search+"%",10);            
        }catch(RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    private TableDataModel<TableDataRow<Integer>> getAutocompleteModel(List<IdNameDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {

            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();
            String entryText = element.getName();

            TableDataRow<Integer> data = new TableDataRow<Integer>(entryId,
                                                                   new StringObject(entryText));
            dataModel.add(data);
        }
        
        return dataModel;
    }
}
