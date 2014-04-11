package org.openelis.modules.method.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

import org.openelis.ui.common.ModulePermission;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestMethodScreen {

    private MethodScreenUI screen;
    
    @Before
    public void init() {
        ModulePermission userPermission = mock(ModulePermission.class);
        when(userPermission.hasSelectPermission()).thenReturn(true);
        when(userPermission.hasAddPermission()).thenReturn(true);
        when(userPermission.hasDeletePermission()).thenReturn(true);
        when(userPermission.hasUpdatePermission()).thenReturn(true);
        screen = new MethodScreenUI(userPermission);
    }
    
    @Test
    public void testDefault() {
        verify(screen.query).setEnabled(true);
        verify(screen.previous).setEnabled(false);
        verify(screen.next).setEnabled(false);
        verify(screen.add).setEnabled(true);
        verify(screen.update).setEnabled(false);
        verify(screen.commit).setEnabled(false);
        verify(screen.abort).setEnabled(false);
        verify(screen.optionsMenu).setEnabled(false);
        verify(screen.optionsButton).setEnabled(false);
        verify(screen.history).setEnabled(false);
        
    }
}
