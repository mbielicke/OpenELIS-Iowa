/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.todo.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ToDoBean;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.todo.client.ToDoServiceInt;

@WebServlet("/openelis/todo")
public class ToDoServlet extends RemoteServlet implements ToDoServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    ToDoBean toDo;

    public ArrayList<AnalysisViewVO> getLoggedIn() throws Exception {
        return toDo.getLoggedIn();
    }
    
    public ArrayList<AnalysisViewVO> getInitiated() throws Exception {
        return toDo.getInitiated();
    }
    
    public ArrayList<AnalysisViewVO> getCompleted() throws Exception {
        return toDo.getCompleted();
    }
    
    public ArrayList<AnalysisViewVO> getReleased() throws Exception {
        return toDo.getReleased();
    }
    
    public ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception {
        return toDo.getToBeVerified();
    }
    
    public ArrayList<AnalysisViewVO> getOther() throws Exception {
        return toDo.getOther();
    }
    
    public ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception {
        return toDo.getWorksheet();
    }   
}