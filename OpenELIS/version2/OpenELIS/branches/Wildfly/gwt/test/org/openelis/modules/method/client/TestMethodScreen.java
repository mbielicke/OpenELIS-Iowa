package org.openelis.modules.method.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Window;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestMethodScreen {

    MethodScreenUI screen;
    ValueChangeHandler<String> nameChangeHandler;
    
    ModulePermission userPermission;
    SystemUserPermission systemUserPermission;
    
    UserCacheService userCache;
    
    MethodService    methodService;
    
    OpenELISConstants messages;
    
    @Before
    public void init() {
        
        setMocks();
        
        Window window = new Window();
        
        try {
            screen = new MethodScreenUI(window) {
                @Override
                protected MethodService getMethodService() {
                    return methodService;
                }
                
                @Override
                protected UserCacheService getUserCacheService() {
                    return userCache;
                }
                
                @Override
                protected OpenELISConstants getMessages() {
                    return messages;
                }
            };
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        window.setContent(screen);
    }
    
    @Test
    public void testDefault() {
        verifyDefaultState();
        
        verify(screen.atozButtons).setEnabled(true);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(true);
    }
    
    @Test
    public void testAdd() {
        
        resetCalls();
        
        screen.add(null);
       
        verifyAddState();
        
        verify(screen.atozButtons).setEnabled(false);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(false);
        

    }
    
    @Test
    public void testQuery() {
        resetCalls();
        
        screen.query(null);
       
        verifyQueryState();
        
        verify(screen.atozButtons).setEnabled(false);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(false);
    }
    
    @Test
    public void testPressAtoZButton() {
        Button button = GWT.create(Button.class);
        when(button.getAction()).thenReturn("*");
        ClickEvent event = GWT.create(ClickEvent.class);
        when(event.getSource()).thenReturn(button);
        
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ArrayList<IdNameVO> list = new ArrayList<IdNameVO>();
                list.add(new IdNameVO(1,"Mock Record"));
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                callback.onSuccess(list);
                return null;
          
            }
        }).when(methodService).query(Mockito.any(Query.class),(AsyncCallbackUI)Mockito.any(AsyncCallbackUI.class));
        
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).fetchById(Mockito.anyInt(),(AsyncCallbackUI)Mockito.any());
        
        screen.atozQuery(event);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        
        verify(screen.atozButtons).setEnabled(true);
        verify(screen.atozNext, times(2)).setEnabled(true);
        verify(screen.atozPrev, times(2)).setEnabled(true);
        verify(screen.atozTable).setEnabled(true);
    }   
     
    @Test 
    public void testUpdate() {
        
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).fetchForUpdate(Mockito.anyInt(), (AsyncCallbackUI)Mockito.any());
        
        resetCalls();
        
        screen.update(null);
        
        verifyUpdateState();
        
        verify(screen.atozButtons).setEnabled(false);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(false);
    }

    
    @Test
    public void testCommitQuery() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ArrayList<IdNameVO> list = new ArrayList<IdNameVO>();
                list.add(new IdNameVO(1,"Mock Record"));
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                callback.onSuccess(list);
                return null;
          
            }
        }).when(methodService).query(Mockito.any(Query.class),(AsyncCallbackUI)Mockito.any(AsyncCallbackUI.class));
        
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).fetchById(Mockito.anyInt(),(AsyncCallbackUI)Mockito.any());
        
        screen.setState(State.QUERY);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        
        verify(screen.atozButtons).setEnabled(true);
        verify(screen.atozNext, times(2)).setEnabled(true);
        verify(screen.atozPrev, times(2)).setEnabled(true);
        verify(screen.atozTable).setEnabled(true);
    }
    
    @Test
    public void testCommitAdd() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).add(Mockito.any(MethodDO.class),(AsyncCallbackUI)Mockito.any());
        
        screen.setState(State.ADD);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        
        verify(screen.atozButtons).setEnabled(true);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(true);
        
    }
    
    @Test
    public void testCommitAddFailure() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onFailure(new Exception("Error Message"));
                return null;
            }
        }).when(methodService).add(Mockito.any(MethodDO.class),(AsyncCallbackUI)Mockito.any());
        
        screen.setState(State.ADD);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.ADD));
        
        
    }
    
    @Test
    public void testCommitUpdate() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).update(Mockito.any(MethodDO.class),(AsyncCallbackUI)Mockito.any());
        
        screen.setState(State.UPDATE);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        
        verify(screen.atozButtons).setEnabled(true);
        verify(screen.atozNext).setEnabled(false);
        verify(screen.atozPrev).setEnabled(false);
        verify(screen.atozTable).setEnabled(true);
    }
    
    @Test
    public void testCommitUpdateFailure() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onFailure(new Exception("Error Message"));
                return null;
            }
        }).when(methodService).update(Mockito.any(MethodDO.class),(AsyncCallbackUI)Mockito.any());
        
        screen.setState(State.UPDATE);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.UPDATE));
        
    }
    
    @Test
    public void testValidationError() {
    
        screen.addScreenHandler(screen.name,"name", new ScreenHandler<String>() {
            @Override
            public void isValid(Validation validation) {
                validation.addException(new Exception("Error"));
            }
        });
        
        screen.setState(State.ADD);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.ADD));
    }
    
    private void verifyQueryState() {
        verify(screen.query).setEnabled(true);
        verify(screen.query).setPressed(true);
        verify(screen.query).lock();
        verify(screen.previous).setEnabled(false);
        verify(screen.next).setEnabled(false);
        verify(screen.add).setEnabled(false);
        verify(screen.update).setEnabled(false);
        verify(screen.commit).setEnabled(true);
        verify(screen.abort).setEnabled(true);
        verify(screen.optionsMenu).setEnabled(false);
        verify(screen.optionsButton).setEnabled(false);
        verify(screen.history).setEnabled(false);
        
        verify(screen.name).setEnabled(true);
        verify(screen.name).setQueryMode(true);
        verify(screen.name).setValue(null);
        
        verify(screen.description).setEnabled(true);
        verify(screen.description).setQueryMode(true);
        verify(screen.description).setValue(null);
        
        verify(screen.reportingDescription).setEnabled(true);
        verify(screen.reportingDescription).setQueryMode(true);
        verify(screen.reportingDescription).setValue(null);
        
        verify(screen.isActive).setEnabled(true);
        verify(screen.isActive).setQueryMode(true);
        verify(screen.isActive).setValue(null);
        
        verify(screen.activeBegin).setValue(null);
        verify(screen.activeBegin).setQueryMode(true);
        verify(screen.activeBegin).setEnabled(true);
        
        verify(screen.activeEnd).setValue(null);
        verify(screen.activeEnd).setQueryMode(true);
        verify(screen.activeEnd).setEnabled(true);
       
    }
    
    @Test
    public void testAbortQuery() {
        screen.setState(State.QUERY);
        
        resetCalls();
        
        screen.abort(null);
        
        testDefault();
    }
    
    @Test
    public void testAbortAdd() {
        screen.setState(State.ADD);
        
        resetCalls();
        
        screen.abort(null);
        
        testDefault();
    }
    
    @Test
    public void testAbortUpdate() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallbackUI callback = (AsyncCallbackUI) invocation.getArguments()[1];
                resetCalls();
                callback.onSuccess(new MethodDO());
                return null;
            }
        }).when(methodService).abortUpdate(Mockito.anyInt(),(AsyncCallbackUI)Mockito.any());

        screen.setState(State.UPDATE);
        
        screen.abort(null);
        
        assertTrue(screen.isState(State.DISPLAY));

        verifyDisplayState();
        
    }
    
    @Test
    public void testValueChange() {
        
        when(screen.name.addValueChangeHandler((ValueChangeHandler<String>)Mockito.any(ValueChangeHandler.class)))
        .thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                nameChangeHandler = (ValueChangeHandler<String>)invocation.getArguments()[0];
                return null;
            }
        });
        
        screen.initialize();
        
        ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
        when(event.getValue()).thenReturn("My Value");
        nameChangeHandler.onValueChange(event);
        
    }
    
    private void setMocks() {
        userPermission = mock(ModulePermission.class);
        when(userPermission.hasSelectPermission()).thenReturn(true);
        when(userPermission.hasAddPermission()).thenReturn(true);
        when(userPermission.hasDeletePermission()).thenReturn(true);
        when(userPermission.hasUpdatePermission()).thenReturn(true);
        
        userCache = mock(UserCacheService.class);
        systemUserPermission = mock(SystemUserPermission.class);
        
        
        try{
            when(userCache.getPermission()).thenReturn(systemUserPermission);
            when(systemUserPermission.getModule("method")).thenReturn(userPermission);
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        methodService = mock(MethodService.class);
        
        messages = GWT.create(OpenELISConstants.class);
    }
    
    private void verifyDefaultState() {
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
        
        verify(screen.name).setEnabled(false);
        verify(screen.name).setValue(null);
        
        verify(screen.description).setEnabled(false);
        verify(screen.description).setValue(null);
        
        verify(screen.reportingDescription).setEnabled(false);
        verify(screen.reportingDescription).setValue(null);
        
        verify(screen.isActive).setEnabled(false);
        verify(screen.isActive).setValue(null);
        
        verify(screen.activeBegin).setValue(null);
        verify(screen.activeBegin).setEnabled(false);
        
        verify(screen.activeEnd).setValue(null);
        verify(screen.activeEnd).setEnabled(false);
        
    }
    
    private void verifyAddState() {
        verify(screen.query).setEnabled(false);
        verify(screen.previous).setEnabled(false);
        verify(screen.next).setEnabled(false);
        verify(screen.add).setEnabled(true);
        verify(screen.add).setPressed(true);
        verify(screen.add).lock();
        verify(screen.update).setEnabled(false);
        verify(screen.commit).setEnabled(true);
        verify(screen.abort).setEnabled(true);
        verify(screen.optionsMenu).setEnabled(false);
        verify(screen.optionsButton).setEnabled(false);
        verify(screen.history).setEnabled(false);
        
        verify(screen.name).setEnabled(true);
        verify(screen.name).setValue(null);
        
        verify(screen.description).setEnabled(true);
        verify(screen.description).setValue(null);
        
        verify(screen.reportingDescription).setEnabled(true);
        verify(screen.reportingDescription).setValue(null);
        
        verify(screen.isActive).setEnabled(true);
        verify(screen.isActive).setValue("Y");
        
        verify(screen.activeBegin).setValue(null);
        verify(screen.activeBegin).setEnabled(true);
        
        verify(screen.activeEnd).setValue(null);
        verify(screen.activeEnd).setEnabled(true);
        
    }
    
    private void verifyDisplayState() {
        verify(screen.query).setEnabled(true);
        verify(screen.previous).setEnabled(true);
        verify(screen.next).setEnabled(true);
        verify(screen.add).setEnabled(true);
        verify(screen.update).setEnabled(true);
        verify(screen.commit).setEnabled(false);
        verify(screen.abort).setEnabled(false);
        verify(screen.optionsMenu).setEnabled(true);
        verify(screen.optionsButton).setEnabled(true);
        verify(screen.history).setEnabled(true);
        
        verify(screen.name).setEnabled(false);
        verify(screen.name).setValue(null);
        
        verify(screen.description).setEnabled(false);
        verify(screen.description).setValue(null);
        
        verify(screen.reportingDescription).setEnabled(false);
        verify(screen.reportingDescription).setValue(null);
        
        verify(screen.isActive).setEnabled(false);
        verify(screen.isActive).setValue(null);
        
        verify(screen.activeBegin).setValue(null);
        verify(screen.activeBegin).setEnabled(false);
        
        verify(screen.activeEnd).setValue(null);
        verify(screen.activeEnd).setEnabled(false);
        
    }
    
    private void verifyUpdateState() {
        verify(screen.query).setEnabled(false);
        verify(screen.previous).setEnabled(false);
        verify(screen.next).setEnabled(false);
        verify(screen.add).setEnabled(false);
        verify(screen.update).setPressed(true);
        verify(screen.update).lock();
        verify(screen.update).setEnabled(true);
        verify(screen.commit).setEnabled(true);
        verify(screen.abort).setEnabled(true);
        verify(screen.optionsMenu).setEnabled(false);
        verify(screen.optionsButton).setEnabled(false);
        verify(screen.history).setEnabled(false);
        
        verify(screen.name).setEnabled(true);
        verify(screen.name).setValue(null);
        
        verify(screen.description).setEnabled(true);
        verify(screen.description).setValue(null);
        
        verify(screen.reportingDescription).setEnabled(true);
        verify(screen.reportingDescription).setValue(null);
        
        verify(screen.isActive).setEnabled(true);
        verify(screen.isActive).setValue(null);
        
        verify(screen.activeBegin).setValue(null);
        verify(screen.activeBegin).setEnabled(true);
        
        verify(screen.activeEnd).setValue(null);
        verify(screen.activeEnd).setEnabled(true);
    }
    
    private void resetCalls() {
        reset(screen.query,
              screen.previous,
              screen.next,
              screen.add,
              screen.update,
              screen.commit,
              screen.abort,
              screen.optionsMenu,
              screen.optionsButton,
              screen.history,
              screen.name,
              screen.description,
              screen.reportingDescription,
              screen.isActive,
              screen.activeBegin,
              screen.activeEnd,
              screen.atozButtons,
              screen.atozNext,
              screen.atozPrev,
              screen.atozTable);
    }
}
