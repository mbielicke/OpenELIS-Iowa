package org.openelis.webdriver;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openelis.modules.main.client.LoginPage;
import org.openelis.modules.method.client.MethodPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestWebDriver {
    static WebDriver driver;
    
    @BeforeClass
    public static void init() {
        driver = new FirefoxDriver();
    }
    
    @Before
    public void load() {
        driver.get("http://ap-serv-j/openelis/OpenELIS.html");
    }
    
    @AfterClass
    public static void destroy() {
        driver.quit();
    }
    
    @Test
    public  void testMethod() {
        MethodPage page = new LoginPage(driver).login().clickMaintenance().clickMethod();
        
        String value = page.clickAdd().typeInName("My Name").getNameText();
        
        assertEquals("My Name",value);        
    }
}
