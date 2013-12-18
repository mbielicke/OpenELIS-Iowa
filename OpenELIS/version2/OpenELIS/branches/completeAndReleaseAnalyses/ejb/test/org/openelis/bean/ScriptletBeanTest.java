package org.openelis.bean;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.*;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.easymock.EasyMock;
import org.openelis.domain.Constants;
import org.openelis.domain.ScriptletDO;
import org.openelis.entity.Scriptlet;
import org.openelis.utils.EJBFactory;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ScriptletBeanTest {
   
    UserCacheBean userCache;
    EntityManager manager;
    ScriptletBean test;
    LockBean      lock;
    
    @BeforeMethod
    public void setMocks() throws Exception {
        
        // Setup UserCache Mock
        userCache = createMock(UserCacheBean.class);
        userCache.applyPermission("scriptlet", ModuleFlags.ADD);
        expect(userCache.getId()).andReturn(new Integer(1));
        
        Constants.setConstants(new Constants());
        
        lock = createMock(LockBean.class);
        lock.unlock(Constants.table().SCRIPTLET, 1);
        lock.validateLock(Constants.table().SCRIPTLET, 1);
  
            
        manager = createMock(EntityManager.class);
 
        test = new ScriptletBean();
        
        test.manager = manager;
        test.userCache = userCache;
       
    }
    
    @Test
    public void add() {
        ScriptletDO data;
        
        data = new ScriptletDO(1, "test", "test", "Y", new Date("2013/11/13"), new Date("2099/12/31"));
        manager.persist((Scriptlet)EasyMock.anyObject());
        expect(EJBFactory.lookup("test")).andReturn(new Object());
        
        replay(userCache,manager);
        
        try {
            test.add(data);
        }catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        
        
       
      
    }
}
