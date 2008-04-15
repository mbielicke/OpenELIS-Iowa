package org.openelis.modules.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.QueryCheckField;
import org.openelis.gwt.common.data.QueryDateField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenAToZPanel;
import org.openelis.gwt.screen.ScreenAbsolute;
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenButtonPanel;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenHTML;
import org.openelis.gwt.screen.ScreenHorizontal;
import org.openelis.gwt.screen.ScreenLabel;
import org.openelis.gwt.screen.ScreenMaskedBox;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuLabel;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenQueryTableWidget;
import org.openelis.gwt.screen.ScreenRadio;
import org.openelis.gwt.screen.ScreenStack;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTablePanel;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenText;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindowBrowser;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.ProxyListener;
import org.openelis.gwt.widget.QueryCheck;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableCalendar;
import org.openelis.gwt.widget.table.TableCheck;
import org.openelis.gwt.widget.table.TableCollection;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableMaskedTextBox;
import org.openelis.gwt.widget.table.TableTextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELIS implements EntryPoint {
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
	RootPanel.get("main").add((AppScreen)ClassFactory.forName("OpenELIS"));
  }
  
  public void setWidgetMap() {
	  ClassFactory.addClass(new String[] {"OpenELIS"},
			                new ClassFactory.Factory() {
		  	                   private org.openelis.modules.main.client.openelis.OpenELIS mainScreen;
		  	                   public Object newInstance(Object[] args){
		  	                	   if(mainScreen == null)
		  	                		   mainScreen = new org.openelis.modules.main.client.openelis.OpenELIS();
		  	                	   return mainScreen;
		  	                   }
	  });
      ClassFactory.addClass(new String[] {"ScreenVertical",ScreenVertical.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenVertical();
                                      else if(args[0] instanceof Node)
                                          return new ScreenVertical((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenHorizontal",ScreenHorizontal.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHorizontal();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHorizontal((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTablePanel",ScreenTablePanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object args[]) {
                                      if(args == null)
                                          return new ScreenTablePanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTablePanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
     /* ClassFactory.addClass(new String[] {"ScreenDeck",ScreenDeck.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object args[]) {
                                      if(args == null)
                                          return new ScreenDeck();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDeck((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClass(new String[] {"ScreenHorizontalSplit",ScreenHorizontalSplit.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHorizontalSplit();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHorizontalSplit((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      */
      ClassFactory.addClass(new String[] {"ScreenAbsolute",ScreenAbsolute.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAbsolute();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAbsolute((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenAppButton",ScreenAppButton.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAppButton();
                                      else if(args[0] instanceof Node) 
                                         return new ScreenAppButton((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenButtonPanel",ScreenButtonPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenButtonPanel();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenButtonPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenCalendar",ScreenCalendar.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCalendar();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenCalendar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenCheck",ScreenCheck.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCheck();
                                      else if(args[0] instanceof Node)
                                          return new ScreenCheck((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenQueryTableWidget",ScreenQueryTableWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenQueryTableWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenQueryTableWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      /*
      ClassFactory.addClass(new String[] {"ScreenDragSelect",ScreenDragSelect.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenDragSelect();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDragSelect((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenImage",ScreenImage.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenImage();
                                      else if(args[0] instanceof Node)
                                          return new ScreenImage((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      */
      ClassFactory.addClass(new String[] {"ScreenMaskedBox",ScreenMaskedBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMaskedBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMaskedBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenRadio",ScreenRadio.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenRadio();
                                      else if(args[0] instanceof Node)
                                          return new ScreenRadio((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTableWidget",ScreenTableWidget.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTableWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTableWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenText",ScreenText.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenText();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenText((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTextArea",ScreenTextArea.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextArea();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextArea((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTextBox",ScreenTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      /*
      ClassFactory.addClass(new String[] {"ScreenTabBrowser",ScreenTabBrowser.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTabBrowser();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTabBrowser((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTree",ScreenTree.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTree();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTree((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      */
      ClassFactory.addClass(new String[] {"TableCalendar",TableCalendar.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCalendar();
                                      else if(args[0] instanceof Node)
                                          return new TableCalendar((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableLabel",TableLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableLabel();
                                      else if(args[0] instanceof Node)
                                          return new TableLabel((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableCheck",TableCheck.TAG_NAME},
              new ClassFactory.Factory() {
                    public Object newInstance(Object[] args) {
                        if(args == null)
                            return new TableCheck();
                        else if(args[0] instanceof Node)
                            return new TableCheck((Node)args[0]);
                        return null;
                    }
      });
      ClassFactory.addClass(new String[] {"TableMaskedTextBox",TableMaskedTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableMaskedTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableMaskedTextBox((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableTextBox",TableTextBox.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableTextBox((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableAutoDropdown",TableAutoDropdown.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableAutoDropdown();
                                      else if(args[0] instanceof Node)
                                          return new TableAutoDropdown((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableCollection",TableCollection.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCollection();
                                      else if(args[0] instanceof Node)
                                          return new TableCollection((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenLabel",ScreenLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuPanel",ScreenMenuPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuItem",ScreenMenuItem.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args){
                                      if(args == null)
                                          return new ScreenMenuItem();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuItem((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuLabel",ScreenMenuLabel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"CheckField",CheckField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new CheckField();
                                      else if(args[0] instanceof Node)
                                          return new CheckField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ModelField",ModelField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ModelField();
                                      else if(args[0] instanceof Node)
                                          return new ModelField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"DateField",DateField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new DateField();
                                      else if(args[0] instanceof Node)
                                          return new DateField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"NumberField",NumberField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new NumberField();
                                      else if(args[0] instanceof Node)
                                          return new NumberField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"StringField",StringField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new StringField();
                                      else if(args[0] instanceof Node)
                                          return new StringField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"CollectionField",CollectionField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new CollectionField();
                                      else if(args[0] instanceof Node)
                                          return new CollectionField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableField",TableField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableField();
                                      else if (args[0] instanceof Node)
                                          return new TableField((Node)args[0]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClass(new String[] {"PagedTreeField",PagedTreeField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new PagedTreeField();
                                      else if (args[0] instanceof Node)
                                          return new PagedTreeField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryCheckField",QueryCheckField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryCheckField();
                                      else if (args[0] instanceof Node)
                                          return new QueryCheckField((Node)args[0]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClass(new String[] {"QueryDateField",QueryDateField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryDateField();
                                      else if (args[0] instanceof Node)
                                          return new QueryDateField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryNumberField",QueryNumberField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryNumberField();
                                      else if (args[0] instanceof Node)
                                          return new QueryNumberField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryStringField",QueryStringField.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new QueryStringField();
                                      else if (args[0] instanceof Node)
                                          return new QueryStringField((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTab",ScreenTab.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTab();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTab((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenStack",ScreenStack.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenStack();
                                      else if(args[0] instanceof Node)
                                          return new ScreenStack((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenWindowBrowser",ScreenWindowBrowser.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenWindowBrowser();
                                      else if(args[0] instanceof Node)
                                          return new ScreenWindowBrowser((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenHTML",ScreenHTML.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHTML();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHTML((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ProxyListener","ProxyListener"},
                            new ClassFactory.Factory() {
                                  private ProxyListener listener;
                                  public Object newInstance(Object[] args) {
                                      if(listener == null)
                                          listener = new ProxyListener();
                                      return listener;
                                  }
      });
      ClassFactory.addClass(new String[] {"HoverListener","HoverListener"},
                            new ClassFactory.Factory() {
                                  private HoverListener listener;
                                  public Object newInstance(Object[] args) {
                                      if(listener == null)
                                          listener = new HoverListener();
                                      return listener;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenAToZPanel",ScreenAToZPanel.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAToZPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAToZPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClass(new String[] {"ScreenPagedTree",ScreenPagedTree.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenPagedTree();
                                      else if(args[0] instanceof Node)
                                          return new ScreenPagedTree((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      
      ClassFactory.addClass(new String[] {"ScreenAutoDropdown",ScreenAutoDropdown.TAG_NAME},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAutoDropdown();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAutoDropdown((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"DropDownField",DropDownField.TAG_NAME}, 
                            new ClassFactory.Factory() {
                                public Object newInstance(Object[] args) {
                                    if(args == null)
                                        return new DropDownField();
                                    else if(args[0] instanceof Node)
                                        return new DropDownField((Node)args[0]);
                                    return null;
                                }
                           
      });
  }
  
}
