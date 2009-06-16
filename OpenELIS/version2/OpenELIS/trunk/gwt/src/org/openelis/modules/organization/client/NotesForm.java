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
    public transient StringField subject;
    public transient StringField text;
    public transient StringField notesPanel;
      
    public ArrayList<NoteDO> data;
    
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
      
      public void load(ArrayList<NoteDO> data) {
      Iterator itr = data.iterator();
      try{
      Document doc = XMLParser.parse("<panel/>");
      Element root = (Element) doc.getDocumentElement();
      root.setAttribute("key", "notePanel");   
      int i=0;
      while(itr.hasNext()){
          NoteDO noteRow = (NoteDO)itr.next();
          
          //user id
          String userName = noteRow.getSystemUser();
          //body
          String body = noteRow.getText();
          
          if(body == null)
              body = "";
          
          //date
          String date = noteRow.getTimestamp().toString();
          //subject
          String subject = noteRow.getSubject();
          
          if(subject == null)
              subject = "";
                      
           Element mainRowPanel = (Element) doc.createElement("VerticalPanel");
           Element topRowPanel = (Element) doc.createElement("HorizontalPanel");
           Element titleWidgetTag = (Element) doc.createElement("widget");
           Element titleText = (Element) doc.createElement("text");
           Element authorWidgetTag = (Element) doc.createElement("widget");
           Element authorPanel = (Element) doc.createElement("VerticalPanel");
           Element dateText = (Element) doc.createElement("text");
           Element authorText = (Element) doc.createElement("text");
           Element bodyWidgetTag = (Element) doc.createElement("widget");
           Element bodytextTag =  (Element) doc.createElement("text");
           
           mainRowPanel.setAttribute("key", "note"+i);
           if(i % 2 == 1){
               mainRowPanel.setAttribute("style", "AltTableRow");
           }else{
               mainRowPanel.setAttribute("style", "TableRow");
           }
           mainRowPanel.setAttribute("width", "530px");
           
           topRowPanel.setAttribute("width", "530px");
           titleText.setAttribute("key", "note"+i+"Title");
           titleText.setAttribute("style", "notesSubjectText");
           titleText.appendChild(doc.createTextNode(subject));
           authorWidgetTag.setAttribute("halign", "right");
           dateText.setAttribute("key", "note"+i+"Date");
           dateText.appendChild(doc.createTextNode(date));
           authorText.setAttribute("key", "note"+i+"Author");
           authorText.appendChild(doc.createTextNode("by "+userName));
           bodytextTag.setAttribute("key", "note"+i+"Body");
           bodytextTag.setAttribute("wordwrap", "true");
           bodytextTag.appendChild(doc.createTextNode(body));
           
           root.appendChild(mainRowPanel);
           mainRowPanel.appendChild(topRowPanel);
           mainRowPanel.appendChild(bodyWidgetTag);
           topRowPanel.appendChild(titleWidgetTag);
           topRowPanel.appendChild(authorWidgetTag);
           titleWidgetTag.appendChild(titleText);
           authorWidgetTag.appendChild(authorPanel);
           authorPanel.appendChild(dateText);
           authorPanel.appendChild(authorText);
           bodyWidgetTag.appendChild(bodytextTag);
           
           i++;
    }
      
      notesPanel.setValue(doc.toString());
      }catch(Exception e) {
          
      }
      }
}
