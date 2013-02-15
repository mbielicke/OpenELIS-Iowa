package org.openelis.modules.todo.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ToDoService implements ToDoServiceInt, ToDoServiceIntAsync {
    
    static ToDoService instance;
    
    ToDoServiceIntAsync service;
    
    public static ToDoService get() {
        if(instance == null)
            instance = new ToDoService();
        
        return instance;
    }
    
    private ToDoService() {
        service = (ToDoServiceIntAsync)GWT.create(ToDoServiceInt.class);
    }

    @Override
    public void getCompleted(AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.getCompleted(callback);
    }

    @Override
    public void getInitiated(AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.getInitiated(callback);
    }

    @Override
    public void getLoggedIn(AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.getLoggedIn(callback);
    }

    @Override
    public void getOther(AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.getOther(callback);
    }

    @Override
    public void getReleased(AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.getReleased(callback);
    }

    @Override
    public void getToBeVerified(AsyncCallback<ArrayList<ToDoSampleViewVO>> callback) {
        service.getToBeVerified(callback);
    }

    @Override
    public void getWorksheet(AsyncCallback<ArrayList<ToDoWorksheetVO>> callback) {
        service.getWorksheet(callback);
    }

    @Override
    public ArrayList<AnalysisViewVO> getLoggedIn() throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.getLoggedIn(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalysisViewVO> getInitiated() throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.getInitiated(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalysisViewVO> getCompleted() throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.getCompleted(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalysisViewVO> getReleased() throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.getReleased(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception {
        Callback<ArrayList<ToDoSampleViewVO>> callback;
        
        callback = new Callback<ArrayList<ToDoSampleViewVO>>();
        service.getToBeVerified(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalysisViewVO> getOther() throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.getOther(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception {
        Callback<ArrayList<ToDoWorksheetVO>> callback;
        
        callback = new Callback<ArrayList<ToDoWorksheetVO>>();
        service.getWorksheet(callback);
        return callback.getResult();
    }

}
