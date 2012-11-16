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

import org.openelis.domain.ToDoAnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.server.EJBFactory;

public class ToDoService {

    public ArrayList<ToDoAnalysisViewVO> getLoggedIn() throws Exception {
        return EJBFactory.getToDo().getLoggedIn();
    }
    
    public ArrayList<ToDoAnalysisViewVO> getInitiated() throws Exception {
        return EJBFactory.getToDo().getInitiated();
    }
    
    public ArrayList<ToDoAnalysisViewVO> getCompleted() throws Exception {
        return EJBFactory.getToDo().getCompleted();
    }
    
    public ArrayList<ToDoAnalysisViewVO> getReleased() throws Exception {
        return EJBFactory.getToDo().getReleased();
    }
    
    public ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception {
        return EJBFactory.getToDo().getToBeVerified();
    }
    
    public ArrayList<ToDoAnalysisViewVO> getOther() throws Exception {
        return EJBFactory.getToDo().getOther();
    }
    
    public ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception {
        return EJBFactory.getToDo().getWorksheet();
    }   
}

