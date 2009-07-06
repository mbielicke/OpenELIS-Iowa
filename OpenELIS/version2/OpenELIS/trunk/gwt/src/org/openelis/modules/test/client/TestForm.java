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
package org.openelis.modules.test.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.TestMetaMap;

public class TestForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public IntegerField id; 
    public StringField name;
    public DropDownField<Integer> methodId;
    public StringField description; 
    public StringField reportingDescription;
    public IntegerField timeTaMax;
    public IntegerField timeTaAverage;
    public IntegerField timeTaWarning;
    public IntegerField timeTransit;
    public CheckField isActive;
    public CheckField isReportable;
    public DateField activeBegin;
    public DateField activeEnd;
    public IntegerField timeHolding;
    public DropDownField<Integer> labelId;
    public IntegerField labelQty;
    public DropDownField<Integer> testTrailerId;
    public DropDownField<Integer> testFormatId;
    public DropDownField<Integer> scriptletId;
    public DropDownField<Integer> revisionMethodId;
    public DropDownField<Integer> reportingMethodId;
    public IntegerField reportingSequence;
    public DropDownField<Integer> sortingMethodId;
    public TableField<TableDataRow<Integer>> sectionTable;
    public Boolean duplicate;
    public String testTabPanel = "detailsTab";
    public PrepAndReflexForm prepAndReflex;
    public SampleTypeForm sampleType;
    public TestAnalyteForm testAnalyte;
    public WorksheetForm worksheet;
    
    public TableDataModel<TableDataRow<Integer>> resultTableModel;
    
    public Integer numGroups;
    
    public TestForm() {
        TestMetaMap meta = new TestMetaMap();
        id = new IntegerField(meta.getId());
        name = new StringField(meta.getName());
        methodId = new DropDownField<Integer>(meta.getMethod().getName());
        description = new StringField(meta.getDescription());
        reportingDescription = new StringField(meta.getReportingDescription());
        timeTaMax = new IntegerField(meta.getTimeTaMax());
        timeTaAverage = new IntegerField(meta.getTimeTaAverage());
        timeTaWarning = new IntegerField(meta.getTimeTaWarning());
        timeTransit = new IntegerField(meta.getTimeTransit());
        isActive = new CheckField(meta.getIsActive());
        isReportable = new CheckField(meta.getIsReportable());
        activeBegin = new DateField(meta.getActiveBegin());
        activeEnd = new DateField(meta.getActiveEnd());
        timeHolding = new IntegerField(meta.getTimeHolding());
        labelId = new DropDownField<Integer>(meta.getLabel().getName());
        labelQty = new IntegerField(meta.getLabelQty());
        testTrailerId = new DropDownField<Integer>(meta.getTestTrailerId());
        testFormatId = new DropDownField<Integer>(meta.getTestFormatId());
        scriptletId = new DropDownField<Integer>(meta.getScriptlet().getName());
        revisionMethodId = new DropDownField<Integer>(meta.getRevisionMethodId());
        reportingMethodId = new DropDownField<Integer>(meta.getReportingMethodId());
        reportingSequence = new IntegerField(meta.getReportingSequence());
        sortingMethodId = new DropDownField<Integer>(meta.getSortingMethodId());
        sectionTable = new TableField<TableDataRow<Integer>>("sectionTable");        
        prepAndReflex = new PrepAndReflexForm("prepAndReflex");
        sampleType = new SampleTypeForm("sampleType");
        testAnalyte = new TestAnalyteForm("testAnalyte");
        worksheet = new WorksheetForm("worksheet");
    }
    
    public TestForm(Node node) {
        this();
        createFields(node);
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    name,
                                    methodId,
                                    description,
                                    reportingDescription,
                                    timeTaMax,
                                    timeTaAverage,
                                    timeTaWarning,
                                    timeTransit,
                                    isActive,
                                    isReportable,
                                    activeBegin,
                                    activeEnd,
                                    timeHolding,
                                    labelId,
                                    labelQty,
                                    testTrailerId,
                                    testFormatId,
                                    scriptletId,
                                    revisionMethodId,
                                    reportingMethodId,
                                    reportingSequence,
                                    sortingMethodId,
                                    sectionTable,
                                    prepAndReflex,
                                    sampleType,
                                    testAnalyte,
                                    worksheet
        };
    }

}
