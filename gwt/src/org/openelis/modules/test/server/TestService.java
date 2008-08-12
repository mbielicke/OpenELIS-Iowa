package org.openelis.modules.test.server;


import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.gwt.widget.pagedtree.TreeModelItem;

import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestService implements AppScreenFormServiceInt {

    private static final int leftTableRowsPerPage = 20;
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List testNames;
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("TestQuery");
    
            if(rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
            try{
                testNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            //fields.remove("contactsTable");

            try{    
                testNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("TestQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        while(i < testNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)testNames.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());
            
            row.setKey(id);         
            row.addObject(name);
            model.add(row);
            i++;
        } 
 
        return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl"); 
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl"));
        HashMap map = new HashMap();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public PagedTreeField getTreeModel() throws RPCException{
        //StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
        PagedTreeField returnTree = new PagedTreeField();
        TreeModel treeModel = new TreeModel();
                       
        int i=0;
        while(i<5){
            //Object[] result = (Object[])catItr.next();
            //standard note category id
            //Integer idParam = (Integer)result[0];
            Integer idParam = (Integer)i;
            //standard note category name
            //String nameParam = (String)result[1];
            String nameParam = "Group"+ new Integer(i+1).toString();
            
            TreeModelItem treeModelItem = new TreeModelItem();
            treeModelItem.setText(nameParam);
            treeModelItem.setUserObject(String.valueOf(idParam));
            treeModelItem.setHasDummyChild(true);
            treeModel.addItem(treeModelItem);
            
            i++;
        }   
        returnTree.setValue(treeModel);
        return returnTree;
    }
    
    public DataObject getAnalyte(NumberObject groupIdObj){
        TableField field  = new TableField();
        TableModel model = new TableModel();
        TableRow trow = new TableRow();
        StringField value = new StringField("Analyte1");
        DropDownField scriptlet = new DropDownField();
        CheckField reportable = new CheckField();
        reportable.setValue("Y");
        DropDownField type = new DropDownField();
        trow.addColumn(value);     
        trow.addColumn(scriptlet);
        trow.addColumn(reportable);        
        trow.addColumn(type);
        model.addRow(trow);
        field.setValue(model);        
        return field; 
    }

}
