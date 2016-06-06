package org.openelis.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.openelis.entity.Preferences;

@RunWith(MockitoJUnitRunner.class)
public class TestPreferencesBean {

    PreferencesBean   bean;

    OpenELISConstants constants;

    @Before
    public void init() {
        Constants.setConstants(new Constants());

        LocaleProxy.initialize();
        constants = LocaleFactory.get(OpenELISConstants.class, "en");

        bean = new PreferencesBean();

        bean.manager = mock(EntityManager.class);
    }

    @Test
    public void testGetPreferences() {
        Preferences data = mock(Preferences.class);
        when(data.getText()).thenReturn("test");
        when(bean.manager.find(eq(Preferences.class), anyInt())).thenReturn(data);

        try {
            assertEquals("test", bean.getPreferences(1));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = NoResultException.class)
    public void testGetPreferencesNotFound() throws Exception {
        when(bean.manager.find(eq(Preferences.class), anyInt())).thenThrow(new NoResultException());
        bean.getPreferences(0);
    }
}
