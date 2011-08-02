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

import javax.naming.InitialContext;

import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.AnalysisUserLocal;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.AuxFieldGroupLocal;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.HistoryLocal;
import org.openelis.local.InstrumentLocal;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.local.InventoryComponentLocal;
import org.openelis.local.InventoryItemCacheLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXAdjustLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.InventoryXUseLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.NoteLocal;
import org.openelis.local.OrderContainerLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrderRecurrenceLocal;
import org.openelis.local.OrderTestLocal;
import org.openelis.local.OrganizationContactLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.OrganizationParameterLocal;
import org.openelis.local.PanelItemLocal;
import org.openelis.local.PanelLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.ProjectParameterLocal;
import org.openelis.local.ProviderLocal;
import org.openelis.local.ProviderLocationLocal;
import org.openelis.local.PWSAddressLocal;
import org.openelis.local.PWSFacilityLocal;
import org.openelis.local.PWSLocal;
import org.openelis.local.PWSMonitorLocal;
import org.openelis.local.QaeventLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.QcLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SectionLocal;
import org.openelis.local.SectionParameterLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.ShippingItemLocal;
import org.openelis.local.ShippingLocal;
import org.openelis.local.ShippingTrackingLocal;
import org.openelis.local.StorageLocal;
import org.openelis.local.StorageLocationLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.local.TestLocal;
import org.openelis.local.TestPrepLocal;
import org.openelis.local.TestReflexLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.local.TestSectionLocal;
import org.openelis.local.TestTypeOfSampleLocal;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.local.WorksheetItemLocal;
import org.openelis.local.WorksheetLocal;
import org.openelis.local.WorksheetQcResultLocal;
import org.openelis.local.WorksheetResultLocal;
import org.openelis.security.remote.SystemUserPermissionRemote;

/**
 * This static class is used to get local instance EJB beans for non ejb class
 * calls.
 */

public class EJBFactory {
    
    public static AnalysisLocal getAnalysis() {
        return (AnalysisLocal)lookup("openelis/AnalysisBean/local");
    }

    public static AnalysisQAEventLocal getAnalysisQAEvent() {
        return (AnalysisQAEventLocal)lookup("openelis/AnalysisQAEventBean/local");
    }

    public static AnalysisReportFlagsLocal getAnalysisReportFlags() {
        return (AnalysisReportFlagsLocal)lookup("openelis/AnalysisReportFlagsBean/local");
    }

    public static AnalysisUserLocal getAnalysisUser() {
        return (AnalysisUserLocal)lookup("openelis/AnalysisUserBean/local");
    }
    
    public static AnalyteParameterLocal getAnalyteParameter() {
        return (AnalyteParameterLocal)lookup("openelis/AnalyteParameterBean/local");
    }

    public static AuxDataLocal getAuxData() {
        return (AuxDataLocal)lookup("openelis/AuxDataBean/local");
    }    

    public static AuxFieldGroupLocal getAuxFieldGroup() {
        return (AuxFieldGroupLocal)lookup("openelis/AuxFieldGroupBean/local");
    }

    public static AuxFieldLocal getAuxField() {
        return (AuxFieldLocal)lookup("openelis/AuxFieldBean/local");
    }

    public static AuxFieldValueLocal getAuxFieldValue() {
        return (AuxFieldValueLocal)lookup("openelis/AuxFieldValueBean/local");
    }
    
    public static CategoryLocal getCategory() {
        return (CategoryLocal)lookup("openelis/CategoryBean/local");
    }

    public static DictionaryLocal getDictionary() {
        return (DictionaryLocal)lookup("openelis/DictionaryBean/local");
    }    
    
    public static HistoryLocal getHistory() {
        return (HistoryLocal)lookup("openelis/HistoryBean/local");
    }

    public static InstrumentLocal getInstrument() {
        return (InstrumentLocal)lookup("openelis/InstrumentBean/local");
    }   
    
    public static InstrumentLogLocal getInstrumentLog() {
        return (InstrumentLogLocal)lookup("openelis/InstrumentLogBean/local");
    }
    
    public static InventoryAdjustmentLocal getInventoryAdjustment() {
        return (InventoryAdjustmentLocal)lookup("openelis/InventoryAdjustmentBean/local");
    }

    public static InventoryComponentLocal getInventoryComponent() {
        return (InventoryComponentLocal)lookup("openelis/InventoryComponentBean/local");
    }

    public static InventoryItemLocal getInventoryItem() {
        return (InventoryItemLocal)lookup("openelis/InventoryItemBean/local");
    }

    public static InventoryLocationLocal getInventoryLocation() {
        return (InventoryLocationLocal)lookup("openelis/InventoryLocationBean/local");
    }    

    public static InventoryReceiptLocal getInventoryReceipt() {
        return (InventoryReceiptLocal)lookup("openelis/InventoryReceiptBean/local");
    }

    public static InventoryXAdjustLocal getInventoryXAdjust() {
        return (InventoryXAdjustLocal)lookup("openelis/InventoryXAdjustBean/local");
    }

    public static InventoryXPutLocal getInventoryXPut() {
        return (InventoryXPutLocal)lookup("openelis/InventoryXPutBean/local");
    }

    public static InventoryXUseLocal getInventoryXUse() {
        return (InventoryXUseLocal)lookup("openelis/InventoryXUseBean/local");
    }
    
    public static LockLocal getLock() {
        return (LockLocal)lookup("openelis/LockBean/local");
    }

    public static NoteLocal getNote() {
        return (NoteLocal)lookup("openelis/NoteBean/local");
    }

    public static OrderContainerLocal getOrderContainer() {
        return (OrderContainerLocal)lookup("openelis/OrderContainerBean/local");
    }

    public static OrderItemLocal getOrderItem() {
        return (OrderItemLocal)lookup("openelis/OrderItemBean/local");
    }
    
    public static OrderLocal getOrder() {
        return (OrderLocal)lookup("openelis/OrderBean/local");
    }
    
    public static OrderTestLocal getOrderTest() {
        return (OrderTestLocal)lookup("openelis/OrderTestBean/local");
    }
    
    public static OrderRecurrenceLocal getOrderRecurrence() {
        return (OrderRecurrenceLocal)lookup("openelis/OrderRecurrenceBean/local");
    }
    
    public static OrganizationContactLocal getOrganizationContact() {
        return (OrganizationContactLocal)lookup("openelis/OrganizationContactBean/local");
    }
    
    public static OrganizationLocal getOrganization() {
        return (OrganizationLocal)lookup("openelis/OrganizationBean/local");
    }
    
    public static OrganizationParameterLocal getOrganizationParameter() {
        return (OrganizationParameterLocal)lookup("openelis/OrganizationParameterBean/local");
    }

    public static PanelItemLocal getPanelItem() {
        return (PanelItemLocal)lookup("openelis/PanelItemBean/local");
    }
    
    public static PanelLocal getPanel() {
        return (PanelLocal)lookup("openelis/PanelBean/local");
    }
    
    public static ProjectLocal getProject() {
        return (ProjectLocal)lookup("openelis/ProjectBean/local");
    }
    
    public static ProjectParameterLocal getProjectParameter() {
        return (ProjectParameterLocal)lookup("openelis/ProjectParameterBean/local");
    }
    
    public static ProviderLocal getProvider() {
        return (ProviderLocal)lookup("openelis/ProviderBean/local");
    }
    
    public static ProviderLocationLocal getProviderLocation() {
        return (ProviderLocationLocal)lookup("openelis/ProviderLocationBean/local");
    }
    
    public static PWSAddressLocal getPWSAddress() {
        return (PWSAddressLocal)lookup("openelis/PWSAddressBean/local");
    }
    
    public static PWSFacilityLocal getPWSFacility() {
        return (PWSFacilityLocal)lookup("openelis/PWSFacilityBean/local");
    }        
    
    public static PWSLocal getPWS() {
        return (PWSLocal)lookup("openelis/PWSBean/local");
    }
    
    public static PWSMonitorLocal getPWSMonitor() {
        return (PWSMonitorLocal)lookup("openelis/PWSMonitorBean/local");
    }

    public static QaeventLocal getQaevent() {
        return (QaeventLocal)lookup("openelis/QaEventBean/local");
    }
    
    public static QcAnalyteLocal getQcAnalyte() {
        return (QcAnalyteLocal)lookup("openelis/QcAnalyteBean/local");
    }
    
    public static QcLocal getQc() {
        return (QcLocal)lookup("openelis/QcBean/local");
    }

    public static ResultLocal getResult() {
        return (ResultLocal)lookup("openelis/ResultBean/local");
    }
    
    public static SampleEnvironmentalLocal getSampleEnvironmental() {
        return (SampleEnvironmentalLocal)lookup("openelis/SampleEnvironmentalBean/local");
    }
    
    public static SampleItemLocal getSampleItem() {
        return (SampleItemLocal)lookup("openelis/SampleItemBean/local");
    }
    
    public static SampleLocal getSample() {
        return (SampleLocal)lookup("openelis/SampleBean/local");
    }
    
    public static SampleManagerLocal getSampleManager() {
        return (SampleManagerLocal)lookup("openelis/SampleManagerBean/local");
    }
    
    public static SampleOrganizationLocal getSampleOrganization() {
        return (SampleOrganizationLocal)lookup("openelis/SampleOrganizationBean/local");
    }
    
    public static SamplePrivateWellLocal getSamplePrivateWell() {
        return (SamplePrivateWellLocal)lookup("openelis/SamplePrivateWellBean/local");
    }
    
    public static SampleProjectLocal getSampleProject() {
        return (SampleProjectLocal)lookup("openelis/SampleProjectBean/local");
    }
    
    public static SampleQAEventLocal getSampleQAEvent() {
        return (SampleQAEventLocal)lookup("openelis/SampleQAEventBean/local");
    }
    
    public static SampleSDWISLocal getSampleSDWIS() {
        return (SampleSDWISLocal)lookup("openelis/SampleSDWISBean/local");
    }
    
    public static SectionLocal getSection() {
        return (SectionLocal)lookup("openelis/SectionBean/local");
    }    
    
    public static SectionParameterLocal getSectionParameter() {
        return (SectionParameterLocal)lookup("openelis/SectionParameterBean/local");    
    }
    
    public static ShippingItemLocal getShippingItem() {
        return (ShippingItemLocal)lookup("openelis/ShippingItemBean/local");
    }
    
    public static ShippingLocal getShipping() {
        return (ShippingLocal)lookup("openelis/ShippingBean/local");
    }
    
    public static ShippingTrackingLocal getShippingTracking() {
        return (ShippingTrackingLocal)lookup("openelis/ShippingTrackingBean/local");
    }
    
    public static StorageLocal getStorage() {
        return (StorageLocal)lookup("openelis/StorageBean/local");
    }
    
    public static StorageLocationLocal getStorageLocation() {
        return (StorageLocationLocal)lookup("openelis/StorageLocationBean/local");
    }
    
    public static SystemVariableLocal getSystemVariable() {
        return (SystemVariableLocal)lookup("openelis/SystemVariableBean/local");
    }
    
    public static TestAnalyteLocal getTestAnalyte() {
        return (TestAnalyteLocal)lookup("openelis/TestAnalyteBean/local");
    }
    
    public static TestLocal getTest() {
        return (TestLocal)lookup("openelis/TestBean/local");
    }
    
    public static TestPrepLocal getTestPrep() {
        return (TestPrepLocal)lookup("openelis/TestPrepBean/local");
    }
    
    public static TestReflexLocal getTestReflex() {
        return (TestReflexLocal)lookup("openelis/TestReflexBean/local");
    }
    
    public static TestResultLocal getTestResult() {
        return (TestResultLocal)lookup("openelis/TestResultBean/local");
    }
    
    public static TestSectionLocal getTestSection() {
        return (TestSectionLocal)lookup("openelis/TestSectionBean/local");
    }
    
    public static TestTypeOfSampleLocal getTestTypeOfSample() {
        return (TestTypeOfSampleLocal)lookup("openelis/TestTypeOfSampleBean/local");
    }
    
    public static TestWorksheetAnalyteLocal getTestWorksheetAnalyte() {
        return (TestWorksheetAnalyteLocal)lookup("openelis/TestWorksheetAnalyteBean/local");
    }
    
    public static TestWorksheetItemLocal getTestWorksheetItem() {
        return (TestWorksheetItemLocal)lookup("openelis/TestWorksheetItemBean/local");
    }
    
    public static TestWorksheetLocal getTestWorksheet() {
        return (TestWorksheetLocal)lookup("openelis/TestWorksheetBean/local");
    }
    
    public static WorksheetAnalysisLocal getWorksheetAnalysis() {
        return (WorksheetAnalysisLocal)lookup("openelis/WorksheetAnalysisBean/local");
    }
    
    public static WorksheetItemLocal getWorksheetItem() {
        return (WorksheetItemLocal)lookup("openelis/WorksheetItemBean/local");
    }
    
    public static WorksheetLocal getWorksheet() {
        return (WorksheetLocal)lookup("openelis/WorksheetBean/local");
    }
    
    public static WorksheetQcResultLocal getWorksheetQcResult() {
        return (WorksheetQcResultLocal)lookup("openelis/WorksheetQcResultBean/local");
    }
    
    public static WorksheetResultLocal getWorksheetResult() {
        return (WorksheetResultLocal)lookup("openelis/WorksheetResultBean/local");
    }
    
    public static DictionaryCacheLocal getDictionaryCache() {
        return (DictionaryCacheLocal) lookup("openelis/DictionaryCacheBean/local");
    }
    
    public static InventoryItemCacheLocal getInventoryItemCache() {
        return (InventoryItemCacheLocal) lookup("openelis/InventoryItemCacheBean/local");
    }
    
    public static SectionCacheLocal getSectionCache() {
        return (SectionCacheLocal) lookup("openelis/SectionCacheBean/local");
    }
    
    public static SessionCacheLocal getSessionCache() {
        return (SessionCacheLocal)lookup("openelis/SessionCacheBean/local");
    }
    
    public static UserCacheLocal getUserCache() {
        return (UserCacheLocal)lookup("openelis/UserCacheBean/local");
    }

    /*
     * Bean from Security project
     */
    public static SystemUserPermissionRemote getSecurity() {
        return (SystemUserPermissionRemote)lookup("security/SystemUserPermissionBean/remote");
    }

    private static Object lookup(String bean) {
        InitialContext ctx;

        try {
            ctx = new InitialContext();
            return ctx.lookup(bean);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}