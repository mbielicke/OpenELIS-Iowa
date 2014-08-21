package org.openelis.modules.main.client;

import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.OptionField;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.QueryCheckField;
import org.openelis.gwt.common.data.QueryDateField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryOptionField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenAToZPanel;
import org.openelis.gwt.screen.ScreenAbsolute;
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenAuto;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenButtonPanel;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenDeck;
import org.openelis.gwt.screen.ScreenDragList;
import org.openelis.gwt.screen.ScreenDragSelect;
import org.openelis.gwt.screen.ScreenHTML;
import org.openelis.gwt.screen.ScreenHorizontal;
import org.openelis.gwt.screen.ScreenHorizontalSplit;
import org.openelis.gwt.screen.ScreenImage;
import org.openelis.gwt.screen.ScreenLabel;
import org.openelis.gwt.screen.ScreenMaskedBox;
import org.openelis.gwt.screen.ScreenMenuBar;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuLabel;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.screen.ScreenOption;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenRadio;
import org.openelis.gwt.screen.ScreenStack;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTabBrowser;
import org.openelis.gwt.screen.ScreenTablePanel;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenText;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTitledPanel;
import org.openelis.gwt.screen.ScreenTree;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindowBrowser;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.ProxyListener;
import org.openelis.gwt.widget.WidgetMap;
import org.openelis.gwt.widget.table.TableAuto;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableCalendar;
import org.openelis.gwt.widget.table.TableCheck;
import org.openelis.gwt.widget.table.TableCollection;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableMaskedTextBox;
import org.openelis.gwt.widget.table.TableOption;
import org.openelis.gwt.widget.table.TableTextBox;
import org.openelis.gwt.screen.ScreenBase;
//import org.openelis.modules.main.client.constants.OpenELISConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Node;

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
	RootPanel.get("main").add(new org.openelis.modules.main.client.openelis.OpenELIS());
  }
  
  public void setWidgetMap() {
      ClassFactory.addClass(new String[] {"ScreenVertical",WidgetMap.PANEL_VERTICAL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenVertical();
                                      else if(args[0] instanceof Node)
                                          return new ScreenVertical((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenHorizontal",WidgetMap.PANEL_HORIZONTAL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHorizontal();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHorizontal((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTablePanel",WidgetMap.PANEL_TABLE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object args[]) {
                                      if(args == null)
                                          return new ScreenTablePanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTablePanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenDeck",WidgetMap.PANEL_DECK},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object args[]) {
                                      if(args == null)
                                          return new ScreenDeck();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDeck((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenHorizontalSplit",WidgetMap.PANEL_HSPLIT},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenHorizontalSplit();
                                      else if(args[0] instanceof Node)
                                          return new ScreenHorizontalSplit((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenAbsolute",WidgetMap.PANEL_ABSOLUTE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAbsolute();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAbsolute((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenAppButton",WidgetMap.BUTTON,"appButton"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAppButton();
                                      else if(args[0] instanceof Node) 
                                         return new ScreenAppButton((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenButtonPanel",WidgetMap.BUTTON_PANEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenButtonPanel();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenButtonPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenCalendar",WidgetMap.CALENDAR},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCalendar();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenCalendar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenCheck",WidgetMap.CHECKBOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenCheck();
                                      else if(args[0] instanceof Node)
                                          return new ScreenCheck((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenDragSelect",WidgetMap.DRAG_SELECT},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenDragSelect();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDragSelect((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenImage",WidgetMap.IMAGE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenImage();
                                      else if(args[0] instanceof Node)
                                          return new ScreenImage((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMaskedBox",WidgetMap.MASKED_BOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMaskedBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMaskedBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenOption",WidgetMap.OPTION_LIST},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenOption();
                                      else if(args[0] instanceof Node)
                                          return new ScreenOption((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenRadio",WidgetMap.RADIO_BUTTON},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenRadio();
                                      else if(args[0] instanceof Node)
                                          return new ScreenRadio((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTableWidget",WidgetMap.TABLE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTableWidget();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTableWidget((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenText",WidgetMap.TEXT},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenText();
                                      else if(args[0] instanceof Node) 
                                          return new ScreenText((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTextArea",WidgetMap.TEXT_AREA},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextArea();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextArea((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTextBox",WidgetMap.TEXBOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTextBox();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTextBox((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTabBrowser",WidgetMap.TAB_BROWSER},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTabBrowser();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTabBrowser((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTree",WidgetMap.TREE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTree();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTree((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableCalendar",WidgetMap.TABLE_CALENDAR},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCalendar();
                                      else if(args[0] instanceof Node)
                                          return new TableCalendar((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableLabel",WidgetMap.TABLE_LABEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableLabel();
                                      else if(args[0] instanceof Node)
                                          return new TableLabel((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableMaskedTextBox",WidgetMap.TABLE_MASKED_BOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableMaskedTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableMaskedTextBox((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableOption",WidgetMap.TABLE_OPTION_LIST},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableOption();
                                      else if(args[0] instanceof Node)
                                          return new TableOption((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableTextBox",WidgetMap.TABLE_TEXTBOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableTextBox();
                                      else if(args[0] instanceof Node)
                                          return new TableTextBox((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableAuto",WidgetMap.TABLE_AUTO},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableAuto();
                                      else if(args[0] instanceof Node)
                                          return new TableAuto((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableAutoDropdown",WidgetMap.TABLE_AUTO_DROPDOWN},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableAutoDropdown();
                                      else if(args[0] instanceof Node)
                                          return new TableAutoDropdown((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"TableCollection",WidgetMap.TABLE_COLLECTION},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCollection();
                                      else if(args[0] instanceof Node)
                                          return new TableCollection((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenDragList",WidgetMap.DRAGLIST},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenDragList();
                                      else if(args[0] instanceof Node)
                                          return new ScreenDragList((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenLabel",WidgetMap.LABEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuPanel","menuPanel"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuItem","menuItem"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args){
                                      if(args == null)
                                          return new ScreenMenuItem();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuItem((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuLabel",WidgetMap.MENU_LABEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuLabel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuLabel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenMenuBar",WidgetMap.MENU_BAR},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuBar();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuBar((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"CheckField",WidgetMap.RPC_CHECKBOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new CheckField();
                                  }
      });
      ClassFactory.addClass(new String[] {"ModelField",WidgetMap.RPC_MODEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {   
                                      return new ModelField();
                                  }
      });
      ClassFactory.addClass(new String[] {"DateField",WidgetMap.RPC_DATE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new DateField();
                                  }
      });
      ClassFactory.addClass(new String[] {"NumberField",WidgetMap.RPC_NUMBER},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new NumberField();
                                  }
      });
      ClassFactory.addClass(new String[] {"OptionField",WidgetMap.RPC_OPTION},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new OptionField();
                                  }
      });
      ClassFactory.addClass(new String[] {"StringField",WidgetMap.RPC_STRING},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new StringField();
                                  }
      });
      ClassFactory.addClass(new String[] {"CollectionField","rpc-collection"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new CollectionField();
                                  }
      });
      ClassFactory.addClass(new String[] {"TableField",WidgetMap.RPC_TABLE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new TableField();
                                  }
      });
      ClassFactory.addClass(new String[] {"PagedTreeField",WidgetMap.RPC_PAGED_TREE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new PagedTreeField();
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryCheckField",WidgetMap.RPC_QUERY_CHECK},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new QueryCheckField();
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryDateField",WidgetMap.RPC_QUERY_DATE},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new QueryDateField();
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryNumberField",WidgetMap.RPC_QUERY_NUMBER},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new QueryNumberField();
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryOptionField",WidgetMap.RPC_QUERY_OPTION},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new QueryOptionField();
                                  }
      });
      ClassFactory.addClass(new String[] {"QueryStringField",WidgetMap.RPC_QUERY_STRING},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new QueryStringField();
                                  }
      });
      ClassFactory.addClass(new String[] {"TableCheck",WidgetMap.TABLE_CHECKBOX},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new TableCheck();
                                      else if(args[0] instanceof Node)
                                          return new TableCheck((Node)args[0]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTab",WidgetMap.PANEL_TAB},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTab();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTab((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenStack",WidgetMap.PANEL_STACK},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenStack();
                                      else if(args[0] instanceof Node)
                                          return new ScreenStack((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenWindowBrowser",WidgetMap.WINBROWSER},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenWindowBrowser();
                                      else if(args[0] instanceof Node)
                                          return new ScreenWindowBrowser((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenAuto",WidgetMap.AUTO},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAuto();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAuto((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenHTML",WidgetMap.HTML},
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
      ClassFactory.addClass(new String[] {"ScreenAToZPanel",WidgetMap.LEFT_MENU_PANEL},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAToZPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAToZPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTitledPanel","titledPanel"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTitledPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTitledPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"menuPopupPanel","meuPopupPanel"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenMenuPopupPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenMenuPopupPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"pagedTree","pagedTree"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenPagedTree();
                                      else if(args[0] instanceof Node)
                                          return new ScreenPagedTree((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
      ClassFactory.addClass(new String[] {"ScreenTitledPanel","titledPanel"},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenTitledPanel();
                                      else if(args[0] instanceof Node)
                                          return new ScreenTitledPanel((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });   
      ClassFactory.addClass(new String[] {"ScreenAutoDropdown",WidgetMap.AUTO_DROPDOWN},
                            new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      if(args == null)
                                          return new ScreenAutoDropdown();
                                      else if(args[0] instanceof Node)
                                          return new ScreenAutoDropdown((Node)args[0],(ScreenBase)args[1]);
                                      return null;
                                  }
      });
  }
  
}
