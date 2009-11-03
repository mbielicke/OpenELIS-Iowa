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
package org.openelis.modules.provider.server;

import java.util.ArrayList;

import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProviderAddressManager;
import org.openelis.manager.ProviderManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ProviderManagerRemote;
import org.openelis.remote.ProviderRemote;


public class ProviderService {
    
    private static final int rowPP = 18;

    public ProviderManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager fetchWithAddresses(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithAddresses(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithNotes(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdLastNameFirstNameDO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }

    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }


    //
    // support for ProviderContactManager and ProviderParameterManager
    //
    public ProviderAddressManager fetchAddressByProviderId(Integer id) throws Exception {
        try {
            return remoteManager().fetchAddressByProviderId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    private ProviderRemote remote() {
        return (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
    }

    private ProviderManagerRemote remoteManager() {
        return (ProviderManagerRemote)EJBFactory.lookup("openelis/ProviderManagerBean/remote");
    }
/*    
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {        
        List providers = new ArrayList();

            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");            
            
                try{
                    providers = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
                }catch(LastPageException e) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }              
             
            
            //fill the model with the query result
            int i=0;
            if(query.results == null)
                query.results = new TableDataModel<TableDataRow<Integer>>();
            else
                query.results.clear();
            while(i < providers.size() && i < leftTableRowsPerPage) {
        
                IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)providers.get(i);
        
                query.results.add(new TableDataRow<Integer>(resultDO.getId(),
                                                    new FieldType[] {
                                                                     new StringObject(resultDO.getLastName()),
                                                                     new StringObject(resultDO.getFirstName())
                                                    }
                          )
                );
                i++;
             }
        return query;
    }

    public ProviderForm commitAdd(ProviderForm rpc) throws Exception {               
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpc);
    
        NoteViewDO providerNote = new NoteViewDO();       
        
        TableDataModel<TableDataRow<Integer>> addressTable = rpc.addresses.providerAddressTable.getValue();
        Integer providerId = providerDO.getId();
                
        ArrayList<ProviderAddressDO> provAddDOList =  getProviderAddressListFromRPC(addressTable,providerId);
                                        
        //build the noteDO from the form
        providerNote.setSubject(rpc.notes.subject.getValue());
        providerNote.setText(rpc.notes.text.getValue());
        providerNote.setIsExternal("Y");
        
                         
        try{
            providerId = (Integer)remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
         
        providerDO.setId(providerId);
        
        setFieldsInRPC(rpc, providerDO); 
        
        String tab = rpc.provTabPanel;
        if(tab.equals("notesTab")){
            loadNotes(providerDO.getId(), rpc.notes);
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        rpc.notes.load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.notes.subject.setValue(null);
        rpc.notes.text.setValue(null);
        
        return rpc;
    }

    public ProviderForm commitUpdate(ProviderForm rpc) throws Exception {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpc);
    
        ArrayList<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
        NoteViewDO providerNote = new NoteViewDO();       
        
        TableDataModel<TableDataRow<Integer>> addressTable = rpc.addresses.providerAddressTable.getValue();
        if(rpc.addresses.load)        
         provAddDOList = getProviderAddressListFromRPC(addressTable,providerDO.getId());
         
        if(rpc.notes.load){
            NotesForm notesRPC = rpc.notes;  
            providerNote.setSubject(notesRPC.subject.getValue());
            providerNote.setText(notesRPC.text.getValue());
            providerNote.setIsExternal("Y");
        }       
                         
        try{
            remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }

        //set the fields in the RPC
        setFieldsInRPC(rpc, providerDO);
         
        String tab = rpc.provTabPanel;
        if(tab.equals("notesTab")){
            loadNotes(providerDO.getId(), rpc.notes);
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        rpc.notes.load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.notes.subject.setValue(null);
        rpc.notes.text.setValue(null);
        
        return rpc;
    }

    public ProviderForm commitDelete(ProviderForm rpcReturn) throws Exception {
        return null;
    }

    public ProviderForm abort(ProviderForm rpc) throws Exception {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        Integer providerId = rpc.entityKey;
        String tab = rpc.provTabPanel;

        ProviderDO provDO = new ProviderDO();
        try {
            provDO = (ProviderDO)remote.getProviderAndUnlock(providerId,
                                                             SessionManager.getSession()
                                                                           .getId());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        // set the fields in the RPC
        setFieldsInRPC(rpc, provDO);

        if ("addressesTab".equals(tab))
            loadAddressesModel(rpc.entityKey,
                               rpc.addresses.providerAddressTable);
        else if ("notesTab".equals(tab))
            rpc.notes.notesPanel.setValue(getNotesModel(rpc.entityKey));

        return rpc;
           
    }

    public ProviderForm fetch(ProviderForm rpc) throws Exception {
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = rpc.entityKey;
                    
            ProviderDO provDO = (ProviderDO)remote.getProvider(providerId);        
            // set the fields in the RPC
            setFieldsInRPC(rpc, provDO);
             
            String tab = rpc.provTabPanel;
            if(tab.equals("addressesTab")){
                loadAddresses(rpc.entityKey,rpc.addresses);
            }
           
            if(tab.equals("notesTab")){
                loadNotes(rpc.entityKey, rpc.notes);
            }
            return rpc;
        }   
    
    public AddressesForm loadAddresses(AddressesForm rpc) throws Exception {
        AddressesForm form = (AddressesForm) rpc;
        loadAddressesModel(rpc.entityKey, form.providerAddressTable);
        form.load = true;
        return rpc;
    }
    
    public NotesForm loadNotes(NotesForm rpc) throws Exception {
        StringObject so = getNotesModel(rpc.entityKey);
        NotesForm form = (NotesForm)rpc; 
        form.notesPanel.setValue(so.getValue());
        form.load = true;
        return rpc;
    }

    public ProviderForm fetchForUpdate(ProviderForm rpc) throws Exception {
       ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
       Integer providerId = rpc.entityKey;
                      
       ProviderDO provDO = new ProviderDO();
        try{
         provDO =  (ProviderDO)remote.getProviderAndLock(providerId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }  
             
        // set the fields in the RPC
        setFieldsInRPC(rpc, provDO);              

        String tab = rpc.provTabPanel;
        if(tab.equals("addressesTab")){
            loadAddresses(rpc.entityKey,rpc.addresses);
       }
            
        if(tab.equals("notesTab")){
            loadNotes(rpc.entityKey, rpc.notes);
        }
                                                          
         return rpc;
     }
    
    public ProviderForm getScreen(ProviderForm rpc) throws Exception{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl");
        
        return rpc;
    }
    
    public TableDataModel<TableDataRow<Integer>> fillAddressTable(TableDataModel<TableDataRow<Integer>> addressModel, List contactsList){       
        try{
            addressModel.clear();
            
            for(int iter = 0;iter < contactsList.size();iter++) {
                ProviderAddressDO addressRow = (ProviderAddressDO)contactsList.get(iter);
                   TableDataRow<Integer> row = addressModel.createNewSet();                   
                    row.key = addressRow.getId();                  
                    row.setData(new IntegerObject(addressRow.getAddressDO().getId()));                   
                    row.getCells().get(0).setValue(addressRow.getLocation());
                    row.getCells().get(1).setValue(addressRow.getExternalId());
                    row.getCells().get(2).setValue(addressRow.getAddressDO().getMultipleUnit());
                    row.getCells().get(3).setValue(addressRow.getAddressDO().getStreetAddress());
                    row.getCells().get(4).setValue(addressRow.getAddressDO().getCity());     
                    if(addressRow.getAddressDO().getState()!=null){
                     ((DropDownField<String>)row.getCells().get(5)).setValue(new TableDataRow<String>(addressRow.getAddressDO().getState()));
                    }else{
                      ((DropDownField<String>)row.getCells().get(5)).setValue(new TableDataRow<String>(""));  
                    } 
                    if(addressRow.getAddressDO().getCountry()!=null){                    
                      ((DropDownField<String>)row.getCells().get(6)).setValue(new TableDataRow<String>(addressRow.getAddressDO().getCountry()));
                    }else{
                        ((DropDownField<String>)row.getCells().get(6)).setValue(new TableDataRow<String>(""));  
                    }                    
                                        
                    row.getCells().get(7).setValue(addressRow.getAddressDO().getZipCode());
                    row.getCells().get(8).setValue(addressRow.getAddressDO().getWorkPhone());
                    row.getCells().get(9).setValue(addressRow.getAddressDO().getHomePhone());
                    row.getCells().get(10).setValue(addressRow.getAddressDO().getCellPhone());
                    row.getCells().get(11).setValue(addressRow.getAddressDO().getFaxPhone());
                    row.getCells().get(12).setValue(addressRow.getAddressDO().getEmail());
                                                            
                    addressModel.add(row);
           } 
            
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }       
        
        return addressModel;  
    }

    

    public TableDataModel getDisplay(String cat, TableDataModel model, AbstractField value) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public void loadAddressesModel(Integer orgId,TableField model){
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        List contactsList = remote.getProviderAddresses(orgId);
        fillAddressTable(model.getValue(),contactsList);
    }
    
    public StringObject getNotesModel(Integer key){
//      remote interface to call the provider bean
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");

        //gets the whole notes list now
        List notesList = remote.getProviderNotes(key);
        
        Iterator itr = notesList.iterator();
        try{
        Document doc = XMLUtil.createNew("panel");
        Element root = (Element) doc.getDocumentElement();
        root.setAttribute("key", "notePanel");   
        int i=0;
        while(itr.hasNext()){           
            NoteViewDO noteRow = (NoteViewDO)itr.next();
            
            //user id
            String userName = noteRow.getSystemUser();
            //body
            String body = noteRow.getText();
            
            if(body == null)
                body = "";
            
            //date
       //     String date = noteRow.getTimestamp().toString();
            //subject
            String subject = noteRow.getSubject();
            
            if(subject == null)
                subject = "";                        
            
            Element mainRowPanel = (Element) doc.createElement("VerticalPanel");
            Element topRowPanel = (Element) doc.createElement("HorizontalPanel");
            Element titleWidgetTag = (Element) doc.createElement("widget");
            Element titleText = (Element) doc.createElement("text");
            Element authorWidgetTag = (Element) doc.createElement("widget");
            Element authorPanel = (Element) doc.createElement("VerticalPanel");
            Element dateText = (Element) doc.createElement("text");
            Element authorText = (Element) doc.createElement("text");
            Element bodyWidgetTag = (Element) doc.createElement("widget");
            Element bodytextTag =  (Element) doc.createElement("text");
             
             mainRowPanel.setAttribute("key", "note"+i);
             if(i % 2 == 1){
                 mainRowPanel.setAttribute("style", "AltTableRow");
             }else{
                 mainRowPanel.setAttribute("style", "TableRow");
             }
             mainRowPanel.setAttribute("width", "530px");

             topRowPanel.setAttribute("width", "530px");
             titleText.setAttribute("key", "note"+i+"Title");
             titleText.setAttribute("style", "notesSubjectText");
             titleText.appendChild(doc.createTextNode(subject));
             authorWidgetTag.setAttribute("halign", "right");
             dateText.setAttribute("key", "note"+i+"Date");
           //  dateText.appendChild(doc.createTextNode(date));
             authorText.setAttribute("key", "note"+i+"Author");
             authorText.appendChild(doc.createTextNode("by "+userName));
             bodytextTag.setAttribute("key", "note"+i+"Body");
             bodytextTag.setAttribute("wordwrap", "true");
             bodytextTag.appendChild(doc.createTextNode(body));
             
             root.appendChild(mainRowPanel);
             mainRowPanel.appendChild(topRowPanel);
             mainRowPanel.appendChild(bodyWidgetTag);
             topRowPanel.appendChild(titleWidgetTag);
             topRowPanel.appendChild(authorWidgetTag);
             titleWidgetTag.appendChild(titleText);
             authorWidgetTag.appendChild(authorPanel);
             authorPanel.appendChild(dateText);
             authorPanel.appendChild(authorText);
             bodyWidgetTag.appendChild(bodytextTag);
          
          i++;
      }

        StringObject returnObject = new StringObject();
        returnObject.setValue(XMLUtil.toString(doc));
        
        return returnObject;
        
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;        
    }

    public TableField getAddressModel(IntegerObject providerId,TableField model){
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        List addressList = remote.getProviderAddresses(providerId.getValue());
        model.setValue(fillAddressTable(model.getValue(),addressList));
        return model;
    }
    
    private AddressesForm loadAddresses(Integer key, AddressesForm form) throws Exception {
        loadAddressesModel(key, form.providerAddressTable);
        form.load = true;
        return form;
    }
    
    private NotesForm loadNotes(Integer key, NotesForm form) throws Exception {
        StringObject so = getNotesModel(key);
        form.notesPanel.setValue(so.getValue());
        form.load = true;
        return form;
    }
        
    private void setFieldsInRPC(ProviderForm form, ProviderDO provDO){
        form.id.setValue(provDO.getId());
        form.lastName.setValue(provDO.getLastName());
        form.firstName.setValue(provDO.getFirstName());
        form.npi.setValue(provDO.getNpi());        
        form.middleName.setValue(provDO.getMiddleName());              
        form.typeId.setValue(new TableDataRow<Integer>(provDO.getTypeId()));         
    }
    
    private ProviderDO getProviderDOFromRPC(ProviderForm form){
        ProviderDO providerDO = new ProviderDO();
        //provider info        
        providerDO.setId(form.id.getValue());
        providerDO.setFirstName(form.firstName.getValue());
        providerDO.setLastName(form.lastName.getValue());
        providerDO.setMiddleName(form.middleName.getValue());
        providerDO.setNpi(form.npi.getValue());     
        providerDO.setTypeId((Integer)(form.typeId.getSelectedKey()));
     
        return providerDO;
    }
    
    
    private ArrayList<ProviderAddressDO> getProviderAddressListFromRPC(TableDataModel<TableDataRow<Integer>> addressTable, Integer providerId){
        ArrayList<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
       
        IntegerObject addId = null;
           for(int iter = 0; iter < addressTable.size(); iter++){            
            ProviderAddressDO provAddDO = new ProviderAddressDO();
            TableDataRow<Integer> row = addressTable.get(iter);            
            provAddDO.setId(row.key);            
            
            addId = (IntegerObject)row.getData();
                        
            if(addId != null)
                provAddDO.getAddressDO().setId(addId.getValue());
           
            provAddDO.setLocation((String)((StringField)row.getCells().get(0)).getValue());
            provAddDO.setExternalId((String)((StringField)row.getCells().get(1)).getValue());
            provAddDO.setProviderId((Integer)providerId);
           
 
            //provAddDO.setDelete(false);

            provAddDO.getAddressDO().setMultipleUnit(((String)((StringField)row.getCells().get(2)).getValue()));
            provAddDO.getAddressDO().setStreetAddress(((String)((StringField)row.getCells().get(3)).getValue()));
            provAddDO.getAddressDO().setCity(((String)((StringField)row.getCells().get(4)).getValue()));
            provAddDO.getAddressDO().setState((String)((DropDownField)row.getCells().get(5)).getSelectedKey());
            provAddDO.getAddressDO().setCountry((String)((DropDownField)row.getCells().get(6)).getSelectedKey());
            provAddDO.getAddressDO().setZipCode(((String)((StringField)row.getCells().get(7)).getValue()));
            provAddDO.getAddressDO().setWorkPhone(((String)((StringField)row.getCells().get(8)).getValue()));
            provAddDO.getAddressDO().setHomePhone(((String)((StringField)row.getCells().get(9)).getValue()));
            provAddDO.getAddressDO().setCellPhone(((String)((StringField)row.getCells().get(10)).getValue()));
            provAddDO.getAddressDO().setFaxPhone(((String)((StringField)row.getCells().get(11)).getValue()));
            provAddDO.getAddressDO().setEmail(((String)((StringField)row.getCells().get(12)).getValue()));
            
            provAddDOList.add(provAddDO);                                    
        }
          if(addressTable.getDeletions()!=null) {
           for(int iter = 0; iter < addressTable.getDeletions().size(); iter++){            
               ProviderAddressDO provAddDO = new ProviderAddressDO();
               TableDataRow<Integer> row = (TableDataRow<Integer>)addressTable.getDeletions().get(iter);
               provAddDO.setId(row.key);            
               
               addId = (IntegerObject)row.getData();
                           
               if(addId != null)
                 provAddDO.getAddressDO().setId(addId.getValue());
               
               provAddDO.setLocation((String)((StringField)row.getCells().get(0)).getValue());
               provAddDO.setExternalId((String)((StringField)row.getCells().get(1)).getValue());
               provAddDO.setProviderId((Integer)providerId);              
               //provAddDO.setDelete(true);                          
               provAddDOList.add(provAddDO);   
              }
           addressTable.getDeletions().clear();
          }     
       return provAddDOList;    
    }

    private void setRpcErrors(List exceptionList, ProviderForm form){
        TableField contactsTable = form.addresses.providerAddressTable;
        HashMap<String,AbstractField> map = null;
        int index;
        String fieldName, error;
        TableFieldErrorException exc;
        AbstractField field;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                exc = (TableFieldErrorException)exceptionList.get(i);
                index =  exc.getRowIndex();
                fieldName = exc.getFieldName();
                error = openElisConstants.getString(exc.getMessage());
                field = contactsTable.getField(index, fieldName);
                if(!field.getErrors().contains(error))
                    field.addError(error);
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException) {
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            }
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException) {
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }
        }   
        form.status = Form.Status.invalid;
    }
*/
}
