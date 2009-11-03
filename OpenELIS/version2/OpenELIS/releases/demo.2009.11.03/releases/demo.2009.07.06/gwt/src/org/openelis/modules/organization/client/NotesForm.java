package org.openelis.modules.organization.client;


import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrganizationMetaMap;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Specific instance of Form for loading and submitting 
 * data on the NotesTab on the OrganizationScreen
 * 
 * This is included as a SubForm in OrganizationForm
 * @author tschmidt
 *
 */
public class NotesForm extends Form<Integer> {
    

    private static final long serialVersionUID = 1L;
    public StringField subject;
    public StringField text;
    public StringField notesPanel;
      
    public NotesForm() {
          OrganizationMetaMap meta = new OrganizationMetaMap();
          subject = new StringField(meta.NOTE.getSubject());
          text = new StringField(meta.NOTE.getText());
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
