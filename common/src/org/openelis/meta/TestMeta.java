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
  * Test META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class TestMeta implements Meta, MetaMap {
	
	private static final String ID ="_test.id",
                                NAME ="_test.name",
                                DESCRIPTION ="_test.description",
                                REPORTING_DESCRIPTION ="_test.reportingDescription",
                                METHOD_ID ="_test.methodId",
                                IS_ACTIVE ="_test.isActive",
                                ACTIVE_BEGIN ="_test.activeBegin",
                                ACTIVE_END ="_test.activeEnd",
                                IS_REPORTABLE ="_test.isReportable",
                                TIME_TRANSIT ="_test.timeTransit",
                                TIME_HOLDING ="_test.timeHolding",
                                TIME_TA_AVERAGE ="_test.timeTaAverage",
                                TIME_TA_WARNING ="_test.timeTaWarning",
                                TIME_TA_MAX ="_test.timeTaMax",
                                LABEL_ID ="_test.labelId",
                                LABEL_QTY ="_test.labelQty",
                                TEST_TRAILER_ID ="_test.testTrailerId",
                                SCRIPTLET_ID ="_test.scriptletId",
                                TEST_FORMAT_ID ="_test.testFormatId",
                                REVISION_METHOD_ID ="_test.revisionMethodId",
                                REPORTING_METHOD_ID ="_test.reportingMethodId",
                                SORTING_METHOD_ID = "_test.sortingMethodId",
                                REPORTING_SEQUENCE = "_test.reportingSequence",
                                
                                SECT_ID = "_testSection.id",
                                SECT_TEST_ID ="_testSection.testId",
                                SECT_SECTION_ID ="_testSection.sectionId",
                                SECT_FLAG_ID ="_testSection.flagId",
                                
                                TOS_ID = "_testTypeOfSample.id",
                                TOS_TEST_ID = "_testTypeOfSample.testId",
                                TOS_TYPE_OF_SAMPLE_ID = "_testTypeOfSample.typeOfSampleId",
                                TOS_UNIT_OF_MEASURE_ID = "_testTypeOfSample.unitOfMeasureId",
                                
                                ANA_ID ="_testAnalyte.id",
                                ANA_TEST_ID ="_testAnalyte.testId",
                                ANA_ANALYTE_GROUP = "_testAnalyte.analyteGroup",
                                ANA_RESULT_GROUP = "_testAnalyte.resultGroup",
                                ANA_SORT_ORDER = "_testAnalyte.sortOrder",
                                ANA_TYPE_ID = "_testAnalyte.typeId",
                                ANA_ANALYTE_ID = "_testAnalyte.analyteId",
                                ANA_IS_REPORTABLE = "_testAnalyte.isReportable",
                                ANA_SCRIPTLET_ID = "_testAnalyte.scriptletId",
                                
                                RES_ID = "_testResult.id",
                                RES_TEST_ID = "_testResult.testId",
                                RES_RESULT_GROUP = "_testResult.resultGroup",
                                RES_SORT_ORDER = "_testResult.sortOrder",       
                                RES_FLAGS_ID = "_testResult.flagsId",
                                RES_TYPE_ID = "_testResult.typeId",
                                RES_VALUE = "_testResult.value",
                                RES_SIGNIFICANT_DIGITS = "_testResult.significantDigits",
                                RES_ROUNDING_METHOD_ID = "_testResult.roundingMethodId", 
                                RES_QUANT_LIMIT = "_testResult.quantLimit",
                                RES_CONT_LEVEL = "_testResult.contLevel",
                                RES_HAZARD_LEVEL = "_testResult.hazardLevel",
                                RES_UNIT_OF_MEASURE_ID = "_testResult.unitOfMeasureId",
                                
                                PREP_ID = "_testPrep.id",
                                PREP_TEST_ID = "_testPrep.testId",
                                PREP_PREP_TEST_ID = "_testPrep.prepTestId",
                                PREP_IS_OPTIONAL = "_testPrep.isOptional",
                                
                                REF_ID = "_testReflex.id",
                                REF_TEST_ID = "_testReflex.testId",
                                REF_TEST_ANALYTE_ID = "_testReflex.testAnalyteId",
                                REF_TEST_RESULT_ID = "_testReflex.testResultId",
                                REF_FLAGS_ID = "_testReflex.flagsId",
                                REF_ADD_TEST_ID = "_testReflex.addTestId",
                                
                                WS_ID = "_testWorksheet.id",
                                WS_TEST_ID = "_testWorksheet.testId",
                                WS_BATCH_CAPACITY = "_testWorksheet.batchCapacity",
                                WS_TOTAL_CAPACITY = "_testWorksheet.totalCapacity",
                                WS_FORMAT_ID = "_testWorksheet.formatId",
                                WS_SCRIPTLET_ID ="_testWorksheet.scriptletId",
                                
                                WSI_ID = "_testWorksheetItem.id",
                                WSI_TEST_WORKSHEET_ID = "_testWorksheetItem.testWorksheetId",
                                WSI_POSITION = "_testWorksheetItem.position",
                                WSI_TYPE_ID = "_testWorksheetItem.typeId",
                                WSI_QC_NAME = "_testWorksheetItem.qcName",
                                
                                WSA_ID = "_testWorksheetAnalyte.id",
                                WSA_TEST_ID = "_testWorksheetAnalyte.testId",
                                WSA_ANALYTE_ID = "_testWorksheetAnalyte.analyteId",
                                WSA_REPEAT = "_testWorksheetAnalyte.repeat",
                                WSA_FLAG_ID = "_testWorksheetAnalyte.flagId",
                                
                                LABEL_NAME = "_test.label.name",
	                            METHOD_NAME = "_test.method.name",   
	                            METHOD_DESCRIPTION = "_test.method.description",     
	                            TEST_TRAILER_NAME = "_test.testTrailer.name",
	                            SCRIPTLET_NAME = "_test.scriptlet.name",
	
	                            ANA_SCRIPTLET_NAME = "_testAnalyte.scriptlet.name",
	                            ANA_ANALYTE_NAME = "_testAnalyte.analyte.name",
	
	                            PREP_PREP_TEST_NAME = "_testPrep.prepTest.name",
	
	                            REF_TEST_ANALYTE_NAME = "_testReflex.testAnalyte.analyte.name",
	                            REF_TEST_RESULT_VALUE = "_testReflex.testResult.value",
	                            REF_ADD_TEST_NAME = "_testReflex.addTest.name",
	                            
	                            WS_SCRIPTLET_NAME ="_testWorksheet.scriptlet.name";
  	  
	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,
                                                  METHOD_ID,IS_ACTIVE,ACTIVE_BEGIN,
                                                  ACTIVE_END,IS_REPORTABLE,TIME_TRANSIT,
                                                  TIME_HOLDING,TIME_TA_AVERAGE,TIME_TA_WARNING,
                                                  TIME_TA_MAX,LABEL_ID,LABEL_QTY,TEST_TRAILER_ID,
                                                  SCRIPTLET_ID,TEST_FORMAT_ID,REVISION_METHOD_ID,
                                                  REPORTING_METHOD_ID,SORTING_METHOD_ID,REPORTING_SEQUENCE,
                                                  SECT_ID,SECT_TEST_ID,SECT_SECTION_ID,SECT_FLAG_ID,
                                                  TOS_ID,TOS_TEST_ID, TOS_TYPE_OF_SAMPLE_ID,TOS_UNIT_OF_MEASURE_ID,
                                                  ANA_ID,ANA_TEST_ID,ANA_ANALYTE_GROUP,ANA_RESULT_GROUP,
                                                  ANA_SORT_ORDER,ANA_TYPE_ID,ANA_TYPE_ID,ANA_ANALYTE_ID,
                                                  ANA_IS_REPORTABLE,ANA_SCRIPTLET_ID,PREP_ID,PREP_TEST_ID,
                                                  PREP_PREP_TEST_ID,PREP_IS_OPTIONAL,REF_ID,REF_TEST_ID,
                                                  REF_TEST_ANALYTE_ID,REF_TEST_RESULT_ID,REF_FLAGS_ID,
                                                  REF_ADD_TEST_ID,WS_ID,WS_FORMAT_ID,WS_TOTAL_CAPACITY,
                                                  WS_BATCH_CAPACITY,WS_SCRIPTLET_ID,WS_SCRIPTLET_NAME,
                                                  WS_TEST_ID,WSI_ID,WSI_TEST_WORKSHEET_ID,
                                                  WSI_POSITION,WSI_TYPE_ID,WSI_QC_NAME,WSA_ID,WSA_TEST_ID,
                                                  WSA_ANALYTE_ID,WSA_REPEAT,WSA_FLAG_ID,LABEL_NAME,METHOD_NAME,
                                                  TEST_TRAILER_NAME,SCRIPTLET_NAME,ANA_SCRIPTLET_NAME, 
                                                  ANA_ANALYTE_NAME,PREP_PREP_TEST_NAME,REF_TEST_ANALYTE_NAME,
                                                  REF_TEST_RESULT_VALUE,REF_ADD_TEST_NAME));
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
        
    public String getId() {
        return ID;
    } 

    public String getName() {
        return NAME;
    } 

    public String getDescription() {
        return DESCRIPTION;
    } 

    public String getReportingDescription() {
        return REPORTING_DESCRIPTION;
    } 

    public String getMethodId() {
        return METHOD_ID;
    } 

    public String getIsActive() {
        return IS_ACTIVE;
    } 

    public String getActiveBegin() {
        return ACTIVE_BEGIN;
    } 

    public String getActiveEnd() {
        return ACTIVE_END;
    } 

    public String getIsReportable() {
        return IS_REPORTABLE;
    } 

    public String getTimeTransit() {
        return TIME_TRANSIT;
    } 

    public String getTimeHolding() {
        return TIME_HOLDING;
    } 

    public String getTimeTaAverage() {
        return TIME_TA_AVERAGE;
    } 

    public String getTimeTaWarning() {
        return TIME_TA_WARNING;
    } 

    public String getTimeTaMax() {
        return TIME_TA_MAX;
    } 

    public String getLabelId() {
        return LABEL_ID;
    } 

    public String getLabelQty() {
        return LABEL_QTY;
    } 

    public String getTestTrailerId() {
        return TEST_TRAILER_ID;
    } 

    public String getScriptletId() {
        return SCRIPTLET_ID;
    } 

    public String getTestFormatId() {
        return TEST_FORMAT_ID;
    } 

    public String getRevisionMethodId() {
        return REVISION_METHOD_ID;
    }

    public String getReportingMethodId() {
        return REPORTING_METHOD_ID;
    }

    public String getReportingSequence() {
        return REPORTING_SEQUENCE;
    }
    
    public String getSortingMethodId(){
        return SORTING_METHOD_ID;
    }
    
    public String getMethodName() {
        return METHOD_NAME;
    }
    
    public String getMethodDescription() {
        return METHOD_DESCRIPTION;
    }
    
    public String getLabelName() {
        return LABEL_NAME;
    } 
    
    public String getTestTrailerName() {
        return TEST_TRAILER_NAME;
    }
    
    public String getScriptletName() {
        return SCRIPTLET_NAME;
    }
    
    public String getSectionId() {
        return SECT_ID;
    } 

    public String getSectionTestId() {
        return SECT_TEST_ID;
    } 

    public String getSectionSectionId() {
        return SECT_SECTION_ID;
    } 

    public String getSectionFlagId() {
        return SECT_FLAG_ID;
    } 
    
    public String getTypeOfSampleId() {
        return TOS_ID;
    } 

    public String getTypeOfSampleTestId() {
        return TOS_TEST_ID;
    } 

    public String getTypeOfSampleTypeOfSampleId() {
        return TOS_TYPE_OF_SAMPLE_ID;
    } 

    public String getTypeOfSampleUnitOfMeasureId() {
        return TOS_UNIT_OF_MEASURE_ID;
    }
    
    public String getAnalyteId() {
        return ANA_ID;
    } 

    public String getAnalyteTestId() {
        return ANA_TEST_ID;
    } 
    
    public String getAnalyteAnalyteGroup() {
        return ANA_ANALYTE_GROUP;
    }

    public String getAnalyteResultGroup() {
        return ANA_RESULT_GROUP;
    } 

    public String getAnalyteSortOrder() {
        return ANA_SORT_ORDER;
    } 

    public String getAnalyteTypeId() {
        return ANA_TYPE_ID;
    } 

    public String getAnalyteAnalyteId() {
        return ANA_ANALYTE_ID;
    } 

    public String getAnalyteIsReportable() {
        return ANA_IS_REPORTABLE;
    } 

    public String getAnalyteScriptletId() {
        return ANA_SCRIPTLET_ID;
    }
    
    public String getAnalyteAnalyteName() {
        return ANA_ANALYTE_NAME;
    }
    
    public String getAnalyteScriptletName() {
        return ANA_SCRIPTLET_NAME;
    }
    
    public String getResultId() {
        return RES_ID;
    } 

    public String getResultTestId() {
        return RES_TEST_ID;
    } 

    public String getResultResultGroup() {
        return RES_RESULT_GROUP;
    } 
    
    public String getResultSortOrder() {
        return RES_SORT_ORDER;
    } 

    public String getResultFlagsId() {
        return RES_FLAGS_ID;
    } 

    public String getResultTypeId() {
        return RES_TYPE_ID;
    } 

    public String getResultValue() {
        return RES_VALUE;
    } 

    public String getResultSignificantDigits() {
        return RES_SIGNIFICANT_DIGITS;
    } 
    
    public String getResultRoundingMethodId() {
        return RES_ROUNDING_METHOD_ID;
    }

    public String getResultQuantLimit() {
        return RES_QUANT_LIMIT;
    } 

    public String getResultContLevel() {
        return RES_CONT_LEVEL;
    } 
    
    public String getResultHazardLevel() {
        return RES_HAZARD_LEVEL;
    }
    
    public String getResultUnitOfMeasureId() {
        return RES_UNIT_OF_MEASURE_ID;
    }
    
    public String getPrepId() {
        return PREP_ID;
    } 

    public String getPrepTestId() {
        return PREP_TEST_ID;
    } 

    public String getPrepPrepTestId() {
        return PREP_PREP_TEST_ID;
    } 

    public String getPrepIsOptional() {
        return PREP_IS_OPTIONAL;
    } 
    
    public String getPrepPrepTestName() {
        return PREP_PREP_TEST_NAME;
    }
    
    public String getReflexId() {
        return REF_ID;
    } 

    public String getReflexTestId() {
        return REF_TEST_ID;
    } 

    public String getReflexTestAnalyteId() {
        return REF_TEST_ANALYTE_ID;
    } 

    public String getReflexTestResultId() {
        return REF_TEST_RESULT_ID;
    } 

    public String getReflexFlagsId() {
        return REF_FLAGS_ID;
    } 

    public String getReflexAddTestId() {
        return REF_ADD_TEST_ID;
    }
    
    public String getReflexTestAnalyteName() {
        return REF_TEST_ANALYTE_NAME;
    } 

    public String getReflexTestResultValue() {
        return REF_TEST_RESULT_VALUE;
    } 
    
    public String getReflexAddTestName() {
        return REF_ADD_TEST_NAME;
    }
    
    public String getWorksheetId() {
        return WS_ID;
    } 

    public String getWorksheetTestId() {
        return WS_TEST_ID;
    } 

    public String getWorksheetBatchCapacity() {
        return WS_BATCH_CAPACITY;
    } 

    public String getWorksheetTotalCapacity() {
        return WS_TOTAL_CAPACITY;
    } 

    public String getWorksheetFormatId() {
        return WS_FORMAT_ID;
    } 

    public String getWorksheetScriptletId() {
        return WS_SCRIPTLET_ID;
    }
    
    public String getWorksheetScriptletName() {
        return WS_SCRIPTLET_NAME;
    }
    
    public String getWorksheetItemId() {
        return WSI_ID;
    } 

    public String getWorksheetItemTestWorksheetId() {
        return WSI_TEST_WORKSHEET_ID;
    } 

    public String getWorksheetItemPosition() {
        return WSI_POSITION;
    } 

    public String getWorksheetItemTypeId() {
        return WSI_TYPE_ID;
    } 

    public String getWorksheetItemQcName() {
        return WSI_QC_NAME;
    } 
    
    public String getWorksheetAnalyteId() {
        return WSA_ID;
    } 

    public String getWorksheetAnalyteTestId() {
        return WSA_TEST_ID;
    } 

    public String getWorksheetAnalyteAnalyteId() {
        return WSA_ANALYTE_ID;
    } 

    public String getWorksheetAnalyteRepeat() {
        return WSA_REPEAT;
    } 

    public String getWorksheetAnalyteFlagId() {
        return WSA_FLAG_ID;
    } 
    
    public String buildFrom(String where) {
        String from, wsFrom, wsiFrom;
        
        wsFrom = ", IN (_test.testWorksheet) _testWorksheet ";
        wsiFrom = ", IN (_testWorksheet.testWorksheetItem) _testWorksheetItem ";
        
        from = "Test _test ";
        if (where.indexOf("testPrep.") > -1)
            from += ", IN (_test.testPrep) _testPrep ";
        if (where.indexOf("testTypeOfSample.") > -1)
            from += ", IN (_test.testTypeOfSample) _testTypeOfSample ";
        if (where.indexOf("testReflex.") > -1)
            from += ", IN (_test.testReflex) _testReflex ";
        if (where.indexOf("testAnalyte.") > -1)
            from += ", IN (_test.testAnalyte) _testAnalyte ";
        if (where.indexOf("testSection.") > -1)
            from += ", IN (_test.testSection) _testSection ";
        if (where.indexOf("testResult.") > -1)
            from += ", IN (_test.testResult) _testResult ";
        if (where.indexOf("testWorksheetAnalyte.") > -1)
            from += ", IN (_test.testWorksheetAnalyte) _testWorksheetAnalyte ";
        if (where.indexOf("testWorksheet.") > -1)
            from += wsFrom;
        if (where.indexOf("testWorksheetItem.") > -1)
            if (from.indexOf(wsFrom) < 0)
                from += wsFrom + wsiFrom;
            else
                from += wsiFrom;
        return from;
    }
   
}   
