package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrganizationMetaMap;

/**
 * Specific instance of Form for loading and submitting 
 * data on the NotesTab on the OrganizationScreen
 * 
 * This is included as a SubForm in OrganizationForm
 * @author tschmidt
 *
 */
public class NotesForm extends Form {
    

    private static final long serialVersionUID = 1L;
    public StringField subject;
    public StringField text;
    public StringField notesPanel;
      
      public NotesForm() {
          OrganizationMetaMap meta = new OrganizationMetaMap();
          fields.put(meta.NOTE.getSubject(), subject = new StringField());
          fields.put(meta.NOTE.getText(),text = new StringField());
          fields.put("notesPanel",notesPanel = new StringField());
      }
      
      public NotesForm(Node node){
          this();
          createFields(node);
      }

}
