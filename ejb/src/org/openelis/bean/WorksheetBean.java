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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.entity.Worksheet;
import org.openelis.entity.WorksheetItem;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.WorksheetLocal;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.remote.WorksheetRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetBean implements WorksheetRemote, WorksheetLocal {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
    @EJB private LockLocal lockBean;
    
    private static int worksheetRefTableId;
    private static final WorksheetMetaMap WorksheetMeta = new WorksheetMetaMap();
    
    public WorksheetBean(){
        worksheetRefTableId = ReferenceTableCache.getReferenceTable("worksheet");
    }
    
	public WorksheetViewDO fetchById(Integer worksheetId) throws Exception {		
		Query query = manager.createNamedQuery("Worksheet.Worksheet");
		query.setParameter("id", worksheetId);
		WorksheetViewDO worksheetDO = (WorksheetViewDO) query.getSingleResult();

        return worksheetDO;
	}
	
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
/*
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();

        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + meta.getId() + ", " +
                          meta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(meta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
*/
            throw new LastPageException();

//        return (ArrayList<IdNameVO>)list;
    }

	public void add(WorksheetViewDO worksheetDO){
/*	    
         manager.setFlushMode(FlushModeType.COMMIT);
         Worksheet worksheet = new Worksheet();
        
        //send the address to the update address bean
        addressBean.add(worksheetDO.getAddressDO());
        
        //update worksheet
        worksheet.setAddressId(worksheetDO.getAddressDO().getId());
        
        worksheet.setIsActive(worksheetDO.getIsActive());
        worksheet.setName(worksheetDO.getName());
        worksheet.setParentWorksheetId(worksheetDO.getParentWorksheetId());
                
        manager.persist(worksheet);
        
        worksheetDO.setId(worksheet.getId());
*/
    }
    
    public void update(WorksheetViewDO worksheetDO) throws Exception {
/*
        lockBean.validateLock(worksheetRefTableId, worksheetDO.getId());
        
         manager.setFlushMode(FlushModeType.COMMIT);
         Worksheet worksheet = manager.find(Worksheet.class, worksheetDO.getId());

        //send the address to the update address bean
        addressBean.update(worksheetDO.getAddressDO());
        
        //update worksheet
        worksheet.setIsActive(worksheetDO.getIsActive());
        worksheet.setName(worksheetDO.getName());
        worksheet.setParentWorksheetId(worksheetDO.getParentWorksheetId());
        
        lockBean.giveUpLock(worksheetRefTableId, worksheet.getId());
*/                                  
    }

    public void addItem(WorksheetItemDO itemDO) throws Exception{
/*    
        System.out.println("inside add contact!!");
        manager.setFlushMode(FlushModeType.COMMIT);
        
        WorksheetContact orgContact = new WorksheetContact();
    
        //send the contact address to the address bean
        addressBean.add(contactDO.getAddressDO());
            
        orgContact.setContactTypeId(contactDO.getContactTypeId());
        orgContact.setName(contactDO.getName());
        orgContact.setWorksheetId(contactDO.getWorksheetId());
        orgContact.setAddressId(contactDO.getAddressDO().getId());
        
        manager.persist(orgContact);
        contactDO.setId(orgContact.getId());
*/
    }

    public void updateItem(WorksheetItemDO itemDO) throws Exception{
/*
        System.out.println("inside org bean updateContact");
        manager.setFlushMode(FlushModeType.COMMIT);
        
        WorksheetContact orgContact = manager.find(WorksheetContact.class, contactDO.getId());

        //send the contact address to the address bean
        addressBean.update(contactDO.getAddressDO());
            
        orgContact.setContactTypeId(contactDO.getContactTypeId());
        orgContact.setName(contactDO.getName());
        orgContact.setWorksheetId(contactDO.getWorksheetId());
        orgContact.setAddressId(contactDO.getAddressDO().getId());
*/        
    }
    
	public List<WorksheetItemDO> fetchItemsById(Integer worksheetId) throws Exception {
/*
	    Query query = manager.createNamedQuery("WorksheetContact.ContactsByOrgId");
		query.setParameter("id", worksheetId);
		
		List contactsList = query.getResultList();
		
		if(contactsList.size() == 0)
*/
		    throw new NotFoundException();
		
//        return contactsList;
	}
	
	public List autoCompleteLookupById(Integer id){
		Query query = null;
		query = manager.createNamedQuery("Worksheet.AutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
	}
	
	public void validateWorksheet(WorksheetViewDO worksheetDO, List contacts) throws Exception {
/*	    
	    ValidationErrorsList list = new ValidationErrorsList();
		//name required
		if(worksheetDO.getName() == null || "".equals(worksheetDO.getName())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.getName()));
		}
		
		//street address required
		if(worksheetDO.getAddressDO().getStreetAddress() == null || "".equals(worksheetDO.getAddressDO().getStreetAddress())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getStreetAddress()));
		}

		//city required
		if(worksheetDO.getAddressDO().getCity() == null || "".equals(worksheetDO.getAddressDO().getCity())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCity()));
		}

		//zipcode required
		if(worksheetDO.getAddressDO().getZipCode() == null || "".equals(worksheetDO.getAddressDO().getZipCode())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getZipCode()));
		}
		
		//country required
		if(worksheetDO.getAddressDO().getCountry() == null || "".equals(worksheetDO.getAddressDO().getCountry())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCountry()));
		}	
		
		for(int i=0; i<contacts.size();i++)       
            validateContactAndAddress((WorksheetContactDO)contacts.get(i), i, list);
		
		if(list.size() > 0)
            throw list;
*/
	}
/*	
	private void validateContactAndAddress(WorksheetContactDO orgContactDO, int rowIndex, ValidationErrorsList exceptionList){
		//contact type required
		if(orgContactDO.getContactTypeId() == null || "".equals(orgContactDO.getContactTypeId())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getContactTypeId(),"contactsTable"));
		}

		//name required
		if(orgContactDO.getName() == null || "".equals(orgContactDO.getName())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getName(),"contactsTable"));
		}
		
		//street address required
		if(orgContactDO.getAddressDO().getStreetAddress() == null || "".equals(orgContactDO.getAddressDO().getStreetAddress())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getStreetAddress(),"contactsTable"));
		}
		
		//city required
		if(orgContactDO.getAddressDO().getCity() == null || "".equals(orgContactDO.getAddressDO().getCity())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCity(),"contactsTable"));	
		}
		
		//zipcode required
		if(orgContactDO.getAddressDO().getZipCode() == null || "".equals(orgContactDO.getAddressDO().getZipCode())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getZipCode(),"contactsTable"));
		}
		
		//country required
		if(orgContactDO.getAddressDO().getCountry() == null || "".equals(orgContactDO.getAddressDO().getCountry())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCountry(),"contactsTable"));
		}		
	}
*/
}