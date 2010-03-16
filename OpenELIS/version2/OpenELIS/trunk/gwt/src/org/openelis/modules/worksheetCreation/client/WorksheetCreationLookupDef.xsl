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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCreationMeta">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCreationLookup" name="{resource:getString($constants,'worksheetCreationLookup')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <HorizontalPanel>
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'test')" />:
              </text>
              <autoComplete key="{meta:getAnalysisTestId()}" width="150" case="LOWER" popWidth="auto" tab="{meta:getAnalysisSectionId()},analysisTable" field="Integer">
                <col width="150" header="Test" />
                <col width="150" header="Method" />
                <col width="200" header="Description" />
                <col width="75" header="Begin Date" />
                <col width="75" header="End Date" />
              </autoComplete>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'method')" />:
              </text>
              <textbox key="{meta:getAnalysisTestMethodName()}" width="150" case="LOWER" field="String" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'section')" />:
              </text>
              <dropdown key="{meta:getAnalysisSectionId()}" width="130" popWidth="130px" tab="{meta:getSampleAccessionNumber()},meta:getAnalysisTestId()" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getSampleAccessionNumber()}" width="80" tab="{meta:getAnalysisStatusId()},{meta:getAnalysisSectionId()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getAnalysisStatusId()}" width="100" popWidth="100" tab="{meta:getSampleItemTypeOfSampleId()},{meta:getSampleAccessionNumber()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'sampleType')" />:
              </text>
              <dropdown key="{meta:getSampleItemTypeOfSampleId()}" width="175" popWidth="175" tab="{meta:getSampleReceivedDate()},meta:getAnalysisStatusId()" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getSampleReceivedDate()}" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getSampleEnteredDate()},{meta:getSampleItemTypeOfSampleId()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'entered')" />:
              </text>
              <calendar key="{meta:getSampleEnteredDate()}" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="searchButton,{meta:getSampleReceivedDate()}" />
            </row>
          </TablePanel>
          <widget halign="center" valign="middle">
            <appButton key="searchButton" style="Button" tab="analysisTable,{meta:getSampleEnteredDate()}" action="search">
              <HorizontalPanel>
                <AbsolutePanel style="FindButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'search')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
        </HorizontalPanel>
        <table key="analysesTable" width="800" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" tab="addButton,searchButton" title="">
          <col key="{meta:getSampleAccessionNumber()}" width="90" sort="true" header="{resource:getString($constants,'accessionNum')}">
            <label field="String" />
          </col>
          <col key="{meta:getSampleEnvironmentalDescription()}" width="150" sort="true" header="{resource:getString($constants,'description')}">
            <label field="String" />
          </col>
          <col key="{meta:getAnalysisTestId()}" width="100" sort="true" header="{resource:getString($constants,'test')}">
            <label field="Integer" />
          </col>
          <col key="{meta:getAnalysisTestMethodName()}" width="100" sort="true" header="{resource:getString($constants,'method')}">
            <label field="String" />
          </col>
          <col key="{meta:getAnalysisSectionId()}" width="100" sort="true" header="{resource:getString($constants,'section')}">
            <dropdown width="80" field="Integer" />
          </col>
          <col key="{meta:getAnalysisStatusId()}" width="75" sort="true" header="{resource:getString($constants,'status')}">
            <dropdown width="55" field="Integer" />
          </col>
          <col key="{meta:getSampleCollectionDate}" width="75" sort="true" header="{resource:getString($constants,'collected')}">
            <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
          </col>
          <col key="{meta:getSampleReceivedDate}" width="110" sort="true" header="{resource:getString($constants,'received')}">
            <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </col>
          <col key="{meta:getAnalysisDueDays()}" width="50" sort="true" header="{resource:getString($constants,'due')}">
            <label field="Integer" />
          </col>
          <col key="{meta:getAnalysisExpireDate()}" width="110" sort="true" header="{resource:getString($constants,'expire')}">
            <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </col>
          <col key="{meta:getSampleEnvironmentalPriority()}" width="65" sort="true" header="{resource:getString($constants,'priority')}">
            <label field="String" />
          </col>
        </table>
        <widget style="TableFooterPanel">
          <HorizontalPanel>
            <appButton key="addButton" style="Button" tab="selectAllButton,analysesTable" action="add">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'add')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="selectAllButton" style="Button" tab="{meta:getAnalysisTestId()},addButton" action="selectAll">
              <HorizontalPanel>
                <AbsolutePanel style="SelectAllButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'selectAll')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
        </widget>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
