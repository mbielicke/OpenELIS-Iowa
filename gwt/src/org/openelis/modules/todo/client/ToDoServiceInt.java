package org.openelis.modules.todo.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("todo")
public interface ToDoServiceInt extends RemoteService {

    ArrayList<AnalysisViewVO> getLoggedIn() throws Exception;

    ArrayList<AnalysisViewVO> getInitiated() throws Exception;

    ArrayList<AnalysisViewVO> getCompleted() throws Exception;

    ArrayList<AnalysisViewVO> getReleased() throws Exception;

    ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception;

    ArrayList<AnalysisViewVO> getOther() throws Exception;

    ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception;

}