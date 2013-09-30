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
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.manager.OrderManager1;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used for transporting the data needed for adding tests to a
 * order and any errors or warnings resulting from trying to do so
 */
public class OrderTestReturnVO implements Serializable {

    private static final long      serialVersionUID = 1L;

    protected OrderManager1        manager;

    protected ValidationErrorsList errors;

    public OrderManager1 getManager() {
        return manager;
    }

    public void setManager(OrderManager1 manager) {
        this.manager = manager;
    }

    public ValidationErrorsList getErrors() {
        return errors;
    }

    public void setErrors(ValidationErrorsList errors) {
        this.errors = errors;
    }
}