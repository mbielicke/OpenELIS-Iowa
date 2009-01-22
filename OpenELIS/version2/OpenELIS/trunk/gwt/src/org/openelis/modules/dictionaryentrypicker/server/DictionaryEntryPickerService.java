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
package org.openelis.modules.dictionaryentrypicker.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class DictionaryEntryPickerService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                  .getAttribute("locale"));

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Data commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Data commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Data fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionaryEntryPicker.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/dictionaryEntryPicker.xsl"));
        
        HashMap<String, Data> map = new HashMap<String, Data>();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        return null;
    }
    
    public DataModel getDictionaryEntries(DataModel model, NumberObject categoryId,StringObject pattern){
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer catValue = (Integer)categoryId.getValue();
        if(catValue == -1){
           catValue = null; 
        }
        
        QueryStringField nameField = new QueryStringField();
        nameField.setValue(pattern.getValue());
        
        List<IdNameDO> dictDOList =null;
       try{ 
          dictDOList = remote.getDictionaryListByPatternAndCategory(nameField, catValue);
       }catch(Exception ex) {
           ex.printStackTrace();
       }
        model.clear();
         for(int iter = 0; iter < dictDOList.size(); iter++){
             IdNameDO dictDO = dictDOList.get(iter);             
             DataSet row = model.createNewSet();
             NumberField id = new NumberField(dictDO.getId());
             
             DataMap data = new DataMap();             
             data.put("id", id);             
             row.setData(data);
             
             row.get(0).setValue(dictDO.getName());
             model.add(row);
         }
        return model;
    }
    
    public NumberObject getEntryId(StringObject entry){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer entryId = null;
        NumberObject numObj = null;
        try{
            entryId = remote.getEntryIdForSystemName((String)entry.getValue());
            numObj = new NumberObject(entryId);
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return numObj;
    }
    
    public DataModel getInitialModel(){
       DataModel model = new DataModel();
       CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
       List<IdNameDO> list = remote.getCategoryList();
       IdNameDO methodDO = null;
       StringObject textObject = null;
       NumberObject numberId = null;                
       DataSet set = null;
       DataSet blankset = new DataSet();  
       blankset.add(new StringObject(openElisConstants.getString("allCategories")));
       blankset.setKey(new NumberObject(-1));
            
       model.add(blankset);

       for (Iterator iter = list.iterator(); iter.hasNext();) {
           methodDO = (IdNameDO)iter.next();
           set = new DataSet();
           textObject = new StringObject(methodDO.getName());
           numberId = new NumberObject(methodDO.getId());
           set.add(textObject);
           set.setKey(numberId);
           model.add(set);
       }

       return model;
    } 

}
