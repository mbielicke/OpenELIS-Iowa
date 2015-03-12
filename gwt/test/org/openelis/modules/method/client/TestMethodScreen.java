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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestMethodScreen {

   
    
    @Mock ModulePermission userPermission;
    @Mock SystemUserPermission systemUserPermission;
    
    @Mock UserCacheService userCache;
    
    @Mock MethodServiceImpl    methodService;
    
    OpenELISConstants messages;
    
    MethodViewImpl view;
    
    @Mock WindowInt window;
    
    @InjectMocks MethodPresenter screen;
    
    @Before
    public void preArrange() {
        MockitoAnnotations.initMocks(this);
        setMocks();
        
        try {
            screen.present(view,window);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setMocks() {

        when(userPermission.hasSelectPermission()).thenReturn(true);
        when(userPermission.hasAddPermission()).thenReturn(true);
        when(userPermission.hasDeletePermission()).thenReturn(true);
        when(userPermission.hasUpdatePermission()).thenReturn(true);
        
        view = new MethodViewImpl();
        
        try{
            when(userCache.getPermission()).thenReturn(systemUserPermission);
            when(systemUserPermission.getModule("method")).thenReturn(userPermission);
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        screen.messages = GWT.create(OpenELISConstants.class);
        screen.data = new MethodDO();
    }
    
    @Test
    public void default_state() {
        verifyDefaultState();
        verifyEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyEnabled(screen.view.atozTable);
    }
    
    @Test
    public void add() {        
        screen.add(null);
       
        verifyAddState();
        verifyNotEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyNotEnabled(screen.view.atozTable);
    }
    
    @Test
    public void query() {
        screen.query(null);
       
        verifyQueryState();
        verifyNotEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyNotEnabled(screen.view.atozTable);
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
        verifyEnabled(screen.view.atozButtons);
        verifyEnabled(screen.view.atozNext);
        verifyEnabled(screen.view.atozPrev);
        verifyEnabled(screen.view.atozTable);
    }   
     
    @SuppressWarnings("unchecked")
    @Test 
    public void update() {
        
        doAnswer(createAnswerWithResult(new MethodDO()))
        .when(methodService).fetchForUpdate(Mockito.anyInt(),any(AsyncCallbackUI.class));
        
        screen.update(null);
        
        verifyUpdateState();
        verifyNotEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyNotEnabled(screen.view.atozTable);
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
        
        when(screen.view.activeBegin.getQuery()).thenReturn(null);
        when(screen.view.activeEnd.getQuery()).thenReturn(null);
        when(screen.view.atozTable.getQuery()).thenReturn(null);
        when(screen.view.description.getQuery()).thenReturn(null);
        when(screen.view.isActive.getQuery()).thenReturn(null);
        when(screen.view.name.getQuery()).thenReturn(null);
        when(screen.view.reportingDescription.getQuery()).thenReturn(null);
       
        screen.setState(State.QUERY);
        
        screen.commit(null);
        
        assertTrue(screen.isState(State.DISPLAY));
        verifyDisplayState();
        verifyEnabled(screen.view.atozButtons);
        verifyEnabled(screen.view.atozNext);
        verifyEnabled(screen.view.atozPrev);
        verifyEnabled(screen.view.atozTable);
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
        verifyEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyEnabled(screen.view.atozTable);
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
        verifyEnabled(screen.view.atozButtons);
        verifyNotEnabled(screen.view.atozNext);
        verifyNotEnabled(screen.view.atozPrev);
        verifyEnabled(screen.view.atozTable);
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
    
    private void verifyDefaultState() {
        verifyEnabled(screen.view.query);
        verifyNotEnabled(screen.view.previous);
        verifyNotEnabled(screen.view.next);
        verifyEnabled(screen.view.add);
        verifyNotEnabled(screen.view.update);
        verifyNotEnabled(screen.view.commit);
        verifyNotEnabled(screen.view.abort);
        verifyNotEnabled(screen.view.optionsMenu);
        verifyNotEnabled(screen.view.optionsButton);
        verifyNotEnabled(screen.view.history);
        
        verifyNotEnabled(screen.view.name);
        verifyValue(screen.view.name,null);
        
        verifyNotEnabled(screen.view.description);
        verifyValue(screen.view.description,null);
        
        verifyNotEnabled(screen.view.reportingDescription);
        verifyValue(screen.view.reportingDescription,null);
        
        verifyNotEnabled(screen.view.isActive);
        verifyValue(screen.view.isActive,null);
        
        verifyValue(screen.view.activeBegin,null);
        verifyNotEnabled(screen.view.activeBegin);
        
        verifyValue(screen.view.activeEnd,null);
        verifyNotEnabled(screen.view.activeEnd);
    }
    
    private void verifyAddState() {
        verifyNotEnabled(screen.view.query);
        verifyNotEnabled(screen.view.previous);
        verifyNotEnabled(screen.view.next);
        verifyEnabled(screen.view.add);
        verifyEnabled(screen.view.add);
        verify(screen.view.add,last()).lock();
        verifyNotEnabled(screen.view.update);
        verifyEnabled(screen.view.commit);
        verifyEnabled(screen.view.abort);
        verifyNotEnabled(screen.view.optionsMenu);
        verifyNotEnabled(screen.view.optionsButton);
        verifyNotEnabled(screen.view.history);
        
        verifyEnabled(screen.view.name);
        verifyValue(screen.view.name,null);
        
        verifyEnabled(screen.view.description);
        verifyValue(screen.view.description,null);
        
        verifyEnabled(screen.view.reportingDescription);
        verifyValue(screen.view.reportingDescription,null);
        
        verifyEnabled(screen.view.isActive);
        verifyValue(screen.view.isActive,"Y");
        
        verifyValue(screen.view.activeBegin,null);
        verifyEnabled(screen.view.activeBegin);
        
        verifyValue(screen.view.activeEnd,null);
        verifyEnabled(screen.view.activeEnd);
        
    }
    
    private void verifyDisplayState() {
        verifyEnabled(screen.view.query);
        verifyEnabled(screen.view.previous);
        verifyEnabled(screen.view.next);
        verifyEnabled(screen.view.add);
        verifyEnabled(screen.view.update);
        verifyNotEnabled(screen.view.commit);
        verifyNotEnabled(screen.view.abort);
        verifyEnabled(screen.view.optionsMenu);
        verifyEnabled(screen.view.optionsButton);
        verifyEnabled(screen.view.history);
        
        verifyNotEnabled(screen.view.name);
        verifyValue(screen.view.name,null);
        
        verifyNotEnabled(screen.view.description);
        verifyValue(screen.view.description,null);
        
        verifyNotEnabled(screen.view.reportingDescription);
        verifyValue(screen.view.reportingDescription,null);
        
        verifyNotEnabled(screen.view.isActive);
        verifyValue(screen.view.isActive,null);
        
        verifyValue(screen.view.activeBegin,null);
        verifyNotEnabled(screen.view.activeBegin);
        
        verifyValue(screen.view.activeEnd,null);
        verifyNotEnabled(screen.view.activeEnd);
        
    }
    
    private void verifyUpdateState() {
        verifyNotEnabled(screen.view.query);
        verifyNotEnabled(screen.view.previous);
        verifyNotEnabled(screen.view.next);
        verifyNotEnabled(screen.view.add);
        verify(screen.view.update,last()).setPressed(true);
        verify(screen.view.update,last()).lock();
        verifyEnabled(screen.view.update);
        verifyEnabled(screen.view.commit);
        verifyEnabled(screen.view.abort);
        verifyNotEnabled(screen.view.optionsMenu);
        verifyNotEnabled(screen.view.optionsButton);
        verifyNotEnabled(screen.view.history);
        
        verifyEnabled(screen.view.name);
        verifyValue(screen.view.name,null);
        
        verifyEnabled(screen.view.description);
        verifyValue(screen.view.description,null);
        
        verifyEnabled(screen.view.reportingDescription);
        verifyValue(screen.view.reportingDescription,null);
        
        verifyEnabled(screen.view.isActive);
        verifyValue(screen.view.isActive,null);
        
        verifyValue(screen.view.activeBegin,null);
        verifyEnabled(screen.view.activeBegin);
        
        verifyValue(screen.view.activeEnd,null);
        verifyEnabled(screen.view.activeEnd);
    }
    
    private void verifyQueryState() {
        verifyEnabled(screen.view.query);
        verifyEnabled(screen.view.query);
        verify(screen.view.query,last()).lock();
        verifyNotEnabled(screen.view.previous);
        verifyNotEnabled(screen.view.next);
        verifyNotEnabled(screen.view.add);
        verifyNotEnabled(screen.view.update);
        verifyEnabled(screen.view.commit);
        verifyEnabled(screen.view.abort);
        verifyNotEnabled(screen.view.optionsMenu);
        verifyNotEnabled(screen.view.optionsButton);
        verifyNotEnabled(screen.view.history);
        
        verifyEnabled(screen.view.name);
        verify(screen.view.name,last()).setQueryMode(true);
        verifyValue(screen.view.name,null);
        
        verifyEnabled(screen.view.description);
        verify(screen.view.description,last()).setQueryMode(true);
        verifyValue(screen.view.description,null);
        
        verifyEnabled(screen.view.reportingDescription);
        verify(screen.view.reportingDescription,last()).setQueryMode(true);
        verifyValue(screen.view.reportingDescription,null);
        
        verifyEnabled(screen.view.isActive);
        verify(screen.view.isActive,last()).setQueryMode(true);
        verifyValue(screen.view.isActive,null);
        
        verifyValue(screen.view.activeBegin,null);
        verify(screen.view.activeBegin,last()).setQueryMode(true);
        verifyEnabled(screen.view.activeBegin);
        
        verifyValue(screen.view.activeEnd,null);
        verify(screen.view.activeEnd,last()).setQueryMode(true);
        verifyEnabled(screen.view.activeEnd);
    }
}
