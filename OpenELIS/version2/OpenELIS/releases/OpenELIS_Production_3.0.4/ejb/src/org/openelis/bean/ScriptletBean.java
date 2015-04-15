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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.entity.Scriptlet;
import org.openelis.meta.ScriptletMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class ScriptletBean  {

    @PersistenceContext(unitName = "openelis")
    protected EntityManager           manager;

    @EJB
    protected LockBean               lock;
    
    @EJB
    protected UserCacheBean           userCache;
    
    private static final ScriptletMeta meta = new ScriptletMeta();
    
    @SuppressWarnings("unchecked")
	public ArrayList<IdNameVO> fetchByName(String match, int maxResults) {
        Query query;
        
        query = manager.createNamedQuery("Scriptlet.FetchByName");
        query.setParameter("name", match);
        query.setMaxResults(maxResults);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ScriptletDO fetchById(Integer id) throws Exception {
        Query  query;
        
        query = manager.createNamedQuery("Scriptlet.FetchById");
        query.setParameter("id",id);
        try {
            return (ScriptletDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<ScriptletDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("Scriptlet.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          ScriptletMeta.getId() + "," + ScriptletMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ScriptletMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public ScriptletDO add(ScriptletDO data) throws Exception {
        Scriptlet entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Scriptlet();
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setBean(data.getBean());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;

    }

    public ScriptletDO update(ScriptletDO data) throws Exception {
        Scriptlet entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().SCRIPTLET, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(Constants.table().SCRIPTLET, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Scriptlet.class, data.getId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setBean(data.getBean());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());

        lock.unlock(Constants.table().SCRIPTLET, data.getId());

        return data;
    }

    public ScriptletDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().SCRIPTLET, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public ScriptletDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().SCRIPTLET, id);
        return fetchById(id);
    }

    public void validate(ScriptletDO data) throws Exception {
        ValidationErrorsList list;
        Object bean;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) 
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ScriptletMeta.getName()));


        if (DataBaseUtil.isEmpty(data.getBean()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ScriptletMeta.getName()));
        else {
            
            bean = EJBFactory.lookup(data.getBean());
           
            if(bean == null) 
               list.add(new FieldErrorException(Messages.get().invalidBeanPath(), ScriptletMeta.getBean()));
           
       }        
        
        if (DataBaseUtil.isEmpty(data.getActiveBegin()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ScriptletMeta.getActiveBegin()));

        if (DataBaseUtil.isEmpty(data.getActiveEnd()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ScriptletMeta.getActiveEnd()));

        if (DataBaseUtil.isAfter(data.getActiveBegin(), data.getActiveEnd()))
            list.add(new FormErrorException(Messages.get().endDateAfterBeginDateException()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("scriptlet", flag);
    }
}
