package org.openelis.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.NoResultException;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.manager.Preferences1;

@RunWith(MockitoJUnitRunner.class)
public class TestPreferencesManager1Bean {

    PreferencesManager1Bean bean;

    OpenELISConstants       constants;

    @Before
    public void init() {
        Constants.setConstants(new Constants());

        LocaleProxy.initialize();
        constants = LocaleFactory.get(OpenELISConstants.class, "en");

        bean = new PreferencesManager1Bean();
        bean.preferences = mock(PreferencesBean.class);
        bean.userCache = mock(UserCacheBean.class);
    }

    @Test
    public void testUserRoot() {
        Preferences1 data = mock(Preferences1.class);
        when(bean.userCache.getId()).thenReturn(1);
        when(data.get(eq("favorites"), anyString())).thenReturn("analyte,analyteParameter,dictionary,label,organization");
        when(bean.preferences.getPreferences(anyInt())).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><favorites>analyte,analyteParameter,dictionary,label,organization</favorites></root>");

        try {
            assertEquals(data.get("favorites", null), bean.userRoot().get("favorites", null));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSystemRoot() {
        Preferences1 data = mock(Preferences1.class);
        when(bean.userCache.getId()).thenReturn(0);
        when(data.get(eq("favorites"), anyString())).thenReturn("analyte,analyteParameter,dictionary,label,organization");
        when(bean.preferences.getPreferences(anyInt())).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><favorites>analyte,analyteParameter,dictionary,label,organization</favorites></root>");

        try {
            assertEquals(data.get("favorites", null), bean.systemRoot().get("favorites", null));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = NoResultException.class)
    public void testGetPreferencesNotFound() throws Exception {
        when(bean.preferences.getPreferences(anyInt())).thenThrow(new NoResultException());
        bean.preferences.getPreferences(0);
    }
}
