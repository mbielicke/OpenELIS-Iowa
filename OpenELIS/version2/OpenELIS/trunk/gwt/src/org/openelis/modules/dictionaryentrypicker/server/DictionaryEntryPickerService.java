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
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerDataRPC;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;

import java.util.Iterator;
import java.util.List;

public class DictionaryEntryPickerService implements AppScreenFormServiceInt<DictionaryEntryPickerForm, Query<TableDataRow<Integer>>> {
    //private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
    //                                                              .getAttribute("locale"));

    public DictionaryEntryPickerForm abort(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerForm commitAdd(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerForm commitDelete(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerForm commitUpdate(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerForm fetch(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DictionaryEntryPickerForm fetchForUpdate(DictionaryEntryPickerForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DictionaryEntryPickerForm getScreen(DictionaryEntryPickerForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/dictionaryEntryPicker.xsl");
        rpc.categoryModel = getInitialModel();
        return rpc;
    }
    
    public DictionaryEntryPickerDataRPC getDictionaryEntries(DictionaryEntryPickerDataRPC rpc){
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer catValue = rpc.id;
        //if(catValue == -1)
          // catValue = null;         
        
        QueryStringField nameField = new QueryStringField();
        nameField.setValue(rpc.stringValue);
        
        List<IdNameDO> dictDOList =null;
       try{ 
          dictDOList = remote.getDictionaryListByPatternAndCategory(nameField, catValue);
       }catch(Exception ex) {
           ex.printStackTrace();
       }
        rpc.dictionaryTableModel.clear();
         for(int iter = 0; iter < dictDOList.size(); iter++){
             IdNameDO dictDO = dictDOList.get(iter);             
             TableDataRow<Integer> row = rpc.dictionaryTableModel.createNewSet();                         
             row.key = dictDO.getId();                          
             row.cells[0].setValue(dictDO.getName());
             rpc.dictionaryTableModel.add(row);
         }
        return rpc;
    }
    

    
    private TableDataModel<TableDataRow<Integer>> getInitialModel(){
       TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
       CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
       List<IdNameDO> list = remote.getCategoryList();
       IdNameDO methodDO = null;         
       TableDataRow<Integer> set = null;
       //TableDataRow<Integer> blankset = new TableDataRow<Integer>(-1,new StringObject(openElisConstants.getString("allCategories"))); 
       TableDataRow<Integer> blankset = new TableDataRow<Integer>(null,new StringObject(""));  
            
       model.add(blankset);

       for (Iterator iter = list.iterator(); iter.hasNext();) {
           methodDO = (IdNameDO)iter.next();
           set = new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName()));
           model.add(set);
       }
      
       return model;
    } 

}
