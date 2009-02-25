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

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DictionaryEntryPickerService implements AppScreenFormServiceInt<DefaultRPC, Integer> {
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                  .getAttribute("locale"));

    public DefaultRPC abort(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitAdd(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitDelete(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC commitUpdate(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetch(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetchForUpdate(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionaryEntryPicker.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/dictionaryEntryPicker.xsl"));
        
        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }
    
    public DefaultRPC getScreen(DefaultRPC rpc){
        return rpc;
    }
    
    public DataModel getDictionaryEntries(DataModel model, NumberObject categoryId,StringObject pattern){
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer catValue = categoryId.getIntegerValue();
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
             DataSet<Data> row = model.createNewSet();
             NumberField id = new NumberField(dictDO.getId());
             
             DataMap data = new DataMap();             
             data.put("id", id);             
             row.setData(data);
             
             ((Field)row.get(0)).setValue(dictDO.getName());
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
       DataModel<Integer> model = new DataModel<Integer>();
       CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
       List<IdNameDO> list = remote.getCategoryList();
       IdNameDO methodDO = null;
       StringObject textObject = null;           
       DataSet<Integer> set = null;
       DataSet<Integer> blankset = new DataSet<Integer>();  
       blankset.add(new StringObject(openElisConstants.getString("allCategories")));
       blankset.setKey(-1);
            
       model.add(blankset);

       for (Iterator iter = list.iterator(); iter.hasNext();) {
           methodDO = (IdNameDO)iter.next();
           set = new DataSet<Integer>();
           textObject = new StringObject(methodDO.getName());
           set.add(textObject);
           set.setKey(methodDO.getId());
           model.add(set);
       }

       return model;
    } 

}
