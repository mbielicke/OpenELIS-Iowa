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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.TestMetaMap;

import com.google.gwt.xml.client.Node;

public class DetailsForm extends Form {

    /**
     * 
     */
    private static final long serialVersionUID = 62980022669194464L;
    
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
    public DropDownField labelId;
    public IntegerField labelQty;
    public DropDownField testTrailerId;
    public DropDownField testFormatId;
    public DropDownField scriptletId;
    public DropDownField revisionMethodId;
    public DropDownField reportingMethodId;
    public IntegerField reportingSequence;
    public DropDownField sortingMethodId;
    public TableField sectionTable;
    public Boolean duplicate;
    
    public DetailsForm() {
       TestMetaMap meta = new TestMetaMap();
       fields.put(meta.getDescription(), description = new StringField());
       fields.put(meta.getReportingDescription(), reportingDescription = new StringField());
       fields.put(meta.getTimeTaMax(), timeTaMax = new IntegerField());
       fields.put(meta.getTimeTaAverage(), timeTaAverage = new IntegerField());
       fields.put(meta.getTimeTaWarning(), timeTaWarning = new IntegerField());
       fields.put(meta.getTimeTransit(), timeTransit = new IntegerField());
       fields.put(meta.getIsActive(), isActive = new CheckField());
       fields.put(meta.getIsReportable(), isReportable = new CheckField());
       fields.put(meta.getActiveBegin(), activeBegin = new DateField());
       fields.put(meta.getActiveEnd(), activeEnd = new DateField());
       fields.put(meta.getTimeHolding(), timeHolding = new IntegerField());
       fields.put(meta.getLabelId(), labelId = new DropDownField());
       fields.put(meta.getLabelQty(), labelQty = new IntegerField());
       fields.put(meta.getTestTrailerId(), testTrailerId = new DropDownField());
       fields.put(meta.getTestFormatId(), testFormatId = new DropDownField());
       fields.put(meta.getScriptletId(), scriptletId = new DropDownField());
       fields.put(meta.getRevisionMethodId(), revisionMethodId = new DropDownField());
       fields.put(meta.getReportingMethodId(), reportingMethodId = new DropDownField());
       fields.put(meta.getReportingSequence(), reportingSequence = new IntegerField());
       fields.put(meta.getSortingMethodId(), sortingMethodId = new DropDownField());
       fields.put("sectionTable",sectionTable = new TableField());       
    }
    
    public DetailsForm(Node node) {
        this();
        createFields(node);
    }
}
