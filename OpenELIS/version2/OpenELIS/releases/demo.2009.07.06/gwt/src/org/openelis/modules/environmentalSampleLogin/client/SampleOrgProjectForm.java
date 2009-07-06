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
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.SampleEnvironmentalMetaMap;

import com.google.gwt.xml.client.Node;

public class SampleOrgProjectForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;

    public DropDownField<Integer> projectName;
    public DropDownField<Integer> reportToName;
    public DropDownField<Integer> billToName;
    
    public SampleOrganizationForm sampleOrgForm;
    public SampleProjectForm sampleProjectForm;
    
    public SampleOrgProjectForm() {
        SampleEnvironmentalMetaMap meta = new SampleEnvironmentalMetaMap();
        projectName = new DropDownField<Integer>(meta.SAMPLE.SAMPLE_PROJECT.PROJECT.getName());
        reportToName = new DropDownField<Integer>(meta.SAMPLE.SAMPLE_ORGANIZATION.ORGANIZATION.getName());
        billToName = new DropDownField<Integer>("billTo");
        sampleOrgForm = new SampleOrganizationForm("sampleOrganization");
        sampleProjectForm = new SampleProjectForm("sampleProject");
   }
   
   public SampleOrgProjectForm(Node node) {
       this();
       createFields(node);
   }
   
   public SampleOrgProjectForm(String key) {
       this();
       this.key = key;
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   projectName,
                                   reportToName,
                                   billToName,
                                   sampleOrgForm,
                                   sampleProjectForm
       };
   }
}
