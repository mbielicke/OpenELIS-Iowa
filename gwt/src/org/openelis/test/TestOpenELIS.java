package org.openelis.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.openelis.client.analysis.screen.qaevent.QAEventsNamesTable;
import org.openelis.client.dataEntry.screen.Provider.ProviderAddressesTable;
import org.openelis.client.dataEntry.screen.Provider.ProviderNamesTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationContactsTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationNameTable;
import org.openelis.client.dataEntry.screen.organization.OrganizationScreen;
import org.openelis.client.main.OpenELIS;
import org.openelis.client.main.constants.OpenELISConstants;
import org.openelis.client.main.service.OpenELISService;
import org.openelis.client.supply.screen.storage.StorageNameTable;
import org.openelis.client.supply.screen.storageUnit.StorageUnitDescTable;
import org.openelis.client.utilities.screen.dictionary.CategorySystemNamesTable;
import org.openelis.client.utilities.screen.dictionary.DictionaryEntriesTable;
import org.openelis.gwt.client.screen.ScreenAToZPanel;
import org.openelis.gwt.client.screen.ScreenAppButton;
import org.openelis.gwt.client.screen.ScreenAppMessage;
import org.openelis.gwt.client.screen.ScreenAuto;
import org.openelis.gwt.client.screen.ScreenAutoDropdown;
import org.openelis.gwt.client.screen.ScreenBase;
import org.openelis.gwt.client.screen.ScreenButtonPanel;
import org.openelis.gwt.client.screen.ScreenCalendar;
import org.openelis.gwt.client.screen.ScreenCheck;
import org.openelis.gwt.client.screen.ScreenDeck;
import org.openelis.gwt.client.screen.ScreenDragList;
import org.openelis.gwt.client.screen.ScreenDragSelect;
import org.openelis.gwt.client.screen.ScreenError;
import org.openelis.gwt.client.screen.ScreenHTML;
import org.openelis.gwt.client.screen.ScreenHorizontal;
import org.openelis.gwt.client.screen.ScreenHorizontalSplit;
import org.openelis.gwt.client.screen.ScreenImage;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenMaskedBox;
import org.openelis.gwt.client.screen.ScreenMenuBar;
import org.openelis.gwt.client.screen.ScreenMenuLabel;
import org.openelis.gwt.client.screen.ScreenMenuPanel;
import org.openelis.gwt.client.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.client.screen.ScreenOption;
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.screen.ScreenRadio;
import org.openelis.gwt.client.screen.ScreenStack;
import org.openelis.gwt.client.screen.ScreenTab;
import org.openelis.gwt.client.screen.ScreenTabBrowser;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenTableWidget;
import org.openelis.gwt.client.screen.ScreenText;
import org.openelis.gwt.client.screen.ScreenTextArea;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.screen.ScreenTitledPanel;
import org.openelis.gwt.client.screen.ScreenTree;
import org.openelis.gwt.client.screen.ScreenVertical;
import org.openelis.gwt.client.screen.ScreenWindowBrowser;
import org.openelis.gwt.client.widget.HoverListener;
import org.openelis.gwt.client.widget.ProxyListener;
import org.openelis.gwt.client.widget.WidgetMap;
import org.openelis.gwt.client.widget.table.TableAuto;
import org.openelis.gwt.client.widget.table.TableAutoDropdown;
import org.openelis.gwt.client.widget.table.TableCalendar;
import org.openelis.gwt.client.widget.table.TableCheck;
import org.openelis.gwt.client.widget.table.TableLabel;
import org.openelis.gwt.client.widget.table.TableMaskedTextBox;
import org.openelis.gwt.client.widget.table.TableOption;
import org.openelis.gwt.client.widget.table.TableTextBox;
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

public class TestOpenELIS extends GWTTestCase {
    public TestOpenELIS() {
        super();
        

        }
    
    public String getModuleName() {
        return "org.openelis.TestOpenELIS";
    }
    
    public void testInit() {
        try {
            new OpenELIS().setWidgetMap();
            assertTrue(true);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
    public void testOrganizationScreen() {
        System.out.println("Creating org screen");
        final OrganizationScreen orgScreen = new OrganizationScreen();
        Timer timer = new Timer() {
            public void run() {
                assertNotNull(orgScreen.forms.get("display"));
                finishTest();
            }
        }; 
        assertNotNull(orgScreen);
        delayTestFinish(5000);
        timer.schedule(2000);
    }
    

}
