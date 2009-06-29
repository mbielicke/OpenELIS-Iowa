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
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.SampleEnvironmentalMetaMap;

import com.google.gwt.xml.client.Node;

public class EnvironmentalSampleLoginForm extends Form<Integer> {
private static final long serialVersionUID = 1L;
    
    public IntegerField id;
    public IntegerField accessionNumber;
    public IntegerField orderNumber;
    public DateField collectionDate;
    public DateField collectionTime;
    public DateField receivedDate;
    public DropDownField<Integer> statusId;
    public StringField clientReference;
    
    public EnvironmentalSubForm envInfoForm;
    public SampleItemAndAnalysisForm sampleItemAndAnalysisForm;
    public SampleOrgProjectForm orgProjectForm;
    public SampleItemForm sampleItemForm;
    public TestInfoForm testInfoForm;
    public AnalysisForm analysisForm;
    public ExternalCommentForm externalCommentForm;
    public InternalCommentForm internalCommentForm;
    
    public Integer nextItemSequence;
    
    public EnvironmentalSampleLoginForm() {
        SampleEnvironmentalMetaMap meta = new SampleEnvironmentalMetaMap();
        id = new IntegerField(meta.SAMPLE.getId());
        accessionNumber = new IntegerField(meta.SAMPLE.getAccessionNumber());
        orderNumber = new IntegerField("orderNumber");
        collectionDate = new DateField(meta.SAMPLE.getCollectionDate());
        collectionTime = new DateField(meta.SAMPLE.getCollectionTime());
        receivedDate = new DateField(meta.SAMPLE.getReceivedDate());
        statusId = new DropDownField<Integer>(meta.SAMPLE.getStatusId());
        clientReference = new StringField(meta.SAMPLE.getClientReference());
        
        //forms
        envInfoForm = new EnvironmentalSubForm("envInfo");
        sampleItemAndAnalysisForm = new SampleItemAndAnalysisForm("sampleItemAndAnalysis");
        orgProjectForm = new SampleOrgProjectForm("orgprojectInfo");
        sampleItemForm = new SampleItemForm("sampleItemForm");
        testInfoForm = new TestInfoForm("testInfoResult");
        analysisForm = new AnalysisForm("analysis");
        externalCommentForm = new ExternalCommentForm("analysisExternalComment");
        internalCommentForm = new InternalCommentForm("analysisInternalComments");
   }
   
   public EnvironmentalSampleLoginForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                       id,
                                       accessionNumber,
                                       orderNumber,
                                       collectionDate,
                                       collectionTime,
                                       receivedDate,
                                       statusId,
                                       clientReference,
                                       envInfoForm,
                                       sampleItemAndAnalysisForm,
                                       sampleItemForm,
                                       orgProjectForm,
                                       testInfoForm,
                                       analysisForm,
                                       externalCommentForm,
                                       internalCommentForm
       };
   }
}
