package org.openelis.bean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.entity.Method;
import org.openelis.meta.MethodMeta;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.common.data.QueryData.Type;


@RunWith(MockitoJUnitRunner.class)
public class TestMethodBean {
    
    MethodBean bean;
   
    OpenELISConstants constants;
    
    @Before
    public void init() {
        Constants.setConstants(new Constants());
        
        LocaleProxy.initialize();
        constants = LocaleFactory.get(OpenELISConstants.class, "en"); 
       
        bean = new MethodBean() {
            protected OpenELISConstants getMessages() {
                return constants;
            }
        };
        
        bean.manager = mock(EntityManager.class);
        bean.lock = mock(LockBean.class);
        bean.userCache = mock(UserCacheBean.class);
    }
    
    @Test
    public void testFetchById() {
        Query query = mock(Query.class);
        MethodDO data = mock(MethodDO.class);
        
        when(bean.manager.createNamedQuery("Method.FetchById")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(data);
        
        try {
            assertEquals(data,bean.fetchById(1));
        }catch(Exception e) {
            fail();
        }
    }
    
    @Test(expected=NotFoundException.class)
    public void testFetchByIdNotFound() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchById")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        bean.fetchById(1);
    }
    
    @Test(expected=DatabaseException.class)
    public void testFetchByIdDatabaseException() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchById")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException());
        
        bean.fetchById(1);
    }
    
    @Test
    public void testFetchByName() {
        Query query = mock(Query.class);
        MethodDO data = mock(MethodDO.class);
        
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(data);
        
        try {
            assertEquals(data,bean.fetchByName("name"));
        }catch(Exception e) {
            fail();
        }
    }
    
    @Test(expected=NotFoundException.class)
    public void testFetchByNameNotFound() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        bean.fetchByName("name");
    }
    
    @Test(expected=DatabaseException.class)
    public void testFetchByNameDatabaseException() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException());
        
        bean.fetchByName("name");
    }
    
    @Test
    public void testFetchActiveByName() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchActiveByName")).thenReturn(query);
        
        List<MethodDO> results = new ArrayList<MethodDO>();
        results.add(mock(MethodDO.class));
        
        when(query.getResultList()).thenReturn(results);
        
        assertEquals(results,bean.fetchActiveByName("name", 10));
    }
    
    @Test
    public void testFetchActiveByNameNoResult() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchActiveByName")).thenReturn(query);
                
        when(query.getResultList()).thenReturn((List<MethodDO>)new ArrayList<MethodDO>());
        
        assertTrue(bean.fetchActiveByName("name", 10).isEmpty());
    }
    
    @Test
    public void testFetchByIds() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByIds")).thenReturn(query);
        
        List<MethodDO> results = new ArrayList<MethodDO>();
        results.add(mock(MethodDO.class));
        
        when(query.getResultList()).thenReturn(results);
        
        assertEquals(results,bean.fetchByIds(mock(Collection.class)));
    }
    
    @Test
    public void testFetchByIdsNoResult() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByIds")).thenReturn(query);
                
        when(query.getResultList()).thenReturn((List<MethodDO>)new ArrayList<MethodDO>());
        
        assertTrue(bean.fetchByIds(mock(Collection.class)).isEmpty());
    }
    
    @Test
    public void testQuery() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createQuery(anyString())).thenReturn(query);
        ArrayList<IdNameVO> results = new ArrayList<IdNameVO>();
        results.add(mock(IdNameVO.class));
        when(query.getResultList()).thenReturn(results);
        
        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(MethodMeta.getName());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);
        
        assertEquals(results,bean.query(fields, 0, 10));
    }
    
    @Test(expected=NotFoundException.class)
    public void testQueryNotFound() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createQuery(anyString())).thenReturn(query);
        ArrayList<IdNameVO> results = new ArrayList<IdNameVO>();
        
        when(query.getResultList()).thenReturn(results);
        
        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(MethodMeta.getName());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);
        
        bean.query(fields, 0, 10);
    }
    
    @Test(expected=LastPageException.class)
    public void testQueryLastPage() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createQuery(anyString())).thenReturn(query);
        ArrayList<IdNameVO> results = new ArrayList<IdNameVO>();
        results.add(mock(IdNameVO.class));
        when(query.getResultList()).thenReturn(results);
        
        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(MethodMeta.getName());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);
        
        bean.query(fields, 1, 10);
    }
    
    @Test
    public void testAdd() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        MethodDO data = mock(MethodDO.class);
        when(data.getActiveBegin()).thenReturn(Datetime.getInstance());
        when(data.getActiveEnd()).thenReturn(Datetime.getInstance());
        when(data.getDescription()).thenReturn("Test Description");
        when(data.getIsActive()).thenReturn("Y");
        when(data.getName()).thenReturn("Test name");
        when(data.getReportingDescription()).thenReturn("Test Reporting Description");
        
        assertEquals(data,bean.add(data));
    }
    
    @Test(expected=ValidationErrorsList.class)
    public void testAddDuplicate() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        MethodDO dup = mock(MethodDO.class);
        when(dup.getId()).thenReturn(1);
        when(query.getSingleResult()).thenReturn(dup);
        
        MethodDO data = mock(MethodDO.class);
        when(data.getActiveBegin()).thenReturn(Datetime.getInstance());
        when(data.getActiveEnd()).thenReturn(Datetime.getInstance());
        when(data.getDescription()).thenReturn("Test Description");
        when(data.getIsActive()).thenReturn("Y");
        when(data.getName()).thenReturn("Test name");
        when(data.getReportingDescription()).thenReturn("Test Reporting Description");
        when(data.getId()).thenReturn(null);
        
        assertEquals(data,bean.add(data));
    }
    
    @Test(expected=Exception.class)
    public void testAddNoPermission() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(bean.userCache).applyPermission("method", ModuleFlags.ADD);
        bean.add(mock(MethodDO.class));
    }
    
    @Test
    public void update() throws Exception {
 
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        MethodDO data = mock(MethodDO.class);
        when(data.getActiveBegin()).thenReturn(Datetime.getInstance());
        when(data.getActiveEnd()).thenReturn(Datetime.getInstance());
        when(data.getDescription()).thenReturn("Test Description");
        when(data.getIsActive()).thenReturn("Y");
        when(data.getName()).thenReturn("Test name");
        when(data.getReportingDescription()).thenReturn("Test Reporting Description");
        when(data.getId()).thenReturn(1);
        when(data.isChanged()).thenReturn(true);
        
        when(bean.manager.find(Method.class,1)).thenReturn(mock(Method.class));
        
        assertEquals(data,bean.update(data));
        
    }
    
    @Test(expected=Exception.class) 
    public void testUpdateLocked() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(bean.lock).validateLock(Constants.table().METHOD, 1);
        MethodDO data = mock(MethodDO.class);
        when(data.getActiveBegin()).thenReturn(Datetime.getInstance());
        when(data.getActiveEnd()).thenReturn(Datetime.getInstance());
        when(data.getDescription()).thenReturn("Test Description");
        when(data.getIsActive()).thenReturn("Y");
        when(data.getName()).thenReturn("Test name");
        when(data.getReportingDescription()).thenReturn("Test Reporting Description");
        when(data.getId()).thenReturn(1);
        when(data.isChanged()).thenReturn(true);
        
        bean.update(data);
    }
    
    @Test(expected=Exception.class)
    public void testUpdateNoPermission() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(bean.userCache).applyPermission("method", ModuleFlags.UPDATE);
        MethodDO data = mock(MethodDO.class);
        when(data.isChanged()).thenReturn(true);
        
        bean.update(data);
    }
    
    @Test(expected=Exception.class)
    public void testUpdateNotFound() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        MethodDO dup = mock(MethodDO.class);
        when(dup.getId()).thenReturn(1);
        when(query.getSingleResult()).thenReturn(dup);
        
        when(bean.manager.find(Method.class,1)).thenReturn(null);
        
        MethodDO data = mock(MethodDO.class);
        when(data.isChanged()).thenReturn(true);
        when(data.getId()).thenReturn(1);
        
        bean.update(data);
    }
    
    @Test
    public void testUpdateNotChanged() throws Exception {
        MethodDO data = mock(MethodDO.class);
        when(data.getId()).thenReturn(1);
        when(data.isChanged()).thenReturn(false);
        
        assertEquals(data,bean.update(data));
        
        Mockito.verify(bean.lock,Mockito.times(1)).unlock(Constants.table().METHOD,1);
    }
    
    @Test
    public void testValidate() throws Exception {
        MethodDO data = new MethodDO();
        try {
            bean.validate(data);
            fail("Should have failed validation");
        }catch(ValidationErrorsList list) {
            assertEquals(3,list.size());
        }
    }
    
    @Test
    public void testValidateDateAfter() throws Exception {
        Query query = mock(Query.class);
        when(bean.manager.createNamedQuery("Method.FetchByName")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        MethodDO data = new MethodDO();
        data.setName("name");
        data.setActiveBegin(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, new Date("2014/03/01")));
        data.setActiveEnd(Datetime.getInstance(Datetime.YEAR,Datetime.DAY, new Date("2014/02/01")));
        
        try {
            bean.validate(data);
            fail();
        }catch(ValidationErrorsList list) {
            assertEquals(1,list.size());
            assertTrue(list.getErrorList().get(0).getMessage().equals(constants.endDateAfterBeginDateException()));
        }
    }
    
    
}
