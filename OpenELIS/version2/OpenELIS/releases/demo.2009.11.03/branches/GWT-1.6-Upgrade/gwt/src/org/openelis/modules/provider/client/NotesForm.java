package org.openelis.modules.provider.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.ProviderMetaMap;

/**
 * Specific instance of Form for loading and submitting 
 * data on the notes tab on the ProviderScreen
 * 
 * This is included as a SubForm in ProviderForm
 * @author akampoow
 *
 */
public class NotesForm extends Form {
        
    private static final long serialVersionUID = 1L;
    public StringField subject;
    public StringField text;
    public StringField notesPanel;
      
      public NotesForm() {
          ProviderMetaMap meta = new ProviderMetaMap();
          fields.put(meta.getNote().getSubject(), subject = new StringField());
          fields.put(meta.getNote().getText(),text = new StringField());
          fields.put("notesPanel",notesPanel = new StringField());
      }
      
      public NotesForm(Node node){
          this();
          createFields(node);
      }

}
