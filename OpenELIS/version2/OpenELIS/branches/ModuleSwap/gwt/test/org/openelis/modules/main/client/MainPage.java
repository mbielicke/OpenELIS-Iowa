package org.openelis.modules.main.client;

import static org.openelis.modules.main.client.OpenELIS.Ids.*;

import org.openelis.modules.method.client.MethodPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import org.openqa.selenium.support.ui.WebDriverWait;


public class MainPage {
    
    private final WebDriver driver;
    private final String id = "main";
    private WebDriverWait wait;
    
    public MainPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver,10);
    }
    
    By preference = By.id(id + PREFRENCE);
    By logout = By.id(id + LOGOUT); 
    By sampleLoginLabelReport = By.id(id + SAMPLE_LOGIN_LABEL_REPORT);
    By sampleLoginLabelAdditionalReport = By.id(id + SAMPLE_LOGIN_ADDITIONAL_REPORT);
    By quickEntry = By.id(id + QUICK_ENTRY);
    By verification = By.id(id + VERIFICATION); 
    By tracking = By.id(id + TRACKING);
    By environmentalSampleLogin = By.id(id + ENVIRONMENTAL_SAMPLE_LOGIN);
    By privateWellWaterSampleLogin = By.id(id + PRIVATE_WELL_WATER_SAMPLE_LOGIN);
    By sdwisSampleLogin = By.id(id + SDWIS_SAMPLE_LOGIN);
    By clinicalSampleLogin = By.id(id + CLINICAL_SAMPLE_LOGIN);
    By neonatalScreeningSampleLogin = By.id(id + NEONATAL_SCREENING_SAMPLE_LOGIN);
    By animalSampleLogin = By.id(id + ANIMAL_SAMPLE_LOGIN);
    By ptSampleLogin = By.id(id + PT_SAMPLE_LOGIN);
    By testSampleManager = By.id(id + TEST_SAMPLE_MANAGER);
    By project = By.id(id + PROJECT); 
    By provider = By.id(id + PROVIDER);
    By organization = By.id(id + ORGANIZATION);
    By worksheetBuilder = By.id(id + WORKSHEET_BUILDER); 
    By worksheetCreation = By.id(id + WORKSHEET_CREATION); 
    By worksheetCompletion = By.id(id + WORKSHEET_COMPLETION);
    By addOrCancel = By.id(id + ADD_OR_CANCEL);
    By reviewAndRelease = By.id(id + REVIEW_AND_RELEASE);
    By toDo = By.id(id + TO_DO);
    By labelFor = By.id(id + LABEL_FOR); 
    By storage = By.id(id + STORAGE);
    By QCm = By.id(id + QC); 
    By analyteParameter = By.id(id + ANALYTE_PARAMETER);
    By internalOrder = By.id(id + INTERNAL_ORDER);
    By vendorOrder = By.id(id + VENDOR_ORDER);
    By sendoutOrder = By.id(id + SENDOUT_ORDER);
    By fillOrder = By.id(id + FILL_ORDER);
    By shipping = By.id(id + SHIPPING);
    By buildKits = By.id(id + BUILD_KITS);
    By inventoryTransfer = By.id(id + INVENTORY_TRANSFER);
    By inventoryReceipt = By.id(id + INVENTORY_RECEIPT);
    By inventoryAdjustment = By.id(id + INVENTORY_ADJUSTMENT);
    By inventoryItem = By.id(id + INVENTORY_ITEM);
    By verificationReport = By.id(id + VERIFICAITON_REPORT);
    By testRequestFormReport = By.id(id + TEST_REQUEST_FORM_REPORT);
    By orderRequestForm = By.id(id + ORDER_REQUEST_FORM); 
    By holdRefuseOrganization = By.id(id + HOLD_REFUSE_ORGANIZATION);
    By testReport = By.id(id + TEST_REPORT);
    By billingReport = By.id(id + BILLING_REPORT);
    By sampleInhouseReport = By.id(id + SAMPLE_IN_HOUSE_REPORT); 
    By volumeReport = By.id(id + VOLUME_REPORT);
    By toDoAnalyteReport = By.id(id + TO_DO_ANALYTE_REPORT);
    By sampleDataExport = By.id(id + SAMPLE_DATA_EXPORT);
    By QASummaryReport = By.id(id + QA_SUMMARY_REPORT);
    By testCountByFacility = By.id(id + TEST_COUNTY_BY_FACILITY);
    By turnaround = By.id(id + TURNAROUND);
    By turnAroundStatisticReport = By.id(id + TURNAROUND_STATISTIC_REPORT);
    By kitTrackingReport = By.id(id + KIT_TRACKING_REPORT);
    By sdwisUnloadReport = By.id(id + SDWIS_UNLOAD_REPORT);
    By dataView = By.id(id + DATA_VIEW);
    By qcChart = By.id(id + QC_CHART);
    By finalReport = By.id(id + FINAL_REPORT);
    By finalReportBatch = By.id(id + FINAL_REPORT_BATCH);
    By finalReportBatchReprint = By.id(id + FINAL_REPORT_BATCH_REPRINT);
    By test = By.id(id + TEST);
    By method = By.id(id + METHOD);
    By panel = By.id(id + PANEL);
    By QAEvent = By.id(id + QA_EVENT);
    By labSection = By.id(id + LAB_SECTION); 
    By analyte = By.id(id + ANALYTE);
    By dictionary = By.id(id + DICTIONARY);
    By auxiliaryPrompt = By.id(id + AUXILIARY_PROMPT);
    By exchangeVocabularyMap = By.id(id + EXCHANGE_VOCABULARY_MAP);
    By exchangeDataSelection = By.id(id + EXCHANGE_DATA_SELECTION); 
    By label = By.id(id + LABEL);
    By standardNote = By.id(id + STANDARD_NOTE);
    By trailerForTest = By.id(id + TRAILER_FOR_TEST);
    By storageUnit = By.id(id + STORAGE_UNIT);
    By storageLocation = By.id(id + STORAGE_LOCATION);
    By instrument = By.id(id + INSTRUMENT);
    By scriptlet = By.id(id + SCRIPTLET);
    By systemVariable = By.id(id + SYSTEM_VARIABLE);
    By pws = By.id(id + PWS);
    By cron = By.id(id + CRON); 
    By logs = By.id(id + LOGS);  
    By maintenance = By.id(id + MAINTENANCE);
    
    public MainPage clickMaintenance() {
        wait.until(presenceOfElementLocated(maintenance)).click();
        return this;
    }
    
    public MethodPage clickMethod() {
        wait.until(presenceOfElementLocated(method)).click();
        return new MethodPage(driver);
    }
}
