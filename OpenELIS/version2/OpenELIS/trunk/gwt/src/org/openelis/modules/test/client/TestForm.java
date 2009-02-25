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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.TestMetaMap;

import com.google.gwt.xml.client.Node;

public class TestForm extends Form {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IntegerField id; 
    public StringField name;
    public DropDownField methodId;
    public String testTabPanel = "detailsTab";
    public DetailsForm details;
    public PrepAndReflexForm prepAndReflex;
    public SampleTypeForm sampleType;
    public TestAnalyteForm testAnalyte;
    public WorksheetForm worksheet;
    
    public TestForm() {
        TestMetaMap meta = new TestMetaMap();
        fields.put(meta.getId(), id = new IntegerField());
        fields.put(meta.getName(), name = new StringField());
        fields.put(meta.getMethodId(), methodId = new DropDownField());
        fields.put("details",details = new DetailsForm());
        fields.put("prepAndReflex", prepAndReflex = new PrepAndReflexForm());
        fields.put("sampleType", sampleType = new SampleTypeForm());
        fields.put("testAnalyte", testAnalyte = new TestAnalyteForm());
        fields.put("worksheet", worksheet = new WorksheetForm());        
    }
    
    public TestForm(Node node) {
        this();
        createFields(node);
    }

}
