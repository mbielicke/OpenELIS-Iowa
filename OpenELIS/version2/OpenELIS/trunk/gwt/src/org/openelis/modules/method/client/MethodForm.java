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

package org.openelis.modules.method.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.CheckField;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.IntegerField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.metamap.MethodMetaMap;

import com.google.gwt.xml.client.Node;

public class MethodForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public IntegerField id;
    public StringField name;   
    public CheckField isActive;
    public DateField activeBegin;
    public DateField activeEnd;
    public StringField description; 
    public StringField reportingDescription;
    
    public MethodForm() {
        MethodMetaMap meta = new MethodMetaMap();
        id = new IntegerField(meta.getId());
        name = new StringField(meta.getName());
        description = new StringField(meta.getDescription());
        reportingDescription = new StringField(meta.getReportingDescription());
        isActive = new CheckField(meta.getIsActive());
        activeBegin = new DateField(meta.getActiveBegin());
        activeEnd = new DateField(meta.getActiveEnd());
     }
    
    public MethodForm(Node node) {
        this();
        createFields(node);
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    name,
                                    description,
                                    reportingDescription,
                                    isActive,
                                    activeBegin,
                                    activeEnd
        };
    }
    

}
