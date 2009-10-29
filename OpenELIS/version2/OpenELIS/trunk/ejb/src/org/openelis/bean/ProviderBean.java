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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Provider;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.ProviderLocal;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderBean implements ProviderRemote, ProviderLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
        
    private static final ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    public ProviderDO fetchById(Integer providerId) {                 
        Query query;
        ProviderDO provider;
        
        query = manager.createNamedQuery("Provider.FetchById");
        query.setParameter("id", providerId);
        provider = (ProviderDO) query.getSingleResult();// getting provider 

        return provider;
    }

    @SuppressWarnings("unchecked")
	public ArrayList<IdLastNameFirstNameDO> query (ArrayList<QueryData> fields, int first, int max) throws Exception {                         
       Query query;
       QueryBuilderV2 qb = new QueryBuilderV2();
       List list;
                
       qb.setMeta(ProvMeta);
        
       qb.setSelect("distinct new org.openelis.domain.IdLastNameFirstNameDO("+ProvMeta.getId()+", "+ProvMeta.getLastName()+", "+ProvMeta.getFirstName() + ") ");
       
       
       qb.constructWhere(fields);   
       
       qb.setOrderBy(ProvMeta.getLastName()+", "+ProvMeta.getFirstName());       
          
       query = manager.createQuery(qb.getEJBQL());
       
       query.setMaxResults(first+max);
       
       QueryBuilderV2.setQueryParams(query,fields);
       
       list = query.getResultList();
       if (list.isEmpty())
           throw new NotFoundException();
       list = (ArrayList<IdLastNameFirstNameDO>)DataBaseUtil.subList(list, first, max);
       if (list == null)
           throw new LastPageException();

       return (ArrayList<IdLastNameFirstNameDO>)list;
        
    }

    public ProviderDO add(ProviderDO data) throws Exception{
    	Provider entity;
                
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new Provider();
            
        entity.setFirstName(data.getFirstName());
        entity.setLastName(data.getLastName());
        entity.setMiddleName(data.getMiddleName());
        entity.setNpi(data.getNpi());
        entity.setTypeId(data.getTypeId());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        
        return data;
    }
    
    public ProviderDO update(ProviderDO data) throws Exception{
    	Provider entity;
        
        if(!data.isChanged())
        	return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Provider.class,data.getId());
            
        entity.setFirstName(data.getFirstName());
        entity.setLastName(data.getLastName());
        entity.setMiddleName(data.getMiddleName());
        entity.setNpi(data.getNpi());
        entity.setTypeId(data.getTypeId());
        
        return data;
    }


   public void validate(ProviderDO providerDO) throws ValidationErrorsList { 
	   ValidationErrorsList vl = new ValidationErrorsList();
       
	   if("".equals(providerDO.getLastName())){           
           vl.add(new FieldErrorException("fieldRequiredException",ProvMeta.getLastName()));
       }
       
	   if(providerDO.getTypeId()==null){           
           vl.add(new FieldErrorException("fieldRequiredException",ProvMeta.getTypeId()));
       }
	   
	   if(vl.size() > 0)
		   throw vl;
   }
   
}
