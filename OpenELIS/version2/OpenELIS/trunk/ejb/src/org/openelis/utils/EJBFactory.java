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
import org.openelis.local.AnalysisUserLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.AuxFieldGroupLocal;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InstrumentLocal;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.local.InventoryComponentLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXAdjustLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.InventoryXUseLocal;
import org.openelis.local.NoteLocal;
import org.openelis.local.OrderContainerLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.ResultLocal;
import org.openelis.security.remote.SystemUserPermissionRemote;

/**
 * This static class is used to get local instance EJB beans for non ejb class
 * calls.
 */

public class EJBFactory {

    public static DictionaryCacheLocal getDictionaryCache() {
        return (DictionaryCacheLocal) lookup("openelis/DictionaryCacheBean/local");
    }

    public static AnalysisLocal getAnalysis() {
        return (AnalysisLocal)lookup("openelis/AnalysisBean/local");
    }

    public static AnalysisQAEventLocal getAnalysisQAEvent() {
        return (AnalysisQAEventLocal)lookup("openelis/AnalysisQAEventBean/local");
    }

    public static AnalysisUserLocal getAnalysisUser() {
        return (AnalysisUserLocal)lookup("openelis/AnalysisUserBean/local");
    }

    public static AuxDataLocal getAuxData() {
        return (AuxDataLocal)lookup("openelis/AuxDataBean/local");
    }

    public static AuxFieldLocal getAuxField() {
        return (AuxFieldLocal)lookup("openelis/AuxFieldBean/local");
    }

    public static AuxFieldGroupLocal getAuxFieldGroup() {
        return (AuxFieldGroupLocal)lookup("openelis/AuxFieldGroupBean/local");
    }

    public static AuxFieldValueLocal getAuxFieldValue() {
        return (AuxFieldValueLocal)lookup("openelis/AuxFieldValueBean/local");
    }

    public static ResultLocal getResult() {
        return (ResultLocal)lookup("openelis/ResultBean/local");
    }

    public static InventoryXUseLocal getInventoryXUse() {
        return (InventoryXUseLocal)lookup("openelis/InventoryXUseBean/local");
    }

    public static InventoryLocationLocal getInventoryLocation() {
        return (InventoryLocationLocal)lookup("openelis/InventoryLocationBean/local");
    }

    public static InventoryXPutLocal getInventoryXPut() {
        return (InventoryXPutLocal)lookup("openelis/InventoryXPutBean/local");
    }

    public static InventoryReceiptLocal getInventoryReceipt() {
        return (InventoryReceiptLocal)lookup("openelis/InventoryReceiptBean/local");
    }

    public static OrderItemLocal getOrderItem() {
        return (OrderItemLocal)lookup("openelis/OrderItemBean/local");
    }

    public static CategoryLocal getCategory() {
        return (CategoryLocal)lookup("openelis/CategoryBean/local");
    }

    public static DictionaryLocal getDictionary() {
        return (DictionaryLocal)lookup("openelis/DictionaryBean/local");
    }

    public static InstrumentLogLocal getInstrumentLog() {
        return (InstrumentLogLocal)lookup("openelis/InstrumentLogBean/local");
    }

    public static InstrumentLocal getInstrument() {
        return (InstrumentLocal)lookup("openelis/InstrumentBean/local");
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

    public static InventoryXAdjustLocal getInventoryXAdjust() {
        return (InventoryXAdjustLocal)lookup("openelis/InventoryXAdjustBean/local");
    }

    public static NoteLocal getNote() {
        return (NoteLocal)lookup("openelis/NoteBean/local");
    }

    public static OrderContainerLocal getOrderContainer() {
        return (OrderContainerLocal)lookup("openelis/OrderContainerBean/local");
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
