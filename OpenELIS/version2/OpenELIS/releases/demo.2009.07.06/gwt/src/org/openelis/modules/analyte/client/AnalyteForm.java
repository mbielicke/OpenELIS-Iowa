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
package org.openelis.modules.analyte.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.AnalyteMetaMap;

import com.google.gwt.xml.client.Node;

public class AnalyteForm extends Form<Integer>{
    private static final long serialVersionUID = 1L;

  	public IntegerField id;
  	public StringField name;
  	public DropDownField<Integer> parentName;
  	public StringField externalId;
  	public CheckField isActive;
  	
    public AnalyteForm() {
       AnalyteMetaMap meta = new AnalyteMetaMap();
       id = new IntegerField(meta.getId());
       name = new StringField(meta.getName());
       parentName = new DropDownField<Integer>(meta.PARENT_ANALYTE.getName());
       externalId = new StringField(meta.getExternalId());
       isActive = new CheckField(meta.getIsActive());
   }
   
   public AnalyteForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   id,
                                   name,
                                   parentName,
                                   externalId,
                                   isActive
       };
   }
   
}