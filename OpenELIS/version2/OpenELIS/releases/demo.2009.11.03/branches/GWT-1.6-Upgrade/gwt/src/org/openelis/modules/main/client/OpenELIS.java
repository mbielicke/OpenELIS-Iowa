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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.QueryCheckField;
import org.openelis.gwt.common.data.QueryDateField;
import org.openelis.gwt.common.data.QueryDoubleField;
import org.openelis.gwt.common.data.QueryIntegerField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TreeField;
import org.openelis.gwt.screen.AppConstants;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenAToZTable;
import org.openelis.gwt.screen.ScreenAbsolute;
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenAutoCompleteWidget;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenButtonPanel;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenCollapsePanel;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenHTML;
import org.openelis.gwt.screen.ScreenHorizontal;
import org.openelis.gwt.screen.ScreenLabel;
import org.openelis.gwt.screen.ScreenLookUp;
import org.openelis.gwt.screen.ScreenMaskedBox;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuLabel;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenMultipleLookUp;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenQueryTable;
import org.openelis.gwt.screen.ScreenRadio;
import org.openelis.gwt.screen.ScreenRichTextArea;
import org.openelis.gwt.screen.ScreenScrollableTabBar;
import org.openelis.gwt.screen.ScreenStack;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTablePanel;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenText;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTitledPanel;
import org.openelis.gwt.screen.ScreenTreeWidget;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindowBrowser;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.ProxyListener;
import org.openelis.gwt.widget.table.TableAutoComplete;
import org.openelis.gwt.widget.table.TableCalendar;
import org.openelis.gwt.widget.table.TableCheck;
import org.openelis.gwt.widget.table.TableCollection;
import org.openelis.gwt.widget.table.TableDragLabel;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableMaskedTextBox;
import org.openelis.gwt.widget.table.TableTextBox;
import org.openelis.gwt.widget.tree.TableTree;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELIS implements EntryPoint, EventListener {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  setWidgetMap();
      Window.addWindowCloseListener(new WindowCloseListener() {
          public String onWindowClosing() {
               org.openelis.modules.main.client.openelis.OpenELIS.screenService.logout(new AsyncCallback() {
                    public void onSuccess(Object result) {
                    }
                          
                    public void onFailure(Throwable caught) {
                    }
                });
                return null;
         }
                 
        public void onWindowClosed() {
                
        }
    });
    Window.enableScrolling(false);
	RootPanel.get("main").add((AppScreen)ClassFactory.forName("org.openelis.modules.main.client.openelis.OpenELIS"));
  }
  
  public void setWidgetMap() {
	  ClassFactory.addClassFactory(new String[] {org.openelis.modules.main.client.openelis.OpenELIS.class.getName()},
			                new ClassFactory.Factory() {
		  	                   private org.openelis.modules.main.client.openelis.OpenELIS mainScreen;
		  	                   public Object newInstance(Object[] args){
		  	                	   if(mainScreen == null)
		  	                		   mainScreen = new org.openelis.modules.main.client.openelis.OpenELIS();
		  	                	   return mainScreen;
		  	                   }
	  });
      ClassFactory.addClassFactory(new String[] {AppConstants.class.getName(),"AppConstants"},
                            new ClassFactory.Factory() {
                              private AppConstants consts;
                              public Object newInstance(Object[] args){
                                  if(consts == null)
                                      consts = new AppConstants();
                                  return consts;
                              }
      });
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
      ClassFactory.addClassFactory(new String[] {TableDragLabel.class.getName(),TableDragLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableDragLabel();
                                      else if(args[0] instanceof Node)
                                          return new TableDragLabel((Node)args[0],(ScreenBase)args[1]);
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
      ClassFactory.addClassFactory(new String[] {ModelField.class.getName(),ModelField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ModelField();
                                      else if(args[0] instanceof Node)
                                          return new ModelField((Node)args[0]);
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
      ClassFactory.addClassFactory(new String[] {NumberField.class.getName(),NumberField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new NumberField();
                                      else if(args[0] instanceof Node)
                                          return new NumberField((Node)args[0]);
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
      ClassFactory.addClassFactory(new String[] {Form.class.getName(),"rpc-rpc"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new Form();
                                      else if(args[0] instanceof Node)
                                          return new Form((Node)args[0]);
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
      ClassFactory.addClassFactory(new String[] {CollectionField.class.getName(),CollectionField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new CollectionField();
                                      else if(args[0] instanceof Node)
                                          return new CollectionField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClassFactory(new String[] {TableField.class.getName(),TableField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableField<FieldType>();
                                      else if (args[0] instanceof Node)
                                          return new TableField<FieldType>((Node)args[0]);
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
      ClassFactory.addClassFactory(new String[] {QueryNumberField.class.getName(),QueryNumberField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryNumberField();
                                      else if (args[0] instanceof Node)
                                          return new QueryNumberField((Node)args[0]);
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
      ClassFactory.addClassFactory(new String[] {ScreenTab.class.getName(),ScreenTab.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTab();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTab((Node)args[0],(ScreenBase)args[1]);
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
      ClassFactory.addClassFactory(new String[] {ScreenPagedTree.class.getName(),ScreenPagedTree.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenPagedTree();
                                      else if(args[0] instanceof Node)
                                          return new ScreenPagedTree((Node)args[0],(ScreenBase)args[1]);
                                      return null;
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

  public void onBrowserEvent(Event event) {
      Window.alert("module on browser");
      DOM.eventPreventDefault(event);
  }
  
}
