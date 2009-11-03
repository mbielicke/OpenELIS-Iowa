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

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.AuxFieldDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.metamap.AuxFieldMetaMap;
import org.openelis.metamap.AuxFieldValueMetaMap;
import org.openelis.modules.auxiliary.client.AuxiliaryForm;
import org.openelis.modules.auxiliary.client.AuxiliaryGeneralPurposeRPC;
import org.openelis.modules.auxiliary.client.AuxiliaryRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AuxiliaryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.AuxFieldValueTypeCacheHandler;
import org.openelis.server.handlers.UnitOfMeasureCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class AuxiliaryService implements
                             AppScreenFormServiceInt<AuxiliaryRPC, Integer> , AutoCompleteServiceInt{
    
    private static final int leftTableRowsPerPage = 21;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    private static final AuxFieldGroupMetaMap AuxFieldGroupMeta = new AuxFieldGroupMetaMap();
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List auxfgNames;
        // if the rpc is null then we need to get the page
        if (form == null) {

            form = (Form)SessionManager.getSession()
                                                 .getAttribute("AuxiliaryQuery");

            if (form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");

            try {
                auxfgNames = remote.query(form.getFieldMap(),
                                         (model.getPage() * leftTableRowsPerPage),
                                         leftTableRowsPerPage + 1);
            } catch (Exception e) {
                if (e instanceof LastPageException) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                } else {
                    e.printStackTrace();
                    throw new RPCException(e.getMessage());
                }
            }
        } else {
            AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");

            HashMap<String, AbstractField> fields = form.getFieldMap();
            fields.remove("auxiliaryTable");
            fields.remove("auxFieldValueTable");

            try {
                auxfgNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("AuxiliaryQuery", form);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel<Integer>();
        else 
            model.clear();
        while (i < auxfgNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)auxfgNames.get(i);
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

        return model;
    }
    
    public AuxiliaryRPC commitAdd(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = getAuxFieldGroupDOFromRPC(rpc.form);
        Integer axfgId;
        List<AuxFieldDO> axfDOList = getAuxFieldDOListFromRPC(null, rpc.form);
        List<AuxFieldValueDO> axfvDOList = getAuxFieldValueDOListFromRPC(null, rpc.form);
        List exceptionList = remote.validateForAdd(axfgDO,axfDOList,axfvDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);    
            return rpc;
        }
    
        try {
            axfgId = remote.updateAuxiliary(axfgDO,axfDOList,axfvDOList);
            axfgDO = remote.getAuxFieldGroup(axfgId);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        axfgDO.setId(axfgId);
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }
    
    public AuxiliaryRPC commitUpdate(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = getAuxFieldGroupDOFromRPC(rpc.form);
        IntegerField axfgId = rpc.form.id;
        List<AuxFieldDO> axfDOList = getAuxFieldDOListFromRPC(axfgId.getValue(), rpc.form);
        List<AuxFieldValueDO> axfvDOList = getAuxFieldValueDOListFromRPC(axfgId.getValue(), rpc.form);
        
        List exceptionList = remote.validateForUpdate(axfgDO,axfDOList,axfvDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);    
            return rpc;
        }
    
        try {
            remote.updateAuxiliary(axfgDO,axfDOList,axfvDOList);
            axfgDO = remote.getAuxFieldGroup(axfgId.getValue());
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
           
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }
    
    public AuxiliaryRPC commitDelete(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public AuxiliaryRPC abort(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = remote.getAuxFieldGroupAndUnlock(rpc.key,SessionManager.getSession().getId());
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }
       

    public AuxiliaryRPC fetch(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = remote.getAuxFieldGroup(rpc.key);
        List<AuxFieldDO> auxfields = remote.getAuxFields(rpc.key);
        setFieldsInRPC(rpc.form, axfgDO);        
        fillAuxFieldTable(rpc.key, rpc.form);
        return rpc;
    }

    public AuxiliaryRPC fetchForUpdate(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = new AuxFieldGroupDO(); 
        try{
            axfgDO = remote.getAuxFieldGroupAndLock(rpc.key, SessionManager.getSession().getId());
        }   catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        setFieldsInRPC(rpc.form, axfgDO);
        fillAuxFieldTable(rpc.key, rpc.form);
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl");
    }       
    
    public AuxiliaryRPC getScreen(AuxiliaryRPC rpc) throws RPCException {
        try {
         AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
         List<IdNameDO> scriptletList = remote.getScriptletDropDownValues();   
         
         rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/auxiliary.xsl");         
         
         rpc.units = UnitOfMeasureCacheHandler.getUnitsOfMeasure();
         SessionManager.getSession().setAttribute("unitsOfMeasureVersion", UnitOfMeasureCacheHandler.version);
         rpc.auxFieldValueTypes = AuxFieldValueTypeCacheHandler.getAuxFieldValueTypes();
         SessionManager.getSession().setAttribute("auxFieldValueTypesVersion", AuxFieldValueTypeCacheHandler.version);         
         rpc.scriptlets = loadDropDown(scriptletList);
        }catch(Exception ex) {
            ex.printStackTrace();
            throw new RPCException(ex.getMessage());
        } 
        return rpc;
    }
    
    public void checkModels(AuxiliaryRPC rpc) {
        
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        List<IdNameDO> scriptletList = remote.getScriptletDropDownValues();
        /*
         * Retrieve current version of models from session.
         */
        int units = (Integer)SessionManager.getSession().getAttribute("unitsOfMeasureVersion");        
        int auxFieldValueTypes = (Integer)SessionManager.getSession().getAttribute("auxFieldValueTypesVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(units != UnitOfMeasureCacheHandler.version){
            rpc.units = UnitOfMeasureCacheHandler.getUnitsOfMeasure();
            SessionManager.getSession().setAttribute("unitsOfMeasureVersion", UnitOfMeasureCacheHandler.version);
        }
        if(auxFieldValueTypes != AuxFieldValueTypeCacheHandler.version){
            rpc.auxFieldValueTypes = AuxFieldValueTypeCacheHandler.getAuxFieldValueTypes();
            SessionManager.getSession().setAttribute("auxFieldValueTypesVersion", AuxFieldValueTypeCacheHandler.version);
        }
        rpc.scriptlets = loadDropDown(scriptletList);
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl"));       

        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }

    public DataModel getMatches(String cat,DataModel model,String match,
                                HashMap<String, FieldType> params) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        List entries = remote.getMatchingEntries(match.trim()+"%", 10,cat);
        DataModel<Integer> dataModel = new DataModel<Integer>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            
            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();                   
            String entryText = element.getName();
            
            DataSet<Integer> data = new DataSet<Integer>(entryId,new StringObject(entryText));                                      
            dataModel.add(data);
        }       
        
        return dataModel;
    }  
    
    public AuxiliaryGeneralPurposeRPC getAuxFieldValueModel(AuxiliaryGeneralPurposeRPC rpc) {
      AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
      List<AuxFieldValueDO> valueDOList = remote.getAuxFieldValues(rpc.key);
      DataSet<Integer> set = null;
      
      for(AuxFieldValueDO valueDO: valueDOList) {
       set = rpc.auxFieldValueModel.createNewSet();       
       set.setKey(valueDO.getId());      
       set.get(0).setValue(new DataSet<Integer>(valueDO.getTypeId()));
       set.get(1).setValue(valueDO.getValue());
       rpc.auxFieldValueModel.add(set);
      }
      
      return rpc;
    }
    
    private AuxFieldGroupDO getAuxFieldGroupDOFromRPC(AuxiliaryForm form){
        AuxFieldGroupDO auxFieldGroupDO = new AuxFieldGroupDO();
        if(form.id!=null)
         auxFieldGroupDO.setId(form.id.getValue());
        auxFieldGroupDO.setName(((String)form.name.getValue()));   
        DatetimeRPC activeBegin = form.activeBegin.getValue();
        if (activeBegin != null)
            auxFieldGroupDO.setActiveBegin(activeBegin.getDate());

        DatetimeRPC activeEnd = form.activeEnd.getValue();
        if (activeEnd != null)
            auxFieldGroupDO.setActiveEnd(activeEnd.getDate());

        auxFieldGroupDO.setDescription(form.description.getValue());
        auxFieldGroupDO.setIsActive(form.isActive.getValue());
        
        return auxFieldGroupDO;
    }
       
    
    private void setFieldsInRPC(AuxiliaryForm form,AuxFieldGroupDO axfgDO) {
        form.id.setValue(axfgDO.getId());
        form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,
                                                          axfgDO.getActiveBegin()
                                                                       .getDate()));
        form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,
                                                          axfgDO.getActiveEnd()
                                                                       .getDate()));
        form.isActive.setValue(axfgDO.getIsActive());         
        form.name.setValue(axfgDO.getName());        
        form.description.setValue(axfgDO.getDescription());
        
    }
    
     private List<AuxFieldDO> getAuxFieldDOListFromRPC(Integer key,AuxiliaryForm form) {
         DataModel<Integer> model = form.auxFieldTable.getValue();
         DataSet<Integer> row = null;
         List<AuxFieldDO> afDOList = new ArrayList<AuxFieldDO>();
         AuxFieldDO afDO = null;
         
         for(int i= 0; i < model.size(); i++) {
              row = model.get(i);
              afDO = new AuxFieldDO();             
              if(row.getKey() != null)
                 afDO.setId(row.getKey()); 
                            
              afDO.setDelete(false);
              
              afDO.setUnitOfMeasureId((Integer)((DropDownField)row.get(2)).getSelectedKey());
              afDO.setAnalyteId((Integer)((DropDownField)row.get(0)).getSelectedKey());
              afDO.setAuxFieldGroupId(key);
              afDO.setDescription((String)row.get(6).getValue());
              afDO.setIsActive((String)row.get(3).getValue());
              afDO.setIsReportable((String)row.get(5).getValue());
              afDO.setIsRequired((String)row.get(4).getValue());
              afDO.setMethodId((Integer)((DropDownField)row.get(1)).getSelectedKey());
              afDO.setScriptletId((Integer)((DropDownField)row.get(7)).getSelectedKey());
              afDO.setSortOrder(i);
              afDOList.add(afDO);
         }
         
         for(int i= 0; i < model.getDeletions().size(); i++) {
             row = model.getDeletions().get(i);
             afDO = new AuxFieldDO();
             if(row.getKey() != null)
                 afDO.setId(row.getKey());
             
             afDO.setDelete(true); 
             afDOList.add(afDO);
        }     
         model.getDeletions().clear();
         return afDOList;
         
     }
    
    private List<AuxFieldValueDO> getAuxFieldValueDOListFromRPC(Integer key, AuxiliaryForm form) {
       DataModel<Integer> model = null;
       Iterator<Integer> iter = null;
       List<AuxFieldValueDO> valueDOList = null;
       DataSet<Integer> row = null;       
       IntegerObject id = null;
       AuxFieldValueDO valueDO = null;
       Integer auxFieldId = null;
       DropDownField<Integer> type = null; 
       DataModel<Integer> fieldModel = form.auxFieldValueTable.getValue();       
       valueDOList = new ArrayList<AuxFieldValueDO>();
        for(int i = 0 ; i < fieldModel.size(); i++) {
         auxFieldId = iter.next();   
         model = (DataModel)fieldModel.get(i).getData();
         for(int j = 0 ; j <  model.size(); j++) {
           valueDO = new AuxFieldValueDO();  
           row = model.get(j);
          
           if(row.getKey() != null) {            
            valueDO.setId(row.getKey());     
           }
            valueDO.setAuxFieldId(auxFieldId);
            valueDO.setDelete(false);
            
            type = (DropDownField<Integer>)row.get(0);            
            valueDO.setTypeId((Integer)type.getSelectedKey());
               
            valueDO.setValue(((StringField)row.get(1)).getValue());
            valueDOList.add(valueDO);
         }
         
         for(int j = 0 ; j <  model.getDeletions().size(); j++) {
             valueDO = new AuxFieldValueDO();  
             row = model.getDeletions().get(i);
             if(row.getKey() != null) {            
                valueDO.setId(row.getKey());    
             }              
              valueDO.setDelete(true);       
              valueDOList.add(valueDO);
           }
          model.getDeletions().clear();
        }        

         return valueDOList;
    }
    
    private void fillAuxFieldTable(Integer key,AuxiliaryForm form) {
        DataSet<Integer> row = null;       
        IntegerObject id = null;
        AuxFieldDO afDO = null;
        DataSet<Integer> analyteSet = null;
        DataModel<Integer> analyteModel = null;
        DataSet<Integer> methodSet = null;
        DataModel<Integer> methodModel = null;
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        DataModel<Integer> model = form.auxFieldTable.getValue();
        List<AuxFieldDO> afDOList = remote.getAuxFields(key); 
        model.clear();
        
        for(int i = 0; i < afDOList.size(); i++) {
            row = model.createNewSet();
            afDO = afDOList.get(i);                        
            row.setKey(afDO.getId());
                        
            analyteSet = new DataSet<Integer>(afDO.getAnalyteId(),new StringObject(afDO.getAnalyteName()));       
            analyteModel = new DataModel<Integer>();
            analyteModel.add(analyteSet);            
            ((DropDownField)row.get(0)).setModel(analyteModel);
            row.get(0).setValue(analyteSet);
            
            if(afDO.getMethodId()!=null) {
             methodSet = new DataSet<Integer>(afDO.getMethodId(),new StringObject(afDO.getMethodName()));       
             methodModel = new DataModel<Integer>();
             methodModel.add(methodSet);            
             ((DropDownField)row.get(1)).setModel(methodModel);
             row.get(1).setValue(methodSet);
            }
            
            if(afDO.getUnitOfMeasureId()!=null)
             row.get(2).setValue(new DataSet<Integer>(afDO.getUnitOfMeasureId()));
            
            row.get(3).setValue(afDO.getIsActive());
            row.get(4).setValue(afDO.getIsRequired());
            row.get(5).setValue(afDO.getIsReportable());
            row.get(6).setValue(afDO.getDescription());
            
            if(afDO.getScriptletId()!=null)
            row.get(7).setValue(new DataSet<Integer>(afDO.getScriptletId()));
            model.add(row);
        }
        
    }
    
    private DataModel<Integer> loadDropDown(List<IdNameDO> list) {
        DataSet<Integer> blankset = new DataSet<Integer>(-1,new StringObject(""));
        DataModel<Integer> model = new DataModel<Integer>();
        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            DataSet<Integer> set = new DataSet<Integer>(methodDO.getId(),new StringObject(methodDO.getName()));
            model.add(set);
        }
        return model;
    }

    private void setRpcErrors(List exceptionList, AuxiliaryForm form) {
        TableField auxFieldTable = form.auxFieldTable;
        TableField auxFieldValueTable = form.auxFieldValueTable;        

        // we need to get the keys and look them up in the resource bundle for
        // internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                FieldErrorException ferrex = (FieldErrorException)exceptionList.get(i);

                if (ferrex.getFieldName()
                          .startsWith(AuxFieldMetaMap.getTableName() + ":")) {
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                       .substring(AuxFieldMetaMap.getTableName()
                                                                                                                 .length() + 1);
                   int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                   auxFieldTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));                                        
                } else if (ferrex.getFieldName()
                                 .startsWith(AuxFieldValueMetaMap.getTableName() + ":")) {
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName()
                                                                                       .substring(AuxFieldValueMetaMap.getTableName()
                                                                                                                         .length() + 1);
                    int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                    auxFieldValueTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                } 
            } else if (exceptionList.get(i) instanceof FieldErrorException) {
                form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName())
                .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));   
            }
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        form.status = Form.Status.invalid;
        
    }

}
