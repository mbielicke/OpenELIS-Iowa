/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.meta;

/**
  * Instrument META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class InstrumentMeta implements Meta, MetaMap {
    
	private static final String ID = "_instrument.id",
                                NAME = "_instrument.name",
                                DESCRIPTION = "_instrument.description",
                                MODEL_NUMBER = "_instrument.modelNumber",
                                SERIAL_NUMBER	= "_instrument.serialNumber",
                                TYPE_ID = "_instrument.typeId",
                                LOCATION = "_instrument.location",
                                IS_ACTIVE	= "_instrument.isActive",
                                ACTIVE_BEGIN = "_instrument.activeBegin",
                                ACTIVE_END = "_instrument.activeEnd",
                                SCRIPTLET_ID = "_instrument.scriptletId",
                                
                                LOG_ID = "_instrumentLog.id",
                                LOG_INSTRUMENT_ID = "_instrumentLog.instrumentId",
                                LOG_TYPE_ID = "_instrumentLog.typeId",
                                LOG_WORKSHEET_ID = "_instrumentLog.worksheetId",
                                LOG_EVENT_BEGIN = "_instrumentLog.eventBegin",
                                LOG_EVENT_END = "_instrumentLog.eventEnd",
                                LOG_TEXT = "_instrumentLog.text",
                                
                                SCRIPTLET_NAME = "_instrument.scriptlet.name";

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,DESCRIPTION,MODEL_NUMBER,
                                                  SERIAL_NUMBER,TYPE_ID,LOCATION,
                                                  IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,
                                                  SCRIPTLET_ID,LOG_ID,LOG_INSTRUMENT_ID,
                                                  LOG_TYPE_ID,LOG_WORKSHEET_ID,
                                                  LOG_EVENT_BEGIN,LOG_EVENT_END,
                                                  LOG_TEXT,SCRIPTLET_NAME));
    } 
    
    public static String getId() {
        return ID;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getDescription() {
        return DESCRIPTION;
    } 

    public static String getModelNumber() {
        return MODEL_NUMBER;
    } 

    public static String getSerialNumber() {
        return SERIAL_NUMBER;
    } 

    public static String getTypeId() {
        return TYPE_ID;
    } 

    public static String getLocation() {
        return LOCATION;
    } 

    public static String getIsActive() {
        return IS_ACTIVE;
    } 

    public static String getActiveBegin() {
        return ACTIVE_BEGIN;
    } 

    public static String getActiveEnd() {
        return ACTIVE_END;
    } 

    public static String getScriptletId() {
        return SCRIPTLET_ID;
    } 
    
    public static String getScriptletName() {
        return SCRIPTLET_NAME;
    }
    
    public static String getLogId() {
        return LOG_ID;
    } 

    public static String getLogInstrumentId() {
        return LOG_INSTRUMENT_ID;
    } 

    public static String getLogTypeId() {
        return LOG_TYPE_ID;
    } 

    public static String getLogWorksheetId() {
        return LOG_WORKSHEET_ID;
    } 

    public static String getLogEventBegin() {
        return LOG_EVENT_BEGIN;
    } 

    public static String getLogEventEnd() {
        return LOG_EVENT_END;
    } 
    
    public static String getLogText() {
        return LOG_TEXT;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from = "Instrument _instrument ";       
        if(where.indexOf("instrumentLog.") > -1)
            from += ", IN (_instrument.instrumentLog) _instrumentLog ";
        return from;
    }

  
}   
