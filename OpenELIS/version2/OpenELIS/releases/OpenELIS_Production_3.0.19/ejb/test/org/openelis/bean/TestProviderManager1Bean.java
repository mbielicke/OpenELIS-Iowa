package org.openelis.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openelis.manager.ProviderManager1Accessor.getLocations;
import static org.openelis.manager.ProviderManager1Accessor.getNotes;
import static org.openelis.manager.ProviderManager1Accessor.setProvider;
import static org.openelis.manager.ProviderManager1Accessor.setRemoved;

import java.util.ArrayList;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.domain.SampleDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.common.data.QueryData.Type;

@RunWith(MockitoJUnitRunner.class)
public class TestProviderManager1Bean {

    ProviderManager1Bean bean;

    OpenELISConstants    constants;

    @Before
    public void init() {
        Constants.setConstants(new Constants());

        LocaleProxy.initialize();
        constants = LocaleFactory.get(OpenELISConstants.class, "en");

        bean = new ProviderManager1Bean();

        bean.provider = mock(ProviderBean.class);
        bean.lock = mock(LockBean.class);
        bean.providerLocation = mock(ProviderLocationBean.class);
        bean.note = mock(NoteBean.class);
    }

    @Test
    public void testFetchById() throws Exception {
        ProviderManager1 pm = mock(ProviderManager1.class);
        ProviderDO provider = mock(ProviderDO.class);
        ProviderLocationDO loc = mock(ProviderLocationDO.class);
        NoteViewDO note = mock(NoteViewDO.class);
        ArrayList<ProviderDO> providers = new ArrayList<ProviderDO>();
        ArrayList<ProviderLocationDO> locations = new ArrayList<ProviderLocationDO>();
        ArrayList<NoteViewDO> notes = new ArrayList<NoteViewDO>();
        providers.add(provider);
        locations.add(loc);
        notes.add(note);
        when(bean.provider.fetchByIds((ArrayList<Integer>)any())).thenReturn(providers);
        when(bean.providerLocation.fetchByProviderIds((ArrayList<Integer>)any())).thenReturn(locations);
        when(bean.note.fetchByIdsAndTables((ArrayList<Integer>)any(), (ArrayList<Integer>)any())).thenReturn(notes);
        when(pm.getProvider()).thenReturn(provider);

        try {
            ProviderManager1 ret = bean.fetchById(1);
            assertEquals(pm.getProvider(), ret.getProvider());
            assertEquals(locations, getLocations(ret));
            assertEquals(notes, getNotes(ret));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testFetchByIds() throws Exception {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ProviderManager1 pm = mock(ProviderManager1.class);
        ProviderDO provider = mock(ProviderDO.class);
        ProviderLocationDO loc = mock(ProviderLocationDO.class);
        NoteViewDO note = mock(NoteViewDO.class);
        ArrayList<ProviderDO> providers = new ArrayList<ProviderDO>();
        ArrayList<ProviderLocationDO> locations = new ArrayList<ProviderLocationDO>();
        ArrayList<NoteViewDO> notes = new ArrayList<NoteViewDO>();
        providers.add(provider);
        locations.add(loc);
        notes.add(note);
        when(bean.provider.fetchByIds((ArrayList<Integer>)any())).thenReturn(providers);
        when(bean.providerLocation.fetchByProviderIds((ArrayList<Integer>)any())).thenReturn(locations);
        when(bean.note.fetchByIdsAndTables((ArrayList<Integer>)any(), (ArrayList<Integer>)any())).thenReturn(notes);
        when(pm.getProvider()).thenReturn(provider);

        try {
            ProviderManager1 ret = bean.fetchByIds(ids).get(0);
            assertEquals(pm.getProvider(), ret.getProvider());
            assertEquals(locations, getLocations(ret));
            assertEquals(notes, getNotes(ret));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testFetchByQuery() throws Exception {
        ArrayList<ProviderManager1> results = new ArrayList<ProviderManager1>();
        results.add(mock(ProviderManager1.class));
        ProviderManager1 pm = mock(ProviderManager1.class);
        ProviderDO provider = mock(ProviderDO.class);
        ProviderLocationDO loc = mock(ProviderLocationDO.class);
        NoteViewDO note = mock(NoteViewDO.class);
        ArrayList<ProviderDO> providers = new ArrayList<ProviderDO>();
        ArrayList<ProviderLocationDO> locations = new ArrayList<ProviderLocationDO>();
        ArrayList<NoteViewDO> notes = new ArrayList<NoteViewDO>();
        providers.add(provider);
        locations.add(loc);
        notes.add(note);
        when(bean.provider.fetchByIds((ArrayList<Integer>)any())).thenReturn(providers);
        when(bean.providerLocation.fetchByProviderIds((ArrayList<Integer>)any())).thenReturn(locations);
        when(bean.note.fetchByIdsAndTables((ArrayList<Integer>)any(), (ArrayList<Integer>)any())).thenReturn(notes);
        when(pm.getProvider()).thenReturn(provider);

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        try {
            ProviderManager1 ret = bean.fetchByQuery(fields, 0, 10).get(0);
            assertEquals(pm.getProvider(), ret.getProvider());
            assertEquals(locations, getLocations(ret));
            assertEquals(notes, getNotes(ret));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = NotFoundException.class)
    public void testFetchByQueryNotFound() throws Exception {
        when(bean.provider.query((ArrayList<QueryData>)any(), anyInt(), anyInt())).thenThrow(new NotFoundException());

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        bean.fetchByQuery(fields, 0, 10);
    }

    @Test(expected = LastPageException.class)
    public void testFetchByQueryLastPageException() throws Exception {
        when(bean.provider.query((ArrayList<QueryData>)any(), anyInt(), anyInt())).thenThrow(new LastPageException());

        ArrayList<QueryData> fields = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.setKey(ProviderMeta.getNpi());
        field.setQuery("n*");
        field.setType(Type.STRING);
        fields.add(field);

        bean.fetchByQuery(fields, 0, 10);
    }

    @Test
    public void testFetchForUpdateSingle() throws Exception {
        ProviderManager1 pm = mock(ProviderManager1.class);
        ProviderDO provider = mock(ProviderDO.class);
        ProviderLocationDO loc = mock(ProviderLocationDO.class);
        NoteViewDO note = mock(NoteViewDO.class);
        ArrayList<ProviderDO> providers = new ArrayList<ProviderDO>();
        ArrayList<ProviderLocationDO> locations = new ArrayList<ProviderLocationDO>();
        ArrayList<NoteViewDO> notes = new ArrayList<NoteViewDO>();
        providers.add(provider);
        locations.add(loc);
        notes.add(note);
        when(bean.provider.fetchByIds((ArrayList<Integer>)any())).thenReturn(providers);
        when(bean.providerLocation.fetchByProviderIds((ArrayList<Integer>)any())).thenReturn(locations);
        when(bean.note.fetchByIdsAndTables((ArrayList<Integer>)any(), (ArrayList<Integer>)any())).thenReturn(notes);
        when(pm.getProvider()).thenReturn(provider);

        try {
            ProviderManager1 ret = bean.fetchForUpdate(1);
            assertEquals(pm.getProvider(), ret.getProvider());
            assertEquals(locations, getLocations(ret));
            assertEquals(notes, getNotes(ret));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testFetchForUpdate() throws Exception {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ProviderManager1 pm = mock(ProviderManager1.class);
        ProviderDO provider = mock(ProviderDO.class);
        ProviderLocationDO loc = mock(ProviderLocationDO.class);
        NoteViewDO note = mock(NoteViewDO.class);
        ArrayList<ProviderDO> providers = new ArrayList<ProviderDO>();
        ArrayList<ProviderLocationDO> locations = new ArrayList<ProviderLocationDO>();
        ArrayList<NoteViewDO> notes = new ArrayList<NoteViewDO>();
        providers.add(provider);
        locations.add(loc);
        notes.add(note);
        when(bean.provider.fetchByIds(((ArrayList<Integer>)any()))).thenReturn(providers);
        when(bean.providerLocation.fetchByProviderIds((ArrayList<Integer>)any())).thenReturn(locations);
        when(bean.note.fetchByIdsAndTables((ArrayList<Integer>)any(), (ArrayList<Integer>)any())).thenReturn(notes);
        when(pm.getProvider()).thenReturn(provider);

        try {
            ProviderManager1 ret = bean.fetchForUpdate(ids).get(0);
            assertEquals(pm.getProvider(), ret.getProvider());
            assertEquals(locations, getLocations(ret));
            assertEquals(notes, getNotes(ret));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdate() throws Exception {
        ArrayList<ProviderManager1> pms = new ArrayList<ProviderManager1>();
        ProviderManager1 pm = new ProviderManager1();
        ProviderDO provider = mock(ProviderDO.class);
        setProvider(pm, provider);
        when(provider.getId()).thenReturn(1);
        pms.add(pm);
        when(pm.getProvider().getId()).thenReturn(1);
        assertEquals(pms, bean.update(pms, true));
        verify(bean.lock, times(1)).validateLock(anyInt(), (ArrayList<Integer>)any());
        verify(bean.lock, times(1)).unlock(anyInt(), (ArrayList<Integer>)any());
        verify(bean.providerLocation, times(0)).delete((ProviderLocationDO)any());
        verify(bean.note, times(0)).delete((NoteViewDO)any());
        verify(bean.provider, times(1)).update((ProviderDO)any());
        verify(bean.providerLocation, times(0)).add((ProviderLocationDO)any());
        verify(bean.providerLocation, times(0)).update((ProviderLocationDO)any());
        verify(bean.note, times(0)).add((NoteViewDO)any());
        verify(bean.note, times(0)).update((NoteViewDO)any());
    }

    @Test(expected = Exception.class)
    public void testInvalidRemoveObject() throws Exception {
        ArrayList<DataObject> removed = new ArrayList<DataObject>();
        removed.add(new SampleDO());
        ProviderManager1 pm = new ProviderManager1();
        ProviderDO provider = mock(ProviderDO.class);
        setProvider(pm, provider);
        when(pm.getProvider().getId()).thenReturn(1);
        setRemoved(pm, removed);

        try {
            bean.update(pm, true);
        } catch (Exception e) {
            assertEquals("ERROR: DataObject passed for removal is of unknown type", e.getMessage());
            throw e;
        }
    }
}
