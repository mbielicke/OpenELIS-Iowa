package org.openelis.modules.method.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openelis.ui.util.TestingUtil.createAnswerWithFailure;
import static org.openelis.ui.util.TestingUtil.createAnswerWithResult;
import static org.openelis.ui.util.TestingUtil.last;
import static org.openelis.ui.util.TestingUtil.verifyEnabled;
import static org.openelis.ui.util.TestingUtil.verifyNotEnabled;
import static org.openelis.ui.util.TestingUtil.verifyValue;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
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
    public void preArrange() {
        
        createMocks();
        
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
    public void default_state() {
        verifyDefaultState();
        verifyEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyEnabled(screen.atozTable);
    }
    
    @Test
    public void add() {        
        screen.add(null);
       
        verifyAddState();
        verifyNotEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyNotEnabled(screen.atozTable);
    }
    
    @Test
    public void query() {
        screen.query(null);
       
        verifyQueryState();
        verifyNotEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyNotEnabled(screen.atozTable);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void pressAtoZButton() {
        Button button = GWT.create(Button.class);
        ClickEvent event = GWT.create(ClickEvent.class);
        ArrayList<IdNameVO> list = new ArrayList<IdNameVO>();
        
        when(button.getAction()).thenReturn("*");
        when(event.getSource()).thenReturn(button);
        list.add(new IdNameVO(1,"Mock Record"));
        
        doAnswer(createAnswerWithResult(list))
        .when(methodService).query(Mockito.any(Query.class),any(AsyncCallbackUI.class));
        
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).fetchById(Mockito.anyInt(),any(AsyncCallbackUI.class));
        
        screen.atozQuery(event);
        
        assertTrue(screen.isState(State.DISPLAY));
        verifyDisplayState();
        verifyEnabled(screen.atozButtons);
        verifyEnabled(screen.atozNext);
        verifyEnabled(screen.atozPrev);
        verifyEnabled(screen.atozTable);
    }   
     
    @SuppressWarnings("unchecked")
    @Test 
    public void update() {
        
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).fetchForUpdate(Mockito.anyInt(),any(AsyncCallbackUI.class));
        
        screen.update(null);
        
        verifyUpdateState();
        verifyNotEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyNotEnabled(screen.atozTable);
    }

    
    @SuppressWarnings("unchecked")
    @Test
    public void commit_query() {
        ArrayList<IdNameVO> list = new ArrayList<IdNameVO>();
        list.add(new IdNameVO(1,"Mock Record"));
        
        doAnswer(createAnswerWithResult(list)) 
        .when(methodService).query(Mockito.any(Query.class),any(AsyncCallbackUI.class));
        
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).fetchById(Mockito.anyInt(),any(AsyncCallbackUI.class));
        
        when(screen.activeBegin.getQuery()).thenReturn(null);
        when(screen.activeEnd.getQuery()).thenReturn(null);
        when(screen.atozTable.getQuery()).thenReturn(null);
        when(screen.description.getQuery()).thenReturn(null);
        when(screen.isActive.getQuery()).thenReturn(null);
        when(screen.name.getQuery()).thenReturn(null);
        when(screen.reportingDescription.getQuery()).thenReturn(null);
       
        screen.setState(State.QUERY);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        verifyDisplayState();
        verifyEnabled(screen.atozButtons);
        verifyEnabled(screen.atozNext);
        verifyEnabled(screen.atozPrev);
        verifyEnabled(screen.atozTable);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void commit_add() {
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).add(Mockito.any(MethodDO.class),any(AsyncCallbackUI.class));
        
        screen.setState(State.ADD);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        verifyEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyEnabled(screen.atozTable);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void commit_addWithFailure() {
        doAnswer(createAnswerWithFailure(new Exception("Error Message")))
        .when(methodService).add(Mockito.any(MethodDO.class),any(AsyncCallbackUI.class));
        
        screen.setState(State.ADD);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.ADD));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void commit_update() {
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).update(Mockito.any(MethodDO.class),any(AsyncCallbackUI.class));
        
        screen.setState(State.UPDATE);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        
        verifyDisplayState();
        verifyEnabled(screen.atozButtons);
        verifyNotEnabled(screen.atozNext);
        verifyNotEnabled(screen.atozPrev);
        verifyEnabled(screen.atozTable);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void commit_updateWithFailure() {
        doAnswer(createAnswerWithFailure(new Exception("Error Message")))
        .when(methodService).update(Mockito.any(MethodDO.class),any(AsyncCallbackUI.class));
        
        screen.setState(State.UPDATE);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.UPDATE));
    }
    
    @Test
    public void validationError() {
    
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
    

    
    @Test
    public void abort_query() {
        screen.setState(State.QUERY);
        
        screen.abort(null);
        
        default_state();
    }
    
    @Test
    public void abort_add() {
        screen.setState(State.ADD);
        
        screen.abort(null);
        
        default_state();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void abort_update() {
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).abortUpdate(Mockito.anyInt(),any(AsyncCallbackUI.class));
        screen.setState(State.UPDATE);
        
        screen.abort(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        verifyDisplayState();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void value_change() { 
        ValueChangeEvent<String> event = (ValueChangeEvent<String>)mock(ValueChangeEvent.class);
        when(event.getValue()).thenReturn("value");
        
        screen.<String>getHandler(screen.name).onValueChange(event);
        Assert.assertEquals("value",screen.data.getName());
    }
    
    private void createMocks() {
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
        verifyEnabled(screen.query);
        verifyNotEnabled(screen.previous);
        verifyNotEnabled(screen.next);
        verifyEnabled(screen.add);
        verifyNotEnabled(screen.update);
        verifyNotEnabled(screen.commit);
        verifyNotEnabled(screen.abort);
        verifyNotEnabled(screen.optionsMenu);
        verifyNotEnabled(screen.optionsButton);
        verifyNotEnabled(screen.history);
        
        verifyNotEnabled(screen.name);
        verifyValue(screen.name,null);
        
        verifyNotEnabled(screen.description);
        verifyValue(screen.description,null);
        
        verifyNotEnabled(screen.reportingDescription);
        verifyValue(screen.reportingDescription,null);
        
        verifyNotEnabled(screen.isActive);
        verifyValue(screen.isActive,null);
        
        verifyValue(screen.activeBegin,null);
        verifyNotEnabled(screen.activeBegin);
        
        verifyValue(screen.activeEnd,null);
        verifyNotEnabled(screen.activeEnd);
    }
    
    private void verifyAddState() {
        verifyNotEnabled(screen.query);
        verifyNotEnabled(screen.previous);
        verifyNotEnabled(screen.next);
        verifyEnabled(screen.add);
        verifyEnabled(screen.add);
        verify(screen.add,last()).lock();
        verifyNotEnabled(screen.update);
        verifyEnabled(screen.commit);
        verifyEnabled(screen.abort);
        verifyNotEnabled(screen.optionsMenu);
        verifyNotEnabled(screen.optionsButton);
        verifyNotEnabled(screen.history);
        
        verifyEnabled(screen.name);
        verifyValue(screen.name,null);
        
        verifyEnabled(screen.description);
        verifyValue(screen.description,null);
        
        verifyEnabled(screen.reportingDescription);
        verifyValue(screen.reportingDescription,null);
        
        verifyEnabled(screen.isActive);
        verifyValue(screen.isActive,"Y");
        
        verifyValue(screen.activeBegin,null);
        verifyEnabled(screen.activeBegin);
        
        verifyValue(screen.activeEnd,null);
        verifyEnabled(screen.activeEnd);
        
    }
    
    private void verifyDisplayState() {
        verifyEnabled(screen.query);
        verifyEnabled(screen.previous);
        verifyEnabled(screen.next);
        verifyEnabled(screen.add);
        verifyEnabled(screen.update);
        verifyNotEnabled(screen.commit);
        verifyNotEnabled(screen.abort);
        verifyEnabled(screen.optionsMenu);
        verifyEnabled(screen.optionsButton);
        verifyEnabled(screen.history);
        
        verifyNotEnabled(screen.name);
        verifyValue(screen.name,null);
        
        verifyNotEnabled(screen.description);
        verifyValue(screen.description,null);
        
        verifyNotEnabled(screen.reportingDescription);
        verifyValue(screen.reportingDescription,null);
        
        verifyNotEnabled(screen.isActive);
        verifyValue(screen.isActive,null);
        
        verifyValue(screen.activeBegin,null);
        verifyNotEnabled(screen.activeBegin);
        
        verifyValue(screen.activeEnd,null);
        verifyNotEnabled(screen.activeEnd);
        
    }
    
    private void verifyUpdateState() {
        verifyNotEnabled(screen.query);
        verifyNotEnabled(screen.previous);
        verifyNotEnabled(screen.next);
        verifyNotEnabled(screen.add);
        verify(screen.update,last()).setPressed(true);
        verify(screen.update,last()).lock();
        verifyEnabled(screen.update);
        verifyEnabled(screen.commit);
        verifyEnabled(screen.abort);
        verifyNotEnabled(screen.optionsMenu);
        verifyNotEnabled(screen.optionsButton);
        verifyNotEnabled(screen.history);
        
        verifyEnabled(screen.name);
        verifyValue(screen.name,null);
        
        verifyEnabled(screen.description);
        verifyValue(screen.description,null);
        
        verifyEnabled(screen.reportingDescription);
        verifyValue(screen.reportingDescription,null);
        
        verifyEnabled(screen.isActive);
        verifyValue(screen.isActive,null);
        
        verifyValue(screen.activeBegin,null);
        verifyEnabled(screen.activeBegin);
        
        verifyValue(screen.activeEnd,null);
        verifyEnabled(screen.activeEnd);
    }
    
    private void verifyQueryState() {
        verifyEnabled(screen.query);
        verifyEnabled(screen.query);
        verify(screen.query,last()).lock();
        verifyNotEnabled(screen.previous);
        verifyNotEnabled(screen.next);
        verifyNotEnabled(screen.add);
        verifyNotEnabled(screen.update);
        verifyEnabled(screen.commit);
        verifyEnabled(screen.abort);
        verifyNotEnabled(screen.optionsMenu);
        verifyNotEnabled(screen.optionsButton);
        verifyNotEnabled(screen.history);
        
        verifyEnabled(screen.name);
        verify(screen.name,last()).setQueryMode(true);
        verifyValue(screen.name,null);
        
        verifyEnabled(screen.description);
        verify(screen.description,last()).setQueryMode(true);
        verifyValue(screen.description,null);
        
        verifyEnabled(screen.reportingDescription);
        verify(screen.reportingDescription,last()).setQueryMode(true);
        verifyValue(screen.reportingDescription,null);
        
        verifyEnabled(screen.isActive);
        verify(screen.isActive,last()).setQueryMode(true);
        verifyValue(screen.isActive,null);
        
        verifyValue(screen.activeBegin,null);
        verify(screen.activeBegin,last()).setQueryMode(true);
        verifyEnabled(screen.activeBegin);
        
        verifyValue(screen.activeEnd,null);
        verify(screen.activeEnd,last()).setQueryMode(true);
        verifyEnabled(screen.activeEnd);
    }
}
