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

import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used for transporting the data needed for making choices about
 * which QC Lot to put in worksheet positions where there are more than one active
 * QC matching the name for the specified position.
 */
public class WorksheetQcChoiceVO implements Serializable {

    private static final long                    serialVersionUID = 1L;

    protected ArrayList<WorksheetAnalysisViewDO> newAnalyses, removedAnalyses;
    protected ArrayList<String>                  choiceUids, reagentChoiceUids;
    protected ArrayList<QcLotViewDO>             choices, reagentChoices;
    protected ValidationErrorsList               errors;
    protected WorksheetManager1                  manager;

    public WorksheetManager1 getManager() {
        return manager;
    }

    public void setManager(WorksheetManager1 manager) {
        this.manager = manager;
    }

    public void addChoices(ArrayList<QcLotViewDO> newChoices) {
        if (choices == null)
            choices = new ArrayList<QcLotViewDO>();
        choices.addAll(newChoices);
    }

    public ArrayList<QcLotViewDO> getChoices() {
        return choices;
    }
    
    public void addChoiceUids(ArrayList<String> newUids) {
        if (choiceUids == null)
            choiceUids = new ArrayList<String>();
        choiceUids.addAll(newUids);
    }

    public ArrayList<String> getChoiceUids() {
        return choiceUids;
    }
    
    public void addReagentChoices(ArrayList<QcLotViewDO> newChoices) {
        if (reagentChoices == null)
            reagentChoices = new ArrayList<QcLotViewDO>();
        reagentChoices.addAll(newChoices);
    }

    public ArrayList<QcLotViewDO> getReagentChoices() {
        return reagentChoices;
    }
    
    public void addReagentChoiceUids(ArrayList<String> newUids) {
        if (reagentChoiceUids == null)
            reagentChoiceUids = new ArrayList<String>();
        reagentChoiceUids.addAll(newUids);
    }

    public ArrayList<String> getReagentChoiceUids() {
        return reagentChoiceUids;
    }
    
    public ArrayList<WorksheetAnalysisViewDO> getNewAnalyses() {
        return newAnalyses;
    }
    
    public void setNewAnalyses(ArrayList<WorksheetAnalysisViewDO> analyses) {
        newAnalyses = analyses;
    }

    public ArrayList<WorksheetAnalysisViewDO> getRemovedAnalyses() {
        return removedAnalyses;
    }
    
    public void setRemovedAnalyses(ArrayList<WorksheetAnalysisViewDO> analyses) {
        removedAnalyses = analyses;
    }

    public ValidationErrorsList getErrors() {
        return errors;
    }

    public void setErrors(ValidationErrorsList errors) {
        this.errors = errors;
    }
}