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
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerDataRPC;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DictionaryEntryPickerService implements AppScreenFormServiceInt<DictionaryEntryPickerRPC, Integer> {
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                  .getAttribute("locale"));

    public DictionaryEntryPickerRPC abort(DictionaryEntryPickerRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerRPC commitAdd(DictionaryEntryPickerRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerRPC commitDelete(DictionaryEntryPickerRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerRPC commitUpdate(DictionaryEntryPickerRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerRPC fetch(DictionaryEntryPickerRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerRPC fetchForUpdate(DictionaryEntryPickerRPC rpc) throws RPCException {
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
    
    public DictionaryEntryPickerRPC getScreen(DictionaryEntryPickerRPC rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/dictionaryEntryPicker.xsl");
        return rpc;
    }
    
    public DictionaryEntryPickerDataRPC getDictionaryEntries(DictionaryEntryPickerDataRPC rpc){
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer catValue = rpc.key;
        if(catValue == -1){
           catValue = null; 
        }
        
        QueryStringField nameField = new QueryStringField();
        nameField.setValue(rpc.stringValue);
        
        List<IdNameDO> dictDOList =null;
       try{ 
          dictDOList = remote.getDictionaryListByPatternAndCategory(nameField, catValue);
       }catch(Exception ex) {
           ex.printStackTrace();
       }
        rpc.model.clear();
         for(int iter = 0; iter < dictDOList.size(); iter++){
             IdNameDO dictDO = dictDOList.get(iter);             
             DataSet row = rpc.model.createNewSet();
             IntegerField id = new IntegerField(dictDO.getId());
             
             DataMap data = new DataMap();             
             data.put("id", id);             
             row.setData(data);
             
             ((Field)row.get(0)).setValue(dictDO.getName());
             rpc.model.add(row);
         }
        return rpc;
    }
    
    public DictionaryEntryPickerDataRPC getEntryId(DictionaryEntryPickerDataRPC rpc){
        CategoryRemote remote =  (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer entryId = null;
        
        try{
            entryId = remote.getEntryIdForSystemName(rpc.stringValue);
            rpc.key = entryId;
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return rpc;
    }
    
    public DictionaryEntryPickerDataRPC getInitialModel(DictionaryEntryPickerDataRPC rpc){
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
       rpc.model = model;

       return rpc;
    } 

}
