/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;

public class WorksheetResultsTransferVO implements Serializable {

    private static final long    serialVersionUID = 1L;

    protected ArrayList<ArrayList<ResultViewDO>> reflexResultsList;
    protected ArrayList<SampleManager1> sampleManagers;
    protected WorksheetManager1 worksheetManager;

    protected WorksheetResultsTransferVO() {
    }

    public WorksheetResultsTransferVO(WorksheetManager1 worksheetManager, ArrayList<SampleManager1> sampleManagers) {
        this.worksheetManager = worksheetManager;
        this.sampleManagers = sampleManagers;
    }

    public WorksheetResultsTransferVO(WorksheetManager1 worksheetManager, ArrayList<SampleManager1> sampleManagers,
                                      ArrayList<ArrayList<ResultViewDO>> reflexResultsList) {
        this.worksheetManager = worksheetManager;
        this.sampleManagers = sampleManagers;
        this.reflexResultsList = reflexResultsList;
    }

    public WorksheetManager1 getWorksheetManager() {
        return worksheetManager;
    }

    public ArrayList<SampleManager1> getSampleManagers() {
        return sampleManagers;
    }

    public ArrayList<ArrayList<ResultViewDO>> getReflexResultsList() {
        return reflexResultsList;
    }
}