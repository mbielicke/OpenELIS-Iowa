<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->
<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:meta="xalan://org.openelis.meta.SampleMeta">

  <xsl:template name="AnalysisTab">
    <VerticalPanel padding="0" spacing="0">
      <TablePanel style="Form">
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'test')" />:
          </text>
          <autoComplete key="{meta:getAnalysisTestName()}" width="150" case="LOWER" tab="{meta:getAnalysisStatusId()},analysisUserTable">
            <col width="170" header="{resource:getString($constants,'test')}" />
            <col width="150" header="{resource:getString($constants,'method')}" />
            <col width="290" header="{resource:getString($constants,'description')}" />
          </autoComplete>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'method')" />:
          </text>
          <widget colspan="3">
          <autoComplete key="{meta:getAnalysisMethodName()}" width="151" case="LOWER">
            <col width="150" header="{resource:getString($constants,'method')}" />
          </autoComplete>
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'started')" />:
          </text>
          <calendar key="{meta:getAnalysisStartedDate()}" width="125" begin="0" end="4" tab="{meta:getAnalysisCompletedDate()},{meta:getAnalysisSamplePrep()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
        <row>
        <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'status')" />:
          </text>
          <dropdown key="{meta:getAnalysisStatusId()}" width="150" tab="{meta:getAnalysisSectionId()},{meta:getAnalysisTestName()}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'revision')" />:
          </text>
          <textbox key="{meta:getAnalysisRevision()}" width="60" field="Integer" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'testReportable')" />:
          </text>
          <check key="{meta:getAnalysisIsReportable()}" tab = "{meta:getAnalysisUnitOfMeasureId()},{meta:getAnalysisSectionId()}"/>
           <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'completed')" />:
          </text>
          <calendar key="{meta:getAnalysisCompletedDate()}" width="125" begin="0" end="4" tab = "worksheetTable,{meta:getAnalysisStartedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
          <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'section')" />:
          </text>
          <dropdown key="{meta:getAnalysisSectionId()}" width="151" case="LOWER" field="Integer" tab = "{meta:getAnalysisIsReportable()},{meta:getAnalysisStatusId()}"/>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'unit')" />:
          </text>
          <widget colspan="3">
          <dropdown key="{meta:getAnalysisUnitOfMeasureId()}" width="150" field="Integer" tab = "{meta:getAnalysisSamplePrep()},{meta:getAnalysisIsReportable()}" />
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'released')" />:
          </text>
          <calendar key="{meta:getAnalysisReleasedDate()}" width="125" begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
       </row>
       <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'samplePrep')" />:
          </text>
          <widget colspan="5">
          <autoComplete key="{meta:getAnalysisSamplePrep()}" width="365"  tab = "{meta:getAnalysisStartedDate()},{meta:getAnalysisUnitOfMeasureId()}" >
            <col width="350" header="Name" />
          </autoComplete>
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'printed')" />:
          </text>
          <calendar key="{meta:getAnalysisPrintedDate()}" width="125" begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
      </TablePanel>
      <HorizontalPanel spacing="0" padding="0">
                   <VerticalPanel spacing="0" padding="0">
            <table key="worksheetTable" style="ScreenTableWithSides" rows="3" vscroll="ALWAYS" hscroll="ALWAYS" tab = "analysisUserTable,{meta:getAnalysisCompletedDate()}">
            	<col width="70" header="Worksheet">
              		<label field="Integer"/>
            	</col>
            	<col width="115" header="Created">
              		<calendar width="115" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
            	</col>
            	<col width="115" header="Status">
              		<dropdown width="100" field="Integer" />
            	</col>
            	<col width="120" header="User">
              		<label field="String"/>
            	</col>
            </table>
	            <HorizontalPanel style="TableFooterPanel">
                  <button key="selectWkshtButton" icon="selectIcon" text="{resource:getString($constants,'select')}" style="Button"/>
                </HorizontalPanel>
                  </VerticalPanel>
            <AbsolutePanel style="Divider" />
                  <VerticalPanel spacing="0" padding="0">
      		<table key="analysisUserTable" style="ScreenTableWithSides" rows="3" vscroll="ALWAYS" hscroll="ALWAYS" tab = "{meta:getAnalysisTestName()},worksheetTable">
            	<col width="115" header="User">
              		<autoComplete width="100" case="LOWER" field="Integer"/>
            	</col>
            	<col width="114" header="Action">
              		<dropdown width="100" field="Integer" />
            	</col>
            </table>
            <HorizontalPanel style="TableFooterPanel">
               <button key="addActionButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
               <button key="removeActionButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
            </HorizontalPanel>
         </VerticalPanel>
      </HorizontalPanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
