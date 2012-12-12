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

import org.openelis.gwt.common.RPC;

/**
 * The class provides application wide constants that are used in the EJB and
 * GWT context. Parts of the class is loaded from the database at server
 * start-up which makes some of these enums not immutable.
 */
public class Constants implements RPC {
    private static final long serialVersionUID = 1L;

    /**
     * The enum is used for table reference id. Table reference ids are used for
     * common tables such as notes to link to other tables-records.
     */
    public static enum Table {
        PERSON(1), PATIENT(2), PATIENT_RELATION(3), PROVIDER(4), ORGANIZATION(5),
        ORGANIZATION_CONTACT(6), SAMPLE(8), SAMPLE_ENVIRONMENTAL(9), SAMPLE_ANIMAL(10),
        SAMPLE_HUMAN(11), SAMPLE_PROJECT(12), SAMPLE_ORGANIZATION(13), SAMPLE_ITEM(14),
        ANALYSIS(15), ANALYSIS_QAEVENT(16), ANALYSIS_USER(17), RESULT(18), QAEVENT(19), NOTE(20),
        STANDARD_NOTE(21), TEST(22), TEST_ANALYTE(23), TEST_RESULT(24), TEST_REFLEX(25),
        TEST_WORKSHEET(26), TEST_WORKSHEET_ITEM(27), SECTION(28), TEST_TRAILER(29), METHOD(30),
        SAMPLE_PRIVATE_WELL(31), PROJECT(33), PROJECT_PARAMETER(34), INVENTORY_ITEM(35),
        INVENTORY_COMPONENT(36), INVENTORY_LOCATION(37), INVENTORY_RECEIPT(38), ORDER(39),
        ORDER_ITEM(40), INSTRUMENT(41), INSTRUMENT_LOG(42), INSTRUMENT_METHOD(43), HISTORY(44),
        REFERENCE_TABLE(45), PANEL(46), PANEL_ITEM(47), ANALYTE(48), CATEGORY(49), DICTIONARY(50),
        QC(51), QC_ANALYTE(52), STORAGE_UNIT(53), STORAGE_LOCATION(54), STORAGE(55), SCRIPTLET(56),
        LABEL(57), ATTACHMENT(58), ATTACHMENT_ITEM(59), SYSTEM_VARIABLE(60), ADDRESS(61),
        AUX_FIELD(62), AUX_FIELD_VALUE(63), AUX_DATA(64), ORDER_CUSTOMER_NOTE(65),
        ORDER_SHIPPING_NOTE(66), INVENTORY_X_USE(68), INVENTORY_X_PUT(70), INVENTORY_X_ADJUST(71),
        INVENTORY_ADJUSTMENT(72), SHIPPING(73), SHIPPING_TRACKING(74), SHIPPING_ITEM(75), LOCK(76),
        PROVIDER_LOCATION(78), PREFERENCES(79), AUX_FIELD_GROUP(80),
        INVENTORY_ITEM_MANUFACTURING(82), SAMPLE_SDWIS(83), SAMPLE_QAEVENT(85), WORKSHEET(86),
        INVENTORY_RECEIPT_ORDER_ITEM(87), ORGANIZATION_PARAMETER(88), TEST_PREP(89),
        TEST_SECTION(90), TEST_TYPE_OF_SAMPLE(91), TEST_WORKSHEET_ANALYTE(92),
        WORKSHEET_ANALYSIS(93), WORKSHEET_ITEM(94), WORKSHEET_QC_RESULT(96), WORKSHEET_RESULT(97),
        INVENTORY_TRANSACTION(98), PWS(99), PWS_FACILITY(100), PWS_ADDRESS(101), PWS_MONITOR(102),
        ORDER_CONTAINER(103), ORDER_TEST(104), ANALYTE_PARAMETER(105), SECTION_PARAMETER(106),
        ANALYSIS_REPORT_FLAGS(107), ORDER_RECURRENCE(108), CRON(109), ORDER_ORGANIZATION(110),
        ORDER_TEST_ANALYTE(111), ORDER_SAMPLE_NOTE(112), EXCHANGE_LOCAL_TERM(113),
        EXCHANGE_EXTERNAL_TERM(114), EXCHANGE_CRITERIA(115), EXCHANGE_PROFILE(116), EVENT_LOG(117),
        QC_LOT(118);

        protected final int id;

        private Table(int id) {
            this.id = id;
        }

        /**
         * Returns the table reference id
         */
        public int getValue() {
            return id;
        }
    }

    /**
     * The enum represents all the sample domains that can be used in sample
     * record
     */
    public static enum Domain {
        QUICKENTRY("Q"), ENVIRONMENTAL("E"), SDWIS("S"), PRIVATEWELL("W"), NEWBORN("N"),
        HUMAN("H"), ANIMAL("A"), PT("P");

        protected final String value;

        private Domain(String value) {
            this.value = value;
        }

        /**
         * Returns the string representing the sample domain field
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * The enum represents all the operations that can be performed for
     * auditing.
     */
    public static enum Audit {
        ADD(1), UPDATE(2), DELETE(3), VIEW(4);

        protected final int value;

        private Audit(int value) {
            this.value = value;
        }

        /**
         * Returns the numeric value for the audit operation 
         */
        public int getValue() {
            return value;
        }
    }
}
