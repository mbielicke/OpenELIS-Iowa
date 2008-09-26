/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.bean;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.remote.PanelRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
public class PanelBean implements PanelRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final PanelMetaMap PanelMeta = new PanelMetaMap(); 
    

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        
    }
    
    public PanelDO getPanel(Integer panelId) {
        // TODO Auto-generated method stub
        return null;
    }

    public PanelDO getPanelAndLock(Integer panelId, String session) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public PanelDO getPanelAndUnlock(Integer panelId, String session) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<PanelItemDO> getPanelItems(Integer panelId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getTestMethodNames() {
        Query query = manager.createNamedQuery("Test.Names");
        List testMethodList = query.getResultList();
        return testMethodList;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(PanelMeta);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + PanelMeta.getId()
                     + ", "
                     + PanelMeta.getName()                     
                     + ") ");
        
        qb.addWhere(fields);

        qb.setOrderBy(PanelMeta.getName());
        
        sb.append(qb.getEJBQL());                

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }

    public Integer updatePanel(PanelDO panelDO,
                               List<PanelItemDO> panelItemDOList) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public List validateForAdd(PanelDO panelDO,
                               List<PanelItemDO> panelItemDOList) {
        // TODO Auto-generated method stub
        return null;
    }

    public List validateForUpdate(PanelDO panelDO,
                                  List<PanelItemDO> panelItemDOList) {
        // TODO Auto-generated method stub
        return null;
    }

}
