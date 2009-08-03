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
package org.openelis.modules.standardnotepicker.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.QueryIntegerField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.common.rewrite.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class EditNoteService {
    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    
    
    public Query<StandardNoteDO> query(Query<StandardNoteDO> query) throws RPCException {

        StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");

        try{    
            query.results = new ArrayList<StandardNoteDO>();
            ArrayList<StandardNoteDO> results = (ArrayList<StandardNoteDO>)remote.newQuery(query.fields);
            for(StandardNoteDO result : results) {
                query.results.add(result);
            }
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        return query;
    }
    
    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/editNote.xsl");      
    }
    
    /*
    public ArrayList<IdNameDO> getTreeModel(StandardNotePickerForm rpc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		nameField.setValue(rpc.queryString);
		descField.setValue(rpc.queryString);
		
		HashMap fields = new HashMap();
		fields.put(StandardNoteMeta.getName(), nameField);
		fields.put(StandardNoteMeta.getDescription(), descField);
		
		List standardNoteCategories = new ArrayList();
		try{
			standardNoteCategories = remote.queryForType(fields);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		return standardNoteCategories
	}
	*/

    public StandardNotePickerForm getTreeModelSecondLevel(StandardNotePickerForm rpc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		QueryIntegerField typeField = new QueryIntegerField();
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		typeField.setValue(String.valueOf(rpc.id));
		nameField.setValue(rpc.queryString);
		descField.setValue(rpc.queryString);
		HashMap<String,AbstractField> fields = new HashMap<String, AbstractField>();
		fields.put(StandardNoteMeta.getTypeId(), typeField);
		fields.put(StandardNoteMeta.getName(), nameField);
		fields.put(StandardNoteMeta.getDescription(), descField);
		List list = new ArrayList();
		try{
			list = remote.getStandardNoteByType(fields);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
	       StringObject returnString = new StringObject(); 
	     try {
	    	 Iterator itr = list.iterator();
	            TreeDataModel treeModel = new TreeDataModel();
	            while(itr.hasNext()){
	                StandardNoteDO standardNoteDO = (StandardNoteDO) itr.next();
	                
	                TreeDataItem treeModelItem = new TreeDataItem(1);
	                treeModelItem.leafType = "leaf";
	                treeModelItem.key = (rpc.id);
	                treeModelItem.setData(new StringObject(standardNoteDO.getText()));
	                treeModelItem.cells[0] = (new StringObject(standardNoteDO.getName()+" : "+standardNoteDO.getDescription()));
	                
	                
	                treeModel.add(treeModelItem);
	            }   
	            rpc.treeModel = treeModel;
	            return rpc;
		}catch(Exception e){
			return rpc;
		}
	}
}
