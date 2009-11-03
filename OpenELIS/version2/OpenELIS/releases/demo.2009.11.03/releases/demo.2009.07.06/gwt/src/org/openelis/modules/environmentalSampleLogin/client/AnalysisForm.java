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
package org.openelis.modules.environmentalSampleLogin.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.AnalysisMetaMap;

import com.google.gwt.xml.client.Node;

public class AnalysisForm extends Form<Integer> {
    public DropDownField<Integer> testId;
    public DropDownField<Integer> methodId;
    public DropDownField<Integer> statusId;
    public IntegerField revision;
    public CheckField isReportable;
    public DropDownField<Integer> sectionId;
    public DateField startedDate;
    public DateField completedDate;
    public DateField releasedDate;
    public DateField printedDate;
    
    private static final long serialVersionUID = 1L;

    public AnalysisForm() {
        AnalysisMetaMap meta = new AnalysisMetaMap("a.");
        testId = new DropDownField<Integer>(meta.TEST.getName());
        methodId = new DropDownField<Integer>(meta.TEST.METHOD.getName());
        statusId = new DropDownField<Integer>(meta.getStatusId());
        revision = new IntegerField(meta.getRevision());
        isReportable = new CheckField(meta.getIsReportable());
        sectionId = new DropDownField<Integer>(meta.getSectionId());
        startedDate = new DateField(meta.getStartedDate());
        completedDate = new DateField(meta.getCompletedDate());
        releasedDate = new DateField(meta.getReleasedDate());
        printedDate = new DateField(meta.getPrintedDate());
        
    }
    
    public AnalysisForm(Node node) {
        this();
        createFields(node);
    }
    
    public AnalysisForm(String key) {
        this();
        this.key = key;
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    testId,
                                    methodId,
                                    statusId,
                                    revision,
                                    isReportable,
                                    sectionId,
                                    startedDate,
                                    completedDate,
                                    releasedDate,
                                    printedDate
        };
    }
}
