package org.openelis.bean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.openelis.ui.util.TestingUtil.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Provider;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.common.data.QueryData.Type;

@RunWith(MockitoJUnitRunner.class)
public class TestProviderBean {

    ProviderBean      bean;

    OpenELISConstants constants;

    //@formatter:off
    final static String FETCH_BY_ID = "Provider.FetchById",
                    FETCH_BY_NPI = "Provider.FetchByNpi",
                    FETCH_BY_LAST_NAME_NPI_EXTERNAL_ID = "Provider.FetchByLastNameNpiExternalId",
                    FETCH_BY_IDS = "Provider.FetchByIds";
    //@formatter:on

    @Before
    public void init() {
        Constants.setConstants(new Constants());

        LocaleProxy.initialize();
        constants = LocaleFactory.get(OpenELISConstants.class, "en");

        bean = new ProviderBean();
        bean.manager = mock(EntityManager.class);
    }

    @Test
    public void testFetchById() {
        ProviderDO data = mock(ProviderDO.class);
        mockNamedQueryWithSingleResult(bean.manager, FETCH_BY_ID, data);

        try {
            assertEquals(data, bean.fetchById(1));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = NotFoundException.class)
    public void testFetchByIdNotFound() throws Exception {
        mockNamedQueryThatThrowsException(bean.manager, FETCH_BY_ID, new NoResultException());

        bean.fetchById(1);
    }

    @Test(expected = DatabaseException.class)
    public void testFetchByIdDatabaseException() throws Exception {
        mockNamedQueryThatThrowsException(bean.manager, FETCH_BY_ID, new RuntimeException());

        bean.fetchById(1);
    }

    @Test
    public void testFetchByIds() throws Exception {
        List<ProviderDO> results = new ArrayList<ProviderDO>();
        ArrayList<Integer> ids = mock(ArrayList.class);
        when(ids.size()).thenReturn(1);
        results.add(mock(ProviderDO.class));

        mockNamedQueryWithResultList(bean.manager, FETCH_BY_IDS, results);

        assertEquals(results, bean.fetchByIds(ids));
    }

    @Test
    public void testFetchByIdsNoResult() throws Exception {
        mockNamedQueryWithResultList(bean.manager, FETCH_BY_IDS, new ArrayList<ProviderDO>());

        assertTrue(bean.fetchByIds(mock(ArrayList.class)).isEmpty());
    }

    @Test
    public void testFetchByNpi() throws Exception {
        List<ProviderDO> results = new ArrayList<ProviderDO>();
        results.add(mock(ProviderDO.class));

        mockNamedQueryWithResultList(bean.manager, FETCH_BY_NPI, results);

        assertEquals(results, bean.fetchByNpi("npi", 10));
    }

    @Test
    public void testFetchByNpiNoResult() throws Exception {
        mockNamedQueryWithResultList(bean.manager, FETCH_BY_NPI, new ArrayList<ProviderDO>());

        assertTrue(bean.fetchByNpi("npi", 10).isEmpty());
    }

    @Test
    public void testFetchByLastNameNpiExternalId() throws Exception {
        List<ProviderDO> results = new ArrayList<ProviderDO>();
        results.add(mock(ProviderDO.class));

        mockNamedQueryWithResultList(bean.manager, FETCH_BY_LAST_NAME_NPI_EXTERNAL_ID, results);

        assertEquals(results, bean.fetchByLastNameNpiExternalId("search", 10));
    }

    @Test
    public void testFetchByLastNameNpiExternalIdNoResult() throws Exception {
        mockNamedQueryWithResultList(bean.manager,
                                     FETCH_BY_LAST_NAME_NPI_EXTERNAL_ID,
                                     new ArrayList<ProviderDO>());

        assertTrue(bean.fetchByLastNameNpiExternalId("search", 10).isEmpty());
    }

    @Test
    public void testQuery() throws Exception {
        ArrayList<IdFirstLastNameVO> results = new ArrayList<IdFirstLastNameVO>();
        results.add(mock(IdFirstLastNameVO.class));

        mockQueryWithResultList(bean.manager, results);

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        assertEquals(results, bean.query(fields, 0, 10));
    }

    @Test(expected = NotFoundException.class)
    public void testQueryNotFound() throws Exception {
        mockQueryWithResultList(bean.manager, new ArrayList<IdFirstLastNameVO>());

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        bean.query(fields, 0, 10);
    }

    @Test(expected = LastPageException.class)
    public void testQueryLastPage() throws Exception {
        ArrayList<IdFirstLastNameVO> results = new ArrayList<IdFirstLastNameVO>();
        results.add(mock(IdFirstLastNameVO.class));

        mockQueryWithResultList(bean.manager, results);

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        bean.query(fields, 1, 10);
    }

    @Test
    public void testAdd() throws Exception {
        mockNamedQueryThatThrowsException(bean.manager, FETCH_BY_NPI, new NoResultException());

        ProviderDO data = mock(ProviderDO.class);
        when(data.getFirstName()).thenReturn("first");
        when(data.getLastName()).thenReturn("last");
        when(data.getMiddleName()).thenReturn("middle");
        when(data.getNpi()).thenReturn("npi");
        when(data.getTypeId()).thenReturn(2);

        assertEquals(data, bean.add(data));
    }

    @Test
    public void testUpdate() throws Exception {
        mockNamedQueryThatThrowsException(bean.manager, FETCH_BY_NPI, new NoResultException());

        ProviderDO data = mock(ProviderDO.class);
        when(data.getFirstName()).thenReturn("first");
        when(data.getLastName()).thenReturn("last");
        when(data.getMiddleName()).thenReturn("middle");
        when(data.getNpi()).thenReturn("npi");
        when(data.getTypeId()).thenReturn(2);
        when(data.getId()).thenReturn(1);
        when(data.isChanged()).thenReturn(true);

        when(bean.manager.find(Provider.class, 1)).thenReturn(mock(Provider.class));

        assertEquals(data, bean.update(data));
    }

    @Test(expected = Exception.class)
    public void testUpdateNotFound() throws Exception {
        ProviderDO dup = mock(ProviderDO.class);
        when(dup.getId()).thenReturn(1);

        mockNamedQueryWithSingleResult(bean.manager, FETCH_BY_NPI, dup);

        when(bean.manager.find(Provider.class, 1)).thenReturn(null);

        ProviderDO data = mock(ProviderDO.class);
        when(data.isChanged()).thenReturn(true);
        when(data.getId()).thenReturn(1);

        bean.update(data);
    }

    @Test
    public void testValidate() throws Exception {
        ProviderDO data = new ProviderDO();
        try {
            bean.validate(data);
            fail("Should have failed validation");
        } catch (ValidationErrorsList list) {
            assertEquals(1, list.size());
        }
    }
}
