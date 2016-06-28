/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.bean.*;
import org.openelis.security.remote.SystemUserPermissionRemote;

/**
 * This static class is used to get local instance EJB beans for non ejb class
 * calls.
 */

public class EJBFactory {

    private static final Logger log = Logger.getLogger("openelis");

    public static AnalysisBean getAnalysis() {
        return lookup("AnalysisBean");
    }

    public static AnalysisQAEventBean getAnalysisQAEvent() {
        return lookup("AnalysisQAEventBean");
    }

    public static AnalysisReportFlagsBean getAnalysisReportFlags() {
        return lookup("AnalysisReportFlagsBean");
    }

    public static AnalysisUserBean getAnalysisUser() {
        return lookup("AnalysisUserBean");
    }

    public static AnalyteBean getAnalyte() {
        return lookup("AnalyteBean");
    }

    public static AnalyteParameterBean getAnalyteParameter() {
        return lookup("AnalyteParameterBean");
    }
    
    public static AnalyteParameterManager1Bean getAnalyteParameterManager1() {
        return lookup("AnalyteParameterManager1Bean");
    }

    public static AuxDataBean getAuxData() {
        return lookup("AuxDataBean");
    }

    public static AuxFieldGroupBean getAuxFieldGroup() {
        return lookup("AuxFieldGroupBean");
    }

    public static AuxFieldBean getAuxField() {
        return lookup("AuxFieldBean");
    }

    public static AuxFieldValueBean getAuxFieldValue() {
        return lookup("AuxFieldValueBean");
    }

    public static CategoryBean getCategory() {
        return lookup("CategoryBean");
    }

    public static DictionaryBean getDictionary() {
        return lookup("DictionaryBean");
    }

    public static ExchangeLocalTermBean getExchangeLocalTerm() {
        return lookup("ExchangeLocalTermBean");
    }

    public static ExchangeExternalTermBean getExchangeExternalTerm() {
        return lookup("ExchangeExternalTermBean");
    }

    public static ExchangeCriteriaBean getExchangeCriteria() {
        return lookup("ExchangeCriteriaBean");
    }

    public static ExchangeProfileBean getExchangeProfile() {
        return lookup("ExchangeProfileBean");
    }

    public static EventLogBean getEventLog() {
        return lookup("EventLogBean");
    }

    public static HistoryBean getHistory() {
        return lookup("HistoryBean");
    }

    public static InstrumentBean getInstrument() {
        return lookup("InstrumentBean");
    }

    public static InstrumentLogBean getInstrumentLog() {
        return lookup("InstrumentLogBean");
    }

    public static InventoryAdjustmentBean getInventoryAdjustment() {
        return lookup("InventoryAdjustmentBean");
    }

    public static InventoryComponentBean getInventoryComponent() {
        return lookup("InventoryComponentBean");
    }

    public static InventoryItemBean getInventoryItem() {
        return lookup("InventoryItemBean");
    }

    public static InventoryLocationBean getInventoryLocation() {
        return lookup("InventoryLocationBean");
    }

    public static InventoryReceiptBean getInventoryReceipt() {
        return lookup("InventoryReceiptBean");
    }

    public static InventoryXAdjustBean getInventoryXAdjust() {
        return lookup("InventoryXAdjustBean");
    }

    public static InventoryXPutBean getInventoryXPut() {
        return lookup("InventoryXPutBean");
    }

    public static InventoryXUseBean getInventoryXUse() {
        return lookup("InventoryXUseBean");
    }

    public static LockBean getLock() {
        return lookup("LockBean");
    }

    public static NoteBean getNote() {
        return lookup("NoteBean");
    }

    public static IOrderOrganizationBean getIOrderOrganization() {
        return lookup("IOrderOrganizationBean");
    }

    public static IOrderContainerBean getIOrderContainer() {
        return lookup("IOrderContainerBean");
    }

    public static IOrderItemBean getIOrderItem() {
        return lookup("IOrderItemBean");
    }

    public static IOrderBean getIOrder() {
        return lookup("IOrderBean");
    }

    public static IOrderTestBean getIOrderTest() {
        return lookup("IOrderTestBean");
    }

    public static IOrderTestAnalyteBean getIOrderTestAnalyte() {
        return lookup("IOrderTestAnalyteBean");
    }

    public static IOrderRecurrenceBean getIOrderRecurrence() {
        return lookup("IOrderRecurrenceBean");
    }

    public static OrganizationContactBean getOrganizationContact() {
        return lookup("OrganizationContactBean");
    }

    public static OrganizationBean getOrganization() {
        return lookup("OrganizationBean");
    }

    public static OrganizationParameterBean getOrganizationParameter() {
        return lookup("OrganizationParameterBean");
    }

    public static PanelItemBean getPanelItem() {
        return lookup("PanelItemBean");
    }

    public static PanelBean getPanel() {
        return lookup("PanelBean");
    }

    public static ProjectBean getProject() {
        return lookup("ProjectBean");
    }

    public static ProjectParameterBean getProjectParameter() {
        return lookup("ProjectParameterBean");
    }

    public static ProviderBean getProvider() {
        return lookup("ProviderBean");
    }

    public static ProviderLocationBean getProviderLocation() {
        return lookup("ProviderLocationBean");
    }

    public static PWSAddressBean getPWSAddress() {
        return lookup("PWSAddressBean");
    }

    public static PWSFacilityBean getPWSFacility() {
        return lookup("PWSFacilityBean");
    }

    public static PWSBean getPWS() {
        return lookup("PWSBean");
    }

    public static PWSMonitorBean getPWSMonitor() {
        return lookup("PWSMonitorBean");
    }
    
    public static PWSViolationBean getPWSViolation() {
        return lookup("PWSViolationBean");
    }

    public static QaEventBean getQaevent() {
        return lookup("QaEventBean");
    }

    public static QcAnalyteBean getQcAnalyte() {
        return lookup("QcAnalyteBean");
    }

    public static QcLotBean getQcLot() {
        return lookup("QcLotBean");
    }

    public static QcBean getQc() {
        return lookup("QcBean");
    }

    public static ResultBean getResult() {
        return lookup("ResultBean");
    }

    public static SampleEnvironmentalBean getSampleEnvironmental() {
        return lookup("SampleEnvironmentalBean");
    }

    public static SampleItemBean getSampleItem() {
        return lookup("SampleItemBean");
    }

    public static SampleBean getSample() {
        return lookup("SampleBean");
    }

    public static SampleManager1Bean getSampleManager1() {
        return lookup("SampleManager1Bean");
    }

    public static SampleOrganizationBean getSampleOrganization() {
        return lookup("SampleOrganizationBean");
    }

    public static SampleProjectBean getSampleProject() {
        return lookup("SampleProjectBean");
    }

    public static SampleQAEventBean getSampleQAEvent() {
        return lookup("SampleQAEventBean");
    }

    public static SampleSDWISBean getSampleSDWIS() {
        return lookup("SampleSDWISBean");
    }

    public static SectionBean getSection() {
        return lookup("SectionBean");
    }

    public static SectionParameterBean getSectionParameter() {
        return lookup("SectionParameterBean");
    }

    public static ShippingItemBean getShippingItem() {
        return lookup("ShippingItemBean");
    }

    public static ShippingBean getShipping() {
        return lookup("ShippingBean");
    }

    public static ShippingTrackingBean getShippingTracking() {
        return lookup("ShippingTrackingBean");
    }
    
    public static StandardNoteBean getStandardNote() {
        return lookup("StandardNoteBean");
    }

    public static StorageBean getStorage() {
        return lookup("StorageBean");
    }

    public static StorageLocationBean getStorageLocation() {
        return lookup("StorageLocationBean");
    }

    public static SystemVariableBean getSystemVariable() {
        return lookup("SystemVariableBean");
    }

    public static TestAnalyteBean getTestAnalyte() {
        return lookup("TestAnalyteBean");
    }

    public static TestBean getTest() {
        return lookup("TestBean");
    }
    
    public static TestManagerBean getTestManager() {
        return lookup("TestManagerBean");
    }

    public static TestPrepBean getTestPrep() {
        return lookup("TestPrepBean");
    }

    public static TestReflexBean getTestReflex() {
        return lookup("TestReflexBean");
    }

    public static TestResultBean getTestResult() {
        return lookup("TestResultBean");
    }

    public static TestSectionBean getTestSection() {
        return lookup("TestSectionBean");
    }

    public static TestTrailerBean getTestTrailer() {
        return lookup("TestTrailerBean");
    }

    public static TestTypeOfSampleBean getTestTypeOfSample() {
        return lookup("TestTypeOfSampleBean");
    }

    public static TestWorksheetAnalyteBean getTestWorksheetAnalyte() {
        return lookup("TestWorksheetAnalyteBean");
    }

    public static TestWorksheetItemBean getTestWorksheetItem() {
        return lookup("TestWorksheetItemBean");
    }

    public static TestWorksheetBean getTestWorksheet() {
        return lookup("TestWorksheetBean");
    }

    public static WorksheetBean getWorksheet() {
        return lookup("WorksheetBean");
    }

    public static WorksheetAnalysisBean getWorksheetAnalysis() {
        return lookup("WorksheetAnalysisBean");
    }

    public static WorksheetItemBean getWorksheetItem() {
        return lookup("WorksheetItemBean");
    }

    public static WorksheetManager1Bean getWorksheetManager1() {
        return lookup("WorksheetManager1Bean");
    }

    public static WorksheetQcResultBean getWorksheetQcResult() {
        return lookup("WorksheetQcResultBean");
    }

    public static WorksheetResultBean getWorksheetResult() {
        return lookup("WorksheetResultBean");
    }

    public static DictionaryCacheBean getDictionaryCache() {
        return lookup("DictionaryCacheBean");
    }

    public static CategoryCacheBean getCategoryCache() {
        return lookup("CategoryCacheBean");
    }

    public static InventoryItemCacheBean getInventoryItemCache() {
        return lookup("InventoryItemCacheBean");
    }

    public static SectionCacheBean getSectionCache() {
        return lookup("SectionCacheBean");
    }

    public static SessionCacheBean getSessionCache() {
        return lookup("SessionCacheBean");
    }

    public static UserCacheBean getUserCache() {
        return lookup("UserCacheBean");
    }

    public static PreferencesBean getPreferences() {
        return lookup("PreferencesBean");
    }
    
    public static PreferencesManager1Bean getPreferencesManager1() {
        return lookup("PreferencesManager1Bean");
    }

    public static PrinterCacheBean getPrinterCache() {
        return lookup("PrinterCacheBean");
    }

    public static FinalReportBean getFinalReport() {
        return lookup("FinalReportBean");
    }

    /*
     * Bean from Security project
     */
    public static SystemUserPermissionRemote getSecurity() {
        return lookup("java:global/security/security.jar/SystemUserPermissionBean!org.openelis.security.remote.SystemUserPermissionRemote");
    }

    public static <T> T lookup(String bean) {
        InitialContext ctx;

        if (!bean.startsWith("java"))
            bean = "java:global/openelis/openelis.jar/" + bean;

        try {
            ctx = new InitialContext();
            return (T)ctx.lookup(bean);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Bean Lookup Failed (" + bean + ") ", e);
            return null;
        }

    }
}