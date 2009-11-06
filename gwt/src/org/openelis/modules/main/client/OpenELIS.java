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
package org.openelis.modules.main.client;

import org.openelis.gwt.common.data.deprecated.CheckField;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DoubleField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.IntegerField;
import org.openelis.gwt.common.data.deprecated.QueryCheckField;
import org.openelis.gwt.common.data.deprecated.QueryDateField;
import org.openelis.gwt.common.data.deprecated.QueryDoubleField;
import org.openelis.gwt.common.data.deprecated.QueryIntegerField;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.data.deprecated.TableField;
import org.openelis.gwt.common.data.deprecated.TreeField;
import org.openelis.gwt.screen.deprecated.ClassFactory;
import org.openelis.gwt.screen.deprecated.ScreenAToZTable;
import org.openelis.gwt.screen.deprecated.ScreenAbsolute;
import org.openelis.gwt.screen.deprecated.ScreenAppButton;
import org.openelis.gwt.screen.deprecated.ScreenAutoCompleteWidget;
import org.openelis.gwt.screen.deprecated.ScreenBase;
import org.openelis.gwt.screen.deprecated.ScreenButtonPanel;
import org.openelis.gwt.screen.deprecated.ScreenCalendar;
import org.openelis.gwt.screen.deprecated.ScreenCheck;
import org.openelis.gwt.screen.deprecated.ScreenCollapsePanel;
import org.openelis.gwt.screen.deprecated.ScreenCommandButton;
import org.openelis.gwt.screen.deprecated.ScreenDropDownWidget;
import org.openelis.gwt.screen.deprecated.ScreenHTML;
import org.openelis.gwt.screen.deprecated.ScreenHorizontal;
import org.openelis.gwt.screen.deprecated.ScreenLabel;
import org.openelis.gwt.screen.deprecated.ScreenLookUp;
import org.openelis.gwt.screen.deprecated.ScreenMaskedBox;
import org.openelis.gwt.screen.deprecated.ScreenMenuItem;
import org.openelis.gwt.screen.deprecated.ScreenMenuLabel;
import org.openelis.gwt.screen.deprecated.ScreenMenuPanel;
import org.openelis.gwt.screen.deprecated.ScreenMultipleLookUp;
import org.openelis.gwt.screen.deprecated.ScreenRadio;
import org.openelis.gwt.screen.deprecated.ScreenResultsTable;
import org.openelis.gwt.screen.deprecated.ScreenRichTextArea;
import org.openelis.gwt.screen.deprecated.ScreenScrollableTabBar;
import org.openelis.gwt.screen.deprecated.ScreenStack;
import org.openelis.gwt.screen.deprecated.ScreenTabPanel;
import org.openelis.gwt.screen.deprecated.ScreenTablePanel;
import org.openelis.gwt.screen.deprecated.ScreenTableWidget;
import org.openelis.gwt.screen.deprecated.ScreenText;
import org.openelis.gwt.screen.deprecated.ScreenTextArea;
import org.openelis.gwt.screen.deprecated.ScreenTextBox;
import org.openelis.gwt.screen.deprecated.ScreenTitledPanel;
import org.openelis.gwt.screen.deprecated.ScreenTreeWidget;
import org.openelis.gwt.screen.deprecated.ScreenVertical;
import org.openelis.gwt.screen.deprecated.ScreenWindowBrowser;
import org.openelis.gwt.widget.deprecated.HoverListener;
import org.openelis.gwt.widget.deprecated.ProxyListener;
import org.openelis.gwt.widget.table.deprecated.TableAutoComplete;
import org.openelis.gwt.widget.table.deprecated.TableCalendar;
import org.openelis.gwt.widget.table.deprecated.TableCheck;
import org.openelis.gwt.widget.table.deprecated.TableCollection;
import org.openelis.gwt.widget.table.deprecated.TableDropdown;
import org.openelis.gwt.widget.table.deprecated.TableLabel;
import org.openelis.gwt.widget.table.deprecated.TableMaskedTextBox;
import org.openelis.gwt.widget.table.deprecated.TableTextBox;
import org.openelis.gwt.widget.tree.deprecated.TableTree;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Node;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELIS implements EntryPoint, NativePreviewHandler {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  setWidgetMap();
	  //All Events will flow this this handler first before any other handlers.
	  Event.addNativePreviewHandler(this);
      try {
    	  RootPanel.get("main").add(new org.openelis.modules.main.client.openelis.OpenELIS());
      }catch(Throwable e){
    	  e.printStackTrace();
    	  Window.alert("Unable to start app : "+e.getMessage());
      }
  }
  
  public void setWidgetMap() {

      ClassFactory.addClassFactory(new String[] {ScreenVertical.class.getName(),ScreenVertical.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenVertical();
                                      else if(args[0] instanceof Node)
                                          return new ScreenVertical((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenHorizontal.class.getName(),ScreenHorizontal.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHorizontal();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHorizontal((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenTablePanel.class.getName(),ScreenTablePanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object args[]) {
                                      if(args == null)
                                          return new ScreenTablePanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTablePanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenCommandButton.class.getName(),ScreenCommandButton.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object args[]) {
                                             if(args == null)
                                                 return new ScreenCommandButton();
                                             else if(args[0] instanceof Node)
                                                 return new ScreenCommandButton((Node)args[0],(ScreenBase)args[1]);
                                             return null;
                                         }
             });
      ClassFactory.addClassFactory(new String[] {ScreenAbsolute.class.getName(),ScreenAbsolute.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAbsolute();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAbsolute((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenAppButton.class.getName(),ScreenAppButton.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAppButton();
                                      else if(args[0] instanceof Node) 
                                         return new ScreenAppButton((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenButtonPanel.class.getName(),ScreenButtonPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenButtonPanel();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenButtonPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenCalendar.class.getName(),ScreenCalendar.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCalendar();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenCalendar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenRichTextArea.class.getName(),ScreenRichTextArea.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenRichTextArea();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenRichTextArea((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenCheck.class.getName(),ScreenCheck.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCheck();
                                      else if(args[0] instanceof Node)
                                          return new ScreenCheck((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      /*
      ClassFactory.addClassFactory(new String[] {ScreenQueryTable.class.getName(),ScreenQueryTable.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenQueryTable();
                                      else if(args[0] instanceof Node)
                                          return new ScreenQueryTable((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      */
      ClassFactory.addClassFactory(new String[] {ScreenTitledPanel.class.getName(), ScreenTitledPanel.TAG_NAME},
              new ClassFactory.Factory() {
                    public Object newInstance(Object[] args) {
                        if(args == null)
                            return new ScreenTitledPanel();
                        else if(args[0] instanceof Node)
                            return new ScreenTitledPanel((Node)args[0],(ScreenBase)args[1]);
                        return null;
                    }
      });
      ClassFactory.addClassFactory(new String[] {ScreenMaskedBox.class.getName(),ScreenMaskedBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMaskedBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMaskedBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenRadio.class.getName(),ScreenRadio.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenRadio();
                                      else if(args[0] instanceof Node)
                                          return new ScreenRadio((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenTableWidget.class.getName(),ScreenTableWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTableWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTableWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenTreeWidget.class.getName(),ScreenTreeWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTreeWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTreeWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenText.class.getName(),ScreenText.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenText();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenText((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenTextArea.class.getName(),ScreenTextArea.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextArea();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextArea((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenTextBox.class.getName(),ScreenTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableCalendar.class.getName(),TableCalendar.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCalendar();
                                      else if(args[0] instanceof Node)
                                          return new TableCalendar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableLabel.class.getName(),TableLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableLabel();
                                      else if(args[0] instanceof Node)
                                          return new TableLabel((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableCheck.class.getName(),TableCheck.TAG_NAME},
              new ClassFactory.Factory() {
                    public Object newInstance(Object[] args) {
                        if(args == null)
                            return new TableCheck();
                        else if(args[0] instanceof Node)
                            return new TableCheck((Node)args[0],(ScreenBase)args[1]);
                        return null;
                    }
      });
      ClassFactory.addClassFactory(new String[] {TableMaskedTextBox.class.getName(),TableMaskedTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableMaskedTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableMaskedTextBox((Node)args[0], (ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableTextBox.class.getName(),TableTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableTextBox((Node)args[0], (ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableAutoComplete.class.getName(),TableAutoComplete.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableAutoComplete();
                                      else if(args[0] instanceof Node)
                                          return new TableAutoComplete((Node)args[0], (ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableDropdown.class.getName(),TableDropdown.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableDropdown();
                                      else if(args[0] instanceof Node)
                                          return new TableDropdown((Node)args[0], (ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableCollection.class.getName(),TableCollection.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCollection();
                                      else if(args[0] instanceof Node)
                                          return new TableCollection((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableTree.class.getName(),TableTree.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableTree();
                                      else if(args[0] instanceof Node)
                                          return new TableTree((Node)args[0], (ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenLabel.class.getName(),ScreenLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenMenuPanel.class.getName(),ScreenMenuPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenMenuItem.class.getName(),ScreenMenuItem.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args){
                                      if(args == null)
                                          return new ScreenMenuItem();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuItem((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenMenuLabel.class.getName(),ScreenMenuLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {CheckField.class.getName(),CheckField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new CheckField();
                                      else if(args[0] instanceof Node)
                                          return new CheckField((Node)args[0]);
                                      return null;
                                  }
      });

      ClassFactory.addClassFactory(new String[] {DateField.class.getName(),DateField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new DateField();
                                      else if(args[0] instanceof Node)
                                          return new DateField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {IntegerField.class.getName(),IntegerField.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object[] args) {
                                             if(args == null)
                                                 return new IntegerField();
                                             else if(args[0] instanceof Node)
                                                 return new IntegerField((Node)args[0]);
                                             return null;
                                         }
       });
      ClassFactory.addClassFactory(new String[] {DoubleField.class.getName(),DoubleField.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object[] args) {
                                             if(args == null)
                                                 return new DoubleField();
                                             else if(args[0] instanceof Node)
                                                 return new DoubleField((Node)args[0]);
                                             return null;
                                         }
       });
      ClassFactory.addClassFactory(new String[] {StringField.class.getName(),StringField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new StringField();
                                      else if(args[0] instanceof Node)
                                          return new StringField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableField.class.getName(),TableField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableField<TableDataRow>();
                                      else if (args[0] instanceof Node)
                                          return new TableField<TableDataRow>((Node)args[0]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClassFactory(new String[] {TreeField.class.getName(),TreeField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TreeField();
                                      else if (args[0] instanceof Node)
                                          return new TreeField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {QueryCheckField.class.getName(),QueryCheckField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryCheckField();
                                      else if (args[0] instanceof Node)
                                          return new QueryCheckField((Node)args[0]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClassFactory(new String[] {QueryDateField.class.getName(),QueryDateField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryDateField();
                                      else if (args[0] instanceof Node)
                                          return new QueryDateField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {QueryDoubleField.class.getName(),QueryDoubleField.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object[] args) {
                                             if(args == null)
                                                 return new QueryDoubleField();
                                             else if (args[0] instanceof Node)
                                                 return new QueryDoubleField((Node)args[0]);
                                             return null;
                                         }
      });
      ClassFactory.addClassFactory(new String[] {QueryIntegerField.class.getName(),QueryIntegerField.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object[] args) {
                                             if(args == null)
                                                 return new QueryIntegerField();
                                             else if (args[0] instanceof Node)
                                                 return new QueryIntegerField((Node)args[0]);
                                             return null;
                                         }
      });
      ClassFactory.addClassFactory(new String[] {QueryStringField.class.getName(),QueryStringField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryStringField();
                                      else if (args[0] instanceof Node)
                                          return new QueryStringField((Node)args[0]);
                                      return null;
                                  }
      });
     
      ClassFactory.addClassFactory(new String[] {ScreenTabPanel.class.getName(),ScreenTabPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTabPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTabPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenScrollableTabBar.class.getName(),ScreenScrollableTabBar.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenScrollableTabBar();
                                      else if(args[0] instanceof Node)
                                          return new ScreenScrollableTabBar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });      
      ClassFactory.addClassFactory(new String[] {ScreenStack.class.getName(),ScreenStack.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenStack();
                                      else if(args[0] instanceof Node)
                                          return new ScreenStack((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenWindowBrowser.class.getName(),ScreenWindowBrowser.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenWindowBrowser();
                                      else if(args[0] instanceof Node)
                                          return new ScreenWindowBrowser((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenHTML.class.getName(),ScreenHTML.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHTML();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHTML((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ProxyListener.class.getName(),"ProxyListener"},
                            new ClassFactory.Factory() {
                                  private ProxyListener listener;
                                  public Object newInstance(Object[] args) {
                                      if(listener == null)
                                          listener = new ProxyListener();
                                      return listener;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {HoverListener.class.getName(),"HoverListener"},
                            new ClassFactory.Factory() {
                                  private HoverListener listener;
                                  public Object newInstance(Object[] args) {
                                      if(listener == null)
                                          listener = new HoverListener();
                                      return listener;
                                  }
      });      
      
      ClassFactory.addClassFactory(new String[] {ScreenAutoCompleteWidget.class.getName(),ScreenAutoCompleteWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAutoCompleteWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAutoCompleteWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenDropDownWidget.class.getName(),ScreenDropDownWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenDropDownWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDropDownWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClassFactory(new String[] {DropDownField.class.getName(),DropDownField.TAG_NAME}, 
                            new ClassFactory.Factory() {
                                public Object newInstance(Object[] args) {
                                    if(args == null)
                                        return new DropDownField<FieldType>();
                                    else if(args[0] instanceof Node)
                                        return new DropDownField<FieldType>((Node)args[0]);
                                    return null;
                                }
                           
      });
      
      ClassFactory.addClassFactory(new String[] {ScreenCollapsePanel.class.getName(),ScreenCollapsePanel.TAG_NAME}, 
                            new ClassFactory.Factory() {
                                public Object newInstance(Object[] args) {
                                    if(args == null)
                                        return new ScreenCollapsePanel();
                                    else if(args[0] instanceof Node)
                                        return new ScreenCollapsePanel((Node)args[0],(ScreenBase)args[1]);
                                    return null;
                                }
                           
      });
      ClassFactory.addClassFactory(new String[] {ScreenAToZTable.class.getName(),ScreenAToZTable.TAG_NAME}, 
                            new ClassFactory.Factory() {
                                public Object newInstance(Object[] args) {
                                    if(args == null)
                                        return new ScreenAToZTable();
                                    else if(args[0] instanceof Node)
                                        return new ScreenAToZTable((Node)args[0],(ScreenBase)args[1]);
                                    return null;
                                }
                           
      });
      ClassFactory.addClassFactory(new String[] {ScreenResultsTable.class.getName(),ScreenResultsTable.TAG_NAME}, 
                                   new ClassFactory.Factory() {
                                       public Object newInstance(Object[] args) {
                                           if(args == null)
                                               return new ScreenResultsTable();
                                           else if(args[0] instanceof Node)
                                               return new ScreenResultsTable((Node)args[0],(ScreenBase)args[1]);
                                           return null;
                                       }
                                  
             });
      ClassFactory.addClassFactory(new String[] {ScreenLookUp.class.getName(),ScreenLookUp.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenLookUp();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenLookUp((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {ScreenMultipleLookUp.class.getName(),ScreenMultipleLookUp.TAG_NAME},
                                   new ClassFactory.Factory() {
                                         public Object newInstance(Object[] args) {
                                             if(args == null)
                                                 return new ScreenMultipleLookUp();
                                             else if(args[0] instanceof Node) 
                                                 return new ScreenMultipleLookUp((Node)args[0],(ScreenBase)args[1]);
                                             return null;
                                         }
             });
  }

  /**
   * All events created by the application will flow through here.  The event can be inspected for type and other user input
   * then certain actions can be taken such as preventing default browser before or even cancelling events
   */
  public void onPreviewNativeEvent(NativePreviewEvent event) {
	  //This check is to prevent FireFox from highlighting HTML Elements when mouseDown is combined with the ctrl key
	  if(event.getTypeInt() == Event.ONMOUSEDOWN && event.getNativeEvent().getCtrlKey())
		  event.getNativeEvent().preventDefault();
	
  }
  
}
