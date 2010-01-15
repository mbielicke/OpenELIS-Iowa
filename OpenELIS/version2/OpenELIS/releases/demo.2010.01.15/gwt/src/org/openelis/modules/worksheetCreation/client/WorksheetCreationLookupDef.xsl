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
              <autoComplete key="{meta:getAnalysisTestId()}" width="150px" case="LOWER" popWidth="auto" tab="{meta:getAnalysisSectionId()},analysisTable" field="Integer">
                <col width="150" header="Test" />
                <col width="150" header="Method" />
                <col width="200" header="Description" />
                <col width="75" header="Begin Date" />
                <col width="75" header="End Date" />
              </autoComplete>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'method')" />:
              </text>
              <textbox key="{meta:getAnalysisTestMethodName()}" width="150px" case="LOWER" field="String" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'section')" />:
              </text>
              <dropdown key="{meta:getAnalysisSectionId()}" width="130px" popWidth="130px" tab="{meta:getSampleAccessionNumber()},meta:getAnalysisTestId()" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getSampleAccessionNumber()}" width="75px" tab="{meta:getAnalysisStatusId()},{meta:getAnalysisSectionId()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getAnalysisStatusId()}" width="100px" popWidth="100px" tab="{meta:getSampleItemTypeOfSampleId()},{meta:getSampleAccessionNumber()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'sampleType')" />:
              </text>
              <dropdown key="{meta:getSampleItemTypeOfSampleId()}" width="175px" popWidth="175px" tab="{meta:getSampleReceivedDate()},meta:getAnalysisStatusId()" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getSampleReceivedDate()}" begin="0" end="4" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getSampleEnteredDate()},{meta:getSampleItemTypeOfSampleId()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'entered')" />:
              </text>
              <calendar key="{meta:getSampleEnteredDate()}" begin="0" end="4" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="searchButton,{meta:getSampleReceivedDate()}" />
            </row>
          </TablePanel>
          <widget halign="center" valign="middle">
              <appButton key="searchButton" style="Button" action="search" tab="analysisTable,{meta:getSampleEnteredDate()}">
                <HorizontalPanel>
                  <AbsolutePanel style="FindButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'search')" />
                  </text>
                </HorizontalPanel>
              </appButton>
          </widget>
        </HorizontalPanel>
        <table key="analysesTable" width="800" maxRows="9" showScroll="ALWAYS" tab="addButton,searchButton" title="" style="ScreenTableWithSides">
          <col key="{meta:getSampleAccessionNumber()}" width="90" header="{resource:getString($constants,'accessionNum')}" sort="true">
            <label />
          </col>
          <col key="{meta:getSampleEnvironmentalDescription()}" width="150" header="{resource:getString($constants,'description')}" sort="true">
            <label />
          </col>
          <col key="{meta:getAnalysisTestId()}" width="100" header="{resource:getString($constants,'test')}" sort="true">
            <label />
          </col>
          <col key="{meta:getAnalysisTestMethodName()}" width="100" header="{resource:getString($constants,'method')}" sort="true">
            <label />
          </col>
          <col key="{meta:getAnalysisSectionName()}" width="100" header="{resource:getString($constants,'section')}" sort="true">
            <label />
          </col>
          <col key="{meta:getAnalysisStatusId()}" width="75" header="{resource:getString($constants,'status')}" sort="true">
            <dropdown width="55"/>
          </col>
          <col key="{meta:getSampleCollectionDate}" width="75" header="{resource:getString($constants,'collected')}" sort="true">
            <calendar pattern="{resource:getString($constants,'datePattern')}" begin="0" end="2"/>
          </col>
          <col key="{meta:getSampleReceivedDate}" width="100" header="{resource:getString($constants,'received')}" sort="true">
            <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
          </col>
          <col key="{meta:getAnalysisDueDays()}" width="50" header="{resource:getString($constants,'due')}" sort="true">
            <label />
          </col>
          <col key="{meta:getAnalysisExpireDate()}" width="100" header="{resource:getString($constants,'expire')}" sort="true">
            <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
          </col>
          <col key="{meta:getSampleEnvironmentalPriority()}" width="65" header="{resource:getString($constants,'priority')}" sort="true">
            <label />
          </col>
        </table>
        <widget style="TableFooterPanel">
          <HorizontalPanel>
            <appButton key="addButton" style="Button" action="add" tab="selectAllButton,analysesTable">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'add')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="selectAllButton" style="Button" action="selectAll" tab="{meta:getAnalysisTestId()},addButton">
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
