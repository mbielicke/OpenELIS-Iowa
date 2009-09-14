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

package org.openelis.modules.provider.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.metamap.ProviderMetaMap;

import com.google.gwt.xml.client.Node;

/**
 * Specific instance of Form for loading and submitting 
 * data on the notes tab on the ProviderScreen
 * 
 * This is included as a SubForm in ProviderForm
 * @author akampoow
 *
 */
public class NotesForm extends Form<Integer> {
        
    private static final long serialVersionUID = 1L;
    public StringField subject;
    public StringField text;
    public StringField notesPanel;
      
      public NotesForm() {
          ProviderMetaMap meta = new ProviderMetaMap();
          subject = new StringField(meta.getNote().getSubject());
          text = new StringField(meta.getNote().getText());
          notesPanel = new StringField("notesPanel");
      }
      
      public NotesForm(Node node){
          this();
          createFields(node);
      }
      
      public NotesForm(String key) {
          this();
          this.key = key;
      }
      
      public AbstractField[] getFields() {
          return new AbstractField[] {
                                      subject,
                                      text,
                                      notesPanel
          };
      }

}
