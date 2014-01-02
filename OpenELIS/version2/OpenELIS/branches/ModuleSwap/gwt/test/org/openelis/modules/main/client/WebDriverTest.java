package org.openelis.modules.main.client;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverTest {
    
    static WebDriver driver;
    
    @BeforeClass
    public static void init() {
        driver = new FirefoxDriver();
    }
    
    @Before
    public void load() {
        driver.get("http://192.168.111.202:8080/openelis/OpenELIS.html");
    }
    
    @AfterClass
    public static void destroy() {
        driver.quit();
    }

    @Test
    public void clickMethod() {
        new LoginPage(driver).login().clickMaintenance().clickMethod();
    }
}
